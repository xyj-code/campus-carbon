<template>
  <view class="page">
    <scroll-view scroll-y class="scroller" :show-scrollbar="false">
      <view class="header">
        <text class="header-kicker">{{ copy.kicker }}</text>
        <text class="header-title">{{ copy.title }}</text>
        <text class="header-subtitle">{{ copy.subtitle }}</text>
      </view>

      <view class="form-card">
        <view class="form-row">
          <text class="label">{{ copy.weekdayLabel }}</text>
          <picker :range="weekdayOptions" range-key="label" :value="weekdayIndex" @change="onWeekdayChange">
            <view class="picker-box">{{ weekdayOptions[weekdayIndex].label }}</view>
          </picker>
        </view>

        <view class="form-row">
          <text class="label">{{ copy.courseLabel }}</text>
          <input class="input" v-model="form.courseName" :placeholder="copy.coursePlaceholder" />
        </view>

        <view class="form-row">
          <text class="label">{{ copy.locationLabel }}</text>
          <input class="input" v-model="form.location" :placeholder="copy.locationPlaceholder" />
        </view>

        <view class="time-grid">
          <view class="time-field">
            <text class="label">{{ copy.startLabel }}</text>
            <picker mode="time" :value="form.startTime" @change="onStartTimeChange">
              <view class="picker-box">{{ form.startTime }}</view>
            </picker>
          </view>
          <view class="time-field">
            <text class="label">{{ copy.endLabel }}</text>
            <picker mode="time" :value="form.endTime" @change="onEndTimeChange">
              <view class="picker-box">{{ form.endTime }}</view>
            </picker>
          </view>
        </view>

        <view class="primary-btn" :class="{ disabled: submitting }" @click="submitSchedule">
          <text class="primary-btn-text">{{ form.id ? copy.updateAction : copy.addAction }}</text>
        </view>
        <view class="ghost-btn" v-if="form.id" @click="resetForm">
          <text class="ghost-btn-text">{{ copy.cancelEdit }}</text>
        </view>
      </view>

      <view class="context-card" v-if="context">
        <view class="context-head">
          <text class="context-tag">{{ context.stateLabel || copy.contextFallback }}</text>
          <text class="context-count">{{ context.todayCourseCount || 0 }} {{ copy.courseCountUnit }}</text>
        </view>
        <text class="context-title">{{ contextTitle }}</text>
        <text class="context-reason">{{ context.recommendation }}</text>
      </view>

      <view class="list-card">
        <view class="list-head">
          <text class="list-title">{{ copy.listTitle }}</text>
          <text class="list-count">{{ scheduleList.length }}</text>
        </view>
        <view class="empty" v-if="!scheduleList.length">
          <text class="empty-text">{{ copy.emptyText }}</text>
        </view>
        <view class="schedule-item" v-for="item in scheduleList" :key="item.id">
          <view class="item-main" @click="editSchedule(item)">
            <text class="item-weekday">{{ weekdayName(item.weekday) }}</text>
            <text class="item-title">{{ item.courseName }}</text>
            <text class="item-meta">{{ item.startTime }} - {{ item.endTime }} {{ copy.metaSeparator }} {{ item.location || copy.noLocation }}</text>
          </view>
          <view class="delete-btn" @click="removeSchedule(item)">
            <text class="delete-text">{{ copy.deleteAction }}</text>
          </view>
        </view>
      </view>

      <view class="spacer"></view>
    </scroll-view>
  </view>
</template>

<script>
import {
  getScheduleList,
  getScheduleContext,
  saveSchedule,
  deleteSchedule
} from '../../utils/request';

const COPY = {
  kicker: 'CAMPUS TIME',
  title: '\u6211\u7684\u8bfe\u8868',
  subtitle: '\u8ba9 Agent \u6839\u636e\u4e0a\u8bfe\u548c\u7a7a\u95f2\u65f6\u95f4\u5b89\u6392\u4efb\u52a1',
  weekdayLabel: '\u661f\u671f',
  courseLabel: '\u8bfe\u7a0b',
  coursePlaceholder: '\u4f8b\u5982\uff1a\u9ad8\u6570',
  locationLabel: '\u5730\u70b9',
  locationPlaceholder: '\u4f8b\u5982\uff1a\u7b2c\u4e09\u6559\u5b66\u697c',
  startLabel: '\u5f00\u59cb',
  endLabel: '\u7ed3\u675f',
  addAction: '\u6dfb\u52a0\u8bfe\u7a0b',
  updateAction: '\u66f4\u65b0\u8bfe\u7a0b',
  cancelEdit: '\u53d6\u6d88\u7f16\u8f91',
  listTitle: '\u672c\u5468\u8bfe\u8868',
  deleteAction: '\u5220\u9664',
  emptyText: '\u6682\u65e0\u8bfe\u8868\uff0c\u5148\u6dfb\u52a0\u4e00\u8282\u8bfe',
  noLocation: '\u672a\u586b\u5730\u70b9',
  contextFallback: '\u8bfe\u8868\u8282\u594f',
  courseCountUnit: '\u8282\u8bfe',
  metaSeparator: '\u00b7',
  loginHint: '\u8bf7\u5148\u767b\u5f55',
  saveSuccess: '\u8bfe\u8868\u5df2\u4fdd\u5b58',
  deleteSuccess: '\u8bfe\u7a0b\u5df2\u5220\u9664',
  timeRangeError: '\u7ed3\u675f\u65f6\u95f4\u9700\u8981\u665a\u4e8e\u5f00\u59cb\u65f6\u95f4',
  deleteConfirmTitle: '\u5220\u9664\u8bfe\u7a0b',
  deleteConfirmContent: '\u5220\u9664\u540e Agent \u5c06\u4e0d\u518d\u4f7f\u7528\u8fd9\u8282\u8bfe\u8c03\u6574\u4eca\u65e5\u8282\u594f\u3002',
  deleteConfirmOk: '\u5220\u9664',
  deleteConfirmCancel: '\u53d6\u6d88',
  requestError: '\u64cd\u4f5c\u5931\u8d25\uff0c\u8bf7\u91cd\u8bd5'
};

const WEEKDAY_OPTIONS = [
  { value: 1, label: '\u5468\u4e00' },
  { value: 2, label: '\u5468\u4e8c' },
  { value: 3, label: '\u5468\u4e09' },
  { value: 4, label: '\u5468\u56db' },
  { value: 5, label: '\u5468\u4e94' },
  { value: 6, label: '\u5468\u516d' },
  { value: 7, label: '\u5468\u65e5' }
];

function emptyForm() {
  return {
    id: null,
    username: '',
    weekday: 1,
    courseName: '',
    location: '',
    startTime: '08:00',
    endTime: '09:40',
    remark: ''
  };
}

export default {
  data() {
    return {
      copy: COPY,
      weekdayOptions: WEEKDAY_OPTIONS,
      username: '',
      form: emptyForm(),
      scheduleList: [],
      context: null,
      submitting: false
    };
  },
  computed: {
    weekdayIndex() {
      const index = WEEKDAY_OPTIONS.findIndex(item => item.value === this.form.weekday);
      return index >= 0 ? index : 0;
    },
    contextTitle() {
      if (!this.context) {
        return COPY.contextFallback;
      }
      if (this.context.currentCourseName) {
        return `${this.context.currentCourseName} \u00b7 ${this.context.currentCourseTime || ''}`;
      }
      if (this.context.nextCourseName) {
        return `${this.context.nextCourseName} \u00b7 ${this.context.nextCourseTime || ''}`;
      }
      return this.context.stateLabel || COPY.contextFallback;
    }
  },
  onLoad() {
    const username = uni.getStorageSync('username');
    if (!username) {
      uni.showToast({ title: COPY.loginHint, icon: 'none' });
      return;
    }
    this.username = username;
    this.form.username = username;
    this.loadData();
  },
  methods: {
    async loadData() {
      await Promise.all([this.loadScheduleList(), this.loadContext()]);
    },
    async loadScheduleList() {
      try {
        this.scheduleList = await getScheduleList(this.username);
      } catch (error) {
        this.scheduleList = [];
      }
    },
    async loadContext() {
      try {
        this.context = await getScheduleContext(this.username);
      } catch (error) {
        this.context = null;
      }
    },
    onWeekdayChange(event) {
      const index = Number(event.detail.value || 0);
      this.form.weekday = WEEKDAY_OPTIONS[index].value;
    },
    onStartTimeChange(event) {
      this.form.startTime = event.detail.value;
    },
    onEndTimeChange(event) {
      this.form.endTime = event.detail.value;
    },
    async submitSchedule() {
      if (!this.username || this.submitting) {
        return;
      }
      const courseName = (this.form.courseName || '').trim();
      if (!courseName) {
        uni.showToast({ title: COPY.coursePlaceholder, icon: 'none' });
        return;
      }
      if (!this.isTimeRangeValid(this.form.startTime, this.form.endTime)) {
        uni.showToast({ title: COPY.timeRangeError, icon: 'none' });
        return;
      }
      this.submitting = true;
      try {
        await saveSchedule({
          ...this.form,
          username: this.username,
          courseName,
          location: (this.form.location || '').trim()
        });
        uni.showToast({ title: COPY.saveSuccess, icon: 'success' });
        this.resetForm();
        this.loadData();
      } catch (error) {
        uni.showToast({ title: COPY.requestError, icon: 'none' });
      } finally {
        this.submitting = false;
      }
    },
    editSchedule(item) {
      this.form = {
        id: item.id,
        username: this.username,
        weekday: item.weekday || 1,
        courseName: item.courseName || '',
        location: item.location || '',
        startTime: item.startTime || '08:00',
        endTime: item.endTime || '09:40',
        remark: item.remark || ''
      };
    },
    async removeSchedule(item) {
      if (!item || !item.id) {
        return;
      }
      const confirmed = await new Promise((resolve) => {
        uni.showModal({
          title: COPY.deleteConfirmTitle,
          content: COPY.deleteConfirmContent,
          confirmText: COPY.deleteConfirmOk,
          cancelText: COPY.deleteConfirmCancel,
          success: (res) => resolve(Boolean(res.confirm)),
          fail: () => resolve(false)
        });
      });
      if (!confirmed) {
        return;
      }
      try {
        await deleteSchedule(item.id, this.username);
        uni.showToast({ title: COPY.deleteSuccess, icon: 'success' });
        this.loadData();
      } catch (error) {
        uni.showToast({ title: COPY.requestError, icon: 'none' });
      }
    },
    resetForm() {
      this.form = emptyForm();
      this.form.username = this.username;
    },
    weekdayName(value) {
      const match = WEEKDAY_OPTIONS.find(item => item.value === value);
      return match ? match.label : COPY.weekdayLabel;
    },
    isTimeRangeValid(startTime, endTime) {
      const start = String(startTime || '');
      const end = String(endTime || '');
      return /^\d{2}:\d{2}$/.test(start) && /^\d{2}:\d{2}$/.test(end) && start < end;
    }
  }
};
</script>

<style scoped>
.page {
  min-height: 100vh;
  background: linear-gradient(180deg, #edf7ef 0%, #dceee2 100%);
}

.scroller {
  height: 100vh;
  padding: 88rpx 28rpx 0;
  box-sizing: border-box;
}

.header {
  margin-bottom: 24rpx;
}

.header-kicker {
  display: block;
  font-size: 22rpx;
  letter-spacing: 2rpx;
  color: #4d8b62;
}

.header-title {
  display: block;
  margin-top: 10rpx;
  font-size: 44rpx;
  font-weight: 800;
  color: #214d33;
}

.header-subtitle {
  display: block;
  margin-top: 12rpx;
  font-size: 24rpx;
  line-height: 1.7;
  color: #607263;
}

.form-card,
.context-card,
.list-card {
  margin-bottom: 24rpx;
  padding: 28rpx;
  border-radius: 30rpx;
  background: rgba(255, 255, 255, 0.92);
  box-shadow: 0 16rpx 32rpx rgba(42, 88, 57, 0.08);
  box-sizing: border-box;
}

.form-row {
  margin-bottom: 20rpx;
}

.label {
  display: block;
  margin-bottom: 10rpx;
  font-size: 24rpx;
  font-weight: 700;
  color: #315d42;
}

.input,
.picker-box {
  width: 100%;
  height: 78rpx;
  padding: 0 22rpx;
  border-radius: 22rpx;
  background: #f4fbf5;
  border: 2rpx solid rgba(73, 142, 93, 0.14);
  font-size: 26rpx;
  color: #234b32;
  box-sizing: border-box;
  display: flex;
  align-items: center;
}

.time-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 18rpx;
}

.primary-btn,
.ghost-btn {
  height: 82rpx;
  margin-top: 20rpx;
  border-radius: 999rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.primary-btn {
  background: linear-gradient(135deg, #3d8c5b, #2c6f46);
  box-shadow: 0 12rpx 24rpx rgba(44, 111, 70, 0.22);
}

.primary-btn.disabled {
  opacity: 0.7;
}

.ghost-btn {
  background: rgba(65, 131, 83, 0.08);
  border: 1rpx solid rgba(65, 131, 83, 0.16);
}

.primary-btn-text {
  font-size: 26rpx;
  font-weight: 700;
  color: #ffffff;
}

.ghost-btn-text {
  font-size: 24rpx;
  font-weight: 700;
  color: #2d6240;
}

.context-head,
.list-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 18rpx;
}

.context-tag {
  padding: 8rpx 16rpx;
  border-radius: 999rpx;
  background: rgba(46, 125, 50, 0.12);
  font-size: 22rpx;
  font-weight: 700;
  color: #2f7d4c;
}

.context-count,
.list-count {
  font-size: 22rpx;
  color: #6f8778;
}

.context-title {
  display: block;
  margin-top: 18rpx;
  font-size: 34rpx;
  font-weight: 800;
  color: #214d33;
}

.context-reason {
  display: block;
  margin-top: 12rpx;
  font-size: 24rpx;
  line-height: 1.7;
  color: #607263;
}

.list-title {
  font-size: 30rpx;
  font-weight: 800;
  color: #214d33;
}

.empty {
  padding: 34rpx 0;
  text-align: center;
}

.empty-text {
  font-size: 24rpx;
  color: #6f8778;
}

.schedule-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 18rpx;
  margin-top: 18rpx;
  padding: 20rpx;
  border-radius: 24rpx;
  background: #f8fbf8;
  border: 1rpx solid rgba(59, 121, 79, 0.08);
}

.item-main {
  flex: 1;
}

.item-weekday {
  display: inline-flex;
  padding: 6rpx 12rpx;
  border-radius: 999rpx;
  background: rgba(61, 140, 91, 0.1);
  font-size: 20rpx;
  font-weight: 700;
  color: #35674a;
}

.item-title {
  display: block;
  margin-top: 12rpx;
  font-size: 28rpx;
  font-weight: 800;
  color: #21462f;
}

.item-meta {
  display: block;
  margin-top: 8rpx;
  font-size: 22rpx;
  color: #6d8776;
}

.delete-btn {
  padding: 14rpx 18rpx;
  border-radius: 18rpx;
  background: rgba(222, 92, 92, 0.08);
}

.delete-text {
  font-size: 22rpx;
  font-weight: 700;
  color: #c65a5a;
}

.spacer {
  height: 60rpx;
}
</style>

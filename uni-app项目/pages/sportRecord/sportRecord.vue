<template>
  <view class="container">
    <view class="form-section">
      <text class="section-title">添加运动记录</text>

      <text class="label">运动类型：</text>
      <picker :range="sportTypes" :value="sportTypeIndex" @change="onSportTypeChange">
        <view class="picker">{{ sportTypes[sportTypeIndex] }}</view>
      </picker>

      <text class="label">开始时间：</text>
      <picker mode="datetime" :value="startTime" @change="onStartTimeChange">
        <view class="picker">{{ startTime }}</view>
      </picker>

      <text class="label">结束时间：</text>
      <picker mode="datetime" :value="endTime" @change="onEndTimeChange">
        <view class="picker">{{ endTime }}</view>
      </picker>

      <text class="label">运动量（公里）：</text>
      <input
        v-model="amount"
        class="input"
        type="digit"
        placeholder="请输入运动距离"
      />

      <button class="btn" @click="submitRecord" :disabled="submitting">
        {{ submitting ? '提交中...' : '提交记录' }}
      </button>
    </view>

    <view class="record-section">
      <text class="section-title">运动记录列表</text>
      <view v-if="recordList.length > 0">
        <view class="record-item" v-for="item in recordList" :key="item.id">
          <view class="record-row">
            <text class="record-type">{{ item.sportType }}</text>
            <text class="record-amount">{{ item.amount }} km</text>
          </view>
          <view class="record-row">
            <text class="record-time">{{ formatTime(item.startTime) }} ~ {{ formatTime(item.endTime) }}</text>
          </view>
        </view>
      </view>
      <view class="empty-section" v-else>
        <text class="empty-text">暂无运动记录</text>
      </view>
    </view>
  </view>
</template>

<script>
import { getSportRecord, saveSportRecord } from '../../utils/request.js';

export default {
  data() {
    return {
      sportTypes: ['走路', '跑步'],
      sportTypeIndex: 0,
      startTime: new Date().toISOString().slice(0, 16).replace('T', ' '),
      endTime: new Date().toISOString().slice(0, 16).replace('T', ' '),
      amount: '',
      recordList: [],
      submitting: false,
      stuNo: ''
    };
  },
  onLoad() {
    const stuNo = uni.getStorageSync('stuNo');
    if (!stuNo) {
      uni.reLaunch({ url: '/pages/login/login' });
      return;
    }
    this.stuNo = stuNo;
    this.loadRecords();
  },
  methods: {
    onSportTypeChange(e) {
      this.sportTypeIndex = e.detail.value;
    },
    onStartTimeChange(e) {
      this.startTime = e.detail.value;
    },
    onEndTimeChange(e) {
      this.endTime = e.detail.value;
    },
    async submitRecord() {
      if (!this.amount) {
        uni.showToast({ title: '请输入运动量', icon: 'none' });
        return;
      }
      this.submitting = true;
      try {
        await saveSportRecord({
          studentId: this.stuNo,
          sportType: this.sportTypes[this.sportTypeIndex],
          startTime: this.startTime,
          endTime: this.endTime,
          amount: parseFloat(this.amount)
        });
        uni.showToast({ title: '提交成功', icon: 'success' });
        this.amount = '';
        this.loadRecords();
      } catch (e) {
        uni.showToast({ title: '提交失败', icon: 'none' });
      } finally {
        this.submitting = false;
      }
    },
    async loadRecords() {
      try {
        const res = await getSportRecord(this.stuNo);
        this.recordList = res || [];
      } catch (e) {
        // 静默失败
      }
    },
    formatTime(timeStr) {
      if (!timeStr) return '';
      return String(timeStr).substring(0, 16).replace('T', ' ');
    }
  }
};
</script>

<style scoped>
.container {
  padding: 20rpx;
  background-color: #f5f5f5;
  min-height: 100vh;
}

.form-section,
.record-section {
  background-color: #fff;
  border-radius: 16rpx;
  padding: 30rpx;
  margin-bottom: 30rpx;
  box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.1);
}

.section-title {
  display: block;
  font-size: 30rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 30rpx;
}

.label {
  display: block;
  font-size: 28rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 16rpx;
}

.picker,
.input {
  width: 100%;
  height: 80rpx;
  border: 2rpx solid #e0e0e0;
  border-radius: 8rpx;
  padding: 0 20rpx;
  line-height: 80rpx;
  font-size: 24rpx;
  margin-bottom: 30rpx;
  box-sizing: border-box;
}

.btn {
  width: 100%;
  height: 80rpx;
  background-color: #4CAF50;
  color: #fff;
  font-size: 28rpx;
  font-weight: bold;
  border-radius: 8rpx;
  border: none;
}

.btn[disabled] {
  background-color: #a5d6a7;
}

.record-item {
  padding: 20rpx 0;
  border-bottom: 1rpx solid #f0f0f0;
}

.record-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8rpx;
}

.record-type {
  font-size: 28rpx;
  font-weight: bold;
  color: #333;
}

.record-amount {
  font-size: 28rpx;
  color: #4CAF50;
  font-weight: bold;
}

.record-time {
  font-size: 22rpx;
  color: #999;
}

.empty-section {
  padding: 40rpx 0;
  text-align: center;
}

.empty-text {
  font-size: 26rpx;
  color: #999;
}
</style>

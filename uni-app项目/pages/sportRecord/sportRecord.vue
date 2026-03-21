<template>
  <view class="container">
    <!-- 装饰元素 -->
    <view class="decorations">
      <view class="leaf leaf-1">🍃</view>
      <view class="leaf leaf-2">🍃</view>
      <view class="leaf leaf-3">🍃</view>
      <view class="leaf leaf-4">🍃</view>
    </view>
    
    <!-- 顶部标题 -->
    <view class="page-header">
      <text class="header-icon">🌞</text>
      <text class="header-title">运动记录</text>
      <text class="header-icon">🍃</text>
    </view>

    <view class="form-section" :class="{ 'loaded': isLoaded }">
      <text class="section-title">添加运动记录</text>

      <text class="label">运动类型：</text>
      <picker :range="sportTypes" :value="sportTypeIndex" @change="onSportTypeChange">
        <view class="picker">
          <text class="icon-sport">🏃</text>
          <text>{{ sportTypes[sportTypeIndex] }}</text>
        </view>
      </picker>

      <text class="label">开始时间：</text>
      <picker mode="datetime" :value="startTime" @change="onStartTimeChange">
        <view class="picker">
          <text class="icon-time">🕒</text>
          <text>{{ startTime }}</text>
        </view>
      </picker>

      <text class="label">结束时间：</text>
      <picker mode="datetime" :value="endTime" @change="onEndTimeChange">
        <view class="picker">
          <text class="icon-time">🕒</text>
          <text>{{ endTime }}</text>
        </view>
      </picker>

      <text class="label">运动量（公里）：</text>
      <view class="input-container">
        <text class="icon-step">👣</text>
        <input
          v-model="amount"
          class="input"
          type="digit"
          placeholder="请输入运动距离"
        />
        <text class="input-unit">km</text>
      </view>

      <button class="btn primary" @click="submitRecord" :loading="submitting">
        <text class="btn-text">{{ submitting ? '提交中...' : '提交记录' }}</text>
      </button>
    </view>

    <view class="record-section" v-if="recordList.length > 0" :class="{ 'loaded': isLoaded }">
      <text class="section-title">运动记录列表</text>
      <view class="record-list">
        <view class="history-item" v-for="item in recordList" :key="item.id">
          <view class="record-content">
            <text class="record-type">{{ item.sportType }}</text>
            <text class="record-amount">{{ item.amount }} km</text>
            <text class="record-time">{{ formatTime(item.startTime) }} ~ {{ formatTime(item.endTime) }}</text>
          </view>
        </view>
      </view>
    </view>
    <view class="empty-section" v-else :class="{ 'loaded': isLoaded }">
      <text class="empty-text">暂无运动记录</text>
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
      stuNo: '',
      isLoaded: false
    };
  },
  onLoad() {
    const stuNo = uni.getStorageSync('username');
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
        this.isLoaded = true;
      } catch (e) {
        // 静默失败
        this.isLoaded = true;
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
  min-height: 100vh;
  background: linear-gradient(135deg, #f1f8e9 0%, #ffffff 100%);
  position: relative;
  overflow: hidden;
}

/* 装饰元素 */
.decorations {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  pointer-events: none;
  z-index: 0;
}

.leaf {
  position: absolute;
  font-size: 32rpx;
  opacity: 0.6;
  animation: float 10s ease-in-out infinite;
}

.leaf-1 {
  top: 10%;
  left: 5%;
  animation-delay: 0s;
}

.leaf-2 {
  top: 30%;
  right: 8%;
  animation-delay: 2s;
}

.leaf-3 {
  bottom: 20%;
  left: 10%;
  animation-delay: 4s;
}

.leaf-4 {
  bottom: 40%;
  right: 5%;
  animation-delay: 6s;
}

@keyframes float {
  0%, 100% {
    transform: translateY(0) rotate(0deg);
  }
  50% {
    transform: translateY(-20px) rotate(10deg);
  }
}

/* 顶部标题 */
.page-header {
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 30rpx 0 40rpx;
  position: relative;
  z-index: 1;
}

.header-icon {
  font-size: 36rpx;
  margin: 0 12rpx;
  animation: bounce 2s ease-in-out infinite;
}

.header-title {
  font-size: 36rpx;
  font-weight: 700;
  color: #2d5016;
  letter-spacing: 2rpx;
  text-shadow: 0 2px 4px rgba(45, 80, 22, 0.1);
}

@keyframes bounce {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-5px);
  }
}

/* 表单区域 */
.form-section {
  margin-bottom: 30rpx;
  padding: 30rpx;
  border-radius: 16px;
  background: linear-gradient(135deg, #f1f8e9 0%, #ffffff 100%);
  box-shadow: 0 8px 32px rgba(45, 80, 22, 0.08);
  border: 1px solid rgba(139, 191, 159, 0.2);
  opacity: 0;
  transform: translateY(20px);
  transition: all 0.6s ease;
  position: relative;
  z-index: 1;
}

.form-section.loaded {
  opacity: 1;
  transform: translateY(0);
}

/* 记录区域 */
.record-section {
  margin-bottom: 30rpx;
  padding: 30rpx;
  border-radius: 16px;
  background: linear-gradient(135deg, #f1f8e9 0%, #ffffff 100%);
  box-shadow: 0 8px 32px rgba(45, 80, 22, 0.08);
  border: 1px solid rgba(139, 191, 159, 0.2);
  opacity: 0;
  transform: translateY(20px);
  transition: all 0.6s ease 0.2s;
  position: relative;
  z-index: 1;
}

.record-section.loaded {
  opacity: 1;
  transform: translateY(0);
}

.section-title {
  display: block;
  font-size: 28rpx;
  font-weight: 600;
  color: #2d5016;
  letter-spacing: 0.5px;
  margin-bottom: 30rpx;
}

/* 标签 */
.label {
  display: block;
  font-size: 24rpx;
  font-weight: 500;
  color: #2d5016;
  margin-bottom: 12rpx;
  letter-spacing: 0.5px;
}

/* 选择器 */
.picker {
  display: flex;
  align-items: center;
  height: 80rpx;
  background: rgba(255, 255, 255, 0.9);
  border: 2rpx solid rgba(139, 191, 159, 0.3);
  border-radius: 12rpx;
  padding: 0 20rpx;
  margin-bottom: 24rpx;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(45, 80, 22, 0.05);
}

.picker:hover {
  border-color: #6a9d87;
  box-shadow: 0 4px 12px rgba(45, 80, 22, 0.1);
}

.picker .icon-sport,
.picker .icon-time {
  font-size: 28rpx;
  margin-right: 16rpx;
  color: #6a9d87;
}

.picker text {
  flex: 1;
  font-size: 24rpx;
  color: #2d5016;
}

/* 输入容器 */
.input-container {
  display: flex;
  align-items: center;
  height: 80rpx;
  background: rgba(255, 255, 255, 0.9);
  border: 2rpx solid rgba(139, 191, 159, 0.3);
  border-radius: 12rpx;
  padding: 0 20rpx;
  margin-bottom: 24rpx;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(45, 80, 22, 0.05);
}

.input-container:hover {
  border-color: #6a9d87;
  box-shadow: 0 4px 12px rgba(45, 80, 22, 0.1);
}

.input-container .icon-step {
  font-size: 28rpx;
  margin-right: 16rpx;
  color: #6a9d87;
}

.input-container .input {
  flex: 1;
  height: 100%;
  border: none;
  outline: none;
  font-size: 24rpx;
  color: #2d5016;
  background: transparent;
}

.input-container .input::placeholder {
  color: #a5d6a7;
}

.input-container .input-unit {
  font-size: 24rpx;
  color: #6a9d87;
  font-weight: 500;
  margin-left: 12rpx;
}

/* 按钮 */
.btn {
  width: 100%;
  height: 88rpx;
  border-radius: 12rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 26rpx;
  font-weight: 600;
  transition: all 0.3s ease;
  margin-bottom: 16rpx;
  border: none;
  position: relative;
  overflow: hidden;
}

.btn::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.3), transparent);
  transition: left 0.6s ease;
}

.btn:hover::before {
  left: 100%;
}

.btn.primary {
  background: linear-gradient(135deg, #2d5016, #6a9d87);
  color: #ffffff;
  box-shadow: 0 4px 16px rgba(45, 80, 22, 0.3);
}

.btn.primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(45, 80, 22, 0.4);
}

.btn.primary:active {
  transform: translateY(0);
  box-shadow: 0 2px 8px rgba(45, 80, 22, 0.3);
}

.btn.secondary {
  background: rgba(139, 191, 159, 0.1);
  color: #2d5016;
  border: 2rpx solid rgba(139, 191, 159, 0.3);
}

.btn.secondary:hover {
  background: rgba(139, 191, 159, 0.2);
  border-color: #6a9d87;
  transform: translateY(-2px);
}

.btn-text {
  position: relative;
  z-index: 1;
  letter-spacing: 1px;
}

/* 记录列表 */
.record-list {
  margin-top: 20rpx;
}

.history-item {
  display: flex;
  align-items: center;
  padding: 24rpx;
  margin-bottom: 16rpx;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 12px;
  box-shadow: 0 4px 16px rgba(45, 80, 22, 0.05);
  transition: all 0.3s ease;
  border: 1px solid rgba(139, 191, 159, 0.2);
}

.history-item:hover,
.history-item.hover {
  transform: translateX(10px);
  background: rgba(240, 249, 232, 0.9);
  box-shadow: 0 6px 20px rgba(45, 80, 22, 0.1);
}

.record-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8rpx;
}

.record-type {
  font-size: 24rpx;
  font-weight: 500;
  color: #2d5016;
}

.record-amount {
  font-size: 28rpx;
  font-weight: 600;
  color: #6a9d87;
  margin-top: 4rpx;
}

.record-time {
  font-size: 20rpx;
  color: #a5d6a7;
  margin-top: 4rpx;
}

/* 空状态 */
.empty-section {
  text-align: center;
  padding: 60rpx 30rpx;
  margin-bottom: 30rpx;
  border-radius: 16px;
  background: linear-gradient(135deg, #f1f8e9 0%, #ffffff 100%);
  box-shadow: 0 8px 32px rgba(45, 80, 22, 0.08);
  border: 1px solid rgba(139, 191, 159, 0.2);
  opacity: 0;
  transform: translateY(20px);
  transition: all 0.6s ease 0.2s;
  position: relative;
  z-index: 1;
}

.empty-section.loaded {
  opacity: 1;
  transform: translateY(0);
}

.empty-text {
  font-size: 26rpx;
  color: #6a9d87;
  font-weight: 500;
}

/* 加载状态 */
.btn[loading] {
  opacity: 0.8;
}

/* 响应式调整 */
@media (max-width: 375px) {
  .container {
    padding: 16rpx;
  }
  
  .form-section,
  .record-section {
    padding: 24rpx;
  }
  
  .header-title {
    font-size: 32rpx;
  }
  
  .section-title {
    font-size: 26rpx;
  }
  
  .picker,
  .input-container {
    height: 72rpx;
  }
  
  .btn {
    height: 80rpx;
  }
}
</style>

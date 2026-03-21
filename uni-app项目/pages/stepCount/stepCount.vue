<template>
  <view class="container">
    <view class="date-section">
      <text class="label">选择日期：</text>
      <picker mode="date" :value="date" @change="onDateChange">
        <view class="picker">{{ date }}</view>
      </picker>
      <button class="btn" @click="queryStepCount">查询步数</button>
    </view>

    <view class="step-section" v-if="stepData">
      <text class="step-title">步数统计</text>
      <view class="step-info">
        <view class="step-item">
          <text class="step-label">步数：</text>
          <text class="step-value">{{ stepData.steps || 0 }} 步</text>
        </view>
        <view class="step-item">
          <text class="step-label">卡路里：</text>
          <text class="step-value">{{ stepData.calories || 0 }} kcal</text>
        </view>
        <view class="step-item">
          <text class="step-label">运动时长：</text>
          <text class="step-value">{{ stepData.duration || 0 }} 分钟</text>
        </view>
      </view>
    </view>

    <view class="empty-section" v-if="queried && !stepData">
      <text class="empty-text">该日期暂无步数记录</text>
    </view>

    <view class="record-section">
      <text class="record-title">近期步数记录</text>
      <view v-if="historyList.length > 0">
        <view class="history-item" v-for="item in historyList" :key="item.id">
          <text class="history-date">{{ formatDate(item.date) }}</text>
          <text class="history-steps">{{ item.steps }} 步</text>
        </view>
      </view>
      <view class="empty-section" v-else>
        <text class="empty-text">暂无历史记录</text>
      </view>
    </view>
  </view>
</template>

<script>
import { getStepCount, getStepCountList } from '../../utils/request.js';

export default {
  data() {
    return {
      date: new Date().toISOString().split('T')[0],
      stepData: null,
      historyList: [],
      queried: false,
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
    this.loadHistory();
  },
  methods: {
    onDateChange(e) {
      this.date = e.detail.value;
    },
    async queryStepCount() {
      uni.showLoading({ title: '查询中...' });
      try {
        const res = await getStepCount(this.stuNo, this.date);
        this.stepData = res;
        this.queried = true;
      } catch (e) {
        uni.showToast({ title: '查询失败', icon: 'none' });
      } finally {
        uni.hideLoading();
      }
    },
    async loadHistory() {
      try {
        const res = await getStepCountList(this.stuNo);
        this.historyList = res || [];
      } catch (e) {
        // 静默失败
      }
    },
    formatDate(dateStr) {
      if (!dateStr) return '';
      return String(dateStr).substring(0, 10);
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

.date-section {
  background-color: #fff;
  border-radius: 16rpx;
  padding: 30rpx;
  margin-bottom: 30rpx;
  box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.1);
}

.label {
  display: block;
  font-size: 28rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 20rpx;
}

.picker {
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

.step-section {
  background-color: #fff;
  border-radius: 16rpx;
  padding: 30rpx;
  margin-bottom: 30rpx;
  box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.1);
}

.step-title {
  display: block;
  font-size: 28rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 20rpx;
}

.step-info {
  display: flex;
  flex-direction: column;
  gap: 20rpx;
}

.step-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15rpx 0;
  border-bottom: 1rpx solid #e0e0e0;
}

.step-label {
  font-size: 24rpx;
  color: #666;
}

.step-value {
  font-size: 28rpx;
  font-weight: bold;
  color: #4CAF50;
}

.record-section {
  background-color: #fff;
  border-radius: 16rpx;
  padding: 30rpx;
  box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.1);
}

.record-title {
  display: block;
  font-size: 28rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 20rpx;
}

.history-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20rpx 0;
  border-bottom: 1rpx solid #f0f0f0;
}

.history-date {
  font-size: 26rpx;
  color: #666;
}

.history-steps {
  font-size: 26rpx;
  font-weight: bold;
  color: #333;
}

.empty-section {
  background-color: #fff;
  border-radius: 16rpx;
  padding: 60rpx 30rpx;
  margin-bottom: 30rpx;
  box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.1);
  text-align: center;
}

.empty-text {
  font-size: 26rpx;
  color: #999;
}
</style>

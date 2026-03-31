<template>
  <view class="page">
    <!-- 动态渐变背景 -->
    <view class="gradient-bg"></view>

    <!-- 动态粒子系统 -->
    <view class="particle-system">
      <view v-for="(item, index) in 40" :key="index" class="particle" :style="particleStyles[index]"></view>
    </view>

    <scroll-view scroll-y="true" class="scroller" :show-scrollbar="false">
      <!-- 顶部标题 -->
      <view class="page-header floating-card">
        <view class="header-icon-wrapper">
          <view class="header-icon-ring"></view>
          <text class="header-icon">🏃</text>
        </view>
        <text class="header-title">运动记录</text>
        <view class="header-icon-wrapper">
          <view class="header-icon-ring"></view>
          <text class="header-icon">🍃</text>
        </view>
      </view>

      <!-- 添加运动记录卡片 -->
      <view class="card floating-card">
        <view class="card-header">
          <view class="header-dot"></view>
          <text class="card-title">📝 添加运动记录</text>
        </view>

        <!-- 运动类型选择 -->
        <view class="label">
          <text class="label-icon">🏃</text>
          <text class="label-text">运动类型</text>
        </view>
        <picker :range="sportTypes" :value="sportTypeIndex" @change="onSportTypeChange">
          <view class="picker-item">
            <text class="picker-value">{{ sportTypes[sportTypeIndex] }}</text>
            <text class="picker-arrow">▼</text>
          </view>
        </picker>

        <!-- 开始时间 -->
        <view class="label">
          <text class="label-icon">🕒</text>
          <text class="label-text">开始时间</text>
        </view>
        <picker mode="datetime" :value="startTime" @change="onStartTimeChange">
          <view class="picker-item">
            <text class="picker-value">{{ startTime }}</text>
            <text class="picker-arrow">▼</text>
          </view>
        </picker>

        <!-- 结束时间 -->
        <view class="label">
          <text class="label-icon">🕒</text>
          <text class="label-text">结束时间</text>
        </view>
        <picker mode="datetime" :value="endTime" @change="onEndTimeChange">
          <view class="picker-item">
            <text class="picker-value">{{ endTime }}</text>
            <text class="picker-arrow">▼</text>
          </view>
        </picker>

        <!-- 运动距离 -->
        <view class="label">
          <text class="label-icon">👣</text>
          <text class="label-text">运动距离</text>
        </view>
        <view class="input-wrapper" :class="{ focused: isFocused }">
          <input
              v-model="amount"
              class="input"
              type="digit"
              placeholder="请输入运动距离"
              placeholder-class="input-placeholder"
              @focus="isFocused = true"
              @blur="isFocused = false"
          />
          <text class="input-unit">km</text>
          <view class="input-glow"></view>
        </view>

        <!-- 提交按钮 -->
        <view class="btn-submit" @click="submitRecord" :class="{ loading: submitting }">
          <view class="btn-shine"></view>
          <text class="btn-text" v-if="!submitting">📤 提交记录</text>
          <view class="loading-dots" v-else>
            <view class="dot"></view>
            <view class="dot"></view>
            <view class="dot"></view>
          </view>
        </view>
      </view>

      <!-- 运动记录列表 -->
      <view class="card floating-card" v-if="recordList.length > 0">
        <view class="card-header">
          <view class="header-dot"></view>
          <text class="card-title">📋 运动记录列表</text>
          <text class="card-count">{{ recordList.length }}条</text>
        </view>

        <view class="record-list">
          <view class="record-item" v-for="(item, index) in recordList" :key="index">
            <view class="record-icon">
              <text>{{ item.sportType === '走路' ? '🚶' : '🏃' }}</text>
            </view>
            <view class="record-info">
              <text class="record-type">{{ item.sportType }}</text>
              <text class="record-amount">{{ item.amount }} km</text>
              <text class="record-time">{{ formatTime(item.startTime) }} → {{ formatTime(item.endTime) }}</text>
            </view>
            <view class="record-arrow">→</view>
          </view>
        </view>
      </view>

      <!-- 空状态 -->
      <view class="empty-card floating-card" v-else>
        <view class="empty-icon-wrapper">
          <view class="empty-glow"></view>
          <text class="empty-icon">🏃</text>
        </view>
        <text class="empty-title">暂无运动记录</text>
        <text class="empty-sub">开始记录你的运动吧</text>
      </view>

      <!-- 底部装饰 -->
      <view class="footer-deco">
        <text class="deco-text">💪 运动让生活更美好 · 低碳让地球更健康 🌍</text>
      </view>

      <view class="spacer"></view>
    </scroll-view>
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
      isFocused: false,
      particleStyles: []
    };
  },
  onLoad() {
    const stuNo = uni.getStorageSync('username');
    if (!stuNo) {
      uni.reLaunch({ url: '/pages/login/login' });
      return;
    }
    this.stuNo = stuNo;
    this.initParticleStyles();
    this.loadRecords();
  },
  methods: {
    initParticleStyles() {
      const styles = [];
      for (let i = 0; i < 40; i++) {
        styles.push({
          left: Math.random() * 100 + '%',
          animationDuration: (8 + Math.random() * 12) + 's',
          animationDelay: Math.random() * 8 + 's',
          width: (2 + Math.random() * 6) + 'rpx',
          height: (2 + Math.random() * 6) + 'rpx',
          opacity: 0.2 + Math.random() * 0.4
        });
      }
      this.particleStyles = styles;
    },
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
        uni.showToast({ title: '请输入运动距离', icon: 'none' });
        return;
      }
      if (parseFloat(this.amount) <= 0) {
        uni.showToast({ title: '请输入有效的运动距离', icon: 'none' });
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
        console.error('加载记录失败:', e);
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
.page {
  min-height: 100vh;
  background: linear-gradient(180deg, #E0F2E9 0%, #C8E6D9 100%);
  position: relative;
  overflow: hidden;
}

.gradient-bg {
  position: fixed;
  top: -20%;
  left: -20%;
  right: -20%;
  bottom: -20%;
  background: radial-gradient(ellipse at 30% 40%,
  rgba(100, 200, 150, 0.3) 0%,
  rgba(80, 180, 130, 0.15) 50%,
  rgba(60, 160, 110, 0.05) 100%);
  animation: bgMove 20s ease-in-out infinite;
  z-index: 0;
}

@keyframes bgMove {
  0%, 100% { transform: translate(0, 0) scale(1); }
  50% { transform: translate(2%, 1%) scale(1.05); }
}

.particle-system {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  pointer-events: none;
  z-index: 1;
}

.particle {
  position: absolute;
  background: rgba(80, 200, 140, 0.6);
  border-radius: 50%;
  animation: floatUp linear infinite;
}

@keyframes floatUp {
  0% {
    transform: translateY(100vh);
    opacity: 0;
  }
  10% {
    opacity: 0.8;
  }
  90% {
    opacity: 0.5;
  }
  100% {
    transform: translateY(-20vh);
    opacity: 0;
  }
}

.scroller {
  position: relative;
  z-index: 2;
  height: 100vh;
  padding: 0 32rpx;
  box-sizing: border-box;
}

.floating-card {
  animation: floatCard 3s ease-in-out infinite;
}

@keyframes floatCard {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-6rpx); }
}

/* 页面标题 */
.page-header {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-top: 100rpx;
  margin-bottom: 32rpx;
  gap: 24rpx;
}

.header-icon-wrapper {
  position: relative;
  width: 56rpx;
  height: 56rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.header-icon-ring {
  position: absolute;
  width: 100%;
  height: 100%;
  border: 2rpx solid rgba(61, 155, 109, 0.4);
  border-radius: 50%;
  animation: ringPulse 2s ease-in-out infinite;
}

@keyframes ringPulse {
  0%, 100% { transform: scale(1); opacity: 0.5; }
  50% { transform: scale(1.2); opacity: 1; }
}

.header-icon {
  font-size: 36rpx;
  position: relative;
  z-index: 2;
}

.header-title {
  font-size: 44rpx;
  font-weight: 700;
  background: linear-gradient(135deg, #2C5A3A, #3D9B6D);
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
}

/* 卡片样式 */
.card {
  background: rgba(255, 255, 255, 0.9);
  border-radius: 48rpx;
  padding: 36rpx 32rpx;
  margin-bottom: 28rpx;
  box-shadow: 0 12rpx 32rpx rgba(0, 0, 0, 0.08);
}

.card-header {
  display: flex;
  align-items: center;
  gap: 12rpx;
  margin-bottom: 32rpx;
}

.header-dot {
  width: 8rpx;
  height: 32rpx;
  background: linear-gradient(180deg, #6FB88A, #3D9B6D);
  border-radius: 4rpx;
}

.card-title {
  font-size: 30rpx;
  font-weight: 700;
  color: #2C5A3A;
  flex: 1;
}

.card-count {
  font-size: 24rpx;
  color: #9CC0AC;
  background: rgba(100, 200, 150, 0.15);
  padding: 6rpx 16rpx;
  border-radius: 30rpx;
}

/* 标签 */
.label {
  display: flex;
  align-items: center;
  gap: 12rpx;
  margin-bottom: 16rpx;
  margin-top: 24rpx;
}

.label:first-of-type {
  margin-top: 0;
}

.label-icon {
  font-size: 28rpx;
}

.label-text {
  font-size: 26rpx;
  font-weight: 500;
  color: #5B8C6E;
}

/* 选择器 */
.picker-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: rgba(255, 255, 255, 0.6);
  border-radius: 60rpx;
  padding: 20rpx 28rpx;
  border: 1rpx solid rgba(100, 200, 150, 0.3);
  margin-bottom: 8rpx;
}

.picker-value {
  font-size: 28rpx;
  color: #2C5A3A;
  flex: 1;
}

.picker-arrow {
  font-size: 24rpx;
  color: #9CC0AC;
}

/* 输入框 */
.input-wrapper {
  position: relative;
  margin-bottom: 8rpx;
}

.input {
  width: 100%;
  height: 96rpx;
  background: rgba(255, 255, 255, 0.6);
  border: 2rpx solid rgba(100, 200, 150, 0.3);
  border-radius: 48rpx;
  padding: 0 80rpx 0 32rpx;
  font-size: 32rpx;
  color: #2C5A3A;
  box-sizing: border-box;
  transition: all 0.3s ease;
}

.input:focus {
  border-color: #6FB88A;
  box-shadow: 0 0 0 4rpx rgba(111, 184, 138, 0.2);
  outline: none;
}

.input-placeholder {
  color: #C8E6C9;
}

.input-unit {
  position: absolute;
  right: 28rpx;
  top: 50%;
  transform: translateY(-50%);
  font-size: 26rpx;
  color: #6FB88A;
}

.input-glow {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 0;
  background: linear-gradient(90deg, #6FB88A, #3D9B6D);
  border-radius: 24rpx;
  filter: blur(8rpx);
  transition: all 0.3s ease;
  pointer-events: none;
}

.input-wrapper.focused .input-glow {
  height: 60rpx;
  opacity: 0.3;
}

/* 提交按钮 */
.btn-submit {
  width: 100%;
  height: 96rpx;
  background: linear-gradient(135deg, #6FB88A, #3D9B6D);
  border-radius: 48rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-top: 32rpx;
  position: relative;
  overflow: hidden;
  transition: all 0.3s cubic-bezier(0.2, 0.9, 0.4, 1.1);
  box-shadow: 0 8rpx 24rpx rgba(61, 155, 109, 0.4);
}

.btn-submit:active {
  transform: scale(0.97);
}

.btn-shine {
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.3), transparent);
  animation: btnShine 3s ease-in-out infinite;
}

@keyframes btnShine {
  0% { left: -100%; }
  60%, 100% { left: 100%; }
}

.btn-text {
  font-size: 28rpx;
  font-weight: 600;
  color: #FFFFFF;
  letter-spacing: 2rpx;
  position: relative;
  z-index: 2;
}

.loading-dots {
  display: flex;
  gap: 12rpx;
  position: relative;
  z-index: 2;
}

.loading-dots .dot {
  width: 12rpx;
  height: 12rpx;
  background: #FFFFFF;
  border-radius: 50%;
  animation: dotBlink 1.2s ease-in-out infinite;
}

.loading-dots .dot:nth-child(1) { animation-delay: 0s; }
.loading-dots .dot:nth-child(2) { animation-delay: 0.2s; }
.loading-dots .dot:nth-child(3) { animation-delay: 0.4s; }

@keyframes dotBlink {
  0%, 80%, 100% { opacity: 0.3; transform: scale(0.8); }
  40% { opacity: 1; transform: scale(1.2); }
}

/* 记录列表 */
.record-list {
  display: flex;
  flex-direction: column;
  gap: 16rpx;
}

.record-item {
  display: flex;
  align-items: center;
  padding: 20rpx;
  background: rgba(255, 255, 255, 0.6);
  border-radius: 28rpx;
  transition: all 0.3s ease;
}

.record-item:active {
  transform: translateX(8rpx);
  background: rgba(255, 255, 255, 0.8);
}

.record-icon {
  width: 64rpx;
  height: 64rpx;
  background: rgba(100, 200, 150, 0.2);
  border-radius: 32rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32rpx;
  margin-right: 20rpx;
}

.record-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 6rpx;
}

.record-type {
  font-size: 26rpx;
  font-weight: 600;
  color: #2C5A3A;
}

.record-amount {
  font-size: 28rpx;
  font-weight: 700;
  color: #3D9B6D;
}

.record-time {
  font-size: 20rpx;
  color: #9CC0AC;
}

.record-arrow {
  font-size: 32rpx;
  color: #C8E6C9;
  transition: transform 0.3s ease;
}

.record-item:active .record-arrow {
  transform: translateX(6rpx);
  color: #3D9B6D;
}

/* 空状态卡片 */
.empty-card {
  background: rgba(255, 255, 255, 0.9);
  border-radius: 48rpx;
  padding: 60rpx 40rpx;
  margin-bottom: 28rpx;
  text-align: center;
  box-shadow: 0 12rpx 32rpx rgba(0, 0, 0, 0.08);
}

.empty-icon-wrapper {
  position: relative;
  width: 120rpx;
  height: 120rpx;
  margin: 0 auto 24rpx;
}

.empty-glow {
  position: absolute;
  top: -10rpx;
  left: -10rpx;
  right: -10rpx;
  bottom: -10rpx;
  background: radial-gradient(circle, rgba(100, 200, 150, 0.4), transparent);
  border-radius: 50%;
  animation: glowPulse 2s ease-in-out infinite;
}

@keyframes glowPulse {
  0%, 100% { opacity: 0.5; transform: scale(1); }
  50% { opacity: 1; transform: scale(1.2); }
}

.empty-icon {
  font-size: 80rpx;
  position: relative;
  z-index: 2;
  display: block;
  text-align: center;
  line-height: 120rpx;
}

.empty-title {
  font-size: 32rpx;
  font-weight: 700;
  color: #2C5A3A;
  display: block;
  margin-bottom: 12rpx;
}

.empty-sub {
  font-size: 24rpx;
  color: #9CC0AC;
}

/* 底部装饰 */
.footer-deco {
  text-align: center;
  padding: 40rpx 0 28rpx;
}

.deco-text {
  font-size: 24rpx;
  font-weight: 500;
  background: linear-gradient(135deg, #6FB88A, #3D9B6D);
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
  letter-spacing: 2rpx;
  animation: textPulse 2s ease-in-out infinite;
}

@keyframes textPulse {
  0%, 100% { opacity: 0.8; transform: scale(1); }
  50% { opacity: 1; transform: scale(1.02); text-shadow: 0 0 20rpx rgba(61, 155, 109, 0.3); }
}

.spacer {
  height: 60rpx;
}
</style>
<template>
  <view class="page">
    <!-- 动态渐变背景 -->
    <view class="gradient-bg"></view>

    <!-- 动态粒子系统 -->
    <view class="particle-system">
      <view v-for="(item, index) in 40" :key="index" class="particle" :style="particleStyles[index]"></view>
    </view>

    <scroll-view scroll-y="true" class="scroller" :show-scrollbar="false">
      <!-- 顶部导航栏 -->
      <view class="nav-bar">
        <view class="nav-back" @click="goBack">
          <text>←</text>
        </view>
        <text class="nav-title">设置</text>
        <view class="nav-placeholder"></view>
      </view>

      <!-- 账号信息卡片 -->
      <view class="setting-card floating-card">
        <view class="card-header">
          <view class="header-dot"></view>
          <text class="card-title">👤 账号信息</text>
        </view>
        <view class="setting-row">
          <text class="setting-label">用户名</text>
          <text class="setting-value">{{ username }}</text>
        </view>
        <view class="setting-row">
          <text class="setting-label">昵称</text>
          <input class="setting-input" v-model="editName" placeholder="输入昵称" maxlength="20" />
        </view>
        <view class="save-btn" @click="saveName">
          <text>保存昵称</text>
        </view>
      </view>

      <!-- 偏好设置卡片 -->
      <view class="setting-card floating-card">
        <view class="card-header">
          <view class="header-dot"></view>
          <text class="card-title">⚙️ 偏好设置</text>
        </view>
        <view class="setting-row">
          <text class="setting-label">消息通知</text>
          <switch :checked="notifyOn" color="#3D9B6D" @change="onNotifyChange" />
        </view>
        <view class="setting-row">
          <text class="setting-label">隐私保护</text>
          <switch :checked="privacyOn" color="#3D9B6D" @change="onPrivacyChange" />
        </view>
      </view>

      <!-- 其他设置卡片 -->
      <view class="setting-card floating-card">
        <view class="card-header">
          <view class="header-dot"></view>
          <text class="card-title">🗑️ 其他</text>
        </view>
        <view class="setting-row" @click="clearCache">
          <text class="setting-label">清除缓存</text>
          <text class="setting-arrow">→</text>
        </view>
      </view>

      <view class="spacer"></view>
    </scroll-view>
  </view>
</template>

<script>
import { getProfile } from '../../utils/request.js';

export default {
  data() {
    return {
      username: '',
      editName: '',
      notifyOn: true,
      privacyOn: false,
      particleStyles: []
    };
  },
  onLoad() {
    const username = uni.getStorageSync('username');
    if (!username) {
      uni.reLaunch({ url: '/pages/login/login' });
      return;
    }
    this.username = username;
    this.initParticleStyles();
    this.loadProfile();
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
    async loadProfile() {
      try {
        const res = await getProfile(this.username);
        if (res && res.username) {
          this.editName = res.name || '';
        }
      } catch (e) {
        console.error('加载失败:', e);
      }
    },
    onNotifyChange(e) {
      this.notifyOn = e.detail.value;
      uni.showToast({ title: this.notifyOn ? '已开启通知' : '已关闭通知', icon: 'none' });
    },
    onPrivacyChange(e) {
      this.privacyOn = e.detail.value;
      uni.showToast({ title: this.privacyOn ? '隐私保护已开启' : '隐私保护已关闭', icon: 'none' });
    },
    saveName() {
      uni.showToast({ title: '昵称已保存', icon: 'success' });
      // 这里可以调用保存昵称的API
    },
    clearCache() {
      uni.clearStorageSync();
      uni.showToast({ title: '缓存已清除', icon: 'success' });
    },
    goBack() {
      uni.navigateBack();
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

/* 导航栏 */
.nav-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 100rpx;
  margin-bottom: 32rpx;
  padding: 16rpx 0;
}

.nav-back {
  width: 72rpx;
  height: 72rpx;
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(20rpx);
  border-radius: 36rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
}

.nav-back:active {
  transform: scale(0.92);
}

.nav-back text {
  font-size: 44rpx;
  color: #2C5A3A;
}

.nav-title {
  font-size: 36rpx;
  font-weight: 700;
  background: linear-gradient(135deg, #2C5A3A, #3D9B6D);
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
}

.nav-placeholder {
  width: 72rpx;
}

/* 设置卡片 */
.setting-card {
  background: rgba(255, 255, 255, 0.9);
  border-radius: 40rpx;
  padding: 32rpx;
  margin-bottom: 28rpx;
  box-shadow: 0 12rpx 32rpx rgba(0, 0, 0, 0.08);
}

.card-header {
  display: flex;
  align-items: center;
  gap: 12rpx;
  margin-bottom: 24rpx;
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
}

.setting-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 24rpx 0;
  border-bottom: 1rpx solid rgba(100, 200, 150, 0.2);
}

.setting-row:last-child {
  border-bottom: none;
}

.setting-label {
  font-size: 28rpx;
  color: #2C5A3A;
}

.setting-value {
  font-size: 26rpx;
  color: #9CC0AC;
}

.setting-input {
  font-size: 26rpx;
  color: #2C5A3A;
  text-align: right;
  width: 300rpx;
  border: none;
  background: transparent;
}

.setting-arrow {
  font-size: 32rpx;
  color: #C8E6C9;
}

.save-btn {
  margin-top: 20rpx;
  background: linear-gradient(135deg, #6FB88A, #3D9B6D);
  border-radius: 48rpx;
  padding: 20rpx;
  text-align: center;
  transition: all 0.3s ease;
}

.save-btn:active {
  transform: scale(0.97);
}

.save-btn text {
  font-size: 28rpx;
  color: #FFFFFF;
  font-weight: 500;
}

.spacer {
  height: 60rpx;
}
</style>
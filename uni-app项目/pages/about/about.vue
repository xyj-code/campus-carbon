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
        <text class="nav-title">关于我们</text>
        <view class="nav-placeholder"></view>
      </view>

      <!-- Logo区域 -->
      <view class="logo-card floating-card">
        <view class="logo-glow"></view>
        <text class="logo-icon">🌍</text>
        <text class="app-name">低碳生活</text>
        <text class="app-version">版本 v1.0.0</text>
        <text class="app-desc">致力于帮助每个人养成低碳生活习惯，记录绿色足迹，共同守护地球家园。</text>
      </view>

      <!-- 联系我们卡片 -->
      <view class="info-card floating-card">
        <view class="card-header">
          <view class="header-dot"></view>
          <text class="card-title">📞 联系我们</text>
        </view>
        <view class="info-row" @click="copyService">
          <text class="info-label">客服邮箱</text>
          <view class="info-right">
            <text class="info-value">service@lowcarbon.app</text>
            <text class="info-arrow">📋</text>
          </view>
        </view>
        <view class="info-row">
          <text class="info-label">官方公众号</text>
          <view class="info-right">
            <text class="info-value">低碳生活</text>
          </view>
        </view>
        <view class="info-row">
          <text class="info-label">客服热线</text>
          <view class="info-right">
            <text class="info-value">400-888-6655</text>
          </view>
        </view>
      </view>

      <!-- 法律信息卡片 -->
      <view class="info-card floating-card">
        <view class="card-header">
          <view class="header-dot"></view>
          <text class="card-title">⚖️ 法律信息</text>
        </view>
        <view class="info-row">
          <text class="info-label">用户协议</text>
          <view class="info-right">
            <text class="info-link">查看详情 ›</text>
          </view>
        </view>
        <view class="info-row">
          <text class="info-label">隐私政策</text>
          <view class="info-right">
            <text class="info-link">查看详情 ›</text>
          </view>
        </view>
      </view>

      <view class="spacer"></view>
    </scroll-view>
  </view>
</template>

<script>
export default {
  data() {
    return {
      particleStyles: []
    };
  },
  onLoad() {
    this.initParticleStyles();
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
    copyService() {
      uni.setClipboardData({
        data: 'service@lowcarbon.app',
        success: () => {
          uni.showToast({ title: '邮箱已复制', icon: 'success' });
        }
      });
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

/* Logo卡片 */
.logo-card {
  position: relative;
  background: linear-gradient(135deg, #3D9B6D, #2C5A3A);
  border-radius: 56rpx;
  padding: 48rpx 36rpx;
  margin-bottom: 28rpx;
  text-align: center;
  overflow: hidden;
}

.logo-glow {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: radial-gradient(circle at 20% 30%, rgba(255, 255, 255, 0.2), transparent);
}

.logo-icon {
  font-size: 100rpx;
  display: block;
  margin-bottom: 20rpx;
  position: relative;
  z-index: 2;
}

.app-name {
  font-size: 40rpx;
  font-weight: 700;
  color: #FFFFFF;
  display: block;
  margin-bottom: 12rpx;
  position: relative;
  z-index: 2;
}

.app-version {
  font-size: 24rpx;
  color: rgba(255, 255, 255, 0.7);
  display: block;
  margin-bottom: 24rpx;
  position: relative;
  z-index: 2;
}

.app-desc {
  font-size: 26rpx;
  color: rgba(255, 255, 255, 0.85);
  line-height: 1.6;
  display: block;
  position: relative;
  z-index: 2;
}

/* 信息卡片 */
.info-card {
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

.info-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 24rpx 0;
  border-bottom: 1rpx solid rgba(100, 200, 150, 0.2);
}

.info-row:last-child {
  border-bottom: none;
}

.info-label {
  font-size: 28rpx;
  color: #2C5A3A;
}

.info-right {
  display: flex;
  align-items: center;
  gap: 12rpx;
}

.info-value {
  font-size: 26rpx;
  color: #9CC0AC;
}

.info-arrow {
  font-size: 28rpx;
  color: #C8E6C9;
}

.info-link {
  font-size: 26rpx;
  color: #3D9B6D;
}

.spacer {
  height: 60rpx;
}
</style>
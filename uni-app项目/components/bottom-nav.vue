<template>
  <view class="nav-bar">
    <view
        v-for="(item, index) in navItems"
        :key="index"
        class="nav-item"
        :class="{ 'active': currentIndex === index }"
        @click="switchTab(index)"
    >
      <!-- 图标容器 - 带旋转光环 -->
      <view class="nav-icon-container">
        <!-- 外圈旋转光环 - 更鲜艳 -->
        <view class="icon-outer-ring" :class="{ 'ring-active': currentIndex === index }"></view>
        <!-- 中圈旋转光环 -->
        <view class="icon-middle-ring" :class="{ 'ring-active': currentIndex === index }"></view>
        <!-- 内圈旋转光环 -->
        <view class="icon-inner-ring" :class="{ 'ring-active': currentIndex === index }"></view>
        <!-- 图标主体 - 圆润饱满 -->
        <view class="nav-icon-box" :class="{ 'icon-active': currentIndex === index }">
          <text class="nav-icon" :class="{ 'icon-lit': currentIndex === index }">{{ item.icon }}</text>
          <view class="icon-glow-dot" v-if="currentIndex === index"></view>
        </view>
      </view>
      <text class="nav-label" :class="{ 'label-lit': currentIndex === index }">{{ item.text }}</text>
    </view>
  </view>
</template>

<script>
export default {
  name: 'BottomNav',
  props: {
    currentIndex: { type: Number, default: 0 }
  },
  data() {
    return {
      navItems: [
        { icon: '🌱', text: '首页', url: '/pages/index/index' },
        { icon: '✨', text: '积分商城', url: '/pages/mall/mall' },
        { icon: '💚', text: '我的', url: '/pages/profile/profile' }
      ]
    };
  },
  mounted() {
    uni.hideTabBar({ animation: false });
  },
  methods: {
    switchTab(index) {
      if (index === this.currentIndex) return;
      uni.switchTab({ url: this.navItems[index].url });
    }
  }
};
</script>

<style scoped>
.nav-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  height: 130rpx;
  padding-bottom: env(safe-area-inset-bottom);
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: space-around;
  background: rgba(255, 255, 255, 0.92);
  backdrop-filter: blur(32rpx);
  -webkit-backdrop-filter: blur(32rpx);
  border-top: 1rpx solid rgba(0, 245, 160, 0.3);
  box-shadow: 0 -8rpx 32rpx rgba(0, 200, 150, 0.15);
  z-index: 999;
}

.nav-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 6rpx;
  padding: 12rpx 0;
  transition: all 0.3s cubic-bezier(0.2, 0.9, 0.4, 1.1);
  cursor: pointer;
}

.nav-item.active {
  transform: translateY(-6rpx);
}

/* 图标容器 - 更大更饱满 */
.nav-icon-container {
  position: relative;
  width: 88rpx;
  height: 88rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 6rpx;
}

/* 旋转光环 - 更鲜艳 */
.icon-outer-ring,
.icon-middle-ring,
.icon-inner-ring {
  position: absolute;
  border-radius: 50%;
  pointer-events: none;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.icon-outer-ring {
  top: -12rpx;
  left: -12rpx;
  right: -12rpx;
  bottom: -12rpx;
  border: 3rpx solid rgba(0, 245, 160, 0.7);
  animation: ringRotate 12s linear infinite;
  box-shadow: 0 0 12rpx rgba(0, 245, 160, 0.5);
}

.icon-middle-ring {
  top: -6rpx;
  left: -6rpx;
  right: -6rpx;
  bottom: -6rpx;
  border: 3rpx solid rgba(0, 245, 160, 0.6);
  animation: ringRotateReverse 9s linear infinite;
  box-shadow: 0 0 8rpx rgba(0, 245, 160, 0.4);
}

.icon-inner-ring {
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  border: 3rpx solid rgba(0, 245, 160, 0.5);
  animation: ringRotate 6s linear infinite;
  box-shadow: 0 0 6rpx rgba(0, 245, 160, 0.3);
}

.ring-active {
  opacity: 1;
}

@keyframes ringRotate {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

@keyframes ringRotateReverse {
  from { transform: rotate(0deg); }
  to { transform: rotate(-360deg); }
}

/* 图标盒子 - 圆润饱满 */
.nav-icon-box {
  width: 76rpx;
  height: 76rpx;
  border-radius: 32rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  background: transparent;
  transition: all 0.3s cubic-bezier(0.2, 0.9, 0.4, 1.1);
  z-index: 2;
}

.nav-icon-box.icon-active {
  background: linear-gradient(135deg, rgba(0, 245, 160, 0.35), rgba(0, 200, 150, 0.3));
  box-shadow: 0 0 32rpx rgba(0, 245, 160, 0.5);
  animation: iconGlowPulse 1.5s ease-in-out infinite;
}

@keyframes iconGlowPulse {
  0%, 100% {
    transform: scale(1);
    box-shadow: 0 0 32rpx rgba(0, 245, 160, 0.5);
  }
  50% {
    transform: scale(1.08);
    box-shadow: 0 0 48rpx rgba(0, 245, 160, 0.8);
  }
}

/* 图标文字 - 圆润饱满 */
.nav-icon {
  font-size: 52rpx;
  color: rgba(100, 140, 100, 0.6);
  line-height: 1;
  transition: all 0.3s ease;
  position: relative;
  z-index: 2;
  filter: drop-shadow(0 2rpx 4rpx rgba(0, 0, 0, 0.05));
}

.nav-icon.icon-lit {
  color: #00F5A0;
  text-shadow: 0 0 24rpx rgba(0, 245, 160, 0.9);
  animation: iconBounce 2s ease-in-out infinite;
}

@keyframes iconBounce {
  0%, 100% {
    transform: translateY(0) scale(1);
  }
  50% {
    transform: translateY(-6rpx) scale(1.1);
  }
}

/* 光点指示器 - 更亮更饱满 */
.icon-glow-dot {
  position: absolute;
  bottom: -8rpx;
  left: 50%;
  transform: translateX(-50%);
  width: 10rpx;
  height: 10rpx;
  border-radius: 50%;
  background: radial-gradient(circle, #00F5A0, #00C896);
  box-shadow: 0 0 16rpx rgba(0, 245, 160, 0.9);
  animation: dotBeat 1s ease-in-out infinite;
}

@keyframes dotBeat {
  0%, 100% {
    opacity: 0.7;
    transform: translateX(-50%) scale(1);
  }
  50% {
    opacity: 1;
    transform: translateX(-50%) scale(1.5);
  }
}

/* 标签文字 - 更鲜艳 */
.nav-label {
  font-size: 24rpx;
  color: rgba(100, 130, 90, 0.7);
  font-weight: 600;
  transition: all 0.3s ease;
  letter-spacing: 1rpx;
}

.nav-label.label-lit {
  color: #00F5A0;
  font-weight: 700;
  text-shadow: 0 0 12rpx rgba(0, 245, 160, 0.6);
  animation: labelPulse 1.5s ease-in-out infinite;
}

@keyframes labelPulse {
  0%, 100% {
    opacity: 0.9;
    transform: scale(1);
  }
  50% {
    opacity: 1;
    transform: scale(1.05);
  }
}

/* 点击反馈 - 更活泼 */
.nav-item:active {
  transform: scale(0.92);
  transition: transform 0.1s ease;
}

.nav-item.active:active {
  transform: translateY(-3rpx) scale(0.92);
}
</style>
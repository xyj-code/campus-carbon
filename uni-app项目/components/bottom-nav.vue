<template>
  <view class="nav-bar">
    <view
      v-for="(item, index) in navItems"
      :key="index"
      class="nav-item"
      @click="switchTab(index)"
    >
      <view class="nav-icon-box" :class="{ 'icon-active': currentIndex === index }">
        <text class="nav-icon" :class="{ 'icon-lit': currentIndex === index }">{{ item.icon }}</text>
        <view class="icon-glow-dot" v-if="currentIndex === index"></view>
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
        { icon: '⌂', text: '首页',    url: '/pages/index/index' },
        { icon: '◎', text: '积分商城', url: '/pages/mall/mall' },
        { icon: '☺', text: '我的',    url: '/pages/profile/profile' }
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
  height: 110rpx;
  padding-bottom: env(safe-area-inset-bottom);
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: space-around;
  background: rgba(255, 255, 255, 0.88);
  backdrop-filter: blur(24rpx);
  -webkit-backdrop-filter: blur(24rpx);
  border-top: 1.5rpx solid rgba(255, 255, 255, 0.95);
  box-shadow: 0 -4rpx 32rpx rgba(0, 0, 0, 0.06);
  z-index: 999;
}

.nav-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 4rpx;
  padding: 10rpx 0;
}

.nav-icon-box {
  width: 60rpx;
  height: 60rpx;
  border-radius: 20rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  background: transparent;
  transition: all 0.25s ease;
}

.nav-icon-box.icon-active {
  background: linear-gradient(135deg, rgba(0, 245, 160, 0.18), rgba(0, 200, 150, 0.12));
  box-shadow: 0 0 16rpx rgba(0, 200, 150, 0.22);
}

.nav-icon {
  font-size: 38rpx;
  color: #B0BEC5;
  line-height: 1;
  transition: color 0.25s ease;
}

.nav-icon.icon-lit {
  color: #00C896;
  text-shadow: 0 0 10rpx rgba(0, 200, 150, 0.55);
}

.icon-glow-dot {
  position: absolute;
  bottom: -2rpx;
  left: 50%;
  transform: translateX(-50%);
  width: 6rpx;
  height: 6rpx;
  border-radius: 50%;
  background: #00C896;
  box-shadow: 0 0 8rpx rgba(0, 200, 150, 0.9);
}

.nav-label {
  font-size: 20rpx;
  color: #B0BEC5;
  font-weight: 400;
  transition: color 0.25s ease;
}

.nav-label.label-lit {
  color: #00C896;
  font-weight: 600;
}
</style>

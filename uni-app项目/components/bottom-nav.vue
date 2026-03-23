<template>
  <view class="bottom-nav">
    <view
      v-for="(item, index) in navItems"
      :key="index"
      class="nav-item"
      :class="{ active: currentIndex === index }"
      @click="switchTab(index)"
    >
      <view class="nav-icon-wrap">
        <view class="icon-svg" :class="{ active: currentIndex === index }">
          <text class="icon-char">{{ item.icon }}</text>
        </view>
      </view>
      <text class="nav-text">{{ item.text }}</text>
      <view v-if="currentIndex === index" class="active-bar"></view>
    </view>
  </view>
</template>

<script>
export default {
  name: 'BottomNav',
  props: {
    currentIndex: {
      type: Number,
      default: 0
    }
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
    // 隐藏微信原生 tabBar，只显示自定义导航
    uni.hideTabBar({ animation: false });
  },
  methods: {
    switchTab(index) {
      if (index === this.currentIndex) return;
      // tabBar 页面已被微信缓存，switchTab 无需重建页面，无卡顿
      uni.switchTab({ url: this.navItems[index].url });
    }
  }
};
</script>

<style scoped>
.bottom-nav {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  height: 110rpx;
  background: #fff;
  display: flex;
  align-items: stretch;
  box-shadow: 0 -2rpx 16rpx rgba(46, 125, 50, 0.08);
  z-index: 999;
  border-top: 1rpx solid #e8f5e9;
}

.nav-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  position: relative;
  padding-bottom: 4rpx;
}
.nav-item:active { opacity: 0.6; }

.nav-icon-wrap {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 4rpx;
}
.icon-svg {
  width: 48rpx;
  height: 48rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}
.icon-char {
  font-size: 44rpx;
  color: #bdbdbd;
  line-height: 1;
  transition: color 0.15s;
}
.icon-svg.active .icon-char { color: #2e7d32; }

.nav-text {
  font-size: 20rpx;
  color: #bdbdbd;
  font-weight: 400;
  transition: color 0.15s;
}
.nav-item.active .nav-text {
  color: #2e7d32;
  font-weight: 600;
}

.active-bar {
  position: absolute;
  top: 0;
  left: 25%;
  right: 25%;
  height: 4rpx;
  background: linear-gradient(90deg, #4caf50, #2e7d32);
  border-radius: 0 0 4rpx 4rpx;
}
</style>

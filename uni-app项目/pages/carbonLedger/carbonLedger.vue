<template>
  <view class="page">
    <!-- 动态渐变背景 -->
    <view class="gradient-bg"></view>

    <!-- 动态粒子系统 -->
    <view class="particle-system">
      <view v-for="(item, index) in 50" :key="index" class="particle" :style="particleStyles[index]"></view>
    </view>

    <scroll-view scroll-y="true" class="scroller" :show-scrollbar="false">
      <!-- 顶部导航栏 -->
      <view class="nav-bar">
        <view class="nav-back" @click="goBack">
          <text>←</text>
        </view>
        <text class="nav-title">个人碳账本</text>
        <view class="nav-placeholder"></view>
      </view>

      <!-- 累计减碳量卡片 -->
      <view class="carbon-card floating-card">
        <view class="card-glow"></view>
        <view class="carbon-content">
          <text class="carbon-label">累计减碳量</text>
          <view class="carbon-value-wrap">
            <text class="carbon-value">{{ totalCarbon }}</text>
            <text class="carbon-unit">kg</text>
          </view>
          <text class="carbon-tip">🌿 每一步低碳，都在守护地球</text>
        </view>
      </view>

      <!-- 碳足迹统计卡片 -->
      <view class="stats-card floating-card">
        <view class="stats-header">
          <view class="header-dot"></view>
          <text class="stats-title">📊 碳足迹统计</text>
        </view>
        <view class="stats-grid">
          <view class="stat-item">
            <text class="stat-icon">🌳</text>
            <text class="stat-value">{{ treesCount }}</text>
            <text class="stat-label">棵树吸碳量/天</text>
          </view>
          <view class="stat-divider"></view>
          <view class="stat-item">
            <text class="stat-icon">🚗</text>
            <text class="stat-value">{{ kmCount }}</text>
            <text class="stat-label">公里少开车</text>
          </view>
        </view>
      </view>

      <!-- 近7天步数趋势 -->
      <view class="trend-card floating-card">
        <view class="stats-header">
          <view class="header-dot"></view>
          <text class="stats-title">📈 近7天步数趋势</text>
        </view>
        <view class="week-bars">
          <view v-for="(bar, i) in weekBars" :key="i" class="bar-col">
            <view class="bar-wrap">
              <view class="bar-fill" :style="{ height: bar.height + 'rpx' }"></view>
            </view>
            <text class="bar-label">{{ bar.day }}</text>
            <text class="bar-steps">{{ bar.steps > 0 ? (bar.steps >= 10000 ? (bar.steps/1000).toFixed(1)+'k' : bar.steps) : '-' }}</text>
          </view>
        </view>
      </view>

      <!-- 环保贡献 -->
      <view class="contribute-card floating-card">
        <view class="stats-header">
          <view class="header-dot"></view>
          <text class="stats-title">🎯 环保贡献</text>
        </view>
        <view class="contribute-content">
          <text class="contribute-text">你已累计减少碳排放</text>
          <text class="contribute-value">{{ totalCarbon }} kg</text>
          <text class="contribute-text">相当于为地球种下</text>
          <text class="contribute-value">{{ treesCount }} 棵树</text>
        </view>
      </view>

      <view class="spacer"></view>
    </scroll-view>
  </view>
</template>

<script>
import { getProfile, getStepCountList } from '../../utils/request.js';

export default {
  data() {
    return {
      username: '',
      totalCarbon: 0,
      weekBars: [],
      particleStyles: []
    };
  },
  computed: {
    treesCount() {
      return Math.round((this.totalCarbon || 0) / 0.02);
    },
    kmCount() {
      return Math.round((this.totalCarbon || 0) / 0.21);
    }
  },
  onLoad() {
    const username = uni.getStorageSync('username');
    if (!username) {
      uni.reLaunch({ url: '/pages/login/login' });
      return;
    }
    this.username = username;
    this.initParticleStyles();
    this.loadData();
    this.loadWeekBars();
  },
  methods: {
    initParticleStyles() {
      const styles = [];
      for (let i = 0; i < 50; i++) {
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
    async loadData() {
      try {
        const res = await getProfile(this.username);
        if (res && res.username) {
          this.totalCarbon = res.totalCarbon || 0;
        }
      } catch (e) {
        console.error('加载数据失败:', e);
      }
    },
    async loadWeekBars() {
      try {
        const list = await getStepCountList(this.username);
        const days = ['日', '一', '二', '三', '四', '五', '六'];
        const stepMap = {};
        (list || []).forEach(item => {
          if (item.date) {
            const d = item.date.substring ? item.date.substring(0, 10) : '';
            if (d) stepMap[d] = item.steps || 0;
          }
        });
        const bars = [];
        const maxSteps = Math.max(...Object.values(stepMap), 1000);
        for (let i = 6; i >= 0; i--) {
          const d = new Date();
          d.setDate(d.getDate() - i);
          const key = d.toISOString().substring(0, 10);
          const steps = stepMap[key] || 0;
          bars.push({
            day: days[d.getDay()],
            steps,
            height: Math.max(8, Math.round((steps / maxSteps) * 100))
          });
        }
        this.weekBars = bars;
      } catch (e) {
        this.weekBars = [];
      }
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

/* 减碳卡片 */
.carbon-card {
  position: relative;
  background: linear-gradient(135deg, #3D9B6D, #2C5A3A);
  border-radius: 56rpx;
  padding: 40rpx 36rpx;
  margin-bottom: 28rpx;
  overflow: hidden;
}

.card-glow {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: radial-gradient(circle at 20% 30%, rgba(255, 255, 255, 0.2), transparent);
}

.carbon-content {
  position: relative;
  z-index: 2;
}

.carbon-label {
  font-size: 28rpx;
  color: rgba(255, 255, 255, 0.8);
  display: block;
  margin-bottom: 16rpx;
}

.carbon-value-wrap {
  display: flex;
  align-items: baseline;
  gap: 8rpx;
  margin-bottom: 16rpx;
}

.carbon-value {
  font-size: 80rpx;
  font-weight: 700;
  color: #FFD700;
  line-height: 1;
}

.carbon-unit {
  font-size: 32rpx;
  color: rgba(255, 255, 255, 0.9);
}

.carbon-tip {
  font-size: 24rpx;
  color: rgba(255, 255, 255, 0.7);
}

/* 统计卡片 */
.stats-card,
.trend-card,
.contribute-card {
  background: rgba(255, 255, 255, 0.9);
  border-radius: 40rpx;
  padding: 32rpx;
  margin-bottom: 28rpx;
  box-shadow: 0 12rpx 32rpx rgba(0, 0, 0, 0.08);
}

.stats-header {
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

.stats-title {
  font-size: 30rpx;
  font-weight: 700;
  color: #2C5A3A;
}

.stats-grid {
  display: flex;
  align-items: center;
}

.stat-item {
  flex: 1;
  text-align: center;
}

.stat-icon {
  font-size: 48rpx;
  display: block;
  margin-bottom: 12rpx;
}

.stat-value {
  font-size: 36rpx;
  font-weight: 700;
  color: #3D9B6D;
  display: block;
  margin-bottom: 8rpx;
}

.stat-label {
  font-size: 22rpx;
  color: #9CC0AC;
}

.stat-divider {
  width: 1rpx;
  height: 80rpx;
  background: rgba(100, 200, 150, 0.3);
}

/* 趋势图 */
.week-bars {
  display: flex;
  gap: 12rpx;
  align-items: flex-end;
}

.bar-col {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.bar-wrap {
  width: 100%;
  height: 120rpx;
  display: flex;
  align-items: flex-end;
  justify-content: center;
}

.bar-fill {
  width: 32rpx;
  background: linear-gradient(180deg, #6FB88A, #3D9B6D);
  border-radius: 8rpx 8rpx 0 0;
  min-height: 8rpx;
  transition: height 0.5s ease;
}

.bar-label {
  font-size: 22rpx;
  color: #9CC0AC;
  margin-top: 12rpx;
}

.bar-steps {
  font-size: 20rpx;
  color: #3D9B6D;
  margin-top: 4rpx;
}

/* 环保贡献 */
.contribute-content {
  text-align: center;
  padding: 20rpx;
}

.contribute-text {
  font-size: 26rpx;
  color: #5B8C6E;
  display: block;
  margin-bottom: 12rpx;
}

.contribute-value {
  font-size: 44rpx;
  font-weight: 700;
  color: #3D9B6D;
  display: block;
  margin-bottom: 12rpx;
}

.spacer {
  height: 60rpx;
}
</style>
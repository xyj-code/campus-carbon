<template>
  <view class="container">

    <!-- 顶部 Header -->
    <view class="header">
      <view class="header-top">
        <view class="user-info">
          <text class="leaf">🌿</text>
          <view class="user-text">
            <text class="greeting">{{ greeting }}，{{ studentName }}</text>
            <text class="date-label">{{ todayDate }}</text>
          </view>
        </view>
        <view class="points-btn" @click="showPointsTip">
          <text class="points-star">⭐</text>
          <text class="points-num">{{ points }}</text>
          <text class="points-unit">积分</text>
        </view>
      </view>
      <view class="header-center">
        <text class="main-title">校园低碳系统</text>
        <text class="main-sub">🌍 每一步，都是对地球的承诺</text>
      </view>
    </view>

    <!-- 今日数据概览 -->
    <view class="overview-card">
      <view class="overview-title-row">
        <text class="overview-title">今日数据概览</text>
        <text class="overview-date">{{ todayDate }}</text>
      </view>
      <view class="stats-row">
        <view class="stat-item">
          <text class="stat-icon">👟</text>
          <text class="stat-value orange">{{ todayData.steps }}</text>
          <text class="stat-label">步数</text>
        </view>
        <view class="stat-sep"></view>
        <view class="stat-item">
          <text class="stat-icon">⏱️</text>
          <text class="stat-value blue">{{ todayData.duration }}</text>
          <text class="stat-label">时长(分)</text>
        </view>
        <view class="stat-sep"></view>
        <view class="stat-item">
          <text class="stat-icon">🌱</text>
          <text class="stat-value green">{{ todayData.carbon }}</text>
          <text class="stat-label">减碳(g)</text>
        </view>
      </view>
    </view>

    <!-- 功能卡片 -->
    <view class="section-label">功能入口</view>

    <view class="card ai-card"
      @click="navigateTo('/pages/aiSuggest/aiSuggest')"
      :style="pressIdx === 0 ? 'transform:scale(0.97)' : ''"
      @touchstart="pressIdx = 0" @touchend="pressIdx = -1">
      <view class="card-icon-wrap ai-bg">
        <text class="card-icon">🤖</text>
      </view>
      <view class="card-body">
        <text class="card-name">AI 减排建议</text>
        <text class="card-desc">智能分析碳足迹，给出个性化减排方案</text>
      </view>
      <text class="card-arrow">›</text>
    </view>

    <view class="card step-card"
      @click="navigateTo('/pages/stepCount/stepCount')"
      :style="pressIdx === 1 ? 'transform:scale(0.97)' : ''"
      @touchstart="pressIdx = 1" @touchend="pressIdx = -1">
      <view class="card-icon-wrap step-bg">
        <text class="card-icon">👟</text>
      </view>
      <view class="card-body">
        <text class="card-name">步数统计</text>
        <text class="card-desc">记录每日步行，量化你的绿色出行成果</text>
      </view>
      <text class="card-arrow">›</text>
    </view>

    <view class="card sport-card"
      @click="navigateTo('/pages/sportRecord/sportRecord')"
      :style="pressIdx === 2 ? 'transform:scale(0.97)' : ''"
      @touchstart="pressIdx = 2" @touchend="pressIdx = -1">
      <view class="card-icon-wrap sport-bg">
        <text class="card-icon">🏃</text>
      </view>
      <view class="card-body">
        <text class="card-name">运动记录</text>
        <text class="card-desc">追踪跑步骑行等运动，积累低碳积分</text>
      </view>
      <text class="card-arrow">›</text>
    </view>

    <!-- 底部标语 -->
    <view class="footer">
      <text class="footer-text">🌿 低碳生活，从校园开始 · 绿色未来，从你我共建 🌿</text>
    </view>

  </view>
</template>

<script>
import { getStepCount } from '../../utils/request.js';

export default {
  data() {
    return {
      studentName: '',
      stuNo: '',
      todayDate: '',
      greeting: '',
      pressIdx: -1,
      points: 0,
      todayData: { steps: 0, duration: 0, carbon: 0 }
    };
  },
  onLoad() {
    const stuNo = uni.getStorageSync('stuNo');
    if (!stuNo) {
      uni.reLaunch({ url: '/pages/login/login' });
      return;
    }
    this.stuNo = stuNo;
    this.studentName = uni.getStorageSync('studentName') || '同学';
    this.initDate();
    this.loadTodayData();
  },
  methods: {
    initDate() {
      const now = new Date();
      const h = now.getHours();
      if (h < 6) this.greeting = '夜深了';
      else if (h < 12) this.greeting = '早上好';
      else if (h < 14) this.greeting = '中午好';
      else if (h < 18) this.greeting = '下午好';
      else this.greeting = '晚上好';
      const y = now.getFullYear();
      const m = String(now.getMonth() + 1).padStart(2, '0');
      const d = String(now.getDate()).padStart(2, '0');
      this.todayDate = `${y}-${m}-${d}`;
    },
    async loadTodayData() {
      try {
        const res = await getStepCount(this.stuNo, this.todayDate);
        if (res && res.steps) {
          this.todayData.steps = res.steps;
          this.todayData.duration = res.duration || 0;
          this.todayData.carbon = Math.round(res.steps * 0.08);
          this.points = Math.floor(res.steps / 50) + (res.duration || 0) * 2;
        }
      } catch (e) {}
    },
    navigateTo(url) {
      uni.navigateTo({ url });
    },
    showPointsTip() {
      uni.showToast({ title: `当前积分：${this.points} 分`, icon: 'none', duration: 2000 });
    }
  }
};
</script>

<style scoped>
.container {
  min-height: 100vh;
  background: linear-gradient(180deg, #e8f5e9 0%, #f1f8e9 40%, #f5f5f5 100%);
  padding-bottom: 60rpx;
}

/* ---- Header ---- */
.header {
  background: linear-gradient(135deg, #2e7d32 0%, #43a047 60%, #66bb6a 100%);
  padding: 100rpx 40rpx 50rpx;
  border-radius: 0 0 48rpx 48rpx;
}

.header-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 36rpx;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 16rpx;
}

.leaf {
  font-size: 52rpx;
}

.user-text {
  display: flex;
  flex-direction: column;
}

.greeting {
  font-size: 30rpx;
  font-weight: bold;
  color: #fff;
}

.date-label {
  font-size: 22rpx;
  color: rgba(255,255,255,0.75);
  margin-top: 4rpx;
}

.points-btn {
  display: flex;
  align-items: center;
  gap: 6rpx;
  background: rgba(255,255,255,0.2);
  border-radius: 40rpx;
  padding: 10rpx 24rpx;
  border: 1rpx solid rgba(255,255,255,0.35);
}

.points-star { font-size: 28rpx; }
.points-num {
  font-size: 28rpx;
  font-weight: bold;
  color: #fff;
}
.points-unit {
  font-size: 22rpx;
  color: rgba(255,255,255,0.85);
}

.header-center {
  text-align: center;
}

.main-title {
  display: block;
  font-size: 44rpx;
  font-weight: bold;
  color: #fff;
  letter-spacing: 4rpx;
  margin-bottom: 12rpx;
  text-shadow: 0 2rpx 8rpx rgba(0,0,0,0.2);
}

.main-sub {
  display: block;
  font-size: 24rpx;
  color: rgba(255,255,255,0.85);
}

/* ---- 今日概览 ---- */
.overview-card {
  margin: -24rpx 32rpx 32rpx;
  background: #fff;
  border-radius: 28rpx;
  padding: 36rpx 32rpx 30rpx;
  box-shadow: 0 8rpx 40rpx rgba(46,125,50,0.12);
}

.overview-title-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 32rpx;
}

.overview-title {
  font-size: 28rpx;
  font-weight: bold;
  color: #333;
}

.overview-date {
  font-size: 22rpx;
  color: #aaa;
}

.stats-row {
  display: flex;
  justify-content: space-around;
  align-items: center;
}

.stat-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8rpx;
}

.stat-icon { font-size: 40rpx; }

.stat-value {
  font-size: 36rpx;
  font-weight: bold;
}

.stat-value.orange { color: #ff7043; }
.stat-value.blue   { color: #1e88e5; }
.stat-value.green  { color: #43a047; }

.stat-label {
  font-size: 22rpx;
  color: #999;
}

.stat-sep {
  width: 1rpx;
  height: 80rpx;
  background: #f0f0f0;
}

/* ---- 功能卡片 ---- */
.section-label {
  font-size: 26rpx;
  font-weight: bold;
  color: #888;
  padding: 0 40rpx;
  margin-bottom: 16rpx;
  letter-spacing: 2rpx;
}

.card {
  display: flex;
  align-items: center;
  margin: 0 32rpx 24rpx;
  background: #fff;
  border-radius: 24rpx;
  padding: 30rpx 28rpx;
  box-shadow: 0 4rpx 20rpx rgba(0,0,0,0.07);
  transition: transform 0.15s;
}

.card-icon-wrap {
  width: 96rpx;
  height: 96rpx;
  border-radius: 24rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 28rpx;
  flex-shrink: 0;
}

.ai-bg   { background: linear-gradient(135deg, #a5d6a7, #43a047); }
.step-bg { background: linear-gradient(135deg, #ffcc80, #ff7043); }
.sport-bg{ background: linear-gradient(135deg, #90caf9, #1e88e5); }

.card-icon { font-size: 48rpx; }

.card-body {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8rpx;
}

.card-name {
  font-size: 30rpx;
  font-weight: bold;
  color: #222;
}

.card-desc {
  font-size: 22rpx;
  color: #999;
  line-height: 1.5;
}

.card-arrow {
  font-size: 40rpx;
  color: #ccc;
  margin-left: 8rpx;
}

/* 左侧竖线装饰 */
.ai-card    { border-left: 6rpx solid #43a047; }
.step-card  { border-left: 6rpx solid #ff7043; }
.sport-card { border-left: 6rpx solid #1e88e5; }

/* ---- 底部 ---- */
.footer {
  text-align: center;
  padding: 20rpx 40rpx 0;
}

.footer-text {
  font-size: 22rpx;
  color: #aaa;
  line-height: 1.8;
}
</style>

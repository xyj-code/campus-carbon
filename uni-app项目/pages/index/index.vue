<template>
  <view class="page">
    <!-- 动态渐变背景 -->
    <view class="gradient-bg"></view>

    <!-- 动态粒子系统 -->
    <view class="particle-system">
      <view v-for="(item, index) in 60" :key="index" class="particle" :style="particleStyles[index]"></view>
    </view>

    <scroll-view scroll-y="true" class="scroller" :show-scrollbar="false">
      <!-- ===== 头部卡片 ===== -->
      <view class="header-card floating-card">
        <view class="hd-left">
          <text class="hi-name">{{ greeting }}，{{ studentName }} 🌿</text>
          <text class="hi-date">{{ todayDate }}</text>
        </view>
        <view class="pts-pill" @click="showPointsTip">
          <text class="pts-star">✨</text>
          <text class="pts-num">{{ points }}</text>
          <text class="pts-unit">积分</text>
        </view>
      </view>

      <!-- ===== 主视觉：能量胶囊 + 向外扩张光晕 ===== -->
      <view class="hero-section">
        <view class="capsule-wrap">
          <!-- 三层旋转光环 -->
          <view class="capsule-outer-ring"></view>
          <view class="capsule-middle-ring"></view>
          <view class="capsule-inner-ring"></view>

          <!-- 向外扩张的生命力光晕层 -->
          <view class="life-glow life-glow-1"></view>
          <view class="life-glow life-glow-2"></view>
          <view class="life-glow life-glow-3"></view>
          <view class="life-glow life-glow-4"></view>
          <view class="life-glow life-glow-5"></view>

          <!-- 向上飘浮的生命粒子 -->
          <view class="life-particle life-particle-1">✨</view>
          <view class="life-particle life-particle-2">⭐</view>
          <view class="life-particle life-particle-3">💚</view>
          <view class="life-particle life-particle-4">🌱</view>
          <view class="life-particle life-particle-5">✨</view>
          <view class="life-particle life-particle-6">⭐</view>

          <view class="capsule">
            <!-- 闪烁粒子 -->
            <view class="sp sp1"></view>
            <view class="sp sp2"></view>
            <view class="sp sp3"></view>
            <view class="sp sp4"></view>
            <view class="sp sp5"></view>

            <!-- 树冠层 -->
            <view class="tree-aura"></view>
            <view class="leaf l1"></view>
            <view class="leaf l2"></view>
            <view class="leaf l3"></view>
            <view class="leaf l4"></view>
            <view class="leaf l5"></view>
            <view class="trunk"></view>

            <!-- 光泽效果 -->
            <view class="capsule-shine"></view>
            <view class="capsule-bottom-glow"></view>
          </view>
        </view>

        <!-- 统计数据 - 浮动卡片 -->
        <view class="hero-info floating-card">
          <view class="hinfo-item">
            <text class="hinfo-val neon">{{ carbonKg }}kg</text>
            <text class="hinfo-lbl">减少碳排放</text>
          </view>
          <view class="hinfo-div"></view>
          <view class="hinfo-item">
            <text class="hinfo-val">Lv.{{ treeLevel }}</text>
            <text class="hinfo-lbl">{{ treeName }}</text>
          </view>
          <view class="hinfo-div"></view>
          <view class="hinfo-item">
            <text class="hinfo-val">#{{ userRank }}</text>
            <text class="hinfo-lbl">减碳排名</text>
          </view>
        </view>
      </view>

      <!-- ===== 今日数据卡片 ===== -->
      <view class="stats-row">
        <view class="stat-card stat-card-1 floating-card">
          <view class="stat-icon">👟</view>
          <text class="stat-value neon">{{ todayData.steps }}</text>
          <text class="stat-label">今日步数</text>
        </view>
        <view class="stat-card stat-card-2 floating-card">
          <view class="stat-icon">⏱️</view>
          <text class="stat-value">{{ todayData.duration }}</text>
          <text class="stat-label">分钟</text>
        </view>
        <view class="stat-card stat-card-3 floating-card">
          <view class="stat-icon">🌱</view>
          <text class="stat-value">{{ todayData.carbon }}</text>
          <text class="stat-label">减碳(g)</text>
        </view>
      </view>

      <!-- ===== 步行目标进度 ===== -->
      <view class="progress-card floating-card">
        <view class="progress-header">
          <view>
            <text class="progress-title">今日步行目标</text>
            <text class="progress-sub">目标 10,000 步挑战</text>
          </view>
          <view class="progress-percent">
            <text class="percent-value neon">{{ stepPct }}%</text>
          </view>
        </view>

        <view class="progress-track">
          <view class="progress-fill" :style="{ width: stepPct + '%' }">
            <view class="progress-dot"></view>
          </view>
        </view>

        <text class="progress-hint" v-if="stepsLeft > 0">
          🎯 再走 {{ stepsLeft }} 步完成目标
        </text>
        <text class="progress-hint done" v-else>
          🎉 太棒了！今日目标已完成
        </text>
      </view>

      <!-- ===== 功能导航标题 ===== -->
      <view class="section-header">
        <view class="section-bar"></view>
        <text class="section-title">功能导航</text>
        <text class="section-sub">探索更多低碳方式</text>
      </view>

      <!-- ===== 2x2 功能导航网格 ===== -->
      <view class="feature-grid">
        <view class="feature-row">
          <view class="feature-card feature-card-ai floating-card" @click="navigateTo('/pages/aiSuggest/aiSuggest')">
            <view class="feature-icon-container">
              <view class="particle-glow particle-glow-1"></view>
              <view class="particle-glow particle-glow-2"></view>
              <view class="particle-glow particle-glow-3"></view>
              <view class="particle-glow particle-glow-4"></view>
              <view class="particle-glow particle-glow-5"></view>
              <view class="feature-icon-wrapper">
                <view class="feature-outer-ring"></view>
                <view class="feature-middle-ring"></view>
                <view class="feature-inner-ring"></view>
                <view class="feature-icon-box">
                  <text class="feature-icon">🤖</text>
                </view>
              </view>
            </view>
            <text class="feature-name">AI 减排建议</text>
            <text class="feature-desc">智能分析碳足迹</text>
            <view class="feature-arrow">→</view>
            <view class="radial-glow"></view>
          </view>

          <view class="feature-card feature-card-steps floating-card" @click="navigateTo('/pages/stepCount/stepCount')">
            <view class="feature-icon-container">
              <view class="particle-glow particle-glow-1"></view>
              <view class="particle-glow particle-glow-2"></view>
              <view class="particle-glow particle-glow-3"></view>
              <view class="particle-glow particle-glow-4"></view>
              <view class="particle-glow particle-glow-5"></view>
              <view class="feature-icon-wrapper">
                <view class="feature-outer-ring"></view>
                <view class="feature-middle-ring"></view>
                <view class="feature-inner-ring"></view>
                <view class="feature-icon-box">
                  <text class="feature-icon">👟</text>
                </view>
              </view>
            </view>
            <text class="feature-name">步数统计</text>
            <text class="feature-desc">量化绿色出行</text>
            <view class="feature-arrow">→</view>
            <view class="radial-glow"></view>
          </view>
        </view>

        <view class="feature-row">
          <view class="feature-card feature-card-sport floating-card" @click="navigateTo('/pages/sportRecord/sportRecord')">
            <view class="feature-icon-container">
              <view class="particle-glow particle-glow-1"></view>
              <view class="particle-glow particle-glow-2"></view>
              <view class="particle-glow particle-glow-3"></view>
              <view class="particle-glow particle-glow-4"></view>
              <view class="particle-glow particle-glow-5"></view>
              <view class="feature-icon-wrapper">
                <view class="feature-outer-ring"></view>
                <view class="feature-middle-ring"></view>
                <view class="feature-inner-ring"></view>
                <view class="feature-icon-box">
                  <text class="feature-icon">🏃</text>
                </view>
              </view>
            </view>
            <text class="feature-name">运动记录</text>
            <text class="feature-desc">追踪运动轨迹</text>
            <view class="feature-arrow">→</view>
            <view class="radial-glow"></view>
          </view>

          <view class="feature-card feature-card-rank floating-card" @click="navigateTo('/pages/rank/rank')">
            <view class="feature-icon-container">
              <view class="particle-glow particle-glow-1"></view>
              <view class="particle-glow particle-glow-2"></view>
              <view class="particle-glow particle-glow-3"></view>
              <view class="particle-glow particle-glow-4"></view>
              <view class="particle-glow particle-glow-5"></view>
              <view class="feature-icon-wrapper">
                <view class="feature-outer-ring"></view>
                <view class="feature-middle-ring"></view>
                <view class="feature-inner-ring"></view>
                <view class="feature-icon-box">
                  <text class="feature-icon">🏆</text>
                </view>
              </view>
            </view>
            <text class="feature-name">减碳排名</text>
            <text class="feature-desc">减碳榜单</text>
            <view class="feature-arrow">→</view>
            <view class="radial-glow"></view>
          </view>
        </view>
      </view>

      <!-- ===== 环保徽章 ===== -->
      <view class="eco-badge floating-card" v-if="badgeVisible">
        <view class="badge-shimmer"></view>
        <view class="badge-icon">🏅</view>
        <view class="badge-info">
          <text class="badge-title">绿色先锋徽章</text>
          <text class="badge-text">今日已低碳出行 · 坚持就是胜利</text>
        </view>
        <view class="badge-close" @click="badgeVisible = false">
          <text>×</text>
        </view>
      </view>

      <!-- 底部 -->
      <view class="footer">
        <text class="footer-text">🌿 低碳生活，从我做起 · 绿色未来，从你我共建 🌿</text>
      </view>

      <view class="spacer"></view>
    </scroll-view>

    <bottom-nav :currentIndex="0" />
  </view>
</template>

<script>
import { getStepCount, getRankData } from '../../utils/request.js';
import BottomNav from '../../components/bottom-nav.vue';

export default {
  components: { BottomNav },
  data() {
    return {
      studentName: '',
      stuNo: '',
      todayDate: '',
      greeting: '',
      points: 0,
      todayData: { steps: 0, duration: 0, carbon: 0 },
      carbonKg: '0.000',
      treeLevel: 1,
      treeName: '嫩芽',
      userRank: '--',
      stepPct: 0,
      stepsLeft: 10000,
      badgeVisible: false,
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
    this.studentName = uni.getStorageSync('userName') || '同学';
    this.initDate();
    this.initParticleStyles();
  },
  onShow() {
    if (this.stuNo) {
      this.initDate();
      this.loadTodayData();
      this.loadRankData();
    }
  },
  methods: {
    initParticleStyles() {
      const styles = [];
      for (let i = 0; i < 60; i++) {
        styles.push({
          left: Math.random() * 100 + '%',
          animationDuration: (8 + Math.random() * 12) + 's',
          animationDelay: Math.random() * 8 + 's',
          width: (2 + Math.random() * 8) + 'rpx',
          height: (2 + Math.random() * 8) + 'rpx',
          opacity: 0.2 + Math.random() * 0.5
        });
      }
      this.particleStyles = styles;
    },
    initDate() {
      const now = new Date();
      const h = now.getHours();
      if (h < 6) this.greeting = '夜深了';
      else if (h < 12) this.greeting = '早上好';
      else if (h < 14) this.greeting = '中午好';
      else if (h < 18) this.greeting = '下午好';
      else this.greeting = '晚上好';

      const days = ['周日', '周一', '周二', '周三', '周四', '周五', '周六'];
      const months = ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月'];
      this.todayDate = `${days[now.getDay()]}，${months[now.getMonth()]} ${now.getDate()}日`;
    },
    async loadRankData() {
      if (!this.stuNo) return;
      try {
        const res = await getRankData(this.stuNo, 'today');
        if (res && res.code === 200 && res.data && res.data.myRank) {
          this.userRank = res.data.myRank.rank || '--';
        }
      } catch (error) {
        console.error('获取排名数据失败:', error);
      }
    },
    async loadTodayData() {
      try {
        const now = new Date();
        const dateStr = `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}-${String(now.getDate()).padStart(2, '0')}`;
        const res = await getStepCount(this.stuNo, dateStr);
        if (res) {
          const steps = res.steps || 0;
          const duration = res.duration || 0;
          const carbonKg = steps * 0.005 / 100;
          const carbonG = Math.round(carbonKg * 1000);
          this.todayData = { steps, duration, carbon: carbonG };
          this.points = Math.round(carbonKg * 100);
          this.carbonKg = carbonKg.toFixed(3);
          this.stepPct = Math.min(Math.round((steps / 10000) * 100), 100);
          this.stepsLeft = Math.max(10000 - steps, 0);

          if (steps >= 15000) { this.treeLevel = 5; this.treeName = '参天大树'; }
          else if (steps >= 10000) { this.treeLevel = 4; this.treeName = '茁壮成长'; }
          else if (steps >= 6000) { this.treeLevel = 3; this.treeName = '小树苗'; }
          else if (steps >= 3000) { this.treeLevel = 2; this.treeName = '破土而出'; }
          else { this.treeLevel = 1; this.treeName = '嫩芽'; }

          if (steps > 0) this.badgeVisible = true;
        }
      } catch (e) {
        console.error('获取今日数据失败:', e);
      }
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
.page {
  min-height: 100vh;
  background: linear-gradient(180deg, #E0F2E9 0%, #C8E6D9 100%);
  position: relative;
  overflow: hidden;
}

/* 动态渐变背景 */
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

/* 粒子系统 */
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
  background: radial-gradient(circle, rgba(80, 200, 140, 0.6), rgba(60, 160, 100, 0.2));
  border-radius: 50%;
  animation: floatUp linear infinite;
  box-shadow: 0 0 12rpx rgba(80, 200, 140, 0.4);
}

@keyframes floatUp {
  0% {
    transform: translateY(100vh) translateX(0);
    opacity: 0;
  }
  10% {
    opacity: 0.8;
  }
  90% {
    opacity: 0.5;
  }
  100% {
    transform: translateY(-20vh) translateX(40rpx);
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

/* ===== 浮动动画 ===== */
.floating-card {
  animation: floatCard 3s ease-in-out infinite;
}

@keyframes floatCard {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-6rpx); }
}

.header-card { animation-delay: 0s; }
.hero-info { animation-delay: 0.2s; }
.stat-card-1 { animation-delay: 0.4s; }
.stat-card-2 { animation-delay: 0.6s; }
.stat-card-3 { animation-delay: 0.8s; }
.progress-card { animation-delay: 0.3s; }
.feature-card-ai { animation-delay: 0.1s; }
.feature-card-steps { animation-delay: 0.3s; }
.feature-card-sport { animation-delay: 0.5s; }
.feature-card-rank { animation-delay: 0.7s; }

/* ===== 头部卡片 ===== */
.header-card {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 100rpx;
  margin-bottom: 32rpx;
  padding: 28rpx 32rpx;
  background: linear-gradient(135deg, rgba(200, 230, 210, 0.9), rgba(180, 220, 190, 0.85));
  backdrop-filter: blur(24rpx);
  border-radius: 40rpx;
  border: 1rpx solid rgba(255, 255, 255, 0.6);
  box-shadow: 0 12rpx 32rpx rgba(0, 0, 0, 0.08);
}

.hi-name {
  font-size: 34rpx;
  font-weight: 700;
  color: #2C5A3A;
  display: block;
}

.hi-date {
  font-size: 24rpx;
  color: #5B8C6E;
  margin-top: 6rpx;
  display: block;
}

.pts-pill {
  display: flex;
  align-items: center;
  gap: 8rpx;
  background: linear-gradient(135deg, #6FB88A, #4A9B6E);
  border-radius: 60rpx;
  padding: 12rpx 28rpx;
  box-shadow: 0 4rpx 20rpx rgba(74, 155, 110, 0.4);
  animation: pillPulse 2s ease-in-out infinite;
}

@keyframes pillPulse {
  0%, 100% { transform: scale(1); box-shadow: 0 4rpx 20rpx rgba(74, 155, 110, 0.4); }
  50% { transform: scale(1.05); box-shadow: 0 8rpx 28rpx rgba(74, 155, 110, 0.6); }
}

/* ===== 主视觉区域 ===== */
.hero-section {
  margin-bottom: 32rpx;
  position: relative;
}

.capsule-wrap {
  position: relative;
  display: flex;
  justify-content: center;
  margin-bottom: 28rpx;
  animation: float 3s ease-in-out infinite;
}

@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-12rpx); }
}

/* 三层旋转光环 */
.capsule-outer-ring,
.capsule-middle-ring,
.capsule-inner-ring {
  position: absolute;
  border-radius: 50%;
  pointer-events: none;
}

.capsule-outer-ring {
  width: 220rpx;
  height: 220rpx;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  border: 3rpx solid rgba(80, 200, 140, 0.6);
  animation: ringRotate 12s linear infinite;
  box-shadow: 0 0 20rpx rgba(80, 200, 140, 0.4);
}

.capsule-middle-ring {
  width: 190rpx;
  height: 190rpx;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  border: 3rpx solid rgba(80, 200, 140, 0.5);
  animation: ringRotateReverse 9s linear infinite;
}

.capsule-inner-ring {
  width: 160rpx;
  height: 160rpx;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  border: 3rpx solid rgba(80, 200, 140, 0.4);
  animation: ringRotate 6s linear infinite;
}

@keyframes ringRotate {
  from { transform: translate(-50%, -50%) rotate(0deg); }
  to { transform: translate(-50%, -50%) rotate(360deg); }
}

@keyframes ringRotateReverse {
  from { transform: translate(-50%, -50%) rotate(0deg); }
  to { transform: translate(-50%, -50%) rotate(-360deg); }
}

/* ===== 向外扩张的生命力光晕层 ===== */
.life-glow {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  border-radius: 50%;
  pointer-events: none;
  animation: expandGlow 3s ease-out infinite;
}

.life-glow-1 {
  width: 180rpx;
  height: 180rpx;
  background: radial-gradient(circle, rgba(100, 220, 150, 0.6), rgba(80, 200, 130, 0.3), transparent);
  animation-delay: 0s;
}

.life-glow-2 {
  width: 220rpx;
  height: 220rpx;
  background: radial-gradient(circle, rgba(120, 240, 170, 0.5), rgba(100, 220, 150, 0.2), transparent);
  animation-delay: 0.6s;
}

.life-glow-3 {
  width: 260rpx;
  height: 260rpx;
  background: radial-gradient(circle, rgba(140, 255, 190, 0.4), rgba(120, 240, 170, 0.15), transparent);
  animation-delay: 1.2s;
}

.life-glow-4 {
  width: 300rpx;
  height: 300rpx;
  background: radial-gradient(circle, rgba(160, 255, 210, 0.3), rgba(140, 255, 190, 0.1), transparent);
  animation-delay: 1.8s;
}

.life-glow-5 {
  width: 340rpx;
  height: 340rpx;
  background: radial-gradient(circle, rgba(180, 255, 230, 0.2), rgba(160, 255, 210, 0.05), transparent);
  animation-delay: 2.4s;
}

@keyframes expandGlow {
  0% {
    opacity: 0.8;
    transform: translate(-50%, -50%) scale(0.8);
  }
  50% {
    opacity: 0.5;
    transform: translate(-50%, -50%) scale(1.2);
  }
  100% {
    opacity: 0;
    transform: translate(-50%, -50%) scale(1.6);
  }
}

/* ===== 向上飘浮的生命粒子 ===== */
.life-particle {
  position: absolute;
  font-size: 28rpx;
  pointer-events: none;
  animation: floatUpLife 2.5s ease-out infinite;
  filter: drop-shadow(0 0 8rpx rgba(100, 220, 150, 0.8));
}

.life-particle-1 {
  bottom: 20rpx;
  left: 50%;
  transform: translateX(-50%);
  animation-delay: 0s;
}

.life-particle-2 {
  bottom: 40rpx;
  left: 35%;
  transform: translateX(-50%);
  animation-delay: 0.5s;
}

.life-particle-3 {
  bottom: 10rpx;
  left: 65%;
  transform: translateX(-50%);
  animation-delay: 1s;
}

.life-particle-4 {
  bottom: 50rpx;
  left: 45%;
  transform: translateX(-50%);
  animation-delay: 1.5s;
}

.life-particle-5 {
  bottom: 30rpx;
  left: 55%;
  transform: translateX(-50%);
  animation-delay: 2s;
}

.life-particle-6 {
  bottom: 60rpx;
  left: 40%;
  transform: translateX(-50%);
  animation-delay: 2.5s;
}

@keyframes floatUpLife {
  0% {
    opacity: 1;
    transform: translateX(-50%) translateY(0) scale(1);
  }
  50% {
    opacity: 0.8;
    transform: translateX(-50%) translateY(-60rpx) scale(1.2);
  }
  100% {
    opacity: 0;
    transform: translateX(-50%) translateY(-120rpx) scale(1.5);
  }
}

.capsule {
  width: 160rpx;
  height: 320rpx;
  background: linear-gradient(180deg,
  rgba(140, 210, 160, 0.5) 0%,
  rgba(100, 180, 130, 0.4) 100%);
  border-radius: 80rpx;
  backdrop-filter: blur(8rpx);
  border: 2rpx solid rgba(255, 255, 255, 0.6);
  position: relative;
  overflow: hidden;
  box-shadow: inset 0 0 40rpx rgba(255, 255, 255, 0.3), 0 12rpx 32rpx rgba(0, 0, 0, 0.1);
  z-index: 10;
}

/* 闪烁粒子 */
.sp {
  position: absolute;
  border-radius: 50%;
  background: radial-gradient(circle, #FFFFFF, #A5D6A7);
  animation: sparkle 2s ease-in-out infinite;
}

.sp1 { width: 8rpx; height: 8rpx; top: 50rpx; left: 28rpx; animation-delay: 0s; }
.sp2 { width: 6rpx; height: 6rpx; top: 90rpx; right: 28rpx; animation-delay: 0.4s; }
.sp3 { width: 10rpx; height: 10rpx; top: 150rpx; left: 24rpx; animation-delay: 0.8s; }
.sp4 { width: 6rpx; height: 6rpx; top: 200rpx; right: 30rpx; animation-delay: 1.2s; }
.sp5 { width: 7rpx; height: 7rpx; top: 260rpx; left: 40rpx; animation-delay: 1.6s; }

@keyframes sparkle {
  0%, 100% { opacity: 0.2; transform: scale(0.5); }
  50% { opacity: 1; transform: scale(1.2); box-shadow: 0 0 12rpx rgba(255, 255, 255, 0.8); }
}

/* 树冠层 */
.tree-aura {
  position: absolute;
  width: 100rpx;
  height: 40rpx;
  background: radial-gradient(ellipse, rgba(80, 200, 140, 0.5), transparent);
  border-radius: 50%;
  bottom: 210rpx;
  left: 50%;
  transform: translateX(-50%);
  animation: auraPulse 2s ease-in-out infinite;
}

@keyframes auraPulse {
  0%, 100% { opacity: 0.5; transform: translateX(-50%) scale(1); }
  50% { opacity: 1; transform: translateX(-50%) scale(1.2); }
}

.leaf {
  position: absolute;
  border-radius: 50%;
  left: 50%;
  transform: translateX(-50%);
  transition: all 0.3s ease;
}

.l1 { width: 100rpx; height: 52rpx; background: #2E8B57; bottom: 80rpx; opacity: 0.9; }
.l2 { width: 84rpx; height: 48rpx; background: #3CB371; bottom: 108rpx; opacity: 0.9; }
.l3 { width: 70rpx; height: 42rpx; background: #66CDAA; bottom: 132rpx; opacity: 0.9; }
.l4 { width: 56rpx; height: 36rpx; background: #90EE90; bottom: 154rpx; opacity: 0.9; }
.l5 { width: 42rpx; height: 28rpx; background: #C1FFC1; bottom: 176rpx; opacity: 0.9; }

.trunk {
  position: absolute;
  width: 16rpx;
  height: 44rpx;
  background: linear-gradient(180deg, #8B5A2B, #5D3A1A);
  border-radius: 8rpx;
  bottom: 38rpx;
  left: 50%;
  transform: translateX(-50%);
}

.capsule-shine {
  position: absolute;
  width: 32rpx;
  height: 180rpx;
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.6), rgba(255, 255, 255, 0.1));
  border-radius: 16rpx;
  top: 28rpx;
  left: 20rpx;
  transform: rotate(-8deg);
  pointer-events: none;
}

.capsule-bottom-glow {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 70rpx;
  background: linear-gradient(0deg, rgba(80, 200, 140, 0.2), transparent);
  pointer-events: none;
}

/* 统计信息栏 */
.hero-info {
  display: flex;
  align-items: center;
  background: linear-gradient(135deg, rgba(210, 240, 220, 0.9), rgba(190, 225, 200, 0.85));
  backdrop-filter: blur(24rpx);
  border-radius: 36rpx;
  padding: 24rpx 28rpx;
  border: 1rpx solid rgba(255, 255, 255, 0.7);
  position: relative;
  z-index: 10;
}

.hinfo-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.hinfo-val {
  font-size: 36rpx;
  font-weight: 700;
  color: #2C5A3A;
}

.hinfo-val.neon {
  color: #3D9B6D;
  text-shadow: 0 0 12rpx rgba(61, 155, 109, 0.5);
}

.hinfo-lbl {
  font-size: 22rpx;
  color: #5B8C6E;
  margin-top: 6rpx;
}

.hinfo-div {
  width: 1rpx;
  height: 48rpx;
  background: rgba(91, 140, 110, 0.2);
}

/* ===== 今日数据卡片 ===== */
.stats-row {
  display: flex;
  gap: 16rpx;
  margin-bottom: 28rpx;
}

.stat-card {
  flex: 1;
  backdrop-filter: blur(24rpx);
  border-radius: 32rpx;
  padding: 28rpx 16rpx;
  text-align: center;
  border: 1rpx solid rgba(255, 255, 255, 0.7);
  transition: all 0.3s ease;
}

.stat-card:active { transform: scale(0.96); }
.stat-card-1 { background: linear-gradient(135deg, rgba(100, 200, 150, 0.85), rgba(80, 180, 130, 0.8)); }
.stat-card-2 { background: linear-gradient(135deg, rgba(80, 180, 200, 0.85), rgba(60, 160, 180, 0.8)); }
.stat-card-3 { background: linear-gradient(135deg, rgba(120, 190, 140, 0.85), rgba(100, 170, 120, 0.8)); }

.stat-icon { font-size: 44rpx; margin-bottom: 12rpx; }
.stat-value { font-size: 40rpx; font-weight: 700; color: #FFFFFF; display: block; margin-bottom: 6rpx; text-shadow: 0 2rpx 4rpx rgba(0,0,0,0.1); }
.stat-label { font-size: 22rpx; color: rgba(255,255,255,0.9); }

/* ===== 进度卡片 ===== */
.progress-card {
  background: linear-gradient(135deg, rgba(200, 230, 210, 0.9), rgba(180, 215, 190, 0.85));
  backdrop-filter: blur(24rpx);
  border-radius: 40rpx;
  padding: 32rpx;
  margin-bottom: 32rpx;
  border: 1rpx solid rgba(255, 255, 255, 0.7);
}

.progress-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 24rpx;
}

.progress-title { font-size: 30rpx; font-weight: 700; color: #2C5A3A; }
.progress-sub { font-size: 22rpx; color: #5B8C6E; margin-top: 6rpx; }
.progress-percent { background: rgba(61, 155, 109, 0.2); border-radius: 24rpx; padding: 8rpx 20rpx; }
.percent-value { font-size: 36rpx; font-weight: 700; color: #3D9B6D; }

.progress-track {
  height: 12rpx;
  background: rgba(91, 140, 110, 0.2);
  border-radius: 6rpx;
  margin-bottom: 20rpx;
  position: relative;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, #6FB88A, #3D9B6D);
  border-radius: 6rpx;
  position: relative;
  transition: width 0.5s ease;
  box-shadow: 0 0 12rpx rgba(61, 155, 109, 0.5);
}

.progress-dot {
  position: absolute;
  right: -8rpx;
  top: 50%;
  transform: translateY(-50%);
  width: 20rpx;
  height: 20rpx;
  background: #FFFFFF;
  border: 3rpx solid #3D9B6D;
  border-radius: 50%;
  box-shadow: 0 0 10rpx rgba(61, 155, 109, 0.8);
}

.progress-hint { font-size: 24rpx; color: #5B8C6E; }
.progress-hint.done { color: #3D9B6D; font-weight: 500; }

/* ===== 功能导航标题 ===== */
.section-header {
  display: flex;
  align-items: baseline;
  gap: 12rpx;
  margin-bottom: 24rpx;
}

.section-bar {
  width: 6rpx;
  height: 32rpx;
  background: linear-gradient(180deg, #6FB88A, #3D9B6D);
  border-radius: 3rpx;
  box-shadow: 0 0 8rpx rgba(61, 155, 109, 0.5);
}

.section-title { font-size: 32rpx; font-weight: 700; color: #2C5A3A; }
.section-sub { font-size: 22rpx; color: #5B8C6E; margin-left: auto; }

/* ===== 2x2 功能导航网格 ===== */
.feature-grid {
  display: flex;
  flex-direction: column;
  gap: 20rpx;
  margin-bottom: 32rpx;
}

.feature-row {
  display: flex;
  gap: 20rpx;
  width: 100%;
}

.feature-card {
  flex: 1;
  backdrop-filter: blur(24rpx);
  border-radius: 36rpx;
  padding: 28rpx 24rpx;
  border: 1rpx solid rgba(255, 255, 255, 0.7);
  position: relative;
  transition: all 0.3s cubic-bezier(0.2, 0.9, 0.4, 1.1);
  box-shadow: 0 12rpx 28rpx rgba(0, 0, 0, 0.08);
  overflow: hidden;
}

.feature-card:active { transform: scale(0.97); }

.feature-card-ai { background: linear-gradient(135deg, rgba(80, 200, 140, 0.9), rgba(60, 170, 120, 0.85)); }
.feature-card-steps { background: linear-gradient(135deg, rgba(100, 190, 160, 0.9), rgba(80, 160, 140, 0.85)); }
.feature-card-sport { background: linear-gradient(135deg, rgba(90, 180, 170, 0.9), rgba(70, 150, 140, 0.85)); }
.feature-card-rank { background: linear-gradient(135deg, rgba(70, 170, 180, 0.9), rgba(50, 140, 150, 0.85)); }

/* 图标容器 */
.feature-icon-container {
  position: relative;
  width: 100rpx;
  height: 100rpx;
  margin-bottom: 20rpx;
}

.particle-glow {
  position: absolute;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(255, 255, 255, 0.8), rgba(255, 255, 200, 0.4), transparent);
  pointer-events: none;
  animation: particleGlowFloat 2s ease-in-out infinite;
}

.particle-glow-1 { width: 20rpx; height: 20rpx; top: -10rpx; left: 20rpx; animation-delay: 0s; }
.particle-glow-2 { width: 15rpx; height: 15rpx; top: 60rpx; right: -15rpx; animation-delay: 0.5s; }
.particle-glow-3 { width: 18rpx; height: 18rpx; bottom: -10rpx; left: 40rpx; animation-delay: 1s; }
.particle-glow-4 { width: 12rpx; height: 12rpx; top: 20rpx; left: -12rpx; animation-delay: 1.5s; }
.particle-glow-5 { width: 25rpx; height: 25rpx; bottom: 30rpx; right: -8rpx; animation-delay: 2s; }

@keyframes particleGlowFloat {
  0%, 100% { opacity: 0; transform: scale(0.5); }
  50% { opacity: 0.8; transform: scale(1.2); box-shadow: 0 0 20rpx rgba(255, 255, 200, 0.8); }
}

.feature-icon-wrapper {
  position: relative;
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 10;
}

.feature-outer-ring,
.feature-middle-ring,
.feature-inner-ring {
  position: absolute;
  border-radius: 50%;
  pointer-events: none;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.feature-card:active .feature-outer-ring,
.feature-card:active .feature-middle-ring,
.feature-card:active .feature-inner-ring {
  opacity: 1;
}

.feature-outer-ring {
  top: -12rpx;
  left: -12rpx;
  right: -12rpx;
  bottom: -12rpx;
  border: 2rpx solid rgba(255, 255, 255, 0.6);
  animation: ringRotate 10s linear infinite;
}

.feature-middle-ring {
  top: -6rpx;
  left: -6rpx;
  right: -6rpx;
  bottom: -6rpx;
  border: 2rpx solid rgba(255, 255, 255, 0.5);
  animation: ringRotateReverse 7s linear infinite;
}

.feature-inner-ring {
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  border: 2rpx solid rgba(255, 255, 255, 0.4);
  animation: ringRotate 5s linear infinite;
}

.feature-icon-box {
  width: 88rpx;
  height: 88rpx;
  border-radius: 28rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  z-index: 10;
  background: rgba(255, 255, 255, 0.25);
  backdrop-filter: blur(4rpx);
  transition: all 0.3s ease;
}

.feature-card:active .feature-icon-box {
  transform: scale(1.05);
  background: rgba(255, 255, 255, 0.35);
}

.feature-icon { font-size: 52rpx; }
.feature-name { font-size: 28rpx; font-weight: 700; color: #FFFFFF; margin-bottom: 6rpx; text-shadow: 0 2rpx 4rpx rgba(0,0,0,0.1); }
.feature-desc { font-size: 22rpx; color: rgba(255,255,255,0.85); line-height: 1.4; }
.feature-arrow { position: absolute; bottom: 24rpx; right: 24rpx; font-size: 36rpx; color: rgba(255,255,255,0.6); transition: all 0.3s ease; }
.feature-card:active .feature-arrow { transform: translateX(6rpx); color: #FFFFFF; }
.radial-glow { position: absolute; top: 50%; left: 50%; transform: translate(-50%, -50%); width: 0; height: 0; border-radius: 50%; background: radial-gradient(circle, rgba(255,255,255,0.4), transparent); transition: all 0.3s ease; pointer-events: none; opacity: 0; }
.feature-card:active .radial-glow { width: 200%; height: 200%; opacity: 1; transition: all 0.4s ease; }

/* ===== 环保徽章 ===== */
.eco-badge {
  display: flex;
  align-items: center;
  gap: 20rpx;
  background: linear-gradient(135deg, rgba(210, 235, 220, 0.95), rgba(190, 220, 200, 0.9));
  backdrop-filter: blur(24rpx);
  border-radius: 36rpx;
  padding: 24rpx 28rpx;
  margin-bottom: 28rpx;
  border: 1rpx solid rgba(255, 255, 255, 0.6);
  position: relative;
  overflow: hidden;
  animation: badgeSlide 0.5s ease-out;
}

@keyframes badgeSlide {
  from { opacity: 0; transform: translateX(-40rpx); }
  to { opacity: 1; transform: translateX(0); }
}

.badge-shimmer {
  position: absolute;
  top: 0;
  left: -100%;
  width: 60%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.5), transparent);
  animation: shimmer 3s ease-in-out infinite;
}

@keyframes shimmer {
  0% { left: -100%; }
  60%, 100% { left: 120%; }
}

.badge-icon { font-size: 56rpx; animation: medalBounce 2s ease-in-out infinite; }
@keyframes medalBounce { 0%, 100% { transform: scale(1); } 50% { transform: scale(1.1); } }
.badge-info { flex: 1; }
.badge-title { font-size: 28rpx; font-weight: 700; color: #2C5A3A; display: block; margin-bottom: 6rpx; }
.badge-text { font-size: 22rpx; color: #3D9B6D; }
.badge-close { width: 48rpx; height: 48rpx; border-radius: 50%; background: rgba(0,0,0,0.05); display: flex; align-items: center; justify-content: center; }
.badge-close text { font-size: 36rpx; color: #5B8C6E; }

/* ===== 底部 ===== */
.footer { text-align: center; padding: 20rpx 0; }
.footer-text { font-size: 22rpx; color: #9CC0AC; line-height: 1.6; }
.spacer { height: 140rpx; }
</style>
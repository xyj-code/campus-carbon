<template>
  <view class="page">
    <!-- Aurora background blobs -->
    <view class="blob b1"></view>
    <view class="blob b2"></view>
    <view class="blob b3"></view>

    <scroll-view scroll-y="true" class="scroller" :show-scrollbar="false">

      <!-- ===== HEADER ===== -->
      <view class="header-card">
        <view class="hd-left">
          <text class="hi-name">{{ greeting }}，{{ studentName }} 🌿</text>
          <text class="hi-date">{{ todayDate }}</text>
        </view>
        <view class="pts-pill" @click="showPointsTip">
          <text class="pts-star">✦</text>
          <text class="pts-num">{{ points }}</text>
          <text class="pts-unit">pts</text>
        </view>
      </view>

      <!-- ===== HERO: GLASS TREE CAPSULE ===== -->
      <view class="hero-section">
        <view class="hero-ambient"></view>

        <!-- Capsule wrapper (animated float) -->
        <view class="capsule-wrap">
          <view class="capsule-glow-bg"></view>
          <view class="capsule">
            <!-- Sparkle particles -->
            <view class="sp sp1"></view>
            <view class="sp sp2"></view>
            <view class="sp sp3"></view>
            <view class="sp sp4"></view>

            <!-- Tree layers (absolutely positioned) -->
            <view class="tree-aura"></view>
            <view class="leaf l1"></view>
            <view class="leaf l2"></view>
            <view class="leaf l3"></view>
            <view class="leaf l4"></view>
            <view class="leaf l5"></view>
            <view class="trunk"></view>
            <view class="ground-dot"></view>

            <!-- 3D glass shine -->
            <view class="capsule-shine"></view>
            <!-- Bottom glow -->
            <view class="capsule-bottom-glow"></view>
          </view>
        </view>

        <!-- Stats below capsule -->
        <view class="hero-info">
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

      <!-- ===== TODAY STATS ROW ===== -->
      <view class="stats-row">
        <view class="stat-card">
          <text class="sc-emoji">👟</text>
          <text class="sc-val neon">{{ todayData.steps }}</text>
          <text class="sc-lbl">今日步数</text>
        </view>
        <view class="stat-card">
          <text class="sc-emoji">⏱️</text>
          <text class="sc-val">{{ todayData.duration }}</text>
          <text class="sc-lbl">分钟</text>
        </view>
        <view class="stat-card">
          <text class="sc-emoji">🌱</text>
          <text class="sc-val">{{ todayData.carbon }}</text>
          <text class="sc-lbl">减碳(g)</text>
        </view>
      </view>

      <!-- ===== STEP PROGRESS ===== -->
      <view class="prog-card">
        <view class="prog-top">
          <view class="prog-top-left">
            <text class="prog-title">今日步行目标</text>
            <text class="prog-sub">目标 10,000 步挑战</text>
          </view>
          <view class="prog-pct-box">
            <text class="prog-pct neon">{{ stepPct }}%</text>
          </view>
        </view>
        <view class="prog-track">
          <view class="prog-fill" :style="{ width: stepPct + '%' }">
            <view class="prog-dot"></view>
          </view>
        </view>
        <text class="prog-hint" v-if="stepsLeft > 0">再走 {{ stepsLeft }} 步完成目标 🎯</text>
        <text class="prog-hint done" v-else>太棒了！今日目标已完成 🎉</text>
      </view>

      <!-- ===== FEATURE GRID ===== -->
      <view class="sec-header">
        <view class="sec-bar"></view>
        <text class="sec-title">功能导航</text>
      </view>

      <view class="feat-grid">
        <view class="feat-card" @click="navigateTo('/pages/aiSuggest/aiSuggest')">
          <view class="feat-icon-box ibox-0"><text class="feat-icon">🤖</text></view>
          <text class="feat-name">AI 减排建议</text>
          <text class="feat-desc">智能分析碳足迹</text>
          <text class="feat-arr">›</text>
        </view>
        <view class="feat-card" @click="navigateTo('/pages/stepCount/stepCount')">
          <view class="feat-icon-box ibox-1"><text class="feat-icon">👟</text></view>
          <text class="feat-name">步数统计</text>
          <text class="feat-desc">量化绿色出行</text>
          <text class="feat-arr">›</text>
        </view>
        <view class="feat-card" @click="navigateTo('/pages/sportRecord/sportRecord')">
          <view class="feat-icon-box ibox-2"><text class="feat-icon">🏃</text></view>
          <text class="feat-name">运动记录</text>
          <text class="feat-desc">追踪运动轨迹</text>
          <text class="feat-arr">›</text>
        </view>
        <view class="feat-card" @click="navigateTo('/pages/rank/rank')">
          <view class="feat-icon-box ibox-3"><text class="feat-icon">🏆</text></view>
          <text class="feat-name">减碳排名</text>
          <text class="feat-desc">减碳榜单</text>
          <text class="feat-arr">›</text>
        </view>
      </view>

      <!-- ===== ECO BADGE ===== -->
      <view class="eco-badge" v-if="badgeVisible">
        <view class="badge-shimmer"></view>
        <text class="badge-medal">🏅</text>
        <view class="badge-info">
          <text class="badge-name">绿色先锋徽章</text>
          <text class="badge-days">今日已低碳出行 · 坚持就是胜利</text>
        </view>
        <view class="badge-close" @click="badgeVisible = false">
          <text class="badge-x">×</text>
        </view>
      </view>

      <!-- Footer -->
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
      rankLoading: false,
      stepPct: 0,
      stepsLeft: 10000,
      badgeVisible: false,
      loading: true
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
  },
  onShow() {
    if (this.stuNo) {
      this.initDate();
      this.loadTodayData();
      this.loadRankData();
    }
  },
  methods: {
    initDate() {
      const now = new Date();
      const h = now.getHours();
      if (h < 6)       this.greeting = '夜深了';
      else if (h < 12) this.greeting = '早上好';
      else if (h < 14) this.greeting = '中午好';
      else if (h < 18) this.greeting = '下午好';
      else             this.greeting = '晚上好';
      const days = ['周日','周一','周二','周三','周四','周五','周六'];
      const months = ['Jan','Feb','Mar','Apr','May','Jun','Jul','Aug','Sep','Oct','Nov','Dec'];
      this.todayDate = `${days[now.getDay()]}，${months[now.getMonth()]} ${now.getDate()}`;
    },
    async loadRankData() {
  if (!this.stuNo) return;
  
  this.rankLoading = true;
  try {
    const res = await getRankData(this.stuNo, 'today');
    if (res && res.code === 200 && res.data && res.data.myRank) {
      this.userRank = res.data.myRank.rank || '--';
    }
  } catch (error) {
    console.error('获取排名数据失败:', error);
  } finally {
    this.rankLoading = false;
  }
},
    async loadTodayData() {
      this.loading = true;
      try {
        const now = new Date();
        const dateStr = `${now.getFullYear()}-${String(now.getMonth()+1).padStart(2,'0')}-${String(now.getDate()).padStart(2,'0')}`;
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
          // Tree level
          if (steps >= 15000)     { this.treeLevel = 5; this.treeName = '参天大树'; }
          else if (steps >= 10000){ this.treeLevel = 4; this.treeName = '茁壮成长'; }
          else if (steps >= 6000) { this.treeLevel = 3; this.treeName = '小树苗';  }
          else if (steps >= 3000) { this.treeLevel = 2; this.treeName = '破土而出';}
          else                    { this.treeLevel = 1; this.treeName = '嫩芽';    }
          if (steps > 0) this.badgeVisible = true;
        }
      } catch (e) {
        console.error('获取今日数据失败:', e);
      } finally {
        this.loading = false;
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
/* ===== PAGE ===== */
.page {
  min-height: 100vh;
  background: #FFFFFF;
  position: relative;
  overflow: hidden;
}

.scroller {
  height: 100vh;
  padding: 0 28rpx;
  box-sizing: border-box;
}

/* ===== BACKGROUND AURORA BLOBS ===== */
.blob {
  position: fixed;
  border-radius: 50%;
  pointer-events: none;
  z-index: 0;
}
.b1 {
  width: 560rpx; height: 560rpx;
  background: radial-gradient(circle, rgba(0, 245, 160, 0.18) 0%, transparent 70%);
  top: -120rpx; right: -120rpx;
}
.b2 {
  width: 420rpx; height: 420rpx;
  background: radial-gradient(circle, rgba(0, 217, 245, 0.12) 0%, transparent 70%);
  top: 380rpx; left: -100rpx;
}
.b3 {
  width: 380rpx; height: 380rpx;
  background: radial-gradient(circle, rgba(123, 104, 238, 0.10) 0%, transparent 70%);
  bottom: 250rpx; right: -80rpx;
}

/* ===== HEADER ===== */
.header-card {
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: space-between;
  margin-top: 100rpx;
  margin-bottom: 36rpx;
  padding: 28rpx 32rpx;
  background: rgba(255, 255, 255, 0.78);
  backdrop-filter: blur(20rpx);
  -webkit-backdrop-filter: blur(20rpx);
  border: 1.5rpx solid rgba(255, 255, 255, 0.92);
  border-radius: 32rpx;
  box-shadow: 0 8rpx 32rpx rgba(0, 0, 0, 0.06);
  position: relative;
  z-index: 1;
}

.hd-left { display: flex; flex-direction: column; }

.hi-name {
  font-size: 34rpx;
  font-weight: 700;
  color: #1A2332;
  display: block;
}

.hi-date {
  font-size: 24rpx;
  color: #8A9BB0;
  margin-top: 4rpx;
  display: block;
}

.pts-pill {
  display: flex;
  flex-direction: row;
  align-items: center;
  gap: 6rpx;
  background: linear-gradient(135deg, #00F5A0, #00C896);
  border-radius: 100rpx;
  padding: 12rpx 26rpx;
  box-shadow: 0 4rpx 20rpx rgba(0, 200, 150, 0.42);
}

.pts-star { font-size: 22rpx; color: #FFFFFF; }

.pts-num {
  font-size: 32rpx;
  font-weight: 700;
  color: #FFFFFF;
}

.pts-unit {
  font-size: 20rpx;
  color: rgba(255, 255, 255, 0.82);
}

/* ===== HERO SECTION ===== */
.hero-section {
  position: relative;
  z-index: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 36rpx;
}

.hero-ambient {
  position: absolute;
  width: 340rpx; height: 340rpx;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(0, 200, 150, 0.18) 0%, transparent 70%);
  top: 50%; left: 50%;
  transform: translate(-50%, -50%);
  pointer-events: none;
}

/* Capsule float wrapper */
.capsule-wrap {
  position: relative;
  margin-bottom: 32rpx;
  animation: cap-float 4s ease-in-out infinite;
}

@keyframes cap-float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-14rpx); }
}

.capsule-glow-bg {
  position: absolute;
  inset: -24rpx;
  border-radius: 110rpx;
  box-shadow: 0 0 80rpx rgba(0, 200, 150, 0.38), 0 0 160rpx rgba(0, 200, 150, 0.16);
  pointer-events: none;
}

/* The glass capsule */
.capsule {
  width: 174rpx;
  height: 344rpx;
  border-radius: 87rpx;
  background: linear-gradient(
    180deg,
    rgba(255, 255, 255, 0.28) 0%,
    rgba(240, 255, 250, 0.20) 50%,
    rgba(210, 255, 240, 0.28) 100%
  );
  border: 2rpx solid rgba(255, 255, 255, 0.72);
  position: relative;
  overflow: hidden;
  box-shadow:
    inset 0 0 30rpx rgba(255, 255, 255, 0.28),
    inset 0 -20rpx 40rpx rgba(0, 200, 150, 0.10),
    0 20rpx 60rpx rgba(0, 0, 0, 0.08);
}

/* Glass shine strip (3D effect) */
.capsule-shine {
  position: absolute;
  width: 38rpx; height: 190rpx;
  border-radius: 19rpx;
  background: linear-gradient(180deg, rgba(255,255,255,0.56) 0%, rgba(255,255,255,0.04) 100%);
  top: 28rpx; left: 20rpx;
  transform: rotate(-6deg);
  pointer-events: none;
  z-index: 20;
}

/* Bottom inner glow */
.capsule-bottom-glow {
  position: absolute;
  bottom: 0; left: 0; right: 0;
  height: 80rpx;
  background: linear-gradient(0deg, rgba(0, 200, 150, 0.18) 0%, transparent 100%);
  pointer-events: none;
  z-index: 0;
}

/* Sparkles */
.sp {
  position: absolute;
  border-radius: 50%;
  background: #A7F3D0;
  z-index: 18;
  animation: sp-blink 2.4s ease-in-out infinite;
}
.sp1 { width: 9rpx; height: 9rpx;  top: 54rpx;  left: 26rpx;  animation-delay: 0s; }
.sp2 { width: 7rpx; height: 7rpx;  top: 86rpx;  right: 24rpx; animation-delay: 0.6s; }
.sp3 { width: 11rpx; height: 11rpx; top: 164rpx; left: 20rpx;  animation-delay: 1.2s; }
.sp4 { width: 7rpx; height: 7rpx;  top: 204rpx; right: 28rpx; animation-delay: 1.8s; }

@keyframes sp-blink {
  0%, 100% { opacity: 0.2; transform: scale(0.7); }
  50% { opacity: 1; transform: scale(1.3); box-shadow: 0 0 8rpx rgba(167, 243, 208, 0.9); }
}

/* ---- Tree ---- */
.tree-aura {
  position: absolute;
  width: 100rpx; height: 42rpx;
  border-radius: 50%;
  background: radial-gradient(ellipse, rgba(0, 200, 150, 0.45) 0%, transparent 70%);
  bottom: 222rpx;
  left: 50%; transform: translateX(-50%);
  z-index: 0;
}

.leaf {
  position: absolute;
  border-radius: 50%;
  left: 50%;
  transform: translateX(-50%);
}

/* z-index: tip (l5) in front, base (l1) behind */
.l1 { width: 112rpx; height: 58rpx; background: #059669; bottom: 84rpx;  z-index: 1; box-shadow: 0 0 18rpx rgba(5, 150, 105, 0.72), 0 0 36rpx rgba(5, 150, 105, 0.28); }
.l2 { width: 92rpx;  height: 52rpx; background: #10B981; bottom: 112rpx; z-index: 2; box-shadow: 0 0 15rpx rgba(16, 185, 129, 0.72), 0 0 30rpx rgba(16, 185, 129, 0.28); }
.l3 { width: 74rpx;  height: 46rpx; background: #34D399; bottom: 138rpx; z-index: 3; box-shadow: 0 0 13rpx rgba(52, 211, 153, 0.82), 0 0 26rpx rgba(52, 211, 153, 0.32); }
.l4 { width: 56rpx;  height: 38rpx; background: #6EE7B7; bottom: 160rpx; z-index: 4; box-shadow: 0 0 12rpx rgba(110, 231, 183, 0.90), 0 0 24rpx rgba(110, 231, 183, 0.40); }
.l5 { width: 38rpx;  height: 28rpx; background: #A7F3D0; bottom: 182rpx; z-index: 5; box-shadow: 0 0 12rpx rgba(167, 243, 208, 1.00), 0 0 24rpx rgba(167, 243, 208, 0.55); }

.trunk {
  position: absolute;
  width: 15rpx; height: 46rpx;
  background: linear-gradient(180deg, #92400E, #78350F);
  border-radius: 8rpx;
  bottom: 40rpx;
  left: 50%; transform: translateX(-50%);
  z-index: 6;
}

.ground-dot {
  position: absolute;
  width: 72rpx; height: 14rpx;
  border-radius: 50%;
  background: rgba(0, 200, 150, 0.38);
  bottom: 26rpx;
  left: 50%; transform: translateX(-50%);
  z-index: 0;
}

/* Hero info bar */
.hero-info {
  display: flex;
  flex-direction: row;
  align-items: center;
  background: rgba(255, 255, 255, 0.78);
  backdrop-filter: blur(20rpx);
  -webkit-backdrop-filter: blur(20rpx);
  border: 1.5rpx solid rgba(255, 255, 255, 0.92);
  border-radius: 28rpx;
  padding: 22rpx 28rpx;
  box-shadow: 0 8rpx 32rpx rgba(0, 0, 0, 0.05);
  width: 100%;
  box-sizing: border-box;
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
  color: #1A2332;
  display: block;
}

.hinfo-val.neon {
  color: #00C896;
  text-shadow: 0 0 12rpx rgba(0, 200, 150, 0.5);
}

.hinfo-lbl {
  font-size: 22rpx;
  color: #8A9BB0;
  margin-top: 4rpx;
  display: block;
}

.hinfo-div {
  width: 1rpx; height: 54rpx;
  background: rgba(0, 0, 0, 0.08);
  margin: 0 6rpx;
}

/* ===== TODAY STATS ===== */
.stats-row {
  display: flex;
  flex-direction: row;
  gap: 16rpx;
  margin-bottom: 24rpx;
  position: relative;
  z-index: 1;
}

.stat-card {
  flex: 1;
  background: rgba(255, 255, 255, 0.78);
  backdrop-filter: blur(20rpx);
  -webkit-backdrop-filter: blur(20rpx);
  border: 1.5rpx solid rgba(255, 255, 255, 0.92);
  border-radius: 26rpx;
  padding: 28rpx 14rpx;
  box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.05);
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8rpx;
}

.sc-emoji { font-size: 38rpx; }

.sc-val {
  font-size: 40rpx;
  font-weight: 700;
  color: #1A2332;
  font-variant-numeric: tabular-nums;
  letter-spacing: -1rpx;
}

.sc-val.neon {
  color: #00C896;
  text-shadow: 0 0 14rpx rgba(0, 200, 150, 0.52);
}

.sc-lbl {
  font-size: 22rpx;
  color: #8A9BB0;
}

/* ===== STEP PROGRESS ===== */
.prog-card {
  background: rgba(255, 255, 255, 0.78);
  backdrop-filter: blur(20rpx);
  -webkit-backdrop-filter: blur(20rpx);
  border: 1.5rpx solid rgba(255, 255, 255, 0.92);
  border-radius: 32rpx;
  padding: 32rpx 32rpx;
  box-shadow: 0 8rpx 32rpx rgba(0, 0, 0, 0.05);
  margin-bottom: 36rpx;
  position: relative;
  z-index: 1;
}

.prog-top {
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 22rpx;
}

.prog-title {
  font-size: 30rpx;
  font-weight: 600;
  color: #1A2332;
  display: block;
}

.prog-sub {
  font-size: 22rpx;
  color: #8A9BB0;
  margin-top: 4rpx;
  display: block;
}

.prog-pct-box {
  background: linear-gradient(135deg, rgba(0, 245, 160, 0.14), rgba(0, 200, 150, 0.12));
  border-radius: 16rpx;
  padding: 8rpx 22rpx;
}

.prog-pct {
  font-size: 38rpx;
  font-weight: 700;
  color: #00C896;
  text-shadow: 0 0 12rpx rgba(0, 200, 150, 0.5);
}

.prog-track {
  height: 14rpx;
  background: rgba(0, 200, 150, 0.12);
  border-radius: 7rpx;
  overflow: visible;
  margin-bottom: 18rpx;
  position: relative;
}

.prog-fill {
  height: 100%;
  background: linear-gradient(90deg, #00F5A0, #00C896);
  border-radius: 7rpx;
  position: relative;
  min-width: 3%;
  max-width: 100%;
  box-shadow: 0 0 14rpx rgba(0, 200, 150, 0.65);
}

.prog-dot {
  position: absolute;
  right: -7rpx; top: 50%;
  transform: translateY(-50%);
  width: 20rpx; height: 20rpx;
  border-radius: 50%;
  background: #FFFFFF;
  border: 3rpx solid #00C896;
  box-shadow: 0 0 10rpx rgba(0, 200, 150, 0.9);
}

.prog-hint {
  font-size: 24rpx;
  color: #8A9BB0;
}

.prog-hint.done {
  color: #00C896;
  font-weight: 500;
}

/* ===== FEATURE GRID ===== */
.sec-header {
  display: flex;
  flex-direction: row;
  align-items: center;
  gap: 14rpx;
  margin-bottom: 22rpx;
  position: relative;
  z-index: 1;
}

.sec-bar {
  width: 7rpx; height: 32rpx;
  border-radius: 4rpx;
  background: linear-gradient(180deg, #00F5A0, #00C896);
  box-shadow: 0 0 10rpx rgba(0, 200, 150, 0.55);
}

.sec-title {
  font-size: 32rpx;
  font-weight: 700;
  color: #1A2332;
}

.feat-grid {
  display: flex;
  flex-direction: row;
  flex-wrap: wrap;
  gap: 16rpx;
  margin-bottom: 32rpx;
  position: relative;
  z-index: 1;
}

.feat-card {
  width: calc(50% - 8rpx);
  box-sizing: border-box;
  background: rgba(255, 255, 255, 0.78);
  backdrop-filter: blur(20rpx);
  -webkit-backdrop-filter: blur(20rpx);
  border: 1.5rpx solid rgba(255, 255, 255, 0.92);
  border-radius: 28rpx;
  padding: 28rpx 22rpx;
  box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.05);
  position: relative;
  overflow: hidden;
}

.feat-icon-box {
  width: 76rpx; height: 76rpx;
  border-radius: 22rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 18rpx;
}

.feat-icon { font-size: 38rpx; }

.ibox-0 { background: rgba(0, 200, 150, 0.14); }
.ibox-1 { background: rgba(0, 217, 245, 0.14); }
.ibox-2 { background: rgba(123, 104, 238, 0.14); }
.ibox-3 { background: rgba(245, 158, 11, 0.14); }

.feat-name {
  font-size: 28rpx;
  font-weight: 600;
  color: #1A2332;
  display: block;
  margin-bottom: 7rpx;
}

.feat-desc {
  font-size: 22rpx;
  color: #8A9BB0;
  display: block;
  line-height: 1.4;
}

.feat-arr {
  position: absolute;
  bottom: 22rpx; right: 22rpx;
  font-size: 40rpx;
  color: #CBD5E0;
  font-weight: 300;
  line-height: 1;
}

/* ===== ECO BADGE ===== */
.eco-badge {
  display: flex;
  flex-direction: row;
  align-items: center;
  gap: 18rpx;
  background: rgba(255, 255, 255, 0.88);
  backdrop-filter: blur(20rpx);
  -webkit-backdrop-filter: blur(20rpx);
  border: 1.5rpx solid rgba(0, 200, 150, 0.28);
  border-radius: 28rpx;
  padding: 22rpx 26rpx;
  margin-bottom: 24rpx;
  box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.05), 0 0 32rpx rgba(0, 200, 150, 0.10);
  position: relative;
  overflow: hidden;
  z-index: 1;
}

.badge-shimmer {
  position: absolute;
  top: 0; left: -100%;
  width: 60%; height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255,255,255,0.45), transparent);
  animation: shimmer 3.2s ease-in-out infinite;
  pointer-events: none;
}

@keyframes shimmer {
  0% { left: -100%; }
  60%, 100% { left: 120%; }
}

.badge-medal { font-size: 52rpx; }

.badge-info { flex: 1; }

.badge-name {
  font-size: 28rpx;
  font-weight: 600;
  color: #1A2332;
  display: block;
}

.badge-days {
  font-size: 22rpx;
  color: #00C896;
  margin-top: 5rpx;
  display: block;
}

.badge-close {
  width: 48rpx; height: 48rpx;
  border-radius: 50%;
  background: rgba(0, 0, 0, 0.06);
  display: flex;
  align-items: center;
  justify-content: center;
}

.badge-x {
  font-size: 36rpx;
  color: #8A9BB0;
  line-height: 1;
}

/* ===== FOOTER ===== */
.footer {
  text-align: center;
  padding: 10rpx 0 0;
}

.footer-text {
  font-size: 22rpx;
  color: #C4D0DC;
  line-height: 1.8;
}

.spacer { height: 140rpx; }
</style>

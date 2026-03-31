<template>
  <view class="page">
    <!-- 动态渐变背景 -->
    <view class="gradient-bg"></view>

    <!-- 动态粒子系统 - 增强版 -->
    <view class="particle-system">
      <view v-for="(item, index) in 80" :key="index" class="particle" :style="particleStyles[index]"></view>
    </view>

    <scroll-view scroll-y="true" class="scroller" :show-scrollbar="false">
      <!-- 顶部高级光晕卡片 -->
      <view class="hero-card floating-card">
        <view class="hero-glow"></view>
        <view class="hero-glow-2"></view>
        <view class="hero-glow-3"></view>

        <!-- 漂浮粒子光晕 -->
        <view class="hero-particles">
          <view v-for="(item, index) in 12" :key="index" class="hero-particle" :style="heroParticleStyles[index]"></view>
        </view>

        <view class="avatar-section">
          <view class="avatar-wrap">
            <text class="avatar-text">{{ (profileData.name || username || 'U').charAt(0).toUpperCase() }}</text>
            <view class="avatar-ring"></view>
            <view class="avatar-ring-2"></view>
          </view>
          <text class="user-name">{{ profileData.name || username || '低碳用户' }}</text>
          <text class="user-id">ID: {{ username }}</text>
          <view class="badge-level" :style="{ background: levelGradient }">
            <text>🌿 Lv.{{ userLevel }}</text>
          </view>
        </view>

        <!-- 统计数据 -->
        <view class="hero-stats">
          <view class="hero-stat">
            <text class="hero-stat-value">{{ profileData.totalCarbon || 0 }}</text>
            <text class="hero-stat-label">减碳量(kg)</text>
          </view>
          <view class="hero-stat-divider"></view>
          <view class="hero-stat">
            <text class="hero-stat-value">{{ profileData.points || 0 }}</text>
            <text class="hero-stat-label">碳积分</text>
          </view>
          <view class="hero-stat-divider"></view>
          <view class="hero-stat">
            <text class="hero-stat-value">{{ profileData.consecutiveDays || 0 }}</text>
            <text class="hero-stat-label">连续打卡</text>
          </view>
        </view>
      </view>

      <!-- 2x2 功能网格 -->
      <view class="feature-grid">
        <!-- 我的低碳保护区 -->
        <view class="feature-card card-1 floating-card" @click="goCarbonReserve">
          <view class="card-glow-layer"></view>
          <view class="card-icon-wrap">
            <text class="card-icon">🌳</text>
            <view class="icon-sparkle"></view>
          </view>
          <text class="card-title">低碳保护区</text>
          <text class="card-desc">守护绿色家园</text>
          <view class="card-arrow">→</view>
        </view>

        <!-- 积分明细 -->
        <view class="feature-card card-2 floating-card" @click="goPointsDetail">
          <view class="card-glow-layer"></view>
          <view class="card-icon-wrap">
            <text class="card-icon">⭐</text>
            <view class="icon-sparkle"></view>
          </view>
          <text class="card-title">积分明细</text>
          <text class="card-desc">查看积分记录</text>
          <view class="card-arrow">→</view>
        </view>

        <!-- 个人碳账本 - 改为页面跳转 -->
        <view class="feature-card card-3 floating-card" @click="goCarbonLedger">
          <view class="card-glow-layer"></view>
          <view class="card-icon-wrap">
            <text class="card-icon">📊</text>
            <view class="icon-sparkle"></view>
          </view>
          <text class="card-title">碳账本</text>
          <text class="card-desc">追踪碳足迹</text>
          <view class="card-arrow">→</view>
        </view>

        <!-- 设置 - 改为页面跳转 -->
        <view class="feature-card card-4 floating-card" @click="goSettings">
          <view class="card-glow-layer"></view>
          <view class="card-icon-wrap">
            <text class="card-icon">⚙️</text>
            <view class="icon-sparkle"></view>
          </view>
          <text class="card-title">设置</text>
          <text class="card-desc">个性化配置</text>
          <view class="card-arrow">→</view>
        </view>
      </view>

      <!-- 关于我们 - 改为页面跳转 -->
      <view class="about-card floating-card" @click="goAbout">
        <view class="about-glow"></view>
        <view class="about-content">
          <view class="about-icon-wrap">
            <text class="about-icon">🌍</text>
          </view>
          <view class="about-text">
            <text class="about-title">关于我们</text>
            <text class="about-desc">低碳生活 · 绿色未来</text>
          </view>
          <text class="about-arrow">→</text>
        </view>
      </view>

      <!-- 退出登录 -->
      <view class="logout-btn floating-card" @click="logout">
        <text class="logout-text">退出登录</text>
      </view>

      <!-- 底部装饰 -->
      <view class="footer-deco">
        <text class="deco-text">💚 每一步低碳，都是对地球的温柔 💚</text>
      </view>

      <view class="spacer"></view>
    </scroll-view>

    <!-- 底部导航 -->
    <bottom-nav :currentIndex="2" />
  </view>
</template>

<script>
import BottomNav from '../../components/bottom-nav.vue';
import { getProfile, getStepCountList } from '../../utils/request.js';

export default {
  name: 'ProfilePage',
  components: { BottomNav },
  data() {
    return {
      username: '',
      profileData: {},
      weekBars: [],
      editName: '',
      notifyOn: true,
      privacyOn: false,
      particleStyles: []
    };
  },
  computed: {
    userLevel() {
      const points = this.profileData.points || 0;
      if (points >= 10000) return 5;
      if (points >= 5000) return 4;
      if (points >= 2000) return 3;
      if (points >= 500) return 2;
      return 1;
    },
    levelGradient() {
      const level = this.userLevel;
      if (level === 5) return 'linear-gradient(135deg, #FFD700, #FFA500)';
      if (level === 4) return 'linear-gradient(135deg, #C0C0C0, #A0A0A0)';
      if (level === 3) return 'linear-gradient(135deg, #CD7F32, #B87333)';
      if (level === 2) return 'linear-gradient(135deg, #6FB88A, #3D9B6D)';
      return 'linear-gradient(135deg, #A5D6A7, #81C784)';
    },
    treesCount() {
      return Math.round((this.profileData.totalCarbon || 0) / 0.02);
    },
    kmCount() {
      return Math.round((this.profileData.totalCarbon || 0) / 0.21);
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
  },
  onShow() {
    if (this.username) {
      this.loadProfile();
      this.loadWeekBars();
    }
  },
  methods: {
    initParticleStyles() {
      const styles = [];
      for (let i = 0; i < 80; i++) {
        styles.push({
          left: Math.random() * 100 + '%',
          animationDuration: (6 + Math.random() * 10) + 's',
          animationDelay: Math.random() * 8 + 's',
          width: (2 + Math.random() * 8) + 'rpx',
          height: (2 + Math.random() * 8) + 'rpx',
          opacity: 0.2 + Math.random() * 0.5
        });
      }
      this.particleStyles = styles;
    },
    getHeroParticleStyle(i) {
      return {
        left: Math.random() * 100 + '%',
        top: Math.random() * 100 + '%',
        animationDuration: (3 + Math.random() * 4) + 's',
        animationDelay: Math.random() * 2 + 's',
        width: (4 + Math.random() * 12) + 'rpx',
        height: (4 + Math.random() * 12) + 'rpx'
      };
    },
    async loadProfile() {
      try {
        const res = await getProfile(this.username);
        if (res && res.username) {
          this.profileData = res;
          this.editName = res.name || '';
        }
      } catch (e) {
        console.error('profile error', e);
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
    // 跳转到碳保护区
    goCarbonReserve() {
      uni.navigateTo({ url: '/pages/carbon保护区/carbon保护区' });
    },
    // 跳转到积分明细
    goPointsDetail() {
      uni.navigateTo({ url: '/pages/pointsDetail/pointsDetail' });
    },
    // 跳转到碳账本页面
    goCarbonLedger() {
      uni.navigateTo({ url: '/pages/carbonLedger/carbonLedger' });
    },
    // 跳转到设置页面
    goSettings() {
      uni.navigateTo({ url: '/pages/settings/settings' });
    },
    // 跳转到关于我们页面
    goAbout() {
      uni.navigateTo({ url: '/pages/about/about' });
    },
    // 保存昵称
    saveName() {
      uni.showToast({ title: '昵称已保存', icon: 'success' });
      this.profileData.name = this.editName;
      // 这里可以调用保存昵称的API
    },
    // 清除缓存
    clearCache() {
      uni.clearStorageSync();
      uni.showToast({ title: '缓存已清除', icon: 'success' });
    },
    // 复制邮箱
    copyService() {
      uni.setClipboardData({ data: 'service@lowcarbon.app' });
      uni.showToast({ title: '邮箱已复制', icon: 'success' });
    },
    // 退出登录
    logout() {
      uni.showModal({
        title: '退出登录',
        content: '确定要退出吗？',
        success: (res) => {
          if (res.confirm) {
            uni.removeStorageSync('username');
            uni.removeStorageSync('userName');
            uni.reLaunch({ url: '/pages/login/login' });
          }
        }
      });
    }
  }
};
</script>

<style scoped>
.page {
  min-height: 100vh;
  background: linear-gradient(180deg, #E8F5E9 0%, #C8E6C9 100%);
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
  rgba(129, 199, 132, 0.4) 0%,
  rgba(102, 187, 106, 0.2) 50%,
  rgba(76, 175, 80, 0.1) 100%);
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
  background: radial-gradient(circle, rgba(80, 200, 140, 0.6), rgba(60, 160, 100, 0.2));
  border-radius: 50%;
  animation: floatUp linear infinite;
  box-shadow: 0 0 12rpx rgba(80, 200, 140, 0.4);
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

/* ===== 高级光晕头部卡片 ===== */
.hero-card {
  position: relative;
  margin-top: 100rpx;
  margin-bottom: 32rpx;
  background: linear-gradient(135deg, #2E7D32, #1B5E20);
  border-radius: 64rpx;
  padding: 48rpx 32rpx 40rpx;
  overflow: hidden;
  box-shadow: 0 20rpx 40rpx rgba(0, 0, 0, 0.2);
}

.hero-glow {
  position: absolute;
  top: -30%;
  left: -20%;
  width: 140%;
  height: 140%;
  background: radial-gradient(circle, rgba(255, 255, 255, 0.25), transparent);
  animation: rotateGlow 12s linear infinite;
}

.hero-glow-2 {
  position: absolute;
  bottom: -30%;
  right: -20%;
  width: 120%;
  height: 120%;
  background: radial-gradient(circle, rgba(255, 215, 0, 0.2), transparent);
  animation: rotateGlowReverse 10s linear infinite;
}

.hero-glow-3 {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 200%;
  height: 200%;
  background: radial-gradient(circle, rgba(100, 200, 150, 0.15), transparent);
  animation: pulseGlow 4s ease-in-out infinite;
}

@keyframes rotateGlow {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

@keyframes rotateGlowReverse {
  from { transform: rotate(0deg); }
  to { transform: rotate(-360deg); }
}

@keyframes pulseGlow {
  0%, 100% { opacity: 0.3; transform: translate(-50%, -50%) scale(1); }
  50% { opacity: 0.6; transform: translate(-50%, -50%) scale(1.1); }
}

.hero-particles {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  pointer-events: none;
}

.hero-particle {
  position: absolute;
  background: radial-gradient(circle, rgba(255, 255, 255, 0.8), transparent);
  border-radius: 50%;
  animation: heroParticleFloat 3s ease-in-out infinite;
}

@keyframes heroParticleFloat {
  0%, 100% { transform: translateY(0) scale(1); opacity: 0; }
  50% { transform: translateY(-30rpx) scale(1.2); opacity: 0.6; }
}

.avatar-section {
  text-align: center;
  position: relative;
  z-index: 10;
}

.avatar-wrap {
  position: relative;
  width: 140rpx;
  height: 140rpx;
  margin: 0 auto 20rpx;
}

.avatar-text {
  width: 100%;
  height: 100%;
  background: rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(8rpx);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 64rpx;
  font-weight: 700;
  color: #FFFFFF;
  box-shadow: 0 8rpx 20rpx rgba(0, 0, 0, 0.2);
}

.avatar-ring {
  position: absolute;
  top: -12rpx;
  left: -12rpx;
  right: -12rpx;
  bottom: -12rpx;
  border: 3rpx solid rgba(255, 255, 255, 0.5);
  border-radius: 50%;
  animation: ringRotate 10s linear infinite;
}

.avatar-ring-2 {
  position: absolute;
  top: -24rpx;
  left: -24rpx;
  right: -24rpx;
  bottom: -24rpx;
  border: 2rpx solid rgba(255, 215, 0, 0.3);
  border-radius: 50%;
  animation: ringRotateReverse 8s linear infinite;
}

@keyframes ringRotate {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

@keyframes ringRotateReverse {
  from { transform: rotate(0deg); }
  to { transform: rotate(-360deg); }
}

.user-name {
  font-size: 40rpx;
  font-weight: 700;
  color: #FFFFFF;
  display: block;
  margin-bottom: 8rpx;
  text-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.2);
}

.user-id {
  font-size: 24rpx;
  color: rgba(255, 255, 255, 0.7);
  display: block;
  margin-bottom: 20rpx;
}

.badge-level {
  display: inline-block;
  padding: 8rpx 28rpx;
  border-radius: 40rpx;
  font-size: 24rpx;
  font-weight: 600;
  color: #FFFFFF;
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.1);
}

.hero-stats {
  display: flex;
  align-items: center;
  justify-content: space-around;
  margin-top: 32rpx;
  padding-top: 24rpx;
  border-top: 1rpx solid rgba(255, 255, 255, 0.2);
  position: relative;
  z-index: 10;
}

.hero-stat {
  text-align: center;
  flex: 1;
}

.hero-stat-value {
  font-size: 44rpx;
  font-weight: 700;
  color: #FFD700;
  display: block;
  text-shadow: 0 2rpx 4rpx rgba(0, 0, 0, 0.2);
}

.hero-stat-label {
  font-size: 22rpx;
  color: rgba(255, 255, 255, 0.7);
  display: block;
  margin-top: 8rpx;
}

.hero-stat-divider {
  width: 1rpx;
  height: 50rpx;
  background: rgba(255, 255, 255, 0.3);
}

/* ===== 2x2 功能网格 ===== */
.feature-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20rpx;
  margin-bottom: 20rpx;
}

.feature-card {
  position: relative;
  border-radius: 40rpx;
  padding: 32rpx 24rpx;
  overflow: hidden;
  transition: all 0.3s ease;
  box-shadow: 0 8rpx 24rpx rgba(0, 0, 0, 0.08);
}

.feature-card:active {
  transform: scale(0.97);
}

.card-glow-layer {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: radial-gradient(circle at 30% 20%, rgba(255, 255, 255, 0.2), transparent);
  pointer-events: none;
}

/* 卡片不同颜色 */
.card-1 {
  background: linear-gradient(135deg, #6FB88A, #3D9B6D);
}
.card-2 {
  background: linear-gradient(135deg, #FFB347, #FF8C00);
}
.card-3 {
  background: linear-gradient(135deg, #5C9EFF, #3D7AE5);
}
.card-4 {
  background: linear-gradient(135deg, #A56BFF, #8B4CFF);
}

.card-icon-wrap {
  position: relative;
  width: 80rpx;
  height: 80rpx;
  margin-bottom: 20rpx;
}

.card-icon {
  font-size: 52rpx;
  position: relative;
  z-index: 2;
}

.icon-sparkle {
  position: absolute;
  top: -8rpx;
  right: -8rpx;
  width: 24rpx;
  height: 24rpx;
  background: radial-gradient(circle, #FFD700, transparent);
  border-radius: 50%;
  animation: sparklePulse 2s ease-in-out infinite;
}

@keyframes sparklePulse {
  0%, 100% { opacity: 0.5; transform: scale(0.8); }
  50% { opacity: 1; transform: scale(1.2); }
}

.card-title {
  font-size: 32rpx;
  font-weight: 700;
  color: #FFFFFF;
  display: block;
  margin-bottom: 8rpx;
}

.card-desc {
  font-size: 22rpx;
  color: rgba(255, 255, 255, 0.8);
  display: block;
}

.card-arrow {
  position: absolute;
  bottom: 28rpx;
  right: 24rpx;
  font-size: 32rpx;
  color: rgba(255, 255, 255, 0.6);
  transition: transform 0.3s ease;
}

.feature-card:active .card-arrow {
  transform: translateX(8rpx);
  color: #FFFFFF;
}

/* ===== 关于我们卡片 ===== */
.about-card {
  background: linear-gradient(135deg, #FF8C69, #FF6B4A);
  border-radius: 40rpx;
  margin-bottom: 20rpx;
  overflow: hidden;
  position: relative;
  transition: all 0.3s ease;
}

.about-card:active {
  transform: scale(0.98);
}

.about-glow {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: radial-gradient(circle at 70% 30%, rgba(255, 255, 255, 0.3), transparent);
  pointer-events: none;
}

.about-content {
  display: flex;
  align-items: center;
  padding: 32rpx 28rpx;
}

.about-icon-wrap {
  width: 80rpx;
  height: 80rpx;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 28rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 24rpx;
}

.about-icon {
  font-size: 48rpx;
}

.about-text {
  flex: 1;
}

.about-title {
  font-size: 32rpx;
  font-weight: 700;
  color: #FFFFFF;
  display: block;
  margin-bottom: 6rpx;
}

.about-desc {
  font-size: 22rpx;
  color: rgba(255, 255, 255, 0.8);
}

.about-arrow {
  font-size: 40rpx;
  color: rgba(255, 255, 255, 0.6);
  transition: transform 0.3s ease;
}

.about-card:active .about-arrow {
  transform: translateX(8rpx);
  color: #FFFFFF;
}

/* 退出登录按钮 */
.logout-btn {
  background: rgba(255, 255, 255, 0.9);
  border-radius: 60rpx;
  padding: 28rpx;
  text-align: center;
  margin-bottom: 32rpx;
  transition: all 0.3s ease;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.04);
}

.logout-btn:active {
  transform: scale(0.98);
}

.logout-text {
  font-size: 30rpx;
  font-weight: 600;
  color: #F44336;
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
  height: 120rpx;
}
</style>
<template>
  <view class="page">
    <!-- 动态渐变背景 -->
    <view class="gradient-bg"></view>

    <!-- 动态粒子系统 -->
    <view class="particle-system">
      <view v-for="(item, index) in 50" :key="index" class="particle" :style="particleStyles[index]"></view>
    </view>

    <scroll-view scroll-y="true" class="scroller" :show-scrollbar="false">
      <!-- 顶部标题 -->
      <view class="page-header floating-card">
        <view class="header-icon-wrapper">
          <view class="header-icon-ring"></view>
          <text class="header-icon">⭐</text>
        </view>
        <text class="header-title">积分明细</text>
        <view class="header-icon-wrapper">
          <view class="header-icon-ring"></view>
          <text class="header-icon">📊</text>
        </view>
      </view>

      <!-- 积分余额卡片 -->
      <view class="balance-card floating-card">
        <view class="balance-glow"></view>
        <view class="balance-content">
          <text class="balance-label">当前积分</text>
          <view class="balance-value-wrap">
            <text class="balance-value">{{ profileData.points || 0 }}</text>
            <text class="balance-unit">分</text>
          </view>
          <text class="balance-tip">✨ 低碳生活，积攒绿色积分</text>
        </view>
        <view class="balance-decoration">
          <text>🎁</text>
        </view>
      </view>

      <!-- 统计卡片 - 三栏水平对齐 -->
      <view class="stats-row">
        <view class="stat-card">
          <text class="stat-icon">📈</text>
          <text class="stat-value">{{ totalIncome }}</text>
          <text class="stat-label">总收入</text>
        </view>
        <view class="stat-divider"></view>
        <view class="stat-card">
          <text class="stat-icon">📉</text>
          <text class="stat-value">{{ totalExpense }}</text>
          <text class="stat-label">总支出</text>
        </view>
        <view class="stat-divider"></view>
        <view class="stat-card">
          <text class="stat-icon">⚖️</text>
          <text class="stat-value">{{ netPoints }}</text>
          <text class="stat-label">净增积分</text>
        </view>
      </view>

      <!-- 筛选栏 -->
      <view class="filter-section">
        <view
            v-for="f in filters"
            :key="f.value"
            class="filter-btn"
            :class="{ active: activeFilter === f.value }"
            @click="switchFilter(f.value)"
        >
          <text>{{ f.label }}</text>
        </view>
      </view>

      <!-- 记录列表 -->
      <view class="records-list" v-if="records.length > 0">
        <view
            v-for="(r, i) in records"
            :key="i"
            class="record-item floating-card"
            :style="{ animationDelay: i * 0.05 + 's' }"
        >
          <view class="record-icon" :class="r.type">
            <text>{{ r.type === 'income' ? '↑' : '↓' }}</text>
          </view>
          <view class="record-info">
            <text class="record-title">{{ r.title }}</text>
            <text class="record-remark">{{ r.remark }}</text>
            <text class="record-time">{{ formatTime(r.createTime) }}</text>
          </view>
          <text class="record-amount" :class="r.type">
            {{ r.type === 'income' ? '+' : '-' }}{{ r.amount }}
          </text>
        </view>
      </view>

      <!-- 空状态 -->
      <view v-else-if="!loading" class="empty-card floating-card">
        <view class="empty-icon-wrap">
          <view class="empty-glow"></view>
          <text class="empty-icon">🪙</text>
        </view>
        <text class="empty-title">暂无积分记录</text>
        <text class="empty-desc">参与低碳活动赚取积分吧</text>
      </view>

      <!-- 加载中 -->
      <view v-if="loading" class="loading-card floating-card">
        <view class="loading-spinner"></view>
        <text>加载中...</text>
      </view>

      <!-- 加载更多 -->
      <view class="load-more" v-if="records.length > 0 && currentPage < totalPages && !loading">
        <view class="load-more-btn" @click="loadMore">
          <text>加载更多</text>
          <text class="btn-arrow">↓</text>
        </view>
      </view>
      <view class="no-more" v-else-if="records.length > 0 && currentPage >= totalPages">
        <text>— 已经到底了 —</text>
      </view>

      <!-- 底部装饰 -->
      <view class="footer-deco">
        <text class="deco-text">💚 积分虽小，汇聚成绿色力量 💚</text>
      </view>

      <view class="spacer"></view>
    </scroll-view>
  </view>
</template>

<script>
import { getProfile, getPointsRecords } from '../../utils/request.js';

export default {
  name: 'PointsDetailPage',
  data() {
    return {
      username: '',
      profileData: {},
      filters: [
        { label: '全部', value: 'all' },
        { label: '收入', value: 'income' },
        { label: '支出', value: 'expense' }
      ],
      activeFilter: 'all',
      records: [],
      currentPage: 1,
      totalPages: 1,
      pageSize: 15,
      loading: false,
      particleStyles: []
    };
  },
  computed: {
    totalIncome() {
      if (!this.records || this.records.length === 0) return 0;
      return this.records
          .filter(r => r.type === 'income')
          .reduce((sum, r) => sum + (Number(r.amount) || 0), 0);
    },
    totalExpense() {
      if (!this.records || this.records.length === 0) return 0;
      return this.records
          .filter(r => r.type === 'expense')
          .reduce((sum, r) => sum + (Number(r.amount) || 0), 0);
    },
    netPoints() {
      const net = this.totalIncome - this.totalExpense;
      return net >= 0 ? `+${net}` : `-${Math.abs(net)}`;
    }
  },
  onLoad() {
    this.username = uni.getStorageSync('username');
    if (!this.username) {
      uni.reLaunch({ url: '/pages/login/login' });
      return;
    }
    this.initParticleStyles();
    this.loadProfile();
    this.loadRecords(true);
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
    formatTime(timeStr) {
      if (!timeStr) return '';
      return timeStr.substring(0, 16).replace('T', ' ');
    },
    async loadProfile() {
      try {
        const res = await getProfile(this.username);
        this.profileData = res;
      } catch (e) {
        console.error('加载积分失败:', e);
      }
    },
    async loadRecords(reset = false) {
      if (this.loading) return;
      if (reset) {
        this.currentPage = 1;
        this.records = [];
      }
      this.loading = true;
      try {
        const res = await getPointsRecords(this.username, this.activeFilter, this.currentPage, this.pageSize);
        const newList = res.list || [];
        if (reset) {
          this.records = newList;
        } else {
          this.records = [...this.records, ...newList];
        }
        this.totalPages = res.totalPages || 1;
      } catch (e) {
        uni.showToast({ title: '加载失败', icon: 'none' });
        this.records = [];
      } finally {
        this.loading = false;
      }
    },
    switchFilter(val) {
      if (this.activeFilter === val) return;
      this.activeFilter = val;
      this.loadRecords(true);
    },
    loadMore() {
      if (this.currentPage < this.totalPages) {
        this.currentPage++;
        this.loadRecords(false);
      }
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

/* 页面标题 */
.page-header {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-top: 100rpx;
  margin-bottom: 32rpx;
  gap: 24rpx;
}

.header-icon-wrapper {
  position: relative;
  width: 56rpx;
  height: 56rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.header-icon-ring {
  position: absolute;
  width: 100%;
  height: 100%;
  border: 2rpx solid rgba(61, 155, 109, 0.4);
  border-radius: 50%;
  animation: ringPulse 2s ease-in-out infinite;
}

@keyframes ringPulse {
  0%, 100% { transform: scale(1); opacity: 0.5; }
  50% { transform: scale(1.2); opacity: 1; }
}

.header-icon {
  font-size: 36rpx;
  position: relative;
  z-index: 2;
}

.header-title {
  font-size: 44rpx;
  font-weight: 700;
  background: linear-gradient(135deg, #2C5A3A, #3D9B6D);
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
}

/* 积分余额卡片 */
.balance-card {
  position: relative;
  background: linear-gradient(135deg, #3D9B6D, #2C5A3A);
  border-radius: 56rpx;
  padding: 40rpx 36rpx;
  margin-bottom: 28rpx;
  overflow: hidden;
  box-shadow: 0 16rpx 40rpx rgba(61, 155, 109, 0.3);
}

.balance-glow {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: radial-gradient(circle at 20% 30%, rgba(255, 255, 255, 0.2), transparent);
  pointer-events: none;
}

.balance-content {
  position: relative;
  z-index: 2;
}

.balance-label {
  font-size: 28rpx;
  color: rgba(255, 255, 255, 0.8);
  display: block;
  margin-bottom: 16rpx;
}

.balance-value-wrap {
  display: flex;
  align-items: baseline;
  gap: 8rpx;
  margin-bottom: 16rpx;
}

.balance-value {
  font-size: 80rpx;
  font-weight: 700;
  color: #FFD700;
  line-height: 1;
}

.balance-unit {
  font-size: 32rpx;
  color: rgba(255, 255, 255, 0.9);
}

.balance-tip {
  font-size: 24rpx;
  color: rgba(255, 255, 255, 0.7);
}

.balance-decoration {
  position: absolute;
  bottom: 20rpx;
  right: 20rpx;
  font-size: 80rpx;
  opacity: 0.3;
  z-index: 1;
}

/* 统计卡片行 - 水平对齐 */
.stats-row {
  display: flex;
  align-items: stretch;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 48rpx;
  margin-bottom: 28rpx;
  overflow: hidden;
  box-shadow: 0 12rpx 32rpx rgba(0, 0, 0, 0.08);
}

.stat-card {
  flex: 1;
  padding: 28rpx 16rpx;
  text-align: center;
  background: transparent;
}

.stat-icon {
  font-size: 44rpx;
  display: block;
  margin-bottom: 12rpx;
}

.stat-value {
  font-size: 36rpx;
  font-weight: 700;
  color: #2C5A3A;
  display: block;
  margin-bottom: 8rpx;
}

.stat-label {
  font-size: 24rpx;
  color: #9CC0AC;
  display: block;
}

.stat-divider {
  width: 1rpx;
  background: rgba(100, 200, 150, 0.3);
  margin: 20rpx 0;
}

/* 筛选栏 */
.filter-section {
  display: flex;
  gap: 20rpx;
  margin-bottom: 28rpx;
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(24rpx);
  border-radius: 60rpx;
  padding: 8rpx;
  border: 1rpx solid rgba(255, 255, 255, 0.6);
}

.filter-btn {
  flex: 1;
  height: 72rpx;
  border-radius: 52rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
  background: transparent;
}

.filter-btn text {
  font-size: 28rpx;
  font-weight: 500;
  color: #5B8C6E;
  transition: all 0.3s ease;
}

.filter-btn.active {
  background: linear-gradient(135deg, #6FB88A, #3D9B6D);
  box-shadow: 0 4rpx 16rpx rgba(61, 155, 109, 0.3);
}

.filter-btn.active text {
  color: #FFFFFF;
}

.filter-btn:active {
  transform: scale(0.96);
}

/* 记录列表 */
.records-list {
  display: flex;
  flex-direction: column;
  gap: 16rpx;
  margin-bottom: 28rpx;
}

.record-item {
  display: flex;
  align-items: center;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 36rpx;
  padding: 24rpx;
  transition: all 0.3s ease;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.04);
}

.record-item:active {
  transform: translateX(8rpx);
}

.record-icon {
  width: 72rpx;
  height: 72rpx;
  border-radius: 36rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32rpx;
  font-weight: bold;
  margin-right: 20rpx;
  flex-shrink: 0;
}

.record-icon.income {
  background: linear-gradient(135deg, rgba(61, 155, 109, 0.2), rgba(61, 155, 109, 0.1));
  color: #3D9B6D;
}

.record-icon.expense {
  background: linear-gradient(135deg, rgba(255, 107, 53, 0.2), rgba(255, 107, 53, 0.1));
  color: #FF6B35;
}

.record-info {
  flex: 1;
}

.record-title {
  font-size: 28rpx;
  font-weight: 600;
  color: #2C5A3A;
  display: block;
  margin-bottom: 6rpx;
}

.record-remark {
  font-size: 22rpx;
  color: #9CC0AC;
  display: block;
  margin-bottom: 4rpx;
}

.record-time {
  font-size: 20rpx;
  color: #C8E6C9;
  display: block;
}

.record-amount {
  font-size: 32rpx;
  font-weight: 700;
  flex-shrink: 0;
}

.record-amount.income {
  color: #3D9B6D;
}

.record-amount.expense {
  color: #FF6B35;
}

/* 空状态卡片 */
.empty-card {
  background: rgba(255, 255, 255, 0.9);
  border-radius: 48rpx;
  padding: 80rpx 40rpx;
  margin-bottom: 28rpx;
  text-align: center;
  box-shadow: 0 12rpx 32rpx rgba(0, 0, 0, 0.08);
}

.empty-icon-wrap {
  position: relative;
  width: 120rpx;
  height: 120rpx;
  margin: 0 auto 24rpx;
}

.empty-glow {
  position: absolute;
  top: -10rpx;
  left: -10rpx;
  right: -10rpx;
  bottom: -10rpx;
  background: radial-gradient(circle, rgba(100, 200, 150, 0.4), transparent);
  border-radius: 50%;
  animation: glowPulse 2s ease-in-out infinite;
}

@keyframes glowPulse {
  0%, 100% { opacity: 0.5; transform: scale(1); }
  50% { opacity: 1; transform: scale(1.2); }
}

.empty-icon {
  font-size: 80rpx;
  position: relative;
  z-index: 2;
  display: block;
  text-align: center;
  line-height: 120rpx;
}

.empty-title {
  font-size: 32rpx;
  font-weight: 700;
  color: #2C5A3A;
  display: block;
  margin-bottom: 12rpx;
}

.empty-desc {
  font-size: 24rpx;
  color: #9CC0AC;
}

/* 加载卡片 */
.loading-card {
  background: rgba(255, 255, 255, 0.9);
  border-radius: 48rpx;
  padding: 60rpx;
  text-align: center;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20rpx;
  color: #9CC0AC;
}

.loading-spinner {
  width: 48rpx;
  height: 48rpx;
  border: 4rpx solid rgba(61, 155, 109, 0.2);
  border-top-color: #3D9B6D;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

/* 加载更多 */
.load-more {
  text-align: center;
  padding: 20rpx 0 40rpx;
}

.load-more-btn {
  display: inline-flex;
  align-items: center;
  gap: 12rpx;
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(24rpx);
  padding: 20rpx 48rpx;
  border-radius: 60rpx;
  transition: all 0.3s ease;
}

.load-more-btn:active {
  transform: scale(0.96);
  background: rgba(255, 255, 255, 0.95);
}

.load-more-btn text {
  font-size: 26rpx;
  color: #3D9B6D;
  font-weight: 500;
}

.btn-arrow {
  font-size: 28rpx;
  animation: bounceArrow 1s ease-in-out infinite;
}

@keyframes bounceArrow {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(4rpx); }
}

.no-more {
  text-align: center;
  padding: 20rpx 0 40rpx;
  font-size: 24rpx;
  color: #C8E6C9;
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
  height: 60rpx;
}
</style>
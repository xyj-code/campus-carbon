<template>
  <view class="page">
    <!-- 动态渐变背景 -->
    <view class="gradient-bg"></view>

    <!-- 动态粒子系统 -->
    <view class="particle-system">
      <view v-for="(item, index) in 40" :key="index" class="particle" :style="particleStyles[index]"></view>
    </view>

    <scroll-view scroll-y="true" class="scroller" :show-scrollbar="false">
      <!-- 顶部标题 -->
      <view class="page-header floating-card">
        <view class="header-icon-wrapper">
          <view class="header-icon-ring"></view>
          <text class="header-icon">🏆</text>
        </view>
        <text class="header-title">减碳排名</text>
        <view class="header-icon-wrapper">
          <view class="header-icon-ring"></view>
          <text class="header-icon">📈</text>
        </view>
      </view>

      <!-- 时间筛选 -->
      <view class="filter-section">
        <view
            class="filter-btn"
            :class="{ active: activeFilter === 'today' }"
            @click="switchFilter('today')"
        >
          <text>今日</text>
        </view>
        <view
            class="filter-btn"
            :class="{ active: activeFilter === 'week' }"
            @click="switchFilter('week')"
        >
          <text>本周</text>
        </view>
        <view
            class="filter-btn"
            :class="{ active: activeFilter === 'month' }"
            @click="switchFilter('month')"
        >
          <text>本月</text>
        </view>
      </view>

      <!-- 我的排名卡片 -->
      <view class="card my-rank-card floating-card">
        <view class="card-header">
          <view class="header-dot"></view>
          <text class="card-title">👑 我的排名</text>
        </view>

        <view class="rank-stats">
          <view class="stat-item">
            <text class="stat-icon">🏅</text>
            <text class="stat-value gradient-text">{{ myRank.rank || 0 }}</text>
            <text class="stat-label">当前排名</text>
          </view>
          <view class="stat-divider"></view>
          <view class="stat-item">
            <text class="stat-icon">🌿</text>
            <text class="stat-value gradient-text">{{ myRank.carbonReduction || 0 }}</text>
            <text class="stat-label">减碳量(kg)</text>
          </view>
          <view class="stat-divider"></view>
          <view class="stat-item">
            <text class="stat-icon">⭐</text>
            <text class="stat-value gradient-text">{{ myRank.points || 0 }}</text>
            <text class="stat-label">碳积分</text>
          </view>
        </view>
      </view>

      <!-- 排行榜标题 -->
      <view class="section-header">
        <view class="section-bar"></view>
        <text class="section-title">排行榜</text>
        <text class="section-sub">{{ activeFilter === 'today' ? '今日' : activeFilter === 'week' ? '本周' : '本月' }}排行</text>
      </view>

      <!-- 排行榜列表 -->
      <view class="rank-list">
        <view
            v-for="(item, index) in rankList"
            :key="item.id || index"
            class="rank-item floating-card"
            :class="{
            'top-1': index === 0,
            'top-2': index === 1,
            'top-3': index === 2,
            'is-self': item.isSelf
          }"
            @click="viewUserProfile(item.id)"
        >
          <view class="rank-number" :class="{ 'top-rank': index < 3 }">
            <text v-if="index === 0">🥇</text>
            <text v-else-if="index === 1">🥈</text>
            <text v-else-if="index === 2">🥉</text>
            <text v-else>{{ index + 1 }}</text>
          </view>

          <view class="user-avatar">
            <text>{{ item.avatar || '👤' }}</text>
            <view class="avatar-glow" v-if="index < 3"></view>
          </view>

          <view class="user-info">
            <text class="user-name">{{ item.name || item.username || '用户' }}</text>
            <text class="user-badge" v-if="item.isSelf">我</text>
          </view>

          <view class="user-stats">
            <text class="carbon-value">{{ item.carbonReduction || 0 }} kg</text>
            <text class="points-value">{{ item.points || 0 }} 积分</text>
          </view>

          <view class="like-btn" @click.stop="likeUser(item.id)">
            <text class="like-icon">{{ item.isLiked ? '❤️' : '🤍' }}</text>
            <text class="like-count">{{ item.likes || 0 }}</text>
          </view>
        </view>
      </view>

      <!-- 加载更多 -->
      <view class="load-more" v-if="hasMore && !isLoading" @click="loadMore">
        <text class="load-more-text">上拉加载更多</text>
      </view>
      <view class="loading" v-if="isLoading">
        <view class="loading-spinner"></view>
        <text class="loading-text">加载中...</text>
      </view>

      <!-- 空状态 -->
      <view class="empty-card" v-if="rankList.length === 0 && !isLoading">
        <view class="empty-icon-wrapper">
          <view class="empty-glow"></view>
          <text class="empty-icon">🏆</text>
        </view>
        <text class="empty-title">暂无排行数据</text>
        <text class="empty-sub">快去参与低碳活动吧</text>
      </view>

      <!-- 底部装饰 -->
      <view class="footer-deco">
        <text class="deco-text">💚 低碳生活，绿色未来 · 一起为地球减碳 💚</text>
      </view>

      <view class="spacer"></view>
    </scroll-view>
  </view>
</template>

<script>
import { getRankData } from '../../utils/request.js';

export default {
  data() {
    return {
      activeFilter: 'today',
      isLoading: false,
      hasMore: true,
      myRank: {
        rank: 0,
        carbonReduction: 0,
        points: 0
      },
      rankList: [],
      stuNo: '',
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
    this.initParticleStyles();
    this.loadRankData();
  },
  onPullDownRefresh() {
    this.refreshRankData();
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
    switchFilter(filter) {
      this.activeFilter = filter;
      this.loadRankData();
    },
    async loadRankData() {
      this.isLoading = true;
      try {
        const res = await getRankData(this.stuNo, this.activeFilter);
        if (res && res.code === 200 && res.data) {
          this.myRank = res.data.myRank || {
            rank: 0,
            carbonReduction: 0,
            points: 0
          };

          let rankList = res.data.rankList || [];
          // 确保当前用户在自己的排名中高亮显示
          rankList = rankList.map(item => ({
            ...item,
            isSelf: item.id === this.stuNo
          }));

          this.rankList = rankList;
        } else {
          // 模拟数据用于测试
          this.myRank = {
            rank: 15,
            carbonReduction: 12.5,
            points: 1250
          };
          this.rankList = [
            { id: '1', username: '低碳达人', avatar: '🌿', carbonReduction: 89.2, points: 8920, likes: 128, isLiked: false },
            { id: '2', username: '绿色先锋', avatar: '🍃', carbonReduction: 76.5, points: 7650, likes: 96, isLiked: false },
            { id: '3', username: '环保卫士', avatar: '🌍', carbonReduction: 68.3, points: 6830, likes: 84, isLiked: false },
            { id: '4', username: '低碳行者', avatar: '🚲', carbonReduction: 55.8, points: 5580, likes: 67, isLiked: false },
            { id: '5', username: '绿色使者', avatar: '🌸', carbonReduction: 48.2, points: 4820, likes: 52, isLiked: false },
            { id: '6', username: this.stuNo, carbonReduction: 12.5, points: 1250, likes: 23, isLiked: false, isSelf: true },
            { id: '7', username: '环保新人', avatar: '🌱', carbonReduction: 8.6, points: 860, likes: 15, isLiked: false },
            { id: '8', username: '低碳萌新', avatar: '🍀', carbonReduction: 5.2, points: 520, likes: 8, isLiked: false }
          ];
        }
      } catch (e) {
        console.error('获取排名数据失败:', e);
      } finally {
        this.isLoading = false;
      }
    },
    async refreshRankData() {
      await this.loadRankData();
      uni.stopPullDownRefresh();
    },
    loadMore() {
      if (this.isLoading) return;
      this.isLoading = true;
      setTimeout(() => {
        this.isLoading = false;
        this.hasMore = false;
        uni.showToast({ title: '已加载全部', icon: 'none' });
      }, 1000);
    },
    viewUserProfile(userId) {
      uni.showToast({ title: '查看用户主页', icon: 'none' });
    },
    likeUser(userId) {
      const user = this.rankList.find(item => item.id === userId);
      if (user) {
        if (user.isLiked) {
          user.isLiked = false;
          user.likes = (user.likes || 0) - 1;
          uni.showToast({ title: '取消点赞', icon: 'none' });
        } else {
          user.isLiked = true;
          user.likes = (user.likes || 0) + 1;
          uni.showToast({ title: '点赞成功', icon: 'success' });
        }
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

/* 时间筛选 */
.filter-section {
  display: flex;
  gap: 20rpx;
  margin-bottom: 32rpx;
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

/* 卡片样式 */
.card {
  background: rgba(255, 255, 255, 0.9);
  border-radius: 48rpx;
  padding: 36rpx 32rpx;
  margin-bottom: 28rpx;
  box-shadow: 0 12rpx 32rpx rgba(0, 0, 0, 0.08);
}

.card-header {
  display: flex;
  align-items: center;
  gap: 12rpx;
  margin-bottom: 28rpx;
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
  flex: 1;
}

/* 我的排名统计 */
.rank-stats {
  display: flex;
  align-items: center;
  justify-content: space-around;
  padding: 16rpx 0;
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
  font-size: 44rpx;
  font-weight: 700;
  display: block;
  margin-bottom: 8rpx;
}

.gradient-text {
  background: linear-gradient(135deg, #2C5A3A, #3D9B6D);
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
}

.stat-label {
  font-size: 24rpx;
  color: #9CC0AC;
}

.stat-divider {
  width: 1rpx;
  height: 80rpx;
  background: rgba(100, 200, 150, 0.3);
}

/* 区块标题 */
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

.section-title {
  font-size: 32rpx;
  font-weight: 700;
  color: #2C5A3A;
}

.section-sub {
  font-size: 24rpx;
  color: #9CC0AC;
  margin-left: auto;
}

/* 排行榜列表 */
.rank-list {
  display: flex;
  flex-direction: column;
  gap: 16rpx;
  margin-bottom: 28rpx;
}

.rank-item {
  display: flex;
  align-items: center;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 36rpx;
  padding: 20rpx 24rpx;
  transition: all 0.3s ease;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.04);
}

.rank-item:active {
  transform: translateX(8rpx);
  background: rgba(255, 255, 255, 0.95);
}

.rank-item.is-self {
  background: linear-gradient(135deg, rgba(111, 184, 138, 0.2), rgba(61, 155, 109, 0.15));
  border: 1rpx solid rgba(61, 155, 109, 0.3);
}

/* 排名数字 */
.rank-number {
  width: 72rpx;
  text-align: center;
}

.rank-number text {
  font-size: 32rpx;
  font-weight: 700;
  color: #5B8C6E;
}

.rank-number.top-rank text {
  font-size: 44rpx;
}

.rank-item.top-1 .rank-number text { color: #FFD700; text-shadow: 0 0 8rpx rgba(255, 215, 0, 0.5); }
.rank-item.top-2 .rank-number text { color: #C0C0C0; text-shadow: 0 0 8rpx rgba(192, 192, 192, 0.5); }
.rank-item.top-3 .rank-number text { color: #CD7F32; text-shadow: 0 0 8rpx rgba(205, 127, 50, 0.5); }

/* 用户头像 */
.user-avatar {
  position: relative;
  width: 80rpx;
  height: 80rpx;
  background: rgba(100, 200, 150, 0.2);
  border-radius: 40rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 44rpx;
  margin: 0 20rpx;
}

.avatar-glow {
  position: absolute;
  top: -4rpx;
  left: -4rpx;
  right: -4rpx;
  bottom: -4rpx;
  border-radius: 44rpx;
  background: radial-gradient(circle, rgba(100, 220, 150, 0.6), transparent);
  animation: avatarPulse 2s ease-in-out infinite;
}

@keyframes avatarPulse {
  0%, 100% { opacity: 0.3; transform: scale(1); }
  50% { opacity: 0.8; transform: scale(1.05); }
}

/* 用户信息 */
.user-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 6rpx;
}

.user-name {
  font-size: 28rpx;
  font-weight: 600;
  color: #2C5A3A;
}

.user-badge {
  display: inline-block;
  width: 40rpx;
  background: linear-gradient(135deg, #6FB88A, #3D9B6D);
  color: #FFFFFF;
  font-size: 20rpx;
  padding: 4rpx 12rpx;
  border-radius: 20rpx;
  text-align: center;
}

/* 用户数据 */
.user-stats {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 4rpx;
  margin-right: 20rpx;
}

.carbon-value {
  font-size: 26rpx;
  font-weight: 700;
  color: #3D9B6D;
}

.points-value {
  font-size: 22rpx;
  color: #9CC0AC;
}

/* 点赞按钮 */
.like-btn {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4rpx;
  padding: 12rpx 20rpx;
  background: rgba(100, 200, 150, 0.1);
  border-radius: 32rpx;
  transition: all 0.3s ease;
}

.like-btn:active {
  transform: scale(0.92);
  background: rgba(100, 200, 150, 0.2);
}

.like-icon {
  font-size: 28rpx;
}

.like-count {
  font-size: 20rpx;
  color: #9CC0AC;
}

/* 加载更多 */
.load-more {
  text-align: center;
  padding: 28rpx 0;
}

.load-more-text {
  font-size: 26rpx;
  color: #9CC0AC;
  transition: all 0.3s ease;
}

.load-more-text:active {
  opacity: 0.6;
}

.loading {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 16rpx;
  padding: 28rpx 0;
}

.loading-spinner {
  width: 32rpx;
  height: 32rpx;
  border: 3rpx solid rgba(61, 155, 109, 0.2);
  border-top-color: #3D9B6D;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.loading-text {
  font-size: 26rpx;
  color: #9CC0AC;
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

.empty-icon-wrapper {
  position: relative;
  width: 140rpx;
  height: 140rpx;
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
  font-size: 88rpx;
  position: relative;
  z-index: 2;
  display: block;
  text-align: center;
  line-height: 140rpx;
}

.empty-title {
  font-size: 32rpx;
  font-weight: 700;
  color: #2C5A3A;
  display: block;
  margin-bottom: 12rpx;
}

.empty-sub {
  font-size: 26rpx;
  color: #9CC0AC;
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
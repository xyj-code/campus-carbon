<template>
  <view class="container">
    <!-- 装饰元素 -->
    <view class="decorations">
      <view class="leaf leaf-1">🍃</view>
      <view class="leaf leaf-2">🚲</view>
      <view class="leaf leaf-3">🍃</view>
      <view class="leaf leaf-4">🚲</view>
    </view>
    
    <!-- 顶部标题 -->
    <view class="page-header">
      <text class="header-icon">🏆</text>
      <text class="header-title">减碳排名</text>
      <text class="header-icon">📈</text>
    </view>

    <!-- 时间筛选 -->
    <view class="filter-section" :class="{ 'loaded': isLoaded }">
      <button 
        class="filter-btn" 
        :class="{ 'active': activeFilter === 'today' }"
        @click="switchFilter('today')"
      >
        今日
      </button>
      <button 
        class="filter-btn" 
        :class="{ 'active': activeFilter === 'week' }"
        @click="switchFilter('week')"
      >
        本周
      </button>
      <button 
        class="filter-btn" 
        :class="{ 'active': activeFilter === 'month' }"
        @click="switchFilter('month')"
      >
        本月
      </button>
    </view>

    <!-- 我的排名卡片 -->
    <view class="my-rank-section" :class="{ 'loaded': isLoaded }">
      <text class="section-title">我的排名</text>
      <view class="my-rank-card">
        <view class="rank-info">
          <text class="rank-label">当前排名</text>
          <text class="rank-value gradient-text">{{ myRank.rank }}</text>
        </view>
        <view class="carbon-info">
          <text class="carbon-label">{{ activeFilter === 'today' ? '今日减碳量' : '我的减碳量' }}</text>
          <text class="carbon-value gradient-text">{{ myRank.carbonReduction }} kg</text>
        </view>
        <view class="points-info">
          <text class="points-label">{{ activeFilter === 'today' ? '今日积分' : '我的积分' }}</text>
          <text class="points-value gradient-text">{{ myRank.points }}</text>
        </view>
      </view>
    </view>

    <!-- 排行榜列表 -->
    <view class="rank-list-section" :class="{ 'loaded': isLoaded }">
      <text class="section-title">排行榜</text>
      <view class="rank-list">
        <view 
          v-for="(item, index) in rankList" 
          :key="item.id"
          class="rank-item"
          :class="{
            'top-1': index === 0,
            'top-2': index === 1,
            'top-3': index === 2,
            'self': item.isSelf
          }"
          @click="viewUserProfile(item.id)"
        >
          <view class="rank-number" :class="{
            'top-1': index === 0,
            'top-2': index === 1,
            'top-3': index === 2
          }">
            {{ index + 1 }}
          </view>
          <view class="user-info">
            <view class="avatar" :class="{
              'top-1': index === 0,
              'top-2': index === 1,
              'top-3': index === 2
            }">
              <text>{{ item.avatar }}</text>
            </view>
            <text class="username">{{ item.name || item.username }}</text>
          </view>
          <view class="rank-stats">
            <text class="carbon-amount">{{ item.carbonReduction }} kg</text>
            <text class="points">{{ item.points }} 积分</text>
          </view>
          <button 
            class="like-btn"
            @click.stop="likeUser(item.id)"
          >
            <text class="like-icon">{{ item.isLiked ? '❤️' : '🤍' }}</text>
            <text class="like-count">{{ item.likes }}</text>
          </button>
        </view>
      </view>
      <view class="load-more" v-if="!isLoading && hasMore">
        <text class="load-more-text" @click="loadMore">上拉加载更多</text>
      </view>
      <view class="loading" v-if="isLoading">
        <text class="loading-text">加载中...</text>
      </view>
    </view>
  </view>
</template>

<script>
import { getRankData } from '../../utils/request.js';

export default {
  data() {
    return {
      activeFilter: 'today',
      isLoaded: false,
      isLoading: false,
      hasMore: true,
      myRank: {
        rank: 0,
        change: 0,
        carbonReduction: 0,
        points: 0
      },
      rankList: [],
      stuNo: ''
    };
  },
  onLoad() {
    const stuNo = uni.getStorageSync('username');
    console.log('获取到的stuNo:', stuNo);
    if (!stuNo) {
      uni.reLaunch({ url: '/pages/login/login' });
      return;
    }
    this.stuNo = stuNo;
    this.loadRankData();
  },
  onPullDownRefresh() {
    this.refreshRankData();
  },
  methods: {
    switchFilter(filter) {
      this.activeFilter = filter;
      this.loadRankData();
    },
    async loadRankData() {
      // 从后端获取排名数据
      this.isLoaded = false;
      console.log('开始获取排名数据:', this.stuNo, this.activeFilter);
      try {
        const res = await getRankData(this.stuNo, this.activeFilter);
        console.log('获取排名数据响应:', res);
        if (res) {
          // 检查响应格式
          if (res.code === 200 && res.data) {
            this.myRank = res.data.myRank || {
              rank: 0,
              carbonReduction: 0,
              points: 0
            };
            this.rankList = res.data.rankList || [];
            console.log('更新排名数据:', this.myRank, this.rankList);
          } else {
            // 处理不同的响应格式
            console.error('获取排名数据失败，响应格式不正确:', res);
            // 设置默认数据
            this.myRank = {
              rank: 1,
              carbonReduction: 0,
              points: 0
            };
            this.rankList = [{
              id: this.stuNo,
              username: '我的账号',
              avatar: '👤',
              carbonReduction: 0,
              points: 0,
              likes: 0,
              isLiked: false,
              isSelf: true
            }];
          }
        } else {
          console.error('获取排名数据失败，无响应');
          // 设置默认数据
          this.myRank = {
            rank: 1,
            change: 0,
            carbonReduction: 0,
            points: 0
          };
          this.rankList = [{
            id: this.stuNo,
            username: '我的账号',
            avatar: '👤',
            carbonReduction: 0,
            points: 0,
            likes: 0,
            isLiked: false,
            isSelf: true
          }];
        }
      } catch (e) {
        console.error('获取排名数据失败:', e);
        // 设置默认数据
        this.myRank = {
          rank: 1,
          change: 0,
          carbonReduction: 0,
          points: 0
        };
        this.rankList = [{
          id: this.stuNo,
          username: '我的账号',
          avatar: '👤',
          carbonReduction: 0,
          points: 0,
          likes: 0,
          isLiked: false,
          isSelf: true
        }];
      } finally {
        this.isLoaded = true;
      }
    },
    async refreshRankData() {
      // 刷新排名数据
      this.isLoading = true;
      try {
        const res = await getRankData(this.stuNo, this.activeFilter);
        console.log('刷新排名数据响应:', res);
        if (res) {
          // 检查响应格式
          if (res.code === 200 && res.data) {
            this.myRank = res.data.myRank || {
              rank: 0,
              change: 0,
              carbonReduction: 0,
              points: 0
            };
            this.rankList = res.data.rankList || [];
            console.log('更新排名数据:', this.myRank, this.rankList);
          } else {
            // 处理不同的响应格式
            console.error('刷新排名数据失败，响应格式不正确:', res);
          }
        } else {
          console.error('刷新排名数据失败，无响应');
        }
      } catch (e) {
        console.error('刷新排名数据失败:', e);
      } finally {
        this.isLoading = false;
        uni.stopPullDownRefresh();
      }
    },
    loadMore() {
      // 加载更多数据
      if (this.isLoading) return;
      this.isLoading = true;
      // 这里可以实现分页加载逻辑
      setTimeout(() => {
        this.isLoading = false;
        this.hasMore = false;
      }, 1000);
    },
    viewUserProfile(userId) {
      // 查看用户个人主页
      uni.showToast({ title: '查看用户主页', icon: 'none' });
    },
    likeUser(userId) {
      // 点赞/取消点赞用户
      const user = this.rankList.find(item => item.id === userId);
      if (user) {
        if (user.isLiked) {
          // 取消点赞
          user.isLiked = false;
          user.likes -= 1;
          uni.showToast({ title: '取消点赞', icon: 'success' });
        } else {
          // 点赞
          user.isLiked = true;
          user.likes += 1;
          uni.showToast({ title: '点赞成功', icon: 'success' });
        }
      }
    }
  }
};
</script>

<style scoped>
.container {
  padding: 20rpx;
  min-height: 100vh;
  background: linear-gradient(135deg, #f1f8e9 0%, #ffffff 100%);
  position: relative;
  overflow: hidden;
}

/* 装饰元素 */
.decorations {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  pointer-events: none;
  z-index: 0;
}

.leaf {
  position: absolute;
  font-size: 32rpx;
  opacity: 0.6;
  animation: float 10s ease-in-out infinite;
}

.leaf-1 {
  top: 10%;
  left: 5%;
  animation-delay: 0s;
}

.leaf-2 {
  top: 30%;
  right: 8%;
  animation-delay: 2s;
}

.leaf-3 {
  bottom: 20%;
  left: 10%;
  animation-delay: 4s;
}

.leaf-4 {
  bottom: 40%;
  right: 5%;
  animation-delay: 6s;
}

@keyframes float {
  0%, 100% {
    transform: translateY(0) rotate(0deg);
  }
  50% {
    transform: translateY(-20px) rotate(10deg);
  }
}

/* 顶部标题 */
.page-header {
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 30rpx 0 40rpx;
  position: relative;
  z-index: 1;
}

.header-icon {
  font-size: 36rpx;
  margin: 0 12rpx;
  animation: bounce 2s ease-in-out infinite;
}

.header-title {
  font-size: 36rpx;
  font-weight: 700;
  color: #2d5016;
  letter-spacing: 2rpx;
  text-shadow: 0 2px 4px rgba(45, 80, 22, 0.1);
}

@keyframes bounce {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-5px);
  }
}

/* 时间筛选 */
.filter-section {
  display: flex;
  gap: 16rpx;
  margin-bottom: 30rpx;
  opacity: 0;
  transform: translateY(20px);
  transition: all 0.6s ease;
  position: relative;
  z-index: 1;
}

.filter-section.loaded {
  opacity: 1;
  transform: translateY(0);
}

.filter-btn {
  flex: 1;
  height: 72rpx;
  background: rgba(139, 191, 159, 0.1);
  color: #2d5016;
  border: 2rpx solid rgba(139, 191, 159, 0.3);
  border-radius: 12rpx;
  font-size: 24rpx;
  font-weight: 500;
  transition: all 0.3s ease;
}

.filter-btn.active {
  background: linear-gradient(135deg, #2d5016, #6a9d87);
  color: #ffffff;
  border-color: #6a9d87;
  box-shadow: 0 4px 12px rgba(45, 80, 22, 0.3);
}

.filter-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(45, 80, 22, 0.1);
}

/* 我的排名卡片 */
.my-rank-section {
  margin-bottom: 30rpx;
  padding: 30rpx;
  border-radius: 16px;
  background: linear-gradient(135deg, #f1f8e9 0%, #ffffff 100%);
  box-shadow: 0 8px 32px rgba(45, 80, 22, 0.08);
  border: 1px solid rgba(139, 191, 159, 0.2);
  opacity: 0;
  transform: translateY(20px);
  transition: all 0.6s ease 0.2s;
  position: relative;
  z-index: 1;
}

.my-rank-section.loaded {
  opacity: 1;
  transform: translateY(0);
}

.section-title {
  display: block;
  font-size: 28rpx;
  font-weight: 600;
  color: #2d5016;
  letter-spacing: 0.5px;
  margin-bottom: 24rpx;
}

.my-rank-card {
  display: flex;
  justify-content: space-around;
  align-items: center;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 12px;
  padding: 24rpx;
  box-shadow: 0 4px 16px rgba(45, 80, 22, 0.05);
  border: 1px solid rgba(139, 191, 159, 0.2);
}

.rank-info,
.carbon-info,
.points-info {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8rpx;
}

.rank-label,
.carbon-label,
.points-label {
  font-size: 20rpx;
  color: #6a9d87;
  font-weight: 500;
}

.rank-value,
.carbon-value,
.points-value {
  font-size: 32rpx;
  font-weight: 700;
}

.gradient-text {
  background: linear-gradient(135deg, #2d5016, #6a9d87);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  font-weight: 700;
}

.rank-change {
  font-size: 18rpx;
  font-weight: 500;
}

.rank-change.up {
  color: #4caf50;
}

.rank-change.down {
  color: #f44336;
}

.rank-change.no-change {
  color: #9e9e9e;
}

/* 排行榜列表 */
.rank-list-section {
  margin-bottom: 30rpx;
  padding: 30rpx;
  border-radius: 16px;
  background: linear-gradient(135deg, #f1f8e9 0%, #ffffff 100%);
  box-shadow: 0 8px 32px rgba(45, 80, 22, 0.08);
  border: 1px solid rgba(139, 191, 159, 0.2);
  opacity: 0;
  transform: translateY(20px);
  transition: all 0.6s ease 0.4s;
  position: relative;
  z-index: 1;
}

.rank-list-section.loaded {
  opacity: 1;
  transform: translateY(0);
}

.rank-list {
  margin-top: 20rpx;
  display: flex;
  flex-direction: column;
  gap: 16rpx;
}

.rank-item {
  display: flex;
  align-items: center;
  padding: 20rpx;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 12px;
  box-shadow: 0 4px 16px rgba(45, 80, 22, 0.05);
  transition: all 0.3s ease;
  border: 1px solid rgba(139, 191, 159, 0.2);
}

.rank-item:hover,
.rank-item.hover {
  transform: translateX(10px);
  box-shadow: 0 6px 20px rgba(45, 80, 22, 0.1);
}

.rank-item.self {
  background: rgba(240, 249, 232, 0.9);
  border-color: #6a9d87;
}

.rank-number {
  width: 48rpx;
  height: 48rpx;
  border-radius: 50%;
  background: #e0e0e0;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24rpx;
  font-weight: 600;
  color: #666;
  margin-right: 16rpx;
}

.rank-item.top-1 .rank-number {
  background: #ffd700;
  color: #fff;
  font-size: 28rpx;
  width: 56rpx;
  height: 56rpx;
}

.rank-item.top-2 .rank-number {
  background: #c0c0c0;
  color: #fff;
  font-size: 28rpx;
  width: 56rpx;
  height: 56rpx;
}

.rank-item.top-3 .rank-number {
  background: #cd7f32;
  color: #fff;
  font-size: 28rpx;
  width: 56rpx;
  height: 56rpx;
}

.user-info {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 16rpx;
}

.avatar {
  width: 64rpx;
  height: 64rpx;
  border-radius: 50%;
  background: #f0f0f0;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32rpx;
  border: 2rpx solid #e0e0e0;
}

.rank-item.top-1 .avatar {
  border: 4rpx solid #ffd700;
}

.rank-item.top-2 .avatar {
  border: 4rpx solid #c0c0c0;
}

.rank-item.top-3 .avatar {
  border: 4rpx solid #cd7f32;
}

.username {
  font-size: 24rpx;
  font-weight: 500;
  color: #2d5016;
}

.rank-stats {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 4rpx;
  margin-right: 16rpx;
}

.carbon-amount {
  font-size: 22rpx;
  font-weight: 600;
  color: #6a9d87;
}

.points {
  font-size: 18rpx;
  color: #a5d6a7;
}

.like-btn {
  width: 80rpx;
  height: 60rpx;
  background: rgba(139, 191, 159, 0.1);
  border: 2rpx solid rgba(139, 191, 159, 0.3);
  border-radius: 8rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  font-size: 16rpx;
  color: #6a9d87;
  transition: all 0.3s ease;
}

.like-btn:hover {
  background: rgba(139, 191, 159, 0.2);
  border-color: #6a9d87;
  transform: scale(1.05);
}

.like-btn[disabled] {
  background: rgba(244, 67, 54, 0.1);
  border-color: rgba(244, 67, 54, 0.3);
  color: #f44336;
}

.like-icon {
  font-size: 24rpx;
  margin-bottom: 4rpx;
}

.like-count {
  font-size: 16rpx;
}

/* 加载更多 */
.load-more {
  text-align: center;
  padding: 20rpx;
  margin-top: 20rpx;
}

.load-more-text {
  font-size: 20rpx;
  color: #6a9d87;
  font-weight: 500;
}

.loading {
  text-align: center;
  padding: 20rpx;
  margin-top: 20rpx;
}

.loading-text {
  font-size: 20rpx;
  color: #6a9d87;
  font-weight: 500;
}

/* 响应式调整 */
@media (max-width: 375px) {
  .container {
    padding: 16rpx;
  }
  
  .my-rank-section,
  .rank-list-section {
    padding: 24rpx;
  }
  
  .header-title {
    font-size: 32rpx;
  }
  
  .section-title {
    font-size: 26rpx;
  }
  
  .my-rank-card {
    flex-direction: column;
    gap: 20rpx;
  }
  
  .rank-item {
    padding: 16rpx;
  }
  
  .avatar {
    width: 56rpx;
    height: 56rpx;
    font-size: 28rpx;
  }
  
  .username {
    font-size: 22rpx;
  }
  
  .carbon-amount {
    font-size: 20rpx;
  }
  
  .points {
    font-size: 16rpx;
  }
}
</style>
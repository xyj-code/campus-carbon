<template>
  <view class="container">
    <!-- 顶部导航栏 -->
    <view class="nav-bar">
      <text class="nav-back" @click="goBack">←</text>
      <text class="nav-title">项目详情</text>
    </view>

    <!-- 项目详情内容 -->
    <view class="project-section" v-if="projectDetail">
      <!-- 项目基本信息 -->
      <view class="project-header">
        <text class="project-name">{{ projectDetail.name }}</text>
        <text class="project-location">{{ projectDetail.location }}</text>
      </view>

      <!-- 项目介绍 -->
      <view class="project-info">
        <text class="info-title">项目介绍</text>
        <text class="info-content">{{ projectDetail.description }}</text>
      </view>

      <!-- 项目进度 -->
      <view class="project-progress">
        <text class="progress-title">项目进度</text>
        <view class="progress-bar">
          <view 
            class="progress-fill" 
            :style="{ width: projectDetail.progress + '%' }"
          ></view>
        </view>
        <text class="progress-value">{{ projectDetail.progress }}%</text>
      </view>

      <!-- 项目实拍 -->
      <view class="project-images" v-if="projectDetail.images && projectDetail.images.length > 0">
        <text class="images-title">项目实拍</text>
        <view class="images-grid">
          <image 
            v-for="(img, index) in projectDetail.images" 
            :key="index"
            :src="img"
            class="image-item"
            mode="aspectFill"
            @click="previewImage(img, projectDetail.images)"
          ></image>
        </view>
      </view>

      <!-- 解锁条件 -->
      <view class="unlock-condition">
        <text class="condition-title">解锁条件</text>
        <text class="condition-content">累计减碳 {{ getRequiredCarbon(projectDetail.id) }} kg</text>
      </view>
    </view>
    <view v-else class="loading-state">
      <text class="loading-text">加载中...</text>
    </view>
  </view>
</template>

<script>
import { getProjectDetail } from '../../utils/carbonProject.js';

export default {
  data() {
    return {
      projectId: 1,
      projectDetail: null
    };
  },
  onLoad(options) {
    if (options.id) {
      this.projectId = options.id;
    }
    this.loadData();
  },
  methods: {
    async loadData() {
      uni.showLoading({ title: '加载中...' });
      try {
        const res = await getProjectDetail(this.projectId);
        this.projectDetail = res.data || {};
      } catch (error) {
        console.error('加载项目详情失败:', error);
        uni.showToast({ title: '加载失败', icon: 'none' });
      } finally {
        uni.hideLoading();
      }
    },
    getRequiredCarbon(projectId) {
      // 模拟不同项目的解锁碳量
      const carbonMap = {
        1: 100, // 梭梭树
        2: 500, // 江豚
        3: 1000 // 图书室
      };
      return carbonMap[projectId] || 0;
    },
    previewImage(current, urls) {
      uni.previewImage({
        current: current,
        urls: urls
      });
    },
    goBack() {
      uni.navigateBack();
    }
  }
};
</script>

<style scoped>
.container {
  min-height: 100vh;
  background: #f8fff8;
}

.nav-bar {
  background: linear-gradient(160deg, #1b5e20, #4caf50);
  padding: 48rpx 30rpx 30rpx;
  display: flex;
  align-items: center;
  position: relative;
}

.nav-back {
  font-size: 40rpx;
  color: #fff;
  position: absolute;
  left: 30rpx;
  top: 48rpx;
}

.nav-title {
  font-size: 36rpx;
  font-weight: bold;
  color: #fff;
  flex: 1;
  text-align: center;
}

.project-section {
  padding: 30rpx;
}

.project-header {
  text-align: center;
  margin-bottom: 40rpx;
}

.project-name {
  display: block;
  font-size: 36rpx;
  font-weight: bold;
  color: #2e7d32;
  margin-bottom: 12rpx;
}

.project-location {
  display: block;
  font-size: 24rpx;
  color: #666;
}

.project-info {
  background: #fff;
  border-radius: 16rpx;
  padding: 24rpx;
  margin-bottom: 24rpx;
  box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.06);
}

.info-title {
  display: block;
  font-size: 28rpx;
  font-weight: 600;
  color: #333;
  margin-bottom: 16rpx;
}

.info-content {
  display: block;
  font-size: 24rpx;
  color: #666;
  line-height: 1.6;
}

.project-progress {
  background: #fff;
  border-radius: 16rpx;
  padding: 24rpx;
  margin-bottom: 24rpx;
  box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.06);
}

.progress-title {
  display: block;
  font-size: 28rpx;
  font-weight: 600;
  color: #333;
  margin-bottom: 16rpx;
}

.progress-bar {
  width: 100%;
  height: 12rpx;
  background: #e8f5e9;
  border-radius: 6rpx;
  overflow: hidden;
  margin-bottom: 8rpx;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, #4caf50, #2e7d32);
  border-radius: 6rpx;
  transition: width 0.5s ease;
}

.progress-value {
  display: block;
  font-size: 22rpx;
  color: #4caf50;
  text-align: right;
  font-weight: 600;
}

.project-images {
  background: #fff;
  border-radius: 16rpx;
  padding: 24rpx;
  margin-bottom: 24rpx;
  box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.06);
}

.images-title {
  display: block;
  font-size: 28rpx;
  font-weight: 600;
  color: #333;
  margin-bottom: 16rpx;
}

.images-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12rpx;
}

.image-item {
  width: 100%;
  height: 200rpx;
  border-radius: 12rpx;
  transition: transform 0.3s ease;
}

.image-item:hover {
  transform: scale(1.02);
}

.unlock-condition {
  background: #fff;
  border-radius: 16rpx;
  padding: 24rpx;
  margin-bottom: 100rpx;
  box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.06);
}

.condition-title {
  display: block;
  font-size: 28rpx;
  font-weight: 600;
  color: #333;
  margin-bottom: 16rpx;
}

.condition-content {
  display: block;
  font-size: 24rpx;
  color: #666;
  line-height: 1.6;
}

.loading-state {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 600rpx;
}

.loading-text {
  font-size: 28rpx;
  color: #999;
}
</style>
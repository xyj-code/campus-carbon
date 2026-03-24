<template>
  <view class="container">
    <!-- 顶部导航栏 -->
    <view class="nav-bar">
      <text class="nav-back" @click="goBack">←</text>
      <text class="nav-title">电子认养证书</text>
    </view>

    <!-- 证书生成区域 -->
    <view class="certificate-section">
      <view class="certificate-container" v-if="certificateData">
        <!-- 证书背景 -->
        <view class="certificate-bg">
          <!-- 证书内容 -->
          <view class="certificate-content">
            <text class="certificate-title">生态认养证书</text>
            <text class="certificate-subtitle">ECOLOGICAL ADOPTION CERTIFICATE</text>
            
            <view class="certificate-body">
              <text class="certificate-text">兹证明</text>
              <text class="certificate-username">{{ certificateData.userName }}</text>
              <text class="certificate-text">通过累计减碳</text>
              <text class="certificate-carbon">{{ totalCarbon }} kg</text>
              <text class="certificate-text">成功解锁</text>
              <text class="certificate-project">{{ certificateData.projectName }}</text>
              <text class="certificate-text">项目</text>
            </view>
            
            <view class="certificate-info">
              <text class="info-item">唯一编号：{{ certificateData.uniqueCode }}</text>
              <text class="info-item">解锁时间：{{ certificateData.unlockTime }}</text>
            </view>
            
            <!-- 电子公章 -->
            <view class="certificate-seal">
              <text class="seal-text">生态保护认证</text>
            </view>
          </view>
        </view>
      </view>
      <view v-else class="loading-state">
        <text class="loading-text">生成证书中...</text>
      </view>
    </view>

    <!-- 操作按钮 -->
    <view class="action-buttons">
      <button class="btn-primary" @click="saveToAlbum">保存到相册</button>
      <button class="btn-secondary" @click="shareToFriends">分享给好友</button>
    </view>
  </view>
</template>

<script>
import { getCertificate } from '../../utils/carbonProject.js';

export default {
  data() {
    return {
      username: '',
      userProjectId: null,
      certificateData: null,
      totalCarbon: 0
    };
  },
  onLoad(options) {
    this.username = options.username || uni.getStorageSync('username') || '';
    if (options.userProjectId) {
      this.userProjectId = parseInt(options.userProjectId);
    }
    this.loadData();
  },
  methods: {
    async loadData() {
      if (!this.username || !this.userProjectId) {
        uni.showToast({ title: '参数错误', icon: 'none' });
        return;
      }
      uni.showLoading({ title: '生成证书中...' });
      try {
        const certRes = await getCertificate(this.username, this.userProjectId);
        this.certificateData = certRes.data || {};
        // certificateUrl 字段后端存储的是 totalCarbon 值
        this.totalCarbon = parseFloat(this.certificateData.certificateUrl || 0).toFixed(2);
      } catch (error) {
        console.error('生成证书失败:', error);
        uni.showToast({ title: '生成失败', icon: 'none' });
      } finally {
        uni.hideLoading();
      }
    },
    saveToAlbum() {
      // 模拟保存到相册
      uni.showToast({ 
        title: '证书已保存到相册', 
        icon: 'success',
        duration: 2000
      });
    },
    shareToFriends() {
      // 模拟分享给好友
      uni.showToast({ 
        title: '分享成功', 
        icon: 'success',
        duration: 2000
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

.certificate-section {
  padding: 40rpx 30rpx;
}

.certificate-container {
  display: flex;
  justify-content: center;
}

.certificate-bg {
  background: #fff;
  border: 8rpx solid #2e7d32;
  border-radius: 24rpx;
  padding: 60rpx 40rpx;
  width: 90%;
  box-shadow: 0 8rpx 32rpx rgba(0,0,0,0.1);
  position: relative;
  background-image: url('https://example.com/cert-template.png');
  background-size: cover;
  background-position: center;
}

.certificate-content {
  text-align: center;
  position: relative;
  z-index: 1;
}

.certificate-title {
  display: block;
  font-size: 48rpx;
  font-weight: bold;
  color: #2e7d32;
  margin-bottom: 16rpx;
  text-shadow: 1rpx 1rpx 2rpx rgba(0,0,0,0.1);
}

.certificate-subtitle {
  display: block;
  font-size: 24rpx;
  color: #666;
  margin-bottom: 48rpx;
  letter-spacing: 2rpx;
}

.certificate-body {
  margin-bottom: 48rpx;
}

.certificate-text {
  display: block;
  font-size: 32rpx;
  color: #333;
  margin-bottom: 16rpx;
  line-height: 1.6;
}

.certificate-username {
  display: block;
  font-size: 40rpx;
  font-weight: bold;
  color: #2e7d32;
  margin-bottom: 16rpx;
  text-shadow: 1rpx 1rpx 2rpx rgba(0,0,0,0.1);
}

.certificate-carbon {
  display: block;
  font-size: 36rpx;
  font-weight: bold;
  color: #ff9800;
  margin-bottom: 16rpx;
}

.certificate-project {
  display: block;
  font-size: 36rpx;
  font-weight: bold;
  color: #1976d2;
  margin-bottom: 16rpx;
  text-shadow: 1rpx 1rpx 2rpx rgba(0,0,0,0.1);
}

.certificate-info {
  margin-bottom: 48rpx;
  text-align: left;
  background: rgba(248, 255, 248, 0.8);
  padding: 24rpx;
  border-radius: 12rpx;
}

.info-item {
  display: block;
  font-size: 24rpx;
  color: #666;
  margin-bottom: 8rpx;
}

.certificate-seal {
  position: absolute;
  bottom: 60rpx;
  right: 60rpx;
  width: 160rpx;
  height: 160rpx;
  background: #f44336;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0.8;
  transform: rotate(15deg);
}

.seal-text {
  font-size: 24rpx;
  color: #fff;
  font-weight: bold;
  text-align: center;
  line-height: 1.4;
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

.action-buttons {
  padding: 40rpx 30rpx;
  display: flex;
  gap: 20rpx;
}

.btn-primary {
  flex: 1;
  background: linear-gradient(90deg, #4caf50, #2e7d32);
  color: #fff;
  border: none;
  border-radius: 16rpx;
  font-size: 28rpx;
  padding: 24rpx 0;
  font-weight: 600;
}

.btn-secondary {
  flex: 1;
  background: #fff;
  color: #2e7d32;
  border: 2rpx solid #2e7d32;
  border-radius: 16rpx;
  font-size: 28rpx;
  padding: 24rpx 0;
  font-weight: 600;
}
</style>
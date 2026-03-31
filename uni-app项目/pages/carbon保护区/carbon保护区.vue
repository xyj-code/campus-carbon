<template>
  <view class="page">
    <!-- 动态渐变背景 -->
    <view class="gradient-bg"></view>

    <!-- 动态粒子系统 -->
    <view class="particle-system">
      <view v-for="(item, index) in 40" :key="index" class="particle" :style="particleStyles[index]"></view>
    </view>

    <scroll-view scroll-y="true" class="scroller" :show-scrollbar="false">
      <!-- 顶部减碳量和进度条 -->
      <view class="carbon-card">
        <view class="carbon-header">
          <text class="carbon-label">当前累计减碳量</text>
          <text class="carbon-value">{{ totalCarbon }} kg</text>
        </view>

        <!-- 待解锁进度条 -->
        <view class="progress-section">
          <view class="progress-header">
            <text class="progress-label">下一解锁目标：{{ nextGoal.name }}</text>
            <text class="progress-percent">{{ progressPercent }}%</text>
          </view>
          <view class="progress-bar">
            <view class="progress-fill" :style="{ width: progressPercent + '%' }"></view>
          </view>
          <view class="progress-info">
            <text>还需 {{ nextGoal.remaining }} kg 解锁 {{ nextGoal.name }}</text>
          </view>
        </view>
      </view>

      <!-- 核心可视化地图区域 -->
      <view class="map-card">
        <view class="map-container">
          <map
              class="china-map"
              :longitude="104.20"
              :latitude="35.86"
              :scale="4"
              :markers="mapMarkers"
              :enable-zoom="true"
              :enable-scroll="true"
              :show-scale="true"
              @markertap="onMarkerTap"
          ></map>
        </view>
      </view>

      <!-- 我的解锁记录 -->
      <view class="records-section">
        <text class="section-title">我的解锁记录</text>
        <view class="records-list" v-if="userProjects.length > 0">
          <view
              v-for="project in userProjects"
              :key="project.id"
              class="record-item"
              @click="goToCertificate(project.id)"
          >
            <view class="record-left">
              <view class="record-icon" :class="project.projectType">
                <text v-if="project.projectType === 'tree'">🌳</text>
                <text v-else-if="project.projectType === 'porpoise'">🐬</text>
                <text v-else-if="project.projectType === 'library'">📚</text>
                <text v-else>🌱</text>
              </view>
              <view class="record-info">
                <text class="record-name">{{ project.projectName }}</text>
                <text class="record-time">{{ project.unlockTime }}</text>
              </view>
            </view>
            <view class="record-right">
              <text class="record-code">{{ project.uniqueCode }}</text>
              <text class="record-arrow">›</text>
            </view>
          </view>
        </view>
        <view v-else class="empty-state">
          <text class="empty-text">暂无解锁记录</text>
          <text class="empty-hint">继续低碳生活，解锁更多生态保护项目</text>
        </view>
      </view>

      <!-- 项目详情弹窗 -->
      <view v-if="showDetail" class="modal-overlay" @click.self="showDetail = false">
        <view class="detail-modal">
          <view class="modal-header">
            <text class="modal-title">{{ projectDetail.name }}</text>
            <text class="modal-close" @click="showDetail = false">×</text>
          </view>
          <view class="modal-body">
            <text class="detail-location">📍 {{ projectDetail.location }}</text>
            <text class="detail-description">{{ projectDetail.description }}</text>
            <view class="detail-progress">
              <text class="progress-label">项目进度</text>
              <view class="progress-bar">
                <view class="progress-fill" :style="{ width: (projectDetail.progress || 0) + '%' }"></view>
              </view>
              <text class="progress-value">{{ projectDetail.progress || 0 }}%</text>
            </view>
            <view v-if="projectDetail.requiredCarbon" class="detail-require">
              <text class="require-text">解锁需要 {{ projectDetail.requiredCarbon }} kg 减碳量</text>
            </view>
          </view>
          <view class="modal-footer">
            <button class="btn-primary" @click="showDetail = false">关闭</button>
          </view>
        </view>
      </view>
    </scroll-view>
  </view>
</template>

<script>
import { getUserTotalCarbon, getUserProjectList, checkAndUnlock, getProjectDetail } from '../../utils/carbonProject.js';

const PROJECT_COORDS = {
  1: { latitude: 38.85, longitude: 105.70 },
  2: { latitude: 30.60, longitude: 111.30 },
  3: { latitude: 25.04, longitude: 102.72 }
};

export default {
  data() {
    return {
      username: '',
      totalCarbon: 0,
      userProjects: [],
      allProjects: [],
      projectDetail: {},
      showDetail: false,
      nextGoal: { name: '', remaining: 0 },
      progressPercent: 0,
      particleStyles: []
    };
  },
  computed: {
    mapMarkers() {
      return this.allProjects.map(p => {
        const coord = PROJECT_COORDS[p.id] || { latitude: 35.86, longitude: 104.20 };
        const unlocked = this.isUnlockedById(p.id);
        return {
          id: p.id,
          latitude: coord.latitude,
          longitude: coord.longitude,
          width: 40,
          height: 40,
          callout: {
            content: (unlocked ? '✅ ' : '🔒 ') + p.name,
            color: '#ffffff',
            fontSize: 12,
            borderRadius: 8,
            bgColor: unlocked ? '#2e7d32' : '#757575',
            padding: 6,
            display: 'ALWAYS'
          }
        };
      });
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
      this.loadData();
    }
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
    async loadData() {
      uni.showLoading({ title: '加载中...' });
      try {
        const [unlockRes, carbonRes] = await Promise.all([
          checkAndUnlock(this.username),
          getUserTotalCarbon(this.username)
        ]);
        this.userProjects = unlockRes.data || [];

        const profile = carbonRes.data || {};
        this.totalCarbon = typeof profile === 'object' ? (profile.totalCarbon || 0) : (profile || 0);

        this.allProjects = [
          { id: 1, name: '阿拉善梭梭树认养', requiredCarbon: 100 },
          { id: 2, name: '长江江豚栖息地保护', requiredCarbon: 500 },
          { id: 3, name: '乡村校园绿色图书室', requiredCarbon: 1000 }
        ];

        this.calculateNextGoal();
      } catch (error) {
        console.error('加载数据失败:', error);
        uni.showToast({ title: '加载失败', icon: 'none' });
      } finally {
        uni.hideLoading();
      }
    },
    calculateNextGoal() {
      const goals = this.allProjects.map(p => ({
        name: p.name,
        carbon: p.requiredCarbon
      })).sort((a, b) => a.carbon - b.carbon);

      for (let i = 0; i < goals.length; i++) {
        const goal = goals[i];
        if (this.totalCarbon < goal.carbon) {
          this.nextGoal.name = goal.name;
          this.nextGoal.remaining = (goal.carbon - this.totalCarbon).toFixed(2);
          const prevCarbon = i > 0 ? goals[i - 1].carbon : 0;
          const range = goal.carbon - prevCarbon;
          const progress = this.totalCarbon - prevCarbon;
          this.progressPercent = Math.min(100, Math.round((progress / range) * 100));
          return;
        }
      }
      this.nextGoal.name = '所有项目已解锁';
      this.nextGoal.remaining = 0;
      this.progressPercent = 100;
    },
    isUnlockedById(projectId) {
      return this.userProjects.some(p => p.projectId === projectId);
    },
    async onMarkerTap(e) {
      const projectId = e.markerId;
      await this.showProjectDetail(projectId);
    },
    async showProjectDetail(projectId) {
      uni.showLoading({ title: '加载中...' });
      try {
        const res = await getProjectDetail(projectId);
        this.projectDetail = res.data || {};
        this.showDetail = true;
      } catch (error) {
        console.error('加载项目详情失败:', error);
        uni.showToast({ title: '加载失败', icon: 'none' });
      } finally {
        uni.hideLoading();
      }
    },
    goToCertificate(userProjectId) {
      uni.navigateTo({
        url: `/pages/certificate/certificate?userProjectId=${userProjectId}&username=${this.username}`
      });
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
  background: rgba(80, 200, 140, 0.5);
  border-radius: 50%;
  animation: floatUp linear infinite;
}

@keyframes floatUp {
  0% {
    transform: translateY(100vh);
    opacity: 0;
  }
  10% {
    opacity: 0.6;
  }
  90% {
    opacity: 0.3;
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

/* 减碳卡片 */
.carbon-card {
  background: rgba(255, 255, 255, 0.92);
  backdrop-filter: blur(20rpx);
  border-radius: 32rpx;
  margin: 100rpx 0 28rpx;
  padding: 32rpx;
  box-shadow: 0 8rpx 32rpx rgba(0, 0, 0, 0.08);
  border: 1rpx solid rgba(255, 255, 255, 0.6);
}

.carbon-header {
  text-align: center;
  margin-bottom: 32rpx;
}

.carbon-label {
  display: block;
  font-size: 28rpx;
  color: #5B8C6E;
  margin-bottom: 12rpx;
}

.carbon-value {
  font-size: 64rpx;
  font-weight: bold;
  background: linear-gradient(135deg, #3D9B6D, #2C5A3A);
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
}

/* 进度条 */
.progress-section {
  margin-top: 20rpx;
}

.progress-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12rpx;
}

.progress-label {
  font-size: 26rpx;
  color: #2C5A3A;
  font-weight: 500;
}

.progress-percent {
  font-size: 24rpx;
  color: #3D9B6D;
  font-weight: bold;
}

.progress-bar {
  width: 100%;
  height: 12rpx;
  background: #E8F5E9;
  border-radius: 6rpx;
  overflow: hidden;
  margin-bottom: 8rpx;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, #6FB88A, #3D9B6D);
  border-radius: 6rpx;
  transition: width 0.5s ease;
}

.progress-info {
  font-size: 22rpx;
  color: #9CC0AC;
  text-align: center;
}

/* 地图卡片 */
.map-card {
  background: rgba(255, 255, 255, 0.92);
  backdrop-filter: blur(20rpx);
  border-radius: 32rpx;
  margin-bottom: 28rpx;
  padding: 20rpx;
  box-shadow: 0 8rpx 32rpx rgba(0, 0, 0, 0.08);
  border: 1rpx solid rgba(255, 255, 255, 0.6);
}

.map-container {
  border-radius: 24rpx;
  overflow: hidden;
}

.china-map {
  width: 100%;
  height: 520rpx;
}

/* 记录区域 */
.records-section {
  margin-bottom: 60rpx;
}

.section-title {
  font-size: 32rpx;
  font-weight: bold;
  background: linear-gradient(135deg, #3D9B6D, #2C5A3A);
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
  margin-bottom: 20rpx;
  display: block;
}

.records-list {
  background: rgba(255, 255, 255, 0.92);
  backdrop-filter: blur(20rpx);
  border-radius: 32rpx;
  overflow: hidden;
  box-shadow: 0 8rpx 32rpx rgba(0, 0, 0, 0.08);
  border: 1rpx solid rgba(255, 255, 255, 0.6);
}

.record-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24rpx 32rpx;
  border-bottom: 1rpx solid rgba(100, 200, 150, 0.2);
  transition: all 0.3s ease;
}

.record-item:active {
  background: rgba(100, 200, 150, 0.1);
  transform: translateX(8rpx);
}

.record-item:last-child {
  border-bottom: none;
}

.record-left {
  display: flex;
  align-items: center;
  gap: 16rpx;
}

.record-icon {
  width: 64rpx;
  height: 64rpx;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32rpx;
  background: rgba(100, 200, 150, 0.2);
}

.record-icon.tree { background: rgba(61, 155, 109, 0.2); }
.record-icon.porpoise { background: rgba(33, 150, 243, 0.2); }
.record-icon.library { background: rgba(255, 152, 0, 0.2); }

.record-info {
  flex: 1;
}

.record-name {
  display: block;
  font-size: 28rpx;
  font-weight: 600;
  color: #2C5A3A;
  margin-bottom: 4rpx;
}

.record-time {
  display: block;
  font-size: 22rpx;
  color: #9CC0AC;
}

.record-right {
  display: flex;
  align-items: center;
  gap: 12rpx;
}

.record-code {
  font-size: 20rpx;
  color: #C8E6C9;
  font-family: monospace;
}

.record-arrow {
  font-size: 32rpx;
  color: #C8E6C9;
  transition: transform 0.3s ease;
}

.record-item:active .record-arrow {
  transform: translateX(6rpx);
  color: #3D9B6D;
}

/* 空状态 */
.empty-state {
  background: rgba(255, 255, 255, 0.92);
  backdrop-filter: blur(20rpx);
  border-radius: 32rpx;
  padding: 64rpx 32rpx;
  text-align: center;
  box-shadow: 0 8rpx 32rpx rgba(0, 0, 0, 0.08);
  border: 1rpx solid rgba(255, 255, 255, 0.6);
}

.empty-text {
  display: block;
  font-size: 28rpx;
  color: #5B8C6E;
  margin-bottom: 12rpx;
}

.empty-hint {
  font-size: 24rpx;
  color: #9CC0AC;
  line-height: 1.5;
}

/* 弹窗样式 */
.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.5);
  backdrop-filter: blur(8rpx);
  z-index: 999;
  display: flex;
  align-items: center;
  justify-content: center;
}

.detail-modal {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(24rpx);
  border-radius: 32rpx;
  width: 90%;
  max-height: 80vh;
  overflow: hidden;
  animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
  from { opacity: 0; transform: scale(0.95); }
  to { opacity: 1; transform: scale(1); }
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 28rpx 32rpx;
  border-bottom: 1rpx solid rgba(100, 200, 150, 0.2);
}

.modal-title {
  font-size: 32rpx;
  font-weight: bold;
  color: #2C5A3A;
}

.modal-close {
  font-size: 40rpx;
  color: #9CC0AC;
  padding: 8rpx;
}

.modal-body {
  padding: 32rpx;
  max-height: 50vh;
  overflow-y: auto;
}

.detail-location {
  display: block;
  font-size: 24rpx;
  color: #5B8C6E;
  margin-bottom: 16rpx;
}

.detail-description {
  display: block;
  font-size: 26rpx;
  color: #2C5A3A;
  line-height: 1.6;
  margin-bottom: 24rpx;
}

.detail-progress {
  margin-bottom: 24rpx;
}

.detail-progress .progress-label {
  display: block;
  font-size: 24rpx;
  color: #5B8C6E;
  margin-bottom: 8rpx;
}

.detail-progress .progress-bar {
  margin-bottom: 8rpx;
}

.detail-progress .progress-value {
  display: block;
  font-size: 22rpx;
  color: #3D9B6D;
  text-align: right;
}

.detail-require {
  background: rgba(100, 200, 150, 0.15);
  border-radius: 20rpx;
  padding: 16rpx 20rpx;
}

.require-text {
  font-size: 24rpx;
  color: #3D9B6D;
}

.modal-footer {
  padding: 24rpx 32rpx;
  border-top: 1rpx solid rgba(100, 200, 150, 0.2);
  text-align: center;
}

.btn-primary {
  background: linear-gradient(135deg, #6FB88A, #3D9B6D);
  color: #fff;
  border: none;
  border-radius: 40rpx;
  font-size: 28rpx;
  padding: 20rpx 0;
  width: 100%;
  transition: all 0.3s ease;
}

.btn-primary:active {
  transform: scale(0.98);
}
</style>
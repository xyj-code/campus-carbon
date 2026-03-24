<template>
  <view class="container">
    <!-- 顶部导航栏 -->
   

    <!-- 顶部减碳量和进度条 -->
    <view class="carbon-section">
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
          <view
            class="progress-fill"
            :style="{ width: progressPercent + '%' }"
          ></view>
        </view>
        <view class="progress-info">
          <text>还需 {{ nextGoal.remaining }} kg 解锁 {{ nextGoal.name }}</text>
        </view>
      </view>
    </view>

    <!-- 核心可视化地图区域 -->
    <view class="map-section">
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
              <view
                class="progress-fill"
                :style="{ width: (projectDetail.progress || 0) + '%' }"
              ></view>
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
  </view>
</template>

<script>
import { getUserTotalCarbon, getUserProjectList, checkAndUnlock, getProjectDetail } from '../../utils/carbonProject.js';

// 项目坐标配置（与数据库 project.id 对应）
const PROJECT_COORDS = {
  1: { latitude: 38.85, longitude: 105.70 }, // 阿拉善
  2: { latitude: 30.60, longitude: 111.30 }, // 长江
  3: { latitude: 25.04, longitude: 102.72 }  // 云南
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
      progressPercent: 0
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
  onShow() {
    this.username = uni.getStorageSync('username') || '';
    if (this.username) {
      this.loadData();
    }
  },
  methods: {
    async loadData() {
      uni.showLoading({ title: '加载中...' });
      try {
        // 先自动解锁达标项目，同时获取已解锁列表
        const [unlockRes, carbonRes] = await Promise.all([
          checkAndUnlock(this.username),
          getUserTotalCarbon(this.username)
        ]);
        this.userProjects = unlockRes.data || [];

        const profile = carbonRes.data || {};
        this.totalCarbon = typeof profile === 'object'
          ? (profile.totalCarbon || 0)
          : (profile || 0);

        // 获取所有项目用于地图标记
        const allRes = await getUserProjectList(this.username);
        // allProjects 用于地图：从 checkAndUnlock 拿到的是已解锁，我们需要全部项目
        // 此处用一个固定的项目列表做地图展示（与后端数据库保持一致）
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
    isUnlocked(type) {
      return this.userProjects.some(p => p.projectType === type);
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
.container {
  min-height: 100vh;
  background: #f8fff8;
}

.nav-bar {
  background: linear-gradient(160deg, #1b5e20, #4caf50);
  padding: 48rpx 30rpx 30rpx;
  text-align: center;
}

.nav-title {
  font-size: 36rpx;
  font-weight: bold;
  color: #fff;
}

.carbon-section {
  background: #fff;
  margin: 20rpx 30rpx;
  border-radius: 20rpx;
  padding: 32rpx;
  box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.06);
}

.carbon-header {
  text-align: center;
  margin-bottom: 32rpx;
}

.carbon-label {
  display: block;
  font-size: 28rpx;
  color: #666;
  margin-bottom: 12rpx;
}

.carbon-value {
  font-size: 64rpx;
  font-weight: bold;
  color: #2e7d32;
}

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
  color: #333;
  font-weight: 600;
}

.progress-percent {
  font-size: 24rpx;
  color: #4caf50;
  font-weight: bold;
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

.progress-info {
  font-size: 22rpx;
  color: #888;
  text-align: center;
}

.map-section {
  margin: 20rpx 30rpx;
}

.section-title {
  font-size: 30rpx;
  font-weight: bold;
  color: #2e7d32;
  margin-bottom: 16rpx;
  display: block;
}

.map-container {
  border-radius: 20rpx;
  overflow: hidden;
  box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.1);
}

.china-map {
  width: 100%;
  height: 520rpx;
}

.records-section {
  margin: 20rpx 30rpx 100rpx;
}

.records-list {
  background: #fff;
  border-radius: 20rpx;
  overflow: hidden;
  box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.06);
}

.record-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24rpx 32rpx;
  border-bottom: 1rpx solid #f5f5f5;
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
  background: #e8f5e9;
}

.record-info {
  flex: 1;
}

.record-name {
  display: block;
  font-size: 28rpx;
  font-weight: 600;
  color: #333;
  margin-bottom: 4rpx;
}

.record-time {
  display: block;
  font-size: 22rpx;
  color: #888;
}

.record-right {
  display: flex;
  align-items: center;
  gap: 12rpx;
}

.record-code {
  font-size: 20rpx;
  color: #999;
  font-family: monospace;
}

.record-arrow {
  font-size: 32rpx;
  color: #ccc;
}

.empty-state {
  background: #fff;
  border-radius: 20rpx;
  padding: 64rpx 32rpx;
  text-align: center;
  box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.06);
}

.empty-text {
  display: block;
  font-size: 28rpx;
  color: #999;
  margin-bottom: 12rpx;
}

.empty-hint {
  font-size: 24rpx;
  color: #bbb;
  line-height: 1.5;
}

/* 弹窗样式 */
.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0,0,0,0.5);
  z-index: 999;
  display: flex;
  align-items: center;
  justify-content: center;
}

.detail-modal {
  background: #fff;
  border-radius: 24rpx;
  width: 90%;
  max-height: 80vh;
  overflow: hidden;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24rpx 32rpx;
  border-bottom: 1rpx solid #f5f5f5;
}

.modal-title {
  font-size: 32rpx;
  font-weight: bold;
  color: #2e7d32;
}

.modal-close {
  font-size: 40rpx;
  color: #999;
}

.modal-body {
  padding: 32rpx;
  max-height: 50vh;
  overflow-y: auto;
}

.detail-location {
  display: block;
  font-size: 24rpx;
  color: #666;
  margin-bottom: 16rpx;
}

.detail-description {
  display: block;
  font-size: 26rpx;
  color: #333;
  line-height: 1.6;
  margin-bottom: 24rpx;
}

.detail-progress {
  margin-bottom: 24rpx;
}

.detail-progress .progress-label {
  display: block;
  font-size: 24rpx;
  color: #666;
  margin-bottom: 8rpx;
}

.detail-progress .progress-bar {
  margin-bottom: 8rpx;
}

.detail-progress .progress-value {
  display: block;
  font-size: 22rpx;
  color: #4caf50;
  text-align: right;
}

.detail-require {
  background: #e8f5e9;
  border-radius: 12rpx;
  padding: 16rpx 20rpx;
}

.require-text {
  font-size: 24rpx;
  color: #2e7d32;
}

.modal-footer {
  padding: 24rpx 32rpx;
  border-top: 1rpx solid #f5f5f5;
  text-align: center;
}

.btn-primary {
  background: linear-gradient(90deg, #4caf50, #2e7d32);
  color: #fff;
  border: none;
  border-radius: 16rpx;
  font-size: 28rpx;
  padding: 20rpx 0;
  width: 100%;
}
</style>

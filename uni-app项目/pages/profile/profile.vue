<template>
  <view class="container">
    <!-- 顶部绿色 Banner -->
    <view class="header-banner">
      <view class="avatar-wrap">
        <text class="avatar-text">{{ (profileData.name || 'U').charAt(0) }}</text>
      </view>
      <text class="user-name">{{ profileData.name || '-' }}</text>
      <text class="user-id">ID: {{ username }}</text>
    </view>

    <!-- 数据概览 -->
    <view class="overview-card">
      <view class="overview-item">
        <text class="ov-value green">{{ profileData.totalCarbon || 0 }}</text>
        <text class="ov-label">减碳量(kg)</text>
      </view>
      <view class="ov-sep"></view>
      <view class="overview-item">
        <text class="ov-value green">{{ profileData.points || 0 }}</text>
        <text class="ov-label">当前积分</text>
      </view>
      <view class="ov-sep"></view>
      <view class="overview-item">
        <text class="ov-value green">{{ profileData.consecutiveDays || 0 }}</text>
        <text class="ov-label">连续打卡</text>
      </view>
    </view>

    <!-- 功能列表 -->
    <view class="func-list">
      <view class="func-item" @click="goCarbonReserve">
        <view class="func-left">
          <view class="func-icon-wrap tree"><text class="func-icon">🌳</text></view>
          <text class="func-text">我的低碳保护区</text>
        </view>
        <text class="func-arrow">›</text>
      </view>
      <view class="func-item" @click="goPointsDetail">
        <view class="func-left">
          <view class="func-icon-wrap star"><text class="func-icon">⭐</text></view>
          <text class="func-text">积分明细</text>
        </view>
        <text class="func-arrow">›</text>
      </view>
      <view class="func-item" @click="showCarbonLedger = true">
        <view class="func-left">
          <view class="func-icon-wrap leaf"><text class="func-icon">📊</text></view>
          <text class="func-text">个人碳账本</text>
        </view>
        <text class="func-arrow">›</text>
      </view>
      <view class="func-item" @click="showSettings = true">
        <view class="func-left">
          <view class="func-icon-wrap gear"><text class="func-icon">⚙️</text></view>
          <text class="func-text">设置</text>
        </view>
        <text class="func-arrow">›</text>
      </view>
      <view class="func-item last" @click="showAbout = true">
        <view class="func-left">
          <view class="func-icon-wrap info"><text class="func-icon">ℹ️</text></view>
          <text class="func-text">关于我们</text>
        </view>
        <text class="func-arrow">›</text>
      </view>
    </view>

    <!-- 退出登录 -->
    <button class="logout-btn" @click="logout">退出登录</button>

    <!-- 底部导航 -->
    <bottom-nav :currentIndex="2" />

    <!-- ======= 碳账本弹窗 ======= -->
    <view v-if="showCarbonLedger" class="modal-overlay" @click.self="showCarbonLedger = false">
      <view class="modal-sheet">
        <view class="modal-header">
          <text class="modal-title">🌿 个人碳账本</text>
          <text class="modal-close" @click="showCarbonLedger = false">✕</text>
        </view>
        <view class="ledger-total">
          <text class="ledger-kg">{{ profileData.totalCarbon || 0 }}</text>
          <text class="ledger-unit">kg CO₂</text>
          <text class="ledger-sub">累计减碳量</text>
        </view>
        <view class="equiv-row">
          <view class="equiv-item">
            <text class="equiv-icon">🌳</text>
            <text class="equiv-val">{{ treesCount }}</text>
            <text class="equiv-desc">棵树一天的吸碳量</text>
          </view>
          <view class="equiv-item">
            <text class="equiv-icon">🚗</text>
            <text class="equiv-val">{{ kmCount }}</text>
            <text class="equiv-desc">公里少开车</text>
          </view>
        </view>
        <view class="ledger-section-title">近7天步数</view>
        <view class="week-bars">
          <view v-for="(bar, i) in weekBars" :key="i" class="bar-col">
            <view class="bar-wrap">
              <view class="bar-fill" :style="{ height: bar.height + 'rpx' }"></view>
            </view>
            <text class="bar-label">{{ bar.day }}</text>
            <text class="bar-steps">{{ bar.steps > 0 ? (bar.steps >= 10000 ? (bar.steps/1000).toFixed(0)+'k' : bar.steps) : '-' }}</text>
          </view>
        </view>
      </view>
    </view>

    <!-- ======= 设置弹窗 ======= -->
    <view v-if="showSettings" class="modal-overlay" @click.self="showSettings = false">
      <view class="modal-sheet">
        <view class="modal-header">
          <text class="modal-title">⚙️ 设置</text>
          <text class="modal-close" @click="showSettings = false">✕</text>
        </view>

        <view class="setting-group">
          <text class="setting-group-title">账号</text>
          <view class="setting-row">
            <text class="setting-label">用户名</text>
            <text class="setting-value">{{ username }}</text>
          </view>
          <view class="setting-row">
            <text class="setting-label">昵称</text>
            <input class="setting-input" v-model="editName" placeholder="输入昵称" maxlength="20" />
          </view>
          <button class="save-name-btn" @click="saveName">保存昵称</button>
        </view>

        <view class="setting-group">
          <text class="setting-group-title">偏好</text>
          <view class="setting-row">
            <text class="setting-label">消息通知</text>
            <switch :checked="notifyOn" color="#4caf50" @change="notifyOn = $event.detail.value" />
          </view>
          <view class="setting-row">
            <text class="setting-label">隐私保护</text>
            <switch :checked="privacyOn" color="#4caf50" @change="privacyOn = $event.detail.value" />
          </view>
        </view>

        <view class="setting-group">
          <button class="clear-cache-btn" @click="clearCache">清除缓存</button>
        </view>
      </view>
    </view>

    <!-- ======= 关于我们弹窗 ======= -->
    <view v-if="showAbout" class="modal-overlay" @click.self="showAbout = false">
      <view class="modal-sheet">
        <view class="modal-header">
          <text class="modal-title">关于我们</text>
          <text class="modal-close" @click="showAbout = false">✕</text>
        </view>
        <view class="about-logo">🌍</view>
        <text class="about-appname">低碳生活</text>
        <text class="about-version">版本 v1.0.0</text>
        <text class="about-desc">致力于帮助每个人养成低碳生活习惯，记录绿色足迹，共同守护地球家园。</text>
        <view class="about-divider"></view>
        <view class="about-row" @click="copyService">
          <text class="about-label">客服邮箱</text>
          <text class="about-val">service@lowcarbon.app</text>
        </view>
        <view class="about-row">
          <text class="about-label">用户协议</text>
          <text class="about-link">查看 ›</text>
        </view>
        <view class="about-row">
          <text class="about-label">隐私政策</text>
          <text class="about-link">查看 ›</text>
        </view>
      </view>
    </view>
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
      showCarbonLedger: false,
      showSettings: false,
      showAbout: false,
      editName: '',
      notifyOn: true,
      privacyOn: false
    };
  },
  computed: {
    treesCount() {
      // 1棵树每天吸收约0.02kg CO₂
      return Math.round((this.profileData.totalCarbon || 0) / 0.02);
    },
    kmCount() {
      // 汽车每公里约0.21kg CO₂
      return Math.round((this.profileData.totalCarbon || 0) / 0.21);
    }
  },
  onShow() {
    this.username = uni.getStorageSync('username');
    if (this.username) {
      this.loadProfile();
      this.loadWeekBars();
    }
  },
  methods: {
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
    goCarbonReserve() {
      uni.navigateTo({
        url: '/pages/carbon保护区/carbon保护区'
      });
    },
    goPointsDetail() {
      uni.navigateTo({
        url: '/pages/pointsDetail/pointsDetail'
      });
    },
    async saveName() {
      uni.showToast({ title: '昵称已保存', icon: 'success' });
      this.profileData.name = this.editName;
      this.showSettings = false;
    },
    clearCache() {
      uni.clearStorageSync();
      uni.showToast({ title: '缓存已清除', icon: 'success' });
    },
    copyService() {
      uni.setClipboardData({ data: 'service@lowcarbon.app' });
    },
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
.container {
  min-height: 100vh;
  background: #f0f7f0;
  padding-bottom: 80rpx;
}

/* Header Banner */
.header-banner {
  background: linear-gradient(160deg, #2e7d32, #66bb6a);
  padding: 60rpx 40rpx 48rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
}
.avatar-wrap {
  width: 120rpx;
  height: 120rpx;
  border-radius: 50%;
  background: rgba(255,255,255,0.25);
  border: 4rpx solid rgba(255,255,255,0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 20rpx;
}
.avatar-text {
  font-size: 56rpx;
  color: #fff;
  font-weight: bold;
}
.user-name {
  font-size: 36rpx;
  font-weight: bold;
  color: #fff;
  margin-bottom: 8rpx;
}
.user-id {
  font-size: 24rpx;
  color: rgba(255,255,255,0.75);
}

/* Overview */
.overview-card {
  background: #fff;
  margin: -32rpx 30rpx 24rpx;
  border-radius: 20rpx;
  padding: 32rpx 20rpx;
  display: flex;
  box-shadow: 0 4rpx 20rpx rgba(46,125,50,0.1);
}
.overview-item {
  flex: 1;
  text-align: center;
}
.ov-sep {
  width: 1rpx;
  background: #e8f5e9;
  margin: 8rpx 0;
}
.ov-value {
  display: block;
  font-size: 40rpx;
  font-weight: bold;
  margin-bottom: 8rpx;
}
.ov-value.green { color: #2e7d32; }
.ov-label {
  font-size: 22rpx;
  color: #888;
}

/* Function List */
.func-list {
  background: #fff;
  border-radius: 20rpx;
  margin: 0 30rpx 24rpx;
  box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.04);
  overflow: hidden;
}
.func-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 32rpx 32rpx;
  border-bottom: 1rpx solid #f0f0f0;
}
.func-item.last { border-bottom: none; }
.func-left {
  display: flex;
  align-items: center;
  gap: 20rpx;
}
.func-icon-wrap {
  width: 64rpx;
  height: 64rpx;
  border-radius: 16rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}
.func-icon-wrap.tree { background: #e8f5e9; }
.func-icon-wrap.star { background: #fff8e1; }
.func-icon-wrap.leaf { background: #e8f5e9; }
.func-icon-wrap.gear { background: #e3f2fd; }
.func-icon-wrap.info { background: #f3e5f5; }
.func-icon { font-size: 32rpx; }
.func-text {
  font-size: 30rpx;
  color: #333;
  font-weight: 500;
}
.func-arrow {
  font-size: 36rpx;
  color: #bbb;
}

/* Logout */
.logout-btn {
  margin: 0 30rpx;
  background: #fff;
  color: #e53935;
  border: 2rpx solid #ffcdd2;
  border-radius: 16rpx;
  font-size: 30rpx;
  padding: 24rpx;
}

/* Modal */
.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0,0,0,0.45);
  z-index: 999;
  display: flex;
  align-items: flex-end;
}
.modal-sheet {
  background: #fff;
  border-radius: 32rpx 32rpx 0 0;
  padding: 32rpx 40rpx 60rpx;
  width: 100%;
  max-height: 85vh;
  overflow-y: auto;
}
.modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 32rpx;
}
.modal-title { font-size: 34rpx; font-weight: bold; color: #1b5e20; }
.modal-close { font-size: 36rpx; color: #999; padding: 8rpx; }

/* 碳账本 */
.ledger-total {
  text-align: center;
  padding: 24rpx 0 32rpx;
  background: linear-gradient(135deg, #e8f5e9, #f1f8e9);
  border-radius: 20rpx;
  margin-bottom: 32rpx;
}
.ledger-kg { font-size: 72rpx; font-weight: bold; color: #2e7d32; }
.ledger-unit { font-size: 28rpx; color: #4caf50; margin-left: 8rpx; }
.ledger-sub { display: block; font-size: 24rpx; color: #888; margin-top: 8rpx; }
.equiv-row { display: flex; gap: 20rpx; margin-bottom: 32rpx; }
.equiv-item {
  flex: 1;
  background: #f9fef9;
  border-radius: 16rpx;
  padding: 24rpx 16rpx;
  text-align: center;
}
.equiv-icon { display: block; font-size: 48rpx; margin-bottom: 8rpx; }
.equiv-val { display: block; font-size: 36rpx; font-weight: bold; color: #2e7d32; }
.equiv-desc { display: block; font-size: 20rpx; color: #888; margin-top: 4rpx; }
.ledger-section-title { font-size: 26rpx; color: #555; margin-bottom: 20rpx; font-weight: 500; }
.week-bars { display: flex; gap: 8rpx; align-items: flex-end; }
.bar-col { flex: 1; display: flex; flex-direction: column; align-items: center; }
.bar-wrap { width: 100%; height: 100rpx; display: flex; align-items: flex-end; justify-content: center; }
.bar-fill { width: 24rpx; background: linear-gradient(180deg, #4caf50, #a5d6a7); border-radius: 4rpx 4rpx 0 0; min-height: 8rpx; }
.bar-label { font-size: 20rpx; color: #888; margin-top: 6rpx; }
.bar-steps { font-size: 18rpx; color: #2e7d32; }

/* 设置 */
.setting-group { margin-bottom: 28rpx; }
.setting-group-title { font-size: 24rpx; color: #888; margin-bottom: 12rpx; padding-left: 4rpx; }
.setting-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 24rpx 0;
  border-bottom: 1rpx solid #f5f5f5;
}
.setting-label { font-size: 28rpx; color: #333; }
.setting-value { font-size: 26rpx; color: #888; }
.setting-input {
  font-size: 26rpx;
  color: #333;
  text-align: right;
  width: 300rpx;
  border: none;
  background: transparent;
}
.save-name-btn {
  margin-top: 16rpx;
  background: linear-gradient(90deg, #4caf50, #2e7d32);
  color: #fff;
  border: none;
  border-radius: 12rpx;
  font-size: 26rpx;
  padding: 18rpx;
}
.clear-cache-btn {
  background: #f5f5f5;
  color: #666;
  border: none;
  border-radius: 12rpx;
  font-size: 26rpx;
  padding: 20rpx;
}

/* 关于 */
.about-logo { text-align: center; font-size: 96rpx; margin: 16rpx 0 12rpx; display: block; }
.about-appname { display: block; text-align: center; font-size: 36rpx; font-weight: bold; color: #1b5e20; margin-bottom: 8rpx; }
.about-version { display: block; text-align: center; font-size: 24rpx; color: #999; margin-bottom: 24rpx; }
.about-desc { display: block; text-align: center; font-size: 26rpx; color: #555; line-height: 1.7; margin-bottom: 28rpx; padding: 0 8rpx; }
.about-divider { height: 1rpx; background: #f0f0f0; margin-bottom: 16rpx; }
.about-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 24rpx 0;
  border-bottom: 1rpx solid #f5f5f5;
}
.about-label { font-size: 28rpx; color: #333; }
.about-val { font-size: 26rpx; color: #888; }
.about-link { font-size: 26rpx; color: #4caf50; }
</style>

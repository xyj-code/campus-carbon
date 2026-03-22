<template>
  <view class="container">
    <!-- 积分余额卡 -->
    <view class="balance-card">
      <text class="balance-label">当前积分</text>
      <text class="balance-value">{{ profileData.points || 0 }}</text>
      <text class="balance-unit">分</text>
    </view>

    <!-- 类型筛选 -->
    <view class="filter-bar">
      <view
        v-for="f in filters" :key="f.value"
        class="filter-chip"
        :class="{ active: activeFilter === f.value }"
        @click="switchFilter(f.value)"
      >{{ f.label }}</view>
    </view>

    <!-- 记录列表 -->
    <view class="records-list">
      <view v-if="loading" class="empty-state">
        <text class="empty-text">加载中…</text>
      </view>
      <template v-else-if="records.length > 0">
        <view v-for="(r, i) in records" :key="i" class="record-item">
          <view class="rec-left">
            <view class="rec-icon" :class="r.type">
              <text>{{ r.type === 'income' ? '↑' : '↓' }}</text>
            </view>
            <view class="rec-info">
              <text class="rec-title">{{ r.title }}</text>
              <text class="rec-remark">{{ r.remark }}</text>
              <text class="rec-time">{{ r.createTime }}</text>
            </view>
          </view>
          <text class="rec-amount" :class="r.type">
            {{ r.type === 'income' ? '+' : '-' }}{{ r.amount }}
          </text>
        </view>
      </template>
      <view v-else class="empty-state">
        <text class="empty-icon">🪙</text>
        <text class="empty-text">暂无积分记录</text>
      </view>
    </view>

    <!-- 加载更多 -->
    <view v-if="records.length > 0" class="load-more-row">
      <button
        v-if="currentPage < totalPages"
        class="load-more-btn"
        @click="loadMore"
      >加载更多</button>
      <text v-else class="no-more">— 已全部加载 —</text>
    </view>
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
      loading: false
    };
  },
  onLoad() {
    this.username = uni.getStorageSync('username');
    this.loadProfile();
    this.loadRecords(true);
  },
  methods: {
    async loadProfile() {
      try {
        this.profileData = await getProfile(this.username);
      } catch (e) {}
    },
    async loadRecords(reset = false) {
      if (this.loading) return;
      if (reset) { this.currentPage = 1; this.records = []; }
      this.loading = true;
      try {
        const res = await getPointsRecords(this.username, this.activeFilter, this.currentPage, this.pageSize);
        this.records = reset ? (res.list || []) : [...this.records, ...(res.list || [])];
        this.totalPages = res.totalPages || 1;
      } catch (e) {
        uni.showToast({ title: '加载失败', icon: 'none' });
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
      this.currentPage++;
      this.loadRecords(false);
    }
  }
};
</script>

<style scoped>
.container {
  min-height: 100vh;
  background: #f0f7f0;
  padding: 24rpx 30rpx 60rpx;
}

.balance-card {
  background: linear-gradient(135deg, #2e7d32, #66bb6a);
  border-radius: 24rpx;
  padding: 48rpx 40rpx;
  text-align: center;
  margin-bottom: 24rpx;
  box-shadow: 0 8rpx 24rpx rgba(46,125,50,0.2);
}
.balance-label { display: block; font-size: 26rpx; color: rgba(255,255,255,0.8); margin-bottom: 12rpx; }
.balance-value { font-size: 80rpx; font-weight: bold; color: #fff; }
.balance-unit { font-size: 30rpx; color: rgba(255,255,255,0.85); margin-left: 4rpx; }

.filter-bar {
  display: flex;
  gap: 16rpx;
  margin-bottom: 24rpx;
}
.filter-chip {
  flex: 1;
  text-align: center;
  padding: 16rpx 0;
  border-radius: 20rpx;
  font-size: 26rpx;
  background: #fff;
  color: #555;
  border: 2rpx solid transparent;
}
.filter-chip.active {
  background: #e8f5e9;
  color: #2e7d32;
  border-color: #a5d6a7;
  font-weight: 600;
}

.records-list {
  background: #fff;
  border-radius: 20rpx;
  overflow: hidden;
  box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.04);
}

.record-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 28rpx 32rpx;
  border-bottom: 1rpx solid #f5f5f5;
}
.record-item:last-child { border-bottom: none; }

.rec-left { display: flex; align-items: center; gap: 20rpx; flex: 1; }
.rec-icon {
  width: 64rpx;
  height: 64rpx;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28rpx;
  font-weight: bold;
  flex-shrink: 0;
}
.rec-icon.income { background: #e8f5e9; color: #2e7d32; }
.rec-icon.expense { background: #fafafa; color: #999; }

.rec-info { flex: 1; }
.rec-title { display: block; font-size: 28rpx; color: #333; font-weight: 500; }
.rec-remark { display: block; font-size: 22rpx; color: #888; margin-top: 4rpx; }
.rec-time { display: block; font-size: 20rpx; color: #bbb; margin-top: 4rpx; }

.rec-amount { font-size: 32rpx; font-weight: bold; flex-shrink: 0; }
.rec-amount.income { color: #2e7d32; }
.rec-amount.expense { color: #999; }

.empty-state { padding: 80rpx 0; text-align: center; }
.empty-icon { display: block; font-size: 72rpx; margin-bottom: 20rpx; }
.empty-text { font-size: 26rpx; color: #aaa; }

.load-more-row { text-align: center; padding: 32rpx 0; }
.load-more-btn {
  background: #e8f5e9;
  color: #2e7d32;
  border: none;
  border-radius: 20rpx;
  font-size: 26rpx;
  padding: 16rpx 48rpx;
}
.no-more { font-size: 24rpx; color: #ccc; }
</style>

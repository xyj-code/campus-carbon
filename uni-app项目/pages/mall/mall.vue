<template>
  <view class="page">
    <!-- 高级渐变背景 -->
    <view class="gradient-bg"></view>

    <!-- 动态粒子系统 -->
    <view class="particle-system">
      <view v-for="(item, index) in 40" :key="index" class="particle" :style="particleStyles[index]"></view>
    </view>

    <scroll-view scroll-y="true" class="scroller" :show-scrollbar="false">
      <!-- 高级积分余额卡片 -->
      <view class="balance-card floating-card">
        <view class="balance-glow"></view>
        <view class="balance-glow-2"></view>
        <view class="balance-content">
          <view class="balance-left">
            <text class="balance-label">我的积分</text>
            <view class="balance-value-wrap">
              <text class="balance-value">{{ userPoints }}</text>
              <text class="balance-unit">分</text>
            </view>
            <text class="balance-tip">✨ 兑换心仪的低碳好物</text>
          </view>
          <view class="balance-icon">
            <text>🎁</text>
            <view class="icon-ring"></view>
            <view class="icon-ring-2"></view>
          </view>
        </view>
      </view>

      <!-- ========== 热门好物 - 大卡片包裹小卡片 ========== -->
      <view class="section-wrapper floating-card">
        <view class="wrapper-glow"></view>
        <view class="wrapper-glow-2"></view>

        <view class="section-header">
          <view class="section-bar"></view>
          <text class="section-title">热门好物</text>
          <text class="section-sub">低碳生活 · 积分兑换</text>
        </view>

        <!-- 商品网格 - 蓝色卡片 -->
        <view class="goods-grid" v-if="productList.length > 0">
          <view
              v-for="(p, idx) in productList"
              :key="p.id"
              class="goods-card goods-card-blue floating-card"
              :class="{ 'out-stock': p.stock === 0 }"
              @click="goToProductDetail(p)"
              :style="{ animationDelay: idx * 0.05 + 's' }"
          >
            <view class="card-glow-layer"></view>
            <view class="card-glow-layer-2"></view>
            <view class="goods-img-wrap">
              <text class="goods-img">{{ getGoodsIcon(p.name) }}</text>
              <view class="img-sparkle"></view>
            </view>
            <text class="goods-name">{{ p.name }}</text>
            <text class="goods-desc">{{ p.description }}</text>
            <view class="goods-footer">
              <view class="goods-point-wrap">
                <text class="goods-point">{{ p.point }}</text>
                <text class="goods-unit">积分</text>
              </view>
              <view class="goods-stock" :class="{ low: p.stock < 20 }">
                <text>📦 {{ p.stock }}</text>
              </view>
            </view>
            <view class="card-arrow">→</view>
            <view v-if="p.stock === 0" class="sold-out-mask">
              <text>已售罄</text>
            </view>
          </view>
        </view>

        <view v-else-if="loadingProducts" class="loading-card">
          <view class="loading-spinner"></view>
          <text>加载商品中...</text>
        </view>
        <view v-else class="empty-card">
          <view class="empty-icon">🎁</view>
          <text>暂无商品</text>
          <text>敬请期待更多好物</text>
        </view>

        <!-- 加载更多 -->
        <view class="load-more" v-if="productPage < productTotalPages">
          <view class="load-more-btn" @click="loadMoreProducts">
            <text>加载更多</text>
            <text class="btn-arrow">↓</text>
          </view>
        </view>
      </view>

      <!-- ========== 兑换记录 - 大卡片包裹小卡片 ========== -->
      <view class="section-wrapper floating-card">
        <view class="wrapper-glow"></view>
        <view class="wrapper-glow-2"></view>

        <view class="section-header" style="margin-top: 0;">
          <view class="section-bar"></view>
          <text class="section-title">兑换记录</text>
          <text class="section-sub">绿色足迹</text>
        </view>

        <!-- 兑换记录网格 - 紫色卡片 -->
        <view class="exchange-grid" v-if="exchangeRecords.length > 0">
          <view
              v-for="(r, i) in exchangeRecords"
              :key="i"
              class="exchange-card exchange-card-purple floating-card"
              @click="viewRecordDetail(r)"
              :style="{ animationDelay: i * 0.05 + 's' }"
          >
            <view class="card-glow-layer"></view>
            <view class="card-glow-layer-2"></view>
            <view class="goods-img-wrap">
              <text class="goods-img">{{ getExchangeIcon(r.productName) }}</text>
              <view class="img-sparkle"></view>
            </view>
            <text class="goods-name">{{ r.productName }}</text>
            <text class="exchange-time">{{ r.createTime }}</text>
            <view class="goods-footer">
              <view class="goods-point-wrap">
                <text class="goods-point">-{{ r.point }}</text>
                <text class="goods-unit">积分</text>
              </view>
              <view class="exchange-status">
                <text>✅ 已兑换</text>
              </view>
            </view>
            <view class="card-arrow">→</view>
          </view>
        </view>

        <view v-else class="empty-record-card">
          <view class="empty-icon-wrap">
            <view class="empty-glow"></view>
            <text class="empty-icon">📭</text>
          </view>
          <text class="empty-title">暂无兑换记录</text>
          <text class="empty-desc">快去兑换心仪好物吧</text>
        </view>
      </view>

      <!-- 底部装饰 -->
      <view class="footer-deco">
        <text class="deco-text">💚 用积分兑换绿色生活 · 让地球更美好 💚</text>
      </view>

      <view class="spacer"></view>
    </scroll-view>

    <!-- 底部导航 -->
    <bottom-nav :currentIndex="1" />
  </view>
</template>

<script>
import BottomNav from '../../components/bottom-nav.vue';
import { getProfile, getProductList, getExchangeRecords } from '../../utils/request.js';

export default {
  name: 'MallPage',
  components: { BottomNav },
  data() {
    return {
      username: '',
      userPoints: 0,
      productList: [],
      productPage: 1,
      productTotalPages: 1,
      loadingProducts: false,
      exchangeRecords: [],
      confirmProduct: null,
      particleStyles: []
    };
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
      this.loadBalance();
      this.loadProducts(true);
      this.loadExchangeRecords();
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
    getGoodsIcon(name) {
      const icons = {
        '环保水杯': '🥤',
        '竹制牙刷': '🪥',
        '帆布袋': '🛍️',
        '可降解垃圾袋': '🗑️',
        '环保餐具': '🥢',
        '种子卡片': '🌱',
        '植物盆栽': '🪴',
        '环保笔记本': '📓'
      };
      return icons[name] || '🎁';
    },
    // 兑换记录专用图标方法
    getExchangeIcon(name) {
      const icons = {
        '环保水杯': '🥤',
        '竹制牙刷': '🪥',
        '帆布袋': '🛍️',
        '可降解垃圾袋': '🗑️',
        '环保餐具': '🥢',
        '种子卡片': '🌱',
        '植物盆栽': '🪴',
        '环保笔记本': '📓'
      };
      return icons[name] || '🎁';
    },
    async loadBalance() {
      try {
        const res = await getProfile(this.username);
        this.userPoints = res.points || 0;
      } catch (e) {
        console.error('加载积分失败:', e);
      }
    },
    async loadProducts(reset = false) {
      if (this.loadingProducts) return;
      if (reset) { this.productPage = 1; this.productList = []; }
      this.loadingProducts = true;
      try {
        const mockProducts = [
          { id: 1, name: '环保水杯', description: '304不锈钢保温杯，减少一次性纸杯使用', point: 500, stock: 50 },
          { id: 2, name: '竹制牙刷', description: '可降解竹制牙刷，环保又健康', point: 200, stock: 100 },
          { id: 3, name: '帆布袋', description: '可重复使用的环保帆布袋', point: 150, stock: 80 },
          { id: 4, name: '可降解垃圾袋', description: '可生物降解垃圾袋，环保无污染', point: 100, stock: 200 },
          { id: 5, name: '环保餐具', description: '便携式环保餐具套装', point: 300, stock: 60 },
          { id: 6, name: '种子卡片', description: '可种植的种子卡片，种出绿色希望', point: 80, stock: 150 },
          { id: 7, name: '植物盆栽', description: '迷你多肉植物盆栽', point: 400, stock: 40 },
          { id: 8, name: '环保笔记本', description: '再生纸制作，环保记录本', point: 250, stock: 70 }
        ];

        if (reset) {
          this.productList = mockProducts;
        } else {
          this.productList = [...this.productList, ...mockProducts];
        }
        this.productTotalPages = 1;
      } catch (e) {
        uni.showToast({ title: '商品加载失败', icon: 'none' });
      } finally {
        this.loadingProducts = false;
      }
    },
    async loadExchangeRecords() {
      try {
        const mockRecords = [
          { id: 1, productName: '环保水杯', point: 500, createTime: '2025-03-28 10:30:00' },
          { id: 2, productName: '种子卡片', point: 80, createTime: '2025-03-27 15:20:00' },
          { id: 3, productName: '帆布袋', point: 150, createTime: '2025-03-26 09:15:00' },
          { id: 4, productName: '竹制牙刷', point: 200, createTime: '2025-03-25 14:45:00' },
          { id: 5, productName: '环保餐具', point: 300, createTime: '2025-03-24 11:30:00' },
          { id: 6, productName: '植物盆栽', point: 400, createTime: '2025-03-23 16:20:00' }
        ];
        this.exchangeRecords = mockRecords;
      } catch (e) {
        console.error('加载兑换记录失败:', e);
      }
    },
    loadMoreProducts() {
      if (this.productPage < this.productTotalPages) {
        this.productPage++;
        this.loadProducts(false);
      }
    },
    goToProductDetail(product) {
      uni.showToast({ title: `查看 ${product.name} 详情`, icon: 'none' });
    },
    viewRecordDetail(record) {
      uni.showToast({ title: `已兑换 ${record.productName}`, icon: 'success' });
    }
  }
};
</script>

<style scoped>
.page {
  min-height: 100vh;
  background: #2A4538;
  position: relative;
  overflow: hidden;
}

/* 高级渐变背景 */
.gradient-bg {
  position: fixed;
  top: -20%;
  left: -20%;
  right: -20%;
  bottom: -20%;
  background: radial-gradient(ellipse at 30% 40%,
  rgba(85, 125, 100, 0.5) 0%,
  rgba(55, 95, 75, 0.6) 50%,
  rgba(42, 69, 56, 0.8) 100%);
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
  background: rgba(160, 220, 180, 0.5);
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
    opacity: 0.4;
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
  50% { transform: translateY(-4rpx); }
}

/* ===== 高级积分余额卡片 ===== */
.balance-card {
  position: relative;
  margin-top: 100rpx;
  margin-bottom: 32rpx;
  background: linear-gradient(135deg, #4A8A62, #2E6A46);
  border-radius: 56rpx;
  padding: 40rpx 36rpx;
  overflow: hidden;
  box-shadow: 0 20rpx 40rpx rgba(0, 0, 0, 0.25);
  border: 1rpx solid rgba(180, 230, 180, 0.3);
}

.balance-glow {
  position: absolute;
  top: -30%;
  left: -20%;
  width: 140%;
  height: 140%;
  background: radial-gradient(circle, rgba(180, 230, 180, 0.25), transparent);
  animation: rotateSlow 20s linear infinite;
}

.balance-glow-2 {
  position: absolute;
  bottom: -30%;
  right: -20%;
  width: 120%;
  height: 120%;
  background: radial-gradient(circle, rgba(255, 240, 170, 0.2), transparent);
  animation: rotateSlowReverse 18s linear infinite;
}

@keyframes rotateSlow {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

@keyframes rotateSlowReverse {
  from { transform: rotate(0deg); }
  to { transform: rotate(-360deg); }
}

.balance-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  position: relative;
  z-index: 2;
}

.balance-left {
  flex: 1;
}

.balance-label {
  font-size: 26rpx;
  color: rgba(255, 255, 255, 0.75);
  display: block;
  margin-bottom: 12rpx;
  letter-spacing: 1rpx;
}

.balance-value-wrap {
  display: flex;
  align-items: baseline;
  gap: 8rpx;
  margin-bottom: 12rpx;
}

.balance-value {
  font-size: 68rpx;
  font-weight: 700;
  color: #FFE8A0;
  line-height: 1;
  text-shadow: 0 0 20rpx rgba(255, 232, 160, 0.4);
}

.balance-unit {
  font-size: 26rpx;
  color: rgba(255, 255, 255, 0.85);
}

.balance-tip {
  font-size: 22rpx;
  color: rgba(255, 255, 255, 0.65);
}

.balance-icon {
  position: relative;
  width: 90rpx;
  height: 90rpx;
  background: rgba(255, 255, 255, 0.12);
  border-radius: 45rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 48rpx;
  backdrop-filter: blur(8rpx);
}

.icon-ring {
  position: absolute;
  top: -4rpx;
  left: -4rpx;
  right: -4rpx;
  bottom: -4rpx;
  border: 1rpx solid rgba(255, 255, 255, 0.35);
  border-radius: 50%;
  animation: ringRotate 8s linear infinite;
}

.icon-ring-2 {
  position: absolute;
  top: -10rpx;
  left: -10rpx;
  right: -10rpx;
  bottom: -10rpx;
  border: 1rpx solid rgba(255, 232, 160, 0.25);
  border-radius: 50%;
  animation: ringRotateReverse 6s linear infinite;
}

@keyframes ringRotate {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

@keyframes ringRotateReverse {
  from { transform: rotate(0deg); }
  to { transform: rotate(-360deg); }
}

/* ===== 大卡片包裹器 ===== */
.section-wrapper {
  position: relative;
  background: linear-gradient(135deg, #3A6B4A, #23563A);
  border-radius: 48rpx;
  padding: 28rpx 24rpx 32rpx;
  margin-bottom: 32rpx;
  overflow: hidden;
  box-shadow: 0 16rpx 32rpx rgba(0, 0, 0, 0.2);
  border: 1rpx solid rgba(180, 230, 180, 0.3);
}

.wrapper-glow {
  position: absolute;
  top: -30%;
  left: -20%;
  width: 140%;
  height: 140%;
  background: radial-gradient(circle, rgba(180, 230, 180, 0.2), transparent);
  animation: rotateSlow 25s linear infinite;
  pointer-events: none;
}

.wrapper-glow-2 {
  position: absolute;
  bottom: -30%;
  right: -20%;
  width: 120%;
  height: 120%;
  background: radial-gradient(circle, rgba(255, 240, 170, 0.15), transparent);
  animation: rotateSlowReverse 22s linear infinite;
  pointer-events: none;
}

/* 区块标题 */
.section-header {
  display: flex;
  align-items: baseline;
  gap: 12rpx;
  margin-bottom: 20rpx;
  position: relative;
  z-index: 2;
}

.section-bar {
  width: 6rpx;
  height: 32rpx;
  background: linear-gradient(180deg, #FFE8A0, #D6B84A);
  border-radius: 3rpx;
  box-shadow: 0 0 8rpx rgba(255, 232, 160, 0.4);
}

.section-title {
  font-size: 32rpx;
  font-weight: 700;
  color: #F0F8EA;
}

.section-sub {
  font-size: 22rpx;
  color: #B8D8A8;
  margin-left: auto;
}

/* ===== 蓝色卡片（热门好物）===== */
.goods-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16rpx;
  margin-bottom: 16rpx;
  position: relative;
  z-index: 2;
}

.goods-card-blue {
  background: linear-gradient(135deg, #2D5F6E, #1C3F52);
  border: 1rpx solid rgba(100, 180, 200, 0.4);
}

/* ===== 紫色卡片（兑换记录）===== */
.exchange-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16rpx;
  margin-bottom: 16rpx;
  position: relative;
  z-index: 2;
}

.exchange-card-purple {
  background: linear-gradient(135deg, #5A3F6E, #3A2A4A);
  border: 1rpx solid rgba(180, 130, 200, 0.4);
}

/* 卡片通用样式 */
.goods-card,
.exchange-card {
  position: relative;
  border-radius: 28rpx;
  padding: 20rpx 16rpx 18rpx;
  overflow: hidden;
  transition: all 0.3s ease;
  box-shadow: 0 8rpx 20rpx rgba(0, 0, 0, 0.2);
}

.goods-card:active,
.exchange-card:active {
  transform: scale(0.97);
}

/* 卡片光晕层 */
.card-glow-layer {
  position: absolute;
  top: -30%;
  left: -20%;
  width: 140%;
  height: 140%;
  background: radial-gradient(circle, rgba(180, 230, 180, 0.2), transparent);
  animation: rotateSlow 20s linear infinite;
  pointer-events: none;
}

.card-glow-layer-2 {
  position: absolute;
  bottom: -30%;
  right: -20%;
  width: 120%;
  height: 120%;
  background: radial-gradient(circle, rgba(255, 240, 170, 0.15), transparent);
  animation: rotateSlowReverse 18s linear infinite;
  pointer-events: none;
}

.goods-img-wrap {
  position: relative;
  width: 100%;
  text-align: center;
  margin-bottom: 12rpx;
}

.goods-img {
  font-size: 64rpx;
  display: inline-block;
  position: relative;
  z-index: 2;
}

.img-sparkle {
  position: absolute;
  top: 15%;
  right: 20%;
  width: 16rpx;
  height: 16rpx;
  background: radial-gradient(circle, #FFE8A0, transparent);
  border-radius: 50%;
  animation: sparkle 2s ease-in-out infinite;
}

@keyframes sparkle {
  0%, 100% { opacity: 0; transform: scale(0.5); }
  50% { opacity: 0.9; transform: scale(1); }
}

.goods-name {
  font-size: 26rpx;
  font-weight: 600;
  color: #F0F8EA;
  display: block;
  margin-bottom: 6rpx;
  text-align: center;
}

.goods-desc {
  font-size: 20rpx;
  color: #B8D8A8;
  line-height: 1.4;
  margin-bottom: 14rpx;
  text-align: center;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.exchange-time {
  font-size: 20rpx;
  color: #B8D8A8;
  line-height: 1.4;
  margin-bottom: 14rpx;
  text-align: center;
  display: block;
}

.goods-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.goods-point-wrap {
  display: flex;
  align-items: baseline;
  gap: 4rpx;
}

.goods-point {
  font-size: 32rpx;
  font-weight: 700;
  color: #FFE8A0;
}

.goods-unit {
  font-size: 20rpx;
  color: #FFE8A0;
}

.goods-stock {
  font-size: 20rpx;
  color: #B8D8A8;
  background: rgba(100, 180, 130, 0.25);
  padding: 4rpx 12rpx;
  border-radius: 24rpx;
}

.goods-stock.low {
  color: #FFB86C;
  background: rgba(255, 184, 108, 0.2);
}

.exchange-status {
  font-size: 20rpx;
  color: #B8D8A8;
  background: rgba(100, 180, 130, 0.25);
  padding: 4rpx 12rpx;
  border-radius: 24rpx;
}

.card-arrow {
  position: absolute;
  bottom: 12rpx;
  right: 16rpx;
  font-size: 32rpx;
  color: rgba(255, 255, 255, 0.4);
  transition: transform 0.3s ease;
}

.goods-card:active .card-arrow,
.exchange-card:active .card-arrow {
  transform: translateX(6rpx);
  color: #FFE8A0;
}

.sold-out-mask {
  position: absolute;
  inset: 0;
  background: rgba(31, 51, 39, 0.92);
  backdrop-filter: blur(8rpx);
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 28rpx;
  z-index: 10;
}

.sold-out-mask text {
  font-size: 26rpx;
  font-weight: 600;
  color: #FFFFFF;
  background: rgba(0, 0, 0, 0.65);
  padding: 8rpx 20rpx;
  border-radius: 30rpx;
}

/* 加载卡片 */
.loading-card {
  background: rgba(45, 85, 63, 0.85);
  backdrop-filter: blur(20rpx);
  border-radius: 40rpx;
  padding: 60rpx;
  text-align: center;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20rpx;
  color: #B8D8A8;
}

.loading-spinner {
  width: 44rpx;
  height: 44rpx;
  border: 3rpx solid rgba(140, 200, 140, 0.3);
  border-top-color: #FFE8A0;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

/* 空状态卡片 */
.empty-card {
  background: rgba(45, 85, 63, 0.85);
  backdrop-filter: blur(20rpx);
  border-radius: 40rpx;
  padding: 60rpx 40rpx;
  text-align: center;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12rpx;
  color: #B8D8A8;
}

.empty-icon {
  font-size: 64rpx;
  opacity: 0.5;
  margin-bottom: 12rpx;
}

/* 加载更多 */
.load-more {
  text-align: center;
  padding: 16rpx 0 8rpx;
  position: relative;
  z-index: 2;
}

.load-more-btn {
  display: inline-flex;
  align-items: center;
  gap: 12rpx;
  background: rgba(45, 85, 63, 0.8);
  backdrop-filter: blur(20rpx);
  padding: 16rpx 40rpx;
  border-radius: 50rpx;
  transition: all 0.3s ease;
  border: 1rpx solid rgba(140, 200, 140, 0.35);
}

.load-more-btn:active {
  transform: scale(0.96);
}

.load-more-btn text {
  font-size: 24rpx;
  color: #FFE8A0;
  font-weight: 500;
}

.btn-arrow {
  font-size: 26rpx;
  animation: bounceArrow 1s ease-in-out infinite;
}

@keyframes bounceArrow {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(4rpx); }
}

/* 空兑换记录卡片 */
.empty-record-card {
  background: rgba(45, 85, 63, 0.8);
  backdrop-filter: blur(20rpx);
  border-radius: 40rpx;
  padding: 50rpx 40rpx;
  text-align: center;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12rpx;
  position: relative;
  z-index: 2;
}

.empty-icon-wrap {
  position: relative;
  width: 100rpx;
  height: 100rpx;
  margin: 0 auto 16rpx;
}

.empty-glow {
  position: absolute;
  top: -8rpx;
  left: -8rpx;
  right: -8rpx;
  bottom: -8rpx;
  background: radial-gradient(circle, rgba(255, 232, 160, 0.25), transparent);
  border-radius: 50%;
  animation: glowPulse 2s ease-in-out infinite;
}

@keyframes glowPulse {
  0%, 100% { opacity: 0.5; transform: scale(1); }
  50% { opacity: 0.9; transform: scale(1.1); }
}

.empty-icon {
  font-size: 56rpx;
  position: relative;
  z-index: 2;
  display: block;
  text-align: center;
  line-height: 100rpx;
}

.empty-title {
  font-size: 28rpx;
  font-weight: 600;
  color: #F0F8EA;
  display: block;
  margin-bottom: 6rpx;
}

.empty-desc {
  font-size: 22rpx;
  color: #B8D8A8;
}

/* 底部装饰 */
.footer-deco {
  text-align: center;
  padding: 32rpx 0 24rpx;
}

.deco-text {
  font-size: 22rpx;
  font-weight: 500;
  color: #B8D8A8;
  letter-spacing: 1rpx;
}

.spacer {
  height: 100rpx;
}
</style>
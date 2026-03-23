<template>
  <view class="container">
    <!-- 顶部积分余额 -->
    <view class="balance-banner">
      <view class="balance-inner">
        <text class="b-label">我的积分</text>
        <text class="b-value">{{ userPoints }}</text>
        <text class="b-unit">分</text>
      </view>
      <text class="b-tip">兑换心仪的低碳好物吧 🌿</text>
    </view>

    <!-- 商品列表 -->
    <view class="section-title">热门商品</view>
    <view class="goods-grid" v-if="productList.length > 0">
      <view
        v-for="p in productList" :key="p.id"
        class="goods-card"
        :class="{ 'out-stock': p.stock === 0 }"
        @click="showConfirm(p)"
      >
        <text class="goods-img">{{ p.image }}</text>
        <text class="goods-name">{{ p.name }}</text>
        <text class="goods-desc">{{ p.description }}</text>
        <view class="goods-footer">
          <view class="goods-point-wrap">
            <text class="goods-point">{{ p.point }}</text>
            <text class="goods-unit">积分</text>
          </view>
          <view class="goods-stock-badge" :class="{ low: p.stock < 20 }">
            <text>库存 {{ p.stock }}</text>
          </view>
        </view>
        <view v-if="p.stock === 0" class="sold-out-mask">
          <text>已售罄</text>
        </view>
      </view>
    </view>
    <view v-else-if="loadingProducts" class="empty-hint"><text>加载商品中…</text></view>
    <view v-else class="empty-hint"><text>暂无商品</text></view>

    <!-- 加载更多 -->
    <view class="load-more-row" v-if="productPage < productTotalPages">
      <button class="load-more-btn" @click="loadMoreProducts">加载更多</button>
    </view>

    <!-- 兑换记录 -->
    <view class="section-title" style="margin-top:32rpx;">兑换记录</view>
    <view class="exchange-list" v-if="exchangeRecords.length > 0">
      <view v-for="(r, i) in exchangeRecords" :key="i" class="ex-item">
        <view class="ex-left">
          <text class="ex-icon">🎁</text>
          <view>
            <text class="ex-name">{{ r.productName }}</text>
            <text class="ex-time">{{ r.createTime }}</text>
          </view>
        </view>
        <text class="ex-point">-{{ r.point }} 分</text>
      </view>
    </view>
    <view v-else class="empty-hint"><text>暂无兑换记录</text></view>

    <!-- 底部导航 -->
    <bottom-nav :currentIndex="1" />

    <!-- 兑换确认弹窗 -->
    <view v-if="confirmProduct" class="modal-overlay" @click.self="confirmProduct = null">
      <view class="confirm-sheet">
        <text class="conf-icon">{{ confirmProduct.image }}</text>
        <text class="conf-name">{{ confirmProduct.name }}</text>
        <text class="conf-desc">{{ confirmProduct.description }}</text>
        <view class="conf-points-row">
          <text class="conf-cost">消耗 {{ confirmProduct.point }} 积分</text>
          <text class="conf-remain">剩余 {{ userPoints - confirmProduct.point }} 积分</text>
        </view>
        <view v-if="userPoints < confirmProduct.point" class="conf-warn">
          <text>积分不足 😔</text>
        </view>
        <view class="conf-btns">
          <button class="conf-cancel" @click="confirmProduct = null">取消</button>
          <button
            class="conf-ok"
            :disabled="userPoints < confirmProduct.point || confirmProduct.stock === 0"
            @click="doExchange"
          >确认兑换</button>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import BottomNav from '../../components/bottom-nav.vue';
import { getProfile, getProductList, exchangeProduct, getExchangeRecords } from '../../utils/request.js';

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
      confirmProduct: null
    };
  },
  onShow() {
    this.username = uni.getStorageSync('username');
    if (this.username) {
      this.loadBalance();
      this.loadProducts(true);
      this.loadExchangeRecords();
    }
  },
  methods: {
    async loadBalance() {
      try {
        const res = await getProfile(this.username);
        this.userPoints = res.points || 0;
      } catch (e) {}
    },
    async loadProducts(reset = false) {
      if (this.loadingProducts) return;
      if (reset) { this.productPage = 1; this.productList = []; }
      this.loadingProducts = true;
      try {
        const res = await getProductList(this.productPage, 12);
        this.productList = reset ? (res.list || []) : [...this.productList, ...(res.list || [])];
        this.productTotalPages = res.totalPages || 1;
      } catch (e) {
        uni.showToast({ title: '商品加载失败', icon: 'none' });
      } finally {
        this.loadingProducts = false;
      }
    },
    async loadExchangeRecords() {
      try {
        const res = await getExchangeRecords(this.username);
        this.exchangeRecords = res || [];
      } catch (e) {}
    },
    loadMoreProducts() {
      this.productPage++;
      this.loadProducts(false);
    },
    showConfirm(product) {
      if (product.stock === 0) {
        uni.showToast({ title: '该商品已售罄', icon: 'none' });
        return;
      }
      this.confirmProduct = product;
    },
    async doExchange() {
      if (!this.confirmProduct) return;
      uni.showLoading({ title: '兑换中…' });
      try {
        const res = await exchangeProduct(this.username, this.confirmProduct.id);
        uni.hideLoading();
        if (res.code === 200) {
          uni.showToast({ title: '兑换成功！', icon: 'success' });
          this.userPoints = res.remainPoints;
          this.confirmProduct.stock--;
          // 刷新兑换记录
          this.loadExchangeRecords();
        } else {
          uni.showToast({ title: res.message || '兑换失败', icon: 'none' });
        }
      } catch (e) {
        uni.hideLoading();
        uni.showToast({ title: '网络错误', icon: 'none' });
      } finally {
        this.confirmProduct = null;
      }
    }
  }
};
</script>

<style scoped>
.container {
  min-height: 100vh;
  background: #f0f7f0;
  padding: 0 0 100rpx;
}

/* 顶部余额 */
.balance-banner {
  background: linear-gradient(160deg, #1b5e20, #4caf50);
  padding: 48rpx 40rpx 40rpx;
}
.balance-inner { display: flex; align-items: baseline; gap: 8rpx; margin-bottom: 12rpx; }
.b-label { font-size: 28rpx; color: rgba(255,255,255,0.8); margin-right: 8rpx; }
.b-value { font-size: 72rpx; font-weight: bold; color: #fff; line-height: 1; }
.b-unit { font-size: 28rpx; color: rgba(255,255,255,0.85); }
.b-tip { font-size: 24rpx; color: rgba(255,255,255,0.7); }

.section-title {
  font-size: 30rpx;
  font-weight: bold;
  color: #2e7d32;
  padding: 32rpx 30rpx 16rpx;
}

/* 商品网格 */
.goods-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20rpx;
  padding: 0 30rpx;
}
.goods-card {
  background: #fff;
  border-radius: 20rpx;
  padding: 28rpx 24rpx 24rpx;
  box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.06);
  position: relative;
  overflow: hidden;
}
.goods-card.out-stock { opacity: 0.6; }
.goods-img { display: block; font-size: 72rpx; margin-bottom: 12rpx; }
.goods-name { display: block; font-size: 28rpx; font-weight: 600; color: #333; margin-bottom: 8rpx; }
.goods-desc { display: block; font-size: 22rpx; color: #888; line-height: 1.5; margin-bottom: 16rpx;
  overflow: hidden; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; }
.goods-footer { display: flex; align-items: center; justify-content: space-between; }
.goods-point-wrap { display: flex; align-items: baseline; gap: 4rpx; }
.goods-point { font-size: 36rpx; font-weight: bold; color: #e65100; }
.goods-unit { font-size: 22rpx; color: #e65100; }
.goods-stock-badge {
  font-size: 20rpx;
  color: #888;
  background: #f5f5f5;
  padding: 4rpx 12rpx;
  border-radius: 20rpx;
}
.goods-stock-badge.low { background: #fff3e0; color: #e65100; }
.sold-out-mask {
  position: absolute;
  inset: 0;
  background: rgba(255,255,255,0.7);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28rpx;
  font-weight: bold;
  color: #999;
}

/* 兑换记录 */
.exchange-list {
  background: #fff;
  margin: 0 30rpx;
  border-radius: 20rpx;
  overflow: hidden;
  box-shadow: 0 2rpx 10rpx rgba(0,0,0,0.04);
}
.ex-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 24rpx 32rpx;
  border-bottom: 1rpx solid #f5f5f5;
}
.ex-item:last-child { border-bottom: none; }
.ex-left { display: flex; align-items: center; gap: 16rpx; }
.ex-icon { font-size: 40rpx; }
.ex-name { display: block; font-size: 28rpx; color: #333; }
.ex-time { display: block; font-size: 20rpx; color: #bbb; margin-top: 4rpx; }
.ex-point { font-size: 28rpx; font-weight: bold; color: #999; }

.empty-hint { padding: 48rpx; text-align: center; color: #aaa; font-size: 26rpx; }

.load-more-row { text-align: center; padding: 24rpx; }
.load-more-btn {
  background: #e8f5e9;
  color: #2e7d32;
  border: none;
  border-radius: 20rpx;
  font-size: 26rpx;
  padding: 14rpx 48rpx;
}

/* 确认弹窗 */
.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0,0,0,0.5);
  z-index: 999;
  display: flex;
  align-items: center;
  justify-content: center;
}
.confirm-sheet {
  background: #fff;
  border-radius: 32rpx;
  padding: 48rpx 40rpx 40rpx;
  width: 580rpx;
  text-align: center;
}
.conf-icon { font-size: 96rpx; display: block; margin-bottom: 16rpx; }
.conf-name { display: block; font-size: 36rpx; font-weight: bold; color: #1b5e20; margin-bottom: 8rpx; }
.conf-desc { display: block; font-size: 24rpx; color: #888; margin-bottom: 28rpx; line-height: 1.6; }
.conf-points-row {
  background: #f9fef9;
  border-radius: 16rpx;
  padding: 24rpx;
  margin-bottom: 20rpx;
}
.conf-cost { display: block; font-size: 36rpx; font-weight: bold; color: #e65100; margin-bottom: 8rpx; }
.conf-remain { display: block; font-size: 24rpx; color: #888; }
.conf-warn { background: #fff3e0; border-radius: 12rpx; padding: 16rpx; margin-bottom: 16rpx; color: #e65100; font-size: 26rpx; }
.conf-btns { display: flex; gap: 20rpx; margin-top: 20rpx; }
.conf-cancel {
  flex: 1;
  background: #f5f5f5;
  color: #666;
  border: none;
  border-radius: 16rpx;
  font-size: 28rpx;
  padding: 24rpx 0;
}
.conf-ok {
  flex: 1;
  background: linear-gradient(90deg, #4caf50, #2e7d32);
  color: #fff;
  border: none;
  border-radius: 16rpx;
  font-size: 28rpx;
  padding: 24rpx 0;
}
.conf-ok:disabled {
  background: #ccc;
  color: #fff;
}
</style>

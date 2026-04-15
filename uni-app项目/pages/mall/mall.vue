<template>
  <view class="page">
    <view class="gradient-bg"></view>

    <view class="particle-system">
      <view
        v-for="(item, index) in particleStyles"
        :key="index"
        class="particle"
        :style="item"
      ></view>
    </view>

    <scroll-view scroll-y class="scroller" :show-scrollbar="false">
      <view class="hero-card floating-card">
        <view class="hero-glow hero-glow-a"></view>
        <view class="hero-glow hero-glow-b"></view>

        <view class="hero-top">
          <view>
            <text class="hero-title">{{ texts.pageTitle }}</text>
            <text class="hero-subtitle">{{ texts.pageSubtitle }}</text>
          </view>
          <view class="point-pill">
            <text class="point-pill-icon">{{ sparkleIcon }}</text>
            <text class="point-pill-value">{{ userPoints }}</text>
            <text class="point-pill-unit">{{ texts.pointUnit }}</text>
          </view>
        </view>

        <view class="scene-row">
          <view
            v-for="scene in sceneCards"
            :key="scene.code"
            class="scene-card"
            :class="scene.cardClass"
            @click="selectCategory(scene.code)"
          >
            <text class="scene-icon">{{ scene.icon }}</text>
            <text class="scene-name">{{ scene.label }}</text>
            <text class="scene-count">{{ scene.count }}</text>
          </view>
        </view>
      </view>

      <view class="section-head">
        <view>
          <text class="section-title">{{ texts.catalogTitle }}</text>
          <text class="section-subtitle">{{ texts.catalogSubtitle }}</text>
        </view>
      </view>

      <scroll-view scroll-x class="chip-row" :show-scrollbar="false">
        <view
          v-for="option in categoryOptions"
          :key="option.code"
          class="chip"
          :class="{ active: activeCategory === option.code }"
          @click="selectCategory(option.code)"
        >
          <text class="chip-text">{{ option.label }}</text>
        </view>
      </scroll-view>

      <view v-if="loadingCatalog" class="state-card floating-card">
        <text class="state-title">{{ texts.loading }}</text>
      </view>

      <view v-else-if="filteredBenefits.length === 0" class="state-card floating-card">
        <text class="state-title">{{ texts.emptyBenefits }}</text>
        <text class="state-desc">{{ texts.emptyBenefitsHint }}</text>
      </view>

        <view v-else class="benefit-grid">
          <view
            v-for="item in displayBenefits"
            :key="item.id"
            class="benefit-card"
            @click="openBenefit(item)"
          >
            <view class="benefit-accent" :style="item.accentStyle"></view>

            <view class="benefit-top">
              <text class="benefit-category-badge" :style="item.badgeStyle">{{ item.categoryLabel }}</text>
              <view class="benefit-stock" :class="{ soldout: !item.available }">
                <text>{{ item.stockLabel }}</text>
              </view>
            </view>

            <view class="benefit-cover-shell">
              <image
                v-if="item.imageSrc"
                class="benefit-cover"
                :src="item.imageSrc"
                mode="aspectFill"
              />
              <view v-else class="benefit-icon-wrap">
                <text class="benefit-icon">{{ item.iconText }}</text>
              </view>
            </view>

            <text class="benefit-name">{{ item.name }}</text>
            <text class="benefit-subtitle">{{ item.subtitle }}</text>
            <text class="benefit-sponsor">{{ texts.sponsorLabel }}{{ item.sponsor }}</text>

            <view class="benefit-bottom">
              <view>
                <text class="benefit-point">{{ item.point }}</text>
                <text class="benefit-unit">{{ texts.pointUnit }}</text>
              </view>
              <text class="benefit-arrow" :style="item.arrowStyle">{{ arrowIcon }}</text>
            </view>

            <view class="benefit-actions">
              <view class="benefit-detail-btn" @click.stop="openBenefit(item)">
                <text class="benefit-detail-text">{{ texts.detailAction }}</text>
              </view>
              <view
                class="benefit-redeem-btn"
                :class="{ disabled: !item.canRedeemNow || redeeming }"
                @click.stop="handleCardRedeem(item)"
              >
                <text class="benefit-redeem-text">{{ redeeming && activeBenefit && activeBenefit.id === item.id ? texts.redeeming : item.actionText }}</text>
              </view>
            </view>
          </view>
        </view>

      <view class="section-head record-head">
        <view>
          <text class="section-title">{{ texts.recordTitle }}</text>
          <text class="section-subtitle">{{ texts.recordSubtitle }}</text>
        </view>
      </view>

      <view v-if="loadingRecords" class="state-card floating-card">
        <text class="state-title">{{ texts.loading }}</text>
      </view>

      <view v-else-if="records.length === 0" class="state-card floating-card">
        <text class="state-title">{{ texts.emptyRecords }}</text>
        <text class="state-desc">{{ texts.emptyRecordsHint }}</text>
      </view>

      <view v-else class="record-list">
        <view v-for="record in displayRecords" :key="record.id" class="record-card floating-card">
          <view class="record-left">
            <view class="record-icon-wrap" :class="record.cardClass">
              <text class="record-icon">{{ record.iconText }}</text>
            </view>
            <view class="record-copy">
              <text class="record-name">{{ record.name }}</text>
              <text class="record-next">{{ record.nextStep }}</text>
              <text class="record-time">{{ record.timeText }}</text>
            </view>
          </view>
          <view class="record-right">
            <text class="record-point">-{{ record.point }}</text>
            <text class="record-status" :class="record.statusClass">{{ record.status }}</text>
          </view>
        </view>
      </view>

      <view class="footer-space"></view>
    </scroll-view>

    <view v-if="activeBenefit" class="modal-mask" @click.self="closeDetail">
      <view class="modal-card">
        <view class="modal-head">
          <view>
            <text class="modal-title">{{ activeBenefit.name }}</text>
            <text class="modal-subtitle">{{ activeBenefit.categoryLabel }}</text>
          </view>
          <text class="modal-close" @click="closeDetail">{{ closeIcon }}</text>
        </view>

        <view class="modal-highlight" :class="activeBenefitCardClass">
          <image
            v-if="activeBenefitImage"
            class="modal-cover"
            :src="activeBenefitImage"
            mode="aspectFill"
          />
          <text v-else class="modal-icon">{{ activeBenefit.icon || defaultBenefitIcon }}</text>
          <view class="modal-highlight-copy">
            <text class="modal-line">{{ activeBenefit.subtitle }}</text>
            <text class="modal-line">{{ texts.sponsorLabel }}{{ activeBenefit.sponsor }}</text>
            <text class="modal-line">{{ texts.deliveryLabel }}{{ activeBenefit.deliveryMode }}</text>
          </view>
        </view>

        <text class="modal-description">{{ activeBenefit.description }}</text>

        <view class="modal-hint-box">
          <text class="modal-hint-title">{{ texts.redeemHintTitle }}</text>
          <text class="modal-hint-text">{{ activeBenefit.exchangeHint }}</text>
        </view>

        <view class="modal-price-row">
          <text class="modal-price">{{ activeBenefit.point }} {{ texts.pointUnit }}</text>
          <text class="modal-stock">{{ activeBenefitStockText }}</text>
        </view>

        <view class="modal-actions">
          <button class="secondary-btn" @click="closeDetail">{{ texts.close }}</button>
          <button
            class="primary-btn"
            :disabled="!activeBenefitCanRedeem || redeeming"
            @click="redeemActiveBenefit"
          >
            {{ redeeming ? texts.redeeming : activeBenefitActionText }}
          </button>
        </view>
      </view>
    </view>

    <bottom-nav :currentIndex="1" />
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
      benefits: [],
      records: [],
      activeCategory: 'ALL',
      activeBenefit: null,
      redeeming: false,
      loadingCatalog: false,
      loadingRecords: false,
      particleStyles: [],
      sparkleIcon: '\u2728',
      arrowIcon: '\u2192',
      closeIcon: 'x',
      defaultBenefitIcon: '\u2728',
      texts: {
        pageTitle: '\u7eff\u8272\u6743\u76ca\u4e2d\u5fc3',
        pageSubtitle: '\u79ef\u5206\u53ef\u5728\u9910\u996e\u3001\u5b66\u4e60\u3001\u8363\u8a89\u573a\u666f\u4e2d\u771f\u5b9e\u5151\u73b0',
        catalogTitle: '\u53ef\u5151\u6362\u6743\u76ca',
        catalogSubtitle: '\u53ea\u4fdd\u7559\u5bf9\u6821\u56ed\u573a\u666f\u6709\u771f\u5b9e\u610f\u4e49\u7684\u6743\u76ca',
        recordTitle: '\u6211\u7684\u6838\u9500\u8bb0\u5f55',
        recordSubtitle: '\u5151\u6362\u540e\u53ef\u6309\u6307\u5f15\u5b8c\u6210\u6838\u9500\u6216\u67e5\u770b\u751f\u6210\u7ed3\u679c',
        sponsorLabel: '\u63d0\u4f9b\u65b9\uff1a',
        deliveryLabel: '\u53d1\u653e\u65b9\u5f0f\uff1a',
        redeemHintTitle: '\u9886\u53d6\u6307\u5f15',
        pointUnit: '\u79ef\u5206',
        soldOut: '\u5df2\u5151\u5b8c',
        loading: '\u52a0\u8f7d\u4e2d...',
        emptyBenefits: '\u5f53\u524d\u573a\u666f\u6682\u65e0\u53ef\u7528\u6743\u76ca',
        emptyBenefitsHint: '\u53ef\u4ee5\u5207\u6362\u5230\u5176\u4ed6\u573a\u666f\u67e5\u770b',
        emptyRecords: '\u8fd8\u6ca1\u6709\u6743\u76ca\u8bb0\u5f55',
        emptyRecordsHint: '\u5b8c\u6210\u4e00\u6b21\u5151\u6362\u540e\uff0c\u8fd9\u91cc\u4f1a\u663e\u793a\u76f8\u5e94\u6838\u9500\u4fe1\u606f',
        close: '\u5173\u95ed',
        detailAction: '\u67e5\u770b\u8be6\u60c5',
        redeeming: '\u63d0\u4ea4\u4e2d...',
        redeemNow: '\u7acb\u5373\u5151\u6362',
        soldOutAction: '\u5e93\u5b58\u4e0d\u8db3',
        lackPoints: '\u79ef\u5206\u4e0d\u8db3',
        confirmRedeemTitle: '\u786e\u8ba4\u5151\u6362',
        confirmRedeemPrefix: '\u5c06\u4f7f\u7528 ',
        confirmRedeemMiddle: ' ',
        confirmRedeemSuffix: '\u5151\u6362',
        redeemSuccessTitle: '\u5151\u6362\u6210\u529f',
        redeemSuccessView: '\u53bb\u67e5\u770b',
        redeemSuccessDone: '\u7a0d\u540e\u518d\u8bf4',
        badgeSuccessHint: '\u5fbd\u7ae0\u5df2\u751f\u6210\uff0c\u53ef\u5728\u9996\u9875\u663e\u8457\u4f4d\u7f6e\u67e5\u770b',
        redeemFailSoldOut: '\u5f53\u524d\u6743\u76ca\u5df2\u5151\u5b8c',
        redeemFailPoints: '\u79ef\u5206\u4e0d\u8db3\uff0c\u6682\u65f6\u65e0\u6cd5\u5151\u6362',
        redeemFailMissing: '\u672a\u627e\u5230\u8be5\u6743\u76ca',
        redeemFailGeneric: '\u5151\u6362\u5931\u8d25\uff0c\u8bf7\u7a0d\u540e\u518d\u8bd5',
        networkError: '\u7f51\u7edc\u5f02\u5e38',
        loadError: '\u52a0\u8f7d\u5931\u8d25'
      },
      categoryOptions: [
        { code: 'ALL', label: '\u5168\u90e8' },
        { code: 'DINING', label: '\u9910\u996e' },
        { code: 'STATIONERY', label: '\u5b66\u4e60' },
        { code: 'CERT', label: '\u8363\u8a89' }
      ]
    };
  },
  computed: {
    filteredBenefits() {
      const source = this.activeCategory === 'ALL'
        ? this.benefits
        : this.benefits.filter(item => item.category === this.activeCategory);

      return [...source].sort((a, b) => {
        if (a.available === b.available) {
          return (a.point || 0) - (b.point || 0);
        }
        return a.available ? -1 : 1;
      });
    },
    displayBenefits() {
      return this.filteredBenefits.map(item => ({
        ...item,
        imageSrc: this.resolveBenefitImage(item),
        iconText: item.icon || this.defaultBenefitIcon,
        stockLabel: item.available ? this.stockText(item.stock) : this.texts.soldOut,
        accentStyle: this.categoryAccentStyle(item.category),
        badgeStyle: this.categoryBadgeStyle(item.category),
        arrowStyle: this.categoryArrowStyle(item.category),
        canRedeemNow: this.canRedeem(item),
        actionText: this.redeemButtonText(item)
      }));
    },
    displayRecords() {
      return this.records.map(record => ({
        ...record,
        cardClass: this.cardClass(record.category),
        iconText: record.icon || this.getCategoryIcon(record.category),
        statusClass: this.statusClass(record.status),
        timeText: this.formatTime(record.createTime)
      }));
    },
    activeBenefitCardClass() {
      return this.activeBenefit ? this.cardClass(this.activeBenefit.category) : 'card-default';
    },
    activeBenefitImage() {
      return this.activeBenefit ? this.resolveBenefitImage(this.activeBenefit) : '';
    },
    activeBenefitStockText() {
      if (!this.activeBenefit) {
        return '';
      }
      return this.activeBenefit.available ? this.stockText(this.activeBenefit.stock) : this.texts.soldOut;
    },
    activeBenefitCanRedeem() {
      return this.activeBenefit ? this.canRedeem(this.activeBenefit) : false;
    },
    activeBenefitActionText() {
      return this.activeBenefit ? this.redeemButtonText(this.activeBenefit) : this.texts.redeemNow;
    },
    sceneCards() {
      return [
        {
          code: 'DINING',
          label: '\u9910\u996e',
          icon: this.getCategoryIcon('DINING'),
          count: this.getCategoryCount('DINING'),
          cardClass: 'scene-dining'
        },
        {
          code: 'STATIONERY',
          label: '\u5b66\u4e60',
          icon: this.getCategoryIcon('STATIONERY'),
          count: this.getCategoryCount('STATIONERY'),
          cardClass: 'scene-library'
        },
        {
          code: 'CERT',
          label: '\u8363\u8a89',
          icon: this.getCategoryIcon('CERT'),
          count: this.getCategoryCount('CERT'),
          cardClass: 'scene-cert'
        }
      ];
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
      this.loadAll();
    }
  },
  methods: {
    initParticleStyles() {
      const styles = [];
      for (let i = 0; i < 36; i++) {
        styles.push({
          left: Math.random() * 100 + '%',
          animationDuration: (8 + Math.random() * 8) + 's',
          animationDelay: Math.random() * 6 + 's',
          width: (6 + Math.random() * 10) + 'rpx',
          height: (6 + Math.random() * 10) + 'rpx',
          opacity: 0.18 + Math.random() * 0.28
        });
      }
      this.particleStyles = styles;
    },
    async loadAll() {
      await Promise.all([this.loadProfile(), this.loadBenefits(), this.loadRecords()]);
    },
    async loadProfile() {
      try {
        const res = await getProfile(this.username);
        this.userPoints = res.points || 0;
      } catch (e) {
        console.error('profile load failed', e);
      }
    },
    async loadBenefits() {
      this.loadingCatalog = true;
      try {
        const res = await getProductList(1, 12);
        this.benefits = res.list || [];
      } catch (e) {
        console.error('benefit load failed', e);
        uni.showToast({ title: this.texts.loadError, icon: 'none' });
      } finally {
        this.loadingCatalog = false;
      }
    },
    async loadRecords() {
      this.loadingRecords = true;
      try {
        this.records = await getExchangeRecords(this.username);
      } catch (e) {
        console.error('record load failed', e);
        this.records = [];
      } finally {
        this.loadingRecords = false;
      }
    },
    getCategoryCount(category) {
      return this.benefits.filter(item => item.category === category && item.available).length;
    },
    resolveBenefitImage(item) {
      if (!item) {
        return '';
      }

      if (item.image) {
        return item.image;
      }

      const name = item.name || '';
      if (name.indexOf('\u5957\u9910\u5238') !== -1) {
        return '/static/mall/dining-meal-coupon.png';
      }
      if (name.indexOf('\u996e\u54c1') !== -1) {
        return '/static/mall/dining-drink-coupon.png';
      }
      if (name.indexOf('\u5706\u73e0\u7b14') !== -1) {
        return '/static/mall/stationery-pen.png';
      }
      if (name.indexOf('\u94c5\u7b14') !== -1) {
        return '/static/mall/stationery-pencil.png';
      }
      if (name.indexOf('\u7eff\u8272\u5148\u950b') !== -1) {
        return '/static/mall/cert-green-pioneer.png';
      }
      if (name.indexOf('\u4f4e\u78b3\u4e4b\u661f') !== -1) {
        return '/static/mall/cert-low-carbon-star.png';
      }

      return '';
    },
    getCategoryIcon(category) {
      if (category === 'DINING') {
        return '\uD83C\uDF5D';
      }
      if (category === 'STATIONERY') {
        return '\uD83D\uDCDD';
      }
      if (category === 'CERT') {
        return '\uD83C\uDFC5';
      }
      return this.defaultBenefitIcon;
    },
    categoryAccentStyle(category) {
      if (category === 'DINING') {
        return 'background: linear-gradient(90deg, #6bc88f, #4dad71);';
      }
      if (category === 'STATIONERY') {
        return 'background: linear-gradient(90deg, #73c5d4, #4fa8bb);';
      }
      if (category === 'CERT') {
        return 'background: linear-gradient(90deg, #ffbe63, #f39c3b);';
      }
      return 'background: linear-gradient(90deg, #b8d9c2, #9cc7aa);';
    },
    categoryBadgeStyle(category) {
      if (category === 'DINING') {
        return 'background: rgba(107, 200, 143, 0.14); color: #2c8551;';
      }
      if (category === 'STATIONERY') {
        return 'background: rgba(115, 197, 212, 0.16); color: #2d7f8d;';
      }
      if (category === 'CERT') {
        return 'background: rgba(255, 190, 99, 0.18); color: #b96e16;';
      }
      return 'background: rgba(156, 199, 170, 0.18); color: #4f7c5f;';
    },
    categoryArrowStyle(category) {
      if (category === 'DINING') {
        return 'color: #43a464;';
      }
      if (category === 'STATIONERY') {
        return 'color: #3f95a7;';
      }
      if (category === 'CERT') {
        return 'color: #df8d2f;';
      }
      return 'color: #6b9b78;';
    },
    cardClass(category) {
      if (category === 'DINING') {
        return 'card-dining';
      }
      if (category === 'STATIONERY') {
        return 'card-library';
      }
      if (category === 'CERT') {
        return 'card-cert';
      }
      return 'card-default';
    },
    statusClass(status) {
      return status && status.indexOf('\u5df2') === 0 ? 'status-ready' : 'status-pending';
    },
    stockText(stock) {
      return '\u5e93\u5b58 ' + (stock || 0);
    },
    selectCategory(code) {
      this.activeCategory = code;
    },
    openBenefit(item) {
      this.activeBenefit = item;
    },
    closeDetail() {
      this.activeBenefit = null;
      this.redeeming = false;
    },
    canRedeem(item) {
      return !!item && !!item.available && this.userPoints >= (item.point || 0);
    },
    redeemButtonText(item) {
      if (!item.available) {
        return this.texts.soldOutAction;
      }
      if (this.userPoints < (item.point || 0)) {
        return this.texts.lackPoints;
      }
      return this.texts.redeemNow;
    },
    confirmRedeem(item) {
      return new Promise((resolve) => {
        uni.showModal({
          title: this.texts.confirmRedeemTitle,
          content: this.texts.confirmRedeemPrefix + item.point + this.texts.confirmRedeemMiddle + this.texts.pointUnit + this.texts.confirmRedeemSuffix + item.name,
          success: (res) => resolve(!!res.confirm),
          fail: () => resolve(false)
        });
      });
    },
    resolveRedeemError(code) {
      if (code === 'RIGHT_SOLD_OUT') {
        return this.texts.redeemFailSoldOut;
      }
      if (code === 'POINTS_NOT_ENOUGH') {
        return this.texts.redeemFailPoints;
      }
      if (code === 'RIGHT_NOT_FOUND') {
        return this.texts.redeemFailMissing;
      }
      return this.texts.redeemFailGeneric;
    },
    async performRedeem(item, shouldCloseDetail) {
      if (!item || this.redeeming) {
        return;
      }

      if (!item.available) {
        uni.showToast({ title: this.texts.soldOutAction, icon: 'none' });
        return;
      }

      if (this.userPoints < (item.point || 0)) {
        uni.showToast({ title: this.texts.lackPoints, icon: 'none' });
        return;
      }

      const confirmed = await this.confirmRedeem(item);
      if (!confirmed) {
        return;
      }

      this.activeBenefit = item;
      this.redeeming = true;
      try {
        const res = await exchangeProduct(this.username, item.id);
        if (res.code === 200) {
          await Promise.all([this.loadProfile(), this.loadBenefits(), this.loadRecords()]);
          await this.showRedeemSuccess(res, item);
          if (shouldCloseDetail) {
            this.closeDetail();
          }
          return;
        }

        uni.showToast({ title: this.resolveRedeemError(res.message), icon: 'none' });
      } catch (e) {
        console.error('redeem failed', e);
        uni.showToast({ title: this.texts.networkError, icon: 'none' });
      } finally {
        this.redeeming = false;
      }
    },
    showRedeemSuccess(res, item) {
      const isGreenCertificate = res.productCode === 'CERT_GREEN_PIONEER';
      const isBadge = res.productCode === 'CERT_LOW_CARBON_STAR';
      const content = isBadge
        ? this.texts.badgeSuccessHint
        : (res.claimMessage || item.exchangeHint);

      return new Promise((resolve) => {
        uni.showModal({
          title: this.texts.redeemSuccessTitle,
          content,
          showCancel: isGreenCertificate,
          confirmText: isGreenCertificate ? this.texts.redeemSuccessView : this.texts.close,
          cancelText: this.texts.redeemSuccessDone,
          success: (modalRes) => {
            if (isGreenCertificate && modalRes.confirm) {
              uni.navigateTo({
                url: '/pages/certificate/certificate?mode=benefit&productCode=CERT_GREEN_PIONEER'
              });
            }
            resolve();
          },
          fail: () => resolve()
        });
      });
    },
    async redeemActiveBenefit() {
      await this.performRedeem(this.activeBenefit, true);
    },
    async handleCardRedeem(item) {
      await this.performRedeem(item, false);
    },
    formatTime(value) {
      if (!value) {
        return '';
      }
      return String(value).replace('T', ' ').slice(0, 16);
    }
  }
};
</script>

<style scoped>
.page {
  min-height: 100vh;
  background: linear-gradient(180deg, #e6f4ea 0%, #cde9d7 100%);
  position: relative;
  overflow: hidden;
}

.gradient-bg {
  position: fixed;
  top: -20%;
  left: -20%;
  right: -20%;
  bottom: -20%;
  background: radial-gradient(circle at 30% 20%, rgba(108, 198, 143, 0.35), transparent 42%),
    radial-gradient(circle at 78% 32%, rgba(250, 206, 126, 0.18), transparent 32%),
    radial-gradient(circle at 55% 80%, rgba(108, 182, 221, 0.18), transparent 30%);
  animation: bgMove 18s ease-in-out infinite;
}

@keyframes bgMove {
  0%, 100% { transform: translate(0, 0) scale(1); }
  50% { transform: translate(2%, 1%) scale(1.04); }
}

.particle-system {
  position: fixed;
  inset: 0;
  pointer-events: none;
}

.particle {
  position: absolute;
  bottom: -40rpx;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(103, 196, 141, 0.8), rgba(103, 196, 141, 0.12));
  animation: floatUp linear infinite;
}

@keyframes floatUp {
  0% {
    transform: translateY(0);
    opacity: 0;
  }
  12% {
    opacity: 1;
  }
  100% {
    transform: translateY(-120vh);
    opacity: 0;
  }
}

.scroller {
  position: relative;
  z-index: 2;
  height: 100vh;
  padding: 0 28rpx;
  box-sizing: border-box;
}

.floating-card {
  animation: floatCard 4s ease-in-out infinite;
}

@keyframes floatCard {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-5rpx); }
}

.hero-card {
  position: relative;
  margin-top: 98rpx;
  padding: 30rpx 28rpx 26rpx;
  border-radius: 42rpx;
  overflow: hidden;
  background: linear-gradient(135deg, #2f7a4a, #1f5d37);
  box-shadow: 0 18rpx 40rpx rgba(30, 80, 48, 0.2);
}

.hero-glow {
  position: absolute;
  border-radius: 50%;
  pointer-events: none;
}

.hero-glow-a {
  width: 360rpx;
  height: 360rpx;
  top: -170rpx;
  right: -110rpx;
  background: radial-gradient(circle, rgba(255, 255, 255, 0.18), transparent 72%);
}

.hero-glow-b {
  width: 280rpx;
  height: 280rpx;
  left: -100rpx;
  bottom: -130rpx;
  background: radial-gradient(circle, rgba(255, 226, 157, 0.18), transparent 72%);
}

.hero-top {
  position: relative;
  z-index: 2;
  display: flex;
  justify-content: space-between;
  gap: 20rpx;
  align-items: flex-start;
}

.hero-title {
  display: block;
  font-size: 40rpx;
  font-weight: 700;
  color: #ffffff;
}

.hero-subtitle {
  display: block;
  margin-top: 8rpx;
  font-size: 22rpx;
  line-height: 1.55;
  color: rgba(255, 255, 255, 0.76);
}

.point-pill {
  min-width: 172rpx;
  padding: 14rpx 22rpx;
  border-radius: 999rpx;
  background: rgba(255, 255, 255, 0.14);
  backdrop-filter: blur(8rpx);
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6rpx;
}

.point-pill-icon {
  font-size: 24rpx;
}

.point-pill-value {
  font-size: 36rpx;
  font-weight: 700;
  color: #ffe7a8;
}

.point-pill-unit {
  font-size: 20rpx;
  color: rgba(255, 255, 255, 0.82);
}

.scene-row {
  position: relative;
  z-index: 2;
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 14rpx;
  margin-top: 24rpx;
}

.scene-card {
  padding: 18rpx 14rpx;
  border-radius: 28rpx;
  text-align: center;
  background: rgba(255, 255, 255, 0.12);
  border: 1rpx solid rgba(255, 255, 255, 0.14);
}

.scene-dining {
  box-shadow: inset 0 0 0 1rpx rgba(255, 192, 120, 0.12);
}

.scene-library {
  box-shadow: inset 0 0 0 1rpx rgba(132, 210, 230, 0.12);
}

.scene-cert {
  box-shadow: inset 0 0 0 1rpx rgba(255, 227, 160, 0.12);
}

.scene-icon {
  display: block;
  font-size: 34rpx;
}

.scene-name {
  display: block;
  margin-top: 10rpx;
  font-size: 22rpx;
  color: rgba(255, 255, 255, 0.76);
}

.scene-count {
  display: block;
  margin-top: 6rpx;
  font-size: 34rpx;
  font-weight: 700;
  color: #ffffff;
}

.section-head {
  margin-top: 28rpx;
  margin-bottom: 16rpx;
}

.section-title {
  display: block;
  font-size: 32rpx;
  font-weight: 700;
  color: #234e33;
}

.section-subtitle {
  display: block;
  margin-top: 8rpx;
  font-size: 22rpx;
  color: #69907a;
}

.record-head {
  margin-top: 30rpx;
}

.chip-row {
  white-space: nowrap;
  margin-bottom: 12rpx;
}

.chip {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  margin-right: 14rpx;
  padding: 14rpx 26rpx;
  border-radius: 999rpx;
  background: rgba(255, 255, 255, 0.9);
  border: 1rpx solid rgba(35, 78, 51, 0.08);
}

.chip.active {
  background: linear-gradient(135deg, #5cb980, #3b9562);
  box-shadow: 0 10rpx 22rpx rgba(59, 149, 98, 0.18);
}

.chip-text {
  font-size: 24rpx;
  color: #3e6550;
}

.chip.active .chip-text {
  color: #ffffff;
}

.benefit-grid {
  display: flex;
  flex-wrap: wrap;
  justify-content: space-between;
}

.benefit-card {
  position: relative;
  overflow: hidden;
  width: 48%;
  min-height: 356rpx;
  border-radius: 34rpx;
  padding: 18rpx 20rpx 22rpx;
  box-sizing: border-box;
  background: rgba(255, 255, 255, 0.82);
  border: 1rpx solid rgba(255, 255, 255, 0.72);
  box-shadow: 0 14rpx 28rpx rgba(42, 83, 58, 0.1);
  backdrop-filter: blur(10rpx);
  margin-bottom: 18rpx;
}

.benefit-card::after {
  content: '';
  position: absolute;
  top: 0;
  right: 0;
  width: 140rpx;
  height: 140rpx;
  background: radial-gradient(circle, rgba(86, 170, 118, 0.12), transparent 72%);
}

.benefit-accent {
  height: 10rpx;
  border-radius: 999rpx;
  margin-bottom: 16rpx;
}

.benefit-top {
  position: relative;
  z-index: 2;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.benefit-category-badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 8rpx 16rpx;
  border-radius: 999rpx;
  font-size: 20rpx;
  font-weight: 600;
}

.benefit-cover-shell {
  position: relative;
  z-index: 2;
  width: 100%;
  height: 136rpx;
  margin-top: 16rpx;
  border-radius: 26rpx;
  background: linear-gradient(135deg, rgba(240, 248, 243, 0.96), rgba(225, 240, 231, 0.78));
  display: flex;
  align-items: center;
  justify-content: center;
}

.benefit-cover {
  width: 120rpx;
  height: 120rpx;
  border-radius: 24rpx;
  background: rgba(255, 255, 255, 0.18);
}

.benefit-icon-wrap {
  width: 88rpx;
  height: 88rpx;
  border-radius: 22rpx;
  background: rgba(92, 185, 128, 0.14);
  display: flex;
  align-items: center;
  justify-content: center;
}

.benefit-icon {
  font-size: 44rpx;
}

.benefit-stock {
  padding: 6rpx 14rpx;
  border-radius: 999rpx;
  background: rgba(46, 125, 80, 0.08);
  font-size: 18rpx;
  color: #5c7d69;
}

.benefit-stock.soldout {
  background: rgba(156, 64, 31, 0.12);
  color: #9b5e41;
}

.benefit-category,
.benefit-name,
.benefit-subtitle,
.benefit-sponsor,
.benefit-bottom {
  position: relative;
  z-index: 2;
}

.benefit-category {
  display: block;
  margin-top: 18rpx;
  font-size: 20rpx;
  color: #5d8770;
}

.benefit-name {
  display: block;
  margin-top: 8rpx;
  font-size: 30rpx;
  font-weight: 700;
  line-height: 1.36;
  color: #234e33;
}

.benefit-subtitle {
  display: block;
  margin-top: 10rpx;
  min-height: 60rpx;
  font-size: 21rpx;
  line-height: 1.45;
  color: #547564;
}

.benefit-sponsor {
  display: block;
  margin-top: 10rpx;
  font-size: 19rpx;
  line-height: 1.4;
  color: #779486;
}

.benefit-bottom {
  margin-top: 16rpx;
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
}

.benefit-actions {
  position: relative;
  z-index: 2;
  display: flex;
  gap: 12rpx;
  margin-top: 18rpx;
}

.benefit-detail-btn,
.benefit-redeem-btn {
  flex: 1;
  height: 64rpx;
  border-radius: 999rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.benefit-detail-btn {
  background: rgba(46, 125, 80, 0.08);
  border: 1rpx solid rgba(46, 125, 80, 0.1);
}

.benefit-redeem-btn {
  background: linear-gradient(135deg, #59b87d, #3c9663);
  box-shadow: 0 10rpx 18rpx rgba(60, 150, 99, 0.18);
}

.benefit-redeem-btn.disabled {
  background: rgba(140, 168, 149, 0.34);
  box-shadow: none;
}

.benefit-detail-text {
  font-size: 22rpx;
  font-weight: 600;
  color: #356148;
}

.benefit-redeem-text {
  font-size: 22rpx;
  font-weight: 700;
  color: #ffffff;
}

.benefit-point {
  font-size: 34rpx;
  font-weight: 700;
  color: #234e33;
}

.benefit-unit {
  margin-left: 4rpx;
  font-size: 20rpx;
  color: #5d8770;
}

.benefit-arrow {
  font-size: 34rpx;
}

.record-list {
  display: flex;
  flex-direction: column;
  gap: 14rpx;
}

.record-card {
  display: flex;
  justify-content: space-between;
  gap: 16rpx;
  align-items: flex-start;
  padding: 18rpx 20rpx;
  border-radius: 28rpx;
  background: rgba(255, 255, 255, 0.88);
  border: 1rpx solid rgba(255, 255, 255, 0.76);
  box-shadow: 0 10rpx 24rpx rgba(53, 94, 70, 0.08);
}

.record-left {
  display: flex;
  gap: 14rpx;
  flex: 1;
}

.record-icon-wrap {
  width: 60rpx;
  height: 60rpx;
  border-radius: 20rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.record-icon {
  font-size: 32rpx;
}

.record-copy {
  flex: 1;
}

.record-name {
  display: block;
  font-size: 26rpx;
  font-weight: 700;
  color: #234e33;
}

.record-next {
  display: block;
  margin-top: 8rpx;
  font-size: 20rpx;
  line-height: 1.45;
  color: #6c8573;
}

.record-time {
  display: block;
  margin-top: 8rpx;
  font-size: 18rpx;
  color: #9aab9f;
}

.record-right {
  text-align: right;
}

.record-point {
  display: block;
  font-size: 28rpx;
  font-weight: 700;
  color: #234e33;
}

.record-status {
  display: inline-block;
  margin-top: 10rpx;
  padding: 6rpx 12rpx;
  border-radius: 999rpx;
  font-size: 18rpx;
}

.status-pending {
  background: rgba(88, 184, 127, 0.12);
  color: #3e9961;
}

.status-ready {
  background: rgba(246, 175, 78, 0.14);
  color: #c97b17;
}

.state-card {
  padding: 40rpx 30rpx;
  border-radius: 30rpx;
  text-align: center;
  background: rgba(255, 255, 255, 0.9);
  box-shadow: 0 10rpx 24rpx rgba(53, 94, 70, 0.08);
}

.state-title {
  display: block;
  font-size: 28rpx;
  font-weight: 700;
  color: #234e33;
}

.state-desc {
  display: block;
  margin-top: 10rpx;
  font-size: 22rpx;
  line-height: 1.5;
  color: #7b9382;
}

.modal-mask {
  position: fixed;
  inset: 0;
  background: rgba(11, 25, 17, 0.52);
  display: flex;
  align-items: flex-end;
  justify-content: center;
  z-index: 20;
}

.modal-card {
  width: 100%;
  padding: 26rpx 24rpx 32rpx;
  border-radius: 34rpx 34rpx 0 0;
  background: #f6fbf7;
  box-sizing: border-box;
}

.modal-head {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 20rpx;
}

.modal-title {
  display: block;
  font-size: 34rpx;
  font-weight: 700;
  color: #214b32;
}

.modal-subtitle {
  display: block;
  margin-top: 8rpx;
  font-size: 22rpx;
  color: #6f8576;
}

.modal-close {
  font-size: 42rpx;
  line-height: 1;
  color: #7a8d80;
}

.modal-highlight {
  margin-top: 20rpx;
  display: flex;
  gap: 16rpx;
  align-items: flex-start;
  padding: 18rpx;
  border-radius: 28rpx;
}

.modal-icon {
  font-size: 46rpx;
}

.modal-cover {
  width: 92rpx;
  height: 92rpx;
  border-radius: 24rpx;
  background: rgba(255, 255, 255, 0.18);
  flex-shrink: 0;
}

.modal-highlight-copy {
  flex: 1;
}

.modal-line {
  display: block;
  font-size: 22rpx;
  line-height: 1.5;
  color: #ffffff;
}

.modal-description {
  display: block;
  margin-top: 20rpx;
  font-size: 24rpx;
  line-height: 1.65;
  color: #476051;
}

.modal-hint-box {
  margin-top: 18rpx;
  padding: 18rpx;
  border-radius: 24rpx;
  background: #eef7f0;
}

.modal-hint-title {
  display: block;
  font-size: 22rpx;
  font-weight: 700;
  color: #275239;
}

.modal-hint-text {
  display: block;
  margin-top: 8rpx;
  font-size: 22rpx;
  line-height: 1.55;
  color: #64816d;
}

.modal-price-row {
  margin-top: 18rpx;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.modal-price {
  font-size: 30rpx;
  font-weight: 700;
  color: #214b32;
}

.modal-stock {
  font-size: 21rpx;
  color: #6f8576;
}

.modal-actions {
  display: flex;
  gap: 16rpx;
  margin-top: 22rpx;
}

.primary-btn,
.secondary-btn {
  flex: 1;
  border-radius: 999rpx;
  font-size: 26rpx;
  line-height: 92rpx;
  padding: 0;
}

.primary-btn {
  background: linear-gradient(135deg, #2f7a4a, #1f5d37);
  color: #ffffff;
}

.primary-btn[disabled] {
  opacity: 0.45;
}

.secondary-btn {
  background: #e8f1ea;
  color: #2c6943;
}

.footer-space {
  height: 140rpx;
}
</style>

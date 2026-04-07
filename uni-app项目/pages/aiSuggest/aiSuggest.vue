<template>
  <view class="page">
    <!-- 动态渐变背景 -->
    <view class="gradient-bg"></view>

    <!-- 动态粒子系统 -->
    <view class="particle-system">
      <view v-for="(item, index) in 50" :key="index" class="particle" :style="particleStyles[index]"></view>
    </view>

    <scroll-view scroll-y="true" class="scroller" :show-scrollbar="false">
      <!-- ===== 模式切换 Tab ===== -->
      <view class="mode-tabs floating-card">
        <view class="tab-item" :class="{ active: currentMode === 'carbon' }" @click="switchMode('carbon')">
          <text class="tab-icon">🌍</text>
          <text class="tab-text">碳足迹建议</text>
        </view>
        <view class="tab-item" :class="{ active: currentMode === 'health' }" @click="switchMode('health')">
          <text class="tab-icon">❤️</text>
          <text class="tab-text">健康建议</text>
        </view>
      </view>

      <!-- ===== 顶部 Banner - 碳足迹模式 ===== -->
      <view class="banner floating-card" v-if="currentMode === 'carbon'">
        <view class="banner-glow"></view>
        <view class="banner-icon-wrapper">
          <view class="icon-outer-ring"></view>
          <view class="icon-middle-ring"></view>
          <view class="icon-inner-ring"></view>
          <view class="banner-icon-box">
            <text class="banner-icon">🤖</text>
          </view>
        </view>
        <view class="banner-text">
          <text class="banner-title">AI 减排建议</text>
          <text class="banner-sub">描述你的日常行为，AI 为你定制减碳方案</text>
        </view>
      </view>

      <!-- ===== 顶部 Banner - 健康模式 ===== -->
      <view class="banner health-banner floating-card" v-else>
        <view class="banner-glow"></view>
        <view class="banner-icon-wrapper">
          <view class="icon-outer-ring"></view>
          <view class="icon-middle-ring"></view>
          <view class="icon-inner-ring"></view>
          <view class="banner-icon-box">
            <text class="banner-icon">🏥</text>
          </view>
        </view>
        <view class="banner-text">
          <text class="banner-title">AI 健康建议</text>
          <text class="banner-sub">基于你的健康数据，AI 为你定制每日健康方案</text>
        </view>
      </view>

      <!-- ===== 输入卡片 - 碳足迹模式 ===== -->
      <view class="card input-card floating-card" v-if="currentMode === 'carbon'">
        <view class="card-header">
          <view class="header-left">
            <view class="header-dot"></view>
            <text class="card-label">🌿 描述你的碳足迹行为</text>
          </view>
          <text class="char-count">{{ carbonFootprint.length }}/200</text>
        </view>

        <!-- 快捷示例 chips - 带动态效果 -->
        <scroll-view scroll-x class="chips-scroll" :show-scrollbar="false">
          <view class="chips-row">
            <view class="chip" v-for="(c, i) in chips" :key="i" @click="appendChip(c)">
              <view class="chip-glow"></view>
              <text class="chip-text">{{ c }}</text>
              <text class="chip-plus">+</text>
            </view>
          </view>
        </scroll-view>

        <!-- 输入框 -->
        <view class="textarea-wrapper" :class="{ focused: isFocused }">
          <textarea
              v-model="carbonFootprint"
              class="textarea"
              placeholder="例如：每天开车上学，使用一次性餐具，不做垃圾分类..."
              placeholder-class="ph"
              :maxlength="200"
              auto-height
              :show-confirm-bar="false"
              @focus="isFocused = true"
              @blur="isFocused = false"
          />
          <view class="textarea-glow"></view>
        </view>

        <!-- 按钮行 -->
        <view class="btn-row">
          <view class="btn-clear" @click="clearInput" v-if="carbonFootprint">
            <text class="btn-clear-text">清空</text>
          </view>
          <view
              class="btn-submit"
              :class="{ loading: isLoading, 'btn-press': isPress }"
              @click="getSuggestion"
              @touchstart="isPress = true"
              @touchend="isPress = false"
          >
            <view class="btn-shine"></view>
            <view class="loading-dots" v-if="isLoading">
              <view class="dot d1"></view>
              <view class="dot d2"></view>
              <view class="dot d3"></view>
            </view>
            <text class="btn-submit-text" v-else>✨ 获取 AI 建议</text>
          </view>
        </view>
      </view>

      <!-- ===== 健康数据卡片 - 健康模式 ===== -->
      <view class="card health-data-card floating-card" v-else>
        <view class="card-header">
          <view class="header-left">
            <view class="header-dot health-dot"></view>
            <text class="card-label">📊 今日健康数据</text>
          </view>
        </view>

        <view class="health-actions">
          <view class="btn-health" @click="getHealthSuggestion" :class="{ loading: isHealthLoading }">
            <view class="loading-dots" v-if="isHealthLoading">
              <view class="dot d1"></view>
              <view class="dot d2"></view>
              <view class="dot d3"></view>
            </view>
            <text class="btn-health-text" v-else>🩺 获取 AI 健康建议</text>
          </view>
          <view class="btn-history" @click="goToHealthPage">
            <text class="btn-history-text">📝 编辑健康数据</text>
          </view>
        </view>
      </view>

      <!-- ===== AI 回复卡片 - 碳足迹模式 ===== -->
      <view class="card result-card floating-card" v-if="currentMode === 'carbon' && suggestion">
        <view class="result-header">
          <view class="result-title-wrap">
            <view class="result-icon-box">
              <text class="result-icon">🤖</text>
            </view>
            <text class="result-title">AI 减排建议</text>
          </view>
          <view class="btn-copy" @click="copyText">
            <text class="copy-text">📋 复制</text>
          </view>
        </view>

        <view class="divider"></view>

        <scroll-view scroll-y class="result-scroll" :show-scrollbar="false">
          <view class="paragraphs">
            <view
                class="para"
                v-for="(p, i) in paragraphs"
                :key="i"
                v-if="p.trim()"
                :style="{ animationDelay: i * 0.1 + 's' }"
            >
              <view class="para-dot"></view>
              <text class="para-text">{{ p.trim() }}</text>
            </view>
          </view>
        </scroll-view>

        <!-- 装饰粒子 -->
        <view class="result-particles">
          <view class="rp rp1">✨</view>
          <view class="rp rp2">⭐</view>
          <view class="rp rp3">💚</view>
        </view>
      </view>

      <!-- ===== AI 健康建议回复卡片 ===== -->
      <view class="card result-card health-result floating-card" v-else-if="currentMode === 'health' && healthSuggestion">
        <view class="result-header">
          <view class="result-title-wrap">
            <view class="result-icon-box health-icon-box">
              <text class="result-icon">🏥</text>
            </view>
            <text class="result-title">AI 健康建议</text>
          </view>
          <view class="btn-copy" @click="copyText">
            <text class="copy-text">📋 复制</text>
          </view>
        </view>

        <view class="divider"></view>

        <scroll-view scroll-y class="result-scroll" :show-scrollbar="false">
          <view class="paragraphs">
            <view
                class="para"
                v-for="(p, i) in paragraphs"
                :key="i"
                v-if="p.trim()"
                :style="{ animationDelay: i * 0.1 + 's' }"
            >
              <view class="para-dot health-para-dot"></view>
              <text class="para-text">{{ p.trim() }}</text>
            </view>
          </view>
        </scroll-view>

        <!-- 装饰粒子 -->
        <view class="result-particles">
          <view class="rp rp1">💪</view>
          <view class="rp rp2">❤️</view>
          <view class="rp rp3">🌿</view>
        </view>
      </view>

      <!-- ===== 空状态提示 - 碳足迹模式 ===== -->
      <view class="empty-tip floating-card" v-if="currentMode === 'carbon' && !suggestion && !isLoading">
        <view class="empty-icon-wrapper">
          <view class="empty-glow"></view>
          <text class="empty-icon">🌍</text>
        </view>
        <text class="empty-text">输入你的日常行为</text>
        <text class="empty-sub">AI 将为你生成个性化的低碳减排建议</text>
      </view>

      <!-- ===== 空状态提示 - 健康模式 ===== -->
      <view class="empty-tip floating-card" v-else-if="currentMode === 'health' && !healthSuggestion && !isHealthLoading">
        <view class="empty-icon-wrapper">
          <view class="empty-glow health-glow"></view>
          <text class="empty-icon">❤️</text>
        </view>
        <text class="empty-text">获取你的专属健康建议</text>
        <text class="empty-sub">基于你的健康数据，AI 将为你生成每日健康方案</text>
      </view>

      <!-- ===== 底部装饰 - 绿色放大三倍 ===== -->
      <view class="footer-deco">
        <text class="deco-text">每一次选择，都是对自己的关爱</text>
      </view>

      <view class="spacer"></view>
    </scroll-view>
  </view>
</template>

<script>
import { getCarbonSuggestion, getHealthSuggestion, getHealthDataList } from '../../utils/request';

export default {
  data() {
    return {
      // 当前模式：'carbon' 或 'health'
      currentMode: 'carbon',
      // 碳足迹模式
      carbonFootprint: '',
      suggestion: '',
      isLoading: false,
      isFocused: false,
      isPress: false,
      chips: ['每天开车上班', '使用一次性餐具', '不做垃圾分类', '长时间开灯', '外卖点餐频繁', '不使用公共交通'],
      // 健康模式
      healthSuggestion: '',
      isHealthLoading: false,
      hasHealthData: false,
      // 公共
      particleStyles: [],
      userId: ''
    };
  },
  computed: {
    paragraphs() {
      return (this.currentMode === 'carbon' ? this.suggestion : this.healthSuggestion).split('\n').filter(p => p.trim());
    }
  },
  onLoad() {
    this.initParticleStyles();
    this.userId = uni.getStorageSync('username') || '';
  },
  onShow() {
    // 每次显示页面时刷新用户ID和健康数据
    this.userId = uni.getStorageSync('username') || '';
    if (this.currentMode === 'health' && this.userId) {
      this.checkHealthData();
    }
  },
  methods: {

    initParticleStyles() {
      const styles = [];
      for (let i = 0; i < 50; i++) {
        styles.push({
          left: Math.random() * 100 + '%',
          animationDuration: (8 + Math.random() * 12) + 's',
          animationDelay: Math.random() * 8 + 's',
          width: (2 + Math.random() * 8) + 'rpx',
          height: (2 + Math.random() * 8) + 'rpx',
          opacity: 0.2 + Math.random() * 0.5
        });
      }
      this.particleStyles = styles;
    },
    // 切换模式
    switchMode(mode) {
      this.currentMode = mode;
      if (mode === 'health' && this.userId) {
        this.checkHealthData();
      }
    },
    // 检查健康数据
    async checkHealthData() {
      if (!this.userId) {
        uni.showToast({ title: '请先登录', icon: 'none' });
        return;
      }
      try {
        const data = await getHealthDataList(this.userId);
        this.hasHealthData = data && data.length > 0;
      } catch (e) {
        this.hasHealthData = false;
      }
    },
    // 获取健康建议
    async getHealthSuggestion() {
      if (this.isHealthLoading) return;
      if (!this.userId) {
        uni.showToast({ title: '请先登录', icon: 'none' });
        return;
      }
      this.isHealthLoading = true;
      this.healthSuggestion = '';
      try {
        const result = await getHealthSuggestion(this.userId);
        this.healthSuggestion = result.suggestion || '';
      } catch (e) {
        uni.showToast({ title: '获取健康建议失败，请重试', icon: 'none' });
      } finally {
        this.isHealthLoading = false;
      }
    },
    // 跳转到健康数据页面
    goToHealthPage() {
      uni.navigateTo({ url: '/pages/healthData/healthData' });
    },
    appendChip(chip) {
      const sep = this.carbonFootprint && !this.carbonFootprint.endsWith('，') && !this.carbonFootprint.endsWith('，') ? '，' : '';
      this.carbonFootprint = (this.carbonFootprint + sep + chip).slice(0, 200);
    },
    clearInput() {
      this.carbonFootprint = '';
      this.suggestion = '';
    },
    async getSuggestion() {
      if (this.isLoading) return;
      if (!this.carbonFootprint.trim()) {
        uni.showToast({ title: '请输入碳足迹情况', icon: 'none' });
        return;
      }
      this.isLoading = true;
      this.suggestion = '';
      try {
        const result = await getCarbonSuggestion(this.carbonFootprint);
        this.suggestion = result.suggestion || '';
      } catch (e) {
        uni.showToast({ title: '获取建议失败，请重试', icon: 'none' });
      } finally {
        this.isLoading = false;
      }
    },
    copyText() {
      const text = this.currentMode === 'carbon' ? this.suggestion : this.healthSuggestion;
      uni.setClipboardData({
        data: text,
        success: () => uni.showToast({ title: '已复制到剪贴板', icon: 'success' })
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

/* 动态渐变背景 */
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

/* 粒子系统 */
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
  background: radial-gradient(circle, rgba(80, 200, 140, 0.6), rgba(60, 160, 100, 0.2));
  border-radius: 50%;
  animation: floatUp linear infinite;
  box-shadow: 0 0 12rpx rgba(80, 200, 140, 0.4);
}

@keyframes floatUp {
  0% {
    transform: translateY(100vh) translateX(0);
    opacity: 0;
  }
  10% {
    opacity: 0.8;
  }
  90% {
    opacity: 0.5;
  }
  100% {
    transform: translateY(-20vh) translateX(40rpx);
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

/* ===== 浮动动画 ===== */
.floating-card {
  animation: floatCard 3s ease-in-out infinite;
}

@keyframes floatCard {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-6rpx); }
}

/* ===== Banner 毛玻璃卡片 ===== */
.banner {
  margin-top: 100rpx;
  margin-bottom: 32rpx;
  padding: 36rpx 32rpx;
  background: linear-gradient(135deg, rgba(80, 200, 140, 0.85), rgba(60, 170, 120, 0.8));
  backdrop-filter: blur(24rpx);
  border-radius: 48rpx;
  display: flex;
  align-items: center;
  gap: 28rpx;
  position: relative;
  overflow: hidden;
  border: 1rpx solid rgba(255, 255, 255, 0.5);
  box-shadow: 0 12rpx 32rpx rgba(0, 0, 0, 0.1);
}

.banner-glow {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: radial-gradient(circle at 30% 40%, rgba(255, 255, 255, 0.3), transparent);
  pointer-events: none;
  animation: glowMove 4s ease-in-out infinite;
}

@keyframes glowMove {
  0%, 100% { opacity: 0.5; transform: scale(1); }
  50% { opacity: 1; transform: scale(1.05); }
}

.banner-icon-wrapper {
  position: relative;
  width: 100rpx;
  height: 100rpx;
  flex-shrink: 0;
}

.icon-outer-ring,
.icon-middle-ring,
.icon-inner-ring {
  position: absolute;
  border-radius: 50%;
  pointer-events: none;
}

.icon-outer-ring {
  top: -10rpx;
  left: -10rpx;
  right: -10rpx;
  bottom: -10rpx;
  border: 2rpx solid rgba(255, 255, 255, 0.5);
  animation: ringRotate 10s linear infinite;
}

.icon-middle-ring {
  top: -5rpx;
  left: -5rpx;
  right: -5rpx;
  bottom: -5rpx;
  border: 2rpx solid rgba(255, 255, 255, 0.4);
  animation: ringRotateReverse 7s linear infinite;
}

.icon-inner-ring {
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  border: 2rpx solid rgba(255, 255, 255, 0.3);
  animation: ringRotate 5s linear infinite;
}

@keyframes ringRotate {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

@keyframes ringRotateReverse {
  from { transform: rotate(0deg); }
  to { transform: rotate(-360deg); }
}

.banner-icon-box {
  width: 100%;
  height: 100%;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 28rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  z-index: 2;
}

.banner-icon {
  font-size: 56rpx;
}

.banner-text {
  flex: 1;
}

.banner-title {
  font-size: 36rpx;
  font-weight: 700;
  color: #FFFFFF;
  display: block;
  text-shadow: 0 2rpx 4rpx rgba(0, 0, 0, 0.1);
}

.banner-sub {
  font-size: 24rpx;
  color: rgba(255, 255, 255, 0.85);
  display: block;
  margin-top: 8rpx;
  line-height: 1.5;
}

/* ===== 卡片通用样式 ===== */
.card {
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(24rpx);
  border-radius: 40rpx;
  padding: 36rpx 32rpx;
  margin-bottom: 28rpx;
  border: 1rpx solid rgba(255, 255, 255, 0.6);
  box-shadow: 0 12rpx 32rpx rgba(0, 0, 0, 0.08);
  position: relative;
  overflow: hidden;
}

/* ===== 输入卡片 ===== */
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 28rpx;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12rpx;
}

.header-dot {
  width: 8rpx;
  height: 32rpx;
  background: linear-gradient(180deg, #6FB88A, #3D9B6D);
  border-radius: 4rpx;
  box-shadow: 0 0 8rpx rgba(61, 155, 109, 0.5);
}

.card-label {
  font-size: 28rpx;
  font-weight: 700;
  color: #2C5A3A;
}

.char-count {
  font-size: 22rpx;
  color: #9CC0AC;
}

/* 快捷chips */
.chips-scroll {
  white-space: nowrap;
  margin-bottom: 28rpx;
}

.chips-row {
  display: flex;
  gap: 16rpx;
  padding: 4rpx 0;
}

.chip {
  display: inline-flex;
  align-items: center;
  gap: 8rpx;
  background: rgba(100, 200, 150, 0.15);
  border: 1rpx solid rgba(100, 200, 150, 0.3);
  border-radius: 60rpx;
  padding: 12rpx 28rpx;
  flex-shrink: 0;
  position: relative;
  overflow: hidden;
  transition: all 0.3s ease;
}

.chip:active {
  transform: scale(0.95);
  background: rgba(100, 200, 150, 0.25);
}

.chip-glow {
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.4), transparent);
  animation: chipShine 3s ease-in-out infinite;
}

@keyframes chipShine {
  0% { left: -100%; }
  60%, 100% { left: 100%; }
}

.chip-text {
  font-size: 24rpx;
  color: #3D9B6D;
  white-space: nowrap;
}

.chip-plus {
  font-size: 20rpx;
  color: #6FB88A;
  font-weight: bold;
}

/* 输入框 */
.textarea-wrapper {
  position: relative;
  margin-bottom: 28rpx;
}

.textarea {
  width: 100%;
  min-height: 200rpx;
  background: rgba(245, 255, 245, 0.9);
  border: 2rpx solid rgba(100, 200, 150, 0.3);
  border-radius: 24rpx;
  padding: 24rpx;
  font-size: 28rpx;
  color: #2C5A3A;
  line-height: 1.7;
  box-sizing: border-box;
  transition: all 0.3s ease;
}

.textarea-wrapper.focused .textarea {
  border-color: #6FB88A;
  box-shadow: 0 0 0 4rpx rgba(111, 184, 138, 0.2);
}

.textarea-glow {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 0;
  background: linear-gradient(90deg, #6FB88A, #3D9B6D);
  border-radius: 2rpx;
  filter: blur(8rpx);
  transition: all 0.3s ease;
  pointer-events: none;
}

.textarea-wrapper.focused .textarea-glow {
  height: 30rpx;
  opacity: 0.3;
}

.ph {
  color: #C8E6C9;
}

/* 按钮行 */
.btn-row {
  display: flex;
  gap: 20rpx;
}

.btn-clear {
  height: 88rpx;
  padding: 0 36rpx;
  background: rgba(200, 230, 210, 0.5);
  border-radius: 44rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
}

.btn-clear:active {
  transform: scale(0.96);
  background: rgba(200, 230, 210, 0.7);
}

.btn-clear-text {
  font-size: 26rpx;
  color: #5B8C6E;
}

.btn-submit {
  flex: 1;
  height: 88rpx;
  background: linear-gradient(135deg, #6FB88A, #3D9B6D);
  border-radius: 44rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
  transition: all 0.3s cubic-bezier(0.2, 0.9, 0.4, 1.1);
  box-shadow: 0 8rpx 24rpx rgba(61, 155, 109, 0.4);
}

.btn-shine {
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.3), transparent);
  animation: btnShine 3s ease-in-out infinite;
}

@keyframes btnShine {
  0% { left: -100%; }
  60%, 100% { left: 100%; }
}

.btn-submit.btn-press {
  transform: scale(0.96);
}

.btn-submit.loading {
  background: linear-gradient(135deg, #86EFAC, #4ADE80);
  box-shadow: none;
}

.btn-submit-text {
  font-size: 28rpx;
  font-weight: 700;
  color: #FFFFFF;
  letter-spacing: 2rpx;
  position: relative;
  z-index: 2;
}

/* Loading 点动画 */
.loading-dots {
  display: flex;
  align-items: center;
  gap: 12rpx;
}

.dot {
  width: 12rpx;
  height: 12rpx;
  background: #FFFFFF;
  border-radius: 50%;
  animation: dotBlink 1.2s ease-in-out infinite;
}

.d1 { animation-delay: 0s; }
.d2 { animation-delay: 0.2s; }
.d3 { animation-delay: 0.4s; }

@keyframes dotBlink {
  0%, 80%, 100% { opacity: 0.3; transform: scale(0.8); }
  40% { opacity: 1; transform: scale(1.2); }
}

/* ===== 结果卡片 ===== */
.result-card {
  position: relative;
  overflow: visible;
}

.result-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24rpx;
}

.result-title-wrap {
  display: flex;
  align-items: center;
  gap: 12rpx;
}

.result-icon-box {
  width: 48rpx;
  height: 48rpx;
  background: rgba(100, 200, 150, 0.2);
  border-radius: 16rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.result-icon {
  font-size: 28rpx;
}

.result-title {
  font-size: 28rpx;
  font-weight: 700;
  color: #2C5A3A;
}

.btn-copy {
  background: rgba(100, 200, 150, 0.15);
  border-radius: 32rpx;
  padding: 10rpx 24rpx;
  transition: all 0.3s ease;
}

.btn-copy:active {
  transform: scale(0.95);
  background: rgba(100, 200, 150, 0.25);
}

.copy-text {
  font-size: 22rpx;
  color: #3D9B6D;
}

.divider {
  height: 1rpx;
  background: linear-gradient(90deg, transparent, #C8E6C9, #6FB88A, #C8E6C9, transparent);
  margin-bottom: 28rpx;
}

.result-scroll {
  max-height: 600rpx;
}

.paragraphs {
  display: flex;
  flex-direction: column;
  gap: 20rpx;
}

.para {
  display: flex;
  gap: 16rpx;
  background: rgba(100, 200, 150, 0.08);
  border-radius: 20rpx;
  padding: 20rpx 24rpx;
  animation: slideIn 0.4s ease-out forwards;
  opacity: 0;
  transform: translateX(-20rpx);
}

@keyframes slideIn {
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

.para-dot {
  width: 8rpx;
  height: 8rpx;
  background: #6FB88A;
  border-radius: 50%;
  margin-top: 12rpx;
  flex-shrink: 0;
  animation: dotPulse 1.5s ease-in-out infinite;
}

@keyframes dotPulse {
  0%, 100% { opacity: 0.5; transform: scale(1); }
  50% { opacity: 1; transform: scale(1.5); }
}

.para-text {
  font-size: 26rpx;
  color: #4A6A5A;
  line-height: 1.8;
  flex: 1;
}

/* 结果卡片装饰粒子 */
.result-particles {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  pointer-events: none;
  overflow: hidden;
}

.rp {
  position: absolute;
  font-size: 20rpx;
  animation: particleFloat 3s ease-in-out infinite;
}

.rp1 { top: 20rpx; right: 30rpx; animation-delay: 0s; }
.rp2 { bottom: 40rpx; left: 20rpx; animation-delay: 1s; }
.rp3 { top: 50%; right: 50rpx; animation-delay: 2s; }

@keyframes particleFloat {
  0%, 100% { transform: translateY(0) rotate(0deg); opacity: 0; }
  50% { transform: translateY(-30rpx) rotate(180deg); opacity: 0.5; }
}

/* ===== 空状态 ===== */
.empty-tip {
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(24rpx);
  border-radius: 48rpx;
  padding: 60rpx 40rpx;
  margin-bottom: 28rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16rpx;
  border: 1rpx solid rgba(255, 255, 255, 0.6);
}

.empty-icon-wrapper {
  position: relative;
  width: 120rpx;
  height: 120rpx;
  margin-bottom: 16rpx;
}

.empty-glow {
  position: absolute;
  top: -10rpx;
  left: -10rpx;
  right: -10rpx;
  bottom: -10rpx;
  background: radial-gradient(circle, rgba(100, 200, 150, 0.4), transparent);
  border-radius: 50%;
  animation: glowPulse 2s ease-in-out infinite;
}

@keyframes glowPulse {
  0%, 100% { opacity: 0.5; transform: scale(1); }
  50% { opacity: 1; transform: scale(1.2); }
}

.empty-icon {
  font-size: 80rpx;
  position: relative;
  z-index: 2;
  display: block;
  text-align: center;
  line-height: 120rpx;
}

.empty-text {
  font-size: 28rpx;
  font-weight: 600;
  color: #2C5A3A;
  text-align: center;
}

.empty-sub {
  font-size: 24rpx;
  color: #9CC0AC;
  text-align: center;
}

/* ===== 底部装饰 - 绿色放大三倍 ===== */
.footer-deco {
  text-align: center;
  padding: 48rpx 0 28rpx;
}

.deco-text {
  font-size: 30rpx;
  font-weight: 600;
  background: linear-gradient(135deg, #3D9B6D, #6FB88A, #3D9B6D);
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
  letter-spacing: 4rpx;
  animation: textPulse 2s ease-in-out infinite;
  display: inline-block;
  text-shadow: 0 0 30rpx rgba(61, 155, 109, 0.3);
}

@keyframes textPulse {
  0%, 100% {
    opacity: 0.9;
    transform: scale(1);
  }
  50% {
    opacity: 1;
    transform: scale(1.02);
    text-shadow: 0 0 40rpx rgba(61, 155, 109, 0.6);
  }
}

.spacer {
  height: 60rpx;
}

/* ===== 模式切换 Tabs ===== */
.mode-tabs {
  margin-top: 100rpx;
  margin-bottom: 28rpx;
  display: flex;
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(24rpx);
  border-radius: 32rpx;
  padding: 8rpx;
  border: 1rpx solid rgba(255, 255, 255, 0.6);
  box-shadow: 0 8rpx 24rpx rgba(0, 0, 0, 0.06);
}

.tab-item {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12rpx;
  padding: 20rpx 0;
  border-radius: 24rpx;
  transition: all 0.3s ease;
}

.tab-item.active {
  background: linear-gradient(135deg, #6FB88A, #3D9B6D);
  box-shadow: 0 4rpx 16rpx rgba(61, 155, 109, 0.3);
}

.tab-icon {
  font-size: 32rpx;
}

.tab-text {
  font-size: 28rpx;
  font-weight: 600;
  color: #666;
}

.tab-item.active .tab-text {
  color: #fff;
}

/* ===== 健康模式 Banner ===== */
.health-banner {
  background: linear-gradient(135deg, rgba(220, 80, 80, 0.85), rgba(200, 60, 60, 0.8)) !important;
}

/* ===== 健康数据卡片 ===== */
.health-data-card {
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(24rpx);
  border-radius: 40rpx;
  padding: 36rpx 32rpx;
  margin-bottom: 28rpx;
  border: 1rpx solid rgba(255, 255, 255, 0.6);
  box-shadow: 0 12rpx 32rpx rgba(0, 0, 0, 0.08);
  position: relative;
  overflow: hidden;
}

.health-dot {
  background: linear-gradient(180deg, #E85D5D, #C83C3C) !important;
  box-shadow: 0 0 8rpx rgba(200, 60, 60, 0.5) !important;
}

.health-actions {
  display: flex;
  flex-direction: column;
  gap: 20rpx;
}

.btn-health {
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #E85D5D, #C83C3C);
  border-radius: 32rpx;
  padding: 28rpx 0;
  position: relative;
  overflow: hidden;
  box-shadow: 0 8rpx 24rpx rgba(200, 60, 60, 0.3);
  transition: all 0.3s ease;
}

.btn-health:active {
  transform: scale(0.98);
}

.btn-health-text {
  font-size: 30rpx;
  font-weight: 700;
  color: #fff;
  letter-spacing: 2rpx;
}

.btn-history {
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(100, 200, 150, 0.15);
  border: 1rpx solid rgba(100, 200, 150, 0.3);
  border-radius: 32rpx;
  padding: 24rpx 0;
  transition: all 0.3s ease;
}

.btn-history:active {
  transform: scale(0.98);
  background: rgba(100, 200, 150, 0.25);
}

.btn-history-text {
  font-size: 28rpx;
  font-weight: 600;
  color: #3D9B6D;
}

/* ===== 健康结果卡片 ===== */
.health-result {
  border-color: rgba(220, 80, 80, 0.2) !important;
}

.health-icon-box {
  background: rgba(220, 80, 80, 0.1) !important;
}

.health-para-dot {
  background: #E85D5D !important;
}

.health-glow {
  background: radial-gradient(circle, rgba(220, 80, 80, 0.4), transparent) !important;
}
</style>
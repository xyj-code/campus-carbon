<template>
  <view class="container">

    <!-- 顶部 Banner -->
    <view class="banner">
      <view class="banner-icon-wrap">
        <text class="banner-icon">🤖</text>
      </view>
      <view class="banner-text">
        <text class="banner-title">AI 减排建议</text>
        <text class="banner-sub">描述你的日常行为，AI 为你定制减碳方案</text>
      </view>
    </view>

    <!-- 输入卡片 -->
    <view class="card input-card">
      <view class="card-header">
        <text class="card-label">🌿 描述你的碳足迹行为</text>
        <text class="char-count">{{ carbonFootprint.length }}/200</text>
      </view>

      <!-- 快捷示例 chips -->
      <scroll-view scroll-x class="chips-scroll">
        <view class="chips-row">
          <view class="chip" v-for="(c, i) in chips" :key="i" @click="appendChip(c)">
            <text class="chip-text">{{ c }}</text>
          </view>
        </view>
      </scroll-view>

      <textarea
        v-model="carbonFootprint"
        class="textarea"
        :class="{ focused: isFocused }"
        placeholder="例如：每天开车上学，使用一次性餐具，不做垃圾分类..."
        placeholder-class="ph"
        :maxlength="200"
        auto-height
        :show-confirm-bar="false"
        @focus="isFocused = true"
        @blur="isFocused = false"
      />

      <view class="btn-row">
        <view class="btn-clear" @click="clearInput" v-if="carbonFootprint">
          <text class="btn-clear-text">清空输入</text>
        </view>
        <view
          class="btn-submit"
          :class="{ loading: isLoading, 'btn-press': isPress }"
          @click="getSuggestion"
          @touchstart="isPress = true"
          @touchend="isPress = false"
        >
          <view class="loading-dots" v-if="isLoading">
            <text class="dot d1">●</text>
            <text class="dot d2">●</text>
            <text class="dot d3">●</text>
          </view>
          <text class="btn-submit-text" v-else>✨ 获取 AI 建议</text>
        </view>
      </view>
    </view>

    <!-- AI 回复卡片 -->
    <view class="card result-card" v-if="suggestion">
      <view class="result-header">
        <view class="result-title-wrap">
          <text class="result-icon">🤖</text>
          <text class="result-title">AI 减排建议</text>
        </view>
        <view class="btn-copy" @click="copyText">
          <text class="copy-text">📋 复制</text>
        </view>
      </view>

      <view class="divider"></view>

      <scroll-view scroll-y class="result-scroll">
        <view class="paragraphs">
          <view
            class="para"
            v-for="(p, i) in paragraphs"
            :key="i"
            v-if="p.trim()"
          >
            <text class="para-text">{{ p.trim() }}</text>
          </view>
        </view>
      </scroll-view>
    </view>

    <!-- 空状态提示 -->
    <view class="empty-tip" v-if="!suggestion && !isLoading">
      <text class="empty-icon">🌍</text>
      <text class="empty-text">输入你的日常行为，AI 将为你</text>
      <text class="empty-text">生成个性化的低碳减排建议</text>
    </view>

  </view>
</template>

<script>
import { getCarbonSuggestion } from '../../utils/request';

export default {
  data() {
    return {
      carbonFootprint: '',
      suggestion: '',
      isLoading: false,
      isFocused: false,
      isPress: false,
      chips: ['每天开车上班', '使用一次性餐具', '不做垃圾分类', '长时间开灯', '外卖点餐频繁', '不使用公共交通', '空调常年不关', '频繁购买快时尚']
    };
  },
  computed: {
    paragraphs() {
      return this.suggestion.split('\n').filter(p => p.trim());
    }
  },
  methods: {
    appendChip(chip) {
      const sep = this.carbonFootprint && !this.carbonFootprint.endsWith('，') ? '，' : '';
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
      uni.setClipboardData({
        data: this.suggestion,
        success: () => uni.showToast({ title: '已复制到剪贴板', icon: 'success' })
      });
    }
  }
};
</script>

<style scoped>
/* ---- 整体 ---- */
.container {
  min-height: 100vh;
  background: linear-gradient(180deg, #f0f9f0 0%, #e6f7e6 100%);
  padding: 0 0 60rpx;
}

/* ---- Banner ---- */
.banner {
  background: linear-gradient(135deg, #2e7d32, #43a047 60%, #66bb6a);
  padding: 60rpx 40rpx 48rpx;
  border-radius: 0 0 40rpx 40rpx;
  display: flex;
  align-items: center;
  gap: 30rpx;
  margin-bottom: 32rpx;
}

.banner-icon-wrap {
  width: 100rpx;
  height: 100rpx;
  border-radius: 24rpx;
  background: rgba(255,255,255,0.2);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.banner-icon { font-size: 54rpx; }

.banner-text {
  display: flex;
  flex-direction: column;
  gap: 8rpx;
}

.banner-title {
  font-size: 36rpx;
  font-weight: bold;
  color: #fff;
}

.banner-sub {
  font-size: 22rpx;
  color: rgba(255,255,255,0.8);
  line-height: 1.5;
}

/* ---- 卡片通用 ---- */
.card {
  margin: 0 28rpx 28rpx;
  background: #fff;
  border-radius: 28rpx;
  padding: 36rpx 32rpx;
  box-shadow: 0 4rpx 24rpx rgba(46,125,50,0.10);
}

/* ---- 输入卡片 ---- */
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24rpx;
}

.card-label {
  font-size: 28rpx;
  font-weight: bold;
  color: #2e7d32;
}

.char-count {
  font-size: 22rpx;
  color: #bbb;
}

/* 快捷chips */
.chips-scroll {
  white-space: nowrap;
  margin-bottom: 24rpx;
}

.chips-row {
  display: flex;
  gap: 16rpx;
  padding: 4rpx 0;
}

.chip {
  display: inline-flex;
  align-items: center;
  background: #f0faf0;
  border: 1rpx solid #a5d6a7;
  border-radius: 40rpx;
  padding: 10rpx 24rpx;
  flex-shrink: 0;
}

.chip-text {
  font-size: 22rpx;
  color: #2e7d32;
  white-space: nowrap;
}

/* 输入框 */
.textarea {
  width: 100%;
  min-height: 200rpx;
  border: 2rpx solid #e0e0e0;
  border-radius: 16rpx;
  padding: 24rpx;
  font-size: 26rpx;
  color: #333;
  line-height: 1.7;
  box-sizing: border-box;
  transition: border-color 0.2s;
  background: #fafff8;
}

.textarea.focused {
  border-color: #43a047;
  box-shadow: 0 0 0 4rpx rgba(67,160,71,0.12);
}

.ph { color: #ccc; }

/* 按钮行 */
.btn-row {
  display: flex;
  gap: 20rpx;
  margin-top: 28rpx;
}

.btn-clear {
  height: 88rpx;
  padding: 0 36rpx;
  border: 2rpx solid #e0e0e0;
  border-radius: 44rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.btn-clear-text {
  font-size: 26rpx;
  color: #999;
}

.btn-submit {
  flex: 1;
  height: 88rpx;
  background: linear-gradient(90deg, #22c55e, #16a34a);
  border-radius: 44rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 6rpx 24rpx rgba(34,197,94,0.35);
  transition: transform 0.15s;
}

.btn-submit.btn-press { transform: scale(0.96); }

.btn-submit.loading {
  background: linear-gradient(90deg, #86efac, #4ade80);
  box-shadow: none;
}

.btn-submit-text {
  font-size: 28rpx;
  font-weight: bold;
  color: #fff;
  letter-spacing: 2rpx;
}

/* Loading 点动画 */
.loading-dots {
  display: flex;
  align-items: center;
  gap: 10rpx;
}

.dot {
  font-size: 20rpx;
  color: #fff;
  animation: blink 1.2s infinite;
}

.d1 { animation-delay: 0s; }
.d2 { animation-delay: 0.3s; }
.d3 { animation-delay: 0.6s; }

@keyframes blink {
  0%, 80%, 100% { opacity: 0.2; }
  40% { opacity: 1; }
}

/* ---- 结果卡片 ---- */
.result-card {
  padding: 32rpx 32rpx 28rpx;
}

.result-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20rpx;
}

.result-title-wrap {
  display: flex;
  align-items: center;
  gap: 12rpx;
}

.result-icon { font-size: 32rpx; }

.result-title {
  font-size: 28rpx;
  font-weight: bold;
  color: #2e7d32;
}

.btn-copy {
  background: #f0faf0;
  border: 1rpx solid #a5d6a7;
  border-radius: 32rpx;
  padding: 10rpx 24rpx;
}

.copy-text {
  font-size: 22rpx;
  color: #43a047;
}

.divider {
  height: 1rpx;
  background: #f0f0f0;
  margin-bottom: 24rpx;
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
  background: #f9fef9;
  border-left: 6rpx solid #a5d6a7;
  border-radius: 0 12rpx 12rpx 0;
  padding: 20rpx 20rpx 20rpx 24rpx;
}

.para-text {
  font-size: 26rpx;
  color: #444;
  line-height: 1.8;
}

/* ---- 空状态 ---- */
.empty-tip {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 60rpx 0 0;
  gap: 12rpx;
}

.empty-icon {
  font-size: 80rpx;
  margin-bottom: 8rpx;
}

.empty-text {
  font-size: 26rpx;
  color: #aaa;
  text-align: center;
}
</style>

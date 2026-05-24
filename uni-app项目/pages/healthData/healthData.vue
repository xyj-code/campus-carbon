<template>
  <view class="page">
    <!-- 动态渐变背景 -->
    <view class="gradient-bg"></view>

    <!-- 动态粒子系统 -->
    <view class="particle-system">
      <view v-for="(item, index) in 40" :key="index" class="particle" :style="particleStyles[index]"></view>
    </view>

    <scroll-view scroll-y="true" class="scroller" :show-scrollbar="false">
      <!-- 顶部标题 -->
      <view class="page-header floating-card">
        <view class="header-icon-wrapper">
          <view class="header-icon-ring"></view>
          <text class="header-icon">❤️</text>
        </view>
        <text class="header-title">健康统计</text>
        <view class="header-icon-wrapper">
          <view class="header-icon-ring"></view>
          <text class="header-icon">🍃</text>
        </view>
      </view>

      <!-- BMI展示卡片 -->
      <view class="bmi-card floating-card" v-if="latestRecord && latestRecord.bmi">
        <view class="bmi-header">
          <text class="bmi-title">当前BMI指数</text>
          <text class="bmi-value neon">{{ latestRecord.bmi }}</text>
          <text class="bmi-status">{{ bmiStatus }}</text>
        </view>
        <view class="bmi-bar">
          <view class="bmi-fill" :style="{ width: bmiPercent + '%' }"></view>
        </view>
        <view class="bmi-labels">
          <text class="bmi-label">偏瘦</text>
          <text class="bmi-label">正常</text>
          <text class="bmi-label">偏胖</text>
          <text class="bmi-label">肥胖</text>
        </view>
      </view>

      <!-- 添加健康数据卡片 -->
      <view class="card floating-card">
        <view class="card-header">
          <view class="header-dot"></view>
          <text class="card-title">📝 记录健康数据</text>
        </view>

        <!-- 记录日期 -->
        <view class="label">
          <text class="label-icon">📅</text>
          <text class="label-text">记录日期</text>
        </view>
        <picker mode="date" :value="recordDate" @change="onDateChange">
          <view class="picker-item">
            <text class="picker-value">{{ recordDate }}</text>
            <text class="picker-arrow">▼</text>
          </view>
        </picker>

        <!-- 身高 -->
        <view class="label">
          <text class="label-icon">📏</text>
          <text class="label-text">身高</text>
        </view>
        <view class="input-wrapper">
          <input
              v-model="height"
              class="input"
              type="digit"
              placeholder="请输入身高"
              placeholder-class="input-placeholder"
          />
          <text class="input-unit">cm</text>
          <view class="input-glow"></view>
        </view>

        <!-- 体重 -->
        <view class="label">
          <text class="label-icon">⚖️</text>
          <text class="label-text">体重</text>
        </view>
        <view class="input-wrapper">
          <input
              v-model="weight"
              class="input"
              type="digit"
              placeholder="请输入体重"
              placeholder-class="input-placeholder"
          />
          <text class="input-unit">kg</text>
          <view class="input-glow"></view>
        </view>

        <!-- 血压 -->
        <view class="label">
          <text class="label-icon">💓</text>
          <text class="label-text">血压</text>
        </view>
        <view class="input-wrapper">
          <input
              v-model="bloodPressure"
              class="input"
              type="text"
              placeholder="如：120/80"
              placeholder-class="input-placeholder"
          />
          <text class="input-unit">mmHg</text>
          <view class="input-glow"></view>
        </view>

        <!-- 心率 -->
        <view class="label">
          <text class="label-icon">💗</text>
          <text class="label-text">心率</text>
        </view>
        <view class="input-wrapper">
          <input
              v-model="heartRate"
              class="input"
              type="number"
              placeholder="请输入心率"
              placeholder-class="input-placeholder"
          />
          <text class="input-unit">次/分</text>
          <view class="input-glow"></view>
        </view>

        <!-- 血糖 -->
        <view class="label">
          <text class="label-icon">🩸</text>
          <text class="label-text">血糖</text>
        </view>
        <view class="input-wrapper">
          <input
              v-model="bloodSugar"
              class="input"
              type="digit"
              placeholder="请输入血糖值"
              placeholder-class="input-placeholder"
          />
          <text class="input-unit">mmol/L</text>
          <view class="input-glow"></view>
        </view>

        <!-- 备注 -->
        <view class="label">
          <text class="label-icon">📝</text>
          <text class="label-text">备注</text>
        </view>
        <textarea
            v-model="remark"
            class="textarea"
            placeholder="可选填写备注信息"
            maxlength="200"
        ></textarea>

        <!-- 提交按钮 -->
        <view class="btn-submit" @click="submitRecord" :class="{ loading: submitting }">
          <view class="btn-shine"></view>
          <text class="btn-text" v-if="!submitting">📤 提交记录</text>
          <view class="loading-dots" v-else>
            <view class="dot"></view>
            <view class="dot"></view>
            <view class="dot"></view>
          </view>
        </view>
      </view>

      <!-- 健康记录列表 -->
      <view class="card floating-card" v-if="recordList.length > 0">
        <view class="card-header">
          <view class="header-dot"></view>
          <text class="card-title">📋 历史记录</text>
          <text class="card-count">{{ recordList.length }}条</text>
        </view>

        <view class="record-list">
          <view class="record-item" v-for="(item, index) in recordList" :key="index">
            <view class="record-main">
              <view class="record-top">
                <text class="record-date">{{ item.recordDate }}</text>
                <text class="record-bmi" v-if="item.bmi">BMI: {{ item.bmi }}</text>
              </view>
              <view class="record-details">
                <text class="detail" v-if="item.height">身高: {{ item.height }}cm</text>
                <text class="detail" v-if="item.weight">体重: {{ item.weight }}kg</text>
                <text class="detail" v-if="item.bloodPressure">血压: {{ item.bloodPressure }}</text>
                <text class="detail" v-if="item.heartRate">心率: {{ item.heartRate }}次/分</text>
                <text class="detail" v-if="item.bloodSugar">血糖: {{ item.bloodSugar }}mmol/L</text>
              </view>
              <text class="record-remark" v-if="item.remark">备注: {{ item.remark }}</text>
            </view>
            <view class="record-delete" @click="deleteRecord(item.id)">🗑️</view>
          </view>
        </view>
      </view>

      <!-- 空状态 -->
      <view class="empty-card floating-card" v-else>
        <view class="empty-icon-wrapper">
          <view class="empty-glow"></view>
          <text class="empty-icon">❤️</text>
        </view>
        <text class="empty-title">暂无健康记录</text>
        <text class="empty-sub">开始记录你的健康指标吧</text>
      </view>

      <!-- 底部装饰 -->
      <view class="footer-deco">
        <text class="deco-text">💪 关注健康数据 · 享受低碳生活 🌍</text>
      </view>

      <view class="spacer"></view>
    </scroll-view>
  </view>
</template>

<script>
import { getHealthDataList, saveHealthData, deleteHealthData } from '../../utils/request.js';

export default {
  data() {
    return {
      recordDate: new Date().toISOString().slice(0, 10),
      height: '',
      weight: '',
      bloodPressure: '',
      heartRate: '',
      bloodSugar: '',
      remark: '',
      recordList: [],
      submitting: false,
      userId: '',
      particleStyles: []
    };
  },
  computed: {
    latestRecord() {
      return this.recordList.length > 0 ? this.recordList[0] : null;
    },
    bmiStatus() {
      if (!this.latestRecord || !this.latestRecord.bmi) return '';
      const bmi = parseFloat(this.latestRecord.bmi);
      if (bmi < 18.5) return '偏瘦';
      if (bmi < 24) return '正常';
      if (bmi < 28) return '偏胖';
      return '肥胖';
    },
    bmiPercent() {
      if (!this.latestRecord || !this.latestRecord.bmi) return 0;
      const bmi = parseFloat(this.latestRecord.bmi);
      // BMI范围 15-35 映射到 0-100%
      const percent = ((bmi - 15) / 20) * 100;
      return Math.max(0, Math.min(100, percent));
    }
  },
  onLoad() {
    const username = uni.getStorageSync('username');
    if (!username) {
      uni.reLaunch({ url: '/pages/login/login' });
      return;
    }
    this.userId = username;
    this.initParticleStyles();
    this.loadRecords();
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
    onDateChange(e) {
      this.recordDate = e.detail.value;
    },
    async submitRecord() {
      if (!this.height && !this.weight) {
        uni.showToast({ title: '请至少填写身高或体重', icon: 'none' });
        return;
      }

      this.submitting = true;
      try {
        const data = {
          userId: this.userId,
          recordDate: this.recordDate,
          height: this.height ? this.height : null,
          weight: this.weight ? this.weight : null,
          bloodPressure: this.bloodPressure || null,
          heartRate: this.heartRate ? this.heartRate : null,
          bloodSugar: this.bloodSugar ? this.bloodSugar : null,
          remark: this.remark || null
        };

        console.log('提交健康数据:', data); // 调试日志

        const result = await saveHealthData(data);
        console.log('保存结果:', result); // 调试日志
        
        uni.showToast({ title: '保存成功', icon: 'success' });

        // 清空表单
        this.height = '';
        this.weight = '';
        this.bloodPressure = '';
        this.heartRate = '';
        this.bloodSugar = '';
        this.remark = '';

        this.loadRecords();
      } catch (err) {
        console.error('保存失败:', err); // 调试日志
        uni.showToast({ title: '保存失败', icon: 'none' });
      } finally {
        this.submitting = false;
      }
    },
    async loadRecords() {
      try {
        const res = await getHealthDataList(this.userId);
        this.recordList = res || [];
      } catch (err) {
        console.error('加载健康数据失败:', err);
      }
    },
    async deleteRecord(id) {
      uni.showModal({
        title: '确认删除',
        content: '确定要删除这条健康记录吗？',
        success: async (res) => {
          if (res.confirm) {
            try {
              await deleteHealthData(id);
              uni.showToast({ title: '删除成功', icon: 'success' });
              this.loadRecords();
            } catch (err) {
              uni.showToast({ title: '删除失败', icon: 'none' });
            }
          }
        }
      });
    }
  }
};
</script>

<style scoped>
.page {
  min-height: 100vh;
  background: linear-gradient(135deg, #e8f5e9 0%, #c8e6c9 50%, #a5d6a7 100%);
  padding: 24rpx;
  box-sizing: border-box;
  position: relative;
  overflow: hidden;
}

.gradient-bg {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #e8f5e9 0%, #c8e6c9 50%, #a5d6a7 100%);
  z-index: 0;
}

.particle-system {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 1;
}

.particle {
  position: absolute;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(76, 175, 80, 0.6) 0%, transparent 70%);
  animation: float 10s infinite ease-in-out;
}

@keyframes float {
  0%, 100% { transform: translateY(100vh) scale(0); opacity: 0; }
  10% { opacity: 1; }
  90% { opacity: 1; }
  100% { transform: translateY(-10vh) scale(1); opacity: 0; }
}

.scroller {
  position: relative;
  z-index: 2;
  height: calc(100vh - 48rpx);
}

/* 顶部标题 */
.page-header {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 24rpx;
  padding: 32rpx;
  margin-bottom: 24rpx;
  background: linear-gradient(135deg, #ffffff 0%, #f1f8e9 100%);
  border-radius: 24rpx;
  box-shadow: 0 8rpx 32rpx rgba(46, 125, 50, 0.15);
}

.header-icon-wrapper {
  position: relative;
  width: 64rpx;
  height: 64rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.header-icon-ring {
  position: absolute;
  width: 100%;
  height: 100%;
  border-radius: 50%;
  border: 2rpx solid rgba(76, 175, 80, 0.3);
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0%, 100% { transform: scale(1); opacity: 1; }
  50% { transform: scale(1.2); opacity: 0.5; }
}

.header-icon {
  font-size: 40rpx;
  z-index: 1;
}

.header-title {
  font-size: 36rpx;
  font-weight: bold;
  color: #2e7d32;
}

/* BMI展示卡片 */
.bmi-card {
  padding: 32rpx;
  margin-bottom: 24rpx;
  background: linear-gradient(135deg, #ffffff 0%, #f1f8e9 100%);
  border-radius: 24rpx;
  box-shadow: 0 8rpx 32rpx rgba(46, 125, 50, 0.15);
}

.bmi-header {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 24rpx;
}

.bmi-title {
  font-size: 28rpx;
  color: #666;
  margin-bottom: 12rpx;
}

.bmi-value {
  font-size: 64rpx;
  font-weight: bold;
  color: #2e7d32;
  text-shadow: 0 0 20rpx rgba(46, 125, 50, 0.3);
}

.bmi-status {
  font-size: 24rpx;
  color: #4caf50;
  margin-top: 8rpx;
}

.bmi-bar {
  height: 16rpx;
  background: #e0e0e0;
  border-radius: 8rpx;
  overflow: hidden;
  position: relative;
}

.bmi-fill {
  height: 100%;
  background: linear-gradient(90deg, #4caf50 0%, #8bc34a 50%, #ff9800 75%, #f44336 100%);
  border-radius: 8rpx;
  transition: width 0.5s ease;
}

.bmi-labels {
  display: flex;
  justify-content: space-between;
  margin-top: 12rpx;
}

.bmi-label {
  font-size: 20rpx;
  color: #999;
}

/* 表单卡片 */
.card {
  padding: 32rpx;
  margin-bottom: 24rpx;
  background: linear-gradient(135deg, #ffffff 0%, #f1f8e9 100%);
  border-radius: 24rpx;
  box-shadow: 0 8rpx 32rpx rgba(46, 125, 50, 0.15);
}

.card-header {
  display: flex;
  align-items: center;
  gap: 12rpx;
  margin-bottom: 24rpx;
  padding-bottom: 16rpx;
  border-bottom: 2rpx solid rgba(76, 175, 80, 0.2);
}

.header-dot {
  width: 12rpx;
  height: 12rpx;
  background: #4caf50;
  border-radius: 50%;
  animation: blink 1.5s infinite;
}

@keyframes blink {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.3; }
}

.card-title {
  font-size: 30rpx;
  font-weight: bold;
  color: #2e7d32;
}

.card-count {
  margin-left: auto;
  font-size: 24rpx;
  color: #999;
}

.label {
  display: flex;
  align-items: center;
  gap: 8rpx;
  margin-bottom: 12rpx;
  margin-top: 20rpx;
}

.label-icon {
  font-size: 28rpx;
}

.label-text {
  font-size: 26rpx;
  color: #666;
  font-weight: 500;
}

.picker-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20rpx 24rpx;
  background: #f5f5f5;
  border-radius: 16rpx;
  border: 2rpx solid transparent;
  transition: all 0.3s ease;
}

.picker-item:active {
  border-color: #4caf50;
  background: #e8f5e9;
}

.picker-value {
  font-size: 28rpx;
  color: #333;
}

.picker-arrow {
  font-size: 20rpx;
  color: #999;
}

.input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
  padding: 20rpx 24rpx;
  background: #f5f5f5;
  border-radius: 16rpx;
  border: 2rpx solid transparent;
  transition: all 0.3s ease;
}

.input-wrapper:focus-within {
  border-color: #4caf50;
  background: #e8f5e9;
}

.input {
  flex: 1;
  font-size: 28rpx;
  color: #333;
  background: transparent;
}

.input-placeholder {
  color: #bbb;
}

.input-unit {
  font-size: 24rpx;
  color: #999;
  margin-left: 12rpx;
}

.input-glow {
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 0;
  height: 4rpx;
  background: linear-gradient(90deg, transparent, #4caf50, transparent);
  transition: width 0.3s ease;
}

.input-wrapper:focus-within .input-glow {
  width: 80%;
}

.textarea {
  width: 100%;
  min-height: 120rpx;
  padding: 20rpx 24rpx;
  background: #f5f5f5;
  border-radius: 16rpx;
  font-size: 28rpx;
  color: #333;
  box-sizing: border-box;
}

.btn-submit {
  position: relative;
  overflow: hidden;
  margin-top: 32rpx;
  padding: 24rpx;
  background: linear-gradient(135deg, #4caf50 0%, #2e7d32 100%);
  border-radius: 16rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 8rpx 24rpx rgba(76, 175, 80, 0.3);
}

.btn-shine {
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  animation: shine 2s infinite;
}

@keyframes shine {
  0% { left: -100%; }
  100% { left: 100%; }
}

.btn-text {
  font-size: 30rpx;
  color: #fff;
  font-weight: bold;
  z-index: 1;
}

.loading-dots {
  display: flex;
  gap: 12rpx;
}

.loading-dots .dot {
  width: 12rpx;
  height: 12rpx;
  background: #fff;
  border-radius: 50%;
  animation: bounce 1.4s infinite;
}

.loading-dots .dot:nth-child(2) { animation-delay: 0.2s; }
.loading-dots .dot:nth-child(3) { animation-delay: 0.4s; }

@keyframes bounce {
  0%, 80%, 100% { transform: scale(0); }
  40% { transform: scale(1); }
}

/* 记录列表 */
.record-list {
  display: flex;
  flex-direction: column;
  gap: 16rpx;
}

.record-item {
  display: flex;
  align-items: flex-start;
  gap: 16rpx;
  padding: 20rpx;
  background: #f9f9f9;
  border-radius: 16rpx;
  border-left: 6rpx solid #4caf50;
}

.record-main {
  flex: 1;
}

.record-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12rpx;
}

.record-date {
  font-size: 26rpx;
  font-weight: bold;
  color: #2e7d32;
}

.record-bmi {
  font-size: 24rpx;
  color: #4caf50;
  font-weight: 500;
}

.record-details {
  display: flex;
  flex-wrap: wrap;
  gap: 12rpx;
  margin-bottom: 8rpx;
}

.detail {
  font-size: 22rpx;
  color: #666;
  background: #e8f5e9;
  padding: 6rpx 12rpx;
  border-radius: 8rpx;
}

.record-remark {
  font-size: 22rpx;
  color: #999;
  margin-top: 8rpx;
}

.record-delete {
  font-size: 32rpx;
  padding: 8rpx;
  opacity: 0.6;
}

.record-delete:active {
  opacity: 1;
}

/* 空状态 */
.empty-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80rpx 40rpx;
  background: linear-gradient(135deg, #ffffff 0%, #f1f8e9 100%);
  border-radius: 24rpx;
  box-shadow: 0 8rpx 32rpx rgba(46, 125, 50, 0.15);
}

.empty-icon-wrapper {
  position: relative;
  margin-bottom: 24rpx;
}

.empty-glow {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 120rpx;
  height: 120rpx;
  background: radial-gradient(circle, rgba(76, 175, 80, 0.2) 0%, transparent 70%);
  border-radius: 50%;
  animation: glow 2s infinite;
}

@keyframes glow {
  0%, 100% { transform: translate(-50%, -50%) scale(1); opacity: 0.5; }
  50% { transform: translate(-50%, -50%) scale(1.2); opacity: 0.8; }
}

.empty-icon {
  font-size: 100rpx;
  position: relative;
  z-index: 1;
}

.empty-title {
  font-size: 32rpx;
  font-weight: bold;
  color: #2e7d32;
  margin-bottom: 12rpx;
}

.empty-sub {
  font-size: 26rpx;
  color: #999;
}

.footer-deco {
  margin-top: 40rpx;
  text-align: center;
}

.deco-text {
  font-size: 22rpx;
  color: #999;
}

.spacer {
  height: 120rpx;
}

/* 通用样式 */
.floating-card {
  animation: floatCard 3s ease-in-out infinite;
}

@keyframes floatCard {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-4rpx); }
}

.neon {
  text-shadow: 0 0 10rpx rgba(76, 175, 80, 0.5), 0 0 20rpx rgba(76, 175, 80, 0.3);
}
</style>

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
          <text class="header-icon">🌟</text>
        </view>
        <text class="header-title">步数统计</text>
        <view class="header-icon-wrapper">
          <view class="header-icon-ring"></view>
          <text class="header-icon">🍃</text>
        </view>
      </view>

      <!-- 日期和步数输入卡片 -->
      <view class="card floating-card">
        <view class="card-header">
          <view class="header-dot"></view>
          <text class="card-title">📅 日期选择</text>
        </view>

        <picker mode="date" :value="date" @change="onDateChange">
          <view class="date-picker">
            <text class="picker-icon">📅</text>
            <text class="picker-date">{{ date }}</text>
            <text class="picker-weekday">{{ getWeekday(date) }}</text>
            <text class="picker-arrow">▼</text>
          </view>
        </picker>

        <view class="card-header" style="margin-top: 32rpx;">
          <view class="header-dot"></view>
          <text class="card-title">👟 步数输入</text>
        </view>

        <view class="input-wrapper" :class="{ focused: isFocused }">
          <text class="input-icon">👟</text>
          <input
              v-model="inputSteps"
              class="input"
              type="number"
              placeholder="请输入今日步数"
              placeholder-class="input-placeholder"
              @focus="isFocused = true"
              @blur="isFocused = false"
          />
          <text class="input-unit">步</text>
          <view class="input-glow"></view>
        </view>

        <!-- 快捷输入按钮 -->
        <view class="quick-buttons">
          <view class="quick-btn" @click="quickInput(5000)">
            <text class="quick-text">5000步</text>
            <view class="quick-glow"></view>
          </view>
          <view class="quick-btn" @click="quickInput(8000)">
            <text class="quick-text">8000步</text>
            <view class="quick-glow"></view>
          </view>
          <view class="quick-btn" @click="quickInput(10000)">
            <text class="quick-text">10000步</text>
            <view class="quick-glow"></view>
          </view>
        </view>

        <view class="btn-group">
          <view class="btn btn-primary" @click="queryStepCount" :class="{ loading: loading }">
            <view class="btn-shine"></view>
            <text class="btn-text" v-if="!loading">🔍 查询步数</text>
            <view class="loading-dots" v-else>
              <view class="dot"></view>
              <view class="dot"></view>
              <view class="dot"></view>
            </view>
          </view>
          <view class="btn btn-secondary" @click="uploadStepCount" :class="{ loading: uploading }">
            <view class="btn-shine"></view>
            <text class="btn-text" v-if="!uploading">📤 上传步数</text>
            <view class="loading-dots" v-else>
              <view class="dot"></view>
              <view class="dot"></view>
              <view class="dot"></view>
            </view>
          </view>
        </view>
      </view>

      <!-- 步数统计卡片 -->
      <view class="card floating-card" v-if="stepData">
        <view class="card-header">
          <view class="header-dot"></view>
          <text class="card-title">📊 今日步数统计</text>
        </view>

        <!-- 今日步数进度条 -->
        <view class="progress-section">
          <view class="progress-header">
            <text class="progress-label">今日目标进度</text>
            <text class="progress-percent">{{ stepPercent }}%</text>
          </view>
          <view class="progress-bar">
            <view class="progress-fill" :style="{ width: stepPercent + '%' }">
              <view class="progress-dot"></view>
            </view>
          </view>
          <text class="progress-goal">目标：10000步</text>
        </view>

        <view class="step-stats">
          <view class="stat-item">
            <text class="stat-icon">👟</text>
            <text class="stat-value">{{ stepData.steps || 0 }}</text>
            <text class="stat-label">总步数</text>
          </view>
          <view class="stat-divider"></view>
          <view class="stat-item">
            <text class="stat-icon">🔥</text>
            <text class="stat-value">{{ stepData.calories || 0 }}</text>
            <text class="stat-label">卡路里(kcal)</text>
          </view>
          <view class="stat-divider"></view>
          <view class="stat-item">
            <text class="stat-icon">⏱️</text>
            <text class="stat-value">{{ stepData.duration || 0 }}</text>
            <text class="stat-label">运动时长(分)</text>
          </view>
          <view class="stat-divider"></view>
          <view class="stat-item">
            <text class="stat-icon">🌿</text>
            <text class="stat-value">{{ Math.round((stepData.steps || 0) * 0.08) }}</text>
            <text class="stat-label">减碳量(g)</text>
          </view>
        </view>
      </view>

      <!-- 近7天步数趋势图 -->
      <view class="card floating-card">
        <view class="card-header">
          <view class="header-dot"></view>
          <text class="card-title">📈 近7天步数趋势</text>
          <text class="card-icon">🚲</text>
        </view>
        <view class="chart-container">
          <canvas
              canvas-id="stepChart"
              :style="'width:' + canvasW + 'px;height:' + canvasH + 'px;'"
          ></canvas>
        </view>
      </view>

      <!-- 近期步数记录 -->
      <view class="card floating-card">
        <view class="card-header">
          <view class="header-dot"></view>
          <text class="card-title">📋 近期步数记录</text>
          <text class="card-icon">💡</text>
        </view>

        <view v-if="historyList.length > 0" class="history-list">
          <view
              class="history-item"
              v-for="(item, index) in historyList"
              :key="index"
              @click="showActionMenu(item)"
          >
            <view class="history-date">
              <text class="history-icon">🕒</text>
              <text class="history-day">{{ formatDate(item.date).split('-')[2] }}日</text>
              <text class="history-weekday">{{ getWeekday(item.date) }}</text>
            </view>
            <text class="history-steps">{{ item.steps }} 步</text>
            <view class="history-arrow">→</view>
          </view>
        </view>
        <view class="empty-history" v-else>
          <text class="empty-icon">📭</text>
          <text class="empty-text">暂无历史记录</text>
        </view>
      </view>

      <!-- 底部装饰 -->
      <view class="footer-deco">
        <text class="deco-text">🚶 每一步，都是对地球的关爱 🌍</text>
      </view>

      <view class="spacer"></view>
    </scroll-view>

    <!-- 操作菜单 -->
    <view v-if="menuVisible" class="action-menu" @click="menuVisible = false">
      <view class="menu-content" @click.stop>
        <view class="menu-header">
          <text class="menu-title">操作</text>
          <view class="menu-close" @click="menuVisible = false">✕</view>
        </view>
        <view class="menu-item" @click="copyStepData">
          <text class="menu-icon">📋</text>
          <text>复制数据</text>
        </view>
        <view class="menu-item" @click="deleteStepData">
          <text class="menu-icon">🗑️</text>
          <text>删除记录</text>
        </view>
        <view class="menu-item cancel" @click="menuVisible = false">
          <text>取消</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import { getStepCount, getStepCountList } from '../../utils/request.js';

export default {
  data() {
    return {
      date: new Date().toISOString().split('T')[0],
      stepData: null,
      historyList: [],
      stuNo: '',
      inputSteps: '',
      loading: false,
      uploading: false,
      menuVisible: false,
      selectedItem: null,
      isFocused: false,
      chartData: {
        categories: [],
        data: []
      },
      canvasW: 0,
      canvasH: 0,
      particleStyles: []
    };
  },
  computed: {
    stepPercent() {
      if (!this.stepData) return 0;
      return Math.min(100, Math.round((this.stepData.steps || 0) / 10000 * 100));
    }
  },
  onLoad() {
    const stuNo = uni.getStorageSync('username');
    if (!stuNo) {
      uni.reLaunch({ url: '/pages/login/login' });
      return;
    }
    this.stuNo = stuNo;
    this.initParticleStyles();
    this.loadHistory();
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
      this.date = e.detail.value;
    },
    quickInput(steps) {
      this.inputSteps = steps.toString();
    },
    formatDate(dateStr) {
      if (!dateStr) return '';
      return String(dateStr).substring(0, 10);
    },
    getWeekday(dateStr) {
      if (!dateStr) return '';
      const date = new Date(dateStr);
      const weekdays = ['周日', '周一', '周二', '周三', '周四', '周五', '周六'];
      return weekdays[date.getDay()];
    },
    async queryStepCount() {
      if (this.loading) return;
      this.loading = true;
      uni.showLoading({ title: '查询中...' });
      try {
        const res = await getStepCount(this.stuNo, this.date);
        this.stepData = res;
        this.calculateAndDrawChart();
      } catch (e) {
        uni.showToast({ title: '查询失败', icon: 'none' });
      } finally {
        this.loading = false;
        uni.hideLoading();
      }
    },
    async loadHistory() {
      try {
        const res = await getStepCountList(this.stuNo);
        this.historyList = res || [];
        this.calculateAndDrawChart();
      } catch (e) {
        console.error('加载历史失败:', e);
      }
    },
    calculateAndDrawChart() {
      const today = new Date();
      const last7Days = [];

      for (let i = 6; i >= 0; i--) {
        const date = new Date(today);
        date.setDate(today.getDate() - i);
        const dateStr = date.toISOString().split('T')[0];
        const month = date.getMonth() + 1;
        const day = date.getDate();

        let steps = 0;
        if (this.historyList && this.historyList.length > 0) {
          const record = this.historyList.find(item => this.formatDate(item.date) === dateStr);
          if (record) steps = record.steps;
        }

        last7Days.push({
          label: `${month}/${day}`,
          steps: steps
        });
      }

      this.chartData.categories = last7Days.map(d => d.label);
      this.chartData.data = last7Days.map(d => d.steps);

      this.$nextTick(() => {
        this.initChart();
      });
    },
    initChart() {
      const query = uni.createSelectorQuery().in(this);
      query.select('.chart-container').boundingClientRect(rect => {
        if (rect && rect.width > 0 && rect.height > 0) {
          this.canvasW = rect.width;
          this.canvasH = rect.height;
          this.$nextTick(() => {
            this.drawChart();
          });
        }
      }).exec();
    },
    drawChart() {
      if (!this.canvasW || !this.canvasH) return;

      const ctx = uni.createCanvasContext('stepChart', this);
      const data = this.chartData.data;
      const labels = this.chartData.categories;
      const W = this.canvasW;
      const H = this.canvasH;

      if (data.length === 0) return;

      const PAD_LEFT = 50;
      const PAD_RIGHT = 10;
      const PAD_TOP = 30;
      const PAD_BOTTOM = 40;

      const chartW = W - PAD_LEFT - PAD_RIGHT;
      const chartH = H - PAD_TOP - PAD_BOTTOM;

      const maxValue = Math.max(...data, 1000);
      const yMax = Math.ceil(maxValue / 5000) * 5000;

      ctx.clearRect(0, 0, W, H);
      ctx.fillStyle = 'rgba(255, 255, 255, 0.6)';
      ctx.fillRect(0, 0, W, H);

      ctx.setStrokeStyle('#C8E6C9');
      ctx.setLineWidth(1);
      for (let i = 0; i <= 4; i++) {
        const y = PAD_TOP + (chartH / 4) * i;
        ctx.beginPath();
        ctx.moveTo(PAD_LEFT, y);
        ctx.lineTo(W - PAD_RIGHT, y);
        ctx.stroke();
      }

      ctx.beginPath();
      ctx.setStrokeStyle('#3D9B6D');
      ctx.setLineWidth(3);
      ctx.setLineCap('round');
      ctx.setLineJoin('round');

      const xOf = (i) => PAD_LEFT + (i / (data.length - 1)) * chartW;
      const yOf = (v) => PAD_TOP + chartH - (v / yMax) * chartH;

      data.forEach((value, index) => {
        const x = xOf(index);
        const y = yOf(value);
        if (index === 0) {
          ctx.moveTo(x, y);
        } else {
          ctx.lineTo(x, y);
        }
      });
      ctx.stroke();

      data.forEach((value, index) => {
        const x = xOf(index);
        const y = yOf(value);
        ctx.beginPath();
        ctx.arc(x, y, 6, 0, 2 * Math.PI);
        ctx.setFillStyle('#6FB88A');
        ctx.fill();
        ctx.setStrokeStyle('#FFFFFF');
        ctx.setLineWidth(2);
        ctx.stroke();

        if (value > 0) {
          ctx.setFillStyle('#2C5A3A');
          ctx.setFontSize(10);
          ctx.setTextAlign('center');
          ctx.fillText(value, x, y - 10);
        }
      });

      labels.forEach((label, index) => {
        const x = xOf(index);
        ctx.setFillStyle('#5B8C6E');
        ctx.setFontSize(10);
        ctx.setTextAlign('center');
        ctx.fillText(label, x, H - PAD_BOTTOM + 15);
      });

      ctx.draw();
    },
    showActionMenu(item) {
      this.selectedItem = item;
      this.menuVisible = true;
    },
    copyStepData() {
      if (!this.selectedItem) return;
      const data = `${this.selectedItem.date}: ${this.selectedItem.steps}步`;
      uni.setClipboardData({
        data: data,
        success: () => {
          uni.showToast({ title: '复制成功', icon: 'success' });
          this.menuVisible = false;
        }
      });
    },
    deleteStepData() {
      uni.showModal({
        title: '确认删除',
        content: '确定要删除这条记录吗？',
        success: (res) => {
          if (res.confirm) {
            uni.showToast({ title: '删除成功', icon: 'success' });
            this.menuVisible = false;
            this.loadHistory();
          }
        }
      });
    },
    async uploadStepCount() {
      const steps = parseInt(this.inputSteps);
      if (isNaN(steps) || steps < 0) {
        uni.showToast({ title: '请输入有效的步数', icon: 'none' });
        return;
      }

      this.uploading = true;
      uni.showLoading({ title: '上传中...' });
      try {
        const calories = Math.round(steps * 0.05);
        const duration = Math.round(steps / 100);

        const res = await uni.request({
          url: 'http://localhost:8080/api/step/save',
          method: 'POST',
          data: {
            studentId: this.stuNo,
            date: this.date,
            steps: steps,
            calories: calories,
            duration: duration
          }
        });

        const response = Array.isArray(res) ? res[1] : res;

        if (response.statusCode === 200) {
          uni.showToast({ title: '上传成功', icon: 'success' });
          await this.queryStepCount();
          await this.loadHistory();
          this.inputSteps = '';
        } else {
          uni.showToast({ title: '上传失败', icon: 'none' });
        }
      } catch (e) {
        uni.showToast({ title: '上传失败', icon: 'none' });
      } finally {
        this.uploading = false;
        uni.hideLoading();
      }
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
  background: rgba(80, 200, 140, 0.6);
  border-radius: 50%;
  animation: floatUp linear infinite;
}

@keyframes floatUp {
  0% {
    transform: translateY(100vh);
    opacity: 0;
  }
  10% {
    opacity: 0.8;
  }
  90% {
    opacity: 0.5;
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
  50% { transform: translateY(-6rpx); }
}

/* 页面标题 */
.page-header {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-top: 100rpx;
  margin-bottom: 32rpx;
  gap: 24rpx;
}

.header-icon-wrapper {
  position: relative;
  width: 56rpx;
  height: 56rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.header-icon-ring {
  position: absolute;
  width: 100%;
  height: 100%;
  border: 2rpx solid rgba(61, 155, 109, 0.4);
  border-radius: 50%;
  animation: ringPulse 2s ease-in-out infinite;
}

@keyframes ringPulse {
  0%, 100% { transform: scale(1); opacity: 0.5; }
  50% { transform: scale(1.2); opacity: 1; }
}

.header-icon {
  font-size: 32rpx;
  position: relative;
  z-index: 2;
}

.header-title {
  font-size: 44rpx;
  font-weight: 700;
  background: linear-gradient(135deg, #2C5A3A, #3D9B6D);
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
}

/* 卡片样式 */
.card {
  background: rgba(255, 255, 255, 0.9);
  border-radius: 48rpx;
  padding: 36rpx 32rpx;
  margin-bottom: 28rpx;
  box-shadow: 0 12rpx 32rpx rgba(0, 0, 0, 0.08);
}

.card-header {
  display: flex;
  align-items: center;
  gap: 12rpx;
  margin-bottom: 28rpx;
}

.header-dot {
  width: 8rpx;
  height: 32rpx;
  background: linear-gradient(180deg, #6FB88A, #3D9B6D);
  border-radius: 4rpx;
}

.card-title {
  font-size: 30rpx;
  font-weight: 700;
  color: #2C5A3A;
  flex: 1;
}

.card-icon {
  font-size: 32rpx;
  animation: spin 4s linear infinite;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

/* 日期选择器 */
.date-picker {
  display: flex;
  align-items: center;
  background: rgba(255, 255, 255, 0.6);
  border-radius: 60rpx;
  padding: 20rpx 28rpx;
  border: 1rpx solid rgba(100, 200, 150, 0.3);
}

.picker-icon {
  font-size: 32rpx;
  margin-right: 16rpx;
}

.picker-date {
  font-size: 28rpx;
  font-weight: 500;
  color: #2C5A3A;
  flex: 1;
}

.picker-weekday {
  font-size: 24rpx;
  color: #6FB88A;
  margin-right: 16rpx;
}

.picker-arrow {
  font-size: 24rpx;
  color: #9CC0AC;
}

/* 输入框 */
.input-wrapper {
  position: relative;
  margin-bottom: 28rpx;
}

.input-icon {
  position: absolute;
  left: 28rpx;
  top: 50%;
  transform: translateY(-50%);
  font-size: 32rpx;
  z-index: 2;
}

.input {
  width: 100%;
  height: 96rpx;
  background: rgba(255, 255, 255, 0.6);
  border: 2rpx solid rgba(100, 200, 150, 0.3);
  border-radius: 48rpx;
  padding: 0 80rpx 0 80rpx;
  font-size: 32rpx;
  color: #2C5A3A;
  box-sizing: border-box;
  transition: all 0.3s ease;
}

.input:focus {
  border-color: #6FB88A;
  box-shadow: 0 0 0 4rpx rgba(111, 184, 138, 0.2);
  outline: none;
}

.input-placeholder {
  color: #C8E6C9;
}

.input-unit {
  position: absolute;
  right: 28rpx;
  top: 50%;
  transform: translateY(-50%);
  font-size: 26rpx;
  color: #6FB88A;
}

.input-glow {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 0;
  background: linear-gradient(90deg, #6FB88A, #3D9B6D);
  border-radius: 24rpx;
  filter: blur(8rpx);
  transition: all 0.3s ease;
  pointer-events: none;
}

/* 修复：用父级 class 控制，不使用 ~ 选择器 */
.input-wrapper.focused .input-glow {
  height: 60rpx;
  opacity: 0.3;
}

/* 快捷按钮 */
.quick-buttons {
  display: flex;
  gap: 16rpx;
  margin-bottom: 32rpx;
}

.quick-btn {
  flex: 1;
  height: 72rpx;
  background: rgba(100, 200, 150, 0.15);
  border-radius: 36rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
  transition: all 0.3s ease;
}

.quick-btn:active {
  transform: scale(0.96);
  background: rgba(100, 200, 150, 0.3);
}

.quick-text {
  font-size: 26rpx;
  color: #3D9B6D;
  font-weight: 500;
  position: relative;
  z-index: 2;
}

.quick-glow {
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.4), transparent);
  animation: quickShine 3s ease-in-out infinite;
}

@keyframes quickShine {
  0% { left: -100%; }
  60%, 100% { left: 100%; }
}

/* 按钮组 */
.btn-group {
  display: flex;
  gap: 20rpx;
}

.btn {
  flex: 1;
  height: 96rpx;
  border-radius: 48rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
  transition: all 0.3s cubic-bezier(0.2, 0.9, 0.4, 1.1);
}

.btn-primary {
  background: linear-gradient(135deg, #6FB88A, #3D9B6D);
  box-shadow: 0 8rpx 24rpx rgba(61, 155, 109, 0.4);
}

.btn-secondary {
  background: linear-gradient(135deg, #9CC0AC, #6FB88A);
  box-shadow: 0 8rpx 24rpx rgba(111, 184, 138, 0.3);
}

.btn:active {
  transform: scale(0.97);
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

.btn-text {
  font-size: 28rpx;
  font-weight: 600;
  color: #FFFFFF;
  letter-spacing: 2rpx;
  position: relative;
  z-index: 2;
}

.loading-dots {
  display: flex;
  gap: 12rpx;
  position: relative;
  z-index: 2;
}

.loading-dots .dot {
  width: 12rpx;
  height: 12rpx;
  background: #FFFFFF;
  border-radius: 50%;
  animation: dotBlink 1.2s ease-in-out infinite;
}

.loading-dots .dot:nth-child(1) { animation-delay: 0s; }
.loading-dots .dot:nth-child(2) { animation-delay: 0.2s; }
.loading-dots .dot:nth-child(3) { animation-delay: 0.4s; }

@keyframes dotBlink {
  0%, 80%, 100% { opacity: 0.3; transform: scale(0.8); }
  40% { opacity: 1; transform: scale(1.2); }
}

/* 进度条 */
.progress-section {
  margin-bottom: 32rpx;
}

.progress-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 12rpx;
}

.progress-label {
  font-size: 24rpx;
  color: #5B8C6E;
}

.progress-percent {
  font-size: 24rpx;
  font-weight: 600;
  color: #3D9B6D;
}

.progress-bar {
  height: 16rpx;
  background: rgba(100, 200, 150, 0.2);
  border-radius: 8rpx;
  overflow: visible;
  margin-bottom: 12rpx;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, #6FB88A, #3D9B6D);
  border-radius: 8rpx;
  position: relative;
  transition: width 0.5s ease;
  box-shadow: 0 0 12rpx rgba(61, 155, 109, 0.5);
}

.progress-dot {
  position: absolute;
  right: -10rpx;
  top: 50%;
  transform: translateY(-50%);
  width: 24rpx;
  height: 24rpx;
  background: #FFFFFF;
  border: 3rpx solid #3D9B6D;
  border-radius: 50%;
  box-shadow: 0 0 12rpx rgba(61, 155, 109, 0.8);
}

.progress-goal {
  font-size: 22rpx;
  color: #9CC0AC;
  text-align: right;
}

/* 步数统计 */
.step-stats {
  display: flex;
  align-items: center;
  justify-content: space-around;
  padding: 16rpx 0;
}

.stat-item {
  flex: 1;
  text-align: center;
}

.stat-icon {
  font-size: 36rpx;
  display: block;
  margin-bottom: 12rpx;
}

.stat-value {
  font-size: 32rpx;
  font-weight: 700;
  color: #2C5A3A;
  display: block;
  margin-bottom: 6rpx;
}

.stat-label {
  font-size: 20rpx;
  color: #9CC0AC;
}

.stat-divider {
  width: 1rpx;
  height: 60rpx;
  background: rgba(100, 200, 150, 0.3);
}

/* 图表容器 */
.chart-container {
  height: 480rpx;
  position: relative;
}

/* 历史记录 */
.history-list {
  display: flex;
  flex-direction: column;
  gap: 16rpx;
}

.history-item {
  display: flex;
  align-items: center;
  padding: 24rpx;
  background: rgba(255, 255, 255, 0.6);
  border-radius: 28rpx;
  transition: all 0.3s ease;
}

.history-item:active {
  transform: translateX(8rpx);
  background: rgba(255, 255, 255, 0.8);
}

.history-date {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 6rpx;
}

.history-icon {
  font-size: 28rpx;
}

.history-day {
  font-size: 26rpx;
  font-weight: 600;
  color: #2C5A3A;
}

.history-weekday {
  font-size: 20rpx;
  color: #9CC0AC;
}

.history-steps {
  font-size: 28rpx;
  font-weight: 700;
  margin: 0 20rpx;
}

.history-arrow {
  font-size: 32rpx;
  color: #C8E6C9;
  transition: transform 0.3s ease;
}

.history-item:active .history-arrow {
  transform: translateX(6rpx);
  color: #3D9B6D;
}

.empty-history {
  text-align: center;
  padding: 60rpx 0;
}

.empty-icon {
  font-size: 64rpx;
  display: block;
  margin-bottom: 16rpx;
  opacity: 0.5;
}

.empty-text {
  font-size: 26rpx;
  color: #9CC0AC;
}

/* 底部装饰 */
.footer-deco {
  text-align: center;
  padding: 40rpx 0 28rpx;
}

.deco-text {
  font-size: 28rpx;
  font-weight: 500;
  background: linear-gradient(135deg, #6FB88A, #3D9B6D);
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
  letter-spacing: 2rpx;
  animation: textPulse 2s ease-in-out infinite;
}

@keyframes textPulse {
  0%, 100% { opacity: 0.8; transform: scale(1); }
  50% { opacity: 1; transform: scale(1.02); text-shadow: 0 0 20rpx rgba(61, 155, 109, 0.3); }
}

.spacer {
  height: 60rpx;
}

/* 操作菜单 */
.action-menu {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  backdrop-filter: blur(8rpx);
  display: flex;
  align-items: flex-end;
  justify-content: center;
  z-index: 1000;
  animation: fadeIn 0.2s ease;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

.menu-content {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(24rpx);
  border-radius: 48rpx 48rpx 0 0;
  width: 100%;
  padding: 32rpx;
  animation: slideUp 0.3s ease;
}

@keyframes slideUp {
  from { transform: translateY(100%); }
  to { transform: translateY(0); }
}

.menu-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24rpx;
  padding-bottom: 16rpx;
  border-bottom: 1rpx solid rgba(100, 200, 150, 0.3);
}

.menu-title {
  font-size: 32rpx;
  font-weight: 700;
  color: #2C5A3A;
}

.menu-close {
  width: 56rpx;
  height: 56rpx;
  border-radius: 50%;
  background: rgba(100, 200, 150, 0.15);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32rpx;
  color: #3D9B6D;
}

.menu-item {
  display: flex;
  align-items: center;
  gap: 20rpx;
  padding: 24rpx;
  border-radius: 28rpx;
  font-size: 28rpx;
  color: #2C5A3A;
  transition: all 0.2s ease;
}

.menu-item:active {
  background: rgba(100, 200, 150, 0.15);
}

.menu-icon {
  font-size: 32rpx;
}

.menu-item.cancel {
  margin-top: 16rpx;
  justify-content: center;
  color: #9CC0AC;
  border-top: 1rpx solid rgba(100, 200, 150, 0.3);
}
</style>
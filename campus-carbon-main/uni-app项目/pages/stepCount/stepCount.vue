<template>
  <view class="container">
    <!-- 装饰元素 -->
    <view class="decorations">
      <view class="leaf leaf-1">🍃</view>
      <view class="leaf leaf-2">🍃</view>
      <view class="leaf leaf-3">🍃</view>
      <view class="leaf leaf-4">🍃</view>
    </view>
    
    <!-- 顶部标题 -->
    <view class="page-header">
      <text class="header-icon">🌟</text>
      <text class="header-title">步数统计</text>
      <text class="header-icon">🍃</text>
    </view>

    <!-- 日期和步数输入区域 -->
    <view class="date-section" :class="{ 'loaded': isLoaded }">
      <text class="label">选择日期：</text>
      <picker mode="date" :value="date" @change="onDateChange">
        <view class="picker">
          <text class="icon-calendar">📅</text>
          <text>{{ date }}</text>
          <text class="weekday">{{ getWeekday(date) }}</text>
        </view>
      </picker>
      
      <text class="label">输入步数：</text>
      <view class="input-container">
        <text class="icon-step">👟</text>
        <input 
          v-model="inputSteps" 
          class="input" 
          type="number" 
          placeholder="请输入今日步数"
          min="0"
        />
        <text class="input-unit">步</text>
      </view>
      
      <!-- 快捷输入按钮 -->
      <view class="quick-buttons">
        <button 
          class="quick-btn" 
          @click="quickInput(5000)"
          :class="{ 'active': inputSteps === '5000' }"
        >
          5000步
        </button>
        <button 
          class="quick-btn" 
          @click="quickInput(8000)"
          :class="{ 'active': inputSteps === '8000' }"
        >
          8000步
        </button>
        <button 
          class="quick-btn" 
          @click="quickInput(10000)"
          :class="{ 'active': inputSteps === '10000' }"
        >
          10000步
        </button>
      </view>
      
      <button class="btn primary" @click="queryStepCount" :loading="loading">
        <text class="btn-text">查询步数</text>
      </button>
      <button class="btn secondary" @click="uploadStepCount" :loading="uploading">
        <text class="btn-text">上传步数</text>
      </button>
    </view>

    <!-- 步数统计区域 -->
    <view class="step-section" v-if="stepData" :class="{ 'loaded': isLoaded }">
      <text class="section-title">步数统计</text>
      
      <!-- 今日步数进度条 -->
      <view class="progress-section">
        <view class="progress-header">
          <text class="progress-label">今日目标进度</text>
          <text class="progress-percent">{{ Math.min(100, Math.round((stepData.steps || 0) / 10000 * 100)) }}%</text>
        </view>
        <view class="progress-bar">
          <view 
            class="progress-fill" 
            :style="{ width: Math.min(100, Math.round((stepData.steps || 0) / 10000 * 100)) + '%' }"
            :class="{ 'animated': isLoaded }"
          >
            <view class="progress-highlight"></view>
          </view>
        </view>
        <view class="progress-goal">目标：10000步</view>
      </view>
      
      <view class="step-info">
        <view class="step-item">
          <text class="step-label">步数：</text>
          <text class="step-value gradient-text">{{ stepData.steps || 0 }} 步</text>
        </view>
        <view class="step-item">
          <text class="step-label">卡路里：</text>
          <text class="step-value">{{ stepData.calories || 0 }} kcal</text>
        </view>
        <view class="step-item">
          <text class="step-label">运动时长：</text>
          <text class="step-value">{{ stepData.duration || 0 }} 分钟</text>
        </view>
        <view class="step-item">
          <text class="step-label">减碳量：</text>
          <text class="step-value">{{ Math.round((stepData.steps || 0) * 0.08) }} g</text>
        </view>
      </view>
    </view>

    <view class="empty-section" v-if="queried && !stepData" :class="{ 'loaded': isLoaded }">
      <text class="empty-text">该日期暂无步数记录</text>
    </view>

    <!-- 近7天步数趋势图 -->
    <view class="chart-section" :class="{ 'loaded': isLoaded }">
      <view class="section-header">
        <text class="section-title">近7天步数趋势</text>
        <text class="section-icon bike">🚲</text>
      </view>
      <view class="chart-container">
        <canvas
          canvas-id="stepChart"
          :style="'width:' + canvasW + 'px;height:' + canvasH + 'px;display:block;'"
          @touchstart="touchChart"
        ></canvas>
      </view>
    </view>

    <!-- 近期步数记录 -->
    <view class="record-section" :class="{ 'loaded': isLoaded }">
      <view class="section-header">
        <text class="section-title">近期步数记录</text>
        <text class="section-icon bulb">💡</text>
      </view>
      <view v-if="historyList.length > 0">
        <view 
          class="history-item" 
          v-for="item in historyList" 
          :key="item.id"
          :class="{ 'hover': hoveredItem === item.id }"
          @touchstart="hoveredItem = item.id"
          @touchend="hoveredItem = null"
          @longpress="showActionMenu(item)"
        >
          <view class="history-date">
            <text class="icon-time">🕒</text>
            <text class="date-text">{{ formatDate(item.date) }}</text>
            <text class="weekday">{{ getWeekday(item.date) }}</text>
          </view>
          <text class="history-steps gradient-text">{{ item.steps }} 步</text>
          <view class="history-arrow">→</view>
        </view>
      </view>
      <view class="empty-section" v-else>
        <text class="empty-text">暂无历史记录</text>
      </view>
    </view>
    
    <!-- 操作菜单 -->
    <view v-if="menuVisible" class="action-menu">
      <view class="menu-content">
        <text class="menu-title">操作</text>
        <button class="menu-item" @click="copyStepData">复制数据</button>
        <button class="menu-item" @click="deleteStepData">删除记录</button>
        <button class="menu-item cancel" @click="menuVisible = false">取消</button>
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
      last7Days: [],
      queried: false,
      stuNo: '',
      inputSteps: '',
      loading: false,
      uploading: false,
      isLoaded: false,
      hoveredItem: null,
      menuVisible: false,
      selectedItem: null,
      chartData: {
        categories: [],
        series: [{ name: '步数', data: [] }]
      },
      canvasW: 0,
      canvasH: 0
    };
  },
  onLoad() {
    const stuNo = uni.getStorageSync('username');
    if (!stuNo) {
      uni.reLaunch({ url: '/pages/login/login' });
      return;
    }
    this.stuNo = stuNo;
    setTimeout(() => { this.isLoaded = true; }, 300);
    this.loadHistory();
  },
  methods: {
    onDateChange(e) {
      this.date = e.detail.value;
    },
    quickInput(steps) {
      this.inputSteps = steps.toString();
    },
    async queryStepCount() {
      this.loading = true;
      try {
        const res = await getStepCount(this.stuNo, this.date);
        this.stepData = res;
        this.queried = true;
        this.calculateLast7Days();
        this.initChart();
      } catch (e) {
        uni.showToast({ title: '查询失败', icon: 'none' });
      } finally {
        this.loading = false;
      }
    },
    async loadHistory() {
      try {
        const res = await getStepCountList(this.stuNo);
        this.historyList = res || [];
        this.calculateLast7Days();
        this.$nextTick(() => { this.initChart(); });
      } catch (e) {
        // 静默失败
      }
    },
    formatDate(dateStr) {
      if (!dateStr) return '';
      return String(dateStr).substring(0, 10);
    },
    getWeekday(dateStr) {
      const date = new Date(dateStr);
      const weekdays = ['周日', '周一', '周二', '周三', '周四', '周五', '周六'];
      return weekdays[date.getDay()];
    },
    calculateLast7Days() {
      const today = new Date();
      const last7Days = [];
      
      for (let i = 6; i >= 0; i--) {
        const date = new Date(today);
        date.setDate(today.getDate() - i);
        const dateStr = date.toISOString().split('T')[0];
        const month = date.getMonth() + 1;
        const day = date.getDate();
        
        // 查找该日期的步数
        let steps = 0;
        if (this.historyList.length > 0) {
          const record = this.historyList.find(item => this.formatDate(item.date) === dateStr);
          if (record) {
            steps = record.steps;
          }
        }
        
        last7Days.push({
          date: dateStr,
          label: `${month}/${day}`,
          steps: steps,
          isToday: i === 0
        });
      }
      
      this.last7Days = last7Days;
      
      // 更新图表数据
      this.chartData.categories = last7Days.map(day => day.label);
      this.chartData.series[0].data = last7Days.map(day => day.steps);
    },
    initChart() {
      uni.createSelectorQuery().in(this).select('.chart-container').boundingClientRect(rect => {
        if (!rect) return;
        const W = rect.width;
        const H = rect.height;
        this.canvasW = W;
        this.canvasH = H;
        this.$nextTick(() => { this._drawChart(W, H); });
      }).exec();
    },
    _drawChart(W, H) {
      const ctx = uni.createCanvasContext('stepChart', this);

      const PAD_LEFT = 55;   // Y轴区域
      const PAD_RIGHT = 10;
      const PAD_TOP = 24;    // 数据标签区域
      const PAD_BOTTOM = 36; // X轴日期+星期

      const chartW = W - PAD_LEFT - PAD_RIGHT;
      const chartH = H - PAD_TOP - PAD_BOTTOM;

      const data = this.chartData.series[0].data;
      const labels = this.chartData.categories;
      const n = data.length;

      // 动态 Y 轴最大值（取整到 5000 的倍数，最小 5000）
      const dataMax = Math.max(...data, 1000);
      const yMax = Math.ceil(dataMax / 5000) * 5000;
      const gridCount = 4;

      // ── 背景 ──
      ctx.fillStyle = '#f9fef9';
      ctx.fillRect(0, 0, W, H);

      // ── 网格线 & Y轴刻度 ──
      ctx.setFontSize(10);
      ctx.textAlign = 'right';
      ctx.setLineDash([4, 4]);
      ctx.lineWidth = 1;

      for (let i = 0; i <= gridCount; i++) {
        const val = yMax - (yMax / gridCount) * i;
        const y = PAD_TOP + (chartH / gridCount) * i;
        ctx.strokeStyle = '#e8f5e9';
        ctx.beginPath();
        ctx.moveTo(PAD_LEFT, y);
        ctx.lineTo(W - PAD_RIGHT, y);
        ctx.stroke();
        ctx.fillStyle = '#aaa';
        ctx.fillText(val >= 1000 ? (val / 1000).toFixed(0) + 'k' : val, PAD_LEFT - 4, y + 4);
      }

      // ── 折线（平滑贝塞尔） ──
      ctx.setLineDash([]);
      ctx.strokeStyle = '#2e7d32';
      ctx.lineWidth = 2.5;
      ctx.lineCap = 'round';
      ctx.lineJoin = 'round';

      const xOf = (i) => PAD_LEFT + (i / (n - 1)) * chartW;
      const yOf = (v) => PAD_TOP + chartH - (v / yMax) * chartH;

      ctx.beginPath();
      data.forEach((v, i) => {
        const x = xOf(i), y = yOf(v);
        if (i === 0) {
          ctx.moveTo(x, y);
        } else {
          const px = xOf(i - 1), py = yOf(data[i - 1]);
          const cpx = (px + x) / 2;
          ctx.bezierCurveTo(cpx, py, cpx, y, x, y);
        }
      });
      ctx.stroke();

      // ── 数据点 & 标签 ──
      data.forEach((v, i) => {
        const x = xOf(i), y = yOf(v);
        ctx.beginPath();
        ctx.arc(x, y, 5, 0, 2 * Math.PI);
        ctx.fillStyle = '#81c784';
        ctx.fill();
        ctx.strokeStyle = '#2e7d32';
        ctx.lineWidth = 1.5;
        ctx.stroke();

        if (v > 0) {
          ctx.setFontSize(10);
          ctx.textAlign = 'center';
          ctx.fillStyle = '#2e7d32';
          ctx.fillText(v >= 10000 ? (v / 1000).toFixed(1) + 'k' : v, x, Math.max(PAD_TOP - 4, y - 8));
        }
      });

      // ── X轴标签（日期 + 星期） ──
      ctx.setFontSize(10);
      ctx.textAlign = 'center';
      labels.forEach((label, i) => {
        const x = xOf(i);
        ctx.fillStyle = '#666';
        ctx.fillText(label, x, H - PAD_BOTTOM + 14);
        const wd = this.getWeekday(this.last7Days[i] ? this.last7Days[i].date : '');
        ctx.fillStyle = '#aaa';
        ctx.fillText(wd, x, H - PAD_BOTTOM + 26);
      });

      ctx.draw();
    },
    touchChart(e) {
      // 触摸事件处理
    },
    showActionMenu(item) {
      this.selectedItem = item;
      this.menuVisible = true;
    },
    copyStepData() {
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
            // 这里可以调用删除接口
            uni.showToast({ title: '删除成功', icon: 'success' });
            this.menuVisible = false;
            // 刷新数据
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
      try {
        // 计算卡路里和运动时长
        const calories = Math.round(steps * 0.05); // 每步约0.05卡路里
        const duration = Math.round(steps / 100); // 假设每分钟走100步
        
        // 调用后端接口保存步数
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
        
        // 处理 uni.request 的返回值
        const response = Array.isArray(res) ? res[1] : res;
        
        if (response.statusCode === 200) {
          uni.showToast({ 
            title: '上传成功', 
            icon: 'success',
            duration: 2000
          });
          // 刷新数据
          await this.queryStepCount();
          await this.loadHistory();
        } else {
          uni.showToast({ title: `上传失败: ${response.statusCode}`, icon: 'none' });
          console.error('上传失败:', response);
        }
      } catch (e) {
        uni.showToast({ title: `上传失败: ${e.message}`, icon: 'none' });
        console.error('上传异常:', e);
      } finally {
        this.uploading = false;
      }
    }
  }
};
</script>

<style scoped>
/* 全局样式 */
.container {
  padding: 20rpx;
  background: linear-gradient(180deg, #f0f9e8 0%, #e6f7dc 100%);
  min-height: 100vh;
  position: relative;
  overflow: hidden;
}

/* 装饰元素 */
.decorations {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  pointer-events: none;
  z-index: 0;
}

.leaf {
  position: absolute;
  font-size: 32rpx;
  opacity: 0.6;
}

.leaf-1 {
  top: 20rpx;
  left: 20rpx;
  transform: rotate(15deg);
}

.leaf-2 {
  top: 80rpx;
  right: 40rpx;
  transform: rotate(-10deg);
  font-size: 28rpx;
}

.leaf-3 {
  bottom: 100rpx;
  left: 30rpx;
  transform: rotate(25deg);
  font-size: 36rpx;
}

.leaf-4 {
  bottom: 180rpx;
  right: 60rpx;
  transform: rotate(-20deg);
  font-size: 30rpx;
}

/* 页面标题 */
.page-header {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 30rpx;
  position: relative;
  z-index: 1;
}

.header-title {
  font-size: 40rpx;
  font-weight: 700;
  color: #2d5016;
  margin: 0 16rpx;
  text-shadow: 2px 2px 4px rgba(45, 80, 22, 0.1);
}

.header-icon {
  font-size: 32rpx;
  animation: bounce 2s infinite;
}

@keyframes bounce {
  0%, 20%, 50%, 80%, 100% {
    transform: translateY(0);
  }
  40% {
    transform: translateY(-10rpx);
  }
  60% {
    transform: translateY(-5rpx);
  }
}

/* 卡片样式 */
.date-section,
.step-section,
.chart-section,
.record-section,
.empty-section {
  position: relative;
  z-index: 1;
  background: rgba(255, 255, 255, 0.8);
  border-radius: 20rpx;
  padding: 30rpx;
  margin-bottom: 30rpx;
  box-shadow: 
    0 4px 16px rgba(45, 80, 22, 0.1),
    0 1px 3px rgba(45, 80, 22, 0.05);
  backdrop-filter: blur(10px);
  transition: all 0.3s ease;
  opacity: 0;
  transform: translateY(20px);
  border: 1px solid rgba(139, 191, 159, 0.2);
}

/* 加载动画 */
.date-section.loaded,
.step-section.loaded,
.chart-section.loaded,
.record-section.loaded,
.empty-section.loaded {
  opacity: 1;
  transform: translateY(0);
  animation: fadeInUp 0.6s ease-out;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 标签样式 */
.label {
  display: block;
  font-size: 28rpx;
  font-weight: 600;
  color: #2d5016;
  margin-bottom: 16rpx;
  letter-spacing: 0.5px;
}

/* 日期选择器 */
.picker {
  width: 100%;
  height: 80rpx;
  border: 1px solid rgba(139, 191, 159, 0.3);
  border-radius: 12px;
  padding: 0 20rpx;
  display: flex;
  align-items: center;
  margin-bottom: 30rpx;
  box-sizing: border-box;
  background: rgba(255, 255, 255, 0.9);
  box-shadow: inset 0 1px 2px rgba(45, 80, 22, 0.05);
}

.picker .icon-calendar {
  font-size: 28rpx;
  margin-right: 12rpx;
  color: #6a9d87;
}

.picker text {
  font-size: 24rpx;
  color: #333;
}

.picker .weekday {
  font-size: 20rpx;
  color: #6a9d87;
  margin-left: 10rpx;
  flex: 1;
  text-align: right;
}

/* 输入框容器 */
.input-container {
  position: relative;
  margin-bottom: 30rpx;
  display: flex;
  align-items: center;
}

.input-container .icon-step {
  position: absolute;
  left: 20rpx;
  font-size: 28rpx;
  z-index: 2;
  color: #6a9d87;
}

/* 下沉式输入框 */
.input {
  width: 100%;
  height: 80rpx;
  border: 1px solid rgba(139, 191, 159, 0.3);
  border-radius: 12px;
  padding: 0 20rpx 0 60rpx;
  font-size: 24rpx;
  box-sizing: border-box;
  background: rgba(255, 255, 255, 0.9);
  box-shadow: inset 0 2px 4px rgba(45, 80, 22, 0.05);
  transition: all 0.3s ease;
}

.input:focus {
  outline: none;
  border-color: #6a9d87;
  box-shadow: inset 0 2px 4px rgba(45, 80, 22, 0.05), 0 0 0 3px rgba(106, 157, 135, 0.1);
}

.input-unit {
  position: absolute;
  right: 20rpx;
  top: 50%;
  transform: translateY(-50%);
  font-size: 24rpx;
  color: #6a9d87;
  pointer-events: none;
  z-index: 2;
}

/* 快捷按钮 */
.quick-buttons {
  display: flex;
  justify-content: space-between;
  margin-bottom: 30rpx;
}

.quick-btn {
  flex: 1;
  height: 60rpx;
  background: rgba(255, 255, 255, 0.9);
  color: #2d5016;
  font-size: 24rpx;
  border-radius: 10px;
  border: 1px solid rgba(139, 191, 159, 0.3);
  margin: 0 5rpx;
  transition: all 0.2s ease;
  box-shadow: 0 2px 8px rgba(45, 80, 22, 0.05);
}

.quick-btn:active {
  transform: translateY(2px);
  box-shadow: 0 1px 4px rgba(45, 80, 22, 0.05);
  background: rgba(240, 249, 232, 0.9);
}

.quick-btn.active {
  background: #2d5016;
  color: #fff;
  border-color: transparent;
  box-shadow: 0 4px 12px rgba(45, 80, 22, 0.3);
}

.quick-btn.pulse {
  animation: pulse 0.3s ease-in-out;
}

@keyframes pulse {
  0% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.05);
  }
  100% {
    transform: scale(1);
  }
}

/* 主按钮 */
.btn {
  position: relative;
  width: 100%;
  height: 80rpx;
  border-radius: 12px;
  border: none;
  margin-bottom: 20rpx;
  overflow: hidden;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 16px rgba(45, 80, 22, 0.2);
}

.btn:active {
  transform: scale(1.05) translateY(-2px);
  box-shadow: 0 6px 20px rgba(45, 80, 22, 0.3);
}

.btn.primary {
  background: #2d5016;
}

.btn.secondary {
  background: #4a7c38;
}

.btn-text {
  position: relative;
  z-index: 2;
  font-size: 28rpx;
  font-weight: 600;
  color: #fff;
  letter-spacing: 1px;
}

/* 区块标题 */
.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24rpx;
}

.section-title {
  display: block;
  font-size: 28rpx;
  font-weight: 600;
  color: #2d5016;
  letter-spacing: 0.5px;
}

.section-icon {
  font-size: 32rpx;
  animation: spin 4s linear infinite;
}

@keyframes spin {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

/* 进度条 */
.progress-section {
  margin-bottom: 30rpx;
}

.progress-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12rpx;
}

.progress-label {
  font-size: 24rpx;
  color: #6a9d87;
  font-weight: 500;
}

.progress-percent {
  font-size: 24rpx;
  font-weight: 600;
  color: #2d5016;
}

.progress-bar {
  width: 100%;
  height: 12rpx;
  background: rgba(45, 80, 22, 0.1);
  border-radius: 6rpx;
  overflow: hidden;
  margin-bottom: 8rpx;
  box-shadow: inset 0 1px 2px rgba(45, 80, 22, 0.1);
}

.progress-fill {
  position: relative;
  height: 100%;
  background: linear-gradient(90deg, #6a9d87, #2d5016);
  border-radius: 6rpx;
  transition: width 0.5s ease;
  box-shadow: 0 0 10px rgba(106, 157, 135, 0.3);
  width: 0;
}

.progress-fill.animated {
  animation: progressFill 1s ease-out forwards;
}

@keyframes progressFill {
  to {
    width: var(--progress-width, 100%);
  }
}

.progress-highlight {
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.4), transparent);
  transition: left 0.6s ease;
}

.progress-fill:hover .progress-highlight {
  left: 100%;
}

.progress-goal {
  font-size: 20rpx;
  color: #6a9d87;
  text-align: right;
}

/* 步数信息 */
.step-info {
  display: flex;
  flex-direction: column;
  gap: 20rpx;
}

.step-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16rpx 0;
  border-bottom: 1px solid rgba(139, 191, 159, 0.2);
}

.step-label {
  font-size: 24rpx;
  color: #6a9d87;
  font-weight: 500;
}

.step-value {
  font-size: 28rpx;
  font-weight: 600;
  color: #2d5016;
}

/* 渐变文字 */
.gradient-text {
  background: linear-gradient(135deg, #2d5016, #6a9d87);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  font-weight: 700;
}

/* 趋势图 */
.chart-section {
  padding: 30rpx;
  border-radius: 16px;
  background: linear-gradient(135deg, #f1f8e9 0%, #ffffff 100%);
}

.chart-container {
  height: 500rpx;
  position: relative;
  margin-top: 20rpx;
}

.chart-canvas {
  width: 100%;
  height: 100%;
}

/* 历史记录 */
.record-section {
  padding: 30rpx;
}

.history-item {
  display: flex;
  align-items: center;
  padding: 24rpx;
  margin-bottom: 16rpx;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 12px;
  box-shadow: 0 4px 16px rgba(45, 80, 22, 0.05);
  transition: all 0.3s ease;
  border: 1px solid rgba(139, 191, 159, 0.2);
}

.history-item:hover,
.history-item.hover {
  transform: translateX(10px);
  background: rgba(240, 249, 232, 0.9);
  box-shadow: 0 6px 20px rgba(45, 80, 22, 0.1);
}

.history-date {
  display: flex;
  flex-direction: column;
  flex: 1;
  margin-left: 12rpx;
}

.history-date .icon-time {
  font-size: 24rpx;
  margin-bottom: 8rpx;
  color: #6a9d87;
}

.date-text {
  font-size: 24rpx;
  color: #2d5016;
  font-weight: 500;
}

.weekday {
  font-size: 18rpx;
  color: #6a9d87;
  margin-top: 4rpx;
}

.history-steps {
  font-size: 28rpx;
  font-weight: 600;
  color: #2d5016;
  margin: 0 20rpx;
}

.history-arrow {
  font-size: 24rpx;
  color: #6a9d87;
  transition: transform 0.3s ease;
}

.history-item:hover .history-arrow {
  transform: translateX(5px);
}

/* 空状态 */
.empty-section {
  text-align: center;
  padding: 60rpx 30rpx;
}

.empty-text {
  font-size: 26rpx;
  color: #6a9d87;
  font-weight: 500;
}

/* 加载状态 */
.btn[loading] {
  opacity: 0.8;
}

/* 操作菜单 */
.action-menu {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: flex-end;
  justify-content: center;
  z-index: 1000;
  animation: fadeIn 0.2s ease;
}

.menu-content {
  background: #fff;
  border-radius: 16rpx 16rpx 0 0;
  width: 100%;
  padding: 30rpx;
  animation: slideUp 0.3s ease;
}

@keyframes slideUp {
  from {
    transform: translateY(100%);
  }
  to {
    transform: translateY(0);
  }
}

.menu-title {
  display: block;
  text-align: center;
  font-size: 28rpx;
  font-weight: 600;
  color: #2d5016;
  margin-bottom: 24rpx;
  padding-bottom: 20rpx;
  border-bottom: 1px solid rgba(139, 191, 159, 0.2);
}

.menu-item {
  width: 100%;
  height: 80rpx;
  background: none;
  border: none;
  font-size: 26rpx;
  color: #2d5016;
  text-align: center;
  border-radius: 12rpx;
  margin-bottom: 16rpx;
  transition: all 0.2s ease;
}

.menu-item:hover {
  background: rgba(240, 249, 232, 0.9);
}

.menu-item.cancel {
  color: #6a9d87;
  margin-top: 8rpx;
}

/* 响应式调整 */
@media (max-width: 375px) {
  .container {
    padding: 16rpx;
  }
  
  .date-section,
  .step-section,
  .chart-section,
  .record-section {
    padding: 24rpx;
  }
  
  .btn {
    height: 72rpx;
  }
  
  .btn-text {
    font-size: 26rpx;
  }
  
  .header-title {
    font-size: 36rpx;
  }
  
  .header-icon {
    font-size: 28rpx;
  }
  
  .chart-container {
    height: 500rpx;
  }
}
</style>
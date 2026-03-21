<template>
  <view class="container">

    <!-- 背景装饰圆 -->
    <view class="deco-circle c1"></view>
    <view class="deco-circle c2"></view>
    <view class="deco-circle c3"></view>

    <!-- Logo 区域 -->
    <view class="logo-area">
      <view class="logo-ring">
        <text class="logo-icon">🌱</text>
      </view>
      <text class="app-name">校园低碳系统</text>
      <text class="app-slogan">绿色出行 · 低碳生活</text>
    </view>

    <!-- 登录卡片 -->
    <view class="glass-card">
      <text class="card-title">欢迎回来</text>

      <view class="field">
        <view class="field-icon-wrap">
          <text class="field-icon">👤</text>
        </view>
        <input
          v-model="stuNo"
          class="field-input"
          type="text"
          placeholder="请输入学号"
          placeholder-class="ph"
        />
      </view>

      <view class="field">
        <view class="field-icon-wrap">
          <text class="field-icon">🔒</text>
        </view>
        <input
          v-model="password"
          class="field-input"
          type="password"
          placeholder="请输入密码"
          placeholder-class="ph"
        />
      </view>

      <button class="btn-sign" @click="handleLogin" :disabled="loading">
        <text class="btn-text">{{ loading ? '登录中...' : 'Sign in' }}</text>
      </button>

      <view class="hint-row">
        <text class="hint-text">🌿 默认密码：123456</text>
      </view>
    </view>

    <!-- 底部标语 -->
    <view class="bottom-tip">
      <text class="bottom-text">共建绿色校园，从我做起</text>
    </view>

  </view>
</template>

<script>
import { login } from '../../utils/request.js';

export default {
  data() {
    return {
      stuNo: '',
      password: '',
      loading: false
    };
  },
  methods: {
    async handleLogin() {
      if (!this.stuNo.trim()) {
        uni.showToast({ title: '请输入学号', icon: 'none' });
        return;
      }
      if (!this.password.trim()) {
        uni.showToast({ title: '请输入密码', icon: 'none' });
        return;
      }
      this.loading = true;
      try {
        const res = await login(this.stuNo.trim(), this.password.trim());
        if (res.code === 200) {
          uni.setStorageSync('stuNo', res.stuNo);
          uni.setStorageSync('studentName', res.name);
          uni.reLaunch({ url: '/pages/index/index' });
        } else {
          uni.showToast({ title: res.message || '登录失败', icon: 'none' });
        }
      } catch (e) {
        uni.showToast({ title: '网络错误，请重试', icon: 'none' });
      } finally {
        this.loading = false;
      }
    }
  }
};
</script>

<style scoped>
.container {
  min-height: 100vh;
  background: linear-gradient(160deg, #1b5e20 0%, #2e7d32 30%, #1565c0 100%);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60rpx 48rpx;
  position: relative;
  overflow: hidden;
}

/* 背景装饰圆 */
.deco-circle {
  position: absolute;
  border-radius: 50%;
  opacity: 0.12;
  background: #fff;
}
.c1 { width: 500rpx; height: 500rpx; top: -160rpx; right: -140rpx; }
.c2 { width: 320rpx; height: 320rpx; bottom: 80rpx; left: -100rpx; }
.c3 { width: 200rpx; height: 200rpx; top: 260rpx; left: 40rpx; opacity: 0.07; }

/* Logo */
.logo-area {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 60rpx;
  z-index: 1;
}

.logo-ring {
  width: 140rpx;
  height: 140rpx;
  border-radius: 50%;
  background: rgba(255,255,255,0.18);
  border: 3rpx solid rgba(255,255,255,0.4);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 24rpx;
  box-shadow: 0 0 40rpx rgba(102,187,106,0.4);
}

.logo-icon { font-size: 72rpx; }

.app-name {
  font-size: 44rpx;
  font-weight: bold;
  color: #fff;
  letter-spacing: 4rpx;
  margin-bottom: 12rpx;
  text-shadow: 0 2rpx 12rpx rgba(0,0,0,0.3);
}

.app-slogan {
  font-size: 24rpx;
  color: rgba(255,255,255,0.7);
  letter-spacing: 2rpx;
}

/* 玻璃卡片 */
.glass-card {
  width: 100%;
  background: rgba(255,255,255,0.14);
  border-radius: 32rpx;
  border: 1rpx solid rgba(255,255,255,0.3);
  padding: 52rpx 44rpx 44rpx;
  box-shadow: 0 16rpx 64rpx rgba(0,0,0,0.25);
  z-index: 1;
}

.card-title {
  display: block;
  font-size: 36rpx;
  font-weight: bold;
  color: #fff;
  text-align: center;
  margin-bottom: 48rpx;
  letter-spacing: 2rpx;
}

/* 输入框 */
.field {
  display: flex;
  align-items: center;
  background: rgba(255,255,255,0.12);
  border-radius: 16rpx;
  border: 1rpx solid rgba(255,255,255,0.25);
  margin-bottom: 28rpx;
  padding: 0 24rpx;
  height: 96rpx;
}

.field-icon-wrap {
  margin-right: 20rpx;
  display: flex;
  align-items: center;
}

.field-icon { font-size: 36rpx; }

.field-input {
  flex: 1;
  height: 96rpx;
  font-size: 28rpx;
  color: #fff;
  background: transparent;
}

.ph { color: rgba(255,255,255,0.45); }

/* 登录按钮 */
.btn-sign {
  width: 100%;
  height: 96rpx;
  margin-top: 20rpx;
  background: linear-gradient(90deg, #66bb6a, #43a047);
  border-radius: 48rpx;
  border: none;
  box-shadow: 0 8rpx 32rpx rgba(67,160,71,0.45);
  display: flex;
  align-items: center;
  justify-content: center;
}

.btn-sign[disabled] {
  background: rgba(102,187,106,0.4);
  box-shadow: none;
}

.btn-text {
  font-size: 32rpx;
  font-weight: bold;
  color: #fff;
  letter-spacing: 4rpx;
}

.hint-row {
  text-align: center;
  margin-top: 28rpx;
}

.hint-text {
  font-size: 22rpx;
  color: rgba(255,255,255,0.5);
}

/* 底部 */
.bottom-tip {
  margin-top: 48rpx;
  z-index: 1;
}

.bottom-text {
  font-size: 24rpx;
  color: rgba(255,255,255,0.5);
  letter-spacing: 2rpx;
}
</style>

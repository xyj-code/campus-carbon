<template>
  <view class="page">
    <!-- Aurora background blobs -->
    <view class="blob b1"></view>
    <view class="blob b2"></view>
    <view class="blob b3"></view>

    <view class="content">
      <!-- Logo -->
      <view class="logo-section">
        <view class="logo-wrap">
          <view class="logo-inner">
            <text class="logo-icon">🌱</text>
          </view>
          <view class="logo-ring r1"></view>
          <view class="logo-ring r2"></view>
        </view>
        <text class="app-name">校园低碳生活</text>
        <text class="app-tag">每一步，都是对地球的承诺</text>
      </view>

      <!-- Login card -->
      <view class="login-card">
        <text class="card-title">欢迎回来</text>
        <text class="card-sub">登录你的低碳账户</text>

        <view class="input-row" :class="{ focused: focusUser }">
          <text class="in-icon">👤</text>
          <input
            class="in-field"
            placeholder="请输入账号"
            placeholder-class="in-ph"
            v-model="username"
            type="text"
            @focus="focusUser = true"
            @blur="focusUser = false"
          />
        </view>

        <view class="input-row" :class="{ focused: focusPass }">
          <text class="in-icon">🔒</text>
          <input
            class="in-field"
            placeholder="请输入密码"
            placeholder-class="in-ph"
            v-model="password"
            password
            @focus="focusPass = true"
            @blur="focusPass = false"
          />
        </view>

        <view class="login-btn" :class="{ loading: loading }" @click="handleLogin">
          <text class="btn-text" v-if="!loading">登 录</text>
          <view class="dots" v-else>
            <view class="dot d1"></view>
            <view class="dot d2"></view>
            <view class="dot d3"></view>
          </view>
        </view>

        <text class="hint">🌿 默认密码：123456</text>
      </view>

      <!-- Bottom tip icons -->
      <view class="tip-row">
        <view class="tip-item">
          <text class="tip-ico">🌍</text>
          <text class="tip-lbl">低碳出行</text>
        </view>
        <view class="tip-item">
          <text class="tip-ico">♻️</text>
          <text class="tip-lbl">绿色生活</text>
        </view>
        <view class="tip-item">
          <text class="tip-ico">🌿</text>
          <text class="tip-lbl">守护自然</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import { login } from '../../utils/request.js';

export default {
  data() {
    return {
      username: '',
      password: '',
      loading: false,
      focusUser: false,
      focusPass: false
    };
  },
  methods: {
    async handleLogin() {
      if (!this.username.trim()) {
        uni.showToast({ title: '请输入账号', icon: 'none' });
        return;
      }
      if (!this.password.trim()) {
        uni.showToast({ title: '请输入密码', icon: 'none' });
        return;
      }
      this.loading = true;
      try {
        const res = await login(this.username.trim(), this.password.trim());
        if (res.code === 200) {
          uni.setStorageSync('username', res.username);
          uni.setStorageSync('userName', res.name);
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
.page {
  min-height: 100vh;
  background: #FFFFFF;
  position: relative;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* Aurora blobs */
.blob {
  position: absolute;
  border-radius: 50%;
  pointer-events: none;
}
.b1 {
  width: 640rpx; height: 640rpx;
  background: radial-gradient(circle, rgba(0, 245, 160, 0.22) 0%, transparent 70%);
  top: -220rpx; right: -200rpx;
}
.b2 {
  width: 480rpx; height: 480rpx;
  background: radial-gradient(circle, rgba(0, 217, 245, 0.15) 0%, transparent 70%);
  bottom: -120rpx; left: -160rpx;
}
.b3 {
  width: 320rpx; height: 320rpx;
  background: radial-gradient(circle, rgba(123, 104, 238, 0.12) 0%, transparent 70%);
  top: 45%; left: 5%;
}

.content {
  width: 100%;
  padding: 0 44rpx;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  align-items: center;
  position: relative;
  z-index: 1;
}

/* ---- Logo ---- */
.logo-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 64rpx;
  margin-top: 80rpx;
}

.logo-wrap {
  position: relative;
  width: 130rpx;
  height: 130rpx;
  margin-bottom: 28rpx;
}

.logo-inner {
  position: absolute;
  top: 15rpx; left: 15rpx;
  width: 100rpx; height: 100rpx;
  border-radius: 32rpx;
  background: linear-gradient(135deg, #00F5A0, #00C896);
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 10rpx 36rpx rgba(0, 200, 150, 0.45);
}

.logo-icon { font-size: 58rpx; }

.logo-ring {
  position: absolute;
  border-radius: 36rpx;
  border: 2rpx solid rgba(0, 200, 150, 0.35);
  animation: ring-pulse 2.2s ease-in-out infinite;
}
.r1 { top: 0; left: 0; width: 130rpx; height: 130rpx; }
.r2 { top: -12rpx; left: -12rpx; width: 154rpx; height: 154rpx; border-color: rgba(0, 200, 150, 0.15); animation-delay: 0.55s; }

@keyframes ring-pulse {
  0%, 100% { transform: scale(1); opacity: 0.7; }
  50% { transform: scale(1.04); opacity: 1; }
}

.app-name {
  font-size: 46rpx;
  font-weight: 700;
  color: #1A2332;
  display: block;
  text-align: center;
  letter-spacing: 2rpx;
}

.app-tag {
  font-size: 26rpx;
  color: #8A9BB0;
  margin-top: 10rpx;
  display: block;
  text-align: center;
}

/* ---- Login card ---- */
.login-card {
  width: 100%;
  background: rgba(255, 255, 255, 0.78);
  backdrop-filter: blur(24rpx);
  -webkit-backdrop-filter: blur(24rpx);
  border: 1.5rpx solid rgba(255, 255, 255, 0.92);
  border-radius: 40rpx;
  padding: 52rpx 44rpx 44rpx;
  box-shadow: 0 20rpx 60rpx rgba(0, 0, 0, 0.08);
  margin-bottom: 44rpx;
}

.card-title {
  font-size: 46rpx;
  font-weight: 700;
  color: #1A2332;
  display: block;
  margin-bottom: 8rpx;
}

.card-sub {
  font-size: 26rpx;
  color: #8A9BB0;
  display: block;
  margin-bottom: 52rpx;
}

.input-row {
  display: flex;
  flex-direction: row;
  align-items: center;
  background: rgba(248, 250, 252, 0.9);
  border: 1.5rpx solid rgba(226, 232, 240, 0.9);
  border-radius: 22rpx;
  padding: 0 28rpx;
  height: 100rpx;
  margin-bottom: 22rpx;
  transition: all 0.25s ease;
}

.input-row.focused {
  border-color: #00C896;
  background: #FFFFFF;
  box-shadow: 0 0 0 4rpx rgba(0, 200, 150, 0.14);
}

.in-icon {
  font-size: 34rpx;
  margin-right: 18rpx;
}

.in-field {
  flex: 1;
  font-size: 30rpx;
  color: #1A2332;
  height: 100%;
}

.in-ph { color: #A0AEC0; }

/* ---- Button ---- */
.login-btn {
  width: 100%;
  height: 104rpx;
  background: linear-gradient(135deg, #00F5A0, #00C896);
  border-radius: 24rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-top: 36rpx;
  box-shadow: 0 10rpx 36rpx rgba(0, 200, 150, 0.42);
  transition: opacity 0.2s ease;
}

.login-btn.loading { opacity: 0.8; }

.btn-text {
  font-size: 34rpx;
  font-weight: 700;
  color: #FFFFFF;
  letter-spacing: 5rpx;
}

.dots { display: flex; gap: 14rpx; }
.dot {
  width: 14rpx; height: 14rpx;
  border-radius: 50%;
  background: #FFFFFF;
  animation: dot-bounce 0.84s ease-in-out infinite;
}
.d1 { animation-delay: 0s; }
.d2 { animation-delay: 0.17s; }
.d3 { animation-delay: 0.34s; }

@keyframes dot-bounce {
  0%, 80%, 100% { transform: scale(0.6); opacity: 0.5; }
  40% { transform: scale(1); opacity: 1; }
}

.hint {
  font-size: 24rpx;
  color: #A0AEC0;
  text-align: center;
  display: block;
  margin-top: 24rpx;
}

/* ---- Bottom tips ---- */
.tip-row {
  display: flex;
  flex-direction: row;
  gap: 48rpx;
  margin-bottom: 60rpx;
}

.tip-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8rpx;
}

.tip-ico { font-size: 38rpx; }

.tip-lbl {
  font-size: 22rpx;
  color: #8A9BB0;
}
</style>

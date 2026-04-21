<template>
  <view class="page">
    <view class="gradient-bg"></view>

    <scroll-view scroll-y class="scroller" :show-scrollbar="false">
      <view class="hero-card">
        <text class="hero-kicker">{{ copy.heroKicker }}</text>
        <text class="hero-title">{{ plan.summary.title }}</text>
        <text class="hero-subtitle">{{ plan.summary.reason }}</text>
        <view class="hero-metrics">
          <view class="metric-chip">
            <text class="metric-label">{{ copy.focusLabel }}</text>
            <text class="metric-value">{{ plan.summary.focusLabel }}</text>
          </view>
          <view class="metric-chip">
            <text class="metric-label">{{ copy.carbonLabel }}</text>
            <text class="metric-value">{{ plan.summary.estimatedCarbonSaving }}g</text>
          </view>
          <view class="metric-chip">
            <text class="metric-label">{{ copy.pointsLabel }}</text>
            <text class="metric-value">{{ plan.summary.estimatedPoints }} pts</text>
          </view>
        </view>
        <view class="hero-footer">
          <text class="hero-status">{{ plan.summary.completionLabel }}</text>
          <text class="hero-time">{{ copy.updatedLabel }} {{ plan.summary.updatedAt }}</text>
        </view>
      </view>

      <view class="composer-card">
        <view class="section-head">
          <text class="section-title">{{ copy.composerTitle }}</text>
          <text class="section-subtitle">{{ copy.composerSubtitle }}</text>
        </view>
        <scroll-view scroll-x class="chip-scroll" :show-scrollbar="false">
          <view class="chip-row">
            <view class="chip" v-for="note in quickNotes" :key="note" @click="appendQuickNote(note)">
              <text class="chip-text">{{ note }}</text>
            </view>
          </view>
        </scroll-view>
        <textarea
          v-model="userNote"
          class="note-textarea"
          :maxlength="120"
          :placeholder="copy.notePlaceholder"
          :show-confirm-bar="false"
          auto-height
        />
        <view class="composer-actions">
          <view class="ghost-btn" @click="fetchPlan('')">
            <text class="ghost-btn-text">{{ copy.refreshAction }}</text>
          </view>
          <view class="primary-btn" :class="{ disabled: loading }" @click="submitNotePlan">
            <text class="primary-btn-text">{{ loading ? copy.loadingText : copy.generateAction }}</text>
          </view>
        </view>
      </view>

      <view class="actions-card">
        <view class="section-head">
          <text class="section-title">{{ copy.actionsTitle }}</text>
          <text class="section-subtitle">{{ copy.actionsSubtitle }}</text>
        </view>
        <view class="action-list" v-if="plan.actions.length">
          <view class="action-item" v-for="action in plan.actions" :key="action.id">
            <view class="action-top">
              <view class="action-copy">
                <text class="action-tag">{{ action.priorityTag }}</text>
                <text class="action-title">{{ action.title }}</text>
                <text class="action-reason">{{ action.reason }}</text>
              </view>
              <view class="action-points">
                <text class="action-points-value">+{{ action.estimatedPoints }}</text>
                <text class="action-points-label">pts</text>
              </view>
            </view>
            <view class="action-meta">
              <view class="meta-pill">
                <text class="meta-pill-text">{{ action.durationMinutes }} min</text>
              </view>
              <view class="meta-pill">
                <text class="meta-pill-text">{{ action.estimatedCarbonSaving }}g CO2</text>
              </view>
              <view class="meta-pill" v-if="action.actionText">
                <text class="meta-pill-text">{{ action.actionText }}</text>
              </view>
            </view>
            <view class="action-buttons">
              <view class="ghost-btn small" @click="openAction(action)">
                <text class="ghost-btn-text">{{ copy.openAction }}</text>
              </view>
              <view class="primary-btn small" @click="adoptAction(action)">
                <text class="primary-btn-text">{{ copy.adoptAction }}</text>
              </view>
            </view>
          </view>
        </view>
        <view class="empty-card" v-else>
          <text class="empty-title">{{ copy.emptyTitle }}</text>
          <text class="empty-subtitle">{{ copy.emptySubtitle }}</text>
        </view>
      </view>

      <view class="evidence-card" v-if="plan.evidence.length">
        <view class="section-head">
          <text class="section-title">{{ copy.evidenceTitle }}</text>
          <text class="section-subtitle">{{ copy.evidenceSubtitle }}</text>
        </view>
        <view class="evidence-list">
          <view class="evidence-item" v-for="(item, index) in plan.evidence" :key="index">
            <view class="evidence-dot"></view>
            <text class="evidence-text">{{ item }}</text>
          </view>
        </view>
      </view>

      <view class="history-card">
        <view class="section-head">
          <text class="section-title">{{ copy.historyTitle }}</text>
          <text class="section-subtitle">{{ copy.historySubtitle }}</text>
        </view>
        <view class="history-list" v-if="adoptedHistory.length">
          <view class="history-item" v-for="item in adoptedHistory" :key="item.id">
            <view class="history-copy">
              <text class="history-title">{{ item.title }}</text>
              <text class="history-time">{{ item.adoptedAt }}</text>
            </view>
            <view class="history-meta">
              <text class="history-meta-text">{{ item.estimatedCarbonSaving }}g</text>
              <text class="history-meta-text">+{{ item.estimatedPoints }} pts</text>
            </view>
          </view>
        </view>
        <view class="empty-card" v-else>
          <text class="empty-title">{{ copy.historyEmptyTitle }}</text>
          <text class="empty-subtitle">{{ copy.historyEmptySubtitle }}</text>
        </view>
      </view>

      <view class="spacer"></view>
    </scroll-view>
  </view>
</template>

<script>
import { getAgentBrief, getAgentPlan } from '../../utils/request';

const COPY = {
  heroKicker: '\u7eff\u8272\u751f\u6d3b Agent',
  focusLabel: '\u7126\u70b9',
  carbonLabel: '\u9884\u8ba1\u51cf\u6392',
  pointsLabel: '\u9884\u8ba1\u79ef\u5206',
  updatedLabel: '\u66f4\u65b0',
  composerTitle: '\u8865\u5145\u4eca\u65e5\u60c5\u51b5',
  composerSubtitle: '\u8ba9 Agent \u6309\u4f60\u5f53\u4e0b\u7684\u8282\u594f\u91cd\u6392\u8ba1\u5212',
  notePlaceholder: '\u4f8b\u5982\uff1a\u4eca\u5929\u8d76\u8bfe\u3001\u6ca1\u6709\u592a\u591a\u65f6\u95f4\uff0c\u5e0c\u671b\u52a8\u4f5c\u66f4\u8f7b\u91cf',
  refreshAction: '\u6062\u590d\u9ed8\u8ba4\u8ba1\u5212',
  generateAction: '\u6309\u8865\u5145\u8bf4\u660e\u91cd\u6392',
  loadingText: '\u751f\u6210\u4e2d...',
  actionsTitle: '\u4eca\u65e5\u884c\u52a8\u6e05\u5355',
  actionsSubtitle: '\u6bcf\u6761\u5efa\u8bae\u90fd\u53ef\u76f4\u63a5\u91c7\u7eb3\u5e76\u8df3\u8f6c\u6267\u884c',
  openAction: '\u67e5\u770b\u53bb\u5b8c\u6210',
  adoptAction: '\u91c7\u7eb3\u8fd9\u6761',
  emptyTitle: '\u6682\u65e0\u53ef\u7528\u52a8\u4f5c',
  emptySubtitle: '\u53ef\u4ee5\u5148\u8865\u5145\u60c5\u51b5\uff0c\u518d\u8ba9 Agent \u91cd\u65b0\u751f\u6210',
  evidenceTitle: '\u51b3\u7b56\u4f9d\u636e',
  evidenceSubtitle: '\u8ba9\u4f60\u77e5\u9053 Agent \u5728\u8bfb\u53d6\u54ea\u4e9b\u4fe1\u606f',
  historyTitle: '\u6700\u8fd1\u91c7\u7eb3',
  historySubtitle: '\u672c\u5730\u8bb0\u4f4f\u4f60\u6700\u8fd1\u7eb3\u5165\u8ba1\u5212\u7684\u52a8\u4f5c',
  historyEmptyTitle: '\u8fd8\u6ca1\u6709\u91c7\u7eb3\u8bb0\u5f55',
  historyEmptySubtitle: '\u5148\u9009\u4e00\u6761\u52a8\u4f5c\u52a0\u5165\u4eca\u65e5\u8ba1\u5212',
  adoptedToast: '\u5df2\u52a0\u5165\u4eca\u65e5\u8ba1\u5212'
};

const QUICK_NOTES = [
  '\u4eca\u5929\u8d76\u8bfe',
  '\u60f3\u5148\u505a\u6700\u7701\u529b\u7684',
  '\u4eca\u5929\u60f3\u8865\u8db3\u6b65\u6570',
  '\u60f3\u987a\u4fbf\u517c\u987e\u5065\u5eb7'
];

function createEmptyPlan() {
  return {
    summary: {
      title: '\u4eca\u65e5\u884c\u52a8\u5de5\u4f5c\u53f0',
      reason: '\u6b63\u5728\u8bfb\u53d6\u4f60\u7684\u4efb\u52a1\u3001\u5065\u5eb7\u548c\u8fdb\u5ea6\u6570\u636e',
      focusLabel: '\u5f85\u751f\u6210',
      completionLabel: '0/0',
      estimatedCarbonSaving: 0,
      estimatedPoints: 0,
      updatedAt: '--'
    },
    actions: [],
    evidence: []
  };
}

export default {
  data() {
    return {
      copy: COPY,
      quickNotes: QUICK_NOTES,
      userId: '',
      userNote: '',
      loading: false,
      plan: createEmptyPlan(),
      adoptedHistory: []
    };
  },
  onLoad() {
    this.userId = uni.getStorageSync('username') || '';
    this.loadHistory();
  },
  onShow() {
    this.userId = uni.getStorageSync('username') || this.userId;
    this.loadHistory();
    this.fetchPlan('');
  },
  methods: {
    async fetchPlan(note) {
      if (!this.userId) {
        uni.showToast({ title: '\u8bf7\u5148\u767b\u5f55', icon: 'none' });
        return;
      }
      this.loading = true;
      try {
        const requestNote = typeof note === 'string' ? note : this.userNote;
        const result = requestNote
          ? await getAgentPlan(this.userId, requestNote)
          : await getAgentBrief(this.userId);
        this.plan = this.normalizePlan(result);
      } catch (error) {
        uni.showToast({ title: '\u751f\u6210\u8ba1\u5212\u5931\u8d25\uff0c\u8bf7\u91cd\u8bd5', icon: 'none' });
      } finally {
        this.loading = false;
      }
    },
    normalizePlan(result) {
      const nextPlan = createEmptyPlan();
      if (!result || typeof result !== 'object') {
        return nextPlan;
      }
      if (result.summary && typeof result.summary === 'object') {
        nextPlan.summary = {
          ...nextPlan.summary,
          ...result.summary
        };
      }
      nextPlan.actions = Array.isArray(result.actions) ? result.actions : [];
      nextPlan.evidence = Array.isArray(result.evidence) ? result.evidence : [];
      return nextPlan;
    },
    appendQuickNote(note) {
      const current = (this.userNote || '').trim();
      if (!current) {
        this.userNote = note;
        return;
      }
      if (current.includes(note)) {
        return;
      }
      this.userNote = `${current}\uff0c${note}`;
    },
    submitNotePlan() {
      this.fetchPlan(this.userNote.trim());
    },
    openAction(action) {
      if (!action || !action.actionPath) {
        return;
      }
      if (action.actionType === 'tab') {
        uni.switchTab({ url: action.actionPath });
        return;
      }
      uni.navigateTo({ url: action.actionPath });
    },
    adoptAction(action) {
      if (!action) {
        return;
      }
      const history = this.readHistory();
      const entry = {
        id: `${action.id || action.taskCode || 'action'}-${Date.now()}`,
        title: action.title || '',
        estimatedCarbonSaving: Number(action.estimatedCarbonSaving || 0),
        estimatedPoints: Number(action.estimatedPoints || 0),
        adoptedAt: this.formatNow()
      };
      history.unshift(entry);
      this.adoptedHistory = history.slice(0, 8);
      uni.setStorageSync(this.historyKey(), JSON.stringify(this.adoptedHistory));
      uni.showToast({ title: COPY.adoptedToast, icon: 'success' });
      if (action.actionPath) {
        setTimeout(() => {
          this.openAction(action);
        }, 180);
      }
    },
    historyKey() {
      return `agent_history_${this.userId || 'guest'}`;
    },
    readHistory() {
      const raw = uni.getStorageSync(this.historyKey());
      if (!raw) {
        return [];
      }
      try {
        const parsed = typeof raw === 'string' ? JSON.parse(raw) : raw;
        return Array.isArray(parsed) ? parsed : [];
      } catch (error) {
        return [];
      }
    },
    loadHistory() {
      this.adoptedHistory = this.readHistory();
    },
    formatNow() {
      const date = new Date();
      const month = String(date.getMonth() + 1).padStart(2, '0');
      const day = String(date.getDate()).padStart(2, '0');
      const hour = String(date.getHours()).padStart(2, '0');
      const minute = String(date.getMinutes()).padStart(2, '0');
      return `${month}-${day} ${hour}:${minute}`;
    }
  }
};
</script>

<style scoped>
.page {
  min-height: 100vh;
  background: linear-gradient(180deg, #eef6ef 0%, #d7eadc 100%);
  position: relative;
}

.gradient-bg {
  position: fixed;
  inset: 0;
  background:
    radial-gradient(circle at 20% 20%, rgba(84, 166, 109, 0.18), transparent 32%),
    radial-gradient(circle at 85% 10%, rgba(53, 129, 81, 0.12), transparent 28%),
    radial-gradient(circle at 50% 75%, rgba(129, 195, 145, 0.2), transparent 36%);
  pointer-events: none;
}

.scroller {
  position: relative;
  z-index: 1;
  height: 100vh;
  padding: 96rpx 28rpx 0;
  box-sizing: border-box;
}

.hero-card,
.composer-card,
.actions-card,
.evidence-card,
.history-card {
  margin-bottom: 24rpx;
  padding: 28rpx;
  border-radius: 34rpx;
  background: rgba(255, 255, 255, 0.9);
  border: 1rpx solid rgba(255, 255, 255, 0.7);
  box-shadow: 0 18rpx 36rpx rgba(42, 88, 57, 0.08);
  backdrop-filter: blur(18rpx);
}

.hero-card {
  background: linear-gradient(140deg, rgba(41, 112, 63, 0.95), rgba(86, 156, 105, 0.9));
  color: #ffffff;
}

.hero-kicker {
  display: block;
  font-size: 22rpx;
  letter-spacing: 2rpx;
  opacity: 0.85;
}

.hero-title {
  display: block;
  margin-top: 14rpx;
  font-size: 40rpx;
  font-weight: 700;
  line-height: 1.3;
}

.hero-subtitle {
  display: block;
  margin-top: 14rpx;
  font-size: 24rpx;
  line-height: 1.7;
  color: rgba(255, 255, 255, 0.88);
}

.hero-metrics {
  display: flex;
  flex-wrap: wrap;
  gap: 14rpx;
  margin-top: 22rpx;
}

.metric-chip {
  min-width: 180rpx;
  padding: 16rpx 18rpx;
  border-radius: 24rpx;
  background: rgba(255, 255, 255, 0.14);
}

.metric-label {
  display: block;
  font-size: 20rpx;
  color: rgba(255, 255, 255, 0.76);
}

.metric-value {
  display: block;
  margin-top: 8rpx;
  font-size: 28rpx;
  font-weight: 700;
  color: #ffffff;
}

.hero-footer {
  margin-top: 20rpx;
  display: flex;
  justify-content: space-between;
  gap: 16rpx;
}

.hero-status,
.hero-time {
  font-size: 22rpx;
  color: rgba(255, 255, 255, 0.86);
}

.section-head {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 20rpx;
  margin-bottom: 18rpx;
}

.section-title {
  font-size: 30rpx;
  font-weight: 700;
  color: #234b32;
}

.section-subtitle {
  flex: 1;
  text-align: right;
  font-size: 21rpx;
  line-height: 1.5;
  color: #6c8a75;
}

.chip-scroll {
  white-space: nowrap;
  margin-bottom: 18rpx;
}

.chip-row {
  display: flex;
  gap: 12rpx;
}

.chip {
  flex-shrink: 0;
  padding: 12rpx 22rpx;
  border-radius: 999rpx;
  background: rgba(65, 131, 83, 0.1);
  border: 1rpx solid rgba(65, 131, 83, 0.14);
}

.chip-text {
  font-size: 22rpx;
  color: #35674a;
}

.note-textarea {
  width: 100%;
  min-height: 160rpx;
  padding: 22rpx;
  box-sizing: border-box;
  border-radius: 24rpx;
  background: #f4fbf5;
  border: 2rpx solid rgba(73, 142, 93, 0.15);
  font-size: 26rpx;
  color: #234b32;
  line-height: 1.7;
}

.composer-actions,
.action-buttons {
  display: flex;
  gap: 16rpx;
  margin-top: 20rpx;
}

.ghost-btn,
.primary-btn {
  flex: 1;
  height: 82rpx;
  border-radius: 999rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.ghost-btn {
  background: rgba(65, 131, 83, 0.08);
  border: 1rpx solid rgba(65, 131, 83, 0.16);
}

.primary-btn {
  background: linear-gradient(135deg, #3d8c5b, #2c6f46);
  box-shadow: 0 12rpx 24rpx rgba(44, 111, 70, 0.24);
}

.primary-btn.disabled {
  opacity: 0.7;
}

.ghost-btn.small,
.primary-btn.small {
  height: 72rpx;
}

.ghost-btn-text {
  font-size: 24rpx;
  color: #2d6240;
  font-weight: 600;
}

.primary-btn-text {
  font-size: 24rpx;
  color: #ffffff;
  font-weight: 700;
}

.action-list,
.evidence-list,
.history-list {
  display: flex;
  flex-direction: column;
  gap: 16rpx;
}

.action-item,
.history-item {
  padding: 22rpx;
  border-radius: 26rpx;
  background: #f8fbf8;
  border: 1rpx solid rgba(59, 121, 79, 0.08);
}

.action-top,
.history-item {
  display: flex;
  justify-content: space-between;
  gap: 18rpx;
}

.action-copy,
.history-copy {
  flex: 1;
}

.action-tag {
  display: inline-flex;
  padding: 8rpx 14rpx;
  border-radius: 999rpx;
  background: rgba(61, 140, 91, 0.12);
  color: #35674a;
  font-size: 20rpx;
  font-weight: 700;
}

.action-title,
.history-title {
  display: block;
  margin-top: 12rpx;
  font-size: 28rpx;
  font-weight: 700;
  color: #21462f;
}

.action-reason,
.history-time {
  display: block;
  margin-top: 10rpx;
  font-size: 22rpx;
  line-height: 1.6;
  color: #6d8776;
}

.action-points {
  min-width: 110rpx;
  text-align: right;
}

.action-points-value {
  display: block;
  font-size: 30rpx;
  font-weight: 700;
  color: #d58b28;
}

.action-points-label {
  display: block;
  margin-top: 6rpx;
  font-size: 20rpx;
  color: #a8804a;
}

.action-meta,
.history-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 10rpx;
  margin-top: 18rpx;
}

.meta-pill {
  padding: 10rpx 16rpx;
  border-radius: 999rpx;
  background: rgba(61, 140, 91, 0.08);
}

.meta-pill-text,
.history-meta-text {
  font-size: 20rpx;
  color: #3f6e50;
}

.evidence-item {
  display: flex;
  gap: 14rpx;
  align-items: flex-start;
}

.evidence-dot {
  width: 10rpx;
  height: 10rpx;
  margin-top: 12rpx;
  border-radius: 50%;
  background: #4a8e62;
  flex-shrink: 0;
}

.evidence-text {
  flex: 1;
  font-size: 24rpx;
  line-height: 1.7;
  color: #466855;
}

.empty-card {
  padding: 30rpx 16rpx 16rpx;
  text-align: center;
}

.empty-title {
  display: block;
  font-size: 28rpx;
  font-weight: 700;
  color: #2b583b;
}

.empty-subtitle {
  display: block;
  margin-top: 10rpx;
  font-size: 22rpx;
  line-height: 1.6;
  color: #73907b;
}

.spacer {
  height: 56rpx;
}
</style>

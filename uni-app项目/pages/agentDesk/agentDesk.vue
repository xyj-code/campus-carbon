<template>
  <view class="page">
    <view class="gradient-bg"></view>

    <scroll-view scroll-y class="scroller" :show-scrollbar="false">
      <view class="hero-card">
        <text class="hero-kicker">{{ copy.heroKicker }}</text>
        <text class="hero-title">{{ plan.summary.title }}</text>
        <text class="hero-subtitle">{{ plan.summary.reason }}</text>
        <view class="hero-preference" v-if="preferenceEvidence">
          <text class="hero-preference-text">{{ preferenceEvidence }}</text>
        </view>
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
          <text class="hero-status">{{ copy.sessionLabel }} {{ sessionStatusLabel }}</text>
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
              <view class="action-step">
                <text class="action-step-text">0{{ action.stepOrder || 0 }}</text>
              </view>
              <view class="action-copy">
                <view class="action-headline">
                  <text class="action-tag">{{ action.priorityTag || copy.defaultTag }}</text>
                  <text class="action-status">{{ action.statusLabel }}</text>
                </view>
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
            <view class="action-hint">
              <text class="action-hint-text">{{ action.executionHint }}</text>
            </view>
            <view class="result-box" v-if="action.resultNote">
              <text class="result-label">{{ copy.resultLabel }}</text>
              <text class="result-text">{{ action.resultNote }}</text>
            </view>
            <view class="action-buttons">
              <view class="ghost-btn small" v-if="action.actionPath" @click="openAction(action)">
                <text class="ghost-btn-text">{{ copy.openAction }}</text>
              </view>
              <view class="primary-btn small" v-if="action.canStart" @click="startAction(action)">
                <text class="primary-btn-text">{{ copy.startAction }}</text>
              </view>
              <view class="primary-btn small" v-if="action.canFinish" @click="completeAction(action)">
                <text class="primary-btn-text">{{ copy.finishAction }}</text>
              </view>
              <view class="ghost-btn small danger" v-if="action.canSkip" @click="skipAction(action)">
                <text class="ghost-btn-text danger-text">{{ copy.skipAction }}</text>
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

      <view class="spacer"></view>
    </scroll-view>
  </view>
</template>

<script>
import {
  getAgentBrief,
  getAgentPlan,
  startAgentAction,
  completeAgentAction,
  skipAgentAction
} from '../../utils/request';

const COPY = {
  heroKicker: '\u7eff\u8272\u751f\u6d3b Agent',
  focusLabel: '\u7126\u70b9',
  carbonLabel: '\u672a\u5b8c\u6210\u51cf\u6392',
  pointsLabel: '\u672a\u7ed3\u7b97\u79ef\u5206',
  updatedLabel: '\u66f4\u65b0',
  sessionLabel: '\u4f1a\u8bdd\u72b6\u6001',
  composerTitle: '\u8865\u5145\u4eca\u65e5\u60c5\u51b5',
  composerSubtitle: '\u8ba9 Agent \u6839\u636e\u4f60\u5f53\u4e0b\u7684\u8282\u594f\u91cd\u6392\u4efb\u52a1',
  notePlaceholder: '\u4f8b\u5982\uff1a\u4eca\u5929\u8d76\u8bfe\uff0c\u5e0c\u671b\u5148\u5b8c\u6210\u4f4e\u6469\u64e6\u7684\u884c\u52a8',
  refreshAction: '\u5237\u65b0\u6700\u65b0\u72b6\u6001',
  generateAction: '\u751f\u6210\u65b0\u8ba1\u5212',
  loadingText: '\u52a0\u8f7d\u4e2d...',
  actionsTitle: '\u4eca\u65e5 Agent \u6267\u884c\u961f\u5217',
  actionsSubtitle: '\u6309\u987a\u5e8f\u6267\u884c\uff0c\u6bcf\u5b8c\u6210\u4e00\u6b65\u5c31\u4f1a\u89e6\u53d1\u91cd\u6392',
  defaultTag: 'AGENT',
  openAction: '\u6253\u5f00\u6267\u884c\u9875',
  startAction: '\u5f00\u59cb\u8fd9\u4e00\u6b65',
  finishAction: '\u6211\u5df2\u5b8c\u6210',
  skipAction: '\u8df3\u8fc7\u8fd9\u4e00\u6b65',
  resultLabel: '\u6267\u884c\u8bb0\u5f55',
  emptyTitle: '\u6682\u65e0\u53ef\u6267\u884c\u52a8\u4f5c',
  emptySubtitle: '\u53ef\u4ee5\u5148\u8865\u5145\u60c5\u51b5\uff0c\u518d\u8ba9 Agent \u91cd\u65b0\u751f\u6210\u4efb\u52a1',
  evidenceTitle: '\u51b3\u7b56\u4f9d\u636e',
  evidenceSubtitle: '\u8ba9\u4f60\u770b\u5230 Agent \u5f53\u524d\u8bfb\u53d6\u4e86\u54ea\u4e9b\u4fe1\u606f',
  startSuccess: '\u5df2\u8fdb\u5165\u6267\u884c\u72b6\u6001',
  finishSuccess: '\u5df2\u8bb0\u5f55\u5b8c\u6210',
  skipSuccess: '\u5df2\u8df3\u8fc7\u8fd9\u4e00\u6b65',
  requestError: '\u64cd\u4f5c\u5931\u8d25\uff0c\u8bf7\u91cd\u8bd5',
  loginHint: '\u8bf7\u5148\u767b\u5f55'
};

const QUICK_NOTES = [
  '\u4eca\u5929\u8d76\u8bfe',
  '\u5148\u505a\u6700\u7701\u529b\u7684',
  '\u4eca\u5929\u60f3\u8865\u6b65\u6570',
  '\u5e0c\u671b\u987a\u4fbf\u517c\u987e\u5065\u5eb7'
];

function createEmptyPlan() {
  return {
    sessionId: '',
    sessionStatus: 'idle',
    currentActionId: '',
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
      plan: createEmptyPlan()
    };
  },
  onLoad() {
    this.userId = uni.getStorageSync('username') || '';
  },
  onShow() {
    this.userId = uni.getStorageSync('username') || this.userId;
    this.fetchPlan('');
  },
  computed: {
    preferenceEvidence() {
      return Array.isArray(this.plan.evidence)
        ? (this.plan.evidence.find(item => typeof item === 'string' && item.indexOf('\u5df2\u5e94\u7528\u504f\u597d') === 0) || '')
        : '';
    },
    sessionStatusLabel() {
      const status = this.plan.sessionStatus || 'idle';
      if (status === 'in_progress') {
        return '\u6267\u884c\u4e2d';
      }
      if (status === 'completed') {
        return '\u5df2\u5b8c\u6210';
      }
      if (status === 'ready') {
        return '\u5f85\u6267\u884c';
      }
      return '\u672a\u5f00\u59cb';
    }
  },
  methods: {
    async fetchPlan(note) {
      if (!this.userId) {
        uni.showToast({ title: COPY.loginHint, icon: 'none' });
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
        uni.showToast({ title: COPY.requestError, icon: 'none' });
      } finally {
        this.loading = false;
      }
    },
    normalizePlan(result) {
      const nextPlan = createEmptyPlan();
      if (!result || typeof result !== 'object') {
        return nextPlan;
      }
      nextPlan.sessionId = result.sessionId || '';
      nextPlan.sessionStatus = result.sessionStatus || 'idle';
      nextPlan.currentActionId = result.currentActionId || '';
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
    async startAction(action) {
      if (!action || !action.id || !this.plan.sessionId) {
        return;
      }
      this.loading = true;
      try {
        const result = await startAgentAction(this.userId, this.plan.sessionId, action.id, '');
        this.plan = this.normalizePlan(result);
        uni.showToast({ title: COPY.startSuccess, icon: 'success' });
        if (action.actionPath) {
          setTimeout(() => {
            this.openAction(action);
          }, 160);
        }
      } catch (error) {
        uni.showToast({ title: COPY.requestError, icon: 'none' });
      } finally {
        this.loading = false;
      }
    },
    async completeAction(action) {
      if (!action || !action.id || !this.plan.sessionId) {
        return;
      }
      this.loading = true;
      try {
        const result = await completeAgentAction(
          this.userId,
          this.plan.sessionId,
          action.id,
          '\u7528\u6237\u5728\u524d\u7aef\u786e\u8ba4\u5df2\u5b8c\u6210\u8be5\u6b65'
        );
        this.plan = this.normalizePlan(result);
        uni.showToast({ title: COPY.finishSuccess, icon: 'success' });
      } catch (error) {
        uni.showToast({ title: COPY.requestError, icon: 'none' });
      } finally {
        this.loading = false;
      }
    },
    async skipAction(action) {
      if (!action || !action.id || !this.plan.sessionId) {
        return;
      }
      this.loading = true;
      try {
        const result = await skipAgentAction(
          this.userId,
          this.plan.sessionId,
          action.id,
          '\u7528\u6237\u5728\u524d\u7aef\u9009\u62e9\u8df3\u8fc7\u8be5\u6b65'
        );
        this.plan = this.normalizePlan(result);
        uni.showToast({ title: COPY.skipSuccess, icon: 'success' });
      } catch (error) {
        uni.showToast({ title: COPY.requestError, icon: 'none' });
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
.evidence-card {
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

.hero-preference {
  margin-top: 16rpx;
  padding: 14rpx 18rpx;
  border-radius: 22rpx;
  background: rgba(255, 255, 255, 0.14);
}

.hero-preference-text {
  font-size: 22rpx;
  line-height: 1.6;
  color: rgba(255, 255, 255, 0.88);
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
  flex-wrap: wrap;
  gap: 16rpx;
  margin-top: 20rpx;
}

.ghost-btn,
.primary-btn {
  flex: 1;
  min-width: 200rpx;
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

.ghost-btn.danger {
  background: rgba(222, 92, 92, 0.08);
  border-color: rgba(222, 92, 92, 0.16);
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

.ghost-btn-text.danger-text {
  color: #c65a5a;
}

.primary-btn-text {
  font-size: 24rpx;
  color: #ffffff;
  font-weight: 700;
}

.action-list,
.evidence-list {
  display: flex;
  flex-direction: column;
  gap: 16rpx;
}

.action-item {
  padding: 22rpx;
  border-radius: 26rpx;
  background: #f8fbf8;
  border: 1rpx solid rgba(59, 121, 79, 0.08);
}

.action-top {
  display: flex;
  justify-content: space-between;
  gap: 18rpx;
}

.action-step {
  width: 68rpx;
  height: 68rpx;
  border-radius: 20rpx;
  background: linear-gradient(135deg, #3d8c5b, #2c6f46);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.action-step-text {
  font-size: 24rpx;
  font-weight: 700;
  color: #ffffff;
}

.action-copy {
  flex: 1;
}

.action-headline {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 10rpx;
}

.action-tag,
.action-status {
  display: inline-flex;
  padding: 8rpx 14rpx;
  border-radius: 999rpx;
  font-size: 20rpx;
  font-weight: 700;
}

.action-tag {
  background: rgba(61, 140, 91, 0.12);
  color: #35674a;
}

.action-status {
  background: rgba(44, 111, 70, 0.08);
  color: #4c6c58;
}

.action-title {
  display: block;
  margin-top: 12rpx;
  font-size: 28rpx;
  font-weight: 700;
  color: #21462f;
}

.action-reason {
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

.action-meta {
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

.meta-pill-text {
  font-size: 20rpx;
  color: #3f6e50;
}

.action-hint,
.result-box {
  margin-top: 16rpx;
  padding: 18rpx;
  border-radius: 20rpx;
}

.action-hint {
  background: rgba(61, 140, 91, 0.06);
}

.result-box {
  background: rgba(214, 171, 92, 0.08);
}

.action-hint-text,
.result-text {
  font-size: 22rpx;
  line-height: 1.7;
  color: #4d6f59;
}

.result-label {
  display: block;
  font-size: 20rpx;
  font-weight: 700;
  color: #9b7a35;
  margin-bottom: 8rpx;
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

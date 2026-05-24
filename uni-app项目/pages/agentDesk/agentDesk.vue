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
            <text class="metric-value">{{ plan.summary.estimatedPoints }} 积分</text>
          </view>
        </view>
        <view class="hero-footer">
          <text class="hero-status">{{ copy.sessionLabel }} {{ sessionStatusLabel }}</text>
          <text class="hero-time">{{ copy.updatedLabel }} {{ plan.summary.updatedAt }}</text>
        </view>
      </view>

      <view class="progress-card">
        <view class="progress-head">
          <view class="progress-copy">
            <text class="progress-title">{{ copy.progressTitle }}</text>
            <text class="progress-subtitle">{{ progressSubtitle }}</text>
          </view>
          <view class="progress-ring">
            <text class="progress-ring-text">{{ progressPercent }}%</text>
          </view>
        </view>
        <view class="progress-track">
          <view class="progress-fill" :style="{ width: progressPercent + '%' }"></view>
        </view>
        <view class="progress-stats">
          <view class="progress-stat">
            <text class="progress-stat-value">{{ completedCount }}</text>
            <text class="progress-stat-label">{{ copy.progressDone }}</text>
          </view>
          <view class="progress-stat active">
            <text class="progress-stat-value">{{ inProgressCount }}</text>
            <text class="progress-stat-label">{{ copy.progressActive }}</text>
          </view>
          <view class="progress-stat">
            <text class="progress-stat-value">{{ pendingCount }}</text>
            <text class="progress-stat-label">{{ copy.progressPending }}</text>
          </view>
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
        <view class="location-bar">
          <text class="location-text">{{ locationLabel }}</text>
        </view>
        <view class="composer-actions">
          <view class="ghost-btn" @click="captureLocationAndRefresh">
            <text class="ghost-btn-text">{{ copy.captureLocation }}</text>
          </view>
          <view class="ghost-btn" @click="fetchPlan(userNote.trim())">
            <text class="ghost-btn-text">{{ copy.refreshAction }}</text>
          </view>
          <view class="primary-btn" :class="{ disabled: loading }" @click="submitNotePlan">
            <text class="primary-btn-text">{{ loading ? copy.loadingText : copy.generateAction }}</text>
          </view>
        </view>
      </view>

      <view class="schedule-card" v-if="plan.schedule">
        <view class="section-head">
          <text class="section-title">{{ copy.scheduleTitle }}</text>
          <text class="section-subtitle">{{ copy.scheduleSubtitle }}</text>
        </view>
        <view class="schedule-main">
          <view class="schedule-copy">
            <text class="schedule-tag">{{ scheduleStatusLabel }}</text>
            <text class="schedule-title">{{ scheduleTitle }}</text>
            <text class="schedule-reason">{{ scheduleReason }}</text>
          </view>
          <view class="schedule-count">
            <text class="schedule-count-value">{{ plan.schedule.todayCourseCount || 0 }}</text>
            <text class="schedule-count-label">{{ copy.scheduleCountLabel }}</text>
          </view>
        </view>
        <view class="schedule-meta">
          <view class="meta-pill" v-if="plan.schedule.currentCourseTime">
            <text class="meta-pill-text">{{ plan.schedule.currentCourseTime }}</text>
          </view>
          <view class="meta-pill" v-if="plan.schedule.nextCourseTime">
            <text class="meta-pill-text">{{ plan.schedule.nextCourseTime }}</text>
          </view>
          <view class="meta-pill" v-if="hasScheduleFreeMinutes">
            <text class="meta-pill-text">可用 {{ plan.schedule.freeMinutes }} 分钟</text>
          </view>
        </view>
        <view class="venue-actions">
          <view class="ghost-btn small" @click="openSchedulePage">
            <text class="ghost-btn-text">{{ scheduleManageLabel }}</text>
          </view>
        </view>
      </view>

      <view class="weather-card" :class="{ 'weather-pending': !weatherReady }" v-if="plan.weather">
        <view class="section-head">
          <text class="section-title">{{ copy.weatherTitle }}</text>
          <text class="section-subtitle">{{ copy.weatherSubtitle }}</text>
        </view>
        <view class="weather-main">
          <view class="weather-copy">
            <text class="weather-tag">{{ weatherStatusLabel }}</text>
            <text class="weather-title">{{ weatherTitle }}</text>
            <text class="weather-reason">{{ weatherReason }}</text>
          </view>
          <view class="weather-temp" v-if="plan.weather.temperature">
            <text class="weather-temp-value">{{ plan.weather.temperature }}°</text>
          </view>
        </view>
        <view class="weather-meta">
          <view class="meta-pill" v-if="plan.weather.city">
            <text class="meta-pill-text">{{ plan.weather.city }}</text>
          </view>
          <view class="meta-pill" v-if="plan.weather.windDirection || plan.weather.windPower">
            <text class="meta-pill-text">{{ windLabel }}</text>
          </view>
          <view class="meta-pill" v-if="plan.weather.reportTime">
            <text class="meta-pill-text">{{ plan.weather.reportTime }}</text>
          </view>
        </view>
        <view class="venue-actions" v-if="weatherNeedsLocation || weatherCanRetry">
          <view class="ghost-btn small" @click="captureLocationAndRefresh">
            <text class="ghost-btn-text">{{ weatherNeedsLocation ? copy.weatherLocateAction : copy.weatherRetryAction }}</text>
          </view>
        </view>
      </view>

      <view class="venue-card" v-if="plan.sportPlan">
        <view class="section-head">
          <text class="section-title">{{ copy.venueTitle }}</text>
          <text class="section-subtitle">{{ copy.venueSubtitle }}</text>
        </view>
        <view class="venue-header">
          <view class="venue-copy">
            <text class="venue-tag">{{ plan.sportPlan.sportLabel || copy.venueFallback }}</text>
            <text class="venue-name">{{ plan.sportPlan.venueName || copy.venueWaiting }}</text>
            <text class="venue-reason">{{ plan.sportPlan.recommendationReason }}</text>
          </view>
          <view class="venue-badge">
            <text class="venue-badge-text">{{ plan.sportPlan.transportMode || copy.venueRouteLabel }}</text>
          </view>
        </view>
        <view class="venue-meta" v-if="plan.sportPlan.venueName">
          <view class="meta-pill" v-if="plan.sportPlan.venueDistanceMeters">
            <text class="meta-pill-text">{{ plan.sportPlan.venueDistanceMeters }}m</text>
          </view>
          <view class="meta-pill" v-if="plan.sportPlan.routeDurationMinutes">
            <text class="meta-pill-text">{{ plan.sportPlan.routeDurationMinutes }} 分钟</text>
          </view>
          <view class="meta-pill" v-if="plan.sportPlan.routeDistanceMeters">
            <text class="meta-pill-text">{{ plan.sportPlan.routeDistanceMeters }}m 路线</text>
          </view>
        </view>
        <view class="venue-address-box" v-if="plan.sportPlan.venueAddress">
          <text class="venue-address">{{ plan.sportPlan.venueAddress }}</text>
        </view>
        <view class="route-box" v-if="plan.sportPlan.routeSteps && plan.sportPlan.routeSteps.length">
          <text class="route-title">{{ copy.routeTitle }}</text>
          <view class="route-step" v-for="(step, index) in plan.sportPlan.routeSteps" :key="index">
            <view class="route-dot"></view>
            <text class="route-text">{{ step }}</text>
          </view>
        </view>
        <view class="venue-actions">
          <view class="ghost-btn small" v-if="plan.sportPlan.destinationLatitude && plan.sportPlan.destinationLongitude" @click="openSportVenue">
            <text class="ghost-btn-text">{{ copy.venueOpenAction }}</text>
          </view>
        </view>
      </view>

      <view class="actions-card">
        <view class="section-head">
          <text class="section-title">{{ copy.actionsTitle }}</text>
          <text class="section-subtitle">{{ copy.actionsSubtitle }}</text>
        </view>
        <view class="timeline-list" v-if="plan.actions.length">
          <view class="action-item" v-for="action in plan.actions" :key="action.id">
            <view class="timeline-rail">
              <view
                class="timeline-node"
                :class="{
                  done: action.status === 'completed',
                  active: action.status === 'in_progress',
                  skipped: action.status === 'skipped'
                }"
              >
                <text class="timeline-node-text">{{ action.stepOrder || 0 }}</text>
              </view>
              <view class="timeline-line" v-if="(action.stepOrder || 0) < plan.actions.length"></view>
            </view>
            <view class="action-top">
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
                <text class="action-points-label">积分</text>
              </view>
            </view>
            <view class="action-meta">
              <view class="meta-pill">
                <text class="meta-pill-text">{{ action.durationMinutes }} 分钟</text>
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
  captureLocation: '\u83b7\u53d6\u5f53\u524d\u4f4d\u7f6e',
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
  progressTitle: '\u6267\u884c\u8fdb\u5ea6',
  scheduleTitle: '\u8bfe\u8868\u8282\u594f',
  scheduleSubtitle: 'Agent \u4f1a\u6839\u636e\u8bfe\u95f4\u548c\u7a7a\u95f2\u65f6\u95f4\u8c03\u6574\u4efb\u52a1',
  scheduleFallback: '\u6821\u56ed\u65f6\u95f4',
  scheduleCountLabel: '\u4eca\u65e5\u8bfe\u7a0b',
  scheduleManageAction: '\u7ba1\u7406\u8bfe\u8868',
  scheduleAddAction: '\u8865\u5145\u8bfe\u8868',
  scheduleLoginRequired: '\u767b\u5f55\u540e\u8bfb\u53d6',
  weatherTitle: '\u5929\u6c14\u8054\u52a8',
  weatherSubtitle: 'Agent \u4f1a\u6839\u636e\u5929\u6c14\u8c03\u6574\u6237\u5916\u548c\u5ba4\u5185\u4efb\u52a1',
  weatherFallback: '\u73af\u5883\u5224\u65ad',
  weatherLocateAction: '\u83b7\u53d6\u4f4d\u7f6e\u5e76\u91cd\u6392',
  weatherRetryAction: '\u91cd\u65b0\u8bfb\u53d6\u5929\u6c14',
  weatherLocationTitle: '\u5f85\u83b7\u53d6\u4f4d\u7f6e',
  weatherConfigTitle: '\u5929\u6c14\u672a\u914d\u7f6e',
  weatherUnavailableTitle: '\u5929\u6c14\u6682\u4e0d\u53ef\u7528',
  weatherLocationReason: '\u6388\u6743\u4f4d\u7f6e\u540e\uff0cAgent \u4f1a\u91cd\u65b0\u8bfb\u53d6\u5f53\u5730\u5929\u6c14\u5e76\u8c03\u6574\u6237\u5916\u8fd0\u52a8\u4f18\u5148\u7ea7\u3002',
  venueTitle: '\u9644\u8fd1\u8fd0\u52a8\u573a\u5730',
  venueSubtitle: '\u5982\u679c\u4f60\u60f3\u53bb\u8fd0\u52a8\uff0cAgent \u4f1a\u5e2e\u4f60\u627e\u9644\u8fd1\u5408\u9002\u7684\u573a\u5730',
  venueFallback: '\u8fd0\u52a8\u8ba1\u5212',
  venueWaiting: '\u6b63\u5728\u51c6\u5907\u573a\u5730\u63a8\u8350',
  venueRouteLabel: '\u6b65\u884c',
  venueOpenAction: '\u53bb\u8fd9\u91cc',
  routeTitle: '\u8def\u5f84\u8981\u70b9',
  progressDone: '\u5df2\u5b8c\u6210',
  progressActive: '\u6267\u884c\u4e2d',
  progressPending: '\u5f85\u5904\u7406',
  startSuccess: '\u5df2\u8fdb\u5165\u6267\u884c\u72b6\u6001',
  finishSuccess: '\u5df2\u8bb0\u5f55\u5b8c\u6210',
  finishClaimed: '\u5df2\u6309\u7533\u62a5\u8bb0\u5f55\u5b8c\u6210',
  finishRejected: '\u6682\u672a\u68c0\u6d4b\u5230\u5b8c\u6210\u7ed3\u679c',
  skipSuccess: '\u5df2\u8df3\u8fc7\u8fd9\u4e00\u6b65',
  requestError: '\u64cd\u4f5c\u5931\u8d25\uff0c\u8bf7\u91cd\u8bd5',
  loginHint: '\u8bf7\u5148\u767b\u5f55',
  locationHint: '\u672a\u83b7\u53d6\u5230\u4f4d\u7f6e\uff0c\u573a\u5730\u63a8\u8350\u53ef\u80fd\u4e0d\u51c6',
  locationReady: '\u5df2\u83b7\u53d6\u4f4d\u7f6e',
  locationEmpty: '\u672a\u83b7\u53d6\u4f4d\u7f6e\uff0c\u573a\u5730\u63a8\u8350\u4f1a\u53d7\u5f71\u54cd',
  locationRefreshing: '\u5df2\u83b7\u53d6\u4f4d\u7f6e\uff0c\u6b63\u5728\u7ed3\u5408\u5929\u6c14\u91cd\u6392',
  locationDeniedTitle: '\u9700\u8981\u4f4d\u7f6e\u6388\u6743',
  locationDeniedContent: '\u8bf7\u5728\u8bbe\u7f6e\u4e2d\u5141\u8bb8\u4f4d\u7f6e\u6743\u9650\uff0cAgent \u624d\u80fd\u4e3a\u4f60\u641c\u7d22\u9644\u8fd1\u573a\u5730',
  openSettingConfirm: '\u53bb\u8bbe\u7f6e',
  openSettingCancel: '\u7a0d\u540e',
  openLocationError: '\u6253\u5f00\u8def\u7ebf\u89c4\u5212\u5931\u8d25'
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
    sportPlan: null,
    weather: null,
    schedule: null,
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
      currentLocation: null,
      autoLocationTried: false,
      plan: createEmptyPlan()
    };
  },
  onLoad() {
    this.userId = uni.getStorageSync('username') || '';
  },
  onShow() {
    this.userId = uni.getStorageSync('username') || this.userId;
    this.refreshPlanOnShow();
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
    },
    totalCount() {
      return Array.isArray(this.plan.actions) ? this.plan.actions.length : 0;
    },
    completedCount() {
      return this.plan.actions.filter(item => item && item.status === 'completed').length;
    },
    inProgressCount() {
      return this.plan.actions.filter(item => item && item.status === 'in_progress').length;
    },
    pendingCount() {
      return this.plan.actions.filter(item => item && item.status === 'pending').length;
    },
    progressPercent() {
      if (!this.totalCount) {
        return 0;
      }
      return Math.min(100, Math.round((this.completedCount / this.totalCount) * 100));
    },
    progressSubtitle() {
      if (!this.totalCount) {
        return '\u7b49\u5f85 Agent \u751f\u6210\u7b2c\u4e00\u8f6e\u52a8\u4f5c';
      }
      if (this.completedCount === this.totalCount) {
        return '\u672c\u8f6e\u52a8\u4f5c\u5df2\u5168\u90e8\u5b8c\u6210';
      }
      if (this.inProgressCount > 0) {
        return '\u5148\u5b8c\u6210\u5f53\u524d\u6b63\u5728\u6267\u884c\u7684\u8fd9\u4e00\u6b65';
      }
      return '\u6309\u65f6\u95f4\u8f74\u81ea\u4e0a\u800c\u4e0b\u6267\u884c\uff0c\u5b8c\u6210\u540e Agent \u4f1a\u91cd\u6392';
    },
    locationLabel() {
      if (this.currentLocation && typeof this.currentLocation.latitude === 'number' && typeof this.currentLocation.longitude === 'number') {
        return `${COPY.locationReady}: ${this.currentLocation.latitude.toFixed(4)}, ${this.currentLocation.longitude.toFixed(4)}`;
      }
      return COPY.locationEmpty;
    },
    weatherTitle() {
      const weather = this.plan.weather || {};
      if (weather.status === 'config_missing') {
        return COPY.weatherConfigTitle;
      }
      if (this.weatherNeedsLocation) {
        return COPY.weatherLocationTitle;
      }
      if (this.weatherCanRetry) {
        return COPY.weatherUnavailableTitle;
      }
      const city = weather.city || '';
      const text = weather.weather || '';
      if (city && text) {
        return `${city} \u00b7 ${text}`;
      }
      return text || city || COPY.weatherFallback;
    },
    windLabel() {
      const weather = this.plan.weather || {};
      const direction = weather.windDirection || '';
      const power = weather.windPower || '';
      if (direction && power) {
        return `${direction}\u98ce ${power}\u7ea7`;
      }
      return direction || (power ? `${power}\u7ea7` : '');
    },
    weatherReady() {
      const weather = this.plan.weather || {};
      return weather.status === 'ready';
    },
    weatherNeedsLocation() {
      const weather = this.plan.weather || {};
      return weather.status === 'location_required';
    },
    weatherCanRetry() {
      const weather = this.plan.weather || {};
      return weather.status === 'request_failed' || weather.status === 'no_result';
    },
    weatherStatusLabel() {
      const weather = this.plan.weather || {};
      if (weather.status === 'config_missing') {
        return COPY.weatherConfigTitle;
      }
      if (this.weatherNeedsLocation) {
        return COPY.weatherLocationTitle;
      }
      if (this.weatherCanRetry) {
        return COPY.weatherUnavailableTitle;
      }
      return weather.suitability || COPY.weatherFallback;
    },
    weatherReason() {
      const weather = this.plan.weather || {};
      if (this.weatherNeedsLocation) {
        return weather.recommendation || COPY.weatherLocationReason;
      }
      return weather.recommendation || COPY.weatherLocationReason;
    },
    scheduleTitle() {
      const schedule = this.plan.schedule || {};
      if (schedule.currentCourseName) {
        return schedule.currentCourseName;
      }
      if (schedule.nextCourseName) {
        return `${schedule.nextCourseName} \u00b7 ${schedule.minutesToNextCourse || 0}\u5206\u949f`;
      }
      return schedule.stateLabel || COPY.scheduleFallback;
    },
    scheduleStatusLabel() {
      const schedule = this.plan.schedule || {};
      if (schedule.status === 'login_required') {
        return COPY.scheduleLoginRequired;
      }
      return schedule.stateLabel || COPY.scheduleFallback;
    },
    scheduleReason() {
      const schedule = this.plan.schedule || {};
      return schedule.recommendation || COPY.scheduleSubtitle;
    },
    scheduleManageLabel() {
      const schedule = this.plan.schedule || {};
      if (schedule.status === 'ready' && schedule.state === 'no_course' && Number(schedule.todayCourseCount || 0) === 0) {
        return COPY.scheduleAddAction;
      }
      return COPY.scheduleManageAction;
    },
    hasScheduleFreeMinutes() {
      const schedule = this.plan.schedule || {};
      return Number.isFinite(Number(schedule.freeMinutes));
    }
  },
  methods: {
    async refreshPlanOnShow() {
      if (!this.userId) {
        return;
      }
      const note = this.userNote.trim();
      if (this.currentLocation || this.autoLocationTried) {
        this.fetchPlan(note);
        return;
      }
      this.autoLocationTried = true;
      const location = await this.captureLocation(false, false);
      this.fetchPlan(note, location);
    },
    async fetchPlan(note, location = this.currentLocation) {
      if (!this.userId) {
        uni.showToast({ title: COPY.loginHint, icon: 'none' });
        return;
      }
      this.loading = true;
      try {
        const requestNote = typeof note === 'string' ? note : this.userNote;
        const result = requestNote
          ? await getAgentPlan(
            this.userId,
            requestNote,
            location && typeof location.latitude === 'number' ? location.latitude : null,
            location && typeof location.longitude === 'number' ? location.longitude : null
          )
          : await getAgentBrief(
            this.userId,
            location && typeof location.latitude === 'number' ? location.latitude : null,
            location && typeof location.longitude === 'number' ? location.longitude : null
          );
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
      nextPlan.sportPlan = result.sportPlan && typeof result.sportPlan === 'object' ? result.sportPlan : null;
      nextPlan.weather = result.weather && typeof result.weather === 'object' ? result.weather : null;
      nextPlan.schedule = result.schedule && typeof result.schedule === 'object' ? result.schedule : null;
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
    async submitNotePlan() {
      const note = this.userNote.trim();
      const location = await this.ensureLocationForNote(note);
      this.fetchPlan(note, location);
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
        const result = await startAgentAction(
          this.userId,
          this.plan.sessionId,
          action.id,
          '',
          this.currentLocation && typeof this.currentLocation.latitude === 'number' ? this.currentLocation.latitude : null,
          this.currentLocation && typeof this.currentLocation.longitude === 'number' ? this.currentLocation.longitude : null
        );
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
          '\u7528\u6237\u5728\u524d\u7aef\u786e\u8ba4\u5df2\u5b8c\u6210\u8be5\u6b65',
          this.currentLocation && typeof this.currentLocation.latitude === 'number' ? this.currentLocation.latitude : null,
          this.currentLocation && typeof this.currentLocation.longitude === 'number' ? this.currentLocation.longitude : null
        );
        this.plan = this.normalizePlan(result);
        const nextAction = this.findActionById(this.plan.actions, action.id);
        if (nextAction && nextAction.status === 'completed') {
          const toastTitle = this.isClaimAction(nextAction) ? COPY.finishClaimed : COPY.finishSuccess;
          uni.showToast({ title: toastTitle, icon: 'success' });
        } else {
          uni.showToast({ title: COPY.finishRejected, icon: 'none' });
        }
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
          '\u7528\u6237\u5728\u524d\u7aef\u9009\u62e9\u8df3\u8fc7\u8be5\u6b65',
          this.currentLocation && typeof this.currentLocation.latitude === 'number' ? this.currentLocation.latitude : null,
          this.currentLocation && typeof this.currentLocation.longitude === 'number' ? this.currentLocation.longitude : null
        );
        this.plan = this.normalizePlan(result);
        uni.showToast({ title: COPY.skipSuccess, icon: 'success' });
      } catch (error) {
        uni.showToast({ title: COPY.requestError, icon: 'none' });
      } finally {
        this.loading = false;
      }
    },
    findActionById(actions, actionId) {
      if (!Array.isArray(actions)) {
        return null;
      }
      return actions.find(item => item && item.id === actionId) || null;
    },
    isClaimAction(action) {
      const taskCode = action && action.taskCode ? action.taskCode : '';
      return taskCode === 'MAINTAIN_WALK' || taskCode === 'MAINTAIN_INDOOR' || !taskCode;
    },
    ensureLocationForNote(note) {
      if (this.currentLocation && typeof this.currentLocation.latitude === 'number' && typeof this.currentLocation.longitude === 'number') {
        return Promise.resolve(this.currentLocation);
      }
      return this.captureLocation(false, true);
    },
    async captureLocationAndRefresh() {
      const location = await this.captureLocation(true, true);
      if (location) {
        uni.showToast({ title: COPY.locationRefreshing, icon: 'none' });
        this.fetchPlan(this.userNote.trim(), location);
      }
    },
    captureLocation(showSuccess = true, showFailure = true) {
      if (typeof showSuccess !== 'boolean') {
        showSuccess = true;
      }
      if (typeof showFailure !== 'boolean') {
        showFailure = true;
      }
      return new Promise((resolve) => {
        uni.getLocation({
          type: 'gcj02',
          success: (res) => {
            this.currentLocation = {
              latitude: Number(res.latitude),
              longitude: Number(res.longitude)
            };
            if (showSuccess) {
              uni.showToast({ title: COPY.locationReady, icon: 'success' });
            }
            resolve(this.currentLocation);
          },
          fail: (err) => {
            console.error('getLocation failed', err);
            this.currentLocation = null;
            const message = err && err.errMsg ? String(err.errMsg) : '';
            if (message.indexOf('auth deny') !== -1 || message.indexOf('authorize') !== -1 || message.indexOf('auth denied') !== -1) {
              if (!showFailure) {
                resolve(null);
                return;
              }
              this.promptLocationPermission(resolve, showFailure);
              return;
            }
            if (showFailure) {
              uni.showToast({ title: COPY.locationHint, icon: 'none' });
            }
            resolve(null);
          }
        });
      });
    },
    promptLocationPermission(resolve, showFailure = true) {
      uni.showModal({
        title: COPY.locationDeniedTitle,
        content: COPY.locationDeniedContent,
        confirmText: COPY.openSettingConfirm,
        cancelText: COPY.openSettingCancel,
        success: (modalRes) => {
          if (!modalRes.confirm) {
            resolve(null);
            return;
          }
          uni.openSetting({
            success: (settingRes) => {
              const authSetting = settingRes && settingRes.authSetting ? settingRes.authSetting : {};
              if (authSetting['scope.userLocation']) {
                this.captureLocation(false, showFailure).then(resolve);
                return;
              }
              if (showFailure) {
                uni.showToast({ title: COPY.locationHint, icon: 'none' });
              }
              resolve(null);
            },
            fail: () => {
              if (showFailure) {
                uni.showToast({ title: COPY.locationHint, icon: 'none' });
              }
              resolve(null);
            }
          });
        },
        fail: () => {
          resolve(null);
        }
      });
    },
    openSportVenue() {
      const sportPlan = this.plan && this.plan.sportPlan ? this.plan.sportPlan : null;
      if (!sportPlan || typeof sportPlan.destinationLatitude !== 'number' || typeof sportPlan.destinationLongitude !== 'number') {
        return;
      }
      const routePlan = {
        ...sportPlan,
        originLatitude: typeof sportPlan.originLatitude === 'number'
          ? sportPlan.originLatitude
          : (this.currentLocation && typeof this.currentLocation.latitude === 'number' ? this.currentLocation.latitude : null),
        originLongitude: typeof sportPlan.originLongitude === 'number'
          ? sportPlan.originLongitude
          : (this.currentLocation && typeof this.currentLocation.longitude === 'number' ? this.currentLocation.longitude : null)
      };
      uni.setStorageSync('agent_route_plan', routePlan);
      uni.navigateTo({
        url: '/pages/agentRoute/agentRoute',
        fail: () => {
          uni.showToast({ title: COPY.openLocationError, icon: 'none' });
        }
      });
    },
    openSchedulePage() {
      uni.navigateTo({ url: '/pages/schedule/schedule' });
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
.progress-card,
.composer-card,
.schedule-card,
.weather-card,
.venue-card,
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

.venue-card {
  background: linear-gradient(145deg, rgba(243, 250, 244, 0.96), rgba(227, 242, 230, 0.92));
}

.weather-card {
  background: linear-gradient(145deg, rgba(255, 252, 243, 0.96), rgba(235, 246, 238, 0.94));
}

.weather-card.weather-pending {
  background: linear-gradient(145deg, rgba(255, 250, 239, 0.96), rgba(246, 238, 224, 0.94));
}

.schedule-card {
  background: linear-gradient(145deg, rgba(245, 250, 255, 0.96), rgba(230, 243, 236, 0.94));
}

.schedule-main {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 18rpx;
}

.schedule-copy {
  flex: 1;
}

.schedule-tag {
  display: inline-flex;
  align-items: center;
  padding: 8rpx 16rpx;
  border-radius: 999rpx;
  background: rgba(46, 125, 50, 0.12);
  color: #2f7d4c;
  font-size: 22rpx;
  font-weight: 600;
}

.schedule-title {
  display: block;
  margin-top: 16rpx;
  font-size: 34rpx;
  font-weight: 700;
  color: #214d33;
  line-height: 1.35;
}

.schedule-reason {
  display: block;
  margin-top: 12rpx;
  font-size: 24rpx;
  line-height: 1.7;
  color: #607263;
}

.schedule-count {
  min-width: 118rpx;
  min-height: 102rpx;
  padding: 16rpx;
  border-radius: 28rpx;
  background: rgba(255, 255, 255, 0.9);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.schedule-count-value {
  font-size: 40rpx;
  font-weight: 800;
  color: #2f7d4c;
}

.schedule-count-label {
  margin-top: 6rpx;
  font-size: 20rpx;
  color: #6f8778;
}

.schedule-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 12rpx;
  margin-top: 20rpx;
}

.weather-main {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 18rpx;
}

.weather-copy {
  flex: 1;
}

.weather-tag {
  display: inline-flex;
  align-items: center;
  padding: 8rpx 16rpx;
  border-radius: 999rpx;
  background: rgba(213, 139, 40, 0.12);
  color: #a0641e;
  font-size: 22rpx;
  font-weight: 600;
}

.weather-title {
  display: block;
  margin-top: 16rpx;
  font-size: 34rpx;
  font-weight: 700;
  color: #214d33;
  line-height: 1.35;
}

.weather-reason {
  display: block;
  margin-top: 12rpx;
  font-size: 24rpx;
  line-height: 1.7;
  color: #607263;
}

.weather-temp {
  min-width: 118rpx;
  min-height: 102rpx;
  padding: 16rpx;
  border-radius: 28rpx;
  background: rgba(255, 255, 255, 0.9);
  display: flex;
  align-items: center;
  justify-content: center;
}

.weather-temp-value {
  font-size: 40rpx;
  font-weight: 800;
  color: #2f7d4c;
}

.weather-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 12rpx;
  margin-top: 20rpx;
}

.venue-header {
  display: flex;
  justify-content: space-between;
  gap: 18rpx;
}

.venue-copy {
  flex: 1;
}

.venue-tag {
  display: inline-flex;
  align-items: center;
  padding: 8rpx 16rpx;
  border-radius: 999rpx;
  background: rgba(63, 140, 87, 0.14);
  color: #2f7d4c;
  font-size: 22rpx;
  font-weight: 600;
}

.venue-name {
  display: block;
  margin-top: 16rpx;
  font-size: 34rpx;
  font-weight: 700;
  color: #214d33;
  line-height: 1.35;
}

.venue-reason {
  display: block;
  margin-top: 12rpx;
  font-size: 24rpx;
  line-height: 1.7;
  color: #547064;
}

.venue-badge {
  align-self: flex-start;
  padding: 16rpx 18rpx;
  border-radius: 22rpx;
  background: rgba(255, 255, 255, 0.92);
}

.venue-badge-text {
  font-size: 22rpx;
  font-weight: 600;
  color: #2f7d4c;
}

.venue-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 12rpx;
  margin-top: 20rpx;
}

.venue-address-box {
  margin-top: 18rpx;
  padding: 18rpx 20rpx;
  border-radius: 22rpx;
  background: rgba(255, 255, 255, 0.88);
}

.venue-address {
  font-size: 24rpx;
  line-height: 1.7;
  color: #3c5d49;
}

.route-box {
  margin-top: 20rpx;
  padding: 20rpx;
  border-radius: 24rpx;
  background: rgba(255, 255, 255, 0.88);
}

.route-title {
  display: block;
  font-size: 24rpx;
  font-weight: 600;
  color: #2d6d43;
}

.route-step {
  display: flex;
  align-items: flex-start;
  gap: 14rpx;
  margin-top: 16rpx;
}

.route-dot {
  width: 12rpx;
  height: 12rpx;
  margin-top: 12rpx;
  border-radius: 50%;
  background: #56a26c;
  flex-shrink: 0;
}

.route-text {
  flex: 1;
  font-size: 24rpx;
  line-height: 1.7;
  color: #496655;
}

.venue-actions {
  display: flex;
  justify-content: flex-end;
  margin-top: 22rpx;
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

.location-bar {
  margin-top: 18rpx;
  padding: 16rpx 18rpx;
  border-radius: 22rpx;
  background: rgba(236, 244, 237, 0.9);
}

.location-text {
  display: block;
  font-size: 22rpx;
  line-height: 1.6;
  color: #547064;
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

.progress-card {
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.96), rgba(245, 251, 246, 0.94));
}

.progress-head {
  display: flex;
  justify-content: space-between;
  gap: 20rpx;
  align-items: center;
}

.progress-copy {
  flex: 1;
}

.progress-title {
  display: block;
  font-size: 30rpx;
  font-weight: 700;
  color: #21462f;
}

.progress-subtitle {
  display: block;
  margin-top: 10rpx;
  font-size: 22rpx;
  line-height: 1.6;
  color: #688474;
}

.progress-ring {
  width: 110rpx;
  height: 110rpx;
  border-radius: 55rpx;
  background: radial-gradient(circle at center, #ffffff 0%, #ffffff 56%, rgba(61, 140, 91, 0.12) 57%, rgba(61, 140, 91, 0.18) 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: inset 0 0 0 6rpx rgba(61, 140, 91, 0.12);
}

.progress-ring-text {
  font-size: 24rpx;
  font-weight: 700;
  color: #2e6b44;
}

.progress-track {
  width: 100%;
  height: 20rpx;
  margin-top: 22rpx;
  border-radius: 999rpx;
  overflow: hidden;
  background: rgba(61, 140, 91, 0.12);
}

.progress-fill {
  height: 100%;
  border-radius: 999rpx;
  background: linear-gradient(90deg, #3d8c5b, #86c59a);
  box-shadow: 0 8rpx 14rpx rgba(61, 140, 91, 0.18);
}

.progress-stats {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 16rpx;
  margin-top: 22rpx;
}

.progress-stat {
  padding: 18rpx 16rpx;
  border-radius: 22rpx;
  background: rgba(61, 140, 91, 0.06);
  text-align: center;
}

.progress-stat.active {
  background: rgba(213, 139, 40, 0.10);
}

.progress-stat-value {
  display: block;
  font-size: 30rpx;
  font-weight: 700;
  color: #21462f;
}

.progress-stat-label {
  display: block;
  margin-top: 8rpx;
  font-size: 20rpx;
  color: #708879;
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

.timeline-list,
.evidence-list {
  display: flex;
  flex-direction: column;
  gap: 16rpx;
}

.action-item {
  position: relative;
  padding: 22rpx 22rpx 22rpx 106rpx;
  border-radius: 26rpx;
  background: #f8fbf8;
  border: 1rpx solid rgba(59, 121, 79, 0.08);
}

.timeline-rail {
  position: absolute;
  left: 24rpx;
  top: 22rpx;
  bottom: 22rpx;
  width: 56rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.timeline-node {
  width: 56rpx;
  height: 56rpx;
  border-radius: 28rpx;
  background: linear-gradient(135deg, #d4e7d8, #bcd8c4);
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: inset 0 0 0 2rpx rgba(61, 140, 91, 0.10);
}

.timeline-node.done {
  background: linear-gradient(135deg, #3d8c5b, #2c6f46);
}

.timeline-node.active {
  background: linear-gradient(135deg, #d58b28, #c67416);
}

.timeline-node.skipped {
  background: linear-gradient(135deg, #d7dfe0, #b9c3c4);
}

.timeline-node-text {
  font-size: 20rpx;
  font-weight: 700;
  color: #ffffff;
}

.timeline-line {
  flex: 1;
  width: 4rpx;
  margin-top: 10rpx;
  border-radius: 999rpx;
  background: linear-gradient(180deg, rgba(61, 140, 91, 0.28), rgba(61, 140, 91, 0.08));
}

.action-top {
  display: flex;
  justify-content: space-between;
  gap: 18rpx;
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

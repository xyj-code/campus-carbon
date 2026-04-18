<template>
  <view class="page">
    <view class="gradient-bg"></view>

    <scroll-view scroll-y="true" class="scroller" :show-scrollbar="false">
      <view v-if="selectedActivity" class="hero-card">
        <view class="hero-head">
          <view class="hero-copy">
            <text class="hero-tag">{{ copy.activityTag }}</text>
            <text class="hero-title">{{ selectedActivity.bannerTitle || selectedActivity.title }}</text>
            <text class="hero-subtitle">{{ selectedActivity.bannerSubtitle || selectedActivity.subtitle || selectedActivity.description }}</text>
          </view>
          <view class="hero-icon" :style="{ background: heroThemeColor }">
            <text class="hero-icon-text">{{ heroIcon }}</text>
          </view>
        </view>

        <view class="hero-metrics">
          <view class="hero-metric">
            <text class="hero-metric-label">{{ copy.rewardLabel }}</text>
            <text class="hero-metric-value">{{ selectedActivity.rewardText || copy.defaultReward }}</text>
          </view>
          <view class="hero-metric">
            <text class="hero-metric-label">{{ copy.targetLabel }}</text>
            <text class="hero-metric-value">{{ selectedActivity.targetText || copy.defaultTarget }}</text>
          </view>
        </view>

        <view class="hero-action-row">
          <view class="hero-progress-chip">
            <text>{{ activityProgress.progressText || copy.progressFallback }}</text>
          </view>
          <view class="hero-action-btn" v-if="activityProgress.nextActionPath" @tap="goNextAction">
            <text class="hero-action-text">{{ activityProgress.nextActionText || copy.goNow }}</text>
          </view>
        </view>
      </view>

      <view v-else class="empty-card">
        <text class="empty-title">{{ copy.emptyTitle }}</text>
        <text class="empty-subtitle">{{ copy.emptySub }}</text>
      </view>

      <view v-if="activities.length" class="panel">
        <view class="panel-head">
          <text class="panel-title">{{ copy.switchTitle }}</text>
          <text class="panel-subtitle">{{ copy.switchSub }}</text>
        </view>

        <view class="activity-tab-list">
          <view
            v-for="item in activities"
            :key="item.activityCode"
            class="activity-tab"
            :class="{ active: item.activityCode === selectedCode, pending: item.activityCode === pendingCode }"
            @tap.stop="switchActivity(item.activityCode)"
          >
            <text class="activity-tab-icon">{{ item.coverEmoji || leafEmoji }}</text>
            <view class="activity-tab-copy">
              <text class="activity-tab-title">{{ item.title }}</text>
              <text class="activity-tab-subtitle">{{ item.subtitle || copy.tabFallback }}</text>
            </view>
          </view>
        </view>
      </view>

      <view v-if="selectedActivity" class="panel progress-panel">
        <view class="panel-head">
          <view>
            <text class="panel-title">{{ copy.progressTitle }}</text>
            <text class="panel-subtitle">{{ selectedActivity.description || copy.progressSub }}</text>
          </view>
          <view class="progress-ring">
            <text class="progress-ring-value">{{ activityProgress.completionRate || 0 }}%</text>
          </view>
        </view>

        <view class="metric-grid">
          <view class="metric-card">
            <text class="metric-label">{{ copy.metricCompleted }}</text>
            <text class="metric-value">{{ activityProgress.completedCount || 0 }}/{{ activityProgress.taskCount || 0 }}</text>
          </view>
          <view class="metric-card">
            <text class="metric-label">{{ copy.metricReward }}</text>
            <text class="metric-value">{{ activityProgress.completedRewardPoints || 0 }}/{{ activityProgress.totalRewardPoints || 0 }}</text>
          </view>
          <view class="metric-card">
            <text class="metric-label">{{ copy.metricNext }}</text>
            <text class="metric-value metric-value-small">{{ activityProgress.nextTaskTitle || copy.none }}</text>
          </view>
        </view>
      </view>

      <view v-if="selectedActivity && journeySteps.length" class="panel">
        <view class="panel-head">
          <text class="panel-title">{{ copy.journeyTitle }}</text>
          <text class="panel-subtitle">{{ copy.journeySub }}</text>
        </view>

        <view class="journey-list">
          <view v-for="step in journeySteps" :key="step.key" class="journey-card">
            <view class="journey-index">
              <text>{{ step.index }}</text>
            </view>
            <view class="journey-copy">
              <text class="journey-title">{{ step.title }}</text>
              <text class="journey-desc">{{ step.desc }}</text>
            </view>
            <text class="journey-state" :class="{ done: step.done }">{{ step.state }}</text>
          </view>
        </view>
      </view>

      <view v-if="mainlineTasks.length" class="panel">
        <view class="panel-head">
          <text class="panel-title">{{ copy.taskTitle }}</text>
          <text class="panel-subtitle">{{ copy.taskSub }}</text>
        </view>

        <view class="task-list">
          <view v-for="task in mainlineTasks" :key="task.taskCode" class="task-card">
            <view class="task-head">
              <view class="task-icon-wrap" :style="{ background: task.accent }">
                <text class="task-icon">{{ task.icon }}</text>
              </view>
              <view class="task-copy">
                <view class="task-title-row">
                  <text class="task-title">{{ task.title }}</text>
                  <text class="task-badge">{{ task.periodType === 'WEEKLY' ? copy.weeklyLabel : copy.dailyLabel }}</text>
                </view>
                <text class="task-subtitle">{{ task.subtitle }}</text>
              </view>
            </view>

            <view class="task-progress-track">
              <view class="task-progress-fill" :style="{ width: task.progressPercent + '%', background: task.accent }"></view>
            </view>

            <view class="task-foot">
              <view class="task-meta">
                <text class="task-progress-text">{{ task.progressText }}</text>
                <text class="task-status" :class="{ done: task.completed }">{{ task.statusText }}</text>
              </view>
              <view class="task-action" @tap="openTask(task)">
                <text class="task-action-text">{{ task.actionText || copy.goNow }}</text>
              </view>
            </view>
          </view>
        </view>
      </view>

      <view v-if="selectedActivity" class="support-grid">
        <view class="support-card" @tap="goMall">
          <text class="support-title">{{ copy.supportMallTitle }}</text>
          <text class="support-desc">{{ copy.supportMallDesc }}</text>
        </view>
        <view class="support-card support-card-blue" @tap="goRank">
          <text class="support-title">{{ copy.supportRankTitle }}</text>
          <text class="support-desc">{{ copy.supportRankDesc }}</text>
        </view>
      </view>

      <view class="footer-space"></view>
    </scroll-view>
  </view>
</template>

<script>
import { getActivityHub } from '../../utils/request.js';

export default {
  data() {
    return {
      username: '',
      selectedCode: '',
      pendingCode: '',
      hubRequestId: 0,
      loadingHub: false,
      selectedActivity: null,
      activities: [],
      activityProgress: {
        taskCount: 0,
        completedCount: 0,
        completionRate: 0,
        progressText: '',
        totalRewardPoints: 0,
        completedRewardPoints: 0,
        nextTaskTitle: '',
        nextActionText: '',
        nextActionPath: '',
        nextActionType: ''
      },
      taskBoard: {
        summary: {},
        dailyTasks: [],
        weeklyTasks: [],
        allTasks: []
      },
      leafEmoji: '\uD83C\uDF3F',
      copy: {
        activityTag: '\u4e3b\u9898\u6d3b\u52a8',
        rewardLabel: '\u6d3b\u52a8\u5956\u52b1',
        targetLabel: '\u4e3b\u7ebf\u76ee\u6807',
        defaultReward: '\u4efb\u52a1\u79ef\u5206 + \u7eff\u8272\u6743\u76ca + \u6210\u5c31\u89e3\u9501',
        defaultTarget: '\u5b8c\u6210\u5f53\u524d\u6d3b\u52a8\u4e3b\u7ebf\u4efb\u52a1',
        progressFallback: '\u56f4\u7ed5\u4e3b\u7ebf\u4efb\u52a1\u63a8\u8fdb\u6d3b\u52a8\u8fdb\u5ea6',
        goNow: '\u7ee7\u7eed\u5b8c\u6210',
        emptyTitle: '\u5f53\u524d\u6682\u65e0\u53ef\u53c2\u4e0e\u6d3b\u52a8',
        emptySub: '\u53ef\u5728\u8f7b\u91cf\u540e\u53f0\u53d1\u5e03\u65b0\u7684\u4e3b\u9898\u6d3b\u52a8',
        switchTitle: '\u6d3b\u52a8\u4e3b\u7ebf\u5207\u6362',
        switchSub: '\u70b9\u51fb\u4e0d\u540c\u6d3b\u52a8\uff0c\u9875\u9762\u4f1a\u91cd\u65b0\u805a\u7126\u5bf9\u5e94\u4efb\u52a1\u4e0e\u5956\u52b1',
        tabFallback: '\u5207\u6362\u540e\u53ef\u7acb\u5373\u67e5\u770b\u8fd9\u6761\u4e3b\u7ebf',
        progressTitle: '\u4e3b\u7ebf\u8fdb\u5ea6',
        progressSub: '\u56f4\u7ed5\u5f53\u524d\u4e13\u9898\u7684\u4efb\u52a1\u4e0e\u5956\u52b1\u8fdb\u884c\u63a8\u8fdb',
        metricCompleted: '\u5df2\u5b8c\u6210',
        metricReward: '\u5956\u52b1\u79ef\u5206',
        metricNext: '\u4e0b\u4e00\u6b65',
        none: '\u6682\u65e0',
        journeyTitle: '\u6d3b\u52a8\u8def\u5f84',
        journeySub: '\u4ece\u53c2\u4e0e\u4efb\u52a1\u5230\u83b7\u5f97\u5956\u52b1\u4e0e\u6210\u5c31\uff0c\u5f62\u6210\u4e00\u6761\u660e\u786e\u7684\u4e3b\u7ebf',
        taskTitle: '\u4e3b\u7ebf\u4efb\u52a1',
        taskSub: '\u70b9\u51fb\u6309\u94ae\u53ef\u76f4\u63a5\u8df3\u8f6c\u5230\u5bf9\u5e94\u529f\u80fd\u9875',
        dailyLabel: '\u6bcf\u65e5',
        weeklyLabel: '\u6bcf\u5468',
        supportMallTitle: '\u53bb\u7eff\u8272\u6743\u76ca',
        supportMallDesc: '\u628a\u4efb\u52a1\u6210\u679c\u8f6c\u5316\u4e3a\u6821\u56ed\u771f\u5b9e\u6fc0\u52b1',
        supportRankTitle: '\u67e5\u4e3b\u9898\u699c\u5355',
        supportRankDesc: '\u67e5\u770b\u5f53\u524d\u6d3b\u52a8\u4e2d\u7684\u4f4e\u78b3\u6392\u540d',
        switchingText: '\u6b63\u5728\u5207\u6362\u6d3b\u52a8\u4e3b\u7ebf...'
      }
    };
  },
  computed: {
    heroThemeColor() {
      return this.selectedActivity && this.selectedActivity.themeColor
        ? this.selectedActivity.themeColor
        : '#3f9a63';
    },
    heroIcon() {
      return this.selectedActivity && this.selectedActivity.coverEmoji
        ? this.selectedActivity.coverEmoji
        : this.leafEmoji;
    },
    mainlineTasks() {
      return this.taskBoard && this.taskBoard.allTasks ? this.taskBoard.allTasks : [];
    },
    journeySteps() {
      const taskCount = this.activityProgress.taskCount || 0;
      const completedCount = this.activityProgress.completedCount || 0;
      const rewardPoints = this.activityProgress.completedRewardPoints || 0;
      const allDone = taskCount > 0 && completedCount >= taskCount;
      return [
        {
          key: 'task',
          index: '01',
          title: '\u53c2\u4e0e\u4e3b\u7ebf\u4efb\u52a1',
          desc: this.activityProgress.progressText || this.copy.progressFallback,
          state: taskCount > 0 ? completedCount + '/' + taskCount : '\u672a\u5f00\u59cb',
          done: completedCount > 0
        },
        {
          key: 'reward',
          index: '02',
          title: '\u83b7\u5f97\u6d3b\u52a8\u5956\u52b1',
          desc: '\u5f53\u524d\u7d2f\u8ba1 ' + rewardPoints + ' \u79ef\u5206',
          state: rewardPoints > 0 ? '\u5df2\u83b7\u53d6' : '\u5f85\u89e3\u9501',
          done: rewardPoints > 0
        },
        {
          key: 'achievement',
          index: '03',
          title: '\u70b9\u4eae\u6210\u5c31\u8d44\u4ea7',
          desc: '\u8fde\u52a8\u5fbd\u7ae0\u3001\u8bc1\u4e66\u548c\u7eff\u8272\u6743\u76ca\u7684\u6210\u679c\u5c55\u793a',
          state: allDone ? '\u4e3b\u7ebf\u5df2\u8fbe\u6210' : '\u7ee7\u7eed\u63a8\u8fdb',
          done: allDone
        }
      ];
    }
  },
  onLoad(options) {
    const username = uni.getStorageSync('username');
    if (!username) {
      uni.reLaunch({ url: '/pages/login/login' });
      return;
    }
    this.username = username;
    this.selectedCode = options && options.activityCode ? options.activityCode : '';
  },
  onShow() {
    if (this.username) {
      this.loadHub(this.selectedCode);
    }
  },
  methods: {
    async loadHub(activityCode = '') {
      const requestId = ++this.hubRequestId;
      this.loadingHub = true;
      try {
        const res = await getActivityHub(this.username, activityCode || '');
        if (requestId !== this.hubRequestId) {
          return;
        }
        this.activities = res.activities || [];
        this.selectedActivity = res.selected || res.featured || null;
        this.taskBoard = res.taskBoard || {
          summary: {},
          dailyTasks: [],
          weeklyTasks: [],
          allTasks: []
        };
        this.activityProgress = res.activityProgress || this.buildSwitchingProgress(activityCode || '');
        this.selectedCode = this.selectedActivity && this.selectedActivity.activityCode
          ? this.selectedActivity.activityCode
          : '';
        this.pendingCode = '';
      } catch (error) {
        if (requestId === this.hubRequestId) {
          this.pendingCode = '';
          uni.showToast({ title: 'Activity load failed', icon: 'none' });
        }
      } finally {
        if (requestId === this.hubRequestId) {
          this.loadingHub = false;
        }
      }
    },
    buildSwitchingProgress(activityCode) {
      return {
        activityCode: activityCode || '',
        taskCount: 0,
        completedCount: 0,
        completionRate: 0,
        progressText: this.copy.switchingText,
        totalRewardPoints: 0,
        completedRewardPoints: 0,
        nextTaskTitle: '',
        nextActionText: '',
        nextActionPath: '',
        nextActionType: ''
      };
    },
    switchActivity(activityCode) {
      const nextCode = activityCode || '';
      if (!nextCode || nextCode === this.selectedCode) {
        return;
      }
      const nextItem = this.activities.find(item => item && item.activityCode === nextCode) || null;
      this.selectedCode = nextCode;
      this.pendingCode = nextCode;
      if (nextItem) {
        this.selectedActivity = nextItem;
      }
      this.activityProgress = this.buildSwitchingProgress(nextCode);
      this.loadHub(nextCode);
    },
    openTask(task) {
      if (!task || !task.actionPath) {
        return;
      }
      this.goSupport(task.actionPath, task.actionType || 'navigate');
    },
    goNextAction() {
      if (!this.activityProgress.nextActionPath) {
        return;
      }
      this.goSupport(this.activityProgress.nextActionPath, this.activityProgress.nextActionType || 'navigate');
    },
    goSupport(path, type) {
      if (!path) {
        return;
      }
      if (type === 'tab') {
        uni.switchTab({ url: path });
        return;
      }
      uni.navigateTo({ url: path });
    },
    goMall() {
      this.goSupport('/pages/mall/mall', 'tab');
    },
    goRank() {
      this.goSupport('/pages/rank/rank', 'navigate');
    }
  }
};
</script>

<style scoped>
.page {
  min-height: 100vh;
  background: linear-gradient(180deg, #e9f5ec 0%, #d8ecd9 100%);
}

.gradient-bg {
  position: fixed;
  inset: -20%;
  background:
    radial-gradient(circle at 12% 18%, rgba(104, 192, 135, 0.22), transparent 22%),
    radial-gradient(circle at 82% 20%, rgba(251, 197, 103, 0.14), transparent 20%),
    radial-gradient(circle at 70% 78%, rgba(87, 170, 190, 0.14), transparent 18%);
}

.scroller {
  position: relative;
  z-index: 2;
  height: 100vh;
  padding: 24rpx 24rpx 0;
  box-sizing: border-box;
}

.hero-card,
.panel,
.support-card,
.empty-card {
  border-radius: 34rpx;
  border: 1rpx solid rgba(255, 255, 255, 0.76);
  box-shadow: 0 18rpx 34rpx rgba(44, 86, 57, 0.1);
}

.hero-card {
  margin-top: 18rpx;
  padding: 30rpx 28rpx;
  background: linear-gradient(135deg, rgba(42, 115, 68, 0.97), rgba(28, 89, 51, 0.94));
}

.hero-head {
  display: flex;
  gap: 20rpx;
  justify-content: space-between;
}

.hero-copy {
  flex: 1;
}

.hero-tag {
  display: inline-flex;
  height: 44rpx;
  padding: 0 18rpx;
  align-items: center;
  border-radius: 999rpx;
  background: rgba(255, 255, 255, 0.15);
  color: #ffffff;
  font-size: 20rpx;
  font-weight: 700;
}

.hero-title {
  display: block;
  margin-top: 18rpx;
  color: #ffffff;
  font-size: 40rpx;
  line-height: 1.3;
  font-weight: 700;
}

.hero-subtitle {
  display: block;
  margin-top: 12rpx;
  color: rgba(255, 255, 255, 0.78);
  font-size: 24rpx;
  line-height: 1.55;
}

.hero-icon {
  width: 112rpx;
  height: 112rpx;
  border-radius: 30rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 12rpx 24rpx rgba(0, 0, 0, 0.18);
}

.hero-icon-text {
  font-size: 56rpx;
}

.hero-metrics {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 14rpx;
  margin-top: 24rpx;
}

.hero-metric {
  min-height: 110rpx;
  padding: 18rpx 20rpx;
  border-radius: 24rpx;
  background: rgba(255, 255, 255, 0.12);
}

.hero-metric-label {
  display: block;
  color: rgba(255, 255, 255, 0.68);
  font-size: 20rpx;
}

.hero-metric-value {
  display: block;
  margin-top: 10rpx;
  color: #ffffff;
  font-size: 24rpx;
  line-height: 1.45;
  font-weight: 700;
}

.hero-action-row {
  margin-top: 22rpx;
  display: flex;
  gap: 16rpx;
  align-items: center;
}

.hero-progress-chip {
  flex: 1;
  min-height: 60rpx;
  padding: 0 20rpx;
  border-radius: 999rpx;
  background: rgba(255, 255, 255, 0.12);
  display: flex;
  align-items: center;
  color: #ffffff;
  font-size: 22rpx;
  font-weight: 600;
}

.hero-action-btn {
  min-width: 180rpx;
  height: 60rpx;
  padding: 0 22rpx;
  border-radius: 999rpx;
  background: #ffffff;
  display: flex;
  align-items: center;
  justify-content: center;
}

.hero-action-text {
  color: #2d7748;
  font-size: 22rpx;
  font-weight: 700;
}

.empty-card {
  margin-top: 20rpx;
  padding: 42rpx 28rpx;
  text-align: center;
  background: rgba(255, 255, 255, 0.86);
}

.empty-title {
  display: block;
  font-size: 30rpx;
  font-weight: 700;
  color: #234c33;
}

.empty-subtitle {
  display: block;
  margin-top: 12rpx;
  font-size: 22rpx;
  line-height: 1.6;
  color: #6b8977;
}

.panel {
  margin-top: 22rpx;
  padding: 24rpx;
  background: rgba(255, 255, 255, 0.88);
}

.panel-head {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 18rpx;
}

.panel-title {
  display: block;
  color: #224c34;
  font-size: 30rpx;
  font-weight: 700;
}

.panel-subtitle {
  display: block;
  margin-top: 8rpx;
  color: #6d8b79;
  font-size: 22rpx;
  line-height: 1.55;
}

.activity-tab-list {
  display: flex;
  flex-direction: column;
  gap: 14rpx;
  margin-top: 18rpx;
}

.activity-tab {
  display: flex;
  align-items: center;
  gap: 16rpx;
  padding: 18rpx;
  border-radius: 26rpx;
  background: rgba(238, 247, 241, 0.96);
  transition: all 0.24s ease;
}

.activity-tab.pending {
  opacity: 0.88;
}

.activity-tab.active {
  background: linear-gradient(135deg, #68c48c, #3f9a63);
  box-shadow: 0 12rpx 20rpx rgba(63, 154, 99, 0.18);
}

.activity-tab-icon {
  width: 68rpx;
  height: 68rpx;
  border-radius: 20rpx;
  background: rgba(255, 255, 255, 0.42);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32rpx;
  flex-shrink: 0;
}

.activity-tab-copy {
  flex: 1;
}

.activity-tab-title {
  display: block;
  color: #244f36;
  font-size: 24rpx;
  font-weight: 700;
}

.activity-tab-subtitle {
  display: block;
  margin-top: 8rpx;
  color: #6d8b79;
  font-size: 20rpx;
  line-height: 1.5;
}

.activity-tab.active .activity-tab-title,
.activity-tab.active .activity-tab-subtitle {
  color: #ffffff;
}

.progress-panel {
  background: rgba(255, 255, 255, 0.9);
}

.progress-ring {
  width: 108rpx;
  height: 108rpx;
  border-radius: 50%;
  background: linear-gradient(135deg, rgba(104, 192, 135, 0.2), rgba(79, 168, 187, 0.16));
  display: flex;
  align-items: center;
  justify-content: center;
}

.progress-ring-value {
  font-size: 28rpx;
  font-weight: 700;
  color: #2d5d40;
}

.metric-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 14rpx;
  margin-top: 20rpx;
}

.metric-card {
  min-height: 116rpx;
  padding: 18rpx;
  border-radius: 24rpx;
  background: rgba(244, 250, 246, 0.98);
}

.metric-label {
  display: block;
  color: #6b8978;
  font-size: 20rpx;
}

.metric-value {
  display: block;
  margin-top: 8rpx;
  color: #234f35;
  font-size: 30rpx;
  font-weight: 700;
}

.metric-value-small {
  font-size: 22rpx;
  line-height: 1.45;
}

.journey-list,
.task-list {
  display: flex;
  flex-direction: column;
  gap: 14rpx;
  margin-top: 18rpx;
}

.journey-card {
  display: flex;
  align-items: center;
  gap: 16rpx;
  padding: 20rpx 18rpx;
  border-radius: 24rpx;
  background: rgba(244, 250, 246, 0.98);
}

.journey-index {
  width: 72rpx;
  height: 72rpx;
  border-radius: 22rpx;
  background: rgba(104, 192, 135, 0.14);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #3a8d5c;
  font-size: 22rpx;
  font-weight: 700;
  flex-shrink: 0;
}

.journey-copy {
  flex: 1;
}

.journey-title {
  display: block;
  color: #244f36;
  font-size: 24rpx;
  font-weight: 700;
}

.journey-desc {
  display: block;
  margin-top: 8rpx;
  color: #6d8b79;
  font-size: 20rpx;
  line-height: 1.5;
}

.journey-state {
  padding: 8rpx 14rpx;
  border-radius: 999rpx;
  background: rgba(109, 139, 121, 0.12);
  color: #6f8d7c;
  font-size: 18rpx;
}

.journey-state.done {
  background: rgba(80, 181, 116, 0.14);
  color: #35925b;
}

.task-card {
  padding: 22rpx 22rpx 20rpx;
  border-radius: 28rpx;
  background: rgba(247, 252, 249, 0.98);
}

.task-head {
  display: flex;
  align-items: flex-start;
  gap: 16rpx;
}

.task-icon-wrap {
  width: 70rpx;
  height: 70rpx;
  border-radius: 22rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.task-icon {
  font-size: 32rpx;
}

.task-copy {
  flex: 1;
}

.task-title-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12rpx;
}

.task-title {
  color: #244f36;
  font-size: 26rpx;
  font-weight: 700;
}

.task-badge {
  height: 38rpx;
  padding: 0 12rpx;
  border-radius: 999rpx;
  background: rgba(79, 168, 187, 0.12);
  display: inline-flex;
  align-items: center;
  color: #4a8b98;
  font-size: 18rpx;
  font-weight: 700;
}

.task-subtitle {
  display: block;
  margin-top: 8rpx;
  color: #6d8b79;
  font-size: 20rpx;
  line-height: 1.5;
}

.task-progress-track {
  margin-top: 16rpx;
  height: 14rpx;
  border-radius: 999rpx;
  background: rgba(189, 214, 196, 0.55);
  overflow: hidden;
}

.task-progress-fill {
  height: 100%;
  border-radius: 999rpx;
}

.task-foot {
  margin-top: 14rpx;
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 18rpx;
}

.task-meta {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8rpx;
}

.task-progress-text {
  color: #648170;
  font-size: 20rpx;
}

.task-status {
  color: #6d8b79;
  font-size: 20rpx;
}

.task-status.done {
  color: #35925b;
}

.task-action {
  min-width: 154rpx;
  height: 58rpx;
  padding: 0 18rpx;
  border-radius: 999rpx;
  background: linear-gradient(135deg, #68c48c, #419d65);
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 10rpx 18rpx rgba(65, 157, 101, 0.16);
}

.task-action-text {
  color: #ffffff;
  font-size: 20rpx;
  font-weight: 700;
}

.support-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16rpx;
  margin-top: 22rpx;
}

.support-card {
  padding: 24rpx 22rpx;
  background: linear-gradient(135deg, rgba(245, 252, 247, 0.96), rgba(226, 243, 231, 0.92));
}

.support-card-blue {
  background: linear-gradient(135deg, rgba(243, 249, 255, 0.96), rgba(225, 238, 247, 0.92));
}

.support-title {
  display: block;
  color: #244f36;
  font-size: 26rpx;
  font-weight: 700;
}

.support-desc {
  display: block;
  margin-top: 10rpx;
  color: #6c8979;
  font-size: 20rpx;
  line-height: 1.5;
}

.footer-space {
  height: 34rpx;
}
</style>

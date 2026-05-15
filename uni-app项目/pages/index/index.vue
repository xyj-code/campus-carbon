<template>
  <view class="page">
    <!-- 动态渐变背景 -->
    <view class="gradient-bg"></view>

    <!-- 动态粒子系统 -->
    <view class="particle-system">
      <view v-for="(item, index) in 60" :key="index" class="particle" :style="particleStyles[index]"></view>
    </view>

    <scroll-view scroll-y="true" class="scroller" :show-scrollbar="false">
      <!-- ===== 头部卡片 ===== -->
      <view class="header-card floating-card">
        <view class="hd-left">
          <text class="hi-name">{{ greeting }}，{{ studentName }} 🌿</text>
          <text class="hi-date">{{ todayDate }}</text>
        </view>
        <view class="pts-pill" @click="showPointsTip">
          <text class="pts-star">✨</text>
          <text class="pts-num">{{ points }}</text>
          <text class="pts-unit">积分</text>
        </view>
      </view>

      <!-- ===== 主视觉：能量胶囊 + 向外扩张光晕 ===== -->
      <view class="hero-section">
        <view class="capsule-wrap" :class="treeStageClass">
          <!-- 三层旋转光环 -->
          <view class="capsule-outer-ring"></view>
          <view class="capsule-middle-ring"></view>
          <view class="capsule-inner-ring"></view>

          <!-- 向外扩张的生命力光晕层 -->
          <view class="life-glow life-glow-1"></view>
          <view class="life-glow life-glow-2"></view>
          <view class="life-glow life-glow-3"></view>
          <view class="life-glow life-glow-4"></view>
          <view class="life-glow life-glow-5"></view>

          <!-- 向上飘浮的生命粒子 -->
          <view class="life-particle life-particle-1">✨</view>
          <view class="life-particle life-particle-2">⭐</view>
          <view class="life-particle life-particle-3">💚</view>
          <view class="life-particle life-particle-4">🌱</view>
          <view class="life-particle life-particle-5">✨</view>
          <view class="life-particle life-particle-6">⭐</view>

          <view class="capsule" :class="treeStageClass">
            <!-- 闪烁粒子 -->
            <view class="sp sp1"></view>
            <view class="sp sp2"></view>
            <view class="sp sp3"></view>
            <view class="sp sp4"></view>
            <view class="sp sp5"></view>

            <!-- 树冠层 -->
            <view class="tree-aura"></view>
            <view class="leaf l1"></view>
            <view class="leaf l2"></view>
            <view class="leaf l3"></view>
            <view class="leaf l4"></view>
            <view class="leaf l5"></view>
            <view class="trunk"></view>

            <!-- 光泽效果 -->
            <view class="capsule-shine"></view>
            <view class="capsule-bottom-glow"></view>
          </view>
        </view>

        <!-- 统计数据 - 浮动卡片 -->
        <view class="hero-info floating-card">
          <view class="hinfo-item">
            <text class="hinfo-val neon">{{ carbonKg }}kg</text>
            <text class="hinfo-lbl">减少碳排放</text>
          </view>
          <view class="hinfo-div"></view>
          <view class="hinfo-item">
            <text class="hinfo-val">Lv.{{ treeLevel }}</text>
            <text class="hinfo-lbl">{{ treeName }}</text>
          </view>
          <view class="hinfo-div"></view>
          <view class="hinfo-item">
            <text class="hinfo-val">#{{ userRank }}</text>
            <text class="hinfo-lbl">减碳排名</text>
          </view>
        </view>
      </view>

      <!-- ===== 今日数据卡片 ===== -->
      <view class="stats-row">
        <view class="stat-card stat-card-1 floating-card">
          <view class="stat-icon">👟</view>
          <text class="stat-value neon">{{ todayData.steps }}</text>
          <text class="stat-label">今日步数</text>
        </view>
        <view class="stat-card stat-card-2 floating-card">
          <view class="stat-icon">⏱️</view>
          <text class="stat-value">{{ todayData.duration }}</text>
          <text class="stat-label">分钟</text>
        </view>
        <view class="stat-card stat-card-3 floating-card">
          <view class="stat-icon">🌱</view>
          <text class="stat-value">{{ todayData.carbon }}</text>
          <text class="stat-label">减碳(g)</text>
        </view>
      </view>

      <!-- ===== 步行目标进度 ===== -->
      <view class="progress-card floating-card">
        <view class="progress-header">
          <view>
            <text class="progress-title">今日步行目标</text>
            <text class="progress-sub">目标 10,000 步挑战</text>
          </view>
          <view class="progress-percent">
            <text class="percent-value neon">{{ stepPct }}%</text>
          </view>
        </view>

        <view class="progress-track">
          <view class="progress-fill" :style="{ width: stepPct + '%' }">
            <view class="progress-dot"></view>
          </view>
        </view>

        <text class="progress-hint" v-if="stepsLeft > 0">
          🎯 再走 {{ stepsLeft }} 步完成目标
        </text>
        <text class="progress-hint done" v-else>
          🎉 太棒了！今日目标已完成
        </text>
      </view>

      <!-- ===== 任务系统 ===== -->
      <view class="agent-brief-card floating-card" v-if="agentBrief.actions.length" @click="goToAgentDesk">
        <view class="agent-brief-head">
          <view class="agent-brief-copy">
            <text class="agent-brief-kicker">{{ agentBrief.kicker }}</text>
            <text class="agent-brief-title">{{ agentBrief.summary.title }}</text>
            <text class="agent-brief-reason">{{ agentBrief.summary.reason }}</text>
          </view>
          <view class="agent-brief-points">
            <text class="agent-brief-points-value">+{{ agentBrief.summary.estimatedPoints }}</text>
            <text class="agent-brief-points-label">积分</text>
          </view>
        </view>
        <view class="agent-brief-metrics">
          <view class="agent-metric-pill">
            <text class="agent-metric-label">{{ agentBrief.focusLabel }}</text>
            <text class="agent-metric-value">{{ agentBrief.summary.focusLabel }}</text>
          </view>
          <view class="agent-metric-pill">
            <text class="agent-metric-label">{{ agentBrief.carbonLabel }}</text>
            <text class="agent-metric-value">{{ agentBrief.summary.estimatedCarbonSaving }}g</text>
          </view>
          <view class="agent-metric-pill">
            <text class="agent-metric-label">{{ agentBrief.progressLabel }}</text>
            <text class="agent-metric-value">{{ agentBrief.summary.completionLabel }}</text>
          </view>
        </view>
        <view class="agent-action-list">
          <view class="agent-action-item" v-for="action in agentBriefActions" :key="action.id">
            <view class="agent-action-copy">
              <text class="agent-action-tag">{{ action.priorityTag }}</text>
              <text class="agent-action-title">{{ action.title }}</text>
              <text class="agent-action-meta">{{ action.durationMinutes }} 分钟 | {{ action.estimatedCarbonSaving }}g CO2</text>
            </view>
            <text class="agent-action-arrow">{{ agentBrief.actionText }}</text>
          </view>
        </view>
      </view>

      <view class="task-board floating-card" v-if="taskBoard.dailyTasks.length || taskBoard.weeklyTasks.length">
        <view class="task-board-header">
          <view>
            <text class="task-board-title">活动任务</text>
            <text class="task-board-sub">每日目标与每周挑战会自动结算奖励</text>
          </view>
          <view class="task-board-badge">
            <text class="task-board-badge-text">自动奖励</text>
          </view>
        </view>

        <view class="task-summary-row">
          <view class="task-summary-pill">
            <text class="task-summary-label">每日任务</text>
            <text class="task-summary-value">{{ taskBoard.summary.dailyCompleted }}/{{ taskBoard.summary.dailyTotal }}</text>
          </view>
          <view class="task-summary-pill weekly">
            <text class="task-summary-label">每周挑战</text>
            <text class="task-summary-value">{{ taskBoard.summary.weeklyCompleted }}/{{ taskBoard.summary.weeklyTotal }}</text>
          </view>
        </view>

        <view class="task-group" v-if="taskBoard.dailyTasks.length">
          <view class="task-group-head">
            <text class="task-group-title">每日任务</text>
            <text class="task-group-period">{{ taskBoard.summary.dailyPeriodLabel }}</text>
          </view>
          <view class="task-list">
            <view class="task-item" v-for="task in taskBoard.dailyTasks" :key="task.taskCode">
              <view class="task-item-top">
                <view class="task-icon-wrap" :style="{ background: task.accent }">
                  <text class="task-icon">{{ task.icon }}</text>
                </view>
                <view class="task-copy">
                  <text class="task-title">{{ task.title }}</text>
                  <text class="task-subtitle">{{ task.subtitle }}</text>
                </view>
                <view class="task-status-wrap">
                  <text class="task-reward">+{{ task.rewardPoints }}</text>
                  <text class="task-status" :class="{ done: task.completed }">{{ task.statusText }}</text>
                </view>
              </view>
              <view class="task-progress-track">
                <view class="task-progress-fill" :style="{ width: task.progressPercent + '%', background: task.accent }"></view>
              </view>
              <view class="task-progress-row">
                <text class="task-progress-text">{{ task.progressText }}</text>
                <text class="task-progress-percent">{{ task.progressPercent }}%</text>
              </view>
            </view>
          </view>
        </view>

        <view class="task-group" v-if="taskBoard.weeklyTasks.length">
          <view class="task-group-head">
            <text class="task-group-title">每周挑战</text>
            <text class="task-group-period">{{ taskBoard.summary.weeklyPeriodLabel }}</text>
          </view>
          <view class="task-list">
            <view class="task-item weekly" v-for="task in taskBoard.weeklyTasks" :key="task.taskCode">
              <view class="task-item-top">
                <view class="task-icon-wrap" :style="{ background: task.accent }">
                  <text class="task-icon">{{ task.icon }}</text>
                </view>
                <view class="task-copy">
                  <text class="task-title">{{ task.title }}</text>
                  <text class="task-subtitle">{{ task.subtitle }}</text>
                </view>
                <view class="task-status-wrap">
                  <text class="task-reward">+{{ task.rewardPoints }}</text>
                  <text class="task-status" :class="{ done: task.completed }">{{ task.statusText }}</text>
                </view>
              </view>
              <view class="task-progress-track">
                <view class="task-progress-fill" :style="{ width: task.progressPercent + '%', background: task.accent }"></view>
              </view>
              <view class="task-progress-row">
                <text class="task-progress-text">{{ task.progressText }}</text>
                <text class="task-progress-percent">{{ task.progressPercent }}%</text>
              </view>
            </view>
          </view>
        </view>
      </view>

      <!-- ===== 每日AI健康建议卡片 ===== -->
      <view class="daily-health-card floating-card" v-if="dailyHealthSuggestion" @click="goToAiSuggest('health')">
        <view class="dhc-header">
          <view class="dhc-title-wrap">
            <view class="dhc-icon-box">
              <text class="dhc-icon">🩺</text>
            </view>
            <text class="dhc-title">今日AI健康建议</text>
          </view>
          <text class="dhc-arrow">→</text>
        </view>
        <view class="dhc-divider"></view>
        <view class="dhc-content">
          <text class="dhc-text">{{ dailyHealthSuggestion.substring(0, 80) }}...</text>
        </view>
        <view class="dhc-footer">
          <text class="dhc-action">点击查看详情 ✨</text>
        </view>
      </view>

      <!-- ===== 功能导航标题 ===== -->
      <view class="activity-preview-card floating-card" v-if="activityPreview" @click="goToActivityHub">
        <view class="activity-preview-top">
          <text class="activity-preview-tag">{{ activityCopy.title }}</text>
          <text class="activity-preview-action">{{ activityCopy.action }}</text>
        </view>
        <view class="activity-preview-main">
          <view class="activity-preview-icon" :style="{ background: activityPreviewTheme }">
            <text class="activity-preview-emoji">{{ activityPreview.coverEmoji || '\uD83C\uDF3F' }}</text>
          </view>
          <view class="activity-preview-copy">
            <text class="activity-preview-title">{{ activityPreview.bannerTitle || activityPreview.title }}</text>
            <text class="activity-preview-desc">{{ activityPreviewDescription }}</text>
          </view>
        </view>
        <view class="activity-preview-meta">
          <view class="activity-preview-pill">
            <text class="activity-preview-pill-label">{{ activityPreviewTaskText }}</text>
          </view>
          <view class="activity-preview-pill">
            <text class="activity-preview-pill-label">{{ activityPreviewRewardText }}</text>
          </view>
        </view>
        <view class="activity-preview-progress">
          <view class="activity-preview-progress-track">
            <view class="activity-preview-progress-fill" :style="{ width: activityPreviewProgressRate + '%', background: activityPreviewTheme }"></view>
          </view>
          <view class="activity-preview-progress-row">
            <text class="activity-preview-progress-text">{{ activityPreviewProgress.progressText || activityCopy.progressFallback }}</text>
            <text class="activity-preview-progress-value">{{ activityPreviewProgressRate }}%</text>
          </view>
        </view>
        <view class="activity-preview-footer">
          <text class="activity-preview-target">{{ activityPreviewTargetText }}</text>
        </view>
      </view>

      <view class="section-header">
        <view class="section-bar"></view>
        <text class="section-title">功能导航</text>
        <text class="section-sub">探索更多低碳方式</text>
      </view>

      <!-- ===== 2x2 功能导航网格 ===== -->
      <view class="feature-grid">
        <view class="feature-row">
          <view class="feature-card feature-card-ai floating-card" @click="goToAiSuggest('carbon')">
            <view class="feature-icon-container">
              <view class="particle-glow particle-glow-1"></view>
              <view class="particle-glow particle-glow-2"></view>
              <view class="particle-glow particle-glow-3"></view>
              <view class="particle-glow particle-glow-4"></view>
              <view class="particle-glow particle-glow-5"></view>
              <view class="feature-icon-wrapper">
                <view class="feature-outer-ring"></view>
                <view class="feature-middle-ring"></view>
                <view class="feature-inner-ring"></view>
                <view class="feature-icon-box">
                  <text class="feature-icon">🤖</text>
                </view>
              </view>
            </view>
            <text class="feature-name">AI 生活建议</text>
            <text class="feature-desc">智能分析碳足迹</text>
            <view class="feature-arrow">→</view>
            <view class="radial-glow"></view>
          </view>

          <view class="feature-card feature-card-steps floating-card" @click="navigateTo('/pages/stepCount/stepCount')">
            <view class="feature-icon-container">
              <view class="particle-glow particle-glow-1"></view>
              <view class="particle-glow particle-glow-2"></view>
              <view class="particle-glow particle-glow-3"></view>
              <view class="particle-glow particle-glow-4"></view>
              <view class="particle-glow particle-glow-5"></view>
              <view class="feature-icon-wrapper">
                <view class="feature-outer-ring"></view>
                <view class="feature-middle-ring"></view>
                <view class="feature-inner-ring"></view>
                <view class="feature-icon-box">
                  <text class="feature-icon">👟</text>
                </view>
              </view>
            </view>
            <text class="feature-name">步数统计</text>
            <text class="feature-desc">量化绿色出行</text>
            <view class="feature-arrow">→</view>
            <view class="radial-glow"></view>
          </view>
        </view>

        <view class="feature-row">
          <view class="feature-card feature-card-sport floating-card" @click="navigateTo('/pages/sportRecord/sportRecord')">
            <view class="feature-icon-container">
              <view class="particle-glow particle-glow-1"></view>
              <view class="particle-glow particle-glow-2"></view>
              <view class="particle-glow particle-glow-3"></view>
              <view class="particle-glow particle-glow-4"></view>
              <view class="particle-glow particle-glow-5"></view>
              <view class="feature-icon-wrapper">
                <view class="feature-outer-ring"></view>
                <view class="feature-middle-ring"></view>
                <view class="feature-inner-ring"></view>
                <view class="feature-icon-box">
                  <text class="feature-icon">🏃</text>
                </view>
              </view>
            </view>
            <text class="feature-name">运动记录</text>
            <text class="feature-desc">追踪运动轨迹</text>
            <view class="feature-arrow">→</view>
            <view class="radial-glow"></view>
          </view>

          <view class="feature-card feature-card-rank floating-card" @click="navigateTo('/pages/rank/rank')">
            <view class="feature-icon-container">
              <view class="particle-glow particle-glow-1"></view>
              <view class="particle-glow particle-glow-2"></view>
              <view class="particle-glow particle-glow-3"></view>
              <view class="particle-glow particle-glow-4"></view>
              <view class="particle-glow particle-glow-5"></view>
              <view class="feature-icon-wrapper">
                <view class="feature-outer-ring"></view>
                <view class="feature-middle-ring"></view>
                <view class="feature-inner-ring"></view>
                <view class="feature-icon-box">
                  <text class="feature-icon">🏆</text>
                </view>
              </view>
            </view>
            <text class="feature-name">减碳排名</text>
            <text class="feature-desc">减碳榜单</text>
            <view class="feature-arrow">→</view>
            <view class="radial-glow"></view>
          </view>
        </view>

        <view class="feature-row">
          <view v-if="false" class="feature-card feature-card-activity floating-card" @click="navigateTo('/pages/activityHub/activityHub')">
            <view class="feature-icon-container">
              <view class="particle-glow particle-glow-1"></view>
              <view class="particle-glow particle-glow-2"></view>
              <view class="particle-glow particle-glow-3"></view>
              <view class="particle-glow particle-glow-4"></view>
              <view class="particle-glow particle-glow-5"></view>
              <view class="feature-icon-wrapper">
                <view class="feature-outer-ring"></view>
                <view class="feature-middle-ring"></view>
                <view class="feature-inner-ring"></view>
                <view class="feature-icon-box">
                  <text class="feature-icon">🎯</text>
                </view>
              </view>
            </view>
            <text class="feature-name">主题活动</text>
            <text class="feature-desc">统一查看主题活动与任务主线</text>
            <view class="feature-arrow">→</view>
            <view class="radial-glow"></view>
          </view>

          <view class="feature-card feature-card-health floating-card" @click="navigateTo('/pages/healthData/healthData')">
            <view class="feature-icon-container">
              <view class="particle-glow particle-glow-1"></view>
              <view class="particle-glow particle-glow-2"></view>
              <view class="particle-glow particle-glow-3"></view>
              <view class="particle-glow particle-glow-4"></view>
              <view class="particle-glow particle-glow-5"></view>
              <view class="feature-icon-wrapper">
                <view class="feature-outer-ring"></view>
                <view class="feature-middle-ring"></view>
                <view class="feature-inner-ring"></view>
                <view class="feature-icon-box">
                  <text class="feature-icon">❤️</text>
                </view>
              </view>
            </view>
            <text class="feature-name">健康统计</text>
            <text class="feature-desc">管理健康指标</text>
            <view class="feature-arrow">→</view>
            <view class="radial-glow"></view>
          </view>
        </view>
      </view>

      <!-- ===== 成就徽章 ===== -->
      <view class="eco-badge floating-card" v-if="featuredBadge">
        <view class="badge-shimmer"></view>
        <view class="badge-icon">🏅</view>
        <view class="badge-info">
          <text class="badge-title">{{ featuredBadge.name }}</text>
          <text class="badge-text">{{ featuredBadge.subtitle }}</text>
          <text class="badge-time">{{ featuredBadge.timeText }}</text>
        </view>
        <view class="badge-tag">
          <text>已激活</text>
        </view>
      </view>

      <!-- 底部 -->
      <view class="footer">
        <text class="footer-text">🌿 低碳生活，从我做起 · 绿色未来，从你我共建 🌿</text>
      </view>

      <view class="spacer"></view>
    </scroll-view>

    <bottom-nav :currentIndex="0" />
  </view>
</template>

<script>
import { getStepCount, getRankData, getHealthSuggestion, getHealthDataList, getExchangeRecords, getTaskBoard, getProfile, getActivityHub, getAgentBrief } from '../../utils/request.js';
import BottomNav from '../../components/bottom-nav.vue';

export default {
  components: { BottomNav },
  data() {
    return {
      studentName: '',
      stuNo: '',
      todayDate: '',
      greeting: '',
      points: 0,
      todayData: { steps: 0, duration: 0, carbon: 0 },
      carbonKg: '0.000',
      treeLevel: 1,
      treeStage: 'seed',
      treeName: '嫩芽',
      userRank: '--',
      stepPct: 0,
      stepsLeft: 10000,
      featuredBadge: null,
      taskBoard: {
        summary: {
          dailyCompleted: 0,
          dailyTotal: 0,
          weeklyCompleted: 0,
          weeklyTotal: 0,
          dailyPeriodLabel: '',
          weeklyPeriodLabel: ''
        },
        dailyTasks: [],
        weeklyTasks: []
      },
      agentBrief: {
        kicker: '\u4eca\u65e5 Agent \u7b80\u62a5',
        focusLabel: '\u7126\u70b9',
        carbonLabel: '\u9884\u8ba1\u51cf\u6392',
        progressLabel: '\u603b\u8fdb\u5ea6',
        actionText: '\u8fdb\u5165\u5de5\u4f5c\u53f0',
        summary: {
          title: '\u6b63\u5728\u751f\u6210\u4eca\u65e5\u8ba1\u5212',
          reason: '\u8bfb\u53d6\u4f60\u7684\u4efb\u52a1\u3001\u5065\u5eb7\u4e0e\u8fdb\u5ea6\u6570\u636e',
          focusLabel: '\u5f85\u751f\u6210',
          estimatedCarbonSaving: 0,
          estimatedPoints: 0,
          completionLabel: '0/0'
        },
        actions: []
      },
      particleStyles: [],
      // 每日健康建议
      dailyHealthSuggestion: '',
      activityPreview: null,
      activityPreviewProgress: {
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
      activityCopy: {
        title: '\u4e3b\u9898\u6d3b\u52a8',
        action: '\u67e5\u770b\u4e3b\u7ebf',
        progressLabel: '\u4e3b\u7ebf\u8fdb\u5ea6',
        progressFallback: '\u70b9\u51fb\u8fdb\u5165\u4e13\u9898\u9875\u7ee7\u7eed\u5b8c\u6210\u6d3b\u52a8\u4efb\u52a1',
        rewardFallback: '\u4efb\u52a1\u79ef\u5206\u4e0e\u6210\u5c31\u5956\u52b1',
        targetFallback: '\u67e5\u770b\u5f53\u524d\u6d3b\u52a8\u76ee\u6807',
        descriptionFallback: '\u8fdb\u5165\u4e13\u9898\u9875\u67e5\u770b\u6d3b\u52a8\u5956\u52b1\u3001\u4efb\u52a1\u8def\u5f84\u4e0e\u5b8c\u6210\u60c5\u51b5',
        taskEmpty: '\u6682\u65e0\u4efb\u52a1'
      },
      userId: ''
    };
  },
  onLoad() {
    const stuNo = uni.getStorageSync('username');
    if (!stuNo) {
      uni.reLaunch({ url: '/pages/login/login' });
      return;
    }
    this.stuNo = stuNo;
    this.userId = stuNo;
    this.studentName = uni.getStorageSync('userName') || '同学';
    this.initDate();
    this.initParticleStyles();
  },
  onShow() {
    if (this.stuNo) {
      this.initDate();
      this.loadTodayData().finally(() => {
        this.loadProfileData();
      });
      this.loadRankData();
      this.loadTaskBoard();
      this.loadAgentBrief();
      this.loadDailyHealthSuggestion();
      this.loadActivityPreview();
      this.loadAchievementBadge();
    }
  },
  computed: {
    treeStageClass() {
      return 'tree-stage-' + this.resolveTreeStage(this.treeStage, this.treeLevel);
    },
    activityPreviewTheme() {
      return this.activityPreview && this.activityPreview.themeColor
        ? this.activityPreview.themeColor
        : '#4fa86f';
    },
    activityPreviewDescription() {
      if (!this.activityPreview) {
        return '';
      }
      return this.activityPreview.bannerSubtitle
        || this.activityPreview.subtitle
        || this.activityPreview.description
        || this.activityCopy.descriptionFallback;
    },
    activityPreviewTaskText() {
      const completed = Number(this.activityPreviewProgress.completedCount || 0);
      const total = Number(this.activityPreviewProgress.taskCount || 0);
      if (!total) {
        return this.activityCopy.taskEmpty;
      }
      return `${this.activityCopy.progressLabel} ${completed}/${total}`;
    },
    activityPreviewRewardText() {
      if (this.activityPreview && this.activityPreview.rewardText) {
        return this.activityPreview.rewardText;
      }
      return this.activityCopy.rewardFallback;
    },
    activityPreviewTargetText() {
      if (this.activityPreview && this.activityPreview.targetText) {
        return this.activityPreview.targetText;
      }
      if (this.activityPreviewProgress.nextTaskTitle) {
        return this.activityPreviewProgress.nextTaskTitle;
      }
      return this.activityCopy.targetFallback;
    },
    activityPreviewProgressRate() {
      const rate = Number(this.activityPreviewProgress.completionRate || 0);
      return Math.max(0, Math.min(100, rate));
    },
    agentBriefActions() {
      return Array.isArray(this.agentBrief.actions)
        ? this.agentBrief.actions.slice(0, 2)
        : [];
    }
  },
  methods: {
    initParticleStyles() {
      const styles = [];
      for (let i = 0; i < 60; i++) {
        styles.push({
          left: Math.random() * 100 + '%',
          animationDuration: (8 + Math.random() * 12) + 's',
          animationDelay: Math.random() * 8 + 's',
          width: (2 + Math.random() * 8) + 'rpx',
          height: (2 + Math.random() * 8) + 'rpx',
          opacity: 0.2 + Math.random() * 0.5
        });
      }
      this.particleStyles = styles;
    },
    normalizeNumber(value, digits = 0) {
      const num = Number(value);
      if (!Number.isFinite(num)) {
        return digits > 0 ? (0).toFixed(digits) : 0;
      }
      return digits > 0 ? num.toFixed(digits) : num;
    },
    resolveTreeStage(stage, level) {
      if (stage) return stage;
      if (level >= 6) return 'guardian';
      if (level >= 5) return 'canopy';
      if (level >= 4) return 'flourishing';
      if (level >= 3) return 'sapling';
      if (level >= 2) return 'sprout';
      return 'seed';
    },
    async loadProfileData() {
      if (!this.stuNo) return;
      try {
        const profile = await getProfile(this.stuNo);
        if (!profile || profile.code === 404) return;

        this.studentName = profile.name || this.studentName;
        this.points = Number(profile.points || 0);
        this.carbonKg = this.normalizeNumber(profile.totalCarbon, 3);
        this.treeLevel = Number(profile.treeLevel || 1);
        this.treeStage = this.resolveTreeStage(profile.treeStage, this.treeLevel);
        if (profile.treeName) {
          this.treeName = profile.treeName;
        }
      } catch (error) {
        console.error('load profile failed', error);
      }
    },
    initDate() {
      const now = new Date();
      const h = now.getHours();
      if (h < 6) this.greeting = '夜深了';
      else if (h < 12) this.greeting = '早上好';
      else if (h < 14) this.greeting = '中午好';
      else if (h < 18) this.greeting = '下午好';
      else this.greeting = '晚上好';

      const days = ['周日', '周一', '周二', '周三', '周四', '周五', '周六'];
      const months = ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月'];
      this.todayDate = `${days[now.getDay()]}，${months[now.getMonth()]} ${now.getDate()}日`;
    },
    async loadRankData() {
      if (!this.stuNo) return;
      try {
        const res = await getRankData(this.stuNo, 'all');
        if (res && res.code === 200 && res.data && res.data.myRank) {
          this.userRank = res.data.myRank.rank || '--';
        }
      } catch (error) {
        console.error('获取排名数据失败:', error);
      }
    },
    async loadTodayData() {
      try {
        const now = new Date();
        const dateStr = `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}-${String(now.getDate()).padStart(2, '0')}`;
        const res = await getStepCount(this.stuNo, dateStr);
        if (res) {
          const steps = res.steps || 0;
          const duration = res.duration || 0;
          const carbonKg = steps * 0.005 / 100;
          const carbonG = Math.round(carbonKg * 1000);
          this.todayData = { steps, duration, carbon: carbonG };
          this.stepPct = Math.min(Math.round((steps / 10000) * 100), 100);
          this.stepsLeft = Math.max(10000 - steps, 0);

          if (steps >= 15000) { this.treeLevel = 5; this.treeName = '参天大树'; }
          else if (steps >= 10000) { this.treeLevel = 4; this.treeName = '茁壮成长'; }
          else if (steps >= 6000) { this.treeLevel = 3; this.treeName = '小树苗'; }
          else if (steps >= 3000) { this.treeLevel = 2; this.treeName = '破土而出'; }
          else { this.treeLevel = 1; this.treeName = '嫩芽'; }
        }
      } catch (e) {
        console.error('获取今日数据失败:', e);
      }
    },
    async loadAchievementBadge() {
      try {
        const records = await getExchangeRecords(this.stuNo);
        const badgeRecord = (records || []).find(item => item.productCode === 'CERT_LOW_CARBON_STAR');
        if (!badgeRecord) {
          this.featuredBadge = null;
          return;
        }

        this.featuredBadge = {
          name: badgeRecord.name || '低碳之星徽章',
          subtitle: badgeRecord.nextStep || '系统已生成徽章记录，可作为低碳成就展示',
          timeText: this.formatBadgeTime(badgeRecord.createTime)
        };
      } catch (e) {
        this.featuredBadge = null;
      }
    },
    async loadTaskBoard() {
      try {
        const res = await getTaskBoard(this.stuNo);
        this.taskBoard = {
          summary: res.summary || {
            dailyCompleted: 0,
            dailyTotal: 0,
            weeklyCompleted: 0,
            weeklyTotal: 0,
            dailyPeriodLabel: '',
            weeklyPeriodLabel: ''
          },
          dailyTasks: res.dailyTasks || [],
          weeklyTasks: res.weeklyTasks || []
        };
      } catch (e) {
        this.taskBoard = {
          summary: {
            dailyCompleted: 0,
            dailyTotal: 0,
            weeklyCompleted: 0,
            weeklyTotal: 0,
            dailyPeriodLabel: '',
            weeklyPeriodLabel: ''
          },
          dailyTasks: [],
          weeklyTasks: []
        };
      }
    },
    async loadAgentBrief() {
      if (!this.stuNo) return;
      try {
        const location = await this.resolveAuthorizedLocation();
        const res = await getAgentBrief(
          this.stuNo,
          location && typeof location.latitude === 'number' ? location.latitude : null,
          location && typeof location.longitude === 'number' ? location.longitude : null
        );
        if (!res || typeof res !== 'object') {
          return;
        }
        this.agentBrief = {
          ...this.agentBrief,
          summary: {
            ...this.agentBrief.summary,
            ...(res.summary || {})
          },
          actions: Array.isArray(res.actions) ? res.actions : []
        };
      } catch (error) {
        console.error('load agent brief failed', error);
      }
    },
    resolveAuthorizedLocation() {
      return new Promise((resolve) => {
        uni.getSetting({
          success: (settingRes) => {
            const authSetting = settingRes && settingRes.authSetting ? settingRes.authSetting : {};
            if (!authSetting['scope.userLocation']) {
              resolve(null);
              return;
            }
            uni.getLocation({
              type: 'gcj02',
              success: (locationRes) => {
                resolve({
                  latitude: Number(locationRes.latitude),
                  longitude: Number(locationRes.longitude)
                });
              },
              fail: () => resolve(null)
            });
          },
          fail: () => resolve(null)
        });
      });
    },
    async loadActivityPreview() {
      if (!this.stuNo) return;
      try {
        const res = await getActivityHub(this.stuNo);
        this.activityPreview = (res && (res.selected || res.featured)) ? (res.selected || res.featured) : null;
        this.activityPreviewProgress = res && res.activityProgress
          ? {
              taskCount: Number(res.activityProgress.taskCount || 0),
              completedCount: Number(res.activityProgress.completedCount || 0),
              completionRate: Number(res.activityProgress.completionRate || 0),
              progressText: res.activityProgress.progressText || '',
              totalRewardPoints: Number(res.activityProgress.totalRewardPoints || 0),
              completedRewardPoints: Number(res.activityProgress.completedRewardPoints || 0),
              nextTaskTitle: res.activityProgress.nextTaskTitle || '',
              nextActionText: res.activityProgress.nextActionText || '',
              nextActionPath: res.activityProgress.nextActionPath || '',
              nextActionType: res.activityProgress.nextActionType || ''
            }
          : {
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
            };
      } catch (error) {
        console.error('load activity preview failed', error);
        this.activityPreview = null;
        this.activityPreviewProgress = {
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
        };
      }
    },
    formatBadgeTime(value) {
      if (!value) {
        return '';
      }
      return '点亮时间 ' + String(value).replace('T', ' ').slice(0, 16);
    },
    navigateTo(url) {
      uni.navigateTo({ url });
    },
    showPointsTip() {
      uni.showToast({ title: `当前积分：${this.points} 分`, icon: 'none', duration: 2000 });
    },
    // 加载每日健康建议
    async loadDailyHealthSuggestion() {
      if (!this.userId) return;
      try {
        // 先检查是否有健康数据
        const healthData = await getHealthDataList(this.userId);
        if (healthData && healthData.length > 0) {
          const result = await getHealthSuggestion(this.userId);
          if (result && result.suggestion) {
            this.dailyHealthSuggestion = result.suggestion;
          }
        }
      } catch (e) {
        console.error('获取每日健康建议失败:', e);
      }
    },
    // 跳转到AI建议页面
    showPointsTip() {
      uni.showToast({
        title: `\u5f53\u524d\u79ef\u5206\uff1a${this.points}\u5206`,
        icon: 'none',
        duration: 2000
      });
    },
    goToActivityHub() {
      const activityCode = this.activityPreview && this.activityPreview.activityCode
        ? encodeURIComponent(this.activityPreview.activityCode)
        : '';
      const url = activityCode
        ? `/pages/activityHub/activityHub?activityCode=${activityCode}`
        : '/pages/activityHub/activityHub';
      uni.navigateTo({ url });
    },
    goToAiSuggest(mode = 'carbon') {
      const safeMode = mode === 'health' ? 'health' : 'carbon';
      uni.navigateTo({ url: `/pages/aiSuggest/aiSuggest?mode=${safeMode}` });
    },
    goToAgentDesk() {
      uni.navigateTo({ url: '/pages/agentDesk/agentDesk' });
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

/* 动态渐变背景 */
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

/* 粒子系统 */
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
  background: radial-gradient(circle, rgba(80, 200, 140, 0.6), rgba(60, 160, 100, 0.2));
  border-radius: 50%;
  animation: floatUp linear infinite;
  box-shadow: 0 0 12rpx rgba(80, 200, 140, 0.4);
}

@keyframes floatUp {
  0% {
    transform: translateY(100vh) translateX(0);
    opacity: 0;
  }
  10% {
    opacity: 0.8;
  }
  90% {
    opacity: 0.5;
  }
  100% {
    transform: translateY(-20vh) translateX(40rpx);
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

/* ===== 浮动动画 ===== */
.floating-card {
  animation: floatCard 3s ease-in-out infinite;
}

@keyframes floatCard {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-6rpx); }
}

.header-card { animation-delay: 0s; }
.hero-info { animation-delay: 0.2s; }
.stat-card-1 { animation-delay: 0.4s; }
.stat-card-2 { animation-delay: 0.6s; }
.stat-card-3 { animation-delay: 0.8s; }
.progress-card { animation-delay: 0.3s; }
.feature-card-ai { animation-delay: 0.1s; }
.feature-card-steps { animation-delay: 0.3s; }
.feature-card-sport { animation-delay: 0.5s; }
.feature-card-rank { animation-delay: 0.7s; }

/* ===== 头部卡片 ===== */
.header-card {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 100rpx;
  margin-bottom: 32rpx;
  padding: 28rpx 32rpx;
  background: linear-gradient(135deg, rgba(200, 230, 210, 0.9), rgba(180, 220, 190, 0.85));
  backdrop-filter: blur(24rpx);
  border-radius: 40rpx;
  border: 1rpx solid rgba(255, 255, 255, 0.6);
  box-shadow: 0 12rpx 32rpx rgba(0, 0, 0, 0.08);
}

.hi-name {
  font-size: 34rpx;
  font-weight: 700;
  color: #2C5A3A;
  display: block;
}

.hi-date {
  font-size: 24rpx;
  color: #5B8C6E;
  margin-top: 6rpx;
  display: block;
}

.pts-pill {
  display: flex;
  align-items: center;
  gap: 8rpx;
  background: linear-gradient(135deg, #6FB88A, #4A9B6E);
  border-radius: 60rpx;
  padding: 12rpx 28rpx;
  box-shadow: 0 4rpx 20rpx rgba(74, 155, 110, 0.4);
  animation: pillPulse 2s ease-in-out infinite;
}

@keyframes pillPulse {
  0%, 100% { transform: scale(1); box-shadow: 0 4rpx 20rpx rgba(74, 155, 110, 0.4); }
  50% { transform: scale(1.05); box-shadow: 0 8rpx 28rpx rgba(74, 155, 110, 0.6); }
}

/* ===== 主视觉区域 ===== */
.hero-section {
  margin-bottom: 32rpx;
  position: relative;
}

.capsule-wrap {
  position: relative;
  display: flex;
  justify-content: center;
  margin-bottom: 28rpx;
  animation: float 3s ease-in-out infinite;
}

@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-12rpx); }
}

/* 三层旋转光环 */
.capsule-outer-ring,
.capsule-middle-ring,
.capsule-inner-ring {
  position: absolute;
  border-radius: 50%;
  pointer-events: none;
}

.capsule-outer-ring {
  width: 220rpx;
  height: 220rpx;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  border: 3rpx solid rgba(80, 200, 140, 0.6);
  animation: ringRotate 12s linear infinite;
  box-shadow: 0 0 20rpx rgba(80, 200, 140, 0.4);
}

.capsule-middle-ring {
  width: 190rpx;
  height: 190rpx;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  border: 3rpx solid rgba(80, 200, 140, 0.5);
  animation: ringRotateReverse 9s linear infinite;
}

.capsule-inner-ring {
  width: 160rpx;
  height: 160rpx;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  border: 3rpx solid rgba(80, 200, 140, 0.4);
  animation: ringRotate 6s linear infinite;
}

@keyframes ringRotate {
  from { transform: translate(-50%, -50%) rotate(0deg); }
  to { transform: translate(-50%, -50%) rotate(360deg); }
}

@keyframes ringRotateReverse {
  from { transform: translate(-50%, -50%) rotate(0deg); }
  to { transform: translate(-50%, -50%) rotate(-360deg); }
}

/* ===== 向外扩张的生命力光晕层 ===== */
.life-glow {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  border-radius: 50%;
  pointer-events: none;
  animation: expandGlow 3s ease-out infinite;
}

.life-glow-1 {
  width: 180rpx;
  height: 180rpx;
  background: radial-gradient(circle, rgba(100, 220, 150, 0.6), rgba(80, 200, 130, 0.3), transparent);
  animation-delay: 0s;
}

.life-glow-2 {
  width: 220rpx;
  height: 220rpx;
  background: radial-gradient(circle, rgba(120, 240, 170, 0.5), rgba(100, 220, 150, 0.2), transparent);
  animation-delay: 0.6s;
}

.life-glow-3 {
  width: 260rpx;
  height: 260rpx;
  background: radial-gradient(circle, rgba(140, 255, 190, 0.4), rgba(120, 240, 170, 0.15), transparent);
  animation-delay: 1.2s;
}

.life-glow-4 {
  width: 300rpx;
  height: 300rpx;
  background: radial-gradient(circle, rgba(160, 255, 210, 0.3), rgba(140, 255, 190, 0.1), transparent);
  animation-delay: 1.8s;
}

.life-glow-5 {
  width: 340rpx;
  height: 340rpx;
  background: radial-gradient(circle, rgba(180, 255, 230, 0.2), rgba(160, 255, 210, 0.05), transparent);
  animation-delay: 2.4s;
}

@keyframes expandGlow {
  0% {
    opacity: 0.8;
    transform: translate(-50%, -50%) scale(0.8);
  }
  50% {
    opacity: 0.5;
    transform: translate(-50%, -50%) scale(1.2);
  }
  100% {
    opacity: 0;
    transform: translate(-50%, -50%) scale(1.6);
  }
}

/* ===== 向上飘浮的生命粒子 ===== */
.life-particle {
  position: absolute;
  font-size: 28rpx;
  pointer-events: none;
  animation: floatUpLife 2.5s ease-out infinite;
  filter: drop-shadow(0 0 8rpx rgba(100, 220, 150, 0.8));
}

.life-particle-1 {
  bottom: 20rpx;
  left: 50%;
  transform: translateX(-50%);
  animation-delay: 0s;
}

.life-particle-2 {
  bottom: 40rpx;
  left: 35%;
  transform: translateX(-50%);
  animation-delay: 0.5s;
}

.life-particle-3 {
  bottom: 10rpx;
  left: 65%;
  transform: translateX(-50%);
  animation-delay: 1s;
}

.life-particle-4 {
  bottom: 50rpx;
  left: 45%;
  transform: translateX(-50%);
  animation-delay: 1.5s;
}

.life-particle-5 {
  bottom: 30rpx;
  left: 55%;
  transform: translateX(-50%);
  animation-delay: 2s;
}

.life-particle-6 {
  bottom: 60rpx;
  left: 40%;
  transform: translateX(-50%);
  animation-delay: 2.5s;
}

@keyframes floatUpLife {
  0% {
    opacity: 1;
    transform: translateX(-50%) translateY(0) scale(1);
  }
  50% {
    opacity: 0.8;
    transform: translateX(-50%) translateY(-60rpx) scale(1.2);
  }
  100% {
    opacity: 0;
    transform: translateX(-50%) translateY(-120rpx) scale(1.5);
  }
}

.capsule {
  width: 160rpx;
  height: 320rpx;
  background: linear-gradient(180deg,
  rgba(140, 210, 160, 0.5) 0%,
  rgba(100, 180, 130, 0.4) 100%);
  border-radius: 80rpx;
  backdrop-filter: blur(8rpx);
  border: 2rpx solid rgba(255, 255, 255, 0.6);
  position: relative;
  overflow: hidden;
  box-shadow: inset 0 0 40rpx rgba(255, 255, 255, 0.3), 0 12rpx 32rpx rgba(0, 0, 0, 0.1);
  z-index: 10;
}

/* 闪烁粒子 */
.sp {
  position: absolute;
  border-radius: 50%;
  background: radial-gradient(circle, #FFFFFF, #A5D6A7);
  animation: sparkle 2s ease-in-out infinite;
}

.sp1 { width: 8rpx; height: 8rpx; top: 50rpx; left: 28rpx; animation-delay: 0s; }
.sp2 { width: 6rpx; height: 6rpx; top: 90rpx; right: 28rpx; animation-delay: 0.4s; }
.sp3 { width: 10rpx; height: 10rpx; top: 150rpx; left: 24rpx; animation-delay: 0.8s; }
.sp4 { width: 6rpx; height: 6rpx; top: 200rpx; right: 30rpx; animation-delay: 1.2s; }
.sp5 { width: 7rpx; height: 7rpx; top: 260rpx; left: 40rpx; animation-delay: 1.6s; }

@keyframes sparkle {
  0%, 100% { opacity: 0.2; transform: scale(0.5); }
  50% { opacity: 1; transform: scale(1.2); box-shadow: 0 0 12rpx rgba(255, 255, 255, 0.8); }
}

/* 树冠层 */
.tree-aura {
  position: absolute;
  width: 100rpx;
  height: 40rpx;
  background: radial-gradient(ellipse, rgba(80, 200, 140, 0.5), transparent);
  border-radius: 50%;
  bottom: 210rpx;
  left: 50%;
  transform: translateX(-50%);
  animation: auraPulse 2s ease-in-out infinite;
}

@keyframes auraPulse {
  0%, 100% { opacity: 0.5; transform: translateX(-50%) scale(1); }
  50% { opacity: 1; transform: translateX(-50%) scale(1.2); }
}

.leaf {
  position: absolute;
  border-radius: 50%;
  left: 50%;
  transform: translateX(-50%);
  transition: all 0.3s ease;
}

.l1 { width: 100rpx; height: 52rpx; background: #2E8B57; bottom: 80rpx; opacity: 0.9; }
.l2 { width: 84rpx; height: 48rpx; background: #3CB371; bottom: 108rpx; opacity: 0.9; }
.l3 { width: 70rpx; height: 42rpx; background: #66CDAA; bottom: 132rpx; opacity: 0.9; }
.l4 { width: 56rpx; height: 36rpx; background: #90EE90; bottom: 154rpx; opacity: 0.9; }
.l5 { width: 42rpx; height: 28rpx; background: #C1FFC1; bottom: 176rpx; opacity: 0.9; }

.trunk {
  position: absolute;
  width: 16rpx;
  height: 44rpx;
  background: linear-gradient(180deg, #8B5A2B, #5D3A1A);
  border-radius: 8rpx;
  bottom: 38rpx;
  left: 50%;
  transform: translateX(-50%);
}

.capsule-shine {
  position: absolute;
  width: 32rpx;
  height: 180rpx;
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.6), rgba(255, 255, 255, 0.1));
  border-radius: 16rpx;
  top: 28rpx;
  left: 20rpx;
  transform: rotate(-8deg);
  pointer-events: none;
}

.capsule-bottom-glow {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 70rpx;
  background: linear-gradient(0deg, rgba(80, 200, 140, 0.2), transparent);
  pointer-events: none;
}

.capsule-wrap.tree-stage-seed .capsule-outer-ring,
.capsule-wrap.tree-stage-seed .capsule-middle-ring,
.capsule-wrap.tree-stage-seed .capsule-inner-ring {
  opacity: 0.45;
}

.capsule-wrap.tree-stage-seed .capsule-outer-ring {
  transform: translate(-50%, -50%) scale(0.82);
}

.capsule-wrap.tree-stage-seed .capsule-middle-ring {
  transform: translate(-50%, -50%) scale(0.78);
}

.capsule-wrap.tree-stage-seed .capsule-inner-ring {
  transform: translate(-50%, -50%) scale(0.74);
}

.capsule.tree-stage-seed .tree-aura {
  width: 74rpx;
  bottom: 168rpx;
  opacity: 0.35;
}

.capsule.tree-stage-seed .trunk {
  height: 26rpx;
}

.capsule.tree-stage-seed .l1 {
  transform: translateX(-50%) scale(0.68);
}

.capsule.tree-stage-seed .l2,
.capsule.tree-stage-seed .l3,
.capsule.tree-stage-seed .l4,
.capsule.tree-stage-seed .l5 {
  opacity: 0;
  transform: translateX(-50%) scale(0.5);
}

.capsule.tree-stage-sprout .tree-aura {
  width: 82rpx;
  bottom: 182rpx;
  opacity: 0.46;
}

.capsule.tree-stage-sprout .trunk {
  height: 34rpx;
}

.capsule.tree-stage-sprout .l1 {
  transform: translateX(-50%) scale(0.8);
}

.capsule.tree-stage-sprout .l2 {
  opacity: 0.92;
  transform: translateX(-50%) scale(0.76);
}

.capsule.tree-stage-sprout .l3,
.capsule.tree-stage-sprout .l4,
.capsule.tree-stage-sprout .l5 {
  opacity: 0;
  transform: translateX(-50%) scale(0.5);
}

.capsule.tree-stage-sapling .tree-aura {
  width: 92rpx;
  bottom: 194rpx;
  opacity: 0.58;
}

.capsule.tree-stage-sapling .trunk {
  height: 40rpx;
}

.capsule.tree-stage-sapling .l1,
.capsule.tree-stage-sapling .l2,
.capsule.tree-stage-sapling .l3 {
  opacity: 0.94;
}

.capsule.tree-stage-sapling .l4,
.capsule.tree-stage-sapling .l5 {
  opacity: 0;
  transform: translateX(-50%) scale(0.58);
}

.capsule.tree-stage-flourishing .tree-aura {
  width: 104rpx;
  opacity: 0.72;
}

.capsule.tree-stage-flourishing .trunk {
  height: 46rpx;
}

.capsule.tree-stage-flourishing .l5 {
  opacity: 0;
  transform: translateX(-50%) scale(0.7);
}

.capsule.tree-stage-canopy .tree-aura,
.capsule.tree-stage-guardian .tree-aura {
  width: 116rpx;
  opacity: 0.82;
}

.capsule.tree-stage-canopy .trunk {
  height: 50rpx;
}

.capsule.tree-stage-canopy .leaf {
  opacity: 0.96;
}

.capsule.tree-stage-guardian .trunk {
  height: 54rpx;
}

.capsule.tree-stage-guardian .leaf {
  opacity: 0.98;
  filter: drop-shadow(0 0 10rpx rgba(117, 215, 154, 0.26));
}

.capsule.tree-stage-guardian .l1 {
  transform: translateX(-50%) scale(1.04);
}

.capsule.tree-stage-guardian .l2 {
  transform: translateX(-50%) scale(1.03);
}

.capsule.tree-stage-guardian .l3 {
  transform: translateX(-50%) scale(1.02);
}

.capsule.tree-stage-guardian .capsule-bottom-glow {
  background: linear-gradient(0deg, rgba(80, 200, 140, 0.3), transparent);
}

/* 统计信息栏 */
.hero-info {
  display: flex;
  align-items: center;
  background: linear-gradient(135deg, rgba(210, 240, 220, 0.9), rgba(190, 225, 200, 0.85));
  backdrop-filter: blur(24rpx);
  border-radius: 36rpx;
  padding: 24rpx 28rpx;
  border: 1rpx solid rgba(255, 255, 255, 0.7);
  position: relative;
  z-index: 10;
}

.hinfo-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.hinfo-val {
  font-size: 36rpx;
  font-weight: 700;
  color: #2C5A3A;
}

.hinfo-val.neon {
  color: #3D9B6D;
  text-shadow: 0 0 12rpx rgba(61, 155, 109, 0.5);
}

.hinfo-lbl {
  font-size: 22rpx;
  color: #5B8C6E;
  margin-top: 6rpx;
}

.hinfo-div {
  width: 1rpx;
  height: 48rpx;
  background: rgba(91, 140, 110, 0.2);
}

/* ===== 今日数据卡片 ===== */
.stats-row {
  display: flex;
  gap: 16rpx;
  margin-bottom: 28rpx;
}

.stat-card {
  flex: 1;
  backdrop-filter: blur(24rpx);
  border-radius: 32rpx;
  padding: 28rpx 16rpx;
  text-align: center;
  border: 1rpx solid rgba(255, 255, 255, 0.7);
  transition: all 0.3s ease;
}

.stat-card:active { transform: scale(0.96); }
.stat-card-1 { background: linear-gradient(135deg, rgba(100, 200, 150, 0.85), rgba(80, 180, 130, 0.8)); }
.stat-card-2 { background: linear-gradient(135deg, rgba(80, 180, 200, 0.85), rgba(60, 160, 180, 0.8)); }
.stat-card-3 { background: linear-gradient(135deg, rgba(120, 190, 140, 0.85), rgba(100, 170, 120, 0.8)); }

.stat-icon { font-size: 44rpx; margin-bottom: 12rpx; }
.stat-value { font-size: 40rpx; font-weight: 700; color: #FFFFFF; display: block; margin-bottom: 6rpx; text-shadow: 0 2rpx 4rpx rgba(0,0,0,0.1); }
.stat-label { font-size: 22rpx; color: rgba(255,255,255,0.9); }

/* ===== 进度卡片 ===== */
.progress-card {
  background: linear-gradient(135deg, rgba(200, 230, 210, 0.9), rgba(180, 215, 190, 0.85));
  backdrop-filter: blur(24rpx);
  border-radius: 40rpx;
  padding: 32rpx;
  margin-bottom: 32rpx;
  border: 1rpx solid rgba(255, 255, 255, 0.7);
}

.progress-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 24rpx;
}

.progress-title { font-size: 30rpx; font-weight: 700; color: #2C5A3A; }
.progress-sub { font-size: 22rpx; color: #5B8C6E; margin-top: 6rpx; }
.progress-percent { background: rgba(61, 155, 109, 0.2); border-radius: 24rpx; padding: 8rpx 20rpx; }
.percent-value { font-size: 36rpx; font-weight: 700; color: #3D9B6D; }

.progress-track {
  height: 12rpx;
  background: rgba(91, 140, 110, 0.2);
  border-radius: 6rpx;
  margin-bottom: 20rpx;
  position: relative;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, #6FB88A, #3D9B6D);
  border-radius: 6rpx;
  position: relative;
  transition: width 0.5s ease;
  box-shadow: 0 0 12rpx rgba(61, 155, 109, 0.5);
}

.progress-dot {
  position: absolute;
  right: -8rpx;
  top: 50%;
  transform: translateY(-50%);
  width: 20rpx;
  height: 20rpx;
  background: #FFFFFF;
  border: 3rpx solid #3D9B6D;
  border-radius: 50%;
  box-shadow: 0 0 10rpx rgba(61, 155, 109, 0.8);
}

.progress-hint { font-size: 24rpx; color: #5B8C6E; }
.progress-hint.done { color: #3D9B6D; font-weight: 500; }

.agent-brief-card {
  margin-bottom: 28rpx;
  padding: 28rpx;
  border-radius: 36rpx;
  background: linear-gradient(140deg, rgba(41, 112, 63, 0.96), rgba(86, 156, 105, 0.92));
  box-shadow: 0 20rpx 36rpx rgba(35, 83, 51, 0.14);
}

.agent-brief-head {
  display: flex;
  justify-content: space-between;
  gap: 20rpx;
}

.agent-brief-copy {
  flex: 1;
}

.agent-brief-kicker {
  display: block;
  font-size: 22rpx;
  color: rgba(255, 255, 255, 0.78);
  letter-spacing: 2rpx;
}

.agent-brief-title {
  display: block;
  margin-top: 12rpx;
  font-size: 34rpx;
  font-weight: 700;
  color: #ffffff;
}

.agent-brief-reason {
  display: block;
  margin-top: 10rpx;
  font-size: 22rpx;
  line-height: 1.6;
  color: rgba(255, 255, 255, 0.84);
}

.agent-brief-points {
  min-width: 120rpx;
  text-align: right;
}

.agent-brief-points-value {
  display: block;
  font-size: 34rpx;
  font-weight: 700;
  color: #ffe69c;
}

.agent-brief-points-label {
  display: block;
  margin-top: 8rpx;
  font-size: 20rpx;
  color: rgba(255, 255, 255, 0.76);
}

.agent-brief-metrics {
  display: flex;
  flex-wrap: wrap;
  gap: 12rpx;
  margin-top: 20rpx;
}

.agent-metric-pill {
  min-width: 180rpx;
  padding: 16rpx 18rpx;
  border-radius: 24rpx;
  background: rgba(255, 255, 255, 0.14);
}

.agent-metric-label {
  display: block;
  font-size: 20rpx;
  color: rgba(255, 255, 255, 0.72);
}

.agent-metric-value {
  display: block;
  margin-top: 8rpx;
  font-size: 26rpx;
  font-weight: 700;
  color: #ffffff;
}

.agent-action-list {
  margin-top: 20rpx;
  display: flex;
  flex-direction: column;
  gap: 14rpx;
}

.agent-action-item {
  padding: 22rpx;
  border-radius: 24rpx;
  background: rgba(255, 255, 255, 0.12);
  display: flex;
  justify-content: space-between;
  gap: 18rpx;
  align-items: center;
}

.agent-action-copy {
  flex: 1;
}

.agent-action-tag {
  display: inline-flex;
  padding: 8rpx 14rpx;
  border-radius: 999rpx;
  background: rgba(255, 255, 255, 0.18);
  color: #ffffff;
  font-size: 20rpx;
  font-weight: 700;
}

.agent-action-title {
  display: block;
  margin-top: 10rpx;
  font-size: 26rpx;
  font-weight: 700;
  color: #ffffff;
}

.agent-action-meta {
  display: block;
  margin-top: 8rpx;
  font-size: 20rpx;
  color: rgba(255, 255, 255, 0.8);
}

.agent-action-arrow {
  font-size: 22rpx;
  color: rgba(255, 255, 255, 0.82);
  font-weight: 600;
}

.task-board {
  position: relative;
  overflow: hidden;
  margin-bottom: 28rpx;
  padding: 28rpx 26rpx;
  border-radius: 36rpx;
  background: linear-gradient(135deg, rgba(245, 253, 248, 0.96), rgba(222, 243, 229, 0.9));
  box-shadow: 0 16rpx 28rpx rgba(57, 110, 73, 0.1);
}

.task-board-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 20rpx;
}

.task-board-title {
  display: block;
  font-size: 34rpx;
  font-weight: 700;
  color: #255238;
}

.task-board-sub {
  display: block;
  margin-top: 8rpx;
  font-size: 22rpx;
  color: #6c907a;
}

.task-board-badge {
  min-width: 124rpx;
  height: 56rpx;
  padding: 0 18rpx;
  border-radius: 999rpx;
  background: linear-gradient(135deg, #67c48b, #419d65);
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 10rpx 18rpx rgba(65, 157, 101, 0.16);
}

.task-board-badge-text {
  font-size: 20rpx;
  font-weight: 700;
  color: #FFFFFF;
}

.task-summary-row {
  display: flex;
  gap: 16rpx;
  margin-top: 20rpx;
}

.task-summary-pill {
  flex: 1;
  padding: 18rpx 20rpx;
  border-radius: 26rpx;
  background: rgba(95, 190, 127, 0.12);
}

.task-summary-pill.weekly {
  background: rgba(82, 174, 197, 0.14);
}

.task-summary-label {
  display: block;
  font-size: 20rpx;
  color: #638270;
}

.task-summary-value {
  display: block;
  margin-top: 8rpx;
  font-size: 32rpx;
  font-weight: 700;
  color: #255238;
}

.task-group {
  margin-top: 24rpx;
}

.task-group-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 14rpx;
}

.task-group-title {
  font-size: 28rpx;
  font-weight: 700;
  color: #255238;
}

.task-group-period {
  font-size: 20rpx;
  color: #6f907c;
}

.task-list {
  display: flex;
  flex-direction: column;
  gap: 14rpx;
}

.task-item {
  padding: 22rpx 22rpx 18rpx;
  border-radius: 28rpx;
  background: rgba(255, 255, 255, 0.8);
  border: 1rpx solid rgba(255, 255, 255, 0.72);
}

.task-item.weekly {
  background: rgba(248, 253, 255, 0.88);
}

.task-item-top {
  display: flex;
  gap: 16rpx;
  align-items: flex-start;
}

.task-icon-wrap {
  width: 68rpx;
  height: 68rpx;
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

.task-title {
  display: block;
  font-size: 26rpx;
  font-weight: 700;
  color: #255238;
}

.task-subtitle {
  display: block;
  margin-top: 8rpx;
  font-size: 20rpx;
  line-height: 1.45;
  color: #688776;
}

.task-status-wrap {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 10rpx;
}

.task-reward {
  font-size: 24rpx;
  font-weight: 700;
  color: #f3a33d;
}

.task-status {
  padding: 8rpx 14rpx;
  border-radius: 999rpx;
  background: rgba(108, 144, 122, 0.12);
  font-size: 18rpx;
  color: #6f8f7c;
}

.task-status.done {
  background: rgba(80, 181, 116, 0.14);
  color: #35925b;
}

.task-progress-track {
  margin-top: 16rpx;
  height: 14rpx;
  border-radius: 999rpx;
  background: rgba(190, 215, 198, 0.56);
  overflow: hidden;
}

.task-progress-fill {
  height: 100%;
  border-radius: 999rpx;
}

.task-progress-row {
  margin-top: 12rpx;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.task-progress-text {
  font-size: 20rpx;
  color: #638270;
}

.task-progress-percent {
  font-size: 20rpx;
  font-weight: 700;
  color: #2b5d41;
}

/* ===== 每日AI健康建议卡片 ===== */
.daily-health-card {
  background: linear-gradient(135deg, rgba(255, 240, 240, 0.9), rgba(255, 220, 220, 0.85));
  backdrop-filter: blur(24rpx);
  border-radius: 40rpx;
  padding: 28rpx;
  margin-bottom: 32rpx;
  border: 1rpx solid rgba(220, 80, 80, 0.2);
  position: relative;
  overflow: hidden;
  transition: all 0.3s ease;
}

.daily-health-card:active {
  transform: scale(0.98);
}

.dhc-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.dhc-title-wrap {
  display: flex;
  align-items: center;
  gap: 16rpx;
}

.dhc-icon-box {
  width: 56rpx;
  height: 56rpx;
  background: rgba(220, 80, 80, 0.15);
  border-radius: 16rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.dhc-icon {
  font-size: 32rpx;
}

.dhc-title {
  font-size: 30rpx;
  font-weight: 700;
  color: #8B3A3A;
}

.dhc-arrow {
  font-size: 32rpx;
  color: #C85D5D;
  font-weight: bold;
}

.dhc-divider {
  height: 1rpx;
  background: linear-gradient(90deg, transparent, rgba(200, 80, 80, 0.3), transparent);
  margin: 20rpx 0;
}

.dhc-content {
  margin-bottom: 16rpx;
}

.dhc-text {
  font-size: 26rpx;
  color: #6B4A4A;
  line-height: 1.6;
}

.dhc-footer {
  display: flex;
  justify-content: flex-end;
}

.dhc-action {
  font-size: 24rpx;
  color: #C85D5D;
  font-weight: 600;
}

/* ===== 功能导航标题 ===== */
.activity-preview-card {
  background: linear-gradient(135deg, rgba(239, 249, 243, 0.95), rgba(221, 242, 228, 0.92));
  border-radius: 40rpx;
  padding: 28rpx;
  margin-bottom: 32rpx;
  border: 1rpx solid rgba(255, 255, 255, 0.75);
  box-shadow: 0 14rpx 32rpx rgba(46, 102, 67, 0.1);
  overflow: hidden;
}

.activity-preview-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 20rpx;
}

.activity-preview-tag {
  display: inline-flex;
  align-items: center;
  padding: 10rpx 20rpx;
  border-radius: 999rpx;
  background: rgba(79, 168, 111, 0.14);
  font-size: 22rpx;
  font-weight: 700;
  color: #2d6a45;
}

.activity-preview-action {
  font-size: 22rpx;
  color: #4f8e67;
  font-weight: 600;
}

.activity-preview-main {
  margin-top: 20rpx;
  display: flex;
  align-items: center;
  gap: 18rpx;
}

.activity-preview-icon {
  width: 92rpx;
  height: 92rpx;
  border-radius: 28rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 10rpx 22rpx rgba(79, 168, 111, 0.18);
  flex-shrink: 0;
}

.activity-preview-emoji {
  font-size: 40rpx;
}

.activity-preview-copy {
  flex: 1;
  min-width: 0;
}

.activity-preview-title {
  display: block;
  font-size: 30rpx;
  font-weight: 700;
  color: #255238;
}

.activity-preview-desc {
  display: block;
  margin-top: 10rpx;
  font-size: 22rpx;
  line-height: 1.5;
  color: #6a8b76;
}

.activity-preview-meta {
  margin-top: 20rpx;
  display: flex;
  flex-wrap: wrap;
  gap: 12rpx;
}

.activity-preview-pill {
  padding: 12rpx 18rpx;
  border-radius: 999rpx;
  background: rgba(255, 255, 255, 0.72);
}

.activity-preview-pill-label {
  font-size: 20rpx;
  color: #4a7d5f;
  font-weight: 600;
}

.activity-preview-progress {
  margin-top: 20rpx;
}

.activity-preview-progress-track {
  height: 14rpx;
  border-radius: 999rpx;
  background: rgba(162, 202, 173, 0.35);
  overflow: hidden;
}

.activity-preview-progress-fill {
  height: 100%;
  border-radius: 999rpx;
  box-shadow: 0 0 12rpx rgba(79, 168, 111, 0.28);
}

.activity-preview-progress-row {
  margin-top: 12rpx;
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16rpx;
}

.activity-preview-progress-text {
  flex: 1;
  font-size: 20rpx;
  color: #5f8370;
}

.activity-preview-progress-value {
  font-size: 22rpx;
  font-weight: 700;
  color: #2d6a45;
}

.activity-preview-footer {
  margin-top: 16rpx;
}

.activity-preview-target {
  font-size: 22rpx;
  color: #335f45;
  font-weight: 600;
}

.section-header {
  display: flex;
  align-items: baseline;
  gap: 12rpx;
  margin-bottom: 24rpx;
}

.section-bar {
  width: 6rpx;
  height: 32rpx;
  background: linear-gradient(180deg, #6FB88A, #3D9B6D);
  border-radius: 3rpx;
  box-shadow: 0 0 8rpx rgba(61, 155, 109, 0.5);
}

.section-title { font-size: 32rpx; font-weight: 700; color: #2C5A3A; }
.section-sub { font-size: 22rpx; color: #5B8C6E; margin-left: auto; }

/* ===== 2x2 功能导航网格 ===== */
.feature-grid {
  display: flex;
  flex-direction: column;
  gap: 20rpx;
  margin-bottom: 32rpx;
}

.feature-row {
  display: flex;
  gap: 20rpx;
  width: 100%;
}

.feature-card {
  flex: 1;
  backdrop-filter: blur(24rpx);
  border-radius: 36rpx;
  padding: 28rpx 24rpx;
  border: 1rpx solid rgba(255, 255, 255, 0.7);
  position: relative;
  transition: all 0.3s cubic-bezier(0.2, 0.9, 0.4, 1.1);
  box-shadow: 0 12rpx 28rpx rgba(0, 0, 0, 0.08);
  overflow: hidden;
}

.feature-card:active { transform: scale(0.97); }

.feature-card-ai { background: linear-gradient(135deg, rgba(80, 200, 140, 0.9), rgba(60, 170, 120, 0.85)); }
.feature-card-steps { background: linear-gradient(135deg, rgba(100, 190, 160, 0.9), rgba(80, 160, 140, 0.85)); }
.feature-card-sport { background: linear-gradient(135deg, rgba(90, 180, 170, 0.9), rgba(70, 150, 140, 0.85)); }
.feature-card-rank { background: linear-gradient(135deg, rgba(70, 170, 180, 0.9), rgba(50, 140, 150, 0.85)); }
.feature-card-activity { background: linear-gradient(135deg, rgba(88, 184, 122, 0.92), rgba(58, 145, 95, 0.86)); }

/* 图标容器 */
.feature-icon-container {
  position: relative;
  width: 100rpx;
  height: 100rpx;
  margin-bottom: 20rpx;
}

.particle-glow {
  position: absolute;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(255, 255, 255, 0.8), rgba(255, 255, 200, 0.4), transparent);
  pointer-events: none;
  animation: particleGlowFloat 2s ease-in-out infinite;
}

.particle-glow-1 { width: 20rpx; height: 20rpx; top: -10rpx; left: 20rpx; animation-delay: 0s; }
.particle-glow-2 { width: 15rpx; height: 15rpx; top: 60rpx; right: -15rpx; animation-delay: 0.5s; }
.particle-glow-3 { width: 18rpx; height: 18rpx; bottom: -10rpx; left: 40rpx; animation-delay: 1s; }
.particle-glow-4 { width: 12rpx; height: 12rpx; top: 20rpx; left: -12rpx; animation-delay: 1.5s; }
.particle-glow-5 { width: 25rpx; height: 25rpx; bottom: 30rpx; right: -8rpx; animation-delay: 2s; }

@keyframes particleGlowFloat {
  0%, 100% { opacity: 0; transform: scale(0.5); }
  50% { opacity: 0.8; transform: scale(1.2); box-shadow: 0 0 20rpx rgba(255, 255, 200, 0.8); }
}

.feature-icon-wrapper {
  position: relative;
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 10;
}

.feature-outer-ring,
.feature-middle-ring,
.feature-inner-ring {
  position: absolute;
  border-radius: 50%;
  pointer-events: none;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.feature-card:active .feature-outer-ring,
.feature-card:active .feature-middle-ring,
.feature-card:active .feature-inner-ring {
  opacity: 1;
}

.feature-outer-ring {
  top: -12rpx;
  left: -12rpx;
  right: -12rpx;
  bottom: -12rpx;
  border: 2rpx solid rgba(255, 255, 255, 0.6);
  animation: ringRotate 10s linear infinite;
}

.feature-middle-ring {
  top: -6rpx;
  left: -6rpx;
  right: -6rpx;
  bottom: -6rpx;
  border: 2rpx solid rgba(255, 255, 255, 0.5);
  animation: ringRotateReverse 7s linear infinite;
}

.feature-inner-ring {
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  border: 2rpx solid rgba(255, 255, 255, 0.4);
  animation: ringRotate 5s linear infinite;
}

.feature-icon-box {
  width: 88rpx;
  height: 88rpx;
  border-radius: 28rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  z-index: 10;
  background: rgba(255, 255, 255, 0.25);
  backdrop-filter: blur(4rpx);
  transition: all 0.3s ease;
}

.feature-card:active .feature-icon-box {
  transform: scale(1.05);
  background: rgba(255, 255, 255, 0.35);
}

.feature-icon { font-size: 52rpx; }
.feature-name { font-size: 28rpx; font-weight: 700; color: #FFFFFF; margin-bottom: 6rpx; text-shadow: 0 2rpx 4rpx rgba(0,0,0,0.1); }
.feature-desc { font-size: 22rpx; color: rgba(255,255,255,0.85); line-height: 1.4; }
.feature-arrow { position: absolute; bottom: 24rpx; right: 24rpx; font-size: 36rpx; color: rgba(255,255,255,0.6); transition: all 0.3s ease; }
.feature-card:active .feature-arrow { transform: translateX(6rpx); color: #FFFFFF; }
.radial-glow { position: absolute; top: 50%; left: 50%; transform: translate(-50%, -50%); width: 0; height: 0; border-radius: 50%; background: radial-gradient(circle, rgba(255,255,255,0.4), transparent); transition: all 0.3s ease; pointer-events: none; opacity: 0; }
.feature-card:active .radial-glow { width: 200%; height: 200%; opacity: 1; transition: all 0.4s ease; }

/* ===== 环保徽章 ===== */
.eco-badge {
  display: flex;
  align-items: center;
  gap: 20rpx;
  background: linear-gradient(135deg, rgba(210, 235, 220, 0.95), rgba(190, 220, 200, 0.9));
  backdrop-filter: blur(24rpx);
  border-radius: 36rpx;
  padding: 24rpx 28rpx;
  margin-bottom: 28rpx;
  border: 1rpx solid rgba(255, 255, 255, 0.6);
  position: relative;
  overflow: hidden;
  animation: badgeSlide 0.5s ease-out;
}

@keyframes badgeSlide {
  from { opacity: 0; transform: translateX(-40rpx); }
  to { opacity: 1; transform: translateX(0); }
}

.badge-shimmer {
  position: absolute;
  top: 0;
  left: -100%;
  width: 60%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.5), transparent);
  animation: shimmer 3s ease-in-out infinite;
}

@keyframes shimmer {
  0% { left: -100%; }
  60%, 100% { left: 120%; }
}

.badge-icon { font-size: 56rpx; animation: medalBounce 2s ease-in-out infinite; }
@keyframes medalBounce { 0%, 100% { transform: scale(1); } 50% { transform: scale(1.1); } }
.badge-info { flex: 1; }
.badge-title { font-size: 28rpx; font-weight: 700; color: #2C5A3A; display: block; margin-bottom: 6rpx; }
.badge-text { font-size: 22rpx; color: #3D9B6D; }
.badge-time { display: block; margin-top: 8rpx; font-size: 20rpx; color: #6B8E79; }
.badge-tag {
  min-width: 110rpx;
  height: 56rpx;
  border-radius: 999rpx;
  background: linear-gradient(135deg, #6FB88A, #3D9B6D);
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 10rpx 18rpx rgba(61, 155, 109, 0.18);
}
.badge-tag text { font-size: 22rpx; font-weight: 700; color: #FFFFFF; }

/* ===== 底部 ===== */
.footer { text-align: center; padding: 20rpx 0; }
.footer-text { font-size: 22rpx; color: #9CC0AC; line-height: 1.6; }
.spacer { height: 140rpx; }
</style>

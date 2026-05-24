<template>
  <view class="page">
    <map
      class="route-map"
      :latitude="centerLatitude"
      :longitude="centerLongitude"
      :markers="markers"
      :polyline="polyline"
      :include-points="includePoints"
      show-location
    />

    <view class="route-panel">
      <view class="panel-handle"></view>
      <text class="route-kicker">{{ copy.routeKicker }}</text>
      <text class="route-title">{{ routePlan.venueName || copy.routeTitleFallback }}</text>
      <text class="route-address" v-if="routePlan.venueAddress">{{ routePlan.venueAddress }}</text>

      <view class="route-metrics">
        <view class="metric">
          <text class="metric-value">{{ distanceLabel }}</text>
          <text class="metric-label">{{ copy.distanceLabel }}</text>
        </view>
        <view class="metric">
          <text class="metric-value">{{ durationLabel }}</text>
          <text class="metric-label">{{ copy.durationLabel }}</text>
        </view>
        <view class="metric">
          <text class="metric-value">{{ routePlan.transportMode || copy.walkLabel }}</text>
          <text class="metric-label">{{ copy.modeLabel }}</text>
        </view>
      </view>

      <view class="steps-card" v-if="routeSteps.length">
        <text class="steps-title">{{ copy.stepsTitle }}</text>
        <view class="step-item" v-for="(step, index) in routeSteps" :key="index">
          <view class="step-index">
            <text class="step-index-text">{{ index + 1 }}</text>
          </view>
          <text class="step-text">{{ step }}</text>
        </view>
      </view>

      <view class="empty-card" v-else>
        <text class="empty-text">{{ copy.emptyRoute }}</text>
      </view>
    </view>
  </view>
</template>

<script>
const COPY = {
  routeKicker: '\u8def\u7ebf\u89c4\u5212',
  routeTitleFallback: '\u63a8\u8350\u573a\u5730',
  distanceLabel: '\u8def\u7ebf\u8ddd\u79bb',
  durationLabel: '\u9884\u8ba1\u8017\u65f6',
  modeLabel: '\u51fa\u884c\u65b9\u5f0f',
  walkLabel: '\u6b65\u884c',
  stepsTitle: '\u884c\u8d70\u8981\u70b9',
  emptyRoute: '\u6682\u65e0\u8be6\u7ec6\u6b65\u884c\u6307\u5f15\uff0c\u5df2\u5728\u5730\u56fe\u4e0a\u6807\u51fa\u8d77\u70b9\u548c\u7ec8\u70b9',
  originLabel: '\u5f53\u524d\u4f4d\u7f6e',
  destinationLabel: '\u76ee\u7684\u5730'
};

function validNumber(value) {
  return typeof value === 'number' && !Number.isNaN(value);
}

export default {
  data() {
    return {
      copy: COPY,
      routePlan: {}
    };
  },
  onLoad() {
    const storedPlan = uni.getStorageSync('agent_route_plan');
    this.routePlan = storedPlan && typeof storedPlan === 'object' ? storedPlan : {};
  },
  computed: {
    originPoint() {
      if (!validNumber(this.routePlan.originLatitude) || !validNumber(this.routePlan.originLongitude)) {
        return null;
      }
      return {
        latitude: this.routePlan.originLatitude,
        longitude: this.routePlan.originLongitude
      };
    },
    destinationPoint() {
      if (!validNumber(this.routePlan.destinationLatitude) || !validNumber(this.routePlan.destinationLongitude)) {
        return null;
      }
      return {
        latitude: this.routePlan.destinationLatitude,
        longitude: this.routePlan.destinationLongitude
      };
    },
    centerLatitude() {
      if (this.originPoint && this.destinationPoint) {
        return (this.originPoint.latitude + this.destinationPoint.latitude) / 2;
      }
      return this.destinationPoint ? this.destinationPoint.latitude : 20.0442;
    },
    centerLongitude() {
      if (this.originPoint && this.destinationPoint) {
        return (this.originPoint.longitude + this.destinationPoint.longitude) / 2;
      }
      return this.destinationPoint ? this.destinationPoint.longitude : 110.1999;
    },
    markers() {
      const markers = [];
      if (this.originPoint) {
        markers.push({
          id: 1,
          latitude: this.originPoint.latitude,
          longitude: this.originPoint.longitude,
          title: COPY.originLabel,
          width: 28,
          height: 28,
          callout: {
            content: COPY.originLabel,
            color: '#24563a',
            fontSize: 12,
            borderRadius: 8,
            bgColor: '#ffffff',
            padding: 6,
            display: 'ALWAYS'
          }
        });
      }
      if (this.destinationPoint) {
        markers.push({
          id: 2,
          latitude: this.destinationPoint.latitude,
          longitude: this.destinationPoint.longitude,
          title: this.routePlan.venueName || COPY.destinationLabel,
          width: 34,
          height: 34,
          callout: {
            content: this.routePlan.venueName || COPY.destinationLabel,
            color: '#24563a',
            fontSize: 12,
            borderRadius: 8,
            bgColor: '#ffffff',
            padding: 6,
            display: 'ALWAYS'
          }
        });
      }
      return markers;
    },
    routePoints() {
      const points = Array.isArray(this.routePlan.routePolyline) ? this.routePlan.routePolyline : [];
      const normalized = points
        .filter(item => item && validNumber(item.latitude) && validNumber(item.longitude))
        .map(item => ({ latitude: item.latitude, longitude: item.longitude }));
      if (normalized.length > 1) {
        return normalized;
      }
      if (this.originPoint && this.destinationPoint) {
        return [this.originPoint, this.destinationPoint];
      }
      return normalized;
    },
    polyline() {
      if (this.routePoints.length < 2) {
        return [];
      }
      return [{
        points: this.routePoints,
        color: '#2e7d32',
        width: 6,
        arrowLine: true,
        borderColor: '#ffffff',
        borderWidth: 2
      }];
    },
    includePoints() {
      const points = [];
      if (this.originPoint) {
        points.push(this.originPoint);
      }
      if (this.destinationPoint) {
        points.push(this.destinationPoint);
      }
      return points.concat(this.routePoints);
    },
    routeSteps() {
      return Array.isArray(this.routePlan.routeSteps) ? this.routePlan.routeSteps : [];
    },
    distanceLabel() {
      const distance = Number(this.routePlan.routeDistanceMeters || this.routePlan.venueDistanceMeters || 0);
      if (!distance) {
        return '--';
      }
      if (distance >= 1000) {
        return `${(distance / 1000).toFixed(1)}km`;
      }
      return `${distance}m`;
    },
    durationLabel() {
      const duration = Number(this.routePlan.routeDurationMinutes || 0);
      return duration ? `${duration}min` : '--';
    }
  }
};
</script>

<style scoped>
.page {
  min-height: 100vh;
  background: #eef6ef;
  position: relative;
}

.route-map {
  width: 100%;
  height: 58vh;
}

.route-panel {
  position: relative;
  z-index: 2;
  min-height: 42vh;
  margin-top: -32rpx;
  padding: 24rpx 28rpx 44rpx;
  border-radius: 34rpx 34rpx 0 0;
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.98), rgba(244, 250, 245, 0.98));
  box-shadow: 0 -16rpx 36rpx rgba(40, 89, 54, 0.1);
  box-sizing: border-box;
}

.panel-handle {
  width: 80rpx;
  height: 8rpx;
  margin: 0 auto 22rpx;
  border-radius: 999rpx;
  background: rgba(46, 125, 50, 0.2);
}

.route-kicker {
  display: block;
  font-size: 22rpx;
  color: #4c7d5c;
  letter-spacing: 2rpx;
}

.route-title {
  display: block;
  margin-top: 10rpx;
  font-size: 38rpx;
  font-weight: 700;
  line-height: 1.35;
  color: #21462f;
}

.route-address {
  display: block;
  margin-top: 10rpx;
  font-size: 24rpx;
  line-height: 1.6;
  color: #627b6d;
}

.route-metrics {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 16rpx;
  margin-top: 24rpx;
}

.metric {
  padding: 18rpx 14rpx;
  border-radius: 24rpx;
  background: rgba(46, 125, 50, 0.08);
  text-align: center;
}

.metric-value {
  display: block;
  font-size: 28rpx;
  font-weight: 700;
  color: #255b38;
}

.metric-label {
  display: block;
  margin-top: 8rpx;
  font-size: 20rpx;
  color: #6f8778;
}

.steps-card,
.empty-card {
  margin-top: 24rpx;
  padding: 22rpx;
  border-radius: 26rpx;
  background: rgba(255, 255, 255, 0.9);
}

.steps-title {
  display: block;
  font-size: 26rpx;
  font-weight: 700;
  color: #24563a;
}

.step-item {
  display: flex;
  align-items: flex-start;
  gap: 16rpx;
  margin-top: 18rpx;
}

.step-index {
  width: 40rpx;
  height: 40rpx;
  border-radius: 20rpx;
  background: #2e7d32;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.step-index-text {
  font-size: 20rpx;
  font-weight: 700;
  color: #ffffff;
}

.step-text,
.empty-text {
  flex: 1;
  font-size: 24rpx;
  line-height: 1.7;
  color: #536c5d;
}
</style>

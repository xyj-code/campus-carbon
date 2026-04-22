const baseUrl = 'http://localhost:8080/api';

export const request = (url, options = {}) => {
  return new Promise((resolve, reject) => {
    uni.request({
      url: baseUrl + url,
      method: options.method || 'GET',
      data: options.data || {},
      header: {
        'Content-Type': 'application/json'
      },
      success: (res) => {
        if (res.statusCode >= 200 && res.statusCode < 300) {
          resolve(res.data);
        } else {
          reject(new Error(res.data?.message || 'REQUEST_FAILED'));
        }
      },
      fail: (err) => {
        reject(err);
      }
    });
  });
};

export const login = (username, password) => {
  return request('/user/login', {
    method: 'POST',
    data: { username, password }
  });
};

export const getCarbonSuggestion = (carbonFootprint) => {
  return request('/ai/carbon-suggest', {
    method: 'POST',
    data: { carbonFootprint }
  });
};

export const getHealthSuggestion = (userId) => {
  return request(`/ai/health-suggest?userId=${userId}`);
};

export const getAgentBrief = (userId) => {
  return request(`/ai/agent-brief?userId=${encodeURIComponent(userId)}`);
};

export const getAgentPlan = (userId, userNote = '') => {
  return request('/ai/agent-plan', {
    method: 'POST',
    data: { userId, userNote }
  });
};

export const startAgentAction = (userId, sessionId, actionId, resultNote = '') => {
  return request('/ai/agent-action/start', {
    method: 'POST',
    data: { userId, sessionId, actionId, resultNote }
  });
};

export const completeAgentAction = (userId, sessionId, actionId, resultNote = '') => {
  return request('/ai/agent-action/complete', {
    method: 'POST',
    data: { userId, sessionId, actionId, resultNote }
  });
};

export const skipAgentAction = (userId, sessionId, actionId, resultNote = '') => {
  return request('/ai/agent-action/skip', {
    method: 'POST',
    data: { userId, sessionId, actionId, resultNote }
  });
};

export const getStepCount = (studentId, date) => {
  return request(`/step/count?studentId=${studentId}&date=${date}`);
};

export const getStepCountList = (studentId) => {
  return request(`/step/list?studentId=${studentId}`);
};

export const saveStepCount = (data) => {
  return request('/step/save', {
    method: 'POST',
    data
  });
};

export const getSportRecord = (studentId) => {
  return request(`/sport/record?studentId=${studentId}`);
};

export const saveSportRecord = (data) => {
  return request('/sport/save', {
    method: 'POST',
    data
  });
};

export const getRankData = (studentId, timeRange) => {
  return request(`/rank/data?studentId=${studentId}&timeRange=${timeRange}`);
};

export const getProfile = (username) => {
  return request(`/points/profile?username=${username}`);
};

export const getPointsRecords = (username, type = 'all', page = 1, size = 10) => {
  return request(`/points/records?username=${username}&type=${type}&page=${page}&size=${size}`);
};

export const getTaskBoard = (username) => {
  return request(`/task/board?username=${username}`);
};

export const getActivityHub = (username, activityCode = '') => {
  const query = activityCode
    ? `/activity/hub?username=${username}&activityCode=${encodeURIComponent(activityCode)}`
    : `/activity/hub?username=${username}`;
  return request(query);
};

export const getProductList = (page = 1, size = 12) => {
  return request(`/product/list?page=${page}&size=${size}`);
};

export const exchangeProduct = (username, productId) => {
  return request('/product/exchange', {
    method: 'POST',
    data: { username, productId }
  });
};

export const getExchangeRecords = (username) => {
  return request(`/product/exchange/records?username=${username}`);
};

export const getHealthDataList = (userId) => {
  return request(`/health/list?userId=${userId}`);
};

export const saveHealthData = (data) => {
  return request('/health/save', {
    method: 'POST',
    data
  });
};

export const deleteHealthData = (id) => {
  return request(`/health/${id}`, {
    method: 'DELETE'
  });
};

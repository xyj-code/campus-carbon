// 接口请求工具类
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
          reject(new Error(res.data?.message || '请求失败'));
        }
      },
      fail: (err) => {
        reject(err);
      }
    });
  });
};

// 用户登录
export const login = (username, password) => {
  return request('/user/login', {
    method: 'POST',
    data: { username, password }
  });
};

// AI减排建议接口
export const getCarbonSuggestion = (carbonFootprint) => {
  return request('/ai/carbon-suggest', {
    method: 'POST',
    data: { carbonFootprint }
  });
};

// AI健康建议接口（基于用户健康数据）
export const getHealthSuggestion = (userId) => {
  return request(`/ai/health-suggest?userId=${userId}`);
};

// 步数统计接口
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

// 运动记录接口
export const getSportRecord = (studentId) => {
  return request(`/sport/record?studentId=${studentId}`);
};

export const saveSportRecord = (data) => {
  return request('/sport/save', {
    method: 'POST',
    data
  });
};

// 减碳排名接口
export const getRankData = (studentId, timeRange) => {
  return request(`/rank/data?studentId=${studentId}&timeRange=${timeRange}`);
};

// 积分 & 个人资料
export const getProfile = (username) => {
  return request(`/points/profile?username=${username}`);
};

export const getPointsRecords = (username, type = 'all', page = 1, size = 10) => {
  return request(`/points/records?username=${username}&type=${type}&page=${page}&size=${size}`);
};

// 积分商城
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

// 健康统计接口
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

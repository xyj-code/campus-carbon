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
        resolve(res.data);
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

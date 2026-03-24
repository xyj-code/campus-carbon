// 生态认养项目相关接口
import { request } from './request.js';

// 用户累计减碳量 + 个人资料
export function getUserTotalCarbon(username) {
  return request(`/api/project/carbon-total?username=${username}`);
}

// 用户已解锁项目列表
export function getUserProjectList(username) {
  return request(`/api/project/my-projects?username=${username}`);
}

// 检查并自动解锁达标项目（POST，query param）
export function checkAndUnlock(username) {
  return request(`/api/project/check-unlock?username=${username}`, { method: 'POST' });
}

// 项目详情
export function getProjectDetail(id) {
  return request(`/api/project/detail/${id}`);
}

// 获取电子认养证书
export function getCertificate(username, userProjectId) {
  return request(`/api/project/certificate?username=${username}&userProjectId=${userProjectId}`);
}
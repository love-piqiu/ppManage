import request from '@/utils/request'

// 获取首页看板所有数据（新接口，一次返回所有数据）
export function getDashboardData() {
  return request({
    url: '/dashboard/data',
    method: 'get'
  })
}

// 获取预警信息
export function getAlerts() {
  return request({
    url: '/dashboard/alerts',
    method: 'get'
  })
}

// 获取汇总统计
export function getSummary() {
  return request({
    url: '/dashboard/summary',
    method: 'get'
  })
}

// 获取紧急问题列表
export function getUrgentIssues() {
  return request({
    url: '/dashboard/urgentIssues',
    method: 'get'
  })
}

// 获取高风险列表
export function getHighRisks() {
  return request({
    url: '/dashboard/highRisks',
    method: 'get'
  })
}

// 获取项目进度概览
export function getProjectProgress() {
  return request({
    url: '/dashboard/projectProgress',
    method: 'get'
  })
}
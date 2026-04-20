import request from '@/utils/request'

// 查询风险列表
export function listRisk(query) {
  return request({
    url: '/system/risk/list',
    method: 'get',
    params: query
  })
}

// 查询风险详细
export function getRisk(riskId) {
  return request({
    url: '/system/risk/' + riskId,
    method: 'get'
  })
}

// 新增风险
export function addRisk(data) {
  return request({
    url: '/system/risk',
    method: 'post',
    data: data
  })
}

// 修改风险
export function updateRisk(data) {
  return request({
    url: '/system/risk',
    method: 'put',
    data: data
  })
}

// 更新风险状态
export function updateRiskStatus(data) {
  return request({
    url: '/system/risk/status',
    method: 'put',
    data: data
  })
}

// 删除风险
export function delRisk(riskId) {
  return request({
    url: '/system/risk/' + riskId,
    method: 'delete'
  })
}

// 根据项目ID查询未消除风险列表
export function listRiskByProject(projectId) {
  return request({
    url: '/system/risk/project/' + projectId,
    method: 'get'
  })
}
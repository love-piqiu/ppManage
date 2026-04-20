import request from '@/utils/request'

// 查询问题列表
export function listIssue(query) {
  return request({
    url: '/system/issue/list',
    method: 'get',
    params: query
  })
}

// 查询问题详细
export function getIssue(issueId) {
  return request({
    url: '/system/issue/' + issueId,
    method: 'get'
  })
}

// 新增问题
export function addIssue(data) {
  return request({
    url: '/system/issue',
    method: 'post',
    data: data
  })
}

// 修改问题
export function updateIssue(data) {
  return request({
    url: '/system/issue',
    method: 'put',
    data: data
  })
}

// 更新问题状态
export function updateIssueStatus(data) {
  return request({
    url: '/system/issue/status',
    method: 'put',
    data: data
  })
}

// 删除问题
export function delIssue(issueId) {
  return request({
    url: '/system/issue/' + issueId,
    method: 'delete'
  })
}

// 根据项目ID查询未解决问题列表
export function listIssueByProject(projectId) {
  return request({
    url: '/system/issue/project/' + projectId,
    method: 'get'
  })
}
import request from '@/utils/request'

// 查询项目列表
export function listProject(query) {
  return request({
    url: '/system/project/list',
    method: 'get',
    params: query
  })
}

// 查询项目详细
export function getProject(projectId) {
  return request({
    url: '/system/project/' + projectId,
    method: 'get'
  })
}

// 新增项目
export function addProject(data) {
  return request({
    url: '/system/project',
    method: 'post',
    data: data
  })
}

// 修改项目
export function updateProject(data) {
  return request({
    url: '/system/project',
    method: 'put',
    data: data
  })
}

// 删除项目
export function delProject(projectId) {
  return request({
    url: '/system/project/' + projectId,
    method: 'delete'
  })
}

// 查询所有项目
export function listProjectAll() {
  return request({
    url: '/system/project/optionselect',
    method: 'get'
  })
}

// 查询项目重要事项
export function getMilestones(projectId) {
  return request({
    url: '/system/project/milestone/' + projectId,
    method: 'get'
  })
}

// 新增项目重要事项
export function addMilestone(data) {
  return request({
    url: '/system/project/milestone',
    method: 'post',
    data: data
  })
}

// 删除项目重要事项
export function delMilestone(id) {
  return request({
    url: '/system/project/milestone/' + id,
    method: 'delete'
  })
}
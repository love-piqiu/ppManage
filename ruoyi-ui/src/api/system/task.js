import request from '@/utils/request'

// 查询任务列表
export function listTask(query) {
  return request({
    url: '/system/task/list',
    method: 'get',
    params: query
  })
}

// 查询任务详细
export function getTask(taskId) {
  return request({
    url: '/system/task/' + taskId,
    method: 'get'
  })
}

// 新增任务
export function addTask(data) {
  return request({
    url: '/system/task',
    method: 'post',
    data: data
  })
}

// 修改任务
export function updateTask(data) {
  return request({
    url: '/system/task',
    method: 'put',
    data: data
  })
}

// 删除任务
export function delTask(taskId) {
  return request({
    url: '/system/task/' + taskId,
    method: 'delete'
  })
}

// 查询任务实例列表
export function listInstance(query) {
  return request({
    url: '/system/task/instance/list',
    method: 'get',
    params: query
  })
}

// 查询某人某周期的任务实例
export function getPersonInstances(personId, period) {
  return request({
    url: '/system/task/instance/person/' + personId + '/' + period,
    method: 'get'
  })
}

// 新增任务实例
export function addInstance(data) {
  return request({
    url: '/system/task/instance',
    method: 'post',
    data: data
  })
}

// 完成任务
export function completeTask(id, remark) {
  return request({
    url: '/system/task/instance/complete/' + id,
    method: 'put',
    params: { remark }
  })
}

// 删除任务实例
export function delInstance(id) {
  return request({
    url: '/system/task/instance/' + id,
    method: 'delete'
  })
}

// 获取某人某周期的完成率
export function getCompletionRate(personId, period) {
  return request({
    url: '/system/task/statistics/rate/' + personId + '/' + period,
    method: 'get'
  })
}

// 获取团队某周期的完成率
export function getTeamCompletionRate(period) {
  return request({
    url: '/system/task/statistics/team/' + period,
    method: 'get'
  })
}
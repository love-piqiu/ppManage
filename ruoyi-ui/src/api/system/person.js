import request from '@/utils/request'

// 查询人员列表
export function listPerson(query) {
  return request({
    url: '/system/person/list',
    method: 'get',
    params: query
  })
}

// 查询人员详细
export function getPerson(personId) {
  return request({
    url: '/system/person/' + personId,
    method: 'get'
  })
}

// 新增人员
export function addPerson(data) {
  return request({
    url: '/system/person',
    method: 'post',
    data: data
  })
}

// 修改人员
export function updatePerson(data) {
  return request({
    url: '/system/person',
    method: 'put',
    data: data
  })
}

// 删除人员
export function delPerson(personId) {
  return request({
    url: '/system/person/' + personId,
    method: 'delete'
  })
}

// 查询人员参与的项目
export function getPersonProjects(personId) {
  return request({
    url: '/system/person/projects/' + personId,
    method: 'get'
  })
}

// 分配人员到项目
export function assignProject(data) {
  return request({
    url: '/system/person/project',
    method: 'post',
    data: data
  })
}

// 移除人员项目关联
export function removeProject(id) {
  return request({
    url: '/system/person/project/' + id,
    method: 'delete'
  })
}

// 查询所有人员
export function listPersonAll() {
  return request({
    url: '/system/person/optionselect',
    method: 'get'
  })
}

// 查询所有在职人员（用于项目关联，不限制直属下级）
export function listPersonAllForProject() {
  return request({
    url: '/system/person/optionselectAll',
    method: 'get'
  })
}

// 查询人员状态统计
export function getPersonStatusCount() {
  return request({
    url: '/system/person/statusCount',
    method: 'get'
  })
}

// 查询人员动态记录列表
export function getPersonRecords(personId) {
  return request({
    url: '/system/person/record/list/' + personId,
    method: 'get'
  })
}

// 新增人员动态记录
export function addPersonRecord(data) {
  return request({
    url: '/system/person/record',
    method: 'post',
    data: data
  })
}

// 修改人员动态记录
export function updatePersonRecord(data) {
  return request({
    url: '/system/person/record',
    method: 'put',
    data: data
  })
}

// 删除人员动态记录
export function delPersonRecord(id) {
  return request({
    url: '/system/person/record/' + id,
    method: 'delete'
  })
}
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
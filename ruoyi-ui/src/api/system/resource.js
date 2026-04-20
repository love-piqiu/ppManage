import request from '@/utils/request'

// 查询资源列表（含技能和项目数）
export function listResource(params) {
  return request({
    url: '/system/resource/list',
    method: 'get',
    params: params
  })
}

// 查询技能分类列表
export function getSkillCategories() {
  return request({
    url: '/system/resource/skillCategories',
    method: 'get'
  })
}

// 查询人员技能列表
export function getPersonSkills(personId) {
  return request({
    url: '/system/resource/skills/' + personId,
    method: 'get'
  })
}

// 添加人员技能
export function addPersonSkill(data) {
  return request({
    url: '/system/resource/skill',
    method: 'post',
    data: data
  })
}

// 删除人员技能
export function delPersonSkill(id) {
  return request({
    url: '/system/resource/skill/' + id,
    method: 'delete'
  })
}

// 导出技能数据
export function exportSkill() {
  return request({
    url: '/system/resource/exportSkill',
    method: 'post'
  })
}

// 导出项目参与数据
export function exportProject() {
  return request({
    url: '/system/resource/exportProject',
    method: 'post'
  })
}

// 获取资源统计概览
export function getResourceOverview() {
  return request({
    url: '/system/resource/overview',
    method: 'get'
  })
}

// 更新人员资源状态
export function updateResourceStatus(personId, data) {
  return request({
    url: '/system/resource/status/' + personId,
    method: 'put',
    data: data
  })
}
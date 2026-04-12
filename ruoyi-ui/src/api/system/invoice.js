import request from '@/utils/request'

// 查询项目开票列表
export function listInvoice(projectId) {
  return request({
    url: '/system/invoice/list/' + projectId,
    method: 'get'
  })
}

// 查询项目开票详情
export function getInvoice(id) {
  return request({
    url: '/system/invoice/' + id,
    method: 'get'
  })
}

// 新增项目开票
export function addInvoice(data) {
  return request({
    url: '/system/invoice',
    method: 'post',
    data: data
  })
}

// 修改项目开票
export function updateInvoice(data) {
  return request({
    url: '/system/invoice',
    method: 'put',
    data: data
  })
}

// 删除项目开票
export function delInvoice(ids) {
  return request({
    url: '/system/invoice/' + ids,
    method: 'delete'
  })
}
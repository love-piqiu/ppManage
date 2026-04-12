import request from '@/utils/request'

// 获取周报数据（完整统计数据）
export function getWeeklyReportData(params) {
  return request({
    url: '/report/weekly/data',
    method: 'get',
    params: params
  })
}

// 生成周报HTML
export function generateWeeklyHtml(params) {
  return request({
    url: '/report/weekly/html',
    method: 'get',
    params: params
  })
}

// 发送周报邮件
export function sendWeeklyReport(data) {
  return request({
    url: '/report/weekly/send',
    method: 'post',
    data: data
  })
}

// 获取邮件配置
export function getEmailConfig() {
  return request({
    url: '/report/email/config',
    method: 'get'
  })
}

// 保存邮件配置
export function saveEmailConfig(data) {
  return request({
    url: '/report/email/config',
    method: 'post',
    data: data
  })
}

// 发送测试邮件
export function testEmail(data) {
  return request({
    url: '/report/email/test',
    method: 'post',
    data: data
  })
}

// 获取邮件预览HTML
export function getEmailPreview() {
  return request({
    url: '/report/email/preview',
    method: 'get'
  })
}
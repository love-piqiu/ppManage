import request from '@/utils/request'

// 生成周报内容
export function generateWeekly(params) {
  return request({
    url: '/report/weekly',
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
import request from '@/utils/request'

// 获取周报数据
export function getReportData(period) {
  // period 格式: 2026-W15 (年份-周数)
  let startDate, endDate
  if (period) {
    const [year, week] = period.split('-W')
    const weekNum = parseInt(week)
    const firstDay = new Date(parseInt(year), 0, 1)
    const daysOffset = (weekNum - 1) * 7
    const weekStart = new Date(firstDay.getTime() + daysOffset * 24 * 60 * 60 * 1000)
    const weekEnd = new Date(weekStart.getTime() + 6 * 24 * 60 * 60 * 1000)
    const formatDate = d => `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`
    startDate = formatDate(weekStart)
    endDate = formatDate(weekEnd)
  }
  return request({
    url: '/report/weekly',
    method: 'get',
    params: { startDate, endDate }
  })
}

// 发送周报邮件
export function sendReportEmail(period) {
  let startDate, endDate
  if (period) {
    const [year, week] = period.split('-W')
    const weekNum = parseInt(week)
    const firstDay = new Date(parseInt(year), 0, 1)
    const daysOffset = (weekNum - 1) * 7
    const weekStart = new Date(firstDay.getTime() + daysOffset * 24 * 60 * 60 * 1000)
    const weekEnd = new Date(weekStart.getTime() + 6 * 24 * 60 * 60 * 1000)
    const formatDate = d => `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`
    startDate = formatDate(weekStart)
    endDate = formatDate(weekEnd)
  }
  return request({
    url: '/report/weekly/send',
    method: 'post',
    data: { startDate, endDate }
  })
}

// 测试邮件发送
export function testReportEmail() {
  return request({
    url: '/report/email/test',
    method: 'post'
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
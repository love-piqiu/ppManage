import request from '@/utils/request'

// 获取邮件配置
export function getEmailConfig() {
  return request({
    url: '/system/email/config',
    method: 'get'
  })
}

// 更新邮件配置
export function updateEmailConfig(data) {
  return request({
    url: '/system/email/config',
    method: 'put',
    data: data
  })
}

// 发送测试邮件
export function sendTestEmail() {
  return request({
    url: '/system/email/test',
    method: 'post'
  })
}
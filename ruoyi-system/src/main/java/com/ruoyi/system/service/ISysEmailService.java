package com.ruoyi.system.service;

/**
 * 邮件发送 服务层
 *
 * @author ppmanage
 */
public interface ISysEmailService
{
    /**
     * 发送周报邮件
     *
     * @param subject 邮件主题
     * @param htmlContent HTML内容
     * @return 是否成功
     */
    public boolean sendWeeklyReport(String subject, String htmlContent);

    /**
     * 发送测试邮件
     *
     * @return 是否成功
     */
    public boolean sendTestEmail();
}
package com.ruoyi.system.service;

/**
 * 邥件发送 服务层
 *
 * @author ppmanage
 */
public interface ISysEmailService
{
    /**
     * 发送周报邮件
     *
     * @param subject 邥件主题
     * @param htmlContent HTML内容
     * @return 是否成功
     */
    public boolean sendWeeklyReport(String subject, String htmlContent);

    /**
     * 发送周报邮件（返回详细错误信息）
     *
     * @param subject 邥件主题
     * @param htmlContent HTML内容
     * @return 成功返回null，失败返回错误信息
     */
    public String sendWeeklyReportWithDetail(String subject, String htmlContent);

    /**
     * 发送测试邮件
     *
     * @return 是否成功
     */
    public boolean sendTestEmail();

    /**
     * 发送测试邮件（返回详细错误信息）
     *
     * @return 成功返回null，失败返回错误信息
     */
    public String sendTestEmailWithDetail();
}
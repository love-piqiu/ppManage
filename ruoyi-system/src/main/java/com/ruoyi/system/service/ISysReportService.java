package com.ruoyi.system.service;

import java.util.Map;

/**
 * 周报 服务层
 *
 * @author ppmanage
 */
public interface ISysReportService
{
    /**
     * 获取周报完整数据（新版）
     *
     * @param startDate 开始日期(yyyy-MM-dd)
     * @param endDate 结束日期(yyyy-MM-dd)
     * @return 周报数据（包含overview、projectProgress、issueList等）
     */
    public Map<String, Object> getWeeklyReportData(String startDate, String endDate);

    /**
     * 生成周报内容
     *
     * @param startDate 开始日期(yyyy-MM-dd)
     * @param endDate 结束日期(yyyy-MM-dd)
     * @return 周报内容
     */
    public Map<String, Object> generateWeeklyReport(String startDate, String endDate);

    /**
     * 生成周报HTML
     *
     * @param startDate 开始日期(yyyy-MM-dd)
     * @param endDate 结束日期(yyyy-MM-dd)
     * @return HTML内容
     */
    public String generateWeeklyHtml(String startDate, String endDate);

    /**
     * 生成邮件预览HTML
     *
     * @param startDate 开始日期(yyyy-MM-dd)
     * @param endDate 结束日期(yyyy-MM-dd)
     * @return 邮件HTML内容
     */
    public String generateEmailPreviewHtml(String startDate, String endDate);
}
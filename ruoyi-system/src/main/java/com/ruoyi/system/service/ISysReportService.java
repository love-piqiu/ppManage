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
}
package com.ruoyi.system.service;

import java.util.List;
import java.util.Map;

/**
 * 首页看板Service接口
 */
public interface IDashboardService {

    /**
     * 获取汇总统计数据
     */
    Map<String, Object> getSummary();

    /**
     * 获取预警数据
     */
    Map<String, Object> getAlerts();

    /**
     * 获取任务统计
     */
    Map<String, Object> getTaskStats();

    /**
     * 获取问题统计
     */
    Map<String, Object> getIssueStats();

    /**
     * 获取风险统计
     */
    Map<String, Object> getRiskStats();

    /**
     * 获取任务完成图表数据
     */
    List<Map<String, Object>> getTaskChartData();

    /**
     * 获取最近问题
     */
    List<Map<String, Object>> getRecentIssues(int limit);

    /**
     * 获取最近风险
     */
    List<Map<String, Object>> getRecentRisks(int limit);

    /**
     * 获取项目进度
     */
    List<Map<String, Object>> getProjectProgress();
}
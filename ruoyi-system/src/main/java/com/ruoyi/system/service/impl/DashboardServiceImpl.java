package com.ruoyi.system.service.impl;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruoyi.system.mapper.DashboardMapper;
import com.ruoyi.system.service.IDashboardService;

/**
 * 首页看板Service实现
 */
@Service
public class DashboardServiceImpl implements IDashboardService {

    @Autowired
    private DashboardMapper dashboardMapper;

    @Override
    public Map<String, Object> getSummary() {
        Map<String, Object> summary = new HashMap<>();

        // 进行中项目数
        summary.put("activeProjects", dashboardMapper.countActiveProjects());

        // 待处理问题数
        summary.put("pendingIssues", dashboardMapper.countPendingIssues());

        // 潜在风险数
        summary.put("potentialRisks", dashboardMapper.countPotentialRisks());

        // 任务完成率
        Integer totalTasks = dashboardMapper.countTotalTasksThisWeek();
        Integer completedTasks = dashboardMapper.countCompletedTasksThisWeek();
        int rate = totalTasks > 0 ? (completedTasks * 100 / totalTasks) : 0;
        summary.put("taskCompletionRate", rate);

        // 趋势数据（简化处理）
        summary.put("issueTrend", 0);
        summary.put("riskTrend", 0);
        summary.put("rateChange", 0);

        return summary;
    }

    @Override
    public Map<String, Object> getAlerts() {
        Map<String, Object> alerts = new HashMap<>();

        // 超期任务数
        alerts.put("overdueTasks", dashboardMapper.countOverdueTasks());

        // 高优先级问题数
        alerts.put("highPriorityIssues", dashboardMapper.countHighPriorityIssues());

        // 高风险数
        alerts.put("highRisks", dashboardMapper.countHighRisks());

        // 本周到期任务数
        alerts.put("weekDue", dashboardMapper.countWeekDueTasks());

        return alerts;
    }

    @Override
    public Map<String, Object> getTaskStats() {
        Map<String, Object> stats = new HashMap<>();

        // 本周总任务数
        Integer total = dashboardMapper.countTotalTasksThisWeek();
        stats.put("total", total);

        // 按时完成
        stats.put("onTime", dashboardMapper.countOnTimeTasksThisWeek());

        // 超期完成
        stats.put("late", dashboardMapper.countLateTasksThisWeek());

        // 未完成
        stats.put("incomplete", dashboardMapper.countIncompleteTasksThisWeek());

        // 完成率
        Integer completed = (Integer) stats.get("onTime") + (Integer) stats.get("late");
        int rate = total > 0 ? (completed * 100 / total) : 0;
        stats.put("rate", rate);

        return stats;
    }

    @Override
    public Map<String, Object> getIssueStats() {
        Map<String, Object> stats = new HashMap<>();

        stats.put("pending", dashboardMapper.countIssuesByStatus("待处理"));
        stats.put("inProgress", dashboardMapper.countIssuesByStatus("进行中"));
        stats.put("resolved", dashboardMapper.countIssuesByStatus("已解决"));
        stats.put("closed", dashboardMapper.countIssuesByStatus("已关闭"));

        return stats;
    }

    @Override
    public Map<String, Object> getRiskStats() {
        Map<String, Object> stats = new HashMap<>();

        // 按等级统计
        stats.put("high", dashboardMapper.countRisksByLevel("高"));
        stats.put("medium", dashboardMapper.countRisksByLevel("中"));
        stats.put("low", dashboardMapper.countRisksByLevel("低"));

        // 总数
        int total = (Integer) stats.get("high") + (Integer) stats.get("medium") + (Integer) stats.get("low");
        stats.put("total", total);

        // 按状态统计
        stats.put("potential", dashboardMapper.countRisksByStatus("潜在"));
        stats.put("occurred", dashboardMapper.countRisksByStatus("已发生"));

        return stats;
    }

    @Override
    public List<Map<String, Object>> getTaskChartData() {
        List<Map<String, Object>> chartData = new ArrayList<>();

        // 获取本周一到周五的数据
        LocalDate today = LocalDate.now();
        LocalDate monday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

        String[] dayNames = {"周一", "周二", "周三", "周四", "周五"};
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (int i = 0; i < 5; i++) {
            LocalDate date = monday.plusDays(i);
            String dateStr = date.format(formatter);

            Map<String, Object> dayData = new HashMap<>();
            dayData.put("day", dayNames[i]);
            dayData.put("date", dateStr);
            dayData.put("onTime", dashboardMapper.countTasksByDateAndStatus(dateStr, "ontime"));
            dayData.put("late", dashboardMapper.countTasksByDateAndStatus(dateStr, "late"));
            dayData.put("incomplete", dashboardMapper.countTasksByDateAndStatus(dateStr, "incomplete"));

            chartData.add(dayData);
        }

        return chartData;
    }

    @Override
    public List<Map<String, Object>> getRecentIssues(int limit) {
        return dashboardMapper.selectRecentIssues(limit);
    }

    @Override
    public List<Map<String, Object>> getRecentRisks(int limit) {
        return dashboardMapper.selectRecentRisks(limit);
    }

    @Override
    public List<Map<String, Object>> getProjectProgress() {
        return dashboardMapper.selectProjectProgress();
    }
}
package com.ruoyi.system.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

/**
 * 首页看板Mapper接口
 */
public interface DashboardMapper {

    // 项目统计
    int countActiveProjects();

    // 问题统计
    int countPendingIssues();
    int countHighPriorityIssues();
    int countIssuesByStatus(@Param("status") String status);

    // 风险统计
    int countPotentialRisks();
    int countHighRisks();
    int countRisksByLevel(@Param("level") String level);
    int countRisksByStatus(@Param("status") String status);

    // 任务统计
    int countOverdueTasks();
    int countWeekDueTasks();
    int countTotalTasksThisWeek();
    int countCompletedTasksThisWeek();
    int countOnTimeTasksThisWeek();
    int countLateTasksThisWeek();
    int countIncompleteTasksThisWeek();
    int countTasksByDateAndStatus(@Param("date") String date, @Param("status") String status);

    // 列表查询
    List<Map<String, Object>> selectRecentIssues(@Param("limit") int limit);
    List<Map<String, Object>> selectRecentRisks(@Param("limit") int limit);
    List<Map<String, Object>> selectProjectProgress();
}
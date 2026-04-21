package com.ruoyi.system.service.impl;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Date;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.domain.SysIssue;
import com.ruoyi.system.domain.SysProject;
import com.ruoyi.system.domain.SysRisk;
import com.ruoyi.system.domain.SysTask;
import com.ruoyi.system.domain.SysTaskInstance;
import com.ruoyi.system.domain.SysProjectMilestone;
import com.ruoyi.system.service.ISysIssueService;
import com.ruoyi.system.service.ISysProjectService;
import com.ruoyi.system.service.ISysRiskService;
import com.ruoyi.system.service.ISysPersonService;
import com.ruoyi.system.service.ISysTaskService;
import com.ruoyi.system.service.ISysTaskInstanceService;
import com.ruoyi.system.service.ISysProjectMilestoneService;
import com.ruoyi.system.service.ISysReportService;

/**
 * 周报 服务层处理
 *
 * @author ppmanage
 */
@Service
public class SysReportServiceImpl implements ISysReportService
{
    @Autowired
    private ISysProjectService projectService;

    @Autowired
    private ISysIssueService issueService;

    @Autowired
    private ISysRiskService riskService;

    @Autowired
    private ISysPersonService personService;

    @Autowired
    private ISysTaskService taskService;

    @Autowired
    private ISysTaskInstanceService taskInstanceService;

    @Autowired
    private ISysProjectMilestoneService milestoneService;

    /**
     * 计算周期字符串 (格式: 2026-W15)
     */
    private String getPeriodString(String dateStr)
    {
        try
        {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date date = sdf.parse(dateStr);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            int year = cal.get(Calendar.YEAR);
            int week = cal.get(Calendar.WEEK_OF_YEAR);
            return year + "-W" + week;
        }
        catch (Exception e)
        {
            return "";
        }
    }

    /**
     * 计算成本使用率
     */
    private int calcCostRate(SysProject p)
    {
        if (p.getCost() == null || p.getCost().compareTo(BigDecimal.ZERO) == 0)
        {
            return 0;
        }
        BigDecimal used = p.getCostUsed() != null ? p.getCostUsed() : BigDecimal.ZERO;
        return used.multiply(new BigDecimal(100)).divide(p.getCost(), 0, RoundingMode.HALF_UP).intValue();
    }

    /**
     * 计算工时使用率
     */
    private int calcHoursRate(SysProject p)
    {
        if (p.getWorkHours() == null || p.getWorkHours() == 0)
        {
            return 0;
        }
        int used = p.getWorkHoursUsed() != null ? p.getWorkHoursUsed() : 0;
        return used * 100 / p.getWorkHours();
    }

    /**
     * 格式化日期
     */
    private String formatDate(Date date)
    {
        if (date == null) return "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date);
    }

    /**
     * 生成周报内容
     */
    @Override
    public Map<String, Object> generateWeeklyReport(String startDate, String endDate)
    {
        Map<String, Object> report = new HashMap<>();
        String period = getPeriodString(startDate);

        // ===== 本周概览 =====
        Map<String, Object> overview = new HashMap<>();
        overview.put("activeProjects", projectService.countActiveProject());

        // 统计问题
        int newIssues = 0;
        int resolvedIssues = 0;
        // 基于createTime和updateTime统计
        SysIssue issueQuery = new SysIssue();
        List<SysIssue> allIssues = issueService.selectIssueList(issueQuery);
        for (SysIssue i : allIssues)
        {
            if (i.getCreateTime() != null)
            {
                String created = formatDate(i.getCreateTime());
                if (created.compareTo(startDate) >= 0 && created.compareTo(endDate) <= 0)
                {
                    newIssues++;
                }
            }
            if ("已解决".equals(i.getStatus()) || "已关闭".equals(i.getStatus()))
            {
                if (i.getUpdateTime() != null)
                {
                    String updated = formatDate(i.getUpdateTime());
                    if (updated.compareTo(startDate) >= 0 && updated.compareTo(endDate) <= 0)
                    {
                        resolvedIssues++;
                    }
                }
            }
        }
        overview.put("newIssues", newIssues);
        overview.put("resolvedIssues", resolvedIssues);

        // 任务完成率（使用带日期范围的计算，包含每日任务并过滤直属下级）
        int taskRate = taskInstanceService.getTeamCompletionRate(period, startDate, endDate);
        overview.put("taskRate", taskRate);
        report.put("overview", overview);

        // ===== 项目进度 =====
        List<Map<String, Object>> projectList = new ArrayList<>();

        // 进行中项目（只显示下辖项目）
        SysProject projectQuery = new SysProject();
        projectQuery.setStatus("进行中");
        projectQuery.setIsSubordinate("是");
        List<SysProject> activeProjects = projectService.selectProjectList(projectQuery);
        for (SysProject p : activeProjects)
        {
            Map<String, Object> item = new HashMap<>();
            item.put("id", p.getId());
            // 项目名称：客户名称-项目名称
            String displayName = (p.getCustomer() != null && !p.getCustomer().isEmpty())
                ? p.getCustomer() + "-" + p.getName()
                : p.getName();
            item.put("name", displayName);
            item.put("stage", p.getStage() != null ? p.getStage() : "需求确认");

            // 计算距离截止日期天数
            String stageDate = "";
            boolean isExpiring = false;
            if (p.getEndDate() != null)
            {
                long diff = p.getEndDate().getTime() - new Date().getTime();
                int daysToDeadline = (int) (diff / (24 * 60 * 60 * 1000));
                if ("UAT测试".equals(p.getStage()) && daysToDeadline <= 7 && daysToDeadline > 0)
                {
                    stageDate = "即将到期: " + formatDate(p.getEndDate());
                    isExpiring = true;
                }
                else if (daysToDeadline > 0)
                {
                    stageDate = "验收: " + formatDate(p.getEndDate());
                }
                else if (daysToDeadline <= 0)
                {
                    stageDate = "已到期: " + formatDate(p.getEndDate());
                    isExpiring = true;
                }
            }
            item.put("stageDate", stageDate);
            item.put("isExpiring", isExpiring);

            // 成本使用率和工时使用率
            int costRate = calcCostRate(p);
            int hoursRate = calcHoursRate(p);
            item.put("costRate", costRate);
            item.put("hoursRate", hoursRate);

            item.put("status", p.getStatus());
            item.put("change", ""); // 本周变化需要对比历史数据
            item.put("changeType", "none");
            projectList.add(item);
        }

        // 已完成项目（只显示下辖项目）
        SysProject completedQuery = new SysProject();
        completedQuery.setStatus("已完成");
        completedQuery.setIsSubordinate("是");
        List<SysProject> completedProjects = projectService.selectProjectList(completedQuery);
        for (SysProject p : completedProjects)
        {
            Map<String, Object> item = new HashMap<>();
            item.put("id", p.getId());
            // 项目名称：客户名称-项目名称
            String displayName = (p.getCustomer() != null && !p.getCustomer().isEmpty())
                ? p.getCustomer() + "-" + p.getName()
                : p.getName();
            item.put("name", displayName);
            item.put("stage", "验收交付");
            item.put("stageDate", "已完成 " + formatDate(p.getActualEndDate()));
            item.put("costRate", calcCostRate(p));
            item.put("hoursRate", calcHoursRate(p));
            item.put("status", "已完成");
            item.put("change", "验收通过");
            item.put("changeType", "success");
            projectList.add(item);
        }

        // 暂停项目（只显示下辖项目）
        SysProject pausedQuery = new SysProject();
        pausedQuery.setStatus("暂停");
        pausedQuery.setIsSubordinate("是");
        List<SysProject> pausedProjects = projectService.selectProjectList(pausedQuery);
        for (SysProject p : pausedProjects)
        {
            Map<String, Object> item = new HashMap<>();
            item.put("id", p.getId());
            // 项目名称：客户名称-项目名称
            String displayName = (p.getCustomer() != null && !p.getCustomer().isEmpty())
                ? p.getCustomer() + "-" + p.getName()
                : p.getName();
            item.put("name", displayName);
            item.put("stage", p.getStage() != null ? p.getStage() : "需求确认");
            item.put("stageDate", "已暂停");
            item.put("costRate", calcCostRate(p));
            item.put("hoursRate", calcHoursRate(p));
            item.put("status", "暂停");
            item.put("change", "-");
            item.put("changeType", "none");
            projectList.add(item);
        }

        report.put("projects", projectList);

        // 项目汇总
        Map<String, Object> projectSummary = new HashMap<>();
        projectSummary.put("active", activeProjects.size());
        projectSummary.put("completed", completedProjects.size());
        projectSummary.put("paused", pausedProjects.size());
        projectSummary.put("costOver", (int) projectList.stream().filter(p -> ((Number) p.get("costRate")).intValue() > 100).count());
        projectSummary.put("hoursOver", (int) projectList.stream().filter(p -> ((Number) p.get("hoursRate")).intValue() > 100).count());
        report.put("projectSummary", projectSummary);

        // ===== 问题跟踪 =====
        List<Map<String, Object>> issueList = new ArrayList<>();
        for (SysIssue i : allIssues)
        {
            Map<String, Object> item = new HashMap<>();
            item.put("id", i.getId());
            item.put("description", i.getDescription());
            item.put("projectName", i.getProjectName());
            item.put("severity", i.getSeverity());
            item.put("status", i.getStatus());
            issueList.add(item);
        }
        report.put("issues", issueList);

        // 问题汇总
        Map<String, Object> issueSummary = new HashMap<>();
        issueSummary.put("new", newIssues);
        issueSummary.put("resolved", resolvedIssues);
        issueSummary.put("pending", issueService.countUnresolvedIssue());
        report.put("issueSummary", issueSummary);

        // ===== 风险管理 =====
        List<Map<String, Object>> riskList = new ArrayList<>();
        SysRisk riskQuery = new SysRisk();
        List<SysRisk> allRisks = riskService.selectRiskList(riskQuery);
        for (SysRisk r : allRisks)
        {
            Map<String, Object> item = new HashMap<>();
            item.put("id", r.getId());
            item.put("description", r.getDescription());
            item.put("projectName", r.getProjectName());
            item.put("level", r.getLevel());
            item.put("status", r.getStatus());
            item.put("measure", r.getMeasure());
            riskList.add(item);
        }
        report.put("risks", riskList);

        // 风险汇总
        Map<String, Object> riskSummary = new HashMap<>();
        riskSummary.put("total", allRisks.size());
        riskSummary.put("high", (int) allRisks.stream().filter(r -> "高".equals(r.getLevel())).count());
        report.put("riskSummary", riskSummary);

        // ===== 任务完成情况 =====
        // 周期性任务实例（按周期格式查询，如2026-W15）
        List<SysTaskInstance> cycleInstances = taskInstanceService.selectByPeriod(period);
        // 每日任务实例（按日期范围查询，如2026-04-19）
        List<SysTaskInstance> dailyInstances = taskInstanceService.selectByPeriodRange(startDate, endDate);
        // 一次性任务实例（按日期范围查询）
        List<SysTaskInstance> onceInstances = taskInstanceService.selectOnceInstancesByRange(startDate, endDate);
        // 合并所有实例
        List<SysTaskInstance> instances = new ArrayList<>();
        instances.addAll(cycleInstances);
        instances.addAll(dailyInstances);
        instances.addAll(onceInstances);

        int completedCount = 0;
        int pendingCount = 0;
        int overdueCount = 0;
        Date now = new Date();
        for (SysTaskInstance inst : instances)
        {
            if (inst.getCompleted() != null && inst.getCompleted() == 1)
            {
                completedCount++;
            }
            else
            {
                pendingCount++;
                if (inst.getDeadline() != null && now.after(inst.getDeadline()))
                {
                    overdueCount++;
                }
            }
        }

        Map<String, Object> taskStats = new HashMap<>();
        int total = instances.size();
        taskStats.put("completed", completedCount);
        taskStats.put("pending", pendingCount);
        taskStats.put("overdue", overdueCount);
        taskStats.put("total", total);
        taskStats.put("rate", total > 0 ? Math.round(completedCount * 100.0 / total) : 0);

        // 任务类型统计（从数据库查询实际数据）
        Map<String, Object> cycleStats = taskInstanceService.getCycleTaskStats(period, startDate, endDate);
        Map<String, Object> onceStats = taskInstanceService.getOnceTaskStats(startDate, endDate);
        taskStats.put("cycleTotal", cycleStats.get("total"));
        taskStats.put("cycleRate", cycleStats.get("rate"));
        taskStats.put("onceTotal", onceStats.get("total"));
        taskStats.put("onceRate", onceStats.get("rate"));
        report.put("taskStats", taskStats);

        // 任务详情列表（只显示本周有实例的任务）
        List<Map<String, Object>> taskDetails = new ArrayList<>();
        // 周期性任务详情
        for (SysTaskInstance inst : cycleInstances)
        {
            // 每个任务只显示一次
            Long taskId = inst.getTaskId();
            boolean alreadyAdded = taskDetails.stream().anyMatch(d -> d.get("id").equals(taskId));
            if (!alreadyAdded && taskId != null)
            {
                Map<String, Object> item = new HashMap<>();
                item.put("id", taskId);
                item.put("name", inst.getTaskName());
                item.put("cycle", "周期性");

                // 统计该任务在本周的完成情况
                long taskCompleted = instances.stream()
                    .filter(i -> i.getTaskId() != null && i.getTaskId().equals(taskId) && i.getCompleted() != null && i.getCompleted() == 1)
                    .count();
                long taskTotal = instances.stream()
                    .filter(i -> i.getTaskId() != null && i.getTaskId().equals(taskId))
                    .count();
                item.put("completedCount", (int) taskCompleted);
                item.put("totalCount", (int) taskTotal);
                item.put("rate", taskTotal > 0 ? Math.round(taskCompleted * 100.0 / taskTotal) : 0);
                taskDetails.add(item);
            }
        }
        // 一次性任务详情
        for (SysTaskInstance inst : onceInstances)
        {
            Long taskId = inst.getTaskId();
            boolean alreadyAdded = taskDetails.stream().anyMatch(d -> d.get("id").equals(taskId));
            if (!alreadyAdded && taskId != null)
            {
                Map<String, Object> item = new HashMap<>();
                item.put("id", taskId);
                item.put("name", inst.getTaskName());
                item.put("cycle", "一次性");

                long taskCompleted = instances.stream()
                    .filter(i -> i.getTaskId() != null && i.getTaskId().equals(taskId) && i.getCompleted() != null && i.getCompleted() == 1)
                    .count();
                long taskTotal = instances.stream()
                    .filter(i -> i.getTaskId() != null && i.getTaskId().equals(taskId))
                    .count();
                item.put("completedCount", (int) taskCompleted);
                item.put("totalCount", (int) taskTotal);
                item.put("rate", taskTotal > 0 ? Math.round(taskCompleted * 100.0 / taskTotal) : 0);
                taskDetails.add(item);
            }
        }
        report.put("taskDetails", taskDetails);

        // 人员完成率排名（传入日期范围以包含每日任务）
        List<Map<String, Object>> personRanking = taskInstanceService.getPersonCompletionList(period, startDate, endDate);
        report.put("personRanking", personRanking);

        // ===== 本周重要事项 =====
        List<Map<String, Object>> milestoneList = new ArrayList<>();
        // 获取下辖项目的重要事项
        SysProject subordinateQuery = new SysProject();
        subordinateQuery.setIsSubordinate("是");
        List<SysProject> subordinateProjects = projectService.selectProjectList(subordinateQuery);
        for (SysProject proj : subordinateProjects)
        {
            List<SysProjectMilestone> projMilestones = milestoneService.selectByProjectId(proj.getId());
            for (SysProjectMilestone m : projMilestones)
            {
                if (m.getRecordDate() != null)
                {
                    String recordDateStr = formatDate(m.getRecordDate());
                    if (recordDateStr.compareTo(startDate) >= 0 && recordDateStr.compareTo(endDate) <= 0)
                    {
                        Map<String, Object> item = new HashMap<>();
                        item.put("id", m.getId());
                        item.put("date", recordDateStr);
                        // 项目名称：客户名称-项目名称
                        String displayName = (proj.getCustomer() != null && !proj.getCustomer().isEmpty())
                            ? proj.getCustomer() + "-" + proj.getName()
                            : proj.getName();
                        item.put("projectName", displayName);
                        item.put("description", m.getDescription());
                        milestoneList.add(item);
                    }
                }
            }
        }
        report.put("milestones", milestoneList);

        report.put("startDate", startDate);
        report.put("endDate", endDate);

        return report;
    }

    /**
     * 生成周报HTML（符合设计文档风格）
     */
    @Override
    public String generateWeeklyHtml(String startDate, String endDate)
    {
        Map<String, Object> report = generateWeeklyReport(startDate, endDate);

        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>");
        html.append("<html><head>");
        html.append("<meta charset='UTF-8'>");
        html.append("<style>");
        html.append("body { font-family: 'Plus Jakarta Sans', -apple-system, BlinkMacSystemFont, sans-serif; line-height: 1.6; color: #374151; margin: 0; padding: 20px; background: #F3F4F6; }");
        html.append(".email-container { max-width: 680px; margin: 0 auto; background: white; }");
        html.append(".email-header { text-align: center; padding: 24px 0; border-bottom: 2px solid #2563EB; margin-bottom: 24px; }");
        html.append(".email-header h1 { font-size: 24px; font-weight: 700; color: #2563EB; margin: 0; }");
        html.append(".email-header .date { font-size: 14px; color: #6B7280; margin-top: 8px; }");
        html.append(".overview-section { background: linear-gradient(135deg, #EFF6FF 0%, #DBEAFE 100%); border-radius: 12px; padding: 20px; margin-bottom: 24px; }");
        html.append(".overview-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 16px; text-align: center; }");
        html.append(".stat-value { font-size: 28px; font-weight: 700; color: #1F2937; }");
        html.append(".stat-label { font-size: 12px; color: #6B7280; }");
        html.append(".stat-danger { color: #EF4444; }");
        html.append(".stat-success { color: #10B981; }");
        html.append(".stat-warning { color: #F59E0B; }");
        html.append(".section { margin-bottom: 24px; }");
        html.append(".section-title { font-size: 16px; font-weight: 600; color: #1F2937; margin-bottom: 12px; padding-bottom: 8px; border-bottom: 2px solid #2563EB; display: inline-block; }");
        html.append("table { width: 100%; border-collapse: collapse; font-size: 13px; }");
        html.append("th, td { padding: 10px 8px; border-bottom: 1px solid #E5E7EB; text-align: left; }");
        html.append("th { background: #F3F4F6; font-weight: 600; color: #374151; }");
        html.append(".center { text-align: center; }");
        html.append(".tag { display: inline-block; padding: 2px 6px; border-radius: 4px; font-size: 11px; font-weight: 500; }");
        html.append(".tag-blue { background: #DBEAFE; color: #1D4ED8; }");
        html.append(".tag-green { background: #D1FAE5; color: #059669; }");
        html.append(".tag-orange { background: #FEF3C7; color: #B45309; }");
        html.append(".tag-red { background: #FEE2E2; color: #DC2626; }");
        html.append(".summary-bar { margin-top: 10px; padding: 10px; background: #F9FAFB; border-radius: 6px; font-size: 12px; color: #4B5563; }");
        html.append(".two-column { display: grid; grid-template-columns: 1fr 1fr; gap: 16px; margin-bottom: 24px; }");
        html.append(".card { background: #F9FAFB; border-radius: 8px; padding: 12px; }");
        html.append(".card-row { display: flex; justify-content: space-between; margin-bottom: 8px; font-size: 13px; }");
        html.append(".card-alert { margin-top: 10px; padding-top: 10px; border-top: 1px solid #E5E7EB; font-size: 12px; color: #EF4444; }");
        html.append(".task-stats { display: grid; grid-template-columns: repeat(5, 1fr); gap: 8px; margin-bottom: 16px; }");
        html.append(".task-stat-card { border-radius: 8px; padding: 12px; text-align: center; }");
        html.append(".task-stat-value { font-size: 24px; font-weight: 700; }");
        html.append(".task-stat-label { font-size: 11px; color: #6B7280; }");
        html.append(".task-stat-green { background: #D1FAE5; } .task-stat-green .task-stat-value { color: #10B981; }");
        html.append(".task-stat-yellow { background: #FEF3C7; } .task-stat-yellow .task-stat-value { color: #F59E0B; }");
        html.append(".task-stat-red { background: #FEE2E2; } .task-stat-red .task-stat-value { color: #EF4444; }");
        html.append(".task-stat-gray { background: #F3F4F6; } .task-stat-gray .task-stat-value { color: #374151; }");
        html.append(".task-stat-blue { background: #DBEAFE; } .task-stat-blue .task-stat-value { color: #2563EB; }");
        html.append(".person-ranking { display: flex; gap: 8px; margin-top: 16px; }");
        html.append(".person-rank-card { flex: 1; background: #F0FDF4; border: 1px solid #10B981; border-radius: 6px; padding: 8px; text-align: center; }");
        html.append(".person-rank-name { font-size: 11px; font-weight: 600; color: #1F2937; }");
        html.append(".person-rank-rate { font-size: 14px; font-weight: 700; color: #10B981; }");
        html.append(".email-footer { text-align: center; padding: 16px 0; border-top: 1px solid #E5E7EB; color: #9CA3AF; font-size: 12px; }");
        html.append("</style>");
        html.append("</head><body>");
        html.append("<div class='email-container'>");

        // 头部
        html.append("<div class='email-header'>");
        html.append("<h1>项目周报</h1>");
        html.append("<div class='date'>").append(startDate).append(" ~ ").append(endDate).append("</div>");
        html.append("</div>");

        // 概览统计
        @SuppressWarnings("unchecked")
        Map<String, Object> overview = (Map<String, Object>) report.get("overview");
        html.append("<div class='overview-section'>");
        html.append("<div class='overview-grid'>");
        html.append("<div><div class='stat-value'>").append(overview.get("activeProjects")).append("</div><div class='stat-label'>进行中项目</div></div>");
        html.append("<div><div class='stat-value stat-danger'>").append(overview.get("newIssues")).append("</div><div class='stat-label'>新增问题</div></div>");
        html.append("<div><div class='stat-value stat-success'>").append(overview.get("resolvedIssues")).append("</div><div class='stat-label'>已解决问题</div></div>");
        html.append("<div><div class='stat-value stat-warning'>").append(overview.get("taskRate")).append("%</div><div class='stat-label'>任务完成率</div></div>");
        html.append("</div></div>");

        // 项目进度
        html.append("<div class='section'>");
        html.append("<div class='section-title'>📁 项目进度</div>");
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> projects = (List<Map<String, Object>>) report.get("projects");
        html.append("<table>");
        html.append("<tr><th>项目名称</th><th class='center'>当前阶段</th><th class='center'>成本</th><th class='center'>状态</th></tr>");
        for (Map<String, Object> p : projects)
        {
            String stage = (String) p.get("stage");
            String stageColor = "验收交付".equals(stage) || "上线部署".equals(stage) ? "#10B981" : "UAT测试".equals(stage) ? "#F59E0B" : "#6B7280";

            html.append("<tr>");
            html.append("<td>").append(p.get("name")).append("</td>");
            html.append("<td class='center'><strong style='color:").append(stageColor).append("'>").append(stage).append("</strong></td>");

            // 获取成本金额
            String costDisplay = p.get("costRate") + "%";
            html.append("<td class='center'>").append(costDisplay).append("</td>");

            String status = (String) p.get("status");
            String tagClass = "进行中".equals(status) ? "tag-blue" : "已完成".equals(status) ? "tag-green" : "tag-orange";
            html.append("<td class='center'><span class='tag ").append(tagClass).append("'>").append(status).append("</span></td>");
            html.append("</tr>");
        }
        html.append("</table>");

        // 项目汇总
        @SuppressWarnings("unchecked")
        Map<String, Object> projectSummary = (Map<String, Object>) report.get("projectSummary");
        html.append("<div class='summary-bar'><strong>汇总：</strong>进行中 ").append(projectSummary.get("active")).append(" 个 | 已完成 ").append(projectSummary.get("completed")).append(" 个 | 暂停 ").append(projectSummary.get("paused")).append(" 个 | 成本超支 ").append(projectSummary.get("costOver")).append(" 个</div>");
        html.append("</div>");

        // 问题跟踪和风险管理（并排显示）
        html.append("<div class='two-column'>");

        // 问题跟踪
        html.append("<div>");
        html.append("<div class='section-title'>🐛 问题跟踪</div>");
        html.append("<div class='card'>");
        @SuppressWarnings("unchecked")
        Map<String, Object> issueSummary = (Map<String, Object>) report.get("issueSummary");
        html.append("<div class='card-row'><span>新增问题</span><span class='stat-danger' style='font-weight:600'>").append(issueSummary.get("new")).append(" 个</span></div>");
        html.append("<div class='card-row'><span>已解决</span><span class='stat-success' style='font-weight:600'>").append(issueSummary.get("resolved")).append(" 个</span></div>");
        html.append("<div class='card-row'><span>待处理</span><span class='stat-warning' style='font-weight:600'>").append(issueSummary.get("pending")).append(" 个</span></div>");

        // 高优先级问题
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> issues = (List<Map<String, Object>>) report.get("issues");
        String highPriorityIssue = "";
        for (Map<String, Object> i : issues)
        {
            if ("高".equals(i.get("severity")) && !"已解决".equals(i.get("status")) && !"已关闭".equals(i.get("status")))
            {
                highPriorityIssue = (String) i.get("description");
                break;
            }
        }
        if (!highPriorityIssue.isEmpty())
        {
            html.append("<div class='card-alert'>⚠️ 高优先级：").append(highPriorityIssue).append("</div>");
        }
        html.append("</div>");
        html.append("</div>");

        // 风险管理
        html.append("<div>");
        html.append("<div class='section-title'>⚠️ 风险管理</div>");
        html.append("<div class='card'>");
        @SuppressWarnings("unchecked")
        Map<String, Object> riskSummary = (Map<String, Object>) report.get("riskSummary");
        html.append("<div class='card-row'><span>跟踪风险</span><span style='font-weight:600'>").append(riskSummary.get("total")).append(" 个</span></div>");
        html.append("<div class='card-row'><span>高风险</span><span class='stat-danger' style='font-weight:600'>").append(riskSummary.get("high")).append(" 个</span></div>");
        html.append("<div class='card-row'><span>已消除</span><span class='stat-success' style='font-weight:600'>").append(riskSummary.get("total") != null && riskSummary.get("high") != null ? ((Number)riskSummary.get("total")).intValue() - ((Number)riskSummary.get("high")).intValue() : 0).append(" 个</span></div>");

        // 高风险提示
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> risks = (List<Map<String, Object>>) report.get("risks");
        StringBuilder highRisks = new StringBuilder();
        for (Map<String, Object> r : risks)
        {
            if ("高".equals(r.get("level")))
            {
                if (highRisks.length() > 0) highRisks.append("、");
                highRisks.append(r.get("description"));
            }
        }
        if (highRisks.length() > 0)
        {
            html.append("<div class='card-alert'>⚠️ 高风险：").append(highRisks).append("</div>");
        }
        html.append("</div>");
        html.append("</div>");

        html.append("</div>");

        // 任务完成情况
        html.append("<div class='section'>");
        html.append("<div class='section-title'>✅ 任务完成情况</div>");

        @SuppressWarnings("unchecked")
        Map<String, Object> taskStats = (Map<String, Object>) report.get("taskStats");
        html.append("<div class='task-stats'>");
        html.append("<div class='task-stat-card task-stat-green'><div class='task-stat-value'>").append(taskStats.get("completed")).append("</div><div class='task-stat-label'>已完成</div></div>");
        html.append("<div class='task-stat-card task-stat-yellow'><div class='task-stat-value'>").append(taskStats.get("pending")).append("</div><div class='task-stat-label'>待完成</div></div>");
        html.append("<div class='task-stat-card task-stat-red'><div class='task-stat-value'>").append(taskStats.get("overdue")).append("</div><div class='task-stat-label'>已超期</div></div>");
        html.append("<div class='task-stat-card task-stat-gray'><div class='task-stat-value'>").append(taskStats.get("total")).append("</div><div class='task-stat-label'>总任务</div></div>");
        html.append("<div class='task-stat-card task-stat-blue'><div class='task-stat-value'>").append(taskStats.get("rate")).append("%</div><div class='task-stat-label'>完成率</div></div>");
        html.append("</div>");

        // 任务详情表格
        html.append("<table>");
        html.append("<tr><th>任务名称</th><th class='center'>类型</th><th class='center'>完成人数</th><th class='center'>完成率</th></tr>");
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> taskDetails = (List<Map<String, Object>>) report.get("taskDetails");
        for (Map<String, Object> t : taskDetails)
        {
            int rate = t.get("rate") != null ? ((Number) t.get("rate")).intValue() : 0;
            String rateColor = rate >= 80 ? "#10B981" : rate >= 50 ? "#F59E0B" : "#EF4444";

            html.append("<tr>");
            html.append("<td>").append(t.get("name")).append("</td>");
            html.append("<td class='center'>").append(t.get("cycle")).append("</td>");
            html.append("<td class='center'>").append(t.get("completedCount")).append("/").append(t.get("totalCount")).append("</td>");
            html.append("<td class='center'><strong style='color:").append(rateColor).append("'>").append(rate).append("%</strong></td>");
            html.append("</tr>");
        }
        html.append("</table>");

        // 人员完成率排名 Top 5
        html.append("<div style='margin-top:16px;'>");
        html.append("<div style='font-size:13px;font-weight:600;color:#374151;margin-bottom:8px;'>人员完成率 Top 5</div>");
        html.append("<div class='person-ranking'>");
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> personRanking = (List<Map<String, Object>>) report.get("personRanking");
        // 如果没有数据，使用模拟数据
        if (personRanking == null || personRanking.isEmpty())
        {
            // 从任务实例统计人员完成率
            @SuppressWarnings("unchecked")
            List<SysTaskInstance> instances = (List<SysTaskInstance>) report.get("instances");
            if (instances != null)
            {
                // 计算每人完成率
                Map<Long, Integer> personRates = new HashMap<>();
                Map<Long, String> personNames = new HashMap<>();
                for (SysTaskInstance inst : instances)
                {
                    Long pid = inst.getPersonId();
                    personNames.put(pid, inst.getPersonName());
                    // 简化计算
                }
            }
        }
        int rankCount = 0;
        for (Map<String, Object> person : personRanking)
        {
            if (rankCount >= 5) break;
            int personRate = person.get("rate") != null ? ((Number) person.get("rate")).intValue() : 0;
            html.append("<div class='person-rank-card'>");
            html.append("<div class='person-rank-name'>").append(person.get("name")).append("</div>");
            html.append("<div class='person-rank-rate'>").append(personRate).append("%</div>");
            html.append("</div>");
            rankCount++;
        }
        if (rankCount == 0)
        {
            // 显示默认占位
            for (int i = 0; i < 5; i++)
            {
                html.append("<div class='person-rank-card'>");
                html.append("<div class='person-rank-name'>-</div>");
                html.append("<div class='person-rank-rate'>-</div>");
                html.append("</div>");
            }
        }
        html.append("</div>");
        html.append("</div>");

        html.append("</div>");

        // 底部
        html.append("<div class='email-footer'>此邮件由项目管理系统自动发送 | 请勿直接回复</div>");
        html.append("</div>");
        html.append("</body></html>");

        return html.toString();
    }

    /**
     * 获取周报完整数据（新版）
     */
    @Override
    public Map<String, Object> getWeeklyReportData(String startDate, String endDate)
    {
        Map<String, Object> data = new HashMap<>();
        String period = getPeriodString(startDate);

        // ===== 概览统计 =====
        Map<String, Object> overview = new HashMap<>();
        overview.put("ongoingProjects", projectService.countActiveProject());

        // 统计问题
        int newIssues = 0, resolvedIssues = 0, pendingIssues = 0;
        SysIssue issueQuery = new SysIssue();
        List<SysIssue> allIssues = issueService.selectIssueList(issueQuery);
        for (SysIssue i : allIssues)
        {
            if (i.getCreateTime() != null)
            {
                String created = formatDate(i.getCreateTime());
                if (created.compareTo(startDate) >= 0 && created.compareTo(endDate) <= 0)
                    newIssues++;
            }
            if ("已解决".equals(i.getStatus()) || "已关闭".equals(i.getStatus()))
            {
                if (i.getUpdateTime() != null)
                {
                    String updated = formatDate(i.getUpdateTime());
                    if (updated.compareTo(startDate) >= 0 && updated.compareTo(endDate) <= 0)
                        resolvedIssues++;
                }
            }
            if ("待处理".equals(i.getStatus()) || "进行中".equals(i.getStatus()))
                pendingIssues++;
        }
        overview.put("newIssues", newIssues);
        overview.put("resolvedIssues", resolvedIssues);
        overview.put("pendingIssues", pendingIssues);

        // 统计风险
        SysRisk riskQuery = new SysRisk();
        List<SysRisk> allRisks = riskService.selectRiskList(riskQuery);
        overview.put("totalRisks", allRisks.size());
        overview.put("highRisks", (int) allRisks.stream().filter(r -> "高".equals(r.getLevel())).count());

        // 统计项目（只统计下辖项目）
        SysProject projectQuery = new SysProject();
        projectQuery.setIsSubordinate("是");
        List<SysProject> allProjects = projectService.selectProjectList(projectQuery);
        int ongoing = 0, completed = 0, paused = 0, costOverrun = 0, hourOverrun = 0;
        for (SysProject p : allProjects)
        {
            if ("进行中".equals(p.getStatus())) ongoing++;
            else if ("已完成".equals(p.getStatus())) completed++;
            else if ("暂停".equals(p.getStatus())) paused++;
            if (calcCostRate(p) > 100) costOverrun++;
            if (calcHoursRate(p) > 100) hourOverrun++;
        }
        overview.put("completedProjects", completed);
        overview.put("pausedProjects", paused);
        overview.put("costOverrun", costOverrun);
        overview.put("hourOverrun", hourOverrun);
        overview.put("taskRate", taskInstanceService.getTeamCompletionRate(period, startDate, endDate));
        data.put("overview", overview);

        // ===== 项目进度列表（只显示下辖项目） =====
        List<Map<String, Object>> projectProgress = new ArrayList<>();
        for (SysProject p : allProjects)
        {
            Map<String, Object> item = new HashMap<>();
            item.put("id", p.getId());
            // 项目名称：客户名称-项目名称
            String displayName = (p.getCustomer() != null && !p.getCustomer().isEmpty())
                ? p.getCustomer() + "-" + p.getName()
                : p.getName();
            item.put("name", displayName);
            item.put("stage", p.getStage() != null ? p.getStage() : "需求确认");
            item.put("costUsage", calcCostRate(p));
            item.put("hourUsage", calcHoursRate(p));
            item.put("status", p.getStatus());

            // 阶段状态
            String stageStatus = "pending";
            String stageDateInfo = "";
            String weekChange = "";
            if ("已完成".equals(p.getStatus()))
            {
                stageStatus = "completed";
                stageDateInfo = "已完成 " + formatDate(p.getActualEndDate());
                // 检查是否本周完成
                if (p.getActualEndDate() != null)
                {
                    String completedDate = formatDate(p.getActualEndDate());
                    if (completedDate.compareTo(startDate) >= 0 && completedDate.compareTo(endDate) <= 0)
                    {
                        weekChange = "验收通过";
                    }
                }
            }
            else if ("暂停".equals(p.getStatus()))
            {
                stageStatus = "paused";
                stageDateInfo = "已暂停";
                // 检查是否本周暂停
                if (p.getUpdateTime() != null)
                {
                    String pausedDate = formatDate(p.getUpdateTime());
                    if (pausedDate.compareTo(startDate) >= 0 && pausedDate.compareTo(endDate) <= 0)
                    {
                        weekChange = "项目暂停";
                    }
                }
            }
            else if (p.getEndDate() != null)
            {
                long diff = p.getEndDate().getTime() - new Date().getTime();
                int days = (int) (diff / (24 * 60 * 60 * 1000));
                if (days <= 0)
                {
                    stageStatus = "danger";
                    stageDateInfo = "已到期: " + formatDate(p.getEndDate());
                    // 检查是否本周到期
                    String expireDate = formatDate(p.getEndDate());
                    if (expireDate.compareTo(startDate) >= 0 && expireDate.compareTo(endDate) <= 0)
                    {
                        weekChange = "已到期";
                    }
                }
                else if (days <= 7)
                {
                    stageStatus = "warning";
                    stageDateInfo = "即将到期: " + formatDate(p.getEndDate());
                }
                else
                {
                    stageDateInfo = "验收: " + formatDate(p.getEndDate());
                }
            }
            item.put("stageStatus", stageStatus);
            item.put("stageDateInfo", stageDateInfo);
            item.put("weekChange", weekChange);
            projectProgress.add(item);
        }
        data.put("projectProgress", projectProgress);

        // ===== 问题列表 =====
        List<Map<String, Object>> issueList = new ArrayList<>();
        for (SysIssue i : allIssues)
        {
            Map<String, Object> item = new HashMap<>();
            item.put("id", i.getId());
            item.put("description", i.getDescription());
            // 项目名称：客户名称-项目名称
            String issueProjectName = (i.getCustomer() != null && !i.getCustomer().isEmpty())
                ? i.getCustomer() + "-" + i.getProjectName()
                : i.getProjectName();
            item.put("projectName", issueProjectName);
            item.put("severity", i.getSeverity());
            item.put("status", i.getStatus());
            issueList.add(item);
        }
        data.put("issueList", issueList);

        // ===== 风险列表 =====
        List<Map<String, Object>> riskList = new ArrayList<>();
        for (SysRisk r : allRisks)
        {
            Map<String, Object> item = new HashMap<>();
            item.put("id", r.getId());
            item.put("description", r.getDescription());
            // 项目名称：客户名称-项目名称
            String riskProjectName = (r.getCustomer() != null && !r.getCustomer().isEmpty())
                ? r.getCustomer() + "-" + r.getProjectName()
                : r.getProjectName();
            item.put("projectName", riskProjectName);
            item.put("level", r.getLevel());
            item.put("status", r.getStatus());
            item.put("measure", r.getMeasure());
            riskList.add(item);
        }
        data.put("riskList", riskList);

        // ===== 任务统计 =====
        // 周期性任务实例（按周期格式查询）
        List<SysTaskInstance> cycleInstances = taskInstanceService.selectByPeriod(period);
        // 每日任务实例（按日期范围查询）
        List<SysTaskInstance> dailyInstances = taskInstanceService.selectByPeriodRange(startDate, endDate);
        // 一次性任务实例（按日期范围查询）
        List<SysTaskInstance> onceInstances = taskInstanceService.selectOnceInstancesByRange(startDate, endDate);
        // 合并所有实例
        List<SysTaskInstance> instances = new ArrayList<>();
        instances.addAll(cycleInstances);
        instances.addAll(dailyInstances);
        instances.addAll(onceInstances);

        int taskCompleted = 0, taskPending = 0, taskOverdue = 0;
        Date now = new Date();
        for (SysTaskInstance inst : instances)
        {
            if (inst.getCompleted() != null && inst.getCompleted() == 1)
                taskCompleted++;
            else
            {
                taskPending++;
                if (inst.getDeadline() != null && now.after(inst.getDeadline()))
                    taskOverdue++;
            }
        }
        int taskTotal = instances.size();
        int taskRate = taskTotal > 0 ? Math.round(taskCompleted * 100.0f / taskTotal) : 0;

        // 任务类型统计（从数据库查询实际数据）
        Map<String, Object> cycleStats = taskInstanceService.getCycleTaskStats(period, startDate, endDate);
        Map<String, Object> onceStats = taskInstanceService.getOnceTaskStats(startDate, endDate);

        Map<String, Object> taskStats = new HashMap<>();
        taskStats.put("completed", taskCompleted);
        taskStats.put("pending", taskPending);
        taskStats.put("overdue", taskOverdue);
        taskStats.put("total", taskTotal);
        taskStats.put("rate", taskRate);
        taskStats.put("periodicCount", cycleStats.get("total"));
        taskStats.put("periodicRate", cycleStats.get("rate"));
        taskStats.put("oneoffCount", onceStats.get("total"));
        taskStats.put("oneoffRate", onceStats.get("rate"));
        data.put("taskStats", taskStats);

        // ===== 任务详情列表（只显示本周有实例的任务） =====
        List<Map<String, Object>> taskList = new ArrayList<>();
        // 周期性任务详情
        for (SysTaskInstance inst : cycleInstances)
        {
            Long taskId = inst.getTaskId();
            boolean alreadyAdded = taskList.stream().anyMatch(d -> d.get("id").equals(taskId));
            if (!alreadyAdded && taskId != null)
            {
                Map<String, Object> item = new HashMap<>();
                item.put("id", taskId);
                item.put("name", inst.getTaskName());
                item.put("taskType", "周期性");

                long tCompleted = instances.stream().filter(i -> i.getTaskId() != null && i.getTaskId().equals(taskId) && i.getCompleted() != null && i.getCompleted() == 1).count();
                long tTotal = instances.stream().filter(i -> i.getTaskId() != null && i.getTaskId().equals(taskId)).count();
                item.put("completedCount", (int) tCompleted);
                item.put("totalCount", (int) tTotal);
                item.put("rate", tTotal > 0 ? Math.round(tCompleted * 100.0 / tTotal) : 0);
                taskList.add(item);
            }
        }
        // 一次性任务详情
        for (SysTaskInstance inst : onceInstances)
        {
            Long taskId = inst.getTaskId();
            boolean alreadyAdded = taskList.stream().anyMatch(d -> d.get("id").equals(taskId));
            if (!alreadyAdded && taskId != null)
            {
                Map<String, Object> item = new HashMap<>();
                item.put("id", taskId);
                item.put("name", inst.getTaskName());
                item.put("taskType", "一次性");

                long tCompleted = instances.stream().filter(i -> i.getTaskId() != null && i.getTaskId().equals(taskId) && i.getCompleted() != null && i.getCompleted() == 1).count();
                long tTotal = instances.stream().filter(i -> i.getTaskId() != null && i.getTaskId().equals(taskId)).count();
                item.put("completedCount", (int) tCompleted);
                item.put("totalCount", (int) tTotal);
                item.put("rate", tTotal > 0 ? Math.round(tCompleted * 100.0 / tTotal) : 0);
                taskList.add(item);
            }
        }
        data.put("taskList", taskList);

        // ===== 人员完成率排名 =====
        List<Map<String, Object>> personRank = taskInstanceService.getPersonCompletionList(period, startDate, endDate);
        data.put("personRank", personRank);

        // ===== 重要事项 =====
        List<Map<String, Object>> eventList = new ArrayList<>();
        for (SysProject proj : allProjects)
        {
            List<SysProjectMilestone> milestones = milestoneService.selectByProjectId(proj.getId());
            for (SysProjectMilestone m : milestones)
            {
                if (m.getRecordDate() != null)
                {
                    String recordDateStr = formatDate(m.getRecordDate());
                    if (recordDateStr.compareTo(startDate) >= 0 && recordDateStr.compareTo(endDate) <= 0)
                    {
                        Map<String, Object> item = new HashMap<>();
                        item.put("id", m.getId());
                        item.put("date", recordDateStr);
                        // 项目名称：客户名称-项目名称
                        String displayName = (proj.getCustomer() != null && !proj.getCustomer().isEmpty())
                            ? proj.getCustomer() + "-" + proj.getName()
                            : proj.getName();
                        item.put("projectName", displayName);
                        item.put("description", m.getDescription());
                        eventList.add(item);
                    }
                }
            }
        }
        data.put("eventList", eventList);

        return data;
    }

    /**
     * 生成邮件预览HTML
     */
    @Override
    public String generateEmailPreviewHtml(String startDate, String endDate)
    {
        return generateWeeklyHtml(startDate, endDate);
    }
}
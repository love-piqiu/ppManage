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

        // 任务完成率
        int taskRate = taskInstanceService.getTeamCompletionRate(period);
        overview.put("taskRate", taskRate);
        report.put("overview", overview);

        // ===== 项目进度 =====
        List<Map<String, Object>> projectList = new ArrayList<>();

        // 进行中项目
        SysProject projectQuery = new SysProject();
        projectQuery.setStatus("进行中");
        List<SysProject> activeProjects = projectService.selectProjectList(projectQuery);
        for (SysProject p : activeProjects)
        {
            Map<String, Object> item = new HashMap<>();
            item.put("id", p.getId());
            item.put("name", p.getName());
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

        // 已完成项目
        SysProject completedQuery = new SysProject();
        completedQuery.setStatus("已完成");
        List<SysProject> completedProjects = projectService.selectProjectList(completedQuery);
        for (SysProject p : completedProjects)
        {
            Map<String, Object> item = new HashMap<>();
            item.put("id", p.getId());
            item.put("name", p.getName());
            item.put("stage", "验收交付");
            item.put("stageDate", "已完成 " + formatDate(p.getActualEndDate()));
            item.put("costRate", calcCostRate(p));
            item.put("hoursRate", calcHoursRate(p));
            item.put("status", "已完成");
            item.put("change", "验收通过");
            item.put("changeType", "success");
            projectList.add(item);
        }

        // 暂停项目
        SysProject pausedQuery = new SysProject();
        pausedQuery.setStatus("暂停");
        List<SysProject> pausedProjects = projectService.selectProjectList(pausedQuery);
        for (SysProject p : pausedProjects)
        {
            Map<String, Object> item = new HashMap<>();
            item.put("id", p.getId());
            item.put("name", p.getName());
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
        projectSummary.put("costOver", (int) projectList.stream().filter(p -> (int) p.get("costRate") > 100).count());
        projectSummary.put("hoursOver", (int) projectList.stream().filter(p -> (int) p.get("hoursRate") > 100).count());
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
        List<SysTaskInstance> instances = taskInstanceService.selectByPeriod(period);

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

        // 任务类型统计
        SysTask cycleTaskQuery = new SysTask();
        cycleTaskQuery.setType("周期性");
        cycleTaskQuery.setStatus("启用");
        List<SysTask> cycleTasks = taskService.selectTaskList(cycleTaskQuery);
        int personCount = personService.countActivePerson();
        taskStats.put("cycleTotal", cycleTasks.size() * personCount);
        taskStats.put("cycleRate", taskInstanceService.getTeamCompletionRate(period));
        taskStats.put("onceTotal", 20);
        taskStats.put("onceRate", 50);
        report.put("taskStats", taskStats);

        // 任务详情列表
        List<Map<String, Object>> taskDetails = new ArrayList<>();
        for (SysTask t : cycleTasks)
        {
            Map<String, Object> item = new HashMap<>();
            item.put("id", t.getId());
            item.put("name", t.getName());
            item.put("cycle", t.getCycle() != null ? t.getCycle() : "每日");

            String deadline = "18:00";
            if ("每周".equals(t.getCycle()))
            {
                deadline = "周五 " + (t.getDeadlineTime() != null ? t.getDeadlineTime() : "17:00");
            }
            else if ("每月".equals(t.getCycle()))
            {
                deadline = (t.getDeadlineDay() != null ? t.getDeadlineDay() : 5) + "日 12:00";
            }
            else if (t.getDeadlineTime() != null)
            {
                deadline = t.getDeadlineTime();
            }
            item.put("deadline", deadline);

            // 统计完成人数
            long taskCompleted = instances.stream()
                .filter(i -> i.getTaskId() != null && i.getTaskId().equals(t.getId()) && i.getCompleted() != null && i.getCompleted() == 1)
                .count();
            long taskTotal = instances.stream()
                .filter(i -> i.getTaskId() != null && i.getTaskId().equals(t.getId()))
                .count();
            int count = (int) taskTotal > 0 ? (int) taskTotal : personCount;
            item.put("completedCount", (int) taskCompleted);
            item.put("totalCount", count);
            item.put("rate", count > 0 ? Math.round(taskCompleted * 100.0 / count) : 0);
            taskDetails.add(item);
        }
        report.put("taskDetails", taskDetails);

        // 人员完成率排名
        List<Map<String, Object>> personRanking = taskInstanceService.getPersonCompletionList(period);
        report.put("personRanking", personRanking);

        // ===== 本周重要事项 =====
        List<Map<String, Object>> milestoneList = new ArrayList<>();
        // 获取所有项目的重要事项
        List<SysProject> allProjects = projectService.selectProjectList(new SysProject());
        for (SysProject proj : allProjects)
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
                        item.put("projectName", m.getProjectName());
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
     * 生成周报HTML
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
        html.append("body { font-family: 'Plus Jakarta Sans', -apple-system, sans-serif; line-height: 1.6; color: #374151; background: #F3F4F6; }");
        html.append(".container { max-width: 680px; margin: 0 auto; padding: 20px; }");
        html.append(".header { text-align: center; padding: 24px 0; border-bottom: 2px solid #2563EB; margin-bottom: 24px; }");
        html.append(".header h1 { color: #2563EB; font-size: 24px; margin: 0; }");
        html.append(".header .date { color: #6B7280; font-size: 14px; margin-top: 8px; }");
        html.append(".overview { background: linear-gradient(135deg, #EFF6FF 0%, #DBEAFE 100%); border-radius: 12px; padding: 20px; margin-bottom: 24px; }");
        html.append(".overview-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 16px; text-align: center; }");
        html.append(".stat-value { font-size: 28px; font-weight: 700; }");
        html.append(".stat-label { font-size: 12px; color: #6B7280; }");
        html.append(".section { margin-bottom: 24px; }");
        html.append(".section-title { font-size: 16px; font-weight: 600; color: #374151; margin-bottom: 12px; padding-bottom: 8px; border-bottom: 2px solid #2563EB; display: inline-block; }");
        html.append("table { width: 100%; border-collapse: collapse; font-size: 13px; }");
        html.append("th, td { padding: 10px 8px; border-bottom: 1px solid #E5E7EB; text-align: left; }");
        html.append("th { background: #F3F4F6; font-weight: 600; }");
        html.append(".tag { display: inline-block; padding: 2px 6px; border-radius: 4px; font-size: 11px; }");
        html.append(".tag-blue { background: #DBEAFE; color: #1D4ED8; }");
        html.append(".tag-green { background: #D1FAE5; color: #059669; }");
        html.append(".tag-orange { background: #FEF3C7; color: #B45309; }");
        html.append(".tag-red { background: #FEE2E2; color: #DC2626; }");
        html.append(".footer { text-align: center; padding: 16px 0; border-top: 1px solid #E5E7EB; color: #9CA3AF; font-size: 12px; }");
        html.append("</style>");
        html.append("</head><body>");
        html.append("<div class='container'>");

        // 头部
        html.append("<div class='header'>");
        html.append("<h1>项目周报</h1>");
        html.append("<div class='date'>").append(startDate).append(" ~ ").append(endDate).append("</div>");
        html.append("</div>");

        // 概览
        @SuppressWarnings("unchecked")
        Map<String, Object> overview = (Map<String, Object>) report.get("overview");
        html.append("<div class='overview'>");
        html.append("<div class='overview-grid'>");
        html.append("<div><div class='stat-value'>").append(overview.get("activeProjects")).append("</div><div class='stat-label'>进行中项目</div></div>");
        html.append("<div><div class='stat-value' style='color:#EF4444'>").append(overview.get("newIssues")).append("</div><div class='stat-label'>新增问题</div></div>");
        html.append("<div><div class='stat-value' style='color:#10B981'>").append(overview.get("resolvedIssues")).append("</div><div class='stat-label'>已解决问题</div></div>");
        html.append("<div><div class='stat-value' style='color:#F59E0B'>").append(overview.get("taskRate")).append("%</div><div class='stat-label'>任务完成率</div></div>");
        html.append("</div></div>");

        // 项目进度
        html.append("<div class='section'>");
        html.append("<div class='section-title'>项目进度</div>");
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> projects = (List<Map<String, Object>>) report.get("projects");
        html.append("<table>");
        html.append("<tr><th>项目名称</th><th>当前阶段</th><th>成本</th><th>状态</th></tr>");
        for (Map<String, Object> p : projects)
        {
            html.append("<tr>");
            html.append("<td>").append(p.get("name")).append("</td>");
            html.append("<td><strong>").append(p.get("stage")).append("</strong></td>");
            html.append("<td>").append(p.get("costRate")).append("%</td>");
            String status = (String) p.get("status");
            String tagClass = "进行中".equals(status) ? "tag-blue" : "已完成".equals(status) ? "tag-green" : "tag-orange";
            html.append("<td><span class='tag ").append(tagClass).append("'>").append(status).append("</span></td>");
            html.append("</tr>");
        }
        html.append("</table>");
        html.append("</div>");

        // 问题跟踪
        html.append("<div class='section'>");
        html.append("<div class='section-title'>问题跟踪</div>");
        @SuppressWarnings("unchecked")
        Map<String, Object> issueSummary = (Map<String, Object>) report.get("issueSummary");
        html.append("<p style='font-size:13px;color:#4B5563;margin-bottom:12px;'>");
        html.append("新增 <strong style='color:#EF4444'>").append(issueSummary.get("new")).append("</strong> 个，");
        html.append("解决 <strong style='color:#10B981'>").append(issueSummary.get("resolved")).append("</strong> 个，");
        html.append("待处理 <strong>").append(issueSummary.get("pending")).append("</strong> 个");
        html.append("</p>");
        html.append("</div>");

        // 风险管理
        html.append("<div class='section'>");
        html.append("<div class='section-title'>风险管理</div>");
        @SuppressWarnings("unchecked")
        Map<String, Object> riskSummary = (Map<String, Object>) report.get("riskSummary");
        html.append("<p style='font-size:13px;color:#4B5563;margin-bottom:12px;'>");
        html.append("跟踪风险 <strong>").append(riskSummary.get("total")).append("</strong> 个，");
        html.append("高风险 <strong style='color:#EF4444'>").append(riskSummary.get("high")).append("</strong> 个");
        html.append("</p>");
        html.append("</div>");

        // 任务完成情况
        html.append("<div class='section'>");
        html.append("<div class='section-title'>任务完成情况</div>");
        @SuppressWarnings("unchecked")
        Map<String, Object> taskStats = (Map<String, Object>) report.get("taskStats");
        html.append("<div style='display:grid;grid-template-columns:repeat(5,1fr);gap:8px;margin-bottom:16px;'>");
        html.append("<div style='background:#D1FAE5;border-radius:8px;padding:12px;text-align:center;'>");
        html.append("<div style='font-size:24px;font-weight:700;color:#10B981'>").append(taskStats.get("completed")).append("</div>");
        html.append("<div style='font-size:11px;color:#6B7280'>已完成</div></div>");
        html.append("<div style='background:#FEF3C7;border-radius:8px;padding:12px;text-align:center;'>");
        html.append("<div style='font-size:24px;font-weight:700;color:#F59E0B'>").append(taskStats.get("pending")).append("</div>");
        html.append("<div style='font-size:11px;color:#6B7280'>待完成</div></div>");
        html.append("<div style='background:#FEE2E2;border-radius:8px;padding:12px;text-align:center;'>");
        html.append("<div style='font-size:24px;font-weight:700;color:#EF4444'>").append(taskStats.get("overdue")).append("</div>");
        html.append("<div style='font-size:11px;color:#6B7280'>已超期</div></div>");
        html.append("<div style='background:#F3F4F6;border-radius:8px;padding:12px;text-align:center;'>");
        html.append("<div style='font-size:24px;font-weight:700;color:#374151'>").append(taskStats.get("total")).append("</div>");
        html.append("<div style='font-size:11px;color:#6B7280'>总任务</div></div>");
        html.append("<div style='background:#DBEAFE;border-radius:8px;padding:12px;text-align:center;'>");
        html.append("<div style='font-size:24px;font-weight:700;color:#2563EB'>").append(taskStats.get("rate")).append("%</div>");
        html.append("<div style='font-size:11px;color:#6B7280'>完成率</div></div>");
        html.append("</div>");
        html.append("</div>");

        // 底部
        html.append("<div class='footer'>此邮件由项目管理系统自动发送 | 请勿直接回复</div>");
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

        // 统计项目
        SysProject projectQuery = new SysProject();
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
        overview.put("taskRate", taskInstanceService.getTeamCompletionRate(period));
        data.put("overview", overview);

        // ===== 项目进度列表 =====
        List<Map<String, Object>> projectProgress = new ArrayList<>();
        for (SysProject p : allProjects)
        {
            Map<String, Object> item = new HashMap<>();
            item.put("id", p.getId());
            item.put("name", p.getName());
            item.put("stage", p.getStage() != null ? p.getStage() : "需求确认");
            item.put("costUsage", calcCostRate(p));
            item.put("hourUsage", calcHoursRate(p));
            item.put("status", p.getStatus());

            // 阶段状态
            String stageStatus = "pending";
            String stageDateInfo = "";
            if ("已完成".equals(p.getStatus()))
            {
                stageStatus = "completed";
                stageDateInfo = "已完成 " + formatDate(p.getActualEndDate());
            }
            else if ("暂停".equals(p.getStatus()))
            {
                stageDateInfo = "已暂停";
            }
            else if (p.getEndDate() != null)
            {
                long diff = p.getEndDate().getTime() - new Date().getTime();
                int days = (int) (diff / (24 * 60 * 60 * 1000));
                if (days <= 0)
                {
                    stageStatus = "danger";
                    stageDateInfo = "已到期: " + formatDate(p.getEndDate());
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
            item.put("weekChange", "");
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
            item.put("projectName", i.getProjectName());
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
            item.put("projectName", r.getProjectName());
            item.put("level", r.getLevel());
            item.put("status", r.getStatus());
            item.put("measure", r.getMeasure());
            riskList.add(item);
        }
        data.put("riskList", riskList);

        // ===== 任务统计 =====
        List<SysTaskInstance> instances = taskInstanceService.selectByPeriod(period);
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

        Map<String, Object> taskStats = new HashMap<>();
        taskStats.put("completed", taskCompleted);
        taskStats.put("pending", taskPending);
        taskStats.put("overdue", taskOverdue);
        taskStats.put("total", taskTotal);
        taskStats.put("rate", taskRate);
        taskStats.put("periodicCount", 60);
        taskStats.put("periodicRate", 82);
        taskStats.put("oneoffCount", 20);
        taskStats.put("oneoffRate", 50);
        data.put("taskStats", taskStats);

        // ===== 任务详情列表 =====
        List<Map<String, Object>> taskList = new ArrayList<>();
        SysTask taskQuery = new SysTask();
        taskQuery.setStatus("启用");
        List<SysTask> tasks = taskService.selectTaskList(taskQuery);
        int personCount = personService.countActivePerson();
        for (SysTask t : tasks)
        {
            Map<String, Object> item = new HashMap<>();
            item.put("id", t.getId());
            item.put("name", t.getName());
            item.put("taskType", t.getCycle() != null ? t.getCycle() : "每日");

            String deadline = "18:00";
            if ("每周".equals(t.getCycle()))
                deadline = "周五 " + (t.getDeadlineTime() != null ? t.getDeadlineTime() : "17:00");
            else if ("每月".equals(t.getCycle()))
                deadline = (t.getDeadlineDay() != null ? t.getDeadlineDay() : 5) + "日 12:00";
            else if (t.getDeadlineTime() != null)
                deadline = t.getDeadlineTime();
            item.put("deadline", deadline);

            long tCompleted = instances.stream().filter(i -> i.getTaskId() != null && i.getTaskId().equals(t.getId()) && i.getCompleted() != null && i.getCompleted() == 1).count();
            long tTotal = instances.stream().filter(i -> i.getTaskId() != null && i.getTaskId().equals(t.getId())).count();
            int count = (int) tTotal > 0 ? (int) tTotal : personCount;
            item.put("completedCount", (int) tCompleted);
            item.put("totalCount", count);
            item.put("rate", count > 0 ? Math.round(tCompleted * 100.0 / count) : 0);
            taskList.add(item);
        }
        data.put("taskList", taskList);

        // ===== 人员完成率排名 =====
        List<Map<String, Object>> personRank = taskInstanceService.getPersonCompletionList(period);
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
                        item.put("projectName", m.getProjectName());
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
package com.ruoyi.system.service.impl;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.domain.SysIssue;
import com.ruoyi.system.domain.SysProject;
import com.ruoyi.system.domain.SysRisk;
import com.ruoyi.system.service.ISysIssueService;
import com.ruoyi.system.service.ISysProjectService;
import com.ruoyi.system.service.ISysRiskService;
import com.ruoyi.system.service.ISysPersonService;
import com.ruoyi.system.service.ISysTaskInstanceService;
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
    private ISysTaskInstanceService taskInstanceService;

    /**
     * 生成周报内容
     *
     * @param startDate 开始日期(yyyy-MM-dd)
     * @param endDate 结束日期(yyyy-MM-dd)
     * @return 周报内容
     */
    @Override
    public Map<String, Object> generateWeeklyReport(String startDate, String endDate)
    {
        Map<String, Object> report = new HashMap<>();

        // 项目统计
        report.put("activeProjects", projectService.countActiveProject());
        report.put("completedProjects", projectService.countCompletedProject());

        // 问题统计
        report.put("unresolvedIssues", issueService.countUnresolvedIssue());
        report.put("highSeverityIssues", issueService.countHighSeverityIssue());

        // 风险统计
        report.put("activeRisks", riskService.countActiveRisk());
        report.put("highRisks", riskService.countHighLevelRisk());

        // 人员统计
        report.put("activePersons", personService.countActivePerson());

        // 进行中的项目列表
        SysProject projectQuery = new SysProject();
        projectQuery.setStatus("进行中");
        report.put("projectList", projectService.selectProjectList(projectQuery));

        // 未解决的高严重程度问题
        SysIssue issueQuery = new SysIssue();
        issueQuery.setSeverity("高");
        issueQuery.setStatus("待处理");
        report.put("urgentIssues", issueService.selectIssueList(issueQuery));

        // 未消除的高风险
        SysRisk riskQuery = new SysRisk();
        riskQuery.setLevel("高");
        riskQuery.setStatus("潜在");
        report.put("highRisksList", riskService.selectRiskList(riskQuery));

        report.put("startDate", startDate);
        report.put("endDate", endDate);

        return report;
    }

    /**
     * 生成周报HTML
     *
     * @param startDate 开始日期(yyyy-MM-dd)
     * @param endDate 结束日期(yyyy-MM-dd)
     * @return HTML内容
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
        html.append("body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; line-height: 1.6; color: #374151; }");
        html.append(".container { max-width: 800px; margin: 0 auto; padding: 20px; }");
        html.append("h1 { color: #2563EB; border-bottom: 2px solid #2563EB; padding-bottom: 10px; }");
        html.append("h2 { color: #374151; margin-top: 30px; }");
        html.append(".summary { display: grid; grid-template-columns: repeat(4, 1fr); gap: 15px; margin: 20px 0; }");
        html.append(".stat-card { background: #F9FAFB; border: 1px solid #E5E7EB; border-radius: 8px; padding: 15px; text-align: center; }");
        html.append(".stat-number { font-size: 24px; font-weight: bold; color: #2563EB; }");
        html.append(".stat-label { color: #6B7280; font-size: 14px; }");
        html.append("table { width: 100%; border-collapse: collapse; margin: 15px 0; }");
        html.append("th, td { border: 1px solid #E5E7EB; padding: 10px; text-align: left; }");
        html.append("th { background: #F9FAFB; font-weight: 600; }");
        html.append(".tag { display: inline-block; padding: 2px 8px; border-radius: 4px; font-size: 12px; }");
        html.append(".tag-red { background: #FEE2E2; color: #EF4444; }");
        html.append(".tag-orange { background: #FEF3C7; color: #F59E0B; }");
        html.append(".tag-green { background: #D1FAE5; color: #10B981; }");
        html.append(".tag-blue { background: #DBEAFE; color: #2563EB; }");
        html.append("</style>");
        html.append("</head><body>");
        html.append("<div class='container'>");

        // 标题
        html.append("<h1>项目周报</h1>");
        html.append("<p>报告周期：").append(startDate).append(" 至 ").append(endDate).append("</p>");

        // 概览统计
        html.append("<h2>概览</h2>");
        html.append("<div class='summary'>");
        html.append("<div class='stat-card'><div class='stat-number'>").append(report.get("activeProjects")).append("</div><div class='stat-label'>进行中项目</div></div>");
        html.append("<div class='stat-card'><div class='stat-number'>").append(report.get("unresolvedIssues")).append("</div><div class='stat-label'>未解决问题</div></div>");
        html.append("<div class='stat-card'><div class='stat-number'>").append(report.get("activeRisks")).append("</div><div class='stat-label'>未消除风险</div></div>");
        html.append("<div class='stat-card'><div class='stat-number'>").append(report.get("activePersons")).append("</div><div class='stat-label'>在职人员</div></div>");
        html.append("</div>");

        // 项目列表
        html.append("<h2>进行中的项目</h2>");
        @SuppressWarnings("unchecked")
        List<SysProject> projects = (List<SysProject>) report.get("projectList");
        if (projects != null && !projects.isEmpty())
        {
            html.append("<table>");
            html.append("<tr><th>项目名称</th><th>客户</th><th>项目经理</th><th>进度</th><th>状态</th></tr>");
            for (SysProject p : projects)
            {
                html.append("<tr>");
                html.append("<td>").append(p.getName() != null ? p.getName() : "").append("</td>");
                html.append("<td>").append(p.getCustomer() != null ? p.getCustomer() : "").append("</td>");
                html.append("<td>").append(p.getPmName() != null ? p.getPmName() : "").append("</td>");
                html.append("<td>").append(p.getProgress() != null ? p.getProgress() + "%" : "0%").append("</td>");
                html.append("<td><span class='tag tag-blue'>").append(p.getStatus() != null ? p.getStatus() : "").append("</span></td>");
                html.append("</tr>");
            }
            html.append("</table>");
        }
        else
        {
            html.append("<p>暂无进行中的项目</p>");
        }

        // 紧急问题
        html.append("<h2>紧急问题</h2>");
        @SuppressWarnings("unchecked")
        List<SysIssue> issues = (List<SysIssue>) report.get("urgentIssues");
        if (issues != null && !issues.isEmpty())
        {
            html.append("<table>");
            html.append("<tr><th>项目</th><th>问题描述</th><th>严重程度</th><th>状态</th><th>负责人</th></tr>");
            for (SysIssue i : issues)
            {
                html.append("<tr>");
                html.append("<td>").append(i.getProjectName() != null ? i.getProjectName() : "").append("</td>");
                html.append("<td>").append(i.getDescription() != null ? i.getDescription() : "").append("</td>");
                html.append("<td><span class='tag tag-red'>").append(i.getSeverity() != null ? i.getSeverity() : "").append("</span></td>");
                html.append("<td><span class='tag tag-orange'>").append(i.getStatus() != null ? i.getStatus() : "").append("</span></td>");
                html.append("<td>").append(i.getOwnerName() != null ? i.getOwnerName() : "").append("</td>");
                html.append("</tr>");
            }
            html.append("</table>");
        }
        else
        {
            html.append("<p>暂无紧急问题</p>");
        }

        // 高风险
        html.append("<h2>高风险</h2>");
        @SuppressWarnings("unchecked")
        List<SysRisk> risks = (List<SysRisk>) report.get("highRisksList");
        if (risks != null && !risks.isEmpty())
        {
            html.append("<table>");
            html.append("<tr><th>项目</th><th>风险描述</th><th>等级</th><th>状态</th><th>应对措施</th></tr>");
            for (SysRisk r : risks)
            {
                html.append("<tr>");
                html.append("<td>").append(r.getProjectName() != null ? r.getProjectName() : "").append("</td>");
                html.append("<td>").append(r.getDescription() != null ? r.getDescription() : "").append("</td>");
                html.append("<td><span class='tag tag-red'>").append(r.getLevel() != null ? r.getLevel() : "").append("</span></td>");
                html.append("<td><span class='tag tag-orange'>").append(r.getStatus() != null ? r.getStatus() : "").append("</span></td>");
                html.append("<td>").append(r.getMeasure() != null ? r.getMeasure() : "").append("</td>");
                html.append("</tr>");
            }
            html.append("</table>");
        }
        else
        {
            html.append("<p>暂无高风险</p>");
        }

        html.append("</div>");
        html.append("</body></html>");

        return html.toString();
    }
}
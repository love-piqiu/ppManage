package com.ruoyi.web.controller.system;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.service.IDashboardService;

/**
 * 首页看板Controller
 */
@RestController
@RequestMapping("/dashboard")
public class DashboardController extends BaseController {

    @Autowired
    private IDashboardService dashboardService;

    /**
     * 获取首页看板所有数据
     */
    @GetMapping("/data")
    public AjaxResult getDashboardData() {
        Map<String, Object> data = new HashMap<>();

        // 汇总数据
        data.put("summary", dashboardService.getSummary());

        // 预警数据
        data.put("alerts", dashboardService.getAlerts());

        // 任务统计
        data.put("taskStats", dashboardService.getTaskStats());

        // 问题统计
        data.put("issueStats", dashboardService.getIssueStats());

        // 风险统计
        data.put("riskStats", dashboardService.getRiskStats());

        // 任务完成图表数据
        data.put("taskChartData", dashboardService.getTaskChartData());

        // 最近问题
        data.put("recentIssues", dashboardService.getRecentIssues(5));

        // 最近风险
        data.put("recentRisks", dashboardService.getRecentRisks(5));

        return AjaxResult.success(data);
    }

    /**
     * 获取预警信息
     */
    @GetMapping("/alerts")
    public AjaxResult getAlerts() {
        return AjaxResult.success(dashboardService.getAlerts());
    }

    /**
     * 获取汇总统计
     */
    @GetMapping("/summary")
    public AjaxResult getSummary() {
        return AjaxResult.success(dashboardService.getSummary());
    }

    /**
     * 获取紧急问题
     */
    @GetMapping("/urgentIssues")
    public AjaxResult getUrgentIssues() {
        return AjaxResult.success(dashboardService.getRecentIssues(10));
    }

    /**
     * 获取高风险
     */
    @GetMapping("/highRisks")
    public AjaxResult getHighRisks() {
        return AjaxResult.success(dashboardService.getRecentRisks(10));
    }

    /**
     * 获取项目进度
     */
    @GetMapping("/projectProgress")
    public AjaxResult getProjectProgress() {
        return AjaxResult.success(dashboardService.getProjectProgress());
    }
}
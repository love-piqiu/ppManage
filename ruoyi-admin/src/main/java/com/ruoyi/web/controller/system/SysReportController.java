package com.ruoyi.web.controller.system;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.service.ISysReportService;
import com.ruoyi.system.service.ISysEmailService;

/**
 * 周报信息操作处理
 *
 * @author ppmanage
 */
@RestController
@RequestMapping("/report")
public class SysReportController extends BaseController
{
    @Autowired
    private ISysReportService reportService;

    @Autowired
    private ISysEmailService emailService;

    /**
     * 生成周报内容
     */
    @PreAuthorize("@ss.hasPermi('report:weekly:view')")
    @GetMapping("/weekly")
    public AjaxResult generateWeekly(@RequestParam(required = false) String startDate,
                                      @RequestParam(required = false) String endDate)
    {
        if (startDate == null || endDate == null)
        {
            // 默认本周
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
            startDate = sdf.format(cal.getTime());
            cal.add(Calendar.DAY_OF_WEEK, 6);
            endDate = sdf.format(cal.getTime());
        }

        Map<String, Object> report = reportService.generateWeeklyReport(startDate, endDate);
        return success(report);
    }

    /**
     * 生成周报HTML
     */
    @PreAuthorize("@ss.hasPermi('report:weekly:view')")
    @GetMapping("/weekly/html")
    public AjaxResult generateWeeklyHtml(@RequestParam(required = false) String startDate,
                                          @RequestParam(required = false) String endDate)
    {
        if (startDate == null || endDate == null)
        {
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
            startDate = sdf.format(cal.getTime());
            cal.add(Calendar.DAY_OF_WEEK, 6);
            endDate = sdf.format(cal.getTime());
        }

        String html = reportService.generateWeeklyHtml(startDate, endDate);
        return success(html);
    }

    /**
     * 发送周报邮件
     */
    @PreAuthorize("@ss.hasPermi('report:weekly:send')")
    @Log(title = "周报管理", businessType = BusinessType.OTHER)
    @PostMapping("/weekly/send")
    public AjaxResult sendWeeklyReport(@RequestBody Map<String, String> params)
    {
        String startDate = params.get("startDate");
        String endDate = params.get("endDate");

        if (startDate == null || endDate == null)
        {
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
            startDate = sdf.format(cal.getTime());
            cal.add(Calendar.DAY_OF_WEEK, 6);
            endDate = sdf.format(cal.getTime());
        }

        String html = reportService.generateWeeklyHtml(startDate, endDate);
        String subject = "项目周报 - " + startDate + " 至 " + endDate;

        boolean result = emailService.sendWeeklyReport(subject, html);
        return result ? success("邮件发送成功") : error("邮件发送失败");
    }
}
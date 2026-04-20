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
import com.ruoyi.system.domain.SysEmailConfig;
import com.ruoyi.system.service.ISysEmailConfigService;

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

    @Autowired
    private ISysEmailConfigService emailConfigService;

    /**
     * 获取周报完整数据（新版接口）
     */
    @PreAuthorize("@ss.hasPermi('report:weekly:view')")
    @GetMapping("/weekly/data")
    public AjaxResult getWeeklyReportData(@RequestParam(required = false) String startDate,
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

        Map<String, Object> data = reportService.getWeeklyReportData(startDate, endDate);
        return success(data);
    }

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

        String error = emailService.sendWeeklyReportWithDetail(subject, html);
        if (error == null)
        {
            return success("邮件发送成功");
        }
        else
        {
            return error("邮件发送失败: " + error);
        }
    }

    /**
     * 获取邮件配置
     */
    @PreAuthorize("@ss.hasPermi('report:weekly:view')")
    @GetMapping("/email/config")
    public AjaxResult getEmailConfig()
    {
        SysEmailConfig config = emailConfigService.getConfig();
        // 隐藏真实密码，返回占位符表示密码已配置
        if (config != null)
        {
            if (config.getPassword() != null && !config.getPassword().isEmpty())
            {
                config.setPassword("******"); // 占位符表示密码已存在
            }
            else
            {
                config.setPassword(null);
            }
        }
        return success(config);
    }

    /**
     * 保存邮件配置
     */
    @PreAuthorize("@ss.hasPermi('report:weekly:edit')")
    @Log(title = "邮件配置", businessType = BusinessType.UPDATE)
    @PostMapping("/email/config")
    public AjaxResult saveEmailConfig(@RequestBody SysEmailConfig config)
    {
        config.setUpdateBy(getUsername());
        return toAjax(emailConfigService.updateConfig(config));
    }

    /**
     * 发送测试邮件
     */
    @PreAuthorize("@ss.hasPermi('report:weekly:edit')")
    @Log(title = "邮件配置", businessType = BusinessType.OTHER)
    @PostMapping("/email/test")
    public AjaxResult testEmail()
    {
        String error = emailService.sendTestEmailWithDetail();
        if (error == null)
        {
            return success("测试邮件发送成功，请检查收件箱");
        }
        else
        {
            return error(error);
        }
    }

    /**
     * 获取邮件预览HTML
     */
    @PreAuthorize("@ss.hasPermi('report:weekly:view')")
    @GetMapping("/email/preview")
    public AjaxResult getEmailPreview()
    {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        String startDate = sdf.format(cal.getTime());
        cal.add(Calendar.DAY_OF_WEEK, 6);
        String endDate = sdf.format(cal.getTime());

        String html = reportService.generateEmailPreviewHtml(startDate, endDate);
        return success(html);
    }
}
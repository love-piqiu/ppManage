package com.ruoyi.quartz.task;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.ruoyi.system.domain.SysEmailConfig;
import com.ruoyi.system.service.ISysEmailConfigService;
import com.ruoyi.system.service.ISysEmailService;
import com.ruoyi.system.service.ISysReportService;

/**
 * 邮件自动发送定时任务
 *
 * @author ppmanage
 */
@Component("emailTask")
public class EmailTask
{
    private static final Logger log = LoggerFactory.getLogger(EmailTask.class);

    @Autowired
    private ISysEmailConfigService emailConfigService;

    @Autowired
    private ISysEmailService emailService;

    @Autowired
    private ISysReportService reportService;

    /**
     * 检查并执行邮件自动发送
     * 每分钟执行一次，检查是否需要发送邮件
     */
    public void checkAndSendEmail()
    {
        log.info("开始检查邮件自动发送任务...");

        try
        {
            // 获取邮件配置
            SysEmailConfig config = emailConfigService.getConfig();
            if (config == null)
            {
                log.info("邮件配置不存在，跳过发送");
                return;
            }

            // 检查是否启用自动发送
            if (config.getEnabled() == null || config.getEnabled() != 1)
            {
                log.info("邮件自动发送未启用，跳过发送");
                return;
            }

            // 检查收件人是否配置
            if (config.getRecipientEmail() == null || config.getRecipientEmail().isEmpty())
            {
                log.info("收件人地址未配置，跳过发送");
                return;
            }

            // 获取当前时间
            Calendar now = Calendar.getInstance();
            int currentDayOfWeek = now.get(Calendar.DAY_OF_WEEK); // 1=周日, 2=周一, ..., 6=周五, 7=周六
            int currentHour = now.get(Calendar.HOUR_OF_DAY);
            int currentMinute = now.get(Calendar.MINUTE);

            // 将周日=1 转换为 周一=1, 周日=7 的格式
            int adjustedDayOfWeek = currentDayOfWeek == 1 ? 7 : currentDayOfWeek - 1; // 1=周一, ..., 5=周五, 7=周日

            // 解析配置的发送日期 (周五=5, 周四=4, 周三=3)
            String sendDayStr = config.getSendDay();
            int targetDayOfWeek = parseDayOfWeek(sendDayStr);

            // 解析配置的发送时间 (HH:mm 格式)
            String sendTimeStr = config.getSendTime();
            String[] timeParts = sendTimeStr != null ? sendTimeStr.split(":") : new String[]{"17", "00"};
            int targetHour = Integer.parseInt(timeParts[0].trim());
            int targetMinute = timeParts.length > 1 ? Integer.parseInt(timeParts[1].trim()) : 0;

            log.info("当前时间: 周{} {}:{}, 配置时间: 周{} {}:{}",
                     adjustedDayOfWeek, currentHour, currentMinute,
                     targetDayOfWeek, targetHour, targetMinute);

            // 判断是否匹配发送条件（精确匹配到分钟）
            if (adjustedDayOfWeek == targetDayOfWeek && currentHour == targetHour && currentMinute == targetMinute)
            {
                log.info("时间匹配，开始发送邮件...");

                // 获取本周日期范围
                Calendar cal = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                String startDate = sdf.format(cal.getTime());
                cal.add(Calendar.DAY_OF_WEEK, 6);
                String endDate = sdf.format(cal.getTime());

                // 生成周报HTML
                String htmlContent = reportService.generateWeeklyHtml(startDate, endDate);
                String subject = "项目周报 - " + startDate + " 至 " + endDate;

                // 发送邮件
                String error = emailService.sendWeeklyReportWithDetail(subject, htmlContent);
                if (error == null)
                {
                    log.info("邮件发送成功");
                }
                else
                {
                    log.error("邮件发送失败: {}", error);
                }
            }
            else
            {
                log.info("时间不匹配，跳过发送");
            }
        }
        catch (Exception e)
        {
            log.error("邮件自动发送任务执行异常: {}", e.getMessage(), e);
        }
    }

    /**
     * 解析发送日期字符串
     * 支持格式: "周日", "周一", "周二", "周三", "周四", "周五", "周六" 或 "1", "2", "3", "4", "5", "6", "7"
     * 返回值: 1=周一, 2=周二, 3=周三, 4=周四, 5=周五, 6=周六, 7=周日
     */
    private int parseDayOfWeek(String dayStr)
    {
        if (dayStr == null || dayStr.isEmpty())
        {
            return 5; // 默认周五
        }

        // 中文格式 - 周一=1 到 周日=7
        if ("周一".equals(dayStr) || "星期一".equals(dayStr))
        {
            return 1;
        }
        if ("周二".equals(dayStr) || "星期二".equals(dayStr))
        {
            return 2;
        }
        if ("周三".equals(dayStr) || "星期三".equals(dayStr))
        {
            return 3;
        }
        if ("周四".equals(dayStr) || "星期四".equals(dayStr))
        {
            return 4;
        }
        if ("周五".equals(dayStr) || "星期五".equals(dayStr))
        {
            return 5;
        }
        if ("周六".equals(dayStr) || "星期六".equals(dayStr))
        {
            return 6;
        }
        if ("周日".equals(dayStr) || "星期日".equals(dayStr) || "星期天".equals(dayStr))
        {
            return 7;
        }

        // 数字格式
        try
        {
            return Integer.parseInt(dayStr.trim());
        }
        catch (NumberFormatException e)
        {
            return 5; // 默认周五
        }
    }
}
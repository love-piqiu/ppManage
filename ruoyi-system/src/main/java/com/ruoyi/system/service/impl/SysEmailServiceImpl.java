package com.ruoyi.system.service.impl;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Properties;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import com.ruoyi.system.domain.SysEmailConfig;
import com.ruoyi.system.service.ISysEmailConfigService;
import com.ruoyi.system.service.ISysEmailService;
import com.ruoyi.system.service.ISysReportService;

/**
 * 邥件发送 服务层处理
 *
 * @author ppmanage
 */
@Service
public class SysEmailServiceImpl implements ISysEmailService
{
    private static final Logger log = LoggerFactory.getLogger(SysEmailServiceImpl.class);

    @Autowired
    private ISysEmailConfigService configService;

    @Autowired
    private ISysReportService reportService;

    /**
     * 发送周报邮件
     *
     * @param subject 邥件主题
     * @param htmlContent HTML内容
     * @return 是否成功
     */
    @Override
    public boolean sendWeeklyReport(String subject, String htmlContent)
    {
        String error = sendWeeklyReportWithDetail(subject, htmlContent);
        return error == null;
    }

    /**
     * 发送周报邮件（返回详细错误信息）
     *
     * @param subject 邥件主题
     * @param htmlContent HTML内容
     * @return 成功返回null，失败返回错误信息
     */
    @Override
    public String sendWeeklyReportWithDetail(String subject, String htmlContent)
    {
        return sendWithDetail(subject, htmlContent);
    }

    /**
     * 发送测试邮件
     *
     * @return 是否成功
     */
    @Override
    public boolean sendTestEmail()
    {
        String error = sendTestEmailWithDetail();
        return error == null;
    }

    /**
     * 发送测试邮件（返回详细错误信息）
     * 测试邮件也使用周报模板发送本周周报预览
     *
     * @return 成功返回null，失败返回错误信息
     */
    @Override
    public String sendTestEmailWithDetail()
    {
        // 获取本周日期范围
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        String startDate = sdf.format(cal.getTime());
        cal.add(Calendar.DAY_OF_WEEK, 6);
        String endDate = sdf.format(cal.getTime());

        // 使用周报模板生成内容
        String htmlContent = reportService.generateWeeklyHtml(startDate, endDate);
        String subject = "项目周报（测试） - " + startDate + " ~ " + endDate;

        return sendWithDetail(subject, htmlContent);
    }

    /**
     * 发送邮件（返回详细错误信息）
     *
     * @param subject 邥件主题
     * @param htmlContent HTML内容
     * @return 成功返回null，失败返回错误信息
     */
    private String sendWithDetail(String subject, String htmlContent)
    {
        SysEmailConfig config = configService.getConfig();
        if (config == null)
        {
            return "邮件配置不存在，请先配置邮件参数";
        }
        if (config.getEnabled() == null || config.getEnabled() != 1)
        {
            return "邮件配置未启用，请开启邮件发送功能";
        }
        if (config.getHost() == null || config.getHost().isEmpty())
        {
            return "SMTP服务器地址为空";
        }
        if (config.getUsername() == null || config.getUsername().isEmpty())
        {
            return "发件人账号为空";
        }
        if (config.getPassword() == null || config.getPassword().isEmpty())
        {
            return "授权码/密码为空，请重新输入授权码";
        }
        if (config.getRecipientEmail() == null || config.getRecipientEmail().isEmpty())
        {
            return "收件人地址为空";
        }

        log.info("准备发送邮件: host={}, port={}, username={}, recipient={}",
                 config.getHost(), config.getPort(), config.getUsername(), config.getRecipientEmail());

        try
        {
            JavaMailSenderImpl mailSender = createMailSender(config);
            // 先测试连接
            log.info("正在连接SMTP服务器...");
            mailSender.testConnection();
            log.info("SMTP服务器连接成功");

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(config.getUsername(), config.getSenderName());
            helper.setTo(config.getRecipientEmail().split(","));
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            log.info("正在发送邮件...");
            mailSender.send(message);
            log.info("邮件发送成功");
            return null; // 成功返回null
        }
        catch (MessagingException e)
        {
            String errorMsg = "邮件发送失败: " + e.getMessage();
            log.error(errorMsg);
            log.error("异常详情: ", e);
            return errorMsg;
        }
        catch (Exception e)
        {
            String errorMsg = "邮件发送异常: " + e.getMessage();
            log.error(errorMsg);
            log.error("异常详情: ", e);
            return errorMsg;
        }
    }

    /**
     * 创建邮件发送器
     *
     * @param config 邥件配置
     * @return JavaMailSenderImpl
     */
    private JavaMailSenderImpl createMailSender(SysEmailConfig config)
    {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(config.getHost());
        mailSender.setPort(config.getPort());
        mailSender.setUsername(config.getUsername());
        mailSender.setPassword(config.getPassword());
        mailSender.setDefaultEncoding("UTF-8");

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        // 端口465使用SSL，不需要STARTTLS
        if (config.getPort() == 465)
        {
            props.put("mail.smtp.ssl.enable", "true");
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            props.put("mail.smtp.socketFactory.port", "465");
        }
        else if (config.getPort() == 25)
        {
            // 端口25通常不加密
            props.put("mail.smtp.ssl.enable", "false");
        }
        else
        {
            // 其他端口使用STARTTLS
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.starttls.required", "true");
        }
        props.put("mail.smtp.connectiontimeout", "10000");
        props.put("mail.smtp.timeout", "10000");
        props.put("mail.smtp.writetimeout", "10000");

        return mailSender;
    }
}
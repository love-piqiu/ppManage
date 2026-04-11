package com.ruoyi.system.service.impl;

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

/**
 * 邮件发送 服务层处理
 *
 * @author ppmanage
 */
@Service
public class SysEmailServiceImpl implements ISysEmailService
{
    private static final Logger log = LoggerFactory.getLogger(SysEmailServiceImpl.class);

    @Autowired
    private ISysEmailConfigService configService;

    /**
     * 发送周报邮件
     *
     * @param subject 邮件主题
     * @param htmlContent HTML内容
     * @return 是否成功
     */
    @Override
    public boolean sendWeeklyReport(String subject, String htmlContent)
    {
        SysEmailConfig config = configService.getConfig();
        if (config == null || config.getEnabled() == null || config.getEnabled() != 1)
        {
            log.warn("邮件配置不存在或未启用");
            return false;
        }

        try
        {
            JavaMailSenderImpl mailSender = createMailSender(config);
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(config.getUsername(), config.getSenderName());
            helper.setTo(config.getRecipientEmail().split(","));
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("周报邮件发送成功");
            return true;
        }
        catch (MessagingException e)
        {
            log.error("周报邮件发送失败: {}", e.getMessage());
            return false;
        }
        catch (Exception e)
        {
            log.error("周报邮件发送异常: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 发送测试邮件
     *
     * @return 是否成功
     */
    @Override
    public boolean sendTestEmail()
    {
        String subject = "测试邮件 - ppManage";
        String content = "<h2>测试邮件</h2><p>这是一封来自ppManage系统的测试邮件，邮件配置正常！</p>";
        return sendWeeklyReport(subject, content);
    }

    /**
     * 创建邮件发送器
     *
     * @param config 邮件配置
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
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.starttls.required", "true");
        props.put("mail.smtp.ssl.enable", "true");
        props.put("mail.smtp.connectiontimeout", "5000");
        props.put("mail.smtp.timeout", "5000");

        return mailSender;
    }
}
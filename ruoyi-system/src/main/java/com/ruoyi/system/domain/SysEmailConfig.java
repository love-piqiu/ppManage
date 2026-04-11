package com.ruoyi.system.domain;

import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 邮件配置对象 sys_email_config
 *
 * @author ppmanage
 */
public class SysEmailConfig extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 配置ID */
    private Long id;

    /** SMTP服务器 */
    private String host;

    /** SMTP端口 */
    private Integer port;

    /** 发件人账号 */
    private String username;

    /** 发件人密码/授权码 */
    private String password;

    /** 收件人地址(多个用逗号分隔) */
    private String recipientEmail;

    /** 发件人名称 */
    private String senderName;

    /** 邮件主题 */
    private String emailSubject;

    /** 发送日期 */
    private String sendDay;

    /** 发送时间 */
    private String sendTime;

    /** 是否启用 */
    private Integer enabled;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getHost()
    {
        return host;
    }

    public void setHost(String host)
    {
        this.host = host;
    }

    public Integer getPort()
    {
        return port;
    }

    public void setPort(Integer port)
    {
        this.port = port;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getRecipientEmail()
    {
        return recipientEmail;
    }

    public void setRecipientEmail(String recipientEmail)
    {
        this.recipientEmail = recipientEmail;
    }

    public String getSenderName()
    {
        return senderName;
    }

    public void setSenderName(String senderName)
    {
        this.senderName = senderName;
    }

    public String getEmailSubject()
    {
        return emailSubject;
    }

    public void setEmailSubject(String emailSubject)
    {
        this.emailSubject = emailSubject;
    }

    public String getSendDay()
    {
        return sendDay;
    }

    public void setSendDay(String sendDay)
    {
        this.sendDay = sendDay;
    }

    public String getSendTime()
    {
        return sendTime;
    }

    public void setSendTime(String sendTime)
    {
        this.sendTime = sendTime;
    }

    public Integer getEnabled()
    {
        return enabled;
    }

    public void setEnabled(Integer enabled)
    {
        this.enabled = enabled;
    }
}
package com.ruoyi.system.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 人员项目详情（用于人员详情页显示）
 *
 * @author ppmanage
 */
public class PersonProjectDetail
{
    /** 项目名称 */
    private String name;

    /** 角色 */
    private String role;

    /** 项目进度 */
    private Integer progress;

    /** 项目状态 */
    private String status;

    /** 参与时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date joinDate;

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getRole()
    {
        return role;
    }

    public void setRole(String role)
    {
        this.role = role;
    }

    public Integer getProgress()
    {
        return progress;
    }

    public void setProgress(Integer progress)
    {
        this.progress = progress;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public Date getJoinDate()
    {
        return joinDate;
    }

    public void setJoinDate(Date joinDate)
    {
        this.joinDate = joinDate;
    }
}
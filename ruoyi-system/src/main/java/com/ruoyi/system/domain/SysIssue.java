package com.ruoyi.system.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import jakarta.validation.constraints.NotNull;

/**
 * 问题对象 sys_issue
 *
 * @author ppmanage
 */
public class SysIssue extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 问题ID */
    private Long id;

    /** 项目ID */
    @Excel(name = "项目ID")
    @NotNull(message = "项目ID不能为空")
    private Long projectId;

    /** 项目名称 */
    @Excel(name = "项目名称")
    private String projectName;

    /** 问题描述 */
    @Excel(name = "问题描述")
    @NotNull(message = "问题描述不能为空")
    private String description;

    /** 问题类型 */
    @Excel(name = "问题类型")
    private String type;

    /** 严重程度:高/中/低 */
    @Excel(name = "严重程度")
    private String severity;

    /** 状态:待处理/进行中/已解决/已关闭 */
    @Excel(name = "状态")
    private String status;

    /** 负责人ID */
    private Long ownerId;

    /** 负责人姓名 */
    @Excel(name = "负责人")
    private String ownerName;

    /** 发现日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "发现日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date discoverDate;

    /** 计划解决日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "计划解决", width = 30, dateFormat = "yyyy-MM-dd")
    private Date planDate;

    /** 实际解决日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "实际解决", width = 30, dateFormat = "yyyy-MM-dd")
    private Date actualDate;

    /** 解决方案 */
    @Excel(name = "解决方案")
    private String solution;

    /** 删除标志 */
    private String delFlag;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getProjectId()
    {
        return projectId;
    }

    public void setProjectId(Long projectId)
    {
        this.projectId = projectId;
    }

    public String getProjectName()
    {
        return projectName;
    }

    public void setProjectName(String projectName)
    {
        this.projectName = projectName;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public String getSeverity()
    {
        return severity;
    }

    public void setSeverity(String severity)
    {
        this.severity = severity;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public Long getOwnerId()
    {
        return ownerId;
    }

    public void setOwnerId(Long ownerId)
    {
        this.ownerId = ownerId;
    }

    public String getOwnerName()
    {
        return ownerName;
    }

    public void setOwnerName(String ownerName)
    {
        this.ownerName = ownerName;
    }

    public Date getDiscoverDate()
    {
        return discoverDate;
    }

    public void setDiscoverDate(Date discoverDate)
    {
        this.discoverDate = discoverDate;
    }

    public Date getPlanDate()
    {
        return planDate;
    }

    public void setPlanDate(Date planDate)
    {
        this.planDate = planDate;
    }

    public Date getActualDate()
    {
        return actualDate;
    }

    public void setActualDate(Date actualDate)
    {
        this.actualDate = actualDate;
    }

    public String getSolution()
    {
        return solution;
    }

    public void setSolution(String solution)
    {
        this.solution = solution;
    }

    public String getDelFlag()
    {
        return delFlag;
    }

    public void setDelFlag(String delFlag)
    {
        this.delFlag = delFlag;
    }
}
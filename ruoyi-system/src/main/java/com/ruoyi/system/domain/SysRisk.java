package com.ruoyi.system.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import jakarta.validation.constraints.NotNull;

/**
 * 风险对象 sys_risk
 *
 * @author ppmanage
 */
public class SysRisk extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 风险ID */
    private Long id;

    /** 项目ID */
    @Excel(name = "项目ID")
    @NotNull(message = "项目ID不能为空")
    private Long projectId;

    /** 项目名称 */
    @Excel(name = "项目名称")
    private String projectName;

    /** 风险描述 */
    @Excel(name = "风险描述")
    @NotNull(message = "风险描述不能为空")
    private String description;

    /** 风险等级:高/中/低 */
    @Excel(name = "风险等级")
    private String level;

    /** 状态:潜在/已发生/已消除 */
    @Excel(name = "状态")
    private String status;

    /** 应对措施 */
    @Excel(name = "应对措施")
    private String measure;

    /** 负责人ID */
    private Long ownerId;

    /** 负责人姓名 */
    @Excel(name = "负责人")
    private String ownerName;

    /** 发生日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "发生日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date happenDate;

    /** 消除日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "消除日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date resolveDate;

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

    public String getLevel()
    {
        return level;
    }

    public void setLevel(String level)
    {
        this.level = level;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getMeasure()
    {
        return measure;
    }

    public void setMeasure(String measure)
    {
        this.measure = measure;
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

    public Date getHappenDate()
    {
        return happenDate;
    }

    public void setHappenDate(Date happenDate)
    {
        this.happenDate = happenDate;
    }

    public Date getResolveDate()
    {
        return resolveDate;
    }

    public void setResolveDate(Date resolveDate)
    {
        this.resolveDate = resolveDate;
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
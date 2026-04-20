package com.ruoyi.system.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 项目自定义里程碑对象 sys_project_milestone_custom
 *
 * @author ppmanage
 */
public class SysProjectMilestoneCustom extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;

    /** 项目ID */
    private Long projectId;

    /** 里程碑名称 */
    private String milestoneName;

    /** 计划时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date planDate;

    /** 完成时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date actualDate;

    /** 排序顺序 */
    private Integer sortOrder;

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

    public String getMilestoneName()
    {
        return milestoneName;
    }

    public void setMilestoneName(String milestoneName)
    {
        this.milestoneName = milestoneName;
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

    public Integer getSortOrder()
    {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder)
    {
        this.sortOrder = sortOrder;
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
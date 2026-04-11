package com.ruoyi.system.domain;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import jakarta.validation.constraints.NotNull;

/**
 * 任务对象 sys_task
 *
 * @author ppmanage
 */
public class SysTask extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 任务ID */
    private Long id;

    /** 任务名称 */
    @Excel(name = "任务名称")
    @NotNull(message = "任务名称不能为空")
    private String name;

    /** 类型:周期性/一次性 */
    @Excel(name = "类型")
    @NotNull(message = "类型不能为空")
    private String type;

    /** 周期:每日/每周/每月 */
    @Excel(name = "周期")
    private String cycle;

    /** 截止时间点(如18:00) */
    private String deadlineTime;

    /** 截止日期(如每月5号) */
    private Integer deadlineDay;

    /** 截止星期(如周五) */
    private String deadlineWeekday;

    /** 状态:启用/禁用 */
    @Excel(name = "状态")
    private String status;

    /** 描述 */
    @Excel(name = "描述")
    private String description;

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

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public String getCycle()
    {
        return cycle;
    }

    public void setCycle(String cycle)
    {
        this.cycle = cycle;
    }

    public String getDeadlineTime()
    {
        return deadlineTime;
    }

    public void setDeadlineTime(String deadlineTime)
    {
        this.deadlineTime = deadlineTime;
    }

    public Integer getDeadlineDay()
    {
        return deadlineDay;
    }

    public void setDeadlineDay(Integer deadlineDay)
    {
        this.deadlineDay = deadlineDay;
    }

    public String getDeadlineWeekday()
    {
        return deadlineWeekday;
    }

    public void setDeadlineWeekday(String deadlineWeekday)
    {
        this.deadlineWeekday = deadlineWeekday;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
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
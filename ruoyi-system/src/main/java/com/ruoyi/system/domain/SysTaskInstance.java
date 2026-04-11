package com.ruoyi.system.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import jakarta.validation.constraints.NotNull;

/**
 * 任务实例对象 sys_task_instance
 *
 * @author ppmanage
 */
public class SysTaskInstance extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 实例ID */
    private Long id;

    /** 任务ID */
    @Excel(name = "任务ID")
    @NotNull(message = "任务ID不能为空")
    private Long taskId;

    /** 任务名称 */
    @Excel(name = "任务名称")
    private String taskName;

    /** 人员ID */
    @Excel(name = "人员ID")
    @NotNull(message = "人员ID不能为空")
    private Long personId;

    /** 人员姓名 */
    @Excel(name = "人员姓名")
    private String personName;

    /** 周期标识:2026-04-10/2026-W15/2026-04 */
    @Excel(name = "周期")
    @NotNull(message = "周期不能为空")
    private String period;

    /** 截止时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "截止时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date deadline;

    /** 是否完成:0/1 */
    @Excel(name = "是否完成")
    private Integer completed;

    /** 完成时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "完成时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date completeTime;

    /** 是否按时:0/1 */
    @Excel(name = "是否按时")
    private Integer onTime;

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

    public Long getTaskId()
    {
        return taskId;
    }

    public void setTaskId(Long taskId)
    {
        this.taskId = taskId;
    }

    public String getTaskName()
    {
        return taskName;
    }

    public void setTaskName(String taskName)
    {
        this.taskName = taskName;
    }

    public Long getPersonId()
    {
        return personId;
    }

    public void setPersonId(Long personId)
    {
        this.personId = personId;
    }

    public String getPersonName()
    {
        return personName;
    }

    public void setPersonName(String personName)
    {
        this.personName = personName;
    }

    public String getPeriod()
    {
        return period;
    }

    public void setPeriod(String period)
    {
        this.period = period;
    }

    public Date getDeadline()
    {
        return deadline;
    }

    public void setDeadline(Date deadline)
    {
        this.deadline = deadline;
    }

    public Integer getCompleted()
    {
        return completed;
    }

    public void setCompleted(Integer completed)
    {
        this.completed = completed;
    }

    public Date getCompleteTime()
    {
        return completeTime;
    }

    public void setCompleteTime(Date completeTime)
    {
        this.completeTime = completeTime;
    }

    public Integer getOnTime()
    {
        return onTime;
    }

    public void setOnTime(Integer onTime)
    {
        this.onTime = onTime;
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
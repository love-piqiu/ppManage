package com.ruoyi.system.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import jakarta.validation.constraints.NotNull;

/**
 * 项目对象 sys_project
 *
 * @author ppmanage
 */
public class SysProject extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 项目ID */
    private Long id;

    /** 项目名称 */
    @Excel(name = "项目名称")
    @NotNull(message = "项目名称不能为空")
    private String name;

    /** 客户 */
    @Excel(name = "客户")
    private String customer;

    /** 项目经理ID */
    private Long pmId;

    /** 项目经理姓名 */
    @Excel(name = "项目经理")
    private String pmName;

    /** 销售人员ID */
    private Long salesId;

    /** 销售人员姓名 */
    @Excel(name = "销售人员")
    private String salesName;

    /** 成本(万) */
    @Excel(name = "成本(万)")
    private BigDecimal cost;

    /** 已使用成本(万) */
    @Excel(name = "已用成本(万)")
    private BigDecimal costUsed;

    /** 总工时(人天) */
    @Excel(name = "总工时(人天)")
    private Integer workHours;

    /** 已使用工时(人天) */
    @Excel(name = "已用工时(人天)")
    private Integer workHoursUsed;

    /** 进度(%) */
    @Excel(name = "进度(%)")
    private Integer progress;

    /** 当前阶段 */
    @Excel(name = "当前阶段")
    private String stage;

    /** 状态:进行中/已完成/暂停 */
    @Excel(name = "状态")
    private String status;

    /** 开始日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "开始日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date startDate;

    /** 计划结束日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "计划结束", width = 30, dateFormat = "yyyy-MM-dd")
    private Date endDate;

    /** 实际结束日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "实际结束", width = 30, dateFormat = "yyyy-MM-dd")
    private Date actualEndDate;

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

    public String getCustomer()
    {
        return customer;
    }

    public void setCustomer(String customer)
    {
        this.customer = customer;
    }

    public Long getPmId()
    {
        return pmId;
    }

    public void setPmId(Long pmId)
    {
        this.pmId = pmId;
    }

    public String getPmName()
    {
        return pmName;
    }

    public void setPmName(String pmName)
    {
        this.pmName = pmName;
    }

    public Long getSalesId()
    {
        return salesId;
    }

    public void setSalesId(Long salesId)
    {
        this.salesId = salesId;
    }

    public String getSalesName()
    {
        return salesName;
    }

    public void setSalesName(String salesName)
    {
        this.salesName = salesName;
    }

    public BigDecimal getCost()
    {
        return cost;
    }

    public void setCost(BigDecimal cost)
    {
        this.cost = cost;
    }

    public BigDecimal getCostUsed()
    {
        return costUsed;
    }

    public void setCostUsed(BigDecimal costUsed)
    {
        this.costUsed = costUsed;
    }

    public Integer getWorkHours()
    {
        return workHours;
    }

    public void setWorkHours(Integer workHours)
    {
        this.workHours = workHours;
    }

    public Integer getWorkHoursUsed()
    {
        return workHoursUsed;
    }

    public void setWorkHoursUsed(Integer workHoursUsed)
    {
        this.workHoursUsed = workHoursUsed;
    }

    public Integer getProgress()
    {
        return progress;
    }

    public void setProgress(Integer progress)
    {
        this.progress = progress;
    }

    public String getStage()
    {
        return stage;
    }

    public void setStage(String stage)
    {
        this.stage = stage;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public Date getStartDate()
    {
        return startDate;
    }

    public void setStartDate(Date startDate)
    {
        this.startDate = startDate;
    }

    public Date getEndDate()
    {
        return endDate;
    }

    public void setEndDate(Date endDate)
    {
        this.endDate = endDate;
    }

    public Date getActualEndDate()
    {
        return actualEndDate;
    }

    public void setActualEndDate(Date actualEndDate)
    {
        this.actualEndDate = actualEndDate;
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
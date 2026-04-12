package com.ruoyi.system.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 项目开票对象 sys_project_invoice
 *
 * @author ppmanage
 */
public class SysProjectInvoice extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 开票ID */
    private Long id;

    /** 项目ID */
    @Excel(name = "项目ID")
    private Long projectId;

    /** 开票金额(万) */
    @Excel(name = "开票金额(万)")
    private BigDecimal amount;

    /** 占合同比例(%) */
    @Excel(name = "占合同比例")
    private Integer percent;

    /** 开票类型:预付款/进度款/验收款/尾款 */
    @Excel(name = "开票类型")
    private String invoiceType;

    /** 开票日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "开票日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date invoiceDate;

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

    public BigDecimal getAmount()
    {
        return amount;
    }

    public void setAmount(BigDecimal amount)
    {
        this.amount = amount;
    }

    public Integer getPercent()
    {
        return percent;
    }

    public void setPercent(Integer percent)
    {
        this.percent = percent;
    }

    public String getInvoiceType()
    {
        return invoiceType;
    }

    public void setInvoiceType(String invoiceType)
    {
        this.invoiceType = invoiceType;
    }

    public Date getInvoiceDate()
    {
        return invoiceDate;
    }

    public void setInvoiceDate(Date invoiceDate)
    {
        this.invoiceDate = invoiceDate;
    }
}
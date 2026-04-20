package com.ruoyi.system.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 人员动态记录对象 sys_person_record
 *
 * @author ppmanage
 */
public class SysPersonRecord extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 记录ID */
    private Long id;

    /** 人员ID */
    @Excel(name = "人员ID")
    private Long personId;

    /** 人员姓名 */
    @Excel(name = "人员姓名")
    private String personName;

    /** 记录类型 */
    @Excel(name = "记录类型")
    private String recordType;

    /** 记录内容 */
    @Excel(name = "记录内容")
    private String content;

    /** 记录日期 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    @Excel(name = "记录日期", width = 30, dateFormat = "yyyy-MM-dd HH:mm")
    private Date recordDate;

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

    public String getRecordType()
    {
        return recordType;
    }

    public void setRecordType(String recordType)
    {
        this.recordType = recordType;
    }

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    public Date getRecordDate()
    {
        return recordDate;
    }

    public void setRecordDate(Date recordDate)
    {
        this.recordDate = recordDate;
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
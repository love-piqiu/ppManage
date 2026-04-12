package com.ruoyi.system.domain;

import java.util.Date;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.annotation.Excel.ColumnType;
import com.ruoyi.common.core.domain.BaseEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 人员对象 sys_person
 *
 * @author ppmanage
 */
public class SysPerson extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 人员ID */
    private Long id;

    /** 姓名 */
    @Excel(name = "姓名")
    private String name;

    /** 职位 */
    @Excel(name = "职位")
    private String position;

    /** 等级 */
    @Excel(name = "等级")
    private String level;

    /** 联系方式 */
    @Excel(name = "联系方式")
    private String contact;

    /** 邮箱 */
    @Excel(name = "邮箱")
    private String email;

    /** 出生日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "出生日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date birthDate;

    /** 年龄 */
    @Excel(name = "年龄")
    private Integer age;

    /** 工作年限 */
    @Excel(name = "工作年限")
    private Integer workYears;

    /** 入司年限 */
    @Excel(name = "入司年限")
    private Integer companyYears;

    /** 学历 */
    @Excel(name = "学历")
    private String education;

    /** 毕业院校 */
    @Excel(name = "毕业院校")
    private String school;

    /** 籍贯 */
    @Excel(name = "籍贯")
    private String hometown;

    /** 入职日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "入职日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date entryDate;

    /** 状态 */
    @Excel(name = "状态")
    private String status;

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

    @NotBlank(message = "姓名不能为空")
    @Size(min = 0, max = 50, message = "姓名长度不能超过50个字符")
    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    @Size(min = 0, max = 50, message = "职位长度不能超过50个字符")
    public String getPosition()
    {
        return position;
    }

    public void setPosition(String position)
    {
        this.position = position;
    }

    @Size(min = 0, max = 20, message = "等级长度不能超过20个字符")
    public String getLevel()
    {
        return level;
    }

    public void setLevel(String level)
    {
        this.level = level;
    }

    @Size(min = 0, max = 100, message = "联系方式长度不能超过100个字符")
    public String getContact()
    {
        return contact;
    }

    public void setContact(String contact)
    {
        this.contact = contact;
    }

    @Size(min = 0, max = 100, message = "邮箱长度不能超过100个字符")
    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public Integer getAge()
    {
        return age;
    }

    public void setAge(Integer age)
    {
        this.age = age;
    }

    public Integer getWorkYears()
    {
        return workYears;
    }

    public void setWorkYears(Integer workYears)
    {
        this.workYears = workYears;
    }

    public Integer getCompanyYears()
    {
        return companyYears;
    }

    public void setCompanyYears(Integer companyYears)
    {
        this.companyYears = companyYears;
    }

    @Size(min = 0, max = 20, message = "学历长度不能超过20个字符")
    public String getEducation()
    {
        return education;
    }

    public void setEducation(String education)
    {
        this.education = education;
    }

    @Size(min = 0, max = 100, message = "毕业院校长度不能超过100个字符")
    public String getSchool()
    {
        return school;
    }

    public void setSchool(String school)
    {
        this.school = school;
    }

    @Size(min = 0, max = 100, message = "籍贯长度不能超过100个字符")
    public String getHometown()
    {
        return hometown;
    }

    public void setHometown(String hometown)
    {
        this.hometown = hometown;
    }

    public Date getBirthDate()
    {
        return birthDate;
    }

    public void setBirthDate(Date birthDate)
    {
        this.birthDate = birthDate;
        // 自动计算年龄
        if (birthDate != null)
        {
            LocalDate birthLocalDate = birthDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate now = LocalDate.now();
            this.age = Period.between(birthLocalDate, now).getYears();
        }
        else
        {
            this.age = null;
        }
    }

    public Date getEntryDate()
    {
        return entryDate;
    }

    public void setEntryDate(Date entryDate)
    {
        this.entryDate = entryDate;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
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
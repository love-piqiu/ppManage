package com.ruoyi.system.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import jakarta.validation.constraints.NotNull;

/**
 * 人员-项目关联对象 sys_person_project
 *
 * @author ppmanage
 */
public class SysPersonProject extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 关联ID */
    private Long id;

    /** 人员ID */
    @Excel(name = "人员ID")
    private Long personId;

    /** 人员姓名 */
    @Excel(name = "人员姓名")
    private String personName;

    /** 项目ID */
    @Excel(name = "项目ID")
    private Long projectId;

    /** 项目名称 */
    @Excel(name = "项目名称")
    private String projectName;

    /** 角色 */
    @Excel(name = "角色")
    private String role;

    /** 投入比例 */
    @Excel(name = "投入比例")
    private Integer involvementRate;

    /** 参与时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "参与时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date joinDate;

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

    @NotNull(message = "人员ID不能为空")
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

    @NotNull(message = "项目ID不能为空")
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

    public String getRole()
    {
        return role;
    }

    public void setRole(String role)
    {
        this.role = role;
    }

    public Integer getInvolvementRate()
    {
        return involvementRate;
    }

    public void setInvolvementRate(Integer involvementRate)
    {
        this.involvementRate = involvementRate;
    }

    public Date getJoinDate()
    {
        return joinDate;
    }

    public void setJoinDate(Date joinDate)
    {
        this.joinDate = joinDate;
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
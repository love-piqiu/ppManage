package com.ruoyi.system.domain;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 人员技能对象 sys_person_skill
 *
 * @author ppmanage
 */
public class SysPersonSkill extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 技能记录ID */
    private Long id;

    /** 人员ID */
    @Excel(name = "人员ID")
    private Long personId;

    /** 技能大类 */
    @Excel(name = "技能大类")
    private String category;

    /** 具体技能 */
    @Excel(name = "具体技能")
    private String skill;

    /** 来源 */
    @Excel(name = "来源")
    private String source;

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

    public String getCategory()
    {
        return category;
    }

    public void setCategory(String category)
    {
        this.category = category;
    }

    public String getSkill()
    {
        return skill;
    }

    public void setSkill(String skill)
    {
        this.skill = skill;
    }

    public String getSource()
    {
        return source;
    }

    public void setSource(String source)
    {
        this.source = source;
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
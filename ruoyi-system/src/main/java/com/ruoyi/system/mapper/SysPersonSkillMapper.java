package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.SysPersonSkill;

/**
 * 人员技能 数据层
 *
 * @author ppmanage
 */
public interface SysPersonSkillMapper
{
    /**
     * 查询人员技能列表
     *
     * @param personSkill 人员技能
     * @return 人员技能列表
     */
    public List<SysPersonSkill> selectPersonSkillList(SysPersonSkill personSkill);

    /**
     * 根据人员ID查询技能列表
     *
     * @param personId 人员ID
     * @return 技能列表
     */
    public List<SysPersonSkill> selectSkillByPersonId(Long personId);

    /**
     * 根据技能筛选人员ID列表
     *
     * @param category 技能大类
     * @param skill 具体技能
     * @return 人员ID列表
     */
    public List<Long> selectPersonIdsBySkill(String category, String skill);

    /**
     * 通过技能ID查询技能信息
     *
     * @param id 技能ID
     * @return 技能信息
     */
    public SysPersonSkill selectPersonSkillById(Long id);

    /**
     * 新增人员技能
     *
     * @param personSkill 人员技能
     * @return 结果
     */
    public int insertPersonSkill(SysPersonSkill personSkill);

    /**
     * 修改人员技能
     *
     * @param personSkill 人员技能
     * @return 结果
     */
    public int updatePersonSkill(SysPersonSkill personSkill);

    /**
     * 删除人员技能
     *
     * @param id 技能ID
     * @return 结果
     */
    public int deletePersonSkillById(Long id);

    /**
     * 批量删除人员技能
     *
     * @param ids 需要删除的技能ID
     * @return 结果
     */
    public int deletePersonSkillByIds(Long[] ids);

    /**
     * 根据人员ID删除技能
     *
     * @param personId 人员ID
     * @return 结果
     */
    public int deletePersonSkillByPersonId(Long personId);
}
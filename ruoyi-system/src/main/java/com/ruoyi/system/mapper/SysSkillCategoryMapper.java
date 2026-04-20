package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.SysSkillCategory;

/**
 * 技能分类字典 数据层
 *
 * @author ppmanage
 */
public interface SysSkillCategoryMapper
{
    /**
     * 查询技能分类列表
     *
     * @param skillCategory 技能分类
     * @return 技能分类列表
     */
    public List<SysSkillCategory> selectSkillCategoryList(SysSkillCategory skillCategory);

    /**
     * 查询所有启用的技能分类
     *
     * @return 技能分类列表
     */
    public List<SysSkillCategory> selectSkillCategoryAll();

    /**
     * 通过分类ID查询技能分类
     *
     * @param id 分类ID
     * @return 技能分类
     */
    public SysSkillCategory selectSkillCategoryById(Long id);

    /**
     * 通过分类名称查询技能分类
     *
     * @param category 分类名称
     * @return 技能分类
     */
    public SysSkillCategory selectSkillCategoryByName(String category);

    /**
     * 新增技能分类
     *
     * @param skillCategory 技能分类
     * @return 结果
     */
    public int insertSkillCategory(SysSkillCategory skillCategory);

    /**
     * 修改技能分类
     *
     * @param skillCategory 技能分类
     * @return 结果
     */
    public int updateSkillCategory(SysSkillCategory skillCategory);

    /**
     * 删除技能分类
     *
     * @param id 分类ID
     * @return 结果
     */
    public int deleteSkillCategoryById(Long id);

    /**
     * 批量删除技能分类
     *
     * @param ids 需要删除的分类ID
     * @return 结果
     */
    public int deleteSkillCategoryByIds(Long[] ids);
}
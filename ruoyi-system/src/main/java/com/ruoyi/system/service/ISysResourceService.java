package com.ruoyi.system.service;

import java.util.List;
import java.util.Map;
import com.ruoyi.system.domain.SysPersonSkill;
import com.ruoyi.system.domain.SysSkillCategory;

/**
 * 资源管理 服务层
 *
 * @author ppmanage
 */
public interface ISysResourceService
{
    /**
     * 查询资源列表（含技能和项目数）
     *
     * @param params 筛选参数
     * @return 资源列表
     */
    public List<Map<String, Object>> selectResourceList(Map<String, Object> params);

    /**
     * 查询技能分类列表
     *
     * @return 技能分类列表
     */
    public List<SysSkillCategory> selectSkillCategoryAll();

    /**
     * 根据人员ID查询技能列表
     *
     * @param personId 人员ID
     * @return 技能列表
     */
    public List<SysPersonSkill> selectSkillByPersonId(Long personId);

    /**
     * 添加人员技能
     *
     * @param personSkill 人员技能
     * @return 结果
     */
    public int insertPersonSkill(SysPersonSkill personSkill);

    /**
     * 删除人员技能
     *
     * @param id 技能ID
     * @return 结果
     */
    public int deletePersonSkillById(Long id);

    /**
     * 导出技能数据
     *
     * @return 技能数据列表
     */
    public List<Map<String, Object>> exportSkillData();

    /**
     * 导出项目参与数据
     *
     * @return 项目参与数据列表
     */
    public List<Map<String, Object>> exportProjectData();
}
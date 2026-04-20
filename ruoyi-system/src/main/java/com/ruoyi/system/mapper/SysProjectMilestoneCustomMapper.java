package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.SysProjectMilestoneCustom;

/**
 * 项目自定义里程碑 数据层
 *
 * @author ppmanage
 */
public interface SysProjectMilestoneCustomMapper
{
    /**
     * 查询项目的自定义里程碑列表
     *
     * @param projectId 项目ID
     * @return 里程碑列表
     */
    public List<SysProjectMilestoneCustom> selectByProjectId(Long projectId);

    /**
     * 通过ID查询自定义里程碑
     *
     * @param id 里程碑ID
     * @return 里程碑
     */
    public SysProjectMilestoneCustom selectById(Long id);

    /**
     * 新增自定义里程碑
     *
     * @param milestone 里程碑信息
     * @return 结果
     */
    public int insert(SysProjectMilestoneCustom milestone);

    /**
     * 修改自定义里程碑
     *
     * @param milestone 里程碑信息
     * @return 结果
     */
    public int update(SysProjectMilestoneCustom milestone);

    /**
     * 删除自定义里程碑
     *
     * @param id 里程碑ID
     * @return 结果
     */
    public int deleteById(Long id);

    /**
     * 批量删除项目的自定义里程碑
     *
     * @param projectId 项目ID
     * @return 结果
     */
    public int deleteByProjectId(Long projectId);
}
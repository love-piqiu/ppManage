package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.SysProjectMilestoneCustom;

/**
 * 项目自定义里程碑 服务层
 *
 * @author ppmanage
 */
public interface ISysProjectMilestoneCustomService
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

    /**
     * 批量保存项目的自定义里程碑（新增或修改）
     *
     * @param projectId 项目ID
     * @param milestones 里程碑列表
     * @param username 操作人
     * @return 结果
     */
    public int batchSave(Long projectId, List<SysProjectMilestoneCustom> milestones, String username);
}
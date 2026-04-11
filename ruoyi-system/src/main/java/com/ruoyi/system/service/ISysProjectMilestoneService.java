package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.SysProjectMilestone;

/**
 * 项目重要事项 服务层
 *
 * @author ppmanage
 */
public interface ISysProjectMilestoneService
{
    /**
     * 查询项目的重要事项列表
     *
     * @param projectId 项目ID
     * @return 事项列表
     */
    public List<SysProjectMilestone> selectByProjectId(Long projectId);

    /**
     * 通过ID查询事项信息
     *
     * @param id 事项ID
     * @return 事项信息
     */
    public SysProjectMilestone selectById(Long id);

    /**
     * 新增重要事项
     *
     * @param milestone 事项信息
     * @return 结果
     */
    public int insert(SysProjectMilestone milestone);

    /**
     * 删除重要事项
     *
     * @param id 事项ID
     * @return 结果
     */
    public int deleteById(Long id);
}
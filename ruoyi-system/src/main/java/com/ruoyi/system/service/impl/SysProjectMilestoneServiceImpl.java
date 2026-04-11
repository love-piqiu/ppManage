package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.domain.SysProjectMilestone;
import com.ruoyi.system.mapper.SysProjectMilestoneMapper;
import com.ruoyi.system.service.ISysProjectMilestoneService;

/**
 * 项目重要事项 服务层处理
 *
 * @author ppmanage
 */
@Service
public class SysProjectMilestoneServiceImpl implements ISysProjectMilestoneService
{
    @Autowired
    private SysProjectMilestoneMapper milestoneMapper;

    /**
     * 查询项目的重要事项列表
     *
     * @param projectId 项目ID
     * @return 事项列表
     */
    @Override
    public List<SysProjectMilestone> selectByProjectId(Long projectId)
    {
        return milestoneMapper.selectByProjectId(projectId);
    }

    /**
     * 通过ID查询事项信息
     *
     * @param id 事项ID
     * @return 事项信息
     */
    @Override
    public SysProjectMilestone selectById(Long id)
    {
        return milestoneMapper.selectById(id);
    }

    /**
     * 新增重要事项
     *
     * @param milestone 事项信息
     * @return 结果
     */
    @Override
    public int insert(SysProjectMilestone milestone)
    {
        return milestoneMapper.insert(milestone);
    }

    /**
     * 删除重要事项
     *
     * @param id 事项ID
     * @return 结果
     */
    @Override
    public int deleteById(Long id)
    {
        return milestoneMapper.deleteById(id);
    }
}
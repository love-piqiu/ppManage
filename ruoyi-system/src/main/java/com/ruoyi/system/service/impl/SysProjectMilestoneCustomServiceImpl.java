package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.system.domain.SysProjectMilestoneCustom;
import com.ruoyi.system.mapper.SysProjectMilestoneCustomMapper;
import com.ruoyi.system.service.ISysProjectMilestoneCustomService;

/**
 * 项目自定义里程碑 服务层处理
 *
 * @author ppmanage
 */
@Service
public class SysProjectMilestoneCustomServiceImpl implements ISysProjectMilestoneCustomService
{
    @Autowired
    private SysProjectMilestoneCustomMapper milestoneMapper;

    /**
     * 查询项目的自定义里程碑列表
     */
    @Override
    public List<SysProjectMilestoneCustom> selectByProjectId(Long projectId)
    {
        return milestoneMapper.selectByProjectId(projectId);
    }

    /**
     * 通过ID查询自定义里程碑
     */
    @Override
    public SysProjectMilestoneCustom selectById(Long id)
    {
        return milestoneMapper.selectById(id);
    }

    /**
     * 新增自定义里程碑
     */
    @Override
    public int insert(SysProjectMilestoneCustom milestone)
    {
        return milestoneMapper.insert(milestone);
    }

    /**
     * 修改自定义里程碑
     */
    @Override
    public int update(SysProjectMilestoneCustom milestone)
    {
        return milestoneMapper.update(milestone);
    }

    /**
     * 删除自定义里程碑
     */
    @Override
    public int deleteById(Long id)
    {
        return milestoneMapper.deleteById(id);
    }

    /**
     * 批量删除项目的自定义里程碑
     */
    @Override
    public int deleteByProjectId(Long projectId)
    {
        return milestoneMapper.deleteByProjectId(projectId);
    }

    /**
     * 批量保存项目的自定义里程碑
     */
    @Override
    @Transactional
    public int batchSave(Long projectId, List<SysProjectMilestoneCustom> milestones, String username)
    {
        // 先删除原有的里程碑
        milestoneMapper.deleteByProjectId(projectId);

        // 批量插入新的里程碑
        int count = 0;
        for (int i = 0; i < milestones.size(); i++)
        {
            SysProjectMilestoneCustom milestone = milestones.get(i);
            milestone.setProjectId(projectId);
            milestone.setSortOrder(i);
            milestone.setCreateBy(username);
            count += milestoneMapper.insert(milestone);
        }
        return count;
    }
}
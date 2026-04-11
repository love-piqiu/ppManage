package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.constant.UserConstants;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.SysProject;
import com.ruoyi.system.mapper.SysProjectMapper;
import com.ruoyi.system.mapper.SysProjectMilestoneMapper;
import com.ruoyi.system.mapper.SysPersonProjectMapper;
import com.ruoyi.system.service.ISysProjectService;

/**
 * 项目信息 服务层处理
 *
 * @author ppmanage
 */
@Service
public class SysProjectServiceImpl implements ISysProjectService
{
    @Autowired
    private SysProjectMapper projectMapper;

    @Autowired
    private SysProjectMilestoneMapper milestoneMapper;

    @Autowired
    private SysPersonProjectMapper personProjectMapper;

    /**
     * 查询项目列表
     *
     * @param project 项目信息
     * @return 项目列表
     */
    @Override
    public List<SysProject> selectProjectList(SysProject project)
    {
        return projectMapper.selectProjectList(project);
    }

    /**
     * 查询所有项目
     *
     * @return 项目列表
     */
    @Override
    public List<SysProject> selectProjectAll()
    {
        return projectMapper.selectProjectAll();
    }

    /**
     * 通过项目ID查询项目信息
     *
     * @param id 项目ID
     * @return 项目信息
     */
    @Override
    public SysProject selectProjectById(Long id)
    {
        return projectMapper.selectProjectById(id);
    }

    /**
     * 校验项目名称是否唯一
     *
     * @param project 项目信息
     * @return 结果
     */
    @Override
    public boolean checkNameUnique(SysProject project)
    {
        Long projectId = StringUtils.isNull(project.getId()) ? -1L : project.getId();
        SysProject info = projectMapper.checkNameUnique(project.getName());
        if (StringUtils.isNotNull(info) && info.getId().longValue() != projectId.longValue())
        {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 新增项目
     *
     * @param project 项目信息
     * @return 结果
     */
    @Override
    public int insertProject(SysProject project)
    {
        return projectMapper.insertProject(project);
    }

    /**
     * 修改项目
     *
     * @param project 项目信息
     * @return 结果
     */
    @Override
    public int updateProject(SysProject project)
    {
        return projectMapper.updateProject(project);
    }

    /**
     * 删除项目
     *
     * @param id 项目ID
     * @return 结果
     */
    @Override
    public int deleteProjectById(Long id)
    {
        // 同时删除关联的重要事项和人员关联
        milestoneMapper.deleteByProjectId(id);
        personProjectMapper.deleteByProjectId(id);
        return projectMapper.deleteProjectById(id);
    }

    /**
     * 批量删除项目
     *
     * @param ids 需要删除的项目ID
     * @return 结果
     */
    @Override
    public int deleteProjectByIds(Long[] ids)
    {
        for (Long id : ids)
        {
            milestoneMapper.deleteByProjectId(id);
            personProjectMapper.deleteByProjectId(id);
        }
        return projectMapper.deleteProjectByIds(ids);
    }

    /**
     * 查询进行中的项目数量
     *
     * @return 数量
     */
    @Override
    public int countActiveProject()
    {
        return projectMapper.countActiveProject();
    }

    /**
     * 查询已完成的项目数量
     *
     * @return 数量
     */
    @Override
    public int countCompletedProject()
    {
        return projectMapper.countCompletedProject();
    }
}
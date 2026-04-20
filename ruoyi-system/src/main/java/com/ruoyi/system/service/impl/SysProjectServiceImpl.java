package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.constant.UserConstants;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.SysProject;
import com.ruoyi.system.domain.SysPersonProject;
import com.ruoyi.system.domain.SysProjectMilestoneCustom;
import com.ruoyi.system.mapper.SysProjectMapper;
import com.ruoyi.system.mapper.SysProjectMilestoneMapper;
import com.ruoyi.system.mapper.SysProjectMilestoneCustomMapper;
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
    private SysProjectMilestoneCustomMapper customMilestoneMapper;

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
        List<SysProject> list = projectMapper.selectProjectList(project);
        // 为外包项目查询自定义里程碑
        for (SysProject p : list)
        {
            if ("外包".equals(p.getProjectType()))
            {
                List<SysProjectMilestoneCustom> customMilestones = customMilestoneMapper.selectByProjectId(p.getId());
                p.setCustomMilestones(customMilestones);
            }
        }
        return list;
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
        SysProject project = projectMapper.selectProjectById(id);
        if (project != null)
        {
            // 查询参与人员
            List<SysPersonProject> personProjects = personProjectMapper.selectByProjectId(id);
            List<Long> participants = personProjects.stream()
                .map(SysPersonProject::getPersonId)
                .collect(java.util.stream.Collectors.toList());
            project.setParticipants(participants);

            // 如果是外包项目，查询自定义里程碑
            if ("外包".equals(project.getProjectType()))
            {
                List<SysProjectMilestoneCustom> customMilestones = customMilestoneMapper.selectByProjectId(id);
                project.setCustomMilestones(customMilestones);
            }
        }
        return project;
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
    @Transactional
    public int insertProject(SysProject project)
    {
        int result = projectMapper.insertProject(project);
        // 保存参与人员关联
        saveParticipants(project.getId(), project.getParticipants());
        // 只有下辖项目才保存里程碑
        if ("是".equals(project.getIsSubordinate()) && "外包".equals(project.getProjectType()) && project.getCustomMilestones() != null)
        {
            saveCustomMilestones(project.getId(), project.getCustomMilestones(), project.getCreateBy());
        }
        return result;
    }

    /**
     * 修改项目
     *
     * @param project 项目信息
     * @return 结果
     */
    @Override
    @Transactional
    public int updateProject(SysProject project)
    {
        // 删除旧的参与人员关联
        personProjectMapper.deleteByProjectId(project.getId());
        // 保存新的参与人员关联
        saveParticipants(project.getId(), project.getParticipants());
        // 只有下辖项目才保存里程碑
        if ("是".equals(project.getIsSubordinate()) && "外包".equals(project.getProjectType()) && project.getCustomMilestones() != null)
        {
            saveCustomMilestones(project.getId(), project.getCustomMilestones(), project.getUpdateBy());
        }
        else
        {
            // 如果不再是下辖项目或改为项目类型，删除自定义里程碑
            customMilestoneMapper.deleteByProjectId(project.getId());
        }
        return projectMapper.updateProject(project);
    }

    /**
     * 保存参与人员关联
     *
     * @param projectId 项目ID
     * @param participants 参与人员ID列表
     */
    private void saveParticipants(Long projectId, List<Long> participants)
    {
        if (participants != null && !participants.isEmpty())
        {
            for (Long personId : participants)
            {
                SysPersonProject pp = new SysPersonProject();
                pp.setProjectId(projectId);
                pp.setPersonId(personId);
                pp.setRole("成员");
                pp.setInvolvementRate(100);
                personProjectMapper.insert(pp);
            }
        }
    }

    /**
     * 保存自定义里程碑
     *
     * @param projectId 项目ID
     * @param milestones 自定义里程碑列表
     * @param username 操作人
     */
    private void saveCustomMilestones(Long projectId, List<SysProjectMilestoneCustom> milestones, String username)
    {
        // 先删除原有的里程碑
        customMilestoneMapper.deleteByProjectId(projectId);
        // 批量插入新的里程碑
        for (int i = 0; i < milestones.size(); i++)
        {
            SysProjectMilestoneCustom milestone = milestones.get(i);
            milestone.setProjectId(projectId);
            milestone.setSortOrder(i);
            milestone.setCreateBy(username);
            customMilestoneMapper.insert(milestone);
        }
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
        // 同时删除关联的重要事项、人员关联和自定义里程碑
        milestoneMapper.deleteByProjectId(id);
        personProjectMapper.deleteByProjectId(id);
        customMilestoneMapper.deleteByProjectId(id);
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
            customMilestoneMapper.deleteByProjectId(id);
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
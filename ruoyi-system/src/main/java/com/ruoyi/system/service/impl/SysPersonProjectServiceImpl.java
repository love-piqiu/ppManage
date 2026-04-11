package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.domain.SysPersonProject;
import com.ruoyi.system.mapper.SysPersonProjectMapper;
import com.ruoyi.system.service.ISysPersonProjectService;

/**
 * 人员-项目关联 服务层处理
 *
 * @author ppmanage
 */
@Service
public class SysPersonProjectServiceImpl implements ISysPersonProjectService
{
    @Autowired
    private SysPersonProjectMapper personProjectMapper;

    /**
     * 查询人员参与的项目列表
     *
     * @param personId 人员ID
     * @return 项目关联列表
     */
    @Override
    public List<SysPersonProject> selectByPersonId(Long personId)
    {
        return personProjectMapper.selectByPersonId(personId);
    }

    /**
     * 查询项目的参与人员列表
     *
     * @param projectId 项目ID
     * @return 人员关联列表
     */
    @Override
    public List<SysPersonProject> selectByProjectId(Long projectId)
    {
        return personProjectMapper.selectByProjectId(projectId);
    }

    /**
     * 新增人员-项目关联
     *
     * @param personProject 关联信息
     * @return 结果
     */
    @Override
    public int insert(SysPersonProject personProject)
    {
        return personProjectMapper.insert(personProject);
    }

    /**
     * 删除人员-项目关联
     *
     * @param id 关联ID
     * @return 结果
     */
    @Override
    public int deleteById(Long id)
    {
        return personProjectMapper.deleteById(id);
    }

    /**
     * 根据人员ID删除关联
     *
     * @param personId 人员ID
     * @return 结果
     */
    @Override
    public int deleteByPersonId(Long personId)
    {
        return personProjectMapper.deleteByPersonId(personId);
    }

    /**
     * 根据项目ID删除关联
     *
     * @param projectId 项目ID
     * @return 结果
     */
    @Override
    public int deleteByProjectId(Long projectId)
    {
        return personProjectMapper.deleteByProjectId(projectId);
    }

    /**
     * 统计人员参与的项目数量
     *
     * @param personId 人员ID
     * @return 数量
     */
    @Override
    public int countByPersonId(Long personId)
    {
        return personProjectMapper.countByPersonId(personId);
    }
}
package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.SysPersonProject;

/**
 * 人员-项目关联 数据层
 *
 * @author ppmanage
 */
public interface SysPersonProjectMapper
{
    /**
     * 查询人员参与的项目列表
     *
     * @param personId 人员ID
     * @return 项目关联列表
     */
    public List<SysPersonProject> selectByPersonId(Long personId);

    /**
     * 查询项目的参与人员列表
     *
     * @param projectId 项目ID
     * @return 人员关联列表
     */
    public List<SysPersonProject> selectByProjectId(Long projectId);

    /**
     * 新增人员-项目关联
     *
     * @param personProject 关联信息
     * @return 结果
     */
    public int insert(SysPersonProject personProject);

    /**
     * 删除人员-项目关联
     *
     * @param id 关联ID
     * @return 结果
     */
    public int deleteById(Long id);

    /**
     * 根据人员ID删除关联
     *
     * @param personId 人员ID
     * @return 结果
     */
    public int deleteByPersonId(Long personId);

    /**
     * 根据项目ID删除关联
     *
     * @param projectId 项目ID
     * @return 结果
     */
    public int deleteByProjectId(Long projectId);

    /**
     * 统计人员参与的项目数量
     *
     * @param personId 人员ID
     * @return 数量
     */
    public int countByPersonId(Long personId);
}
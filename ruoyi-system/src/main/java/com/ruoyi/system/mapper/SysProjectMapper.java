package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.SysProject;

/**
 * 项目 数据层
 *
 * @author ppmanage
 */
public interface SysProjectMapper
{
    /**
     * 查询项目列表
     *
     * @param project 项目信息
     * @return 项目列表
     */
    public List<SysProject> selectProjectList(SysProject project);

    /**
     * 查询所有项目
     *
     * @return 项目列表
     */
    public List<SysProject> selectProjectAll();

    /**
     * 通过项目ID查询项目信息
     *
     * @param id 项目ID
     * @return 项目信息
     */
    public SysProject selectProjectById(Long id);

    /**
     * 通过项目名称查询项目信息
     *
     * @param name 项目名称
     * @return 项目信息
     */
    public SysProject selectProjectByName(String name);

    /**
     * 新增项目
     *
     * @param project 项目信息
     * @return 结果
     */
    public int insertProject(SysProject project);

    /**
     * 修改项目
     *
     * @param project 项目信息
     * @return 结果
     */
    public int updateProject(SysProject project);

    /**
     * 删除项目
     *
     * @param id 项目ID
     * @return 结果
     */
    public int deleteProjectById(Long id);

    /**
     * 批量删除项目
     *
     * @param ids 需要删除的项目ID
     * @return 结果
     */
    public int deleteProjectByIds(Long[] ids);

    /**
     * 校验项目名称是否唯一
     *
     * @param name 项目名称
     * @return 项目信息
     */
    public SysProject checkNameUnique(String name);

    /**
     * 查询进行中的项目数量
     *
     * @return 数量
     */
    public int countActiveProject();

    /**
     * 查询已完成的项目数量
     *
     * @return 数量
     */
    public int countCompletedProject();
}
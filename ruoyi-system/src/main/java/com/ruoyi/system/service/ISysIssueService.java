package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.SysIssue;

/**
 * 问题 服务层
 *
 * @author ppmanage
 */
public interface ISysIssueService
{
    /**
     * 查询问题列表
     *
     * @param issue 问题信息
     * @return 问题列表
     */
    public List<SysIssue> selectIssueList(SysIssue issue);

    /**
     * 通过问题ID查询问题信息
     *
     * @param id 问题ID
     * @return 问题信息
     */
    public SysIssue selectIssueById(Long id);

    /**
     * 查询项目的未解决问题列表
     *
     * @param projectId 项目ID
     * @return 问题列表
     */
    public List<SysIssue> selectUnresolvedByProjectId(Long projectId);

    /**
     * 新增问题
     *
     * @param issue 问题信息
     * @return 结果
     */
    public int insertIssue(SysIssue issue);

    /**
     * 修改问题
     *
     * @param issue 问题信息
     * @return 结果
     */
    public int updateIssue(SysIssue issue);

    /**
     * 更新问题状态
     *
     * @param issue 问题信息
     * @return 结果
     */
    public int updateStatus(SysIssue issue);

    /**
     * 删除问题
     *
     * @param id 问题ID
     * @return 结果
     */
    public int deleteIssueById(Long id);

    /**
     * 批量删除问题
     *
     * @param ids 需要删除的问题ID
     * @return 结果
     */
    public int deleteIssueByIds(Long[] ids);

    /**
     * 统计未解决问题数量
     *
     * @return 数量
     */
    public int countUnresolvedIssue();

    /**
     * 统计高严重程度问题数量
     *
     * @return 数量
     */
    public int countHighSeverityIssue();
}
package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.domain.SysIssue;
import com.ruoyi.system.mapper.SysIssueMapper;
import com.ruoyi.system.service.ISysIssueService;

/**
 * 问题 服务层处理
 *
 * @author ppmanage
 */
@Service
public class SysIssueServiceImpl implements ISysIssueService
{
    @Autowired
    private SysIssueMapper issueMapper;

    /**
     * 查询问题列表
     *
     * @param issue 问题信息
     * @return 问题列表
     */
    @Override
    public List<SysIssue> selectIssueList(SysIssue issue)
    {
        return issueMapper.selectIssueList(issue);
    }

    /**
     * 通过问题ID查询问题信息
     *
     * @param id 问题ID
     * @return 问题信息
     */
    @Override
    public SysIssue selectIssueById(Long id)
    {
        return issueMapper.selectIssueById(id);
    }

    /**
     * 查询项目的未解决问题列表
     *
     * @param projectId 项目ID
     * @return 问题列表
     */
    @Override
    public List<SysIssue> selectUnresolvedByProjectId(Long projectId)
    {
        return issueMapper.selectUnresolvedByProjectId(projectId);
    }

    /**
     * 新增问题
     *
     * @param issue 问题信息
     * @return 结果
     */
    @Override
    public int insertIssue(SysIssue issue)
    {
        if (issue.getStatus() == null || issue.getStatus().isEmpty())
        {
            issue.setStatus("待处理");
        }
        return issueMapper.insertIssue(issue);
    }

    /**
     * 修改问题
     *
     * @param issue 问题信息
     * @return 结果
     */
    @Override
    public int updateIssue(SysIssue issue)
    {
        return issueMapper.updateIssue(issue);
    }

    /**
     * 更新问题状态
     *
     * @param issue 问题信息
     * @return 结果
     */
    @Override
    public int updateStatus(SysIssue issue)
    {
        return issueMapper.updateIssue(issue);
    }

    /**
     * 删除问题
     *
     * @param id 问题ID
     * @return 结果
     */
    @Override
    public int deleteIssueById(Long id)
    {
        return issueMapper.deleteIssueById(id);
    }

    /**
     * 批量删除问题
     *
     * @param ids 需要删除的问题ID
     * @return 结果
     */
    @Override
    public int deleteIssueByIds(Long[] ids)
    {
        return issueMapper.deleteIssueByIds(ids);
    }

    /**
     * 统计未解决问题数量
     *
     * @return 数量
     */
    @Override
    public int countUnresolvedIssue()
    {
        return issueMapper.countUnresolvedIssue();
    }

    /**
     * 统计高严重程度问题数量
     *
     * @return 数量
     */
    @Override
    public int countHighSeverityIssue()
    {
        return issueMapper.countHighSeverityIssue();
    }
}
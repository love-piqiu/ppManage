package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.domain.SysRisk;
import com.ruoyi.system.mapper.SysRiskMapper;
import com.ruoyi.system.service.ISysRiskService;

/**
 * 风险 服务层处理
 *
 * @author ppmanage
 */
@Service
public class SysRiskServiceImpl implements ISysRiskService
{
    @Autowired
    private SysRiskMapper riskMapper;

    /**
     * 查询风险列表
     *
     * @param risk 风险信息
     * @return 风险列表
     */
    @Override
    public List<SysRisk> selectRiskList(SysRisk risk)
    {
        return riskMapper.selectRiskList(risk);
    }

    /**
     * 通过风险ID查询风险信息
     *
     * @param id 风险ID
     * @return 风险信息
     */
    @Override
    public SysRisk selectRiskById(Long id)
    {
        return riskMapper.selectRiskById(id);
    }

    /**
     * 查询项目的未消除风险列表
     *
     * @param projectId 项目ID
     * @return 风险列表
     */
    @Override
    public List<SysRisk> selectActiveByProjectId(Long projectId)
    {
        return riskMapper.selectActiveByProjectId(projectId);
    }

    /**
     * 新增风险
     *
     * @param risk 风险信息
     * @return 结果
     */
    @Override
    public int insertRisk(SysRisk risk)
    {
        if (risk.getStatus() == null || risk.getStatus().isEmpty())
        {
            risk.setStatus("潜在");
        }
        return riskMapper.insertRisk(risk);
    }

    /**
     * 修改风险
     *
     * @param risk 风险信息
     * @return 结果
     */
    @Override
    public int updateRisk(SysRisk risk)
    {
        return riskMapper.updateRisk(risk);
    }

    /**
     * 更新风险状态
     *
     * @param risk 风险信息
     * @return 结果
     */
    @Override
    public int updateStatus(SysRisk risk)
    {
        return riskMapper.updateRisk(risk);
    }

    /**
     * 删除风险
     *
     * @param id 风险ID
     * @return 结果
     */
    @Override
    public int deleteRiskById(Long id)
    {
        return riskMapper.deleteRiskById(id);
    }

    /**
     * 批量删除风险
     *
     * @param ids 需要删除的风险ID
     * @return 结果
     */
    @Override
    public int deleteRiskByIds(Long[] ids)
    {
        return riskMapper.deleteRiskByIds(ids);
    }

    /**
     * 统计未消除风险数量
     *
     * @return 数量
     */
    @Override
    public int countActiveRisk()
    {
        return riskMapper.countActiveRisk();
    }

    /**
     * 统计高风险数量
     *
     * @return 数量
     */
    @Override
    public int countHighLevelRisk()
    {
        return riskMapper.countHighLevelRisk();
    }
}
package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.SysRisk;

/**
 * 风险 服务层
 *
 * @author ppmanage
 */
public interface ISysRiskService
{
    /**
     * 查询风险列表
     *
     * @param risk 风险信息
     * @return 风险列表
     */
    public List<SysRisk> selectRiskList(SysRisk risk);

    /**
     * 通过风险ID查询风险信息
     *
     * @param id 风险ID
     * @return 风险信息
     */
    public SysRisk selectRiskById(Long id);

    /**
     * 查询项目的未消除风险列表
     *
     * @param projectId 项目ID
     * @return 风险列表
     */
    public List<SysRisk> selectActiveByProjectId(Long projectId);

    /**
     * 新增风险
     *
     * @param risk 风险信息
     * @return 结果
     */
    public int insertRisk(SysRisk risk);

    /**
     * 修改风险
     *
     * @param risk 风险信息
     * @return 结果
     */
    public int updateRisk(SysRisk risk);

    /**
     * 更新风险状态
     *
     * @param risk 风险信息
     * @return 结果
     */
    public int updateStatus(SysRisk risk);

    /**
     * 删除风险
     *
     * @param id 风险ID
     * @return 结果
     */
    public int deleteRiskById(Long id);

    /**
     * 批量删除风险
     *
     * @param ids 需要删除的风险ID
     * @return 结果
     */
    public int deleteRiskByIds(Long[] ids);

    /**
     * 统计未消除风险数量
     *
     * @return 数量
     */
    public int countActiveRisk();

    /**
     * 统计高风险数量
     *
     * @return 数量
     */
    public int countHighLevelRisk();
}
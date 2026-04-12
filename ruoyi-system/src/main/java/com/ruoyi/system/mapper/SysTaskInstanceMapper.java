package com.ruoyi.system.mapper;

import java.util.List;
import java.util.Map;
import com.ruoyi.system.domain.SysTaskInstance;

/**
 * 任务实例 数据层
 *
 * @author ppmanage
 */
public interface SysTaskInstanceMapper
{
    /**
     * 查询任务实例列表
     *
     * @param instance 任务实例信息
     * @return 任务实例列表
     */
    public List<SysTaskInstance> selectInstanceList(SysTaskInstance instance);

    /**
     * 通过ID查询任务实例
     *
     * @param id 实例ID
     * @return 任务实例
     */
    public SysTaskInstance selectInstanceById(Long id);

    /**
     * 查询某人某周期的任务实例
     *
     * @param params 查询参数
     * @return 任务实例列表
     */
    public List<SysTaskInstance> selectByPersonAndPeriod(Map<String, Object> params);

    /**
     * 新增任务实例
     *
     * @param instance 任务实例信息
     * @return 结果
     */
    public int insertInstance(SysTaskInstance instance);

    /**
     * 更新任务实例
     *
     * @param instance 任务实例信息
     * @return 结果
     */
    public int updateInstance(SysTaskInstance instance);

    /**
     * 完成任务
     *
     * @param instance 任务实例信息
     * @return 结果
     */
    public int completeTask(SysTaskInstance instance);

    /**
     * 删除任务实例
     *
     * @param id 实例ID
     * @return 结果
     */
    public int deleteInstanceById(Long id);

    /**
     * 根据任务ID删除实例
     *
     * @param taskId 任务ID
     * @return 结果
     */
    public int deleteInstanceByTaskId(Long taskId);

    /**
     * 统计某人某周期的任务完成率
     *
     * @param params 查询参数
     * @return 统计结果
     */
    public Map<String, Object> countCompletionRate(Map<String, Object> params);

    /**
     * 统计本周团队完成率
     *
     * @param period 周期
     * @return 统计结果
     */
    public Map<String, Object> countTeamCompletionRate(String period);

    /**
     * 查询某周期的所有任务实例
     *
     * @param period 周期
     * @return 任务实例列表
     */
    public List<SysTaskInstance> selectByPeriod(String period);
}
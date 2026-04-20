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
     * 取消完成任务
     *
     * @param instance 任务实例信息
     * @return 结果
     */
    public int uncompleteTask(SysTaskInstance instance);

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
     * 统计团队完成率（带日期范围和直属下级过滤）
     *
     * @param params 包含 period, startDate, endDate
     * @return 统计结果
     */
    public Map<String, Object> countTeamCompletionRateWithRange(Map<String, Object> params);

    /**
     * 查询某周期的所有任务实例
     *
     * @param period 周期
     * @return 任务实例列表
     */
    public List<SysTaskInstance> selectByPeriod(String period);

    /**
     * 查询某日期范围的所有任务实例（用于每日任务）
     *
     * @param params 包含 startDate 和 endDate
     * @return 任务实例列表
     */
    public List<SysTaskInstance> selectByPeriodRange(Map<String, Object> params);

    /**
     * 查询所有一次性任务的实例
     *
     * @return 任务实例列表
     */
    public List<SysTaskInstance> selectOnceInstances();

    /**
     * 查询某日期范围内的一次性任务实例
     *
     * @param params 包含 startDate 和 endDate
     * @return 任务实例列表
     */
    public List<SysTaskInstance> selectOnceInstancesByRange(Map<String, Object> params);

    /**
     * 统计一次性任务完成情况
     *
     * @param params 包含 startDate 和 endDate
     * @return 统计结果
     */
    public Map<String, Object> countOnceTaskStats(Map<String, Object> params);

    /**
     * 统计周期性任务完成情况（带日期范围和直属下级过滤）
     *
     * @param params 包含 period, startDate, endDate
     * @return 统计结果
     */
    public Map<String, Object> countCycleTaskStats(Map<String, Object> params);

    /**
     * 查询某人某任务某周期的实例
     *
     * @param params 包含 taskId, personId, period
     * @return 任务实例列表
     */
    public List<SysTaskInstance> selectByTaskPersonPeriod(Map<String, Object> params);

    /**
     * 查询某人某任务某日期范围的实例
     *
     * @param params 包含 taskId, personId, startDate, endDate
     * @return 任务实例列表
     */
    public List<SysTaskInstance> selectByTaskPersonPeriodRange(Map<String, Object> params);

    /**
     * 查询人员完成率排名列表
     *
     * @param params 包含 period, startDate, endDate
     * @return 人员完成率列表
     */
    public List<Map<String, Object>> selectPersonCompletionList(Map<String, Object> params);
}
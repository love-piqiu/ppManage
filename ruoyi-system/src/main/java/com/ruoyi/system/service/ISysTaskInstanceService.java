package com.ruoyi.system.service;

import java.util.List;
import java.util.Map;
import com.ruoyi.system.domain.SysTaskInstance;

/**
 * 任务实例 服务层
 *
 * @author ppmanage
 */
public interface ISysTaskInstanceService
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
     * @param personId 人员ID
     * @param period 周期
     * @return 任务实例列表
     */
    public List<SysTaskInstance> selectByPersonAndPeriod(Long personId, String period);

    /**
     * 新增任务实例
     *
     * @param instance 任务实例信息
     * @return 结果
     */
    public int insertInstance(SysTaskInstance instance);

    /**
     * 完成任务
     *
     * @param id 实例ID
     * @param remark 备注
     * @param username 操作人
     * @return 结果
     */
    public int completeTask(Long id, String remark, String username);

    /**
     * 删除任务实例
     *
     * @param id 实例ID
     * @return 结果
     */
    public int deleteInstanceById(Long id);

    /**
     * 统计某人某周期的任务完成率
     *
     * @param personId 人员ID
     * @param period 周期
     * @return 完成率(百分比)
     */
    public int getCompletionRate(Long personId, String period);

    /**
     * 统计团队某周期的任务完成率
     *
     * @param period 周期
     * @return 完成率(百分比)
     */
    public int getTeamCompletionRate(String period);

    /**
     * 获取人员完成统计列表
     *
     * @param period 周期
     * @return 统计列表
     */
    public List<Map<String, Object>> getPersonCompletionList(String period);
}
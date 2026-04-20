package com.ruoyi.system.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.domain.SysTaskInstance;
import com.ruoyi.system.mapper.SysTaskInstanceMapper;
import com.ruoyi.system.service.ISysTaskInstanceService;

/**
 * 任务实例 服务层处理
 *
 * @author ppmanage
 */
@Service
public class SysTaskInstanceServiceImpl implements ISysTaskInstanceService
{
    @Autowired
    private SysTaskInstanceMapper instanceMapper;

    /**
     * 查询任务实例列表
     *
     * @param instance 任务实例信息
     * @return 任务实例列表
     */
    @Override
    public List<SysTaskInstance> selectInstanceList(SysTaskInstance instance)
    {
        return instanceMapper.selectInstanceList(instance);
    }

    /**
     * 通过ID查询任务实例
     *
     * @param id 实例ID
     * @return 任务实例
     */
    @Override
    public SysTaskInstance selectInstanceById(Long id)
    {
        return instanceMapper.selectInstanceById(id);
    }

    /**
     * 查询某人某周期的任务实例
     *
     * @param personId 人员ID
     * @param period 周期
     * @return 任务实例列表
     */
    @Override
    public List<SysTaskInstance> selectByPersonAndPeriod(Long personId, String period)
    {
        Map<String, Object> params = new HashMap<>();
        params.put("personId", personId);
        params.put("period", period);
        return instanceMapper.selectByPersonAndPeriod(params);
    }

    /**
     * 新增任务实例
     *
     * @param instance 任务实例信息
     * @return 结果
     */
    @Override
    public int insertInstance(SysTaskInstance instance)
    {
        if (instance.getCompleted() == null)
        {
            instance.setCompleted(0);
        }
        return instanceMapper.insertInstance(instance);
    }

    /**
     * 完成任务
     *
     * @param id 实例ID
     * @param remark 备注
     * @param username 操作人
     * @return 结果
     */
    @Override
    public int completeTask(Long id, String remark, String username)
    {
        SysTaskInstance instance = new SysTaskInstance();
        instance.setId(id);
        instance.setCompleted(1);
        Date now = new Date();
        instance.setCompleteTime(now);

        // 查询实例判断是否按时
        SysTaskInstance existing = instanceMapper.selectInstanceById(id);
        if (existing != null && existing.getDeadline() != null)
        {
            instance.setOnTime(now.before(existing.getDeadline()) || now.equals(existing.getDeadline()) ? 1 : 0);
        }
        else
        {
            instance.setOnTime(1);
        }

        instance.setRemark(remark);
        instance.setUpdateBy(username);
        return instanceMapper.completeTask(instance);
    }

    /**
     * 取消完成任务
     *
     * @param id 实例ID
     * @param username 操作人
     * @return 结果
     */
    @Override
    public int uncompleteTask(Long id, String username)
    {
        SysTaskInstance instance = new SysTaskInstance();
        instance.setId(id);
        instance.setCompleted(0);
        instance.setCompleteTime(null);
        instance.setOnTime(null);
        instance.setUpdateBy(username);
        return instanceMapper.uncompleteTask(instance);
    }

    /**
     * 删除任务实例
     *
     * @param id 实例ID
     * @return 结果
     */
    @Override
    public int deleteInstanceById(Long id)
    {
        return instanceMapper.deleteInstanceById(id);
    }

    /**
     * 统计某人某周期的任务完成率
     *
     * @param personId 人员ID
     * @param period 周期
     * @return 完成率(百分比)
     */
    @Override
    public int getCompletionRate(Long personId, String period)
    {
        Map<String, Object> params = new HashMap<>();
        params.put("personId", personId);
        params.put("period", period);
        Map<String, Object> result = instanceMapper.countCompletionRate(params);
        if (result == null || result.get("total") == null)
        {
            return 0;
        }
        long total = ((Number) result.get("total")).longValue();
        if (total == 0)
        {
            return 0;
        }
        long completed = result.get("completed_count") != null ? ((Number) result.get("completed_count")).longValue() : 0;
        return (int) (completed * 100 / total);
    }

    /**
     * 统计团队某周期的任务完成率
     *
     * @param period 周期
     * @return 完成率(百分比)
     */
    @Override
    public int getTeamCompletionRate(String period)
    {
        return getTeamCompletionRate(period, null, null);
    }

    /**
     * 统计团队任务完成率（带日期范围和直属下级过滤）
     *
     * @param period 周期
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 完成率(百分比)
     */
    @Override
    public int getTeamCompletionRate(String period, String startDate, String endDate)
    {
        Map<String, Object> params = new HashMap<>();
        params.put("period", period);
        params.put("startDate", startDate != null ? startDate : "");
        params.put("endDate", endDate != null ? endDate : "");
        Map<String, Object> result = instanceMapper.countTeamCompletionRateWithRange(params);
        if (result == null || result.get("total") == null)
        {
            return 0;
        }
        long total = ((Number) result.get("total")).longValue();
        if (total == 0)
        {
            return 0;
        }
        long completed = result.get("completed_count") != null ? ((Number) result.get("completed_count")).longValue() : 0;
        return (int) (completed * 100 / total);
    }

    /**
     * 获取人员完成统计列表
     *
     * @param period 周期
     * @return 统计列表
     */
    @Override
    public List<Map<String, Object>> getPersonCompletionList(String period)
    {
        return getPersonCompletionList(period, null, null);
    }

    /**
     * 获取人员完成统计列表（带日期范围）
     *
     * @param period 周期
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 统计列表
     */
    @Override
    public List<Map<String, Object>> getPersonCompletionList(String period, String startDate, String endDate)
    {
        Map<String, Object> params = new HashMap<>();
        params.put("period", period);
        params.put("startDate", startDate != null ? startDate : "");
        params.put("endDate", endDate != null ? endDate : "");
        return instanceMapper.selectPersonCompletionList(params);
    }

    /**
     * 查询某周期的所有任务实例
     *
     * @param period 周期
     * @return 任务实例列表
     */
    @Override
    public List<SysTaskInstance> selectByPeriod(String period)
    {
        return instanceMapper.selectByPeriod(period);
    }

    /**
     * 查询某日期范围的所有任务实例（用于每日任务）
     *
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 任务实例列表
     */
    @Override
    public List<SysTaskInstance> selectByPeriodRange(String startDate, String endDate)
    {
        Map<String, Object> params = new HashMap<>();
        params.put("startDate", startDate);
        params.put("endDate", endDate);
        return instanceMapper.selectByPeriodRange(params);
    }

    /**
     * 查询所有一次性任务的实例
     *
     * @return 任务实例列表
     */
    @Override
    public List<SysTaskInstance> selectOnceInstances()
    {
        return instanceMapper.selectOnceInstances();
    }

    /**
     * 查询某日期范围内的一次性任务实例
     *
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 任务实例列表
     */
    @Override
    public List<SysTaskInstance> selectOnceInstancesByRange(String startDate, String endDate)
    {
        Map<String, Object> params = new HashMap<>();
        params.put("startDate", startDate);
        params.put("endDate", endDate);
        return instanceMapper.selectOnceInstancesByRange(params);
    }

    /**
     * 统计周期性任务完成情况
     *
     * @param period 周期
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 统计结果（包含total, completedCount, rate）
     */
    @Override
    public Map<String, Object> getCycleTaskStats(String period, String startDate, String endDate)
    {
        Map<String, Object> params = new HashMap<>();
        params.put("period", period);
        params.put("startDate", startDate != null ? startDate : "");
        params.put("endDate", endDate != null ? endDate : "");
        Map<String, Object> result = instanceMapper.countCycleTaskStats(params);
        Map<String, Object> stats = new HashMap<>();
        if (result != null && result.get("total") != null)
        {
            long total = ((Number) result.get("total")).longValue();
            long completed = result.get("completedCount") != null ? ((Number) result.get("completedCount")).longValue() : 0;
            stats.put("total", total);
            stats.put("completedCount", completed);
            stats.put("rate", total > 0 ? (int) (completed * 100 / total) : 0);
        }
        else
        {
            stats.put("total", 0);
            stats.put("completedCount", 0);
            stats.put("rate", 0);
        }
        return stats;
    }

    /**
     * 统计一次性任务完成情况
     *
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 统计结果（包含total, completedCount, rate）
     */
    @Override
    public Map<String, Object> getOnceTaskStats(String startDate, String endDate)
    {
        Map<String, Object> params = new HashMap<>();
        params.put("startDate", startDate);
        params.put("endDate", endDate);
        Map<String, Object> result = instanceMapper.countOnceTaskStats(params);
        Map<String, Object> stats = new HashMap<>();
        if (result != null && result.get("total") != null)
        {
            long total = ((Number) result.get("total")).longValue();
            long completed = result.get("completedCount") != null ? ((Number) result.get("completedCount")).longValue() : 0;
            stats.put("total", total);
            stats.put("completedCount", completed);
            stats.put("rate", total > 0 ? (int) (completed * 100 / total) : 0);
        }
        else
        {
            stats.put("total", 0);
            stats.put("completedCount", 0);
            stats.put("rate", 0);
        }
        return stats;
    }

    /**
     * 查询某人某任务某周期的实例
     *
     * @param taskId 任务ID
     * @param personId 人员ID
     * @param period 周期
     * @return 任务实例列表
     */
    @Override
    public List<SysTaskInstance> selectByTaskPersonPeriod(Long taskId, Long personId, String period)
    {
        Map<String, Object> params = new HashMap<>();
        params.put("taskId", taskId);
        params.put("personId", personId);
        params.put("period", period);
        return instanceMapper.selectByTaskPersonPeriod(params);
    }

    /**
     * 查询某人某任务某日期范围的实例
     *
     * @param taskId 任务ID
     * @param personId 人员ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 任务实例列表
     */
    @Override
    public List<SysTaskInstance> selectByTaskPersonPeriodRange(Long taskId, Long personId, String startDate, String endDate)
    {
        Map<String, Object> params = new HashMap<>();
        params.put("taskId", taskId);
        params.put("personId", personId);
        params.put("startDate", startDate);
        params.put("endDate", endDate);
        return instanceMapper.selectByTaskPersonPeriodRange(params);
    }
}
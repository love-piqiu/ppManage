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
        Map<String, Object> result = instanceMapper.countTeamCompletionRate(period);
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
        // 此方法需要复杂查询，暂时返回空列表
        // 后续可以添加分组统计SQL
        return List.of();
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
}
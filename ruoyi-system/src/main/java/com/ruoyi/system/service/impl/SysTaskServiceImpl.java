package com.ruoyi.system.service.impl;

import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.domain.SysTask;
import com.ruoyi.system.mapper.SysTaskMapper;
import com.ruoyi.system.mapper.SysTaskInstanceMapper;
import com.ruoyi.system.service.ISysTaskService;

/**
 * 任务 服务层处理
 *
 * @author ppmanage
 */
@Service
public class SysTaskServiceImpl implements ISysTaskService
{
    @Autowired
    private SysTaskMapper taskMapper;

    @Autowired
    private SysTaskInstanceMapper instanceMapper;

    /**
     * 查询任务列表
     *
     * @param task 任务信息
     * @return 任务列表
     */
    @Override
    public List<SysTask> selectTaskList(SysTask task)
    {
        return taskMapper.selectTaskList(task);
    }

    /**
     * 查询所有启用的任务
     *
     * @return 任务列表
     */
    @Override
    public List<SysTask> selectActiveTaskAll()
    {
        return taskMapper.selectActiveTaskAll();
    }

    /**
     * 通过任务ID查询任务信息
     *
     * @param id 任务ID
     * @return 任务信息
     */
    @Override
    public SysTask selectTaskById(Long id)
    {
        return taskMapper.selectTaskById(id);
    }

    /**
     * 新增任务
     *
     * @param task 任务信息
     * @return 结果
     */
    @Override
    public int insertTask(SysTask task)
    {
        if (task.getStatus() == null || task.getStatus().isEmpty())
        {
            task.setStatus("启用");
        }
        return taskMapper.insertTask(task);
    }

    /**
     * 修改任务
     *
     * @param task 任务信息
     * @return 结果
     */
    @Override
    public int updateTask(SysTask task)
    {
        return taskMapper.updateTask(task);
    }

    /**
     * 删除任务
     *
     * @param id 任务ID
     * @return 结果
     */
    @Override
    public int deleteTaskById(Long id)
    {
        instanceMapper.deleteInstanceByTaskId(id);
        return taskMapper.deleteTaskById(id);
    }

    /**
     * 批量删除任务
     *
     * @param ids 需要删除的任务ID
     * @return 结果
     */
    @Override
    public int deleteTaskByIds(Long[] ids)
    {
        for (Long id : ids)
        {
            instanceMapper.deleteInstanceByTaskId(id);
        }
        return taskMapper.deleteTaskByIds(ids);
    }
}
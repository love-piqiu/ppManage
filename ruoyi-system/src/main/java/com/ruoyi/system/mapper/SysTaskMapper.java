package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.SysTask;

/**
 * 任务 数据层
 *
 * @author ppmanage
 */
public interface SysTaskMapper
{
    /**
     * 查询任务列表
     *
     * @param task 任务信息
     * @return 任务列表
     */
    public List<SysTask> selectTaskList(SysTask task);

    /**
     * 查询所有启用的任务
     *
     * @return 任务列表
     */
    public List<SysTask> selectActiveTaskAll();

    /**
     * 通过任务ID查询任务信息
     *
     * @param id 任务ID
     * @return 任务信息
     */
    public SysTask selectTaskById(Long id);

    /**
     * 新增任务
     *
     * @param task 任务信息
     * @return 结果
     */
    public int insertTask(SysTask task);

    /**
     * 修改任务
     *
     * @param task 任务信息
     * @return 结果
     */
    public int updateTask(SysTask task);

    /**
     * 删除任务
     *
     * @param id 任务ID
     * @return 结果
     */
    public int deleteTaskById(Long id);

    /**
     * 批量删除任务
     *
     * @param ids 需要删除的任务ID
     * @return 结果
     */
    public int deleteTaskByIds(Long[] ids);
}
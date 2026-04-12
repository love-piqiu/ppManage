package com.ruoyi.web.controller.system;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.system.domain.SysTask;
import com.ruoyi.system.domain.SysTaskInstance;
import com.ruoyi.system.service.ISysTaskService;
import com.ruoyi.system.service.ISysTaskInstanceService;

/**
 * 任务信息操作处理
 *
 * @author ppmanage
 */
@RestController
@RequestMapping("/system/task")
public class SysTaskController extends BaseController
{
    @Autowired
    private ISysTaskService taskService;

    @Autowired
    private ISysTaskInstanceService instanceService;

    /**
     * 获取任务列表
     */
    @PreAuthorize("@ss.hasPermi('system:task:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysTask task)
    {
        startPage();
        List<SysTask> list = taskService.selectTaskList(task);
        return getDataTable(list);
    }

    @Log(title = "任务管理", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('system:task:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysTask task)
    {
        List<SysTask> list = taskService.selectTaskList(task);
        ExcelUtil<SysTask> util = new ExcelUtil<SysTask>(SysTask.class);
        util.exportExcel(response, list, "任务数据");
    }

    /**
     * 根据任务编号获取详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:task:query')")
    @GetMapping(value = "/{taskId}")
    public AjaxResult getInfo(@PathVariable Long taskId)
    {
        return success(taskService.selectTaskById(taskId));
    }

    /**
     * 新增任务
     */
    @PreAuthorize("@ss.hasPermi('system:task:add')")
    @Log(title = "任务管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody SysTask task)
    {
        task.setCreateBy(getUsername());
        return toAjax(taskService.insertTask(task));
    }

    /**
     * 修改任务
     */
    @PreAuthorize("@ss.hasPermi('system:task:edit')")
    @Log(title = "任务管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody SysTask task)
    {
        task.setUpdateBy(getUsername());
        return toAjax(taskService.updateTask(task));
    }

    /**
     * 删除任务
     */
    @PreAuthorize("@ss.hasPermi('system:task:remove')")
    @Log(title = "任务管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{taskIds}")
    public AjaxResult remove(@PathVariable Long[] taskIds)
    {
        return toAjax(taskService.deleteTaskByIds(taskIds));
    }

    /**
     * 获取任务实例列表
     */
    @PreAuthorize("@ss.hasPermi('system:task:query')")
    @GetMapping("/instance/list")
    public TableDataInfo instanceList(SysTaskInstance instance)
    {
        startPage();
        List<SysTaskInstance> list = instanceService.selectInstanceList(instance);
        return getDataTable(list);
    }

    /**
     * 获取某人某周期的任务实例
     */
    @PreAuthorize("@ss.hasPermi('system:task:query')")
    @GetMapping("/instance/person/{personId}/{period}")
    public AjaxResult getPersonInstances(@PathVariable Long personId, @PathVariable String period)
    {
        List<SysTaskInstance> list = instanceService.selectByPersonAndPeriod(personId, period);
        return success(list);
    }

    /**
     * 新增任务实例
     */
    @PreAuthorize("@ss.hasPermi('system:task:edit')")
    @Log(title = "任务管理", businessType = BusinessType.INSERT)
    @PostMapping("/instance")
    public AjaxResult addInstance(@Validated @RequestBody SysTaskInstance instance)
    {
        instance.setCreateBy(getUsername());
        return toAjax(instanceService.insertInstance(instance));
    }

    /**
     * 完成任务
     */
    @PreAuthorize("@ss.hasPermi('system:task:edit')")
    @Log(title = "任务管理", businessType = BusinessType.UPDATE)
    @PutMapping("/instance/complete/{id}")
    public AjaxResult completeTask(@PathVariable Long id, @RequestParam(required = false) String remark)
    {
        return toAjax(instanceService.completeTask(id, remark, getUsername()));
    }

    /**
     * 删除任务实例
     */
    @PreAuthorize("@ss.hasPermi('system:task:edit')")
    @Log(title = "任务管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/instance/{id}")
    public AjaxResult removeInstance(@PathVariable Long id)
    {
        return toAjax(instanceService.deleteInstanceById(id));
    }

    /**
     * 获取某人某周期的完成率
     */
    @PreAuthorize("@ss.hasPermi('system:task:query')")
    @GetMapping("/statistics/rate/{personId}/{period}")
    public AjaxResult getCompletionRate(@PathVariable Long personId, @PathVariable String period)
    {
        int rate = instanceService.getCompletionRate(personId, period);
        return success(rate);
    }

    /**
     * 获取团队某周期的完成率
     */
    @PreAuthorize("@ss.hasPermi('system:task:query')")
    @GetMapping("/statistics/team/{period}")
    public AjaxResult getTeamCompletionRate(@PathVariable String period)
    {
        int rate = instanceService.getTeamCompletionRate(period);
        return success(rate);
    }

    /**
     * 获取某周期的完整统计数据（包括所有任务和人员的实例）
     */
    @PreAuthorize("@ss.hasPermi('system:task:query')")
    @GetMapping("/statistics/period/{period}")
    public AjaxResult getPeriodStatistics(@PathVariable String period)
    {
        Map<String, Object> result = new HashMap<>();
        // 获取该周期所有实例
        List<SysTaskInstance> instances = instanceService.selectByPeriod(period);
        // 计算统计数据
        int total = instances.size();
        int completed = (int) instances.stream().filter(i -> i.getCompleted() == 1).count();
        int pending = (int) instances.stream().filter(i -> i.getCompleted() == 0 && !isOverdue(i.getDeadline())).count();
        int overdue = (int) instances.stream().filter(i -> i.getCompleted() == 0 && isOverdue(i.getDeadline())).count();
        int rate = total > 0 ? completed * 100 / total : 0;

        result.put("total", total);
        result.put("completed", completed);
        result.put("pending", pending);
        result.put("overdue", overdue);
        result.put("rate", rate);
        result.put("instances", instances);
        return success(result);
    }

    private boolean isOverdue(java.util.Date deadline)
    {
        if (deadline == null) return false;
        return deadline.before(new java.util.Date());
    }
}
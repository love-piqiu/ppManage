package com.ruoyi.web.controller.system;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Date;
import java.util.Calendar;
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
import com.ruoyi.system.domain.SysPerson;
import com.ruoyi.system.service.ISysTaskService;
import com.ruoyi.system.service.ISysTaskInstanceService;
import com.ruoyi.system.service.ISysPersonService;

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

    @Autowired
    private ISysPersonService personService;

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
        int result = instanceService.insertInstance(instance);
        if (result > 0)
        {
            return success(instance.getId()); // 返回新创建的 ID
        }
        return error();
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
     * 取消完成任务
     */
    @PreAuthorize("@ss.hasPermi('system:task:edit')")
    @Log(title = "任务管理", businessType = BusinessType.UPDATE)
    @PutMapping("/instance/uncomplete/{id}")
    public AjaxResult uncompleteTask(@PathVariable Long id)
    {
        return toAjax(instanceService.uncompleteTask(id, getUsername()));
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

        // 获取所有直属下级人员
        List<SysPerson> persons = personService.selectPersonAll();

        // 获取所有启用的周期性任务
        List<SysTask> cycleTasks = taskService.selectActiveTaskAll();
        cycleTasks = cycleTasks.stream().filter(t -> "周期性".equals(t.getType())).toList();

        // 计算日期范围
        String[] dateRange = getWeekDateRange(period);
        Date now = new Date();

        // 统计数据
        int totalExpected = 0;
        int totalCompleted = 0;
        int totalOverdue = 0;

        // 获取所有实例
        List<SysTaskInstance> weeklyMonthlyInstances = instanceService.selectByPeriod(period);
        List<SysTaskInstance> dailyInstances = instanceService.selectByPeriodRange(dateRange[0], dateRange[1]);
        List<SysTaskInstance> allInstances = new java.util.ArrayList<>();
        allInstances.addAll(weeklyMonthlyInstances);
        allInstances.addAll(dailyInstances);

        // 按任务和人员逐个统计
        for (SysTask task : cycleTasks) {
            String cycle = task.getCycle();

            // 季度任务只在季度末周统计
            if ("每季".equals(cycle) && !isQuarterEndWeek(dateRange[0], dateRange[1])) {
                continue;
            }

            // 计算截止时间列表
            List<Date> deadlines = calculateTaskDeadlines(period, cycle, task, dateRange);

            // 计算需要统计的天数列表（每日/每工作日需要按天统计）
            List<String> daysToCheck = getDaysToCheck(cycle, dateRange);

            for (SysPerson person : persons) {
                // 对于每日/每工作日任务，按天统计
                if ("每日".equals(cycle) || "每工作日".equals(cycle)) {
                    for (String day : daysToCheck) {
                        totalExpected++;

                        // 查找该天该人员的完成实例
                        boolean dayCompleted = allInstances.stream()
                            .filter(i -> i.getTaskId().equals(task.getId())
                                && i.getPersonId().equals(person.getId())
                                && i.getPeriod().equals(day)
                                && i.getCompleted() == 1)
                            .findFirst()
                            .isPresent();

                        if (dayCompleted) {
                            totalCompleted++;
                        } else {
                            // 检查该天的截止时间是否已过
                            Date dayDeadline = getDayDeadline(day, task.getDeadlineTime());
                            if (dayDeadline != null && now.after(dayDeadline)) {
                                totalOverdue++;
                            }
                        }
                    }
                } else {
                    // 每周/每月/每季任务，按周期统计一次
                    totalExpected++;

                    // 查找该周期该人员的完成实例
                    boolean isCompleted = allInstances.stream()
                        .filter(i -> i.getTaskId().equals(task.getId())
                            && i.getPersonId().equals(person.getId())
                            && i.getPeriod().equals(period)
                            && i.getCompleted() == 1)
                        .findFirst()
                        .isPresent();

                    if (isCompleted) {
                        totalCompleted++;
                    } else {
                        // 检查截止时间是否已过
                        for (Date deadline : deadlines) {
                            if (now.after(deadline)) {
                                totalOverdue++;
                                break;
                            }
                        }
                    }
                }
            }
        }

        // 待完成 = 预期总数 - 已完成 - 已超期
        int totalPending = totalExpected - totalCompleted - totalOverdue;
        if (totalPending < 0) totalPending = 0;

        int rate = totalExpected > 0 ? Math.min(totalCompleted * 100 / totalExpected, 100) : 0;

        result.put("total", totalExpected);
        result.put("completed", Math.min(totalCompleted, totalExpected));
        result.put("pending", totalPending);
        result.put("overdue", totalOverdue);
        result.put("rate", rate);
        result.put("instances", allInstances);

        // 计算每个人员的统计数据
        List<Map<String, Object>> personStats = new java.util.ArrayList<>();
        for (SysPerson person : persons) {
            int personExpected = 0;
            int personCompleted = 0;
            int personOverdue = 0;

            for (SysTask task : cycleTasks) {
                String cycle = task.getCycle();
                List<Date> deadlines = calculateTaskDeadlines(period, cycle, task, dateRange);
                List<String> daysToCheck = getDaysToCheck(cycle, dateRange);

                if ("每日".equals(cycle) || "每工作日".equals(cycle)) {
                    for (String day : daysToCheck) {
                        personExpected++;

                        boolean dayCompleted = allInstances.stream()
                            .filter(i -> i.getTaskId().equals(task.getId())
                                && i.getPersonId().equals(person.getId())
                                && i.getPeriod().equals(day)
                                && i.getCompleted() == 1)
                            .findFirst()
                            .isPresent();

                        if (dayCompleted) {
                            personCompleted++;
                        } else {
                            Date dayDeadline = getDayDeadline(day, task.getDeadlineTime());
                            if (dayDeadline != null && now.after(dayDeadline)) {
                                personOverdue++;
                            }
                        }
                    }
                } else {
                    personExpected++;

                    boolean isCompleted = allInstances.stream()
                        .filter(i -> i.getTaskId().equals(task.getId())
                            && i.getPersonId().equals(person.getId())
                            && i.getPeriod().equals(period)
                            && i.getCompleted() == 1)
                        .findFirst()
                        .isPresent();

                    if (isCompleted) {
                        personCompleted++;
                    } else {
                        for (Date deadline : deadlines) {
                            if (now.after(deadline)) {
                                personOverdue++;
                                break;
                            }
                        }
                    }
                }
            }

            int personPending = personExpected - personCompleted - personOverdue;
            if (personPending < 0) personPending = 0;

            int personRate = personExpected > 0 ? personCompleted * 100 / personExpected : 0;

            Map<String, Object> stat = new HashMap<>();
            stat.put("personId", person.getId());
            stat.put("personName", person.getName());
            stat.put("expected", personExpected);
            stat.put("completed", personCompleted);
            stat.put("pending", personPending);
            stat.put("overdue", personOverdue);
            stat.put("rate", personRate);
            personStats.add(stat);
        }
        result.put("personStats", personStats);

        return success(result);
    }

    /**
     * 获取需要统计的天数列表
     */
    private List<String> getDaysToCheck(String cycle, String[] dateRange)
    {
        List<String> days = new java.util.ArrayList<>();
        try {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
            java.util.Calendar startCal = java.util.Calendar.getInstance();
            startCal.setTime(java.sql.Date.valueOf(dateRange[0]));
            java.util.Calendar endCal = java.util.Calendar.getInstance();
            endCal.setTime(java.sql.Date.valueOf(dateRange[1]));

            while (!startCal.after(endCal)) {
                int dayOfWeek = startCal.get(java.util.Calendar.DAY_OF_WEEK);
                if ("每日".equals(cycle)) {
                    // 每日：包含所有7天
                    days.add(sdf.format(startCal.getTime()));
                } else if ("每工作日".equals(cycle)) {
                    // 每工作日：周一到周五
                    if (dayOfWeek >= java.util.Calendar.MONDAY && dayOfWeek <= java.util.Calendar.FRIDAY) {
                        days.add(sdf.format(startCal.getTime()));
                    }
                }
                startCal.add(java.util.Calendar.DAY_OF_MONTH, 1);
            }
        } catch (Exception e) {
            // 解析失败
        }
        return days;
    }

    /**
     * 获取某天的截止时间
     */
    private Date getDayDeadline(String day, String deadlineTime)
    {
        try {
            String timeStr = deadlineTime != null ? deadlineTime : "23:59:59";
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return sdf.parse(day + " " + timeStr);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取每人预期任务数
     */
    private int getExpectedCountPerPerson(String cycle)
    {
        if ("每日".equals(cycle)) {
            return 7;
        } else if ("每工作日".equals(cycle)) {
            return 5;
        } else if ("每周".equals(cycle) || "每月".equals(cycle) || "每季".equals(cycle)) {
            return 1;
        }
        return 0;
    }

    /**
     * 计算任务截止时间列表（用于每周/每月/每季任务）
     */
    private List<Date> calculateTaskDeadlines(String period, String cycle, SysTask task, String[] dateRange)
    {
        List<Date> deadlines = new java.util.ArrayList<>();
        String timeStr = task.getDeadlineTime() != null ? task.getDeadlineTime() : "23:59:59";

        try {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            if ("每周".equals(cycle)) {
                String weekday = task.getDeadlineWeekday();
                if (weekday != null) {
                    java.util.Calendar cal = java.util.Calendar.getInstance();
                    cal.setMinimalDaysInFirstWeek(4);
                    cal.setFirstDayOfWeek(java.util.Calendar.MONDAY);
                    String[] parts = period.split("-W");
                    int year = Integer.parseInt(parts[0]);
                    int weekNum = Integer.parseInt(parts[1]);
                    cal.set(java.util.Calendar.YEAR, year);
                    cal.set(java.util.Calendar.WEEK_OF_YEAR, weekNum);
                    cal.set(java.util.Calendar.DAY_OF_WEEK, convertWeekday(weekday));
                    String dateStr = new java.text.SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());
                    Date deadline = sdf.parse(dateStr + " " + timeStr);
                    deadlines.add(deadline);
                }
            } else if ("每月".equals(cycle)) {
                Integer day = task.getDeadlineDay();
                if (day != null) {
                    java.util.Calendar cal = java.util.Calendar.getInstance();
                    int year = cal.get(java.util.Calendar.YEAR);
                    int month = cal.get(java.util.Calendar.MONTH) + 1;
                    String dateStr = String.format("%04d-%02d-%02d", year, month, Math.min(day, 28));
                    Date deadline = sdf.parse(dateStr + " " + timeStr);
                    deadlines.add(deadline);
                }
            } else if ("每季".equals(cycle)) {
                // 自动计算当前季度的最后一天
                java.util.Calendar cal = java.util.Calendar.getInstance();
                int year = cal.get(java.util.Calendar.YEAR);
                int month = cal.get(java.util.Calendar.MONTH) + 1;
                int quarter = (month - 1) / 3 + 1; // 当前季度
                int endMonth = quarter * 3; // 季度末月
                // 获取该月最后一天
                cal.set(java.util.Calendar.YEAR, year);
                cal.set(java.util.Calendar.MONTH, endMonth - 1); // 月份从0开始
                int lastDay = cal.getActualMaximum(java.util.Calendar.DAY_OF_MONTH);
                String dateStr = String.format("%04d-%02d-%02d", year, endMonth, lastDay);
                Date deadline = sdf.parse(dateStr + " " + timeStr);
                deadlines.add(deadline);
            }
        } catch (Exception e) {
            // 解析失败
        }

        return deadlines;
    }

    /**
     * 转换星期中文到Calendar常量
     */
    private int convertWeekday(String weekday)
    {
        switch (weekday) {
            case "一": return java.util.Calendar.MONDAY;
            case "二": return java.util.Calendar.TUESDAY;
            case "三": return java.util.Calendar.WEDNESDAY;
            case "四": return java.util.Calendar.THURSDAY;
            case "五": return java.util.Calendar.FRIDAY;
            case "六": return java.util.Calendar.SATURDAY;
            case "日": return java.util.Calendar.SUNDAY;
            default: return java.util.Calendar.FRIDAY;
        }
    }

    /**
     * 获取一次性任务的统计数据（不依赖周期）
     */
    @PreAuthorize("@ss.hasPermi('system:task:query')")
    @GetMapping("/statistics/once")
    public AjaxResult getOnceTaskStatistics()
    {
        Map<String, Object> result = new HashMap<>();

        // 获取所有直属下级人员
        List<SysPerson> persons = personService.selectPersonAll();

        // 获取一次性任务的所有实例
        List<SysTaskInstance> onceInstances = instanceService.selectOnceInstances();

        // 计算统计数据
        int total = onceInstances.size();
        int completed = (int) onceInstances.stream().filter(i -> i.getCompleted() == 1).count();
        int pending = (int) onceInstances.stream().filter(i -> i.getCompleted() == 0 && !isOverdue(i.getDeadline())).count();
        int overdue = (int) onceInstances.stream().filter(i -> i.getCompleted() == 0 && isOverdue(i.getDeadline())).count();
        int rate = total > 0 ? completed * 100 / total : 0;

        result.put("total", total);
        result.put("completed", completed);
        result.put("pending", pending);
        result.put("overdue", overdue);
        result.put("rate", rate);
        result.put("instances", onceInstances);

        // 计算每个人员的统计数据
        List<Map<String, Object>> personStats = new java.util.ArrayList<>();
        for (SysPerson person : persons) {
            Map<String, Object> stat = new HashMap<>();
            stat.put("personId", person.getId());
            stat.put("personName", person.getName());

            // 该人员的一次性任务实例
            List<SysTaskInstance> personInstances = onceInstances.stream()
                .filter(i -> i.getPersonId().equals(person.getId()))
                .toList();

            int personTotal = personInstances.size();
            int personCompleted = (int) personInstances.stream().filter(i -> i.getCompleted() == 1).count();
            int personPending = (int) personInstances.stream().filter(i -> i.getCompleted() == 0 && !isOverdue(i.getDeadline())).count();
            int personOverdue = (int) personInstances.stream().filter(i -> i.getCompleted() == 0 && isOverdue(i.getDeadline())).count();
            int personRate = personTotal > 0 ? personCompleted * 100 / personTotal : 0;

            stat.put("total", personTotal);
            stat.put("completed", personCompleted);
            stat.put("pending", personPending);
            stat.put("overdue", personOverdue);
            stat.put("rate", personRate);
            personStats.add(stat);
        }
        result.put("personStats", personStats);

        return success(result);
    }

    /**
     * 根据周期标识计算日期范围（周一到周日）
     * @param period 格式：2026-W16
     * @return [startDate, endDate] 格式：yyyy-MM-dd
     */
    private String[] getWeekDateRange(String period)
    {
        String[] parts = period.split("-W");
        int year = Integer.parseInt(parts[0]);
        int weekNum = Integer.parseInt(parts[1]);

        // ISO周计算：找到该年第1周的周一
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.setMinimalDaysInFirstWeek(4);
        cal.setFirstDayOfWeek(java.util.Calendar.MONDAY);
        cal.set(java.util.Calendar.YEAR, year);
        cal.set(java.util.Calendar.WEEK_OF_YEAR, weekNum);
        cal.set(java.util.Calendar.DAY_OF_WEEK, java.util.Calendar.MONDAY);

        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
        String startDate = sdf.format(cal.getTime());

        cal.add(java.util.Calendar.DAY_OF_MONTH, 6);
        String endDate = sdf.format(cal.getTime());

        return new String[]{startDate, endDate};
    }

    private boolean isOverdue(java.util.Date deadline)
    {
        if (deadline == null) return false;
        return deadline.before(new java.util.Date());
    }

    /**
     * 判断当前周是否是季度末周（包含季度最后一天）
     * @param startDate 周开始日期
     * @param endDate 周结束日期
     * @return 是否是季度末周
     */
    private boolean isQuarterEndWeek(String startDate, String endDate)
    {
        try {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
            java.util.Date start = sdf.parse(startDate);
            java.util.Date end = sdf.parse(endDate);

            java.util.Calendar cal = java.util.Calendar.getInstance();
            int year = cal.get(java.util.Calendar.YEAR);

            // 检查四个季度的最后一天是否在该周范围内
            for (int quarter = 1; quarter <= 4; quarter++) {
                int endMonth = quarter * 3;
                cal.set(java.util.Calendar.YEAR, year);
                cal.set(java.util.Calendar.MONTH, endMonth - 1);
                int lastDay = cal.getActualMaximum(java.util.Calendar.DAY_OF_MONTH);
                cal.set(java.util.Calendar.DAY_OF_MONTH, lastDay);
                java.util.Date quarterLastDay = cal.getTime();

                if (!quarterLastDay.before(start) && !quarterLastDay.after(end)) {
                    return true;
                }
            }

            return false;
        } catch (Exception e) {
            return false;
        }
    }
}
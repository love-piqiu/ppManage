package com.ruoyi.quartz.task;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.ruoyi.system.domain.SysPerson;
import com.ruoyi.system.domain.SysTask;
import com.ruoyi.system.domain.SysTaskInstance;
import com.ruoyi.system.service.ISysPersonService;
import com.ruoyi.system.service.ISysTaskInstanceService;
import com.ruoyi.system.service.ISysTaskService;

/**
 * 任务实例生成定时任务
 * 每周一自动为周期性任务生成任务实例
 *
 * @author ppmanage
 */
@Component("taskInstanceTask")
public class TaskInstanceTask
{
    private static final Logger log = LoggerFactory.getLogger(TaskInstanceTask.class);

    @Autowired
    private ISysTaskService taskService;

    @Autowired
    private ISysPersonService personService;

    @Autowired
    private ISysTaskInstanceService instanceService;

    /**
     * 生成周期性任务实例
     * 每周一执行，为所有启用的周期性任务和在职人员生成当周的任务实例
     */
    public void generateWeeklyInstances()
    {
        log.info("开始生成周期性任务实例...");

        // 获取当前周期标识（如：2026-W16）
        String period = getCurrentWeekPeriod();
        log.info("当前周期: {}", period);

        // 获取所有启用的周期性任务
        List<SysTask> tasks = taskService.selectActiveTaskAll();
        int taskCount = 0;
        for (SysTask task : tasks)
        {
            if ("周期性".equals(task.getType()) && "每周".equals(task.getCycle()))
            {
                taskCount++;
            }
        }
        log.info("找到 {} 个启用的每周任务", taskCount);

        // 获取所有在职人员
        List<SysPerson> persons = personService.selectPersonAll();
        int personCount = 0;
        for (SysPerson person : persons)
        {
            if ("在职".equals(person.getStatus()))
            {
                personCount++;
            }
        }
        log.info("找到 {} 个在职人员", personCount);

        // 为每个任务和人员生成实例
        int generated = 0;
        for (SysTask task : tasks)
        {
            if (!"周期性".equals(task.getType()) || !"每周".equals(task.getCycle()))
            {
                continue;
            }

            for (SysPerson person : persons)
            {
                if (!"在职".equals(person.getStatus()))
                {
                    continue;
                }

                // 检查是否已存在该周期的实例
                List<SysTaskInstance> existing = instanceService.selectByPersonAndPeriod(person.getId(), period);
                boolean alreadyExists = existing.stream().anyMatch(i -> i.getTaskId().equals(task.getId()));

                if (!alreadyExists)
                {
                    // 创建新实例
                    SysTaskInstance instance = new SysTaskInstance();
                    instance.setTaskId(task.getId());
                    instance.setTaskName(task.getName());
                    instance.setPersonId(person.getId());
                    instance.setPersonName(person.getName());
                    instance.setPeriod(period);
                    instance.setDeadline(calculateDeadline(task));
                    instance.setCompleted(0);
                    instance.setCreateBy("system");

                    instanceService.insertInstance(instance);
                    generated++;
                }
            }
        }

        log.info("任务实例生成完成，共生成 {} 条实例", generated);
    }

    /**
     * 获取当前周期标识
     * 格式：YYYY-WXX（如：2026-W16）
     */
    private String getCurrentWeekPeriod()
    {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int week = cal.get(Calendar.WEEK_OF_YEAR);
        return String.format("%d-W%d", year, week);
    }

    /**
     * 根据任务配置计算截止时间
     */
    private Date calculateDeadline(SysTask task)
    {
        Calendar cal = Calendar.getInstance();

        // 设置截止日期（本周的某一天）
        String weekday = task.getDeadlineWeekday();
        if (weekday != null && !weekday.isEmpty())
        {
            int targetDay = convertWeekdayToCalendar(weekday);
            // Calendar.DAY_OF_WEEK: 周日=1, 周一=2, ..., 周六=7
            int currentDay = cal.get(Calendar.DAY_OF_WEEK);
            int diff = targetDay - currentDay;
            if (diff < 0)
            {
                diff += 7; // 下周
            }
            cal.add(Calendar.DAY_OF_MONTH, diff);
        }

        // 设置截止时间点
        String timeStr = task.getDeadlineTime();
        if (timeStr != null && !timeStr.isEmpty())
        {
            String[] parts = timeStr.split(":");
            if (parts.length >= 2)
            {
                cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(parts[0]));
                cal.set(Calendar.MINUTE, Integer.parseInt(parts[1]));
                cal.set(Calendar.SECOND, 0);
            }
        }
        else
        {
            // 默认周五18:00
            cal.set(Calendar.HOUR_OF_DAY, 18);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
        }

        return cal.getTime();
    }

    /**
     * 将中文星期转换为Calendar常量
     */
    private int convertWeekdayToCalendar(String weekday)
    {
        switch (weekday)
        {
            case "一": return Calendar.MONDAY;
            case "二": return Calendar.TUESDAY;
            case "三": return Calendar.WEDNESDAY;
            case "四": return Calendar.THURSDAY;
            case "五": return Calendar.FRIDAY;
            case "六": return Calendar.SATURDAY;
            case "日": return Calendar.SUNDAY;
            default: return Calendar.FRIDAY;
        }
    }
}
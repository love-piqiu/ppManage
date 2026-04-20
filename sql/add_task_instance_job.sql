-- 添加任务实例自动生成定时任务
-- 每周一凌晨0点5分执行，为所有周期性任务生成实例

INSERT INTO sys_job (job_name, job_group, invoke_target, cron_expression, misfire_policy, concurrent, status, create_by, create_time, remark)
VALUES ('生成周期任务实例', 'DEFAULT', 'taskInstanceTask.generateWeeklyInstances()', '0 5 0 ? * MON', '2', '1', '0', 'admin', NOW(), '每周一自动生成周期性任务实例');

-- 字段说明：
-- job_name: 任务名称
-- job_group: 任务组（DEFAULT为默认组）
-- invoke_target: 调用目标（Spring Bean方法）
-- cron_expression: Cron表达式（0 5 0 ? * MON 表示每周一0:05执行）
-- misfire_policy: 错过执行策略（2=立即执行）
-- concurrent: 是否并发执行（1=允许）
-- status: 状态（0=正常，1=暂停）
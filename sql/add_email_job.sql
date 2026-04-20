-- 添加邮件自动发送定时任务
-- 每分钟执行一次检查，判断是否需要发送邮件
-- status: 0=暂停, 1=正常(启用)

-- 先删除已存在的任务（如果有的话）
DELETE FROM sys_job WHERE job_name = '邮件自动发送检查';

-- 插入新任务，状态为启用(1)
INSERT INTO sys_job (job_name, job_group, invoke_target, cron_expression, misfire_policy, concurrent, status, create_by, create_time, remark)
VALUES ('邮件自动发送检查', 'DEFAULT', 'emailTask.checkAndSendEmail()', '0 * * * * ?', '3', '1', '1', 'admin', sysdate(), '每分钟检查邮件配置，在指定时间自动发送周报');
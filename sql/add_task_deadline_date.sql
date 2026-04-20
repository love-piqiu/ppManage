-- 添加一次性任务截止日期字段
ALTER TABLE sys_task ADD COLUMN deadline_date datetime DEFAULT NULL COMMENT '一次性任务截止日期';
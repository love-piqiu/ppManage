-- 资源管理迭代：添加手动设置状态字段
-- 执行时间: 2026-04-17

-- 添加资源状态相关字段到 sys_person 表
ALTER TABLE sys_person ADD COLUMN resource_status VARCHAR(20) DEFAULT NULL COMMENT '资源状态(空闲/即将空闲/忙碌/待定)';
ALTER TABLE sys_person ADD COLUMN resource_status_remark VARCHAR(500) DEFAULT NULL COMMENT '资源状态说明';
ALTER TABLE sys_person ADD COLUMN next_project VARCHAR(100) DEFAULT NULL COMMENT '下一个预计参与项目';

-- 验证字段添加成功
SELECT COLUMN_NAME, COLUMN_TYPE, COLUMN_COMMENT FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_SCHEMA = 'ppmanage' AND TABLE_NAME = 'sys_person'
AND COLUMN_NAME IN ('resource_status', 'resource_status_remark', 'next_project');
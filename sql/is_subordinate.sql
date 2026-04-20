-- 添加是否下辖字段
ALTER TABLE sys_project ADD COLUMN is_subordinate VARCHAR(10) DEFAULT '是' COMMENT '是否下辖(是/否)';
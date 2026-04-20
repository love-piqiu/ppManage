-- 添加 contract_amount 字段到 sys_project 表
ALTER TABLE sys_project ADD COLUMN contract_amount DECIMAL(10,2) COMMENT '合同金额(元)' AFTER actual_end_date;
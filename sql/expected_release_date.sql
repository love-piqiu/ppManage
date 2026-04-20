-- 添加预计释放日期字段
ALTER TABLE sys_person ADD COLUMN expected_release_date DATE DEFAULT NULL;
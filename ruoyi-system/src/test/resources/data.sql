-- 测试任务数据
INSERT INTO sys_task (id, name, type, cycle, status, del_flag) VALUES
(1, '日报提交', '周期性', '每日', '启用', '0'),
(2, '周报提交', '周期性', '每周', '启用', '0'),
(3, '月报提交', '周期性', '每月', '启用', '0'),
(4, '季度汇报', '周期性', '每季', '启用', '0'),
(5, '项目验收', '一次性', NULL, '启用', '0');

-- 测试人员数据
INSERT INTO sys_person (id, name, status, del_flag) VALUES
(1, '张三', '在职', '0'),
(2, '李四', '在职', '0'),
(3, '王五', '离职', '0');

-- 测试任务实例数据
INSERT INTO sys_task_instance (id, task_id, person_id, period, completed, on_time, deadline) VALUES
(1, 1, 1, '2026-04-21', 1, 1, '2026-04-21 18:00:00'),
(2, 2, 1, '2026-W16', 0, NULL, '2026-04-25 18:00:00'),
(3, 1, 2, '2026-04-21', 0, NULL, '2026-04-21 18:00:00');

-- 测试项目数据
INSERT INTO sys_project (id, name, customer, pm_id, pm_name, status, is_subordinate, project_type, del_flag) VALUES
(1, '测试项目一', '客户A', 1, '张三', '进行中', '是', '项目', '0'),
(2, '测试项目二', '客户B', 2, '李四', '已完成', '是', '项目', '0'),
(3, '外包项目', '客户C', NULL, NULL, '进行中', '否', '外包', '0');

-- 测试问题数据
INSERT INTO sys_issue (id, project_id, description, severity, status, del_flag) VALUES
(1, 1, '测试问题一', '高', '待处理', '0'),
(2, 1, '测试问题二', '中', '处理中', '0');

-- 测试风险数据
INSERT INTO sys_risk (id, project_id, description, level, status, del_flag) VALUES
(1, 1, '测试风险一', '高', '待处理', '0'),
(2, 2, '测试风险二', '低', '已消除', '0');

-- 测试用户数据
INSERT INTO sys_user (user_id, user_name, nick_name, password, status, del_flag) VALUES
(1, 'admin', '管理员', 'admin123', '0', '0'),
(2, 'test', '测试用户', 'test123', '0', '0');
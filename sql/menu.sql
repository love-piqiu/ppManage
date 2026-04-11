-- ppManage 菜单配置
-- 分步执行，避免子查询问题

-- 先清空可能存在的旧菜单（如果需要重新导入）
-- DELETE FROM sys_menu WHERE perms LIKE 'system:project:%' OR perms LIKE 'system:issue:%' OR perms LIKE 'system:risk:%' OR perms LIKE 'system:person:%' OR perms LIKE 'system:task:%' OR perms LIKE 'report:%' OR perms LIKE 'system:email:%' OR perms LIKE 'dashboard:%';

-- 1. 首页看板
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES (2000, '首页看板', 0, 1, 'dashboard', 'dashboard/index', 1, 0, 'C', '0', '0', 'dashboard:view', 'monitor', 'admin', NOW(), '首页看板');

-- 2. 项目管理
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES (2001, '项目管理', 0, 2, 'project', 'system/project/index', 1, 0, 'C', '0', '0', 'system:project:list', 'peoples', 'admin', NOW(), '项目管理菜单');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time) VALUES
(2002, '项目查询', 2001, 1, '', '', 1, 0, 'F', '0', '0', 'system:project:query', '#', 'admin', NOW()),
(2003, '项目新增', 2001, 2, '', '', 1, 0, 'F', '0', '0', 'system:project:add', '#', 'admin', NOW()),
(2004, '项目修改', 2001, 3, '', '', 1, 0, 'F', '0', '0', 'system:project:edit', '#', 'admin', NOW()),
(2005, '项目删除', 2001, 4, '', '', 1, 0, 'F', '0', '0', 'system:project:remove', '#', 'admin', NOW()),
(2006, '项目导出', 2001, 5, '', '', 1, 0, 'F', '0', '0', 'system:project:export', '#', 'admin', NOW());

-- 3. 问题跟踪
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES (2010, '问题跟踪', 0, 3, 'issue', 'system/issue/index', 1, 0, 'C', '0', '0', 'system:issue:list', 'bug', 'admin', NOW(), '问题跟踪菜单');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time) VALUES
(2011, '问题查询', 2010, 1, '', '', 1, 0, 'F', '0', '0', 'system:issue:query', '#', 'admin', NOW()),
(2012, '问题新增', 2010, 2, '', '', 1, 0, 'F', '0', '0', 'system:issue:add', '#', 'admin', NOW()),
(2013, '问题修改', 2010, 3, '', '', 1, 0, 'F', '0', '0', 'system:issue:edit', '#', 'admin', NOW()),
(2014, '问题删除', 2010, 4, '', '', 1, 0, 'F', '0', '0', 'system:issue:remove', '#', 'admin', NOW());

-- 4. 风险管理
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES (2020, '风险管理', 0, 4, 'risk', 'system/risk/index', 1, 0, 'C', '0', '0', 'system:risk:list', 'chart', 'admin', NOW(), '风险管理菜单');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time) VALUES
(2021, '风险查询', 2020, 1, '', '', 1, 0, 'F', '0', '0', 'system:risk:query', '#', 'admin', NOW()),
(2022, '风险新增', 2020, 2, '', '', 1, 0, 'F', '0', '0', 'system:risk:add', '#', 'admin', NOW()),
(2023, '风险修改', 2020, 3, '', '', 1, 0, 'F', '0', '0', 'system:risk:edit', '#', 'admin', NOW()),
(2024, '风险删除', 2020, 4, '', '', 1, 0, 'F', '0', '0', 'system:risk:remove', '#', 'admin', NOW()),
(2025, '风险导出', 2020, 5, '', '', 1, 0, 'F', '0', '0', 'system:risk:export', '#', 'admin', NOW());

-- 5. 人员管理
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES (2030, '人员管理', 0, 5, 'person', 'system/person/index', 1, 0, 'C', '0', '0', 'system:person:list', 'user', 'admin', NOW(), '人员管理菜单');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time) VALUES
(2031, '人员查询', 2030, 1, '', '', 1, 0, 'F', '0', '0', 'system:person:query', '#', 'admin', NOW()),
(2032, '人员新增', 2030, 2, '', '', 1, 0, 'F', '0', '0', 'system:person:add', '#', 'admin', NOW()),
(2033, '人员修改', 2030, 3, '', '', 1, 0, 'F', '0', '0', 'system:person:edit', '#', 'admin', NOW()),
(2034, '人员删除', 2030, 4, '', '', 1, 0, 'F', '0', '0', 'system:person:remove', '#', 'admin', NOW()),
(2035, '人员导出', 2030, 5, '', '', 1, 0, 'F', '0', '0', 'system:person:export', '#', 'admin', NOW());

-- 6. 任务管理
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES (2040, '任务管理', 0, 6, 'task', 'system/task/index', 1, 0, 'C', '0', '0', 'system:task:list', 'checkbox', 'admin', NOW(), '任务管理菜单');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time) VALUES
(2041, '任务查询', 2040, 1, '', '', 1, 0, 'F', '0', '0', 'system:task:query', '#', 'admin', NOW()),
(2042, '任务新增', 2040, 2, '', '', 1, 0, 'F', '0', '0', 'system:task:add', '#', 'admin', NOW()),
(2043, '任务修改', 2040, 3, '', '', 1, 0, 'F', '0', '0', 'system:task:edit', '#', 'admin', NOW()),
(2044, '任务删除', 2040, 4, '', '', 1, 0, 'F', '0', '0', 'system:task:remove', '#', 'admin', NOW());

-- 7. 周报生成
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES (2050, '周报生成', 0, 7, 'report', 'report/index', 1, 0, 'C', '0', '0', 'report:view', 'documentation', 'admin', NOW(), '周报生成菜单');

-- 8. 邮件配置 (放在系统管理下，parent_id=1 是系统管理)
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES (2060, '邮件配置', 1, 99, 'email', 'system/email/index', 1, 0, 'C', '0', '0', 'system:email:edit', 'email', 'admin', NOW(), '邮件配置菜单');
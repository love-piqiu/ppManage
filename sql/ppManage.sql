-- ----------------------------
-- ppManage 业务表结构
-- 项目及人员管理系统
-- ----------------------------

-- ----------------------------
-- 1、人员表
-- ----------------------------
DROP TABLE IF EXISTS sys_person;
CREATE TABLE sys_person (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '人员ID',
  name VARCHAR(50) NOT NULL COMMENT '姓名',
  position VARCHAR(50) COMMENT '职位',
  level VARCHAR(20) COMMENT '等级(如B20/B30/B40)',
  contact VARCHAR(100) COMMENT '联系方式',
  email VARCHAR(100) COMMENT '邮箱',
  age INT COMMENT '年龄',
  work_years INT COMMENT '工作年限',
  company_years INT COMMENT '入司年限',
  education VARCHAR(20) COMMENT '学历',
  school VARCHAR(100) COMMENT '毕业院校',
  hometown VARCHAR(100) COMMENT '籍贯',
  entry_date DATE COMMENT '入职日期',
  status VARCHAR(20) DEFAULT '在职' COMMENT '状态:在职/离职',
  remark VARCHAR(500) COMMENT '备注',
  create_by VARCHAR(64) DEFAULT '' COMMENT '创建者',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_by VARCHAR(64) DEFAULT '' COMMENT '更新者',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  del_flag CHAR(1) DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）'
) ENGINE=InnoDB AUTO_INCREMENT=100 COMMENT = '人员表';

-- ----------------------------
-- 2、项目表
-- ----------------------------
DROP TABLE IF EXISTS sys_project;
CREATE TABLE sys_project (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '项目ID',
  name VARCHAR(100) NOT NULL COMMENT '项目名称',
  customer VARCHAR(100) COMMENT '客户',
  pm_id BIGINT COMMENT '项目经理ID',
  pm_name VARCHAR(50) COMMENT '项目经理姓名',
  sales_id BIGINT COMMENT '销售人员ID',
  sales_name VARCHAR(50) COMMENT '销售人员姓名',
  cost DECIMAL(10,2) COMMENT '成本(万)',
  cost_used DECIMAL(10,2) COMMENT '已使用成本(万)',
  work_hours INT COMMENT '总工时(人天)',
  work_hours_used INT COMMENT '已使用工时(人天)',
  progress INT DEFAULT 0 COMMENT '进度(%)',
  stage VARCHAR(50) COMMENT '当前阶段',
  status VARCHAR(20) DEFAULT '进行中' COMMENT '状态:进行中/已完成/暂停',
  start_date DATE COMMENT '开始日期',
  end_date DATE COMMENT '计划结束日期',
  actual_end_date DATE COMMENT '实际结束日期',
  remark VARCHAR(500) COMMENT '备注',
  create_by VARCHAR(64) DEFAULT '' COMMENT '创建者',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_by VARCHAR(64) DEFAULT '' COMMENT '更新者',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  del_flag CHAR(1) DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）'
) ENGINE=InnoDB AUTO_INCREMENT=100 COMMENT = '项目表';

-- ----------------------------
-- 3、项目重要事项表
-- ----------------------------
DROP TABLE IF EXISTS sys_project_milestone;
CREATE TABLE sys_project_milestone (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '事项ID',
  project_id BIGINT NOT NULL COMMENT '项目ID',
  description VARCHAR(500) NOT NULL COMMENT '描述',
  record_date DATE NOT NULL COMMENT '记录日期',
  create_by VARCHAR(64) DEFAULT '' COMMENT '创建者',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  del_flag CHAR(1) DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）'
) ENGINE=InnoDB AUTO_INCREMENT=100 COMMENT = '项目重要事项表';

-- ----------------------------
-- 4、人员-项目关联表
-- ----------------------------
DROP TABLE IF EXISTS sys_person_project;
CREATE TABLE sys_person_project (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '关联ID',
  person_id BIGINT NOT NULL COMMENT '人员ID',
  project_id BIGINT NOT NULL COMMENT '项目ID',
  role VARCHAR(50) COMMENT '角色',
  involvement_rate INT DEFAULT 100 COMMENT '投入比例(%)',
  join_date DATE COMMENT '参与时间',
  create_by VARCHAR(64) DEFAULT '' COMMENT '创建者',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  del_flag CHAR(1) DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）'
) ENGINE=InnoDB AUTO_INCREMENT=100 COMMENT = '人员-项目关联表';

-- ----------------------------
-- 5、问题表
-- ----------------------------
DROP TABLE IF EXISTS sys_issue;
CREATE TABLE sys_issue (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '问题ID',
  project_id BIGINT NOT NULL COMMENT '项目ID',
  project_name VARCHAR(100) COMMENT '项目名称(冗余)',
  description VARCHAR(500) NOT NULL COMMENT '问题描述',
  type VARCHAR(50) COMMENT '问题类型',
  severity VARCHAR(20) COMMENT '严重程度:高/中/低',
  status VARCHAR(20) DEFAULT '待处理' COMMENT '状态:待处理/进行中/已解决/已关闭',
  owner_id BIGINT COMMENT '负责人ID',
  owner_name VARCHAR(50) COMMENT '负责人姓名',
  discover_date DATE COMMENT '发现日期',
  plan_date DATE COMMENT '计划解决日期',
  actual_date DATE COMMENT '实际解决日期',
  solution VARCHAR(500) COMMENT '解决方案',
  remark VARCHAR(500) COMMENT '备注',
  create_by VARCHAR(64) DEFAULT '' COMMENT '创建者',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_by VARCHAR(64) DEFAULT '' COMMENT '更新者',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  del_flag CHAR(1) DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）'
) ENGINE=InnoDB AUTO_INCREMENT=100 COMMENT = '问题表';

-- ----------------------------
-- 6、风险表
-- ----------------------------
DROP TABLE IF EXISTS sys_risk;
CREATE TABLE sys_risk (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '风险ID',
  project_id BIGINT NOT NULL COMMENT '项目ID',
  project_name VARCHAR(100) COMMENT '项目名称(冗余)',
  description VARCHAR(500) NOT NULL COMMENT '风险描述',
  level VARCHAR(20) COMMENT '风险等级:高/中/低',
  status VARCHAR(20) DEFAULT '潜在' COMMENT '状态:潜在/已发生/已消除',
  measure VARCHAR(500) COMMENT '应对措施',
  owner_id BIGINT COMMENT '负责人ID',
  owner_name VARCHAR(50) COMMENT '负责人姓名',
  happen_date DATE COMMENT '发生日期',
  resolve_date DATE COMMENT '消除日期',
  remark VARCHAR(500) COMMENT '备注',
  create_by VARCHAR(64) DEFAULT '' COMMENT '创建者',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_by VARCHAR(64) DEFAULT '' COMMENT '更新者',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  del_flag CHAR(1) DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）'
) ENGINE=InnoDB AUTO_INCREMENT=100 COMMENT = '风险表';

-- ----------------------------
-- 7、任务表
-- ----------------------------
DROP TABLE IF EXISTS sys_task;
CREATE TABLE sys_task (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '任务ID',
  name VARCHAR(100) NOT NULL COMMENT '任务名称',
  type VARCHAR(20) NOT NULL COMMENT '类型:周期性/一次性',
  cycle VARCHAR(20) COMMENT '周期:每日/每周/每月',
  deadline_time TIME COMMENT '截止时间点(如18:00)',
  deadline_day INT COMMENT '截止日期(如每月5号)',
  deadline_weekday VARCHAR(10) COMMENT '截止星期(如周五)',
  status VARCHAR(20) DEFAULT '启用' COMMENT '状态:启用/禁用',
  description VARCHAR(500) COMMENT '描述',
  remark VARCHAR(500) COMMENT '备注',
  create_by VARCHAR(64) DEFAULT '' COMMENT '创建者',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_by VARCHAR(64) DEFAULT '' COMMENT '更新者',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  del_flag CHAR(1) DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）'
) ENGINE=InnoDB AUTO_INCREMENT=100 COMMENT = '任务表';

-- ----------------------------
-- 8、任务实例表
-- ----------------------------
DROP TABLE IF EXISTS sys_task_instance;
CREATE TABLE sys_task_instance (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '实例ID',
  task_id BIGINT NOT NULL COMMENT '任务ID',
  task_name VARCHAR(100) COMMENT '任务名称(冗余)',
  person_id BIGINT NOT NULL COMMENT '人员ID',
  person_name VARCHAR(50) COMMENT '人员姓名(冗余)',
  period VARCHAR(20) NOT NULL COMMENT '周期标识:2026-04-10/2026-W15/2026-04',
  deadline DATETIME COMMENT '截止时间',
  completed TINYINT DEFAULT 0 COMMENT '是否完成:0/1',
  complete_time DATETIME COMMENT '完成时间',
  on_time TINYINT COMMENT '是否按时:0/1',
  remark VARCHAR(500) COMMENT '备注',
  create_by VARCHAR(64) DEFAULT '' COMMENT '创建者',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_by VARCHAR(64) DEFAULT '' COMMENT '更新者',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  del_flag CHAR(1) DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）'
) ENGINE=InnoDB AUTO_INCREMENT=100 COMMENT = '任务实例表';

-- ----------------------------
-- 9、邮件配置表
-- ----------------------------
DROP TABLE IF EXISTS sys_email_config;
CREATE TABLE sys_email_config (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '配置ID',
  host VARCHAR(100) NOT NULL COMMENT 'SMTP服务器',
  port INT DEFAULT 465 COMMENT 'SMTP端口',
  username VARCHAR(100) NOT NULL COMMENT '发件人账号',
  password VARCHAR(100) NOT NULL COMMENT '发件人密码/授权码',
  recipient_email VARCHAR(500) NOT NULL COMMENT '收件人地址(多个用逗号分隔)',
  sender_name VARCHAR(50) COMMENT '发件人名称',
  email_subject VARCHAR(100) DEFAULT '项目周报 - {日期}' COMMENT '邮件主题',
  send_day VARCHAR(10) DEFAULT '周五' COMMENT '发送日期',
  send_time TIME DEFAULT '17:00:00' COMMENT '发送时间',
  enabled TINYINT DEFAULT 1 COMMENT '是否启用:0/1',
  remark VARCHAR(500) COMMENT '备注',
  create_by VARCHAR(64) DEFAULT '' COMMENT '创建者',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_by VARCHAR(64) DEFAULT '' COMMENT '更新者',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB AUTO_INCREMENT=100 COMMENT = '邮件配置表';

-- ----------------------------
-- 初始化邮件配置数据
-- ----------------------------
INSERT INTO sys_email_config (id, host, port, username, password, recipient_email, sender_name, enabled)
VALUES (1, 'smtp.qq.com', 465, 'sender@qq.com', 'your_auth_code', 'manager@company.com', '项目管理系统', 0);

-- ----------------------------
-- 初始化示例人员数据
-- ----------------------------
INSERT INTO sys_person (id, name, position, level, contact, email, age, work_years, company_years, education, school, hometown, status) VALUES
(1, '张三', '项目经理', 'B40', '13812345678', 'zhangsan@company.com', 32, 8, 3, '硕士', '清华大学', '北京市海淀区', '在职'),
(2, '李四', '开发工程师', 'B30', '13987654321', 'lisi@company.com', 28, 5, 2, '本科', '北京大学', '上海市浦东新区', '在职'),
(3, '王五', '技术总监', 'B50', '13790123456', 'wangwu@company.com', 35, 10, 5, '本科', '浙江大学', '浙江省杭州市', '在职'),
(4, '赵六', '开发工程师', 'B20', '13634567890', 'zhaoliu@company.com', 26, 3, 1, '本科', '南京大学', '江苏省南京市', '在职'),
(5, '孙七', '产品经理', 'B30', '13578901234', 'sunqi@company.com', 29, 6, 2, '硕士', '复旦大学', '四川省成都市', '在职');

-- ----------------------------
-- 初始化示例项目数据
-- ----------------------------
INSERT INTO sys_project (id, name, customer, pm_id, pm_name, cost, cost_used, work_hours, work_hours_used, progress, stage, status, start_date, end_date) VALUES
(1, '智慧园区管理系统', '科技园A区', 1, '张三', 120.00, 93.60, 200, 184, 78, '上线部署', '进行中', '2025-01-15', '2026-05-30'),
(2, '企业数据中台', '集团公司', 3, '王五', 85.00, 55.25, 150, 165, 65, 'UAT测试', '进行中', '2025-06-01', '2026-04-24'),
(3, '移动办公APP', '销售公司', 1, '张三', 60.00, 22.80, 100, 55, 38, '需求确认', '进行中', '2026-02-01', '2026-06-15'),
(4, '客户服务系统升级', '客服中心', 5, '孙七', 45.00, 42.75, 80, 70, 95, '验收交付', '已完成', '2025-10-01', '2026-04-10'),
(5, '供应链管理平台', '物流公司', 2, '李四', 200.00, 210.00, 300, 75, 25, '需求确认', '暂停', '2025-08-01', '2026-06-30');

-- ----------------------------
-- 初始化示例任务数据
-- ----------------------------
INSERT INTO sys_task (id, name, type, cycle, deadline_time, status) VALUES
(1, '每日项目状态更新', '周期性', '每日', '18:00:00', '启用'),
(2, '每日风险检查', '周期性', '每日', '09:00:00', '启用'),
(3, '每周进度汇总', '周期性', '每周', '17:00:00', '启用'),
(4, '每月成本核算', '周期性', '每月', '12:00:00', '启用');
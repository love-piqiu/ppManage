-- sys_task 表
CREATE TABLE IF NOT EXISTS sys_task (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    type VARCHAR(20) NOT NULL,
    cycle VARCHAR(20),
    deadline_time VARCHAR(10),
    deadline_day INT,
    deadline_weekday VARCHAR(10),
    deadline_date DATE,
    deadline_quarter_month VARCHAR(10),
    deadline_quarter_day INT,
    status VARCHAR(20) DEFAULT '启用',
    description VARCHAR(500),
    del_flag VARCHAR(1) DEFAULT '0',
    create_time TIMESTAMP,
    update_time TIMESTAMP,
    create_by VARCHAR(64),
    update_by VARCHAR(64)
);

-- sys_task_instance 表
CREATE TABLE IF NOT EXISTS sys_task_instance (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    task_id BIGINT NOT NULL,
    person_id BIGINT NOT NULL,
    period VARCHAR(50) NOT NULL,
    completed INT DEFAULT 0,
    complete_time TIMESTAMP,
    on_time INT,
    deadline TIMESTAMP,
    remark VARCHAR(500),
    create_time TIMESTAMP,
    update_time TIMESTAMP,
    create_by VARCHAR(64),
    update_by VARCHAR(64)
);

-- sys_person 表
CREATE TABLE IF NOT EXISTS sys_person (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    user_id BIGINT,
    status VARCHAR(20) DEFAULT '在职',
    join_date DATE,
    leave_date DATE,
    email VARCHAR(100),
    phone VARCHAR(20),
    create_time TIMESTAMP,
    update_time TIMESTAMP,
    create_by VARCHAR(64),
    update_by VARCHAR(64),
    del_flag VARCHAR(1) DEFAULT '0'
);

-- sys_project 表
CREATE TABLE IF NOT EXISTS sys_project (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    customer VARCHAR(100),
    pm_id BIGINT,
    pm_name VARCHAR(50),
    sales_id BIGINT,
    sales_name VARCHAR(50),
    cost DECIMAL(12,2),
    cost_used DECIMAL(12,2),
    work_hours INT,
    work_hours_used INT,
    progress INT,
    stage VARCHAR(100),
    status VARCHAR(20) DEFAULT '进行中',
    is_subordinate VARCHAR(10) DEFAULT '是',
    project_type VARCHAR(20) DEFAULT '项目',
    start_date DATE,
    end_date DATE,
    req_plan_date DATE,
    req_actual_date DATE,
    uat_plan_date DATE,
    uat_actual_date DATE,
    launch_plan_date DATE,
    launch_actual_date DATE,
    accept_plan_date DATE,
    accept_actual_date DATE,
    actual_end_date DATE,
    contract_amount DECIMAL(12,2),
    remark VARCHAR(500),
    del_flag VARCHAR(1) DEFAULT '0',
    create_time TIMESTAMP,
    update_time TIMESTAMP,
    create_by VARCHAR(64),
    update_by VARCHAR(64)
);

-- sys_issue 表
CREATE TABLE IF NOT EXISTS sys_issue (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    project_id BIGINT,
    description VARCHAR(500),
    severity VARCHAR(20),
    status VARCHAR(20) DEFAULT '待处理',
    owner_id BIGINT,
    owner_name VARCHAR(50),
    create_time TIMESTAMP,
    update_time TIMESTAMP,
    create_by VARCHAR(64),
    update_by VARCHAR(64),
    del_flag VARCHAR(1) DEFAULT '0'
);

-- sys_risk 表
CREATE TABLE IF NOT EXISTS sys_risk (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    project_id BIGINT,
    description VARCHAR(500),
    level VARCHAR(20),
    status VARCHAR(20) DEFAULT '待处理',
    measure VARCHAR(500),
    create_time TIMESTAMP,
    update_time TIMESTAMP,
    create_by VARCHAR(64),
    update_by VARCHAR(64),
    del_flag VARCHAR(1) DEFAULT '0'
);

-- sys_project_milestone 表
CREATE TABLE IF NOT EXISTS sys_project_milestone (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    project_id BIGINT NOT NULL,
    description VARCHAR(500) NOT NULL,
    record_date DATE NOT NULL,
    create_time TIMESTAMP,
    create_by VARCHAR(64)
);

-- sys_project_invoice 表
CREATE TABLE IF NOT EXISTS sys_project_invoice (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    project_id BIGINT NOT NULL,
    amount DECIMAL(12,2) NOT NULL,
    percent INT,
    invoice_type VARCHAR(50),
    invoice_date DATE,
    remark VARCHAR(500),
    create_time TIMESTAMP,
    create_by VARCHAR(64)
);

-- sys_person_project 表
CREATE TABLE IF NOT EXISTS sys_person_project (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    person_id BIGINT NOT NULL,
    project_id BIGINT NOT NULL,
    role VARCHAR(50),
    join_date DATE,
    leave_date DATE,
    create_time TIMESTAMP,
    create_by VARCHAR(64)
);

-- sys_user 表
CREATE TABLE IF NOT EXISTS sys_user (
    user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_name VARCHAR(50) NOT NULL,
    nick_name VARCHAR(50),
    email VARCHAR(100),
    phonenumber VARCHAR(20),
    sex VARCHAR(10),
    avatar VARCHAR(200),
    password VARCHAR(200),
    status VARCHAR(10) DEFAULT '0',
    del_flag VARCHAR(1) DEFAULT '0',
    create_time TIMESTAMP,
    update_time TIMESTAMP,
    create_by VARCHAR(64),
    update_by VARCHAR(64)
);
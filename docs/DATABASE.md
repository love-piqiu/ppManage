# 数据库设计文档

## 数据库概述

- **数据库名称：** ppmanage
- **字符集：** utf8mb4
- **排序规则：** utf8mb4_general_ci
- **存储引擎：** InnoDB

## 表结构

### 1. sys_person（人员表）

| 字段名 | 类型 | 是否必填 | 说明 |
|--------|------|----------|------|
| id | BIGINT | 是 | 主键，自增 |
| name | VARCHAR(50) | 是 | 姓名 |
| position | VARCHAR(50) | 否 | 职位 |
| level | VARCHAR(20) | 否 | 等级（如B20/B30/B40） |
| contact | VARCHAR(100) | 否 | 联系方式 |
| email | VARCHAR(100) | 否 | 邮箱 |
| age | INT | 否 | 年龄 |
| work_years | INT | 否 | 工作年限 |
| company_years | INT | 否 | 入司年限 |
| education | VARCHAR(20) | 否 | 学历 |
| school | VARCHAR(100) | 否 | 毕业院校 |
| hometown | VARCHAR(100) | 否 | 籍贯 |
| entry_date | DATE | 否 | 入职日期 |
| status | VARCHAR(20) | 否 | 状态：在职/离职 |
| remark | VARCHAR(500) | 否 | 备注 |
| create_by | VARCHAR(64) | 否 | 创建者 |
| create_time | DATETIME | 否 | 创建时间 |
| update_by | VARCHAR(64) | 否 | 更新者 |
| update_time | DATETIME | 否 | 更新时间 |
| del_flag | CHAR(1) | 否 | 删除标志 |

### 2. sys_project（项目表）

| 字段名 | 类型 | 是否必填 | 说明 |
|--------|------|----------|------|
| id | BIGINT | 是 | 主键，自增 |
| name | VARCHAR(100) | 是 | 项目名称 |
| customer | VARCHAR(100) | 否 | 客户 |
| pm_id | BIGINT | 否 | 项目经理ID |
| pm_name | VARCHAR(50) | 否 | 项目经理姓名 |
| sales_id | BIGINT | 否 | 销售人员ID |
| sales_name | VARCHAR(50) | 否 | 销售人员姓名 |
| cost | DECIMAL(10,2) | 否 | 成本（万） |
| cost_used | DECIMAL(10,2) | 否 | 已使用成本（万） |
| work_hours | INT | 否 | 总工时（人天） |
| work_hours_used | INT | 否 | 已使用工时（人天） |
| progress | INT | 否 | 进度（%） |
| stage | VARCHAR(50) | 否 | 当前阶段 |
| status | VARCHAR(20) | 否 | 状态：进行中/已完成/暂停 |
| start_date | DATE | 否 | 开始日期 |
| end_date | DATE | 否 | 计划结束日期 |
| actual_end_date | DATE | 否 | 实际结束日期 |
| remark | VARCHAR(500) | 否 | 备注 |
| create_by | VARCHAR(64) | 否 | 创建者 |
| create_time | DATETIME | 否 | 创建时间 |
| update_by | VARCHAR(64) | 否 | 更新者 |
| update_time | DATETIME | 否 | 更新时间 |
| del_flag | CHAR(1) | 否 | 删除标志 |

### 3. sys_project_milestone（项目重要事项表）

| 字段名 | 类型 | 是否必填 | 说明 |
|--------|------|----------|------|
| id | BIGINT | 是 | 主键，自增 |
| project_id | BIGINT | 是 | 项目ID |
| description | VARCHAR(500) | 是 | 描述 |
| record_date | DATE | 是 | 记录日期 |
| create_by | VARCHAR(64) | 否 | 创建者 |
| create_time | DATETIME | 否 | 创建时间 |
| del_flag | CHAR(1) | 否 | 删除标志 |

### 4. sys_person_project（人员-项目关联表）

| 字段名 | 类型 | 是否必填 | 说明 |
|--------|------|----------|------|
| id | BIGINT | 是 | 主键，自增 |
| person_id | BIGINT | 是 | 人员ID |
| project_id | BIGINT | 是 | 项目ID |
| role | VARCHAR(50) | 否 | 角色 |
| involvement_rate | INT | 否 | 投入比例（%） |
| join_date | DATE | 否 | 参与时间 |
| create_by | VARCHAR(64) | 否 | 创建者 |
| create_time | DATETIME | 否 | 创建时间 |
| del_flag | CHAR(1) | 否 | 删除标志 |

### 5. sys_issue（问题表）

| 字段名 | 类型 | 是否必填 | 说明 |
|--------|------|----------|------|
| id | BIGINT | 是 | 主键，自增 |
| project_id | BIGINT | 是 | 项目ID |
| project_name | VARCHAR(100) | 否 | 项目名称（冗余） |
| description | VARCHAR(500) | 是 | 问题描述 |
| type | VARCHAR(50) | 否 | 问题类型 |
| severity | VARCHAR(20) | 否 | 严重程度：高/中/低 |
| status | VARCHAR(20) | 否 | 状态：待处理/进行中/已解决/已关闭 |
| owner_id | BIGINT | 否 | 负责人ID |
| owner_name | VARCHAR(50) | 否 | 负责人姓名 |
| discover_date | DATE | 否 | 发现日期 |
| plan_date | DATE | 否 | 计划解决日期 |
| actual_date | DATE | 否 | 实际解决日期 |
| solution | VARCHAR(500) | 否 | 解决方案 |
| remark | VARCHAR(500) | 否 | 备注 |
| create_by | VARCHAR(64) | 否 | 创建者 |
| create_time | DATETIME | 否 | 创建时间 |
| update_by | VARCHAR(64) | 否 | 更新者 |
| update_time | DATETIME | 否 | 更新时间 |
| del_flag | CHAR(1) | 否 | 删除标志 |

### 6. sys_risk（风险表）

| 字段名 | 类型 | 是否必填 | 说明 |
|--------|------|----------|------|
| id | BIGINT | 是 | 主键，自增 |
| project_id | BIGINT | 是 | 项目ID |
| project_name | VARCHAR(100) | 否 | 项目名称（冗余） |
| description | VARCHAR(500) | 是 | 风险描述 |
| level | VARCHAR(20) | 否 | 风险等级：高/中/低 |
| status | VARCHAR(20) | 否 | 状态：潜在/已发生/已消除 |
| measure | VARCHAR(500) | 否 | 应对措施 |
| owner_id | BIGINT | 否 | 负责人ID |
| owner_name | VARCHAR(50) | 否 | 负责人姓名 |
| happen_date | DATE | 否 | 发生日期 |
| resolve_date | DATE | 否 | 消除日期 |
| remark | VARCHAR(500) | 否 | 备注 |
| create_by | VARCHAR(64) | 否 | 创建者 |
| create_time | DATETIME | 否 | 创建时间 |
| update_by | VARCHAR(64) | 否 | 更新者 |
| update_time | DATETIME | 否 | 更新时间 |
| del_flag | CHAR(1) | 否 | 删除标志 |

### 7. sys_task（任务表）

| 字段名 | 类型 | 是否必填 | 说明 |
|--------|------|----------|------|
| id | BIGINT | 是 | 主键，自增 |
| name | VARCHAR(100) | 是 | 任务名称 |
| type | VARCHAR(20) | 是 | 类型：周期性/一次性 |
| cycle | VARCHAR(20) | 否 | 周期：每日/每周/每月 |
| deadline_time | TIME | 否 | 截止时间点 |
| deadline_day | INT | 否 | 截止日期（如每月5号） |
| deadline_weekday | VARCHAR(10) | 否 | 截止星期（如周五） |
| status | VARCHAR(20) | 否 | 状态：启用/禁用 |
| description | VARCHAR(500) | 否 | 描述 |
| remark | VARCHAR(500) | 否 | 备注 |
| create_by | VARCHAR(64) | 否 | 创建者 |
| create_time | DATETIME | 否 | 创建时间 |
| update_by | VARCHAR(64) | 否 | 更新者 |
| update_time | DATETIME | 否 | 更新时间 |
| del_flag | CHAR(1) | 否 | 删除标志 |

### 8. sys_task_instance（任务实例表）

| 字段名 | 类型 | 是否必填 | 说明 |
|--------|------|----------|------|
| id | BIGINT | 是 | 主键，自增 |
| task_id | BIGINT | 是 | 任务ID |
| task_name | VARCHAR(100) | 否 | 任务名称（冗余） |
| person_id | BIGINT | 是 | 人员ID |
| person_name | VARCHAR(50) | 否 | 人员姓名（冗余） |
| period | VARCHAR(20) | 是 | 周期标识 |
| deadline | DATETIME | 否 | 截止时间 |
| completed | TINYINT | 否 | 是否完成：0/1 |
| complete_time | DATETIME | 否 | 完成时间 |
| on_time | TINYINT | 否 | 是否按时：0/1 |
| remark | VARCHAR(500) | 否 | 备注 |
| create_by | VARCHAR(64) | 否 | 创建者 |
| create_time | DATETIME | 否 | 创建时间 |
| update_by | VARCHAR(64) | 否 | 更新者 |
| update_time | DATETIME | 否 | 更新时间 |
| del_flag | CHAR(1) | 否 | 删除标志 |

### 9. sys_email_config（邮件配置表）

| 字段名 | 类型 | 是否必填 | 说明 |
|--------|------|----------|------|
| id | BIGINT | 是 | 主键，自增 |
| host | VARCHAR(100) | 是 | SMTP服务器 |
| port | INT | 否 | SMTP端口 |
| username | VARCHAR(100) | 是 | 发件人账号 |
| password | VARCHAR(100) | 是 | 发件人密码/授权码 |
| recipient_email | VARCHAR(500) | 是 | 收件人地址 |
| sender_name | VARCHAR(50) | 否 | 发件人名称 |
| email_subject | VARCHAR(100) | 否 | 邮件主题 |
| send_day | VARCHAR(10) | 否 | 发送日期 |
| send_time | TIME | 否 | 发送时间 |
| enabled | TINYINT | 否 | 是否启用：0/1 |
| remark | VARCHAR(500) | 否 | 备注 |
| create_by | VARCHAR(64) | 否 | 创建者 |
| create_time | DATETIME | 否 | 创建时间 |
| update_by | VARCHAR(64) | 否 | 更新者 |
| update_time | DATETIME | 否 | 更新时间 |

## ER关系图

```
sys_person ────┐
               │
               │ N:M
               ▼
sys_project ◄────────────────────────────────┐
    │                                         │
    │ 1:N                                     │ 1:N
    ▼                                         ▼
sys_project_milestone                   sys_person_project
    │
    │ 1:N
    ├──────────────┬──────────────┐
    ▼              ▼              ▼
sys_issue      sys_risk       (其他)

sys_task ────1:N───► sys_task_instance ────N:1───► sys_person
```

## 索引设计

### 主要索引
- `idx_person_name` ON sys_person(name)
- `idx_project_name` ON sys_project(name)
- `idx_project_status` ON sys_project(status)
- `idx_issue_project` ON sys_issue(project_id)
- `idx_issue_status` ON sys_issue(status)
- `idx_risk_project` ON sys_risk(project_id)
- `idx_risk_status` ON sys_risk(status)
- `idx_task_instance_period` ON sys_task_instance(period)
- `idx_task_instance_person` ON sys_task_instance(person_id)
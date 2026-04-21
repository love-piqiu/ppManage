# API接口文档

## 1. 接口概述

### 1.1 基本信息

| 项目 | 说明 |
|------|------|
| Base URL | http://123.56.191.180/prod-api |
| 认证方式 | JWT Token |
| 请求格式 | JSON |
| 响应格式 | JSON |
| 编码 | UTF-8 |

### 1.2 认证说明

所有业务接口需要在请求头携带 Token：

```
Authorization: Bearer {token}
```

Token 通过登录接口获取。

### 1.3 响应格式

**成功响应：**

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": { ... }
}
```

**失败响应：**

```json
{
  "code": 500,
  "msg": "错误信息",
  "data": null
}
```

---

## 2. 认证接口

### 2.1 登录

**接口地址：** `POST /login`

**请求参数：**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| username | String | 是 | 用户名 |
| password | String | 是 | 密码 |
| code | String | 是 | 验证码 |
| uuid | String | 是 | 验证码UUID |

**请求示例：**

```json
{
  "username": "admin",
  "password": "admin123",
  "code": "1234",
  "uuid": "xxx-xxx-xxx"
}
```

**响应示例：**

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "token": "eyJhbGciOiJIUzUxMiJ9..."
  }
}
```

### 2.2 获取验证码

**接口地址：** `GET /captchaImage`

**响应示例：**

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "uuid": "xxx-xxx-xxx",
    "img": "data:image/png;base64,..."
  }
}
```

---

## 3. 项目管理接口

### 3.1 获取项目列表

**接口地址：** `GET /system/project/list`

**请求参数：**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| pageNum | Integer | 否 | 页码，默认1 |
| pageSize | Integer | 否 | 每页数量，默认10 |
| name | String | 否 | 项目名称（模糊查询） |
| customer | String | 否 | 客户名称（模糊查询） |
| pmId | Long | 否 | PM ID |
| salesId | Long | 否 | 销售ID |
| status | String | 否 | 状态：进行中/已完成/暂停 |
| stage | String | 否 | 当前阶段 |
| isSubordinate | String | 否 | 是否下辖：是/否 |

**响应示例：**

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "total": 100,
    "rows": [
      {
        "id": 1,
        "name": "项目A",
        "customer": "客户X",
        "pmId": 10,
        "pmName": "张三",
        "salesId": 20,
        "salesName": "李四",
        "cost": 50.00,
        "costUsed": 30.00,
        "workHours": 200,
        "workHoursUsed": 150,
        "progress": 75,
        "stage": "开发阶段",
        "status": "进行中",
        "isSubordinate": "是",
        "startDate": "2026-01-01",
        "endDate": "2026-06-30"
      }
    ]
  }
}
```

### 3.2 获取项目详情

**接口地址：** `GET /system/project/{id}`

**响应示例：**

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "id": 1,
    "name": "项目A",
    "customer": "客户X",
    "pmId": 10,
    "pmName": "张三",
    "cost": 50.00,
    "costUsed": 30.00,
    "progress": 75,
    "stage": "开发阶段",
    "status": "进行中",
    "milestones": [
      {
        "id": 1,
        "projectId": 1,
        "description": "需求确认完成",
        "recordDate": "2026-01-15"
      }
    ],
    "participants": [10, 11, 12]
  }
}
```

### 3.3 新增项目

**接口地址：** `POST /system/project`

**请求参数：**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| name | String | 是 | 项目名称 |
| customer | String | 否 | 客户名称 |
| pmId | Long | 否 | PM ID |
| salesId | Long | 否 | 销售ID |
| cost | BigDecimal | 否 | 成本（万） |
| workHours | Integer | 否 | 总工时 |
| contractAmount | BigDecimal | 否 | 合同金额 |
| isSubordinate | String | 否 | 是否下辖 |
| projectType | String | 否 | 项目类型 |
| startDate | Date | 否 | 开始日期 |
| endDate | Date | 否 | 计划结束日期 |
| participants | List<Long> | 否 | 参与人员ID列表 |
| customMilestones | List | 否 | 自定义里程碑（外包项目） |

**请求示例：**

```json
{
  "name": "新项目B",
  "customer": "客户Y",
  "pmId": 10,
  "cost": 100.00,
  "workHours": 300,
  "contractAmount": 200.00,
  "isSubordinate": "是",
  "projectType": "外包",
  "startDate": "2026-04-01",
  "endDate": "2026-12-31",
  "participants": [10, 11]
}
```

### 3.4 编辑项目

**接口地址：** `PUT /system/project`

**请求参数：** 同新增项目，需额外包含 `id`

### 3.5 删除项目

**接口地址：** `DELETE /system/project/{id}`

---

## 4. 问题管理接口

### 4.1 获取问题列表

**接口地址：** `GET /system/issue/list`

**请求参数：**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| pageNum | Integer | 否 | 页码 |
| pageSize | Integer | 否 | 每页数量 |
| projectId | Long | 否 | 项目ID |
| status | String | 否 | 状态 |
| severity | String | 否 | 严重程度 |

**响应示例：**

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "total": 50,
    "rows": [
      {
        "id": 1,
        "projectId": 1,
        "projectName": "项目A",
        "description": "问题描述",
        "type": "技术",
        "severity": "高",
        "status": "待处理",
        "ownerId": 10,
        "ownerName": "张三",
        "discoverDate": "2026-04-01"
      }
    ]
  }
}
```

### 4.2 新增问题

**接口地址：** `POST /system/issue`

**请求参数：**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| projectId | Long | 是 | 项目ID |
| description | String | 是 | 问题描述 |
| type | String | 否 | 问题类型 |
| severity | String | 否 | 严重程度：高/中/低 |
| ownerId | Long | 否 | 负责人ID |
| discoverDate | Date | 否 | 发现日期 |
| planDate | Date | 否 | 计划解决日期 |

### 4.3 编辑问题

**接口地址：** `PUT /system/issue`

### 4.4 删除问题

**接口地址：** `DELETE /system/issue/{id}`

---

## 5. 风险管理接口

### 5.1 获取风险列表

**接口地址：** `GET /system/risk/list`

**请求参数：**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| pageNum | Integer | 否 | 页码 |
| pageSize | Integer | 否 | 每页数量 |
| projectId | Long | 否 | 项目ID |
| level | String | 否 | 风险等级：高/中/低 |
| status | String | 否 | 状态：潜在/已发生/已消除 |

**响应示例：**

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "total": 30,
    "rows": [
      {
        "id": 1,
        "projectId": 1,
        "projectName": "项目A",
        "description": "风险描述",
        "level": "高",
        "status": "潜在",
        "measure": "应对措施",
        "ownerId": 10,
        "ownerName": "张三"
      }
    ]
  }
}
```

### 5.2 新增风险

**接口地址：** `POST /system/risk`

**请求参数：**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| projectId | Long | 是 | 项目ID |
| description | String | 是 | 风险描述 |
| level | String | 否 | 风险等级 |
| measure | String | 否 | 应对措施 |
| ownerId | Long | 否 | 负责人ID |

### 5.3 编辑风险

**接口地址：** `PUT /system/risk`

### 5.4 删除风险

**接口地址：** `DELETE /system/risk/{id}`

---

## 6. 人员管理接口

### 6.1 获取人员列表

**接口地址：** `GET /system/person/list`

**请求参数：**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| pageNum | Integer | 否 | 页码 |
| pageSize | Integer | 否 | 每页数量 |
| name | String | 否 | 姓名 |
| status | String | 否 | 状态：在职/离职 |
| level | String | 否 | 等级 |

**响应示例：**

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "total": 100,
    "rows": [
      {
        "id": 1,
        "name": "张三",
        "position": "项目经理",
        "level": "B40",
        "contact": "13800138000",
        "email": "zhangsan@example.com",
        "workYears": 10,
        "companyYears": 5,
        "status": "在职"
      }
    ]
  }
}
```

### 6.2 获取人员详情

**接口地址：** `GET /system/person/{id}`

### 6.3 新增人员

**接口地址：** `POST /system/person`

**请求参数：**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| name | String | 是 | 姓名 |
| position | String | 否 | 职位 |
| level | String | 否 | 等级 |
| contact | String | 否 | 联系方式 |
| email | String | 否 | 邮箱 |
| entryDate | Date | 否 | 入职日期 |

### 6.4 编辑人员

**接口地址：** `PUT /system/person`

### 6.5 删除人员

**接口地址：** `DELETE /system/person/{id}`

---

## 7. 任务管理接口

### 7.1 获取任务列表

**接口地址：** `GET /system/task/list`

**响应示例：**

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "total": 20,
    "rows": [
      {
        "id": 1,
        "name": "周报提交",
        "type": "周期性",
        "cycle": "每周",
        "deadlineWeekday": "周五",
        "status": "启用"
      }
    ]
  }
}
```

### 7.2 新增任务

**接口地址：** `POST /system/task`

**请求参数：**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| name | String | 是 | 任务名称 |
| type | String | 是 | 类型：周期性/一次性 |
| cycle | String | 否 | 周期：每日/每周/每月 |
| deadlineTime | Time | 否 | 截止时间点 |
| deadlineDay | Integer | 否 | 截止日期（每月N号） |
| deadlineWeekday | String | 否 | 截止星期 |
| status | String | 否 | 状态：启用/禁用 |

### 7.3 编辑任务

**接口地址：** `PUT /system/task`

### 7.4 删除任务

**接口地址：** `DELETE /system/task/{id}`

### 7.5 获取任务实例列表

**接口地址：** `GET /system/taskInstance/list`

**请求参数：**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| taskId | Long | 否 | 任务ID |
| personId | Long | 否 | 人员ID |
| period | String | 否 | 周期标识 |

### 7.6 标记任务完成

**接口地址：** `PUT /system/taskInstance/complete`

**请求参数：**

```json
{
  "id": 1,
  "completed": true
}
```

---

## 8. 周报管理接口

### 8.1 获取周报内容

**接口地址：** `GET /system/report/weekly`

**请求参数：**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| period | String | 否 | 周期标识（默认本周） |

**响应示例：**

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "period": "2026-W16",
    "startDate": "2026-04-14",
    "endDate": "2026-04-20",
    "activeProjectCount": 10,
    "openIssueCount": 5,
    "highRiskCount": 2,
    "taskCompletionRate": 85.5,
    "projects": [...],
    "issues": [...],
    "risks": [...],
    "tasks": [...]
  }
}
```

### 8.2 发送周报邮件

**接口地址：** `POST /system/report/send`

### 8.3 获取邮件配置

**接口地址：** `GET /system/emailConfig`

### 8.4 保存邮件配置

**接口地址：** `POST /system/emailConfig`

**请求参数：**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| host | String | 是 | SMTP服务器 |
| port | Integer | 是 | SMTP端口 |
| username | String | 是 | 发件账号 |
| password | String | 是 | 授权码 |
| recipientEmail | String | 是 | 收件人邮箱 |
| enabled | Boolean | 否 | 是否启用 |

---

## 9. 错误码定义

| 错误码 | 说明 |
|--------|------|
| 200 | 操作成功 |
| 401 | 未登录或Token失效 |
| 403 | 无权限访问 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

---

## 10. 附录

### 10.1 Swagger文档地址

生产环境：http://123.56.191.180/swagger-ui.html

### 10.2 接口调试建议

1. 使用 Postman 或 Apifox 进行接口调试
2. 先调用 `/captchaImage` 获取验证码
3. 再调用 `/login` 获取 Token
4. 将 Token 添加到后续请求头中
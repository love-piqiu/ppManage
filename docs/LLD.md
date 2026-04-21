# 详细设计文档

## 1. 项目管理模块详细设计

### 1.1 类设计

#### SysProject 实体类

```java
public class SysProject extends BaseEntity {
    private Long id;                    // 主键
    private String name;                // 项目名称
    private String customer;            // 客户
    private Long pmId;                  // 项目经理ID
    private String pmName;              // 项目经理姓名（冗余）
    private Long salesId;               // 销售ID
    private String salesName;           // 销售姓名（冗余）
    private BigDecimal cost;            // 成本（万）
    private BigDecimal costUsed;        // 已用成本（万）
    private Integer workHours;          // 总工时（人天）
    private Integer workHoursUsed;      // 已用工时（人天）
    private Integer progress;           // 进度（0-100）
    private String stage;               // 当前阶段
    private String status;              // 状态：进行中/已完成/暂停
    private String isSubordinate;       // 是否下辖：是/否
    private String projectType;         // 项目类型：外包/自营
    private Date startDate;             // 开始日期
    private Date endDate;               // 计划结束日期
    private Date reqPlanDate;           // 需求计划日期
    private Date reqActualDate;         // 需求实际日期
    private Date uatPlanDate;           // UAT计划日期
    private Date uatActualDate;         // UAT实际日期
    private Date launchPlanDate;        // 上线计划日期
    private Date launchActualDate;      // 上线实际日期
    private Date acceptPlanDate;        // 验收计划日期
    private Date acceptActualDate;      // 验收实际日期
    private Date actualEndDate;         // 实际结束日期
    private BigDecimal contractAmount;  // 合同金额
    private String remark;              // 备注
    
    // 关联数据（非持久化）
    private List<SysProjectMilestone> milestones;
    private List<SysProjectInvoice> invoices;
    private List<Long> participants;
}
```

#### SysProjectService 服务类

```java
public interface ISysProjectService {
    List<SysProject> selectProjectList(SysProject project);
    List<SysProject> selectProjectAll();
    SysProject selectProjectById(Long id);
    boolean checkNameUnique(SysProject project);
    int insertProject(SysProject project);
    int updateProject(SysProject project);
    int deleteProjectById(Long id);
    int deleteProjectByIds(Long[] ids);
    int countActiveProject();
    int countCompletedProject();
}
```

### 1.2 数据库设计

#### sys_project 表结构

| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 主键 |
| name | VARCHAR(100) | NOT NULL, UNIQUE | 项目名称 |
| customer | VARCHAR(100) | - | 客户名称 |
| pm_id | BIGINT | FK -> sys_person.id | 项目经理ID |
| pm_name | VARCHAR(50) | - | 项目经理姓名 |
| sales_id | BIGINT | FK -> sys_person.id | 销售ID |
| sales_name | VARCHAR(50) | - | 销售姓名 |
| cost | DECIMAL(10,2) | - | 成本（万元） |
| cost_used | DECIMAL(10,2) | - | 已使用成本 |
| work_hours | INT | - | 总工时（人天） |
| work_hours_used | INT | - | 已使用工时 |
| progress | INT | - | 进度百分比 |
| stage | VARCHAR(50) | - | 当前阶段 |
| status | VARCHAR(20) | - | 状态 |
| is_subordinate | CHAR(1) | - | 是否下辖 |
| project_type | VARCHAR(20) | - | 项目类型 |
| start_date | DATE | - | 开始日期 |
| end_date | DATE | - | 计划结束日期 |
| contract_amount | DECIMAL(10,2) | - | 合同金额 |
| del_flag | CHAR(1) | DEFAULT '0' | 删除标志 |
| create_by | VARCHAR(64) | - | 创建者 |
| create_time | DATETIME | - | 创建时间 |
| update_by | VARCHAR(64) | - | 更新者 |
| update_time | DATETIME | - | 更新时间 |

---

## 2. 问题跟踪模块详细设计

### 2.1 类设计

#### SysIssue 实体类

```java
public class SysIssue extends BaseEntity {
    private Long id;                    // 主键
    private Long projectId;             // 项目ID
    private String projectName;         // 项目名称（冗余）
    private String description;         // 问题描述
    private String type;                // 问题类型
    private String severity;            // 严重程度：高/中/低
    private String status;              // 状态：待处理/进行中/已解决/已关闭
    private Long ownerId;               // 负责人ID
    private String ownerName;           // 负责人姓名
    private Date discoverDate;          // 发现日期
    private Date planDate;              // 计划解决日期
    private Date actualDate;            // 实际解决日期
    private String solution;            // 解决方案
    private String remark;              // 备注
}
```

### 2.2 状态流转逻辑

```java
// 问题状态流转校验
public boolean canTransition(String currentStatus, String newStatus) {
    Map<String, List<String>> allowedTransitions = new HashMap<>();
    allowedTransitions.put("待处理", Arrays.asList("进行中", "已关闭"));
    allowedTransitions.put("进行中", Arrays.asList("已解决", "待处理"));
    allowedTransitions.put("已解决", Arrays.asList("已关闭", "进行中"));
    allowedTransitions.put("已关闭", Arrays.asList()); // 不可流转
    
    return allowedTransitions.getOrDefault(currentStatus, new ArrayList<>())
                             .contains(newStatus);
}
```

### 2.3 数据库设计

#### sys_issue 表结构

| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | BIGINT | PK | 主键 |
| project_id | BIGINT | FK, NOT NULL | 项目ID |
| project_name | VARCHAR(100) | - | 项目名称（冗余） |
| description | VARCHAR(500) | NOT NULL | 问题描述 |
| type | VARCHAR(50) | - | 问题类型 |
| severity | VARCHAR(20) | - | 严重程度 |
| status | VARCHAR(20) | DEFAULT '待处理' | 状态 |
| owner_id | BIGINT | FK | 负责人ID |
| owner_name | VARCHAR(50) | - | 负责人姓名 |
| discover_date | DATE | - | 发现日期 |
| plan_date | DATE | - | 计划解决日期 |
| actual_date | DATE | - | 实际解决日期 |
| solution | VARCHAR(500) | - | 解决方案 |
| del_flag | CHAR(1) | DEFAULT '0' | 删除标志 |

---

## 3. 风险管理模块详细设计

### 3.1 类设计

#### SysRisk 实体类

```java
public class SysRisk extends BaseEntity {
    private Long id;                    // 主键
    private Long projectId;             // 项目ID
    private String projectName;         // 项目名称（冗余）
    private String description;         // 风险描述
    private String level;               // 风险等级：高/中/低
    private String status;              // 状态：潜在/已发生/已消除
    private String measure;             // 应对措施
    private Long ownerId;               // 负责人ID
    private String ownerName;           // 负责人姓名
    private Date happenDate;            // 发生日期
    private Date resolveDate;           // 消除日期
    private String remark;              // 备注
}
```

---

## 4. 人员管理模块详细设计

### 4.1 类设计

#### SysPerson 实体类

```java
public class SysPerson extends BaseEntity {
    private Long id;                    // 主键
    private String name;                // 姓名
    private String position;            // 职位
    private String level;               // 等级：B20/B30/B40等
    private String contact;             // 联系方式
    private String email;               // 邮箱
    private Integer age;                // 年龄
    private Integer workYears;          // 工作年限
    private Integer companyYears;       // 入司年限
    private String education;           // 学历
    private String school;              // 毕业院校
    private String hometown;            // 籍贯
    private Date entryDate;             // 入职日期
    private String status;              // 状态：在职/离职
    private String remark;              // 备注
    
    // 关联数据（非持久化）
    private List<SysProject> projects;  // 参与项目
    private List<SysTaskInstance> tasks; // 本周任务
}
```

---

## 5. 任务管理模块详细设计

### 5.1 类设计

#### SysTask 实体类（任务定义）

```java
public class SysTask extends BaseEntity {
    private Long id;                    // 主键
    private String name;                // 任务名称
    private String type;                // 类型：周期性/一次性
    private String cycle;               // 周期：每日/每周/每月
    private Time deadlineTime;          // 截止时间点
    private Integer deadlineDay;        // 截止日期（每月N号）
    private String deadlineWeekday;     // 截止星期（每周N）
    private String status;              // 状态：启用/禁用
    private String description;         // 描述
}
```

#### SysTaskInstance 实体类（任务实例）

```java
public class SysTaskInstance extends BaseEntity {
    private Long id;                    // 主键
    private Long taskId;                // 任务ID
    private String taskName;            // 任务名称（冗余）
    private Long personId;              // 人员ID
    private String personName;          // 人员姓名（冗余）
    private String period;              // 周期标识：如2026-W16
    private Date deadline;              // 截止时间
    private Boolean completed;          // 是否完成
    private Date completeTime;          // 完成时间
    private Boolean onTime;             // 是否按时完成
}
```

### 5.2 任务实例生成逻辑

```java
// 周期任务实例生成
public void generateTaskInstances(String period) {
    List<SysTask> activeTasks = taskMapper.selectActiveTasks();
    
    for (SysTask task : activeTasks) {
        if ("周期性".equals(task.getType())) {
            // 获取所有在职人员
            List<SysPerson> persons = personMapper.selectActivePersons();
            
            for (SysPerson person : persons) {
                SysTaskInstance instance = new SysTaskInstance();
                instance.setTaskId(task.getId());
                instance.setPersonId(person.getId());
                instance.setPeriod(period);
                instance.setCompleted(false);
                
                // 计算截止时间
                instance.setDeadline(calculateDeadline(task, period));
                
                taskInstanceMapper.insert(instance);
            }
        }
    }
}
```

---

## 6. 周报生成模块详细设计

### 6.1 周报数据结构

```java
public class WeeklyReport {
    private String period;              // 周期：如2026-W16
    private Date startDate;             // 开始日期
    private Date endDate;               // 结束日期
    
    // 统计概览
    private Integer activeProjectCount; // 进行中项目数
    private Integer completedProjectCount; // 已完成项目数
    private Integer openIssueCount;     // 待处理问题数
    private Integer highRiskCount;      // 高风险数
    private Double taskCompletionRate;  // 任务完成率
    
    // 详细数据
    private List<ProjectProgress> projectProgresses; // 项目进度列表
    private List<IssueSummary> issues;  // 问题列表
    private List<RiskSummary> risks;    // 风险列表
    private List<TaskSummary> tasks;    // 任务统计
    private List<MilestoneSummary> milestones; // 重要事项
}
```

### 6.2 周报生成流程

```java
public WeeklyReport generateReport(String period) {
    WeeklyReport report = new WeeklyReport();
    report.setPeriod(period);
    
    // 1. 统计概览
    report.setActiveProjectCount(projectMapper.countActiveProject());
    report.setCompletedProjectCount(projectMapper.countCompletedProject());
    report.setOpenIssueCount(issueMapper.countOpenIssues());
    report.setHighRiskCount(riskMapper.countHighRisks());
    report.setTaskCompletionRate(taskService.calculateCompletionRate(period));
    
    // 2. 项目进度（只统计下辖项目）
    SysProject query = new SysProject();
    query.setIsSubordinate("是");
    List<SysProject> projects = projectMapper.selectProjectList(query);
    report.setProjectProgresses(buildProjectProgresses(projects));
    
    // 3. 问题列表
    report.setIssues(issueMapper.selectRecentIssues(period));
    
    // 4. 风险列表
    report.setRisks(riskMapper.selectActiveRisks());
    
    // 5. 任务完成统计
    report.setTasks(taskService.getTaskSummary(period));
    
    // 6. 重要事项
    report.setMilestones(milestoneMapper.selectRecentMilestones(period));
    
    return report;
}
```

### 6.3 邵件发送逻辑

```java
public void sendWeeklyReport(WeeklyReport report) {
    SysEmailConfig config = emailConfigMapper.selectActiveConfig();
    
    // 构建邮件内容
    String subject = "项目周报 - " + report.getPeriod();
    String content = buildEmailContent(report);
    
    // 发送邮件
    MimeMessage message = mailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
    helper.setFrom(config.getUsername());
    helper.setTo(config.getRecipientEmail().split(","));
    helper.setSubject(subject);
    helper.setText(content, true); // HTML格式
    
    mailSender.send(message);
}
```

---

## 7. 关键算法设计

### 7.1 任务完成率计算

```java
public Double calculateCompletionRate(String period) {
    List<SysTaskInstance> instances = taskInstanceMapper.selectByPeriod(period);
    
    long total = instances.size();
    long completed = instances.stream()
                              .filter(i -> Boolean.TRUE.equals(i.getCompleted()))
                              .count();
    
    return total > 0 ? (double) completed / total * 100 : 0;
}
```

### 7.2 成本/工时使用率计算

```java
public Double calculateUsageRate(BigDecimal used, BigDecimal total) {
    if (total == null || total.compareTo(BigDecimal.ZERO) == 0) {
        return 0.0;
    }
    return used.divide(total, 4, RoundingMode.HALF_UP)
               .multiply(new BigDecimal(100))
               .doubleValue();
}
```

---

## 8. 异常处理设计

### 8.1 业务异常定义

```java
public class BusinessException extends RuntimeException {
    private String code;
    private String message;
    
    public BusinessException(String code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }
}

// 异常码定义
public enum ErrorCode {
    PROJECT_NAME_DUPLICATE("P001", "项目名称已存在"),
    ISSUE_STATUS_INVALID("I001", "问题状态流转不合法"),
    RISK_LEVEL_INVALID("R001", "风险等级不合法"),
    EMAIL_SEND_FAILED("E001", "邮件发送失败");
}
```

### 8.2 全局异常处理

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(BusinessException.class)
    public AjaxResult handleBusinessException(BusinessException e) {
        return AjaxResult.error(e.getCode(), e.getMessage());
    }
    
    @ExceptionHandler(Exception.class)
    public AjaxResult handleException(Exception e) {
        log.error("系统异常", e);
        return AjaxResult.error("系统异常，请联系管理员");
    }
}
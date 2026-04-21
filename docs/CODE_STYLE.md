# 代码规范文档

## 1. 概述

本文档定义 ppManage 项目的代码编写规范，所有开发人员需遵循。

---

## 2. Java编码规范

### 2.1 命名规范

| 类型 | 规范 | 示例 |
|------|------|------|
| 包名 | 全小写，单词间用点分隔 | `com.ruoyi.system.service` |
| 类名 | 大驼峰，名词 | `SysProjectService` |
| 接口名 | 大驼峰，以I开头 | `ISysProjectService` |
| 方法名 | 小驼峰，动词开头 | `selectProjectById()` |
| 变量名 | 小驼峰 | `projectId` |
| 常量名 | 全大写，下划线分隔 | `MAX_PAGE_SIZE` |
| 实体类 | 以实体名命名 | `SysProject` |
| Mapper | 以实体名+Mapper | `SysProjectMapper` |
| Service | 以实体名+ServiceImpl | `SysProjectServiceImpl` |
| Controller | 以实体名+Controller | `SysProjectController` |

### 2.2 代码格式

#### 缩进与空格

- 使用4个空格缩进，禁止使用Tab
- 运算符两侧各一个空格
- 逗号后一个空格

#### 大括号规则

```java
// 推荐：左大括号前不换行，左大括号后换行
if (condition) {
    doSomething();
} else {
    doOther();
}
```

#### 行长度限制

- 单行代码不超过120字符
- 超长行需换行，换行点在运算符后

### 2.3 注释规范

#### 类注释

```java
/**
 * 项目信息 服务层处理
 *
 * @author ppmanage
 * @date 2026-04-09
 */
public class SysProjectServiceImpl implements ISysProjectService {
```

#### 方法注释

```java
/**
 * 查询项目列表
 *
 * @param project 项目信息
 * @return 项目列表
 */
@Override
public List<SysProject> selectProjectList(SysProject project) {
```

#### 行内注释

```java
// 仅在必要时使用行内注释
int progress = project.getProgress(); // 进度百分比
```

### 2.4 异常处理

```java
// 推荐：捕获具体异常，记录日志
try {
    result = doSomething();
} catch (BusinessException e) {
    log.error("业务异常: {}", e.getMessage());
    throw e;
} catch (Exception e) {
    log.error("系统异常", e);
    throw new BusinessException("系统异常");
}
```

### 2.5 日志规范

```java
// 日志级别使用规范
log.debug("调试信息：{}", detail);   // 调试阶段
log.info("操作成功：{}", result);    // 重要操作
log.warn("警告：{}", warning);       // 潜在问题
log.error("错误：{}", error);        // 错误情况
```

### 2.6 Controller规范

```java
@RestController
@RequestMapping("/system/project")
public class SysProjectController extends BaseController {
    
    @Autowired
    private ISysProjectService projectService;
    
    /**
     * 查询项目列表
     */
    @PreAuthorize("@ss.hasPermi('system:project:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysProject project) {
        startPage();
        List<SysProject> list = projectService.selectProjectList(project);
        return getDataTable(list);
    }
}
```

---

## 3. Vue编码规范

### 3.1 命名规范

| 类型 | 规范 | 示例 |
|------|------|------|
| 组件文件名 | 大驼峰或小驼峰 | `ProjectList.vue` |
| 组件名 | 大驼峰 | `<ProjectList />` |
| prop名 | 小驼峰 | `projectId` |
| 事件名 | 小驼峰，on前缀 | `onSubmit` |
| 变量名 | 小驼峰 | `projectList` |
| 常量名 | 全大写 | `DEFAULT_PAGE_SIZE` |

### 3.2 组件结构

```vue
<template>
  <!-- 模板内容 -->
</template>

<script>
export default {
  name: 'ProjectList',
  components: {},
  props: {},
  data() {
    return {}
  },
  computed: {},
  watch: {},
  created() {},
  mounted() {},
  methods: {}
}
</script>

<style scoped>
/* 样式内容 */
</style>
```

### 3.3 API调用规范

```javascript
// 使用统一的API文件
import { listProject, getProject } from '@/api/system/project'

// 在methods中调用
methods: {
  getList() {
    this.loading = true
    listProject(this.queryParams).then(response => {
      this.projectList = response.rows
      this.total = response.total
      this.loading = false
    })
  }
}
```

### 3.4 表单验证规范

```javascript
rules: {
  name: [
    { required: true, message: '项目名称不能为空', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在2到50个字符', trigger: 'blur' }
  ],
  cost: [
    { type: 'number', message: '成本必须为数字', trigger: 'blur' }
  ]
}
```

### 3.5 样式规范

```scss
// 使用scoped避免样式污染
<style scoped>
// 组件样式
.project-card {
  padding: 16px;
  
  .title {
    font-size: 14px;
    font-weight: 600;
  }
}
</style>

// 使用语义化CSS类名
.status-active { color: #10B981; }
.status-warning { color: #F59E0B; }
.status-danger { color: #EF4444; }
```

---

## 4. MyBatis Mapper规范

### 4.1 XML文件结构

```xml
<mapper namespace="com.ruoyi.system.mapper.SysProjectMapper">
    
    <!-- 结果映射 -->
    <resultMap type="SysProject" id="SysProjectResult">
        <id property="id" column="id"/>
        <result property="name" column="name"/>
    </resultMap>
    
    <!-- SQL片段 -->
    <sql id="selectProjectVo">
        select id, name, customer from sys_project
    </sql>
    
    <!-- 查询语句 -->
    <select id="selectProjectList" resultMap="SysProjectResult">
        <include refid="selectProjectVo"/>
        where del_flag = '0'
    </select>
</mapper>
```

### 4.2 参数规范

```xml
<!-- 使用#{param}防止SQL注入 -->
<select id="selectProjectById" resultMap="SysProjectResult">
    select * from sys_project where id = #{id}
</select>

<!-- 模糊查询使用concat -->
<if test="name != null and name != ''">
    AND name like concat('%', #{name}, '%')
</if>
```

---

## 5. 数据库规范

### 5.1 表命名

- 表名以 `sys_` 开头
- 使用小写字母和下划线
- 示例：`sys_project`、`sys_project_milestone`

### 5.2 字段命名

| 规范 | 示例 |
|------|------|
| 主键 | `id` (BIGINT, 自增) |
| 外键 | `xxx_id` |
| 名称 | `name` |
| 状态 | `status` |
| 删除标志 | `del_flag` (CHAR(1)) |
| 创建人 | `create_by` |
| 创建时间 | `create_time` |
| 更新人 | `update_by` |
| 更新时间 | `update_time` |

### 5.3 必备字段

每张表必须包含以下字段：

```sql
del_flag CHAR(1) DEFAULT '0' COMMENT '删除标志',
create_by VARCHAR(64) COMMENT '创建者',
create_time DATETIME COMMENT '创建时间',
update_by VARCHAR(64) COMMENT '更新者',
update_time DATETIME COMMENT '更新时间'
```

---

## 6. Git提交规范

### 6.1 提交信息格式

```
<type>: <description>

<body>

Co-Authored-By: Claude <noreply@anthropic.com>
```

### 6.2 Type类型

| Type | 说明 |
|------|------|
| feat | 新功能 |
| fix | Bug修复 |
| refactor | 重构（不新增功能，不修复Bug） |
| docs | 文档更新 |
| style | 代码格式调整 |
| test | 测试相关 |
| chore | 构建/工具变更 |

### 6.3 示例

```
feat: 添加项目管理模块

新增功能：
- 项目列表查询
- 项目详情查看
- 项目新增/编辑/删除

Co-Authored-By: Claude <noreply@anthropic.com>
```

---

## 7. 代码审查要点

### 7.1 审查清单

- [ ] 命名是否符合规范
- [ ] 代码逻辑是否正确
- [ ] 是否有冗余代码
- [ ] 异常处理是否完善
- [ ] 日志记录是否合理
- [ ] SQL是否使用参数化查询
- [ ] 是否有安全漏洞（SQL注入、XSS等）
- [ ] 性能是否有问题（N+1查询等）

### 7.2 禁止事项

- 禁止硬编码密码、密钥等敏感信息
- 禁止使用 `System.out.println`
- 禁止在循环中执行数据库查询
- 禁止捕获异常后不做处理
- 禁止使用 `SELECT *`
- 禁止直接拼接SQL字符串
# ppManage - 项目及人员管理系统

一个为工程主管设计的项目管理工具，用于跟踪项目、问题、风险、人员和任务。

## 功能特性

- **项目管理**：项目信息、进度跟踪、重要事项记录
- **问题跟踪**：问题状态流转、严重程度标记、预警提醒
- **风险管理**：风险等级、应对措施、状态跟踪
- **人员管理**：人员信息、项目参与、投入比例
- **任务管理**：周期性任务、一次性任务、完成率统计
- **周报生成**：自动生成周报、邮件发送

## 技术栈

- **后端**：Spring Boot + MyBatis + MySQL
- **前端**：Vue.js + Element UI
- **框架**：基于 RuoYi-Vue

## 快速开始

### 环境要求

- JDK 1.8+
- Maven 3.6+
- MySQL 5.7+
- Redis 6.0+
- Node.js 16+

### 安装步骤

1. **克隆项目**
```bash
git clone <repository-url>
cd ppManage
```

2. **创建数据库**
```sql
CREATE DATABASE ppmmanage CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
```

3. **导入SQL脚本**
```bash
mysql -u root -p ppmmanage < sql/ry_20260321.sql
mysql -u root -p ppmmanage < sql/quartz.sql
mysql -u root -p ppmmanage < sql/ppManage.sql
```

4. **修改配置**

编辑 `ruoyi-admin/src/main/resources/application-druid.yml`，修改数据库连接信息。

5. **启动后端**
```bash
mvn spring-boot:run -pl ruoyi-admin
```

6. **启动前端**
```bash
cd ruoyi-ui
npm install
npm run dev
```

7. **访问系统**

- 前端地址：http://localhost:80
- 默认账号：admin / admin123

## 文档

- [项目诞生记录](docs/JOURNEY.md)
- [需求规格说明书](docs/PRD.md)
- [架构设计文档](docs/ARCHITECTURE.md)
- [概要设计文档](docs/HLD.md)
- [详细设计文档](docs/LLD.md)
- [数据库设计](docs/DATABASE.md)
- [API接口文档](docs/API.md)
- [部署文档](docs/DEPLOYMENT.md)
- [用户手册](docs/USER_MANUAL.md)
- [测试计划](docs/TEST_PLAN.md)
- [测试用例](docs/TEST_CASES.md)
- [测试报告](docs/TEST_REPORT.md)
- [代码规范文档](docs/CODE_STYLE.md)
- [运维手册](docs/OPS_MANUAL.md)
- [项目计划](docs/PROJECT_PLAN.md)
- [变更日志](docs/CHANGELOG.md)

## 许可证

MIT License
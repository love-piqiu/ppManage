# 部署文档

## 环境要求

### 后端环境
- JDK 1.8+
- Maven 3.6+
- MySQL 5.7+
- Redis 6.0+

### 前端环境
- Node.js 16+
- npm 8+ 或 yarn

## 本地开发环境配置

### 1. 安装依赖

```bash
# 安装后端依赖
mvn install

# 安装前端依赖
cd ruoyi-ui
npm install
```

### 2. 创建数据库

```sql
CREATE DATABASE ppmmanage CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
```

### 3. 导入SQL脚本

```bash
# 导入RuoYi基础表
mysql -u root -p ppmmanage < sql/ry_20260321.sql

# 导入定时任务表
mysql -u root -p ppmmanage < sql/quartz.sql

# 导入业务表
mysql -u root -p ppmmanage < sql/ppManage.sql
```

### 4. 修改配置文件

编辑 `ruoyi-admin/src/main/resources/application-druid.yml`:

```yaml
spring:
  datasource:
    druid:
      master:
        url: jdbc:mysql://localhost:3306/ppmanage?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8
        username: root
        password: 你的密码
```

### 5. 启动后端

```bash
# 方式1：使用Maven
mvn spring-boot:run -pl ruoyi-admin

# 方式2：打包后运行
mvn clean package -DskipTests
java -jar ruoyi-admin/target/ruoyi-admin.jar
```

### 6. 启动前端

```bash
cd ruoyi-ui
npm run dev
```

### 7. 访问系统

- 前端地址：http://localhost:80
- 后端接口：http://localhost:8080
- 默认账号：admin / admin123

## 生产环境部署

### 1. 打包

```bash
# 后端打包
mvn clean package -DskipTests

# 前端打包
cd ruoyi-ui
npm run build:prod
```

### 2. 后端部署

```bash
# 上传jar包到服务器
scp ruoyi-admin/target/ruoyi-admin.jar user@server:/opt/ppmanage/

# 创建启动脚本
cat > /opt/ppmanage/start.sh << 'EOF'
#!/bin/bash
nohup java -jar /opt/ppmanage/ruoyi-admin.jar > /opt/ppmanage/logs/app.log 2>&1 &
echo $! > /opt/ppmanage/app.pid
EOF

chmod +x /opt/ppmanage/start.sh
```

### 3. 前端部署（Nginx）

```nginx
server {
    listen 80;
    server_name your-domain.com;

    location / {
        root /opt/ppmanage/dist;
        index index.html;
        try_files $uri $uri/ /index.html;
    }

    location /prod-api/ {
        proxy_pass http://127.0.0.1:8080/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }
}
```

### 4. 配置邮件服务

登录系统后，进入"周报管理 > 邮件配置"页面，配置SMTP服务器信息：
- SMTP服务器地址（如：smtp.qq.com）
- SMTP端口（如：465）
- 发件人账号
- 发件人授权码
- 收件人邮箱

## 常见问题

### Q: 后端启动失败，提示数据库连接失败？
A: 检查MySQL服务是否启动，数据库用户名密码是否正确。

### Q: 前端启动失败，提示node-sass错误？
A: 尝试删除node_modules目录后重新安装依赖。

### Q: 登录时提示验证码错误？
A: 检查Redis服务是否启动。

## 回滚方案

1. 保留上一版本jar包
2. 数据库迁移脚本可逆
3. 使用Git版本控制代码
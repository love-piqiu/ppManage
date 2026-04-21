# 运维手册

## 1. 系统概述

| 项目 | 说明 |
|------|------|
| 系统名称 | ppManage - 项目及人员管理系统 |
| 生产地址 | http://123.56.191.180 |
| 技术栈 | Spring Boot + Vue.js + MySQL + Redis |
| 部署方式 | Nginx + jar包 |

---

## 2. 服务器信息

### 2.1 服务器配置

| 配置项 | 值 |
|------|------|
| SSH地址 | root@123.56.191.180 |
| 后端部署路径 | /opt/ppmanage/ |
| 前端部署路径 | /opt/ppmanage/dist/ |
| 日志路径 | /opt/ppmanage/logs/ |

### 2.2 服务端口

| 服务 | 端口 |
|------|------|
| Nginx | 80 |
| Spring Boot | 8080 |
| MySQL | 3306 |
| Redis | 6379 |

---

## 3. 系统监控

### 3.1 服务状态检查

```bash
# 检查后端服务状态
ps -ef | grep ruoyi-admin.jar

# 检查端口占用
netstat -tlnp | grep 8080

# 检查Nginx状态
systemctl status nginx

# 检查MySQL状态
systemctl status mysql

# 检查Redis状态
systemctl status redis
```

### 3.2 日志监控

```bash
# 查看应用日志
tail -f /opt/ppmanage/logs/app.log

# 查看错误日志
grep "ERROR" /opt/ppmanage/logs/app.log

# 查看Nginx日志
tail -f /var/log/nginx/access.log
tail -f /var/log/nginx/error.log
```

### 3.3 资源监控

```bash
# CPU使用率
top -bn1 | grep "Cpu(s)"

# 内存使用
free -h

# 磁盘空间
df -h

# 网络流量
iftop
```

---

## 4. 服务启停

### 4.1 后端服务

```bash
# 启动后端
cd /opt/ppmanage
nohup java -jar ruoyi-admin.jar > logs/app.log 2>&1 &
echo $! > app.pid

# 停止后端
kill $(cat /opt/ppmanage/app.pid)

# 重启后端
kill $(cat /opt/ppmanage/app.pid)
sleep 3
nohup java -jar ruoyi-admin.jar > logs/app.log 2>&1 &
echo $! > app.pid
```

### 4.2 Nginx服务

```bash
# 启动Nginx
systemctl start nginx

# 停止Nginx
systemctl stop nginx

# 重启Nginx
systemctl restart nginx

# 重新加载配置
nginx -s reload
```

### 4.3 MySQL服务

```bash
# 启动MySQL
systemctl start mysql

# 停止MySQL
systemctl stop mysql

# 重启MySQL
systemctl restart mysql
```

### 4.4 Redis服务

```bash
# 启动Redis
systemctl start redis

# 停止Redis
systemctl stop redis

# 重启Redis
systemctl restart redis
```

---

## 5. 数据备份

### 5.1 数据库备份

```bash
# 全量备份
mysqldump -u root -p ppmmanage > /opt/backup/ppmmanage_$(date +%Y%m%d).sql

# 备份脚本（每日执行）
cat > /opt/scripts/db_backup.sh << 'EOF'
#!/bin/bash
BACKUP_DIR=/opt/backup
DATE=$(date +%Y%m%d)
mysqldump -u root -p'password' ppmmanage > $BACKUP_DIR/ppmmanage_$DATE.sql
# 保留7天备份
find $BACKUP_DIR -name "ppmmanage_*.sql" -mtime +7 -delete
EOF

chmod +x /opt/scripts/db_backup.sh
```

### 5.2 配置定时备份

```bash
# 添加到crontab（每日凌晨2点执行）
crontab -e
0 2 * * * /opt/scripts/db_backup.sh
```

### 5.3 数据恢复

```bash
# 恢复数据库
mysql -u root -p ppmmanage < /opt/backup/ppmmanage_20260412.sql
```

---

## 6. 应用更新

### 6.1 后端更新流程

```bash
# 1. 备份当前版本
cp /opt/ppmanage/ruoyi-admin.jar /opt/ppmanage/ruoyi-admin.jar.bak

# 2. 上传新版本jar包
scp ruoyi-admin.jar root@123.56.191.180:/opt/ppmanage/

# 3. 停止服务
kill $(cat /opt/ppmanage/app.pid)

# 4. 启动新版本
nohup java -jar ruoyi-admin.jar > logs/app.log 2>&1 &
echo $! > app.pid

# 5. 检查启动状态
tail -f logs/app.log
```

### 6.2 前端更新流程

```bash
# 1. 备份当前版本
mv /opt/ppmanage/dist /opt/ppmanage/dist.bak

# 2. 上传新版本
scp -r dist/ root@123.56.191.180:/opt/ppmanage/

# 3. 重新加载Nginx
nginx -s reload
```

---

## 7. 故障处理

### 7.1 后端服务无法启动

**现象：** java进程不存在，端口8080无响应

**排查步骤：**

```bash
# 1. 检查日志
tail -100 /opt/ppmanage/logs/app.log

# 2. 检查端口
netstat -tlnp | grep 8080

# 3. 检查MySQL连接
mysql -u root -p -e "SELECT 1"

# 4. 检查Redis连接
redis-cli ping

# 5. 手动启动测试
java -jar ruoyi-admin.jar
```

**常见原因：**
- MySQL连接失败 → 检查数据库配置
- Redis连接失败 → 启动Redis服务
- 端口被占用 → 杀掉占用进程

### 7.2 页面无法访问

**现象：**浏览器访问超时或报错

**排查步骤：**

```bash
# 1. 检查Nginx状态
systemctl status nginx

# 2. 检查Nginx配置
nginx -t

# 3. 检查Nginx日志
tail -100 /var/log/nginx/error.log

# 4. 检查后端服务
curl http://localhost:8080/
```

**常见原因：**
- Nginx未启动 → 启动Nginx
- 配置文件错误 → 修正配置
- 后端服务宕机 → 重启后端

### 7.3 登录失败

**现象：**登录提示错误

**排查步骤：**

```bash
# 1. 检查Redis状态
redis-cli ping

# 2. 检查验证码服务
redis-cli keys "*captcha*"

# 3. 检查数据库用户表
mysql -u root -p -e "SELECT user_name FROM sys_user"
```

**常见原因：**
- Redis未启动 → 启动Redis
- 验证码失效 → 清除Redis缓存
- 用户不存在 → 检查数据库

### 7.4 邵件发送失败

**现象：**周报邮件发送失败

**排查步骤：**

```bash
# 1. 检查邮件配置
SELECT * FROM sys_email_config;

# 2. 测试SMTP连接
telnet smtp.qq.com 465

# 3. 查看应用日志
grep "mail" /opt/ppmanage/logs/app.log
```

**常见原因：**
- SMTP配置错误 → 修正配置
- 授权码错误 → 重新获取授权码
- 网络限制 → 检查防火墙

---

## 8. 性能优化

### 8.1 JVM参数优化

```bash
# 启动参数优化
java -Xms512m -Xmx1024m -XX:+UseG1GC -jar ruoyi-admin.jar
```

### 8.2 MySQL优化

```sql
-- 查看慢查询
SHOW VARIABLES LIKE 'slow_query%';

-- 开启慢查询日志
SET GLOBAL slow_query_log = 'ON';
SET GLOBAL long_query_time = 2;
```

### 8.3 Nginx优化

```nginx
# nginx.conf 优化配置
worker_processes auto;
worker_connections 1024;
keepalive_timeout 65;
gzip on;
gzip_min_length 1k;
gzip_types text/plain application/json;
```

---

## 9. 安全配置

### 9.1 防火墙配置

```bash
# 开放必要端口
firewall-cmd --permanent --add-port=80/tcp
firewall-cmd --permanent --add-port=8080/tcp
firewall-cmd --reload
```

### 9.2 SSH安全

```bash
# 禁用root密码登录
vi /etc/ssh/sshd_config
PermitRootLogin prohibit-password

# 使用密钥登录
ssh-copy-id root@123.56.191.180
```

### 9.3 定期安全更新

```bash
# 系统更新
yum update -y

# 检查安全漏洞
yum check-update --security
```

---

## 10. 运维检查清单

### 10.1 日常检查

| 检查项 | 频率 | 方法 |
|--------|------|------|
| 服务状态 | 每日 | ps/netstat |
| 日志错误 | 每日 | grep ERROR |
| 磁盘空间 | 每周 | df -h |
| 数据备份 | 每日 | 检查备份文件 |
| 安全日志 | 每周 | 检查异常登录 |

### 10.2 定期维护

| 维护项 | 频率 | 操作 |
|--------|------|------|
| 清理日志 | 每月 | 删除30天前日志 |
| 数据库优化 | 每月 | OPTIMIZE TABLE |
| 安全更新 | 每月 | yum update |
| 配置备份 | 每周 | 备份配置文件 |

---

## 11. 联系方式

| 角色 | 联系方式 |
|------|----------|
| 系统管理员 | admin |
| 技术支持 | [配置后填写] |
| 紧急联系 | [配置后填写] |
-- 为人员表添加头像字段
ALTER TABLE sys_person ADD COLUMN avatar varchar(255) DEFAULT NULL COMMENT '头像路径';

-- 更新老邱的头像
UPDATE sys_person SET avatar = '/profile/avatar/laoqiu.jpg' WHERE name = '老邱' AND del_flag = '0';
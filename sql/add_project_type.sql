-- 添加项目类型字段
ALTER TABLE sys_project ADD COLUMN project_type VARCHAR(20) DEFAULT '项目' COMMENT '项目类型:外包/项目';

-- 创建自定义里程碑表（用于外包项目）
CREATE TABLE IF NOT EXISTS sys_project_milestone_custom (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    project_id BIGINT NOT NULL COMMENT '项目ID',
    milestone_name VARCHAR(50) NOT NULL COMMENT '里程碑名称',
    plan_date DATE COMMENT '计划时间',
    actual_date DATE COMMENT '完成时间',
    sort_order INT DEFAULT 0 COMMENT '排序顺序',
    del_flag CHAR(1) DEFAULT '0' COMMENT '删除标志',
    create_by VARCHAR(64) COMMENT '创建者',
    create_time DATETIME COMMENT '创建时间',
    update_by VARCHAR(64) COMMENT '更新者',
    update_time DATETIME COMMENT '更新时间',
    INDEX idx_project_id (project_id)
) ENGINE=InnoDB COMMENT='项目自定义里程碑表';
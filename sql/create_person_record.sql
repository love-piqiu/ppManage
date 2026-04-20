CREATE TABLE sys_person_record (
  id bigint NOT NULL AUTO_INCREMENT,
  person_id bigint NOT NULL COMMENT '人员ID',
  person_name varchar(50) COMMENT '人员姓名',
  record_type varchar(20) COMMENT '记录类型',
  content text COMMENT '记录内容',
  record_date datetime COMMENT '记录日期',
  del_flag char(1) DEFAULT '0' COMMENT '删除标志',
  create_by varchar(64) COMMENT '创建人',
  create_time datetime COMMENT '创建时间',
  update_by varchar(64) COMMENT '更新人',
  update_time datetime COMMENT '更新时间',
  PRIMARY KEY (id)
) COMMENT='人员动态记录表';
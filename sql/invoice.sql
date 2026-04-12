-- 项目开票表
CREATE TABLE IF NOT EXISTS sys_project_invoice (
  id            BIGINT(20)      NOT NULL AUTO_INCREMENT  COMMENT '开票ID',
  project_id    BIGINT(20)      NOT NULL                 COMMENT '项目ID',
  amount        DECIMAL(10,2)   NOT NULL                 COMMENT '开票金额(万)',
  percent       INT(5)          DEFAULT 0                COMMENT '占合同比例(%)',
  invoice_type  VARCHAR(20)     NOT NULL                 COMMENT '开票类型:预付款/进度款/验收款/尾款',
  invoice_date  DATE            DEFAULT NULL             COMMENT '开票日期',
  remark        VARCHAR(255)    DEFAULT ''               COMMENT '备注',
  create_by     VARCHAR(64)     DEFAULT ''               COMMENT '创建者',
  create_time   DATETIME        DEFAULT NULL             COMMENT '创建时间',
  update_by     VARCHAR(64)     DEFAULT ''               COMMENT '更新者',
  update_time   DATETIME        DEFAULT NULL             COMMENT '更新时间',
  PRIMARY KEY (id),
  KEY idx_project_id (project_id)
) ENGINE=InnoDB AUTO_INCREMENT=1 COMMENT='项目开票表';
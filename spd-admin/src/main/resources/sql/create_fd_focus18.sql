-- 18类重点耗材维护表
-- 执行库：业务库 aspt

CREATE TABLE IF NOT EXISTS fd_focus18 (
  id bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  category varchar(100) DEFAULT NULL COMMENT '耗材类别',
  class_code varchar(100) DEFAULT NULL COMMENT '耗材分类代码',
  level1 varchar(200) DEFAULT NULL COMMENT '一级分类(学科/品类)',
  level2 varchar(200) DEFAULT NULL COMMENT '二级分类(用途/品目)',
  level3 varchar(200) DEFAULT NULL COMMENT '三级分类(部位/功能/品种)',
  generic_code varchar(100) DEFAULT NULL COMMENT '通用名代码',
  medical_generic_name varchar(200) DEFAULT NULL COMMENT '医保通用名',
  material_code varchar(100) DEFAULT NULL COMMENT '材质代码',
  material varchar(200) DEFAULT NULL COMMENT '材质',
  feature_code varchar(100) DEFAULT NULL COMMENT '特征代码',
  feature_param varchar(500) DEFAULT NULL COMMENT '特征参数',
  remark varchar(500) DEFAULT NULL COMMENT '备注',
  del_flag int(1) DEFAULT 0 COMMENT '删除标识(0正常 1删除)',
  create_by varchar(64) DEFAULT NULL COMMENT '创建者',
  create_time datetime DEFAULT NULL COMMENT '创建时间',
  update_by varchar(64) DEFAULT NULL COMMENT '更新者',
  update_time datetime DEFAULT NULL COMMENT '更新时间',
  delete_by varchar(64) DEFAULT NULL COMMENT '删除者',
  delete_time datetime DEFAULT NULL COMMENT '删除时间',
  tenant_id varchar(64) DEFAULT NULL COMMENT '租户ID',
  PRIMARY KEY (id),
  KEY idx_fd_focus18_tenant (tenant_id),
  KEY idx_fd_focus18_class_code (tenant_id, class_code),
  KEY idx_fd_focus18_generic (tenant_id, generic_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='18类重点耗材维护';

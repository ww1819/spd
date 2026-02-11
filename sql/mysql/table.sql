-- mysql 追加表（按「/」分段，每段一条语句执行）
/
CREATE TABLE IF NOT EXISTS `fd_material_import` (
  `id` varchar(36) NOT NULL COMMENT '主键UUID7',
  `code` varchar(64) DEFAULT NULL COMMENT '耗材编码',
  `name` varchar(255) DEFAULT NULL COMMENT '耗材名称',
  `supplier_value` varchar(255) DEFAULT NULL COMMENT '导入的供应商原始值（名称或ID）',
  `factory_value` varchar(255) DEFAULT NULL COMMENT '导入的生产厂家原始值（名称或ID）',
  `warehouse_category_value` varchar(255) DEFAULT NULL COMMENT '导入的库房分类原始值（名称或ID）',
  `finance_category_value` varchar(255) DEFAULT NULL COMMENT '导入的财务分类原始值（名称或ID）',
  `unit_value` varchar(255) DEFAULT NULL COMMENT '导入的单位原始值（名称或ID）',
  `location_value` varchar(255) DEFAULT NULL COMMENT '导入的货位原始值（名称或ID）',
  `raw_data` text COMMENT '导入的原始整行数据（JSON）',
  `import_time` datetime NOT NULL COMMENT '导入时间',
  `operator` varchar(64) NOT NULL COMMENT '导入操作人',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='耗材产品导入中间表';

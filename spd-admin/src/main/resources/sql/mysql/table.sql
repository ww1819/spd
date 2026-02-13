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
/

CREATE TABLE IF NOT EXISTS `wh_fixed_number` (
  `id` varchar(36) NOT NULL COMMENT '主键UUID7',
  `material_id` bigint NOT NULL COMMENT '耗材产品ID（关联fd_material.id）',
  `warehouse_id` bigint NOT NULL COMMENT '仓库ID',
  `upper_limit` int NOT NULL DEFAULT 0 COMMENT '上限数量',
  `lower_limit` int NOT NULL DEFAULT 0 COMMENT '下限数量',
  `expiry_reminder` int DEFAULT NULL COMMENT '有效期提醒天数',
  `monitoring` char(1) NOT NULL DEFAULT '1' COMMENT '是否监测 1=是 2=否',
  `location` varchar(128) DEFAULT NULL COMMENT '货位',
  `location_id` bigint DEFAULT NULL COMMENT '货位ID',
  `remark` varchar(512) DEFAULT NULL COMMENT '备注',
  `del_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除标志（0正常 1删除）',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新人',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `delete_by` varchar(64) DEFAULT NULL COMMENT '删除人',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_wh_fixed_number_material` (`warehouse_id`,`material_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='仓库定数监测表';
/

CREATE TABLE IF NOT EXISTS `dept_fixed_number` (
  `id` varchar(36) NOT NULL COMMENT '主键UUID7',
  `material_id` bigint NOT NULL COMMENT '耗材产品ID（关联fd_material.id）',
  `department_id` bigint NOT NULL COMMENT '科室ID（关联fd_department.id）',
  `upper_limit` int NOT NULL DEFAULT 0 COMMENT '上限数量',
  `lower_limit` int NOT NULL DEFAULT 0 COMMENT '下限数量',
  `expiry_reminder` int DEFAULT NULL COMMENT '有效期提醒天数',
  `monitoring` char(1) NOT NULL DEFAULT '1' COMMENT '是否监测 1=是 2=否',
  `location` varchar(128) DEFAULT NULL COMMENT '货位',
  `location_id` bigint DEFAULT NULL COMMENT '货位ID',
  `remark` varchar(512) DEFAULT NULL COMMENT '备注',
  `del_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除标志（0正常 1删除）',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新人',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `delete_by` varchar(64) DEFAULT NULL COMMENT '删除人',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_dept_fixed_number_material` (`department_id`,`material_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='科室定数监测表';

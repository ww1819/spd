-- ========== 耗材模块 建表脚本 ==========
-- 执行顺序建议：1.table.sql 2.column.sql(含存储过程与增量字段) 3.menu.sql 4.data_integrity.sql 5.function.sql/procedure.sql/trigger.sql/view.sql 按需执行
-- 本脚本为完整建表 DDL；期初库存导入明细表 stk_initial_import_entry 已包含 column.sql 中新增字段（第三方ID、耗材编码/规格/型号/注册证/医保/条码等）
-- 按「/」分段，每段一条语句执行
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
/

-- 期初库存导入主表：操作人、导入时间、库存生成时间（审核时填）；审核前不生成批次与库存；主键UUID7
CREATE TABLE IF NOT EXISTS `stk_initial_import` (
  `id` varchar(36) NOT NULL COMMENT '主键UUID7',
  `bill_no` varchar(64) NOT NULL COMMENT '期初单号（如QC+yyyyMMdd+流水）',
  `warehouse_id` bigint(20) NOT NULL COMMENT '所属仓库ID',
  `import_operator` varchar(64) NOT NULL COMMENT '导入操作人',
  `import_time` datetime NOT NULL COMMENT '导入时间',
  `stock_gen_time` datetime DEFAULT NULL COMMENT '库存生成时间（审核时写入）',
  `bill_status` int(1) NOT NULL DEFAULT 0 COMMENT '单据状态 0=待审核 1=已审核',
  `remark` varchar(512) DEFAULT NULL COMMENT '备注',
  `del_flag` int(1) NOT NULL DEFAULT 0 COMMENT '删除标志（0正常 1删除）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新人',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `audit_by` varchar(64) DEFAULT NULL COMMENT '审核人',
  `audit_time` datetime DEFAULT NULL COMMENT '审核时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_stk_initial_import_bill_no` (`bill_no`),
  KEY `idx_warehouse_id` (`warehouse_id`),
  KEY `idx_bill_status` (`bill_status`),
  KEY `idx_import_time` (`import_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='期初库存导入主表';
/

-- 期初库存导入明细表：单价、数量、批号、效期、生产厂家、供应商、所属仓库等；批次号在生成单据时自动生成；主键UUID7
CREATE TABLE IF NOT EXISTS `stk_initial_import_entry` (
  `id` varchar(36) NOT NULL COMMENT '主键UUID7',
  `paren_id` varchar(36) NOT NULL COMMENT '期初导入主表ID',
  `material_id` bigint(20) NOT NULL COMMENT '耗材ID',
  `warehouse_id` bigint(20) NOT NULL COMMENT '所属仓库ID',
  `unit_price` decimal(18,2) DEFAULT NULL COMMENT '单价',
  `qty` decimal(18,2) NOT NULL DEFAULT 0.00 COMMENT '数量',
  `amt` decimal(18,2) DEFAULT NULL COMMENT '金额',
  `batch_no` varchar(100) DEFAULT NULL COMMENT '批次号（自动生成）',
  `batch_number` varchar(100) DEFAULT NULL COMMENT '批号',
  `begin_time` date DEFAULT NULL COMMENT '生产日期',
  `end_time` date DEFAULT NULL COMMENT '效期',
  `factory_id` bigint(20) DEFAULT NULL COMMENT '生产厂家ID',
  `supplier_id` bigint(20) DEFAULT NULL COMMENT '供应商ID',
  `third_party_detail_id` varchar(64) DEFAULT NULL COMMENT '第三方系统库存明细ID',
  `third_party_material_id` varchar(64) DEFAULT NULL COMMENT '第三方系统产品档案ID',
  `material_code` varchar(64) DEFAULT NULL COMMENT '耗材编码',
  `speci` varchar(255) DEFAULT NULL COMMENT '规格',
  `model` varchar(255) DEFAULT NULL COMMENT '型号',
  `register_no` varchar(128) DEFAULT NULL COMMENT '注册证号',
  `medical_no` varchar(64) DEFAULT NULL COMMENT '医保编码',
  `medical_name` varchar(255) DEFAULT NULL COMMENT '医保名称',
  `main_barcode` varchar(128) DEFAULT NULL COMMENT '主条码',
  `del_flag` int(1) NOT NULL DEFAULT 0 COMMENT '删除标志（0正常 1删除）',
  `sort_order` int(11) DEFAULT 0 COMMENT '排序',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_paren_id` (`paren_id`),
  KEY `idx_material_id` (`material_id`),
  KEY `idx_warehouse_id` (`warehouse_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='期初库存导入明细表';
/

-- 科室申领制单模板主表（与打印设置无关，单独维护）
CREATE TABLE IF NOT EXISTS `bas_apply_template` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `template_name` varchar(128) NOT NULL COMMENT '模板名称',
  `warehouse_id` bigint(20) DEFAULT NULL COMMENT '仓库ID',
  `remark` varchar(512) DEFAULT NULL COMMENT '备注',
  `del_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除标志（0正常 1删除）',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新人',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_template_name` (`template_name`),
  KEY `idx_warehouse_id` (`warehouse_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='科室申领制单模板主表';
/

-- 科室申领制单模板明细表
CREATE TABLE IF NOT EXISTS `bas_apply_template_entry` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `paren_id` bigint(20) NOT NULL COMMENT '模板主表ID',
  `material_id` bigint(20) NOT NULL COMMENT '耗材ID',
  `qty` decimal(18,2) NOT NULL DEFAULT 1.00 COMMENT '数量',
  `sort_order` int(11) DEFAULT 0 COMMENT '排序',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_paren_id` (`paren_id`),
  KEY `idx_material_id` (`material_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='科室申领制单模板明细表';
/

-- 耗材档案启用停用记录表（产品档案启用/停用流水）；主键UUID7
CREATE TABLE IF NOT EXISTS `fd_material_status_log` (
  `id` varchar(36) NOT NULL COMMENT '主键UUID7',
  `material_id` bigint(20) NOT NULL COMMENT '产品档案ID（关联fd_material.id）',
  `action` varchar(16) NOT NULL COMMENT '操作类型：enable=启用，disable=停用',
  `action_time` datetime NOT NULL COMMENT '启用/停用时间',
  `operator` varchar(64) NOT NULL COMMENT '操作人',
  `reason` varchar(512) DEFAULT NULL COMMENT '启用/停用原因',
  PRIMARY KEY (`id`),
  KEY `idx_material_id` (`material_id`),
  KEY `idx_action_time` (`action_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='耗材档案启用停用记录表';
/

-- 产品档案变更记录表；主键UUID7
CREATE TABLE IF NOT EXISTS `fd_material_change_log` (
  `id` varchar(36) NOT NULL COMMENT '主键UUID7',
  `material_id` bigint(20) NOT NULL COMMENT '产品档案ID（关联fd_material.id）',
  `change_time` datetime NOT NULL COMMENT '变更时间',
  `operator` varchar(64) NOT NULL COMMENT '操作人',
  `field_name` varchar(64) NOT NULL COMMENT '字段名（英文）',
  `field_label` varchar(64) DEFAULT NULL COMMENT '字段中文名',
  `old_value` text COMMENT '原值',
  `new_value` text COMMENT '新值',
  PRIMARY KEY (`id`),
  KEY `idx_material_id` (`material_id`),
  KEY `idx_change_time` (`change_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='产品档案变更记录表';
/

-- 耗材系统 SaaS 表（与设备共用 sb_customer 客户列表，耗材侧单独建表并与租户 tenant_id 关联）
-- 执行时按「/」分段执行
-- 耗材工作组：使用系统岗位表 sys_post（及 sys_user_post、sys_post_menu 等），不单独建 hc_work_group 表。

-- 耗材用户菜单权限（工作人员菜单权限，与租户关联）
CREATE TABLE IF NOT EXISTS `hc_user_permission_menu` (
  `id` char(36) NOT NULL COMMENT '主键UUID7',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `tenant_id` char(36) NOT NULL COMMENT '租户ID',
  `menu_id` bigint(20) NOT NULL COMMENT '菜单ID(关联sys_menu.menu_id)',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_hc_upm_user_menu` (`user_id`, `menu_id`),
  KEY `idx_hc_upm_tenant` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='耗材用户菜单权限';
/
-- 耗材客户菜单权限（客户在耗材侧可用的菜单，与租户关联；客户列表共用 sb_customer）
CREATE TABLE IF NOT EXISTS `hc_customer_menu` (
  `tenant_id` char(36) NOT NULL COMMENT '租户ID(同customer_id)',
  `menu_id` bigint(20) NOT NULL COMMENT '菜单ID(关联sys_menu.menu_id)',
  `status` char(1) DEFAULT '0' COMMENT '暂停状态（0正常 1暂停）',
  `is_enabled` char(1) DEFAULT '1' COMMENT '是否开启（0关闭 1开启）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`tenant_id`,`menu_id`),
  KEY `idx_hc_cm_menu_id` (`menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='耗材系统客户菜单权限表';
/
-- 耗材客户菜单功能启停用记录表
CREATE TABLE IF NOT EXISTS `hc_customer_menu_status_log` (
  `log_id` char(36) NOT NULL COMMENT '记录ID(UUID7)',
  `tenant_id` char(36) NOT NULL COMMENT '租户ID',
  `menu_id` bigint(20) NOT NULL COMMENT '菜单ID',
  `status` char(1) NOT NULL COMMENT '状态（0启用 1停用）',
  `operate_time` datetime NOT NULL COMMENT '操作时间',
  `operate_by` varchar(64) DEFAULT '' COMMENT '操作人',
  `reason` varchar(500) DEFAULT NULL COMMENT '启停用原因',
  PRIMARY KEY (`log_id`),
  KEY `idx_hc_cm_slog_tenant_menu` (`tenant_id`,`menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='耗材客户菜单功能启停用记录表';
/
-- 耗材客户菜单功能启停用时间段表
CREATE TABLE IF NOT EXISTS `hc_customer_menu_period_log` (
  `period_id` char(36) NOT NULL COMMENT '记录ID(UUID7)',
  `tenant_id` char(36) NOT NULL COMMENT '租户ID',
  `menu_id` bigint(20) NOT NULL COMMENT '菜单ID',
  `period_type` varchar(20) NOT NULL COMMENT '类型：usage=使用时段，suspend=停用时段',
  `start_time` datetime NOT NULL COMMENT '开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '结束时间，NULL表示未结束',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`period_id`),
  KEY `idx_hc_cm_plog_tenant_menu` (`tenant_id`,`menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='耗材客户菜单功能启停用时间段表';
/
-- 耗材客户启停用记录表（客户在耗材侧启停记录）
CREATE TABLE IF NOT EXISTS `hc_customer_status_log` (
  `log_id` char(36) NOT NULL COMMENT '记录ID(UUID7)',
  `tenant_id` char(36) NOT NULL COMMENT '租户ID',
  `status` char(1) NOT NULL COMMENT '状态（0启用 1停用）',
  `operate_time` datetime NOT NULL COMMENT '操作时间',
  `operate_by` varchar(64) DEFAULT '' COMMENT '操作人',
  `reason` varchar(500) DEFAULT NULL COMMENT '启停用原因',
  PRIMARY KEY (`log_id`),
  KEY `idx_hc_csl_tenant` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='耗材客户启停用记录表';
/
-- 耗材客户实际使用/停用时间段表
CREATE TABLE IF NOT EXISTS `hc_customer_period_log` (
  `period_id` char(36) NOT NULL COMMENT '记录ID(UUID7)',
  `tenant_id` char(36) NOT NULL COMMENT '租户ID',
  `period_type` varchar(20) NOT NULL COMMENT '类型：usage=实际使用时段，suspend=实际停用时段',
  `start_time` datetime NOT NULL COMMENT '开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '结束时间，NULL表示当前未结束',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`period_id`),
  KEY `idx_hc_cpl_tenant` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='耗材客户实际使用/停用时间段表';
/

-- ========== 发票管理（主键UUID7，租户、删除者/删除时间、审核状态/审核人/审核时间） ==========
CREATE TABLE IF NOT EXISTS `fin_invoice` (
  `id` varchar(36) NOT NULL COMMENT '主键UUID7',
  `tenant_id` varchar(36) DEFAULT NULL COMMENT '租户ID(同sb_customer.customer_id)',
  `invoice_no` varchar(64) DEFAULT NULL COMMENT '发票号码',
  `invoice_code` varchar(64) DEFAULT NULL COMMENT '发票代码',
  `invoice_date` date DEFAULT NULL COMMENT '开票日期',
  `amount` decimal(18,2) DEFAULT NULL COMMENT '金额',
  `tax_amount` decimal(18,2) DEFAULT NULL COMMENT '税额',
  `total_amount` decimal(18,2) DEFAULT NULL COMMENT '价税合计',
  `buyer_name` varchar(255) DEFAULT NULL COMMENT '购方名称',
  `buyer_tax_no` varchar(64) DEFAULT NULL COMMENT '购方税号',
  `seller_name` varchar(255) DEFAULT NULL COMMENT '销方名称',
  `seller_tax_no` varchar(64) DEFAULT NULL COMMENT '销方税号',
  `supplier_id` bigint(20) DEFAULT NULL COMMENT '供应商ID',
  `audit_status` int(1) NOT NULL DEFAULT 0 COMMENT '审核状态 0=待审核 1=已审核',
  `audit_by` varchar(64) DEFAULT NULL COMMENT '审核人',
  `audit_time` datetime DEFAULT NULL COMMENT '审核时间',
  `del_flag` int(1) NOT NULL DEFAULT 0 COMMENT '删除标志（0正常 1删除）',
  `delete_by` varchar(64) DEFAULT NULL COMMENT '删除者',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  `remark` varchar(512) DEFAULT NULL COMMENT '备注',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新人',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_fin_invoice_tenant` (`tenant_id`),
  KEY `idx_fin_invoice_supplier` (`supplier_id`),
  KEY `idx_fin_invoice_audit` (`audit_status`),
  KEY `idx_fin_invoice_date` (`invoice_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='发票管理表';
/

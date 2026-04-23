-- ========== 耗材模块 建表脚本 ==========
-- 执行顺序建议：1.table.sql 2.column.sql（存储过程 add_table_column + 存量库增量字段）3.menu.sql 4.data_integrity.sql 5.function.sql/procedure.sql/trigger.sql/view.sql 按需执行
-- 分工：本文件为全量 CREATE TABLE IF NOT EXISTS（及必要的 INSERT 种子）；column.sql 仅含 add_table_column 存储过程与 CALL/动态 ALTER 等增量，不含 CREATE TABLE。
-- 本脚本已含：出入库/库存/批次、仓库与科室流水、盘点/盈亏、科室批量消耗、期初导入、结算与 SaaS 权限、打印设置、档案变更日志（fd_*_change_log）、盘盈待入账（stk_profit_loss_pending）、库房申请单（wh_warehouse_apply / wh_warehouse_apply_entry / wh_wh_apply_ck_entry_ref）、高值备货引用（gz_order_entry_code_ref / gz_shipment_entry_ref / gz_refund_goods_entry_ref）、租户级 HIS 外联（sys_his_external_db）等；存量库若已建表可跳过对应 CREATE。
-- 说明：fd_material / fd_warehouse / fd_material_category 等若依基础表通常来自主库初始化 SQL，未重复写入本文件；历史增量字段仍由 column.sql 补齐。
-- 按「/」分段，每段一条语句执行
/

-- 科室主数据（科室定数等关联 fd_department.id；tenant_id 同 sb_customer.customer_id）
CREATE TABLE IF NOT EXISTS `fd_department` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `code` varchar(64) DEFAULT NULL COMMENT '科室编码',
  `name` varchar(255) DEFAULT NULL COMMENT '科室名称',
  `referred_name` varchar(64) DEFAULT NULL COMMENT '名称简码（拼音简码）',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `his_id` varchar(128) DEFAULT NULL COMMENT 'HIS系统科室ID',
  `del_flag` int NOT NULL DEFAULT 0 COMMENT '删除标志（0正常 1删除）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新人',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `tenant_id` varchar(36) DEFAULT NULL COMMENT '租户ID(同sb_customer.customer_id)',
  `parent_id` bigint DEFAULT NULL COMMENT '上级科室ID（NULL表示客户下顶级）',
  PRIMARY KEY (`id`),
  KEY `idx_fd_department_tenant_code` (`tenant_id`,`code`),
  KEY `idx_fd_department_parent` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='科室主数据';
/

-- 科室档案字段变更记录（主键 UUID7）
CREATE TABLE IF NOT EXISTS `fd_department_change_log` (
  `id` varchar(36) NOT NULL COMMENT '主键UUID7',
  `department_id` bigint NOT NULL COMMENT '科室ID（fd_department.id）',
  `change_time` datetime NOT NULL COMMENT '变更时间',
  `operator` varchar(64) NOT NULL COMMENT '操作人',
  `field_name` varchar(64) NOT NULL COMMENT '字段名（英文）',
  `field_label` varchar(64) DEFAULT NULL COMMENT '字段中文名',
  `old_value` text COMMENT '原值',
  `new_value` text COMMENT '新值',
  `tenant_id` varchar(36) DEFAULT NULL COMMENT '租户ID(同sb_customer.customer_id)',
  PRIMARY KEY (`id`),
  KEY `idx_fd_dept_log_dept` (`department_id`),
  KEY `idx_fd_dept_log_time` (`change_time`),
  KEY `idx_fd_dept_log_tenant` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='科室档案变更记录';
/

-- 供应商主数据（业务表 stk_io_bill.suppler_id 等关联 fd_supplier.id）
CREATE TABLE IF NOT EXISTS `fd_supplier` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `code` varchar(64) DEFAULT NULL COMMENT '供应商编码',
  `name` varchar(255) DEFAULT NULL COMMENT '供应商名称',
  `del_flag` int NOT NULL DEFAULT 0 COMMENT '删除标志（0正常 1删除）',
  `tax_number` varchar(64) DEFAULT NULL COMMENT '税号',
  `referred_code` varchar(64) DEFAULT NULL COMMENT '名称简码',
  `reg_money` decimal(18,2) DEFAULT NULL COMMENT '注册资金',
  `valid_time` date DEFAULT NULL COMMENT '资质有效期',
  `contacts` varchar(64) DEFAULT NULL COMMENT '联系人',
  `contacts_phone` varchar(32) DEFAULT NULL COMMENT '联系电话',
  `website` varchar(255) DEFAULT NULL COMMENT '网址',
  `legal_person` varchar(64) DEFAULT NULL COMMENT '法人',
  `zip_code` varchar(16) DEFAULT NULL COMMENT '邮编',
  `email` varchar(128) DEFAULT NULL COMMENT '邮箱',
  `address` varchar(500) DEFAULT NULL COMMENT '地址',
  `company_person` varchar(64) DEFAULT NULL COMMENT '公司负责人',
  `phone` varchar(32) DEFAULT NULL COMMENT '电话',
  `cert_number` varchar(128) DEFAULT NULL COMMENT '证件号',
  `fax` varchar(32) DEFAULT NULL COMMENT '传真',
  `bank_account` varchar(128) DEFAULT NULL COMMENT '银行账号',
  `company_referred` varchar(128) DEFAULT NULL COMMENT '公司简称',
  `supplier_range` varchar(2000) DEFAULT NULL COMMENT '经营范围',
  `supplier_status` varchar(16) DEFAULT NULL COMMENT '状态',
  `supplier_type` varchar(255) DEFAULT NULL COMMENT '供应商类型（可多选逗号分隔）',
  `his_id` varchar(128) DEFAULT NULL COMMENT 'HIS供应商ID（衡水市第三人民医院租户内必填且唯一，保存后不可改）',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新人',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `delete_by` varchar(64) DEFAULT NULL COMMENT '删除者',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  `tenant_id` varchar(36) DEFAULT NULL COMMENT '租户ID(同sb_customer.customer_id)',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_fd_supplier_tenant_his` (`tenant_id`,`his_id`),
  KEY `idx_fd_supplier_tenant_code` (`tenant_id`,`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='供应商主数据';
/

-- 供应商档案字段变更记录
CREATE TABLE IF NOT EXISTS `fd_supplier_change_log` (
  `id` varchar(36) NOT NULL COMMENT '主键UUID7',
  `supplier_id` bigint NOT NULL COMMENT '供应商ID（fd_supplier.id）',
  `change_time` datetime NOT NULL COMMENT '变更时间',
  `operator` varchar(64) NOT NULL COMMENT '操作人',
  `field_name` varchar(64) NOT NULL COMMENT '字段名（英文）',
  `field_label` varchar(64) DEFAULT NULL COMMENT '字段中文名',
  `old_value` text COMMENT '原值',
  `new_value` text COMMENT '新值',
  `tenant_id` varchar(36) DEFAULT NULL COMMENT '租户ID(同sb_customer.customer_id)',
  PRIMARY KEY (`id`),
  KEY `idx_fd_supplier_log_supp` (`supplier_id`),
  KEY `idx_fd_supplier_log_time` (`change_time`),
  KEY `idx_fd_supplier_log_tenant` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='供应商档案变更记录';
/

-- 生产厂家主数据（业务表 fd_material.factory_id 等关联 fd_factory.factory_id）
CREATE TABLE IF NOT EXISTS `fd_factory` (
  `factory_id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `factory_code` varchar(64) DEFAULT NULL COMMENT '厂家编码',
  `factory_name` varchar(255) DEFAULT NULL COMMENT '厂家名称',
  `factory_address` varchar(500) DEFAULT NULL COMMENT '厂家地址',
  `factory_contact` varchar(128) DEFAULT NULL COMMENT '厂家联系方式',
  `factory_referred_code` varchar(64) DEFAULT NULL COMMENT '厂家简码',
  `factory_status` varchar(16) DEFAULT NULL COMMENT '状态',
  `his_id` varchar(128) DEFAULT NULL COMMENT 'HIS生产厂家ID（衡水市第三人民医院租户内必填且唯一，保存后不可改）',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `del_flag` int NOT NULL DEFAULT 0 COMMENT '删除标志（0正常 1删除）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新人',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `delete_by` varchar(64) DEFAULT NULL COMMENT '删除者',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  `tenant_id` varchar(36) DEFAULT NULL COMMENT '租户ID(同sb_customer.customer_id)',
  PRIMARY KEY (`factory_id`),
  UNIQUE KEY `uk_fd_factory_tenant_his` (`tenant_id`,`his_id`),
  KEY `idx_fd_factory_tenant_code` (`tenant_id`,`factory_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='生产厂家主数据';
/

CREATE TABLE IF NOT EXISTS `fd_factory_change_log` (
  `id` varchar(36) NOT NULL COMMENT '主键UUID7',
  `factory_id` bigint NOT NULL COMMENT '生产厂家ID（fd_factory.factory_id）',
  `change_time` datetime NOT NULL COMMENT '变更时间',
  `operator` varchar(64) NOT NULL COMMENT '操作人',
  `field_name` varchar(64) NOT NULL COMMENT '字段名（英文）',
  `field_label` varchar(64) DEFAULT NULL COMMENT '字段中文名',
  `old_value` text COMMENT '原值',
  `new_value` text COMMENT '新值',
  `tenant_id` varchar(36) DEFAULT NULL COMMENT '租户ID(同sb_customer.customer_id)',
  PRIMARY KEY (`id`),
  KEY `idx_fd_factory_log_fid` (`factory_id`),
  KEY `idx_fd_factory_log_time` (`change_time`),
  KEY `idx_fd_factory_log_tenant` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='生产厂家档案变更记录';
/

-- 计量单位（业务表 fd_material.unit_id 等关联 fd_unit.unit_id）
CREATE TABLE IF NOT EXISTS `fd_unit` (
  `unit_id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `unit_code` varchar(64) DEFAULT NULL COMMENT '单位编码',
  `unit_name` varchar(255) DEFAULT NULL COMMENT '单位名称',
  `del_flag` int NOT NULL DEFAULT 0 COMMENT '删除标志（0正常 1删除）',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新人',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `delete_by` varchar(64) DEFAULT NULL COMMENT '删除者',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  `tenant_id` varchar(36) DEFAULT NULL COMMENT '租户ID(同sb_customer.customer_id)',
  PRIMARY KEY (`unit_id`),
  KEY `idx_fd_unit_tenant_code` (`tenant_id`,`unit_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='计量单位';
/

-- 货位（业务表 fd_material.location_id 等关联 fd_location.location_id）
CREATE TABLE IF NOT EXISTS `fd_location` (
  `location_id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `parent_id` bigint DEFAULT NULL COMMENT '父货位ID（0或NULL表示顶级）',
  `location_code` varchar(64) DEFAULT NULL COMMENT '货位编码',
  `location_name` varchar(255) DEFAULT NULL COMMENT '货位名称',
  `warehouse_id` bigint DEFAULT NULL COMMENT '仓库ID（fd_warehouse.id）',
  `del_flag` int NOT NULL DEFAULT 0 COMMENT '删除标志（0正常 1删除）',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新人',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `delete_by` varchar(64) DEFAULT NULL COMMENT '删除者',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  `tenant_id` varchar(36) DEFAULT NULL COMMENT '租户ID(同sb_customer.customer_id)',
  PRIMARY KEY (`location_id`),
  KEY `idx_fd_location_tenant` (`tenant_id`),
  KEY `idx_fd_location_wh` (`warehouse_id`),
  KEY `idx_fd_location_parent` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='货位';
/

-- 库房分类（业务表 fd_material.storeroom_id 等关联 warehouse_category_id）
CREATE TABLE IF NOT EXISTS `fd_warehouse_category` (
  `warehouse_category_id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `parent_id` bigint DEFAULT NULL COMMENT '父分类ID（0或NULL表示顶级）',
  `warehouse_category_code` varchar(64) DEFAULT NULL COMMENT '库房分类编码',
  `warehouse_category_name` varchar(255) DEFAULT NULL COMMENT '库房分类名称',
  `referred_name` varchar(64) DEFAULT NULL COMMENT '名称简码（拼音简码）',
  `remark` varchar(512) DEFAULT NULL COMMENT '备注',
  `del_flag` tinyint NOT NULL DEFAULT 0 COMMENT '删除标志（0正常 1删除）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新人',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `delete_by` varchar(64) DEFAULT NULL COMMENT '删除者',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  `tenant_id` varchar(36) DEFAULT NULL COMMENT '租户ID(同sb_customer.customer_id)',
  `his_id` varchar(128) DEFAULT NULL COMMENT 'HIS系统库房分类ID',
  PRIMARY KEY (`warehouse_category_id`),
  KEY `idx_fd_wh_cat_tenant` (`tenant_id`),
  KEY `idx_fd_wh_cat_parent` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库房分类';
/

-- 财务分类（业务表 fd_material.finance_category_id 等关联 finance_category_id）
CREATE TABLE IF NOT EXISTS `fd_finance_category` (
  `finance_category_id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `parent_id` bigint DEFAULT NULL COMMENT '父分类ID（0或NULL表示顶级）',
  `finance_category_code` varchar(64) DEFAULT NULL COMMENT '财务分类编码',
  `finance_category_name` varchar(255) DEFAULT NULL COMMENT '财务分类名称',
  `referred_name` varchar(64) DEFAULT NULL COMMENT '名称简码',
  `finance_category_address` varchar(500) DEFAULT NULL COMMENT '地址',
  `finance_category_contact` varchar(128) DEFAULT NULL COMMENT '联系方式',
  `is_use` char(1) DEFAULT '1' COMMENT '使用状态（字典 is_use_status）',
  `remark` varchar(512) DEFAULT NULL COMMENT '备注',
  `del_flag` tinyint NOT NULL DEFAULT 0 COMMENT '删除标志（0正常 1删除）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新人',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `delete_by` varchar(64) DEFAULT NULL COMMENT '删除者',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  `tenant_id` varchar(36) DEFAULT NULL COMMENT '租户ID(同sb_customer.customer_id)',
  `his_id` varchar(128) DEFAULT NULL COMMENT 'HIS系统财务分类ID',
  PRIMARY KEY (`finance_category_id`),
  KEY `idx_fd_fin_cat_tenant` (`tenant_id`),
  KEY `idx_fd_fin_cat_parent` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='财务分类';
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
  `tenant_id` varchar(36) DEFAULT NULL COMMENT '租户ID(同sb_customer.customer_id)',
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
  `tenant_id` varchar(36) DEFAULT NULL COMMENT '租户ID(同sb_customer.customer_id)',
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
  `delete_by` varchar(64) DEFAULT NULL COMMENT '删除者',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  `tenant_id` varchar(36) DEFAULT NULL COMMENT '租户ID(同sb_customer.customer_id)',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_stk_initial_import_bill_no` (`bill_no`),
  KEY `idx_warehouse_id` (`warehouse_id`),
  KEY `idx_bill_status` (`bill_status`),
  KEY `idx_import_time` (`import_time`),
  KEY `idx_stk_initial_import_tenant` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='期初库存导入主表';
/

-- 期初库存导入明细表：单价、数量、批号、效期、生产厂家、供应商、所属仓库等；批次号在生成单据时自动生成；主键UUID7
CREATE TABLE IF NOT EXISTS `stk_initial_import_entry` (
  `id` varchar(36) NOT NULL COMMENT '主键UUID7',
  `paren_id` varchar(36) NOT NULL COMMENT '期初导入主表ID',
  `material_id` bigint(20) NOT NULL COMMENT '耗材ID',
  `warehouse_id` bigint(20) NOT NULL COMMENT '所属仓库ID',
  `unit_price` decimal(18,6) DEFAULT NULL COMMENT '单价（六位小数）',
  `qty` decimal(18,6) NOT NULL DEFAULT 0.000000 COMMENT '数量（六位小数）',
  `amt` decimal(18,6) DEFAULT NULL COMMENT '金额（六位小数）',
  `batch_no` varchar(100) DEFAULT NULL COMMENT '批次号（自动生成）',
  `batch_number` varchar(100) DEFAULT NULL COMMENT '批号',
  `begin_time` date DEFAULT NULL COMMENT '生产日期',
  `end_time` date DEFAULT NULL COMMENT '效期',
  `factory_id` bigint(20) DEFAULT NULL COMMENT '生产厂家ID',
  `supplier_id` bigint(20) DEFAULT NULL COMMENT '供应商ID',
  `his_id` varchar(128) DEFAULT NULL COMMENT '第三方/HIS系统库存明细ID（对应导入列，业务主键追溯）',
  `third_party_material_id` varchar(64) DEFAULT NULL COMMENT '第三方系统产品档案ID',
  `material_code` varchar(64) DEFAULT NULL COMMENT '耗材编码',
  `speci` varchar(255) DEFAULT NULL COMMENT '规格',
  `model` varchar(255) DEFAULT NULL COMMENT '型号',
  `register_no` varchar(128) DEFAULT NULL COMMENT '注册证号',
  `medical_no` varchar(64) DEFAULT NULL COMMENT '医保编码',
  `medical_name` varchar(255) DEFAULT NULL COMMENT '医保名称',
  `main_barcode` varchar(128) DEFAULT NULL COMMENT '主条码',
  `sub_barcode` varchar(128) DEFAULT NULL COMMENT '高值耗材辅条码',
  `del_flag` int(1) NOT NULL DEFAULT 0 COMMENT '删除标志（0正常 1删除）',
  `delete_by` varchar(64) DEFAULT NULL COMMENT '删除者',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  `sort_order` int(11) DEFAULT 0 COMMENT '排序',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `tenant_id` varchar(36) DEFAULT NULL COMMENT '租户ID',
  PRIMARY KEY (`id`),
  KEY `idx_paren_id` (`paren_id`),
  KEY `idx_material_id` (`material_id`),
  KEY `idx_warehouse_id` (`warehouse_id`),
  KEY `idx_stk_initial_import_entry_tenant` (`tenant_id`)
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
  `delete_by` varchar(64) DEFAULT NULL COMMENT '删除者',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  `tenant_id` varchar(36) DEFAULT NULL COMMENT '租户ID(同sb_customer.customer_id)',
  PRIMARY KEY (`id`),
  KEY `idx_template_name` (`template_name`),
  KEY `idx_warehouse_id` (`warehouse_id`),
  KEY `idx_bas_apply_template_tenant` (`tenant_id`)
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
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `delete_by` varchar(64) DEFAULT NULL COMMENT '删除者',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  `tenant_id` varchar(36) DEFAULT NULL COMMENT '租户ID',
  PRIMARY KEY (`id`),
  KEY `idx_paren_id` (`paren_id`),
  KEY `idx_material_id` (`material_id`),
  KEY `idx_bas_apply_template_entry_tenant` (`tenant_id`)
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
  `tenant_id` varchar(36) DEFAULT NULL COMMENT '租户ID(同sb_customer.customer_id)',
  PRIMARY KEY (`id`),
  KEY `idx_material_id` (`material_id`),
  KEY `idx_action_time` (`action_time`),
  KEY `idx_fd_material_status_log_tenant` (`tenant_id`)
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
  `tenant_id` varchar(36) DEFAULT NULL COMMENT '租户ID(同sb_customer.customer_id)',
  PRIMARY KEY (`id`),
  KEY `idx_material_id` (`material_id`),
  KEY `idx_change_time` (`change_time`),
  KEY `idx_fd_material_change_log_tenant` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='产品档案变更记录表';
/

-- 耗材系统 SaaS 表（与设备共用 sb_customer 客户列表，耗材侧单独建表并与租户 tenant_id 关联）
-- 执行时按「/」分段执行
-- 耗材工作组：使用系统岗位表 sys_post（及 sys_user_post、sys_post_menu 等），不单独建 hc_work_group 表。

-- 用户与岗位(耗材工作组)关联（与 sys_post.post_id、sys_user.user_id；tenant_id 与 sys_user.customer_id 对齐）
CREATE TABLE IF NOT EXISTS `sys_user_post` (
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `post_id` bigint NOT NULL COMMENT '岗位/工作组ID(sys_post.post_id)',
  `tenant_id` varchar(36) DEFAULT NULL COMMENT '租户ID(同sys_user.customer_id)',
  PRIMARY KEY (`user_id`,`post_id`),
  KEY `idx_sys_user_post_post` (`post_id`),
  KEY `idx_sys_user_post_tenant` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户与岗位(工作组)关联';
/

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

-- ========== 仓库结算单（主键UUID7，含客户id；审核后生成供应商结算单） ==========
CREATE TABLE IF NOT EXISTS `wh_settlement_bill` (
  `id` varchar(36) NOT NULL COMMENT '主键UUID7',
  `tenant_id` varchar(36) DEFAULT NULL COMMENT '客户/租户ID(同sb_customer.customer_id)',
  `bill_no` varchar(64) NOT NULL COMMENT '仓库结算单号',
  `warehouse_id` bigint(20) NOT NULL COMMENT '仓库ID',
  `warehouse_code` varchar(64) DEFAULT NULL COMMENT '仓库编码',
  `warehouse_name` varchar(200) DEFAULT NULL COMMENT '仓库名称',
  `settlement_method` varchar(16) NOT NULL COMMENT '结算方式 1入库结算 2出库结算 3消耗结算',
  `create_by` varchar(64) DEFAULT NULL COMMENT '制单人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '制单时间',
  `audit_by` varchar(64) DEFAULT NULL COMMENT '审核人',
  `audit_time` datetime DEFAULT NULL COMMENT '审核时间',
  `audit_status` int(1) NOT NULL DEFAULT 0 COMMENT '审核状态 0待审核 1已审核',
  `del_flag` int(1) NOT NULL DEFAULT 0 COMMENT '删除标志（0正常 1删除）',
  `delete_by` varchar(64) DEFAULT NULL COMMENT '删除者',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  `remark` varchar(512) DEFAULT NULL COMMENT '备注',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新人',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_wh_settlement_bill_no` (`bill_no`),
  KEY `idx_wh_settlement_warehouse` (`warehouse_id`),
  KEY `idx_wh_settlement_tenant` (`tenant_id`),
  KEY `idx_wh_settlement_audit` (`audit_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='仓库结算单主表';
/

CREATE TABLE IF NOT EXISTS `wh_settlement_bill_entry` (
  `id` varchar(36) NOT NULL COMMENT '主键UUID7',
  `tenant_id` varchar(36) DEFAULT NULL COMMENT '客户/租户ID',
  `paren_id` varchar(36) NOT NULL COMMENT '主表ID',
  `bill_no` varchar(64) DEFAULT NULL COMMENT '仓库结算单号',
  `material_id` bigint(20) NOT NULL COMMENT '产品档案ID',
  `material_name` varchar(255) DEFAULT NULL COMMENT '产品名称',
  `speci` varchar(255) DEFAULT NULL COMMENT '规格',
  `model` varchar(255) DEFAULT NULL COMMENT '型号',
  `unit` varchar(64) DEFAULT NULL COMMENT '单位',
  `unit_price` decimal(18,2) DEFAULT NULL COMMENT '单价',
  `qty` decimal(18,2) NOT NULL DEFAULT 0.00 COMMENT '数量',
  `amt` decimal(18,2) DEFAULT NULL COMMENT '金额',
  `batch_number` varchar(100) DEFAULT NULL COMMENT '批号',
  `end_time` date DEFAULT NULL COMMENT '效期',
  `batch_no` varchar(100) DEFAULT NULL COMMENT '批次',
  `factory_id` bigint(20) DEFAULT NULL COMMENT '生产厂家ID',
  `factory_code` varchar(64) DEFAULT NULL COMMENT '生产厂家编码',
  `factory_name` varchar(200) DEFAULT NULL COMMENT '生产厂家名称',
  `source_bill_type` int(11) DEFAULT NULL COMMENT '数据来源单据类型 101入库 201出库等',
  `source_bill_id` bigint(20) DEFAULT NULL COMMENT '单据主表ID',
  `source_bill_no` varchar(64) DEFAULT NULL COMMENT '单据号',
  `source_entry_id` bigint(20) DEFAULT NULL COMMENT '单据明细ID',
  `supplier_id` bigint(20) DEFAULT NULL COMMENT '供应商ID',
  `supplier_name` varchar(200) DEFAULT NULL COMMENT '供应商名称',
  `del_flag` int(1) NOT NULL DEFAULT 0 COMMENT '删除标志（0正常 1删除）',
  `delete_by` varchar(64) DEFAULT NULL COMMENT '删除者',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  `sort_order` int(11) DEFAULT 0 COMMENT '排序',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_wh_settlement_entry_paren` (`paren_id`),
  KEY `idx_wh_settlement_entry_tenant` (`tenant_id`),
  KEY `idx_wh_settlement_entry_source` (`source_bill_type`,`source_bill_id`,`source_entry_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='仓库结算单明细表';
/

-- ========== 供应商结算单（主键UUID7，含客户id；发票通过关联表多对多） ==========
CREATE TABLE IF NOT EXISTS `supp_settlement_bill` (
  `id` varchar(36) NOT NULL COMMENT '主键UUID7',
  `tenant_id` varchar(36) DEFAULT NULL COMMENT '客户/租户ID(同sb_customer.customer_id)',
  `bill_no` varchar(64) NOT NULL COMMENT '供应商结算单号',
  `supplier_id` bigint(20) NOT NULL COMMENT '供应商ID',
  `supplier_name` varchar(200) DEFAULT NULL COMMENT '供应商名称',
  `wh_settlement_id` varchar(36) DEFAULT NULL COMMENT '来源仓库结算单主表ID（UUID7）',
  `create_by` varchar(64) DEFAULT NULL COMMENT '制单人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '制单时间',
  `audit_by` varchar(64) DEFAULT NULL COMMENT '审核人',
  `audit_time` datetime DEFAULT NULL COMMENT '审核时间',
  `audit_status` int(1) NOT NULL DEFAULT 0 COMMENT '审核状态 0待审核 1已审核',
  `del_flag` int(1) NOT NULL DEFAULT 0 COMMENT '删除标志（0正常 1删除）',
  `delete_by` varchar(64) DEFAULT NULL COMMENT '删除者',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  `remark` varchar(512) DEFAULT NULL COMMENT '备注',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新人',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_supp_settlement_bill_no` (`bill_no`),
  KEY `idx_supp_settlement_supplier` (`supplier_id`),
  KEY `idx_supp_settlement_tenant` (`tenant_id`),
  KEY `idx_supp_settlement_wh` (`wh_settlement_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='供应商结算单主表';
/

-- 供应商结算单与发票关联表（一张供应商结算单可关联多张发票；结算单审核后不得删除、修改关联）
CREATE TABLE IF NOT EXISTS `supp_settlement_invoice` (
  `id` varchar(36) NOT NULL COMMENT '主键UUID7',
  `tenant_id` varchar(36) DEFAULT NULL COMMENT '客户/租户ID',
  `supp_settlement_id` varchar(36) NOT NULL COMMENT '供应商结算单主表ID',
  `invoice_id` varchar(36) NOT NULL COMMENT '发票ID（fin_invoice.id）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `delete_by` varchar(64) DEFAULT NULL COMMENT '删除者',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_supp_settlement_invoice` (`supp_settlement_id`,`invoice_id`),
  KEY `idx_supp_settlement_invoice_supp` (`supp_settlement_id`),
  KEY `idx_supp_settlement_invoice_inv` (`invoice_id`),
  KEY `idx_supp_settlement_invoice_tenant` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='供应商结算单与发票关联表';
/

CREATE TABLE IF NOT EXISTS `supp_settlement_bill_entry` (
  `id` varchar(36) NOT NULL COMMENT '主键UUID7',
  `tenant_id` varchar(36) DEFAULT NULL COMMENT '客户/租户ID',
  `paren_id` varchar(36) NOT NULL COMMENT '主表ID',
  `bill_no` varchar(64) DEFAULT NULL COMMENT '供应商结算单号',
  `wh_settlement_id` varchar(36) DEFAULT NULL COMMENT '仓库结算单主表ID（UUID7）',
  `wh_settlement_bill_no` varchar(64) DEFAULT NULL COMMENT '仓库结算单单号',
  `wh_settlement_entry_id` varchar(36) DEFAULT NULL COMMENT '仓库结算单明细ID（UUID7，追溯库房业务单据明细）',
  `material_id` bigint(20) NOT NULL COMMENT '产品档案ID',
  `material_name` varchar(255) DEFAULT NULL COMMENT '产品名称',
  `speci` varchar(255) DEFAULT NULL COMMENT '规格',
  `model` varchar(255) DEFAULT NULL COMMENT '型号',
  `unit` varchar(64) DEFAULT NULL COMMENT '单位',
  `unit_price` decimal(18,2) DEFAULT NULL COMMENT '单价',
  `qty` decimal(18,2) NOT NULL DEFAULT 0.00 COMMENT '数量',
  `amt` decimal(18,2) DEFAULT NULL COMMENT '金额',
  `batch_number` varchar(100) DEFAULT NULL COMMENT '批号',
  `end_time` date DEFAULT NULL COMMENT '效期',
  `batch_no` varchar(100) DEFAULT NULL COMMENT '批次',
  `factory_id` bigint(20) DEFAULT NULL COMMENT '生产厂家ID',
  `factory_code` varchar(64) DEFAULT NULL COMMENT '生产厂家编码',
  `factory_name` varchar(200) DEFAULT NULL COMMENT '生产厂家名称',
  `source_bill_type` int(11) DEFAULT NULL COMMENT '数据来源单据类型',
  `source_bill_id` bigint(20) DEFAULT NULL COMMENT '单据主表ID',
  `source_bill_no` varchar(64) DEFAULT NULL COMMENT '单据号',
  `source_entry_id` bigint(20) DEFAULT NULL COMMENT '单据明细ID',
  `supplier_id` bigint(20) DEFAULT NULL COMMENT '供应商ID',
  `supplier_name` varchar(200) DEFAULT NULL COMMENT '供应商名称',
  `del_flag` int(1) NOT NULL DEFAULT 0 COMMENT '删除标志（0正常 1删除）',
  `delete_by` varchar(64) DEFAULT NULL COMMENT '删除者',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  `sort_order` int(11) DEFAULT 0 COMMENT '排序',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_supp_settlement_entry_paren` (`paren_id`),
  KEY `idx_supp_settlement_entry_tenant` (`tenant_id`),
  KEY `idx_supp_settlement_entry_wh` (`wh_settlement_id`),
  KEY `idx_supp_settlement_entry_wh_entry` (`wh_settlement_entry_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='供应商结算单明细表';
/
/* 若曾建过 supp_settlement_bill 且含 invoice_id，可手动执行：ALTER TABLE supp_settlement_bill DROP COLUMN invoice_id; */
/* 供应商结算单与发票关联表（一张单可关联多张发票） */
/
/* 采购计划明细与科室申购单明细关联表（逻辑删除：del_flag、delete_by、delete_time、tenant_id） */
/
CREATE TABLE IF NOT EXISTS `purchase_plan_entry_apply` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `purchase_plan_entry_id` bigint(20) NOT NULL COMMENT '采购计划明细ID',
  `bas_apply_entry_id` bigint(20) NOT NULL COMMENT '科室申购单明细ID',
  `del_flag` char(1) NOT NULL DEFAULT '0' COMMENT '删除标志（0存在 1删除）',
  `delete_by` varchar(64) DEFAULT NULL COMMENT '删除者',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  `tenant_id` varchar(36) DEFAULT NULL COMMENT '租户ID',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_plan_entry_apply` (`purchase_plan_entry_id`,`bas_apply_entry_id`),
  KEY `idx_ppea_entry_id` (`purchase_plan_entry_id`),
  KEY `idx_ppea_apply_entry_id` (`bas_apply_entry_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='采购计划明细关联科室申购单明细表(bas_apply)';
/
/* 采购计划明细与科室申购单明细(dep_purchase_apply)关联表，逻辑删除 */
/
CREATE TABLE IF NOT EXISTS `purchase_plan_entry_dep_apply` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `purchase_plan_entry_id` bigint(20) NOT NULL COMMENT '采购计划明细ID',
  `dep_purchase_apply_entry_id` bigint(20) NOT NULL COMMENT '科室申购单明细ID(dep_purchase_apply_entry)',
  `dep_purchase_apply_id` bigint(20) DEFAULT NULL COMMENT '申购单主表ID',
  `purchase_bill_no` varchar(64) DEFAULT NULL COMMENT '申购单号',
  `purchase_plan_id` bigint(20) DEFAULT NULL COMMENT '采购计划主表ID',
  `plan_no` varchar(64) DEFAULT NULL COMMENT '采购计划单号',
  `del_flag` char(1) NOT NULL DEFAULT '0' COMMENT '删除标志（0存在 1删除）',
  `delete_by` varchar(64) DEFAULT NULL COMMENT '删除者',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  `tenant_id` varchar(36) DEFAULT NULL COMMENT '租户ID',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_plan_entry_dep_apply` (`purchase_plan_entry_id`,`dep_purchase_apply_entry_id`),
  KEY `idx_ppeda_entry_id` (`purchase_plan_entry_id`),
  KEY `idx_ppeda_dep_entry_id` (`dep_purchase_apply_entry_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='采购计划明细关联科室申购单明细表(dep)';
/

-- ========== 出入库、库存、批次、仓库/科室流水（与 column.sql 增量字段对齐的完整建表） ==========
-- 主表字段名 suppler_id 与历史代码一致（非 supplier_id）

-- ========== 高值耗材（gz_*）业务表 ==========
CREATE TABLE IF NOT EXISTS `gz_dep_apply` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `apply_bill_no` varchar(64) DEFAULT NULL COMMENT '申领单号',
  `apply_bill_date` datetime DEFAULT NULL COMMENT '申请日期',
  `warehouse_id` bigint DEFAULT NULL COMMENT '仓库ID',
  `user_id` bigint DEFAULT NULL COMMENT '操作人ID',
  `apply_bill_status` int DEFAULT NULL COMMENT '申请状态',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `del_flag` int NOT NULL DEFAULT 0 COMMENT '删除标志',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `delete_by` varchar(64) DEFAULT NULL COMMENT '删除者',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  `tenant_id` varchar(36) DEFAULT NULL COMMENT '租户ID',
  PRIMARY KEY (`id`),
  KEY `idx_gz_dep_apply_no` (`apply_bill_no`),
  KEY `idx_gz_dep_apply_wh` (`warehouse_id`),
  KEY `idx_gz_dep_apply_tenant` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='高值科室申领主表';
/

CREATE TABLE IF NOT EXISTS `gz_dep_apply_entry` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `paren_id` bigint NOT NULL COMMENT '主表ID',
  `material_id` bigint DEFAULT NULL COMMENT '耗材ID',
  `unit_price` decimal(18,6) DEFAULT NULL COMMENT '单价',
  `qty` decimal(18,6) DEFAULT NULL COMMENT '数量',
  `price` decimal(18,6) DEFAULT NULL COMMENT '价格',
  `amt` decimal(18,6) DEFAULT NULL COMMENT '金额',
  `batch_no` varchar(100) DEFAULT NULL COMMENT '批次号',
  `batch_numer` varchar(100) DEFAULT NULL COMMENT '批号',
  `supplier_id` bigint DEFAULT NULL COMMENT '供应商ID',
  `master_barcode` varchar(200) DEFAULT NULL COMMENT '主条码',
  `secondary_barcode` varchar(200) DEFAULT NULL COMMENT '辅条码',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `del_flag` int NOT NULL DEFAULT 0 COMMENT '删除标志',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `delete_by` varchar(64) DEFAULT NULL COMMENT '删除者',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  `tenant_id` varchar(36) DEFAULT NULL COMMENT '租户ID',
  PRIMARY KEY (`id`),
  KEY `idx_gz_dep_apply_entry_paren` (`paren_id`),
  KEY `idx_gz_dep_apply_entry_batch` (`batch_no`),
  KEY `idx_gz_dep_apply_entry_tenant` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='高值科室申领明细表';
/

CREATE TABLE IF NOT EXISTS `gz_order` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `order_no` varchar(64) DEFAULT NULL COMMENT '单号',
  `suppler_id` bigint DEFAULT NULL COMMENT '供应商ID',
  `order_date` date DEFAULT NULL COMMENT '单据日期',
  `warehouse_id` bigint DEFAULT NULL COMMENT '仓库ID',
  `department_id` bigint DEFAULT NULL COMMENT '科室ID',
  `order_status` int DEFAULT NULL COMMENT '状态 1未审核 2已审核',
  `order_type` int DEFAULT NULL COMMENT '类型 101入库 102出库 301退库 401跟台',
  `is_follow_flag` varchar(16) DEFAULT NULL COMMENT '跟台标识',
  `audit_date` datetime DEFAULT NULL COMMENT '审核时间',
  `audit_by` varchar(64) DEFAULT NULL COMMENT '审核人',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `del_flag` int NOT NULL DEFAULT 0 COMMENT '删除标志',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `delete_by` varchar(64) DEFAULT NULL COMMENT '删除者',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  `tenant_id` varchar(36) DEFAULT NULL COMMENT '租户ID',
  PRIMARY KEY (`id`),
  KEY `idx_gz_order_no` (`order_no`),
  KEY `idx_gz_order_type` (`order_type`),
  KEY `idx_gz_order_wh` (`warehouse_id`),
  KEY `idx_gz_order_tenant` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='高值单据主表';
/

CREATE TABLE IF NOT EXISTS `gz_order_entry` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `paren_id` bigint NOT NULL COMMENT '主表ID',
  `material_id` bigint DEFAULT NULL COMMENT '耗材ID',
  `qty` decimal(18,6) DEFAULT NULL COMMENT '数量',
  `price` decimal(18,6) DEFAULT NULL COMMENT '价格',
  `amt` decimal(18,6) DEFAULT NULL COMMENT '金额',
  `batch_no` varchar(100) DEFAULT NULL COMMENT '批次号',
  `batch_number` varchar(100) DEFAULT NULL COMMENT '批号',
  `begin_time` date DEFAULT NULL COMMENT '生产日期',
  `end_time` date DEFAULT NULL COMMENT '有效期',
  `master_barcode` varchar(200) DEFAULT NULL COMMENT '主条码',
  `secondary_barcode` varchar(200) DEFAULT NULL COMMENT '辅条码',
  `in_hospital_code` varchar(200) DEFAULT NULL COMMENT '院内码',
  `supplier_id` bigint DEFAULT NULL COMMENT '供应商ID',
  `warehouse_id` bigint DEFAULT NULL COMMENT '仓库ID',
  `bill_no` varchar(64) DEFAULT NULL COMMENT '单号冗余',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `del_flag` int NOT NULL DEFAULT 0 COMMENT '删除标志',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `delete_by` varchar(64) DEFAULT NULL COMMENT '删除者',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  `tenant_id` varchar(36) DEFAULT NULL COMMENT '租户ID',
  PRIMARY KEY (`id`),
  KEY `idx_gz_order_entry_paren` (`paren_id`),
  KEY `idx_gz_order_entry_batch` (`batch_no`),
  KEY `idx_gz_order_entry_tenant` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='高值单据明细表';
/

CREATE TABLE IF NOT EXISTS `gz_order_entry_inhospitalcode_list` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `parent_id` bigint DEFAULT NULL COMMENT '主ID',
  `code` varchar(200) DEFAULT NULL COMMENT '单据号',
  `detail_id` bigint DEFAULT NULL COMMENT '单据明细ID',
  `material_id` bigint DEFAULT NULL COMMENT '产品档案ID',
  `price` decimal(18,6) DEFAULT NULL COMMENT '单价',
  `qty` decimal(18,6) DEFAULT NULL COMMENT '数量',
  `batch_no` varchar(100) DEFAULT NULL COMMENT '批次号',
  `batch_number` varchar(100) DEFAULT NULL COMMENT '批号',
  `master_barcode` varchar(200) DEFAULT NULL COMMENT '主条码',
  `secondary_barcode` varchar(200) DEFAULT NULL COMMENT '辅条码',
  `end_date` datetime DEFAULT NULL COMMENT '有效期',
  `in_hospital_code` varchar(200) DEFAULT NULL COMMENT '院内码',
  `warehouse_id` bigint DEFAULT NULL COMMENT '仓库ID',
  `supplier_id` bigint DEFAULT NULL COMMENT '供应商ID',
  `del_flag` int NOT NULL DEFAULT 0 COMMENT '删除标志',
  `create_date` datetime DEFAULT NULL COMMENT '创建时间',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `delete_by` varchar(64) DEFAULT NULL COMMENT '删除者',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  `tenant_id` varchar(36) DEFAULT NULL COMMENT '租户ID',
  PRIMARY KEY (`id`),
  KEY `idx_gz_order_entry_code_parent` (`parent_id`),
  KEY `idx_gz_order_entry_code_detail` (`detail_id`),
  KEY `idx_gz_order_entry_code_tenant` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='高值耗材备货单明细院内码列表';
/

CREATE TABLE IF NOT EXISTS `gz_patient_info` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `medical_record_no` varchar(50) NOT NULL COMMENT '病历号',
  `name` varchar(50) DEFAULT NULL COMMENT '姓名',
  `sex` varchar(10) DEFAULT NULL COMMENT '性别',
  `age` int DEFAULT NULL COMMENT '年龄',
  `hospital_number` varchar(50) DEFAULT NULL COMMENT '住院号',
  `ward` varchar(50) DEFAULT NULL COMMENT '病区',
  `ward_no` varchar(50) DEFAULT NULL COMMENT '病房号',
  `bed_no` varchar(50) DEFAULT NULL COMMENT '病床号',
  `apply_dept_id` bigint DEFAULT NULL COMMENT '申请科室ID',
  `exec_dept_id` bigint DEFAULT NULL COMMENT '执行科室ID',
  `hospital_date` date DEFAULT NULL COMMENT '住院日期',
  `contact_phone` varchar(20) DEFAULT NULL COMMENT '联系电话',
  `contact_address` varchar(200) DEFAULT NULL COMMENT '联系地址',
  `chief_surgeon` varchar(50) DEFAULT NULL COMMENT '主刀医生',
  `surgery_date` date DEFAULT NULL COMMENT '手术日期',
  `surgery_name` varchar(200) DEFAULT NULL COMMENT '手术名称',
  `admission_diagnosis` varchar(500) DEFAULT NULL COMMENT '入院诊断',
  `surgery_id` varchar(50) DEFAULT NULL COMMENT '手术ID',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `delete_by` varchar(64) DEFAULT NULL COMMENT '删除者',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  `tenant_id` varchar(36) DEFAULT NULL COMMENT '租户ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_gz_patient_medical_record` (`medical_record_no`),
  KEY `idx_gz_patient_hospital_number` (`hospital_number`),
  KEY `idx_gz_patient_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='患者信息表';
/

CREATE TABLE IF NOT EXISTS `gz_refund_goods` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `goods_no` varchar(64) DEFAULT NULL COMMENT '退货单号',
  `suppler_id` bigint DEFAULT NULL COMMENT '供应商ID',
  `goods_date` date DEFAULT NULL COMMENT '退货日期',
  `warehouse_id` bigint DEFAULT NULL COMMENT '仓库ID',
  `department_id` bigint DEFAULT NULL COMMENT '科室ID',
  `goods_status` int DEFAULT NULL COMMENT '状态 1未审核 2已审核',
  `goods_type` int DEFAULT NULL COMMENT '类型',
  `audit_date` datetime DEFAULT NULL COMMENT '审核时间',
  `audit_by` varchar(64) DEFAULT NULL COMMENT '审核人',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `del_flag` int NOT NULL DEFAULT 0 COMMENT '删除标志',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `delete_by` varchar(64) DEFAULT NULL COMMENT '删除者',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  `tenant_id` varchar(36) DEFAULT NULL COMMENT '租户ID',
  PRIMARY KEY (`id`),
  KEY `idx_gz_refund_goods_no` (`goods_no`),
  KEY `idx_gz_refund_goods_tenant` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='高值退货主表';
/

CREATE TABLE IF NOT EXISTS `gz_refund_goods_entry` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `paren_id` bigint NOT NULL COMMENT '主表ID',
  `material_id` bigint DEFAULT NULL COMMENT '耗材ID',
  `qty` decimal(18,6) DEFAULT NULL COMMENT '数量',
  `price` decimal(18,6) DEFAULT NULL COMMENT '价格',
  `amt` decimal(18,6) DEFAULT NULL COMMENT '金额',
  `batch_no` varchar(100) DEFAULT NULL COMMENT '批次号',
  `batch_number` varchar(100) DEFAULT NULL COMMENT '批号',
  `master_barcode` varchar(200) DEFAULT NULL COMMENT '主条码',
  `secondary_barcode` varchar(200) DEFAULT NULL COMMENT '辅条码',
  `in_hospital_code` varchar(200) DEFAULT NULL COMMENT '院内码',
  `begin_time` date DEFAULT NULL COMMENT '生产日期',
  `end_time` date DEFAULT NULL COMMENT '有效期',
  `supplier_id` bigint DEFAULT NULL COMMENT '供应商ID',
  `warehouse_id` bigint DEFAULT NULL COMMENT '仓库ID',
  `department_id` bigint DEFAULT NULL COMMENT '科室ID(冗余，与备货退库主表一致)',
  `bill_no` varchar(64) DEFAULT NULL COMMENT '单号冗余',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `del_flag` int NOT NULL DEFAULT 0 COMMENT '删除标志',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `delete_by` varchar(64) DEFAULT NULL COMMENT '删除者',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  `tenant_id` varchar(36) DEFAULT NULL COMMENT '租户ID',
  PRIMARY KEY (`id`),
  KEY `idx_gz_refund_goods_entry_paren` (`paren_id`),
  KEY `idx_gz_refund_goods_entry_batch` (`batch_no`),
  KEY `idx_gz_refund_goods_entry_tenant` (`tenant_id`),
  KEY `idx_gz_refund_goods_entry_dept` (`department_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='高值退货明细表';
/

CREATE TABLE IF NOT EXISTS `gz_refund_stock` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `stock_no` varchar(64) DEFAULT NULL COMMENT '退库单号(旧版字段)',
  `stock_date` datetime DEFAULT NULL COMMENT '退库日期(旧版字段)',
  `stock_status` int DEFAULT NULL COMMENT '状态(旧版字段)',
  `stock_type` int DEFAULT NULL COMMENT '类型(旧版字段)',
  `refund_no` varchar(64) DEFAULT NULL COMMENT '退库单号',
  `warehouse_id` bigint DEFAULT NULL COMMENT '仓库ID',
  `department_id` bigint DEFAULT NULL COMMENT '科室ID',
  `refund_date` date DEFAULT NULL COMMENT '退库日期',
  `refund_status` int DEFAULT NULL COMMENT '状态',
  `refund_type` int DEFAULT NULL COMMENT '类型',
  `master_barcode` varchar(200) DEFAULT NULL COMMENT '主条码',
  `secondary_barcode` varchar(200) DEFAULT NULL COMMENT '辅条码',
  `audit_date` datetime DEFAULT NULL COMMENT '审核时间',
  `audit_by` varchar(64) DEFAULT NULL COMMENT '审核人',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `del_flag` int NOT NULL DEFAULT 0 COMMENT '删除标志',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `delete_by` varchar(64) DEFAULT NULL COMMENT '删除者',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  `tenant_id` varchar(36) DEFAULT NULL COMMENT '租户ID',
  PRIMARY KEY (`id`),
  KEY `idx_gz_refund_stock_no` (`refund_no`),
  KEY `idx_gz_refund_stock_tenant` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='高值退库主表';
/

-- 以下为高值「退库」明细 gz_refund_stock_entry（科室/备货退回仓库）；上一段 gz_refund_goods_entry 为「退货」明细（退回供应商），表注释勿混用
CREATE TABLE IF NOT EXISTS `gz_refund_stock_entry` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `paren_id` bigint NOT NULL COMMENT '主表ID',
  `material_id` bigint DEFAULT NULL COMMENT '耗材ID',
  `qty` decimal(18,6) DEFAULT NULL COMMENT '数量',
  `price` decimal(18,6) DEFAULT NULL COMMENT '价格',
  `amt` decimal(18,6) DEFAULT NULL COMMENT '金额',
  `batch_no` varchar(100) DEFAULT NULL COMMENT '批次号',
  `batch_number` varchar(100) DEFAULT NULL COMMENT '批号',
  `in_hospital_code` varchar(200) DEFAULT NULL COMMENT '院内码',
  `begin_time` date DEFAULT NULL COMMENT '生产日期',
  `end_time` date DEFAULT NULL COMMENT '有效期',
  `supplier_id` bigint DEFAULT NULL COMMENT '供应商ID',
  `warehouse_id` bigint DEFAULT NULL COMMENT '仓库ID',
  `department_id` bigint DEFAULT NULL COMMENT '科室ID',
  `bill_no` varchar(64) DEFAULT NULL COMMENT '单号冗余',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `del_flag` int NOT NULL DEFAULT 0 COMMENT '删除标志',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `delete_by` varchar(64) DEFAULT NULL COMMENT '删除者',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  `tenant_id` varchar(36) DEFAULT NULL COMMENT '租户ID',
  PRIMARY KEY (`id`),
  KEY `idx_gz_refund_stock_entry_paren` (`paren_id`),
  KEY `idx_gz_refund_stock_entry_batch` (`batch_no`),
  KEY `idx_gz_refund_stock_entry_tenant` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='高值退库明细表';
/

CREATE TABLE IF NOT EXISTS `gz_shipment` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `shipment_no` varchar(64) DEFAULT NULL COMMENT '出库单号',
  `department_id` bigint DEFAULT NULL COMMENT '科室ID',
  `shipment_date` date DEFAULT NULL COMMENT '出库日期',
  `warehouse_id` bigint DEFAULT NULL COMMENT '仓库ID',
  `shipment_status` int DEFAULT NULL COMMENT '状态 1未审核 2已审核',
  `shipment_type` int DEFAULT NULL COMMENT '类型',
  `audit_date` datetime DEFAULT NULL COMMENT '审核时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `del_flag` int NOT NULL DEFAULT 0 COMMENT '删除标志',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `delete_by` varchar(64) DEFAULT NULL COMMENT '删除者',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  `tenant_id` varchar(36) DEFAULT NULL COMMENT '租户ID',
  PRIMARY KEY (`id`),
  KEY `idx_gz_shipment_no` (`shipment_no`),
  KEY `idx_gz_shipment_tenant` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='高值出库主表';
/

CREATE TABLE IF NOT EXISTS `gz_shipment_entry` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `paren_id` bigint NOT NULL COMMENT '主表ID',
  `material_id` bigint DEFAULT NULL COMMENT '耗材ID',
  `qty` decimal(18,6) DEFAULT NULL COMMENT '数量',
  `price` decimal(18,6) DEFAULT NULL COMMENT '价格',
  `amt` decimal(18,6) DEFAULT NULL COMMENT '金额',
  `batch_no` varchar(100) DEFAULT NULL COMMENT '批次号',
  `batch_number` varchar(100) DEFAULT NULL COMMENT '批号',
  `begin_time` date DEFAULT NULL COMMENT '生产日期',
  `end_time` date DEFAULT NULL COMMENT '有效期',
  `master_barcode` varchar(200) DEFAULT NULL COMMENT '主条码',
  `secondary_barcode` varchar(200) DEFAULT NULL COMMENT '辅条码',
  `in_hospital_code` varchar(200) DEFAULT NULL COMMENT '院内码',
  `supplier_id` bigint DEFAULT NULL COMMENT '供应商ID',
  `warehouse_id` bigint DEFAULT NULL COMMENT '仓库ID',
  `department_id` bigint DEFAULT NULL COMMENT '科室ID',
  `bill_no` varchar(64) DEFAULT NULL COMMENT '单号冗余',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `del_flag` int NOT NULL DEFAULT 0 COMMENT '删除标志',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `delete_by` varchar(64) DEFAULT NULL COMMENT '删除者',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  `tenant_id` varchar(36) DEFAULT NULL COMMENT '租户ID',
  PRIMARY KEY (`id`),
  KEY `idx_gz_shipment_entry_paren` (`paren_id`),
  KEY `idx_gz_shipment_entry_batch` (`batch_no`),
  KEY `idx_gz_shipment_entry_tenant` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='高值出库明细表';
/

CREATE TABLE IF NOT EXISTS `gz_traceability` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `trace_no` varchar(64) DEFAULT NULL COMMENT '追溯单号',
  `medical_record_no` varchar(64) DEFAULT NULL COMMENT '病历号',
  `patient_name` varchar(100) DEFAULT NULL COMMENT '患者姓名',
  `patient_sex` varchar(16) DEFAULT NULL COMMENT '性别',
  `patient_age` varchar(32) DEFAULT NULL COMMENT '年龄',
  `hospital_number` varchar(64) DEFAULT NULL COMMENT '住院号',
  `ward` varchar(128) DEFAULT NULL COMMENT '病区',
  `ward_no` varchar(64) DEFAULT NULL COMMENT '病房号',
  `bed_no` varchar(64) DEFAULT NULL COMMENT '床号',
  `apply_dept_id` bigint DEFAULT NULL COMMENT '申请科室ID',
  `exec_dept_id` bigint DEFAULT NULL COMMENT '执行科室ID',
  `hospital_date` date DEFAULT NULL COMMENT '入院日期',
  `contact_phone` varchar(64) DEFAULT NULL COMMENT '联系电话',
  `contact_address` varchar(255) DEFAULT NULL COMMENT '联系地址',
  `chief_surgeon` varchar(100) DEFAULT NULL COMMENT '主刀医生',
  `surgery_date` datetime DEFAULT NULL COMMENT '手术日期',
  `surgery_name` varchar(255) DEFAULT NULL COMMENT '手术名称',
  `admission_diagnosis` varchar(500) DEFAULT NULL COMMENT '入院诊断',
  `surgery_id` bigint DEFAULT NULL COMMENT '手术ID',
  `order_status` int DEFAULT NULL COMMENT '状态',
  `audit_date` datetime DEFAULT NULL COMMENT '审核时间',
  `audit_by` varchar(64) DEFAULT NULL COMMENT '审核人',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `del_flag` int NOT NULL DEFAULT 0 COMMENT '删除标志',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `delete_by` varchar(64) DEFAULT NULL COMMENT '删除者',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  `tenant_id` varchar(36) DEFAULT NULL COMMENT '租户ID',
  PRIMARY KEY (`id`),
  KEY `idx_gz_trace_no` (`trace_no`),
  KEY `idx_gz_trace_tenant` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='高值追溯主表';
/

CREATE TABLE IF NOT EXISTS `gz_traceability_entry` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `parent_id` bigint NOT NULL COMMENT '主表ID',
  `material_id` bigint DEFAULT NULL COMMENT '耗材ID',
  `inventory_id` bigint DEFAULT NULL COMMENT '科室库存ID',
  `material_name` varchar(200) DEFAULT NULL COMMENT '耗材名称',
  `specification` varchar(200) DEFAULT NULL COMMENT '规格',
  `model` varchar(200) DEFAULT NULL COMMENT '型号',
  `unit` varchar(64) DEFAULT NULL COMMENT '单位',
  `quantity` decimal(18,6) DEFAULT NULL COMMENT '数量',
  `charge_price` decimal(18,6) DEFAULT NULL COMMENT '计费价格',
  `batch_no` varchar(100) DEFAULT NULL COMMENT '批次号',
  `expiry_date` date DEFAULT NULL COMMENT '有效期',
  `master_barcode` varchar(200) DEFAULT NULL COMMENT '主条码',
  `secondary_barcode` varchar(200) DEFAULT NULL COMMENT '辅条码',
  `in_hospital_code` varchar(200) DEFAULT NULL COMMENT '院内码',
  `manufacturer` varchar(200) DEFAULT NULL COMMENT '生产厂家',
  `supplier` varchar(200) DEFAULT NULL COMMENT '供应商名称',
  `supplier_id` bigint DEFAULT NULL COMMENT '供应商ID',
  `certificate_no` varchar(100) DEFAULT NULL COMMENT '注册证号',
  `billing_follow` varchar(64) DEFAULT NULL COMMENT '计费随访',
  `del_flag` int NOT NULL DEFAULT 0 COMMENT '删除标志',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `delete_by` varchar(64) DEFAULT NULL COMMENT '删除者',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  `tenant_id` varchar(36) DEFAULT NULL COMMENT '租户ID',
  PRIMARY KEY (`id`),
  KEY `idx_gz_trace_entry_parent` (`parent_id`),
  KEY `idx_gz_trace_entry_inv` (`inventory_id`),
  KEY `idx_gz_trace_entry_tenant` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='高值追溯明细表';
/

CREATE TABLE IF NOT EXISTS `gz_depot_inventory` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `qty` decimal(18,6) DEFAULT NULL COMMENT '库存数量',
  `material_id` bigint DEFAULT NULL COMMENT '耗材ID',
  `warehouse_id` bigint DEFAULT NULL COMMENT '仓库ID',
  `unit_price` decimal(18,6) DEFAULT NULL COMMENT '单价',
  `amt` decimal(18,6) DEFAULT NULL COMMENT '金额',
  `batch_no` varchar(100) DEFAULT NULL COMMENT '批次号',
  `material_no` varchar(100) DEFAULT NULL COMMENT '批号',
  `material_date` date DEFAULT NULL COMMENT '生产日期',
  `warehouse_date` date DEFAULT NULL COMMENT '入库日期',
  `supplier_id` bigint DEFAULT NULL COMMENT '供应商ID',
  `in_hospital_code` varchar(200) DEFAULT NULL COMMENT '院内码',
  `order_id` bigint DEFAULT NULL COMMENT '备货单ID',
  `order_no` varchar(64) DEFAULT NULL COMMENT '备货单单号',
  `order_entry_id` bigint DEFAULT NULL COMMENT '备货单明细ID',
  `inhospitalcode_list_id` bigint DEFAULT NULL COMMENT '院内码列表ID',
  `master_barcode` varchar(200) DEFAULT NULL COMMENT '主条码',
  `secondary_barcode` varchar(200) DEFAULT NULL COMMENT '辅条码',
  `del_flag` int NOT NULL DEFAULT 0 COMMENT '删除标志',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `delete_by` varchar(64) DEFAULT NULL COMMENT '删除者',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  `tenant_id` varchar(36) DEFAULT NULL COMMENT '租户ID',
  PRIMARY KEY (`id`),
  KEY `idx_gz_depot_inv_batch` (`batch_no`),
  KEY `idx_gz_depot_inv_wh` (`warehouse_id`),
  KEY `idx_gz_depot_inv_tenant` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='高值仓库库存表';
/

CREATE TABLE IF NOT EXISTS `gz_dep_inventory` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `material_id` bigint DEFAULT NULL COMMENT '耗材ID',
  `department_id` bigint DEFAULT NULL COMMENT '科室ID',
  `qty` decimal(18,6) DEFAULT NULL COMMENT '数量',
  `unit_price` decimal(18,6) DEFAULT NULL COMMENT '单价',
  `amt` decimal(18,6) DEFAULT NULL COMMENT '金额',
  `batch_no` varchar(100) DEFAULT NULL COMMENT '批次号',
  `material_no` varchar(18) DEFAULT NULL COMMENT '批号',
  `supplier_id` bigint DEFAULT NULL COMMENT '供应商ID',
  `material_date` date DEFAULT NULL COMMENT '生产日期',
  `warehouse_date` date DEFAULT NULL COMMENT '入库日期',
  `master_barcode` varchar(200) DEFAULT NULL COMMENT '主条码',
  `secondary_barcode` varchar(200) DEFAULT NULL COMMENT '辅条码',
  `in_hospital_code` varchar(200) DEFAULT NULL COMMENT '院内码',
  `del_flag` int NOT NULL DEFAULT 0 COMMENT '删除标志',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `delete_by` varchar(64) DEFAULT NULL COMMENT '删除者',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  `tenant_id` varchar(36) DEFAULT NULL COMMENT '租户ID',
  PRIMARY KEY (`id`),
  KEY `idx_gz_dep_inv_batch` (`batch_no`),
  KEY `idx_gz_dep_inv_dept` (`department_id`),
  KEY `idx_gz_dep_inv_tenant` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='高值科室库存表';
/

CREATE TABLE IF NOT EXISTS `stk_io_bill` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `bill_no` varchar(64) DEFAULT NULL COMMENT '出入库单号',
  `ref_bill_no` varchar(64) DEFAULT NULL COMMENT '引用单号',
  `suppler_id` bigint DEFAULT NULL COMMENT '供应商ID（fd_supplier.id）',
  `bill_date` date DEFAULT NULL COMMENT '出入库日期',
  `warehouse_id` bigint DEFAULT NULL COMMENT '仓库ID',
  `department_id` bigint DEFAULT NULL COMMENT '科室ID',
  `bill_status` int DEFAULT NULL COMMENT '单据状态',
  `user_id` bigint DEFAULT NULL COMMENT '操作人',
  `bill_type` int DEFAULT NULL COMMENT '单据类型 101入库 201出库 301退货 401退库 501调拨',
  `del_flag` int NOT NULL DEFAULT 0 COMMENT '删除标志',
  `audit_date` datetime DEFAULT NULL COMMENT '审核时间',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `del_person` varchar(64) DEFAULT NULL COMMENT '配送员',
  `telephone` varchar(32) DEFAULT NULL COMMENT '联系电话',
  `total_amount` decimal(18,2) DEFAULT NULL COMMENT '金额',
  `invoice_number` varchar(128) DEFAULT NULL COMMENT '发票号',
  `invoice_amount` varchar(64) DEFAULT NULL COMMENT '发票金额',
  `invoice_time` date DEFAULT NULL COMMENT '发票时间',
  `pro_person` bigint DEFAULT NULL COMMENT '采购员',
  `return_reason` varchar(500) DEFAULT NULL COMMENT '退货原因',
  `is_month_init` int DEFAULT NULL COMMENT '是否月结',
  `receipt_confirm_status` int DEFAULT 0 COMMENT '收货确认状态 0未确认 1已确认',
  `audit_by` varchar(64) DEFAULT NULL COMMENT '审核人',
  `tenant_id` varchar(36) DEFAULT NULL COMMENT '租户ID(同sb_customer.customer_id)',
  `delete_by` varchar(64) DEFAULT NULL COMMENT '删除者',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  `settlement_type` varchar(16) DEFAULT NULL COMMENT '结算方式 1入库 2出库 3消耗',
  `d_apply_id` varchar(32) DEFAULT NULL COMMENT '科室申领主表ID（bas_apply.id）',
  `wh_warehouse_apply_id` varchar(36) DEFAULT NULL COMMENT '库房申请单主键（wh_warehouse_apply.id）',
  `wh_warehouse_apply_bill_no` varchar(64) DEFAULT NULL COMMENT '库房申请单号（冗余）',
  PRIMARY KEY (`id`),
  KEY `idx_stk_io_bill_no` (`bill_no`),
  KEY `idx_stk_io_bill_type` (`bill_type`),
  KEY `idx_stk_io_bill_wh` (`warehouse_id`),
  KEY `idx_stk_io_bill_tenant` (`tenant_id`),
  KEY `idx_stk_io_bill_suppler` (`suppler_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='出入库主表';
/

CREATE TABLE IF NOT EXISTS `stk_io_bill_entry` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `paren_id` bigint NOT NULL COMMENT '主表ID',
  `bill_no` varchar(64) DEFAULT NULL COMMENT '出入库单号（冗余主表 bill_no，便于按单号查明细）',
  `commodity_id` bigint DEFAULT NULL COMMENT '商品ID',
  `material_id` bigint DEFAULT NULL COMMENT '耗材ID',
  `material_name` varchar(256) DEFAULT NULL COMMENT '产品名称（快照）',
  `material_speci` varchar(256) DEFAULT NULL COMMENT '规格（快照）',
  `material_model` varchar(256) DEFAULT NULL COMMENT '型号（快照）',
  `material_factory_id` bigint DEFAULT NULL COMMENT '生产厂家ID（快照，fd_factory.factory_id）',
  `unit_price` decimal(18,2) DEFAULT NULL COMMENT '单价',
  `qty` decimal(18,2) DEFAULT NULL COMMENT '数量',
  `price` decimal(18,2) DEFAULT NULL COMMENT '价格',
  `amt` decimal(18,2) DEFAULT NULL COMMENT '金额',
  `batch_no` varchar(100) DEFAULT NULL COMMENT '批次号',
  `batch_number` varchar(100) DEFAULT NULL COMMENT '批号',
  `begin_time` date DEFAULT NULL COMMENT '生产日期',
  `end_time` date DEFAULT NULL COMMENT '有效期',
  `del_flag` int NOT NULL DEFAULT 0 COMMENT '删除标志',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `kc_no` bigint DEFAULT NULL COMMENT '遗留兼容：与 dep_inventory_id 同步（出库审核后）；历史数据可能曾为仓库库存id，请以 stk_inventory_id/dep_inventory_id 为准',
  `warehouse_id` bigint DEFAULT NULL COMMENT '明细仓库ID（冗余，与主表或业务一致）',
  `main_barcode` varchar(128) DEFAULT NULL COMMENT '高值耗材主条码',
  `sub_barcode` varchar(128) DEFAULT NULL COMMENT '高值耗材辅条码',
  `suppler_id` varchar(128) DEFAULT NULL COMMENT '供应商ID（明细，可与主表一致或来自批次）',
  `settlement_type` varchar(16) DEFAULT NULL COMMENT '结算方式（与主表一致）',
  `tenant_id` varchar(36) DEFAULT NULL COMMENT '租户ID',
  `delete_by` varchar(64) DEFAULT NULL COMMENT '删除者',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  `wh_apply_entry_id` varchar(36) DEFAULT NULL COMMENT '库房申请单明细ID（引用库房申请出库时回填）',
  `stk_inventory_id` bigint DEFAULT NULL COMMENT '仓库库存明细主键 stk_inventory.id（入库审核、出库制单/审核来源仓行等）',
  `dep_inventory_id` bigint DEFAULT NULL COMMENT '科室库存明细主键 stk_dep_inventory.id（出库审核后、收货确认、退库等）',
  PRIMARY KEY (`id`),
  KEY `idx_stk_io_entry_paren` (`paren_id`),
  KEY `idx_stk_io_entry_material` (`material_id`),
  KEY `idx_stk_io_entry_batch` (`batch_no`),
  KEY `idx_stk_io_entry_wh` (`warehouse_id`),
  KEY `idx_stk_io_entry_tenant` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='出入库明细表';
/

-- 单据引用/生成关联：从源单（入库/出库/退库/退货等）生成目标单时的明细级追溯；主键为 36 位 UUID7 字符串（与 UUID7.generateUUID7() 一致）
CREATE TABLE IF NOT EXISTS `hc_doc_bill_ref` (
  `id` varchar(36) NOT NULL COMMENT '主键 UUID7（36位含连字符）',
  `tenant_id` varchar(36) DEFAULT NULL COMMENT '租户ID(同 sb_customer.customer_id)',
  `biz_domain` varchar(32) NOT NULL DEFAULT 'STK_IO_BILL' COMMENT '业务域：STK_IO_BILL 出入库单；可扩展 BAS_APPLY 等',
  `ref_type` varchar(64) NOT NULL COMMENT '引用类型：RK_TO_CK/CK_TO_TK/TK_TO_CK/TK_TO_TH/RK_TO_TH 等，见 HcDocBillRefType',
  `src_bill_kind` varchar(32) DEFAULT NULL COMMENT '源单 bill_type 等说明，如 101',
  `src_bill_id` varchar(64) DEFAULT NULL COMMENT '源单据主表ID',
  `src_bill_no` varchar(128) DEFAULT NULL COMMENT '源单号',
  `src_entry_id` varchar(64) DEFAULT NULL COMMENT '源明细ID',
  `src_entry_line_no` int DEFAULT NULL COMMENT '源明细行序号快照(可选)',
  `tgt_bill_kind` varchar(32) DEFAULT NULL COMMENT '生成单 bill_type 等说明',
  `tgt_bill_id` varchar(64) DEFAULT NULL COMMENT '生成后单据主表ID',
  `tgt_bill_no` varchar(128) DEFAULT NULL COMMENT '生成后单号',
  `tgt_entry_id` varchar(64) DEFAULT NULL COMMENT '生成后明细ID',
  `line_no` int DEFAULT NULL COMMENT '本关联行对应目标明细顺序号(从1)，与保存时 docRefList 对齐',
  `ref_qty` decimal(18,4) DEFAULT NULL COMMENT '本次从源引用数量快照',
  `ref_amt` decimal(18,2) DEFAULT NULL COMMENT '本次引用金额快照',
  `lock_warehouse_id` varchar(64) DEFAULT NULL COMMENT '办理时锁定仓库(防串仓)',
  `lock_supplier_id` varchar(64) DEFAULT NULL COMMENT '办理时锁定供应商(防串供)',
  `lock_department_id` varchar(64) DEFAULT NULL COMMENT '办理时锁定科室(防串科室)',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `del_flag` tinyint NOT NULL DEFAULT 0 COMMENT '删除标志 0正常 1作废(被同目标单重新保存时软删)',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `delete_by` varchar(64) DEFAULT NULL COMMENT '删除者(软删操作人)',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间(软删时间)',
  PRIMARY KEY (`id`),
  KEY `idx_hc_ref_tenant_tgt` (`tenant_id`,`tgt_bill_id`),
  KEY `idx_hc_ref_tenant_src` (`tenant_id`,`src_bill_id`),
  KEY `idx_hc_ref_tenant_type` (`tenant_id`,`ref_type`),
  KEY `idx_hc_ref_tgt_entry` (`tenant_id`,`tgt_bill_id`,`tgt_entry_id`),
  KEY `idx_hc_ref_del` (`tenant_id`,`del_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='耗材单据引用关联';
/

CREATE TABLE IF NOT EXISTS `t_hc_ck_flow` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `bill_id` bigint DEFAULT NULL COMMENT '单据主表id',
  `entry_id` bigint DEFAULT NULL COMMENT '单据明细id',
  `ref_bill_id` varchar(36) DEFAULT NULL COMMENT '期初单主表ID（UUID7）',
  `ref_entry_id` varchar(36) DEFAULT NULL COMMENT '期初单明细ID（UUID7）',
  `warehouse_id` bigint DEFAULT NULL COMMENT '仓库ID',
  `material_id` bigint DEFAULT NULL COMMENT '耗材ID',
  `batch_no` varchar(100) DEFAULT NULL COMMENT '批次号',
  `batch_number` varchar(100) DEFAULT NULL COMMENT '批号',
  `batch_id` bigint DEFAULT NULL COMMENT '批次对象表ID（stk_batch.id）',
  `qty` decimal(18,2) DEFAULT 0.00 COMMENT '数量',
  `unit_price` decimal(18,2) DEFAULT NULL COMMENT '单价',
  `amt` decimal(18,2) DEFAULT NULL COMMENT '金额',
  `begin_time` date DEFAULT NULL COMMENT '生产日期',
  `end_time` date DEFAULT NULL COMMENT '有效期',
  `supplier_id` bigint DEFAULT NULL COMMENT '供应商ID（与库存/单据一致）',
  `kc_no` bigint DEFAULT NULL COMMENT '关联仓库库存明细id',
  `lx` varchar(10) DEFAULT NULL COMMENT 'RK入库/CK出库/TH退货/TK退库/ZC转出/ZR转入',
  `flow_time` datetime DEFAULT NULL COMMENT '流水时间',
  `origin_business_type` varchar(64) DEFAULT NULL COMMENT '来源业务类型中文（便于追溯展示）',
  `del_flag` int NOT NULL DEFAULT 0 COMMENT '删除标志',
  `delete_by` varchar(64) DEFAULT NULL COMMENT '删除者',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  `main_barcode` varchar(128) DEFAULT NULL COMMENT '高值主条码',
  `sub_barcode` varchar(128) DEFAULT NULL COMMENT '高值辅条码',
  `factory_id` bigint DEFAULT NULL COMMENT '生产厂家ID（fd_factory.factory_id，冗余追溯）',
  `tenant_id` varchar(36) DEFAULT NULL COMMENT '租户ID',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_hc_ck_bill` (`bill_id`),
  KEY `idx_hc_ck_entry` (`entry_id`),
  KEY `idx_hc_ck_flow_time` (`flow_time`),
  KEY `idx_hc_ck_supplier` (`supplier_id`),
  KEY `idx_hc_ck_factory` (`factory_id`),
  KEY `idx_hc_ck_tenant` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='仓库流水表';
/

CREATE TABLE IF NOT EXISTS `t_hc_ks_flow` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `bill_id` bigint DEFAULT NULL COMMENT '出库单id',
  `entry_id` bigint DEFAULT NULL COMMENT '出库明细id',
  `department_id` bigint DEFAULT NULL COMMENT '科室ID',
  `warehouse_id` bigint DEFAULT NULL COMMENT '库存归属仓库ID（出库来源仓库）',
  `material_id` bigint DEFAULT NULL COMMENT '耗材ID',
  `batch_no` varchar(100) DEFAULT NULL COMMENT '批次号',
  `batch_number` varchar(100) DEFAULT NULL COMMENT '批号',
  `batch_id` bigint DEFAULT NULL COMMENT '批次对象表ID（stk_batch.id）',
  `qty` decimal(18,2) DEFAULT 0.00 COMMENT '数量',
  `unit_price` decimal(18,2) DEFAULT NULL COMMENT '单价',
  `amt` decimal(18,2) DEFAULT NULL COMMENT '金额',
  `begin_time` date DEFAULT NULL COMMENT '生产日期',
  `end_time` date DEFAULT NULL COMMENT '有效期',
  `supplier_id` varchar(64) DEFAULT NULL COMMENT '供应商ID（与仓库库存一致，字符串兼容历史）',
  `kc_no` bigint DEFAULT NULL COMMENT '科室库存明细id',
  `lx` varchar(10) DEFAULT NULL COMMENT 'CK出库',
  `flow_time` datetime DEFAULT NULL COMMENT '流水时间',
  `origin_business_type` varchar(64) DEFAULT NULL COMMENT '来源业务类型中文（便于追溯展示）',
  `del_flag` int NOT NULL DEFAULT 0 COMMENT '删除标志',
  `delete_by` varchar(64) DEFAULT NULL COMMENT '删除者',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  `main_barcode` varchar(128) DEFAULT NULL COMMENT '高值主条码',
  `sub_barcode` varchar(128) DEFAULT NULL COMMENT '高值辅条码',
  `factory_id` bigint DEFAULT NULL COMMENT '生产厂家ID（fd_factory.factory_id，冗余追溯）',
  `tenant_id` varchar(36) DEFAULT NULL COMMENT '租户ID',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_hc_ks_bill` (`bill_id`),
  KEY `idx_hc_ks_entry` (`entry_id`),
  KEY `idx_hc_ks_flow_time` (`flow_time`),
  KEY `idx_hc_ks_wh` (`warehouse_id`),
  KEY `idx_hc_ks_factory` (`factory_id`),
  KEY `idx_hc_ks_tenant` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='科室流水表';
/

CREATE TABLE IF NOT EXISTS `stk_inventory` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `qty` decimal(18,6) DEFAULT NULL COMMENT '库存数量',
  `material_id` bigint DEFAULT NULL COMMENT '耗材ID',
  `warehouse_id` bigint DEFAULT NULL COMMENT '仓库ID',
  `unit_price` decimal(18,6) DEFAULT NULL COMMENT '单价',
  `amt` decimal(18,6) DEFAULT NULL COMMENT '金额',
  `batch_no` varchar(100) DEFAULT NULL COMMENT '入库批次号',
  `batch_id` bigint DEFAULT NULL COMMENT '批次对象表ID（stk_batch.id）',
  `material_no` varchar(100) DEFAULT NULL COMMENT '耗材批次号',
  `material_date` datetime DEFAULT NULL COMMENT '耗材日期',
  `warehouse_date` datetime DEFAULT NULL COMMENT '入库日期',
  `supplier_id` bigint DEFAULT NULL COMMENT '供应商ID（与入库单一致）',
  `begin_time` datetime DEFAULT NULL COMMENT '生产日期',
  `end_time` datetime DEFAULT NULL COMMENT '有效期',
  `receipt_order_no` varchar(100) DEFAULT NULL COMMENT '入库单号',
  `kc_no` bigint DEFAULT NULL COMMENT '科室库存明细id（反写）',
  `his_id` varchar(128) DEFAULT NULL COMMENT 'HIS/第三方系统库存明细唯一标识',
  `batch_number` varchar(100) DEFAULT NULL COMMENT '批号',
  `tenant_id` varchar(36) DEFAULT NULL COMMENT '租户ID(同sb_customer.customer_id)',
  `main_barcode` varchar(128) DEFAULT NULL COMMENT '高值耗材主条码',
  `sub_barcode` varchar(128) DEFAULT NULL COMMENT '高值耗材辅条码',
  `settlement_type` varchar(16) DEFAULT NULL COMMENT '结算方式（来自入库单）',
  `del_flag` int NOT NULL DEFAULT 0 COMMENT '删除标志',
  `delete_by` varchar(64) DEFAULT NULL COMMENT '删除者',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  `factory_id` bigint DEFAULT NULL COMMENT '生产厂家ID（冗余，与批次/档案一致）',
  `material_name` varchar(256) DEFAULT NULL COMMENT '产品名称（快照）',
  `material_speci` varchar(256) DEFAULT NULL COMMENT '规格（快照）',
  `material_model` varchar(256) DEFAULT NULL COMMENT '型号（快照）',
  `material_factory_id` bigint DEFAULT NULL COMMENT '生产厂家ID（快照，fd_factory.factory_id）',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_stk_inv_batch` (`batch_no`),
  KEY `idx_stk_inv_batch_id` (`batch_id`),
  KEY `idx_stk_inv_mat_wh` (`material_id`,`warehouse_id`),
  KEY `idx_stk_inv_supplier` (`supplier_id`),
  KEY `idx_stk_inv_factory` (`factory_id`),
  KEY `idx_stk_inv_tenant` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='仓库库存明细表';
/

CREATE TABLE IF NOT EXISTS `stk_dep_inventory` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `material_id` bigint DEFAULT NULL COMMENT '耗材ID',
  `department_id` bigint DEFAULT NULL COMMENT '科室ID',
  `qty` decimal(18,6) DEFAULT NULL COMMENT '数量',
  `unit_price` decimal(18,6) DEFAULT NULL COMMENT '单价',
  `amt` decimal(18,6) DEFAULT NULL COMMENT '金额',
  `batch_no` varchar(100) DEFAULT NULL COMMENT '批次号',
  `material_no` varchar(100) DEFAULT NULL COMMENT '耗材批次号',
  `material_date` datetime DEFAULT NULL COMMENT '耗材日期',
  `warehouse_date` datetime DEFAULT NULL COMMENT '入库日期',
  `supplier_id` varchar(100) DEFAULT NULL COMMENT '供应商ID（与出库一致；可存编码或第三方键）',
  `begin_time` datetime DEFAULT NULL COMMENT '生产日期',
  `end_time` datetime DEFAULT NULL COMMENT '有效期',
  `kc_no` bigint DEFAULT NULL COMMENT '关联仓库库存主键 stk_inventory.id（反写）',
  `batch_number` varchar(100) DEFAULT NULL COMMENT '批号',
  `receipt_confirm_status` tinyint DEFAULT 0 COMMENT '收货确认 0未确认 1已确认',
  `bill_id` bigint DEFAULT NULL COMMENT '出库单主表id',
  `bill_entry_id` bigint DEFAULT NULL COMMENT '出库明细id',
  `bill_no` varchar(100) DEFAULT NULL COMMENT '单据号',
  `bill_type` int DEFAULT NULL COMMENT '单据类型 201出库',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `tenant_id` varchar(36) DEFAULT NULL COMMENT '租户ID(同sb_customer.customer_id)',
  `main_barcode` varchar(128) DEFAULT NULL COMMENT '高值耗材主条码',
  `sub_barcode` varchar(128) DEFAULT NULL COMMENT '高值耗材辅条码',
  `settlement_type` varchar(16) DEFAULT NULL COMMENT '结算方式（来自出库单）',
  `del_flag` int NOT NULL DEFAULT 0 COMMENT '删除标志',
  `delete_by` varchar(64) DEFAULT NULL COMMENT '删除者',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  `batch_id` bigint DEFAULT NULL COMMENT '批次对象表ID（stk_batch.id）',
  `warehouse_id` bigint DEFAULT NULL COMMENT '库存归属仓库ID（与出库来源仓库一致）',
  `factory_id` bigint DEFAULT NULL COMMENT '生产厂家ID（fd_factory.factory_id，冗余追溯）',
  `out_order_no` varchar(100) DEFAULT NULL COMMENT '出库单号',
  `material_name` varchar(256) DEFAULT NULL COMMENT '产品名称（快照）',
  `material_speci` varchar(256) DEFAULT NULL COMMENT '规格（快照）',
  `material_model` varchar(256) DEFAULT NULL COMMENT '型号（快照）',
  `material_factory_id` bigint DEFAULT NULL COMMENT '生产厂家ID（快照，fd_factory.factory_id）',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_stk_dep_inv_batch` (`batch_no`),
  KEY `idx_stk_dep_inv_batch_id` (`batch_id`),
  KEY `idx_stk_dep_inv_wh` (`warehouse_id`),
  KEY `idx_stk_dep_inv_dept` (`department_id`),
  KEY `idx_stk_dep_inv_bill` (`bill_id`,`bill_entry_id`),
  KEY `idx_stk_dep_inv_tenant` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='科室库存明细表';
/

-- ========== 盘盈新增明细/待入账（不直接污染库存结算数据） ==========
CREATE TABLE IF NOT EXISTS `stk_profit_loss_pending` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `bill_id` bigint DEFAULT NULL COMMENT '盈亏单主表id（stk_io_profit_loss.id）',
  `entry_id` bigint DEFAULT NULL COMMENT '盈亏单明细id（stk_io_profit_loss_entry.id）',
  `warehouse_id` bigint DEFAULT NULL COMMENT '来源仓库ID',
  `department_id` bigint DEFAULT NULL COMMENT '目标科室ID（如适用）',
  `material_id` bigint DEFAULT NULL COMMENT '耗材ID',
  `supplier_id` bigint DEFAULT NULL COMMENT '供应商ID（fd_supplier.id）',
  `batch_no` varchar(100) DEFAULT NULL COMMENT '批次号',
  `batch_id` bigint DEFAULT NULL COMMENT '批次对象表ID（stk_batch.id）',
  `batch_number` varchar(100) DEFAULT NULL COMMENT '批号',
  `qty` decimal(18,2) DEFAULT NULL COMMENT '待入账数量',
  `unit_price` decimal(18,2) DEFAULT NULL COMMENT '单价',
  `amt` decimal(18,2) DEFAULT NULL COMMENT '金额',
  `begin_time` date DEFAULT NULL COMMENT '生产日期',
  `end_time` date DEFAULT NULL COMMENT '有效期',
  `main_barcode` varchar(128) DEFAULT NULL COMMENT '高值耗材主条码',
  `sub_barcode` varchar(128) DEFAULT NULL COMMENT '高值耗材辅条码',
  `apply_status` varchar(32) DEFAULT '待入账' COMMENT '入账状态：待入账/已入账/仅追溯用',
  `settlement_effect_status` varchar(32) DEFAULT '仅追溯用' COMMENT '结算影响：已入账/仅追溯用',
  `tenant_id` varchar(36) DEFAULT NULL COMMENT '租户ID',
  `del_flag` int NOT NULL DEFAULT 0 COMMENT '删除标志',
  `delete_by` varchar(64) DEFAULT NULL COMMENT '删除者',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_stk_profit_pending_bill` (`bill_id`,`entry_id`),
  KEY `idx_stk_profit_pending_batch` (`batch_no`),
  KEY `idx_stk_profit_pending_batch_id` (`batch_id`),
  KEY `idx_stk_profit_pending_wh` (`warehouse_id`),
  KEY `idx_stk_profit_pending_tenant` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='盘盈新增明细/待入账表（不直接影响结算）';
/

CREATE TABLE IF NOT EXISTS `stk_batch` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '批次对象表ID',
  `batch_no` varchar(100) NOT NULL COMMENT '批次号',
  `material_id` bigint DEFAULT NULL COMMENT '产品档案ID',
  `material_code` varchar(64) DEFAULT NULL COMMENT '产品档案编码',
  `material_name` varchar(200) DEFAULT NULL COMMENT '名称',
  `speci` varchar(200) DEFAULT NULL COMMENT '规格',
  `model` varchar(100) DEFAULT NULL COMMENT '型号',
  `unit_id` bigint DEFAULT NULL COMMENT '单位ID',
  `unit_name` varchar(50) DEFAULT NULL COMMENT '单位名称',
  `unit_price` decimal(18,2) DEFAULT NULL COMMENT '单价',
  `batch_number` varchar(100) DEFAULT NULL COMMENT '批号',
  `end_time` date DEFAULT NULL COMMENT '有效期',
  `begin_time` date DEFAULT NULL COMMENT '生产日期',
  `sterilize_batch_no` varchar(100) DEFAULT NULL COMMENT '灭菌批号',
  `sterilize_end_time` date DEFAULT NULL COMMENT '灭菌有效期',
  `use_times` int DEFAULT NULL COMMENT '使用次数/人次',
  `main_barcode` varchar(128) DEFAULT NULL COMMENT '主条码',
  `sub_barcode` varchar(128) DEFAULT NULL COMMENT '辅条码',
  `register_no` varchar(100) DEFAULT NULL COMMENT '注册证号',
  `permit_no` varchar(100) DEFAULT NULL COMMENT '生产许可证号',
  `factory_id` bigint DEFAULT NULL COMMENT '生产厂家ID',
  `factory_code` varchar(64) DEFAULT NULL COMMENT '生产厂家编码',
  `factory_name` varchar(200) DEFAULT NULL COMMENT '生产厂家名称',
  `supplier_id` bigint DEFAULT NULL COMMENT '供应商ID',
  `supplier_code` varchar(64) DEFAULT NULL COMMENT '供应商编码',
  `supplier_name` varchar(200) DEFAULT NULL COMMENT '供应商名称',
  `warehouse_id` bigint DEFAULT NULL COMMENT '仓库ID',
  `warehouse_code` varchar(64) DEFAULT NULL COMMENT '仓库编码',
  `warehouse_name` varchar(200) DEFAULT NULL COMMENT '仓库名称',
  `department_id` bigint DEFAULT NULL COMMENT '科室ID',
  `department_code` varchar(64) DEFAULT NULL COMMENT '科室编码',
  `department_name` varchar(200) DEFAULT NULL COMMENT '科室名称',
  `storeroom_id` bigint DEFAULT NULL COMMENT '库房分类ID',
  `storeroom_code` varchar(64) DEFAULT NULL COMMENT '库房分类编码',
  `storeroom_name` varchar(200) DEFAULT NULL COMMENT '库房分类名称',
  `finance_category_id` bigint DEFAULT NULL COMMENT '财务分类ID',
  `finance_category_code` varchar(64) DEFAULT NULL COMMENT '财务分类编码',
  `finance_category_name` varchar(200) DEFAULT NULL COMMENT '财务分类名称',
  `batch_source` varchar(32) DEFAULT NULL COMMENT '批次产生方式',
  `origin_bill_type` int DEFAULT NULL COMMENT '来源单据类型（stk_io_bill.bill_type）',
  `origin_flow_lx` varchar(16) DEFAULT NULL COMMENT '来源流水lx（如RK/ZR/PY/QC等）',
  `origin_business_type` varchar(64) DEFAULT NULL COMMENT '来源业务类型中文（便于追溯展示）',
  `origin_from_warehouse_id` bigint DEFAULT NULL COMMENT '来源仓库ID（调拨等场景用）',
  `origin_to_warehouse_id` bigint DEFAULT NULL COMMENT '目标仓库ID/科室仓库ID（调拨等场景用）',
  `bill_id` bigint DEFAULT NULL COMMENT '单据主表ID',
  `bill_no` varchar(64) DEFAULT NULL COMMENT '单据号',
  `entry_id` bigint DEFAULT NULL COMMENT '单据明细ID',
  `ref_bill_id` varchar(36) DEFAULT NULL COMMENT '期初单主表ID（UUID7）',
  `ref_entry_id` varchar(36) DEFAULT NULL COMMENT '期初单明细ID（UUID7）',
  `in_code_detail_id` bigint DEFAULT NULL COMMENT '院内码明细ID',
  `audit_time` datetime DEFAULT NULL COMMENT '单据审核时间',
  `audit_by` varchar(64) DEFAULT NULL COMMENT '单据审核人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建人',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新人',
  `del_flag` int NOT NULL DEFAULT 0 COMMENT '删除标志',
  `del_time` datetime DEFAULT NULL COMMENT '删除时间',
  `del_by` varchar(64) DEFAULT NULL COMMENT '删除人',
  `tenant_id` varchar(36) DEFAULT NULL COMMENT '租户ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_stk_batch_no` (`batch_no`),
  KEY `idx_stk_batch_material` (`material_id`),
  KEY `idx_stk_batch_wh` (`warehouse_id`),
  KEY `idx_stk_batch_bill` (`bill_id`),
  KEY `idx_stk_batch_supplier` (`supplier_id`),
  KEY `idx_stk_batch_tenant` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='批次追溯表';
/

-- ========== 仓库盘点单（与 StkIoStocktakingMapper 一致；明细含 tenant_id 与逻辑删除审计） ==========
CREATE TABLE IF NOT EXISTS `stk_io_stocktaking` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `stock_no` varchar(64) DEFAULT NULL COMMENT '盘点单号',
  `suppler_id` bigint DEFAULT NULL COMMENT '供应商ID',
  `stock_date` date DEFAULT NULL COMMENT '盘点日期',
  `warehouse_id` bigint DEFAULT NULL COMMENT '仓库ID',
  `department_id` bigint DEFAULT NULL COMMENT '科室ID',
  `stock_status` int DEFAULT NULL COMMENT '盘点状态',
  `user_id` bigint DEFAULT NULL COMMENT '操作人',
  `stock_type` int DEFAULT NULL COMMENT '盘点类型',
  `del_flag` int NOT NULL DEFAULT 0 COMMENT '删除标志',
  `audit_date` datetime DEFAULT NULL COMMENT '审核时间',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `is_month_init` int DEFAULT NULL COMMENT '是否月结',
  `tenant_id` varchar(36) DEFAULT NULL COMMENT '租户ID(同sb_customer.customer_id)',
  PRIMARY KEY (`id`),
  KEY `idx_stk_stocktaking_wh` (`warehouse_id`),
  KEY `idx_stk_stocktaking_tenant` (`tenant_id`),
  KEY `idx_stk_stocktaking_no` (`stock_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='仓库盘点单主表';
/

CREATE TABLE IF NOT EXISTS `stk_io_stocktaking_entry` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `paren_id` bigint NOT NULL COMMENT '主表ID',
  `commodity_id` bigint DEFAULT NULL COMMENT '商品ID',
  `material_id` bigint DEFAULT NULL COMMENT '耗材ID',
  `unit_price` decimal(18,2) DEFAULT NULL COMMENT '单价',
  `qty` decimal(18,2) DEFAULT NULL COMMENT '数量',
  `price` decimal(18,2) DEFAULT NULL COMMENT '价格',
  `amt` decimal(18,2) DEFAULT NULL COMMENT '金额',
  `batch_no` varchar(100) DEFAULT NULL COMMENT '批次号',
  `kc_no` bigint DEFAULT NULL COMMENT '库存明细id',
  `batch_number` varchar(100) DEFAULT NULL COMMENT '批号',
  `begin_time` date DEFAULT NULL COMMENT '生产日期',
  `end_time` date DEFAULT NULL COMMENT '有效期',
  `del_flag` int NOT NULL DEFAULT 0 COMMENT '删除标志',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `stock_qty` decimal(18,2) DEFAULT NULL COMMENT '盘点数量',
  `profit_qty` decimal(18,2) DEFAULT NULL COMMENT '盈亏数量',
  `stock_amount` decimal(18,2) DEFAULT NULL COMMENT '盘点金额',
  `profit_amount` decimal(18,2) DEFAULT NULL COMMENT '盈亏金额',
  `main_barcode` varchar(128) DEFAULT NULL COMMENT '高值主条码',
  `sub_barcode` varchar(128) DEFAULT NULL COMMENT '高值辅条码',
  `supplier_id` bigint DEFAULT NULL COMMENT '供应商ID（盘盈等）',
  `tenant_id` varchar(36) DEFAULT NULL COMMENT '租户ID',
  `delete_by` varchar(64) DEFAULT NULL COMMENT '删除者',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_stk_ste_paren` (`paren_id`),
  KEY `idx_stk_ste_material` (`material_id`),
  KEY `idx_stk_ste_tenant` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='仓库盘点单明细表';
/

-- ========== 盈亏单（与 StkIoProfitLossMapper 一致） ==========
CREATE TABLE IF NOT EXISTS `stk_io_profit_loss` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `bill_no` varchar(64) DEFAULT NULL COMMENT '盈亏单号',
  `stocktaking_id` bigint DEFAULT NULL COMMENT '关联盘点单ID',
  `stocktaking_no` varchar(64) DEFAULT NULL COMMENT '盘点单号',
  `warehouse_id` bigint DEFAULT NULL COMMENT '仓库ID',
  `bill_status` int DEFAULT 1 COMMENT '单据状态 1待审核 2已审核',
  `audit_by` varchar(64) DEFAULT NULL COMMENT '审核人',
  `audit_date` datetime DEFAULT NULL COMMENT '审核时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建人',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新人',
  `del_flag` int NOT NULL DEFAULT 0 COMMENT '删除标志',
  `tenant_id` varchar(36) DEFAULT NULL COMMENT '租户ID(同sb_customer.customer_id)',
  PRIMARY KEY (`id`),
  KEY `idx_pl_stocktaking` (`stocktaking_id`),
  KEY `idx_pl_warehouse` (`warehouse_id`),
  KEY `idx_pl_tenant` (`tenant_id`),
  KEY `idx_pl_bill_status` (`bill_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='盈亏单主表';
/

CREATE TABLE IF NOT EXISTS `stk_io_profit_loss_entry` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `paren_id` bigint DEFAULT NULL COMMENT '盈亏单ID',
  `stocktaking_entry_id` bigint DEFAULT NULL COMMENT '来源盘点明细ID',
  `kc_no` bigint DEFAULT NULL COMMENT '库存明细id',
  `material_id` bigint DEFAULT NULL COMMENT '耗材ID',
  `batch_no` varchar(100) DEFAULT NULL COMMENT '批次号',
  `batch_number` varchar(100) DEFAULT NULL COMMENT '批号',
  `book_qty` decimal(18,2) DEFAULT NULL COMMENT '账面数量',
  `stock_qty` decimal(18,2) DEFAULT NULL COMMENT '盘点数量',
  `profit_qty` decimal(18,2) DEFAULT NULL COMMENT '盈亏数量',
  `unit_price` decimal(18,2) DEFAULT NULL COMMENT '单价',
  `profit_amount` decimal(18,2) DEFAULT NULL COMMENT '盈亏金额',
  `begin_time` date DEFAULT NULL COMMENT '生产日期',
  `end_time` date DEFAULT NULL COMMENT '有效期',
  `del_flag` int DEFAULT 0 COMMENT '删除标志',
  `main_barcode` varchar(128) DEFAULT NULL COMMENT '高值主条码',
  `sub_barcode` varchar(128) DEFAULT NULL COMMENT '高值辅条码',
  `suppler_id` varchar(128) DEFAULT NULL COMMENT '供应商ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建人',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新人',
  `tenant_id` varchar(36) DEFAULT NULL COMMENT '租户ID',
  `delete_by` varchar(64) DEFAULT NULL COMMENT '删除者',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  PRIMARY KEY (`id`),
  KEY `idx_pl_entry_paren` (`paren_id`),
  KEY `idx_pl_entry_tenant` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='盈亏单明细表';
/

-- ========== 科室批量消耗（与 DeptBatchConsumeMapper 一致；明细列 batch_number 与代码一致） ==========
CREATE TABLE IF NOT EXISTS `t_hc_ks_xh` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `consume_bill_no` varchar(64) DEFAULT NULL COMMENT '消耗单号',
  `consume_bill_date` date DEFAULT NULL COMMENT '消耗日期',
  `warehouse_id` bigint DEFAULT NULL COMMENT '仓库ID',
  `department_id` bigint DEFAULT NULL COMMENT '科室ID',
  `user_id` bigint DEFAULT NULL COMMENT '操作人ID',
  `consume_bill_status` int DEFAULT 1 COMMENT '单据状态 1待审核 2已审核',
  `total_amount` decimal(18,2) DEFAULT 0.00 COMMENT '总金额',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `audit_by` varchar(64) DEFAULT NULL COMMENT '审核人',
  `audit_date` datetime DEFAULT NULL COMMENT '审核时间',
  `reject_reason` varchar(500) DEFAULT NULL COMMENT '驳回原因',
  `reverse_flag` int DEFAULT 0 COMMENT '是否反消耗单(0否1是)',
  `reverse_of_consume_id` bigint DEFAULT NULL COMMENT '反消耗来源主单ID',
  `reverse_of_bill_no` varchar(64) DEFAULT NULL COMMENT '反消耗来源主单号',
  `bill_source` varchar(32) DEFAULT NULL COMMENT '单据来源(MANUAL/HIS_MIRROR_BATCH等)',
  `disallow_reverse` tinyint NOT NULL DEFAULT 0 COMMENT '禁止手工退消耗(1禁止)',
  `his_fetch_batch_id` varchar(36) DEFAULT NULL COMMENT 'HIS计费抓取批次ID',
  `del_flag` int NOT NULL DEFAULT 0 COMMENT '删除标志',
  `delete_by` varchar(64) DEFAULT NULL COMMENT '删除者',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  `tenant_id` varchar(36) DEFAULT NULL COMMENT '租户ID',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_hc_ks_xh_no` (`consume_bill_no`),
  KEY `idx_hc_ks_xh_wh` (`warehouse_id`),
  KEY `idx_hc_ks_xh_dept` (`department_id`),
  KEY `idx_hc_ks_xh_tenant` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='科室批量消耗主表';
/

CREATE TABLE IF NOT EXISTS `t_hc_ks_xh_entry` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `paren_id` bigint NOT NULL COMMENT '父表ID',
  `dep_inventory_id` bigint DEFAULT NULL COMMENT '来源科室库存ID(stk_dep_inventory.id)',
  `gz_dep_inventory_id` bigint DEFAULT NULL COMMENT '高值科室虚拟库存 gz_dep_inventory.id（与 dep_inventory_id 二选一）',
  `kc_no` bigint DEFAULT NULL COMMENT '来源仓库库存ID(stk_inventory.id)',
  `material_id` bigint DEFAULT NULL COMMENT '耗材ID',
  `batch_id` bigint DEFAULT NULL COMMENT '批次对象ID(stk_batch.id)',
  `warehouse_id` bigint DEFAULT NULL COMMENT '库存归属仓库ID',
  `department_id` bigint DEFAULT NULL COMMENT '科室ID',
  `supplier_id` varchar(128) DEFAULT NULL COMMENT '供应商ID',
  `factory_id` bigint DEFAULT NULL COMMENT '生产厂家ID(fd_factory.factory_id)',
  `unit_price` decimal(18,2) DEFAULT 0.00 COMMENT '单价',
  `qty` decimal(18,2) DEFAULT 0.00 COMMENT '数量',
  `price` decimal(18,2) DEFAULT 0.00 COMMENT '价格',
  `amt` decimal(18,2) DEFAULT 0.00 COMMENT '金额',
  `batch_no` varchar(100) DEFAULT NULL COMMENT '批次号',
  `batch_number` varchar(100) DEFAULT NULL COMMENT '批号',
  `material_no` varchar(128) DEFAULT NULL COMMENT '耗材批号',
  `begin_time` date DEFAULT NULL COMMENT '生产日期',
  `end_time` date DEFAULT NULL COMMENT '有效期',
  `material_date` datetime DEFAULT NULL COMMENT '耗材日期',
  `warehouse_date` datetime DEFAULT NULL COMMENT '入库日期',
  `settlement_type` varchar(16) DEFAULT NULL COMMENT '结算方式',
  `material_name` varchar(256) DEFAULT NULL COMMENT '耗材名称快照',
  `material_speci` varchar(256) DEFAULT NULL COMMENT '规格快照',
  `material_model` varchar(256) DEFAULT NULL COMMENT '型号快照',
  `material_factory_id` bigint DEFAULT NULL COMMENT '生产厂家ID快照(fd_factory.factory_id)',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `del_flag` int NOT NULL DEFAULT 0 COMMENT '删除标志',
  `delete_by` varchar(64) DEFAULT NULL COMMENT '删除者',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  `main_barcode` varchar(128) DEFAULT NULL COMMENT '高值主条码',
  `sub_barcode` varchar(128) DEFAULT NULL COMMENT '高值辅条码',
  `tenant_id` varchar(36) DEFAULT NULL COMMENT '租户ID',
  `src_consume_id` bigint DEFAULT NULL COMMENT '反消耗来源主单ID(正向消耗主单)',
  `src_consume_bill_no` varchar(64) DEFAULT NULL COMMENT '反消耗来源主单号(正向消耗单号)',
  `src_consume_entry_id` bigint DEFAULT NULL COMMENT '反消耗来源明细ID(正向消耗明细ID)',
  `src_consume_qty` decimal(18,2) DEFAULT NULL COMMENT '正向消耗数量快照',
  `src_can_reverse_qty` decimal(18,2) DEFAULT NULL COMMENT '反消耗生成时可退数量快照',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_hc_ks_xh_e_paren` (`paren_id`),
  KEY `idx_hc_ks_xh_e_tenant` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='科室批量消耗明细表';
/

-- 科室批量消耗明细 <-> 出库单明细 关联（主键UUID7；双方ID/明细ID/单号使用varchar；含冗余快照）
CREATE TABLE IF NOT EXISTS `t_hc_ks_xh_entry_ref` (
  `id` varchar(36) NOT NULL COMMENT '主键UUID7（36位）',
  `tenant_id` varchar(36) DEFAULT NULL COMMENT '租户ID',
  `consume_id` varchar(36) DEFAULT NULL COMMENT '科室消耗主表ID（t_hc_ks_xh.id，varchar冗余）',
  `consume_bill_no` varchar(64) DEFAULT NULL COMMENT '科室消耗单号',
  `consume_entry_id` varchar(36) DEFAULT NULL COMMENT '科室消耗明细ID（t_hc_ks_xh_entry.id，varchar冗余）',
  `consume_bill_date` date DEFAULT NULL COMMENT '科室消耗日期',
  `consume_status` int DEFAULT NULL COMMENT '科室消耗单状态',
  `src_bill_kind` varchar(32) DEFAULT NULL COMMENT '来源单据类型（OUT_WAREHOUSE）',
  `src_bill_id` varchar(36) DEFAULT NULL COMMENT '来源单据主表ID（出库单ID）',
  `src_bill_no` varchar(64) DEFAULT NULL COMMENT '来源单据号（出库单号）',
  `src_entry_id` varchar(36) DEFAULT NULL COMMENT '来源单据明细ID（出库单明细ID）',
  `department_id` bigint DEFAULT NULL COMMENT '科室ID（冗余）',
  `warehouse_id` bigint DEFAULT NULL COMMENT '仓库ID（冗余）',
  `material_id` bigint DEFAULT NULL COMMENT '耗材ID（冗余）',
  `material_name` varchar(256) DEFAULT NULL COMMENT '耗材名称快照',
  `material_speci` varchar(256) DEFAULT NULL COMMENT '规格快照',
  `material_model` varchar(256) DEFAULT NULL COMMENT '型号快照',
  `batch_no` varchar(100) DEFAULT NULL COMMENT '批次号（追溯）',
  `batch_number` varchar(100) DEFAULT NULL COMMENT '批号（追溯）',
  `material_no` varchar(128) DEFAULT NULL COMMENT '耗材批号',
  `supplier_id` varchar(128) DEFAULT NULL COMMENT '供应商ID',
  `factory_id` bigint DEFAULT NULL COMMENT '生产厂家ID',
  `out_entry_qty` decimal(18,2) DEFAULT NULL COMMENT '来源出库明细数量',
  `available_qty` decimal(18,2) DEFAULT NULL COMMENT '库存剩余数量（引用时）',
  `default_consume_qty` decimal(18,2) DEFAULT NULL COMMENT '默认带出数量（min(库存剩余,出库明细数量)）',
  `consume_qty` decimal(18,2) DEFAULT NULL COMMENT '本次消耗数量',
  `unit_price` decimal(18,2) DEFAULT NULL COMMENT '单价',
  `amount` decimal(18,2) DEFAULT NULL COMMENT '金额',
  `main_barcode` varchar(128) DEFAULT NULL COMMENT '主条码',
  `sub_barcode` varchar(128) DEFAULT NULL COMMENT '辅条码',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_ks_xh_ref_tenant` (`tenant_id`),
  KEY `idx_ks_xh_ref_consume` (`consume_id`,`consume_entry_id`),
  KEY `idx_ks_xh_ref_src` (`src_bill_id`,`src_entry_id`),
  KEY `idx_ks_xh_ref_src_no` (`src_bill_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='科室批量消耗明细-出库单明细关联表';
/

-- 打印设置（耗材入库/出库等单据打印模板；tenant_id 为空=全库默认，与后端 SysPrintSetting / 前端 orderPrint、outOrderPrint 一致）
CREATE TABLE IF NOT EXISTS `sys_print_setting` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `template_name` varchar(100) NOT NULL COMMENT '模板名称',
  `tenant_id` varchar(64) DEFAULT NULL COMMENT '租户/客户ID，NULL表示全库默认模板',
  `bill_type` int(4) DEFAULT NULL COMMENT '单据类型（101入库 201出库等，NULL表示通用）',
  `page_width` decimal(10,2) DEFAULT 210.00 COMMENT '页面宽度（mm）',
  `page_height` decimal(10,2) DEFAULT 297.00 COMMENT '页面高度（mm）',
  `orientation` varchar(20) DEFAULT 'portrait' COMMENT '页面方向（portrait纵向，landscape横向）',
  `margin_top` decimal(10,2) DEFAULT 0.00 COMMENT '上边距（mm）',
  `margin_bottom` decimal(10,2) DEFAULT 0.00 COMMENT '下边距（mm）',
  `margin_left` decimal(10,2) DEFAULT 0.00 COMMENT '左边距（mm）',
  `margin_right` decimal(10,2) DEFAULT 0.00 COMMENT '右边距（mm）',
  `font_size` int(4) DEFAULT 14 COMMENT '字体大小（px）',
  `table_font_size` int(4) DEFAULT 12 COMMENT '表格字体大小（px）',
  `column_spacing` decimal(10,2) DEFAULT 0.00 COMMENT '列间距（mm）',
  `show_purchaser` tinyint(1) DEFAULT 0 COMMENT '显示采购人（0否，1是）',
  `show_creator` tinyint(1) DEFAULT 1 COMMENT '显示制单人（0否，1是）',
  `show_auditor` tinyint(1) DEFAULT 1 COMMENT '显示复核人（0否，1是）',
  `show_receiver` tinyint(1) DEFAULT 0 COMMENT '显示验收人（0否，1是）',
  `purchaser_label` varchar(50) DEFAULT '采购人' COMMENT '采购人标签',
  `creator_label` varchar(50) DEFAULT '制单人' COMMENT '制单人标签',
  `auditor_label` varchar(50) DEFAULT '复核人' COMMENT '复核人标签',
  `receiver_label` varchar(50) DEFAULT '验收人' COMMENT '验收人标签',
  `column_config` text COMMENT '列配置（JSON）',
  `is_default` tinyint(1) DEFAULT 0 COMMENT '是否默认模板（0否，1是）',
  `status` char(1) DEFAULT '0' COMMENT '状态（0正常，1停用）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT '' COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_bill_type` (`bill_type`),
  KEY `idx_status` (`status`),
  KEY `idx_tenant_bill` (`tenant_id`,`bill_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='打印设置表';
/

-- 仓库申请单（科室申领审核通过后按仓库拆分；主键 UUID7 36 位）
CREATE TABLE IF NOT EXISTS `wh_warehouse_apply` (
  `id` varchar(36) NOT NULL COMMENT '主键UUID7（36位含连字符）',
  `tenant_id` varchar(36) DEFAULT NULL COMMENT '租户ID(同sb_customer.customer_id)',
  `apply_bill_no` varchar(64) DEFAULT NULL COMMENT '仓库申请单号',
  `bas_apply_id` varchar(32) NOT NULL COMMENT '科室申领主表ID（bas_apply.id，varchar便于与UUID体系一致）',
  `bas_apply_bill_no` varchar(128) DEFAULT NULL COMMENT '科室申领单号',
  `warehouse_id` bigint NOT NULL COMMENT '发货仓库ID（fd_warehouse.id）',
  `department_id` bigint DEFAULT NULL COMMENT '申领目标科室ID',
  `bill_status` int NOT NULL DEFAULT 2 COMMENT '状态：1待审核 2已生效(已自科室申领审核生成) 3关闭 5整单作废',
  `void_whole_flag` int NOT NULL DEFAULT 0 COMMENT '整单作废：0否 1是',
  `void_whole_by` varchar(64) DEFAULT NULL COMMENT '整单作废人',
  `void_whole_time` datetime DEFAULT NULL COMMENT '整单作废时间',
  `void_whole_reason` varchar(500) DEFAULT NULL COMMENT '整单作废原因',
  `total_qty` decimal(18,2) DEFAULT NULL COMMENT '明细数量合计',
  `total_amt` decimal(18,2) DEFAULT NULL COMMENT '明细金额合计',
  `source_audit_date` datetime DEFAULT NULL COMMENT '科室申领审核时间（快照）',
  `del_flag` int NOT NULL DEFAULT 0 COMMENT '删除标志',
  `delete_by` varchar(64) DEFAULT NULL COMMENT '删除者',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_wh_wh_apply_tenant` (`tenant_id`),
  KEY `idx_wh_wh_apply_bas` (`bas_apply_id`),
  KEY `idx_wh_wh_apply_wh` (`warehouse_id`),
  KEY `idx_wh_wh_apply_no` (`apply_bill_no`),
  KEY `idx_wh_wh_apply_void` (`void_whole_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='仓库申请单主表（由科室申领审核按仓拆分）';
/

CREATE TABLE IF NOT EXISTS `wh_warehouse_apply_entry` (
  `id` varchar(36) NOT NULL COMMENT '主键UUID7',
  `paren_id` varchar(36) NOT NULL COMMENT '仓库申请单主表ID',
  `tenant_id` varchar(36) DEFAULT NULL COMMENT '租户ID',
  `bas_apply_id` varchar(32) NOT NULL COMMENT '科室申领主表ID',
  `bas_apply_bill_no` varchar(128) DEFAULT NULL COMMENT '科室申领单号',
  `bas_apply_entry_id` varchar(32) NOT NULL COMMENT '科室申领明细ID（bas_apply_entry.id）',
  `line_no` int DEFAULT NULL COMMENT '行号',
  `material_id` bigint NOT NULL COMMENT '耗材ID',
  `warehouse_id` bigint NOT NULL COMMENT '仓库ID（冗余，与主表一致）',
  `stk_inventory_id` bigint DEFAULT NULL COMMENT '来源库存明细行ID（stk_inventory.id）',
  `unit_price` decimal(18,2) DEFAULT NULL COMMENT '单价（按拆分时的库存行）',
  `qty` decimal(18,2) DEFAULT NULL COMMENT '数量',
  `price` decimal(18,2) DEFAULT NULL COMMENT '价格',
  `amt` decimal(18,2) DEFAULT NULL COMMENT '金额',
  `batch_no` varchar(100) DEFAULT NULL COMMENT '入库批次号',
  `batch_number` varchar(100) DEFAULT NULL COMMENT '生产批号',
  `begin_time` date DEFAULT NULL COMMENT '生产日期',
  `end_time` date DEFAULT NULL COMMENT '有效期',
  `supplier_id` bigint DEFAULT NULL COMMENT '供应商ID',
  `factory_id` bigint DEFAULT NULL COMMENT '生产厂家ID',
  `line_void_status` int NOT NULL DEFAULT 0 COMMENT '明细作废状态：0正常 1已作废',
  `line_void_qty` decimal(18,2) NOT NULL DEFAULT 0.00 COMMENT '累计作废数量',
  `line_void_by` varchar(64) DEFAULT NULL COMMENT '最近一次明细作废操作人',
  `line_void_time` datetime DEFAULT NULL COMMENT '最近一次明细作废时间',
  `line_void_reason` varchar(500) DEFAULT NULL COMMENT '最近一次明细作废原因',
  `del_flag` int NOT NULL DEFAULT 0 COMMENT '删除标志',
  `delete_by` varchar(64) DEFAULT NULL COMMENT '删除者',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_wh_wh_apply_e_paren` (`paren_id`),
  KEY `idx_wh_wh_apply_e_bas` (`bas_apply_id`),
  KEY `idx_wh_wh_apply_e_mat` (`material_id`),
  KEY `idx_wh_wh_apply_e_tenant` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='仓库申请单明细（含科室申领明细追溯）';
/

-- 库房申请单明细 <-> 出库单明细 关联（主键 UUID7；外键类字段一律 varchar 存字符串）
CREATE TABLE IF NOT EXISTS `wh_wh_apply_ck_entry_ref` (
  `id` varchar(36) NOT NULL COMMENT '主键UUID7（36位）',
  `tenant_id` varchar(36) NOT NULL COMMENT '租户ID',
  `wh_apply_id` varchar(36) NOT NULL COMMENT '库房申请单主表ID',
  `wh_apply_bill_no` varchar(64) DEFAULT NULL COMMENT '库房申请单号（冗余）',
  `wh_apply_entry_id` varchar(36) NOT NULL COMMENT '库房申请单明细ID',
  `ck_bill_id` varchar(32) NOT NULL COMMENT '出库单主表ID（stk_io_bill.id）',
  `ck_bill_no` varchar(64) DEFAULT NULL COMMENT '出库单号（冗余）',
  `ck_entry_id` varchar(32) NOT NULL COMMENT '出库单明细ID（stk_io_bill_entry.id）',
  `ref_qty` decimal(18,2) NOT NULL COMMENT '本行关联数量',
  `ref_amt` decimal(18,2) DEFAULT NULL COMMENT '关联金额快照',
  `link_status` tinyint NOT NULL DEFAULT 1 COMMENT '1有效 0已解除',
  `del_flag` int NOT NULL DEFAULT 0 COMMENT '删除标志',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_wh_ck_pair` (`wh_apply_entry_id`,`ck_entry_id`),
  KEY `idx_wh_ck_ref_apply` (`wh_apply_id`),
  KEY `idx_wh_ck_ref_ck` (`ck_bill_id`),
  KEY `idx_wh_ck_ref_tenant` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库房申请单明细与出库单明细关联';
/

-- ========== 高值备货：明细引用关系（主键 UUID7；与 column.sql 增量段定义一致，全量库请以此为准）==========
-- 与库房申请出库关联 wh_wh_apply_ck_entry_ref、耗材全路径 hc_doc_bill_ref 并列，构成业务侧多类明细追溯

CREATE TABLE IF NOT EXISTS `gz_order_entry_code_ref` (
  `id` varchar(36) NOT NULL COMMENT '主键UUID7',
  `tenant_id` varchar(36) DEFAULT NULL COMMENT '租户',
  `src_acceptance_id` varchar(36) DEFAULT NULL COMMENT '备货验收主表ID',
  `src_acceptance_no` varchar(64) DEFAULT NULL COMMENT '验收单号',
  `src_order_entry_id` varchar(36) DEFAULT NULL COMMENT '验收明细ID',
  `src_barcode_line_id` varchar(36) DEFAULT NULL COMMENT '条码明细ID(gz_order_entry_inhospitalcode_list)',
  `src_in_hospital_code` varchar(200) DEFAULT NULL COMMENT '院内码',
  `tgt_bill_kind` varchar(32) DEFAULT NULL COMMENT '目标单据类型 GZ_SHIPMENT 等',
  `tgt_main_id` varchar(36) DEFAULT NULL COMMENT '目标主表ID',
  `tgt_bill_no` varchar(64) DEFAULT NULL COMMENT '目标单号',
  `tgt_entry_id` varchar(36) DEFAULT NULL COMMENT '目标明细ID',
  `ref_purpose` varchar(200) DEFAULT NULL COMMENT '引用用途（中文）',
  `material_id` bigint DEFAULT NULL COMMENT '耗材ID冗余',
  `material_name` varchar(300) DEFAULT NULL COMMENT '耗材名称冗余',
  `warehouse_id` bigint DEFAULT NULL COMMENT '仓库冗余',
  `create_by` varchar(64) DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_gz_code_ref_barcode` (`src_barcode_line_id`),
  KEY `idx_gz_code_ref_tgt` (`tgt_bill_kind`,`tgt_entry_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='备货验收条码明细引用关系';
/

CREATE TABLE IF NOT EXISTS `gz_shipment_entry_ref` (
  `id` varchar(36) NOT NULL COMMENT '主键UUID7',
  `tenant_id` varchar(36) DEFAULT NULL,
  `shipment_entry_id` varchar(36) DEFAULT NULL COMMENT '备货出库明细ID',
  `src_bill_kind` varchar(32) DEFAULT NULL COMMENT '来源类型',
  `src_main_id` varchar(36) DEFAULT NULL,
  `src_bill_no` varchar(64) DEFAULT NULL,
  `src_detail_id` varchar(36) DEFAULT NULL,
  `src_in_hospital_code` varchar(200) DEFAULT NULL,
  `tgt_bill_kind` varchar(32) DEFAULT NULL,
  `tgt_main_id` varchar(36) DEFAULT NULL,
  `tgt_bill_no` varchar(64) DEFAULT NULL,
  `tgt_entry_id` varchar(36) DEFAULT NULL,
  `ref_purpose` varchar(200) DEFAULT NULL,
  `material_id` bigint DEFAULT NULL,
  `material_name` varchar(300) DEFAULT NULL,
  `create_by` varchar(64) DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_gz_ship_ref_entry` (`shipment_entry_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='备货出库明细引用关系';
/

CREATE TABLE IF NOT EXISTS `gz_refund_goods_entry_ref` (
  `id` varchar(36) NOT NULL COMMENT '主键UUID7',
  `tenant_id` varchar(36) DEFAULT NULL,
  `refund_goods_entry_id` varchar(36) DEFAULT NULL COMMENT '退库/退货明细ID',
  `src_bill_kind` varchar(32) DEFAULT NULL,
  `src_main_id` varchar(36) DEFAULT NULL,
  `src_bill_no` varchar(64) DEFAULT NULL,
  `src_detail_id` varchar(36) DEFAULT NULL,
  `src_in_hospital_code` varchar(200) DEFAULT NULL,
  `tgt_bill_kind` varchar(32) DEFAULT NULL,
  `tgt_main_id` varchar(36) DEFAULT NULL,
  `tgt_bill_no` varchar(64) DEFAULT NULL,
  `tgt_entry_id` varchar(36) DEFAULT NULL,
  `ref_purpose` varchar(200) DEFAULT NULL,
  `material_id` bigint DEFAULT NULL,
  `material_name` varchar(300) DEFAULT NULL,
  `create_by` varchar(64) DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_gz_rg_ref_entry` (`refund_goods_entry_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='备货退库/退货明细引用关系';
/

-- 数据备份配置（与 sys_job 联动；tenant_id 空串表示平台/全库维度）
CREATE TABLE IF NOT EXISTS `sys_data_backup_config` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `tenant_id` varchar(36) NOT NULL DEFAULT '' COMMENT '租户ID(同 sb_customer.customer_id)，空串表示平台',
  `backup_path` varchar(500) NOT NULL DEFAULT '' COMMENT '备份文件目录（服务器本地路径）',
  `mysqldump_path` varchar(500) NOT NULL DEFAULT '' COMMENT 'mysqldump 可执行文件路径（可选；为空则从 PATH 查找）',
  `backup_time` varchar(8) NOT NULL DEFAULT '02:00' COMMENT '每日备份时间 HH:mm',
  `enabled` char(1) NOT NULL DEFAULT '0' COMMENT '是否启用（0停用 1启用）',
  `job_id` bigint DEFAULT NULL COMMENT '关联 sys_job.job_id',
  `retain_days` int NOT NULL DEFAULT 7 COMMENT '保留最近多少天的备份文件（0表示不自动清理）',
  `last_backup_time` datetime DEFAULT NULL COMMENT '最近一次备份完成时间',
  `last_backup_status` varchar(32) DEFAULT NULL COMMENT '最近一次状态 success/failed/skipped',
  `last_backup_message` varchar(500) DEFAULT NULL COMMENT '最近一次结果说明',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sys_data_backup_tenant` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='数据备份配置';
/

-- HIS 外联库按租户（与 sb_customer.customer_id / sys_user.customer_id 对齐）；抓取镜像时按 tenant_id 选库与 SQL
CREATE TABLE IF NOT EXISTS `sys_his_external_db` (
  `tenant_id` varchar(36) NOT NULL COMMENT '租户ID',
  `db_type` varchar(16) NOT NULL DEFAULT 'SQLSERVER' COMMENT 'SQLSERVER=默认视图SQL；MYSQL=须填自定义区间SQL',
  `driver_class` varchar(256) DEFAULT NULL COMMENT '为空则按 db_type 使用内置驱动类名',
  `jdbc_url` varchar(1024) NOT NULL COMMENT 'JDBC URL',
  `username` varchar(128) NOT NULL COMMENT '账号',
  `password` varchar(512) DEFAULT NULL COMMENT '口令',
  `enabled` char(1) NOT NULL DEFAULT '1' COMMENT '0停用 1启用',
  `sql_inpatient_range` mediumtext COMMENT '住院区间查询SQL，两个?为起止时间；SQLSERVER可空走内置',
  `sql_outpatient_range` mediumtext COMMENT '门诊区间查询SQL，两个?为起止时间；SQLSERVER可空走内置',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`tenant_id`),
  KEY `idx_sys_his_ext_enabled` (`enabled`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='租户级HIS外联库连接';
/

INSERT INTO sys_his_external_db (tenant_id, db_type, driver_class, jdbc_url, username, password, enabled, sql_inpatient_range, sql_outpatient_range, remark)
VALUES (
  'hengsui-third-001',
  'SQLSERVER',
  'com.microsoft.sqlserver.jdbc.SQLServerDriver',
  'jdbc:sqlserver://127.0.0.1;databaseName=THIS4;encrypt=false;trustServerCertificate=true;loginTimeout=60',
  'sa',
  '',
  '1',
  NULL,
  NULL,
  '示例：请按现场修改 url/账号/口令；启用抓取时 enabled=1'
) ON DUPLICATE KEY UPDATE remark = VALUES(remark);
/

-- HIS 患者计费镜像（住院）：业务唯一键 (tenant_id, his_inpatient_charge_id)，主键 UUID 36 位
-- 退费说明：HIS 退费/冲账数据在未建立与镜像行、抓取批次、院内码的稳定关联前不做自动处理，避免串批次、串条码；后续单独建模再对接。
CREATE TABLE IF NOT EXISTS `his_inpatient_charge_mirror` (
  `id` varchar(36) NOT NULL COMMENT '主键UUID',
  `tenant_id` varchar(36) NOT NULL COMMENT '租户ID',
  `fetch_batch_id` varchar(36) DEFAULT NULL COMMENT '抓取批次ID',
  `his_inpatient_charge_id` varchar(32) NOT NULL COMMENT 'HIS住院计费明细主键',
  `patient_id` varchar(32) DEFAULT NULL COMMENT '患者ID',
  `patient_name` varchar(128) DEFAULT NULL COMMENT '患者姓名',
  `inpatient_no` varchar(64) DEFAULT NULL COMMENT '住院号',
  `dept_code` varchar(32) DEFAULT NULL COMMENT '费用科室编码',
  `dept_name` varchar(128) DEFAULT NULL COMMENT '费用科室名称',
  `doctor_id` varchar(32) DEFAULT NULL COMMENT '医生ID',
  `doctor_name` varchar(128) DEFAULT NULL COMMENT '医生姓名',
  `charge_item_id` varchar(64) DEFAULT NULL COMMENT '收费项目ID',
  `item_name` varchar(512) DEFAULT NULL COMMENT '项目名称',
  `spec_model` varchar(128) DEFAULT NULL COMMENT '规格型号',
  `batch_no` varchar(128) DEFAULT NULL COMMENT '批号',
  `expire_date` varchar(64) DEFAULT NULL COMMENT '效期',
  `use_date` varchar(32) DEFAULT NULL COMMENT '使用时间',
  `charge_date` varchar(32) DEFAULT NULL COMMENT '计费时间',
  `quantity` decimal(18,6) DEFAULT NULL COMMENT '数量',
  `unit_price` decimal(18,6) DEFAULT NULL COMMENT '单价',
  `total_amount` decimal(18,6) DEFAULT NULL COMMENT '金额',
  `charge_operator` varchar(128) DEFAULT NULL COMMENT '计费操作员',
  `remark` varchar(512) DEFAULT NULL COMMENT '备注',
  `row_fingerprint` varchar(64) DEFAULT NULL COMMENT '关键字段指纹(防重复与漂移检测)',
  `process_status` varchar(32) NOT NULL DEFAULT 'PENDING_CONSUME' COMMENT 'PENDING_CONSUME待处理/PARTIALLY_CONSUMED高值部分消耗/CONSUMED已完成；退费未关联前勿自动回滚',
  `process_type` varchar(32) DEFAULT NULL COMMENT '处理类型 LOW_VALUE/HIGH_VALUE',
  `process_time` datetime DEFAULT NULL COMMENT '处理时间',
  `process_by` varchar(64) DEFAULT NULL COMMENT '处理人',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '本地入库时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_his_inp_mirror_tenant_hisid` (`tenant_id`,`his_inpatient_charge_id`),
  KEY `idx_his_inp_mirror_tenant_charge_date` (`tenant_id`,`charge_date`),
  KEY `idx_his_inp_mirror_fetch_batch` (`fetch_batch_id`),
  KEY `idx_his_inp_mirror_charge_item` (`tenant_id`,`charge_item_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='HIS住院患者耗材计费镜像';
/

-- HIS 患者计费镜像（门诊）；退费处理原则同住院镜像说明。
CREATE TABLE IF NOT EXISTS `his_outpatient_charge_mirror` (
  `id` varchar(36) NOT NULL COMMENT '主键UUID',
  `tenant_id` varchar(36) NOT NULL COMMENT '租户ID',
  `fetch_batch_id` varchar(36) DEFAULT NULL COMMENT '抓取批次ID',
  `his_outpatient_charge_id` varchar(32) NOT NULL COMMENT 'HIS门诊计费明细主键',
  `patient_id` varchar(32) DEFAULT NULL COMMENT '患者ID',
  `patient_name` varchar(128) DEFAULT NULL COMMENT '患者姓名',
  `outpatient_no` varchar(64) DEFAULT NULL COMMENT '门诊号',
  `clinic_code` varchar(32) DEFAULT NULL COMMENT '就诊编码',
  `clinic_name` varchar(128) DEFAULT NULL COMMENT '就诊名称',
  `doctor_id` varchar(32) DEFAULT NULL COMMENT '医生ID',
  `doctor_name` varchar(128) DEFAULT NULL COMMENT '医生姓名',
  `charge_item_id` varchar(64) DEFAULT NULL COMMENT '收费项目ID',
  `item_name` varchar(512) DEFAULT NULL COMMENT '项目名称',
  `spec_model` varchar(128) DEFAULT NULL COMMENT '规格型号',
  `batch_no` varchar(128) DEFAULT NULL COMMENT '批号',
  `expire_date` varchar(64) DEFAULT NULL COMMENT '效期',
  `charge_date` varchar(32) DEFAULT NULL COMMENT '计费时间',
  `quantity` decimal(18,6) DEFAULT NULL COMMENT '数量',
  `unit_price` decimal(18,6) DEFAULT NULL COMMENT '单价',
  `total_amount` decimal(18,6) DEFAULT NULL COMMENT '金额',
  `charge_operator` varchar(128) DEFAULT NULL COMMENT '计费操作员',
  `payment_type` varchar(32) DEFAULT NULL COMMENT '支付方式',
  `receipt_no` varchar(64) DEFAULT NULL COMMENT '收据号',
  `remark` varchar(512) DEFAULT NULL COMMENT '备注',
  `row_fingerprint` varchar(64) DEFAULT NULL COMMENT '关键字段指纹',
  `process_status` varchar(32) NOT NULL DEFAULT 'PENDING_CONSUME' COMMENT 'PENDING_CONSUME待处理/PARTIALLY_CONSUMED高值部分消耗/CONSUMED已完成；退费未关联前勿自动回滚',
  `process_type` varchar(32) DEFAULT NULL COMMENT '处理类型 LOW_VALUE/HIGH_VALUE',
  `process_time` datetime DEFAULT NULL COMMENT '处理时间',
  `process_by` varchar(64) DEFAULT NULL COMMENT '处理人',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '本地入库时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_his_out_mirror_tenant_hisid` (`tenant_id`,`his_outpatient_charge_id`),
  KEY `idx_his_out_mirror_tenant_charge_date` (`tenant_id`,`charge_date`),
  KEY `idx_his_out_mirror_fetch_batch` (`fetch_batch_id`),
  KEY `idx_his_out_mirror_charge_item` (`tenant_id`,`charge_item_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='HIS门诊患者耗材计费镜像';
/

-- HIS 计费抓取批次日志
CREATE TABLE IF NOT EXISTS `his_charge_fetch_batch` (
  `id` varchar(36) NOT NULL COMMENT '主键UUID',
  `tenant_id` varchar(36) NOT NULL COMMENT '租户ID',
  `charge_kind` varchar(16) NOT NULL COMMENT 'INPATIENT/OUTPATIENT',
  `window_start` datetime NOT NULL COMMENT '查询窗口起(含)',
  `window_end` datetime NOT NULL COMMENT '查询窗口止(不含)',
  `inserted_count` int NOT NULL DEFAULT 0 COMMENT '本次新增条数',
  `skipped_count` int NOT NULL DEFAULT 0 COMMENT '已存在且指纹一致跳过',
  `drift_count` int NOT NULL DEFAULT 0 COMMENT '已存在但指纹不一致(HIS可能已变更)',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '抓取完成时间',
  PRIMARY KEY (`id`),
  KEY `idx_his_fetch_batch_tenant_time` (`tenant_id`,`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='HIS计费数据抓取批次';
/

-- HIS 收费项目本地镜像（来源 v_charge_item；用于对照页面查询与排障）
CREATE TABLE IF NOT EXISTS `his_charge_item_mirror` (
  `tenant_id` varchar(64) NOT NULL COMMENT '租户ID',
  `charge_item_id` varchar(64) NOT NULL COMMENT 'HIS收费项目ID（v_charge_item.charge_item_id）',
  `item_code` varchar(64) DEFAULT NULL COMMENT '收费编码',
  `item_name` varchar(256) DEFAULT NULL COMMENT '收费名称',
  `item_type` varchar(32) DEFAULT NULL COMMENT '项目类型',
  `consumable_type` varchar(32) DEFAULT NULL COMMENT '耗材类型',
  `spec_model` varchar(64) DEFAULT NULL COMMENT '规格型号',
  `unit` varchar(32) DEFAULT NULL COMMENT '单位',
  `price` decimal(18,6) DEFAULT NULL COMMENT '价格',
  `manufacturer` varchar(64) DEFAULT NULL COMMENT '生产厂家',
  `register_no` varchar(128) DEFAULT NULL COMMENT '注册证号',
  `is_active` varchar(16) DEFAULT NULL COMMENT '是否有效',
  `referred_code` varchar(64) DEFAULT NULL COMMENT '收费项目拼音简码（首字母）',
  `his_create_time` varchar(32) DEFAULT NULL COMMENT 'HIS创建时间(字符串)',
  `his_update_time` varchar(32) DEFAULT NULL COMMENT 'HIS更新时间(字符串)',
  `deleted_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '本地删除标记：0正常，1已删除(HIS未返回)',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '本地首次入库时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '本地最近刷新时间',
  PRIMARY KEY (`tenant_id`,`charge_item_id`),
  KEY `idx_his_charge_item_mirror_name` (`tenant_id`,`item_name`),
  KEY `idx_his_charge_item_mirror_referred` (`tenant_id`,`referred_code`),
  KEY `idx_his_charge_item_mirror_spec` (`tenant_id`,`spec_model`),
  KEY `idx_his_charge_item_mirror_deleted` (`tenant_id`,`deleted_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='HIS收费项目本地镜像';
/

-- HIS 镜像行与科室批量消耗明细追溯（一条镜像可对应多条库存拆分明细）
CREATE TABLE IF NOT EXISTS `his_mirror_consume_link` (
  `id` varchar(36) NOT NULL COMMENT '主键UUID',
  `tenant_id` varchar(36) NOT NULL COMMENT '租户ID',
  `visit_kind` varchar(16) NOT NULL COMMENT 'INPATIENT/OUTPATIENT',
  `mirror_row_id` varchar(36) NOT NULL COMMENT '镜像表主键 his_*_charge_mirror.id',
  `fetch_batch_id` varchar(36) DEFAULT NULL COMMENT '抓取批次ID',
  `dept_batch_consume_id` bigint NOT NULL COMMENT '科室批量消耗主表 t_hc_ks_xh.id',
  `dept_batch_consume_entry_id` bigint NOT NULL COMMENT '科室批量消耗明细 t_hc_ks_xh_entry.id',
  `alloc_qty` decimal(18,6) NOT NULL COMMENT '本行分摊数量',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_hmcl_mirror` (`mirror_row_id`),
  KEY `idx_hmcl_consume` (`dept_batch_consume_id`),
  KEY `idx_hmcl_fetch` (`tenant_id`,`fetch_batch_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='HIS计费镜像与科室消耗明细关联（退费未建模前勿删改以免串批次/院内码）';
/

-- 按单据类型的打印每页行数（与 spd-ui 打印页 docKind 一致；可重复执行：INSERT 使用 INSERT IGNORE）
CREATE TABLE IF NOT EXISTS `sys_print_doc_rows` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `doc_kind` varchar(32) NOT NULL COMMENT '单据类型：INBOUND/OUTBOUND/REFUND_DEPOT/REFUND_GOODS',
  `rows_per_page` int NOT NULL DEFAULT 6 COMMENT '每页明细行数',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sys_print_doc_rows_doc_kind` (`doc_kind`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='按单据类型的打印每页行数';
/

INSERT IGNORE INTO `sys_print_doc_rows` (`doc_kind`, `rows_per_page`, `create_by`, `remark`) VALUES
('INBOUND', 6, 'system', '入库'),
('OUTBOUND', 6, 'system', '出库'),
('REFUND_DEPOT', 6, 'system', '退库'),
('REFUND_GOODS', 6, 'system', '退货');
/

/* 以下为重复建表定义（与上文 supp_settlement_invoice 一致），仅保留作参考；实际以首次定义为准，已含 delete_by、delete_time */
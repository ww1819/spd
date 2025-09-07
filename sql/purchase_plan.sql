-- 采购计划主表
CREATE TABLE `purchase_plan` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `plan_no` varchar(50) NOT NULL COMMENT '计划单号',
  `supplier_id` bigint(20) DEFAULT NULL COMMENT '供应商ID',
  `warehouse_id` bigint(20) DEFAULT NULL COMMENT '仓库ID',
  `department_id` bigint(20) DEFAULT NULL COMMENT '部门ID',
  `plan_date` date NOT NULL COMMENT '计划日期',
  `plan_status` char(1) NOT NULL DEFAULT '1' COMMENT '计划状态（1待审核 2已审核 3已执行 4已取消）',
  `pro_person` varchar(50) DEFAULT NULL COMMENT '采购员',
  `total_amount` decimal(15,2) DEFAULT '0.00' COMMENT '总金额',
  `telephone` varchar(20) DEFAULT NULL COMMENT '联系电话',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志（0代表存在 1代表删除）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `audit_by` varchar(64) DEFAULT NULL COMMENT '审核人',
  `audit_date` datetime DEFAULT NULL COMMENT '审核日期',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_plan_no` (`plan_no`),
  KEY `idx_plan_date` (`plan_date`),
  KEY `idx_plan_status` (`plan_status`),
  KEY `idx_supplier_id` (`supplier_id`),
  KEY `idx_warehouse_id` (`warehouse_id`),
  KEY `idx_department_id` (`department_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='采购计划主表';

-- 采购计划明细表
CREATE TABLE `purchase_plan_entry` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `parent_id` bigint(20) NOT NULL COMMENT '主表ID',
  `material_id` bigint(20) NOT NULL COMMENT '耗材ID',
  `qty` decimal(15,2) NOT NULL DEFAULT '0.00' COMMENT '计划数量',
  `price` decimal(15,2) DEFAULT '0.00' COMMENT '单价',
  `amt` decimal(15,2) DEFAULT '0.00' COMMENT '金额',
  `speci` varchar(100) DEFAULT NULL COMMENT '规格',
  `model` varchar(100) DEFAULT NULL COMMENT '型号',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志（0代表存在 1代表删除）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_parent_id` (`parent_id`),
  KEY `idx_material_id` (`material_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='采购计划明细表';

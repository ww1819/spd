-- 仓库流水表、科室流水表（出库审核插仓库流水，收货确认插科室流水并反写 kc_no）

-- 1. 仓库流水表 t_hc_ck_flow（出库审核时每条出库明细插一条）
DROP TABLE IF EXISTS `t_hc_ck_flow`;
CREATE TABLE `t_hc_ck_flow` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `bill_id` bigint(20) DEFAULT NULL COMMENT '出库单id',
  `entry_id` bigint(20) DEFAULT NULL COMMENT '出库单明细id',
  `warehouse_id` bigint(20) DEFAULT NULL COMMENT '仓库ID',
  `material_id` bigint(20) DEFAULT NULL COMMENT '耗材ID',
  `batch_no` varchar(100) DEFAULT NULL COMMENT '批次号',
  `batch_number` varchar(100) DEFAULT NULL COMMENT '批号',
  `qty` decimal(18,2) DEFAULT 0.00 COMMENT '数量',
  `unit_price` decimal(18,2) DEFAULT 0.00 COMMENT '单价',
  `amt` decimal(18,2) DEFAULT 0.00 COMMENT '金额',
  `begin_time` date DEFAULT NULL COMMENT '生产日期',
  `end_time` date DEFAULT NULL COMMENT '有效期',
  `supplier_id` bigint(20) DEFAULT NULL COMMENT '供应商ID',
  `kc_no` bigint(20) DEFAULT NULL COMMENT '科室库存明细id（收货确认后反写）',
  `flow_time` datetime DEFAULT NULL COMMENT '流水时间',
  `del_flag` int(1) NOT NULL DEFAULT 0 COMMENT '删除标志（0=正常，1=已删除）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_bill_id` (`bill_id`),
  KEY `idx_entry_id` (`entry_id`),
  KEY `idx_flow_time` (`flow_time`),
  KEY `idx_del_flag` (`del_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='仓库流水表';

-- 2. 科室流水表 t_hc_ks_flow（收货确认时每条出库明细插一条）
DROP TABLE IF EXISTS `t_hc_ks_flow`;
CREATE TABLE `t_hc_ks_flow` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `bill_id` bigint(20) DEFAULT NULL COMMENT '出库单id',
  `entry_id` bigint(20) DEFAULT NULL COMMENT '出库单明细id',
  `department_id` bigint(20) DEFAULT NULL COMMENT '科室ID',
  `material_id` bigint(20) DEFAULT NULL COMMENT '耗材ID',
  `batch_no` varchar(100) DEFAULT NULL COMMENT '批次号',
  `batch_number` varchar(100) DEFAULT NULL COMMENT '批号',
  `qty` decimal(18,2) DEFAULT 0.00 COMMENT '数量',
  `unit_price` decimal(18,2) DEFAULT 0.00 COMMENT '单价',
  `amt` decimal(18,2) DEFAULT 0.00 COMMENT '金额',
  `begin_time` date DEFAULT NULL COMMENT '生产日期',
  `end_time` date DEFAULT NULL COMMENT '有效期',
  `supplier_id` varchar(64) DEFAULT NULL COMMENT '供应商ID',
  `kc_no` bigint(20) DEFAULT NULL COMMENT '科室库存明细id',
  `flow_time` datetime DEFAULT NULL COMMENT '流水时间',
  `del_flag` int(1) NOT NULL DEFAULT 0 COMMENT '删除标志（0=正常，1=已删除）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_bill_id` (`bill_id`),
  KEY `idx_entry_id` (`entry_id`),
  KEY `idx_flow_time` (`flow_time`),
  KEY `idx_del_flag` (`del_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='科室流水表';

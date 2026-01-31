-- 盈亏单：必须引用已审核盘点单，仅加载有盈亏的明细；审核时校验当前库存=账面数量后加减库存、写仓库流水(PY/PK)、盘盈写批次表

-- 1. 盈亏单主表
DROP TABLE IF EXISTS `stk_io_profit_loss`;
CREATE TABLE `stk_io_profit_loss` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `bill_no` varchar(64) DEFAULT NULL COMMENT '盈亏单号',
  `stocktaking_id` bigint(20) DEFAULT NULL COMMENT '关联盘点单ID',
  `stocktaking_no` varchar(64) DEFAULT NULL COMMENT '盘点单号（冗余）',
  `warehouse_id` bigint(20) DEFAULT NULL COMMENT '仓库ID',
  `bill_status` int(2) DEFAULT 1 COMMENT '单据状态 1待审核 2已审核',
  `audit_by` varchar(64) DEFAULT NULL COMMENT '审核人',
  `audit_date` datetime DEFAULT NULL COMMENT '审核时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建人',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新人',
  `del_flag` int(1) NOT NULL DEFAULT 0 COMMENT '删除标志（0=正常，1=已删除）',
  PRIMARY KEY (`id`),
  KEY `idx_stocktaking_id` (`stocktaking_id`),
  KEY `idx_warehouse_id` (`warehouse_id`),
  KEY `idx_bill_status` (`bill_status`),
  KEY `idx_del_flag` (`del_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='盈亏单主表';

-- 2. 盈亏单明细表
DROP TABLE IF EXISTS `stk_io_profit_loss_entry`;
CREATE TABLE `stk_io_profit_loss_entry` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `paren_id` bigint(20) DEFAULT NULL COMMENT '盈亏单ID',
  `stocktaking_entry_id` bigint(20) DEFAULT NULL COMMENT '来源盘点明细ID',
  `material_id` bigint(20) DEFAULT NULL COMMENT '耗材ID',
  `batch_no` varchar(100) DEFAULT NULL COMMENT '批次号',
  `batch_number` varchar(100) DEFAULT NULL COMMENT '批号',
  `book_qty` decimal(18,2) DEFAULT NULL COMMENT '当前库存（盘点单当时账面qty，用于审核校验）',
  `stock_qty` decimal(18,2) DEFAULT NULL COMMENT '盘点库存（盘点数量）',
  `profit_qty` decimal(18,2) DEFAULT NULL COMMENT '盈亏数量（正=盘盈，负=盘亏）',
  `unit_price` decimal(18,2) DEFAULT NULL COMMENT '单价',
  `profit_amount` decimal(18,2) DEFAULT NULL COMMENT '盈亏金额',
  `begin_time` date DEFAULT NULL COMMENT '生产日期',
  `end_time` date DEFAULT NULL COMMENT '有效期',
  `del_flag` int(1) DEFAULT 0 COMMENT '删除标志',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建人',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新人',
  PRIMARY KEY (`id`),
  KEY `idx_paren_id` (`paren_id`),
  KEY `idx_stocktaking_entry_id` (`stocktaking_entry_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='盈亏单明细表';

-- 若表已存在且无 stock_qty 列，可执行以下语句增加“盘点库存”列：
-- ALTER TABLE stk_io_profit_loss_entry ADD COLUMN stock_qty decimal(18,2) DEFAULT NULL COMMENT '盘点库存（盘点数量）' AFTER book_qty;

-- 为 fd_supplier 表添加 supplier_type 列
-- 执行时间: 2025-12-28
-- 说明: 添加供应商类型字段，支持存储多个类型（用逗号分隔）

ALTER TABLE fd_supplier ADD COLUMN supplier_type VARCHAR(255) DEFAULT NULL COMMENT '供应商类型（多个类型用逗号分隔，如：耗材,设备,配件）';


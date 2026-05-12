-- ---------------------------------------------------------------------------
-- 补丁：gz_order 增加 audit_by（审核人用户ID），与代码 BaseEntity / GzOrderMapper 一致
-- 背景：新建库脚本 table.sql 已含该列；若库是早期版本，会报 Unknown column 'gz.audit_by'
-- 用法：在目标库执行本脚本一次；若列已存在会报错，可先查 information_schema 或忽略重复执行错误
-- ---------------------------------------------------------------------------

ALTER TABLE `gz_order`
  ADD COLUMN `audit_by` varchar(64) DEFAULT NULL COMMENT '审核人' AFTER `audit_date`;

-- 可选：将历史已审核单的审核人回填为当时的 update_by（仅当 audit_by 为空时）
-- UPDATE gz_order SET audit_by = update_by WHERE order_status = 2 AND (audit_by IS NULL OR audit_by = '');

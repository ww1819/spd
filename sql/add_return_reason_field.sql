-- ============================================
-- 添加退货原因字段到stk_io_bill表
-- 创建时间：2025-01-20
-- ============================================

-- 方法1：直接添加字段（如果字段已存在会报错，可以忽略）
ALTER TABLE `stk_io_bill` 
ADD COLUMN `return_reason` VARCHAR(500) DEFAULT NULL COMMENT '退货原因' AFTER `pro_person`;

-- 方法2：如果方法1报错说字段已存在，可以使用下面的语句先检查
-- SELECT COLUMN_NAME, DATA_TYPE, CHARACTER_MAXIMUM_LENGTH, COLUMN_COMMENT 
-- FROM information_schema.COLUMNS 
-- WHERE TABLE_SCHEMA = DATABASE() 
-- AND TABLE_NAME = 'stk_io_bill' 
-- AND COLUMN_NAME = 'return_reason';

-- 如果字段已存在，可以使用下面的语句删除后重新添加（谨慎使用）
-- ALTER TABLE `stk_io_bill` DROP COLUMN `return_reason`;
-- ALTER TABLE `stk_io_bill` ADD COLUMN `return_reason` VARCHAR(500) DEFAULT NULL COMMENT '退货原因' AFTER `pro_person`;

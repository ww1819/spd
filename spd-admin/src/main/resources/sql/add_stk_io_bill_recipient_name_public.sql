-- 出库单领用人姓名（可重复执行）
-- stk_io_bill.recipient_name

SET @col_exists := (
  SELECT COUNT(1)
  FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'stk_io_bill'
    AND COLUMN_NAME = 'recipient_name'
);

SET @sql := IF(
  @col_exists = 0,
  'ALTER TABLE `stk_io_bill` ADD COLUMN `recipient_name` varchar(64) DEFAULT NULL COMMENT ''领用人姓名'' AFTER `dep_purchase_apply_bill_no`',
  'SELECT 1'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

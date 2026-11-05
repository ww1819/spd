-- 制单模板明细增加仓库ID（申购模板 SG 用，申领可为空）
-- 可重复执行

SET @col_exists := (
  SELECT COUNT(1)
  FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'bas_apply_template_entry'
    AND COLUMN_NAME = 'warehouse_id'
);

SET @sql := IF(
  @col_exists = 0,
  'ALTER TABLE `bas_apply_template_entry` ADD COLUMN `warehouse_id` bigint(20) DEFAULT NULL COMMENT ''仓库ID(申购模板用)'' AFTER `material_id`',
  'SELECT 1'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

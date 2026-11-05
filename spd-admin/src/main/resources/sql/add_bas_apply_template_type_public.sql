-- 制单模板类型：SL=科室申领，SG=汇总申购；历史数据默认 SL
-- 可重复执行

SET @col_exists := (
  SELECT COUNT(1)
  FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'bas_apply_template'
    AND COLUMN_NAME = 'template_type'
);

SET @sql := IF(
  @col_exists = 0,
  'ALTER TABLE `bas_apply_template` ADD COLUMN `template_type` varchar(10) DEFAULT ''SL'' COMMENT ''模板类型 SL申领 SG申购'' AFTER `template_name`',
  'SELECT 1'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

UPDATE `bas_apply_template`
SET `template_type` = 'SL'
WHERE `template_type` IS NULL OR `template_type` = '';

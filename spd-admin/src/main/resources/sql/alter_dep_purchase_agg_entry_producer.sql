-- 汇总申购明细：落库生产厂家名称快照（新增有、查看/修改无的问题）
-- 可重复执行

SET @db := DATABASE();

SET @exists := (
  SELECT COUNT(1) FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = @db
    AND TABLE_NAME = 'dep_purchase_apply_agg_entry'
    AND COLUMN_NAME = 'producer'
);
SET @sql := IF(
  @exists = 0,
  'ALTER TABLE dep_purchase_apply_agg_entry ADD COLUMN producer varchar(255) DEFAULT NULL COMMENT ''生产厂家名称（选品快照）'' AFTER model',
  'SELECT ''producer already exists'' AS info'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 回填已有明细：从产品档案关联厂家取名称
UPDATE dep_purchase_apply_agg_entry e
LEFT JOIN fd_material mat ON e.material_id = mat.id
LEFT JOIN fd_factory f ON mat.factory_id = f.factory_id
SET e.producer = COALESCE(NULLIF(TRIM(f.factory_name), ''), NULLIF(TRIM(mat.producer), ''))
WHERE (e.del_flag = 0 OR e.del_flag IS NULL)
  AND (e.producer IS NULL OR TRIM(e.producer) = '')
  AND (
    NULLIF(TRIM(f.factory_name), '') IS NOT NULL
    OR NULLIF(TRIM(mat.producer), '') IS NOT NULL
  );

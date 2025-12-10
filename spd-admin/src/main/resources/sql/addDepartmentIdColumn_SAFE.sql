-- ============================================
-- 安全添加科室ID字段（带检查，可重复执行）
-- ============================================
-- 此脚本可以安全地重复执行，不会报错
-- ============================================

-- 检查字段是否存在，如果不存在则添加
SET @db_name = DATABASE();
SET @table_name = 'gz_order';
SET @column_name = 'department_id';

SET @column_exists = (
    SELECT COUNT(*) 
    FROM information_schema.COLUMNS 
    WHERE TABLE_SCHEMA = @db_name 
    AND TABLE_NAME = @table_name 
    AND COLUMN_NAME = @column_name
);

SET @sql = IF(@column_exists = 0,
    'ALTER TABLE gz_order ADD COLUMN department_id bigint(20) NULL COMMENT ''科室ID'' AFTER warehouse_id',
    'SELECT ''字段 department_id 已存在，无需添加'' AS message'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 验证字段是否添加成功
SELECT 
    COLUMN_NAME, 
    DATA_TYPE, 
    IS_NULLABLE, 
    COLUMN_COMMENT,
    CASE 
        WHEN COLUMN_NAME = 'department_id' THEN '字段已存在'
        ELSE '字段不存在'
    END AS status
FROM information_schema.COLUMNS 
WHERE TABLE_SCHEMA = DATABASE() 
AND TABLE_NAME = 'gz_order' 
AND COLUMN_NAME = 'department_id';


-- ============================================
-- 紧急：为 gz_order 表添加 department_id 字段
-- ============================================
-- 此脚本必须执行，否则系统无法正常工作
-- ============================================

-- 方法1：直接添加（推荐，如果字段不存在）
ALTER TABLE gz_order ADD COLUMN department_id bigint(20) NULL COMMENT '科室ID' AFTER warehouse_id;

-- 如果方法1报错说字段已存在，说明字段已经添加成功，可以忽略错误
-- 或者使用方法2（安全脚本，可重复执行）

-- 方法2：安全添加（带检查，可重复执行）
/*
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
*/

-- 验证字段是否添加成功
SELECT 
    COLUMN_NAME, 
    DATA_TYPE, 
    IS_NULLABLE, 
    COLUMN_COMMENT,
    CASE 
        WHEN COLUMN_NAME = 'department_id' THEN '✓ 字段已存在'
        ELSE '✗ 字段不存在'
    END AS status
FROM information_schema.COLUMNS 
WHERE TABLE_SCHEMA = DATABASE() 
AND TABLE_NAME = 'gz_order' 
AND COLUMN_NAME = 'department_id';

-- ============================================
-- 执行完 SQL 后，需要重启后端服务
-- 然后系统会自动使用新的字段
-- ============================================


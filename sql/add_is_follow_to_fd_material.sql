-- ============================================
-- 为 fd_material 表添加 is_follow 字段（是否跟台）
-- ============================================

-- 方法1：直接添加（如果字段不存在）
ALTER TABLE fd_material ADD COLUMN is_follow varchar(1) NULL COMMENT '是否跟台（1=是，2=否）' AFTER is_gz;

-- 方法2：安全添加（带检查，可重复执行）
-- 如果方法1报错说字段已存在，可以使用以下脚本（推荐）
/*
SET @db_name = DATABASE();
SET @table_name = 'fd_material';
SET @column_name = 'is_follow';

SET @column_exists = (
    SELECT COUNT(*) 
    FROM information_schema.COLUMNS 
    WHERE TABLE_SCHEMA = @db_name 
    AND TABLE_NAME = @table_name 
    AND COLUMN_NAME = @column_name
);

SET @sql = IF(@column_exists = 0,
    'ALTER TABLE fd_material ADD COLUMN is_follow varchar(1) NULL COMMENT ''是否跟台（1=是，2=否）'' AFTER is_gz',
    'SELECT ''字段 is_follow 已存在，无需添加'' AS message'
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
    COLUMN_COMMENT
FROM information_schema.COLUMNS 
WHERE TABLE_SCHEMA = DATABASE() 
AND TABLE_NAME = 'fd_material' 
AND COLUMN_NAME = 'is_follow';

-- 如果上面的查询返回结果，说明字段添加成功


-- ============================================
-- 为 gz_order 表添加 is_follow_flag 字段（是否跟台标识）
-- ============================================

-- 方法1：直接添加（如果字段不存在）
ALTER TABLE gz_order ADD COLUMN is_follow_flag varchar(1) NULL COMMENT '是否跟台标识（1=是，2=否）' AFTER audit_date;

-- 方法2：安全添加（带检查，可重复执行）
-- 如果方法1报错说字段已存在，可以使用以下脚本（推荐）
/*
SET @db_name = DATABASE();
SET @table_name = 'gz_order';
SET @column_name = 'is_follow_flag';

SET @column_exists = (
    SELECT COUNT(*) 
    FROM information_schema.COLUMNS 
    WHERE TABLE_SCHEMA = @db_name 
    AND TABLE_NAME = @table_name 
    AND COLUMN_NAME = @column_name
);

SET @sql = IF(@column_exists = 0,
    'ALTER TABLE gz_order ADD COLUMN is_follow_flag varchar(1) NULL COMMENT ''是否跟台标识（1=是，2=否）'' AFTER audit_date',
    'SELECT ''字段 is_follow_flag 已存在，无需添加'' AS message'
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
AND TABLE_NAME = 'gz_order' 
AND COLUMN_NAME = 'is_follow_flag';

-- 如果上面的查询返回结果，说明字段添加成功


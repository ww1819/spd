-- ============================================
-- 为 fd_warehouse 表添加 settlement_type 字段（结算类型）
-- ============================================
-- 结算类型：1=入库结算，2=出库结算，3=消耗结算
-- ============================================

-- 方法1：直接添加（如果字段不存在）
ALTER TABLE fd_warehouse ADD COLUMN settlement_type varchar(1) NULL COMMENT '结算类型（1=入库结算，2=出库结算，3=消耗结算）' AFTER warehouse_type;

-- 方法2：安全添加（带检查，可重复执行）
-- 如果方法1报错说字段已存在，可以使用以下脚本（推荐）
/*
SET @db_name = DATABASE();
SET @table_name = 'fd_warehouse';
SET @column_name = 'settlement_type';

SET @column_exists = (
    SELECT COUNT(*) 
    FROM information_schema.COLUMNS 
    WHERE TABLE_SCHEMA = @db_name 
    AND TABLE_NAME = @table_name 
    AND COLUMN_NAME = @column_name
);

SET @sql = IF(@column_exists = 0,
    'ALTER TABLE fd_warehouse ADD COLUMN settlement_type varchar(1) NULL COMMENT ''结算类型（1=入库结算，2=出库结算，3=消耗结算）'' AFTER warehouse_type',
    'SELECT ''字段 settlement_type 已存在，无需添加'' AS message'
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
AND TABLE_NAME = 'fd_warehouse' 
AND COLUMN_NAME = 'settlement_type';

-- 如果上面的查询返回结果，说明字段添加成功


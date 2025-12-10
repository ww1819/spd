-- ============================================
-- 为 gz_order 表添加 department_id 字段
-- ============================================
-- 执行此 SQL 后，需要修改 GzOrderMapper.xml 文件
-- 将 NULL as department_id 改为 a.department_id 或 gz.department_id
-- ============================================

-- 方法1：直接添加（如果字段不存在）
ALTER TABLE gz_order ADD COLUMN department_id bigint(20) NULL COMMENT '科室ID' AFTER warehouse_id;

-- 方法2：安全添加（带检查，可重复执行）
-- 如果方法1报错说字段已存在，可以使用以下脚本（推荐）
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
    COLUMN_COMMENT
FROM information_schema.COLUMNS 
WHERE TABLE_SCHEMA = DATABASE() 
AND TABLE_NAME = 'gz_order' 
AND COLUMN_NAME = 'department_id';

-- 如果上面的查询返回结果，说明字段添加成功
-- 然后需要修改以下文件：
-- spd/spd-biz/src/main/resources/mapper/gz/GzOrderMapper.xml
-- 
-- 需要修改的位置：
-- 1. selectGzOrderVo 中：将 NULL as department_id 改为 gz.department_id
-- 2. selectGzOrderVo 中：将 left join fd_department d on NULL = d.id 改为 left join fd_department d on gz.department_id = d.id
-- 3. selectGzOrderById 中：将 NULL as department_id 改为 a.department_id


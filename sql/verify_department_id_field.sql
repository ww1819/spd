-- ============================================
-- 验证 gz_order 表中 department_id 字段是否存在
-- ============================================

-- 方法1：检查字段是否存在
SELECT 
    COLUMN_NAME, 
    DATA_TYPE, 
    IS_NULLABLE, 
    COLUMN_COMMENT,
    COLUMN_DEFAULT,
    CASE 
        WHEN COLUMN_NAME = 'department_id' THEN '✓ 字段已存在'
        ELSE '✗ 字段不存在'
    END AS status
FROM information_schema.COLUMNS 
WHERE TABLE_SCHEMA = DATABASE() 
AND TABLE_NAME = 'gz_order' 
AND COLUMN_NAME = 'department_id';

-- 方法2：查看表结构（查看所有字段）
SHOW COLUMNS FROM gz_order LIKE 'department_id';

-- 方法3：查看完整表结构
DESC gz_order;

-- 如果上面的查询没有返回 department_id 字段，说明字段不存在
-- 需要执行以下 SQL 添加字段：
-- ALTER TABLE gz_order ADD COLUMN department_id bigint(20) NULL COMMENT '科室ID' AFTER warehouse_id;

-- 如果字段已存在但仍然报错，可能是：
-- 1. 执行在了错误的数据库（检查当前数据库：SELECT DATABASE();）
-- 2. MyBatis 缓存问题（需要清理 target 目录并重新编译）
-- 3. 需要重启后端服务


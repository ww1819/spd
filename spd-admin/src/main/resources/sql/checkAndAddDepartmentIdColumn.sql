-- 检查并添加 department_id 字段的 SQL 脚本
-- 如果字段已存在，会报错但可以忽略

-- 方法1：直接添加（推荐）
ALTER TABLE gz_order ADD COLUMN department_id bigint(20) NULL COMMENT '科室ID' AFTER warehouse_id;

-- 方法2：如果方法1报错说字段已存在，可以使用以下方式检查
-- SELECT COUNT(*) FROM information_schema.COLUMNS 
-- WHERE TABLE_SCHEMA = DATABASE() 
-- AND TABLE_NAME = 'gz_order' 
-- AND COLUMN_NAME = 'department_id';

-- 如果返回 0，说明字段不存在，执行方法1
-- 如果返回 1，说明字段已存在，可以忽略错误


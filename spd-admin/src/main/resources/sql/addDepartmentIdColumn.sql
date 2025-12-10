-- 为高值订单表添加科室ID字段
-- 如果字段已存在则不会报错（MySQL 5.7+ 支持 IF NOT EXISTS，但 ALTER TABLE ADD COLUMN 不支持）
-- 如果字段已存在，执行此语句会报错，可以忽略

-- 方法1：直接添加（如果字段不存在）
ALTER TABLE gz_order ADD COLUMN department_id bigint(20) NULL COMMENT '科室ID' AFTER warehouse_id;

-- 方法2：如果方法1报错说字段已存在，可以使用以下语句检查并添加（需要手动判断）
-- SELECT COUNT(*) FROM information_schema.COLUMNS 
-- WHERE TABLE_SCHEMA = DATABASE() 
-- AND TABLE_NAME = 'gz_order' 
-- AND COLUMN_NAME = 'department_id';
-- 如果返回0，则执行上面的 ALTER TABLE 语句


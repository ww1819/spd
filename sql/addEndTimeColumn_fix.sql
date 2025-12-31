-- 为高值备货库存表添加有效期字段
-- 请根据实际数据库名称修改表名前缀（如果有的话）
ALTER TABLE gz_depot_inventory ADD COLUMN end_time date NULL COMMENT '有效期';


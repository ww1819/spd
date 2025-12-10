-- 为仓库表添加仓库类型字段
-- 注意：根据实际数据库名称修改，如果数据库名称不是 aspt，请相应修改
ALTER TABLE fd_warehouse ADD warehouse_type varchar(50) NULL DEFAULT '低值' COMMENT '仓库类型（高值、低值、试剂）';


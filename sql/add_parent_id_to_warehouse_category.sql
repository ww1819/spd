-- 为库房分类表添加父级ID字段，支持多级目录
ALTER TABLE fd_warehouse_category 
ADD COLUMN parent_id bigint(20) DEFAULT 0 COMMENT '父分类ID，0表示顶级分类' AFTER warehouse_category_id;

-- 添加索引
CREATE INDEX idx_parent_id ON fd_warehouse_category (parent_id);

-- 更新现有数据，将parent_id设置为0（顶级分类）
UPDATE fd_warehouse_category SET parent_id = 0 WHERE parent_id IS NULL;


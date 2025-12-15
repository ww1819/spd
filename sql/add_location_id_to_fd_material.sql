-- 为耗材产品表添加货位ID字段
ALTER TABLE fd_material 
ADD COLUMN location_id bigint(20) NULL COMMENT '货位ID' AFTER is_follow;

-- 添加索引
CREATE INDEX idx_location_id ON fd_material (location_id);


-- 为高值备货库存表添加有效期字段
ALTER TABLE spd.gz_depot_inventory ADD end_time date NULL COMMENT '有效期';


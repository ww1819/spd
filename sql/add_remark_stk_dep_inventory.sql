-- 科室库存表增加备注字段（若表已有 remark 可跳过）
ALTER TABLE stk_dep_inventory
ADD COLUMN remark VARCHAR(500) NULL COMMENT '备注';

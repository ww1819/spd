-- 科室库存表增加收货确认状态：0=未确认 1=已确认；出库单审核即插入科室库存(未确认)，收货确认后更新为已确认
ALTER TABLE stk_dep_inventory
ADD COLUMN receipt_confirm_status TINYINT DEFAULT 0 COMMENT '收货确认状态 0未确认 1已确认';

-- 已有数据视为已确认，保证历史科室退库等逻辑不受影响（执行一次即可）
UPDATE stk_dep_inventory SET receipt_confirm_status = 1;

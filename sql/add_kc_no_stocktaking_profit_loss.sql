-- 盘点单明细、盈亏单明细增加 kc_no（库存明细 id），用于盈亏审核时按库存主键精确查库存

-- 盘点单明细表：盘点时从库存选择，存 stk_inventory.id
ALTER TABLE stk_io_stocktaking_entry ADD COLUMN kc_no BIGINT NULL COMMENT '库存明细id（盘点时取自stk_inventory.id）' AFTER batch_no;

-- 盈亏单明细表：来自盘点明细，用于审核时按 kc_no 查库存
ALTER TABLE stk_io_profit_loss_entry ADD COLUMN kc_no BIGINT NULL COMMENT '库存明细id（来自盘点明细）' AFTER stocktaking_entry_id;

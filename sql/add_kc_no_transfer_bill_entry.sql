-- 调拨单明细表 stk_io_bill_entry 增加 kc_no 字段
-- 做单时：从转出仓库选库存后，把库存id(stk_inventory.id) 存到明细 kc_no
-- 审核时：优先按 kc_no 查库存再扣减转出、插流水 ZC/ZR
-- 若已执行过 add_kc_no_for_out_audit.sql（已为 stk_io_bill_entry 加过 kc_no），则无需再执行本句

ALTER TABLE stk_io_bill_entry ADD COLUMN kc_no BIGINT NULL COMMENT '库存明细id（出库反写科室库存id；调拨单存转出仓库库存id，审核按此查库存）';

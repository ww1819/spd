-- 出入库明细表 stk_io_bill_entry 增加 warehouse_id（与 StkIoBillMapper.batchStkIoBillEntry 一致）
-- 若列已存在会报错，可忽略或改用 spd-admin material/column.sql 中的 add_table_column

ALTER TABLE stk_io_bill_entry
  ADD COLUMN warehouse_id BIGINT NULL COMMENT '明细仓库ID（冗余，与主表或业务一致）' AFTER kc_no;

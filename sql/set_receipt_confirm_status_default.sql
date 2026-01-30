-- 出库单收货确认标识默认 0（0=未确认，1=已确认）
-- 表 stk_io_bill.receipt_confirm_status：先将现有 NULL 更新为 0，再设置默认值 0

-- aspt 库
UPDATE aspt.stk_io_bill SET receipt_confirm_status = 0 WHERE receipt_confirm_status IS NULL;
ALTER TABLE aspt.stk_io_bill MODIFY COLUMN receipt_confirm_status int DEFAULT 0 COMMENT '收货确认状态（0=未确认，1=已确认）';

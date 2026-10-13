-- 采购调价单：调价原因（与备注同为 varchar(500)）
ALTER TABLE `purchase_price_adjust`
  ADD COLUMN `adjust_reason` varchar(500) DEFAULT NULL COMMENT '调价原因' AFTER `print_date`;

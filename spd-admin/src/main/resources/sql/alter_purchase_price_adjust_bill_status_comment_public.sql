-- 调价单状态含义：0未提交 1未审核（提交后） 2已审核
ALTER TABLE purchase_price_adjust
  MODIFY COLUMN `bill_status` char(1) DEFAULT '0' COMMENT '单据状态 0未提交 1未审核 2已审核';

-- 为采购订单表添加关联采购计划单号字段
ALTER TABLE purchase_order 
ADD COLUMN plan_no varchar(50) DEFAULT NULL COMMENT '关联采购计划单号' 
AFTER order_no;

-- 添加索引
ALTER TABLE purchase_order 
ADD INDEX idx_plan_no (plan_no);


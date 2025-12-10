-- 为采购计划表添加审核意见字段
ALTER TABLE purchase_plan 
ADD COLUMN audit_opinion varchar(500) DEFAULT NULL COMMENT '审核意见' 
AFTER audit_date;


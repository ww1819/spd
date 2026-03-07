-- 科室库存表增加单据关联字段，便于收货确认时精确定位对应出库单及出库明细，避免改错记录
ALTER TABLE stk_dep_inventory
ADD COLUMN bill_id BIGINT NULL COMMENT '单据主表id(出库单id)',
ADD COLUMN bill_entry_id BIGINT NULL COMMENT '单据明细id(出库单明细id)',
ADD COLUMN bill_no VARCHAR(64) NULL COMMENT '单据号',
ADD COLUMN bill_type INT NULL COMMENT '单据类型 201出库';

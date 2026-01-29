-- 出库审核：每条出库明细生成科室库存并反写 kc_no
-- kc_no 存科室库存明细主键 id，用于出库单明细、仓库流水、科室流水三处关联

-- 出库单明细表
ALTER TABLE stk_io_bill_entry ADD COLUMN kc_no BIGINT NULL COMMENT '科室库存明细id（反写）';

-- 仓库库存明细
ALTER TABLE stk_inventory ADD COLUMN kc_no BIGINT NULL COMMENT '科室库存明细id（反写）';

-- 科室库存明细
ALTER TABLE stk_dep_inventory ADD COLUMN kc_no BIGINT NULL COMMENT '科室库存明细id（反写）';

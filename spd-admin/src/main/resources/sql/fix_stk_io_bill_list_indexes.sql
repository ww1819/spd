-- ============================================================
-- 修复到货验收/出入库列表超时：补齐 stk_io_bill / entry 缺失索引
--
-- 现象：本地/小数据正常，正式环境「系统接口请求超时」(前端 10s)
-- 根因：线上库仅有 PRIMARY，列表条件（tenant/bill_type/date/供应商）全表扫描，
--       再叠加「被引用状态」相关子查询与 PageHelper count，易超过 10 秒。
--
-- DBeaver：每条单独 Ctrl+Enter；或整段 Alt+X
-- ============================================================

-- 核查（执行前）
SHOW INDEX FROM stk_io_bill;
SHOW INDEX FROM stk_io_bill_entry;

-- 主表：列表常用过滤
CREATE INDEX idx_stk_io_bill_tenant ON stk_io_bill (tenant_id);
CREATE INDEX idx_stk_io_bill_type ON stk_io_bill (bill_type);
CREATE INDEX idx_stk_io_bill_wh ON stk_io_bill (warehouse_id);
CREATE INDEX idx_stk_io_bill_suppler ON stk_io_bill (suppler_id);
CREATE INDEX idx_stk_io_bill_no ON stk_io_bill (bill_no);
CREATE INDEX idx_stk_io_bill_list ON stk_io_bill (tenant_id, bill_type, del_flag, bill_date);

-- 明细：EXISTS / 被引用状态按主表关联
CREATE INDEX idx_stk_io_entry_paren ON stk_io_bill_entry (paren_id);
CREATE INDEX idx_stk_io_entry_material ON stk_io_bill_entry (material_id);

-- 核查（执行后）
SHOW INDEX FROM stk_io_bill;
SHOW INDEX FROM stk_io_bill_entry;

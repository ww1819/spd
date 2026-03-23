-- ============================================================
-- 耗材侧 tenant_id 回填（在已执行 material/column.sql 补列之后运行）
-- 执行前请备份数据库；按「/」分段执行（与项目其它 mysql 脚本一致）
-- ============================================================

-- 1) 期初导入主表：从仓库推导租户
/
UPDATE stk_initial_import i
INNER JOIN fd_warehouse w ON w.id = i.warehouse_id AND w.tenant_id IS NOT NULL AND w.tenant_id != ''
SET i.tenant_id = w.tenant_id
WHERE (i.tenant_id IS NULL OR i.tenant_id = '');
/

-- 2) 期初导入明细：从主表推导
/
UPDATE stk_initial_import_entry e
INNER JOIN stk_initial_import i ON i.id = e.paren_id AND i.tenant_id IS NOT NULL AND i.tenant_id != ''
SET e.tenant_id = i.tenant_id
WHERE (e.tenant_id IS NULL OR e.tenant_id = '');
/

-- 3) 申领模板主表：从仓库推导
/
UPDATE bas_apply_template t
INNER JOIN fd_warehouse w ON w.id = t.warehouse_id AND w.tenant_id IS NOT NULL AND w.tenant_id != ''
SET t.tenant_id = w.tenant_id
WHERE t.warehouse_id IS NOT NULL
  AND (t.tenant_id IS NULL OR t.tenant_id = '');
/

-- 4) 申领模板明细：从主表推导
/
UPDATE bas_apply_template_entry e
INNER JOIN bas_apply_template t ON t.id = e.paren_id AND t.tenant_id IS NOT NULL AND t.tenant_id != ''
SET e.tenant_id = t.tenant_id
WHERE (e.tenant_id IS NULL OR e.tenant_id = '');
/

-- 5) 批次表：从关联仓库或盈亏/出入库单据（尽力回填，需结合现网数据核对）
/
UPDATE stk_batch b
INNER JOIN fd_warehouse w ON w.id = b.warehouse_id AND w.tenant_id IS NOT NULL AND w.tenant_id != ''
SET b.tenant_id = w.tenant_id
WHERE b.warehouse_id IS NOT NULL
  AND (b.tenant_id IS NULL OR b.tenant_id = '');
/

-- 6) 可选：变更日志类（若已加 tenant_id 列）— 供应商变更日志示例
-- UPDATE fd_supplier_change_log lg
-- INNER JOIN fd_supplier s ON s.id = lg.supplier_id AND s.tenant_id IS NOT NULL
-- SET lg.tenant_id = s.tenant_id
-- WHERE lg.tenant_id IS NULL;

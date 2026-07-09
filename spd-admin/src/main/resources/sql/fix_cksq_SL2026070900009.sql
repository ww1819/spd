-- ============================================================
-- 修复科室申领 SL2026070900009 的 CKSQ 明细不全
--
-- 根因：10:39 审核时仅 6 行明细，CKSQ 只拆了 3 条；10:57 保存后明细变为 11 行，
--       但 CKSQ 不会自动补拆，且旧明细 id(10881~10883) 已软删。
--
-- 本脚本：
--   0) 将 bas_apply.apply_bill_status 改回 2（已审核；10:57 保存后曾被改回 1）
--   1) 将已有 3 条 CKSQ 明细关联到当前有效 bas_apply_entry，并按当前库存校正数量
--   2) 为其余 8 条申领明细补插 CKSQ 明细（FIFO 可分配量，与审核拆单逻辑一致）
--   3) 回写两张 CKSQ 主单合计
--
-- 执行前请确认 wh_wh_apply_ck_entry_ref 无本单关联（应为 0 行）。
-- 建议整段 START TRANSACTION ~ COMMIT 一次执行；或逐步 Ctrl+Enter 并核对每步结果。
-- 租户：hengsui-third-001
-- ============================================================

-- ---------- 修复前核查 ----------

-- 申领主单（正常应为 status=2；若 status=1 但 audit_date 有值，说明 10:57 保存后状态被改回，本脚本会一并修复）
SELECT id, apply_bill_no, apply_bill_status, audit_date, update_time
FROM bas_apply
WHERE apply_bill_no = 'SL2026070900009';

-- 当前有效申领明细（应 11 行，id 10950~10960）
SELECT bae.id, m.name, bae.qty, bae.stock_warehouse_id, w.name AS wh_name
FROM bas_apply_entry bae
JOIN bas_apply ba ON ba.id = bae.paren_id
JOIN fd_material m ON m.id = bae.material_id
LEFT JOIN fd_warehouse w ON w.id = bae.stock_warehouse_id
WHERE ba.apply_bill_no = 'SL2026070900009'
  AND (bae.del_flag = 0 OR bae.del_flag IS NULL)
ORDER BY bae.stock_warehouse_id, bae.id;

-- 已有 CKSQ（修复前可能仅 3 条且 bas_apply_entry_id=10881~10883；修复后应为 11 条、10950~10960）
SELECT wa.apply_bill_no, wa.warehouse_id, wa.total_qty, wa.total_amt
FROM wh_warehouse_apply wa
WHERE wa.bas_apply_bill_no = 'SL2026070900009'
  AND (wa.del_flag = 0 OR wa.del_flag IS NULL);

SELECT wa.apply_bill_no, wae.id, wae.bas_apply_entry_id, m.name, wae.qty, wae.amt
FROM wh_warehouse_apply_entry wae
JOIN wh_warehouse_apply wa ON wa.id = wae.paren_id
JOIN fd_material m ON m.id = wae.material_id
WHERE wa.bas_apply_bill_no = 'SL2026070900009'
  AND (wae.del_flag = 0 OR wae.del_flag IS NULL)
ORDER BY wa.apply_bill_no, wae.line_no;

-- 出库关联（应为 0 行，有数据则勿执行修复）
SELECT COUNT(*) AS ref_cnt
FROM wh_wh_apply_ck_entry_ref
WHERE wh_apply_bill_no IN ('CKSQ2026070900011', 'CKSQ2026070900012')
  AND (del_flag = 0 OR del_flag IS NULL);

-- ---------- 修复（建议整段执行） ----------

START TRANSACTION;

-- 0) 主单状态：已审核且有 CKSQ，但 10:57 保存后 apply_bill_status 被改回 1
UPDATE bas_apply
SET apply_bill_status = 2,
    update_by         = 'fix_script',
    update_time       = NOW()
WHERE apply_bill_no = 'SL2026070900009'
  AND apply_bill_status = 1
  AND audit_date IS NOT NULL;

-- 1) 进项仓 CKSQ2026070900011：捆扎带 100 -> 1000，关联新明细 10950
UPDATE wh_warehouse_apply_entry wae
JOIN wh_warehouse_apply wa ON wa.id = wae.paren_id
SET wae.bas_apply_entry_id = '10950',
    wae.qty              = 1000.00,
    wae.unit_price       = 0.12,
    wae.price            = 0.12,
    wae.amt              = 120.00,
    wae.factory_id       = 4518,
    wae.update_by        = 'fix_script',
    wae.update_time      = NOW(),
    wae.remark           = 'fix: 审核后补录明细，校正 CKSQ 数量与 bas_apply_entry 关联'
WHERE wa.apply_bill_no = 'CKSQ2026070900011'
  AND wae.id = '1f17b3f6-89d9-6a97-864e-5d4e4b969a2e'
  AND (wae.del_flag = 0 OR wae.del_flag IS NULL);

-- 2) SPD库 CKSQ2026070900012：修正已有 2 条
UPDATE wh_warehouse_apply_entry wae
JOIN wh_warehouse_apply wa ON wa.id = wae.paren_id
SET wae.bas_apply_entry_id = '10951',
    wae.qty              = 50.00,
    wae.unit_price       = 6.80,
    wae.price            = 6.80,
    wae.amt              = 340.00,
    wae.supplier_id      = 1345,
    wae.factory_id       = 4342,
    wae.update_by        = 'fix_script',
    wae.update_time      = NOW(),
    wae.remark           = 'fix: 审核后补录明细，校正 bas_apply_entry 关联'
WHERE wa.apply_bill_no = 'CKSQ2026070900012'
  AND wae.id = '1f17b3f6-8a00-6b99-864e-5d4e4b969a2e'
  AND (wae.del_flag = 0 OR wae.del_flag IS NULL);

UPDATE wh_warehouse_apply_entry wae
JOIN wh_warehouse_apply wa ON wa.id = wae.paren_id
SET wae.bas_apply_entry_id = '10952',
    wae.qty              = 1000.00,
    wae.unit_price       = 0.60,
    wae.price            = 0.60,
    wae.amt              = 600.00,
    wae.supplier_id      = 1306,
    wae.factory_id       = 5056,
    wae.update_by        = 'fix_script',
    wae.update_time      = NOW(),
    wae.remark           = 'fix: 审核后补录明细，痰杯由 1 校正为可分配 1000'
WHERE wa.apply_bill_no = 'CKSQ2026070900012'
  AND wae.id = '1f17b3f6-8a27-6c9a-864e-5d4e4b969a2e'
  AND (wae.del_flag = 0 OR wae.del_flag IS NULL);

-- 3) SPD库：补插 8 条缺失明细（按当前 FIFO 可分配量；可重复执行）
INSERT INTO wh_warehouse_apply_entry (
    id, paren_id, tenant_id, bas_apply_id, bas_apply_bill_no, bas_apply_entry_id,
    line_no, material_id, warehouse_id, stk_inventory_id,
    unit_price, qty, price, amt, supplier_id, factory_id,
    line_void_status, line_void_qty, del_flag, create_by, create_time, remark
)
SELECT 'f7fc8e83-7711-4660-bf3e-3032cf8ad596', '1f17b3f6-8a00-6b98-864e-5d4e4b969a2e',
       'hengsui-third-001', '553', 'SL2026070900009', '10953',
       3, 14175, 7, NULL, 25.00, 60.00, 25.00, 1500.00, 1306, 5059,
       0, 0, 0, 'fix_script', NOW(), 'fix: 审核后补拆 CKSQ'
WHERE NOT EXISTS (
    SELECT 1 FROM wh_warehouse_apply_entry e
    WHERE e.bas_apply_entry_id = '10953' AND e.paren_id = '1f17b3f6-8a00-6b98-864e-5d4e4b969a2e'
      AND (e.del_flag = 0 OR e.del_flag IS NULL)
);

INSERT INTO wh_warehouse_apply_entry (
    id, paren_id, tenant_id, bas_apply_id, bas_apply_bill_no, bas_apply_entry_id,
    line_no, material_id, warehouse_id, stk_inventory_id,
    unit_price, qty, price, amt, supplier_id, factory_id,
    line_void_status, line_void_qty, del_flag, create_by, create_time, remark
)
SELECT 'b3c76817-7859-43a1-aa07-ef1f32a9afd8', '1f17b3f6-8a00-6b98-864e-5d4e4b969a2e',
       'hengsui-third-001', '553', 'SL2026070900009', '10954',
       4, 14176, 7, NULL, 20.00, 100.00, 20.00, 2000.00, 1306, 5059,
       0, 0, 0, 'fix_script', NOW(), 'fix: 审核后补拆 CKSQ'
WHERE NOT EXISTS (
    SELECT 1 FROM wh_warehouse_apply_entry e
    WHERE e.bas_apply_entry_id = '10954' AND e.paren_id = '1f17b3f6-8a00-6b98-864e-5d4e4b969a2e'
      AND (e.del_flag = 0 OR e.del_flag IS NULL)
);

INSERT INTO wh_warehouse_apply_entry (
    id, paren_id, tenant_id, bas_apply_id, bas_apply_bill_no, bas_apply_entry_id,
    line_no, material_id, warehouse_id, stk_inventory_id,
    unit_price, qty, price, amt, supplier_id, factory_id,
    line_void_status, line_void_qty, del_flag, create_by, create_time, remark
)
SELECT '341c4d1b-3655-4c7c-89fd-a5a1976b7ee4', '1f17b3f6-8a00-6b98-864e-5d4e4b969a2e',
       'hengsui-third-001', '553', 'SL2026070900009', '10955',
       5, 14456, 7, NULL, 0.40, 600.00, 0.40, 240.00, 1589, 4768,
       0, 0, 0, 'fix_script', NOW(), 'fix: 审核后补拆 CKSQ'
WHERE NOT EXISTS (
    SELECT 1 FROM wh_warehouse_apply_entry e
    WHERE e.bas_apply_entry_id = '10955' AND e.paren_id = '1f17b3f6-8a00-6b98-864e-5d4e4b969a2e'
      AND (e.del_flag = 0 OR e.del_flag IS NULL)
);

INSERT INTO wh_warehouse_apply_entry (
    id, paren_id, tenant_id, bas_apply_id, bas_apply_bill_no, bas_apply_entry_id,
    line_no, material_id, warehouse_id, stk_inventory_id,
    unit_price, qty, price, amt, supplier_id, factory_id,
    line_void_status, line_void_qty, del_flag, create_by, create_time, remark
)
SELECT 'd8727d2c-af33-428d-a34f-5b8961e9c530', '1f17b3f6-8a00-6b98-864e-5d4e4b969a2e',
       'hengsui-third-001', '553', 'SL2026070900009', '10956',
       6, 14229, 7, NULL, 0.01, 75000.00, 0.01, 750.00, 1345, 4239,
       0, 0, 0, 'fix_script', NOW(), 'fix: 审核后补拆 CKSQ'
WHERE NOT EXISTS (
    SELECT 1 FROM wh_warehouse_apply_entry e
    WHERE e.bas_apply_entry_id = '10956' AND e.paren_id = '1f17b3f6-8a00-6b98-864e-5d4e4b969a2e'
      AND (e.del_flag = 0 OR e.del_flag IS NULL)
);

INSERT INTO wh_warehouse_apply_entry (
    id, paren_id, tenant_id, bas_apply_id, bas_apply_bill_no, bas_apply_entry_id,
    line_no, material_id, warehouse_id, stk_inventory_id,
    unit_price, qty, price, amt, supplier_id, factory_id,
    line_void_status, line_void_qty, del_flag, create_by, create_time, remark
)
SELECT '838b1647-84ae-4137-9bfa-30d7c4019d2e', '1f17b3f6-8a00-6b98-864e-5d4e4b969a2e',
       'hengsui-third-001', '553', 'SL2026070900009', '10957',
       7, 15061, 7, NULL, 5.50, 100.00, 5.50, 550.00, 1589, 4408,
       0, 0, 0, 'fix_script', NOW(), 'fix: 审核后补拆 CKSQ'
WHERE NOT EXISTS (
    SELECT 1 FROM wh_warehouse_apply_entry e
    WHERE e.bas_apply_entry_id = '10957' AND e.paren_id = '1f17b3f6-8a00-6b98-864e-5d4e4b969a2e'
      AND (e.del_flag = 0 OR e.del_flag IS NULL)
);

INSERT INTO wh_warehouse_apply_entry (
    id, paren_id, tenant_id, bas_apply_id, bas_apply_bill_no, bas_apply_entry_id,
    line_no, material_id, warehouse_id, stk_inventory_id,
    unit_price, qty, price, amt, supplier_id, factory_id,
    line_void_status, line_void_qty, del_flag, create_by, create_time, remark
)
SELECT 'baeae6b9-622a-4f44-8d7b-9ee9f425e36d', '1f17b3f6-8a00-6b98-864e-5d4e4b969a2e',
       'hengsui-third-001', '553', 'SL2026070900009', '10958',
       8, 15096, 7, NULL, 10.00, 5.00, 10.00, 50.00, 1345, 4369,
       0, 0, 0, 'fix_script', NOW(), 'fix: 审核后补拆 CKSQ'
WHERE NOT EXISTS (
    SELECT 1 FROM wh_warehouse_apply_entry e
    WHERE e.bas_apply_entry_id = '10958' AND e.paren_id = '1f17b3f6-8a00-6b98-864e-5d4e4b969a2e'
      AND (e.del_flag = 0 OR e.del_flag IS NULL)
);

INSERT INTO wh_warehouse_apply_entry (
    id, paren_id, tenant_id, bas_apply_id, bas_apply_bill_no, bas_apply_entry_id,
    line_no, material_id, warehouse_id, stk_inventory_id,
    unit_price, qty, price, amt, supplier_id, factory_id,
    line_void_status, line_void_qty, del_flag, create_by, create_time, remark
)
SELECT 'acc6d28a-b5ef-4f40-a4d8-ff1ec0b0b695', '1f17b3f6-8a00-6b98-864e-5d4e4b969a2e',
       'hengsui-third-001', '553', 'SL2026070900009', '10959',
       9, 11944, 7, NULL, 0.58, 1000.00, 0.58, 580.00, 1345, 4342,
       0, 0, 0, 'fix_script', NOW(), 'fix: 审核后补拆 CKSQ'
WHERE NOT EXISTS (
    SELECT 1 FROM wh_warehouse_apply_entry e
    WHERE e.bas_apply_entry_id = '10959' AND e.paren_id = '1f17b3f6-8a00-6b98-864e-5d4e4b969a2e'
      AND (e.del_flag = 0 OR e.del_flag IS NULL)
);

INSERT INTO wh_warehouse_apply_entry (
    id, paren_id, tenant_id, bas_apply_id, bas_apply_bill_no, bas_apply_entry_id,
    line_no, material_id, warehouse_id, stk_inventory_id,
    unit_price, qty, price, amt, supplier_id, factory_id,
    line_void_status, line_void_qty, del_flag, create_by, create_time, remark
)
SELECT '10e30f39-02f0-4830-a32f-18de853d0a66', '1f17b3f6-8a00-6b98-864e-5d4e4b969a2e',
       'hengsui-third-001', '553', 'SL2026070900009', '10960',
       10, 14712, 7, NULL, 7.00, 100.00, 7.00, 700.00, 1589, 4496,
       0, 0, 0, 'fix_script', NOW(), 'fix: 审核后补拆 CKSQ'
WHERE NOT EXISTS (
    SELECT 1 FROM wh_warehouse_apply_entry e
    WHERE e.bas_apply_entry_id = '10960' AND e.paren_id = '1f17b3f6-8a00-6b98-864e-5d4e4b969a2e'
      AND (e.del_flag = 0 OR e.del_flag IS NULL)
);

-- 4) 回写主单合计
UPDATE wh_warehouse_apply wa
SET wa.total_qty = (
        SELECT COALESCE(SUM(wae.qty), 0)
        FROM wh_warehouse_apply_entry wae
        WHERE wae.paren_id = wa.id AND (wae.del_flag = 0 OR wae.del_flag IS NULL)
    ),
    wa.total_amt = (
        SELECT COALESCE(SUM(wae.amt), 0)
        FROM wh_warehouse_apply_entry wae
        WHERE wae.paren_id = wa.id AND (wae.del_flag = 0 OR wae.del_flag IS NULL)
    ),
    wa.update_by = 'fix_script',
    wa.update_time = NOW()
WHERE wa.bas_apply_bill_no = 'SL2026070900009'
  AND (wa.del_flag = 0 OR wa.del_flag IS NULL);

COMMIT;

-- ---------- 修复后验证 ----------

-- 申领主单（应 apply_bill_status=2）
SELECT id, apply_bill_no, apply_bill_status, audit_date, update_time
FROM bas_apply
WHERE apply_bill_no = 'SL2026070900009';

-- CKSQ 主单合计：CKSQ0011 qty=1000 amt=120；CKSQ0012 qty=78015 amt=7310
SELECT wa.apply_bill_no, wa.warehouse_id, w.name AS wh_name, wa.total_qty, wa.total_amt
FROM wh_warehouse_apply wa
LEFT JOIN fd_warehouse w ON w.id = wa.warehouse_id
WHERE wa.bas_apply_bill_no = 'SL2026070900009'
  AND (wa.del_flag = 0 OR wa.del_flag IS NULL);

-- 明细应 11 行，bas_apply_entry_id 均为 10950~10960
SELECT wa.apply_bill_no, wae.line_no, wae.bas_apply_entry_id, m.name, wae.qty, wae.amt
FROM wh_warehouse_apply_entry wae
JOIN wh_warehouse_apply wa ON wa.id = wae.paren_id
JOIN fd_material m ON m.id = wae.material_id
WHERE wa.bas_apply_bill_no = 'SL2026070900009'
  AND (wae.del_flag = 0 OR wae.del_flag IS NULL)
ORDER BY wa.apply_bill_no, wae.line_no;

-- 申领明细与 CKSQ 逐行对照（每行 bas_apply_entry 应有对应 CKSQ）
SELECT bae.id AS bas_entry_id, m.name,
       bae.qty AS apply_qty, wae.qty AS cksq_qty, wa.apply_bill_no
FROM bas_apply_entry bae
JOIN bas_apply ba ON ba.id = bae.paren_id
JOIN fd_material m ON m.id = bae.material_id
LEFT JOIN wh_warehouse_apply_entry wae
       ON wae.bas_apply_entry_id = CAST(bae.id AS CHAR)
      AND (wae.del_flag = 0 OR wae.del_flag IS NULL)
LEFT JOIN wh_warehouse_apply wa ON wa.id = wae.paren_id
WHERE ba.apply_bill_no = 'SL2026070900009'
  AND (bae.del_flag = 0 OR bae.del_flag IS NULL)
ORDER BY bae.stock_warehouse_id, bae.id;

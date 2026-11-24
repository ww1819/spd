-- =============================================================================
-- 修复草案（未执行）：GZRK2026103000003 / W0010124_3 重复审核多生成的第二批 84
-- 租户：hengsui-third-001（衡水三院）
-- 原则：只作废「第二批」院内码及对应备货库存；不动已出库（第一批含 30 条出库）
-- 预期：备货库存 138 → 54（= 真实验收 84 − 已出库 30）
-- =============================================================================
-- 背景摘要：
--   明细验收 qty=84，但 audit 成功两次 → 院内码/库存各生成 168
--   第一批 id 653~736 / G2610301733000656~0739：已出库 30，库存剩 54
--   第二批 id 737~820 / G2610301733000740~0823：未出库、科室库存无引用，可整批软删
-- =============================================================================

-- 建议在事务中执行；先跑「预览」段，确认 4 项门禁全部通过后再执行 UPDATE。

START TRANSACTION;

-- ---------------------------------------------------------------------------
-- 0) 第二批院内码集合（按 IC 表 id 排序后的后 84 条）
-- ---------------------------------------------------------------------------
DROP TEMPORARY TABLE IF EXISTS tmp_gzrk_batch2_codes;
CREATE TEMPORARY TABLE tmp_gzrk_batch2_codes AS
SELECT id AS ic_id, in_hospital_code
FROM (
    SELECT id, in_hospital_code
    FROM gz_order_entry_inhospitalcode_list
    WHERE parent_id = 196
      AND detail_id = 353
      AND IFNULL(del_flag, 0) = 0
    ORDER BY id
    LIMIT 84 OFFSET 84
) t;

-- =============================================================================
-- A. 预览 / 门禁（必须人工核对输出）
-- =============================================================================

-- A1. 第二批范围（期望：cnt=84, min_c=G2610301733000740, max_c=G2610301733000823）
SELECT COUNT(*) AS batch2_cnt,
       MIN(ic_id) AS min_ic_id,
       MAX(ic_id) AS max_ic_id,
       MIN(in_hospital_code) AS min_code,
       MAX(in_hospital_code) AS max_code
FROM tmp_gzrk_batch2_codes;

-- A2. 第二批不得出现在任何未删除出库明细（期望：ship_cnt=0）
SELECT COUNT(*) AS ship_cnt
FROM gz_shipment_entry se
INNER JOIN gz_shipment s ON s.id = se.paren_id
INNER JOIN tmp_gzrk_batch2_codes b ON b.in_hospital_code = se.in_hospital_code
WHERE IFNULL(se.del_flag, 0) = 0
  AND IFNULL(s.del_flag, 0) = 0
  AND s.tenant_id = 'hengsui-third-001';

-- A3. 第二批不得落在科室库存（期望：dep_cnt=0）
SELECT COUNT(*) AS dep_cnt
FROM gz_dep_inventory d
INNER JOIN tmp_gzrk_batch2_codes b ON b.in_hospital_code = d.in_hospital_code
WHERE d.tenant_id = 'hengsui-third-001'
  AND IFNULL(d.del_flag, 0) = 0;

-- A4. 第二批备货库存应为 84 行且全部 qty=1（期望：rows=84, qty_sum=84, qty0=0）
SELECT COUNT(*) AS inv_rows,
       SUM(IFNULL(di.qty, 0)) AS qty_sum,
       SUM(CASE WHEN IFNULL(di.qty, 0) = 0 THEN 1 ELSE 0 END) AS qty0_rows
FROM gz_depot_inventory di
INNER JOIN tmp_gzrk_batch2_codes b ON b.in_hospital_code = di.in_hospital_code
WHERE di.tenant_id = 'hengsui-third-001'
  AND di.order_no = 'GZRK2026103000003'
  AND di.order_entry_id = 353
  AND di.material_id = 14528
  AND IFNULL(di.del_flag, 0) = 0;

-- A5. 修复前整品库存（期望：138）
SELECT SUM(IFNULL(qty, 0)) AS stock_qty_before
FROM gz_depot_inventory
WHERE tenant_id = 'hengsui-third-001'
  AND material_id = 14528
  AND IFNULL(del_flag, 0) = 0;

-- ※ 若 A2/A3 非 0，或 A4 与预期不符：ROLLBACK; 停止，勿继续。

-- =============================================================================
-- B. 执行软删（仅第二批）
-- =============================================================================

-- B1. 作废第二批备货库存
UPDATE gz_depot_inventory di
INNER JOIN tmp_gzrk_batch2_codes b ON b.in_hospital_code = di.in_hospital_code
SET di.del_flag = 1,
    di.delete_by = 'fix-double-audit-GZRK2026103000003',
    di.delete_time = NOW(),
    di.update_time = NOW(),
    di.update_by = 'fix-double-audit-GZRK2026103000003'
WHERE di.tenant_id = 'hengsui-third-001'
  AND di.order_no = 'GZRK2026103000003'
  AND di.order_entry_id = 353
  AND di.material_id = 14528
  AND IFNULL(di.del_flag, 0) = 0
  AND IFNULL(di.qty, 0) > 0
  -- 再挡一层：排除任何已出库院内码（理论上第二批为 0）
  AND NOT EXISTS (
        SELECT 1
        FROM gz_shipment_entry se
        INNER JOIN gz_shipment s ON s.id = se.paren_id
        WHERE se.in_hospital_code = di.in_hospital_code
          AND IFNULL(se.del_flag, 0) = 0
          AND IFNULL(s.del_flag, 0) = 0
          AND s.tenant_id = 'hengsui-third-001'
  );
-- 期望 affected rows = 84

-- B2. 作废第二批院内码明细
UPDATE gz_order_entry_inhospitalcode_list ic
INNER JOIN tmp_gzrk_batch2_codes b ON b.ic_id = ic.id
SET ic.del_flag = 1,
    ic.delete_by = 'fix-double-audit-GZRK2026103000003',
    ic.delete_time = NOW(),
    ic.update_time = NOW(),
    ic.update_by = 'fix-double-audit-GZRK2026103000003'
WHERE ic.parent_id = 196
  AND ic.detail_id = 353
  AND IFNULL(ic.del_flag, 0) = 0
  AND NOT EXISTS (
        SELECT 1
        FROM gz_shipment_entry se
        INNER JOIN gz_shipment s ON s.id = se.paren_id
        WHERE se.in_hospital_code = ic.in_hospital_code
          AND IFNULL(se.del_flag, 0) = 0
          AND IFNULL(s.del_flag, 0) = 0
          AND s.tenant_id = 'hengsui-third-001'
  );
-- 期望 affected rows = 84

-- =============================================================================
-- C. 修复后核对
-- =============================================================================

-- C1. 院内码：有效应剩 84（第一批）
SELECT COUNT(*) AS ic_active_after
FROM gz_order_entry_inhospitalcode_list
WHERE parent_id = 196
  AND detail_id = 353
  AND IFNULL(del_flag, 0) = 0;

-- C2. 本单备货库存数量（期望：54）
SELECT COUNT(*) AS inv_rows_active,
       SUM(IFNULL(qty, 0)) AS inv_qty_sum
FROM gz_depot_inventory
WHERE tenant_id = 'hengsui-third-001'
  AND order_no = 'GZRK2026103000003'
  AND order_entry_id = 353
  AND material_id = 14528
  AND IFNULL(del_flag, 0) = 0;

-- C3. 整品库存（期望：54；另有历史单 GZRK2026081000001 的 0 库存行可忽略）
SELECT SUM(IFNULL(qty, 0)) AS stock_qty_after
FROM gz_depot_inventory
WHERE tenant_id = 'hengsui-third-001'
  AND material_id = 14528
  AND IFNULL(del_flag, 0) = 0;

-- C4. 第一批出库关联仍在（期望：ship_first=30）
SELECT COUNT(*) AS ship_first_batch
FROM gz_shipment_entry se
INNER JOIN gz_shipment s ON s.id = se.paren_id
WHERE IFNULL(se.del_flag, 0) = 0
  AND IFNULL(s.del_flag, 0) = 0
  AND s.tenant_id = 'hengsui-third-001'
  AND se.in_hospital_code IN (
        SELECT in_hospital_code
        FROM gz_order_entry_inhospitalcode_list
        WHERE parent_id = 196
          AND detail_id = 353
          AND IFNULL(del_flag, 0) = 0
  );

-- ※ 核对通过：COMMIT;   否则：ROLLBACK;

-- COMMIT;
-- ROLLBACK;

DROP TEMPORARY TABLE IF EXISTS tmp_gzrk_batch2_codes;

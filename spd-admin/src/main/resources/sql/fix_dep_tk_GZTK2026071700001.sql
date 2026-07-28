-- ============================================================
-- 修复高值备货退库 GZTK-2026071700001 科室侧脏数据
-- 租户：hengsui-third-001
-- 院内码：G2607161526000208 / G2607161526000206 / G2607031053000137
--
-- 背景：
--   1) 退库审核时已写 gz_wh_flow(lx=TK)，且科室库存数量已扣减
--      （0206/0137 现为 0；0208 后因 GZCK2026072700001 再次出库恢复为 1）
--   2) 未写 gz_dep_flow(lx=TK)，科室流水追溯断层
--   3) gz_wh_flow.bill_id/entry_id 误指向无关退货单 GZTH（单据本体已不存在）
--
-- 本脚本：
--   A) 补插缺失的科室退库流水（幂等：同 bill_no + 院内码 + lx=TK 已存在则跳过）
--   B) 清空错误的仓库流水 bill_id/entry_id（保留 bill_no）
--   C) 不改动 gz_dep_inventory.qty（0208=1 与二次出库一致，属正确现状）
-- ============================================================

START TRANSACTION;

-- ---------- 核查 ----------
SELECT in_hospital_code, id, qty, department_id, update_time
FROM gz_dep_inventory
WHERE tenant_id = 'hengsui-third-001'
  AND in_hospital_code IN ('G2607161526000208', 'G2607161526000206', 'G2607031053000137')
  AND IFNULL(del_flag, 0) != 1;

SELECT bill_no, in_hospital_code, lx, qty, bill_id, entry_id, flow_time
FROM gz_wh_flow
WHERE bill_no = 'GZTK-2026071700001'
  AND IFNULL(del_flag, 0) != 1;

SELECT bill_no, in_hospital_code, lx, qty, flow_time
FROM gz_dep_flow
WHERE bill_no = 'GZTK-2026071700001'
  AND IFNULL(del_flag, 0) != 1;

-- ---------- A) 补科室退库流水 ----------
INSERT INTO gz_dep_flow (
    id, tenant_id, bill_id, bill_no, entry_id,
    department_id, department_name, warehouse_id, warehouse_name,
    material_id, batch_no, batch_number,
    qty, unit_price, amt,
    gz_dep_inventory_id, in_hospital_code, master_barcode, secondary_barcode,
    lx, flow_time, origin_business_type,
    del_flag, create_by, create_time
)
SELECT
    UUID() AS id,
    wf.tenant_id,
    NULL AS bill_id,
    wf.bill_no,
    NULL AS entry_id,
    wf.department_id,
    wf.department_name,
    wf.warehouse_id,
    wf.warehouse_name,
    wf.material_id,
    wf.batch_no,
    wf.batch_number,
    wf.qty,
    di.unit_price,
    CASE WHEN di.unit_price IS NOT NULL AND wf.qty IS NOT NULL
         THEN di.unit_price * wf.qty ELSE NULL END AS amt,
    CAST(di.id AS CHAR) AS gz_dep_inventory_id,
    wf.in_hospital_code,
    di.master_barcode,
    di.secondary_barcode,
    'TK' AS lx,
    wf.flow_time,
    '高值备货退库出科室' AS origin_business_type,
    0 AS del_flag,
    IFNULL(wf.create_by, 'fix') AS create_by,
    wf.flow_time AS create_time
FROM gz_wh_flow wf
JOIN gz_dep_inventory di
  ON di.in_hospital_code = wf.in_hospital_code
 AND di.tenant_id = wf.tenant_id
 AND IFNULL(di.del_flag, 0) != 1
 AND di.department_id = CAST(wf.department_id AS UNSIGNED)
WHERE wf.bill_no = 'GZTK-2026071700001'
  AND wf.lx = 'TK'
  AND IFNULL(wf.del_flag, 0) != 1
  AND wf.tenant_id = 'hengsui-third-001'
  AND NOT EXISTS (
      SELECT 1 FROM gz_dep_flow df
      WHERE df.bill_no = wf.bill_no
        AND df.in_hospital_code = wf.in_hospital_code
        AND df.lx = 'TK'
        AND IFNULL(df.del_flag, 0) != 1
  );

-- ---------- B) 纠正仓库流水错误单据引用 ----------
UPDATE gz_wh_flow
SET bill_id = NULL,
    entry_id = NULL,
    update_by = 'fix',
    update_time = NOW()
WHERE bill_no = 'GZTK-2026071700001'
  AND tenant_id = 'hengsui-third-001'
  AND lx = 'TK'
  AND IFNULL(del_flag, 0) != 1
  AND (bill_id IS NOT NULL OR entry_id IS NOT NULL);

-- ---------- 验证 ----------
SELECT bill_no, in_hospital_code, lx, qty, flow_time, origin_business_type, gz_dep_inventory_id
FROM gz_dep_flow
WHERE bill_no = 'GZTK-2026071700001'
  AND IFNULL(del_flag, 0) != 1
ORDER BY in_hospital_code;

SELECT bill_no, in_hospital_code, lx, bill_id, entry_id
FROM gz_wh_flow
WHERE bill_no = 'GZTK-2026071700001'
  AND IFNULL(del_flag, 0) != 1;

COMMIT;

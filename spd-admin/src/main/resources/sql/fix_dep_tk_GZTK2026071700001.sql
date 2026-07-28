-- ============================================================
-- 修复高值备货退库 GZTK-2026071700001 科室侧脏数据
-- 租户：hengsui-third-001
--
-- DBeaver 正式环境执行说明（重要）：
--   1. 不要一次选中整文件用 Ctrl+Enter（会截断语句，出现 1064 near ''）
--   2. 下面每条 SQL 单独选中，再 Ctrl+Enter；或整段用 Alt+X（执行 SQL 脚本）
--   3. 先跑「一、核查」；若科室 TK 流水已有 3 行，说明已修复，无需再跑 INSERT/UPDATE
--   4. INSERT 带 NOT EXISTS，重复执行安全（已有则插入 0 行）
-- ============================================================


-- ==================== 一、核查（每条单独 Ctrl+Enter） ====================

-- 1) 科室库存（0208 应为 qty=1；0206/0137 应为 0）
SELECT in_hospital_code, id, qty, department_id, update_time
FROM gz_dep_inventory
WHERE tenant_id = 'hengsui-third-001'
  AND in_hospital_code IN ('G2607161526000208', 'G2607161526000206', 'G2607031053000137')
  AND IFNULL(del_flag, 0) != 1;

-- 2) 仓库 TK 流水（应有 3 行）
SELECT bill_no, in_hospital_code, lx, qty, bill_id, entry_id, flow_time
FROM gz_wh_flow
WHERE bill_no = 'GZTK-2026071700001'
  AND IFNULL(del_flag, 0) != 1;

-- 3) 科室 TK 流水（已修复应有 3 行；若已有 3 行则停止，不必再执行二、三）
SELECT bill_no, in_hospital_code, lx, qty, flow_time
FROM gz_dep_flow
WHERE bill_no = 'GZTK-2026071700001'
  AND IFNULL(del_flag, 0) != 1;


-- ==================== 二、补科室退库流水（整段选中后 Ctrl+Enter） ====================
-- 必须从 INSERT 选到最后一个分号；不要只选到 SELECT 中间

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


-- ==================== 三、纠正仓库流水错误单据引用 ====================

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


-- ==================== 四、验证 ====================

SELECT bill_no, in_hospital_code, lx, qty, flow_time, origin_business_type, gz_dep_inventory_id
FROM gz_dep_flow
WHERE bill_no = 'GZTK-2026071700001'
  AND IFNULL(del_flag, 0) != 1
ORDER BY in_hospital_code;

SELECT bill_no, in_hospital_code, lx, bill_id, entry_id
FROM gz_wh_flow
WHERE bill_no = 'GZTK-2026071700001'
  AND IFNULL(del_flag, 0) != 1;

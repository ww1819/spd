-- ============================================================
-- 修复备货验收单 GZRK2026070700002 重复院内码
-- 重复码：G2607071525000144、G2607071525000145
-- 仅改 GZRK2026070700002，GZRK2026070700001 保留原码
--
-- DBeaver 执行说明（重要）：
--   1. 「一、核查」「三、验证」：每次只选中一条 SELECT，按 Ctrl+Enter
--   2. 「二、修复」：按 2-1 → 2-9 顺序，每条单独 Ctrl+Enter（已去掉存储过程）
--   3. 不要一次选中多条语句
-- ============================================================


-- ==================== 一、核查（每条 SELECT 单独执行） ====================

-- 1) 本单待修复条码（应返回 2 行）
SELECT o.id AS order_id, o.order_no, o.order_status,
       ic.id AS barcode_line_id, ic.in_hospital_code,
       ic.hc_barcode_master_id, m.code AS material_code, m.speci
FROM gz_order o
JOIN gz_order_entry_inhospitalcode_list ic
  ON ic.parent_id = o.id AND IFNULL(ic.del_flag, 0) = 0
LEFT JOIN fd_material m ON m.id = ic.material_id
WHERE o.order_no = 'GZRK2026070700002'
  AND ic.in_hospital_code IN ('G2607071525000144', 'G2607071525000145')
ORDER BY ic.id;

-- 2) 全局重复（每个旧码应出现 2 行：0001 + 0002）
SELECT o.order_no, ic.id, ic.in_hospital_code, m.code, m.speci
FROM gz_order_entry_inhospitalcode_list ic
JOIN gz_order o ON o.id = ic.parent_id
LEFT JOIN fd_material m ON m.id = ic.material_id
WHERE ic.in_hospital_code IN ('G2607071525000144', 'G2607071525000145')
  AND IFNULL(ic.del_flag, 0) = 0
ORDER BY ic.in_hospital_code, o.order_no;

-- 3a) 0002 是否被出库引用（必须 0 行才可修复）
SELECT 'code_ref' AS src, r.*
FROM gz_order_entry_code_ref r
WHERE r.src_acceptance_no = 'GZRK2026070700002'
  AND r.src_in_hospital_code IN ('G2607071525000144', 'G2607071525000145');

-- 3b) 出库单是否含这两个码（有结果也正常，不代表不能修，需看 3d 来源）
SELECT 'shipment' AS src, s.shipment_no, se.in_hospital_code
FROM gz_shipment_entry se
JOIN gz_shipment s ON s.id = se.paren_id
WHERE se.in_hospital_code IN ('G2607071525000144', 'G2607071525000145')
  AND IFNULL(se.del_flag, 0) = 0
  AND IFNULL(s.del_flag, 0) != 1;

-- 3c) 科室库存是否含这两个码（有结果也正常，需看是否来自 0002）
SELECT 'dep_inventory' AS src, di.in_hospital_code, di.department_id, di.qty
FROM gz_dep_inventory di
WHERE di.in_hospital_code IN ('G2607071525000144', 'G2607071525000145')
  AND IFNULL(di.del_flag, 0) != 1;

-- 3d) 【关键】出库实际引用的验收单来源（若全是 GZRK2026070700001，可直接修 0002）
SELECT s.shipment_no,
       COALESCE(ser.src_bill_no, cr.src_acceptance_no, '(无引用记录)') AS acceptance_no,
       se.in_hospital_code,
       se.id AS shipment_entry_id
FROM gz_shipment_entry se
JOIN gz_shipment s ON s.id = se.paren_id
LEFT JOIN gz_shipment_entry_ref ser
  ON ser.shipment_entry_id = CAST(se.id AS CHAR)
LEFT JOIN gz_order_entry_code_ref cr
  ON cr.tgt_entry_id = CAST(se.id AS CHAR) AND cr.tgt_bill_kind = 'GZ_SHIPMENT'
WHERE se.in_hospital_code IN ('G2607071525000144', 'G2607071525000145')
  AND IFNULL(se.del_flag, 0) = 0
  AND IFNULL(s.del_flag, 0) != 1;

-- 3e) 0002 备货库存（修复只改这些行，不动 0001 / 已出库行）
SELECT di.id, di.order_no, di.in_hospital_code, di.qty, di.material_id
FROM gz_depot_inventory di
WHERE di.order_no = 'GZRK2026070700002'
  AND di.in_hospital_code IN ('G2607071525000144', 'G2607071525000145')
  AND IFNULL(di.del_flag, 0) != 1;


-- ==================== 二、修复（不用存储过程，每条单独 Ctrl+Enter） ====================
-- 按 2-1 → 2-2 → … → 2-9 顺序执行，不要跳步

-- 2-1) 预览即将分配的新码（记下 new_code_1、new_code_2）
SELECT
    sheet_id AS current_sheet,
    sheet_id + 2 AS sheet_after_fix,
    CONCAT('G', RIGHT(DATE_FORMAT(NOW(), '%Y%m%d%H%i'), 10),
           RIGHT(CAST(sheet_id + 1 + 1000000 AS CHAR), 6)) AS new_code_1,
    CONCAT('G', RIGHT(DATE_FORMAT(NOW(), '%Y%m%d%H%i'), 10),
           RIGHT(CAST(sheet_id + 2 + 1000000 AS CHAR), 6)) AS new_code_2
FROM sys_sheet_id
WHERE business_type = '高值' AND sheet_type = 'gzynm';

-- 2-2) 序列号 +2（两条新码）
UPDATE sys_sheet_id
SET sheet_id = sheet_id + 2
WHERE business_type = '高值' AND sheet_type = 'gzynm';

-- 2-3) 验收条码明细：G2607071525000144 → 新码1
UPDATE gz_order_entry_inhospitalcode_list ic
JOIN gz_order o ON o.id = ic.parent_id
SET ic.in_hospital_code = CONCAT(
        'G', RIGHT(DATE_FORMAT(NOW(), '%Y%m%d%H%i'), 10),
        RIGHT(CAST((SELECT sheet_id FROM sys_sheet_id WHERE business_type = '高值' AND sheet_type = 'gzynm') - 1 + 1000000 AS CHAR), 6)
    ),
    ic.update_by = 'admin_fix_dup_code',
    ic.update_time = NOW()
WHERE o.order_no = 'GZRK2026070700002'
  AND ic.in_hospital_code = 'G2607071525000144'
  AND IFNULL(ic.del_flag, 0) = 0;

-- 2-4) 备货库存：G2607071525000144 → 新码1
UPDATE gz_depot_inventory
SET in_hospital_code = CONCAT(
        'G', RIGHT(DATE_FORMAT(NOW(), '%Y%m%d%H%i'), 10),
        RIGHT(CAST((SELECT sheet_id FROM sys_sheet_id WHERE business_type = '高值' AND sheet_type = 'gzynm') - 1 + 1000000 AS CHAR), 6)
    ),
    update_by = 'admin_fix_dup_code',
    update_time = NOW()
WHERE order_no = 'GZRK2026070700002'
  AND in_hospital_code = 'G2607071525000144'
  AND IFNULL(del_flag, 0) != 1;

-- 2-5) 条码主档 + 流通流水：G2607071525000144 → 新码1
UPDATE hc_barcode_master bm
JOIN gz_order o ON o.id = CAST(bm.bill_id AS UNSIGNED)
SET bm.barcode_value = CONCAT(
        'G', RIGHT(DATE_FORMAT(NOW(), '%Y%m%d%H%i'), 10),
        RIGHT(CAST((SELECT sheet_id FROM sys_sheet_id WHERE business_type = '高值' AND sheet_type = 'gzynm') - 1 + 1000000 AS CHAR), 6)
    ),
    bm.update_by = 'admin_fix_dup_code',
    bm.update_time = NOW()
WHERE o.order_no = 'GZRK2026070700002'
  AND bm.barcode_value = 'G2607071525000144'
  AND IFNULL(bm.del_flag, 0) = 0;

UPDATE hc_barcode_flow bf
JOIN hc_barcode_master bm ON bm.id = bf.hc_barcode_master_id
JOIN gz_order o ON o.id = CAST(bm.bill_id AS UNSIGNED)
SET bf.barcode_value = CONCAT(
        'G', RIGHT(DATE_FORMAT(NOW(), '%Y%m%d%H%i'), 10),
        RIGHT(CAST((SELECT sheet_id FROM sys_sheet_id WHERE business_type = '高值' AND sheet_type = 'gzynm') - 1 + 1000000 AS CHAR), 6)
    )
WHERE o.order_no = 'GZRK2026070700002'
  AND bf.barcode_value = 'G2607071525000144'
  AND IFNULL(bf.del_flag, 0) = 0;

-- 2-6) 仓库流水：G2607071525000144 → 新码1
UPDATE gz_wh_flow
SET in_hospital_code = CONCAT(
        'G', RIGHT(DATE_FORMAT(NOW(), '%Y%m%d%H%i'), 10),
        RIGHT(CAST((SELECT sheet_id FROM sys_sheet_id WHERE business_type = '高值' AND sheet_type = 'gzynm') - 1 + 1000000 AS CHAR), 6)
    ),
    update_by = 'admin_fix_dup_code',
    update_time = NOW()
WHERE bill_no = 'GZRK2026070700002'
  AND in_hospital_code = 'G2607071525000144'
  AND IFNULL(del_flag, 0) = 0;

-- 2-7) 验收条码明细：G2607071525000145 → 新码2
UPDATE gz_order_entry_inhospitalcode_list ic
JOIN gz_order o ON o.id = ic.parent_id
SET ic.in_hospital_code = CONCAT(
        'G', RIGHT(DATE_FORMAT(NOW(), '%Y%m%d%H%i'), 10),
        RIGHT(CAST((SELECT sheet_id FROM sys_sheet_id WHERE business_type = '高值' AND sheet_type = 'gzynm') + 1000000 AS CHAR), 6)
    ),
    ic.update_by = 'admin_fix_dup_code',
    ic.update_time = NOW()
WHERE o.order_no = 'GZRK2026070700002'
  AND ic.in_hospital_code = 'G2607071525000145'
  AND IFNULL(ic.del_flag, 0) = 0;

-- 2-8) 备货库存 + 条码主档 + 流水：G2607071525000145 → 新码2
UPDATE gz_depot_inventory
SET in_hospital_code = CONCAT(
        'G', RIGHT(DATE_FORMAT(NOW(), '%Y%m%d%H%i'), 10),
        RIGHT(CAST((SELECT sheet_id FROM sys_sheet_id WHERE business_type = '高值' AND sheet_type = 'gzynm') + 1000000 AS CHAR), 6)
    ),
    update_by = 'admin_fix_dup_code',
    update_time = NOW()
WHERE order_no = 'GZRK2026070700002'
  AND in_hospital_code = 'G2607071525000145'
  AND IFNULL(del_flag, 0) != 1;

UPDATE hc_barcode_master bm
JOIN gz_order o ON o.id = CAST(bm.bill_id AS UNSIGNED)
SET bm.barcode_value = CONCAT(
        'G', RIGHT(DATE_FORMAT(NOW(), '%Y%m%d%H%i'), 10),
        RIGHT(CAST((SELECT sheet_id FROM sys_sheet_id WHERE business_type = '高值' AND sheet_type = 'gzynm') + 1000000 AS CHAR), 6)
    ),
    bm.update_by = 'admin_fix_dup_code',
    bm.update_time = NOW()
WHERE o.order_no = 'GZRK2026070700002'
  AND bm.barcode_value = 'G2607071525000145'
  AND IFNULL(bm.del_flag, 0) = 0;

UPDATE hc_barcode_flow bf
JOIN hc_barcode_master bm ON bm.id = bf.hc_barcode_master_id
JOIN gz_order o ON o.id = CAST(bm.bill_id AS UNSIGNED)
SET bf.barcode_value = CONCAT(
        'G', RIGHT(DATE_FORMAT(NOW(), '%Y%m%d%H%i'), 10),
        RIGHT(CAST((SELECT sheet_id FROM sys_sheet_id WHERE business_type = '高值' AND sheet_type = 'gzynm') + 1000000 AS CHAR), 6)
    )
WHERE o.order_no = 'GZRK2026070700002'
  AND bf.barcode_value = 'G2607071525000145'
  AND IFNULL(bf.del_flag, 0) = 0;

UPDATE gz_wh_flow
SET in_hospital_code = CONCAT(
        'G', RIGHT(DATE_FORMAT(NOW(), '%Y%m%d%H%i'), 10),
        RIGHT(CAST((SELECT sheet_id FROM sys_sheet_id WHERE business_type = '高值' AND sheet_type = 'gzynm') + 1000000 AS CHAR), 6)
    ),
    update_by = 'admin_fix_dup_code',
    update_time = NOW()
WHERE bill_no = 'GZRK2026070700002'
  AND in_hospital_code = 'G2607071525000145'
  AND IFNULL(del_flag, 0) = 0;

-- 2-9) 修复结果摘要
SELECT
    'GZRK2026070700002' AS order_no,
    'G2607071525000144' AS old_code_1,
    CONCAT('G', RIGHT(DATE_FORMAT(NOW(), '%Y%m%d%H%i'), 10),
           RIGHT(CAST((SELECT sheet_id FROM sys_sheet_id WHERE business_type = '高值' AND sheet_type = 'gzynm') - 1 + 1000000 AS CHAR), 6)) AS new_code_1,
    'G2607071525000145' AS old_code_2,
    CONCAT('G', RIGHT(DATE_FORMAT(NOW(), '%Y%m%d%H%i'), 10),
           RIGHT(CAST((SELECT sheet_id FROM sys_sheet_id WHERE business_type = '高值' AND sheet_type = 'gzynm') + 1000000 AS CHAR), 6)) AS new_code_2;


-- ==================== 三、验证（每条 SELECT 单独执行） ====================

-- 旧码全局各应只剩 GZRK2026070700001 一条
SELECT ic.in_hospital_code, o.order_no, COUNT(*) AS cnt
FROM gz_order_entry_inhospitalcode_list ic
JOIN gz_order o ON o.id = ic.parent_id
WHERE ic.in_hospital_code IN ('G2607071525000144', 'G2607071525000145')
  AND IFNULL(ic.del_flag, 0) = 0
GROUP BY ic.in_hospital_code, o.order_no;

-- 本单条码与库存应一致，且不含旧码
SELECT o.order_no, ic.id, ic.in_hospital_code, di.in_hospital_code AS depot_code
FROM gz_order o
LEFT JOIN gz_order_entry_inhospitalcode_list ic
  ON ic.parent_id = o.id AND IFNULL(ic.del_flag, 0) = 0
LEFT JOIN gz_depot_inventory di
  ON di.order_no = o.order_no AND di.inhospitalcode_list_id = ic.id AND IFNULL(di.del_flag, 0) != 1
WHERE o.order_no = 'GZRK2026070700002'
ORDER BY ic.id;

-- ============================================================
-- 修复科室申领单 SL2026070900009 主单状态与 CKSQ 不一致
-- 现象：apply_bill_status=1（未审核），但已生成 CKSQ 且 audit_date 有值
-- 处理：将 apply_bill_status 改回 2（已审核）
-- 执行：DBeaver 选中下方 UPDATE 单独 Ctrl+Enter
-- ============================================================

-- 修复前核查（应 1 行，apply_bill_status=1，audit_date 非空）
SELECT id, apply_bill_no, apply_bill_status, audit_by, audit_date
FROM bas_apply
WHERE apply_bill_no = 'SL2026070900009';

-- 关联库房申请单（应有 CKSQ 记录）
SELECT apply_bill_no, bas_apply_bill_no, bill_status, source_audit_date
FROM wh_warehouse_apply
WHERE bas_apply_bill_no = 'SL2026070900009'
  AND (del_flag = 0 OR del_flag IS NULL);

-- 修复（单独执行）
UPDATE bas_apply
SET apply_bill_status = 2
WHERE apply_bill_no = 'SL2026070900009'
  AND apply_bill_status = 1;

-- 修复后验证（应 apply_bill_status=2）
SELECT id, apply_bill_no, apply_bill_status, audit_by, audit_date
FROM bas_apply
WHERE apply_bill_no = 'SL2026070900009';

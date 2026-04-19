-- =============================================================================
-- 历史数据迁移（扩展版 / 较激进）：在保守版基础上增加流水推断与 kc_no 对齐
-- =============================================================================
-- 使用建议：先在测试库执行 migrate_stk_io_bill_entry_inventory_refs.sql（保守版），
--           用文末自检仍缺主键时，再 **酌情** 执行本脚本剩余段落（可分段、可先备份）。
-- 注意：含「MAX(id) 取最近流水」假设、以及改写 kc_no，请在业务确认后使用。
-- =============================================================================

SET NAMES utf8mb4;

START TRANSACTION;

-- ---------------------------------------------------------------------------
-- A) 入库 101：无法 join stk_inventory 时仍将 kc_no 写入 stk_inventory_id（仅人工核对场景）
-- ---------------------------------------------------------------------------
UPDATE stk_io_bill_entry e
INNER JOIN stk_io_bill p ON e.paren_id = p.id AND p.bill_type = 101
SET e.stk_inventory_id = e.kc_no
WHERE (IFNULL(e.del_flag, 0) = 0)
  AND e.kc_no IS NOT NULL
  AND e.stk_inventory_id IS NULL
  AND NOT EXISTS (SELECT 1 FROM stk_inventory w WHERE w.id = e.kc_no);

-- ---------------------------------------------------------------------------
-- B) 出库 201：仓库流水 CK / 科室流水 CK 推断
-- ---------------------------------------------------------------------------
UPDATE stk_io_bill_entry e
INNER JOIN stk_io_bill p ON e.paren_id = p.id AND p.bill_type = 201
INNER JOIN (
  SELECT f.entry_id, MAX(f.id) AS max_id
  FROM t_hc_ck_flow f
  WHERE f.lx = 'CK' AND IFNULL(f.del_flag, 0) = 0 AND f.entry_id IS NOT NULL
  GROUP BY f.entry_id
) z ON z.entry_id = e.id
INNER JOIN t_hc_ck_flow f ON f.id = z.max_id AND f.lx = 'CK'
SET e.stk_inventory_id = f.kc_no
WHERE (IFNULL(e.del_flag, 0) = 0)
  AND e.stk_inventory_id IS NULL
  AND f.kc_no IS NOT NULL
  AND EXISTS (SELECT 1 FROM stk_inventory w WHERE w.id = f.kc_no);

UPDATE stk_io_bill_entry e
INNER JOIN stk_io_bill p ON e.paren_id = p.id AND p.bill_type = 201
INNER JOIN (
  SELECT k.entry_id, MAX(k.id) AS max_id
  FROM t_hc_ks_flow k
  WHERE k.lx = 'CK' AND IFNULL(k.del_flag, 0) = 0 AND k.entry_id IS NOT NULL
    AND (k.origin_business_type = '出库结算' OR k.origin_business_type IS NULL)
  GROUP BY k.entry_id
) z ON z.entry_id = e.id
INNER JOIN t_hc_ks_flow k ON k.id = z.max_id
INNER JOIN stk_dep_inventory d ON d.id = k.kc_no AND (IFNULL(d.del_flag, 0) = 0)
SET e.dep_inventory_id = d.id
WHERE (IFNULL(e.del_flag, 0) = 0)
  AND e.dep_inventory_id IS NULL;

-- ---------------------------------------------------------------------------
-- C) 退货 301：仓库流水 TH 推断 stk_inventory_id
-- ---------------------------------------------------------------------------
UPDATE stk_io_bill_entry e
INNER JOIN stk_io_bill p ON e.paren_id = p.id AND p.bill_type = 301
INNER JOIN (
  SELECT f.entry_id, MAX(f.id) AS max_id
  FROM t_hc_ck_flow f
  WHERE f.lx = 'TH' AND IFNULL(f.del_flag, 0) = 0 AND f.entry_id IS NOT NULL
  GROUP BY f.entry_id
) z ON z.entry_id = e.id
INNER JOIN t_hc_ck_flow f ON f.id = z.max_id AND f.lx = 'TH'
SET e.stk_inventory_id = f.kc_no
WHERE (IFNULL(e.del_flag, 0) = 0)
  AND e.stk_inventory_id IS NULL
  AND f.kc_no IS NOT NULL
  AND EXISTS (SELECT 1 FROM stk_inventory w WHERE w.id = f.kc_no);

-- ---------------------------------------------------------------------------
-- D) 退库 401：按单明细关联科室行、科室/仓库流水推断
-- ---------------------------------------------------------------------------
UPDATE stk_io_bill_entry e
INNER JOIN stk_io_bill p ON e.paren_id = p.id AND p.bill_type = 401
INNER JOIN stk_dep_inventory d ON d.bill_id = p.id AND d.bill_entry_id = e.id AND (IFNULL(d.del_flag, 0) = 0)
SET e.dep_inventory_id = d.id
WHERE (IFNULL(e.del_flag, 0) = 0)
  AND e.dep_inventory_id IS NULL;

UPDATE stk_io_bill_entry e
INNER JOIN stk_io_bill p ON e.paren_id = p.id AND p.bill_type = 401
INNER JOIN (
  SELECT k.entry_id, MAX(k.id) AS max_id
  FROM t_hc_ks_flow k
  WHERE k.lx = 'TK' AND IFNULL(k.del_flag, 0) = 0 AND k.entry_id IS NOT NULL
  GROUP BY k.entry_id
) z ON z.entry_id = e.id
INNER JOIN t_hc_ks_flow k ON k.id = z.max_id AND k.lx = 'TK'
INNER JOIN stk_dep_inventory d ON d.id = k.kc_no AND (IFNULL(d.del_flag, 0) = 0)
SET e.dep_inventory_id = d.id
WHERE (IFNULL(e.del_flag, 0) = 0)
  AND e.dep_inventory_id IS NULL;

UPDATE stk_io_bill_entry e
INNER JOIN stk_io_bill p ON e.paren_id = p.id AND p.bill_type = 401
INNER JOIN (
  SELECT f.entry_id, MAX(f.id) AS max_id
  FROM t_hc_ck_flow f
  WHERE f.lx = 'TK' AND IFNULL(f.del_flag, 0) = 0 AND f.entry_id IS NOT NULL
  GROUP BY f.entry_id
) z ON z.entry_id = e.id
INNER JOIN t_hc_ck_flow f ON f.id = z.max_id AND f.lx = 'TK'
SET e.stk_inventory_id = f.kc_no
WHERE (IFNULL(e.del_flag, 0) = 0)
  AND e.stk_inventory_id IS NULL
  AND f.kc_no IS NOT NULL
  AND EXISTS (SELECT 1 FROM stk_inventory w WHERE w.id = f.kc_no);

-- ---------------------------------------------------------------------------
-- E) 调拨 501：仓库流水 ZC 推断 stk_inventory_id
-- ---------------------------------------------------------------------------
UPDATE stk_io_bill_entry e
INNER JOIN stk_io_bill p ON e.paren_id = p.id AND p.bill_type = 501
INNER JOIN (
  SELECT f.entry_id, MAX(f.id) AS max_id
  FROM t_hc_ck_flow f
  WHERE f.lx = 'ZC' AND IFNULL(f.del_flag, 0) = 0 AND f.entry_id IS NOT NULL
  GROUP BY f.entry_id
) z ON z.entry_id = e.id
INNER JOIN t_hc_ck_flow f ON f.id = z.max_id AND f.lx = 'ZC'
SET e.stk_inventory_id = f.kc_no
WHERE (IFNULL(e.del_flag, 0) = 0)
  AND e.stk_inventory_id IS NULL
  AND f.kc_no IS NOT NULL
  AND EXISTS (SELECT 1 FROM stk_inventory w WHERE w.id = f.kc_no);

-- ---------------------------------------------------------------------------
-- F) 将 kc_no 与 dep_inventory_id 对齐（201/401，会改写历史列）
-- ---------------------------------------------------------------------------
UPDATE stk_io_bill_entry e
INNER JOIN stk_io_bill p ON e.paren_id = p.id AND p.bill_type IN (201, 401)
SET e.kc_no = e.dep_inventory_id
WHERE (IFNULL(e.del_flag, 0) = 0)
  AND e.dep_inventory_id IS NOT NULL
  AND (e.kc_no IS NULL OR e.kc_no <> e.dep_inventory_id);

COMMIT;

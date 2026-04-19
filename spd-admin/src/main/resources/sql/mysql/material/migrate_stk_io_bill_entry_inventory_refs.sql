-- =============================================================================
-- 历史数据迁移（保守版）：stk_io_bill_entry 仓库/科室库存主键拆分回填
-- =============================================================================
-- 策略说明（与「扩展版」区别）：
--   - 仅当 kc_no 或科室表字段能 **INNER JOIN 到目标实体表** 时才写入新列，不做猜测。
--   - 不从未经唯一性保证的流水表（t_hc_ck_flow / t_hc_ks_flow）推断主键。
--   - 不把无法命中 stk_inventory 的孤立 kc_no 写入 stk_inventory_id。
--   - **不修改** 明细行原有 kc_no（避免与历史报表/对账习惯冲突；新逻辑以 stk_inventory_id/dep_inventory_id 为准）。
--
-- 扩展版（含流水兜底、kc_no 与 dep 对齐、101 孤立 kc_no 写入等）：见同目录
--   migrate_stk_io_bill_entry_inventory_refs_extended.sql
--
-- 前置：已存在 stk_inventory_id、dep_inventory_id（通常先执行 column.sql 对应片段）。
-- 建议：备份 stk_io_bill_entry；大表可按 id 分段加 AND e.id BETWEEN ? AND ?。
--
-- bill_type：101 入库 201 出库 301 退货 401 退库 501 调拨
-- =============================================================================

SET NAMES utf8mb4;

START TRANSACTION;

-- ---------------------------------------------------------------------------
-- 1) 入库 101：仅当 kc_no 在 stk_inventory 中存在
-- ---------------------------------------------------------------------------
UPDATE stk_io_bill_entry e
INNER JOIN stk_io_bill p ON e.paren_id = p.id AND p.bill_type = 101
INNER JOIN stk_inventory w ON w.id = e.kc_no
SET e.stk_inventory_id = w.id
WHERE (IFNULL(e.del_flag, 0) = 0)
  AND e.kc_no IS NOT NULL
  AND e.stk_inventory_id IS NULL;

-- ---------------------------------------------------------------------------
-- 2) 出库 201：与 column.sql 核心一致 + 已解析科室行上的来源仓 id（均有表校验）
-- ---------------------------------------------------------------------------
UPDATE stk_io_bill_entry e
INNER JOIN stk_io_bill p ON e.paren_id = p.id AND p.bill_type = 201
INNER JOIN stk_dep_inventory d ON d.id = e.kc_no AND (IFNULL(d.del_flag, 0) = 0)
SET e.dep_inventory_id = d.id
WHERE (IFNULL(e.del_flag, 0) = 0)
  AND e.kc_no IS NOT NULL
  AND e.dep_inventory_id IS NULL;

UPDATE stk_io_bill_entry e
INNER JOIN stk_io_bill p ON e.paren_id = p.id AND p.bill_type = 201
INNER JOIN stk_inventory w ON w.id = e.kc_no
SET e.stk_inventory_id = w.id
WHERE (IFNULL(e.del_flag, 0) = 0)
  AND e.kc_no IS NOT NULL
  AND e.stk_inventory_id IS NULL
  AND NOT EXISTS (SELECT 1 FROM stk_dep_inventory d WHERE d.id = e.kc_no AND IFNULL(d.del_flag, 0) = 0);

-- 出库单下「本单本明细」关联的科室库存（须未删除；限制 bill_type=201 减少误绑）
UPDATE stk_io_bill_entry e
INNER JOIN stk_io_bill p ON e.paren_id = p.id AND p.bill_type = 201
INNER JOIN stk_dep_inventory d ON d.bill_id = p.id AND d.bill_entry_id = e.id AND (IFNULL(d.del_flag, 0) = 0)
    AND (d.bill_type = 201 OR d.bill_type IS NULL)
SET e.dep_inventory_id = d.id
WHERE (IFNULL(e.del_flag, 0) = 0)
  AND e.dep_inventory_id IS NULL;

-- 已能确定科室行时，用科室表 kc_no（设计为来源 stk_inventory.id）补仓库库存主键
UPDATE stk_io_bill_entry e
INNER JOIN stk_io_bill p ON e.paren_id = p.id AND p.bill_type = 201
INNER JOIN stk_dep_inventory d ON d.id = e.dep_inventory_id AND (IFNULL(d.del_flag, 0) = 0)
INNER JOIN stk_inventory w ON w.id = d.kc_no
SET e.stk_inventory_id = w.id
WHERE (IFNULL(e.del_flag, 0) = 0)
  AND e.dep_inventory_id IS NOT NULL
  AND e.stk_inventory_id IS NULL
  AND d.kc_no IS NOT NULL;

-- ---------------------------------------------------------------------------
-- 3) 退货 301：仅当 kc_no 命中 stk_inventory
-- ---------------------------------------------------------------------------
UPDATE stk_io_bill_entry e
INNER JOIN stk_io_bill p ON e.paren_id = p.id AND p.bill_type = 301
INNER JOIN stk_inventory w ON w.id = e.kc_no
SET e.stk_inventory_id = w.id
WHERE (IFNULL(e.del_flag, 0) = 0)
  AND e.kc_no IS NOT NULL
  AND e.stk_inventory_id IS NULL;

-- ---------------------------------------------------------------------------
-- 4) 退库 401：仅当 kc_no 命中 stk_dep_inventory（不依赖流水或其它关联猜测）
-- ---------------------------------------------------------------------------
UPDATE stk_io_bill_entry e
INNER JOIN stk_io_bill p ON e.paren_id = p.id AND p.bill_type = 401
INNER JOIN stk_dep_inventory d ON d.id = e.kc_no AND (IFNULL(d.del_flag, 0) = 0)
SET e.dep_inventory_id = d.id
WHERE (IFNULL(e.del_flag, 0) = 0)
  AND e.kc_no IS NOT NULL
  AND e.dep_inventory_id IS NULL;

-- ---------------------------------------------------------------------------
-- 5) 调拨 501：仅当 kc_no 命中 stk_inventory 且非科室主键 id
-- ---------------------------------------------------------------------------
UPDATE stk_io_bill_entry e
INNER JOIN stk_io_bill p ON e.paren_id = p.id AND p.bill_type = 501
INNER JOIN stk_inventory w ON w.id = e.kc_no
SET e.stk_inventory_id = w.id
WHERE (IFNULL(e.del_flag, 0) = 0)
  AND e.kc_no IS NOT NULL
  AND e.stk_inventory_id IS NULL
  AND NOT EXISTS (SELECT 1 FROM stk_dep_inventory d WHERE d.id = e.kc_no AND IFNULL(d.del_flag, 0) = 0);

COMMIT;

-- =============================================================================
-- 迁移后自检（只读）
-- =============================================================================
-- 已审核入库 101 仍缺 stk_inventory_id（多为 kc_no 无效，需人工）
-- SELECT e.id, e.paren_id, e.kc_no, e.stk_inventory_id
-- FROM stk_io_bill_entry e
-- INNER JOIN stk_io_bill p ON p.id = e.paren_id AND p.bill_type = 101 AND IFNULL(p.bill_status, 0) = 2
-- WHERE IFNULL(e.del_flag, 0) = 0 AND e.kc_no IS NOT NULL AND e.stk_inventory_id IS NULL;

-- 已审核出库 201 仍缺列（可再评估是否执行 extended 脚本中的流水兜底）
-- SELECT e.id, e.paren_id, e.kc_no, e.stk_inventory_id, e.dep_inventory_id
-- FROM stk_io_bill_entry e
-- INNER JOIN stk_io_bill p ON p.id = e.paren_id AND p.bill_type = 201 AND IFNULL(p.bill_status, 0) = 2
-- WHERE IFNULL(e.del_flag, 0) = 0 AND (e.stk_inventory_id IS NULL OR e.dep_inventory_id IS NULL);

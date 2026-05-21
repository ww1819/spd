-- =============================================================================
-- 历史数据补全：盘盈生成的科室库存 + 科室退库(401)明细
-- 与线上一致口径：
--   DeptStocktakingServiceImpl / InventoryMaterialSnapshotHelper（科室盘点直审）
--   StkIoProfitLossServiceImpl.buildStkDepInventoryForProfit（科室盈亏盘盈）
--   StkIoBillServiceImpl.fillTk401EntryFromDepInventory（退库401）
-- 执行前请先备份；建议按租户分批，将 @tenant_id 设为实际 customer_id。
-- 重要：历史盘盈科室库存多数无 bill_id，需先跑 1～5 段再跑 401 段；若供应商仍空，请查
--   stk_io_stocktaking_entry.supplier_id / fd_material.supplier_id / stk_batch.supplier_id 是否本身为空。
-- =============================================================================

SET @tenant_id := NULL;  -- 例：'your-customer-uuid'；NULL 表示全库（慎用）

-- -----------------------------------------------------------------------------
-- 0) 诊断：为何未补上（执行 UPDATE 前先看命中情况）
-- -----------------------------------------------------------------------------
SELECT 'dep_missing_snapshot' AS tag, COUNT(*) AS cnt
FROM stk_dep_inventory dep
WHERE (dep.del_flag IS NULL OR dep.del_flag = 0)
  AND (@tenant_id IS NULL OR dep.tenant_id = @tenant_id)
  AND dep.material_id IS NOT NULL
  AND (
    dep.material_name IS NULL OR TRIM(dep.material_name) = ''
    OR dep.supplier_id IS NULL OR TRIM(dep.supplier_id) = ''
  );

SELECT 'can_link_stocktaking_entry' AS tag, COUNT(DISTINCT dep.id) AS cnt
FROM stk_dep_inventory dep
INNER JOIN stk_io_stocktaking_entry se ON (
  (se.dep_inventory_id IS NOT NULL AND TRIM(se.dep_inventory_id) <> '' AND CAST(TRIM(se.dep_inventory_id) AS UNSIGNED) = dep.id)
)
INNER JOIN stk_io_stocktaking st ON st.id = se.paren_id AND st.stock_type IN (501, 502)
WHERE (dep.del_flag IS NULL OR dep.del_flag = 0)
  AND (@tenant_id IS NULL OR dep.tenant_id = @tenant_id)
  AND (dep.supplier_id IS NULL OR TRIM(dep.supplier_id) = '');

SELECT 'can_link_profit_loss_result' AS tag, COUNT(DISTINCT dep.id) AS cnt
FROM stk_dep_inventory dep
INNER JOIN stk_io_profit_loss_entry pl ON pl.result_dep_inventory_id = dep.id
WHERE (dep.del_flag IS NULL OR dep.del_flag = 0)
  AND (@tenant_id IS NULL OR dep.tenant_id = @tenant_id)
  AND (dep.supplier_id IS NULL OR TRIM(dep.supplier_id) = '');

SELECT 'can_link_ks_flow_py' AS tag, COUNT(DISTINCT dep.id) AS cnt
FROM stk_dep_inventory dep
INNER JOIN t_hc_ks_flow f ON f.kc_no = dep.id AND f.lx = 'PY'
  AND (f.del_flag IS NULL OR f.del_flag = 0)
  AND (f.origin_business_type LIKE '%盘盈%' OR f.origin_business_type LIKE '%盘点%')
WHERE (dep.del_flag IS NULL OR dep.del_flag = 0)
  AND (@tenant_id IS NULL OR dep.tenant_id = @tenant_id)
  AND (dep.supplier_id IS NULL OR TRIM(dep.supplier_id) = '');

SELECT 'tk401_entry' AS tag, COUNT(*) AS cnt
FROM stk_io_bill_entry e
INNER JOIN stk_io_bill b ON b.id = e.paren_id AND b.bill_type = 401
WHERE (e.del_flag IS NULL OR e.del_flag != 1)
  AND (b.del_flag IS NULL OR b.del_flag != 1)
  AND (@tenant_id IS NULL OR e.tenant_id = @tenant_id OR b.tenant_id = @tenant_id)
  AND (
    e.dep_inventory_id IS NULL
    OR e.warehouse_id IS NULL
    OR e.suppler_id IS NULL OR e.suppler_id = ''
    OR e.begin_time IS NULL OR e.end_time IS NULL
    OR e.material_name IS NULL OR e.material_name = ''
    OR e.stk_inventory_id IS NULL
  );

-- 批次映射：按 batch_no(+material_id) 取最新 stk_batch（全脚本复用）
DROP TEMPORARY TABLE IF EXISTS tmp_dep_batch_pick;
CREATE TEMPORARY TABLE tmp_dep_batch_pick (
  batch_no VARCHAR(100) NOT NULL,
  material_id BIGINT NOT NULL,
  batch_id BIGINT NOT NULL,
  supplier_id BIGINT DEFAULT NULL,
  factory_id BIGINT DEFAULT NULL,
  PRIMARY KEY (batch_no, material_id)
) ENGINE=InnoDB;

INSERT INTO tmp_dep_batch_pick (batch_no, material_id, batch_id, supplier_id, factory_id)
SELECT x.batch_no, x.material_id, b.id, b.supplier_id, b.factory_id
FROM (
  SELECT batch_no, material_id, MAX(id) AS max_id
  FROM stk_batch
  WHERE batch_no IS NOT NULL AND TRIM(batch_no) <> '' AND material_id IS NOT NULL
    AND (@tenant_id IS NULL OR tenant_id = @tenant_id)
  GROUP BY batch_no, material_id
) x
INNER JOIN stk_batch b ON b.id = x.max_id;

-- -----------------------------------------------------------------------------
-- 1) 【主路径】盘点明细 dep_inventory_id → 科室库存（历史盘盈多数 bill_id 为空）
-- -----------------------------------------------------------------------------
UPDATE stk_dep_inventory dep
INNER JOIN stk_io_stocktaking_entry se ON se.dep_inventory_id IS NOT NULL
  AND TRIM(se.dep_inventory_id) <> ''
  AND CAST(TRIM(se.dep_inventory_id) AS UNSIGNED) = dep.id
INNER JOIN stk_io_stocktaking st ON st.id = se.paren_id AND st.stock_type IN (501, 502)
LEFT JOIN tmp_dep_batch_pick bat ON bat.batch_no = dep.batch_no AND bat.material_id = dep.material_id
LEFT JOIN fd_material m ON m.id = dep.material_id AND (m.del_flag IS NULL OR m.del_flag = 0)
SET
  dep.material_name = COALESCE(NULLIF(TRIM(dep.material_name), ''), NULLIF(TRIM(m.name), '')),
  dep.material_speci = COALESCE(NULLIF(TRIM(dep.material_speci), ''), NULLIF(TRIM(m.speci), '')),
  dep.material_model = COALESCE(NULLIF(TRIM(dep.material_model), ''), NULLIF(TRIM(m.model), '')),
  dep.material_factory_id = COALESCE(dep.material_factory_id, m.factory_id),
  dep.factory_id = COALESCE(dep.factory_id, bat.factory_id, m.factory_id),
  dep.supplier_id = COALESCE(
    NULLIF(TRIM(dep.supplier_id), ''),
    NULLIF(TRIM(se.supplier_id_str), ''),
    CASE WHEN se.supplier_id IS NOT NULL THEN CAST(se.supplier_id AS CHAR) END,
    CASE WHEN bat.supplier_id IS NOT NULL THEN CAST(bat.supplier_id AS CHAR) END,
    CASE WHEN m.supplier_id IS NOT NULL THEN CAST(m.supplier_id AS CHAR) END
  ),
  dep.warehouse_id = COALESCE(dep.warehouse_id, se.return_warehouse_id, st.warehouse_id),
  dep.batch_id = COALESCE(dep.batch_id, bat.batch_id, se.orig_batch_id),
  dep.batch_number = COALESCE(NULLIF(TRIM(dep.batch_number), ''), NULLIF(TRIM(se.batch_number), '')),
  dep.begin_time = COALESCE(dep.begin_time, se.begin_time),
  dep.end_time = COALESCE(dep.end_time, se.end_time),
  dep.main_barcode = COALESCE(NULLIF(TRIM(dep.main_barcode), ''), NULLIF(TRIM(se.main_barcode), '')),
  dep.sub_barcode = COALESCE(NULLIF(TRIM(dep.sub_barcode), ''), NULLIF(TRIM(se.sub_barcode), '')),
  dep.tenant_id = COALESCE(NULLIF(TRIM(dep.tenant_id), ''), se.tenant_id, st.tenant_id),
  dep.bill_id = COALESCE(dep.bill_id, st.id),
  dep.bill_entry_id = COALESCE(dep.bill_entry_id, se.id),
  dep.bill_no = COALESCE(NULLIF(TRIM(dep.bill_no), ''), st.stock_no, se.stock_no),
  dep.out_order_no = COALESCE(NULLIF(TRIM(dep.out_order_no), ''), st.stock_no, se.stock_no),
  dep.kc_no = COALESCE(dep.kc_no, se.kc_no),
  dep.receipt_confirm_status = COALESCE(dep.receipt_confirm_status, 1),
  dep.remark = COALESCE(
    NULLIF(TRIM(dep.remark), ''),
    CASE WHEN st.stock_type = 502
      AND (se.profit_loss_flag = 'PROFIT' OR IFNULL(se.profit_qty, 0) > 0 OR IFNULL(se.stock_qty, 0) > IFNULL(se.qty, 0))
      THEN '本库存由科室盘点盘盈业务产生' END
  ),
  dep.update_time = NOW()
WHERE (dep.del_flag IS NULL OR dep.del_flag = 0)
  AND (@tenant_id IS NULL OR dep.tenant_id = @tenant_id);

-- -----------------------------------------------------------------------------
-- 2) 科室盈亏盘盈 → stk_dep_inventory（result_dep_inventory_id；不限制 pl_kind/biz_scope）
-- -----------------------------------------------------------------------------
UPDATE stk_dep_inventory dep
INNER JOIN stk_io_profit_loss_entry pl ON pl.result_dep_inventory_id = dep.id
  AND (pl.profit_qty IS NULL OR pl.profit_qty > 0)
INNER JOIN stk_io_profit_loss plh ON plh.id = pl.paren_id
  AND (plh.department_id IS NOT NULL OR plh.biz_scope = 'DEP' OR plh.biz_scope IS NULL)
LEFT JOIN stk_io_stocktaking_entry se ON se.id = pl.stocktaking_entry_id
LEFT JOIN tmp_dep_batch_pick bat ON bat.batch_no = dep.batch_no AND bat.material_id = dep.material_id
LEFT JOIN fd_material m ON m.id = dep.material_id AND (m.del_flag IS NULL OR m.del_flag = 0)
SET
  dep.material_name = COALESCE(NULLIF(TRIM(dep.material_name), ''), NULLIF(TRIM(pl.material_name_snap), ''), NULLIF(TRIM(m.name), '')),
  dep.material_speci = COALESCE(NULLIF(TRIM(dep.material_speci), ''), NULLIF(TRIM(pl.material_speci_snap), ''), NULLIF(TRIM(m.speci), '')),
  dep.material_model = COALESCE(NULLIF(TRIM(dep.material_model), ''), NULLIF(TRIM(m.model), '')),
  dep.material_factory_id = COALESCE(dep.material_factory_id, m.factory_id),
  dep.factory_id = COALESCE(dep.factory_id, bat.factory_id, m.factory_id),
  dep.supplier_id = COALESCE(
    NULLIF(TRIM(dep.supplier_id), ''),
    NULLIF(TRIM(pl.suppler_id), ''),
    NULLIF(TRIM(se.supplier_id_str), ''),
    CASE WHEN se.supplier_id IS NOT NULL THEN CAST(se.supplier_id AS CHAR) END,
    CASE WHEN bat.supplier_id IS NOT NULL THEN CAST(bat.supplier_id AS CHAR) END,
    CASE WHEN m.supplier_id IS NOT NULL THEN CAST(m.supplier_id AS CHAR) END
  ),
  dep.warehouse_id = COALESCE(dep.warehouse_id, pl.return_warehouse_id, se.return_warehouse_id, plh.warehouse_id),
  dep.batch_id = COALESCE(dep.batch_id, pl.surplus_stk_batch_id, bat.batch_id),
  dep.batch_number = COALESCE(NULLIF(TRIM(dep.batch_number), ''), NULLIF(TRIM(pl.batch_number), ''), NULLIF(TRIM(se.batch_number), '')),
  dep.begin_time = COALESCE(dep.begin_time, pl.begin_time, se.begin_time),
  dep.end_time = COALESCE(dep.end_time, pl.end_time, se.end_time),
  dep.main_barcode = COALESCE(NULLIF(TRIM(dep.main_barcode), ''), NULLIF(TRIM(pl.main_barcode), ''), NULLIF(TRIM(se.main_barcode), '')),
  dep.sub_barcode = COALESCE(NULLIF(TRIM(dep.sub_barcode), ''), NULLIF(TRIM(pl.sub_barcode), ''), NULLIF(TRIM(se.sub_barcode), '')),
  dep.tenant_id = COALESCE(NULLIF(TRIM(dep.tenant_id), ''), plh.tenant_id),
  dep.bill_id = COALESCE(dep.bill_id, plh.id),
  dep.bill_entry_id = COALESCE(dep.bill_entry_id, pl.id),
  dep.bill_no = COALESCE(NULLIF(TRIM(dep.bill_no), ''), plh.bill_no),
  dep.out_order_no = COALESCE(NULLIF(TRIM(dep.out_order_no), ''), plh.bill_no),
  dep.remark = COALESCE(NULLIF(TRIM(dep.remark), ''), '科室盈亏单盘盈审核生成'),
  dep.receipt_confirm_status = COALESCE(dep.receipt_confirm_status, 1),
  dep.update_time = NOW()
WHERE (dep.del_flag IS NULL OR dep.del_flag = 0)
  AND (@tenant_id IS NULL OR dep.tenant_id = @tenant_id);

-- -----------------------------------------------------------------------------
-- 3) 科室盘点直审（bill_id 已写入主表时）
-- -----------------------------------------------------------------------------
UPDATE stk_dep_inventory dep
INNER JOIN stk_io_stocktaking st ON st.id = dep.bill_id AND st.stock_type IN (501, 502)
LEFT JOIN stk_io_stocktaking_entry se ON se.id = dep.bill_entry_id
LEFT JOIN tmp_dep_batch_pick bat ON bat.batch_no = dep.batch_no AND bat.material_id = dep.material_id
LEFT JOIN fd_material m ON m.id = dep.material_id AND (m.del_flag IS NULL OR m.del_flag = 0)
SET
  dep.material_name = COALESCE(NULLIF(TRIM(dep.material_name), ''), NULLIF(TRIM(m.name), '')),
  dep.material_speci = COALESCE(NULLIF(TRIM(dep.material_speci), ''), NULLIF(TRIM(m.speci), '')),
  dep.material_model = COALESCE(NULLIF(TRIM(dep.material_model), ''), NULLIF(TRIM(m.model), '')),
  dep.material_factory_id = COALESCE(dep.material_factory_id, m.factory_id),
  dep.factory_id = COALESCE(dep.factory_id, bat.factory_id, m.factory_id),
  dep.supplier_id = COALESCE(
    NULLIF(TRIM(dep.supplier_id), ''),
    NULLIF(TRIM(se.supplier_id_str), ''),
    CASE WHEN se.supplier_id IS NOT NULL THEN CAST(se.supplier_id AS CHAR) END,
    CASE WHEN bat.supplier_id IS NOT NULL THEN CAST(bat.supplier_id AS CHAR) END,
    CASE WHEN m.supplier_id IS NOT NULL THEN CAST(m.supplier_id AS CHAR) END
  ),
  dep.warehouse_id = COALESCE(dep.warehouse_id, se.return_warehouse_id, st.warehouse_id),
  dep.batch_id = COALESCE(dep.batch_id, bat.batch_id),
  dep.batch_number = COALESCE(NULLIF(TRIM(dep.batch_number), ''), NULLIF(TRIM(se.batch_number), '')),
  dep.begin_time = COALESCE(dep.begin_time, se.begin_time),
  dep.end_time = COALESCE(dep.end_time, se.end_time),
  dep.main_barcode = COALESCE(NULLIF(TRIM(dep.main_barcode), ''), NULLIF(TRIM(se.main_barcode), '')),
  dep.sub_barcode = COALESCE(NULLIF(TRIM(dep.sub_barcode), ''), NULLIF(TRIM(se.sub_barcode), '')),
  dep.tenant_id = COALESCE(NULLIF(TRIM(dep.tenant_id), ''), st.tenant_id),
  dep.bill_no = COALESCE(NULLIF(TRIM(dep.bill_no), ''), st.stock_no),
  dep.out_order_no = COALESCE(NULLIF(TRIM(dep.out_order_no), ''), st.stock_no),
  dep.kc_no = COALESCE(dep.kc_no, se.kc_no),
  dep.receipt_confirm_status = COALESCE(dep.receipt_confirm_status, 1),
  dep.remark = COALESCE(
    NULLIF(TRIM(dep.remark), ''),
    CASE WHEN st.stock_type = 502
      AND (se.profit_loss_flag = 'PROFIT' OR IFNULL(se.profit_qty, 0) > 0 OR IFNULL(se.stock_qty, 0) > IFNULL(se.qty, 0))
      THEN '本库存由科室盘点盘盈业务产生' END
  ),
  dep.update_time = NOW()
WHERE (dep.del_flag IS NULL OR dep.del_flag = 0)
  AND (@tenant_id IS NULL OR dep.tenant_id = @tenant_id);

-- -----------------------------------------------------------------------------
-- 3b) 科室盘点盘盈 remark 兜底（仅 502 且盈亏为盈，不覆盖已有备注）
-- -----------------------------------------------------------------------------
UPDATE stk_dep_inventory dep
INNER JOIN stk_io_stocktaking_entry se ON se.dep_inventory_id IS NOT NULL
  AND TRIM(se.dep_inventory_id) <> ''
  AND CAST(TRIM(se.dep_inventory_id) AS UNSIGNED) = dep.id
INNER JOIN stk_io_stocktaking st ON st.id = se.paren_id AND st.stock_type = 502
  AND (se.profit_loss_flag = 'PROFIT' OR IFNULL(se.profit_qty, 0) > 0 OR IFNULL(se.stock_qty, 0) > IFNULL(se.qty, 0))
SET dep.remark = '本库存由科室盘点盘盈业务产生',
    dep.update_time = NOW()
WHERE (dep.del_flag IS NULL OR dep.del_flag = 0)
  AND (@tenant_id IS NULL OR dep.tenant_id = @tenant_id)
  AND (dep.remark IS NULL OR TRIM(dep.remark) = '');

-- -----------------------------------------------------------------------------
-- 4) 科室流水 PY（审核时写过 supplier_id / material_name 快照）
-- -----------------------------------------------------------------------------
UPDATE stk_dep_inventory dep
INNER JOIN (
  SELECT kc_no, MAX(id) AS max_flow_id
  FROM t_hc_ks_flow
  WHERE kc_no IS NOT NULL AND lx = 'PY'
    AND (del_flag IS NULL OR del_flag = 0)
    AND (@tenant_id IS NULL OR tenant_id = @tenant_id)
  GROUP BY kc_no
) fp ON fp.kc_no = dep.id
INNER JOIN t_hc_ks_flow f ON f.id = fp.max_flow_id
LEFT JOIN tmp_dep_batch_pick bat ON bat.batch_no = dep.batch_no AND bat.material_id = dep.material_id
LEFT JOIN fd_material m ON m.id = dep.material_id AND (m.del_flag IS NULL OR m.del_flag = 0)
SET
  dep.material_name = COALESCE(NULLIF(TRIM(dep.material_name), ''), NULLIF(TRIM(f.material_name), ''), NULLIF(TRIM(m.name), '')),
  dep.material_speci = COALESCE(NULLIF(TRIM(dep.material_speci), ''), NULLIF(TRIM(m.speci), '')),
  dep.material_model = COALESCE(NULLIF(TRIM(dep.material_model), ''), NULLIF(TRIM(m.model), '')),
  dep.material_factory_id = COALESCE(dep.material_factory_id, f.factory_id, m.factory_id),
  dep.factory_id = COALESCE(dep.factory_id, f.factory_id, bat.factory_id, m.factory_id),
  dep.supplier_id = COALESCE(
    NULLIF(TRIM(dep.supplier_id), ''),
    NULLIF(TRIM(f.supplier_id), ''),
    CASE WHEN bat.supplier_id IS NOT NULL THEN CAST(bat.supplier_id AS CHAR) END,
    CASE WHEN m.supplier_id IS NOT NULL THEN CAST(m.supplier_id AS CHAR) END
  ),
  dep.warehouse_id = COALESCE(dep.warehouse_id, f.warehouse_id),
  dep.batch_id = COALESCE(dep.batch_id, f.batch_id, bat.batch_id),
  dep.batch_number = COALESCE(NULLIF(TRIM(dep.batch_number), ''), NULLIF(TRIM(f.batch_number), '')),
  dep.begin_time = COALESCE(dep.begin_time, f.begin_time),
  dep.end_time = COALESCE(dep.end_time, f.end_time),
  dep.main_barcode = COALESCE(NULLIF(TRIM(dep.main_barcode), ''), NULLIF(TRIM(f.main_barcode), '')),
  dep.sub_barcode = COALESCE(NULLIF(TRIM(dep.sub_barcode), ''), NULLIF(TRIM(f.sub_barcode), '')),
  dep.bill_id = COALESCE(dep.bill_id, f.bill_id),
  dep.bill_entry_id = COALESCE(dep.bill_entry_id, f.entry_id),
  dep.bill_no = COALESCE(NULLIF(TRIM(dep.bill_no), ''), NULLIF(TRIM(f.bill_no), '')),
  dep.out_order_no = COALESCE(NULLIF(TRIM(dep.out_order_no), ''), NULLIF(TRIM(f.bill_no), '')),
  dep.receipt_confirm_status = COALESCE(dep.receipt_confirm_status, 1),
  dep.update_time = NOW()
WHERE (dep.del_flag IS NULL OR dep.del_flag = 0)
  AND (@tenant_id IS NULL OR dep.tenant_id = @tenant_id);

-- -----------------------------------------------------------------------------
-- 5) 兜底：所有仍缺快照且有 material_id 的科室库存（档案 + 批次）
-- -----------------------------------------------------------------------------
UPDATE stk_dep_inventory dep
LEFT JOIN tmp_dep_batch_pick bat ON bat.batch_no = dep.batch_no AND bat.material_id = dep.material_id
INNER JOIN fd_material m ON m.id = dep.material_id AND (m.del_flag IS NULL OR m.del_flag = 0)
SET
  dep.material_name = COALESCE(NULLIF(TRIM(dep.material_name), ''), NULLIF(TRIM(m.name), '')),
  dep.material_speci = COALESCE(NULLIF(TRIM(dep.material_speci), ''), NULLIF(TRIM(m.speci), '')),
  dep.material_model = COALESCE(NULLIF(TRIM(dep.material_model), ''), NULLIF(TRIM(m.model), '')),
  dep.material_factory_id = COALESCE(dep.material_factory_id, m.factory_id),
  dep.factory_id = COALESCE(dep.factory_id, bat.factory_id, m.factory_id),
  dep.supplier_id = COALESCE(
    NULLIF(TRIM(dep.supplier_id), ''),
    CASE WHEN bat.supplier_id IS NOT NULL THEN CAST(bat.supplier_id AS CHAR) END,
    CASE WHEN m.supplier_id IS NOT NULL THEN CAST(m.supplier_id AS CHAR) END
  ),
  dep.batch_id = COALESCE(dep.batch_id, bat.batch_id),
  dep.update_time = NOW()
WHERE (dep.del_flag IS NULL OR dep.del_flag = 0)
  AND (@tenant_id IS NULL OR dep.tenant_id = @tenant_id)
  AND dep.material_id IS NOT NULL
  AND (
    dep.material_name IS NULL OR TRIM(dep.material_name) = ''
    OR dep.material_speci IS NULL OR TRIM(dep.material_speci) = ''
    OR dep.supplier_id IS NULL OR TRIM(dep.supplier_id) = ''
  );

DROP TEMPORARY TABLE IF EXISTS tmp_dep_batch_pick;

-- -----------------------------------------------------------------------------
-- 6) 科室退库(401)明细补全（先完成上文 1～5 段；禁止 JOIN 内逐行子查询）
--    6a 科室库存 + 主表 + 档案；6b 仓库库存（批次+仓库最新一行）
-- -----------------------------------------------------------------------------

DROP TEMPORARY TABLE IF EXISTS tmp_tk401_wh_map;
CREATE TEMPORARY TABLE tmp_tk401_wh_map (
  batch_no VARCHAR(100) NOT NULL,
  warehouse_id BIGINT NOT NULL,
  inv_id BIGINT NOT NULL,
  supplier_id BIGINT DEFAULT NULL,
  begin_time DATETIME DEFAULT NULL,
  end_time DATETIME DEFAULT NULL,
  main_barcode VARCHAR(128) DEFAULT NULL,
  sub_barcode VARCHAR(128) DEFAULT NULL,
  material_name VARCHAR(256) DEFAULT NULL,
  material_speci VARCHAR(256) DEFAULT NULL,
  material_model VARCHAR(256) DEFAULT NULL,
  material_factory_id BIGINT DEFAULT NULL,
  PRIMARY KEY (batch_no, warehouse_id)
) ENGINE=InnoDB;

-- 仅汇总 401 明细实际用到的 batch_no + warehouse_id，避免全表 GROUP BY
INSERT INTO tmp_tk401_wh_map (
  batch_no, warehouse_id, inv_id, supplier_id, begin_time, end_time,
  main_barcode, sub_barcode, material_name, material_speci, material_model, material_factory_id
)
SELECT
  x.batch_no,
  x.warehouse_id,
  w.id,
  w.supplier_id,
  w.begin_time,
  w.end_time,
  w.main_barcode,
  w.sub_barcode,
  w.material_name,
  w.material_speci,
  w.material_model,
  w.material_factory_id
FROM (
  SELECT w2.batch_no, w2.warehouse_id, MAX(w2.id) AS max_inv_id
  FROM stk_inventory w2
  INNER JOIN (
    SELECT DISTINCT
      TRIM(e.batch_no) AS batch_no,
      COALESCE(e.warehouse_id, dep.warehouse_id, b.warehouse_id) AS warehouse_id
    FROM stk_io_bill_entry e
    INNER JOIN stk_io_bill b ON b.id = e.paren_id AND b.bill_type = 401
    LEFT JOIN stk_dep_inventory dep ON dep.id = COALESCE(e.dep_inventory_id, e.kc_no)
      AND (dep.del_flag IS NULL OR dep.del_flag = 0)
    WHERE (e.del_flag IS NULL OR e.del_flag != 1)
      AND (b.del_flag IS NULL OR b.del_flag != 1)
      AND e.batch_no IS NOT NULL AND TRIM(e.batch_no) <> ''
      AND COALESCE(e.warehouse_id, dep.warehouse_id, b.warehouse_id) IS NOT NULL
      AND (@tenant_id IS NULL OR e.tenant_id = @tenant_id OR b.tenant_id = @tenant_id)
  ) need ON need.batch_no = w2.batch_no AND need.warehouse_id = w2.warehouse_id
  WHERE (w2.del_flag IS NULL OR w2.del_flag = 0)
    AND (@tenant_id IS NULL OR w2.tenant_id = @tenant_id)
  GROUP BY w2.batch_no, w2.warehouse_id
) x
INNER JOIN stk_inventory w ON w.id = x.max_inv_id;

-- 6a) 科室库存 + 主表 + 档案（不关联 stk_inventory，通常秒级完成）
UPDATE stk_io_bill_entry e
INNER JOIN stk_io_bill b ON b.id = e.paren_id AND b.bill_type = 401
LEFT JOIN (
  SELECT batch_no, department_id, MIN(id) AS dep_id
  FROM stk_dep_inventory
  WHERE (del_flag IS NULL OR del_flag = 0)
    AND batch_no IS NOT NULL AND TRIM(batch_no) <> ''
    AND department_id IS NOT NULL
    AND (@tenant_id IS NULL OR tenant_id = @tenant_id)
  GROUP BY batch_no, department_id
) dep_pick ON dep_pick.batch_no = e.batch_no AND dep_pick.department_id = b.department_id
LEFT JOIN stk_dep_inventory dep ON dep.id = COALESCE(e.dep_inventory_id, e.kc_no, dep_pick.dep_id)
  AND (dep.del_flag IS NULL OR dep.del_flag = 0)
LEFT JOIN fd_material m ON m.id = e.material_id
  AND (m.del_flag IS NULL OR m.del_flag = 0)
  AND (@tenant_id IS NULL OR m.tenant_id = @tenant_id OR m.tenant_id IS NULL)
SET
  e.dep_inventory_id = COALESCE(e.dep_inventory_id, dep.id),
  e.kc_no = COALESCE(e.kc_no, dep.id),
  e.warehouse_id = COALESCE(e.warehouse_id, dep.warehouse_id, b.warehouse_id),
  e.suppler_id = COALESCE(
    NULLIF(TRIM(e.suppler_id), ''),
    NULLIF(TRIM(dep.supplier_id), ''),
    CASE WHEN m.supplier_id IS NOT NULL THEN CAST(m.supplier_id AS CHAR) END
  ),
  e.supplier_id_str = COALESCE(
    NULLIF(TRIM(e.supplier_id_str), ''),
    NULLIF(TRIM(e.suppler_id), ''),
    NULLIF(TRIM(dep.supplier_id), ''),
    CASE WHEN m.supplier_id IS NOT NULL THEN CAST(m.supplier_id AS CHAR) END
  ),
  e.begin_time = COALESCE(e.begin_time, dep.begin_time),
  e.end_time = COALESCE(e.end_time, dep.end_time),
  e.main_barcode = COALESCE(NULLIF(TRIM(e.main_barcode), ''), NULLIF(TRIM(dep.main_barcode), '')),
  e.sub_barcode = COALESCE(NULLIF(TRIM(e.sub_barcode), ''), NULLIF(TRIM(dep.sub_barcode), '')),
  e.material_name = COALESCE(
    NULLIF(TRIM(e.material_name), ''),
    NULLIF(TRIM(dep.material_name), ''),
    NULLIF(TRIM(m.name), '')
  ),
  e.material_speci = COALESCE(
    NULLIF(TRIM(e.material_speci), ''),
    NULLIF(TRIM(dep.material_speci), ''),
    NULLIF(TRIM(m.speci), '')
  ),
  e.material_model = COALESCE(
    NULLIF(TRIM(e.material_model), ''),
    NULLIF(TRIM(dep.material_model), ''),
    NULLIF(TRIM(m.model), '')
  ),
  e.material_factory_id = COALESCE(e.material_factory_id, dep.material_factory_id, m.factory_id),
  e.warehouse_id_str = COALESCE(NULLIF(TRIM(e.warehouse_id_str), ''), CAST(COALESCE(e.warehouse_id, dep.warehouse_id, b.warehouse_id) AS CHAR)),
  e.department_id_str = COALESCE(NULLIF(TRIM(e.department_id_str), ''), CAST(b.department_id AS CHAR)),
  e.bill_no = COALESCE(NULLIF(TRIM(e.bill_no), ''), b.bill_no)
WHERE (e.del_flag IS NULL OR e.del_flag != 1)
  AND (b.del_flag IS NULL OR b.del_flag != 1)
  AND (@tenant_id IS NULL OR e.tenant_id = @tenant_id OR b.tenant_id = @tenant_id);

-- 6b) 仓库库存字段 + stk_inventory_id（走主键映射，避免相关子查询）
UPDATE stk_io_bill_entry e
INNER JOIN stk_io_bill b ON b.id = e.paren_id AND b.bill_type = 401
LEFT JOIN (
  SELECT batch_no, department_id, MIN(id) AS dep_id
  FROM stk_dep_inventory
  WHERE (del_flag IS NULL OR del_flag = 0)
    AND batch_no IS NOT NULL AND TRIM(batch_no) <> ''
    AND department_id IS NOT NULL
    AND (@tenant_id IS NULL OR tenant_id = @tenant_id)
  GROUP BY batch_no, department_id
) dep_pick ON dep_pick.batch_no = e.batch_no AND dep_pick.department_id = b.department_id
LEFT JOIN stk_dep_inventory dep ON dep.id = COALESCE(e.dep_inventory_id, e.kc_no, dep_pick.dep_id)
  AND (dep.del_flag IS NULL OR dep.del_flag = 0)
INNER JOIN tmp_tk401_wh_map wh ON wh.batch_no = TRIM(e.batch_no)
  AND wh.warehouse_id = COALESCE(e.warehouse_id, dep.warehouse_id, b.warehouse_id)
SET
  e.suppler_id = COALESCE(
    NULLIF(TRIM(e.suppler_id), ''),
    CASE WHEN wh.supplier_id IS NOT NULL THEN CAST(wh.supplier_id AS CHAR) END
  ),
  e.supplier_id_str = COALESCE(
    NULLIF(TRIM(e.supplier_id_str), ''),
    NULLIF(TRIM(e.suppler_id), ''),
    CASE WHEN wh.supplier_id IS NOT NULL THEN CAST(wh.supplier_id AS CHAR) END
  ),
  e.begin_time = COALESCE(e.begin_time, wh.begin_time),
  e.end_time = COALESCE(e.end_time, wh.end_time),
  e.main_barcode = COALESCE(NULLIF(TRIM(e.main_barcode), ''), NULLIF(TRIM(wh.main_barcode), '')),
  e.sub_barcode = COALESCE(NULLIF(TRIM(e.sub_barcode), ''), NULLIF(TRIM(wh.sub_barcode), '')),
  e.material_name = COALESCE(NULLIF(TRIM(e.material_name), ''), NULLIF(TRIM(wh.material_name), '')),
  e.material_speci = COALESCE(NULLIF(TRIM(e.material_speci), ''), NULLIF(TRIM(wh.material_speci), '')),
  e.material_model = COALESCE(NULLIF(TRIM(e.material_model), ''), NULLIF(TRIM(wh.material_model), '')),
  e.material_factory_id = COALESCE(e.material_factory_id, wh.material_factory_id),
  e.stk_inventory_id = COALESCE(e.stk_inventory_id, wh.inv_id)
WHERE (e.del_flag IS NULL OR e.del_flag != 1)
  AND (b.del_flag IS NULL OR b.del_flag != 1)
  AND e.batch_no IS NOT NULL AND TRIM(e.batch_no) <> ''
  AND (@tenant_id IS NULL OR e.tenant_id = @tenant_id OR b.tenant_id = @tenant_id);

DROP TEMPORARY TABLE IF EXISTS tmp_tk401_wh_map;

-- -----------------------------------------------------------------------------
-- 7) 401 已审核但明细仍无 stk_inventory_id（范围更小）
-- -----------------------------------------------------------------------------
DROP TEMPORARY TABLE IF EXISTS tmp_tk401_wh_map2;
CREATE TEMPORARY TABLE tmp_tk401_wh_map2 (
  batch_no VARCHAR(100) NOT NULL,
  warehouse_id BIGINT NOT NULL,
  inv_id BIGINT NOT NULL,
  PRIMARY KEY (batch_no, warehouse_id)
) ENGINE=InnoDB;

INSERT INTO tmp_tk401_wh_map2 (batch_no, warehouse_id, inv_id)
SELECT x.batch_no, x.warehouse_id, w.id
FROM (
  SELECT w2.batch_no, w2.warehouse_id, MAX(w2.id) AS max_inv_id
  FROM stk_inventory w2
  INNER JOIN (
    SELECT DISTINCT TRIM(e.batch_no) AS batch_no, COALESCE(e.warehouse_id, b.warehouse_id) AS warehouse_id
    FROM stk_io_bill_entry e
    INNER JOIN stk_io_bill b ON b.id = e.paren_id AND b.bill_type = 401 AND b.bill_status = 2
    WHERE (e.del_flag IS NULL OR e.del_flag != 1)
      AND (b.del_flag IS NULL OR b.del_flag != 1)
      AND e.stk_inventory_id IS NULL
      AND e.batch_no IS NOT NULL AND TRIM(e.batch_no) <> ''
      AND COALESCE(e.warehouse_id, b.warehouse_id) IS NOT NULL
      AND (@tenant_id IS NULL OR e.tenant_id = @tenant_id OR b.tenant_id = @tenant_id)
  ) need ON need.batch_no = w2.batch_no AND need.warehouse_id = w2.warehouse_id
  WHERE (w2.del_flag IS NULL OR w2.del_flag = 0)
    AND (@tenant_id IS NULL OR w2.tenant_id = @tenant_id)
  GROUP BY w2.batch_no, w2.warehouse_id
) x
INNER JOIN stk_inventory w ON w.id = x.max_inv_id;

UPDATE stk_io_bill_entry e
INNER JOIN stk_io_bill b ON b.id = e.paren_id AND b.bill_type = 401 AND b.bill_status = 2
INNER JOIN tmp_tk401_wh_map2 wh ON wh.batch_no = TRIM(e.batch_no)
  AND wh.warehouse_id = COALESCE(e.warehouse_id, b.warehouse_id)
SET
  e.stk_inventory_id = wh.inv_id,
  e.kc_no = COALESCE(e.kc_no, e.dep_inventory_id)
WHERE (e.del_flag IS NULL OR e.del_flag != 1)
  AND e.stk_inventory_id IS NULL
  AND (@tenant_id IS NULL OR e.tenant_id = @tenant_id OR b.tenant_id = @tenant_id);

DROP TEMPORARY TABLE IF EXISTS tmp_tk401_wh_map2;

-- -----------------------------------------------------------------------------
-- 8) 执行后抽查
-- -----------------------------------------------------------------------------
SELECT dep.id, dep.batch_no, dep.material_name, dep.supplier_id, dep.warehouse_id, dep.bill_no, dep.remark,
  se.supplier_id AS ste_supplier_id, se.supplier_id_str, m.supplier_id AS mat_supplier_id, bat.supplier_id AS batch_supplier_id
FROM stk_dep_inventory dep
LEFT JOIN stk_io_stocktaking_entry se ON CAST(TRIM(se.dep_inventory_id) AS UNSIGNED) = dep.id
LEFT JOIN fd_material m ON m.id = dep.material_id
LEFT JOIN stk_batch bat ON bat.id = dep.batch_id
WHERE (dep.del_flag IS NULL OR dep.del_flag = 0)
  AND (@tenant_id IS NULL OR dep.tenant_id = @tenant_id)
  AND (dep.material_name IS NULL OR TRIM(dep.material_name) = '' OR dep.supplier_id IS NULL OR TRIM(dep.supplier_id) = '')
LIMIT 50;

SELECT e.id, e.bill_no, e.dep_inventory_id, e.warehouse_id, e.suppler_id, e.stk_inventory_id, e.material_name
FROM stk_io_bill_entry e
INNER JOIN stk_io_bill b ON b.id = e.paren_id AND b.bill_type = 401
WHERE (e.del_flag IS NULL OR e.del_flag != 1)
  AND (@tenant_id IS NULL OR e.tenant_id = @tenant_id)
  AND (e.dep_inventory_id IS NULL OR e.suppler_id IS NULL OR e.suppler_id = '' OR e.material_name IS NULL OR e.material_name = '')
LIMIT 50;

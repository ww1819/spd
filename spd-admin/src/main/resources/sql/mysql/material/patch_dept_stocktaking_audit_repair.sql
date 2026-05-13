-- =============================================================================
-- 科室盘点：① 已审核单中「实盘 vs 当前科室库存」不一致明细查询
--           ② 将科室库存数量更正为盘点明细上的实盘数量（stock_qty）
--           ③ 已审核低值科室盘点补 t_hc_ks_flow（按 bill_id+entry_id+lx 去重）
--
-- 使用前请：备份库；在测试环境验证；按需替换 @tenant_id / 注释中的筛选条件。
-- MySQL 8+ 推荐；若 dep_inventory_id 含非数字，JOIN 会自然排除该行。
-- =============================================================================

-- 可选：限定租户（整段脚本可包在 SET + 条件里）
-- SET @tenant_id = 'hengsui-third-001';

/* ---------------------------------------------------------------------------
   1) 查询：已审核科室盘点单中，实盘数量 stock_qty 与当前 stk_dep_inventory.qty 不一致的明细
      科室盘点口径：主表 warehouse_id 为空、department_id 非空；已审核 stock_status=2
--------------------------------------------------------------------------- */
SELECT
    st.tenant_id,
    st.id AS stocktaking_id,
    st.stock_no,
    st.stock_type,
    st.audit_adjusts_inventory,
    st.audit_date,
    st.department_id,
    e.id AS entry_id,
    e.material_id,
    e.batch_no,
    e.dep_inventory_id,
    e.qty AS entry_book_qty_snapshot,
    e.stock_qty AS entry_actual_qty,
    di.id AS dep_inventory_pk,
    di.qty AS dep_current_qty,
    (IFNULL(e.stock_qty, 0) - IFNULL(di.qty, 0)) AS diff_stock_minus_dep
FROM stk_io_stocktaking st
JOIN stk_io_stocktaking_entry e
    ON e.paren_id = st.id
   AND (e.del_flag IS NULL OR e.del_flag != 1)
JOIN stk_dep_inventory di
    ON di.id = CAST(TRIM(e.dep_inventory_id) AS UNSIGNED)
   AND (di.del_flag IS NULL OR di.del_flag = 0)
LEFT JOIN fd_material m ON m.id = e.material_id AND m.tenant_id = st.tenant_id
WHERE (st.del_flag IS NULL OR st.del_flag != 1)
  AND st.stock_status = 2
  AND (st.warehouse_id IS NULL OR st.warehouse_id = 0)
  AND st.department_id IS NOT NULL
  AND e.dep_inventory_id IS NOT NULL
  AND TRIM(e.dep_inventory_id) <> ''
  AND TRIM(e.dep_inventory_id) REGEXP '^[0-9]+$'
  -- 低值耗材（高值 is_gz 常为 '1'；按库内实际字典调整）
  AND IFNULL(m.is_gz, '0') NOT IN ('1', 'Y', 'y')
  -- 实盘与当前科室库存行数量不一致（忽略极小浮点差可用下面一行替代）
  AND (IFNULL(e.stock_qty, 0) <> IFNULL(di.qty, 0))
-- AND st.tenant_id = @tenant_id
ORDER BY st.audit_date DESC, st.id, e.id;


/* ---------------------------------------------------------------------------
   2) 更正库存：将 stk_dep_inventory.qty 更新为盘点明细上的 stock_qty，
      并重算 amt = qty * unit_price（优先用库存行单价，否则用明细单价/价格）
      ⚠ 仅应在确认「以盘点实盘为唯一真相」且业务允许直接改库存时执行。
--------------------------------------------------------------------------- */
/*
UPDATE stk_dep_inventory di
JOIN stk_io_stocktaking_entry e
  ON di.id = CAST(TRIM(e.dep_inventory_id) AS UNSIGNED)
JOIN stk_io_stocktaking st ON st.id = e.paren_id
LEFT JOIN fd_material m ON m.id = e.material_id AND m.tenant_id = st.tenant_id
SET
  di.qty = IFNULL(e.stock_qty, di.qty),
  di.amt = ROUND(
    IFNULL(e.stock_qty, di.qty) * COALESCE(NULLIF(di.unit_price, 0), e.unit_price, e.price, 0),
    6
  ),
  di.unit_price = COALESCE(NULLIF(di.unit_price, 0), e.unit_price, e.price, di.unit_price),
  di.update_time = NOW(),
  di.update_by = 'patch_dept_stocktaking_audit_repair'
WHERE (st.del_flag IS NULL OR st.del_flag != 1)
  AND (e.del_flag IS NULL OR e.del_flag != 1)
  AND (di.del_flag IS NULL OR di.del_flag = 0)
  AND st.stock_status = 2
  AND (st.warehouse_id IS NULL OR st.warehouse_id = 0)
  AND st.department_id IS NOT NULL
  AND e.dep_inventory_id IS NOT NULL
  AND TRIM(e.dep_inventory_id) REGEXP '^[0-9]+$'
  AND IFNULL(m.is_gz, '0') NOT IN ('1', 'Y', 'y')
  AND (IFNULL(e.stock_qty, 0) <> IFNULL(di.qty, 0))
-- AND st.tenant_id = @tenant_id
;
*/


/* ---------------------------------------------------------------------------
   3) 补全 t_hc_ks_flow（低值、已审核科室盘点、审核直改库存），避免重复：
      去重：同一 bill_id + entry_id 下若已存在科室盘点类 PK/PY 流水（origin 以「科室盘点」开头
            或常见四种文案之一）则不再插入。
      数量口径：stock_type=502 时用 (stock_qty - qty) 决定 PK/PY，流水 qty 取绝对值；持平不插。
      说明：代码里「科室盘点盘盈入库」仅出现在审核前无科室库存行、新插 stk_dep_inventory 的分支，
            SQL 无法可靠区分「新行」与「账面为 0 的旧行」，故统一补「科室盘点盘盈/盘亏」；
            若个别单必须用「盘盈入库」文案，请手工改 origin_business_type 或单独名单处理。
--------------------------------------------------------------------------- */
INSERT INTO t_hc_ks_flow (
  bill_id, entry_id, department_id, warehouse_id, material_id,
  batch_no, batch_number, batch_id, qty, unit_price, amt,
  begin_time, end_time, supplier_id, factory_id,
  kc_no, lx, flow_time, origin_business_type, del_flag,
  tenant_id, create_by, create_time,
  bill_no, bill_id_str, entry_id_str, material_id_str, kc_no_str, batch_id_str, factory_id_str,
  warehouse_id_str, department_id_str, material_code, material_name, supplier_name
)
SELECT
  st.id,
  e.id,
  st.department_id,
  di.warehouse_id,
  e.material_id,
  e.batch_no,
  e.batch_number,
  di.batch_id,
  ABS(IFNULL(e.stock_qty, 0) - IFNULL(e.qty, 0)) AS qty,
  COALESCE(NULLIF(e.unit_price, 0), e.price, di.unit_price, 0) AS unit_price,
  ROUND(
    ABS(IFNULL(e.stock_qty, 0) - IFNULL(e.qty, 0))
    * COALESCE(NULLIF(e.unit_price, 0), e.price, di.unit_price, 0),
    2
  ) AS amt,
  e.begin_time,
  e.end_time,
  NULLIF(TRIM(di.supplier_id), ''),
  di.factory_id,
  di.id AS kc_no,
  CASE
    WHEN IFNULL(e.stock_qty, 0) > IFNULL(e.qty, 0) THEN 'PY'
    WHEN IFNULL(e.stock_qty, 0) < IFNULL(e.qty, 0) THEN 'PK'
    ELSE NULL
  END AS lx,
  COALESCE(st.audit_date, st.update_time, NOW()) AS flow_time,
  CASE
    WHEN IFNULL(e.stock_qty, 0) > IFNULL(e.qty, 0) THEN '科室盘点盘盈'
    WHEN IFNULL(e.stock_qty, 0) < IFNULL(e.qty, 0) THEN '科室盘点盘亏'
    ELSE NULL
  END AS origin_business_type,
  0 AS del_flag,
  st.tenant_id,
  'patch_dept_stocktaking_flow' AS create_by,
  NOW() AS create_time,
  st.stock_no,
  CAST(st.id AS CHAR),
  CAST(e.id AS CHAR),
  CAST(e.material_id AS CHAR),
  CAST(di.id AS CHAR),
  CAST(di.batch_id AS CHAR),
  CAST(di.factory_id AS CHAR),
  CAST(di.warehouse_id AS CHAR),
  CAST(st.department_id AS CHAR),
  m.code,
  m.name,
  s.name
FROM stk_io_stocktaking st
JOIN stk_io_stocktaking_entry e
  ON e.paren_id = st.id
 AND (e.del_flag IS NULL OR e.del_flag != 1)
JOIN stk_dep_inventory di
  ON di.id = CAST(TRIM(e.dep_inventory_id) AS UNSIGNED)
 AND (di.del_flag IS NULL OR di.del_flag = 0)
LEFT JOIN fd_material m ON m.id = e.material_id AND m.tenant_id = st.tenant_id
LEFT JOIN fd_supplier s ON s.id = m.supplier_id
WHERE (st.del_flag IS NULL OR st.del_flag != 1)
  AND st.stock_status = 2
  AND IFNULL(st.audit_adjusts_inventory, 0) = 1
  AND (st.warehouse_id IS NULL OR st.warehouse_id = 0)
  AND st.department_id IS NOT NULL
  AND st.stock_type = 502
  AND e.dep_inventory_id IS NOT NULL
  AND TRIM(e.dep_inventory_id) REGEXP '^[0-9]+$'
  AND IFNULL(m.is_gz, '0') NOT IN ('1', 'Y', 'y')
  -- 仅补「应有流水」的盈亏行（与线上一致：持不平不写）
  AND IFNULL(e.stock_qty, 0) <> IFNULL(e.qty, 0)
  AND NOT EXISTS (
    SELECT 1 FROM t_hc_ks_flow f
    WHERE f.del_flag = 0
      AND f.tenant_id = st.tenant_id
      AND f.bill_id = st.id
      AND f.entry_id = e.id
      AND f.lx IN ('PK', 'PY')
      AND (
        f.origin_business_type LIKE '科室盘点%'
        OR f.origin_business_type IN ('科室盘点盘盈', '科室盘点盘亏', '科室盘点盘盈入库', '科室盘点期初')
      )
  );

-- 期初类型 501：若需与线上一致补「科室盘点期初」流水，可单独执行（同样去重）
INSERT INTO t_hc_ks_flow (
  bill_id, entry_id, department_id, warehouse_id, material_id,
  batch_no, batch_number, batch_id, qty, unit_price, amt,
  begin_time, end_time, supplier_id, factory_id,
  kc_no, lx, flow_time, origin_business_type, del_flag,
  tenant_id, create_by, create_time,
  bill_no, bill_id_str, entry_id_str, material_id_str, kc_no_str, batch_id_str, factory_id_str,
  warehouse_id_str, department_id_str, material_code, material_name, supplier_name
)
SELECT
  st.id,
  e.id,
  st.department_id,
  di.warehouse_id,
  e.material_id,
  e.batch_no,
  e.batch_number,
  di.batch_id,
  IFNULL(e.qty, 0) AS qty,
  COALESCE(NULLIF(e.unit_price, 0), e.price, di.unit_price, 0) AS unit_price,
  ROUND(IFNULL(e.qty, 0) * COALESCE(NULLIF(e.unit_price, 0), e.price, di.unit_price, 0), 2) AS amt,
  e.begin_time,
  e.end_time,
  NULLIF(TRIM(di.supplier_id), ''),
  di.factory_id,
  di.id,
  'PY',
  COALESCE(st.audit_date, st.update_time, NOW()),
  '科室盘点期初',
  0,
  st.tenant_id,
  'patch_dept_stocktaking_flow',
  NOW(),
  st.stock_no,
  CAST(st.id AS CHAR),
  CAST(e.id AS CHAR),
  CAST(e.material_id AS CHAR),
  CAST(di.id AS CHAR),
  CAST(di.batch_id AS CHAR),
  CAST(di.factory_id AS CHAR),
  CAST(di.warehouse_id AS CHAR),
  CAST(st.department_id AS CHAR),
  m.code,
  m.name,
  s.name
FROM stk_io_stocktaking st
JOIN stk_io_stocktaking_entry e ON e.paren_id = st.id AND (e.del_flag IS NULL OR e.del_flag != 1)
JOIN stk_dep_inventory di ON di.id = CAST(TRIM(e.dep_inventory_id) AS UNSIGNED) AND (di.del_flag IS NULL OR di.del_flag = 0)
LEFT JOIN fd_material m ON m.id = e.material_id AND m.tenant_id = st.tenant_id
LEFT JOIN fd_supplier s ON s.id = m.supplier_id
WHERE (st.del_flag IS NULL OR st.del_flag != 1)
  AND st.stock_status = 2
  AND IFNULL(st.audit_adjusts_inventory, 0) = 1
  AND (st.warehouse_id IS NULL OR st.warehouse_id = 0)
  AND st.department_id IS NOT NULL
  AND st.stock_type = 501
  AND IFNULL(e.qty, 0) > 0
  AND IFNULL(m.is_gz, '0') NOT IN ('1', 'Y', 'y')
  AND NOT EXISTS (
    SELECT 1 FROM t_hc_ks_flow f
    WHERE f.del_flag = 0 AND f.tenant_id = st.tenant_id
      AND f.bill_id = st.id AND f.entry_id = e.id
      AND f.lx = 'PY' AND f.origin_business_type = '科室盘点期初'
  );


/* ---------------------------------------------------------------------------
   4) 检查：已审核科室盘点单内，多条明细重复引用同一「科室库存 id」(dep_inventory_id)
      口径：stock_status=2；科室盘点（warehouse_id 空、department_id 非空）；有效明细
--------------------------------------------------------------------------- */
-- 4a 汇总：每张单上重复的科室库存 id 及出现次数（内层先 TRIM，外层 GROUP BY 仅用列别名，兼容 ONLY_FULL_GROUP_BY）
SELECT
    x.tenant_id,
    x.stocktaking_id,
    x.stock_no,
    x.stock_type,
    x.department_id,
    x.dep_inventory_id,
    x.entry_line_cnt
FROM (
    SELECT
        r.tenant_id,
        r.stocktaking_id,
        r.stock_no,
        r.stock_type,
        r.department_id,
        r.dep_inventory_id,
        COUNT(*) AS entry_line_cnt
    FROM (
        SELECT
            st.tenant_id,
            st.id AS stocktaking_id,
            st.stock_no,
            st.stock_type,
            st.department_id,
            TRIM(e.dep_inventory_id) AS dep_inventory_id
        FROM stk_io_stocktaking st
        JOIN stk_io_stocktaking_entry e
            ON e.paren_id = st.id
           AND (e.del_flag IS NULL OR e.del_flag != 1)
        WHERE (st.del_flag IS NULL OR st.del_flag != 1)
          AND st.stock_status = 2
          AND (st.warehouse_id IS NULL OR st.warehouse_id = 0)
          AND st.department_id IS NOT NULL
          AND e.dep_inventory_id IS NOT NULL
          AND TRIM(e.dep_inventory_id) <> ''
    ) r
    GROUP BY r.tenant_id, r.stocktaking_id, r.stock_no, r.stock_type, r.department_id, r.dep_inventory_id
) x
WHERE x.entry_line_cnt > 1
ORDER BY x.tenant_id, x.stocktaking_id, x.dep_inventory_id;

-- 4b 明细：展开重复行（派生表先按行 TRIM，再分组计数，避免 GROUP BY 中含 TRIM(列) 触发 1055）
SELECT
    st.tenant_id,
    st.id AS stocktaking_id,
    st.stock_no,
    e.id AS entry_id,
    e.material_id,
    e.batch_no,
    TRIM(e.dep_inventory_id) AS dep_inventory_id,
    e.qty,
    e.stock_qty
FROM stk_io_stocktaking st
JOIN stk_io_stocktaking_entry e
    ON e.paren_id = st.id
   AND (e.del_flag IS NULL OR e.del_flag != 1)
JOIN (
    SELECT
        r.tenant_id,
        r.stocktaking_id,
        r.dep_id_norm,
        COUNT(*) AS entry_line_cnt
    FROM (
        SELECT
            st2.tenant_id,
            st2.id AS stocktaking_id,
            TRIM(e2.dep_inventory_id) AS dep_id_norm
        FROM stk_io_stocktaking st2
        JOIN stk_io_stocktaking_entry e2
            ON e2.paren_id = st2.id
           AND (e2.del_flag IS NULL OR e2.del_flag != 1)
        WHERE (st2.del_flag IS NULL OR st2.del_flag != 1)
          AND st2.stock_status = 2
          AND (st2.warehouse_id IS NULL OR st2.warehouse_id = 0)
          AND st2.department_id IS NOT NULL
          AND e2.dep_inventory_id IS NOT NULL
          AND TRIM(e2.dep_inventory_id) <> ''
    ) r
    GROUP BY r.tenant_id, r.stocktaking_id, r.dep_id_norm
) dup
  ON (dup.tenant_id <=> st.tenant_id)
 AND dup.stocktaking_id = st.id
 AND dup.dep_id_norm = TRIM(e.dep_inventory_id)
 AND dup.entry_line_cnt > 1
WHERE (st.del_flag IS NULL OR st.del_flag != 1)
  AND st.stock_status = 2
  AND (st.warehouse_id IS NULL OR st.warehouse_id = 0)
  AND st.department_id IS NOT NULL
  AND e.dep_inventory_id IS NOT NULL
  AND TRIM(e.dep_inventory_id) <> ''
ORDER BY st.tenant_id, st.id, dep_inventory_id, e.id;


-- ========== 库存进销存全明细 view_stock_all_detail_jxc 说明 ==========
-- 构造方式：仅由「已审核主单 INNER JOIN 对应明细表」各段 UNION ALL，一行 = 一条明细；不含流水表、不含库存表，便于与 t_hc_ck_flow 做单据侧/流水侧双重验证。
-- 与流水核对键：
--   · STK_IO_BILL：bill_id=bill_id，bill_entry_id=bill_entry_id，对应 t_hc_ck_flow.bill_id / entry_id（出入库/调拨 DB 段等同理）。
--   · STK_IO_PROFIT_LOSS：bill_id / bill_entry_id 对应盈亏主键与明细主键，对应流水同名字段。
--   · STK_INITIAL_IMPORT：主单/明细为 UUID 字符串，用 ref_bill_id、ref_entry_id 对应 t_hc_ck_flow.ref_bill_id / ref_entry_id（lx=QC）。
-- 1) 盈亏单 UNION 已补充：仅统计仓库盈亏（biz_scope 为空或 WH），排除科室盈亏（DEP），避免与仓库 stk_inventory 核对时混入科室数据。
-- 2) 本视图不按 fd_material.is_gz 过滤；低值仓库维度汇总请用 view_lv_wh_stock_detail_from_jxc 或流水视图 view_lv_wh_stock_flow_detail。
-- 3) bill_type=501 且单号 DB 开头段落同时承载「仓库调拨」与部分结算/期初类业务，与首段 101/201/301/401 并存；若与实物账不符，请以 t_hc_ck_flow 为准交叉核对。
-- 4) 科室批量消耗、科室申领出库、科室盈亏（低值科室）等更多影响科室库存的事件见 t_hc_ks_flow / view_lv_dep_stock_flow_detail。
-- ===================================================================

CREATE
OR REPLACE ALGORITHM = UNDEFINED VIEW view_stock_all_detail_jxc AS
SELECT CAST(sib.id AS CHAR) AS mid,
       CAST(sibe.id AS CHAR) AS mxId,
       sib.bill_no AS bill_no,
       sib.warehouse_id AS warehouse_id,
       fw.code AS warehouse_code,
       fw.name AS warehouse_name,
       sib.audit_date AS audit_date,
       sib.bill_type AS bill_type,
       (CASE
            WHEN sib.bill_type IN (101, 301) THEN sib.suppler_id
            WHEN sib.bill_type IN (201, 401) THEN sibe.suppler_id
            END) AS suppler_id,
       (CASE
            WHEN sib.bill_type IN (101, 301) THEN fs.name
            WHEN sib.bill_type IN (201, 401) THEN fs2.name
            END) AS suppler_name,
       (CASE
            WHEN sib.bill_type IN (101, 301) THEN 'RK'
            WHEN sib.bill_type IN (201, 401) THEN 'CK'
            END) AS io_type,
       sibe.unit_price AS unit_price,
       (CASE
            WHEN sib.bill_type IN (101, 201) THEN sibe.qty
            WHEN sib.bill_type IN (301, 401) THEN -sibe.qty
            END) AS io_qty,
       (CASE
            WHEN sib.bill_type IN (101, 201) THEN sibe.qty * sibe.unit_price
            WHEN sib.bill_type IN (301, 401) THEN -sibe.qty * sibe.unit_price
            END) AS io_amt,
       (CASE
            WHEN sib.bill_type IN (101, 401) THEN sibe.qty
            WHEN sib.bill_type IN (201, 301) THEN -sibe.qty
            END) AS jc_qty,
       (CASE
            WHEN sib.bill_type IN (101, 401) THEN sibe.qty * sibe.unit_price
            WHEN sib.bill_type IN (201, 301) THEN -sibe.qty * sibe.unit_price
            END) AS jc_amt,
       sibe.material_id AS material_id,
       fm.code AS material_code,
       fm.name AS material_name,
       fm.speci AS speci,
       fm.model AS model,
       fm.unit_id AS unit_id,
       fu.unit_name AS unit_name,
       fm.factory_id AS factory_id,
       ff.factory_code AS factory_code,
       ff.factory_name AS factory_name,
       sibe.batch_number AS batch_number,
       sibe.batch_no AS batch_no,
       sibe.begin_time AS begin_time,
       sibe.end_time AS end_time,
       fm.storeroom_id AS storeroom_id,
       fwc.warehouse_category_code AS warehouse_category_code,
       fwc.warehouse_category_name AS warehouse_category_name,
       fm.finance_category_id AS finance_category_id,
       ffc.finance_category_code AS finance_category_code,
       ffc.finance_category_name AS finance_category_name,
       sib.tenant_id AS tenant_id,
       'STK_IO_BILL' AS bill_domain,
       sib.id AS bill_id,
       sibe.id AS bill_entry_id,
       CAST(NULL AS CHAR(64)) AS ref_bill_id,
       CAST(NULL AS CHAR(64)) AS ref_entry_id
FROM stk_io_bill sib
         INNER JOIN stk_io_bill_entry sibe ON sib.id = sibe.paren_id AND IFNULL(sibe.del_flag, 0) = 0
         LEFT JOIN fd_warehouse fw ON sib.warehouse_id = fw.id
         LEFT JOIN fd_material fm ON sibe.material_id = fm.id
         LEFT JOIN fd_warehouse_category fwc ON fm.storeroom_id = fwc.warehouse_category_id
         LEFT JOIN fd_finance_category ffc ON fm.finance_category_id = ffc.finance_category_id
         LEFT JOIN fd_supplier fs ON sib.suppler_id = fs.id
         LEFT JOIN fd_supplier fs2 ON sibe.suppler_id = fs2.id
         LEFT JOIN fd_unit fu ON fm.unit_id = fu.unit_id
         LEFT JOIN fd_factory ff ON fm.factory_id = ff.factory_id
WHERE IFNULL(sib.del_flag, 0) = 0
  AND sib.bill_status = 2
  AND sib.bill_type IN (101, 201, 301, 401)
UNION ALL
SELECT CAST(sib.id AS CHAR) AS mid,
       CAST(sibe.id AS CHAR) AS mxId,
       sib.bill_no AS bill_no,
       sib.warehouse_id AS warehouse_id,
       fw.code AS warehouse_code,
       fw.name AS warehouse_name,
       sib.audit_date AS audit_date,
       501 AS bill_type,
       sibe.suppler_id AS suppler_id,
       fs.name AS suppler_name,
       'DBC' AS io_type,
       sibe.unit_price AS unit_price,
       sibe.qty AS io_qty,
       sibe.qty * sibe.unit_price AS io_amt,
       -sibe.qty AS jc_qty,
       -sibe.qty * sibe.unit_price AS jc_amt,
       sibe.material_id AS material_id,
       fm.code AS material_code,
       fm.name AS material_name,
       fm.speci AS speci,
       fm.model AS model,
       fm.unit_id AS unit_id,
       fu.unit_name AS unit_name,
       fm.factory_id AS factory_id,
       ff.factory_code AS factory_code,
       ff.factory_name AS factory_name,
       sibe.batch_number AS batch_number,
       sibe.batch_no AS batch_no,
       sibe.begin_time AS begin_time,
       sibe.end_time AS end_time,
       fm.storeroom_id AS storeroom_id,
       fwc.warehouse_category_code AS warehouse_category_code,
       fwc.warehouse_category_name AS warehouse_category_name,
       fm.finance_category_id AS finance_category_id,
       ffc.finance_category_code AS finance_category_code,
       ffc.finance_category_name AS finance_category_name,
       sib.tenant_id AS tenant_id,
       'STK_IO_BILL' AS bill_domain,
       sib.id AS bill_id,
       sibe.id AS bill_entry_id,
       CAST(NULL AS CHAR(64)) AS ref_bill_id,
       CAST(NULL AS CHAR(64)) AS ref_entry_id
FROM stk_io_bill sib
         INNER JOIN stk_io_bill_entry sibe ON sib.id = sibe.paren_id AND IFNULL(sibe.del_flag, 0) = 0
         LEFT JOIN fd_warehouse fw ON sib.warehouse_id = fw.id
         LEFT JOIN fd_material fm ON sibe.material_id = fm.id
         LEFT JOIN fd_warehouse_category fwc ON fm.storeroom_id = fwc.warehouse_category_id
         LEFT JOIN fd_finance_category ffc ON fm.finance_category_id = ffc.finance_category_id
         LEFT JOIN fd_supplier fs ON sibe.suppler_id = fs.id
         LEFT JOIN fd_unit fu ON fm.unit_id = fu.unit_id
         LEFT JOIN fd_factory ff ON fm.factory_id = ff.factory_id
WHERE IFNULL(sib.del_flag, 0) = 0
  AND sib.bill_status = 2
  AND sib.bill_type = 501
  AND LEFT(sib.bill_no, 2) = 'DB'
UNION ALL
SELECT CAST(sib.id AS CHAR) AS mid,
       CAST(sibe.id AS CHAR) AS mxId,
       sib.bill_no AS bill_no,
       sib.department_id AS warehouse_id,
       fw.code AS warehouse_code,
       fw.name AS warehouse_name,
       sib.audit_date AS audit_date,
       501 AS bill_type,
       sibe.suppler_id AS suppler_id,
       fs.name AS suppler_name,
       'DBR' AS io_type,
       sibe.unit_price AS unit_price,
       sibe.qty AS io_qty,
       sibe.qty * sibe.unit_price AS io_amt,
       sibe.qty AS jc_qty,
       sibe.qty * sibe.unit_price AS jc_amt,
       sibe.material_id AS material_id,
       fm.code AS material_code,
       fm.name AS material_name,
       fm.speci AS speci,
       fm.model AS model,
       fm.unit_id AS unit_id,
       fu.unit_name AS unit_name,
       fm.factory_id AS factory_id,
       ff.factory_code AS factory_code,
       ff.factory_name AS factory_name,
       sibe.batch_number AS batch_number,
       sibe.batch_no AS batch_no,
       sibe.begin_time AS begin_time,
       sibe.end_time AS end_time,
       fm.storeroom_id AS storeroom_id,
       fwc.warehouse_category_code AS warehouse_category_code,
       fwc.warehouse_category_name AS warehouse_category_name,
       fm.finance_category_id AS finance_category_id,
       ffc.finance_category_code AS finance_category_code,
       ffc.finance_category_name AS finance_category_name,
       sib.tenant_id AS tenant_id,
       'STK_IO_BILL' AS bill_domain,
       sib.id AS bill_id,
       sibe.id AS bill_entry_id,
       CAST(NULL AS CHAR(64)) AS ref_bill_id,
       CAST(NULL AS CHAR(64)) AS ref_entry_id
FROM stk_io_bill sib
         INNER JOIN stk_io_bill_entry sibe ON sib.id = sibe.paren_id AND IFNULL(sibe.del_flag, 0) = 0
         LEFT JOIN fd_warehouse fw ON sib.department_id = fw.id
         LEFT JOIN fd_material fm ON sibe.material_id = fm.id
         LEFT JOIN fd_warehouse_category fwc ON fm.storeroom_id = fwc.warehouse_category_id
         LEFT JOIN fd_finance_category ffc ON fm.finance_category_id = ffc.finance_category_id
         LEFT JOIN fd_supplier fs ON sibe.suppler_id = fs.id
         LEFT JOIN fd_unit fu ON fm.unit_id = fu.unit_id
         LEFT JOIN fd_factory ff ON fm.factory_id = ff.factory_id
WHERE IFNULL(sib.del_flag, 0) = 0
  AND sib.bill_status = 2
  AND sib.bill_type = 501
  AND LEFT(sib.bill_no, 2) = 'DB'
UNION ALL
SELECT CAST(sipl.id AS CHAR) AS mid,
       CAST(siple.id AS CHAR) AS mxId,
       sipl.bill_no AS bill_no,
       sipl.warehouse_id AS warehouse_id,
       fw.code AS warehouse_code,
       fw.name AS warehouse_name,
       sipl.audit_date AS audit_date,
       601 AS bill_type,
       siple.suppler_id AS suppler_id,
       fs.name AS suppler_name,
       'PD' AS io_type,
       siple.unit_price AS unit_price,
       IFNULL(siple.profit_qty, 0) AS io_qty,
       COALESCE(siple.profit_amount, IFNULL(siple.profit_qty, 0) * IFNULL(siple.unit_price, 0)) AS io_amt,
       IFNULL(siple.profit_qty, 0) AS jc_qty,
       COALESCE(siple.profit_amount, IFNULL(siple.profit_qty, 0) * IFNULL(siple.unit_price, 0)) AS jc_amt,
       siple.material_id AS material_id,
       fm.code AS material_code,
       fm.name AS material_name,
       fm.speci AS speci,
       fm.model AS model,
       fm.unit_id AS unit_id,
       fu.unit_name AS unit_name,
       fm.factory_id AS factory_id,
       ff.factory_code AS factory_code,
       ff.factory_name AS factory_name,
       siple.batch_number AS batch_number,
       siple.batch_no AS batch_no,
       siple.begin_time AS begin_time,
       siple.end_time AS end_time,
       fm.storeroom_id AS storeroom_id,
       fwc.warehouse_category_code AS warehouse_category_code,
       fwc.warehouse_category_name AS warehouse_category_name,
       fm.finance_category_id AS finance_category_id,
       ffc.finance_category_code AS finance_category_code,
       ffc.finance_category_name AS finance_category_name,
       sipl.tenant_id AS tenant_id,
       'STK_IO_PROFIT_LOSS' AS bill_domain,
       sipl.id AS bill_id,
       siple.id AS bill_entry_id,
       CAST(NULL AS CHAR(64)) AS ref_bill_id,
       CAST(NULL AS CHAR(64)) AS ref_entry_id
FROM stk_io_profit_loss sipl
         INNER JOIN stk_io_profit_loss_entry siple ON sipl.id = siple.paren_id AND IFNULL(siple.del_flag, 0) = 0
         LEFT JOIN fd_warehouse fw ON sipl.warehouse_id = fw.id
         LEFT JOIN fd_material fm ON siple.material_id = fm.id
         LEFT JOIN fd_warehouse_category fwc ON fm.storeroom_id = fwc.warehouse_category_id
         LEFT JOIN fd_finance_category ffc ON fm.finance_category_id = ffc.finance_category_id
         LEFT JOIN fd_supplier fs ON siple.suppler_id = fs.id
         LEFT JOIN fd_unit fu ON fm.unit_id = fu.unit_id
         LEFT JOIN fd_factory ff ON fm.factory_id = ff.factory_id
WHERE IFNULL(sipl.del_flag, 0) = 0
  AND sipl.bill_status = 2
  AND (sipl.biz_scope IS NULL OR sipl.biz_scope = '' OR sipl.biz_scope = 'WH')
UNION ALL
SELECT sii.id AS mid,
       sie.id AS mxId,
       sii.bill_no AS bill_no,
       sie.warehouse_id AS warehouse_id,
       fw.code AS warehouse_code,
       fw.name AS warehouse_name,
       COALESCE(sii.audit_time, sii.stock_gen_time, sii.create_time) AS audit_date,
       701 AS bill_type,
       sie.supplier_id AS suppler_id,
       fs.name AS suppler_name,
       'QC' AS io_type,
       sie.unit_price AS unit_price,
       sie.qty AS io_qty,
       COALESCE(sie.amt, sie.qty * IFNULL(sie.unit_price, 0)) AS io_amt,
       sie.qty AS jc_qty,
       COALESCE(sie.amt, sie.qty * IFNULL(sie.unit_price, 0)) AS jc_amt,
       sie.material_id AS material_id,
       fm.code AS material_code,
       fm.name AS material_name,
       fm.speci AS speci,
       fm.model AS model,
       fm.unit_id AS unit_id,
       fu.unit_name AS unit_name,
       fm.factory_id AS factory_id,
       ff.factory_code AS factory_code,
       ff.factory_name AS factory_name,
       sie.batch_number AS batch_number,
       sie.batch_no AS batch_no,
       sie.begin_time AS begin_time,
       sie.end_time AS end_time,
       fm.storeroom_id AS storeroom_id,
       fwc.warehouse_category_code AS warehouse_category_code,
       fwc.warehouse_category_name AS warehouse_category_name,
       fm.finance_category_id AS finance_category_id,
       ffc.finance_category_code AS finance_category_code,
       ffc.finance_category_name AS finance_category_name,
       sii.tenant_id AS tenant_id,
       'STK_INITIAL_IMPORT' AS bill_domain,
       CAST(NULL AS UNSIGNED) AS bill_id,
       CAST(NULL AS UNSIGNED) AS bill_entry_id,
       sii.id AS ref_bill_id,
       sie.id AS ref_entry_id
FROM stk_initial_import sii
         INNER JOIN stk_initial_import_entry sie ON sii.id = sie.paren_id AND IFNULL(sie.del_flag, 0) = 0
         LEFT JOIN fd_warehouse fw ON sie.warehouse_id = fw.id
         LEFT JOIN fd_material fm ON sie.material_id = fm.id
         LEFT JOIN fd_warehouse_category fwc ON fm.storeroom_id = fwc.warehouse_category_id
         LEFT JOIN fd_finance_category ffc ON fm.finance_category_id = ffc.finance_category_id
         LEFT JOIN fd_supplier fs ON sie.supplier_id = fs.id
         LEFT JOIN fd_unit fu ON fm.unit_id = fu.unit_id
         LEFT JOIN fd_factory ff ON fm.factory_id = ff.factory_id
WHERE IFNULL(sii.del_flag, 0) = 0
  AND sii.bill_status = 1;

-- ========== 低值耗材：仓库流水明细（与 stk_inventory 变动同源：t_hc_ck_flow）==========
-- qty_signed_wh：按 lx 汇总后应与仓库维度结存变动一致（RK/ZR/PY/QC 增；CK/TH/TK/ZC/PK 减；KSZR 增；KSZC 减）。
-- 【覆盖边界·非流水直改库存】① 科室盘点审核时 ensureWarehouseQtyZeroInventory 可能 insert/update stk_inventory（多为 qty=0 占位）未必有 ck_flow。
-- ② 库存档案手工维护（StkInventoryController / StkInventoryServiceImpl）无 ck_flow。③ 若存在独立采购审核入口仅改库存未写流水，亦不在此视图（主流程多为 StkIoBillServiceImpl 审核并写 ck_flow）。
CREATE OR REPLACE ALGORITHM = UNDEFINED VIEW view_lv_wh_stock_flow_detail AS
SELECT 'HC_CK' AS src,
       f.id AS flow_id,
       f.tenant_id,
       f.warehouse_id,
       w.code AS warehouse_code,
       w.name AS warehouse_name,
       f.material_id,
       m.code AS material_code,
       m.name AS material_name,
       m.speci AS material_speci,
       m.model AS material_model,
       m.is_gz AS material_is_gz,
       f.batch_no,
       f.batch_number,
       f.lx,
       f.qty AS qty_raw,
       (CASE f.lx
            WHEN 'RK' THEN IFNULL(f.qty, 0)
            WHEN 'ZR' THEN IFNULL(f.qty, 0)
            WHEN 'PY' THEN IFNULL(f.qty, 0)
            WHEN 'QC' THEN IFNULL(f.qty, 0)
            WHEN 'KSZR' THEN IFNULL(f.qty, 0)
            WHEN 'CK' THEN -IFNULL(f.qty, 0)
            WHEN 'TH' THEN -IFNULL(f.qty, 0)
            WHEN 'TK' THEN -IFNULL(f.qty, 0)
            WHEN 'ZC' THEN -IFNULL(f.qty, 0)
            WHEN 'PK' THEN -IFNULL(f.qty, 0)
            WHEN 'KSZC' THEN -IFNULL(f.qty, 0)
            ELSE 0
        END) AS qty_signed_wh,
       f.unit_price,
       f.amt,
       f.flow_time,
       f.origin_business_type,
       f.bill_id,
       f.entry_id,
       f.kc_no,
       f.batch_id
FROM t_hc_ck_flow f
         INNER JOIN fd_material m ON f.material_id = m.id
         LEFT JOIN fd_warehouse w ON f.warehouse_id = w.id
WHERE IFNULL(f.del_flag, 0) = 0
  AND IFNULL(m.del_flag, 0) != 1
  AND (m.is_gz IS NULL OR m.is_gz = '' OR m.is_gz = '2');

-- ========== 高值耗材：仓库流水明细（gz_wh_flow + 条码链路；与 gz_depot_inventory 核对）==========
-- 【覆盖边界】备货验收/退库/退货等条码钩子会写 gz_wh_flow；高值出库单审核会扣减 gz_depot_inventory（GzShipmentServiceImpl）但未必写 gz_wh_flow。
-- 下段 UNION 按「已审核出库单明细」补录备货扣减（lx=CK，qty_signed_wh 为负）；若将来业务在 gz_wh_flow 中为同一 bill_id+entry_id 补写 CK，NOT EXISTS 可避免重复。
-- GzDepotInventoryServiceImpl 等手工维护备货库存亦无 gz_wh_flow。
CREATE OR REPLACE ALGORITHM = UNDEFINED VIEW view_hv_wh_stock_flow_detail AS
SELECT 'GZ_WH' AS src,
       g.id AS flow_id,
       g.tenant_id,
       CAST(NULLIF(TRIM(g.warehouse_id), '') AS UNSIGNED) AS warehouse_id,
       w.code AS warehouse_code,
       w.name AS warehouse_name,
       CAST(NULLIF(TRIM(g.material_id), '') AS UNSIGNED) AS material_id,
       m.code AS material_code,
       m.name AS material_name,
       m.speci AS material_speci,
       m.model AS material_model,
       m.is_gz AS material_is_gz,
       g.batch_no,
       g.batch_number,
       g.lx,
       g.qty AS qty_raw,
       (CASE g.lx
            WHEN 'RK' THEN IFNULL(g.qty, 0)
            WHEN 'ZR' THEN IFNULL(g.qty, 0)
            WHEN 'PY' THEN IFNULL(g.qty, 0)
            WHEN 'CK' THEN -IFNULL(g.qty, 0)
            WHEN 'TH' THEN -IFNULL(g.qty, 0)
            WHEN 'TK' THEN -IFNULL(g.qty, 0)
            WHEN 'ZC' THEN -IFNULL(g.qty, 0)
            WHEN 'PK' THEN -IFNULL(g.qty, 0)
            ELSE IFNULL(g.qty, 0)
        END) AS qty_signed_wh,
       g.unit_price,
       g.amt,
       g.flow_time,
       g.origin_business_type,
       CAST(NULLIF(TRIM(g.bill_id), '') AS UNSIGNED) AS bill_id,
       CAST(NULLIF(TRIM(g.entry_id), '') AS UNSIGNED) AS entry_id,
       CAST(NULLIF(TRIM(g.gz_depot_inventory_id), '') AS UNSIGNED) AS gz_depot_inventory_id,
       g.in_hospital_code,
       g.master_barcode,
       g.secondary_barcode
FROM gz_wh_flow g
         INNER JOIN fd_material m ON m.id = CAST(NULLIF(TRIM(g.material_id), '') AS UNSIGNED)
         LEFT JOIN fd_warehouse w ON w.id = CAST(NULLIF(TRIM(g.warehouse_id), '') AS UNSIGNED)
WHERE IFNULL(g.del_flag, 0) = 0
  AND IFNULL(m.del_flag, 0) != 1
  AND m.is_gz = '1'
UNION ALL
SELECT 'GZ_SHIP_WH_SYN' AS src,
       CONCAT('ZS-WH-', CAST(e.id AS CHAR)) AS flow_id,
       s.tenant_id,
       s.warehouse_id,
       w.code AS warehouse_code,
       w.name AS warehouse_name,
       e.material_id,
       m.code AS material_code,
       m.name AS material_name,
       m.speci AS material_speci,
       m.model AS material_model,
       m.is_gz AS material_is_gz,
       e.batch_no,
       e.batch_number,
       'CK' AS lx,
       e.qty AS qty_raw,
       -IFNULL(e.qty, 0) AS qty_signed_wh,
       e.price AS unit_price,
       e.amt,
       COALESCE(s.audit_date, s.update_time, s.create_time) AS flow_time,
       '高值出库审核-备货扣减(视图补录)' AS origin_business_type,
       s.id AS bill_id,
       e.id AS entry_id,
       CAST(NULL AS UNSIGNED) AS gz_depot_inventory_id,
       e.in_hospital_code,
       e.master_barcode,
       e.secondary_barcode
FROM gz_shipment s
         INNER JOIN gz_shipment_entry e ON e.paren_id = s.id AND IFNULL(e.del_flag, 0) = 0
         INNER JOIN fd_material m ON e.material_id = m.id AND IFNULL(m.del_flag, 0) != 1 AND m.is_gz = '1'
         LEFT JOIN fd_warehouse w ON s.warehouse_id = w.id
WHERE IFNULL(s.del_flag, 0) = 0
  AND s.shipment_status = 2
  AND IFNULL(e.qty, 0) != 0
  AND NOT EXISTS (SELECT 1
                  FROM gz_wh_flow wh
                  WHERE IFNULL(wh.del_flag, 0) = 0
                    AND NULLIF(TRIM(wh.bill_id), '') = CAST(s.id AS CHAR)
                    AND NULLIF(TRIM(wh.entry_id), '') = CAST(e.id AS CHAR)
                    AND wh.lx = 'CK');

-- ========== 低值耗材：科室流水明细（t_hc_ks_flow + 科室盈亏写入 t_hc_ck_flow 的补充段）==========
-- qty_signed_dep：CK 入科+；TK 退库-；XH 消耗-；TXH 退消耗+；科室盘盈/盘亏见 ck 段。
-- 【覆盖边界】① stk_io_stocktaking.audit_adjusts_inventory=1 时审核直接改 stk_dep_inventory，无 t_hc_ks_flow（需在业务补流水或另做盘点差异视图）。② 出库 201 审核可先写科室库存而 ks_flow(CK) 在收货确认后才记，存在表与流水时间差。
CREATE OR REPLACE ALGORITHM = UNDEFINED VIEW view_lv_dep_stock_flow_detail AS
SELECT 'HC_KS' AS src,
       f.id AS flow_id,
       f.tenant_id,
       f.department_id,
       d.code AS department_code,
       d.name AS department_name,
       f.warehouse_id,
       f.material_id,
       m.code AS material_code,
       m.name AS material_name,
       m.speci AS material_speci,
       m.model AS material_model,
       m.is_gz AS material_is_gz,
       f.batch_no,
       f.batch_number,
       f.lx,
       f.qty AS qty_raw,
       (CASE f.lx
            WHEN 'CK' THEN IFNULL(f.qty, 0)
            WHEN 'TK' THEN -IFNULL(f.qty, 0)
            WHEN 'XH' THEN -IFNULL(ABS(f.qty), 0)
            WHEN 'TXH' THEN IFNULL(ABS(f.qty), 0)
            WHEN 'PY' THEN IFNULL(f.qty, 0)
            WHEN 'PK' THEN -IFNULL(f.qty, 0)
            ELSE IFNULL(f.qty, 0)
        END) AS qty_signed_dep,
       f.unit_price,
       f.amt,
       f.flow_time,
       f.origin_business_type,
       f.bill_id,
       f.entry_id,
       f.kc_no AS dep_inventory_id,
       f.batch_id
FROM t_hc_ks_flow f
         INNER JOIN fd_material m ON f.material_id = m.id
         LEFT JOIN fd_department d ON f.department_id = d.id
WHERE IFNULL(f.del_flag, 0) = 0
  AND IFNULL(m.del_flag, 0) != 1
  AND (m.is_gz IS NULL OR m.is_gz = '' OR m.is_gz = '2')
UNION ALL
SELECT 'HC_CK_DEP_PL' AS src,
       f.id AS flow_id,
       f.tenant_id,
       di.department_id,
       d.code AS department_code,
       d.name AS department_name,
       f.warehouse_id,
       f.material_id,
       m.code AS material_code,
       m.name AS material_name,
       m.speci AS material_speci,
       m.model AS material_model,
       m.is_gz AS material_is_gz,
       f.batch_no,
       f.batch_number,
       f.lx,
       f.qty AS qty_raw,
       (CASE f.lx
            WHEN 'PY' THEN IFNULL(f.qty, 0)
            WHEN 'PK' THEN -IFNULL(f.qty, 0)
            ELSE 0
        END) AS qty_signed_dep,
       f.unit_price,
       f.amt,
       f.flow_time,
       f.origin_business_type,
       f.bill_id,
       f.entry_id,
       f.kc_no AS dep_inventory_id,
       f.batch_id
FROM t_hc_ck_flow f
         INNER JOIN stk_dep_inventory di ON di.id = f.kc_no AND IFNULL(di.del_flag, 0) = 0
         INNER JOIN fd_material m ON f.material_id = m.id
         LEFT JOIN fd_department d ON di.department_id = d.id
WHERE IFNULL(f.del_flag, 0) = 0
  AND f.origin_business_type IN ('科室盘亏', '科室盘盈入库')
  AND IFNULL(m.del_flag, 0) != 1
  AND (m.is_gz IS NULL OR m.is_gz = '' OR m.is_gz = '2');

-- ========== 高值耗材：科室流水明细（t_hc_ks_flow 高值 + gz_dep_flow 条码链路）==========
-- 【覆盖边界】gz_dep_flow 在条码主数据存在时由出库钩子写入；主数据缺失时可能不写流水但已改 gz_dep_inventory。下段按已审核出库单补录入科 CK（无 gz_dep_flow 时）。
-- GzDepInventoryServiceImpl / 追溯等直接改科室高值库存的路径未必有 ks / gz_dep_flow。
CREATE OR REPLACE ALGORITHM = UNDEFINED VIEW view_hv_dep_stock_flow_detail AS
SELECT 'HC_KS' AS src,
       f.id AS flow_id,
       f.tenant_id,
       f.department_id,
       d.code AS department_code,
       d.name AS department_name,
       f.warehouse_id,
       f.material_id,
       m.code AS material_code,
       m.name AS material_name,
       m.speci AS material_speci,
       m.model AS material_model,
       m.is_gz AS material_is_gz,
       f.batch_no,
       f.batch_number,
       f.lx,
       f.qty AS qty_raw,
       (CASE f.lx
            WHEN 'CK' THEN IFNULL(f.qty, 0)
            WHEN 'TK' THEN -IFNULL(f.qty, 0)
            WHEN 'XH' THEN -IFNULL(ABS(f.qty), 0)
            WHEN 'TXH' THEN IFNULL(ABS(f.qty), 0)
            ELSE IFNULL(f.qty, 0)
        END) AS qty_signed_dep,
       f.unit_price,
       f.amt,
       f.flow_time,
       f.origin_business_type,
       f.bill_id,
       f.entry_id,
       f.kc_no AS gz_dep_inventory_ref,
       f.batch_id
FROM t_hc_ks_flow f
         INNER JOIN fd_material m ON f.material_id = m.id
         LEFT JOIN fd_department d ON f.department_id = d.id
WHERE IFNULL(f.del_flag, 0) = 0
  AND IFNULL(m.del_flag, 0) != 1
  AND m.is_gz = '1'
UNION ALL
SELECT 'GZ_DEP' AS src,
       g.id AS flow_id,
       g.tenant_id,
       CAST(NULLIF(TRIM(g.department_id), '') AS UNSIGNED) AS department_id,
       d.code AS department_code,
       d.name AS department_name,
       CAST(NULLIF(TRIM(g.warehouse_id), '') AS UNSIGNED) AS warehouse_id,
       CAST(NULLIF(TRIM(g.material_id), '') AS UNSIGNED) AS material_id,
       m.code AS material_code,
       m.name AS material_name,
       m.speci AS material_speci,
       m.model AS material_model,
       m.is_gz AS material_is_gz,
       g.batch_no,
       g.batch_number,
       g.lx,
       g.qty AS qty_raw,
       (CASE g.lx
            WHEN 'CK' THEN IFNULL(g.qty, 0)
            WHEN 'TK' THEN -IFNULL(g.qty, 0)
            WHEN 'TH' THEN -IFNULL(g.qty, 0)
            ELSE IFNULL(g.qty, 0)
        END) AS qty_signed_dep,
       g.unit_price,
       g.amt,
       g.flow_time,
       g.origin_business_type,
       CAST(NULLIF(TRIM(g.bill_id), '') AS UNSIGNED) AS bill_id,
       CAST(NULLIF(TRIM(g.entry_id), '') AS UNSIGNED) AS entry_id,
       CAST(NULLIF(TRIM(g.gz_dep_inventory_id), '') AS UNSIGNED) AS gz_dep_inventory_ref,
       CAST(NULLIF(TRIM(g.batch_id), '') AS UNSIGNED) AS batch_id
FROM gz_dep_flow g
         INNER JOIN fd_material m ON m.id = CAST(NULLIF(TRIM(g.material_id), '') AS UNSIGNED)
         LEFT JOIN fd_department d ON d.id = CAST(NULLIF(TRIM(g.department_id), '') AS UNSIGNED)
WHERE IFNULL(g.del_flag, 0) = 0
  AND IFNULL(m.del_flag, 0) != 1
  AND m.is_gz = '1'
UNION ALL
SELECT 'GZ_SHIP_DEP_SYN' AS src,
       CONCAT('ZS-DEP-', CAST(e.id AS CHAR)) AS flow_id,
       s.tenant_id,
       s.department_id,
       d.code AS department_code,
       d.name AS department_name,
       s.warehouse_id,
       e.material_id,
       m.code AS material_code,
       m.name AS material_name,
       m.speci AS material_speci,
       m.model AS material_model,
       m.is_gz AS material_is_gz,
       e.batch_no,
       e.batch_number,
       'CK' AS lx,
       e.qty AS qty_raw,
       IFNULL(e.qty, 0) AS qty_signed_dep,
       e.price AS unit_price,
       e.amt,
       COALESCE(s.audit_date, s.update_time, s.create_time) AS flow_time,
       '高值出库审核-入科(视图补录)' AS origin_business_type,
       s.id AS bill_id,
       e.id AS entry_id,
       (SELECT di.id
        FROM gz_dep_inventory di
        WHERE IFNULL(di.del_flag, 0) = 0
          AND di.department_id = s.department_id
          AND NULLIF(TRIM(di.in_hospital_code), '') = NULLIF(TRIM(e.in_hospital_code), '')
        ORDER BY di.update_time DESC
        LIMIT 1) AS gz_dep_inventory_ref,
       CAST(NULL AS UNSIGNED) AS batch_id
FROM gz_shipment s
         INNER JOIN gz_shipment_entry e ON e.paren_id = s.id AND IFNULL(e.del_flag, 0) = 0
         INNER JOIN fd_material m ON e.material_id = m.id AND IFNULL(m.del_flag, 0) != 1 AND m.is_gz = '1'
         LEFT JOIN fd_department d ON s.department_id = d.id
WHERE IFNULL(s.del_flag, 0) = 0
  AND s.shipment_status = 2
  AND IFNULL(e.qty, 0) != 0
  AND NOT EXISTS (SELECT 1
                  FROM gz_dep_flow df
                  WHERE IFNULL(df.del_flag, 0) = 0
                    AND NULLIF(TRIM(df.bill_id), '') = CAST(s.id AS CHAR)
                    AND NULLIF(TRIM(df.entry_id), '') = CAST(e.id AS CHAR)
                    AND df.lx = 'CK');

-- ========== 低值耗材：基于进销存全明细视图的仓库维度子集（仅 is_gz=低值）==========
CREATE OR REPLACE ALGORITHM = UNDEFINED VIEW view_lv_wh_stock_detail_from_jxc AS
SELECT v.*
FROM view_stock_all_detail_jxc v
         INNER JOIN fd_material fm ON v.material_id = fm.id
WHERE IFNULL(fm.del_flag, 0) != 1
  AND (fm.is_gz IS NULL OR fm.is_gz = '' OR fm.is_gz = '2');

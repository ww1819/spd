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
       sib.tenant_id AS tenant_id
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
       sib.tenant_id AS tenant_id
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
       sib.tenant_id AS tenant_id
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
       sipl.tenant_id AS tenant_id
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
       sii.tenant_id AS tenant_id
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

CREATE
OR REPLACE ALGORITHM = UNDEFINED VIEW view_stock_all_detail_jxc AS
SELECT
    sib.id AS mid,
    sibe.id AS mxId,
    sib.bill_no AS bill_no,
    sib.warehouse_id AS warehouse_id,
    fw.code AS warehouse_code,
    fw.name AS warehouse_name,
    sib.audit_date AS audit_date,
    sib.bill_type AS bill_type,
    (
        CASE
            WHEN sib.bill_type IN (101, 301) THEN sib.suppler_id
            WHEN sib.bill_type IN (201, 401) THEN sibe.suppler_id
            END
        ) AS suppler_id,
    (
        CASE
            WHEN sib.bill_type IN (101, 301) THEN fs.name
            WHEN sib.bill_type IN (201, 401) THEN fs2.name
            END
        ) AS suppler_name,
    (
        CASE
            WHEN sib.bill_type IN (101, 301) THEN 'RK'
            WHEN sib.bill_type IN (201, 401) THEN 'CK'
            END
        ) AS io_type,
    sibe.unit_price AS unit_price,
    (
        CASE
            WHEN sib.bill_type IN (101, 201) THEN sibe.qty
            WHEN sib.bill_type IN (301, 401) THEN - sibe.qty
            END
        ) AS io_qty,
    (
        CASE
            WHEN sib.bill_type IN (101, 201) THEN sibe.qty * sibe.unit_price
            WHEN sib.bill_type IN (301, 401) THEN - sibe.qty * sibe.unit_price
            END
        ) AS io_amt,
    (
        CASE
            WHEN sib.bill_type IN (101, 401) THEN sibe.qty
            WHEN sib.bill_type IN (201, 301) THEN - sibe.qty
            END
        ) AS jc_qty,
    (
        CASE
            WHEN sib.bill_type IN (101, 401) THEN sibe.qty * sibe.unit_price
            WHEN sib.bill_type IN (201, 301) THEN - sibe.qty * sibe.unit_price
            END
        ) AS jc_amt,
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
    ffc.finance_category_name AS finance_category_name
FROM
    stk_io_bill sib
        LEFT JOIN stk_io_bill_entry sibe ON sib.id = sibe.paren_id
        LEFT JOIN fd_warehouse fw ON sib.warehouse_id = fw.id
        LEFT JOIN fd_material fm ON sibe.material_id = fm.id
        LEFT JOIN fd_warehouse_category fwc ON fm.storeroom_id = fwc.warehouse_category_id
        LEFT JOIN fd_finance_category ffc ON fm.finance_category_id = ffc.finance_category_id
        LEFT JOIN fd_supplier fs ON sib.suppler_id = fs.id
        LEFT JOIN fd_supplier fs2 ON sibe.suppler_id = fs2.id
        LEFT JOIN fd_unit fu ON fm.unit_id = fu.unit_id
        LEFT JOIN fd_factory ff ON fm.factory_id = ff.factory_id
WHERE
    1 = 1
  AND sib.del_flag = 0
  AND sibe.del_flag = 0
  AND sib.bill_status = 2
  AND sib.bill_type IN (101, 201, 301, 401)
UNION ALL
SELECT
    sib.id AS id,
    sibe.id AS id,
    sib.bill_no AS bill_no,
    sib.warehouse_id AS warehouse_id,
    fw.code AS code,
    fw.name AS name,
    sib.audit_date AS audit_date,
    sib.bill_type AS bill_type,
    sibe.suppler_id AS suppler_id,
    fs.name AS name,
    'DBC' AS DBC,
    sibe.unit_price AS unit_price,
    sibe.qty AS qty,
    sibe.qty * sibe.unit_price AS sibe_qty_unit_price,
    - sibe.qty AS minus_sibe_qty,
    - sibe.qty * sibe.unit_price AS minus_sibe_qty_unit_price,
    sibe.material_id AS material_id,
    fm.code AS code,
    fm.name AS name,
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
    ffc.finance_category_name AS finance_category_name
FROM
    stk_io_bill sib
        LEFT JOIN stk_io_bill_entry sibe ON sib.id = sibe.paren_id
        LEFT JOIN fd_warehouse fw ON sib.warehouse_id = fw.id
        LEFT JOIN fd_material fm ON sibe.material_id = fm.id
        LEFT JOIN fd_warehouse_category fwc ON fm.storeroom_id = fwc.warehouse_category_id
        LEFT JOIN fd_finance_category ffc ON fm.finance_category_id = ffc.finance_category_id
        LEFT JOIN fd_supplier fs ON sibe.suppler_id = fs.id
        LEFT JOIN fd_unit fu ON fm.unit_id = fu.unit_id
        LEFT JOIN fd_factory ff ON fm.factory_id = ff.factory_id
WHERE
    1 = 1
  AND sib.del_flag = 0
  AND sibe.del_flag = 0
  AND sib.bill_status = 2
  AND sib.bill_type = 501
  AND LEFT(sib.bill_no, 2) = 'DB'
UNION ALL
SELECT
    sib.id AS id,
    sibe.id AS id,
    sib.bill_no AS bill_no,
    sib.department_id AS department_id,
    fw.code AS code,
    fw.name AS name,
    sib.audit_date AS audit_date,
    sib.bill_type AS bill_type,
    sibe.suppler_id AS suppler_id,
    fs.name AS name,
    'DBR' AS DBR,
    sibe.unit_price AS unit_price,
    sibe.qty AS qty,
    sibe.qty * sibe.unit_price AS sibe_qty_unit_price,
    sibe.qty AS qty,
    sibe.qty * sibe.unit_price AS sibe_qty_unit_price2,
    sibe.material_id AS material_id,
    fm.code AS code,
    fm.name AS name,
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
    ffc.finance_category_name AS finance_category_name
FROM
    stk_io_bill sib
        LEFT JOIN stk_io_bill_entry sibe ON sib.id = sibe.paren_id
        LEFT JOIN fd_warehouse fw ON sib.department_id = fw.id
        LEFT JOIN fd_material fm ON sibe.material_id = fm.id
        LEFT JOIN fd_warehouse_category fwc ON fm.storeroom_id = fwc.warehouse_category_id
        LEFT JOIN fd_finance_category ffc ON fm.finance_category_id = ffc.finance_category_id
        LEFT JOIN fd_supplier fs ON sibe.suppler_id = fs.id
        LEFT JOIN fd_unit fu ON fm.unit_id = fu.unit_id
        LEFT JOIN fd_factory ff ON fm.factory_id = ff.factory_id
WHERE
    1 = 1
  AND sib.del_flag = 0
  AND sibe.del_flag = 0
  AND sib.bill_status = 2
  AND sib.bill_type = 501
  AND LEFT(sib.bill_no, 2) = 'DB'
UNION ALL
SELECT
    sipl.id AS id,
    siple.id AS id,
    sipl.bill_no AS bill_no,
    sipl.warehouse_id AS warehouse_id,
    fw.code AS code,
    fw.name AS name,
    sipl.audit_date AS audit_date,
    601 AS bill_type,
    -- 补充合法别名
    siple.suppler_id AS suppler_id,
    fs.name AS name,
    'PD' AS PD_type,
    -- 补充合法别名
    siple.unit_price AS unit_price,
    siple.profit_qty AS profit_qty,
    siple.profit_qty * siple.unit_price AS siple_profit_qty_unit_price,
    siple.profit_qty AS profit_qty2,
    -- 补充合法别名（避免重复）
    siple.profit_qty * siple.unit_price AS siple_profit_qty_unit_price2,
    siple.material_id AS material_id,
    fm.code AS code,
    fm.name AS name,
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
    ffc.finance_category_name AS finance_category_name
FROM
    stk_io_profit_loss sipl
        LEFT JOIN stk_io_profit_loss_entry siple ON sipl.id = siple.paren_id
        LEFT JOIN fd_warehouse fw ON sipl.warehouse_id = fw.id
        LEFT JOIN fd_material fm ON siple.material_id = fm.id
        LEFT JOIN fd_warehouse_category fwc ON fm.storeroom_id = fwc.warehouse_category_id
        LEFT JOIN fd_finance_category ffc ON fm.finance_category_id = ffc.finance_category_id
        LEFT JOIN fd_supplier fs ON siple.suppler_id = fs.id
        LEFT JOIN fd_unit fu ON fm.unit_id = fu.unit_id
        LEFT JOIN fd_factory ff ON fm.factory_id = ff.factory_id
WHERE
    1 = 1
  AND sipl.del_flag = 0
  AND siple.del_flag = 0
  AND sipl.bill_status = 2;
/
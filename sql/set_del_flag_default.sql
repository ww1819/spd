-- 为所有含 del_flag 的列：先将现有 NULL 更新为 0，再设置默认值 0
-- 基于 information_schema 查询结果生成（aspt / scm 库）

-- ========== 库 aspt ==========

-- int / int unsigned：先更新 NULL，再设默认 0
UPDATE aspt.bas_apply SET del_flag = 0 WHERE del_flag IS NULL;
ALTER TABLE aspt.bas_apply MODIFY COLUMN del_flag int DEFAULT 0;

UPDATE aspt.dep_inventory_warning SET del_flag = 0 WHERE del_flag IS NULL;
ALTER TABLE aspt.dep_inventory_warning MODIFY COLUMN del_flag int DEFAULT 0;

UPDATE aspt.dep_purchase_apply SET del_flag = 0 WHERE del_flag IS NULL;
ALTER TABLE aspt.dep_purchase_apply MODIFY COLUMN del_flag int DEFAULT 0;

UPDATE aspt.fd_category68 SET del_flag = 0 WHERE del_flag IS NULL;
ALTER TABLE aspt.fd_category68 MODIFY COLUMN del_flag int DEFAULT 0;

UPDATE aspt.fd_department SET del_flag = 0 WHERE del_flag IS NULL;
ALTER TABLE aspt.fd_department MODIFY COLUMN del_flag int DEFAULT 0;

UPDATE aspt.fd_equipment_category SET del_flag = 0 WHERE del_flag IS NULL;
ALTER TABLE aspt.fd_equipment_category MODIFY COLUMN del_flag int DEFAULT 0;

UPDATE aspt.fd_equipment_dict SET del_flag = 0 WHERE del_flag IS NULL;
ALTER TABLE aspt.fd_equipment_dict MODIFY COLUMN del_flag int DEFAULT 0;

UPDATE aspt.fd_factory SET del_flag = 0 WHERE del_flag IS NULL;
ALTER TABLE aspt.fd_factory MODIFY COLUMN del_flag int DEFAULT 0;

UPDATE aspt.fd_finance_category SET del_flag = 0 WHERE del_flag IS NULL;
ALTER TABLE aspt.fd_finance_category MODIFY COLUMN del_flag int DEFAULT 0;

UPDATE aspt.fd_location SET del_flag = 0 WHERE del_flag IS NULL;
ALTER TABLE aspt.fd_location MODIFY COLUMN del_flag int DEFAULT 0;

UPDATE aspt.fd_material SET del_flag = 0 WHERE del_flag IS NULL;
ALTER TABLE aspt.fd_material MODIFY COLUMN del_flag int DEFAULT 0;

UPDATE aspt.fd_material_category SET del_flag = 0 WHERE del_flag IS NULL;
ALTER TABLE aspt.fd_material_category MODIFY COLUMN del_flag int DEFAULT 0;

UPDATE aspt.fd_supplier SET del_flag = 0 WHERE del_flag IS NULL;
ALTER TABLE aspt.fd_supplier MODIFY COLUMN del_flag int DEFAULT 0;

UPDATE aspt.fd_unit SET del_flag = 0 WHERE del_flag IS NULL;
ALTER TABLE aspt.fd_unit MODIFY COLUMN del_flag int DEFAULT 0;

UPDATE aspt.fd_warehouse SET del_flag = 0 WHERE del_flag IS NULL;
ALTER TABLE aspt.fd_warehouse MODIFY COLUMN del_flag int DEFAULT 0;

UPDATE aspt.fd_warehouse_category SET del_flag = 0 WHERE del_flag IS NULL;
ALTER TABLE aspt.fd_warehouse_category MODIFY COLUMN del_flag int DEFAULT 0;

UPDATE aspt.gz_dep_apply SET del_flag = 0 WHERE del_flag IS NULL;
ALTER TABLE aspt.gz_dep_apply MODIFY COLUMN del_flag int DEFAULT 0;

UPDATE aspt.gz_order SET del_flag = 0 WHERE del_flag IS NULL;
ALTER TABLE aspt.gz_order MODIFY COLUMN del_flag int DEFAULT 0;

UPDATE aspt.gz_order_entry SET del_flag = 0 WHERE del_flag IS NULL;
ALTER TABLE aspt.gz_order_entry MODIFY COLUMN del_flag int DEFAULT 0;

UPDATE aspt.gz_refund_goods SET del_flag = 0 WHERE del_flag IS NULL;
ALTER TABLE aspt.gz_refund_goods MODIFY COLUMN del_flag int DEFAULT 0;

UPDATE aspt.gz_refund_goods_entry SET del_flag = 0 WHERE del_flag IS NULL;
ALTER TABLE aspt.gz_refund_goods_entry MODIFY COLUMN del_flag int DEFAULT 0;

UPDATE aspt.gz_refund_stock SET del_flag = 0 WHERE del_flag IS NULL;
ALTER TABLE aspt.gz_refund_stock MODIFY COLUMN del_flag int DEFAULT 0;

UPDATE aspt.gz_refund_stock_entry SET del_flag = 0 WHERE del_flag IS NULL;
ALTER TABLE aspt.gz_refund_stock_entry MODIFY COLUMN del_flag int DEFAULT 0;

UPDATE aspt.gz_shipment SET del_flag = 0 WHERE del_flag IS NULL;
ALTER TABLE aspt.gz_shipment MODIFY COLUMN del_flag int DEFAULT 0;

UPDATE aspt.gz_shipment_entry SET del_flag = 0 WHERE del_flag IS NULL;
ALTER TABLE aspt.gz_shipment_entry MODIFY COLUMN del_flag int DEFAULT 0;

UPDATE aspt.new_product_apply SET del_flag = 0 WHERE del_flag IS NULL;
ALTER TABLE aspt.new_product_apply MODIFY COLUMN del_flag int DEFAULT 0;

UPDATE aspt.stk_io_bill SET del_flag = 0 WHERE del_flag IS NULL;
ALTER TABLE aspt.stk_io_bill MODIFY COLUMN del_flag int unsigned DEFAULT 0;

UPDATE aspt.stk_io_bill_entry SET del_flag = 0 WHERE del_flag IS NULL;
ALTER TABLE aspt.stk_io_bill_entry MODIFY COLUMN del_flag int DEFAULT 0;

UPDATE aspt.stk_io_stocktaking SET del_flag = 0 WHERE del_flag IS NULL;
ALTER TABLE aspt.stk_io_stocktaking MODIFY COLUMN del_flag int unsigned DEFAULT 0;

UPDATE aspt.stk_io_stocktaking_entry SET del_flag = 0 WHERE del_flag IS NULL;
ALTER TABLE aspt.stk_io_stocktaking_entry MODIFY COLUMN del_flag int DEFAULT 0;

UPDATE aspt.t_hc_ck_flow SET del_flag = 0 WHERE del_flag IS NULL;
ALTER TABLE aspt.t_hc_ck_flow MODIFY COLUMN del_flag int DEFAULT 0;

UPDATE aspt.t_hc_ks_flow SET del_flag = 0 WHERE del_flag IS NULL;
ALTER TABLE aspt.t_hc_ks_flow MODIFY COLUMN del_flag int DEFAULT 0;

UPDATE aspt.t_hc_ks_xh SET del_flag = 0 WHERE del_flag IS NULL;
ALTER TABLE aspt.t_hc_ks_xh MODIFY COLUMN del_flag int DEFAULT 0;

UPDATE aspt.t_hc_ks_xh_entry SET del_flag = 0 WHERE del_flag IS NULL;
ALTER TABLE aspt.t_hc_ks_xh_entry MODIFY COLUMN del_flag int DEFAULT 0;

-- char(1)：先更新 NULL 为 '0'，再设默认 '0'
UPDATE aspt.equipment_info SET del_flag = '0' WHERE del_flag IS NULL;
ALTER TABLE aspt.equipment_info MODIFY COLUMN del_flag char(1) DEFAULT '0';

UPDATE aspt.equipment_purchase_application SET del_flag = '0' WHERE del_flag IS NULL;
ALTER TABLE aspt.equipment_purchase_application MODIFY COLUMN del_flag char(1) DEFAULT '0';

UPDATE aspt.gz_patient_info SET del_flag = '0' WHERE del_flag IS NULL;
ALTER TABLE aspt.gz_patient_info MODIFY COLUMN del_flag char(1) DEFAULT '0';

UPDATE aspt.gz_traceability SET del_flag = '0' WHERE del_flag IS NULL;
ALTER TABLE aspt.gz_traceability MODIFY COLUMN del_flag char(1) DEFAULT '0';

UPDATE aspt.gz_traceability_entry SET del_flag = '0' WHERE del_flag IS NULL;
ALTER TABLE aspt.gz_traceability_entry MODIFY COLUMN del_flag char(1) DEFAULT '0';

UPDATE aspt.purchase_order SET del_flag = '0' WHERE del_flag IS NULL;
ALTER TABLE aspt.purchase_order MODIFY COLUMN del_flag char(1) DEFAULT '0';

UPDATE aspt.purchase_order_entry SET del_flag = '0' WHERE del_flag IS NULL;
ALTER TABLE aspt.purchase_order_entry MODIFY COLUMN del_flag char(1) DEFAULT '0';

UPDATE aspt.purchase_plan SET del_flag = '0' WHERE del_flag IS NULL;
ALTER TABLE aspt.purchase_plan MODIFY COLUMN del_flag char(1) DEFAULT '0';

UPDATE aspt.purchase_plan_entry SET del_flag = '0' WHERE del_flag IS NULL;
ALTER TABLE aspt.purchase_plan_entry MODIFY COLUMN del_flag char(1) DEFAULT '0';

UPDATE aspt.sys_dept SET del_flag = '0' WHERE del_flag IS NULL;
ALTER TABLE aspt.sys_dept MODIFY COLUMN del_flag char(1) DEFAULT '0';

UPDATE aspt.sys_role SET del_flag = '0' WHERE del_flag IS NULL;
ALTER TABLE aspt.sys_role MODIFY COLUMN del_flag char(1) DEFAULT '0';

UPDATE aspt.sys_user SET del_flag = '0' WHERE del_flag IS NULL;
ALTER TABLE aspt.sys_user MODIFY COLUMN del_flag char(1) DEFAULT '0';

-- ========== 库 scm ==========

UPDATE scm.scm_hospital SET del_flag = '0' WHERE del_flag IS NULL;
ALTER TABLE scm.scm_hospital MODIFY COLUMN del_flag char(1) DEFAULT '0';

UPDATE scm.scm_manufacturer SET del_flag = '0' WHERE del_flag IS NULL;
ALTER TABLE scm.scm_manufacturer MODIFY COLUMN del_flag char(1) DEFAULT '0';

UPDATE scm.scm_material_category SET del_flag = '0' WHERE del_flag IS NULL;
ALTER TABLE scm.scm_material_category MODIFY COLUMN del_flag char(1) DEFAULT '0';

UPDATE scm.scm_material_dict SET del_flag = '0' WHERE del_flag IS NULL;
ALTER TABLE scm.scm_material_dict MODIFY COLUMN del_flag char(1) DEFAULT '0';

UPDATE scm.scm_supplier SET del_flag = '0' WHERE del_flag IS NULL;
ALTER TABLE scm.scm_supplier MODIFY COLUMN del_flag char(1) DEFAULT '0';

UPDATE scm.sys_dept SET del_flag = '0' WHERE del_flag IS NULL;
ALTER TABLE scm.sys_dept MODIFY COLUMN del_flag char(1) DEFAULT '0';

UPDATE scm.sys_role SET del_flag = '0' WHERE del_flag IS NULL;
ALTER TABLE scm.sys_role MODIFY COLUMN del_flag char(1) DEFAULT '0';

UPDATE scm.sys_user SET del_flag = '0' WHERE del_flag IS NULL;
ALTER TABLE scm.sys_user MODIFY COLUMN del_flag char(1) DEFAULT '0';

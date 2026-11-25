-- equipment/column.sql：设备侧增量字段（按「/」分段）
-- 注意：SPD 启动 SqlInitRunner 当前只执行 material/*，不会自动跑本文件。
-- 客户金额小数位等与耗材共用的列，已写入 material/column.sql（add_table_column），重启即可补列。
-- 本文件保留设备侧专用增量；与 material 双轨时以 material 为准补共用表。

-- SYS-F-002（设备侧镜像，可选；耗材环境请以 material/column.sql 为准）
ALTER TABLE `sb_customer`
  ADD COLUMN `price_decimal_places` tinyint NOT NULL DEFAULT 3 COMMENT '单价显示小数位(0-6，已生效)' AFTER `tenant_key`;
/
ALTER TABLE `sb_customer`
  ADD COLUMN `amount_decimal_places` tinyint NOT NULL DEFAULT 3 COMMENT '金额显示小数位(0-6，已生效)' AFTER `price_decimal_places`;
/
ALTER TABLE `sb_customer`
  ADD COLUMN `money_round_mode` varchar(16) NOT NULL DEFAULT 'HALF_UP' COMMENT '金额舍入：HALF_UP/HALF_EVEN/DOWN' AFTER `amount_decimal_places`;
/

-- ========== audit name snapshots (equipment) ==========
-- operator user_name snapshots (idempotent add_table_column)
CALL add_table_column('equipment_accessory', 'create_by_name', 'varchar(64)', 'creator user_name snapshot', NULL);
/
CALL add_table_column('equipment_accessory', 'update_by_name', 'varchar(64)', 'updater user_name snapshot', NULL);
/
CALL add_table_column('equipment_accessory', 'delete_by_name', 'varchar(64)', 'deleter user_name snapshot', NULL);
/
CALL add_table_column('equipment_accessory_io', 'create_by_name', 'varchar(64)', 'creator user_name snapshot', NULL);
/
CALL add_table_column('equipment_accessory_io', 'update_by_name', 'varchar(64)', 'updater user_name snapshot', NULL);
/
CALL add_table_column('equipment_accessory_io', 'delete_by_name', 'varchar(64)', 'deleter user_name snapshot', NULL);
/
CALL add_table_column('equipment_accessory_io_entry', 'create_by_name', 'varchar(64)', 'creator user_name snapshot', NULL);
/
CALL add_table_column('equipment_accessory_io_entry', 'update_by_name', 'varchar(64)', 'updater user_name snapshot', NULL);
/
CALL add_table_column('equipment_accessory_stock', 'create_by_name', 'varchar(64)', 'creator user_name snapshot', NULL);
/
CALL add_table_column('equipment_accessory_stock', 'update_by_name', 'varchar(64)', 'updater user_name snapshot', NULL);
/
CALL add_table_column('equipment_accessory_stock', 'delete_by_name', 'varchar(64)', 'deleter user_name snapshot', NULL);
/
CALL add_table_column('equipment_adverse_event', 'create_by_name', 'varchar(64)', 'creator user_name snapshot', NULL);
/
CALL add_table_column('equipment_adverse_event', 'update_by_name', 'varchar(64)', 'updater user_name snapshot', NULL);
/
CALL add_table_column('equipment_adverse_event', 'delete_by_name', 'varchar(64)', 'deleter user_name snapshot', NULL);
/
CALL add_table_column('equipment_file', 'create_by_name', 'varchar(64)', 'creator user_name snapshot', NULL);
/
CALL add_table_column('equipment_file', 'update_by_name', 'varchar(64)', 'updater user_name snapshot', NULL);
/
CALL add_table_column('equipment_info', 'create_by_name', 'varchar(64)', 'creator user_name snapshot', NULL);
/
CALL add_table_column('equipment_info', 'update_by_name', 'varchar(64)', 'updater user_name snapshot', NULL);
/
CALL add_table_column('equipment_purchase_application', 'create_by_name', 'varchar(64)', 'creator user_name snapshot', NULL);
/
CALL add_table_column('equipment_purchase_application', 'update_by_name', 'varchar(64)', 'updater user_name snapshot', NULL);
/
CALL add_table_column('equipment_return', 'create_by_name', 'varchar(64)', 'creator user_name snapshot', NULL);
/
CALL add_table_column('equipment_return', 'update_by_name', 'varchar(64)', 'updater user_name snapshot', NULL);
/
CALL add_table_column('equipment_storage', 'create_by_name', 'varchar(64)', 'creator user_name snapshot', NULL);
/
CALL add_table_column('equipment_storage', 'update_by_name', 'varchar(64)', 'updater user_name snapshot', NULL);
/
CALL add_table_column('fd_category68', 'create_by_name', 'varchar(64)', 'creator user_name snapshot', NULL);
/
CALL add_table_column('fd_category68', 'update_by_name', 'varchar(64)', 'updater user_name snapshot', NULL);
/
CALL add_table_column('fd_equipment_category', 'create_by_name', 'varchar(64)', 'creator user_name snapshot', NULL);
/
CALL add_table_column('fd_equipment_category', 'update_by_name', 'varchar(64)', 'updater user_name snapshot', NULL);
/
CALL add_table_column('fd_equipment_dict', 'create_by_name', 'varchar(64)', 'creator user_name snapshot', NULL);
/
CALL add_table_column('fd_equipment_dict', 'update_by_name', 'varchar(64)', 'updater user_name snapshot', NULL);
/
CALL add_table_column('sb_asset_category', 'create_by_name', 'varchar(64)', 'creator user_name snapshot', NULL);
/
CALL add_table_column('sb_asset_category', 'update_by_name', 'varchar(64)', 'updater user_name snapshot', NULL);
/
CALL add_table_column('sb_asset_inventory', 'create_by_name', 'varchar(64)', 'creator user_name snapshot', NULL);
/
CALL add_table_column('sb_asset_inventory', 'update_by_name', 'varchar(64)', 'updater user_name snapshot', NULL);
/
CALL add_table_column('sb_asset_inventory', 'audit_by_name', 'varchar(64)', 'auditor user_name snapshot', NULL);
/
CALL add_table_column('sb_asset_inventory_item', 'create_by_name', 'varchar(64)', 'creator user_name snapshot', NULL);
/
CALL add_table_column('sb_asset_inventory_item', 'update_by_name', 'varchar(64)', 'updater user_name snapshot', NULL);
/
CALL add_table_column('sb_asset_inventory_item_print', 'create_by_name', 'varchar(64)', 'creator user_name snapshot', NULL);
/
CALL add_table_column('sb_asset_inventory_item_print', 'update_by_name', 'varchar(64)', 'updater user_name snapshot', NULL);
/
CALL add_table_column('sb_asset_print_log', 'create_by_name', 'varchar(64)', 'creator user_name snapshot', NULL);
/
CALL add_table_column('sb_asset_print_log', 'update_by_name', 'varchar(64)', 'updater user_name snapshot', NULL);
/
CALL add_table_column('sb_asset_print_task', 'create_by_name', 'varchar(64)', 'creator user_name snapshot', NULL);
/
CALL add_table_column('sb_asset_print_task', 'update_by_name', 'varchar(64)', 'updater user_name snapshot', NULL);
/
CALL add_table_column('sb_asset_print_task_item', 'create_by_name', 'varchar(64)', 'creator user_name snapshot', NULL);
/
CALL add_table_column('sb_asset_print_task_item', 'update_by_name', 'varchar(64)', 'updater user_name snapshot', NULL);
/
CALL add_table_column('sb_customer', 'create_by_name', 'varchar(64)', 'creator user_name snapshot', NULL);
/
CALL add_table_column('sb_customer', 'update_by_name', 'varchar(64)', 'updater user_name snapshot', NULL);
/
CALL add_table_column('sb_customer', 'delete_by_name', 'varchar(64)', 'deleter user_name snapshot', NULL);
/
CALL add_table_column('sb_customer_asset_ledger', 'create_by_name', 'varchar(64)', 'creator user_name snapshot', NULL);
/
CALL add_table_column('sb_customer_asset_ledger', 'update_by_name', 'varchar(64)', 'updater user_name snapshot', NULL);
/
CALL add_table_column('sb_customer_category68', 'create_by_name', 'varchar(64)', 'creator user_name snapshot', NULL);
/
CALL add_table_column('sb_customer_category68', 'update_by_name', 'varchar(64)', 'updater user_name snapshot', NULL);
/
CALL add_table_column('sb_customer_menu', 'create_by_name', 'varchar(64)', 'creator user_name snapshot', NULL);
/
CALL add_table_column('sb_customer_menu', 'delete_by_name', 'varchar(64)', 'deleter user_name snapshot', NULL);
/
CALL add_table_column('sb_customer_menu_period_log', 'create_by_name', 'varchar(64)', 'creator user_name snapshot', NULL);
/
CALL add_table_column('sb_customer_money_scale_audit', 'audit_by_name', 'varchar(64)', 'auditor user_name snapshot', NULL);
/
CALL add_table_column('sb_customer_money_scale_audit', 'apply_by_name', 'varchar(64)', 'applicant user_name snapshot', NULL);
/
CALL add_table_column('sb_customer_period_log', 'create_by_name', 'varchar(64)', 'creator user_name snapshot', NULL);
/
CALL add_table_column('sb_equipment_brand', 'create_by_name', 'varchar(64)', 'creator user_name snapshot', NULL);
/
CALL add_table_column('sb_equipment_brand', 'update_by_name', 'varchar(64)', 'updater user_name snapshot', NULL);
/
CALL add_table_column('sb_equipment_manufacturer', 'create_by_name', 'varchar(64)', 'creator user_name snapshot', NULL);
/
CALL add_table_column('sb_equipment_manufacturer', 'update_by_name', 'varchar(64)', 'updater user_name snapshot', NULL);
/
CALL add_table_column('sb_equipment_supplier', 'create_by_name', 'varchar(64)', 'creator user_name snapshot', NULL);
/
CALL add_table_column('sb_equipment_supplier', 'update_by_name', 'varchar(64)', 'updater user_name snapshot', NULL);
/
CALL add_table_column('sb_measuring_category', 'create_by_name', 'varchar(64)', 'creator user_name snapshot', NULL);
/
CALL add_table_column('sb_measuring_category', 'update_by_name', 'varchar(64)', 'updater user_name snapshot', NULL);
/
CALL add_table_column('sb_menu', 'create_by_name', 'varchar(64)', 'creator user_name snapshot', NULL);
/
CALL add_table_column('sb_menu', 'update_by_name', 'varchar(64)', 'updater user_name snapshot', NULL);
/
CALL add_table_column('sb_menu', 'delete_by_name', 'varchar(64)', 'deleter user_name snapshot', NULL);
/
CALL add_table_column('sb_role', 'create_by_name', 'varchar(64)', 'creator user_name snapshot', NULL);
/
CALL add_table_column('sb_role', 'update_by_name', 'varchar(64)', 'updater user_name snapshot', NULL);
/
CALL add_table_column('sb_role', 'delete_by_name', 'varchar(64)', 'deleter user_name snapshot', NULL);
/
CALL add_table_column('sb_role_menu', 'delete_by_name', 'varchar(64)', 'deleter user_name snapshot', NULL);
/
CALL add_table_column('sb_tenant_setting', 'create_by_name', 'varchar(64)', 'creator user_name snapshot', NULL);
/
CALL add_table_column('sb_tenant_setting', 'update_by_name', 'varchar(64)', 'updater user_name snapshot', NULL);
/
CALL add_table_column('sb_tenant_setting', 'delete_by_name', 'varchar(64)', 'deleter user_name snapshot', NULL);
/
CALL add_table_column('sb_user_permission_dept', 'create_by_name', 'varchar(64)', 'creator user_name snapshot', NULL);
/
CALL add_table_column('sb_user_permission_dept', 'delete_by_name', 'varchar(64)', 'deleter user_name snapshot', NULL);
/
CALL add_table_column('sb_user_permission_menu', 'create_by_name', 'varchar(64)', 'creator user_name snapshot', NULL);
/
CALL add_table_column('sb_user_permission_menu', 'delete_by_name', 'varchar(64)', 'deleter user_name snapshot', NULL);
/
CALL add_table_column('sb_user_permission_warehouse', 'create_by_name', 'varchar(64)', 'creator user_name snapshot', NULL);
/
CALL add_table_column('sb_user_permission_warehouse', 'delete_by_name', 'varchar(64)', 'deleter user_name snapshot', NULL);
/
CALL add_table_column('sb_user_role', 'delete_by_name', 'varchar(64)', 'deleter user_name snapshot', NULL);
/
CALL add_table_column('sb_work_group', 'create_by_name', 'varchar(64)', 'creator user_name snapshot', NULL);
/
CALL add_table_column('sb_work_group', 'update_by_name', 'varchar(64)', 'updater user_name snapshot', NULL);
/
CALL add_table_column('sb_work_group', 'delete_by_name', 'varchar(64)', 'deleter user_name snapshot', NULL);
/
CALL add_table_column('sb_work_group_dept', 'create_by_name', 'varchar(64)', 'creator user_name snapshot', NULL);
/
CALL add_table_column('sb_work_group_dept', 'delete_by_name', 'varchar(64)', 'deleter user_name snapshot', NULL);
/
CALL add_table_column('sb_work_group_menu', 'create_by_name', 'varchar(64)', 'creator user_name snapshot', NULL);
/
CALL add_table_column('sb_work_group_menu', 'delete_by_name', 'varchar(64)', 'deleter user_name snapshot', NULL);
/
CALL add_table_column('sb_work_group_user', 'create_by_name', 'varchar(64)', 'creator user_name snapshot', NULL);
/
CALL add_table_column('sb_work_group_user', 'delete_by_name', 'varchar(64)', 'deleter user_name snapshot', NULL);
/
CALL add_table_column('sb_work_group_warehouse', 'create_by_name', 'varchar(64)', 'creator user_name snapshot', NULL);
/
CALL add_table_column('sb_work_group_warehouse', 'delete_by_name', 'varchar(64)', 'deleter user_name snapshot', NULL);
/

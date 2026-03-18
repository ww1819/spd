-- 设备资产相关系统字典类型与数据（使用状态、维修状态、保修类型、标签打印状态）

-- 使用状态 eq_use_status（默认在用）
INSERT INTO sys_dict_type (dict_name, dict_type, status, remark, create_by, create_time)
SELECT '使用状态', 'eq_use_status', '0', '资产使用状态', 'admin', NOW()
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_type WHERE dict_type = 'eq_use_status');
/
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, status, create_time) SELECT 1, '试用', 'trial', 'eq_use_status', '0', NOW() FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'eq_use_status' AND dict_value = 'trial');
/
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, status, is_default, create_time) SELECT 2, '在用', 'in_use', 'eq_use_status', '0', 'Y', NOW() FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'eq_use_status' AND dict_value = 'in_use');
/
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, status, create_time) SELECT 3, '闲置', 'idle', 'eq_use_status', '0', NOW() FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'eq_use_status' AND dict_value = 'idle');
/
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, status, create_time) SELECT 4, '维修中', 'in_repair', 'eq_use_status', '0', NOW() FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'eq_use_status' AND dict_value = 'in_repair');
/
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, status, create_time) SELECT 5, '报废申请中', 'scrap_applying', 'eq_use_status', '0', NOW() FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'eq_use_status' AND dict_value = 'scrap_applying');
/
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, status, create_time) SELECT 6, '已报废', 'scrapped', 'eq_use_status', '0', NOW() FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'eq_use_status' AND dict_value = 'scrapped');
/

-- 维修状态 eq_repair_status（默认无故障）
INSERT INTO sys_dict_type (dict_name, dict_type, status, remark, create_by, create_time)
SELECT '维修状态', 'eq_repair_status', '0', '设备维修状态', 'admin', NOW()
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_type WHERE dict_type = 'eq_repair_status');
/
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, status, create_time) SELECT 1, '报修中', 'repair_applying', 'eq_repair_status', '0', NOW() FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'eq_repair_status' AND dict_value = 'repair_applying');
/
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, status, create_time) SELECT 2, '已派工', 'dispatched', 'eq_repair_status', '0', NOW() FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'eq_repair_status' AND dict_value = 'dispatched');
/
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, status, create_time) SELECT 3, '已接单', 'accepted', 'eq_repair_status', '0', NOW() FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'eq_repair_status' AND dict_value = 'accepted');
/
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, status, create_time) SELECT 4, '院内维修中', 'repair_internal', 'eq_repair_status', '0', NOW() FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'eq_repair_status' AND dict_value = 'repair_internal');
/
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, status, create_time) SELECT 5, '院外维修中', 'repair_external', 'eq_repair_status', '0', NOW() FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'eq_repair_status' AND dict_value = 'repair_external');
/
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, status, create_time) SELECT 6, '已维修待验收', 'pending_accept', 'eq_repair_status', '0', NOW() FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'eq_repair_status' AND dict_value = 'pending_accept');
/
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, status, create_time) SELECT 7, '已验收', 'accepted_ok', 'eq_repair_status', '0', NOW() FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'eq_repair_status' AND dict_value = 'accepted_ok');
/
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, status, is_default, create_time) SELECT 8, '无故障', 'no_fault', 'eq_repair_status', '0', 'Y', NOW() FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'eq_repair_status' AND dict_value = 'no_fault');
/

-- 保修类型 eq_warranty_type
INSERT INTO sys_dict_type (dict_name, dict_type, status, remark, create_by, create_time)
SELECT '保修类型', 'eq_warranty_type', '0', '设备保修类型', 'admin', NOW()
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_type WHERE dict_type = 'eq_warranty_type');
/
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, status, create_time) SELECT 1, '厂保', 'factory', 'eq_warranty_type', '0', NOW() FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'eq_warranty_type' AND dict_value = 'factory');
/
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, status, create_time) SELECT 2, '延保', 'extend', 'eq_warranty_type', '0', NOW() FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'eq_warranty_type' AND dict_value = 'extend');
/
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, status, create_time) SELECT 3, '免保', 'free', 'eq_warranty_type', '0', NOW() FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'eq_warranty_type' AND dict_value = 'free');
/

-- 标签打印状态 eq_label_print_status
INSERT INTO sys_dict_type (dict_name, dict_type, status, remark, create_by, create_time)
SELECT '标签打印状态', 'eq_label_print_status', '0', '资产标签是否已打印', 'admin', NOW()
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_type WHERE dict_type = 'eq_label_print_status');
/
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, status, create_time) SELECT 1, '是', 'Y', 'eq_label_print_status', '0', NOW() FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'eq_label_print_status' AND dict_value = 'Y');
/
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, status, is_default, create_time) SELECT 2, '否', 'N', 'eq_label_print_status', '0', 'Y', NOW() FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'eq_label_print_status' AND dict_value = 'N');
/

-- 盘点类型 eq_inventory_type（按科室/按68分类/按存放地点）
INSERT INTO sys_dict_type (dict_name, dict_type, status, remark, create_by, create_time)
SELECT '盘点类型', 'eq_inventory_type', '0', '资产盘点单盘点类型', 'admin', NOW()
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_type WHERE dict_type = 'eq_inventory_type');
/
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, status, create_time) SELECT 1, '按科室盘点', 'dept', 'eq_inventory_type', '0', NOW() FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'eq_inventory_type' AND dict_value = 'dept');
/
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, status, create_time) SELECT 2, '按68分类盘点', 'category68', 'eq_inventory_type', '0', NOW() FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'eq_inventory_type' AND dict_value = 'category68');
/
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, status, create_time) SELECT 3, '按存放地点盘点', 'storage_place', 'eq_inventory_type', '0', NOW() FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'eq_inventory_type' AND dict_value = 'storage_place');
/

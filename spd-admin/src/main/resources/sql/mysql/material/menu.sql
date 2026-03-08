-- ========== 耗材模块 菜单与权限 ==========
-- 建议在 table.sql、column.sql 之后执行；按「/」分段执行
-- 期初库存导入菜单：若不存在则插入，存在则跳过（项目启动时执行）
/
INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
)
SELECT
  (SELECT IFNULL(MAX(menu_id), 0) + 1 FROM (SELECT menu_id FROM sys_menu) AS t),
  '期初库存导入',
  (SELECT parent_id FROM sys_menu WHERE menu_id = 1552 LIMIT 1),
  15,
  'initialStockImport',
  'warehouse/initialStockImport/index',
  1, 0, 'C', '0', '0',
  'warehouse:initialStockImport:list',
  'upload',
  'admin', NOW(), '期初库存导入'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_name = '期初库存导入' AND parent_id = (SELECT parent_id FROM sys_menu WHERE menu_id = 1552 LIMIT 1));
/
INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
)
SELECT
  (SELECT IFNULL(MAX(menu_id), 0) + 1 FROM (SELECT menu_id FROM sys_menu) AS t),
  '期初库存导入查询',
  (SELECT menu_id FROM sys_menu WHERE menu_name = '期初库存导入' AND parent_id = (SELECT parent_id FROM sys_menu WHERE menu_id = 1552 LIMIT 1) LIMIT 1),
  1, '#', '', 1, 0, 'F', '0', '0',
  'warehouse:initialStockImport:query',
  '#', 'admin', NOW(), ''
FROM DUAL
WHERE EXISTS (SELECT 1 FROM sys_menu WHERE menu_name = '期初库存导入' AND parent_id = (SELECT parent_id FROM sys_menu WHERE menu_id = 1552 LIMIT 1))
  AND NOT EXISTS (SELECT 1 FROM sys_menu m2 WHERE m2.parent_id = (SELECT menu_id FROM sys_menu WHERE menu_name = '期初库存导入' AND parent_id = (SELECT parent_id FROM sys_menu WHERE menu_id = 1552 LIMIT 1) LIMIT 1) AND m2.perms = 'warehouse:initialStockImport:query');
/
INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
)
SELECT
  (SELECT IFNULL(MAX(menu_id), 0) + 1 FROM (SELECT menu_id FROM sys_menu) AS t),
  '期初库存导入',
  (SELECT menu_id FROM sys_menu WHERE menu_name = '期初库存导入' AND parent_id = (SELECT parent_id FROM sys_menu WHERE menu_id = 1552 LIMIT 1) LIMIT 1),
  2, '#', '', 1, 0, 'F', '0', '0',
  'warehouse:initialStockImport:import',
  '#', 'admin', NOW(), ''
FROM DUAL
WHERE EXISTS (SELECT 1 FROM sys_menu WHERE menu_name = '期初库存导入' AND parent_id = (SELECT parent_id FROM sys_menu WHERE menu_id = 1552 LIMIT 1))
  AND NOT EXISTS (SELECT 1 FROM sys_menu m2 WHERE m2.parent_id = (SELECT menu_id FROM sys_menu WHERE menu_name = '期初库存导入' AND parent_id = (SELECT parent_id FROM sys_menu WHERE menu_id = 1552 LIMIT 1) LIMIT 1) AND m2.perms = 'warehouse:initialStockImport:import');
/
INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
)
SELECT
  (SELECT IFNULL(MAX(menu_id), 0) + 1 FROM (SELECT menu_id FROM sys_menu) AS t),
  '期初库存导入审核',
  (SELECT menu_id FROM sys_menu WHERE menu_name = '期初库存导入' AND parent_id = (SELECT parent_id FROM sys_menu WHERE menu_id = 1552 LIMIT 1) LIMIT 1),
  3, '#', '', 1, 0, 'F', '0', '0',
  'warehouse:initialStockImport:audit',
  '#', 'admin', NOW(), ''
FROM DUAL
WHERE EXISTS (SELECT 1 FROM sys_menu WHERE menu_name = '期初库存导入' AND parent_id = (SELECT parent_id FROM sys_menu WHERE menu_id = 1552 LIMIT 1))
  AND NOT EXISTS (SELECT 1 FROM sys_menu m2 WHERE m2.parent_id = (SELECT menu_id FROM sys_menu WHERE menu_name = '期初库存导入' AND parent_id = (SELECT parent_id FROM sys_menu WHERE menu_id = 1552 LIMIT 1) LIMIT 1) AND m2.perms = 'warehouse:initialStockImport:audit');
/

-- 客户管理、客户菜单功能管理（挂在「系统管理」下；设为仅平台管理 is_platform=1，租户不展示、新租户默认授权不包含）
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, is_platform, create_by, create_time, remark)
SELECT 2100, '客户管理',
  COALESCE((SELECT menu_id FROM sys_menu WHERE menu_name = '系统管理' AND menu_type = 'M' LIMIT 1), 1),
  20, 'customer', 'material/system/customer/index', 1, 0, 'C', '0', '0',
  'hc:system:customer:list', 'peoples', '1', 'admin', NOW(), '耗材系统客户（租户）管理'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 2100);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, is_platform, create_by, create_time, remark)
SELECT 2102, '客户查询', 2100, 1, '#', '', 1, 0, 'F', '0', '0',
  'hc:system:customer:query', '#', '1', 'admin', NOW(), '客户查询、启停记录与时间段'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 2102);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, is_platform, create_by, create_time, remark)
SELECT 2101, '客户菜单功能管理',
  COALESCE((SELECT menu_id FROM sys_menu WHERE menu_name = '系统管理' AND menu_type = 'M' LIMIT 1), 1),
  21, 'customerMenuManage', 'material/system/customerMenuManage/index', 1, 0, 'C', '0', '0',
  'hc:system:customerMenuManage:list', 'switch', '1', 'admin', NOW(), '客户名下已具备功能的启用停用，租户不可见'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 2101);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, is_platform, create_by, create_time, remark)
SELECT 2103, '功能管理列表', 2101, 1, '#', '', 1, 0, 'F', '0', '0',
  'hc:system:customerMenuManage:list', '#', '1', 'admin', NOW(), '客户菜单功能管理列表'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 2103);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, is_platform, create_by, create_time, remark)
SELECT 2104, '功能管理查询', 2101, 2, '#', '', 1, 0, 'F', '0', '0',
  'hc:system:customerMenuManage:query', '#', '1', 'admin', NOW(), '启停用记录与时间段查询'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 2104);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, is_platform, create_by, create_time, remark)
SELECT 2105, '功能管理启停用', 2101, 3, '#', '', 1, 0, 'F', '0', '0',
  'hc:system:customerMenuManage:edit', '#', '1', 'admin', NOW(), '客户菜单功能启用停用'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 2105);
/

-- ========== 财务管理、发票管理（合并到老财务管理菜单下，避免出现两个财务管理） ==========

-- 4) 插入发票管理及按钮（若不存在）；parent 为当前唯一的「财务管理」id
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT 2201, '发票管理', 1065, 1, 'invoice', 'finance/invoice/index', 1, 0, 'C', '0', '0',
  'finance:invoice:list', 'form', 'admin', NOW(), '发票管理'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 2201);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT 2202, '发票查询', 2201, 1, '#', '', 1, 0, 'F', '0', '0',
  'finance:invoice:query', '#', 'admin', NOW(), ''
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 2202);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT 2203, '发票新增', 2201, 2, '#', '', 1, 0, 'F', '0', '0',
  'finance:invoice:add', '#', 'admin', NOW(), ''
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 2203);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT 2204, '发票修改', 2201, 3, '#', '', 1, 0, 'F', '0', '0',
  'finance:invoice:edit', '#', 'admin', NOW(), ''
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 2204);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT 2205, '发票删除', 2201, 4, '#', '', 1, 0, 'F', '0', '0',
  'finance:invoice:remove', '#', 'admin', NOW(), ''
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 2205);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT 2206, '发票审核', 2201, 5, '#', '', 1, 0, 'F', '0', '0',
  'finance:invoice:audit', '#', 'admin', NOW(), ''
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 2206);
/
-- 财务管理下：发票录入（为入库单/供应商结算单与发票关联做准备）
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT 2207, '发票录入', 1065, 2, 'invoiceEntry', 'finance/invoice/entry', 1, 0, 'C', '0', '0',
  'finance:invoice:add', 'edit', 'admin', NOW(), '发票录入，供入库单与供应商结算单关联'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 2207);
/
-- 仓库结算单（选仓库+时间范围提取未结算的入库/出库明细，审核后生成供应商结算单）
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT 2210, '仓库结算单', 1065, 3, 'whSettlement', 'finance/whSettlement/index', 1, 0, 'C', '0', '0',
  'finance:whSettlement:list', 'list', 'admin', NOW(), '仓库结算单'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 2210);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT 2212, '仓库结算单查询', 2210, 1, '#', '', 1, 0, 'F', '0', '0', 'finance:whSettlement:query', '#', 'admin', NOW(), ''
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 2212);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT 2213, '仓库结算单新增', 2210, 2, '#', '', 1, 0, 'F', '0', '0', 'finance:whSettlement:add', '#', 'admin', NOW(), ''
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 2213);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT 2214, '仓库结算单修改', 2210, 3, '#', '', 1, 0, 'F', '0', '0', 'finance:whSettlement:edit', '#', 'admin', NOW(), ''
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 2214);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT 2215, '仓库结算单删除', 2210, 4, '#', '', 1, 0, 'F', '0', '0', 'finance:whSettlement:remove', '#', 'admin', NOW(), ''
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 2215);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT 2216, '仓库结算单审核', 2210, 5, '#', '', 1, 0, 'F', '0', '0', 'finance:whSettlement:audit', '#', 'admin', NOW(), ''
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 2216);
/
-- 供应商结算单（由仓库结算单审核生成，可关联发票）
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT 2220, '供应商结算单', 1065, 4, 'suppSettlement', 'finance/suppSettlement/index', 1, 0, 'C', '0', '0',
  'finance:suppSettlement:list', 'money', 'admin', NOW(), '供应商结算单'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 2220);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT 2222, '供应商结算单查询', 2220, 1, '#', '', 1, 0, 'F', '0', '0', 'finance:suppSettlement:query', '#', 'admin', NOW(), ''
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 2222);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT 2223, '供应商结算单关联发票', 2220, 2, '#', '', 1, 0, 'F', '0', '0', 'finance:suppSettlement:linkInvoice', '#', 'admin', NOW(), ''
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 2223);
/

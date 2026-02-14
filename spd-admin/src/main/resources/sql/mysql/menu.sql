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

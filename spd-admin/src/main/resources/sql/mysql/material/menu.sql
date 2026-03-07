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

-- 客户管理、客户菜单功能管理（挂在「系统管理」下，RuoYi 默认系统管理 menu_id=1）
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT 2100, '客户管理',
  COALESCE((SELECT menu_id FROM sys_menu WHERE menu_name = '系统管理' AND menu_type = 'M' LIMIT 1), 1),
  20, 'customer', 'material/system/customer/index', 1, 0, 'C', '0', '0',
  'hc:system:customer:list', 'peoples', 'admin', NOW(), '耗材系统客户（租户）管理'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 2100);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT 2102, '客户查询', 2100, 1, '#', '', 1, 0, 'F', '0', '0',
  'hc:system:customer:query', '#', 'admin', NOW(), '客户查询、启停记录与时间段'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 2102);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT 2101, '客户菜单功能管理',
  COALESCE((SELECT menu_id FROM sys_menu WHERE menu_name = '系统管理' AND menu_type = 'M' LIMIT 1), 1),
  21, 'customerMenuManage', 'material/system/customerMenuManage/index', 1, 0, 'C', '0', '0',
  'hc:system:customerMenuManage:list', 'switch', 'admin', NOW(), '客户名下已具备功能的启用停用，租户不可见'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 2101);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT 2103, '功能管理列表', 2101, 1, '#', '', 1, 0, 'F', '0', '0',
  'hc:system:customerMenuManage:list', '#', 'admin', NOW(), '客户菜单功能管理列表'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 2103);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT 2104, '功能管理查询', 2101, 2, '#', '', 1, 0, 'F', '0', '0',
  'hc:system:customerMenuManage:query', '#', 'admin', NOW(), '启停用记录与时间段查询'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 2104);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT 2105, '功能管理启停用', 2101, 3, '#', '', 1, 0, 'F', '0', '0',
  'hc:system:customerMenuManage:edit', '#', 'admin', NOW(), '客户菜单功能启用停用'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 2105);
/

-- ========== 财务管理、发票管理（挂到一级菜单下，与仓库等同级） ==========
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT 2200, '财务管理', 0, 22, 'finance', NULL, 1, 0, 'M', '0', '0',
  '', 'money', 'admin', NOW(), '财务管理'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 2200);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT 2201, '发票管理', 2200, 1, 'invoice', 'finance/invoice/index', 1, 0, 'C', '0', '0',
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
-- 为管理员角色(role_id=1)授予财务管理、发票管理及按钮权限
INSERT INTO sys_role_menu (role_id, menu_id) SELECT 1, 2200 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE role_id = 1 AND menu_id = 2200);
/
INSERT INTO sys_role_menu (role_id, menu_id) SELECT 1, 2201 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE role_id = 1 AND menu_id = 2201);
/
INSERT INTO sys_role_menu (role_id, menu_id) SELECT 1, 2202 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE role_id = 1 AND menu_id = 2202);
/
INSERT INTO sys_role_menu (role_id, menu_id) SELECT 1, 2203 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE role_id = 1 AND menu_id = 2203);
/
INSERT INTO sys_role_menu (role_id, menu_id) SELECT 1, 2204 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE role_id = 1 AND menu_id = 2204);
/
INSERT INTO sys_role_menu (role_id, menu_id) SELECT 1, 2205 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE role_id = 1 AND menu_id = 2205);
/
INSERT INTO sys_role_menu (role_id, menu_id) SELECT 1, 2206 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE role_id = 1 AND menu_id = 2206);
/
-- 补齐 sys_post_menu：为岗位 post_id=1（admin 用户常用岗位）添加发票管理菜单权限
INSERT INTO sys_post_menu (post_id, menu_id, tenant_id) SELECT 1, 2200, NULL FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_post_menu WHERE post_id = 1 AND menu_id = 2200);
/
INSERT INTO sys_post_menu (post_id, menu_id, tenant_id) SELECT 1, 2201, NULL FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_post_menu WHERE post_id = 1 AND menu_id = 2201);
/
INSERT INTO sys_post_menu (post_id, menu_id, tenant_id) SELECT 1, 2202, NULL FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_post_menu WHERE post_id = 1 AND menu_id = 2202);
/
INSERT INTO sys_post_menu (post_id, menu_id, tenant_id) SELECT 1, 2203, NULL FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_post_menu WHERE post_id = 1 AND menu_id = 2203);
/
INSERT INTO sys_post_menu (post_id, menu_id, tenant_id) SELECT 1, 2204, NULL FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_post_menu WHERE post_id = 1 AND menu_id = 2204);
/
INSERT INTO sys_post_menu (post_id, menu_id, tenant_id) SELECT 1, 2205, NULL FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_post_menu WHERE post_id = 1 AND menu_id = 2205);
/
INSERT INTO sys_post_menu (post_id, menu_id, tenant_id) SELECT 1, 2206, NULL FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_post_menu WHERE post_id = 1 AND menu_id = 2206);
/

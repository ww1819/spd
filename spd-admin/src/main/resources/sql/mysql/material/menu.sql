-- ========== 耗材模块 菜单与权限（由 aspt.sys_menu 扫描刷新）==========
-- 生成说明：mysqldump 条件 menu_id IN (1594–1597,2100–2105,2201–2207,2210–2216,2220,2222–2223,2230–2237,2240–2247,2250–2257,2260–2265,2270–2275)
--           及 perms LIKE 'warehouse:initialStockImport%' / 'hc:system:%'
-- 执行顺序：建议在主库 sys_menu 基础数据（若依）之后执行；按「/」分段执行
-- 依赖：parent_id=1「系统管理」、1065「财务管理」、1070「盘点管理」须已存在（ID 以主库为准）
-- 写入方式：INSERT...ON DUPLICATE KEY UPDATE（不先 DELETE，避免波及 sys_role_menu 等关联）
/

-- ---------- 1) 期初库存导入（挂在「盘点管理」下；现网 menu_id 1594–1597）----------
INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
) VALUES
(1594, '期初库存导入', 1070, 15, 'initialStockImport', 'warehouse/initialStockImport/index', NULL,
 1, 0, 'C', '0', '0', 'warehouse:initialStockImport:list', 'upload',
 'admin', '2026-02-27 09:49:57', '1', '2026-03-20 11:00:13', '期初库存导入',
 '0', '1'),
(1595, '期初库存导入查询', 1594, 1, '#', '', NULL,
 1, 0, 'F', '0', '0', 'warehouse:initialStockImport:query', '#',
 'admin', '2026-02-27 09:49:57', '1', '2026-03-20 11:00:13', '',
 '0', '1'),
(1596, '期初库存导入', 1594, 2, '#', '', NULL,
 1, 0, 'F', '0', '0', 'warehouse:initialStockImport:import', '#',
 'admin', '2026-02-27 09:49:57', '1', '2026-03-20 11:00:13', '',
 '0', '1'),
(1597, '期初库存导入审核', 1594, 3, '#', '', NULL,
 1, 0, 'F', '0', '0', 'warehouse:initialStockImport:audit', '#',
 'admin', '2026-02-27 09:49:57', '1', '2026-03-20 11:00:13', '',
 '0', '1')
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  path = VALUES(path),
  component = VALUES(component),
  `query` = VALUES(`query`),
  is_frame = VALUES(is_frame),
  is_cache = VALUES(is_cache),
  menu_type = VALUES(menu_type),
  visible = VALUES(visible),
  status = VALUES(status),
  perms = VALUES(perms),
  icon = VALUES(icon),
  create_by = VALUES(create_by),
  create_time = VALUES(create_time),
  update_by = VALUES(update_by),
  update_time = VALUES(update_time),
  remark = VALUES(remark),
  is_platform = VALUES(is_platform),
  default_open_to_customer = VALUES(default_open_to_customer);
/

-- ---------- 2) 客户管理、客户菜单功能管理（系统管理下；is_platform=1 租户不展示）----------
INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
) VALUES
(2100, '客户管理', 1, 20, 'customer', 'material/system/customer/index', NULL,
 1, 0, 'C', '0', '0', 'hc:system:customer:list', 'peoples',
 'admin', '2026-03-07 22:20:51', '1', '2026-03-20 11:00:13', '耗材系统客户（租户）管理',
 '1', '0'),
(2101, '客户菜单功能管理', 1, 21, 'customerMenuManage', 'material/system/customerMenuManage/index', NULL,
 1, 0, 'C', '0', '0', 'hc:system:customerMenuManage:list', 'switch',
 'admin', '2026-03-07 22:20:51', '1', '2026-03-20 11:00:13', '客户名下已具备功能的启用停用，租户不可见',
 '1', '0'),
(2102, '客户查询', 2100, 1, '#', '', NULL,
 1, 0, 'F', '0', '0', 'hc:system:customer:query', '#',
 'admin', '2026-03-07 22:20:51', '1', '2026-03-20 11:00:13', '客户查询、启停记录与时间段',
 '1', '0'),
(2103, '功能管理列表', 2101, 1, '#', '', NULL,
 1, 0, 'F', '0', '0', 'hc:system:customerMenuManage:list', '#',
 'admin', '2026-03-07 22:20:51', '1', '2026-03-20 11:00:13', '客户菜单功能管理列表',
 '1', '0'),
(2104, '功能管理查询', 2101, 2, '#', '', NULL,
 1, 0, 'F', '0', '0', 'hc:system:customerMenuManage:query', '#',
 'admin', '2026-03-07 22:20:51', '1', '2026-03-20 11:00:13', '启停用记录与时间段查询',
 '1', '0'),
(2105, '功能管理启停用', 2101, 3, '#', '', NULL,
 1, 0, 'F', '0', '0', 'hc:system:customerMenuManage:edit', '#',
 'admin', '2026-03-07 22:20:51', '1', '2026-03-20 11:00:13', '客户菜单功能启用停用',
 '1', '0')
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  path = VALUES(path),
  component = VALUES(component),
  `query` = VALUES(`query`),
  is_frame = VALUES(is_frame),
  is_cache = VALUES(is_cache),
  menu_type = VALUES(menu_type),
  visible = VALUES(visible),
  status = VALUES(status),
  perms = VALUES(perms),
  icon = VALUES(icon),
  create_by = VALUES(create_by),
  create_time = VALUES(create_time),
  update_by = VALUES(update_by),
  update_time = VALUES(update_time),
  remark = VALUES(remark),
  is_platform = VALUES(is_platform),
  default_open_to_customer = VALUES(default_open_to_customer);
/

-- ---------- 3) 财务管理下：发票、仓库结算单、供应商结算单（合并到既有「财务管理」1065）----------
INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
) VALUES
(2201, '发票管理', 1065, 1, 'invoice', 'finance/invoice/index', NULL,
 1, 0, 'C', '0', '0', 'finance:invoice:list', 'form',
 'admin', '2026-03-08 00:48:03', '1', '2026-03-20 11:00:13', '发票信息新增、修改、查询、审核',
 '0', '0'),
(2202, '发票查询', 2201, 1, '#', '', NULL,
 1, 0, 'F', '0', '0', 'finance:invoice:query', '#',
 'admin', '2026-03-08 00:48:03', '1', '2026-03-20 11:00:13', '发票查询',
 '0', '0'),
(2203, '发票新增', 2201, 2, '#', '', NULL,
 1, 0, 'F', '0', '0', 'finance:invoice:add', '#',
 'admin', '2026-03-08 00:48:03', '1', '2026-03-20 11:00:13', '发票新增',
 '0', '0'),
(2204, '发票修改', 2201, 3, '#', '', NULL,
 1, 0, 'F', '0', '0', 'finance:invoice:edit', '#',
 'admin', '2026-03-08 00:48:03', '1', '2026-03-20 11:00:13', '发票修改（审核后不可修改）',
 '0', '0'),
(2205, '发票删除', 2201, 4, '#', '', NULL,
 1, 0, 'F', '0', '0', 'finance:invoice:remove', '#',
 'admin', '2026-03-08 00:48:03', '1', '2026-03-20 11:00:13', '发票删除',
 '0', '0'),
(2206, '发票审核', 2201, 5, '#', '', NULL,
 1, 0, 'F', '0', '0', 'finance:invoice:audit', '#',
 'admin', '2026-03-08 00:48:03', '1', '2026-03-20 11:00:13', '发票审核（审核后不可变更）',
 '0', '0'),
(2207, '发票录入', 1065, 2, 'invoiceEntry', 'finance/invoice/entry', NULL,
 1, 0, 'C', '0', '0', 'finance:invoice:add', 'edit',
 'admin', '2026-03-08 01:48:59', '1', '2026-03-20 11:00:13', '发票录入，供入库单与供应商结算单关联',
 '0', '0'),
(2210, '仓库结算单', 1065, 3, 'whSettlement', 'finance/whSettlement/index', NULL,
 1, 0, 'C', '0', '0', 'finance:whSettlement:list', 'list',
 'admin', '2026-03-08 01:50:32', '1', '2026-03-20 11:00:13', '仓库结算单',
 '0', '0'),
(2212, '仓库结算单查询', 2210, 1, '#', '', NULL,
 1, 0, 'F', '0', '0', 'finance:whSettlement:query', '#',
 'admin', '2026-03-08 01:50:32', '1', '2026-03-20 11:00:13', '',
 '0', '0'),
(2213, '仓库结算单新增', 2210, 2, '#', '', NULL,
 1, 0, 'F', '0', '0', 'finance:whSettlement:add', '#',
 'admin', '2026-03-08 01:50:32', '1', '2026-03-20 11:00:13', '',
 '0', '0'),
(2214, '仓库结算单修改', 2210, 3, '#', '', NULL,
 1, 0, 'F', '0', '0', 'finance:whSettlement:edit', '#',
 'admin', '2026-03-08 01:50:32', '1', '2026-03-20 11:00:13', '',
 '0', '0'),
(2215, '仓库结算单删除', 2210, 4, '#', '', NULL,
 1, 0, 'F', '0', '0', 'finance:whSettlement:remove', '#',
 'admin', '2026-03-08 01:50:32', '1', '2026-03-20 11:00:13', '',
 '0', '0'),
(2216, '仓库结算单审核', 2210, 5, '#', '', NULL,
 1, 0, 'F', '0', '0', 'finance:whSettlement:audit', '#',
 'admin', '2026-03-08 01:50:32', '1', '2026-03-20 11:00:13', '',
 '0', '0'),
(2220, '供应商结算单', 1065, 4, 'suppSettlement', 'finance/suppSettlement/index', NULL,
 1, 0, 'C', '0', '0', 'finance:suppSettlement:list', 'money',
 'admin', '2026-03-08 01:50:33', '1', '2026-03-20 11:00:13', '供应商结算单',
 '0', '0'),
(2222, '供应商结算单查询', 2220, 1, '#', '', NULL,
 1, 0, 'F', '0', '0', 'finance:suppSettlement:query', '#',
 'admin', '2026-03-08 01:50:33', '1', '2026-03-20 11:00:13', '',
 '0', '0'),
(2223, '供应商结算单关联发票', 2220, 2, '#', '', NULL,
 1, 0, 'F', '0', '0', 'finance:suppSettlement:linkInvoice', '#',
 'admin', '2026-03-08 01:50:33', '1', '2026-03-20 11:00:13', '',
 '0', '0')
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  path = VALUES(path),
  component = VALUES(component),
  `query` = VALUES(`query`),
  is_frame = VALUES(is_frame),
  is_cache = VALUES(is_cache),
  menu_type = VALUES(menu_type),
  visible = VALUES(visible),
  status = VALUES(status),
  perms = VALUES(perms),
  icon = VALUES(icon),
  create_by = VALUES(create_by),
  create_time = VALUES(create_time),
  update_by = VALUES(update_by),
  update_time = VALUES(update_time),
  remark = VALUES(remark),
  is_platform = VALUES(is_platform),
  default_open_to_customer = VALUES(default_open_to_customer);
/

-- ---------- 4) 科室维护（与后端 foundation/depart 一致；含导入，默认对客户开放）----------
INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  2230,
  '科室维护',
  COALESCE(
    (SELECT m.menu_id FROM sys_menu m WHERE m.menu_name = '基础资料' AND m.menu_type = 'M' ORDER BY m.menu_id LIMIT 1),
    (SELECT m.menu_id FROM sys_menu m WHERE m.menu_name = '系统管理' AND m.menu_type = 'M' ORDER BY m.menu_id LIMIT 1),
    1
  ),
  50,
  'depart',
  'foundation/depart/index',
  NULL,
  1,
  0,
  'C',
  '0',
  '0',
  'foundation:depart:list',
  'tree',
  'admin',
  NOW(),
  '1',
  NOW(),
  '耗材科室基础数据（编码/名称/简码/备注/导入）',
  '0',
  '1'
FROM DUAL
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  path = VALUES(path),
  component = VALUES(component),
  `query` = VALUES(`query`),
  is_frame = VALUES(is_frame),
  is_cache = VALUES(is_cache),
  menu_type = VALUES(menu_type),
  visible = VALUES(visible),
  status = VALUES(status),
  perms = VALUES(perms),
  icon = VALUES(icon),
  create_by = VALUES(create_by),
  create_time = VALUES(create_time),
  update_by = VALUES(update_by),
  update_time = VALUES(update_time),
  remark = VALUES(remark),
  is_platform = VALUES(is_platform),
  default_open_to_customer = VALUES(default_open_to_customer);
/

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
) VALUES
(2231, '科室查询', 2230, 1, '#', '', NULL,
 1, 0, 'F', '0', '0', 'foundation:depart:query', '#',
 'admin', NOW(), '1', NOW(), '科室详情查询',
 '0', '1'),
(2232, '科室新增', 2230, 2, '#', '', NULL,
 1, 0, 'F', '0', '0', 'foundation:depart:add', '#',
 'admin', NOW(), '1', NOW(), '',
 '0', '1'),
(2233, '科室修改', 2230, 3, '#', '', NULL,
 1, 0, 'F', '0', '0', 'foundation:depart:edit', '#',
 'admin', NOW(), '1', NOW(), '',
 '0', '1'),
(2234, '科室删除', 2230, 4, '#', '', NULL,
 1, 0, 'F', '0', '0', 'foundation:depart:remove', '#',
 'admin', NOW(), '1', NOW(), '',
 '0', '1'),
(2235, '科室导出', 2230, 5, '#', '', NULL,
 1, 0, 'F', '0', '0', 'foundation:depart:export', '#',
 'admin', NOW(), '1', NOW(), '',
 '0', '1'),
(2236, '科室导入', 2230, 6, '#', '', NULL,
 1, 0, 'F', '0', '0', 'foundation:depart:import', '#',
 'admin', NOW(), '1', NOW(), 'Excel 导入',
 '0', '1'),
(2237, '科室更新简码', 2230, 7, '#', '', NULL,
 1, 0, 'F', '0', '0', 'foundation:depart:updateReferred', '#',
 'admin', NOW(), '1', NOW(), '按名称批量生成拼音简码',
 '0', '1')
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  path = VALUES(path),
  component = VALUES(component),
  `query` = VALUES(`query`),
  is_frame = VALUES(is_frame),
  is_cache = VALUES(is_cache),
  menu_type = VALUES(menu_type),
  visible = VALUES(visible),
  status = VALUES(status),
  perms = VALUES(perms),
  icon = VALUES(icon),
  create_by = VALUES(create_by),
  create_time = VALUES(create_time),
  update_by = VALUES(update_by),
  update_time = VALUES(update_time),
  remark = VALUES(remark),
  is_platform = VALUES(is_platform),
  default_open_to_customer = VALUES(default_open_to_customer);
/

-- ---------- 5) 供应商维护（与后端 foundation/supplier 一致；含导入、变更记录）----------
INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  2240,
  '供应商维护',
  COALESCE(
    (SELECT m.menu_id FROM sys_menu m WHERE m.menu_name = '基础资料' AND m.menu_type = 'M' ORDER BY m.menu_id LIMIT 1),
    (SELECT m.menu_id FROM sys_menu m WHERE m.menu_name = '系统管理' AND m.menu_type = 'M' ORDER BY m.menu_id LIMIT 1),
    1
  ),
  51,
  'supplier',
  'foundation/supplier/index',
  NULL,
  1,
  0,
  'C',
  '0',
  '0',
  'foundation:supplier:list',
  'peoples',
  'admin',
  NOW(),
  '1',
  NOW(),
  '耗材供应商主数据（含衡水HIS供应商ID、导入、变更记录）',
  '0',
  '1'
FROM DUAL
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  path = VALUES(path),
  component = VALUES(component),
  `query` = VALUES(`query`),
  is_frame = VALUES(is_frame),
  is_cache = VALUES(is_cache),
  menu_type = VALUES(menu_type),
  visible = VALUES(visible),
  status = VALUES(status),
  perms = VALUES(perms),
  icon = VALUES(icon),
  create_by = VALUES(create_by),
  create_time = VALUES(create_time),
  update_by = VALUES(update_by),
  update_time = VALUES(update_time),
  remark = VALUES(remark),
  is_platform = VALUES(is_platform),
  default_open_to_customer = VALUES(default_open_to_customer);
/

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
) VALUES
(2241, '供应商查询', 2240, 1, '#', '', NULL,
 1, 0, 'F', '0', '0', 'foundation:supplier:query', '#',
 'admin', NOW(), '1', NOW(), '',
 '0', '1'),
(2242, '供应商新增', 2240, 2, '#', '', NULL,
 1, 0, 'F', '0', '0', 'foundation:supplier:add', '#',
 'admin', NOW(), '1', NOW(), '',
 '0', '1'),
(2243, '供应商修改', 2240, 3, '#', '', NULL,
 1, 0, 'F', '0', '0', 'foundation:supplier:edit', '#',
 'admin', NOW(), '1', NOW(), '',
 '0', '1'),
(2244, '供应商删除', 2240, 4, '#', '', NULL,
 1, 0, 'F', '0', '0', 'foundation:supplier:remove', '#',
 'admin', NOW(), '1', NOW(), '',
 '0', '1'),
(2245, '供应商导出', 2240, 5, '#', '', NULL,
 1, 0, 'F', '0', '0', 'foundation:supplier:export', '#',
 'admin', NOW(), '1', NOW(), '',
 '0', '1'),
(2246, '供应商导入', 2240, 6, '#', '', NULL,
 1, 0, 'F', '0', '0', 'foundation:supplier:import', '#',
 'admin', NOW(), '1', NOW(), 'Excel 导入',
 '0', '1'),
(2247, '供应商更新简码', 2240, 7, '#', '', NULL,
 1, 0, 'F', '0', '0', 'foundation:supplier:updateReferred', '#',
 'admin', NOW(), '1', NOW(), '',
 '0', '1')
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  path = VALUES(path),
  component = VALUES(component),
  `query` = VALUES(`query`),
  is_frame = VALUES(is_frame),
  is_cache = VALUES(is_cache),
  menu_type = VALUES(menu_type),
  visible = VALUES(visible),
  status = VALUES(status),
  perms = VALUES(perms),
  icon = VALUES(icon),
  create_by = VALUES(create_by),
  create_time = VALUES(create_time),
  update_by = VALUES(update_by),
  update_time = VALUES(update_time),
  remark = VALUES(remark),
  is_platform = VALUES(is_platform),
  default_open_to_customer = VALUES(default_open_to_customer);
/

-- ---------- 6) 生产厂家维护（与后端 foundation/factory 一致；含导入、变更记录）----------
INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  2250,
  '生产厂家维护',
  COALESCE(
    (SELECT m.menu_id FROM sys_menu m WHERE m.menu_name = '基础资料' AND m.menu_type = 'M' ORDER BY m.menu_id LIMIT 1),
    (SELECT m.menu_id FROM sys_menu m WHERE m.menu_name = '系统管理' AND m.menu_type = 'M' ORDER BY m.menu_id LIMIT 1),
    1
  ),
  52,
  'factory',
  'foundation/factory/index',
  NULL,
  1,
  0,
  'C',
  '0',
  '0',
  'foundation:factory:list',
  'tree',
  'admin',
  NOW(),
  '1',
  NOW(),
  '耗材生产厂家主数据（含衡水HIS生产厂家ID、导入、变更记录）',
  '0',
  '1'
FROM DUAL
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  path = VALUES(path),
  component = VALUES(component),
  `query` = VALUES(`query`),
  is_frame = VALUES(is_frame),
  is_cache = VALUES(is_cache),
  menu_type = VALUES(menu_type),
  visible = VALUES(visible),
  status = VALUES(status),
  perms = VALUES(perms),
  icon = VALUES(icon),
  create_by = VALUES(create_by),
  create_time = VALUES(create_time),
  update_by = VALUES(update_by),
  update_time = VALUES(update_time),
  remark = VALUES(remark),
  is_platform = VALUES(is_platform),
  default_open_to_customer = VALUES(default_open_to_customer);
/

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
) VALUES
(2251, '生产厂家查询', 2250, 1, '#', '', NULL,
 1, 0, 'F', '0', '0', 'foundation:factory:query', '#',
 'admin', NOW(), '1', NOW(), '',
 '0', '1'),
(2252, '生产厂家新增', 2250, 2, '#', '', NULL,
 1, 0, 'F', '0', '0', 'foundation:factory:add', '#',
 'admin', NOW(), '1', NOW(), '',
 '0', '1'),
(2253, '生产厂家修改', 2250, 3, '#', '', NULL,
 1, 0, 'F', '0', '0', 'foundation:factory:edit', '#',
 'admin', NOW(), '1', NOW(), '',
 '0', '1'),
(2254, '生产厂家删除', 2250, 4, '#', '', NULL,
 1, 0, 'F', '0', '0', 'foundation:factory:remove', '#',
 'admin', NOW(), '1', NOW(), '',
 '0', '1'),
(2255, '生产厂家导出', 2250, 5, '#', '', NULL,
 1, 0, 'F', '0', '0', 'foundation:factory:export', '#',
 'admin', NOW(), '1', NOW(), '',
 '0', '1'),
(2256, '生产厂家导入', 2250, 6, '#', '', NULL,
 1, 0, 'F', '0', '0', 'foundation:factory:import', '#',
 'admin', NOW(), '1', NOW(), 'Excel 导入',
 '0', '1'),
(2257, '生产厂家更新简码', 2250, 7, '#', '', NULL,
 1, 0, 'F', '0', '0', 'foundation:factory:updateReferred', '#',
 'admin', NOW(), '1', NOW(), '',
 '0', '1')
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  path = VALUES(path),
  component = VALUES(component),
  `query` = VALUES(`query`),
  is_frame = VALUES(is_frame),
  is_cache = VALUES(is_cache),
  menu_type = VALUES(menu_type),
  visible = VALUES(visible),
  status = VALUES(status),
  perms = VALUES(perms),
  icon = VALUES(icon),
  create_by = VALUES(create_by),
  create_time = VALUES(create_time),
  update_by = VALUES(update_by),
  update_time = VALUES(update_time),
  remark = VALUES(remark),
  is_platform = VALUES(is_platform),
  default_open_to_customer = VALUES(default_open_to_customer);
/

-- ---------- 7) 单位维护（与后端 foundation/unit 一致）----------
INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  2260,
  '单位维护',
  COALESCE(
    (SELECT m.menu_id FROM sys_menu m WHERE m.menu_name = '基础资料' AND m.menu_type = 'M' ORDER BY m.menu_id LIMIT 1),
    (SELECT m.menu_id FROM sys_menu m WHERE m.menu_name = '系统管理' AND m.menu_type = 'M' ORDER BY m.menu_id LIMIT 1),
    1
  ),
  53,
  'unit',
  'foundation/unit/index',
  NULL,
  1,
  0,
  'C',
  '0',
  '0',
  'foundation:unit:list',
  'skill',
  'admin',
  NOW(),
  '1',
  NOW(),
  '耗材计量单位（租户隔离、逻辑删除记录删除者/时间）',
  '0',
  '1'
FROM DUAL
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  path = VALUES(path),
  component = VALUES(component),
  `query` = VALUES(`query`),
  is_frame = VALUES(is_frame),
  is_cache = VALUES(is_cache),
  menu_type = VALUES(menu_type),
  visible = VALUES(visible),
  status = VALUES(status),
  perms = VALUES(perms),
  icon = VALUES(icon),
  create_by = VALUES(create_by),
  create_time = VALUES(create_time),
  update_by = VALUES(update_by),
  update_time = VALUES(update_time),
  remark = VALUES(remark),
  is_platform = VALUES(is_platform),
  default_open_to_customer = VALUES(default_open_to_customer);
/

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
) VALUES
(2261, '单位查询', 2260, 1, '#', '', NULL,
 1, 0, 'F', '0', '0', 'foundation:unit:query', '#',
 'admin', NOW(), '1', NOW(), '',
 '0', '1'),
(2262, '单位新增', 2260, 2, '#', '', NULL,
 1, 0, 'F', '0', '0', 'foundation:unit:add', '#',
 'admin', NOW(), '1', NOW(), '',
 '0', '1'),
(2263, '单位修改', 2260, 3, '#', '', NULL,
 1, 0, 'F', '0', '0', 'foundation:unit:edit', '#',
 'admin', NOW(), '1', NOW(), '',
 '0', '1'),
(2264, '单位删除', 2260, 4, '#', '', NULL,
 1, 0, 'F', '0', '0', 'foundation:unit:remove', '#',
 'admin', NOW(), '1', NOW(), '',
 '0', '1'),
(2265, '单位导出', 2260, 5, '#', '', NULL,
 1, 0, 'F', '0', '0', 'foundation:unit:export', '#',
 'admin', NOW(), '1', NOW(), '',
 '0', '1')
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  path = VALUES(path),
  component = VALUES(component),
  `query` = VALUES(`query`),
  is_frame = VALUES(is_frame),
  is_cache = VALUES(is_cache),
  menu_type = VALUES(menu_type),
  visible = VALUES(visible),
  status = VALUES(status),
  perms = VALUES(perms),
  icon = VALUES(icon),
  create_by = VALUES(create_by),
  create_time = VALUES(create_time),
  update_by = VALUES(update_by),
  update_time = VALUES(update_time),
  remark = VALUES(remark),
  is_platform = VALUES(is_platform),
  default_open_to_customer = VALUES(default_open_to_customer);
/

-- ---------- 8) 货位维护（与后端 foundation/location 一致）----------
INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  2270,
  '货位维护',
  COALESCE(
    (SELECT m.menu_id FROM sys_menu m WHERE m.menu_name = '基础资料' AND m.menu_type = 'M' ORDER BY m.menu_id LIMIT 1),
    (SELECT m.menu_id FROM sys_menu m WHERE m.menu_name = '系统管理' AND m.menu_type = 'M' ORDER BY m.menu_id LIMIT 1),
    1
  ),
  54,
  'location',
  'foundation/location/index',
  NULL,
  1,
  0,
  'C',
  '0',
  '0',
  'foundation:location:list',
  'tree',
  'admin',
  NOW(),
  '1',
  NOW(),
  '耗材货位（租户隔离、逻辑删除记录删除者/时间）',
  '0',
  '1'
FROM DUAL
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  path = VALUES(path),
  component = VALUES(component),
  `query` = VALUES(`query`),
  is_frame = VALUES(is_frame),
  is_cache = VALUES(is_cache),
  menu_type = VALUES(menu_type),
  visible = VALUES(visible),
  status = VALUES(status),
  perms = VALUES(perms),
  icon = VALUES(icon),
  create_by = VALUES(create_by),
  create_time = VALUES(create_time),
  update_by = VALUES(update_by),
  update_time = VALUES(update_time),
  remark = VALUES(remark),
  is_platform = VALUES(is_platform),
  default_open_to_customer = VALUES(default_open_to_customer);
/

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
) VALUES
(2271, '货位查询', 2270, 1, '#', '', NULL,
 1, 0, 'F', '0', '0', 'foundation:location:query', '#',
 'admin', NOW(), '1', NOW(), '',
 '0', '1'),
(2272, '货位新增', 2270, 2, '#', '', NULL,
 1, 0, 'F', '0', '0', 'foundation:location:add', '#',
 'admin', NOW(), '1', NOW(), '',
 '0', '1'),
(2273, '货位修改', 2270, 3, '#', '', NULL,
 1, 0, 'F', '0', '0', 'foundation:location:edit', '#',
 'admin', NOW(), '1', NOW(), '',
 '0', '1'),
(2274, '货位删除', 2270, 4, '#', '', NULL,
 1, 0, 'F', '0', '0', 'foundation:location:remove', '#',
 'admin', NOW(), '1', NOW(), '',
 '0', '1'),
(2275, '货位导出', 2270, 5, '#', '', NULL,
 1, 0, 'F', '0', '0', 'foundation:location:export', '#',
 'admin', NOW(), '1', NOW(), '',
 '0', '1')
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  path = VALUES(path),
  component = VALUES(component),
  `query` = VALUES(`query`),
  is_frame = VALUES(is_frame),
  is_cache = VALUES(is_cache),
  menu_type = VALUES(menu_type),
  visible = VALUES(visible),
  status = VALUES(status),
  perms = VALUES(perms),
  icon = VALUES(icon),
  create_by = VALUES(create_by),
  create_time = VALUES(create_time),
  update_by = VALUES(update_by),
  update_time = VALUES(update_time),
  remark = VALUES(remark),
  is_platform = VALUES(is_platform),
  default_open_to_customer = VALUES(default_open_to_customer);
/

-- ---------- 9) 库房分类维护（foundation/warehouseCategory）----------
INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  2280,
  '库房分类维护',
  COALESCE(
    (SELECT m.menu_id FROM sys_menu m WHERE m.menu_name = '基础资料' AND m.menu_type = 'M' ORDER BY m.menu_id LIMIT 1),
    (SELECT m.menu_id FROM sys_menu m WHERE m.menu_name = '系统管理' AND m.menu_type = 'M' ORDER BY m.menu_id LIMIT 1),
    1
  ),
  56,
  'warehouseCategory',
  'foundation/warehouseCategory/index',
  NULL,
  1,
  0,
  'C',
  '0',
  '0',
  'foundation:warehouseCategory:list',
  'tree',
  'admin',
  NOW(),
  '1',
  NOW(),
  '耗材库房分类（租户、备注、逻辑删除审计）',
  '0',
  '1'
FROM DUAL
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  path = VALUES(path),
  component = VALUES(component),
  `query` = VALUES(`query`),
  is_frame = VALUES(is_frame),
  is_cache = VALUES(is_cache),
  menu_type = VALUES(menu_type),
  visible = VALUES(visible),
  status = VALUES(status),
  perms = VALUES(perms),
  icon = VALUES(icon),
  create_by = VALUES(create_by),
  create_time = VALUES(create_time),
  update_by = VALUES(update_by),
  update_time = VALUES(update_time),
  remark = VALUES(remark),
  is_platform = VALUES(is_platform),
  default_open_to_customer = VALUES(default_open_to_customer);
/

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
) VALUES
(2281, '库房分类查询', 2280, 1, '#', '', NULL,
 1, 0, 'F', '0', '0', 'foundation:warehouseCategory:query', '#',
 'admin', NOW(), '1', NOW(), '',
 '0', '1'),
(2282, '库房分类新增', 2280, 2, '#', '', NULL,
 1, 0, 'F', '0', '0', 'foundation:warehouseCategory:add', '#',
 'admin', NOW(), '1', NOW(), '',
 '0', '1'),
(2283, '库房分类修改', 2280, 3, '#', '', NULL,
 1, 0, 'F', '0', '0', 'foundation:warehouseCategory:edit', '#',
 'admin', NOW(), '1', NOW(), '',
 '0', '1'),
(2284, '库房分类删除', 2280, 4, '#', '', NULL,
 1, 0, 'F', '0', '0', 'foundation:warehouseCategory:remove', '#',
 'admin', NOW(), '1', NOW(), '',
 '0', '1'),
(2285, '库房分类导出', 2280, 5, '#', '', NULL,
 1, 0, 'F', '0', '0', 'foundation:warehouseCategory:export', '#',
 'admin', NOW(), '1', NOW(), '',
 '0', '1'),
(2286, '库房分类更新简码', 2280, 6, '#', '', NULL,
 1, 0, 'F', '0', '0', 'foundation:warehouseCategory:updateReferred', '#',
 'admin', NOW(), '1', NOW(), '',
 '0', '1')
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  path = VALUES(path),
  component = VALUES(component),
  `query` = VALUES(`query`),
  is_frame = VALUES(is_frame),
  is_cache = VALUES(is_cache),
  menu_type = VALUES(menu_type),
  visible = VALUES(visible),
  status = VALUES(status),
  perms = VALUES(perms),
  icon = VALUES(icon),
  create_by = VALUES(create_by),
  create_time = VALUES(create_time),
  update_by = VALUES(update_by),
  update_time = VALUES(update_time),
  remark = VALUES(remark),
  is_platform = VALUES(is_platform),
  default_open_to_customer = VALUES(default_open_to_customer);
/

-- ---------- 10) 财务分类维护（foundation/financeCategory）----------
INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  2290,
  '财务分类维护',
  COALESCE(
    (SELECT m.menu_id FROM sys_menu m WHERE m.menu_name = '基础资料' AND m.menu_type = 'M' ORDER BY m.menu_id LIMIT 1),
    (SELECT m.menu_id FROM sys_menu m WHERE m.menu_name = '系统管理' AND m.menu_type = 'M' ORDER BY m.menu_id LIMIT 1),
    1
  ),
  57,
  'financeCategory',
  'foundation/financeCategory/index',
  NULL,
  1,
  0,
  'C',
  '0',
  '0',
  'foundation:financeCategory:list',
  'money',
  'admin',
  NOW(),
  '1',
  NOW(),
  '耗材财务分类（租户、备注、逻辑删除审计）',
  '0',
  '1'
FROM DUAL
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  path = VALUES(path),
  component = VALUES(component),
  `query` = VALUES(`query`),
  is_frame = VALUES(is_frame),
  is_cache = VALUES(is_cache),
  menu_type = VALUES(menu_type),
  visible = VALUES(visible),
  status = VALUES(status),
  perms = VALUES(perms),
  icon = VALUES(icon),
  create_by = VALUES(create_by),
  create_time = VALUES(create_time),
  update_by = VALUES(update_by),
  update_time = VALUES(update_time),
  remark = VALUES(remark),
  is_platform = VALUES(is_platform),
  default_open_to_customer = VALUES(default_open_to_customer);
/

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
) VALUES
(2291, '财务分类查询', 2290, 1, '#', '', NULL,
 1, 0, 'F', '0', '0', 'foundation:financeCategory:query', '#',
 'admin', NOW(), '1', NOW(), '',
 '0', '1'),
(2292, '财务分类新增', 2290, 2, '#', '', NULL,
 1, 0, 'F', '0', '0', 'foundation:financeCategory:add', '#',
 'admin', NOW(), '1', NOW(), '',
 '0', '1'),
(2293, '财务分类修改', 2290, 3, '#', '', NULL,
 1, 0, 'F', '0', '0', 'foundation:financeCategory:edit', '#',
 'admin', NOW(), '1', NOW(), '',
 '0', '1'),
(2294, '财务分类删除', 2290, 4, '#', '', NULL,
 1, 0, 'F', '0', '0', 'foundation:financeCategory:remove', '#',
 'admin', NOW(), '1', NOW(), '',
 '0', '1'),
(2295, '财务分类导出', 2290, 5, '#', '', NULL,
 1, 0, 'F', '0', '0', 'foundation:financeCategory:export', '#',
 'admin', NOW(), '1', NOW(), '',
 '0', '1'),
(2296, '财务分类更新简码', 2290, 6, '#', '', NULL,
 1, 0, 'F', '0', '0', 'foundation:financeCategory:updateReferred', '#',
 'admin', NOW(), '1', NOW(), '',
 '0', '1')
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  path = VALUES(path),
  component = VALUES(component),
  `query` = VALUES(`query`),
  is_frame = VALUES(is_frame),
  is_cache = VALUES(is_cache),
  menu_type = VALUES(menu_type),
  visible = VALUES(visible),
  status = VALUES(status),
  perms = VALUES(perms),
  icon = VALUES(icon),
  create_by = VALUES(create_by),
  create_time = VALUES(create_time),
  update_by = VALUES(update_by),
  update_time = VALUES(update_time),
  remark = VALUES(remark),
  is_platform = VALUES(is_platform),
  default_open_to_customer = VALUES(default_open_to_customer);
/

-- ============================================
-- 批次追溯 + 盘盈待入账 明细菜单与权限
-- 目标权限：
--   warehouse:batch:*
--   warehouse:profitLossPending:*
-- 要求：默认对客户开放（default_open_to_customer=1 + 回填 hc_customer_menu）
-- ============================================

-- 1) 找父菜单：优先挂在“盈亏单(warehouse:profitLoss:list)”下，其次“盘点管理”
SET @parent_profit_loss := (
  SELECT menu_id
  FROM sys_menu
  WHERE perms = 'warehouse:profitLoss:list'
    AND menu_type = 'C'
  ORDER BY menu_id
  LIMIT 1
);
/

SET @parent_pd := (
  SELECT menu_id
  FROM sys_menu
  WHERE menu_name = '盘点管理'
    AND menu_type = 'M'
  ORDER BY menu_id
  LIMIT 1
);
/

SET @parent_id := IFNULL(@parent_profit_loss, IFNULL(@parent_pd, 1));
/

-- 2) 批次追溯主菜单（warehouse:batch:list）
INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  (SELECT IFNULL(MAX(menu_id), 0) + 1 FROM (SELECT menu_id FROM sys_menu) t),
  '批次追溯',
  @parent_id,
  (SELECT IFNULL(MAX(order_num), 0) + 1 FROM sys_menu WHERE parent_id = @parent_id),
  'batch',
  'warehouse/batch/index',
  NULL,
  1, 0, 'C', '0', '0', 'warehouse:batch:list', 'search',
  'admin', NOW(), '1', NOW(), '批次追溯独立查询',
  '0', '1'
FROM DUAL
WHERE NOT EXISTS (
  SELECT 1 FROM sys_menu WHERE perms = 'warehouse:batch:list' AND menu_type = 'C'
);
/

SET @batch_menu_id := (
  SELECT menu_id FROM sys_menu WHERE perms = 'warehouse:batch:list' AND menu_type = 'C' ORDER BY menu_id LIMIT 1
);
/

-- 2.1 批次按钮权限
INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  (SELECT IFNULL(MAX(menu_id), 0) + 1 FROM (SELECT menu_id FROM sys_menu) t),
  '批次查询',
  @batch_menu_id,
  1,
  '#', '', NULL,
  1, 0, 'F', '0', '0', 'warehouse:batch:query', '#',
  'admin', NOW(), '1', NOW(), '',
  '0', '1'
FROM DUAL
WHERE @batch_menu_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE parent_id = @batch_menu_id AND perms = 'warehouse:batch:query');
/

-- 3) 盘盈待入账主菜单（warehouse:profitLossPending:list）
INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  (SELECT IFNULL(MAX(menu_id), 0) + 1 FROM (SELECT menu_id FROM sys_menu) t),
  '盘盈待入账',
  @parent_id,
  (SELECT IFNULL(MAX(order_num), 0) + 1 FROM sys_menu WHERE parent_id = @parent_id),
  'profitLossPending',
  'warehouse/profitLossPending/index',
  NULL,
  1, 0, 'C', '0', '0', 'warehouse:profitLossPending:list', 'time',
  'admin', NOW(), '1', NOW(), '盘盈待入账明细查询与状态变更',
  '0', '1'
FROM DUAL
WHERE NOT EXISTS (
  SELECT 1 FROM sys_menu WHERE perms = 'warehouse:profitLossPending:list' AND menu_type = 'C'
);
/

SET @pending_menu_id := (
  SELECT menu_id FROM sys_menu WHERE perms = 'warehouse:profitLossPending:list' AND menu_type = 'C' ORDER BY menu_id LIMIT 1
);
/

-- 3.1 盘盈待入账按钮权限
INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  (SELECT IFNULL(MAX(menu_id), 0) + 1 FROM (SELECT menu_id FROM sys_menu) t),
  '待入账查询',
  @pending_menu_id,
  1,
  '#', '', NULL,
  1, 0, 'F', '0', '0', 'warehouse:profitLossPending:query', '#',
  'admin', NOW(), '1', NOW(), '',
  '0', '1'
FROM DUAL
WHERE @pending_menu_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE parent_id = @pending_menu_id AND perms = 'warehouse:profitLossPending:query');
/

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  (SELECT IFNULL(MAX(menu_id), 0) + 1 FROM (SELECT menu_id FROM sys_menu) t),
  '待入账状态变更',
  @pending_menu_id,
  2,
  '#', '', NULL,
  1, 0, 'F', '0', '0', 'warehouse:profitLossPending:edit', '#',
  'admin', NOW(), '1', NOW(), '',
  '0', '1'
FROM DUAL
WHERE @pending_menu_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE parent_id = @pending_menu_id AND perms = 'warehouse:profitLossPending:edit');
/

-- 4) 默认对客户开放：回填已有客户菜单授权（hc_customer_menu）
-- 仅回填状态正常的客户，避免污染已停用租户
INSERT INTO hc_customer_menu (tenant_id, menu_id, status, is_enabled, create_by, create_time)
SELECT c.customer_id, m.menu_id, '0', '1', 'admin', NOW()
FROM sb_customer c
JOIN sys_menu m
  ON m.perms IN (
    'warehouse:batch:list',
    'warehouse:batch:query',
    'warehouse:profitLossPending:list',
    'warehouse:profitLossPending:query',
    'warehouse:profitLossPending:edit'
  )
WHERE c.hc_status = '0'
  AND NOT EXISTS (
    SELECT 1
    FROM hc_customer_menu h
    WHERE h.tenant_id = c.customer_id
      AND h.menu_id = m.menu_id
  );
/

-- ========== 耗材 sys_menu（父：2100 客户管理）==========
INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
) VALUES
(2280, '初始化数据库(平台)', 2100, 50, '#', '', NULL,
 1, 0, 'F', '0', '0', 'hc:system:customer:initDb', '#',
 'admin', NOW(), '1', NOW(), '清空租户与业务数据，仅保留admin与平台菜单字典等',
 '1', '0'),
(2281, '清理耗材数据(行)', 2100, 51, '#', '', NULL,
 1, 0, 'F', '0', '0', 'hc:system:customer:purgeHc', '#',
 'admin', NOW(), '1', NOW(), '按租户物理删除耗材侧数据',
 '1', '0')
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  perms = VALUES(perms),
  remark = VALUES(remark),
  is_platform = VALUES(is_platform),
  default_open_to_customer = VALUES(default_open_to_customer);
/

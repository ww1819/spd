-- ========== 耗材模块 菜单与权限（由 aspt.sys_menu 扫描刷新）==========
-- 文末含：采购订单(caigou/dingdan)、订单发布(caigou/publish)、到货验收(inWarehouse/audit)、盘点入库(stocktaking/in)、定数监测(monitoring/fixedNumber)、科室新品申购申请/审批、转科申请(department/departmentTransfer/apply)、调拨、hc_customer_menu 回填
-- maintenance/add_warehouse_stocktaking_in_menus.sql 与本段一致，可单独补执行
-- 生成说明：mysqldump 条件 menu_id IN (1594–1597,2100–2105,2201–2207,2210–2216,2220,2222–2223,2230–2237,2240–2247,2250–2257,2260–2265,2270–2275,2298,2280–2287,2290–2297)
--           及 perms LIKE 'warehouse:initialStockImport%' / 'hc:system:%'
-- 执行顺序：建议在主库 sys_menu 基础数据（若依）之后执行；按「/」分段执行
-- 依赖：parent_id=1「系统管理」、1065「财务管理」、1070「盘点管理」须已存在（ID 以主库为准）
-- 写入方式：INSERT...ON DUPLICATE KEY UPDATE（不先 DELETE，避免波及 sys_role_menu 等关联）
--
-- 【命名与去重】基础资料下「厂家维护」仅此一处（menu_id=2250，component=foundation/factory/index），
-- 与设备端 sb_menu 同页但分表，勿在 sys_menu 再手工插入同 path/component。
-- 「财务分类」仅此一处（menu_id=2290）；若界面出现两条，多为历史重复数据，见 spd/sql/maintenance/dedupe_hc_foundation_menus.sql。
/

-- ---------- 1) 期初库存导入（挂在「盘点管理」下；现网 menu_id 1594–1597）----------
INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  1594, '期初库存导入', 1070, 15, 'initialStockImport', 'warehouse/initialStockImport/index', NULL,
  1, 0, 'C', '0', '0', 'warehouse:initialStockImport:list', 'upload',
  'admin', '2026-02-27 09:49:57', '1', '2026-03-20 11:00:13', '期初库存导入',
  '0', '1'
FROM DUAL
WHERE
  -- 允许脚本重复执行更新同 menu_id；但若库中已存在同 perms 的其它 menu_id，则不再插入第二条
  NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='C' AND perms='warehouse:initialStockImport:list')
  OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=1594)
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
)
SELECT
  1595, '期初库存导入查询', 1594, 1, '#', '', NULL,
  1, 0, 'F', '0', '0', 'warehouse:initialStockImport:query', '#',
  'admin', '2026-02-27 09:49:57', '1', '2026-03-20 11:00:13', '',
  '0', '1'
FROM DUAL
WHERE
  NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND perms='warehouse:initialStockImport:query' AND parent_id=1594)
  OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=1595)
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  perms = VALUES(perms),
  update_by = VALUES(update_by),
  update_time = VALUES(update_time),
  remark = VALUES(remark),
  default_open_to_customer = VALUES(default_open_to_customer);
/

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  1596, '期初库存导入', 1594, 2, '#', '', NULL,
  1, 0, 'F', '0', '0', 'warehouse:initialStockImport:import', '#',
  'admin', '2026-02-27 09:49:57', '1', '2026-03-20 11:00:13', '',
  '0', '1'
FROM DUAL
WHERE
  NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND perms='warehouse:initialStockImport:import' AND parent_id=1594)
  OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=1596)
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  perms = VALUES(perms),
  update_by = VALUES(update_by),
  update_time = VALUES(update_time),
  remark = VALUES(remark),
  default_open_to_customer = VALUES(default_open_to_customer);
/

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  1597, '期初库存导入审核', 1594, 3, '#', '', NULL,
  1, 0, 'F', '0', '0', 'warehouse:initialStockImport:audit', '#',
  'admin', '2026-02-27 09:49:57', '1', '2026-03-20 11:00:13', '',
  '0', '1'
FROM DUAL
WHERE
  NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND perms='warehouse:initialStockImport:audit' AND parent_id=1594)
  OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=1597)
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  perms = VALUES(perms),
  update_by = VALUES(update_by),
  update_time = VALUES(update_time),
  remark = VALUES(remark),
  default_open_to_customer = VALUES(default_open_to_customer);
/

-- ---------- 2) 客户管理、客户菜单功能管理（系统管理下；is_platform=1 租户不展示）----------
INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  2100, '客户管理', 1, 20, 'customer', 'material/system/customer/index', NULL,
  1, 0, 'C', '0', '0', 'hc:system:customer:list', 'peoples',
  'admin', '2026-03-07 22:20:51', '1', '2026-03-20 11:00:13', '耗材系统客户（租户）管理',
  '1', '0'
FROM DUAL
WHERE
  NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='C' AND component='material/system/customer/index')
  OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=2100)
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
)
SELECT
  2101, '客户菜单功能管理', 1, 21, 'customerMenuManage', 'material/system/customerMenuManage/index', NULL,
  1, 0, 'C', '0', '0', 'hc:system:customerMenuManage:list', 'switch',
  'admin', '2026-03-07 22:20:51', '1', '2026-03-20 11:00:13', '客户名下已具备功能的启用停用，租户不可见',
  '1', '0'
FROM DUAL
WHERE
  NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='C' AND component='material/system/customerMenuManage/index')
  OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=2101)
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
)
SELECT
  2102, '客户查询', 2100, 1, '#', '', NULL,
  1, 0, 'F', '0', '0', 'hc:system:customer:query', '#',
  'admin', '2026-03-07 22:20:51', '1', '2026-03-20 11:00:13', '客户查询、启停记录与时间段',
  '1', '0'
FROM DUAL
WHERE
  NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=2100 AND perms='hc:system:customer:query')
  OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=2102)
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  perms = VALUES(perms),
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
)
SELECT
  2103, '功能管理列表', 2101, 1, '#', '', NULL,
  1, 0, 'F', '0', '0', 'hc:system:customerMenuManage:list', '#',
  'admin', '2026-03-07 22:20:51', '1', '2026-03-20 11:00:13', '客户菜单功能管理列表',
  '1', '0'
FROM DUAL
WHERE
  NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=2101 AND perms='hc:system:customerMenuManage:list')
  OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=2103)
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  perms = VALUES(perms),
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
)
SELECT
  2104, '功能管理查询', 2101, 2, '#', '', NULL,
  1, 0, 'F', '0', '0', 'hc:system:customerMenuManage:query', '#',
  'admin', '2026-03-07 22:20:51', '1', '2026-03-20 11:00:13', '启停用记录与时间段查询',
  '1', '0'
FROM DUAL
WHERE
  NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=2101 AND perms='hc:system:customerMenuManage:query')
  OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=2104)
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  perms = VALUES(perms),
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
)
SELECT
  2105, '功能管理启停用', 2101, 3, '#', '', NULL,
  1, 0, 'F', '0', '0', 'hc:system:customerMenuManage:edit', '#',
  'admin', '2026-03-07 22:20:51', '1', '2026-03-20 11:00:13', '客户菜单功能启用停用',
  '1', '0'
FROM DUAL
WHERE
  NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=2101 AND perms='hc:system:customerMenuManage:edit')
  OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=2105)
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  perms = VALUES(perms),
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
) SELECT
  2201, '发票管理', 1065, 1, 'invoice', 'finance/invoice/index', NULL,
  1, 0, 'C', '0', '0', 'finance:invoice:list', 'form',
  'admin', '2026-03-08 00:48:03', '1', '2026-03-20 11:00:13', '发票信息新增、修改、查询、审核',
  '0', '0'
FROM DUAL
WHERE
  NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='C' AND component='finance/invoice/index')
  OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=2201)
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
)
SELECT
  2202, '发票查询', 2201, 1, '#', '', NULL,
  1, 0, 'F', '0', '0', 'finance:invoice:query', '#',
  'admin', '2026-03-08 00:48:03', '1', '2026-03-20 11:00:13', '发票查询',
  '0', '0'
FROM DUAL
WHERE
  NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=2201 AND perms='finance:invoice:query')
  OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=2202)
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  perms = VALUES(perms),
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
)
SELECT
  2203, '发票新增', 2201, 2, '#', '', NULL,
  1, 0, 'F', '0', '0', 'finance:invoice:add', '#',
  'admin', '2026-03-08 00:48:03', '1', '2026-03-20 11:00:13', '发票新增',
  '0', '0'
FROM DUAL
WHERE
  NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=2201 AND perms='finance:invoice:add')
  OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=2203)
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  perms = VALUES(perms),
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
)
SELECT
  2204, '发票修改', 2201, 3, '#', '', NULL,
  1, 0, 'F', '0', '0', 'finance:invoice:edit', '#',
  'admin', '2026-03-08 00:48:03', '1', '2026-03-20 11:00:13', '发票修改（审核后不可修改）',
  '0', '0'
FROM DUAL
WHERE
  NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=2201 AND perms='finance:invoice:edit')
  OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=2204)
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  perms = VALUES(perms),
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
)
SELECT
  2205, '发票删除', 2201, 4, '#', '', NULL,
  1, 0, 'F', '0', '0', 'finance:invoice:remove', '#',
  'admin', '2026-03-08 00:48:03', '1', '2026-03-20 11:00:13', '发票删除',
  '0', '0'
FROM DUAL
WHERE
  NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=2201 AND perms='finance:invoice:remove')
  OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=2205)
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  perms = VALUES(perms),
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
)
SELECT
  2206, '发票审核', 2201, 5, '#', '', NULL,
  1, 0, 'F', '0', '0', 'finance:invoice:audit', '#',
  'admin', '2026-03-08 00:48:03', '1', '2026-03-20 11:00:13', '发票审核（审核后不可变更）',
  '0', '0'
FROM DUAL
WHERE
  NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=2201 AND perms='finance:invoice:audit')
  OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=2206)
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  perms = VALUES(perms),
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
)
SELECT
  2207, '发票录入', 1065, 2, 'invoiceEntry', 'finance/invoice/entry', NULL,
  1, 0, 'C', '0', '0', 'finance:invoice:add', 'edit',
  'admin', '2026-03-08 01:48:59', '1', '2026-03-20 11:00:13', '发票录入，供入库单与供应商结算单关联',
  '0', '0'
FROM DUAL
WHERE
  NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='C' AND component='finance/invoice/entry')
  OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=2207)
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  path = VALUES(path),
  component = VALUES(component),
  perms = VALUES(perms),
  icon = VALUES(icon),
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
)
SELECT
  2210, '仓库结算单', 1065, 3, 'whSettlement', 'finance/whSettlement/index', NULL,
  1, 0, 'C', '0', '0', 'finance:whSettlement:list', 'list',
  'admin', '2026-03-08 01:50:32', '1', '2026-03-20 11:00:13', '仓库结算单',
  '0', '0'
FROM DUAL
WHERE
  NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='C' AND component='finance/whSettlement/index')
  OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=2210)
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  path = VALUES(path),
  component = VALUES(component),
  perms = VALUES(perms),
  icon = VALUES(icon),
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
)
SELECT
  2212, '仓库结算单查询', 2210, 1, '#', '', NULL,
  1, 0, 'F', '0', '0', 'finance:whSettlement:query', '#',
  'admin', '2026-03-08 01:50:32', '1', '2026-03-20 11:00:13', '',
  '0', '0'
FROM DUAL
WHERE
  NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=2210 AND perms='finance:whSettlement:query')
  OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=2212)
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  perms = VALUES(perms),
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
)
SELECT
  2213, '仓库结算单新增', 2210, 2, '#', '', NULL,
  1, 0, 'F', '0', '0', 'finance:whSettlement:add', '#',
  'admin', '2026-03-08 01:50:32', '1', '2026-03-20 11:00:13', '',
  '0', '0'
FROM DUAL
WHERE
  NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=2210 AND perms='finance:whSettlement:add')
  OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=2213)
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  perms = VALUES(perms),
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
)
SELECT
  2214, '仓库结算单修改', 2210, 3, '#', '', NULL,
  1, 0, 'F', '0', '0', 'finance:whSettlement:edit', '#',
  'admin', '2026-03-08 01:50:32', '1', '2026-03-20 11:00:13', '',
  '0', '0'
FROM DUAL
WHERE
  NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=2210 AND perms='finance:whSettlement:edit')
  OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=2214)
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  perms = VALUES(perms),
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
)
SELECT
  2215, '仓库结算单删除', 2210, 4, '#', '', NULL,
  1, 0, 'F', '0', '0', 'finance:whSettlement:remove', '#',
  'admin', '2026-03-08 01:50:32', '1', '2026-03-20 11:00:13', '',
  '0', '0'
FROM DUAL
WHERE
  NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=2210 AND perms='finance:whSettlement:remove')
  OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=2215)
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  perms = VALUES(perms),
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
)
SELECT
  2216, '仓库结算单审核', 2210, 5, '#', '', NULL,
  1, 0, 'F', '0', '0', 'finance:whSettlement:audit', '#',
  'admin', '2026-03-08 01:50:32', '1', '2026-03-20 11:00:13', '',
  '0', '0'
FROM DUAL
WHERE
  NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=2210 AND perms='finance:whSettlement:audit')
  OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=2216)
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  perms = VALUES(perms),
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
)
SELECT
  2220, '供应商结算单', 1065, 4, 'suppSettlement', 'finance/suppSettlement/index', NULL,
  1, 0, 'C', '0', '0', 'finance:suppSettlement:list', 'money',
  'admin', '2026-03-08 01:50:33', '1', '2026-03-20 11:00:13', '供应商结算单',
  '0', '0'
FROM DUAL
WHERE
  NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='C' AND component='finance/suppSettlement/index')
  OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=2220)
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  path = VALUES(path),
  component = VALUES(component),
  perms = VALUES(perms),
  icon = VALUES(icon),
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
)
SELECT
  2222, '供应商结算单查询', 2220, 1, '#', '', NULL,
  1, 0, 'F', '0', '0', 'finance:suppSettlement:query', '#',
  'admin', '2026-03-08 01:50:33', '1', '2026-03-20 11:00:13', '',
  '0', '0'
FROM DUAL
WHERE
  NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=2220 AND perms='finance:suppSettlement:query')
  OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=2222)
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  perms = VALUES(perms),
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
)
SELECT
  2223, '供应商结算单关联发票', 2220, 2, '#', '', NULL,
  1, 0, 'F', '0', '0', 'finance:suppSettlement:linkInvoice', '#',
  'admin', '2026-03-08 01:50:33', '1', '2026-03-20 11:00:13', '',
  '0', '0'
FROM DUAL
WHERE
  NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=2220 AND perms='finance:suppSettlement:linkInvoice')
  OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=2223)
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  perms = VALUES(perms),
  update_by = VALUES(update_by),
  update_time = VALUES(update_time),
  remark = VALUES(remark),
  is_platform = VALUES(is_platform),
  default_open_to_customer = VALUES(default_open_to_customer);
/

-- ---------- 3.5) 财务管理下：结算申请（settlement/apply；「生成结算单」走新增接口，需 settlement:apply:add）----------
INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
) SELECT
  3181, '结算申请查询', 1547, 1, '#', '', NULL,
  1, 0, 'F', '0', '0', 'settlement:apply:query', '#',
  'admin', '2026-04-09 00:00:00', '1', '2026-04-09 00:00:00', '详情/查询',
  '0', '1'
FROM DUAL
WHERE
  NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=1547 AND perms='settlement:apply:query')
  OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=3181)
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  perms = VALUES(perms),
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
)
SELECT
  3182, '结算申请新增', 1547, 2, '#', '', NULL,
  1, 0, 'F', '0', '0', 'settlement:apply:add', '#',
  'admin', '2026-04-09 00:00:00', '1', '2026-04-09 00:00:00', '新增与「生成结算单」提交',
  '0', '1'
FROM DUAL
WHERE
  NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=1547 AND perms='settlement:apply:add')
  OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=3182)
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  perms = VALUES(perms),
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
)
SELECT
  3183, '结算申请修改', 1547, 3, '#', '', NULL,
  1, 0, 'F', '0', '0', 'settlement:apply:edit', '#',
  'admin', '2026-04-09 00:00:00', '1', '2026-04-09 00:00:00', '',
  '0', '1'
FROM DUAL
WHERE
  NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=1547 AND perms='settlement:apply:edit')
  OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=3183)
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  perms = VALUES(perms),
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
)
SELECT
  3184, '结算申请删除', 1547, 4, '#', '', NULL,
  1, 0, 'F', '0', '0', 'settlement:apply:remove', '#',
  'admin', '2026-04-09 00:00:00', '1', '2026-04-09 00:00:00', '',
  '0', '1'
FROM DUAL
WHERE
  NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=1547 AND perms='settlement:apply:remove')
  OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=3184)
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  perms = VALUES(perms),
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
)
SELECT
  3185, '结算申请导出', 1547, 5, '#', '', NULL,
  1, 0, 'F', '0', '0', 'settlement:apply:export', '#',
  'admin', '2026-04-09 00:00:00', '1', '2026-04-09 00:00:00', '',
  '0', '1'
FROM DUAL
WHERE
  NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=1547 AND perms='settlement:apply:export')
  OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=3185)
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  perms = VALUES(perms),
  update_by = VALUES(update_by),
  update_time = VALUES(update_time),
  remark = VALUES(remark),
  is_platform = VALUES(is_platform),
  default_open_to_customer = VALUES(default_open_to_customer);
/

-- ---------- 3.6) 财务管理下：结算审核（父菜单 menu_id=1548；前端 v-hasPermi、审核接口 settlement:audit:audit）----------
INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
) SELECT
  3186, '上传结算单', 1548, 1, '#', '', NULL,
  1, 0, 'F', '0', '0', 'settlement:audit:upload', '#',
  'admin', '2026-04-10 00:00:00', '1', '2026-04-10 00:00:00', '结算审核页',
  '0', '1'
FROM DUAL
WHERE
  NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=1548 AND perms='settlement:audit:upload')
  OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=3186)
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  perms = VALUES(perms),
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
) SELECT
  3187, '发票补录', 1548, 2, '#', '', NULL,
  1, 0, 'F', '0', '0', 'settlement:audit:invoice', '#',
  'admin', '2026-04-10 00:00:00', '1', '2026-04-10 00:00:00', '结算审核页',
  '0', '1'
FROM DUAL
WHERE
  NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=1548 AND perms='settlement:audit:invoice')
  OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=3187)
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  perms = VALUES(perms),
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
) SELECT
  3188, '结算审核修改', 1548, 3, '#', '', NULL,
  1, 0, 'F', '0', '0', 'settlement:audit:edit', '#',
  'admin', '2026-04-10 00:00:00', '1', '2026-04-10 00:00:00', '结算审核页',
  '0', '1'
FROM DUAL
WHERE
  NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=1548 AND perms='settlement:audit:edit')
  OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=3188)
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  perms = VALUES(perms),
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
) SELECT
  3189, '结算审核', 1548, 4, '#', '', NULL,
  1, 0, 'F', '0', '0', 'settlement:audit:audit', '#',
  'admin', '2026-04-10 00:00:00', '1', '2026-04-10 00:00:00', '与 SettlementController.audit 一致',
  '0', '1'
FROM DUAL
WHERE
  NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=1548 AND perms='settlement:audit:audit')
  OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=3189)
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  perms = VALUES(perms),
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
) SELECT
  3190, '结算审核删除', 1548, 5, '#', '', NULL,
  1, 0, 'F', '0', '0', 'settlement:audit:remove', '#',
  'admin', '2026-04-10 00:00:00', '1', '2026-04-10 00:00:00', '结算审核页删除',
  '0', '1'
FROM DUAL
WHERE
  NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=1548 AND perms='settlement:audit:remove')
  OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=3190)
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  perms = VALUES(perms),
  update_by = VALUES(update_by),
  update_time = VALUES(update_time),
  remark = VALUES(remark),
  is_platform = VALUES(is_platform),
  default_open_to_customer = VALUES(default_open_to_customer);
/

-- ---------- 4) 科室维护（与后端 foundation/depart 一致；含导入，默认对客户开放）----------
SET @depart_menu_id := (
  SELECT menu_id FROM sys_menu WHERE perms = 'foundation:depart:list' AND menu_type = 'C' ORDER BY menu_id LIMIT 1
);
/
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
WHERE @depart_menu_id IS NULL
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

SET @depart_menu_id := (
  SELECT menu_id FROM sys_menu WHERE perms = 'foundation:depart:list' AND menu_type = 'C' ORDER BY menu_id LIMIT 1
);
/
INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
) SELECT
  2231, '科室查询', @depart_menu_id, 1, '#', '', NULL,
  1, 0, 'F', '0', '0', 'foundation:depart:query', '#',
  'admin', NOW(), '1', NOW(), '科室详情查询',
  '0', '1'
FROM DUAL
WHERE @depart_menu_id IS NOT NULL
  AND (
    NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=@depart_menu_id AND perms='foundation:depart:query')
    OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=2231)
  )
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  perms = VALUES(perms),
  update_by = VALUES(update_by),
  update_time = VALUES(update_time),
  remark = VALUES(remark),
  default_open_to_customer = VALUES(default_open_to_customer);
/

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  2232, '科室新增', @depart_menu_id, 2, '#', '', NULL,
  1, 0, 'F', '0', '0', 'foundation:depart:add', '#',
  'admin', NOW(), '1', NOW(), '',
  '0', '1'
FROM DUAL
WHERE @depart_menu_id IS NOT NULL
  AND (
    NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=@depart_menu_id AND perms='foundation:depart:add')
    OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=2232)
  )
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  perms = VALUES(perms),
  update_by = VALUES(update_by),
  update_time = VALUES(update_time),
  remark = VALUES(remark),
  default_open_to_customer = VALUES(default_open_to_customer);
/

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  2233, '科室修改', @depart_menu_id, 3, '#', '', NULL,
  1, 0, 'F', '0', '0', 'foundation:depart:edit', '#',
  'admin', NOW(), '1', NOW(), '',
  '0', '1'
FROM DUAL
WHERE @depart_menu_id IS NOT NULL
  AND (
    NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=@depart_menu_id AND perms='foundation:depart:edit')
    OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=2233)
  )
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  perms = VALUES(perms),
  update_by = VALUES(update_by),
  update_time = VALUES(update_time),
  remark = VALUES(remark),
  default_open_to_customer = VALUES(default_open_to_customer);
/

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  2234, '科室删除', @depart_menu_id, 4, '#', '', NULL,
  1, 0, 'F', '0', '0', 'foundation:depart:remove', '#',
  'admin', NOW(), '1', NOW(), '',
  '0', '1'
FROM DUAL
WHERE @depart_menu_id IS NOT NULL
  AND (
    NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=@depart_menu_id AND perms='foundation:depart:remove')
    OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=2234)
  )
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  perms = VALUES(perms),
  update_by = VALUES(update_by),
  update_time = VALUES(update_time),
  remark = VALUES(remark),
  default_open_to_customer = VALUES(default_open_to_customer);
/

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  2235, '科室导出', @depart_menu_id, 5, '#', '', NULL,
  1, 0, 'F', '0', '0', 'foundation:depart:export', '#',
  'admin', NOW(), '1', NOW(), '',
  '0', '1'
FROM DUAL
WHERE @depart_menu_id IS NOT NULL
  AND (
    NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=@depart_menu_id AND perms='foundation:depart:export')
    OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=2235)
  )
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  perms = VALUES(perms),
  update_by = VALUES(update_by),
  update_time = VALUES(update_time),
  remark = VALUES(remark),
  default_open_to_customer = VALUES(default_open_to_customer);
/

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  2236, '科室导入', @depart_menu_id, 6, '#', '', NULL,
  1, 0, 'F', '0', '0', 'foundation:depart:import', '#',
  'admin', NOW(), '1', NOW(), 'Excel 导入',
  '0', '1'
FROM DUAL
WHERE @depart_menu_id IS NOT NULL
  AND (
    NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=@depart_menu_id AND perms='foundation:depart:import')
    OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=2236)
  )
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  perms = VALUES(perms),
  update_by = VALUES(update_by),
  update_time = VALUES(update_time),
  remark = VALUES(remark),
  default_open_to_customer = VALUES(default_open_to_customer);
/

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  2237, '科室更新简码', @depart_menu_id, 7, '#', '', NULL,
  1, 0, 'F', '0', '0', 'foundation:depart:updateReferred', '#',
  'admin', NOW(), '1', NOW(), '按名称批量生成拼音简码',
  '0', '1'
FROM DUAL
WHERE @depart_menu_id IS NOT NULL
  AND (
    NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=@depart_menu_id AND perms='foundation:depart:updateReferred')
    OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=2237)
  )
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  perms = VALUES(perms),
  update_by = VALUES(update_by),
  update_time = VALUES(update_time),
  remark = VALUES(remark),
  default_open_to_customer = VALUES(default_open_to_customer);
/

-- ---------- 5) 供应商维护（与后端 foundation/supplier 一致；含导入、变更记录）----------
SET @supplier_menu_id := (
  SELECT menu_id FROM sys_menu WHERE perms = 'foundation:supplier:list' AND menu_type = 'C' ORDER BY menu_id LIMIT 1
);
/
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
WHERE @supplier_menu_id IS NULL
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

SET @supplier_menu_id := (
  SELECT menu_id FROM sys_menu WHERE perms = 'foundation:supplier:list' AND menu_type = 'C' ORDER BY menu_id LIMIT 1
);
/
INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
) SELECT
  2241, '供应商查询', @supplier_menu_id, 1, '#', '', NULL,
  1, 0, 'F', '0', '0', 'foundation:supplier:query', '#',
  'admin', NOW(), '1', NOW(), '',
  '0', '1'
FROM DUAL
WHERE @supplier_menu_id IS NOT NULL
  AND (
    NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=@supplier_menu_id AND perms='foundation:supplier:query')
    OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=2241)
  )
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  perms = VALUES(perms),
  update_by = VALUES(update_by),
  update_time = VALUES(update_time),
  remark = VALUES(remark),
  default_open_to_customer = VALUES(default_open_to_customer);
/

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  2242, '供应商新增', @supplier_menu_id, 2, '#', '', NULL,
  1, 0, 'F', '0', '0', 'foundation:supplier:add', '#',
  'admin', NOW(), '1', NOW(), '',
  '0', '1'
FROM DUAL
WHERE @supplier_menu_id IS NOT NULL
  AND (
    NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=@supplier_menu_id AND perms='foundation:supplier:add')
    OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=2242)
  )
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  perms = VALUES(perms),
  update_by = VALUES(update_by),
  update_time = VALUES(update_time),
  remark = VALUES(remark),
  default_open_to_customer = VALUES(default_open_to_customer);
/

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  2243, '供应商修改', @supplier_menu_id, 3, '#', '', NULL,
  1, 0, 'F', '0', '0', 'foundation:supplier:edit', '#',
  'admin', NOW(), '1', NOW(), '',
  '0', '1'
FROM DUAL
WHERE @supplier_menu_id IS NOT NULL
  AND (
    NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=@supplier_menu_id AND perms='foundation:supplier:edit')
    OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=2243)
  )
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  perms = VALUES(perms),
  update_by = VALUES(update_by),
  update_time = VALUES(update_time),
  remark = VALUES(remark),
  default_open_to_customer = VALUES(default_open_to_customer);
/

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  2244, '供应商删除', @supplier_menu_id, 4, '#', '', NULL,
  1, 0, 'F', '0', '0', 'foundation:supplier:remove', '#',
  'admin', NOW(), '1', NOW(), '',
  '0', '1'
FROM DUAL
WHERE @supplier_menu_id IS NOT NULL
  AND (
    NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=@supplier_menu_id AND perms='foundation:supplier:remove')
    OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=2244)
  )
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  perms = VALUES(perms),
  update_by = VALUES(update_by),
  update_time = VALUES(update_time),
  remark = VALUES(remark),
  default_open_to_customer = VALUES(default_open_to_customer);
/

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  2245, '供应商导出', @supplier_menu_id, 5, '#', '', NULL,
  1, 0, 'F', '0', '0', 'foundation:supplier:export', '#',
  'admin', NOW(), '1', NOW(), '',
  '0', '1'
FROM DUAL
WHERE @supplier_menu_id IS NOT NULL
  AND (
    NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=@supplier_menu_id AND perms='foundation:supplier:export')
    OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=2245)
  )
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  perms = VALUES(perms),
  update_by = VALUES(update_by),
  update_time = VALUES(update_time),
  remark = VALUES(remark),
  default_open_to_customer = VALUES(default_open_to_customer);
/

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  2246, '供应商导入', @supplier_menu_id, 6, '#', '', NULL,
  1, 0, 'F', '0', '0', 'foundation:supplier:import', '#',
  'admin', NOW(), '1', NOW(), 'Excel 导入',
  '0', '1'
FROM DUAL
WHERE @supplier_menu_id IS NOT NULL
  AND (
    NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=@supplier_menu_id AND perms='foundation:supplier:import')
    OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=2246)
  )
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  perms = VALUES(perms),
  update_by = VALUES(update_by),
  update_time = VALUES(update_time),
  remark = VALUES(remark),
  default_open_to_customer = VALUES(default_open_to_customer);
/

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  2247, '供应商更新简码', @supplier_menu_id, 7, '#', '', NULL,
  1, 0, 'F', '0', '0', 'foundation:supplier:updateReferred', '#',
  'admin', NOW(), '1', NOW(), '',
  '0', '1'
FROM DUAL
WHERE @supplier_menu_id IS NOT NULL
  AND (
    NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=@supplier_menu_id AND perms='foundation:supplier:updateReferred')
    OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=2247)
  )
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  perms = VALUES(perms),
  update_by = VALUES(update_by),
  update_time = VALUES(update_time),
  remark = VALUES(remark),
  default_open_to_customer = VALUES(default_open_to_customer);
/

-- ---------- 6) 厂家维护（与后端 foundation/factory 一致；含导入、变更记录；与设备端「厂家维护」同页）----------
SET @factory_menu_id := (
  SELECT menu_id FROM sys_menu WHERE perms = 'foundation:factory:list' AND menu_type = 'C' ORDER BY menu_id LIMIT 1
);
/
INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  2250,
  '厂家维护',
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
WHERE @factory_menu_id IS NULL
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

SET @factory_menu_id := (
  SELECT menu_id FROM sys_menu WHERE perms = 'foundation:factory:list' AND menu_type = 'C' ORDER BY menu_id LIMIT 1
);
/
INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
) SELECT
  2251, '厂家查询', @factory_menu_id, 1, '#', '', NULL,
  1, 0, 'F', '0', '0', 'foundation:factory:query', '#',
  'admin', NOW(), '1', NOW(), '',
  '0', '1'
FROM DUAL
WHERE @factory_menu_id IS NOT NULL
  AND (
    NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=@factory_menu_id AND perms='foundation:factory:query')
    OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=2251)
  )
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  perms = VALUES(perms),
  update_by = VALUES(update_by),
  update_time = VALUES(update_time),
  remark = VALUES(remark),
  default_open_to_customer = VALUES(default_open_to_customer);
/

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  2252, '厂家新增', @factory_menu_id, 2, '#', '', NULL,
  1, 0, 'F', '0', '0', 'foundation:factory:add', '#',
  'admin', NOW(), '1', NOW(), '',
  '0', '1'
FROM DUAL
WHERE @factory_menu_id IS NOT NULL
  AND (
    NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=@factory_menu_id AND perms='foundation:factory:add')
    OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=2252)
  )
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  perms = VALUES(perms),
  update_by = VALUES(update_by),
  update_time = VALUES(update_time),
  remark = VALUES(remark),
  default_open_to_customer = VALUES(default_open_to_customer);
/

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  2253, '厂家修改', @factory_menu_id, 3, '#', '', NULL,
  1, 0, 'F', '0', '0', 'foundation:factory:edit', '#',
  'admin', NOW(), '1', NOW(), '',
  '0', '1'
FROM DUAL
WHERE @factory_menu_id IS NOT NULL
  AND (
    NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=@factory_menu_id AND perms='foundation:factory:edit')
    OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=2253)
  )
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  perms = VALUES(perms),
  update_by = VALUES(update_by),
  update_time = VALUES(update_time),
  remark = VALUES(remark),
  default_open_to_customer = VALUES(default_open_to_customer);
/

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  2254, '厂家删除', @factory_menu_id, 4, '#', '', NULL,
  1, 0, 'F', '0', '0', 'foundation:factory:remove', '#',
  'admin', NOW(), '1', NOW(), '',
  '0', '1'
FROM DUAL
WHERE @factory_menu_id IS NOT NULL
  AND (
    NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=@factory_menu_id AND perms='foundation:factory:remove')
    OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=2254)
  )
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  perms = VALUES(perms),
  update_by = VALUES(update_by),
  update_time = VALUES(update_time),
  remark = VALUES(remark),
  default_open_to_customer = VALUES(default_open_to_customer);
/

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  2255, '厂家导出', @factory_menu_id, 5, '#', '', NULL,
  1, 0, 'F', '0', '0', 'foundation:factory:export', '#',
  'admin', NOW(), '1', NOW(), '',
  '0', '1'
FROM DUAL
WHERE @factory_menu_id IS NOT NULL
  AND (
    NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=@factory_menu_id AND perms='foundation:factory:export')
    OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=2255)
  )
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  perms = VALUES(perms),
  update_by = VALUES(update_by),
  update_time = VALUES(update_time),
  remark = VALUES(remark),
  default_open_to_customer = VALUES(default_open_to_customer);
/

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  2256, '厂家导入', @factory_menu_id, 6, '#', '', NULL,
  1, 0, 'F', '0', '0', 'foundation:factory:import', '#',
  'admin', NOW(), '1', NOW(), 'Excel 导入',
  '0', '1'
FROM DUAL
WHERE @factory_menu_id IS NOT NULL
  AND (
    NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=@factory_menu_id AND perms='foundation:factory:import')
    OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=2256)
  )
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  perms = VALUES(perms),
  update_by = VALUES(update_by),
  update_time = VALUES(update_time),
  remark = VALUES(remark),
  default_open_to_customer = VALUES(default_open_to_customer);
/

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  2257, '厂家更新简码', @factory_menu_id, 7, '#', '', NULL,
  1, 0, 'F', '0', '0', 'foundation:factory:updateReferred', '#',
  'admin', NOW(), '1', NOW(), '',
  '0', '1'
FROM DUAL
WHERE @factory_menu_id IS NOT NULL
  AND (
    NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=@factory_menu_id AND perms='foundation:factory:updateReferred')
    OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=2257)
  )
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  perms = VALUES(perms),
  update_by = VALUES(update_by),
  update_time = VALUES(update_time),
  remark = VALUES(remark),
  default_open_to_customer = VALUES(default_open_to_customer);
/

-- ---------- 7) 单位维护（与后端 foundation/unit 一致）----------
SET @unit_menu_id := (
  SELECT menu_id FROM sys_menu WHERE perms = 'foundation:unit:list' AND menu_type = 'C' ORDER BY menu_id LIMIT 1
);
/
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
WHERE @unit_menu_id IS NULL
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

SET @unit_menu_id := (
  SELECT menu_id FROM sys_menu WHERE perms = 'foundation:unit:list' AND menu_type = 'C' ORDER BY menu_id LIMIT 1
);
/
INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
) SELECT
  2261, '单位查询', @unit_menu_id, 1, '#', '', NULL,
  1, 0, 'F', '0', '0', 'foundation:unit:query', '#',
  'admin', NOW(), '1', NOW(), '',
  '0', '1'
FROM DUAL
WHERE @unit_menu_id IS NOT NULL
  AND (
    NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=@unit_menu_id AND perms='foundation:unit:query')
    OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=2261)
  )
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  perms = VALUES(perms),
  update_by = VALUES(update_by),
  update_time = VALUES(update_time),
  remark = VALUES(remark),
  default_open_to_customer = VALUES(default_open_to_customer);
/

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  2262, '单位新增', @unit_menu_id, 2, '#', '', NULL,
  1, 0, 'F', '0', '0', 'foundation:unit:add', '#',
  'admin', NOW(), '1', NOW(), '',
  '0', '1'
FROM DUAL
WHERE @unit_menu_id IS NOT NULL
  AND (
    NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=@unit_menu_id AND perms='foundation:unit:add')
    OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=2262)
  )
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  perms = VALUES(perms),
  update_by = VALUES(update_by),
  update_time = VALUES(update_time),
  remark = VALUES(remark),
  default_open_to_customer = VALUES(default_open_to_customer);
/

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  2263, '单位修改', @unit_menu_id, 3, '#', '', NULL,
  1, 0, 'F', '0', '0', 'foundation:unit:edit', '#',
  'admin', NOW(), '1', NOW(), '',
  '0', '1'
FROM DUAL
WHERE @unit_menu_id IS NOT NULL
  AND (
    NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=@unit_menu_id AND perms='foundation:unit:edit')
    OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=2263)
  )
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  perms = VALUES(perms),
  update_by = VALUES(update_by),
  update_time = VALUES(update_time),
  remark = VALUES(remark),
  default_open_to_customer = VALUES(default_open_to_customer);
/

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  2264, '单位删除', @unit_menu_id, 4, '#', '', NULL,
  1, 0, 'F', '0', '0', 'foundation:unit:remove', '#',
  'admin', NOW(), '1', NOW(), '',
  '0', '1'
FROM DUAL
WHERE @unit_menu_id IS NOT NULL
  AND (
    NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=@unit_menu_id AND perms='foundation:unit:remove')
    OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=2264)
  )
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  perms = VALUES(perms),
  update_by = VALUES(update_by),
  update_time = VALUES(update_time),
  remark = VALUES(remark),
  default_open_to_customer = VALUES(default_open_to_customer);
/

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  2265, '单位导出', @unit_menu_id, 5, '#', '', NULL,
  1, 0, 'F', '0', '0', 'foundation:unit:export', '#',
  'admin', NOW(), '1', NOW(), '',
  '0', '1'
FROM DUAL
WHERE @unit_menu_id IS NOT NULL
  AND (
    NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=@unit_menu_id AND perms='foundation:unit:export')
    OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=2265)
  )
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  perms = VALUES(perms),
  update_by = VALUES(update_by),
  update_time = VALUES(update_time),
  remark = VALUES(remark),
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
) SELECT
  2271, '货位查询', 2270, 1, '#', '', NULL,
  1, 0, 'F', '0', '0', 'foundation:location:query', '#',
  'admin', NOW(), '1', NOW(), '',
  '0', '1'
FROM DUAL
WHERE
  NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=2270 AND perms='foundation:location:query')
  OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=2271)
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  perms = VALUES(perms),
  update_by = VALUES(update_by),
  update_time = VALUES(update_time),
  remark = VALUES(remark),
  default_open_to_customer = VALUES(default_open_to_customer);
/

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  2272, '货位新增', 2270, 2, '#', '', NULL,
  1, 0, 'F', '0', '0', 'foundation:location:add', '#',
  'admin', NOW(), '1', NOW(), '',
  '0', '1'
FROM DUAL
WHERE
  NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=2270 AND perms='foundation:location:add')
  OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=2272)
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  perms = VALUES(perms),
  update_by = VALUES(update_by),
  update_time = VALUES(update_time),
  remark = VALUES(remark),
  default_open_to_customer = VALUES(default_open_to_customer);
/

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  2273, '货位修改', 2270, 3, '#', '', NULL,
  1, 0, 'F', '0', '0', 'foundation:location:edit', '#',
  'admin', NOW(), '1', NOW(), '',
  '0', '1'
FROM DUAL
WHERE
  NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=2270 AND perms='foundation:location:edit')
  OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=2273)
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  perms = VALUES(perms),
  update_by = VALUES(update_by),
  update_time = VALUES(update_time),
  remark = VALUES(remark),
  default_open_to_customer = VALUES(default_open_to_customer);
/

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  2274, '货位删除', 2270, 4, '#', '', NULL,
  1, 0, 'F', '0', '0', 'foundation:location:remove', '#',
  'admin', NOW(), '1', NOW(), '',
  '0', '1'
FROM DUAL
WHERE
  NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=2270 AND perms='foundation:location:remove')
  OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=2274)
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  perms = VALUES(perms),
  update_by = VALUES(update_by),
  update_time = VALUES(update_time),
  remark = VALUES(remark),
  default_open_to_customer = VALUES(default_open_to_customer);
/

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  2275, '货位导出', 2270, 5, '#', '', NULL,
  1, 0, 'F', '0', '0', 'foundation:location:export', '#',
  'admin', NOW(), '1', NOW(), '',
  '0', '1'
FROM DUAL
WHERE
  NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=2270 AND perms='foundation:location:export')
  OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=2275)
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  perms = VALUES(perms),
  update_by = VALUES(update_by),
  update_time = VALUES(update_time),
  remark = VALUES(remark),
  default_open_to_customer = VALUES(default_open_to_customer);
/

-- ---------- 8.5) 耗材产品档案导入（foundation:material:import；新增导入/更新导入共用；父菜单为若依内置「耗材产品维护」等 foundation:material:list）----------
INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  2298,
  '耗材产品导入',
  (SELECT m.menu_id FROM sys_menu m WHERE m.perms = 'foundation:material:list' AND m.menu_type = 'C' ORDER BY m.menu_id LIMIT 1),
  8,
  '#', '', NULL,
  1, 0, 'F', '0', '0', 'foundation:material:import', '#',
  'admin', NOW(), '1', NOW(), '新增导入与更新导入共用本权限；default_open_to_customer=1 默认对客户开放',
  '0', '1'
FROM DUAL
WHERE EXISTS (
  SELECT 1 FROM sys_menu p WHERE p.perms = 'foundation:material:list' AND p.menu_type = 'C'
)
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
  remark = VALUES(remark),
  is_platform = VALUES(is_platform),
  default_open_to_customer = VALUES(default_open_to_customer),
  update_time = NOW();
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
) SELECT
  2281, '库房分类查询', 2280, 1, '#', '', NULL,
  1, 0, 'F', '0', '0', 'foundation:warehouseCategory:query', '#',
  'admin', NOW(), '1', NOW(), '',
  '0', '1'
FROM DUAL
WHERE
  NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=2280 AND perms='foundation:warehouseCategory:query')
  OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=2281)
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  perms = VALUES(perms),
  update_by = VALUES(update_by),
  update_time = VALUES(update_time),
  remark = VALUES(remark),
  default_open_to_customer = VALUES(default_open_to_customer);
/

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  2282, '库房分类新增', 2280, 2, '#', '', NULL,
  1, 0, 'F', '0', '0', 'foundation:warehouseCategory:add', '#',
  'admin', NOW(), '1', NOW(), '',
  '0', '1'
FROM DUAL
WHERE
  NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=2280 AND perms='foundation:warehouseCategory:add')
  OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=2282)
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  perms = VALUES(perms),
  update_by = VALUES(update_by),
  update_time = VALUES(update_time),
  remark = VALUES(remark),
  default_open_to_customer = VALUES(default_open_to_customer);
/

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  2283, '库房分类修改', 2280, 3, '#', '', NULL,
  1, 0, 'F', '0', '0', 'foundation:warehouseCategory:edit', '#',
  'admin', NOW(), '1', NOW(), '',
  '0', '1'
FROM DUAL
WHERE
  NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=2280 AND perms='foundation:warehouseCategory:edit')
  OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=2283)
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  perms = VALUES(perms),
  update_by = VALUES(update_by),
  update_time = VALUES(update_time),
  remark = VALUES(remark),
  default_open_to_customer = VALUES(default_open_to_customer);
/

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  2284, '库房分类删除', 2280, 4, '#', '', NULL,
  1, 0, 'F', '0', '0', 'foundation:warehouseCategory:remove', '#',
  'admin', NOW(), '1', NOW(), '',
  '0', '1'
FROM DUAL
WHERE
  NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=2280 AND perms='foundation:warehouseCategory:remove')
  OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=2284)
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  perms = VALUES(perms),
  update_by = VALUES(update_by),
  update_time = VALUES(update_time),
  remark = VALUES(remark),
  default_open_to_customer = VALUES(default_open_to_customer);
/

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  2285, '库房分类导出', 2280, 5, '#', '', NULL,
  1, 0, 'F', '0', '0', 'foundation:warehouseCategory:export', '#',
  'admin', NOW(), '1', NOW(), '',
  '0', '1'
FROM DUAL
WHERE
  NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=2280 AND perms='foundation:warehouseCategory:export')
  OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=2285)
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  perms = VALUES(perms),
  update_by = VALUES(update_by),
  update_time = VALUES(update_time),
  remark = VALUES(remark),
  default_open_to_customer = VALUES(default_open_to_customer);
/

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  2286, '库房分类更新简码', 2280, 6, '#', '', NULL,
  1, 0, 'F', '0', '0', 'foundation:warehouseCategory:updateReferred', '#',
  'admin', NOW(), '1', NOW(), '',
  '0', '1'
FROM DUAL
WHERE
  NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=2280 AND perms='foundation:warehouseCategory:updateReferred')
  OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=2286)
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  perms = VALUES(perms),
  update_by = VALUES(update_by),
  update_time = VALUES(update_time),
  remark = VALUES(remark),
  default_open_to_customer = VALUES(default_open_to_customer);
/

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  2287, '库房分类导入', 2280, 7, '#', '', NULL,
  1, 0, 'F', '0', '0', 'foundation:warehouseCategory:import', '#',
  'admin', NOW(), '1', NOW(), '',
  '0', '1'
FROM DUAL
WHERE
  NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=2280 AND perms='foundation:warehouseCategory:import')
  OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=2287)
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  perms = VALUES(perms),
  update_by = VALUES(update_by),
  update_time = VALUES(update_time),
  remark = VALUES(remark),
  default_open_to_customer = VALUES(default_open_to_customer);
/

-- ---------- 10) 财务分类（foundation/financeCategory；与「财务管理」模块区分，勿重复建同页菜单）----------
INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  2290,
  '财务分类',
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
) SELECT
  2291, '财务分类查询', 2290, 1, '#', '', NULL,
  1, 0, 'F', '0', '0', 'foundation:financeCategory:query', '#',
  'admin', NOW(), '1', NOW(), '',
  '0', '1'
FROM DUAL
WHERE
  NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=2290 AND perms='foundation:financeCategory:query')
  OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=2291)
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  perms = VALUES(perms),
  update_by = VALUES(update_by),
  update_time = VALUES(update_time),
  remark = VALUES(remark),
  default_open_to_customer = VALUES(default_open_to_customer);
/

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  2292, '财务分类新增', 2290, 2, '#', '', NULL,
  1, 0, 'F', '0', '0', 'foundation:financeCategory:add', '#',
  'admin', NOW(), '1', NOW(), '',
  '0', '1'
FROM DUAL
WHERE
  NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=2290 AND perms='foundation:financeCategory:add')
  OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=2292)
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  perms = VALUES(perms),
  update_by = VALUES(update_by),
  update_time = VALUES(update_time),
  remark = VALUES(remark),
  default_open_to_customer = VALUES(default_open_to_customer);
/

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  2293, '财务分类修改', 2290, 3, '#', '', NULL,
  1, 0, 'F', '0', '0', 'foundation:financeCategory:edit', '#',
  'admin', NOW(), '1', NOW(), '',
  '0', '1'
FROM DUAL
WHERE
  NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=2290 AND perms='foundation:financeCategory:edit')
  OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=2293)
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  perms = VALUES(perms),
  update_by = VALUES(update_by),
  update_time = VALUES(update_time),
  remark = VALUES(remark),
  default_open_to_customer = VALUES(default_open_to_customer);
/

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  2294, '财务分类删除', 2290, 4, '#', '', NULL,
  1, 0, 'F', '0', '0', 'foundation:financeCategory:remove', '#',
  'admin', NOW(), '1', NOW(), '',
  '0', '1'
FROM DUAL
WHERE
  NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=2290 AND perms='foundation:financeCategory:remove')
  OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=2294)
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  perms = VALUES(perms),
  update_by = VALUES(update_by),
  update_time = VALUES(update_time),
  remark = VALUES(remark),
  default_open_to_customer = VALUES(default_open_to_customer);
/

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  2295, '财务分类导出', 2290, 5, '#', '', NULL,
  1, 0, 'F', '0', '0', 'foundation:financeCategory:export', '#',
  'admin', NOW(), '1', NOW(), '',
  '0', '1'
FROM DUAL
WHERE
  NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=2290 AND perms='foundation:financeCategory:export')
  OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=2295)
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  perms = VALUES(perms),
  update_by = VALUES(update_by),
  update_time = VALUES(update_time),
  remark = VALUES(remark),
  default_open_to_customer = VALUES(default_open_to_customer);
/

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  2296, '财务分类更新简码', 2290, 6, '#', '', NULL,
  1, 0, 'F', '0', '0', 'foundation:financeCategory:updateReferred', '#',
  'admin', NOW(), '1', NOW(), '',
  '0', '1'
FROM DUAL
WHERE
  NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=2290 AND perms='foundation:financeCategory:updateReferred')
  OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=2296)
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  perms = VALUES(perms),
  update_by = VALUES(update_by),
  update_time = VALUES(update_time),
  remark = VALUES(remark),
  default_open_to_customer = VALUES(default_open_to_customer);
/

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  2297, '财务分类导入', 2290, 7, '#', '', NULL,
  1, 0, 'F', '0', '0', 'foundation:financeCategory:import', '#',
  'admin', NOW(), '1', NOW(), '新增导入与更新导入共用本权限；default_open_to_customer=1 默认对客户开放',
  '0', '1'
FROM DUAL
WHERE
  NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=2290 AND perms='foundation:financeCategory:import')
  OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=2297)
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  perms = VALUES(perms),
  update_by = VALUES(update_by),
  update_time = VALUES(update_time),
  remark = VALUES(remark),
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
-- 注意：menu_id 勿与基础资料冲突（如 2280 库房分类、2297 财务分类导入等），此处使用 3100+ 段
INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
) SELECT
  3100, '初始化数据库(平台)', 2100, 50, '#', '', NULL,
  1, 0, 'F', '0', '0', 'hc:system:customer:initDb', '#',
  'admin', NOW(), '1', NOW(), '清空租户与业务数据，仅保留admin与平台菜单字典等',
  '1', '0'
FROM DUAL
WHERE
  NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=2100 AND perms='hc:system:customer:initDb')
  OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=3100)
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  perms = VALUES(perms),
  remark = VALUES(remark),
  is_platform = VALUES(is_platform),
  default_open_to_customer = VALUES(default_open_to_customer);
/

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  3101, '清理耗材数据(行)', 2100, 51, '#', '', NULL,
  1, 0, 'F', '0', '0', 'hc:system:customer:purgeHc', '#',
  'admin', NOW(), '1', NOW(), '按租户物理删除耗材侧数据',
  '1', '0'
FROM DUAL
WHERE
  NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=2100 AND perms='hc:system:customer:purgeHc')
  OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=3101)
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  perms = VALUES(perms),
  remark = VALUES(remark),
  is_platform = VALUES(is_platform),
  default_open_to_customer = VALUES(default_open_to_customer);
/

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  3102, '清理设备数据(行)', 2100, 52, '#', '', NULL,
  1, 0, 'F', '0', '0', 'sb:system:customer:purgeEq', '#',
  'admin', NOW(), '1', NOW(), '按租户物理删除设备侧数据（耗材客户管理行内）',
  '1', '0'
FROM DUAL
WHERE
  NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=2100 AND perms='sb:system:customer:purgeEq')
  OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=3102)
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  perms = VALUES(perms),
  remark = VALUES(remark),
  is_platform = VALUES(is_platform),
  default_open_to_customer = VALUES(default_open_to_customer);
/

-- ========== 采购订单 / 订单发布 / 到货验收：菜单与按钮（CaigouDingdanController；默认对客户开放）==========
-- 采购订单 caigou/dingdan/index：caigou:dingdan:list 及 query/export/add/edit/remove/audit（列表接口 GET /caigou/dingdan/list）
-- 订单发布 caigou/publish/index：与采购订单共用 caigou:dingdan:*（独立页面，可无采购订单菜单时仍插入）
-- 到货验收 inWarehouse/audit：inWarehouse:apply:*
/

SET @caigou_parent := COALESCE(
  (SELECT m.menu_id FROM sys_menu m WHERE m.menu_type = 'M' AND m.path = 'caigou' ORDER BY m.menu_id LIMIT 1),
  (SELECT m.parent_id FROM sys_menu m WHERE m.menu_type = 'C' AND m.component = 'caigou/dingdan/index' ORDER BY m.menu_id LIMIT 1),
  1
);
/

-- ---------- 采购订单（主列表页，保证存在 perms=caigou:dingdan:list）----------
INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  (SELECT IFNULL(MAX(menu_id), 0) + 1 FROM (SELECT menu_id FROM sys_menu) t),
  '采购订单',
  @caigou_parent,
  (SELECT IFNULL(MAX(order_num), 0) + 1 FROM sys_menu WHERE parent_id = @caigou_parent),
  'dingdan',
  'caigou/dingdan/index',
  NULL,
  1, 0, 'C', '0', '0', 'caigou:dingdan:list', 'shopping',
  'admin', NOW(), '1', NOW(), '采购订单列表 CaigouDingdanController',
  '0', '1'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'C' AND component = 'caigou/dingdan/index');
/

SET @dingdan_menu_id := (
  SELECT menu_id FROM sys_menu WHERE menu_type = 'C' AND component = 'caigou/dingdan/index' ORDER BY menu_id DESC LIMIT 1
);
/

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  (SELECT IFNULL(MAX(menu_id), 0) + 1 FROM (SELECT menu_id FROM sys_menu) t),
  '采购订单查询',
  @dingdan_menu_id,
  1,
  '#', '', NULL,
  1, 0, 'F', '0', '0', 'caigou:dingdan:query', '#',
  'admin', NOW(), '1', NOW(), '',
  '0', '1'
FROM DUAL
WHERE @dingdan_menu_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE parent_id = @dingdan_menu_id AND perms = 'caigou:dingdan:query');
/

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  (SELECT IFNULL(MAX(menu_id), 0) + 1 FROM (SELECT menu_id FROM sys_menu) t),
  '采购订单导出',
  @dingdan_menu_id,
  2,
  '#', '', NULL,
  1, 0, 'F', '0', '0', 'caigou:dingdan:export', '#',
  'admin', NOW(), '1', NOW(), '',
  '0', '1'
FROM DUAL
WHERE @dingdan_menu_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE parent_id = @dingdan_menu_id AND perms = 'caigou:dingdan:export');
/

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  (SELECT IFNULL(MAX(menu_id), 0) + 1 FROM (SELECT menu_id FROM sys_menu) t),
  '采购订单新增',
  @dingdan_menu_id,
  3,
  '#', '', NULL,
  1, 0, 'F', '0', '0', 'caigou:dingdan:add', '#',
  'admin', NOW(), '1', NOW(), '',
  '0', '1'
FROM DUAL
WHERE @dingdan_menu_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE parent_id = @dingdan_menu_id AND perms = 'caigou:dingdan:add');
/

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  (SELECT IFNULL(MAX(menu_id), 0) + 1 FROM (SELECT menu_id FROM sys_menu) t),
  '采购订单修改',
  @dingdan_menu_id,
  4,
  '#', '', NULL,
  1, 0, 'F', '0', '0', 'caigou:dingdan:edit', '#',
  'admin', NOW(), '1', NOW(), '',
  '0', '1'
FROM DUAL
WHERE @dingdan_menu_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE parent_id = @dingdan_menu_id AND perms = 'caigou:dingdan:edit');
/

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  (SELECT IFNULL(MAX(menu_id), 0) + 1 FROM (SELECT menu_id FROM sys_menu) t),
  '采购订单删除',
  @dingdan_menu_id,
  5,
  '#', '', NULL,
  1, 0, 'F', '0', '0', 'caigou:dingdan:remove', '#',
  'admin', NOW(), '1', NOW(), '',
  '0', '1'
FROM DUAL
WHERE @dingdan_menu_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE parent_id = @dingdan_menu_id AND perms = 'caigou:dingdan:remove');
/

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  (SELECT IFNULL(MAX(menu_id), 0) + 1 FROM (SELECT menu_id FROM sys_menu) t),
  '采购订单审核',
  @dingdan_menu_id,
  6,
  '#', '', NULL,
  1, 0, 'F', '0', '0', 'caigou:dingdan:audit', '#',
  'admin', NOW(), '1', NOW(), '',
  '0', '1'
FROM DUAL
WHERE @dingdan_menu_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE parent_id = @dingdan_menu_id AND perms = 'caigou:dingdan:audit');
/

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  (SELECT IFNULL(MAX(menu_id), 0) + 1 FROM (SELECT menu_id FROM sys_menu) t),
  '订单发布',
  @caigou_parent,
  (SELECT IFNULL(MAX(order_num), 0) + 1 FROM sys_menu WHERE parent_id = @caigou_parent),
  'publish',
  'caigou/publish/index',
  NULL,
  1, 0, 'C', '0', '0', 'caigou:dingdan:list', 'list',
  'admin', NOW(), '1', NOW(), '采购订单发布（与采购订单共用 caigou:dingdan 接口权限）',
  '0', '1'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'C' AND component = 'caigou/publish/index');
/

SET @publish_menu_id := (
  SELECT menu_id FROM sys_menu WHERE menu_type = 'C' AND component = 'caigou/publish/index' ORDER BY menu_id DESC LIMIT 1
);
/

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  (SELECT IFNULL(MAX(menu_id), 0) + 1 FROM (SELECT menu_id FROM sys_menu) t),
  '订单发布查询',
  @publish_menu_id,
  1,
  '#', '', NULL,
  1, 0, 'F', '0', '0', 'caigou:dingdan:query', '#',
  'admin', NOW(), '1', NOW(), '',
  '0', '1'
FROM DUAL
WHERE @publish_menu_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE parent_id = @publish_menu_id AND perms = 'caigou:dingdan:query');
/

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  (SELECT IFNULL(MAX(menu_id), 0) + 1 FROM (SELECT menu_id FROM sys_menu) t),
  '订单发布导出',
  @publish_menu_id,
  2,
  '#', '', NULL,
  1, 0, 'F', '0', '0', 'caigou:dingdan:export', '#',
  'admin', NOW(), '1', NOW(), '',
  '0', '1'
FROM DUAL
WHERE @publish_menu_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE parent_id = @publish_menu_id AND perms = 'caigou:dingdan:export');
/

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  (SELECT IFNULL(MAX(menu_id), 0) + 1 FROM (SELECT menu_id FROM sys_menu) t),
  '订单发布审核',
  @publish_menu_id,
  3,
  '#', '', NULL,
  1, 0, 'F', '0', '0', 'caigou:dingdan:audit', '#',
  'admin', NOW(), '1', NOW(), '',
  '0', '1'
FROM DUAL
WHERE @publish_menu_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE parent_id = @publish_menu_id AND perms = 'caigou:dingdan:audit');
/

SET @in_wh_parent := COALESCE(
  (SELECT m.menu_id FROM sys_menu m WHERE m.menu_type = 'M' AND (m.path = 'inWarehouse' OR m.path = 'warehouse') ORDER BY m.menu_id LIMIT 1),
  (SELECT m.parent_id FROM sys_menu m WHERE m.menu_type = 'C' AND m.component = 'inWarehouse/apply/index' ORDER BY m.menu_id LIMIT 1),
  (SELECT m.parent_id FROM sys_menu m WHERE m.menu_type = 'C' AND m.perms = 'inWarehouse:apply:list' ORDER BY m.menu_id LIMIT 1),
  1
);
/

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  (SELECT IFNULL(MAX(menu_id), 0) + 1 FROM (SELECT menu_id FROM sys_menu) t),
  '到货验收',
  @in_wh_parent,
  (SELECT IFNULL(MAX(order_num), 0) + 1 FROM sys_menu WHERE parent_id = @in_wh_parent),
  'audit',
  'inWarehouse/audit/index',
  NULL,
  1, 0, 'C', '0', '0', 'inWarehouse:apply:list', 'audit',
  'admin', NOW(), '1', NOW(), '入库单到货验收（StkIoBillController inWarehouse:apply:*）',
  '0', '1'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'C' AND component = 'inWarehouse/audit/index');
/

SET @audit_menu_id := (
  SELECT menu_id FROM sys_menu WHERE menu_type = 'C' AND component = 'inWarehouse/audit/index' ORDER BY menu_id DESC LIMIT 1
);
/

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  (SELECT IFNULL(MAX(menu_id), 0) + 1 FROM (SELECT menu_id FROM sys_menu) t),
  '到货验收查询',
  @audit_menu_id,
  1,
  '#', '', NULL,
  1, 0, 'F', '0', '0', 'inWarehouse:apply:query', '#',
  'admin', NOW(), '1', NOW(), '',
  '0', '1'
FROM DUAL
WHERE @audit_menu_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE parent_id = @audit_menu_id AND perms = 'inWarehouse:apply:query');
/

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  (SELECT IFNULL(MAX(menu_id), 0) + 1 FROM (SELECT menu_id FROM sys_menu) t),
  '到货验收导出',
  @audit_menu_id,
  2,
  '#', '', NULL,
  1, 0, 'F', '0', '0', 'inWarehouse:apply:export', '#',
  'admin', NOW(), '1', NOW(), '',
  '0', '1'
FROM DUAL
WHERE @audit_menu_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE parent_id = @audit_menu_id AND perms = 'inWarehouse:apply:export');
/

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  (SELECT IFNULL(MAX(menu_id), 0) + 1 FROM (SELECT menu_id FROM sys_menu) t),
  '到货验收审核',
  @audit_menu_id,
  3,
  '#', '', NULL,
  1, 0, 'F', '0', '0', 'inWarehouse:apply:audit', '#',
  'admin', NOW(), '1', NOW(), '',
  '0', '1'
FROM DUAL
WHERE @audit_menu_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE parent_id = @audit_menu_id AND perms = 'inWarehouse:apply:audit');
/

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  (SELECT IFNULL(MAX(menu_id), 0) + 1 FROM (SELECT menu_id FROM sys_menu) t),
  '到货验收修改',
  @audit_menu_id,
  4,
  '#', '', NULL,
  1, 0, 'F', '0', '0', 'inWarehouse:apply:edit', '#',
  'admin', NOW(), '1', NOW(), '',
  '0', '1'
FROM DUAL
WHERE @audit_menu_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE parent_id = @audit_menu_id AND perms = 'inWarehouse:apply:edit');
/

-- ========== 仓库盘点入库 stocktaking/in（StkIoStocktakingController；与科室盘点 department:stocktaking 不同）==========
-- 权限：stocktaking:in:list/query/export/add/edit/remove/audit；default_open_to_customer=1
/

SET @stk_parent := COALESCE(
  (SELECT menu_id FROM sys_menu WHERE menu_name = '盘点管理' AND menu_type = 'M' ORDER BY menu_id LIMIT 1),
  1070
);
/

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  (SELECT IFNULL(MAX(menu_id), 0) + 1 FROM (SELECT menu_id FROM sys_menu) t),
  '盘点入库',
  @stk_parent,
  (SELECT IFNULL(MAX(order_num), 0) + 1 FROM sys_menu WHERE parent_id = @stk_parent),
  'stkIn',
  'stocktaking/in/index',
  NULL,
  1, 0, 'C', '0', '0', 'stocktaking:in:list', 'form',
  'admin', NOW(), '1', NOW(), '仓库盘点单 /stocktaking/in',
  '0', '1'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'C' AND component = 'stocktaking/in/index');
/

SET @stk_in_id := (
  SELECT menu_id FROM sys_menu WHERE menu_type = 'C' AND component = 'stocktaking/in/index' ORDER BY menu_id DESC LIMIT 1
);
/

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  (SELECT IFNULL(MAX(menu_id), 0) + 1 FROM (SELECT menu_id FROM sys_menu) t),
  '盘点查询',
  @stk_in_id,
  1,
  '#', '', NULL,
  1, 0, 'F', '0', '0', 'stocktaking:in:query', '#',
  'admin', NOW(), '1', NOW(), '',
  '0', '1'
FROM DUAL
WHERE @stk_in_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE parent_id = @stk_in_id AND perms = 'stocktaking:in:query');
/

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  (SELECT IFNULL(MAX(menu_id), 0) + 1 FROM (SELECT menu_id FROM sys_menu) t),
  '盘点导出',
  @stk_in_id,
  2,
  '#', '', NULL,
  1, 0, 'F', '0', '0', 'stocktaking:in:export', '#',
  'admin', NOW(), '1', NOW(), '',
  '0', '1'
FROM DUAL
WHERE @stk_in_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE parent_id = @stk_in_id AND perms = 'stocktaking:in:export');
/

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  (SELECT IFNULL(MAX(menu_id), 0) + 1 FROM (SELECT menu_id FROM sys_menu) t),
  '盘点新增',
  @stk_in_id,
  3,
  '#', '', NULL,
  1, 0, 'F', '0', '0', 'stocktaking:in:add', '#',
  'admin', NOW(), '1', NOW(), '',
  '0', '1'
FROM DUAL
WHERE @stk_in_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE parent_id = @stk_in_id AND perms = 'stocktaking:in:add');
/

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  (SELECT IFNULL(MAX(menu_id), 0) + 1 FROM (SELECT menu_id FROM sys_menu) t),
  '盘点修改',
  @stk_in_id,
  4,
  '#', '', NULL,
  1, 0, 'F', '0', '0', 'stocktaking:in:edit', '#',
  'admin', NOW(), '1', NOW(), '',
  '0', '1'
FROM DUAL
WHERE @stk_in_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE parent_id = @stk_in_id AND perms = 'stocktaking:in:edit');
/

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  (SELECT IFNULL(MAX(menu_id), 0) + 1 FROM (SELECT menu_id FROM sys_menu) t),
  '盘点删除',
  @stk_in_id,
  5,
  '#', '', NULL,
  1, 0, 'F', '0', '0', 'stocktaking:in:remove', '#',
  'admin', NOW(), '1', NOW(), '',
  '0', '1'
FROM DUAL
WHERE @stk_in_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE parent_id = @stk_in_id AND perms = 'stocktaking:in:remove');
/

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  (SELECT IFNULL(MAX(menu_id), 0) + 1 FROM (SELECT menu_id FROM sys_menu) t),
  '盘点审核',
  @stk_in_id,
  6,
  '#', '', NULL,
  1, 0, 'F', '0', '0', 'stocktaking:in:audit', '#',
  'admin', NOW(), '1', NOW(), '',
  '0', '1'
FROM DUAL
WHERE @stk_in_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE parent_id = @stk_in_id AND perms = 'stocktaking:in:audit');
/

-- ========== 定数监测 monitoring/fixedNumber（FixedNumberController）==========
-- 权限：monitoring:fixedNumber:list/add/remove/export；default_open_to_customer=1
/

SET @mon_parent := COALESCE(
  (SELECT menu_id FROM sys_menu WHERE menu_type = 'M' AND path = 'monitoring' ORDER BY menu_id LIMIT 1),
  (SELECT menu_id FROM sys_menu WHERE menu_name = '盘点管理' AND menu_type = 'M' ORDER BY menu_id LIMIT 1),
  1070
);
/

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  (SELECT IFNULL(MAX(menu_id), 0) + 1 FROM (SELECT menu_id FROM sys_menu) t),
  '定数监测',
  @mon_parent,
  (SELECT IFNULL(MAX(order_num), 0) + 1 FROM sys_menu WHERE parent_id = @mon_parent),
  'fixedNumber',
  'monitoring/fixedNumber/index',
  NULL,
  1, 0, 'C', '0', '0', 'monitoring:fixedNumber:list', 'monitor',
  'admin', NOW(), '1', NOW(), '仓库/科室定数监测 /monitoring/fixedNumber',
  '0', '1'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'C' AND component = 'monitoring/fixedNumber/index');
/

SET @fixed_num_menu := (
  SELECT menu_id FROM sys_menu WHERE component = 'monitoring/fixedNumber/index' AND menu_type = 'C' ORDER BY menu_id DESC LIMIT 1
);
/

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  (SELECT IFNULL(MAX(menu_id), 0) + 1 FROM (SELECT menu_id FROM sys_menu) t),
  '定数新增',
  @fixed_num_menu,
  1,
  '#', '', NULL,
  1, 0, 'F', '0', '0', 'monitoring:fixedNumber:add', '#',
  'admin', NOW(), '1', NOW(), '',
  '0', '1'
FROM DUAL
WHERE @fixed_num_menu IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE parent_id = @fixed_num_menu AND perms = 'monitoring:fixedNumber:add');
/

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  (SELECT IFNULL(MAX(menu_id), 0) + 1 FROM (SELECT menu_id FROM sys_menu) t),
  '定数删除',
  @fixed_num_menu,
  2,
  '#', '', NULL,
  1, 0, 'F', '0', '0', 'monitoring:fixedNumber:remove', '#',
  'admin', NOW(), '1', NOW(), '',
  '0', '1'
FROM DUAL
WHERE @fixed_num_menu IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE parent_id = @fixed_num_menu AND perms = 'monitoring:fixedNumber:remove');
/

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  (SELECT IFNULL(MAX(menu_id), 0) + 1 FROM (SELECT menu_id FROM sys_menu) t),
  '定数导出',
  @fixed_num_menu,
  3,
  '#', '', NULL,
  1, 0, 'F', '0', '0', 'monitoring:fixedNumber:export', '#',
  'admin', NOW(), '1', NOW(), '',
  '0', '1'
FROM DUAL
WHERE @fixed_num_menu IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE parent_id = @fixed_num_menu AND perms = 'monitoring:fixedNumber:export');
/

-- ========== 科室：新品申购申请 / 新品申购审批（department/newProductApply、department/newProductAudit）==========
-- 接口：GET /department/newProductApply/list 等；perms 与 NewProductApplyController、NewProductAuditController 一致；default_open_to_customer=1
/

SET @dept_parent := COALESCE(
  (SELECT m.menu_id FROM sys_menu m WHERE m.menu_type = 'M' AND m.path = 'department' ORDER BY m.menu_id LIMIT 1),
  (SELECT m.parent_id FROM sys_menu m WHERE m.menu_type = 'C' AND m.component LIKE 'department/%' ORDER BY m.menu_id LIMIT 1),
  1
);
/

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  (SELECT IFNULL(MAX(menu_id), 0) + 1 FROM (SELECT menu_id FROM sys_menu) t),
  '新品申购申请',
  @dept_parent,
  (SELECT IFNULL(MAX(order_num), 0) + 1 FROM sys_menu WHERE parent_id = @dept_parent),
  'newProductApply',
  'department/newProductApply/index',
  NULL,
  1, 0, 'C', '0', '0', 'department:newProductApply:list', 'edit',
  'admin', NOW(), '1', NOW(), '新品申购申请',
  '0', '1'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'C' AND component = 'department/newProductApply/index');
/

SET @npa_menu := (
  SELECT menu_id FROM sys_menu WHERE component = 'department/newProductApply/index' AND menu_type = 'C' ORDER BY menu_id DESC LIMIT 1
);
/

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  (SELECT IFNULL(MAX(menu_id), 0) + 1 FROM (SELECT menu_id FROM sys_menu) t),
  '新品申购查询',
  @npa_menu,
  1,
  '#', '', NULL,
  1, 0, 'F', '0', '0', 'department:newProductApply:query', '#',
  'admin', NOW(), '1', NOW(), '',
  '0', '1'
FROM DUAL
WHERE @npa_menu IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE parent_id = @npa_menu AND perms = 'department:newProductApply:query');
/

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  (SELECT IFNULL(MAX(menu_id), 0) + 1 FROM (SELECT menu_id FROM sys_menu) t),
  '新品申购新增',
  @npa_menu,
  2,
  '#', '', NULL,
  1, 0, 'F', '0', '0', 'department:newProductApply:add', '#',
  'admin', NOW(), '1', NOW(), '',
  '0', '1'
FROM DUAL
WHERE @npa_menu IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE parent_id = @npa_menu AND perms = 'department:newProductApply:add');
/

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  (SELECT IFNULL(MAX(menu_id), 0) + 1 FROM (SELECT menu_id FROM sys_menu) t),
  '新品申购导出',
  @npa_menu,
  3,
  '#', '', NULL,
  1, 0, 'F', '0', '0', 'department:newProductApply:export', '#',
  'admin', NOW(), '1', NOW(), '',
  '0', '1'
FROM DUAL
WHERE @npa_menu IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE parent_id = @npa_menu AND perms = 'department:newProductApply:export');
/

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  (SELECT IFNULL(MAX(menu_id), 0) + 1 FROM (SELECT menu_id FROM sys_menu) t),
  '新品申购修改',
  @npa_menu,
  4,
  '#', '', NULL,
  1, 0, 'F', '0', '0', 'department:newProductApply:edit', '#',
  'admin', NOW(), '1', NOW(), '',
  '0', '1'
FROM DUAL
WHERE @npa_menu IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE parent_id = @npa_menu AND perms = 'department:newProductApply:edit');
/

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  (SELECT IFNULL(MAX(menu_id), 0) + 1 FROM (SELECT menu_id FROM sys_menu) t),
  '新品申购删除',
  @npa_menu,
  5,
  '#', '', NULL,
  1, 0, 'F', '0', '0', 'department:newProductApply:remove', '#',
  'admin', NOW(), '1', NOW(), '',
  '0', '1'
FROM DUAL
WHERE @npa_menu IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE parent_id = @npa_menu AND perms = 'department:newProductApply:remove');
/

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  (SELECT IFNULL(MAX(menu_id), 0) + 1 FROM (SELECT menu_id FROM sys_menu) t),
  '新品申购审批',
  @dept_parent,
  (SELECT IFNULL(MAX(order_num), 0) + 1 FROM sys_menu WHERE parent_id = @dept_parent),
  'newProductAudit',
  'department/newProductAudit/index',
  NULL,
  1, 0, 'C', '0', '0', 'department:newProductAudit:list', 'audit',
  'admin', NOW(), '1', NOW(), '新品申购审批',
  '0', '1'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'C' AND component = 'department/newProductAudit/index');
/

SET @npr_menu := (
  SELECT menu_id FROM sys_menu WHERE component = 'department/newProductAudit/index' AND menu_type = 'C' ORDER BY menu_id DESC LIMIT 1
);
/

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  (SELECT IFNULL(MAX(menu_id), 0) + 1 FROM (SELECT menu_id FROM sys_menu) t),
  '新品审批查询',
  @npr_menu,
  1,
  '#', '', NULL,
  1, 0, 'F', '0', '0', 'department:newProductAudit:query', '#',
  'admin', NOW(), '1', NOW(), '',
  '0', '1'
FROM DUAL
WHERE @npr_menu IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE parent_id = @npr_menu AND perms = 'department:newProductAudit:query');
/

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  (SELECT IFNULL(MAX(menu_id), 0) + 1 FROM (SELECT menu_id FROM sys_menu) t),
  '新品审批导出',
  @npr_menu,
  2,
  '#', '', NULL,
  1, 0, 'F', '0', '0', 'department:newProductAudit:export', '#',
  'admin', NOW(), '1', NOW(), '',
  '0', '1'
FROM DUAL
WHERE @npr_menu IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE parent_id = @npr_menu AND perms = 'department:newProductAudit:export');
/

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  (SELECT IFNULL(MAX(menu_id), 0) + 1 FROM (SELECT menu_id FROM sys_menu) t),
  '新品审批通过',
  @npr_menu,
  3,
  '#', '', NULL,
  1, 0, 'F', '0', '0', 'department:newProductAudit:audit', '#',
  'admin', NOW(), '1', NOW(), '',
  '0', '1'
FROM DUAL
WHERE @npr_menu IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE parent_id = @npr_menu AND perms = 'department:newProductAudit:audit');
/

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  (SELECT IFNULL(MAX(menu_id), 0) + 1 FROM (SELECT menu_id FROM sys_menu) t),
  '新品审批驳回',
  @npr_menu,
  4,
  '#', '', NULL,
  1, 0, 'F', '0', '0', 'department:newProductAudit:reject', '#',
  'admin', NOW(), '1', NOW(), '',
  '0', '1'
FROM DUAL
WHERE @npr_menu IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE parent_id = @npr_menu AND perms = 'department:newProductAudit:reject');
/

-- ========== 科室：库房申请单（department/whWarehouseApply；WhWarehouseApplyController）==========
-- 接口：GET /department/whWarehouseApply/list、/{id}；POST voidWhole、voidEntry；perms 与控制器 @PreAuthorize 一致（含 department:dApply / outWarehouse:apply 兼容）
/

SET @dept_parent_wh := COALESCE(
  (SELECT m.menu_id FROM sys_menu m WHERE m.menu_type = 'M' AND m.path = 'department' ORDER BY m.menu_id LIMIT 1),
  (SELECT m.parent_id FROM sys_menu m WHERE m.menu_type = 'C' AND m.component LIKE 'department/%' ORDER BY m.menu_id LIMIT 1),
  1
);
/

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  (SELECT IFNULL(MAX(menu_id), 0) + 1 FROM (SELECT menu_id FROM sys_menu) t),
  '库房申请单',
  @dept_parent_wh,
  (SELECT IFNULL(MAX(order_num), 0) + 1 FROM sys_menu WHERE parent_id = @dept_parent_wh),
  'whWarehouseApply',
  'department/whWarehouseApply/index',
  NULL,
  1, 0, 'C', '0', '0', 'department:whWarehouseApply:list', 'list',
  'admin', NOW(), '1', NOW(), '科室申领审核按仓拆分后的库房申请单',
  '0', '0'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'C' AND component = 'department/whWarehouseApply/index');
/

SET @wh_apply_menu := (
  SELECT menu_id FROM sys_menu WHERE component = 'department/whWarehouseApply/index' AND menu_type = 'C' ORDER BY menu_id DESC LIMIT 1
);
/

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  (SELECT IFNULL(MAX(menu_id), 0) + 1 FROM (SELECT menu_id FROM sys_menu) t),
  '库房申请单查询',
  @wh_apply_menu,
  1,
  '#', '', NULL,
  1, 0, 'F', '0', '0', 'department:whWarehouseApply:query', '#',
  'admin', NOW(), '1', NOW(), '',
  '0', '0'
FROM DUAL
WHERE @wh_apply_menu IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE parent_id = @wh_apply_menu AND perms = 'department:whWarehouseApply:query');
/

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  (SELECT IFNULL(MAX(menu_id), 0) + 1 FROM (SELECT menu_id FROM sys_menu) t),
  '库房申请单整单作废',
  @wh_apply_menu,
  2,
  '#', '', NULL,
  1, 0, 'F', '0', '0', 'department:whWarehouseApply:voidWhole', '#',
  'admin', NOW(), '1', NOW(), '',
  '0', '0'
FROM DUAL
WHERE @wh_apply_menu IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE parent_id = @wh_apply_menu AND perms = 'department:whWarehouseApply:voidWhole');
/

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  (SELECT IFNULL(MAX(menu_id), 0) + 1 FROM (SELECT menu_id FROM sys_menu) t),
  '库房申请单明细作废',
  @wh_apply_menu,
  3,
  '#', '', NULL,
  1, 0, 'F', '0', '0', 'department:whWarehouseApply:voidEntry', '#',
  'admin', NOW(), '1', NOW(), '',
  '0', '0'
FROM DUAL
WHERE @wh_apply_menu IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE parent_id = @wh_apply_menu AND perms = 'department:whWarehouseApply:voidEntry');
/

-- ========== 调拨 warehouseTransfer：补全 F 按钮（WarehouseTransferController；仅 C 行 list 不够，保存需 add/edit 等）==========
-- 父菜单：调拨申请页
SET @wt_apply_menu := (
  SELECT menu_id FROM sys_menu WHERE menu_type = 'C' AND component = 'warehouseTransfer/apply/index' ORDER BY menu_id LIMIT 1
);
/

-- 审核列表与申请共用 GET /list，perms 须为 warehouseTransfer:apply:list（若曾为 warehouseTransfer:audit:list 则修正）
UPDATE sys_menu
SET perms = 'warehouseTransfer:apply:list',
    update_by = 'admin',
    update_time = NOW()
WHERE menu_type = 'C'
  AND component = 'warehouseTransfer/audit/index'
  AND perms = 'warehouseTransfer:audit:list';
/

SET @wt_audit_menu := (
  SELECT menu_id FROM sys_menu WHERE menu_type = 'C' AND component = 'warehouseTransfer/audit/index' ORDER BY menu_id LIMIT 1
);
/

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  (SELECT IFNULL(MAX(menu_id), 0) + 1 FROM (SELECT menu_id FROM sys_menu) t),
  '调拨申请查询',
  @wt_apply_menu,
  1,
  '#', '', NULL,
  1, 0, 'F', '0', '0', 'warehouseTransfer:apply:query', '#',
  'admin', NOW(), '1', NOW(), '详情/查询',
  '0', '1'
FROM DUAL
WHERE @wt_apply_menu IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE parent_id = @wt_apply_menu AND perms = 'warehouseTransfer:apply:query');
/

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  (SELECT IFNULL(MAX(menu_id), 0) + 1 FROM (SELECT menu_id FROM sys_menu) t),
  '调拨申请导出',
  @wt_apply_menu,
  2,
  '#', '', NULL,
  1, 0, 'F', '0', '0', 'warehouseTransfer:apply:export', '#',
  'admin', NOW(), '1', NOW(), '',
  '0', '1'
FROM DUAL
WHERE @wt_apply_menu IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE parent_id = @wt_apply_menu AND perms = 'warehouseTransfer:apply:export');
/

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  (SELECT IFNULL(MAX(menu_id), 0) + 1 FROM (SELECT menu_id FROM sys_menu) t),
  '调拨申请新增',
  @wt_apply_menu,
  3,
  '#', '', NULL,
  1, 0, 'F', '0', '0', 'warehouseTransfer:apply:add', '#',
  'admin', NOW(), '1', NOW(), 'POST 保存/新增',
  '0', '1'
FROM DUAL
WHERE @wt_apply_menu IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE parent_id = @wt_apply_menu AND perms = 'warehouseTransfer:apply:add');
/

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  (SELECT IFNULL(MAX(menu_id), 0) + 1 FROM (SELECT menu_id FROM sys_menu) t),
  '调拨申请修改',
  @wt_apply_menu,
  4,
  '#', '', NULL,
  1, 0, 'F', '0', '0', 'warehouseTransfer:apply:edit', '#',
  'admin', NOW(), '1', NOW(), 'PUT 保存/修改',
  '0', '1'
FROM DUAL
WHERE @wt_apply_menu IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE parent_id = @wt_apply_menu AND perms = 'warehouseTransfer:apply:edit');
/

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  (SELECT IFNULL(MAX(menu_id), 0) + 1 FROM (SELECT menu_id FROM sys_menu) t),
  '调拨申请删除',
  @wt_apply_menu,
  5,
  '#', '', NULL,
  1, 0, 'F', '0', '0', 'warehouseTransfer:apply:remove', '#',
  'admin', NOW(), '1', NOW(), '',
  '0', '1'
FROM DUAL
WHERE @wt_apply_menu IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE parent_id = @wt_apply_menu AND perms = 'warehouseTransfer:apply:remove');
/

-- 审核页：与后端 apply:audit / apply:edit 一致（仅审核角色时可只勾 1546 下按钮）
INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  (SELECT IFNULL(MAX(menu_id), 0) + 1 FROM (SELECT menu_id FROM sys_menu) t),
  '调拨审核查询',
  @wt_audit_menu,
  1,
  '#', '', NULL,
  1, 0, 'F', '0', '0', 'warehouseTransfer:apply:query', '#',
  'admin', NOW(), '1', NOW(), '与申请共用 query',
  '0', '1'
FROM DUAL
WHERE @wt_audit_menu IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE parent_id = @wt_audit_menu AND menu_type = 'F' AND perms = 'warehouseTransfer:apply:query');
/

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  (SELECT IFNULL(MAX(menu_id), 0) + 1 FROM (SELECT menu_id FROM sys_menu) t),
  '调拨审核导出',
  @wt_audit_menu,
  2,
  '#', '', NULL,
  1, 0, 'F', '0', '0', 'warehouseTransfer:apply:export', '#',
  'admin', NOW(), '1', NOW(), '',
  '0', '1'
FROM DUAL
WHERE @wt_audit_menu IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE parent_id = @wt_audit_menu AND menu_type = 'F' AND perms = 'warehouseTransfer:apply:export');
/

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  (SELECT IFNULL(MAX(menu_id), 0) + 1 FROM (SELECT menu_id FROM sys_menu) t),
  '调拨审核',
  @wt_audit_menu,
  3,
  '#', '', NULL,
  1, 0, 'F', '0', '0', 'warehouseTransfer:apply:audit', '#',
  'admin', NOW(), '1', NOW(), '',
  '0', '1'
FROM DUAL
WHERE @wt_audit_menu IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE parent_id = @wt_audit_menu AND perms = 'warehouseTransfer:apply:audit');
/

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  (SELECT IFNULL(MAX(menu_id), 0) + 1 FROM (SELECT menu_id FROM sys_menu) t),
  '调拨审核修改',
  @wt_audit_menu,
  4,
  '#', '', NULL,
  1, 0, 'F', '0', '0', 'warehouseTransfer:apply:edit', '#',
  'admin', NOW(), '1', NOW(), '',
  '0', '1'
FROM DUAL
WHERE @wt_audit_menu IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE parent_id = @wt_audit_menu AND menu_type = 'F' AND perms = 'warehouseTransfer:apply:edit');
/

-- ========== 科室：转科申请（department/departmentTransfer/apply；DepartmentTransferController /department/transfer）==========
-- perms 与 @PreAuthorize 一致：apply:list/query/export/add/edit/remove/audit；前端 views 路径 department/departmentTransfer/apply/index
/

SET @dept_parent_dt := COALESCE(
  (SELECT m.menu_id FROM sys_menu m WHERE m.menu_type = 'M' AND m.path = 'department' ORDER BY m.menu_id LIMIT 1),
  (SELECT m.parent_id FROM sys_menu m WHERE m.menu_type = 'C' AND m.component LIKE 'department/%' ORDER BY m.menu_id LIMIT 1),
  1
);
/

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  (SELECT IFNULL(MAX(menu_id), 0) + 1 FROM (SELECT menu_id FROM sys_menu) t),
  '转科申请',
  @dept_parent_dt,
  (SELECT IFNULL(MAX(order_num), 0) + 1 FROM sys_menu WHERE parent_id = @dept_parent_dt),
  'departmentTransferApply',
  'department/departmentTransfer/apply/index',
  NULL,
  1, 0, 'C', '0', '0', 'departmentTransfer:apply:list', 'guide',
  'admin', NOW(), '1', NOW(), 'BasApply billType=3，/department/transfer',
  '0', '1'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'C' AND component = 'department/departmentTransfer/apply/index');
/

SET @dt_apply_menu := (
  SELECT menu_id FROM sys_menu WHERE component = 'department/departmentTransfer/apply/index' AND menu_type = 'C' ORDER BY menu_id DESC LIMIT 1
);
/

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  (SELECT IFNULL(MAX(menu_id), 0) + 1 FROM (SELECT menu_id FROM sys_menu) t),
  '转科申请查询',
  @dt_apply_menu,
  1,
  '#', '', NULL,
  1, 0, 'F', '0', '0', 'departmentTransfer:apply:query', '#',
  'admin', NOW(), '1', NOW(), 'GET /{id}',
  '0', '1'
FROM DUAL
WHERE @dt_apply_menu IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE parent_id = @dt_apply_menu AND perms = 'departmentTransfer:apply:query');
/

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  (SELECT IFNULL(MAX(menu_id), 0) + 1 FROM (SELECT menu_id FROM sys_menu) t),
  '转科申请导出',
  @dt_apply_menu,
  2,
  '#', '', NULL,
  1, 0, 'F', '0', '0', 'departmentTransfer:apply:export', '#',
  'admin', NOW(), '1', NOW(), '',
  '0', '1'
FROM DUAL
WHERE @dt_apply_menu IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE parent_id = @dt_apply_menu AND perms = 'departmentTransfer:apply:export');
/

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  (SELECT IFNULL(MAX(menu_id), 0) + 1 FROM (SELECT menu_id FROM sys_menu) t),
  '转科申请新增',
  @dt_apply_menu,
  3,
  '#', '', NULL,
  1, 0, 'F', '0', '0', 'departmentTransfer:apply:add', '#',
  'admin', NOW(), '1', NOW(), 'POST',
  '0', '1'
FROM DUAL
WHERE @dt_apply_menu IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE parent_id = @dt_apply_menu AND perms = 'departmentTransfer:apply:add');
/

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  (SELECT IFNULL(MAX(menu_id), 0) + 1 FROM (SELECT menu_id FROM sys_menu) t),
  '转科申请修改',
  @dt_apply_menu,
  4,
  '#', '', NULL,
  1, 0, 'F', '0', '0', 'departmentTransfer:apply:edit', '#',
  'admin', NOW(), '1', NOW(), 'PUT',
  '0', '1'
FROM DUAL
WHERE @dt_apply_menu IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE parent_id = @dt_apply_menu AND perms = 'departmentTransfer:apply:edit');
/

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  (SELECT IFNULL(MAX(menu_id), 0) + 1 FROM (SELECT menu_id FROM sys_menu) t),
  '转科申请删除',
  @dt_apply_menu,
  5,
  '#', '', NULL,
  1, 0, 'F', '0', '0', 'departmentTransfer:apply:remove', '#',
  'admin', NOW(), '1', NOW(), '',
  '0', '1'
FROM DUAL
WHERE @dt_apply_menu IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE parent_id = @dt_apply_menu AND perms = 'departmentTransfer:apply:remove');
/

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  (SELECT IFNULL(MAX(menu_id), 0) + 1 FROM (SELECT menu_id FROM sys_menu) t),
  '转科申请审核',
  @dt_apply_menu,
  6,
  '#', '', NULL,
  1, 0, 'F', '0', '0', 'departmentTransfer:apply:audit', '#',
  'admin', NOW(), '1', NOW(), 'PUT /auditApply（科室申领审核页若调同一接口也需此权限）',
  '0', '1'
FROM DUAL
WHERE @dt_apply_menu IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE parent_id = @dt_apply_menu AND perms = 'departmentTransfer:apply:audit');
/

-- 默认对客户开放：回填 hc_customer_menu（采购订单、订单发布、到货验收、盘点入库、定数监测、科室新品申购）
INSERT INTO hc_customer_menu (tenant_id, menu_id, status, is_enabled, create_by, create_time)
SELECT c.customer_id, m.menu_id, '0', '1', 'admin', NOW()
FROM sb_customer c
JOIN sys_menu m
  ON m.perms IN (
    'caigou:dingdan:list',
    'caigou:dingdan:query',
    'caigou:dingdan:export',
    'caigou:dingdan:add',
    'caigou:dingdan:edit',
    'caigou:dingdan:remove',
    'caigou:dingdan:audit',
    'inWarehouse:apply:list',
    'inWarehouse:apply:query',
    'inWarehouse:apply:export',
    'inWarehouse:apply:audit',
    'inWarehouse:apply:edit',
    'stocktaking:in:list',
    'stocktaking:in:query',
    'stocktaking:in:export',
    'stocktaking:in:add',
    'stocktaking:in:edit',
    'stocktaking:in:remove',
    'stocktaking:in:audit',
    'monitoring:fixedNumber:list',
    'monitoring:fixedNumber:add',
    'monitoring:fixedNumber:remove',
    'monitoring:fixedNumber:export',
    'department:newProductApply:list',
    'department:newProductApply:query',
    'department:newProductApply:add',
    'department:newProductApply:export',
    'department:newProductApply:edit',
    'department:newProductApply:remove',
    'department:newProductAudit:list',
    'department:newProductAudit:query',
    'department:newProductAudit:export',
    'department:newProductAudit:audit',
    'department:newProductAudit:reject',
    'warehouseTransfer:apply:list',
    'warehouseTransfer:apply:query',
    'warehouseTransfer:apply:export',
    'warehouseTransfer:apply:add',
    'warehouseTransfer:apply:edit',
    'warehouseTransfer:apply:remove',
    'warehouseTransfer:apply:audit',
    'departmentTransfer:apply:list',
    'departmentTransfer:apply:query',
    'departmentTransfer:apply:export',
    'departmentTransfer:apply:add',
    'departmentTransfer:apply:edit',
    'departmentTransfer:apply:remove',
    'departmentTransfer:apply:audit'
  )
WHERE c.hc_status = '0'
  AND NOT EXISTS (
    SELECT 1 FROM hc_customer_menu h
    WHERE h.tenant_id = c.customer_id AND h.menu_id = m.menu_id
  );
/

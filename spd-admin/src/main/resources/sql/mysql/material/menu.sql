-- ========== 耗材模块 菜单与权限（由 sys_menu 扫描刷新）==========
-- 文末含：采购订单(caigou/dingdan)、订单发布(caigou/publish)、到货验收(inWarehouse/audit)、盘点入库(stocktaking/in)、定数监测(monitoring/fixedNumber)、科室新品申购申请/审批、转科申请(department/departmentTransfer/apply)、调拨、hc_customer_menu 回填
-- maintenance/add_warehouse_stocktaking_in_menus.sql 与本段一致，可单独补执行
-- 生成说明：mysqldump 条件 menu_id IN (1594–1597,2100–2105,3103–3107,2201–2207,2210–2216,2220,2222–2223,2230–2237,2240–2247,2250–2257,2260–2265,2270–2275,2298,2280–2287,2290–2297,2300–2304)
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

INSERT INTO sys_menu (  menu_id, menu_name, parent_id, order_num, path, component, `query`,
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
-- 页面兜底：耗材产品维护（foundation:material:list）
INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  3300,
  '耗材产品维护',
  COALESCE(
    (SELECT m.menu_id FROM sys_menu m WHERE m.menu_name = '基础资料' AND m.menu_type = 'M' ORDER BY m.menu_id LIMIT 1),
    (SELECT m.menu_id FROM sys_menu m WHERE m.menu_name = '系统管理' AND m.menu_type = 'M' ORDER BY m.menu_id LIMIT 1),
    1
  ),
  1,
  'material',
  'foundation/material/index',
  NULL,
  1, 0, 'C', '0', '0', 'foundation:material:list', 'education',
  'admin', NOW(), '1', NOW(), '耗材产品主数据维护页',
  '0', '1'
FROM DUAL
WHERE
  NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='C' AND perms='foundation:material:list')
  OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=3300)
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  path = VALUES(path),
  component = VALUES(component),
  perms = VALUES(perms),
  icon = VALUES(icon),
  remark = VALUES(remark),
  default_open_to_customer = VALUES(default_open_to_customer),
  update_by = VALUES(update_by),
  update_time = VALUES(update_time);
/

-- 耗材产品维护基础按钮（与前端/后端权限串对齐）
INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  3295, '耗材产品查询',
  (SELECT m.menu_id FROM sys_menu m WHERE m.perms = 'foundation:material:list' AND m.menu_type = 'C' ORDER BY m.menu_id LIMIT 1),
  1, '#', '', NULL,
  1, 0, 'F', '0', '0', 'foundation:material:query', '#',
  'admin', NOW(), '1', NOW(), '',
  '0', '1'
FROM DUAL
WHERE EXISTS (SELECT 1 FROM sys_menu p WHERE p.perms = 'foundation:material:list' AND p.menu_type = 'C')
  AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND perms='foundation:material:query')
       OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=3295))
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  perms = VALUES(perms),
  update_by = VALUES(update_by),
  update_time = VALUES(update_time);
/

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  3296, '耗材产品新增',
  (SELECT m.menu_id FROM sys_menu m WHERE m.perms = 'foundation:material:list' AND m.menu_type = 'C' ORDER BY m.menu_id LIMIT 1),
  2, '#', '', NULL,
  1, 0, 'F', '0', '0', 'foundation:material:add', '#',
  'admin', NOW(), '1', NOW(), '',
  '0', '1'
FROM DUAL
WHERE EXISTS (SELECT 1 FROM sys_menu p WHERE p.perms = 'foundation:material:list' AND p.menu_type = 'C')
  AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND perms='foundation:material:add')
       OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=3296))
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  perms = VALUES(perms),
  update_by = VALUES(update_by),
  update_time = VALUES(update_time);
/

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  3297, '耗材产品修改',
  (SELECT m.menu_id FROM sys_menu m WHERE m.perms = 'foundation:material:list' AND m.menu_type = 'C' ORDER BY m.menu_id LIMIT 1),
  3, '#', '', NULL,
  1, 0, 'F', '0', '0', 'foundation:material:edit', '#',
  'admin', NOW(), '1', NOW(), '',
  '0', '1'
FROM DUAL
WHERE EXISTS (SELECT 1 FROM sys_menu p WHERE p.perms = 'foundation:material:list' AND p.menu_type = 'C')
  AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND perms='foundation:material:edit')
       OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=3297))
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  perms = VALUES(perms),
  update_by = VALUES(update_by),
  update_time = VALUES(update_time);
/

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  3298, '耗材产品删除',
  (SELECT m.menu_id FROM sys_menu m WHERE m.perms = 'foundation:material:list' AND m.menu_type = 'C' ORDER BY m.menu_id LIMIT 1),
  4, '#', '', NULL,
  1, 0, 'F', '0', '0', 'foundation:material:remove', '#',
  'admin', NOW(), '1', NOW(), '',
  '0', '1'
FROM DUAL
WHERE EXISTS (SELECT 1 FROM sys_menu p WHERE p.perms = 'foundation:material:list' AND p.menu_type = 'C')
  AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND perms='foundation:material:remove')
       OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=3298))
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  perms = VALUES(perms),
  update_by = VALUES(update_by),
  update_time = VALUES(update_time);
/

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  3299, '耗材产品导出',
  (SELECT m.menu_id FROM sys_menu m WHERE m.perms = 'foundation:material:list' AND m.menu_type = 'C' ORDER BY m.menu_id LIMIT 1),
  5, '#', '', NULL,
  1, 0, 'F', '0', '0', 'foundation:material:export', '#',
  'admin', NOW(), '1', NOW(), '',
  '0', '1'
FROM DUAL
WHERE EXISTS (SELECT 1 FROM sys_menu p WHERE p.perms = 'foundation:material:list' AND p.menu_type = 'C')
  AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND perms='foundation:material:export')
       OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=3299))
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  perms = VALUES(perms),
  update_by = VALUES(update_by),
  update_time = VALUES(update_time);
/

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

-- ---------- 8.6) 耗材产品维护补齐按钮（foundation:material:updateReferred / foundation:material:push）----------
INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  3301,
  '耗材产品更新简码',
  (SELECT m.menu_id FROM sys_menu m WHERE m.perms = 'foundation:material:list' AND m.menu_type = 'C' ORDER BY m.menu_id LIMIT 1),
  6,
  '#', '', NULL,
  1, 0, 'F', '0', '0', 'foundation:material:updateReferred', '#',
  'admin', NOW(), '1', NOW(), '对应前端更新简码按钮/后端 updateReferred 接口',
  '0', '1'
FROM DUAL
WHERE EXISTS (
  SELECT 1 FROM sys_menu p WHERE p.perms = 'foundation:material:list' AND p.menu_type = 'C'
)
  AND (
    NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND perms='foundation:material:updateReferred')
    OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=3301)
  )
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  perms = VALUES(perms),
  remark = VALUES(remark),
  default_open_to_customer = VALUES(default_open_to_customer),
  update_by = VALUES(update_by),
  update_time = VALUES(update_time);
/

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  3302,
  '耗材产品推送',
  (SELECT m.menu_id FROM sys_menu m WHERE m.perms = 'foundation:material:list' AND m.menu_type = 'C' ORDER BY m.menu_id LIMIT 1),
  7,
  '#', '', NULL,
  1, 0, 'F', '0', '0', 'foundation:material:push', '#',
  'admin', NOW(), '1', NOW(), '对应前端推送按钮/后端 push 接口',
  '0', '1'
FROM DUAL
WHERE EXISTS (
  SELECT 1 FROM sys_menu p WHERE p.perms = 'foundation:material:list' AND p.menu_type = 'C'
)
  AND (
    NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND perms='foundation:material:push')
    OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=3302)
  )
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  perms = VALUES(perms),
  remark = VALUES(remark),
  default_open_to_customer = VALUES(default_open_to_customer),
  update_by = VALUES(update_by),
  update_time = VALUES(update_time);
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

-- ========== HIS 外联库配置（sys_his_external_db；HisExternalDbController /his/externalDb）==========
INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
) SELECT
  3103, 'HIS外联库配置', 1, 22, 'hisExternalDb', 'material/system/hisExternalDb/index', NULL,
  1, 0, 'C', '0', '0', 'hc:system:hisExternalDb:list', 'link',
  'admin', NOW(), '1', NOW(), '主库租户级 HIS JDBC（SQLSERVER/MYSQL）；平台菜单',
  '1', '0'
FROM DUAL
WHERE
  NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'C' AND component = 'material/system/hisExternalDb/index')
  OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 3103)
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  path = VALUES(path),
  component = VALUES(component),
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
) SELECT
  3104, 'HIS外联库查询', 3103, 1, '#', '', NULL,
  1, 0, 'F', '0', '0', 'hc:system:hisExternalDb:query', '#',
  'admin', NOW(), '1', NOW(), 'GET /his/externalDb/{tenantId}',
  '1', '0'
FROM DUAL
WHERE
  NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'F' AND parent_id = 3103 AND perms = 'hc:system:hisExternalDb:query')
  OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 3104)
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
) SELECT
  3105, 'HIS外联库新增', 3103, 2, '#', '', NULL,
  1, 0, 'F', '0', '0', 'hc:system:hisExternalDb:add', '#',
  'admin', NOW(), '1', NOW(), 'POST /his/externalDb',
  '1', '0'
FROM DUAL
WHERE
  NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'F' AND parent_id = 3103 AND perms = 'hc:system:hisExternalDb:add')
  OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 3105)
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
) SELECT
  3106, 'HIS外联库修改', 3103, 3, '#', '', NULL,
  1, 0, 'F', '0', '0', 'hc:system:hisExternalDb:edit', '#',
  'admin', NOW(), '1', NOW(), 'PUT /his/externalDb',
  '1', '0'
FROM DUAL
WHERE
  NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'F' AND parent_id = 3103 AND perms = 'hc:system:hisExternalDb:edit')
  OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 3106)
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
) SELECT
  3107, 'HIS外联库删除', 3103, 4, '#', '', NULL,
  1, 0, 'F', '0', '0', 'hc:system:hisExternalDb:remove', '#',
  'admin', NOW(), '1', NOW(), 'DELETE /his/externalDb/{tenantId}',
  '1', '0'
FROM DUAL
WHERE
  NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'F' AND parent_id = 3103 AND perms = 'hc:system:hisExternalDb:remove')
  OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 3107)
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
-- perms 与 @PreAuthorize 一致；固定 menu_id 与现网一致：C=1407，F=3204–3209；path=departmentTransfer；default_open_to_customer=1 对客户默认开放
/

SET @dept_parent_transfer := COALESCE(
  (SELECT m.menu_id FROM sys_menu m WHERE m.menu_type = 'M' AND m.path = 'department' ORDER BY m.menu_id LIMIT 1),
  1062
);
/

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  1407, '转科申请', @dept_parent_transfer, 3, 'departmentTransfer', 'department/departmentTransfer/apply/index', NULL,
  1, 0, 'C', '0', '0', 'departmentTransfer:apply:list', 'guide',
  'admin', NOW(), '1', NOW(), 'BasApply billType=3，/department/transfer',
  '0', '1'
FROM DUAL
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
  3204, '转科申请查询', 1407, 1, '#', '', NULL,
  1, 0, 'F', '0', '0', 'departmentTransfer:apply:query', '#',
  'admin', NOW(), '1', NOW(), 'GET /{id}',
  '0', '1'
FROM DUAL
WHERE
  NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'F' AND parent_id = 1407 AND perms = 'departmentTransfer:apply:query')
  OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 3204)
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
  3205, '转科申请导出', 1407, 2, '#', '', NULL,
  1, 0, 'F', '0', '0', 'departmentTransfer:apply:export', '#',
  'admin', NOW(), '1', NOW(), '',
  '0', '1'
FROM DUAL
WHERE
  NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'F' AND parent_id = 1407 AND perms = 'departmentTransfer:apply:export')
  OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 3205)
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
  3206, '转科申请新增', 1407, 3, '#', '', NULL,
  1, 0, 'F', '0', '0', 'departmentTransfer:apply:add', '#',
  'admin', NOW(), '1', NOW(), 'POST',
  '0', '1'
FROM DUAL
WHERE
  NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'F' AND parent_id = 1407 AND perms = 'departmentTransfer:apply:add')
  OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 3206)
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
  3207, '转科申请修改', 1407, 4, '#', '', NULL,
  1, 0, 'F', '0', '0', 'departmentTransfer:apply:edit', '#',
  'admin', NOW(), '1', NOW(), 'PUT',
  '0', '1'
FROM DUAL
WHERE
  NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'F' AND parent_id = 1407 AND perms = 'departmentTransfer:apply:edit')
  OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 3207)
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
  3208, '转科申请删除', 1407, 5, '#', '', NULL,
  1, 0, 'F', '0', '0', 'departmentTransfer:apply:remove', '#',
  'admin', NOW(), '1', NOW(), '',
  '0', '1'
FROM DUAL
WHERE
  NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'F' AND parent_id = 1407 AND perms = 'departmentTransfer:apply:remove')
  OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 3208)
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
  3209, '转科申请审核', 1407, 6, '#', '', NULL,
  1, 0, 'F', '0', '0', 'departmentTransfer:apply:audit', '#',
  'admin', NOW(), '1', NOW(), 'PUT /auditApply',
  '0', '1'
FROM DUAL
WHERE
  NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'F' AND parent_id = 1407 AND perms = 'departmentTransfer:apply:audit')
  OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 3209)
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

-- 转科申请：强制对客户默认开放（含历史数据 is_platform/default_open 被改过的情况）
UPDATE sys_menu
SET default_open_to_customer = '1',
    update_by = '1',
    update_time = NOW()
WHERE menu_id IN (1407, 3204, 3205, 3206, 3207, 3208, 3209);
/

-- 侧栏：visible=1 时路由 hidden，父目录隐藏会导致整枝不显示；转科申请及其父目录须可显
UPDATE sys_menu
SET visible = '0',
    update_by = '1',
    update_time = NOW()
WHERE menu_id IN (1407, 3204, 3205, 3206, 3207, 3208, 3209);
/

UPDATE sys_menu p
INNER JOIN sys_menu c ON c.menu_id = 1407 AND c.parent_id = p.menu_id
SET p.visible = '0',
    p.update_by = '1',
    p.update_time = NOW()
WHERE p.menu_type = 'M';
/

-- 耗材「科室」根目录 path=department：若误标 is_platform=1，expandMenuIdsWithAncestorsForTenant 会在父级中断，sys_user_menu 缺祖先 ID，租户侧栏无法挂载转科等子菜单
UPDATE sys_menu
SET is_platform = '0',
    update_by = '1',
    update_time = NOW()
WHERE menu_type = 'M'
  AND path = 'department'
  AND IFNULL(is_platform, '0') = '1';
/

-- 转科：停用历史占位节点（1397 及子 1398/1399/1400，path 为 menu_139x、无真实 component），避免与 1407+3204–3209 重复挂菜单/误授权
-- status=1 停用、visible=1 隐藏（与 RuoYi 字段语义一致）；若库中无这些 menu_id 则本段影响 0 行
UPDATE sys_menu
SET status = '1',
    visible = '1',
    default_open_to_customer = '0',
    update_by = '1',
    update_time = NOW(),
    remark = CASE
      WHEN remark IS NULL OR remark = '' THEN '已由 menu_id=1407 转科申请替代'
      WHEN remark NOT LIKE '%1407%' THEN CONCAT(remark, '；已由 menu_id=1407 转科申请替代')
      ELSE remark
    END
WHERE menu_id IN (1397, 1398, 1399, 1400);
/

DELETE FROM hc_customer_menu
WHERE menu_id IN (1397, 1398, 1399, 1400);
/

-- ---------- 16) 科室申领（department:dApply）页面与按钮补齐 ----------
SET @department_root := (
  SELECT m.menu_id FROM sys_menu m
  WHERE m.path = 'department' AND m.menu_type = 'M'
  ORDER BY m.menu_id LIMIT 1
);
/

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  3310, '科室申领', COALESCE(@department_root, 1), 1, 'dApply', 'department/dApply/index', NULL,
  1, 0, 'C', '0', '0', 'department:dApply:list', 'form',
  'admin', NOW(), '1', NOW(), '科室申领单列表',
  '0', '1'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='C' AND perms='department:dApply:list')
   OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=3310)
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  path = VALUES(path),
  component = VALUES(component),
  perms = VALUES(perms),
  icon = VALUES(icon),
  remark = VALUES(remark),
  default_open_to_customer = VALUES(default_open_to_customer),
  update_by = VALUES(update_by),
  update_time = VALUES(update_time);
/

SET @dapply_menu := (
  SELECT m.menu_id FROM sys_menu m
  WHERE m.perms = 'department:dApply:list' AND m.menu_type = 'C'
  ORDER BY m.menu_id LIMIT 1
);
/

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3311, '科室申领查询', @dapply_menu, 1, '#', '', NULL, 1, 0, 'F', '0', '0', 'department:dApply:query', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL
WHERE @dapply_menu IS NOT NULL
  AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=@dapply_menu AND perms='department:dApply:query')
       OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=3311))
ON DUPLICATE KEY UPDATE menu_name=VALUES(menu_name), parent_id=VALUES(parent_id), order_num=VALUES(order_num), perms=VALUES(perms), update_by=VALUES(update_by), update_time=VALUES(update_time);
/

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3312, '科室申领新增', @dapply_menu, 2, '#', '', NULL, 1, 0, 'F', '0', '0', 'department:dApply:add', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL
WHERE @dapply_menu IS NOT NULL
  AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=@dapply_menu AND perms='department:dApply:add')
       OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=3312))
ON DUPLICATE KEY UPDATE menu_name=VALUES(menu_name), parent_id=VALUES(parent_id), order_num=VALUES(order_num), perms=VALUES(perms), update_by=VALUES(update_by), update_time=VALUES(update_time);
/

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3313, '科室申领修改', @dapply_menu, 3, '#', '', NULL, 1, 0, 'F', '0', '0', 'department:dApply:edit', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL
WHERE @dapply_menu IS NOT NULL
  AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=@dapply_menu AND perms='department:dApply:edit')
       OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=3313))
ON DUPLICATE KEY UPDATE menu_name=VALUES(menu_name), parent_id=VALUES(parent_id), order_num=VALUES(order_num), perms=VALUES(perms), update_by=VALUES(update_by), update_time=VALUES(update_time);
/

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3314, '科室申领删除', @dapply_menu, 4, '#', '', NULL, 1, 0, 'F', '0', '0', 'department:dApply:remove', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL
WHERE @dapply_menu IS NOT NULL
  AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=@dapply_menu AND perms='department:dApply:remove')
       OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=3314))
ON DUPLICATE KEY UPDATE menu_name=VALUES(menu_name), parent_id=VALUES(parent_id), order_num=VALUES(order_num), perms=VALUES(perms), update_by=VALUES(update_by), update_time=VALUES(update_time);
/

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3315, '科室申领导出', @dapply_menu, 5, '#', '', NULL, 1, 0, 'F', '0', '0', 'department:dApply:export', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL
WHERE @dapply_menu IS NOT NULL
  AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=@dapply_menu AND perms='department:dApply:export')
       OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=3315))
ON DUPLICATE KEY UPDATE menu_name=VALUES(menu_name), parent_id=VALUES(parent_id), order_num=VALUES(order_num), perms=VALUES(perms), update_by=VALUES(update_by), update_time=VALUES(update_time);
/

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3316, '科室申领审核', @dapply_menu, 6, '#', '', NULL, 1, 0, 'F', '0', '0', 'department:dApply:audit', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL
WHERE @dapply_menu IS NOT NULL
  AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=@dapply_menu AND perms='department:dApply:audit')
       OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=3316))
ON DUPLICATE KEY UPDATE menu_name=VALUES(menu_name), parent_id=VALUES(parent_id), order_num=VALUES(order_num), perms=VALUES(perms), update_by=VALUES(update_by), update_time=VALUES(update_time);
/

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3317, '科室申领查看', @dapply_menu, 7, '#', '', NULL, 1, 0, 'F', '0', '0', 'department:dApply:view', '#', 'admin', NOW(), '1', NOW(), '仅用于前端弹窗可见性控制', '0', '1'
FROM DUAL
WHERE @dapply_menu IS NOT NULL
  AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=@dapply_menu AND perms='department:dApply:view')
       OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=3317))
ON DUPLICATE KEY UPDATE menu_name=VALUES(menu_name), parent_id=VALUES(parent_id), order_num=VALUES(order_num), perms=VALUES(perms), remark=VALUES(remark), update_by=VALUES(update_by), update_time=VALUES(update_time);
/

-- ---------- 17) 采购订单查看按钮（caigou:dingdan:view）补齐 ----------
SET @dingdan_menu := (
  SELECT m.menu_id FROM sys_menu m
  WHERE m.perms = 'caigou:dingdan:list' AND m.menu_type = 'C'
  ORDER BY m.menu_id LIMIT 1
);
/

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3318, '采购订单查看', @dingdan_menu, 8, '#', '', NULL, 1, 0, 'F', '0', '0', 'caigou:dingdan:view', '#', 'admin', NOW(), '1', NOW(), '仅用于前端选择弹窗可见性控制', '0', '1'
FROM DUAL
WHERE @dingdan_menu IS NOT NULL
  AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=@dingdan_menu AND perms='caigou:dingdan:view')
       OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=3318))
ON DUPLICATE KEY UPDATE menu_name=VALUES(menu_name), parent_id=VALUES(parent_id), order_num=VALUES(order_num), perms=VALUES(perms), remark=VALUES(remark), update_by=VALUES(update_by), update_time=VALUES(update_time);
/

-- ---------- 18) 科室预警（department:inventoryWarning）页面与按钮补齐 ----------
SET @department_root := (SELECT m.menu_id FROM sys_menu m WHERE m.path='department' AND m.menu_type='M' ORDER BY m.menu_id LIMIT 1);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3320, '科室库存预警', COALESCE(@department_root,1), 11, 'inventoryWarning', 'department/inventoryWarning/index', NULL, 1, 0, 'C', '0', '0', 'department:inventoryWarning:list', 'warning', 'admin', NOW(), '1', NOW(), '科室库存预警设置', '0', '1'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='C' AND perms='department:inventoryWarning:list')
   OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=3320)
ON DUPLICATE KEY UPDATE menu_name=VALUES(menu_name), parent_id=VALUES(parent_id), order_num=VALUES(order_num), path=VALUES(path), component=VALUES(component), perms=VALUES(perms), update_by=VALUES(update_by), update_time=VALUES(update_time);
/
SET @inv_warn_menu := (SELECT m.menu_id FROM sys_menu m WHERE m.perms='department:inventoryWarning:list' AND m.menu_type='C' ORDER BY m.menu_id LIMIT 1);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3321, '预警查询', @inv_warn_menu, 1, '#', '', NULL, 1, 0, 'F', '0', '0', 'department:inventoryWarning:query', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE @inv_warn_menu IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=@inv_warn_menu AND perms='department:inventoryWarning:query') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=3321))
ON DUPLICATE KEY UPDATE menu_name=VALUES(menu_name), parent_id=VALUES(parent_id), order_num=VALUES(order_num), perms=VALUES(perms), update_time=VALUES(update_time);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3322, '预警新增', @inv_warn_menu, 2, '#', '', NULL, 1, 0, 'F', '0', '0', 'department:inventoryWarning:add', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE @inv_warn_menu IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=@inv_warn_menu AND perms='department:inventoryWarning:add') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=3322))
ON DUPLICATE KEY UPDATE menu_name=VALUES(menu_name), parent_id=VALUES(parent_id), order_num=VALUES(order_num), perms=VALUES(perms), update_time=VALUES(update_time);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3323, '预警修改', @inv_warn_menu, 3, '#', '', NULL, 1, 0, 'F', '0', '0', 'department:inventoryWarning:edit', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE @inv_warn_menu IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=@inv_warn_menu AND perms='department:inventoryWarning:edit') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=3323))
ON DUPLICATE KEY UPDATE menu_name=VALUES(menu_name), parent_id=VALUES(parent_id), order_num=VALUES(order_num), perms=VALUES(perms), update_time=VALUES(update_time);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3324, '预警删除', @inv_warn_menu, 4, '#', '', NULL, 1, 0, 'F', '0', '0', 'department:inventoryWarning:remove', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE @inv_warn_menu IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=@inv_warn_menu AND perms='department:inventoryWarning:remove') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=3324))
ON DUPLICATE KEY UPDATE menu_name=VALUES(menu_name), parent_id=VALUES(parent_id), order_num=VALUES(order_num), perms=VALUES(perms), update_time=VALUES(update_time);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3325, '预警导出', @inv_warn_menu, 5, '#', '', NULL, 1, 0, 'F', '0', '0', 'department:inventoryWarning:export', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE @inv_warn_menu IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=@inv_warn_menu AND perms='department:inventoryWarning:export') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=3325))
ON DUPLICATE KEY UPDATE menu_name=VALUES(menu_name), parent_id=VALUES(parent_id), order_num=VALUES(order_num), perms=VALUES(perms), update_time=VALUES(update_time);
/

-- ---------- 19) 科室盘点（department:stocktaking / department:stocktakingAudit）----------
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3330, '科室盘点', COALESCE(@department_root,1), 12, 'stocktaking', 'department/stocktaking/index', NULL, 1, 0, 'C', '0', '0', 'department:stocktaking:list', 'date', 'admin', NOW(), '1', NOW(), '科室盘点申请', '0', '1'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='C' AND perms='department:stocktaking:list')
   OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=3330)
ON DUPLICATE KEY UPDATE menu_name=VALUES(menu_name), parent_id=VALUES(parent_id), order_num=VALUES(order_num), path=VALUES(path), component=VALUES(component), perms=VALUES(perms), update_by=VALUES(update_by), update_time=VALUES(update_time);
/
SET @dept_stk_menu := (SELECT m.menu_id FROM sys_menu m WHERE m.perms='department:stocktaking:list' AND m.menu_type='C' ORDER BY m.menu_id LIMIT 1);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3331, '盘点查询', @dept_stk_menu, 1, '#', '', NULL, 1, 0, 'F', '0', '0', 'department:stocktaking:query', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE @dept_stk_menu IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=@dept_stk_menu AND perms='department:stocktaking:query') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=3331))
ON DUPLICATE KEY UPDATE menu_name=VALUES(menu_name), parent_id=VALUES(parent_id), order_num=VALUES(order_num), perms=VALUES(perms), update_time=VALUES(update_time);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3332, '盘点新增', @dept_stk_menu, 2, '#', '', NULL, 1, 0, 'F', '0', '0', 'department:stocktaking:add', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE @dept_stk_menu IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=@dept_stk_menu AND perms='department:stocktaking:add') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=3332))
ON DUPLICATE KEY UPDATE menu_name=VALUES(menu_name), parent_id=VALUES(parent_id), order_num=VALUES(order_num), perms=VALUES(perms), update_time=VALUES(update_time);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3333, '盘点修改', @dept_stk_menu, 3, '#', '', NULL, 1, 0, 'F', '0', '0', 'department:stocktaking:edit', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE @dept_stk_menu IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=@dept_stk_menu AND perms='department:stocktaking:edit') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=3333))
ON DUPLICATE KEY UPDATE menu_name=VALUES(menu_name), parent_id=VALUES(parent_id), order_num=VALUES(order_num), perms=VALUES(perms), update_time=VALUES(update_time);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3334, '盘点删除', @dept_stk_menu, 4, '#', '', NULL, 1, 0, 'F', '0', '0', 'department:stocktaking:remove', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE @dept_stk_menu IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=@dept_stk_menu AND perms='department:stocktaking:remove') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=3334))
ON DUPLICATE KEY UPDATE menu_name=VALUES(menu_name), parent_id=VALUES(parent_id), order_num=VALUES(order_num), perms=VALUES(perms), update_time=VALUES(update_time);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3335, '盘点导出', @dept_stk_menu, 5, '#', '', NULL, 1, 0, 'F', '0', '0', 'department:stocktaking:export', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE @dept_stk_menu IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=@dept_stk_menu AND perms='department:stocktaking:export') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=3335))
ON DUPLICATE KEY UPDATE menu_name=VALUES(menu_name), parent_id=VALUES(parent_id), order_num=VALUES(order_num), perms=VALUES(perms), update_time=VALUES(update_time);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3336, '盘点审核', @dept_stk_menu, 6, '#', '', NULL, 1, 0, 'F', '0', '0', 'department:stocktaking:audit', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE @dept_stk_menu IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=@dept_stk_menu AND perms='department:stocktaking:audit') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=3336))
ON DUPLICATE KEY UPDATE menu_name=VALUES(menu_name), parent_id=VALUES(parent_id), order_num=VALUES(order_num), perms=VALUES(perms), update_time=VALUES(update_time);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3337, '盘点驳回', @dept_stk_menu, 7, '#', '', NULL, 1, 0, 'F', '0', '0', 'department:stocktaking:reject', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE @dept_stk_menu IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=@dept_stk_menu AND perms='department:stocktaking:reject') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=3337))
ON DUPLICATE KEY UPDATE menu_name=VALUES(menu_name), parent_id=VALUES(parent_id), order_num=VALUES(order_num), perms=VALUES(perms), update_time=VALUES(update_time);
/

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3338, '科室盘点审核', COALESCE(@department_root,1), 13, 'stocktakingAudit', 'department/stocktakingAudit/index', NULL, 1, 0, 'C', '0', '0', 'department:stocktakingAudit:list', 'audit', 'admin', NOW(), '1', NOW(), '科室盘点审核页', '0', '1'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='C' AND perms='department:stocktakingAudit:list')
   OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=3338)
ON DUPLICATE KEY UPDATE menu_name=VALUES(menu_name), parent_id=VALUES(parent_id), order_num=VALUES(order_num), path=VALUES(path), component=VALUES(component), perms=VALUES(perms), update_by=VALUES(update_by), update_time=VALUES(update_time);
/
SET @dept_stk_audit_menu := (SELECT m.menu_id FROM sys_menu m WHERE m.perms='department:stocktakingAudit:list' AND m.menu_type='C' ORDER BY m.menu_id LIMIT 1);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3339, '盘点审核导出', @dept_stk_audit_menu, 1, '#', '', NULL, 1, 0, 'F', '0', '0', 'department:stocktakingAudit:export', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE @dept_stk_audit_menu IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=@dept_stk_audit_menu AND perms='department:stocktakingAudit:export') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=3339))
ON DUPLICATE KEY UPDATE menu_name=VALUES(menu_name), parent_id=VALUES(parent_id), order_num=VALUES(order_num), perms=VALUES(perms), update_time=VALUES(update_time);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3340, '盘点审核通过', @dept_stk_audit_menu, 2, '#', '', NULL, 1, 0, 'F', '0', '0', 'department:stocktakingAudit:audit', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE @dept_stk_audit_menu IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=@dept_stk_audit_menu AND perms='department:stocktakingAudit:audit') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=3340))
ON DUPLICATE KEY UPDATE menu_name=VALUES(menu_name), parent_id=VALUES(parent_id), order_num=VALUES(order_num), perms=VALUES(perms), update_time=VALUES(update_time);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3341, '盘点审核驳回', @dept_stk_audit_menu, 3, '#', '', NULL, 1, 0, 'F', '0', '0', 'department:stocktakingAudit:reject', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE @dept_stk_audit_menu IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=@dept_stk_audit_menu AND perms='department:stocktakingAudit:reject') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=3341))
ON DUPLICATE KEY UPDATE menu_name=VALUES(menu_name), parent_id=VALUES(parent_id), order_num=VALUES(order_num), perms=VALUES(perms), update_time=VALUES(update_time);
/

-- ---------- 20) 科室请购（department:purchase / department:purchaseAudit）----------
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3342, '科室请购', COALESCE(@department_root,1), 14, 'dPurchase', 'department/dPurchase/index', NULL, 1, 0, 'C', '0', '0', 'department:purchase:list', 'shopping', 'admin', NOW(), '1', NOW(), '科室请购申请', '0', '1'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='C' AND perms='department:purchase:list')
   OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=3342)
ON DUPLICATE KEY UPDATE menu_name=VALUES(menu_name), parent_id=VALUES(parent_id), order_num=VALUES(order_num), path=VALUES(path), component=VALUES(component), perms=VALUES(perms), update_by=VALUES(update_by), update_time=VALUES(update_time);
/
SET @dept_purchase_menu := (SELECT m.menu_id FROM sys_menu m WHERE m.perms='department:purchase:list' AND m.menu_type='C' ORDER BY m.menu_id LIMIT 1);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3343, '请购查询', @dept_purchase_menu, 1, '#', '', NULL, 1, 0, 'F', '0', '0', 'department:purchase:query', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE @dept_purchase_menu IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=@dept_purchase_menu AND perms='department:purchase:query') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=3343))
ON DUPLICATE KEY UPDATE menu_name=VALUES(menu_name), parent_id=VALUES(parent_id), order_num=VALUES(order_num), perms=VALUES(perms), update_time=VALUES(update_time);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3344, '请购新增', @dept_purchase_menu, 2, '#', '', NULL, 1, 0, 'F', '0', '0', 'department:purchase:add', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE @dept_purchase_menu IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=@dept_purchase_menu AND perms='department:purchase:add') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=3344))
ON DUPLICATE KEY UPDATE menu_name=VALUES(menu_name), parent_id=VALUES(parent_id), order_num=VALUES(order_num), perms=VALUES(perms), update_time=VALUES(update_time);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3345, '请购修改', @dept_purchase_menu, 3, '#', '', NULL, 1, 0, 'F', '0', '0', 'department:purchase:edit', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE @dept_purchase_menu IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=@dept_purchase_menu AND perms='department:purchase:edit') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=3345))
ON DUPLICATE KEY UPDATE menu_name=VALUES(menu_name), parent_id=VALUES(parent_id), order_num=VALUES(order_num), perms=VALUES(perms), update_time=VALUES(update_time);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3346, '请购删除', @dept_purchase_menu, 4, '#', '', NULL, 1, 0, 'F', '0', '0', 'department:purchase:remove', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE @dept_purchase_menu IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=@dept_purchase_menu AND perms='department:purchase:remove') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=3346))
ON DUPLICATE KEY UPDATE menu_name=VALUES(menu_name), parent_id=VALUES(parent_id), order_num=VALUES(order_num), perms=VALUES(perms), update_time=VALUES(update_time);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3347, '请购导出', @dept_purchase_menu, 5, '#', '', NULL, 1, 0, 'F', '0', '0', 'department:purchase:export', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE @dept_purchase_menu IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=@dept_purchase_menu AND perms='department:purchase:export') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=3347))
ON DUPLICATE KEY UPDATE menu_name=VALUES(menu_name), parent_id=VALUES(parent_id), order_num=VALUES(order_num), perms=VALUES(perms), update_time=VALUES(update_time);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3348, '请购审核', @dept_purchase_menu, 6, '#', '', NULL, 1, 0, 'F', '0', '0', 'department:purchase:audit', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE @dept_purchase_menu IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=@dept_purchase_menu AND perms='department:purchase:audit') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=3348))
ON DUPLICATE KEY UPDATE menu_name=VALUES(menu_name), parent_id=VALUES(parent_id), order_num=VALUES(order_num), perms=VALUES(perms), update_time=VALUES(update_time);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3349, '请购驳回', @dept_purchase_menu, 7, '#', '', NULL, 1, 0, 'F', '0', '0', 'department:purchase:reject', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE @dept_purchase_menu IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=@dept_purchase_menu AND perms='department:purchase:reject') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=3349))
ON DUPLICATE KEY UPDATE menu_name=VALUES(menu_name), parent_id=VALUES(parent_id), order_num=VALUES(order_num), perms=VALUES(perms), update_time=VALUES(update_time);
/

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3350, '科室请购审核', COALESCE(@department_root,1), 15, 'dPurchaseAudit', 'department/dPurchaseAudit/index', NULL, 1, 0, 'C', '0', '0', 'department:purchaseAudit:list', 'audit', 'admin', NOW(), '1', NOW(), '科室请购审核页', '0', '1'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='C' AND perms='department:purchaseAudit:list')
   OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=3350)
ON DUPLICATE KEY UPDATE menu_name=VALUES(menu_name), parent_id=VALUES(parent_id), order_num=VALUES(order_num), path=VALUES(path), component=VALUES(component), perms=VALUES(perms), update_by=VALUES(update_by), update_time=VALUES(update_time);
/
SET @dept_purchase_audit_menu := (SELECT m.menu_id FROM sys_menu m WHERE m.perms='department:purchaseAudit:list' AND m.menu_type='C' ORDER BY m.menu_id LIMIT 1);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3351, '请购审核导出', @dept_purchase_audit_menu, 1, '#', '', NULL, 1, 0, 'F', '0', '0', 'department:purchaseAudit:export', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE @dept_purchase_audit_menu IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=@dept_purchase_audit_menu AND perms='department:purchaseAudit:export') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=3351))
ON DUPLICATE KEY UPDATE menu_name=VALUES(menu_name), parent_id=VALUES(parent_id), order_num=VALUES(order_num), perms=VALUES(perms), update_time=VALUES(update_time);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3352, '请购审核通过', @dept_purchase_audit_menu, 2, '#', '', NULL, 1, 0, 'F', '0', '0', 'department:purchaseAudit:audit', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE @dept_purchase_audit_menu IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=@dept_purchase_audit_menu AND perms='department:purchaseAudit:audit') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=3352))
ON DUPLICATE KEY UPDATE menu_name=VALUES(menu_name), parent_id=VALUES(parent_id), order_num=VALUES(order_num), perms=VALUES(perms), update_time=VALUES(update_time);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3353, '请购审核驳回', @dept_purchase_audit_menu, 3, '#', '', NULL, 1, 0, 'F', '0', '0', 'department:purchaseAudit:reject', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE @dept_purchase_audit_menu IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=@dept_purchase_audit_menu AND perms='department:purchaseAudit:reject') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=3353))
ON DUPLICATE KEY UPDATE menu_name=VALUES(menu_name), parent_id=VALUES(parent_id), order_num=VALUES(order_num), perms=VALUES(perms), update_time=VALUES(update_time);
/

-- ---------- 21) 科室申领审核（department:dApplyAudit）----------
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3354, '科室申领审核', COALESCE(@department_root,1), 16, 'dApplyAudit', 'department/dApplyAudit/index', NULL, 1, 0, 'C', '0', '0', 'department:dApplyAudit:list', 'audit', 'admin', NOW(), '1', NOW(), '科室申领审核页', '0', '1'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='C' AND perms='department:dApplyAudit:list')
   OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=3354)
ON DUPLICATE KEY UPDATE menu_name=VALUES(menu_name), parent_id=VALUES(parent_id), order_num=VALUES(order_num), path=VALUES(path), component=VALUES(component), perms=VALUES(perms), update_by=VALUES(update_by), update_time=VALUES(update_time);
/
SET @dapply_audit_menu := (SELECT m.menu_id FROM sys_menu m WHERE m.perms='department:dApplyAudit:list' AND m.menu_type='C' ORDER BY m.menu_id LIMIT 1);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3355, '申领审核导出', @dapply_audit_menu, 1, '#', '', NULL, 1, 0, 'F', '0', '0', 'department:dApplyAudit:export', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE @dapply_audit_menu IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=@dapply_audit_menu AND perms='department:dApplyAudit:export') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=3355))
ON DUPLICATE KEY UPDATE menu_name=VALUES(menu_name), parent_id=VALUES(parent_id), order_num=VALUES(order_num), perms=VALUES(perms), update_time=VALUES(update_time);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3356, '申领审核通过', @dapply_audit_menu, 2, '#', '', NULL, 1, 0, 'F', '0', '0', 'department:dApplyAudit:audit', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE @dapply_audit_menu IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=@dapply_audit_menu AND perms='department:dApplyAudit:audit') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=3356))
ON DUPLICATE KEY UPDATE menu_name=VALUES(menu_name), parent_id=VALUES(parent_id), order_num=VALUES(order_num), perms=VALUES(perms), update_time=VALUES(update_time);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3357, '申领审核驳回', @dapply_audit_menu, 3, '#', '', NULL, 1, 0, 'F', '0', '0', 'department:dApplyAudit:reject', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE @dapply_audit_menu IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=@dapply_audit_menu AND perms='department:dApplyAudit:reject') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=3357))
ON DUPLICATE KEY UPDATE menu_name=VALUES(menu_name), parent_id=VALUES(parent_id), order_num=VALUES(order_num), perms=VALUES(perms), update_time=VALUES(update_time);
/

-- ---------- 22) 高值科室与高值库存（gzDepartment / gz）----------
SET @gz_root := (SELECT m.menu_id FROM sys_menu m WHERE m.path='gz' AND m.menu_type='M' ORDER BY m.menu_id LIMIT 1);
/
SET @gz_dept_root := (SELECT m.menu_id FROM sys_menu m WHERE m.path='gzDepartment' AND m.menu_type='M' ORDER BY m.menu_id LIMIT 1);
/

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3360, '高值科室申领', COALESCE(@gz_dept_root, COALESCE(@gz_root,1)), 1, 'apply', 'gzDepartment/apply/index', NULL, 1, 0, 'C', '0', '0', 'gzDepartment:apply:list', 'form', 'admin', NOW(), '1', NOW(), '高值科室申领列表', '0', '1'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='C' AND perms='gzDepartment:apply:list') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=3360)
ON DUPLICATE KEY UPDATE menu_name=VALUES(menu_name), parent_id=VALUES(parent_id), order_num=VALUES(order_num), path=VALUES(path), component=VALUES(component), perms=VALUES(perms), update_by=VALUES(update_by), update_time=VALUES(update_time);
/
SET @gz_apply_menu := (SELECT m.menu_id FROM sys_menu m WHERE m.perms='gzDepartment:apply:list' AND m.menu_type='C' ORDER BY m.menu_id LIMIT 1);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3361, '高值申领查询', @gz_apply_menu, 1, '#', '', NULL, 1, 0, 'F', '0', '0', 'gzDepartment:apply:query', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE @gz_apply_menu IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=@gz_apply_menu AND perms='gzDepartment:apply:query') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=3361))
ON DUPLICATE KEY UPDATE menu_name=VALUES(menu_name), parent_id=VALUES(parent_id), order_num=VALUES(order_num), perms=VALUES(perms), update_time=VALUES(update_time);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3362, '高值申领新增', @gz_apply_menu, 2, '#', '', NULL, 1, 0, 'F', '0', '0', 'gzDepartment:apply:add', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE @gz_apply_menu IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=@gz_apply_menu AND perms='gzDepartment:apply:add') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=3362))
ON DUPLICATE KEY UPDATE menu_name=VALUES(menu_name), parent_id=VALUES(parent_id), order_num=VALUES(order_num), perms=VALUES(perms), update_time=VALUES(update_time);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3363, '高值申领修改', @gz_apply_menu, 3, '#', '', NULL, 1, 0, 'F', '0', '0', 'gzDepartment:apply:edit', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE @gz_apply_menu IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=@gz_apply_menu AND perms='gzDepartment:apply:edit') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=3363))
ON DUPLICATE KEY UPDATE menu_name=VALUES(menu_name), parent_id=VALUES(parent_id), order_num=VALUES(order_num), perms=VALUES(perms), update_time=VALUES(update_time);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3364, '高值申领删除', @gz_apply_menu, 4, '#', '', NULL, 1, 0, 'F', '0', '0', 'gzDepartment:apply:remove', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE @gz_apply_menu IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=@gz_apply_menu AND perms='gzDepartment:apply:remove') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=3364))
ON DUPLICATE KEY UPDATE menu_name=VALUES(menu_name), parent_id=VALUES(parent_id), order_num=VALUES(order_num), perms=VALUES(perms), update_time=VALUES(update_time);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3365, '高值申领导出', @gz_apply_menu, 5, '#', '', NULL, 1, 0, 'F', '0', '0', 'gzDepartment:apply:export', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE @gz_apply_menu IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=@gz_apply_menu AND perms='gzDepartment:apply:export') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=3365))
ON DUPLICATE KEY UPDATE menu_name=VALUES(menu_name), parent_id=VALUES(parent_id), order_num=VALUES(order_num), perms=VALUES(perms), update_time=VALUES(update_time);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3366, '高值申领审核', @gz_apply_menu, 6, '#', '', NULL, 1, 0, 'F', '0', '0', 'gzDepartment:apply:audit', '#', 'admin', NOW(), '1', NOW(), '前端审核页按钮可见性权限', '0', '1'
FROM DUAL WHERE @gz_apply_menu IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=@gz_apply_menu AND perms='gzDepartment:apply:audit') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=3366))
ON DUPLICATE KEY UPDATE menu_name=VALUES(menu_name), parent_id=VALUES(parent_id), order_num=VALUES(order_num), perms=VALUES(perms), remark=VALUES(remark), update_time=VALUES(update_time);
/

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3367, '高值科室库存查询', COALESCE(@gz_dept_root, COALESCE(@gz_root,1)), 2, 'gzDepInventory', 'gzDepartment/gzDepInventory/index', NULL, 1, 0, 'C', '0', '0', 'gzDepartment:gzDepInventory:list', 'search', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='C' AND perms='gzDepartment:gzDepInventory:list') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=3367)
ON DUPLICATE KEY UPDATE menu_name=VALUES(menu_name), parent_id=VALUES(parent_id), order_num=VALUES(order_num), path=VALUES(path), component=VALUES(component), perms=VALUES(perms), update_time=VALUES(update_time);
/
SET @gz_dep_inv_menu := (SELECT m.menu_id FROM sys_menu m WHERE m.perms='gzDepartment:gzDepInventory:list' AND m.menu_type='C' ORDER BY m.menu_id LIMIT 1);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3368, '高值科室库存查询', @gz_dep_inv_menu, 1, '#', '', NULL, 1, 0, 'F', '0', '0', 'gzDepartment:gzDepInventory:query', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE @gz_dep_inv_menu IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=@gz_dep_inv_menu AND perms='gzDepartment:gzDepInventory:query') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=3368))
ON DUPLICATE KEY UPDATE menu_name=VALUES(menu_name), parent_id=VALUES(parent_id), order_num=VALUES(order_num), perms=VALUES(perms), update_time=VALUES(update_time);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3369, '高值科室库存导出', @gz_dep_inv_menu, 2, '#', '', NULL, 1, 0, 'F', '0', '0', 'gzDepartment:gzDepInventory:export', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE @gz_dep_inv_menu IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=@gz_dep_inv_menu AND perms='gzDepartment:gzDepInventory:export') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=3369))
ON DUPLICATE KEY UPDATE menu_name=VALUES(menu_name), parent_id=VALUES(parent_id), order_num=VALUES(order_num), perms=VALUES(perms), update_time=VALUES(update_time);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3370, '高值科室库存新增', @gz_dep_inv_menu, 3, '#', '', NULL, 1, 0, 'F', '0', '0', 'gzDepartment:gzDepInventory:add', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE @gz_dep_inv_menu IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=@gz_dep_inv_menu AND perms='gzDepartment:gzDepInventory:add') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=3370))
ON DUPLICATE KEY UPDATE menu_name=VALUES(menu_name), parent_id=VALUES(parent_id), order_num=VALUES(order_num), perms=VALUES(perms), update_time=VALUES(update_time);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3371, '高值科室库存修改', @gz_dep_inv_menu, 4, '#', '', NULL, 1, 0, 'F', '0', '0', 'gzDepartment:gzDepInventory:edit', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE @gz_dep_inv_menu IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=@gz_dep_inv_menu AND perms='gzDepartment:gzDepInventory:edit') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=3371))
ON DUPLICATE KEY UPDATE menu_name=VALUES(menu_name), parent_id=VALUES(parent_id), order_num=VALUES(order_num), perms=VALUES(perms), update_time=VALUES(update_time);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3372, '高值科室库存删除', @gz_dep_inv_menu, 5, '#', '', NULL, 1, 0, 'F', '0', '0', 'gzDepartment:gzDepInventory:remove', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE @gz_dep_inv_menu IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=@gz_dep_inv_menu AND perms='gzDepartment:gzDepInventory:remove') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=3372))
ON DUPLICATE KEY UPDATE menu_name=VALUES(menu_name), parent_id=VALUES(parent_id), order_num=VALUES(order_num), perms=VALUES(perms), update_time=VALUES(update_time);
/

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3373, '高值库存查询', COALESCE(@gz_root,1), 6, 'stockQuery', 'gz/stockQuery/index', NULL, 1, 0, 'C', '0', '0', 'gz:stockQuery:list', 'search', 'admin', NOW(), '1', NOW(), '高值库存查询页面', '0', '1'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='C' AND perms='gz:stockQuery:list') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=3373)
ON DUPLICATE KEY UPDATE menu_name=VALUES(menu_name), parent_id=VALUES(parent_id), order_num=VALUES(order_num), path=VALUES(path), component=VALUES(component), perms=VALUES(perms), update_time=VALUES(update_time);
/
SET @gz_stock_query_menu := (SELECT m.menu_id FROM sys_menu m WHERE m.perms='gz:stockQuery:list' AND m.menu_type='C' ORDER BY m.menu_id LIMIT 1);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3374, '高值库存导出', @gz_stock_query_menu, 1, '#', '', NULL, 1, 0, 'F', '0', '0', 'gz:stockQuery:export', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE @gz_stock_query_menu IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=@gz_stock_query_menu AND perms='gz:stockQuery:export') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=3374))
ON DUPLICATE KEY UPDATE menu_name=VALUES(menu_name), parent_id=VALUES(parent_id), order_num=VALUES(order_num), perms=VALUES(perms), update_time=VALUES(update_time);
/

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3375, '备货库存查询', COALESCE(@gz_root,1), 7, 'depotInventory', 'gz/depotInventory/index', NULL, 1, 0, 'C', '0', '0', 'gz:depotInventory:list', 'table', 'admin', NOW(), '1', NOW(), '高值备货库存查询', '0', '1'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='C' AND perms='gz:depotInventory:list') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=3375)
ON DUPLICATE KEY UPDATE menu_name=VALUES(menu_name), parent_id=VALUES(parent_id), order_num=VALUES(order_num), path=VALUES(path), component=VALUES(component), perms=VALUES(perms), update_time=VALUES(update_time);
/
SET @gz_depot_inv_menu := (SELECT m.menu_id FROM sys_menu m WHERE m.perms='gz:depotInventory:list' AND m.menu_type='C' ORDER BY m.menu_id LIMIT 1);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3376, '备货库存查询', @gz_depot_inv_menu, 1, '#', '', NULL, 1, 0, 'F', '0', '0', 'gz:depotInventory:query', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE @gz_depot_inv_menu IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=@gz_depot_inv_menu AND perms='gz:depotInventory:query') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=3376))
ON DUPLICATE KEY UPDATE menu_name=VALUES(menu_name), parent_id=VALUES(parent_id), order_num=VALUES(order_num), perms=VALUES(perms), update_time=VALUES(update_time);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3377, '备货库存新增', @gz_depot_inv_menu, 2, '#', '', NULL, 1, 0, 'F', '0', '0', 'gz:depotInventory:add', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE @gz_depot_inv_menu IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=@gz_depot_inv_menu AND perms='gz:depotInventory:add') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=3377))
ON DUPLICATE KEY UPDATE menu_name=VALUES(menu_name), parent_id=VALUES(parent_id), order_num=VALUES(order_num), perms=VALUES(perms), update_time=VALUES(update_time);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3378, '备货库存修改', @gz_depot_inv_menu, 3, '#', '', NULL, 1, 0, 'F', '0', '0', 'gz:depotInventory:edit', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE @gz_depot_inv_menu IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=@gz_depot_inv_menu AND perms='gz:depotInventory:edit') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=3378))
ON DUPLICATE KEY UPDATE menu_name=VALUES(menu_name), parent_id=VALUES(parent_id), order_num=VALUES(order_num), perms=VALUES(perms), update_time=VALUES(update_time);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3379, '备货库存删除', @gz_depot_inv_menu, 4, '#', '', NULL, 1, 0, 'F', '0', '0', 'gz:depotInventory:remove', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE @gz_depot_inv_menu IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=@gz_depot_inv_menu AND perms='gz:depotInventory:remove') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=3379))
ON DUPLICATE KEY UPDATE menu_name=VALUES(menu_name), parent_id=VALUES(parent_id), order_num=VALUES(order_num), perms=VALUES(perms), update_time=VALUES(update_time);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3380, '备货库存导出', @gz_depot_inv_menu, 5, '#', '', NULL, 1, 0, 'F', '0', '0', 'gz:depotInventory:export', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE @gz_depot_inv_menu IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=@gz_depot_inv_menu AND perms='gz:depotInventory:export') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=3380))
ON DUPLICATE KEY UPDATE menu_name=VALUES(menu_name), parent_id=VALUES(parent_id), order_num=VALUES(order_num), perms=VALUES(perms), update_time=VALUES(update_time);
/

-- ---------- 23) 入/出库及退库模块补齐（inWarehouse / outWarehouse / *Query）----------
SET @in_m := (SELECT m.menu_id FROM sys_menu m WHERE m.path='inWarehouse' AND m.menu_type='M' ORDER BY m.menu_id LIMIT 1);
/
SET @out_m := (SELECT m.menu_id FROM sys_menu m WHERE m.path='outWarehouse' AND m.menu_type='M' ORDER BY m.menu_id LIMIT 1);
/
SET @warehouse_m := (SELECT m.menu_id FROM sys_menu m WHERE m.path='warehouse' AND m.menu_type='M' ORDER BY m.menu_id LIMIT 1);
/

-- 入库申请页
INSERT INTO sys_menu (menu_id,menu_name,parent_id,order_num,path,component,`query`,is_frame,is_cache,menu_type,visible,status,perms,icon,create_by,create_time,update_by,update_time,remark,is_platform,default_open_to_customer)
SELECT 3381,'到货验收',COALESCE(@in_m,COALESCE(@warehouse_m,1)),1,'apply','inWarehouse/apply/index',NULL,1,0,'C','0','0','inWarehouse:apply:list','date','admin',NOW(),'1',NOW(),'入库申请页','0','1'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='C' AND perms='inWarehouse:apply:list') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=3381)
ON DUPLICATE KEY UPDATE menu_name=VALUES(menu_name),parent_id=VALUES(parent_id),order_num=VALUES(order_num),path=VALUES(path),component=VALUES(component),perms=VALUES(perms),update_time=VALUES(update_time);
/
SET @in_apply_c := (SELECT m.menu_id FROM sys_menu m WHERE m.perms='inWarehouse:apply:list' AND m.menu_type='C' ORDER BY m.menu_id LIMIT 1);
/
INSERT INTO sys_menu (menu_id,menu_name,parent_id,order_num,path,component,`query`,is_frame,is_cache,menu_type,visible,status,perms,icon,create_by,create_time,update_by,update_time,remark,is_platform,default_open_to_customer)
SELECT 3382,'入库查询',@in_apply_c,1,'#','',NULL,1,0,'F','0','0','inWarehouse:apply:query','#','admin',NOW(),'1',NOW(),'','0','1' FROM DUAL WHERE @in_apply_c IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=@in_apply_c AND perms='inWarehouse:apply:query') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=3382))
ON DUPLICATE KEY UPDATE menu_name=VALUES(menu_name),parent_id=VALUES(parent_id),order_num=VALUES(order_num),perms=VALUES(perms),update_time=VALUES(update_time);
/
INSERT INTO sys_menu (menu_id,menu_name,parent_id,order_num,path,component,`query`,is_frame,is_cache,menu_type,visible,status,perms,icon,create_by,create_time,update_by,update_time,remark,is_platform,default_open_to_customer)
SELECT 3383,'入库新增',@in_apply_c,2,'#','',NULL,1,0,'F','0','0','inWarehouse:apply:add','#','admin',NOW(),'1',NOW(),'','0','1' FROM DUAL WHERE @in_apply_c IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=@in_apply_c AND perms='inWarehouse:apply:add') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=3383))
ON DUPLICATE KEY UPDATE menu_name=VALUES(menu_name),parent_id=VALUES(parent_id),order_num=VALUES(order_num),perms=VALUES(perms),update_time=VALUES(update_time);
/
INSERT INTO sys_menu (menu_id,menu_name,parent_id,order_num,path,component,`query`,is_frame,is_cache,menu_type,visible,status,perms,icon,create_by,create_time,update_by,update_time,remark,is_platform,default_open_to_customer)
SELECT 3384,'入库修改',@in_apply_c,3,'#','',NULL,1,0,'F','0','0','inWarehouse:apply:edit','#','admin',NOW(),'1',NOW(),'','0','1' FROM DUAL WHERE @in_apply_c IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=@in_apply_c AND perms='inWarehouse:apply:edit') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=3384))
ON DUPLICATE KEY UPDATE menu_name=VALUES(menu_name),parent_id=VALUES(parent_id),order_num=VALUES(order_num),perms=VALUES(perms),update_time=VALUES(update_time);
/
INSERT INTO sys_menu (menu_id,menu_name,parent_id,order_num,path,component,`query`,is_frame,is_cache,menu_type,visible,status,perms,icon,create_by,create_time,update_by,update_time,remark,is_platform,default_open_to_customer)
SELECT 3385,'入库删除',@in_apply_c,4,'#','',NULL,1,0,'F','0','0','inWarehouse:apply:remove','#','admin',NOW(),'1',NOW(),'','0','1' FROM DUAL WHERE @in_apply_c IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=@in_apply_c AND perms='inWarehouse:apply:remove') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=3385))
ON DUPLICATE KEY UPDATE menu_name=VALUES(menu_name),parent_id=VALUES(parent_id),order_num=VALUES(order_num),perms=VALUES(perms),update_time=VALUES(update_time);
/
INSERT INTO sys_menu (menu_id,menu_name,parent_id,order_num,path,component,`query`,is_frame,is_cache,menu_type,visible,status,perms,icon,create_by,create_time,update_by,update_time,remark,is_platform,default_open_to_customer)
SELECT 3386,'入库导出',@in_apply_c,5,'#','',NULL,1,0,'F','0','0','inWarehouse:apply:export','#','admin',NOW(),'1',NOW(),'','0','1' FROM DUAL WHERE @in_apply_c IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=@in_apply_c AND perms='inWarehouse:apply:export') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=3386))
ON DUPLICATE KEY UPDATE menu_name=VALUES(menu_name),parent_id=VALUES(parent_id),order_num=VALUES(order_num),perms=VALUES(perms),update_time=VALUES(update_time);
/
INSERT INTO sys_menu (menu_id,menu_name,parent_id,order_num,path,component,`query`,is_frame,is_cache,menu_type,visible,status,perms,icon,create_by,create_time,update_by,update_time,remark,is_platform,default_open_to_customer)
SELECT 3387,'入库审核',@in_apply_c,6,'#','',NULL,1,0,'F','0','0','inWarehouse:apply:audit','#','admin',NOW(),'1',NOW(),'','0','1' FROM DUAL WHERE @in_apply_c IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=@in_apply_c AND perms='inWarehouse:apply:audit') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=3387))
ON DUPLICATE KEY UPDATE menu_name=VALUES(menu_name),parent_id=VALUES(parent_id),order_num=VALUES(order_num),perms=VALUES(perms),update_time=VALUES(update_time);
/
INSERT INTO sys_menu (menu_id,menu_name,parent_id,order_num,path,component,`query`,is_frame,is_cache,menu_type,visible,status,perms,icon,create_by,create_time,update_by,update_time,remark,is_platform,default_open_to_customer)
SELECT 3388,'由订单生成入库明细',@in_apply_c,7,'#','',NULL,1,0,'F','0','0','inWarehouse:apply:createRkEntriesByDingdan','#','admin',NOW(),'1',NOW(),'后端生成明细接口权限','0','1' FROM DUAL WHERE @in_apply_c IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=@in_apply_c AND perms='inWarehouse:apply:createRkEntriesByDingdan') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=3388))
ON DUPLICATE KEY UPDATE menu_name=VALUES(menu_name),parent_id=VALUES(parent_id),order_num=VALUES(order_num),perms=VALUES(perms),remark=VALUES(remark),update_time=VALUES(update_time);
/

-- 入/退货报表、出/退货报表
INSERT INTO sys_menu (menu_id,menu_name,parent_id,order_num,path,component,`query`,is_frame,is_cache,menu_type,visible,status,perms,icon,create_by,create_time,update_by,update_time,remark,is_platform,default_open_to_customer)
SELECT 3389,'入退库报表',COALESCE(@in_m,COALESCE(@warehouse_m,1)),6,'inWarehouseQuery','inWarehouse/inWarehouseQuery/index',NULL,1,0,'C','0','0','inWarehouse:inWarehouseQuery:list','chart','admin',NOW(),'1',NOW(),'入退库明细/汇总报表','0','1'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='C' AND perms='inWarehouse:inWarehouseQuery:list') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=3389)
ON DUPLICATE KEY UPDATE menu_name=VALUES(menu_name),parent_id=VALUES(parent_id),order_num=VALUES(order_num),path=VALUES(path),component=VALUES(component),perms=VALUES(perms),update_time=VALUES(update_time);
/
INSERT INTO sys_menu (menu_id,menu_name,parent_id,order_num,path,component,`query`,is_frame,is_cache,menu_type,visible,status,perms,icon,create_by,create_time,update_by,update_time,remark,is_platform,default_open_to_customer)
SELECT 3390,'出退库报表',COALESCE(@out_m,COALESCE(@warehouse_m,1)),6,'outWarehouseQuery','outWarehouse/outWarehouseQuery/index',NULL,1,0,'C','0','0','outWarehouse:outWarehouseQuery:list','chart','admin',NOW(),'1',NOW(),'出退库明细/汇总报表','0','1'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='C' AND perms='outWarehouse:outWarehouseQuery:list') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=3390)
ON DUPLICATE KEY UPDATE menu_name=VALUES(menu_name),parent_id=VALUES(parent_id),order_num=VALUES(order_num),path=VALUES(path),component=VALUES(component),perms=VALUES(perms),update_time=VALUES(update_time);
/
SET @out_ck_query_c := (SELECT m.menu_id FROM sys_menu m WHERE m.perms='outWarehouse:outWarehouseQuery:list' AND m.menu_type='C' ORDER BY m.menu_id LIMIT 1);
/
INSERT INTO sys_menu (menu_id,menu_name,parent_id,order_num,path,component,`query`,is_frame,is_cache,menu_type,visible,status,perms,icon,create_by,create_time,update_by,update_time,remark,is_platform,default_open_to_customer)
SELECT 3501,'出退库整体导出',@out_ck_query_c,1,'#','',NULL,1,0,'F','0','0','outWarehouse:outWarehouseQuery:exportOverall','#','admin',NOW(),'1',NOW(),'出退库明细单表导出 POST /warehouse/rthWarehouse/exportCTKOverall','0','1' FROM DUAL WHERE @out_ck_query_c IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=@out_ck_query_c AND perms='outWarehouse:outWarehouseQuery:exportOverall') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=3501))
ON DUPLICATE KEY UPDATE menu_name=VALUES(menu_name),parent_id=VALUES(parent_id),order_num=VALUES(order_num),perms=VALUES(perms),remark=VALUES(remark),update_time=VALUES(update_time);
/

-- 出库申请页 + 按钮
INSERT INTO sys_menu (menu_id,menu_name,parent_id,order_num,path,component,`query`,is_frame,is_cache,menu_type,visible,status,perms,icon,create_by,create_time,update_by,update_time,remark,is_platform,default_open_to_customer)
SELECT 3391,'出库申请',COALESCE(@out_m,COALESCE(@warehouse_m,1)),1,'apply','outWarehouse/apply/index',NULL,1,0,'C','0','0','outWarehouse:apply:list','input','admin',NOW(),'1',NOW(),'出库申请页','0','1'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='C' AND perms='outWarehouse:apply:list') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=3391)
ON DUPLICATE KEY UPDATE menu_name=VALUES(menu_name),parent_id=VALUES(parent_id),order_num=VALUES(order_num),path=VALUES(path),component=VALUES(component),perms=VALUES(perms),update_time=VALUES(update_time);
/
SET @out_apply_c := (SELECT m.menu_id FROM sys_menu m WHERE m.perms='outWarehouse:apply:list' AND m.menu_type='C' ORDER BY m.menu_id LIMIT 1);
/
INSERT INTO sys_menu (menu_id,menu_name,parent_id,order_num,path,component,`query`,is_frame,is_cache,menu_type,visible,status,perms,icon,create_by,create_time,update_by,update_time,remark,is_platform,default_open_to_customer)
SELECT 3392,'出库查询',@out_apply_c,1,'#','',NULL,1,0,'F','0','0','outWarehouse:apply:query','#','admin',NOW(),'1',NOW(),'','0','1' FROM DUAL WHERE @out_apply_c IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=@out_apply_c AND perms='outWarehouse:apply:query') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=3392))
ON DUPLICATE KEY UPDATE menu_name=VALUES(menu_name),parent_id=VALUES(parent_id),order_num=VALUES(order_num),perms=VALUES(perms),update_time=VALUES(update_time);
/
INSERT INTO sys_menu (menu_id,menu_name,parent_id,order_num,path,component,`query`,is_frame,is_cache,menu_type,visible,status,perms,icon,create_by,create_time,update_by,update_time,remark,is_platform,default_open_to_customer)
SELECT 3393,'出库新增',@out_apply_c,2,'#','',NULL,1,0,'F','0','0','outWarehouse:apply:add','#','admin',NOW(),'1',NOW(),'','0','1' FROM DUAL WHERE @out_apply_c IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=@out_apply_c AND perms='outWarehouse:apply:add') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=3393))
ON DUPLICATE KEY UPDATE menu_name=VALUES(menu_name),parent_id=VALUES(parent_id),order_num=VALUES(order_num),perms=VALUES(perms),update_time=VALUES(update_time);
/
INSERT INTO sys_menu (menu_id,menu_name,parent_id,order_num,path,component,`query`,is_frame,is_cache,menu_type,visible,status,perms,icon,create_by,create_time,update_by,update_time,remark,is_platform,default_open_to_customer)
SELECT 3394,'出库修改',@out_apply_c,3,'#','',NULL,1,0,'F','0','0','outWarehouse:apply:edit','#','admin',NOW(),'1',NOW(),'','0','1' FROM DUAL WHERE @out_apply_c IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=@out_apply_c AND perms='outWarehouse:apply:edit') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=3394))
ON DUPLICATE KEY UPDATE menu_name=VALUES(menu_name),parent_id=VALUES(parent_id),order_num=VALUES(order_num),perms=VALUES(perms),update_time=VALUES(update_time);
/
INSERT INTO sys_menu (menu_id,menu_name,parent_id,order_num,path,component,`query`,is_frame,is_cache,menu_type,visible,status,perms,icon,create_by,create_time,update_by,update_time,remark,is_platform,default_open_to_customer)
SELECT 3395,'出库删除',@out_apply_c,4,'#','',NULL,1,0,'F','0','0','outWarehouse:apply:remove','#','admin',NOW(),'1',NOW(),'','0','1' FROM DUAL WHERE @out_apply_c IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=@out_apply_c AND perms='outWarehouse:apply:remove') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=3395))
ON DUPLICATE KEY UPDATE menu_name=VALUES(menu_name),parent_id=VALUES(parent_id),order_num=VALUES(order_num),perms=VALUES(perms),update_time=VALUES(update_time);
/
INSERT INTO sys_menu (menu_id,menu_name,parent_id,order_num,path,component,`query`,is_frame,is_cache,menu_type,visible,status,perms,icon,create_by,create_time,update_by,update_time,remark,is_platform,default_open_to_customer)
SELECT 3396,'出库导出',@out_apply_c,5,'#','',NULL,1,0,'F','0','0','outWarehouse:apply:export','#','admin',NOW(),'1',NOW(),'','0','1' FROM DUAL WHERE @out_apply_c IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=@out_apply_c AND perms='outWarehouse:apply:export') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=3396))
ON DUPLICATE KEY UPDATE menu_name=VALUES(menu_name),parent_id=VALUES(parent_id),order_num=VALUES(order_num),perms=VALUES(perms),update_time=VALUES(update_time);
/
INSERT INTO sys_menu (menu_id,menu_name,parent_id,order_num,path,component,`query`,is_frame,is_cache,menu_type,visible,status,perms,icon,create_by,create_time,update_by,update_time,remark,is_platform,default_open_to_customer)
SELECT 3397,'出库审核',@out_apply_c,6,'#','',NULL,1,0,'F','0','0','outWarehouse:apply:audit','#','admin',NOW(),'1',NOW(),'','0','1' FROM DUAL WHERE @out_apply_c IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=@out_apply_c AND perms='outWarehouse:apply:audit') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=3397))
ON DUPLICATE KEY UPDATE menu_name=VALUES(menu_name),parent_id=VALUES(parent_id),order_num=VALUES(order_num),perms=VALUES(perms),update_time=VALUES(update_time);
/
INSERT INTO sys_menu (menu_id,menu_name,parent_id,order_num,path,component,`query`,is_frame,is_cache,menu_type,visible,status,perms,icon,create_by,create_time,update_by,update_time,remark,is_platform,default_open_to_customer)
SELECT 3398,'审核页导出',@out_apply_c,7,'#','',NULL,1,0,'F','0','0','outWarehouse:audit:export','#','admin',NOW(),'1',NOW(),'','0','1' FROM DUAL WHERE @out_apply_c IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=@out_apply_c AND perms='outWarehouse:audit:export') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=3398))
ON DUPLICATE KEY UPDATE menu_name=VALUES(menu_name),parent_id=VALUES(parent_id),order_num=VALUES(order_num),perms=VALUES(perms),update_time=VALUES(update_time);
/
INSERT INTO sys_menu (menu_id,menu_name,parent_id,order_num,path,component,`query`,is_frame,is_cache,menu_type,visible,status,perms,icon,create_by,create_time,update_by,update_time,remark,is_platform,default_open_to_customer)
SELECT 3399,'生成出库明细(科室申领)',@out_apply_c,8,'#','',NULL,1,0,'F','0','0','outWarehouse:apply:createCkEntriesByDApply','#','admin',NOW(),'1',NOW(),'后端生成明细接口权限','0','1' FROM DUAL WHERE @out_apply_c IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=@out_apply_c AND perms='outWarehouse:apply:createCkEntriesByDApply') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=3399))
ON DUPLICATE KEY UPDATE menu_name=VALUES(menu_name),parent_id=VALUES(parent_id),order_num=VALUES(order_num),perms=VALUES(perms),remark=VALUES(remark),update_time=VALUES(update_time);
/
INSERT INTO sys_menu (menu_id,menu_name,parent_id,order_num,path,component,`query`,is_frame,is_cache,menu_type,visible,status,perms,icon,create_by,create_time,update_by,update_time,remark,is_platform,default_open_to_customer)
SELECT 3400,'生成出库明细(入库单)',@out_apply_c,9,'#','',NULL,1,0,'F','0','0','outWarehouse:apply:createCkEntriesByRkApply','#','admin',NOW(),'1',NOW(),'后端生成明细接口权限','0','1' FROM DUAL WHERE @out_apply_c IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=@out_apply_c AND perms='outWarehouse:apply:createCkEntriesByRkApply') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=3400))
ON DUPLICATE KEY UPDATE menu_name=VALUES(menu_name),parent_id=VALUES(parent_id),order_num=VALUES(order_num),perms=VALUES(perms),remark=VALUES(remark),update_time=VALUES(update_time);
/

-- 入退货/出退货
INSERT INTO sys_menu (menu_id,menu_name,parent_id,order_num,path,component,`query`,is_frame,is_cache,menu_type,visible,status,perms,icon,create_by,create_time,update_by,update_time,remark,is_platform,default_open_to_customer)
SELECT 3401,'入退货申请',COALESCE(@in_m,COALESCE(@warehouse_m,1)),3,'refundGoodsApply','inWarehouse/refundGoodsApply/index',NULL,1,0,'C','0','0','inWarehouse:refundGoodsApply:list','refresh','admin',NOW(),'1',NOW(),'入退货申请页','0','1'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='C' AND perms='inWarehouse:refundGoodsApply:list') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=3401)
ON DUPLICATE KEY UPDATE menu_name=VALUES(menu_name),parent_id=VALUES(parent_id),order_num=VALUES(order_num),path=VALUES(path),component=VALUES(component),perms=VALUES(perms),update_time=VALUES(update_time);
/
SET @in_refund_c := (SELECT m.menu_id FROM sys_menu m WHERE m.perms='inWarehouse:refundGoodsApply:list' AND m.menu_type='C' ORDER BY m.menu_id LIMIT 1);
/
INSERT INTO sys_menu (menu_id,menu_name,parent_id,order_num,path,component,`query`,is_frame,is_cache,menu_type,visible,status,perms,icon,create_by,create_time,update_by,update_time,remark,is_platform,default_open_to_customer)
SELECT 3402,'退货查询',@in_refund_c,1,'#','',NULL,1,0,'F','0','0','inWarehouse:refundGoodsApply:query','#','admin',NOW(),'1',NOW(),'','0','1' FROM DUAL WHERE @in_refund_c IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=@in_refund_c AND perms='inWarehouse:refundGoodsApply:query') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=3402))
ON DUPLICATE KEY UPDATE menu_name=VALUES(menu_name),parent_id=VALUES(parent_id),order_num=VALUES(order_num),perms=VALUES(perms),update_time=VALUES(update_time);
/
INSERT INTO sys_menu (menu_id,menu_name,parent_id,order_num,path,component,`query`,is_frame,is_cache,menu_type,visible,status,perms,icon,create_by,create_time,update_by,update_time,remark,is_platform,default_open_to_customer)
SELECT 3403,'退货新增',@in_refund_c,2,'#','',NULL,1,0,'F','0','0','inWarehouse:refundGoodsApply:add','#','admin',NOW(),'1',NOW(),'','0','1' FROM DUAL WHERE @in_refund_c IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=@in_refund_c AND perms='inWarehouse:refundGoodsApply:add') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=3403))
ON DUPLICATE KEY UPDATE menu_name=VALUES(menu_name),parent_id=VALUES(parent_id),order_num=VALUES(order_num),perms=VALUES(perms),update_time=VALUES(update_time);
/
INSERT INTO sys_menu (menu_id,menu_name,parent_id,order_num,path,component,`query`,is_frame,is_cache,menu_type,visible,status,perms,icon,create_by,create_time,update_by,update_time,remark,is_platform,default_open_to_customer)
SELECT 3404,'退货修改',@in_refund_c,3,'#','',NULL,1,0,'F','0','0','inWarehouse:refundGoodsApply:edit','#','admin',NOW(),'1',NOW(),'','0','1' FROM DUAL WHERE @in_refund_c IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=@in_refund_c AND perms='inWarehouse:refundGoodsApply:edit') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=3404))
ON DUPLICATE KEY UPDATE menu_name=VALUES(menu_name),parent_id=VALUES(parent_id),order_num=VALUES(order_num),perms=VALUES(perms),update_time=VALUES(update_time);
/
INSERT INTO sys_menu (menu_id,menu_name,parent_id,order_num,path,component,`query`,is_frame,is_cache,menu_type,visible,status,perms,icon,create_by,create_time,update_by,update_time,remark,is_platform,default_open_to_customer)
SELECT 3405,'退货删除',@in_refund_c,4,'#','',NULL,1,0,'F','0','0','inWarehouse:refundGoodsApply:remove','#','admin',NOW(),'1',NOW(),'','0','1' FROM DUAL WHERE @in_refund_c IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=@in_refund_c AND perms='inWarehouse:refundGoodsApply:remove') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=3405))
ON DUPLICATE KEY UPDATE menu_name=VALUES(menu_name),parent_id=VALUES(parent_id),order_num=VALUES(order_num),perms=VALUES(perms),update_time=VALUES(update_time);
/
INSERT INTO sys_menu (menu_id,menu_name,parent_id,order_num,path,component,`query`,is_frame,is_cache,menu_type,visible,status,perms,icon,create_by,create_time,update_by,update_time,remark,is_platform,default_open_to_customer)
SELECT 3406,'退货导出',@in_refund_c,5,'#','',NULL,1,0,'F','0','0','inWarehouse:refundGoodsApply:export','#','admin',NOW(),'1',NOW(),'','0','1' FROM DUAL WHERE @in_refund_c IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=@in_refund_c AND perms='inWarehouse:refundGoodsApply:export') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=3406))
ON DUPLICATE KEY UPDATE menu_name=VALUES(menu_name),parent_id=VALUES(parent_id),order_num=VALUES(order_num),perms=VALUES(perms),update_time=VALUES(update_time);
/
INSERT INTO sys_menu (menu_id,menu_name,parent_id,order_num,path,component,`query`,is_frame,is_cache,menu_type,visible,status,perms,icon,create_by,create_time,update_by,update_time,remark,is_platform,default_open_to_customer)
SELECT 3407,'退货审核',@in_refund_c,6,'#','',NULL,1,0,'F','0','0','inWarehouse:refundGoodsApply:audit','#','admin',NOW(),'1',NOW(),'','0','1' FROM DUAL WHERE @in_refund_c IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=@in_refund_c AND perms='inWarehouse:refundGoodsApply:audit') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=3407))
ON DUPLICATE KEY UPDATE menu_name=VALUES(menu_name),parent_id=VALUES(parent_id),order_num=VALUES(order_num),perms=VALUES(perms),update_time=VALUES(update_time);
/

INSERT INTO sys_menu (menu_id,menu_name,parent_id,order_num,path,component,`query`,is_frame,is_cache,menu_type,visible,status,perms,icon,create_by,create_time,update_by,update_time,remark,is_platform,default_open_to_customer)
SELECT 3408,'退库申请',COALESCE(@out_m,COALESCE(@warehouse_m,1)),3,'refundDepotApply','outWarehouse/refundDepotApply/index',NULL,1,0,'C','0','0','outWarehouse:refundDepotApply:list','refresh','admin',NOW(),'1',NOW(),'退库申请页','0','1'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='C' AND perms='outWarehouse:refundDepotApply:list') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=3408)
ON DUPLICATE KEY UPDATE menu_name=VALUES(menu_name),parent_id=VALUES(parent_id),order_num=VALUES(order_num),path=VALUES(path),component=VALUES(component),perms=VALUES(perms),update_time=VALUES(update_time);
/
SET @out_refund_c := (SELECT m.menu_id FROM sys_menu m WHERE m.perms='outWarehouse:refundDepotApply:list' AND m.menu_type='C' ORDER BY m.menu_id LIMIT 1);
/
INSERT INTO sys_menu (menu_id,menu_name,parent_id,order_num,path,component,`query`,is_frame,is_cache,menu_type,visible,status,perms,icon,create_by,create_time,update_by,update_time,remark,is_platform,default_open_to_customer)
SELECT 3409,'退库查询',@out_refund_c,1,'#','',NULL,1,0,'F','0','0','outWarehouse:refundDepotApply:query','#','admin',NOW(),'1',NOW(),'','0','1' FROM DUAL WHERE @out_refund_c IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=@out_refund_c AND perms='outWarehouse:refundDepotApply:query') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=3409))
ON DUPLICATE KEY UPDATE menu_name=VALUES(menu_name),parent_id=VALUES(parent_id),order_num=VALUES(order_num),perms=VALUES(perms),update_time=VALUES(update_time);
/
INSERT INTO sys_menu (menu_id,menu_name,parent_id,order_num,path,component,`query`,is_frame,is_cache,menu_type,visible,status,perms,icon,create_by,create_time,update_by,update_time,remark,is_platform,default_open_to_customer)
SELECT 3410,'退库新增',@out_refund_c,2,'#','',NULL,1,0,'F','0','0','outWarehouse:refundDepotApply:add','#','admin',NOW(),'1',NOW(),'','0','1' FROM DUAL WHERE @out_refund_c IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=@out_refund_c AND perms='outWarehouse:refundDepotApply:add') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=3410))
ON DUPLICATE KEY UPDATE menu_name=VALUES(menu_name),parent_id=VALUES(parent_id),order_num=VALUES(order_num),perms=VALUES(perms),update_time=VALUES(update_time);
/
INSERT INTO sys_menu (menu_id,menu_name,parent_id,order_num,path,component,`query`,is_frame,is_cache,menu_type,visible,status,perms,icon,create_by,create_time,update_by,update_time,remark,is_platform,default_open_to_customer)
SELECT 3411,'退库修改',@out_refund_c,3,'#','',NULL,1,0,'F','0','0','outWarehouse:refundDepotApply:edit','#','admin',NOW(),'1',NOW(),'','0','1' FROM DUAL WHERE @out_refund_c IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=@out_refund_c AND perms='outWarehouse:refundDepotApply:edit') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=3411))
ON DUPLICATE KEY UPDATE menu_name=VALUES(menu_name),parent_id=VALUES(parent_id),order_num=VALUES(order_num),perms=VALUES(perms),update_time=VALUES(update_time);
/
INSERT INTO sys_menu (menu_id,menu_name,parent_id,order_num,path,component,`query`,is_frame,is_cache,menu_type,visible,status,perms,icon,create_by,create_time,update_by,update_time,remark,is_platform,default_open_to_customer)
SELECT 3412,'退库删除',@out_refund_c,4,'#','',NULL,1,0,'F','0','0','outWarehouse:refundDepotApply:remove','#','admin',NOW(),'1',NOW(),'','0','1' FROM DUAL WHERE @out_refund_c IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=@out_refund_c AND perms='outWarehouse:refundDepotApply:remove') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=3412))
ON DUPLICATE KEY UPDATE menu_name=VALUES(menu_name),parent_id=VALUES(parent_id),order_num=VALUES(order_num),perms=VALUES(perms),update_time=VALUES(update_time);
/
INSERT INTO sys_menu (menu_id,menu_name,parent_id,order_num,path,component,`query`,is_frame,is_cache,menu_type,visible,status,perms,icon,create_by,create_time,update_by,update_time,remark,is_platform,default_open_to_customer)
SELECT 3413,'退库导出',@out_refund_c,5,'#','',NULL,1,0,'F','0','0','outWarehouse:refundDepotApply:export','#','admin',NOW(),'1',NOW(),'','0','1' FROM DUAL WHERE @out_refund_c IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=@out_refund_c AND perms='outWarehouse:refundDepotApply:export') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=3413))
ON DUPLICATE KEY UPDATE menu_name=VALUES(menu_name),parent_id=VALUES(parent_id),order_num=VALUES(order_num),perms=VALUES(perms),update_time=VALUES(update_time);
/
INSERT INTO sys_menu (menu_id,menu_name,parent_id,order_num,path,component,`query`,is_frame,is_cache,menu_type,visible,status,perms,icon,create_by,create_time,update_by,update_time,remark,is_platform,default_open_to_customer)
SELECT 3414,'退库审核',@out_refund_c,6,'#','',NULL,1,0,'F','0','0','outWarehouse:refundDepotApply:audit','#','admin',NOW(),'1',NOW(),'','0','1' FROM DUAL WHERE @out_refund_c IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=@out_refund_c AND perms='outWarehouse:refundDepotApply:audit') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=3414))
ON DUPLICATE KEY UPDATE menu_name=VALUES(menu_name),parent_id=VALUES(parent_id),order_num=VALUES(order_num),perms=VALUES(perms),update_time=VALUES(update_time);
/
INSERT INTO sys_menu (menu_id,menu_name,parent_id,order_num,path,component,`query`,is_frame,is_cache,menu_type,visible,status,perms,icon,create_by,create_time,update_by,update_time,remark,is_platform,default_open_to_customer)
SELECT 3415,'由出库单生成退库明细',@out_refund_c,7,'#','',NULL,1,0,'F','0','0','outWarehouse:refundDepotApply:createTkEntriesByCkApply','#','admin',NOW(),'1',NOW(),'后端生成明细接口权限','0','1' FROM DUAL WHERE @out_refund_c IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=@out_refund_c AND perms='outWarehouse:refundDepotApply:createTkEntriesByCkApply') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=3415))
ON DUPLICATE KEY UPDATE menu_name=VALUES(menu_name),parent_id=VALUES(parent_id),order_num=VALUES(order_num),perms=VALUES(perms),remark=VALUES(remark),update_time=VALUES(update_time);
/

-- ---------- 23) 前后端已有权限、sys_menu 补齐（采购计划/科室批量消耗/收货确认/基础资料子页/高值入库与退货申请/结算列表/盈亏新增）----------
SET @foundation_root := (SELECT m.menu_id FROM sys_menu m WHERE m.menu_name = '基础资料' AND m.menu_type = 'M' ORDER BY m.menu_id LIMIT 1);
/
SET @department_root := (SELECT m.menu_id FROM sys_menu m WHERE m.path = 'department' AND m.menu_type = 'M' ORDER BY m.menu_id LIMIT 1);
/
SET @gz_root := (SELECT m.menu_id FROM sys_menu m WHERE m.path = 'gz' AND m.menu_type = 'M' ORDER BY m.menu_id LIMIT 1);
/
SET @caigou_parent := COALESCE(
  (SELECT m.menu_id FROM sys_menu m WHERE m.menu_type = 'M' AND m.path = 'caigou' ORDER BY m.menu_id LIMIT 1),
  (SELECT m.parent_id FROM sys_menu m WHERE m.menu_type = 'C' AND m.component = 'caigou/dingdan/index' ORDER BY m.menu_id LIMIT 1),
  1
);
/

-- 23.1 结算申请：列表权限 settlement:apply:list（父菜单优先 component=settlement/apply/index，否则 menu_id=1547）
SET @settlement_apply_parent := COALESCE(
  (SELECT menu_id FROM sys_menu WHERE menu_type = 'C' AND component = 'settlement/apply/index' ORDER BY menu_id LIMIT 1),
  (SELECT menu_id FROM sys_menu WHERE menu_id = 1547 AND menu_type = 'C' LIMIT 1)
);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3416, '结算申请列表', @settlement_apply_parent, 0, '#', '', NULL, 1, 0, 'F', '0', '0', 'settlement:apply:list', '#', 'admin', NOW(), '1', NOW(), '与前端 v-hasPermi settlement:apply:list 一致', '0', '1'
FROM DUAL
WHERE @settlement_apply_parent IS NOT NULL
  AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'F' AND parent_id = @settlement_apply_parent AND perms = 'settlement:apply:list')
    OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 3416))
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), parent_id = VALUES(parent_id), order_num = VALUES(order_num), perms = VALUES(perms), update_time = VALUES(update_time);
/

-- 23.2 采购计划 caigou/jihua（CaigouJihuaController）
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3417, '采购计划', @caigou_parent, (SELECT IFNULL(MAX(order_num), 0) + 1 FROM sys_menu WHERE parent_id = @caigou_parent), 'jihua', 'caigou/jihua/index', NULL, 1, 0, 'C', '0', '0', 'caigou:jihua:list', 'list', 'admin', NOW(), '1', NOW(), '采购计划列表', '0', '1'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'C' AND component = 'caigou/jihua/index') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 3417)
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), parent_id = VALUES(parent_id), order_num = VALUES(order_num), path = VALUES(path), component = VALUES(component), perms = VALUES(perms), update_time = VALUES(update_time);
/
SET @jihua_menu := (SELECT menu_id FROM sys_menu WHERE menu_type = 'C' AND component = 'caigou/jihua/index' ORDER BY menu_id DESC LIMIT 1);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3418, '采购计划新增', @jihua_menu, 1, '#', '', NULL, 1, 0, 'F', '0', '0', 'caigou:jihua:add', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE @jihua_menu IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'F' AND parent_id = @jihua_menu AND perms = 'caigou:jihua:add') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 3418))
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), parent_id = VALUES(parent_id), order_num = VALUES(order_num), perms = VALUES(perms), update_time = VALUES(update_time);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3419, '采购计划修改', @jihua_menu, 2, '#', '', NULL, 1, 0, 'F', '0', '0', 'caigou:jihua:edit', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE @jihua_menu IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'F' AND parent_id = @jihua_menu AND perms = 'caigou:jihua:edit') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 3419))
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), parent_id = VALUES(parent_id), order_num = VALUES(order_num), perms = VALUES(perms), update_time = VALUES(update_time);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3420, '采购计划导出', @jihua_menu, 3, '#', '', NULL, 1, 0, 'F', '0', '0', 'caigou:jihua:export', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE @jihua_menu IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'F' AND parent_id = @jihua_menu AND perms = 'caigou:jihua:export') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 3420))
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), parent_id = VALUES(parent_id), order_num = VALUES(order_num), perms = VALUES(perms), update_time = VALUES(update_time);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3421, '采购计划删除', @jihua_menu, 4, '#', '', NULL, 1, 0, 'F', '0', '0', 'caigou:jihua:remove', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE @jihua_menu IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'F' AND parent_id = @jihua_menu AND perms = 'caigou:jihua:remove') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 3421))
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), parent_id = VALUES(parent_id), order_num = VALUES(order_num), perms = VALUES(perms), update_time = VALUES(update_time);
/

-- 23.3 科室批量消耗 department/batchConsume（DeptBatchConsumeController）
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3422, '科室批量消耗', COALESCE(@department_root, 1), (SELECT IFNULL(MAX(order_num), 0) + 1 FROM sys_menu WHERE parent_id = COALESCE(@department_root, 1)), 'batchConsume', 'department/batchConsume/index', NULL, 1, 0, 'C', '0', '0', 'department:batchConsume:list', 'list', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'C' AND component = 'department/batchConsume/index') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 3422)
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), parent_id = VALUES(parent_id), order_num = VALUES(order_num), path = VALUES(path), component = VALUES(component), perms = VALUES(perms), update_time = VALUES(update_time);
/
SET @batch_consume_menu := (SELECT menu_id FROM sys_menu WHERE menu_type = 'C' AND component = 'department/batchConsume/index' ORDER BY menu_id DESC LIMIT 1);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3423, '批量消耗新增', @batch_consume_menu, 1, '#', '', NULL, 1, 0, 'F', '0', '0', 'department:batchConsume:add', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE @batch_consume_menu IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'F' AND parent_id = @batch_consume_menu AND perms = 'department:batchConsume:add') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 3423))
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), parent_id = VALUES(parent_id), order_num = VALUES(order_num), perms = VALUES(perms), update_time = VALUES(update_time);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3424, '批量消耗修改', @batch_consume_menu, 2, '#', '', NULL, 1, 0, 'F', '0', '0', 'department:batchConsume:edit', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE @batch_consume_menu IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'F' AND parent_id = @batch_consume_menu AND perms = 'department:batchConsume:edit') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 3424))
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), parent_id = VALUES(parent_id), order_num = VALUES(order_num), perms = VALUES(perms), update_time = VALUES(update_time);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3425, '批量消耗导出', @batch_consume_menu, 3, '#', '', NULL, 1, 0, 'F', '0', '0', 'department:batchConsume:export', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE @batch_consume_menu IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'F' AND parent_id = @batch_consume_menu AND perms = 'department:batchConsume:export') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 3425))
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), parent_id = VALUES(parent_id), order_num = VALUES(order_num), perms = VALUES(perms), update_time = VALUES(update_time);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3426, '批量消耗删除', @batch_consume_menu, 4, '#', '', NULL, 1, 0, 'F', '0', '0', 'department:batchConsume:remove', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE @batch_consume_menu IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'F' AND parent_id = @batch_consume_menu AND perms = 'department:batchConsume:remove') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 3426))
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), parent_id = VALUES(parent_id), order_num = VALUES(order_num), perms = VALUES(perms), update_time = VALUES(update_time);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3427, '批量消耗审核', @batch_consume_menu, 5, '#', '', NULL, 1, 0, 'F', '0', '0', 'department:batchConsume:audit', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE @batch_consume_menu IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'F' AND parent_id = @batch_consume_menu AND perms = 'department:batchConsume:audit') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 3427))
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), parent_id = VALUES(parent_id), order_num = VALUES(order_num), perms = VALUES(perms), update_time = VALUES(update_time);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3498, '批量消耗详情', @batch_consume_menu, 6, '#', '', NULL, 1, 0, 'F', '0', '0', 'department:batchConsume:query', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE @batch_consume_menu IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'F' AND parent_id = @batch_consume_menu AND perms = 'department:batchConsume:query') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 3498))
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), parent_id = VALUES(parent_id), order_num = VALUES(order_num), perms = VALUES(perms), update_time = VALUES(update_time);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3499, '引用出库单', @batch_consume_menu, 7, '#', '', NULL, 1, 0, 'F', '0', '0', 'department:batchConsume:refOutOrder', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE @batch_consume_menu IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'F' AND parent_id = @batch_consume_menu AND perms = 'department:batchConsume:refOutOrder') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 3499))
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), parent_id = VALUES(parent_id), order_num = VALUES(order_num), perms = VALUES(perms), update_time = VALUES(update_time);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3500, '退消耗', @batch_consume_menu, 8, '#', '', NULL, 1, 0, 'F', '0', '0', 'department:batchConsume:reverse', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE @batch_consume_menu IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'F' AND parent_id = @batch_consume_menu AND perms = 'department:batchConsume:reverse') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 3500))
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), parent_id = VALUES(parent_id), order_num = VALUES(order_num), perms = VALUES(perms), update_time = VALUES(update_time);
/

-- 23.3.1 患者收费查询（HIS 镜像抓取与查询 HisPatientChargeController）
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3601, '患者收费查询', COALESCE(@department_root, 1), (SELECT IFNULL(MAX(order_num), 0) + 1 FROM sys_menu WHERE parent_id = COALESCE(@department_root, 1)), 'patientCharge', 'department/patientCharge/index', NULL, 1, 0, 'C', '0', '0', 'department:patientCharge:list', 'money', 'admin', NOW(), '1', NOW(), 'HIS计费镜像', '0', '1'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'C' AND component = 'department/patientCharge/index') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 3601)
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), parent_id = VALUES(parent_id), order_num = VALUES(order_num), path = VALUES(path), component = VALUES(component), perms = VALUES(perms), update_time = VALUES(update_time);
/
SET @patient_charge_menu := (SELECT menu_id FROM sys_menu WHERE menu_type = 'C' AND component = 'department/patientCharge/index' ORDER BY menu_id DESC LIMIT 1);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3602, '住院计费抓取', @patient_charge_menu, 1, '#', '', NULL, 1, 0, 'F', '0', '0', 'department:patientCharge:fetchInpatient', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE @patient_charge_menu IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'F' AND parent_id = @patient_charge_menu AND perms = 'department:patientCharge:fetchInpatient') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 3602))
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), parent_id = VALUES(parent_id), order_num = VALUES(order_num), perms = VALUES(perms), update_time = VALUES(update_time);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3603, '门诊计费抓取', @patient_charge_menu, 2, '#', '', NULL, 1, 0, 'F', '0', '0', 'department:patientCharge:fetchOutpatient', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE @patient_charge_menu IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'F' AND parent_id = @patient_charge_menu AND perms = 'department:patientCharge:fetchOutpatient') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 3603))
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), parent_id = VALUES(parent_id), order_num = VALUES(order_num), perms = VALUES(perms), update_time = VALUES(update_time);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3604, '计费镜像消耗处理(兼容)', @patient_charge_menu, 3, '#', '', NULL, 1, 0, 'F', '0', '0', 'department:patientCharge:generateConsume', '#', 'admin', NOW(), '1', NOW(), '兼容旧权限：与「低值/高值」细粒度权限二选一或同时授予；明细页低值/高值按钮', '0', '1'
FROM DUAL WHERE @patient_charge_menu IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'F' AND parent_id = @patient_charge_menu AND perms = 'department:patientCharge:generateConsume') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 3604))
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), parent_id = VALUES(parent_id), order_num = VALUES(order_num), perms = VALUES(perms), remark = VALUES(remark), update_time = VALUES(update_time);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3605, '计费镜像低值处理', @patient_charge_menu, 4, '#', '', NULL, 1, 0, 'F', '0', '0', 'department:patientCharge:processMirrorLow', '#', 'admin', NOW(), '1', NOW(), 'POST /his/patientCharge/mirror/processLowValue', '0', '1'
FROM DUAL WHERE @patient_charge_menu IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'F' AND parent_id = @patient_charge_menu AND perms = 'department:patientCharge:processMirrorLow') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 3605))
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), parent_id = VALUES(parent_id), order_num = VALUES(order_num), perms = VALUES(perms), remark = VALUES(remark), update_time = VALUES(update_time);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3606, '计费镜像高值扫码', @patient_charge_menu, 5, '#', '', NULL, 1, 0, 'F', '0', '0', 'department:patientCharge:processMirrorHigh', '#', 'admin', NOW(), '1', NOW(), 'POST scanHighBarcode / applyHighConsume', '0', '1'
FROM DUAL WHERE @patient_charge_menu IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'F' AND parent_id = @patient_charge_menu AND perms = 'department:patientCharge:processMirrorHigh') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 3606))
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), parent_id = VALUES(parent_id), order_num = VALUES(order_num), perms = VALUES(perms), remark = VALUES(remark), update_time = VALUES(update_time);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3607, '计费抓取批次查询', @patient_charge_menu, 6, '#', '', NULL, 1, 0, 'F', '0', '0', 'department:patientCharge:fetchBatchList', '#', 'admin', NOW(), '1', NOW(), 'GET /his/patientCharge/fetchBatch/list', '0', '1'
FROM DUAL WHERE @patient_charge_menu IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'F' AND parent_id = @patient_charge_menu AND perms = 'department:patientCharge:fetchBatchList') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 3607))
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), parent_id = VALUES(parent_id), order_num = VALUES(order_num), perms = VALUES(perms), remark = VALUES(remark), update_time = VALUES(update_time);
/

-- 23.4 收货确认 department/receiptConfirm（ReceiptConfirmController）
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3428, '收货确认', COALESCE(@department_root, 1), (SELECT IFNULL(MAX(order_num), 0) + 1 FROM sys_menu WHERE parent_id = COALESCE(@department_root, 1)), 'receiptConfirm', 'department/receiptConfirm/index', NULL, 1, 0, 'C', '0', '0', 'department:receiptConfirm:list', 'checkbox', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'C' AND component = 'department/receiptConfirm/index') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 3428)
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), parent_id = VALUES(parent_id), order_num = VALUES(order_num), path = VALUES(path), component = VALUES(component), perms = VALUES(perms), update_time = VALUES(update_time);
/
SET @receipt_confirm_menu := (SELECT menu_id FROM sys_menu WHERE menu_type = 'C' AND component = 'department/receiptConfirm/index' ORDER BY menu_id DESC LIMIT 1);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3429, '收货确认操作', @receipt_confirm_menu, 1, '#', '', NULL, 1, 0, 'F', '0', '0', 'department:receiptConfirm:confirm', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE @receipt_confirm_menu IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'F' AND parent_id = @receipt_confirm_menu AND perms = 'department:receiptConfirm:confirm') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 3429))
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), parent_id = VALUES(parent_id), order_num = VALUES(order_num), perms = VALUES(perms), update_time = VALUES(update_time);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3430, '收货确认导出', @receipt_confirm_menu, 2, '#', '', NULL, 1, 0, 'F', '0', '0', 'department:receiptConfirm:export', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE @receipt_confirm_menu IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'F' AND parent_id = @receipt_confirm_menu AND perms = 'department:receiptConfirm:export') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 3430))
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), parent_id = VALUES(parent_id), order_num = VALUES(order_num), perms = VALUES(perms), update_time = VALUES(update_time);
/

-- 23.5 基础资料：68 分类 / 装备字典 / 物资分类 / 仓库维护
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3431, '68分类维护', COALESCE(@foundation_root, 1), (SELECT IFNULL(MAX(order_num), 0) + 1 FROM sys_menu WHERE parent_id = COALESCE(@foundation_root, 1)), 'category68', 'foundation/category68/index', NULL, 1, 0, 'C', '0', '0', 'foundation:category68:list', 'tree', 'admin', NOW(), '1', NOW(), 'FdCategory68Controller', '0', '1'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'C' AND component = 'foundation/category68/index') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 3431)
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), parent_id = VALUES(parent_id), order_num = VALUES(order_num), path = VALUES(path), component = VALUES(component), perms = VALUES(perms), update_time = VALUES(update_time);
/
SET @cat68_menu := (SELECT menu_id FROM sys_menu WHERE menu_type = 'C' AND component = 'foundation/category68/index' ORDER BY menu_id DESC LIMIT 1);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3432, '68分类新增', @cat68_menu, 1, '#', '', NULL, 1, 0, 'F', '0', '0', 'foundation:category68:add', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE @cat68_menu IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'F' AND parent_id = @cat68_menu AND perms = 'foundation:category68:add') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 3432))
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), parent_id = VALUES(parent_id), order_num = VALUES(order_num), perms = VALUES(perms), update_time = VALUES(update_time);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3433, '68分类修改', @cat68_menu, 2, '#', '', NULL, 1, 0, 'F', '0', '0', 'foundation:category68:edit', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE @cat68_menu IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'F' AND parent_id = @cat68_menu AND perms = 'foundation:category68:edit') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 3433))
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), parent_id = VALUES(parent_id), order_num = VALUES(order_num), perms = VALUES(perms), update_time = VALUES(update_time);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3434, '68分类导出', @cat68_menu, 3, '#', '', NULL, 1, 0, 'F', '0', '0', 'foundation:category68:export', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE @cat68_menu IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'F' AND parent_id = @cat68_menu AND perms = 'foundation:category68:export') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 3434))
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), parent_id = VALUES(parent_id), order_num = VALUES(order_num), perms = VALUES(perms), update_time = VALUES(update_time);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3435, '68分类删除', @cat68_menu, 4, '#', '', NULL, 1, 0, 'F', '0', '0', 'foundation:category68:remove', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE @cat68_menu IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'F' AND parent_id = @cat68_menu AND perms = 'foundation:category68:remove') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 3435))
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), parent_id = VALUES(parent_id), order_num = VALUES(order_num), perms = VALUES(perms), update_time = VALUES(update_time);
/

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3436, '装备字典', COALESCE(@foundation_root, 1), (SELECT IFNULL(MAX(order_num), 0) + 1 FROM sys_menu WHERE parent_id = COALESCE(@foundation_root, 1)), 'equipmentDict', 'foundation/equipmentDict/index', NULL, 1, 0, 'C', '0', '0', 'foundation:equipmentDict:list', 'education', 'admin', NOW(), '1', NOW(), 'FdEquipmentDictController', '0', '1'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'C' AND component = 'foundation/equipmentDict/index') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 3436)
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), parent_id = VALUES(parent_id), order_num = VALUES(order_num), path = VALUES(path), component = VALUES(component), perms = VALUES(perms), update_time = VALUES(update_time);
/
SET @eq_dict_menu := (SELECT menu_id FROM sys_menu WHERE menu_type = 'C' AND component = 'foundation/equipmentDict/index' ORDER BY menu_id DESC LIMIT 1);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3437, '装备字典新增', @eq_dict_menu, 1, '#', '', NULL, 1, 0, 'F', '0', '0', 'foundation:equipmentDict:add', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE @eq_dict_menu IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'F' AND parent_id = @eq_dict_menu AND perms = 'foundation:equipmentDict:add') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 3437))
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), parent_id = VALUES(parent_id), order_num = VALUES(order_num), perms = VALUES(perms), update_time = VALUES(update_time);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3438, '装备字典修改', @eq_dict_menu, 2, '#', '', NULL, 1, 0, 'F', '0', '0', 'foundation:equipmentDict:edit', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE @eq_dict_menu IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'F' AND parent_id = @eq_dict_menu AND perms = 'foundation:equipmentDict:edit') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 3438))
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), parent_id = VALUES(parent_id), order_num = VALUES(order_num), perms = VALUES(perms), update_time = VALUES(update_time);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3439, '装备字典导出', @eq_dict_menu, 3, '#', '', NULL, 1, 0, 'F', '0', '0', 'foundation:equipmentDict:export', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE @eq_dict_menu IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'F' AND parent_id = @eq_dict_menu AND perms = 'foundation:equipmentDict:export') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 3439))
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), parent_id = VALUES(parent_id), order_num = VALUES(order_num), perms = VALUES(perms), update_time = VALUES(update_time);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3440, '装备字典导入', @eq_dict_menu, 4, '#', '', NULL, 1, 0, 'F', '0', '0', 'foundation:equipmentDict:import', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE @eq_dict_menu IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'F' AND parent_id = @eq_dict_menu AND perms = 'foundation:equipmentDict:import') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 3440))
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), parent_id = VALUES(parent_id), order_num = VALUES(order_num), perms = VALUES(perms), update_time = VALUES(update_time);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3441, '装备字典删除', @eq_dict_menu, 5, '#', '', NULL, 1, 0, 'F', '0', '0', 'foundation:equipmentDict:remove', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE @eq_dict_menu IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'F' AND parent_id = @eq_dict_menu AND perms = 'foundation:equipmentDict:remove') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 3441))
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), parent_id = VALUES(parent_id), order_num = VALUES(order_num), perms = VALUES(perms), update_time = VALUES(update_time);
/

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3442, '物资分类', COALESCE(@foundation_root, 1), (SELECT IFNULL(MAX(order_num), 0) + 1 FROM sys_menu WHERE parent_id = COALESCE(@foundation_root, 1)), 'materialCategory', 'foundation/materialCategory/index', NULL, 1, 0, 'C', '0', '0', 'foundation:materialCategory:list', 'tree', 'admin', NOW(), '1', NOW(), 'FdMaterialCategoryController', '0', '1'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'C' AND component = 'foundation/materialCategory/index') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 3442)
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), parent_id = VALUES(parent_id), order_num = VALUES(order_num), path = VALUES(path), component = VALUES(component), perms = VALUES(perms), update_time = VALUES(update_time);
/
SET @mat_cat_menu := (SELECT menu_id FROM sys_menu WHERE menu_type = 'C' AND component = 'foundation/materialCategory/index' ORDER BY menu_id DESC LIMIT 1);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3443, '物资分类新增', @mat_cat_menu, 1, '#', '', NULL, 1, 0, 'F', '0', '0', 'foundation:materialCategory:add', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE @mat_cat_menu IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'F' AND parent_id = @mat_cat_menu AND perms = 'foundation:materialCategory:add') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 3443))
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), parent_id = VALUES(parent_id), order_num = VALUES(order_num), perms = VALUES(perms), update_time = VALUES(update_time);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3444, '物资分类修改', @mat_cat_menu, 2, '#', '', NULL, 1, 0, 'F', '0', '0', 'foundation:materialCategory:edit', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE @mat_cat_menu IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'F' AND parent_id = @mat_cat_menu AND perms = 'foundation:materialCategory:edit') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 3444))
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), parent_id = VALUES(parent_id), order_num = VALUES(order_num), perms = VALUES(perms), update_time = VALUES(update_time);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3445, '物资分类导出', @mat_cat_menu, 3, '#', '', NULL, 1, 0, 'F', '0', '0', 'foundation:materialCategory:export', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE @mat_cat_menu IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'F' AND parent_id = @mat_cat_menu AND perms = 'foundation:materialCategory:export') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 3445))
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), parent_id = VALUES(parent_id), order_num = VALUES(order_num), perms = VALUES(perms), update_time = VALUES(update_time);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3446, '物资分类删除', @mat_cat_menu, 4, '#', '', NULL, 1, 0, 'F', '0', '0', 'foundation:materialCategory:remove', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE @mat_cat_menu IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'F' AND parent_id = @mat_cat_menu AND perms = 'foundation:materialCategory:remove') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 3446))
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), parent_id = VALUES(parent_id), order_num = VALUES(order_num), perms = VALUES(perms), update_time = VALUES(update_time);
/

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3447, '仓库维护', COALESCE(@foundation_root, 1), (SELECT IFNULL(MAX(order_num), 0) + 1 FROM sys_menu WHERE parent_id = COALESCE(@foundation_root, 1)), 'warehouse', 'foundation/warehouse/index', NULL, 1, 0, 'C', '0', '0', 'foundation:warehouse:list', 'warehouse', 'admin', NOW(), '1', NOW(), 'FdWarehouseController', '0', '1'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'C' AND component = 'foundation/warehouse/index') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 3447)
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), parent_id = VALUES(parent_id), order_num = VALUES(order_num), path = VALUES(path), component = VALUES(component), perms = VALUES(perms), update_time = VALUES(update_time);
/
SET @fd_wh_menu := (SELECT menu_id FROM sys_menu WHERE menu_type = 'C' AND component = 'foundation/warehouse/index' ORDER BY menu_id DESC LIMIT 1);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3448, '仓库新增', @fd_wh_menu, 1, '#', '', NULL, 1, 0, 'F', '0', '0', 'foundation:warehouse:add', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE @fd_wh_menu IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'F' AND parent_id = @fd_wh_menu AND perms = 'foundation:warehouse:add') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 3448))
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), parent_id = VALUES(parent_id), order_num = VALUES(order_num), perms = VALUES(perms), update_time = VALUES(update_time);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3449, '仓库修改', @fd_wh_menu, 2, '#', '', NULL, 1, 0, 'F', '0', '0', 'foundation:warehouse:edit', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE @fd_wh_menu IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'F' AND parent_id = @fd_wh_menu AND perms = 'foundation:warehouse:edit') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 3449))
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), parent_id = VALUES(parent_id), order_num = VALUES(order_num), perms = VALUES(perms), update_time = VALUES(update_time);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3450, '仓库导出', @fd_wh_menu, 3, '#', '', NULL, 1, 0, 'F', '0', '0', 'foundation:warehouse:export', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE @fd_wh_menu IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'F' AND parent_id = @fd_wh_menu AND perms = 'foundation:warehouse:export') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 3450))
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), parent_id = VALUES(parent_id), order_num = VALUES(order_num), perms = VALUES(perms), update_time = VALUES(update_time);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3451, '仓库删除', @fd_wh_menu, 4, '#', '', NULL, 1, 0, 'F', '0', '0', 'foundation:warehouse:remove', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE @fd_wh_menu IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'F' AND parent_id = @fd_wh_menu AND perms = 'foundation:warehouse:remove') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 3451))
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), parent_id = VALUES(parent_id), order_num = VALUES(order_num), perms = VALUES(perms), update_time = VALUES(update_time);
/

-- 23.6 高值管理：入库申请 gzOrder/apply、备货出库/单审核 gzOrder/audit、退货申请 gzOrder/goodsApply（GzOrderController / GzShipment / GzRefundGoodsController）
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3452, '入库申请', COALESCE(@gz_root, 1), (SELECT IFNULL(MAX(order_num), 0) + 1 FROM sys_menu WHERE parent_id = COALESCE(@gz_root, 1)), 'apply', 'gzOrder/apply/index', NULL, 1, 0, 'C', '0', '0', 'gzOrder:apply:list', 'list', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'C' AND component = 'gzOrder/apply/index') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 3452)
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), parent_id = VALUES(parent_id), order_num = VALUES(order_num), path = VALUES(path), component = VALUES(component), perms = VALUES(perms), update_time = VALUES(update_time);
/
SET @gz_order_apply_menu := (SELECT menu_id FROM sys_menu WHERE menu_type = 'C' AND component = 'gzOrder/apply/index' ORDER BY menu_id DESC LIMIT 1);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3453, '高值入库新增', @gz_order_apply_menu, 1, '#', '', NULL, 1, 0, 'F', '0', '0', 'gzOrder:apply:add', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE @gz_order_apply_menu IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'F' AND parent_id = @gz_order_apply_menu AND perms = 'gzOrder:apply:add') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 3453))
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), parent_id = VALUES(parent_id), order_num = VALUES(order_num), perms = VALUES(perms), update_time = VALUES(update_time);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3454, '高值入库修改', @gz_order_apply_menu, 2, '#', '', NULL, 1, 0, 'F', '0', '0', 'gzOrder:apply:edit', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE @gz_order_apply_menu IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'F' AND parent_id = @gz_order_apply_menu AND perms = 'gzOrder:apply:edit') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 3454))
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), parent_id = VALUES(parent_id), order_num = VALUES(order_num), perms = VALUES(perms), update_time = VALUES(update_time);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3455, '高值入库导出', @gz_order_apply_menu, 3, '#', '', NULL, 1, 0, 'F', '0', '0', 'gzOrder:apply:export', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE @gz_order_apply_menu IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'F' AND parent_id = @gz_order_apply_menu AND perms = 'gzOrder:apply:export') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 3455))
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), parent_id = VALUES(parent_id), order_num = VALUES(order_num), perms = VALUES(perms), update_time = VALUES(update_time);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3456, '高值入库删除', @gz_order_apply_menu, 4, '#', '', NULL, 1, 0, 'F', '0', '0', 'gzOrder:apply:remove', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE @gz_order_apply_menu IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'F' AND parent_id = @gz_order_apply_menu AND perms = 'gzOrder:apply:remove') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 3456))
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), parent_id = VALUES(parent_id), order_num = VALUES(order_num), perms = VALUES(perms), update_time = VALUES(update_time);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3457, '高值入库审核', @gz_order_apply_menu, 5, '#', '', NULL, 1, 0, 'F', '0', '0', 'gzOrder:apply:audit', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE @gz_order_apply_menu IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'F' AND parent_id = @gz_order_apply_menu AND perms = 'gzOrder:apply:audit') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 3457))
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), parent_id = VALUES(parent_id), order_num = VALUES(order_num), perms = VALUES(perms), update_time = VALUES(update_time);
/

-- 23.6.0 入库申请（备货验收）：详情/院内码备货库存接口需 gzOrder:apply:query；明细选行引用备货库存列表需 gz:depotInventory:list
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3820, '高值入库查询', @gz_order_apply_menu, 6, '#', '', NULL, 1, 0, 'F', '0', '0', 'gzOrder:apply:query', '#', 'admin', NOW(), '1', NOW(), 'GzOrder getInfo/depotInventory/byInHospitalCode', '0', '1'
FROM DUAL WHERE @gz_order_apply_menu IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'F' AND parent_id = @gz_order_apply_menu AND perms = 'gzOrder:apply:query') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 3820))
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), parent_id = VALUES(parent_id), order_num = VALUES(order_num), perms = VALUES(perms), update_time = VALUES(update_time);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3830, '备货库存列表(入库引用)', @gz_order_apply_menu, 7, '#', '', NULL, 1, 0, 'F', '0', '0', 'gz:depotInventory:list', '#', 'admin', NOW(), '1', NOW(), '入库申请页 listDepotInventory', '0', '1'
FROM DUAL WHERE @gz_order_apply_menu IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'F' AND parent_id = @gz_order_apply_menu AND perms = 'gz:depotInventory:list') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 3830))
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), parent_id = VALUES(parent_id), order_num = VALUES(order_num), perms = VALUES(perms), update_time = VALUES(update_time);
/

-- 23.6.1 备货出库/高值单审核页 gzOrder/audit：与 FE v-hasPermi、GzOrderController/GzShipmentController/GzRefDocController/GzDepotInventoryController 一致（含引用验收单、备货库存）
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3831, '备货出库', COALESCE(@gz_root, 1), (SELECT IFNULL(MAX(order_num), 0) + 1 FROM sys_menu WHERE parent_id = COALESCE(@gz_root, 1)), 'gzOutboundAudit', 'gzOrder/audit/index', NULL, 1, 0, 'C', '0', '0', 'gzOrder:apply:list', 'list', 'admin', NOW(), '1', NOW(), '同组件可配置入库审核路由；权限与出库共用 gzOrder:apply:*', '0', '1'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'C' AND component = 'gzOrder/audit/index') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 3831)
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), parent_id = VALUES(parent_id), order_num = VALUES(order_num), path = VALUES(path), component = VALUES(component), perms = VALUES(perms), update_time = VALUES(update_time);
/
SET @gz_order_audit_menu := (SELECT menu_id FROM sys_menu WHERE menu_type = 'C' AND component = 'gzOrder/audit/index' ORDER BY menu_id DESC LIMIT 1);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3832, '备货出库查询', @gz_order_audit_menu, 1, '#', '', NULL, 1, 0, 'F', '0', '0', 'gzOrder:apply:query', '#', 'admin', NOW(), '1', NOW(), '详情/院内码备货库存', '0', '1'
FROM DUAL WHERE @gz_order_audit_menu IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'F' AND parent_id = @gz_order_audit_menu AND perms = 'gzOrder:apply:query') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 3832))
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), parent_id = VALUES(parent_id), order_num = VALUES(order_num), perms = VALUES(perms), update_time = VALUES(update_time);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3833, '备货出库新增', @gz_order_audit_menu, 2, '#', '', NULL, 1, 0, 'F', '0', '0', 'gzOrder:apply:add', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE @gz_order_audit_menu IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'F' AND parent_id = @gz_order_audit_menu AND perms = 'gzOrder:apply:add') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 3833))
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), parent_id = VALUES(parent_id), order_num = VALUES(order_num), perms = VALUES(perms), update_time = VALUES(update_time);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3834, '备货出库修改', @gz_order_audit_menu, 3, '#', '', NULL, 1, 0, 'F', '0', '0', 'gzOrder:apply:edit', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE @gz_order_audit_menu IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'F' AND parent_id = @gz_order_audit_menu AND perms = 'gzOrder:apply:edit') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 3834))
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), parent_id = VALUES(parent_id), order_num = VALUES(order_num), perms = VALUES(perms), update_time = VALUES(update_time);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3835, '备货出库删除', @gz_order_audit_menu, 4, '#', '', NULL, 1, 0, 'F', '0', '0', 'gzOrder:apply:remove', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE @gz_order_audit_menu IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'F' AND parent_id = @gz_order_audit_menu AND perms = 'gzOrder:apply:remove') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 3835))
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), parent_id = VALUES(parent_id), order_num = VALUES(order_num), perms = VALUES(perms), update_time = VALUES(update_time);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3836, '备货出库导出', @gz_order_audit_menu, 5, '#', '', NULL, 1, 0, 'F', '0', '0', 'gzOrder:apply:export', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE @gz_order_audit_menu IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'F' AND parent_id = @gz_order_audit_menu AND perms = 'gzOrder:apply:export') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 3836))
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), parent_id = VALUES(parent_id), order_num = VALUES(order_num), perms = VALUES(perms), update_time = VALUES(update_time);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3837, '备货出库审核', @gz_order_audit_menu, 6, '#', '', NULL, 1, 0, 'F', '0', '0', 'gzOrder:apply:audit', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE @gz_order_audit_menu IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'F' AND parent_id = @gz_order_audit_menu AND perms = 'gzOrder:apply:audit') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 3837))
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), parent_id = VALUES(parent_id), order_num = VALUES(order_num), perms = VALUES(perms), update_time = VALUES(update_time);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3838, '引用单据查询(备货)', @gz_order_audit_menu, 7, '#', '', NULL, 1, 0, 'F', '0', '0', 'gz:refDoc:query', '#', 'admin', NOW(), '1', NOW(), 'GzRefDocController 引用验收/出库', '0', '1'
FROM DUAL WHERE @gz_order_audit_menu IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'F' AND parent_id = @gz_order_audit_menu AND perms = 'gz:refDoc:query') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 3838))
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), parent_id = VALUES(parent_id), order_num = VALUES(order_num), perms = VALUES(perms), update_time = VALUES(update_time);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3839, '备货库存列表(出库引用)', @gz_order_audit_menu, 8, '#', '', NULL, 1, 0, 'F', '0', '0', 'gz:depotInventory:list', '#', 'admin', NOW(), '1', NOW(), '引用验收单加载备货库存行', '0', '1'
FROM DUAL WHERE @gz_order_audit_menu IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'F' AND parent_id = @gz_order_audit_menu AND perms = 'gz:depotInventory:list') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 3839))
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), parent_id = VALUES(parent_id), order_num = VALUES(order_num), perms = VALUES(perms), update_time = VALUES(update_time);
/

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3458, '退货申请', COALESCE(@gz_root, 1), (SELECT IFNULL(MAX(order_num), 0) + 1 FROM sys_menu WHERE parent_id = COALESCE(@gz_root, 1)), 'goodsApply', 'gzOrder/goodsApply/index', NULL, 1, 0, 'C', '0', '0', 'gzOrder:goodsApply:list', 'list', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'C' AND component = 'gzOrder/goodsApply/index') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 3458)
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), parent_id = VALUES(parent_id), order_num = VALUES(order_num), path = VALUES(path), component = VALUES(component), perms = VALUES(perms), update_time = VALUES(update_time);
/
SET @gz_goods_apply_menu := (SELECT menu_id FROM sys_menu WHERE menu_type = 'C' AND component = 'gzOrder/goodsApply/index' ORDER BY menu_id DESC LIMIT 1);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3459, '退货申请新增', @gz_goods_apply_menu, 1, '#', '', NULL, 1, 0, 'F', '0', '0', 'gzOrder:goodsApply:add', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE @gz_goods_apply_menu IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'F' AND parent_id = @gz_goods_apply_menu AND perms = 'gzOrder:goodsApply:add') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 3459))
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), parent_id = VALUES(parent_id), order_num = VALUES(order_num), perms = VALUES(perms), update_time = VALUES(update_time);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3460, '退货申请修改', @gz_goods_apply_menu, 2, '#', '', NULL, 1, 0, 'F', '0', '0', 'gzOrder:goodsApply:edit', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE @gz_goods_apply_menu IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'F' AND parent_id = @gz_goods_apply_menu AND perms = 'gzOrder:goodsApply:edit') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 3460))
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), parent_id = VALUES(parent_id), order_num = VALUES(order_num), perms = VALUES(perms), update_time = VALUES(update_time);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3461, '退货申请导出', @gz_goods_apply_menu, 3, '#', '', NULL, 1, 0, 'F', '0', '0', 'gzOrder:goodsApply:export', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE @gz_goods_apply_menu IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'F' AND parent_id = @gz_goods_apply_menu AND perms = 'gzOrder:goodsApply:export') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 3461))
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), parent_id = VALUES(parent_id), order_num = VALUES(order_num), perms = VALUES(perms), update_time = VALUES(update_time);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3462, '退货申请删除', @gz_goods_apply_menu, 4, '#', '', NULL, 1, 0, 'F', '0', '0', 'gzOrder:goodsApply:remove', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE @gz_goods_apply_menu IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'F' AND parent_id = @gz_goods_apply_menu AND perms = 'gzOrder:goodsApply:remove') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 3462))
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), parent_id = VALUES(parent_id), order_num = VALUES(order_num), perms = VALUES(perms), update_time = VALUES(update_time);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3463, '退货申请审核', @gz_goods_apply_menu, 5, '#', '', NULL, 1, 0, 'F', '0', '0', 'gzOrder:goodsApply:audit', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE @gz_goods_apply_menu IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'F' AND parent_id = @gz_goods_apply_menu AND perms = 'gzOrder:goodsApply:audit') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 3463))
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), parent_id = VALUES(parent_id), order_num = VALUES(order_num), perms = VALUES(perms), update_time = VALUES(update_time);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3840, '退货申请查询', @gz_goods_apply_menu, 6, '#', '', NULL, 1, 0, 'F', '0', '0', 'gzOrder:goodsApply:query', '#', 'admin', NOW(), '1', NOW(), 'GzRefundGoodsController getInfo', '0', '1'
FROM DUAL WHERE @gz_goods_apply_menu IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'F' AND parent_id = @gz_goods_apply_menu AND perms = 'gzOrder:goodsApply:query') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 3840))
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), parent_id = VALUES(parent_id), order_num = VALUES(order_num), perms = VALUES(perms), update_time = VALUES(update_time);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3860, '引用备货验收单(退货)', @gz_goods_apply_menu, 7, '#', '', NULL, 1, 0, 'F', '0', '0', 'gz:refDoc:query', '#', 'admin', NOW(), '1', NOW(), '备货退货页引用验收单 GzRefDocController', '0', '1'
FROM DUAL WHERE @gz_goods_apply_menu IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'F' AND parent_id = @gz_goods_apply_menu AND perms = 'gz:refDoc:query') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 3860))
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), parent_id = VALUES(parent_id), order_num = VALUES(order_num), perms = VALUES(perms), update_time = VALUES(update_time);
/

-- 23.6.2 备货退库（gzOrder/refund/index）：与退货申请共用 GzRefundGoodsController，权限前缀均为 gzOrder:goodsApply:*
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3841, '备货退库', COALESCE(@gz_root, 1), (SELECT IFNULL(MAX(order_num), 0) + 1 FROM sys_menu WHERE parent_id = COALESCE(@gz_root, 1)), 'refund', 'gzOrder/refund/index', NULL, 1, 0, 'C', '0', '0', 'gzOrder:goodsApply:list', 'international', 'admin', NOW(), '1', NOW(), '高值备货退库（GZTK-）', '0', '1'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'C' AND component = 'gzOrder/refund/index') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 3841)
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), parent_id = VALUES(parent_id), order_num = VALUES(order_num), path = VALUES(path), component = VALUES(component), perms = VALUES(perms), update_time = VALUES(update_time);
/
SET @gz_refund_menu := (SELECT menu_id FROM sys_menu WHERE menu_type = 'C' AND component = 'gzOrder/refund/index' ORDER BY menu_id DESC LIMIT 1);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3842, '备货退库查询', @gz_refund_menu, 1, '#', '', NULL, 1, 0, 'F', '0', '0', 'gzOrder:goodsApply:query', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE @gz_refund_menu IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'F' AND parent_id = @gz_refund_menu AND perms = 'gzOrder:goodsApply:query') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 3842))
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), parent_id = VALUES(parent_id), order_num = VALUES(order_num), perms = VALUES(perms), update_time = VALUES(update_time);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3843, '备货退库新增', @gz_refund_menu, 2, '#', '', NULL, 1, 0, 'F', '0', '0', 'gzOrder:goodsApply:add', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE @gz_refund_menu IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'F' AND parent_id = @gz_refund_menu AND perms = 'gzOrder:goodsApply:add') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 3843))
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), parent_id = VALUES(parent_id), order_num = VALUES(order_num), perms = VALUES(perms), update_time = VALUES(update_time);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3844, '备货退库修改', @gz_refund_menu, 3, '#', '', NULL, 1, 0, 'F', '0', '0', 'gzOrder:goodsApply:edit', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE @gz_refund_menu IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'F' AND parent_id = @gz_refund_menu AND perms = 'gzOrder:goodsApply:edit') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 3844))
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), parent_id = VALUES(parent_id), order_num = VALUES(order_num), perms = VALUES(perms), update_time = VALUES(update_time);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3845, '备货退库删除', @gz_refund_menu, 4, '#', '', NULL, 1, 0, 'F', '0', '0', 'gzOrder:goodsApply:remove', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE @gz_refund_menu IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'F' AND parent_id = @gz_refund_menu AND perms = 'gzOrder:goodsApply:remove') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 3845))
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), parent_id = VALUES(parent_id), order_num = VALUES(order_num), perms = VALUES(perms), update_time = VALUES(update_time);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3846, '备货退库导出', @gz_refund_menu, 5, '#', '', NULL, 1, 0, 'F', '0', '0', 'gzOrder:goodsApply:export', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE @gz_refund_menu IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'F' AND parent_id = @gz_refund_menu AND perms = 'gzOrder:goodsApply:export') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 3846))
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), parent_id = VALUES(parent_id), order_num = VALUES(order_num), perms = VALUES(perms), update_time = VALUES(update_time);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3847, '备货退库审核', @gz_refund_menu, 6, '#', '', NULL, 1, 0, 'F', '0', '0', 'gzOrder:goodsApply:audit', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE @gz_refund_menu IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'F' AND parent_id = @gz_refund_menu AND perms = 'gzOrder:goodsApply:audit') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 3847))
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), parent_id = VALUES(parent_id), order_num = VALUES(order_num), perms = VALUES(perms), update_time = VALUES(update_time);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3861, '引用备货出库单(退库)', @gz_refund_menu, 7, '#', '', NULL, 1, 0, 'F', '0', '0', 'gz:refDoc:query', '#', 'admin', NOW(), '1', NOW(), '备货退库页引用出库单 GzRefDocController', '0', '1'
FROM DUAL WHERE @gz_refund_menu IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'F' AND parent_id = @gz_refund_menu AND perms = 'gz:refDoc:query') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 3861))
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), parent_id = VALUES(parent_id), order_num = VALUES(order_num), perms = VALUES(perms), update_time = VALUES(update_time);
/

-- 23.8 高值管理：跟台入库按钮权限（补齐 FE v-hasPermi 缺失菜单；退库审核见 23.6.4）
SET @gz_follow_menu := (SELECT menu_id FROM sys_menu WHERE menu_type = 'C' AND component = 'gzOrder/follow/index' ORDER BY menu_id DESC LIMIT 1);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3801, '跟台入库新增', @gz_follow_menu, 1, '#', '', NULL, 1, 0, 'F', '0', '0', 'gzOrder:follow:add', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE @gz_follow_menu IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'F' AND parent_id = @gz_follow_menu AND perms = 'gzOrder:follow:add') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 3801))
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), parent_id = VALUES(parent_id), order_num = VALUES(order_num), perms = VALUES(perms), update_time = VALUES(update_time);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3802, '跟台入库修改', @gz_follow_menu, 2, '#', '', NULL, 1, 0, 'F', '0', '0', 'gzOrder:follow:edit', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE @gz_follow_menu IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'F' AND parent_id = @gz_follow_menu AND perms = 'gzOrder:follow:edit') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 3802))
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), parent_id = VALUES(parent_id), order_num = VALUES(order_num), perms = VALUES(perms), update_time = VALUES(update_time);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3803, '跟台入库导出', @gz_follow_menu, 3, '#', '', NULL, 1, 0, 'F', '0', '0', 'gzOrder:follow:export', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE @gz_follow_menu IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'F' AND parent_id = @gz_follow_menu AND perms = 'gzOrder:follow:export') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 3803))
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), parent_id = VALUES(parent_id), order_num = VALUES(order_num), perms = VALUES(perms), update_time = VALUES(update_time);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3804, '跟台入库删除', @gz_follow_menu, 4, '#', '', NULL, 1, 0, 'F', '0', '0', 'gzOrder:follow:remove', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE @gz_follow_menu IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'F' AND parent_id = @gz_follow_menu AND perms = 'gzOrder:follow:remove') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 3804))
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), parent_id = VALUES(parent_id), order_num = VALUES(order_num), perms = VALUES(perms), update_time = VALUES(update_time);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3805, '跟台入库审核', @gz_follow_menu, 5, '#', '', NULL, 1, 0, 'F', '0', '0', 'gzOrder:follow:audit', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE @gz_follow_menu IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'F' AND parent_id = @gz_follow_menu AND perms = 'gzOrder:follow:audit') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 3805))
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), parent_id = VALUES(parent_id), order_num = VALUES(order_num), perms = VALUES(perms), update_time = VALUES(update_time);
/

-- 23.6.4 退库审核（gzOrder/goodsAudit/index）：前端调 GzOrderController，权限与备货出库审核一致 gzOrder:apply:*
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3858, '退库审核', COALESCE(@gz_root, 1), (SELECT IFNULL(MAX(order_num), 0) + 1 FROM sys_menu WHERE parent_id = COALESCE(@gz_root, 1)), 'goodsAudit', 'gzOrder/goodsAudit/index', NULL, 1, 0, 'C', '0', '0', 'gzOrder:apply:list', 'audit', 'admin', NOW(), '1', NOW(), '高值退库单 orderType=301，接口同 /gz/order', '0', '1'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'C' AND component = 'gzOrder/goodsAudit/index') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 3858)
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), parent_id = VALUES(parent_id), order_num = VALUES(order_num), path = VALUES(path), component = VALUES(component), perms = VALUES(perms), update_time = VALUES(update_time);
/

SET @gz_goods_audit_menu := (SELECT menu_id FROM sys_menu WHERE menu_type = 'C' AND component = 'gzOrder/goodsAudit/index' ORDER BY menu_id DESC LIMIT 1);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3806, '退库审核查询', @gz_goods_audit_menu, 1, '#', '', NULL, 1, 0, 'F', '0', '0', 'gzOrder:apply:query', '#', 'admin', NOW(), '1', NOW(), 'GzOrderController getInfo', '0', '1'
FROM DUAL WHERE @gz_goods_audit_menu IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'F' AND parent_id = @gz_goods_audit_menu AND perms = 'gzOrder:apply:query') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 3806))
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), parent_id = VALUES(parent_id), order_num = VALUES(order_num), perms = VALUES(perms), update_time = VALUES(update_time);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3807, '退库审核新增', @gz_goods_audit_menu, 2, '#', '', NULL, 1, 0, 'F', '0', '0', 'gzOrder:apply:add', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE @gz_goods_audit_menu IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'F' AND parent_id = @gz_goods_audit_menu AND perms = 'gzOrder:apply:add') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 3807))
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), parent_id = VALUES(parent_id), order_num = VALUES(order_num), perms = VALUES(perms), update_time = VALUES(update_time);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3808, '退库审核修改', @gz_goods_audit_menu, 3, '#', '', NULL, 1, 0, 'F', '0', '0', 'gzOrder:apply:edit', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE @gz_goods_audit_menu IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'F' AND parent_id = @gz_goods_audit_menu AND perms = 'gzOrder:apply:edit') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 3808))
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), parent_id = VALUES(parent_id), order_num = VALUES(order_num), perms = VALUES(perms), update_time = VALUES(update_time);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3809, '退库审核删除', @gz_goods_audit_menu, 4, '#', '', NULL, 1, 0, 'F', '0', '0', 'gzOrder:apply:remove', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE @gz_goods_audit_menu IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'F' AND parent_id = @gz_goods_audit_menu AND perms = 'gzOrder:apply:remove') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 3809))
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), parent_id = VALUES(parent_id), order_num = VALUES(order_num), perms = VALUES(perms), update_time = VALUES(update_time);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3810, '退库审核导出', @gz_goods_audit_menu, 5, '#', '', NULL, 1, 0, 'F', '0', '0', 'gzOrder:apply:export', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE @gz_goods_audit_menu IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'F' AND parent_id = @gz_goods_audit_menu AND perms = 'gzOrder:apply:export') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 3810))
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), parent_id = VALUES(parent_id), order_num = VALUES(order_num), perms = VALUES(perms), update_time = VALUES(update_time);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3849, '退库审核审核', @gz_goods_audit_menu, 6, '#', '', NULL, 1, 0, 'F', '0', '0', 'gzOrder:apply:audit', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE @gz_goods_audit_menu IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'F' AND parent_id = @gz_goods_audit_menu AND perms = 'gzOrder:apply:audit') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 3849))
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), parent_id = VALUES(parent_id), order_num = VALUES(order_num), perms = VALUES(perms), update_time = VALUES(update_time);
/

-- 23.9 高值管理：住院高值扫码（gz/zyjf）及追溯接口权限
-- 兼容历史数据：若早期菜单使用了 gz:zyjf:list，则统一修正为接口所需的 gz:traceability:list
UPDATE sys_menu
SET perms = 'gz:traceability:list',
    update_time = NOW()
WHERE menu_type = 'C'
  AND component = 'gz/zyjf/index'
  AND perms = 'gz:zyjf:list';
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3811, '住院高值扫码', COALESCE(@gz_root, 1), (SELECT IFNULL(MAX(order_num), 0) + 1 FROM sys_menu WHERE parent_id = COALESCE(@gz_root, 1)), 'zyjf', 'gz/zyjf/index', NULL, 1, 0, 'C', '0', '0', 'gz:traceability:list', 'education', 'admin', NOW(), '1', NOW(), '住院高值扫码页面', '0', '1'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'C' AND component = 'gz/zyjf/index') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 3811)
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), parent_id = VALUES(parent_id), order_num = VALUES(order_num), path = VALUES(path), component = VALUES(component), perms = VALUES(perms), update_time = VALUES(update_time);
/
SET @gz_zyjf_menu := (SELECT menu_id FROM sys_menu WHERE menu_type = 'C' AND component = 'gz/zyjf/index' ORDER BY menu_id DESC LIMIT 1);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3812, '追溯查询', @gz_zyjf_menu, 1, '#', '', NULL, 1, 0, 'F', '0', '0', 'gz:traceability:query', '#', 'admin', NOW(), '1', NOW(), '追溯单详情/扫码页查询', '0', '1'
FROM DUAL WHERE @gz_zyjf_menu IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'F' AND parent_id = @gz_zyjf_menu AND perms = 'gz:traceability:query') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 3812))
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), parent_id = VALUES(parent_id), order_num = VALUES(order_num), perms = VALUES(perms), update_time = VALUES(update_time);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3813, '追溯新增', @gz_zyjf_menu, 2, '#', '', NULL, 1, 0, 'F', '0', '0', 'gz:traceability:add', '#', 'admin', NOW(), '1', NOW(), '扫码页保存（新增）', '0', '1'
FROM DUAL WHERE @gz_zyjf_menu IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'F' AND parent_id = @gz_zyjf_menu AND perms = 'gz:traceability:add') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 3813))
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), parent_id = VALUES(parent_id), order_num = VALUES(order_num), perms = VALUES(perms), update_time = VALUES(update_time);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3814, '追溯修改', @gz_zyjf_menu, 3, '#', '', NULL, 1, 0, 'F', '0', '0', 'gz:traceability:edit', '#', 'admin', NOW(), '1', NOW(), '扫码页保存（修改）', '0', '1'
FROM DUAL WHERE @gz_zyjf_menu IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'F' AND parent_id = @gz_zyjf_menu AND perms = 'gz:traceability:edit') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 3814))
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), parent_id = VALUES(parent_id), order_num = VALUES(order_num), perms = VALUES(perms), update_time = VALUES(update_time);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3815, '追溯删除', @gz_zyjf_menu, 4, '#', '', NULL, 1, 0, 'F', '0', '0', 'gz:traceability:remove', '#', 'admin', NOW(), '1', NOW(), '扫码页删除', '0', '1'
FROM DUAL WHERE @gz_zyjf_menu IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'F' AND parent_id = @gz_zyjf_menu AND perms = 'gz:traceability:remove') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 3815))
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), parent_id = VALUES(parent_id), order_num = VALUES(order_num), perms = VALUES(perms), update_time = VALUES(update_time);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3816, '追溯审核', @gz_zyjf_menu, 5, '#', '', NULL, 1, 0, 'F', '0', '0', 'gz:traceability:audit', '#', 'admin', NOW(), '1', NOW(), '扫码页审核/反审核', '0', '1'
FROM DUAL WHERE @gz_zyjf_menu IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'F' AND parent_id = @gz_zyjf_menu AND perms = 'gz:traceability:audit') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 3816))
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), parent_id = VALUES(parent_id), order_num = VALUES(order_num), perms = VALUES(perms), update_time = VALUES(update_time);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3817, '追溯导出', @gz_zyjf_menu, 6, '#', '', NULL, 1, 0, 'F', '0', '0', 'gz:traceability:export', '#', 'admin', NOW(), '1', NOW(), '追溯单导出', '0', '1'
FROM DUAL WHERE @gz_zyjf_menu IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'F' AND parent_id = @gz_zyjf_menu AND perms = 'gz:traceability:export') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 3817))
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), parent_id = VALUES(parent_id), order_num = VALUES(order_num), perms = VALUES(perms), update_time = VALUES(update_time);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3818, '打印耗材清单', @gz_zyjf_menu, 7, '#', '', NULL, 1, 0, 'F', '0', '0', 'gz:traceability:printMaterial', '#', 'admin', NOW(), '1', NOW(), '住院高值扫码-打印耗材清单', '0', '1'
FROM DUAL WHERE @gz_zyjf_menu IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'F' AND parent_id = @gz_zyjf_menu AND perms = 'gz:traceability:printMaterial') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 3818))
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), parent_id = VALUES(parent_id), order_num = VALUES(order_num), perms = VALUES(perms), update_time = VALUES(update_time);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3819, '打印条码清单', @gz_zyjf_menu, 8, '#', '', NULL, 1, 0, 'F', '0', '0', 'gz:traceability:printBarcode', '#', 'admin', NOW(), '1', NOW(), '住院高值扫码-打印条码清单', '0', '1'
FROM DUAL WHERE @gz_zyjf_menu IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'F' AND parent_id = @gz_zyjf_menu AND perms = 'gz:traceability:printBarcode') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 3819))
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), parent_id = VALUES(parent_id), order_num = VALUES(order_num), perms = VALUES(perms), update_time = VALUES(update_time);
/

-- 23.7 盈亏单：add/edit/remove/audit（父：warehouse/profitLoss/index；扫描 FE∩BE 相对 menu.sql 曾缺 audit/edit/remove 三项）
SET @profit_loss_menu := (SELECT menu_id FROM sys_menu WHERE menu_type = 'C' AND component = 'warehouse/profitLoss/index' ORDER BY menu_id DESC LIMIT 1);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3464, '盈亏单新增', @profit_loss_menu, (SELECT IFNULL(MAX(order_num), 0) + 1 FROM sys_menu WHERE parent_id = @profit_loss_menu), '#', '', NULL, 1, 0, 'F', '0', '0', 'warehouse:profitLoss:add', '#', 'admin', NOW(), '1', NOW(), 'StkIoProfitLossController 新增/保存草稿', '0', '1'
FROM DUAL WHERE @profit_loss_menu IS NOT NULL
  AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'F' AND parent_id = @profit_loss_menu AND perms = 'warehouse:profitLoss:add')
    OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 3464))
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), parent_id = VALUES(parent_id), order_num = VALUES(order_num), perms = VALUES(perms), update_time = VALUES(update_time);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3465, '盈亏单修改', @profit_loss_menu, (SELECT IFNULL(MAX(order_num), 0) + 1 FROM sys_menu WHERE parent_id = @profit_loss_menu), '#', '', NULL, 1, 0, 'F', '0', '0', 'warehouse:profitLoss:edit', '#', 'admin', NOW(), '1', NOW(), 'StkIoProfitLossController', '0', '1'
FROM DUAL WHERE @profit_loss_menu IS NOT NULL
  AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'F' AND parent_id = @profit_loss_menu AND perms = 'warehouse:profitLoss:edit')
    OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 3465))
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), parent_id = VALUES(parent_id), order_num = VALUES(order_num), perms = VALUES(perms), update_time = VALUES(update_time);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3466, '盈亏单删除', @profit_loss_menu, (SELECT IFNULL(MAX(order_num), 0) + 1 FROM sys_menu WHERE parent_id = @profit_loss_menu), '#', '', NULL, 1, 0, 'F', '0', '0', 'warehouse:profitLoss:remove', '#', 'admin', NOW(), '1', NOW(), 'StkIoProfitLossController', '0', '1'
FROM DUAL WHERE @profit_loss_menu IS NOT NULL
  AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'F' AND parent_id = @profit_loss_menu AND perms = 'warehouse:profitLoss:remove')
    OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 3466))
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), parent_id = VALUES(parent_id), order_num = VALUES(order_num), perms = VALUES(perms), update_time = VALUES(update_time);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3467, '盈亏单审核', @profit_loss_menu, (SELECT IFNULL(MAX(order_num), 0) + 1 FROM sys_menu WHERE parent_id = @profit_loss_menu), '#', '', NULL, 1, 0, 'F', '0', '0', 'warehouse:profitLoss:audit', '#', 'admin', NOW(), '1', NOW(), 'StkIoProfitLossController', '0', '1'
FROM DUAL WHERE @profit_loss_menu IS NOT NULL
  AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'F' AND parent_id = @profit_loss_menu AND perms = 'warehouse:profitLoss:audit')
    OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 3467))
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), parent_id = VALUES(parent_id), order_num = VALUES(order_num), perms = VALUES(perms), update_time = VALUES(update_time);
/

-- ---------- 24) 科室审核 department/dAudit：父菜单下补齐申领/申购/转科与后端接口一致的按钮权限（解决仅挂「科室审核」时申购审核 403 等）----------
SET @d_audit_parent := (
  SELECT menu_id FROM sys_menu
  WHERE menu_type = 'C'
    AND (perms = 'department:dAudit:list' OR component = 'department/dAudit/index')
  ORDER BY menu_id LIMIT 1
);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3468, '申领单列表', @d_audit_parent, (SELECT IFNULL(MAX(order_num), 0) + 1 FROM sys_menu WHERE parent_id = @d_audit_parent), '#', '', NULL, 1, 0, 'F', '0', '0', 'department:dApply:list', '#', 'admin', NOW(), '1', NOW(), '科室审核-列表/查询申领', '0', '1'
FROM DUAL WHERE @d_audit_parent IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'F' AND parent_id = @d_audit_parent AND perms = 'department:dApply:list') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 3468))
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), parent_id = VALUES(parent_id), order_num = VALUES(order_num), perms = VALUES(perms), update_time = VALUES(update_time);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3469, '申领单查询', @d_audit_parent, (SELECT IFNULL(MAX(order_num), 0) + 1 FROM sys_menu WHERE parent_id = @d_audit_parent), '#', '', NULL, 1, 0, 'F', '0', '0', 'department:dApply:query', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE @d_audit_parent IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'F' AND parent_id = @d_audit_parent AND perms = 'department:dApply:query') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 3469))
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), parent_id = VALUES(parent_id), order_num = VALUES(order_num), perms = VALUES(perms), update_time = VALUES(update_time);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3470, '申领单修改', @d_audit_parent, (SELECT IFNULL(MAX(order_num), 0) + 1 FROM sys_menu WHERE parent_id = @d_audit_parent), '#', '', NULL, 1, 0, 'F', '0', '0', 'department:dApply:edit', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE @d_audit_parent IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'F' AND parent_id = @d_audit_parent AND perms = 'department:dApply:edit') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 3470))
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), parent_id = VALUES(parent_id), order_num = VALUES(order_num), perms = VALUES(perms), update_time = VALUES(update_time);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3471, '申领单删除', @d_audit_parent, (SELECT IFNULL(MAX(order_num), 0) + 1 FROM sys_menu WHERE parent_id = @d_audit_parent), '#', '', NULL, 1, 0, 'F', '0', '0', 'department:dApply:remove', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE @d_audit_parent IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'F' AND parent_id = @d_audit_parent AND perms = 'department:dApply:remove') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 3471))
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), parent_id = VALUES(parent_id), order_num = VALUES(order_num), perms = VALUES(perms), update_time = VALUES(update_time);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3472, '申领单导出', @d_audit_parent, (SELECT IFNULL(MAX(order_num), 0) + 1 FROM sys_menu WHERE parent_id = @d_audit_parent), '#', '', NULL, 1, 0, 'F', '0', '0', 'department:dApply:export', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE @d_audit_parent IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'F' AND parent_id = @d_audit_parent AND perms = 'department:dApply:export') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 3472))
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), parent_id = VALUES(parent_id), order_num = VALUES(order_num), perms = VALUES(perms), update_time = VALUES(update_time);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3473, '申领单审核', @d_audit_parent, (SELECT IFNULL(MAX(order_num), 0) + 1 FROM sys_menu WHERE parent_id = @d_audit_parent), '#', '', NULL, 1, 0, 'F', '0', '0', 'department:dApply:audit', '#', 'admin', NOW(), '1', NOW(), '与现网 1163 等重复 perms 时本行不插入', '0', '1'
FROM DUAL WHERE @d_audit_parent IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'F' AND parent_id = @d_audit_parent AND perms = 'department:dApply:audit') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 3473))
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), parent_id = VALUES(parent_id), order_num = VALUES(order_num), perms = VALUES(perms), update_time = VALUES(update_time);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3474, '申购单列表', @d_audit_parent, (SELECT IFNULL(MAX(order_num), 0) + 1 FROM sys_menu WHERE parent_id = @d_audit_parent), '#', '', NULL, 1, 0, 'F', '0', '0', 'department:purchase:list', '#', 'admin', NOW(), '1', NOW(), 'DepPurchaseApplyController', '0', '1'
FROM DUAL WHERE @d_audit_parent IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'F' AND parent_id = @d_audit_parent AND perms = 'department:purchase:list') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 3474))
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), parent_id = VALUES(parent_id), order_num = VALUES(order_num), perms = VALUES(perms), update_time = VALUES(update_time);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3475, '申购单查询', @d_audit_parent, (SELECT IFNULL(MAX(order_num), 0) + 1 FROM sys_menu WHERE parent_id = @d_audit_parent), '#', '', NULL, 1, 0, 'F', '0', '0', 'department:purchase:query', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE @d_audit_parent IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'F' AND parent_id = @d_audit_parent AND perms = 'department:purchase:query') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 3475))
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), parent_id = VALUES(parent_id), order_num = VALUES(order_num), perms = VALUES(perms), update_time = VALUES(update_time);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3476, '申购单修改', @d_audit_parent, (SELECT IFNULL(MAX(order_num), 0) + 1 FROM sys_menu WHERE parent_id = @d_audit_parent), '#', '', NULL, 1, 0, 'F', '0', '0', 'department:purchase:edit', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE @d_audit_parent IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'F' AND parent_id = @d_audit_parent AND perms = 'department:purchase:edit') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 3476))
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), parent_id = VALUES(parent_id), order_num = VALUES(order_num), perms = VALUES(perms), update_time = VALUES(update_time);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3477, '申购单删除', @d_audit_parent, (SELECT IFNULL(MAX(order_num), 0) + 1 FROM sys_menu WHERE parent_id = @d_audit_parent), '#', '', NULL, 1, 0, 'F', '0', '0', 'department:purchase:remove', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE @d_audit_parent IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'F' AND parent_id = @d_audit_parent AND perms = 'department:purchase:remove') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 3477))
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), parent_id = VALUES(parent_id), order_num = VALUES(order_num), perms = VALUES(perms), update_time = VALUES(update_time);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3478, '申购单导出', @d_audit_parent, (SELECT IFNULL(MAX(order_num), 0) + 1 FROM sys_menu WHERE parent_id = @d_audit_parent), '#', '', NULL, 1, 0, 'F', '0', '0', 'department:purchase:export', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE @d_audit_parent IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'F' AND parent_id = @d_audit_parent AND perms = 'department:purchase:export') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 3478))
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), parent_id = VALUES(parent_id), order_num = VALUES(order_num), perms = VALUES(perms), update_time = VALUES(update_time);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3479, '申购单审核', @d_audit_parent, (SELECT IFNULL(MAX(order_num), 0) + 1 FROM sys_menu WHERE parent_id = @d_audit_parent), '#', '', NULL, 1, 0, 'F', '0', '0', 'department:purchase:audit', '#', 'admin', NOW(), '1', NOW(), 'PUT /department/purchase/auditApply', '0', '1'
FROM DUAL WHERE @d_audit_parent IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'F' AND parent_id = @d_audit_parent AND perms = 'department:purchase:audit') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 3479))
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), parent_id = VALUES(parent_id), order_num = VALUES(order_num), perms = VALUES(perms), update_time = VALUES(update_time);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3480, '申购单驳回', @d_audit_parent, (SELECT IFNULL(MAX(order_num), 0) + 1 FROM sys_menu WHERE parent_id = @d_audit_parent), '#', '', NULL, 1, 0, 'F', '0', '0', 'department:purchase:reject', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE @d_audit_parent IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'F' AND parent_id = @d_audit_parent AND perms = 'department:purchase:reject') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 3480))
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), parent_id = VALUES(parent_id), order_num = VALUES(order_num), perms = VALUES(perms), update_time = VALUES(update_time);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3481, '转科申请列表', @d_audit_parent, (SELECT IFNULL(MAX(order_num), 0) + 1 FROM sys_menu WHERE parent_id = @d_audit_parent), '#', '', NULL, 1, 0, 'F', '0', '0', 'departmentTransfer:apply:list', '#', 'admin', NOW(), '1', NOW(), 'DepartmentTransferController', '0', '1'
FROM DUAL WHERE @d_audit_parent IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'F' AND parent_id = @d_audit_parent AND perms = 'departmentTransfer:apply:list') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 3481))
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), parent_id = VALUES(parent_id), order_num = VALUES(order_num), perms = VALUES(perms), update_time = VALUES(update_time);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3482, '转科申请查询', @d_audit_parent, (SELECT IFNULL(MAX(order_num), 0) + 1 FROM sys_menu WHERE parent_id = @d_audit_parent), '#', '', NULL, 1, 0, 'F', '0', '0', 'departmentTransfer:apply:query', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE @d_audit_parent IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'F' AND parent_id = @d_audit_parent AND perms = 'departmentTransfer:apply:query') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 3482))
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), parent_id = VALUES(parent_id), order_num = VALUES(order_num), perms = VALUES(perms), update_time = VALUES(update_time);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3483, '转科申请修改', @d_audit_parent, (SELECT IFNULL(MAX(order_num), 0) + 1 FROM sys_menu WHERE parent_id = @d_audit_parent), '#', '', NULL, 1, 0, 'F', '0', '0', 'departmentTransfer:apply:edit', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE @d_audit_parent IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'F' AND parent_id = @d_audit_parent AND perms = 'departmentTransfer:apply:edit') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 3483))
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), parent_id = VALUES(parent_id), order_num = VALUES(order_num), perms = VALUES(perms), update_time = VALUES(update_time);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3484, '转科申请删除', @d_audit_parent, (SELECT IFNULL(MAX(order_num), 0) + 1 FROM sys_menu WHERE parent_id = @d_audit_parent), '#', '', NULL, 1, 0, 'F', '0', '0', 'departmentTransfer:apply:remove', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE @d_audit_parent IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'F' AND parent_id = @d_audit_parent AND perms = 'departmentTransfer:apply:remove') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 3484))
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), parent_id = VALUES(parent_id), order_num = VALUES(order_num), perms = VALUES(perms), update_time = VALUES(update_time);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3485, '转科申请导出', @d_audit_parent, (SELECT IFNULL(MAX(order_num), 0) + 1 FROM sys_menu WHERE parent_id = @d_audit_parent), '#', '', NULL, 1, 0, 'F', '0', '0', 'departmentTransfer:apply:export', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE @d_audit_parent IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'F' AND parent_id = @d_audit_parent AND perms = 'departmentTransfer:apply:export') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 3485))
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), parent_id = VALUES(parent_id), order_num = VALUES(order_num), perms = VALUES(perms), update_time = VALUES(update_time);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3486, '转科申请审核', @d_audit_parent, (SELECT IFNULL(MAX(order_num), 0) + 1 FROM sys_menu WHERE parent_id = @d_audit_parent), '#', '', NULL, 1, 0, 'F', '0', '0', 'departmentTransfer:apply:audit', '#', 'admin', NOW(), '1', NOW(), 'PUT /department/transfer/auditApply', '0', '1'
FROM DUAL WHERE @d_audit_parent IS NOT NULL AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'F' AND parent_id = @d_audit_parent AND perms = 'departmentTransfer:apply:audit') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 3486))
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), parent_id = VALUES(parent_id), order_num = VALUES(order_num), perms = VALUES(perms), update_time = VALUES(update_time);
/

-- ---------- 数据备份管理（系统管理下；须早于下方 hc_customer_menu 回填）----------
SET @data_backup_menu_id := (
  SELECT menu_id FROM sys_menu WHERE perms = 'system:dataBackup:list' AND menu_type = 'C' ORDER BY menu_id LIMIT 1
);
/
INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  2300,
  '数据备份管理',
  COALESCE(
    (SELECT m.menu_id FROM sys_menu m WHERE m.menu_name = '系统管理' AND m.menu_type = 'M' ORDER BY m.menu_id LIMIT 1),
    1
  ),
  92,
  'dataBackup',
  'system/dataBackup/index',
  NULL,
  1,
  0,
  'C',
  '0',
  '0',
  'system:dataBackup:list',
  'download',
  'admin',
  NOW(),
  '1',
  NOW(),
  '每日数据库备份路径/时间/启停（与定时任务 sys_job 联动）',
  '0',
  '1'
FROM DUAL
WHERE @data_backup_menu_id IS NULL
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

SET @data_backup_menu_id := (
  SELECT menu_id FROM sys_menu WHERE perms = 'system:dataBackup:list' AND menu_type = 'C' ORDER BY menu_id LIMIT 1
);
/
INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
) SELECT
  2301, '备份配置查询', @data_backup_menu_id, 1, '#', '', NULL,
  1, 0, 'F', '0', '0', 'system:dataBackup:query', '#',
  'admin', NOW(), '1', NOW(), '',
  '0', '1'
FROM DUAL
WHERE @data_backup_menu_id IS NOT NULL
  AND (
    NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=@data_backup_menu_id AND perms='system:dataBackup:query')
    OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=2301)
  )
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  perms = VALUES(perms),
  update_by = VALUES(update_by),
  update_time = VALUES(update_time),
  default_open_to_customer = VALUES(default_open_to_customer);
/

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
) SELECT
  2302, '备份配置保存', @data_backup_menu_id, 2, '#', '', NULL,
  1, 0, 'F', '0', '0', 'system:dataBackup:edit', '#',
  'admin', NOW(), '1', NOW(), '',
  '0', '1'
FROM DUAL
WHERE @data_backup_menu_id IS NOT NULL
  AND (
    NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=@data_backup_menu_id AND perms='system:dataBackup:edit')
    OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=2302)
  )
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  perms = VALUES(perms),
  update_by = VALUES(update_by),
  update_time = VALUES(update_time),
  default_open_to_customer = VALUES(default_open_to_customer);
/

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
) SELECT
  2303, '备份启停', @data_backup_menu_id, 3, '#', '', NULL,
  1, 0, 'F', '0', '0', 'system:dataBackup:changeStatus', '#',
  'admin', NOW(), '1', NOW(), '',
  '0', '1'
FROM DUAL
WHERE @data_backup_menu_id IS NOT NULL
  AND (
    NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=@data_backup_menu_id AND perms='system:dataBackup:changeStatus')
    OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=2303)
  )
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  perms = VALUES(perms),
  update_by = VALUES(update_by),
  update_time = VALUES(update_time),
  default_open_to_customer = VALUES(default_open_to_customer);
/

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
) SELECT
  2304, '立即备份', @data_backup_menu_id, 4, '#', '', NULL,
  1, 0, 'F', '0', '0', 'system:dataBackup:run', '#',
  'admin', NOW(), '1', NOW(), '',
  '0', '1'
FROM DUAL
WHERE @data_backup_menu_id IS NOT NULL
  AND (
    NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type='F' AND parent_id=@data_backup_menu_id AND perms='system:dataBackup:run')
    OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id=2304)
  )
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  perms = VALUES(perms),
  update_by = VALUES(update_by),
  update_time = VALUES(update_time),
  default_open_to_customer = VALUES(default_open_to_customer);
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
    'departmentTransfer:apply:audit',
    'gz:traceability:list',
    'gz:traceability:query',
    'gz:traceability:add',
    'gz:traceability:edit',
    'gz:traceability:remove',
    'gz:traceability:audit',
    'gz:traceability:export',
    'gz:traceability:printMaterial',
    'gz:traceability:printBarcode',
    'system:dataBackup:list',
    'system:dataBackup:query',
    'system:dataBackup:edit',
    'system:dataBackup:changeStatus',
    'system:dataBackup:run'
  )
WHERE c.hc_status = '0'
  AND NOT EXISTS (
    SELECT 1 FROM hc_customer_menu h
    WHERE h.tenant_id = c.customer_id AND h.menu_id = m.menu_id
  );
/

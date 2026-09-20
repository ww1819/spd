-- 纠正：订单管理/采购报表 应为「采购管理」下的二级目录（京东分栏），不是侧栏一级
-- 目标分栏：采购管理(计划/审核/订单/…) | 订单管理(审查/发布) | 采购报表(综合查询/编码绑定)

SET @purchase_root := (
  SELECT m.menu_id FROM sys_menu m
  WHERE m.parent_id = 0 AND m.menu_type = 'M'
    AND (m.path = 'purchase' OR m.menu_name = '采购管理')
  ORDER BY m.menu_id LIMIT 1
);
SET @order_mgmt := 3947;
SET @purchase_report := 3948;

-- 1) 若曾误做成一级：恢复其后一级菜单的 order_num
SET @was_l1_order := (
  SELECT COUNT(*) FROM sys_menu WHERE menu_id = @order_mgmt AND parent_id = 0
);
UPDATE sys_menu m
JOIN (
  SELECT menu_id FROM sys_menu
  WHERE parent_id = 0 AND order_num >= 5 AND order_num < 30
) t ON t.menu_id = m.menu_id
SET m.order_num = m.order_num - 2,
    m.update_by = '1',
    m.update_time = NOW()
WHERE @was_l1_order > 0;

-- 2) 确保二级目录存在（挂在采购管理下）
INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, query,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  @order_mgmt, '订单管理', @purchase_root, 10, 'orderMgmt', NULL, NULL,
  1, 0, 'M', '0', '0', '', 'list',
  'admin', NOW(), '1', NOW(), '采购管理分栏：订单审查/订单发布',
  '0', '1'
FROM DUAL
WHERE @purchase_root IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = @order_mgmt);

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, query,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  @purchase_report, '采购报表', @purchase_root, 11, 'cgReport', NULL, NULL,
  1, 0, 'M', '0', '0', '', 'chart',
  'admin', NOW(), '1', NOW(), '采购管理分栏：报表综合查询/云平台编码绑定',
  '0', '1'
FROM DUAL
WHERE @purchase_root IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = @purchase_report);

-- 挂到采购管理下并校正排序
UPDATE sys_menu
SET parent_id = @purchase_root,
    order_num = 10,
    path = 'orderMgmt',
    menu_type = 'M',
    component = NULL,
    update_by = '1',
    update_time = NOW()
WHERE menu_id = @order_mgmt;

UPDATE sys_menu
SET parent_id = @purchase_root,
    order_num = 11,
    path = 'cgReport',
    menu_type = 'M',
    component = NULL,
    update_by = '1',
    update_time = NOW()
WHERE menu_id = @purchase_report;

-- 3) 子菜单归属 + 纠正误标目录
UPDATE sys_menu
SET parent_id = @order_mgmt,
    order_num = 1,
    menu_type = 'C',
    path = 'shenhe',
    component = 'caigou/shenhe/index',
    perms = IFNULL(NULLIF(perms, ''), 'caigou:shenhe:list'),
    update_by = '1',
    update_time = NOW()
WHERE menu_id = 1431;

UPDATE sys_menu
SET parent_id = @order_mgmt,
    order_num = 2,
    update_by = '1',
    update_time = NOW()
WHERE menu_id = 1432;

UPDATE sys_menu
SET parent_id = @purchase_report,
    order_num = 1,
    menu_type = 'C',
    path = 'purchaseReport',
    component = 'caigou/report/index',
    update_by = '1',
    update_time = NOW()
WHERE menu_id = 1433;

UPDATE sys_menu
SET parent_id = @purchase_report,
    order_num = 2,
    update_by = '1',
    update_time = NOW()
WHERE menu_id = 3550;

-- 4) 授权回填（一级误建时已授过的可复用）
INSERT IGNORE INTO sys_role_menu (role_id, menu_id)
SELECT DISTINCT rm.role_id, @order_mgmt
FROM sys_role_menu rm WHERE rm.menu_id IN (1431, 1432);

INSERT IGNORE INTO sys_role_menu (role_id, menu_id)
SELECT DISTINCT rm.role_id, @purchase_report
FROM sys_role_menu rm WHERE rm.menu_id IN (1433, 3550);

INSERT INTO hc_customer_menu (tenant_id, menu_id, status, is_enabled, create_by, create_time)
SELECT c.tenant_id, @order_mgmt, '0', '1', 'admin', NOW()
FROM (SELECT DISTINCT tenant_id FROM hc_customer_menu WHERE menu_id IN (1431, 1432)) c
WHERE NOT EXISTS (
  SELECT 1 FROM hc_customer_menu h WHERE h.tenant_id = c.tenant_id AND h.menu_id = @order_mgmt
);

INSERT INTO hc_customer_menu (tenant_id, menu_id, status, is_enabled, create_by, create_time)
SELECT c.tenant_id, @purchase_report, '0', '1', 'admin', NOW()
FROM (SELECT DISTINCT tenant_id FROM hc_customer_menu WHERE menu_id IN (1433, 3550)) c
WHERE NOT EXISTS (
  SELECT 1 FROM hc_customer_menu h WHERE h.tenant_id = c.tenant_id AND h.menu_id = @purchase_report
);

INSERT IGNORE INTO sys_user_menu (user_id, menu_id, tenant_id)
SELECT DISTINCT um.user_id, @order_mgmt, um.tenant_id
FROM sys_user_menu um WHERE um.menu_id IN (1431, 1432);

INSERT IGNORE INTO sys_user_menu (user_id, menu_id, tenant_id)
SELECT DISTINCT um.user_id, @purchase_report, um.tenant_id
FROM sys_user_menu um WHERE um.menu_id IN (1433, 3550);

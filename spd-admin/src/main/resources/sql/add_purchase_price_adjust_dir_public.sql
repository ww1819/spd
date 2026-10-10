-- 采购管理京东分栏：采购报表之后增加「调价管理」
-- 分栏目标：采购管理 | 订单管理 | 采购报表 | 调价管理
-- 公共开放；按已有采购报表权限回填角色/客户/用户

SET @purchase_root := (
  SELECT m.menu_id FROM sys_menu m
  WHERE m.parent_id = 0 AND m.menu_type = 'M'
    AND (m.path = 'purchase' OR m.menu_name = '采购管理')
  ORDER BY m.menu_id LIMIT 1
);
SET @price_adjust_dir := 3961;
SET @price_adjust_page := 3962;
SET @purchase_report := 3948;

-- 1) 二级目录：调价管理
INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, query,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  @price_adjust_dir, '调价管理', @purchase_root, 12, 'priceAdjustDir', NULL, NULL,
  1, 0, 'M', '0', '0', '', 'money',
  'admin', NOW(), '1', NOW(), '采购管理分栏：调价单',
  '0', '1'
FROM DUAL
WHERE @purchase_root IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = @price_adjust_dir);

UPDATE sys_menu
SET parent_id = @purchase_root,
    order_num = 12,
    menu_name = '调价管理',
    path = 'priceAdjustDir',
    menu_type = 'M',
    component = NULL,
    visible = '0',
    status = '0',
    is_platform = '0',
    default_open_to_customer = '1',
    icon = IFNULL(NULLIF(icon, ''), 'money'),
    remark = '采购管理分栏：调价单',
    update_by = '1',
    update_time = NOW()
WHERE menu_id = @price_adjust_dir
  AND @purchase_root IS NOT NULL;

-- 2) 子页面：调价单
INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, query,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  @price_adjust_page, '调价单', @price_adjust_dir, 1, 'priceAdjust', 'caigou/priceAdjust/index', NULL,
  1, 0, 'C', '0', '0', 'caigou:priceAdjust:list', 'form',
  'admin', NOW(), '1', NOW(), '采购调价单（占位）',
  '0', '1'
FROM DUAL
WHERE @purchase_root IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = @price_adjust_page);

UPDATE sys_menu
SET parent_id = @price_adjust_dir,
    order_num = 1,
    menu_name = '调价单',
    path = 'priceAdjust',
    component = 'caigou/priceAdjust/index',
    menu_type = 'C',
    perms = 'caigou:priceAdjust:list',
    visible = '0',
    status = '0',
    is_platform = '0',
    default_open_to_customer = '1',
    update_by = '1',
    update_time = NOW()
WHERE menu_id = @price_adjust_page;

-- 3) 角色：有采购报表或其子菜单权限的，补目录与页面
INSERT IGNORE INTO sys_role_menu (role_id, menu_id)
SELECT DISTINCT rm.role_id, @price_adjust_dir
FROM sys_role_menu rm
WHERE rm.menu_id = @purchase_report
   OR rm.menu_id IN (SELECT menu_id FROM sys_menu WHERE parent_id = @purchase_report);

INSERT IGNORE INTO sys_role_menu (role_id, menu_id)
SELECT DISTINCT rm.role_id, @price_adjust_page
FROM sys_role_menu rm
WHERE rm.menu_id = @purchase_report
   OR rm.menu_id IN (SELECT menu_id FROM sys_menu WHERE parent_id = @purchase_report);

-- 4) 客户开放：已开采购报表的客户
INSERT INTO hc_customer_menu (tenant_id, menu_id, status, is_enabled, create_by, create_time)
SELECT c.tenant_id, @price_adjust_dir, '0', '1', 'admin', NOW()
FROM (SELECT DISTINCT tenant_id FROM hc_customer_menu WHERE menu_id = @purchase_report) c
WHERE NOT EXISTS (
  SELECT 1 FROM hc_customer_menu h
  WHERE h.tenant_id = c.tenant_id AND h.menu_id = @price_adjust_dir
);

INSERT INTO hc_customer_menu (tenant_id, menu_id, status, is_enabled, create_by, create_time)
SELECT c.tenant_id, @price_adjust_page, '0', '1', 'admin', NOW()
FROM (SELECT DISTINCT tenant_id FROM hc_customer_menu WHERE menu_id = @purchase_report) c
WHERE NOT EXISTS (
  SELECT 1 FROM hc_customer_menu h
  WHERE h.tenant_id = c.tenant_id AND h.menu_id = @price_adjust_page
);

-- 5) 用户：有采购报表或其子菜单的，补目录与页面
INSERT IGNORE INTO sys_user_menu (user_id, menu_id, tenant_id)
SELECT DISTINCT um.user_id, @price_adjust_dir, um.tenant_id
FROM sys_user_menu um
WHERE um.menu_id = @purchase_report
   OR um.menu_id IN (SELECT menu_id FROM sys_menu WHERE parent_id = @purchase_report);

INSERT IGNORE INTO sys_user_menu (user_id, menu_id, tenant_id)
SELECT DISTINCT um.user_id, @price_adjust_page, um.tenant_id
FROM sys_user_menu um
WHERE um.menu_id = @purchase_report
   OR um.menu_id IN (SELECT menu_id FROM sys_menu WHERE parent_id = @purchase_report);

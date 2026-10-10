-- 调价管理分栏补齐：调价申请 / 调价审核 / 调价报表查询
-- 目录 3979；申请页 3980；审核页 3981；报表页 3982

SET @purchase_root := (
  SELECT m.menu_id FROM sys_menu m
  WHERE m.parent_id = 0 AND m.menu_type = 'M'
    AND (m.path = 'purchase' OR m.menu_name = '采购管理')
    AND (m.visible = '0' OR m.visible IS NULL)
    AND (m.status = '0' OR m.status IS NULL)
  ORDER BY m.menu_id LIMIT 1
);
SET @purchase_report := 3948;
SET @price_adjust_dir := 3979;
SET @price_adjust_apply := 3980;
SET @price_adjust_audit := 3981;
SET @price_adjust_report := 3982;

-- 0) 确保目录存在且可见
INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, query,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  @price_adjust_dir, '调价管理', @purchase_root, 12, 'priceAdjustDir', NULL, NULL,
  1, 0, 'M', '0', '0', '', 'money',
  'admin', NOW(), '1', NOW(), '采购管理分栏：调价管理',
  '0', '1'
FROM DUAL
WHERE @purchase_root IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = @price_adjust_dir);

UPDATE sys_menu
SET parent_id = @purchase_root,
    order_num = 12,
    menu_name = '调价管理',
    path = 'priceAdjustDir',
    component = NULL,
    menu_type = 'M',
    visible = '0',
    status = '0',
    perms = '',
    is_platform = '0',
    default_open_to_customer = '1',
    icon = IFNULL(NULLIF(icon, ''), 'money'),
    remark = '采购管理分栏：调价管理',
    update_by = '1',
    update_time = NOW()
WHERE menu_id = @price_adjust_dir
  AND @purchase_root IS NOT NULL;

-- 1) 调价申请（原调价单）
INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, query,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  @price_adjust_apply, '调价申请', @price_adjust_dir, 1, 'priceAdjust', 'caigou/priceAdjust/index', NULL,
  1, 0, 'C', '0', '0', 'caigou:priceAdjust:list', 'form',
  'admin', NOW(), '1', NOW(), '采购调价申请',
  '0', '1'
FROM DUAL
WHERE @purchase_root IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = @price_adjust_apply);

UPDATE sys_menu
SET parent_id = @price_adjust_dir,
    order_num = 1,
    menu_name = '调价申请',
    path = 'priceAdjust',
    component = 'caigou/priceAdjust/index',
    menu_type = 'C',
    perms = 'caigou:priceAdjust:list',
    visible = '0',
    status = '0',
    is_platform = '0',
    default_open_to_customer = '1',
    icon = IFNULL(NULLIF(icon, ''), 'form'),
    remark = '采购调价申请',
    update_by = '1',
    update_time = NOW()
WHERE menu_id = @price_adjust_apply;

-- 2) 调价审核
INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, query,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  @price_adjust_audit, '调价审核', @price_adjust_dir, 2, 'priceAdjustAudit', 'caigou/priceAdjust/audit/index', NULL,
  1, 0, 'C', '0', '0', 'caigou:priceAdjust:audit', 'edit',
  'admin', NOW(), '1', NOW(), '采购调价审核',
  '0', '1'
FROM DUAL
WHERE @purchase_root IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = @price_adjust_audit);

UPDATE sys_menu
SET parent_id = @price_adjust_dir,
    order_num = 2,
    menu_name = '调价审核',
    path = 'priceAdjustAudit',
    component = 'caigou/priceAdjust/audit/index',
    menu_type = 'C',
    perms = 'caigou:priceAdjust:audit',
    visible = '0',
    status = '0',
    is_platform = '0',
    default_open_to_customer = '1',
    icon = IFNULL(NULLIF(icon, ''), 'edit'),
    remark = '采购调价审核',
    update_by = '1',
    update_time = NOW()
WHERE menu_id = @price_adjust_audit;

-- 3) 调价报表查询
INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, query,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  @price_adjust_report, '调价报表查询', @price_adjust_dir, 3, 'priceAdjustReport', 'caigou/priceAdjust/report/index', NULL,
  1, 0, 'C', '0', '0', 'caigou:priceAdjust:report', 'chart',
  'admin', NOW(), '1', NOW(), '采购调价报表查询',
  '0', '1'
FROM DUAL
WHERE @purchase_root IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = @price_adjust_report);

UPDATE sys_menu
SET parent_id = @price_adjust_dir,
    order_num = 3,
    menu_name = '调价报表查询',
    path = 'priceAdjustReport',
    component = 'caigou/priceAdjust/report/index',
    menu_type = 'C',
    perms = 'caigou:priceAdjust:report',
    visible = '0',
    status = '0',
    is_platform = '0',
    default_open_to_customer = '1',
    icon = IFNULL(NULLIF(icon, ''), 'chart'),
    remark = '采购调价报表查询',
    update_by = '1',
    update_time = NOW()
WHERE menu_id = @price_adjust_report;

-- 4) 若存在旧隐藏占位 3976/3977，保持隐藏避免干扰
UPDATE sys_menu
SET visible = '1',
    status = '1',
    update_by = '1',
    update_time = NOW()
WHERE menu_id IN (3976, 3977)
  AND parent_id IN (@price_adjust_dir, 3961);

-- 5) 角色：有采购报表或调价申请权限的，补目录与三页
INSERT IGNORE INTO sys_role_menu (role_id, menu_id)
SELECT DISTINCT rm.role_id, @price_adjust_dir
FROM sys_role_menu rm
WHERE rm.menu_id IN (@purchase_report, @price_adjust_apply)
   OR rm.menu_id IN (SELECT menu_id FROM sys_menu WHERE parent_id = @purchase_report);

INSERT IGNORE INTO sys_role_menu (role_id, menu_id)
SELECT DISTINCT rm.role_id, m.menu_id
FROM sys_role_menu rm
CROSS JOIN (
  SELECT @price_adjust_apply AS menu_id
  UNION ALL SELECT @price_adjust_audit
  UNION ALL SELECT @price_adjust_report
) m
WHERE rm.menu_id IN (@purchase_report, @price_adjust_apply, @price_adjust_dir)
   OR rm.menu_id IN (SELECT menu_id FROM sys_menu WHERE parent_id = @purchase_report);

-- 6) 客户开放
INSERT INTO hc_customer_menu (tenant_id, menu_id, status, is_enabled, create_by, create_time)
SELECT c.tenant_id, t.menu_id, '0', '1', 'admin', NOW()
FROM (SELECT DISTINCT tenant_id FROM hc_customer_menu WHERE menu_id IN (@purchase_report, @price_adjust_apply, @price_adjust_dir)) c
CROSS JOIN (
  SELECT @price_adjust_dir AS menu_id
  UNION ALL SELECT @price_adjust_apply
  UNION ALL SELECT @price_adjust_audit
  UNION ALL SELECT @price_adjust_report
) t
WHERE NOT EXISTS (
  SELECT 1 FROM hc_customer_menu h
  WHERE h.tenant_id = c.tenant_id AND h.menu_id = t.menu_id
);

-- 7) 用户菜单
INSERT IGNORE INTO sys_user_menu (user_id, menu_id, tenant_id)
SELECT DISTINCT um.user_id, t.menu_id, um.tenant_id
FROM sys_user_menu um
CROSS JOIN (
  SELECT @price_adjust_dir AS menu_id
  UNION ALL SELECT @price_adjust_apply
  UNION ALL SELECT @price_adjust_audit
  UNION ALL SELECT @price_adjust_report
) t
WHERE um.menu_id IN (@purchase_report, @price_adjust_apply, @price_adjust_dir)
   OR um.menu_id IN (SELECT menu_id FROM sys_menu WHERE parent_id = @purchase_report);

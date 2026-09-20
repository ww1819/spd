-- 公共：高值分栏底部假分组「高值管理」改为真实目录「追溯管理」
-- 1) 建/复用二级目录 3960「追溯管理」
-- 2) 迁入：高值核销确认(3850)、高值追溯页(1238)
-- 影响：全部租户（含 hengsui-third-001、zaoqiang-tcm-001）

SET @gz_root := (
  SELECT m.menu_id FROM sys_menu m
  WHERE m.parent_id = 0 AND m.menu_type = 'M' AND m.path = 'gz'
  ORDER BY m.menu_id LIMIT 1
);
SET @trace_dir := 3960;

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, query,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  @trace_dir, '追溯管理', @gz_root, 5, 'gzTraceMgmt', NULL, NULL,
  1, 0, 'M', '0', '0', '', 'search',
  'admin', NOW(), '1', NOW(), '高值核销确认/高值追溯',
  '0', '1'
FROM DUAL
WHERE @gz_root IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = @trace_dir);

UPDATE sys_menu
SET parent_id = @gz_root,
    order_num = 5,
    menu_name = '追溯管理',
    path = 'gzTraceMgmt',
    menu_type = 'M',
    component = NULL,
    visible = '0',
    status = '0',
    is_platform = '0',
    default_open_to_customer = '1',
    update_by = '1',
    update_time = NOW()
WHERE menu_id = @trace_dir AND @gz_root IS NOT NULL;

-- 高值核销确认、高值追溯页 → 高值追溯目录（不再挂在高值使用 / 一级叶子）
UPDATE sys_menu
SET parent_id = @trace_dir, order_num = 1, is_platform = '0', default_open_to_customer = '1',
    update_by = '1', update_time = NOW()
WHERE menu_id = 3850;

UPDATE sys_menu
SET parent_id = @trace_dir, order_num = 2, is_platform = '0', default_open_to_customer = '1',
    update_by = '1', update_time = NOW()
WHERE menu_id = 1238;

-- 授权回填：有子菜单权限的角色/用户/岗位/租户，补目录权限
INSERT IGNORE INTO sys_role_menu (role_id, menu_id)
SELECT DISTINCT rm.role_id, @trace_dir
FROM sys_role_menu rm
WHERE rm.menu_id IN (3850, 1238);

INSERT IGNORE INTO sys_user_menu (user_id, menu_id, tenant_id)
SELECT DISTINCT um.user_id, @trace_dir, COALESCE(NULLIF(um.tenant_id, ''), u.customer_id)
FROM sys_user_menu um
JOIN sys_user u ON u.user_id = um.user_id
WHERE um.menu_id IN (3850, 1238);

INSERT INTO sys_post_menu (post_id, menu_id, tenant_id)
SELECT DISTINCT pm.post_id, @trace_dir, pm.tenant_id
FROM sys_post_menu pm
WHERE pm.menu_id IN (3850, 1238)
  AND NOT EXISTS (
    SELECT 1 FROM sys_post_menu x
    WHERE x.post_id = pm.post_id AND x.menu_id = @trace_dir
      AND IFNULL(x.tenant_id, '') = IFNULL(pm.tenant_id, '')
  );

INSERT INTO hc_customer_menu (tenant_id, menu_id, status, is_enabled, create_by, create_time)
SELECT c.customer_id, @trace_dir, '0', '1', 'admin', NOW()
FROM sb_customer c
WHERE IFNULL(c.hc_status, '0') = '0'
  AND c.delete_time IS NULL
  AND NOT EXISTS (
    SELECT 1 FROM hc_customer_menu h
    WHERE h.tenant_id = c.customer_id AND h.menu_id = @trace_dir
  );

INSERT INTO hc_customer_menu (tenant_id, menu_id, status, is_enabled, create_by, create_time)
SELECT t.tenant_id, @trace_dir, '0', '1', 'admin', NOW()
FROM (SELECT 'hengsui-third-001' tenant_id UNION ALL SELECT 'zaoqiang-tcm-001') t
WHERE EXISTS (SELECT 1 FROM sb_customer c WHERE c.customer_id = t.tenant_id AND c.delete_time IS NULL)
  AND NOT EXISTS (
    SELECT 1 FROM hc_customer_menu h
    WHERE h.tenant_id = t.tenant_id AND h.menu_id = @trace_dir
  );

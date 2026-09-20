-- 系统管理：日志管理前新增公共二级目录「人员权限」
-- 迁入：用户管理、角色管理、菜单管理、工作组

SET @system_root := (
  SELECT m.menu_id FROM sys_menu m
  WHERE m.parent_id = 0 AND m.menu_type = 'M' AND m.path = 'system'
  ORDER BY m.menu_id LIMIT 1
);
SET @person_perm := 3958;
SET @log_menu := 108;

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, query,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  @person_perm, '人员权限', @system_root, 1, 'personPerm', NULL, NULL,
  1, 0, 'M', '0', '0', '', 'peoples',
  'admin', NOW(), '1', NOW(), '用户/角色/菜单/工作组',
  '0', '1'
FROM DUAL
WHERE @system_root IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = @person_perm);

UPDATE sys_menu
SET parent_id = @system_root,
    order_num = 1,
    menu_name = '人员权限',
    path = 'personPerm',
    menu_type = 'M',
    component = NULL,
    visible = '0',
    status = '0',
    is_platform = '0',
    default_open_to_customer = '1',
    update_by = '1',
    update_time = NOW()
WHERE menu_id = @person_perm;

-- 确保日志管理排在人员权限之后
UPDATE sys_menu
SET order_num = 10,
    update_by = '1',
    update_time = NOW()
WHERE menu_id = @log_menu AND parent_id = @system_root;

UPDATE sys_menu SET parent_id = @person_perm, order_num = 1, is_platform = '0', default_open_to_customer = '1', update_by = '1', update_time = NOW() WHERE menu_id = 100; -- 用户管理
UPDATE sys_menu SET parent_id = @person_perm, order_num = 2, is_platform = '0', default_open_to_customer = '1', update_by = '1', update_time = NOW() WHERE menu_id = 101; -- 角色管理
UPDATE sys_menu SET parent_id = @person_perm, order_num = 3, is_platform = '0', default_open_to_customer = '1', update_by = '1', update_time = NOW() WHERE menu_id = 102; -- 菜单管理
UPDATE sys_menu SET parent_id = @person_perm, order_num = 4, is_platform = '0', default_open_to_customer = '1', update_by = '1', update_time = NOW() WHERE menu_id = 104; -- 工作组

INSERT IGNORE INTO sys_role_menu (role_id, menu_id)
SELECT DISTINCT rm.role_id, @person_perm
FROM sys_role_menu rm
WHERE rm.menu_id IN (100, 101, 102, 104);

INSERT IGNORE INTO sys_user_menu (user_id, menu_id, tenant_id)
SELECT DISTINCT um.user_id, @person_perm, COALESCE(NULLIF(um.tenant_id, ''), u.customer_id)
FROM sys_user_menu um
JOIN sys_user u ON u.user_id = um.user_id
WHERE um.menu_id IN (100, 101, 102, 104);

INSERT INTO sys_post_menu (post_id, menu_id, tenant_id)
SELECT DISTINCT pm.post_id, @person_perm, pm.tenant_id
FROM sys_post_menu pm
WHERE pm.menu_id IN (100, 101, 102, 104)
  AND NOT EXISTS (
    SELECT 1 FROM sys_post_menu x
    WHERE x.post_id = pm.post_id AND x.menu_id = @person_perm
      AND IFNULL(x.tenant_id, '') = IFNULL(pm.tenant_id, '')
  );

INSERT INTO hc_customer_menu (tenant_id, menu_id, status, is_enabled, create_by, create_time)
SELECT c.customer_id, @person_perm, '0', '1', 'admin', NOW()
FROM sb_customer c
WHERE IFNULL(c.hc_status, '0') = '0'
  AND c.delete_time IS NULL
  AND NOT EXISTS (
    SELECT 1 FROM hc_customer_menu h
    WHERE h.tenant_id = c.customer_id AND h.menu_id = @person_perm
  );

INSERT INTO hc_customer_menu (tenant_id, menu_id, status, is_enabled, create_by, create_time)
SELECT t.tenant_id, @person_perm, '0', '1', 'admin', NOW()
FROM (
  SELECT 'hengsui-third-001' AS tenant_id UNION ALL SELECT 'zaoqiang-tcm-001'
) t
WHERE EXISTS (SELECT 1 FROM sb_customer c WHERE c.customer_id = t.tenant_id AND c.delete_time IS NULL)
  AND NOT EXISTS (
    SELECT 1 FROM hc_customer_menu h
    WHERE h.tenant_id = t.tenant_id AND h.menu_id = @person_perm
  );

-- 公共/可重复：打印设置按钮权限（新增/修改/删除/查询）
-- 缺 F 按钮时前端 v-hasPermi 会隐藏工具栏与操作列
-- 执行方式：SqlInitRunner（menu.sql 同步段）或本文件单独补跑（「/」分段）

SET @print_setting_menu := (
  SELECT menu_id FROM sys_menu
  WHERE menu_type = 'C'
    AND (component = 'system/printSetting/index' OR path = 'printSetting' OR menu_name = '打印设置')
  ORDER BY CASE WHEN component = 'system/printSetting/index' THEN 0 ELSE 1 END, menu_id
  LIMIT 1
);
/
UPDATE sys_menu
SET perms = 'system:printSetting:list',
    default_open_to_customer = '1',
    update_by = '1',
    update_time = NOW()
WHERE menu_id = @print_setting_menu
  AND @print_setting_menu IS NOT NULL
  AND (perms IS NULL OR perms = '' OR perms NOT LIKE 'system:printSetting:%');
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3961, '打印设置查询', @print_setting_menu, 1, '#', '', NULL, 1, 0, 'F', '0', '0', 'system:printSetting:query', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE @print_setting_menu IS NOT NULL
  AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'F' AND parent_id = @print_setting_menu AND perms = 'system:printSetting:query') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 3961))
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), parent_id = VALUES(parent_id), order_num = VALUES(order_num), perms = VALUES(perms), update_time = VALUES(update_time);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3962, '打印设置新增', @print_setting_menu, 2, '#', '', NULL, 1, 0, 'F', '0', '0', 'system:printSetting:add', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE @print_setting_menu IS NOT NULL
  AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'F' AND parent_id = @print_setting_menu AND perms = 'system:printSetting:add') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 3962))
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), parent_id = VALUES(parent_id), order_num = VALUES(order_num), perms = VALUES(perms), update_time = VALUES(update_time);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3963, '打印设置修改', @print_setting_menu, 3, '#', '', NULL, 1, 0, 'F', '0', '0', 'system:printSetting:edit', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE @print_setting_menu IS NOT NULL
  AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'F' AND parent_id = @print_setting_menu AND perms = 'system:printSetting:edit') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 3963))
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), parent_id = VALUES(parent_id), order_num = VALUES(order_num), perms = VALUES(perms), update_time = VALUES(update_time);
/
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT 3964, '打印设置删除', @print_setting_menu, 4, '#', '', NULL, 1, 0, 'F', '0', '0', 'system:printSetting:remove', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE @print_setting_menu IS NOT NULL
  AND (NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'F' AND parent_id = @print_setting_menu AND perms = 'system:printSetting:remove') OR EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 3964))
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), parent_id = VALUES(parent_id), order_num = VALUES(order_num), perms = VALUES(perms), update_time = VALUES(update_time);
/
INSERT IGNORE INTO sys_role_menu (role_id, menu_id) VALUES (1, 3961), (1, 3962), (1, 3963), (1, 3964);
/
INSERT IGNORE INTO sys_role_menu (role_id, menu_id)
SELECT DISTINCT rm.role_id, b.menu_id
FROM sys_role_menu rm
JOIN (
  SELECT 3961 AS menu_id UNION ALL SELECT 3962 UNION ALL SELECT 3963 UNION ALL SELECT 3964
) b
WHERE rm.menu_id = @print_setting_menu AND @print_setting_menu IS NOT NULL;
/
INSERT IGNORE INTO sys_user_menu (user_id, menu_id, tenant_id)
SELECT DISTINCT um.user_id, b.menu_id, COALESCE(NULLIF(um.tenant_id, ''), u.customer_id)
FROM sys_user_menu um
JOIN sys_user u ON u.user_id = um.user_id
JOIN (
  SELECT 3961 AS menu_id UNION ALL SELECT 3962 UNION ALL SELECT 3963 UNION ALL SELECT 3964
) b
WHERE um.menu_id = @print_setting_menu AND @print_setting_menu IS NOT NULL;
/
INSERT INTO sys_post_menu (post_id, menu_id, tenant_id)
SELECT DISTINCT pm.post_id, b.menu_id, pm.tenant_id
FROM sys_post_menu pm
JOIN (
  SELECT 3961 AS menu_id UNION ALL SELECT 3962 UNION ALL SELECT 3963 UNION ALL SELECT 3964
) b
WHERE pm.menu_id = @print_setting_menu
  AND @print_setting_menu IS NOT NULL
  AND NOT EXISTS (
    SELECT 1 FROM sys_post_menu x
    WHERE x.post_id = pm.post_id AND x.menu_id = b.menu_id
      AND IFNULL(x.tenant_id, '') = IFNULL(pm.tenant_id, '')
  );
/
INSERT INTO hc_customer_menu (tenant_id, menu_id, status, is_enabled, create_by, create_time)
SELECT DISTINCT h.tenant_id, b.menu_id, '0', '1', 'admin', NOW()
FROM hc_customer_menu h
JOIN (
  SELECT 3961 AS menu_id UNION ALL SELECT 3962 UNION ALL SELECT 3963 UNION ALL SELECT 3964
) b
WHERE h.menu_id = @print_setting_menu
  AND @print_setting_menu IS NOT NULL
  AND NOT EXISTS (
    SELECT 1 FROM hc_customer_menu x
    WHERE x.tenant_id = h.tenant_id AND x.menu_id = b.menu_id
  );
/
INSERT INTO hc_customer_menu (tenant_id, menu_id, status, is_enabled, create_by, create_time)
SELECT t.tenant_id, m.menu_id, '0', '1', 'admin', NOW()
FROM (SELECT 'hengsui-third-001' tenant_id UNION ALL SELECT 'zaoqiang-tcm-001') t
JOIN (
  SELECT 3961 AS menu_id UNION ALL SELECT 3962 UNION ALL SELECT 3963 UNION ALL SELECT 3964
) m
WHERE EXISTS (SELECT 1 FROM sb_customer c WHERE c.customer_id = t.tenant_id AND c.delete_time IS NULL)
  AND NOT EXISTS (
    SELECT 1 FROM hc_customer_menu h
    WHERE h.tenant_id = t.tenant_id AND h.menu_id = m.menu_id
  );
/

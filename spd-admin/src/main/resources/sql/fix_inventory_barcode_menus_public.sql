-- 库房管理：将条码归属/流通、高值即入即出重新挂回「库存管理」，去掉一级下假「库房管理」分组
-- 公共开放 + 回填衡水/枣强

SET @wh_root := (
  SELECT m.menu_id FROM sys_menu m
  WHERE m.parent_id = 0 AND m.menu_type = 'M'
    AND (m.path = 'warehouse' OR m.menu_name = '库房管理')
  ORDER BY m.menu_id LIMIT 1
);
SET @inventory_mgmt := 3951;

UPDATE sys_menu
SET parent_id = @wh_root,
    order_num = 5,
    menu_name = '库存管理',
    path = 'inventoryMgmt',
    menu_type = 'M',
    is_platform = '0',
    default_open_to_customer = '1',
    update_by = '1',
    update_time = NOW()
WHERE menu_id = @inventory_mgmt;

UPDATE sys_menu SET parent_id = @inventory_mgmt, order_num = 1, is_platform = '0', default_open_to_customer = '1', update_by = '1', update_time = NOW() WHERE menu_id = 1069;
UPDATE sys_menu SET parent_id = @inventory_mgmt, order_num = 2, is_platform = '0', default_open_to_customer = '1', update_by = '1', update_time = NOW() WHERE menu_id = 3708;
UPDATE sys_menu SET parent_id = @inventory_mgmt, order_num = 3, is_platform = '0', default_open_to_customer = '1', update_by = '1', update_time = NOW() WHERE menu_id = 3711;
UPDATE sys_menu SET parent_id = @inventory_mgmt, order_num = 4, is_platform = '0', default_open_to_customer = '1', update_by = '1', update_time = NOW() WHERE menu_id = 3720;

INSERT IGNORE INTO sys_role_menu (role_id, menu_id)
SELECT DISTINCT rm.role_id, @inventory_mgmt
FROM sys_role_menu rm
WHERE rm.menu_id IN (1069, 3708, 3711, 3720);

INSERT IGNORE INTO sys_user_menu (user_id, menu_id, tenant_id)
SELECT DISTINCT um.user_id, @inventory_mgmt, COALESCE(NULLIF(um.tenant_id, ''), u.customer_id)
FROM sys_user_menu um
JOIN sys_user u ON u.user_id = um.user_id
WHERE um.menu_id IN (1069, 3708, 3711, 3720);

INSERT INTO sys_post_menu (post_id, menu_id, tenant_id)
SELECT DISTINCT pm.post_id, @inventory_mgmt, pm.tenant_id
FROM sys_post_menu pm
WHERE pm.menu_id IN (1069, 3708, 3711, 3720)
  AND NOT EXISTS (
    SELECT 1 FROM sys_post_menu x
    WHERE x.post_id = pm.post_id AND x.menu_id = @inventory_mgmt
      AND IFNULL(x.tenant_id, '') = IFNULL(pm.tenant_id, '')
  );

INSERT INTO hc_customer_menu (tenant_id, menu_id, status, is_enabled, create_by, create_time)
SELECT c.customer_id, m.menu_id, '0', '1', 'admin', NOW()
FROM sb_customer c
JOIN (
  SELECT 3951 AS menu_id UNION ALL SELECT 1069 UNION ALL SELECT 3708
  UNION ALL SELECT 3711 UNION ALL SELECT 3720
) m
WHERE IFNULL(c.hc_status, '0') = '0'
  AND c.delete_time IS NULL
  AND NOT EXISTS (
    SELECT 1 FROM hc_customer_menu h
    WHERE h.tenant_id = c.customer_id AND h.menu_id = m.menu_id
  );

INSERT INTO hc_customer_menu (tenant_id, menu_id, status, is_enabled, create_by, create_time)
SELECT t.tenant_id, m.menu_id, '0', '1', 'admin', NOW()
FROM (
  SELECT 'hengsui-third-001' AS tenant_id UNION ALL SELECT 'zaoqiang-tcm-001'
) t
JOIN (
  SELECT 3951 AS menu_id UNION ALL SELECT 1069 UNION ALL SELECT 3708
  UNION ALL SELECT 3711 UNION ALL SELECT 3720
) m
WHERE EXISTS (SELECT 1 FROM sb_customer c WHERE c.customer_id = t.tenant_id AND c.delete_time IS NULL)
  AND NOT EXISTS (
    SELECT 1 FROM hc_customer_menu h
    WHERE h.tenant_id = t.tenant_id AND h.menu_id = m.menu_id
  );

-- 库房管理：新建公共二级目录「库存管理」，与「盘点管理」对调位置
-- 顺序：入库 → 出库 → 库存管理 → 调拨 → 盘点
-- 公共：default_open_to_customer=1，回填衡水/枣强等启用客户

SET @wh_root := (
  SELECT m.menu_id FROM sys_menu m
  WHERE m.parent_id = 0 AND m.menu_type = 'M'
    AND (m.path = 'warehouse' OR m.menu_name = '库房管理')
  ORDER BY m.menu_id LIMIT 1
);
SET @inventory_mgmt := 3951;

INSERT IGNORE INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, query,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
) VALUES (
  3951, '库存管理', @wh_root, 5, 'inventoryMgmt', NULL, NULL,
  1, 0, 'M', '0', '0', '', 'chart',
  'admin', NOW(), '1', NOW(), '库存查询/条码/高值即入即出',
  '0', '1'
);

UPDATE sys_menu
SET parent_id = @wh_root,
    order_num = 5,
    menu_name = '库存管理',
    path = 'inventoryMgmt',
    menu_type = 'M',
    component = NULL,
    visible = '0',
    status = '0',
    is_platform = '0',
    default_open_to_customer = '1',
    update_by = '1',
    update_time = NOW()
WHERE menu_id = 3951;

UPDATE sys_menu SET parent_id = 3951, order_num = 1, update_by = '1', update_time = NOW() WHERE menu_id = 1069;
UPDATE sys_menu SET parent_id = 3951, order_num = 2, update_by = '1', update_time = NOW() WHERE menu_id = 3708;
UPDATE sys_menu SET parent_id = 3951, order_num = 3, update_by = '1', update_time = NOW() WHERE menu_id = 3711;
UPDATE sys_menu SET parent_id = 3951, order_num = 4, update_by = '1', update_time = NOW() WHERE menu_id = 3720;

UPDATE sys_menu SET order_num = 6, update_by = '1', update_time = NOW() WHERE menu_id = 1544 AND parent_id = @wh_root;
UPDATE sys_menu SET order_num = 7, update_by = '1', update_time = NOW() WHERE menu_id = 1070 AND parent_id = @wh_root;

UPDATE sys_menu
SET is_platform = '0', default_open_to_customer = '1', update_by = '1', update_time = NOW()
WHERE menu_id IN (3951, 1070);

INSERT INTO hc_customer_menu (tenant_id, menu_id, status, is_enabled, create_by, create_time)
SELECT c.customer_id, m.menu_id, '0', '1', 'admin', NOW()
FROM sb_customer c
JOIN sys_menu m ON m.menu_id IN (3951, 1070)
WHERE IFNULL(c.hc_status, '0') = '0'
  AND c.delete_time IS NULL
  AND NOT EXISTS (
    SELECT 1 FROM hc_customer_menu h
    WHERE h.tenant_id = c.customer_id AND h.menu_id = m.menu_id
  );

INSERT INTO hc_customer_menu (tenant_id, menu_id, status, is_enabled, create_by, create_time)
SELECT 'zaoqiang-tcm-001', m.menu_id, '0', '1', 'admin', NOW()
FROM sys_menu m
WHERE m.menu_id IN (3951, 1070)
  AND EXISTS (SELECT 1 FROM sb_customer c WHERE c.customer_id = 'zaoqiang-tcm-001' AND c.delete_time IS NULL)
  AND NOT EXISTS (
    SELECT 1 FROM hc_customer_menu h
    WHERE h.tenant_id = 'zaoqiang-tcm-001' AND h.menu_id = m.menu_id
  );

INSERT IGNORE INTO sys_user_menu (user_id, menu_id, tenant_id)
SELECT DISTINCT um.user_id, 3951, COALESCE(NULLIF(um.tenant_id, ''), u.customer_id)
FROM sys_user_menu um
JOIN sys_user u ON u.user_id = um.user_id
WHERE um.menu_id IN (1069, 3708, 3711, 3720);

INSERT INTO sys_post_menu (post_id, menu_id, tenant_id)
SELECT p.post_id, m.menu_id, p.tenant_id
FROM sys_post p
JOIN sys_menu m ON m.menu_id IN (3951, 1070)
WHERE IFNULL(p.status, '0') = '0'
  AND p.post_code = 'super'
  AND p.tenant_id IS NOT NULL AND TRIM(p.tenant_id) != ''
  AND EXISTS (
    SELECT 1 FROM sb_customer c
    WHERE c.customer_id = p.tenant_id AND IFNULL(c.hc_status, '0') = '0' AND c.delete_time IS NULL
  )
  AND NOT EXISTS (
    SELECT 1 FROM sys_post_menu x WHERE x.post_id = p.post_id AND x.menu_id = m.menu_id
  );

INSERT IGNORE INTO sys_role_menu (role_id, menu_id)
SELECT DISTINCT rm.role_id, 3951
FROM sys_role_menu rm
WHERE rm.menu_id IN (1069, 3708, 3711, 3720);

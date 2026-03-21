-- 订单发布、到货验收：sys_menu 菜单/按钮 + hc_customer_menu 默认开放
-- 采购订单（caigou/dingdan/index，含 caigou:dingdan:list）已写入 material/menu.sql，单独补库请先执行该段

SET @caigou_parent := COALESCE(
  (SELECT m.menu_id FROM sys_menu m WHERE m.menu_type = 'M' AND m.path = 'caigou' ORDER BY m.menu_id LIMIT 1),
  (SELECT m.parent_id FROM sys_menu m WHERE m.menu_type = 'C' AND m.component = 'caigou/dingdan/index' ORDER BY m.menu_id LIMIT 1),
  1
);

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

SET @publish_menu_id := (
  SELECT menu_id FROM sys_menu WHERE menu_type = 'C' AND component = 'caigou/publish/index' ORDER BY menu_id DESC LIMIT 1
);

-- 按钮：query / export / audit（见 spd-ui caigou/publish/index.vue）
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT (SELECT IFNULL(MAX(menu_id), 0) + 1 FROM (SELECT menu_id FROM sys_menu) t), '订单发布查询', @publish_menu_id, 1, '#', '', NULL, 1, 0, 'F', '0', '0', 'caigou:dingdan:query', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE @publish_menu_id IS NOT NULL AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE parent_id = @publish_menu_id AND perms = 'caigou:dingdan:query');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT (SELECT IFNULL(MAX(menu_id), 0) + 1 FROM (SELECT menu_id FROM sys_menu) t), '订单发布导出', @publish_menu_id, 2, '#', '', NULL, 1, 0, 'F', '0', '0', 'caigou:dingdan:export', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE @publish_menu_id IS NOT NULL AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE parent_id = @publish_menu_id AND perms = 'caigou:dingdan:export');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT (SELECT IFNULL(MAX(menu_id), 0) + 1 FROM (SELECT menu_id FROM sys_menu) t), '订单发布审核', @publish_menu_id, 3, '#', '', NULL, 1, 0, 'F', '0', '0', 'caigou:dingdan:audit', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE @publish_menu_id IS NOT NULL AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE parent_id = @publish_menu_id AND perms = 'caigou:dingdan:audit');

SET @in_wh_parent := COALESCE(
  (SELECT m.menu_id FROM sys_menu m WHERE m.menu_type = 'M' AND (m.path = 'inWarehouse' OR m.path = 'warehouse') ORDER BY m.menu_id LIMIT 1),
  (SELECT m.parent_id FROM sys_menu m WHERE m.menu_type = 'C' AND m.component = 'inWarehouse/apply/index' ORDER BY m.menu_id LIMIT 1),
  (SELECT m.parent_id FROM sys_menu m WHERE m.menu_type = 'C' AND m.perms = 'inWarehouse:apply:list' ORDER BY m.menu_id LIMIT 1),
  1
);

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
  'admin', NOW(), '1', NOW(), '入库单到货验收',
  '0', '1'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'C' AND component = 'inWarehouse/audit/index');

SET @audit_menu_id := (
  SELECT menu_id FROM sys_menu WHERE menu_type = 'C' AND component = 'inWarehouse/audit/index' ORDER BY menu_id DESC LIMIT 1
);

-- 按钮：query / export / audit / edit（见 spd-ui inWarehouse/audit/index.vue）
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT (SELECT IFNULL(MAX(menu_id), 0) + 1 FROM (SELECT menu_id FROM sys_menu) t), '到货验收查询', @audit_menu_id, 1, '#', '', NULL, 1, 0, 'F', '0', '0', 'inWarehouse:apply:query', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE @audit_menu_id IS NOT NULL AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE parent_id = @audit_menu_id AND perms = 'inWarehouse:apply:query');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT (SELECT IFNULL(MAX(menu_id), 0) + 1 FROM (SELECT menu_id FROM sys_menu) t), '到货验收导出', @audit_menu_id, 2, '#', '', NULL, 1, 0, 'F', '0', '0', 'inWarehouse:apply:export', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE @audit_menu_id IS NOT NULL AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE parent_id = @audit_menu_id AND perms = 'inWarehouse:apply:export');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT (SELECT IFNULL(MAX(menu_id), 0) + 1 FROM (SELECT menu_id FROM sys_menu) t), '到货验收审核', @audit_menu_id, 3, '#', '', NULL, 1, 0, 'F', '0', '0', 'inWarehouse:apply:audit', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE @audit_menu_id IS NOT NULL AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE parent_id = @audit_menu_id AND perms = 'inWarehouse:apply:audit');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT (SELECT IFNULL(MAX(menu_id), 0) + 1 FROM (SELECT menu_id FROM sys_menu) t), '到货验收修改', @audit_menu_id, 4, '#', '', NULL, 1, 0, 'F', '0', '0', 'inWarehouse:apply:edit', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE @audit_menu_id IS NOT NULL AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE parent_id = @audit_menu_id AND perms = 'inWarehouse:apply:edit');

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
    'inWarehouse:apply:edit'
  )
WHERE c.hc_status = '0'
  AND NOT EXISTS (
    SELECT 1 FROM hc_customer_menu h
    WHERE h.tenant_id = c.customer_id AND h.menu_id = m.menu_id
  );

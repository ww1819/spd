-- =============================================================================
-- 定数监测：sys_menu + hc_customer_menu（与 FixedNumberController 一致）
-- 正式脚本已合并：spd-admin/src/main/resources/sql/mysql/material/menu.sql「定数监测」段
-- 存量库可单独执行本文件补菜单（与 menu.sql 段逻辑一致）
-- =============================================================================
-- 接口前缀：GET /monitoring/fixedNumber/list、POST /monitoring/fixedNumber 等
-- 权限标识：monitoring:fixedNumber:list / add / remove / export
-- 前端页面：spd-ui/src/views/monitoring/fixedNumber/index.vue
-- =============================================================================

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
  'admin', NOW(), '1', NOW(), '仓库/科室定数监测 FixedNumberController /monitoring/fixedNumber',
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

INSERT INTO hc_customer_menu (tenant_id, menu_id, status, is_enabled, create_by, create_time)
SELECT c.customer_id, m.menu_id, '0', '1', 'admin', NOW()
FROM sb_customer c
JOIN sys_menu m
  ON m.perms IN (
    'monitoring:fixedNumber:list',
    'monitoring:fixedNumber:add',
    'monitoring:fixedNumber:remove',
    'monitoring:fixedNumber:export'
  )
WHERE c.hc_status = '0'
  AND NOT EXISTS (
    SELECT 1 FROM hc_customer_menu h
    WHERE h.tenant_id = c.customer_id AND h.menu_id = m.menu_id
  );
/

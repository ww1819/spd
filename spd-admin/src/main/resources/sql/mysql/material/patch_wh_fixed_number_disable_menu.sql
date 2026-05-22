-- 定数监测：启用/停用按钮权限菜单（存量库单独执行）
-- 权限标识：monitoring:fixedNumber:disable、monitoring:fixedNumber:enable

SET @fixed_num_menu := (
  SELECT menu_id FROM sys_menu WHERE component = 'monitoring/fixedNumber/index' AND menu_type = 'C' ORDER BY menu_id DESC LIMIT 1
);

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  (SELECT IFNULL(MAX(menu_id), 0) + 1 FROM (SELECT menu_id FROM sys_menu) t),
  '定数停用',
  @fixed_num_menu,
  3,
  '#', '', NULL,
  1, 0, 'F', '0', '0', 'monitoring:fixedNumber:disable', '#',
  'admin', NOW(), '1', NOW(), '关闭产品档案与仓库定数关联',
  '0', '1'
FROM DUAL
WHERE @fixed_num_menu IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE parent_id = @fixed_num_menu AND perms = 'monitoring:fixedNumber:disable');

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  (SELECT IFNULL(MAX(menu_id), 0) + 1 FROM (SELECT menu_id FROM sys_menu) t),
  '定数启用',
  @fixed_num_menu,
  4,
  '#', '', NULL,
  1, 0, 'F', '0', '0', 'monitoring:fixedNumber:enable', '#',
  'admin', NOW(), '1', NOW(), '恢复产品档案与仓库定数关联',
  '0', '1'
FROM DUAL
WHERE @fixed_num_menu IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE parent_id = @fixed_num_menu AND perms = 'monitoring:fixedNumber:enable');

UPDATE sys_menu SET order_num = 5
WHERE parent_id = @fixed_num_menu AND perms = 'monitoring:fixedNumber:export' AND order_num < 5;

-- 已有任一定数监测按钮/页面权限的角色，自动挂上停用/启用
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT DISTINCT rm.role_id, m.menu_id
FROM sys_role_menu rm
INNER JOIN sys_menu existing ON existing.menu_id = rm.menu_id
  AND existing.perms LIKE 'monitoring:fixedNumber:%'
INNER JOIN sys_menu m ON m.perms IN ('monitoring:fixedNumber:disable', 'monitoring:fixedNumber:enable')
WHERE NOT EXISTS (
  SELECT 1 FROM sys_role_menu x WHERE x.role_id = rm.role_id AND x.menu_id = m.menu_id
);

-- 超级管理员角色（role_id=1）默认授权
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 1, m.menu_id
FROM sys_menu m
WHERE m.perms IN ('monitoring:fixedNumber:disable', 'monitoring:fixedNumber:enable')
  AND NOT EXISTS (SELECT 1 FROM sys_role_menu x WHERE x.role_id = 1 AND x.menu_id = m.menu_id);

-- 默认对客户开放：回填 hc_customer_menu
INSERT INTO hc_customer_menu (tenant_id, menu_id, status, is_enabled, create_by, create_time)
SELECT c.customer_id, m.menu_id, '0', '1', 'admin', NOW()
FROM sb_customer c
JOIN sys_menu m ON m.perms IN ('monitoring:fixedNumber:disable', 'monitoring:fixedNumber:enable')
WHERE c.hc_status = '0'
  AND NOT EXISTS (
    SELECT 1 FROM hc_customer_menu h
    WHERE h.tenant_id = c.customer_id AND h.menu_id = m.menu_id
  );

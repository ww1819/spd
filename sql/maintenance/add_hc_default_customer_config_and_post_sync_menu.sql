-- =============================================================================
-- 【已并入】spd-admin/src/main/resources/sql/mysql/material/data_integrity.sql
-- 启动/初始化时请执行 data_integrity.sql；本文件仅作存量库单独补执行或查阅备份。
-- =============================================================================
-- 1) 耗材登录页默认组织机构：sys_config.hc.login.defaultCustomerId（值为 sb_customer.customer_id）
-- 2) 岗位管理「同步仓库/科室/菜单」按钮权限：system:post:sync（挂在 system:post:list 菜单下）
-- 执行后请在「参数设置」中刷新缓存；若库中已有同 config_key 则跳过插入
-- =============================================================================

INSERT INTO sys_config (config_name, config_key, config_value, config_type, create_by, create_time, remark)
SELECT
  '耗材登录默认客户',
  'hc.login.defaultCustomerId',
  '',
  'N',
  'admin',
  NOW(),
  '登录页组织机构默认值，填写 sb_customer.customer_id；空表示不默认。可在参数设置中通过下拉选择（键名为 hc.login.defaultCustomerId 时）。'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sys_config WHERE config_key = 'hc.login.defaultCustomerId');
/

SET @post_list_menu_id := (
  SELECT menu_id FROM sys_menu
  WHERE perms = 'system:post:list' AND menu_type = 'C'
  ORDER BY menu_id
  LIMIT 1
);
/

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  2310,
  '工作组同步权限',
  @post_list_menu_id,
  90,
  '#',
  '',
  NULL,
  1, 0, 'F', '0', '0', 'system:post:sync', '#',
  'admin', NOW(), '1', NOW(),
  '将岗位已授权的仓库/科室/菜单批量写入组内用户（耗材端与设备端岗位页按钮一致）',
  '0', '1'
FROM DUAL
WHERE @post_list_menu_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 2310 OR perms = 'system:post:sync')
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  order_num = VALUES(order_num),
  perms = VALUES(perms),
  remark = VALUES(remark),
  update_time = NOW();
/

INSERT INTO sys_role_menu (role_id, menu_id) SELECT 1, 2310 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE role_id = 1 AND menu_id = 2310);
/

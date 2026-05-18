-- 单独执行：HIS 计费自动处理设置页（系统设置或系统管理下）
SET @hc_billing_setting_parent := COALESCE(
  (SELECT menu_id FROM sys_menu WHERE menu_name = '系统设置' AND menu_type = 'M' ORDER BY menu_id LIMIT 1),
  1
);

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
) SELECT
  3611, 'HIS计费自动处理', @hc_billing_setting_parent,
  (SELECT IFNULL(MAX(sm.order_num), 0) + 1 FROM sys_menu sm WHERE sm.parent_id = @hc_billing_setting_parent),
  'billingSetting', 'material/system/billingSetting/index', NULL,
  1, 0, 'C', '0', '0', 'department:patientCharge:billingTenantSetting', 'switch',
  'admin', NOW(), '1', NOW(), '抓取后自动低值消耗/自动退费',
  '0', '1'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'C' AND component = 'material/system/billingSetting/index')
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  component = VALUES(component),
  perms = VALUES(perms),
  update_time = NOW();

-- 为 super 岗位授权（按需调整 role_id）
-- INSERT IGNORE INTO sys_role_menu (role_id, menu_id) SELECT r.role_id, 3611 FROM sys_role r WHERE r.role_key IN ('super', 'admin');

-- 单独执行：众阳 HIS 接口联调页（枣强租户；hidden 路由，可从科室维护页跳转）
SET @hc_msun_probe_parent := COALESCE(
  (SELECT menu_id FROM sys_menu WHERE menu_name = '基础资料' AND menu_type = 'M' ORDER BY menu_id LIMIT 1),
  (SELECT menu_id FROM sys_menu WHERE menu_name = '系统管理' AND menu_type = 'M' ORDER BY menu_id LIMIT 1),
  1
);

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
) SELECT
  3612, '众阳HIS接口联调', @hc_msun_probe_parent,
  (SELECT IFNULL(MAX(sm.order_num), 0) + 1 FROM sys_menu sm WHERE sm.parent_id = @hc_msun_probe_parent),
  'msunProbe', 'foundation/msunProbe/index', NULL,
  1, 0, 'C', '0', '0', 'foundation:depart:list', 'link',
  'admin', NOW(), '1', NOW(), 'SPD 后端代理众阳 HIS 联调（勿直连 scminterface）',
  '0', '1'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'C' AND component = 'foundation/msunProbe/index')
ON DUPLICATE KEY UPDATE
  menu_name = VALUES(menu_name),
  parent_id = VALUES(parent_id),
  component = VALUES(component),
  perms = VALUES(perms),
  update_time = NOW();

-- 为 super 岗位授权（按需调整 role_id）
-- INSERT IGNORE INTO sys_role_menu (role_id, menu_id) SELECT r.role_id, 3612 FROM sys_role r WHERE r.role_key IN ('super', 'admin');

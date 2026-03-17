-- 数据完整性检查 / 测试数据初始化

-- 5. sb_role_menu 已有数据回填 customer_id（从 sb_role 按 role_id 带出）
UPDATE sb_role_menu rm
INNER JOIN sb_role r ON rm.role_id = r.role_id
SET rm.customer_id = r.customer_id;
/

-- 6. 确保平台设备管理员角色拥有「客户菜单功能管理」菜单（解决 客户菜单功能管理未找到）
INSERT INTO sb_role_menu (role_id, menu_id, customer_id)
SELECT '01900000-0000-7000-8000-000000000001', '01900000-0000-7000-8000-000000001457', NULL
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sb_role_menu WHERE role_id = '01900000-0000-7000-8000-000000000001' AND menu_id = '01900000-0000-7000-8000-000000001457');
/
INSERT INTO sb_role_menu (role_id, menu_id, customer_id)
SELECT '01900000-0000-7000-8000-000000000001', '01900000-0000-7000-8000-00000000cb5e', NULL
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sb_role_menu WHERE role_id = '01900000-0000-7000-8000-000000000001' AND menu_id = '01900000-0000-7000-8000-00000000cb5e');
/
INSERT INTO sb_role_menu (role_id, menu_id, customer_id)
SELECT '01900000-0000-7000-8000-000000000001', '01900000-0000-7000-8000-00000000cb5f', NULL
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sb_role_menu WHERE role_id = '01900000-0000-7000-8000-000000000001' AND menu_id = '01900000-0000-7000-8000-00000000cb5f');
/
INSERT INTO sb_role_menu (role_id, menu_id, customer_id)
SELECT '01900000-0000-7000-8000-000000000001', '01900000-0000-7000-8000-00000000cb60', NULL
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sb_role_menu WHERE role_id = '01900000-0000-7000-8000-000000000001' AND menu_id = '01900000-0000-7000-8000-00000000cb60');
/

-- 7. 将全部菜单权限赋给 admin 用户（平台管理员，假定 user_id=1；用户权限表作为菜单权限数据源）
INSERT INTO sb_user_permission_menu (id, user_id, customer_id, menu_id, create_by, create_time)
SELECT UUID(), 1, '', m.menu_id, 'admin', NOW()
FROM sb_menu m
WHERE m.status = '0' AND (m.delete_time IS NULL)
ON DUPLICATE KEY UPDATE delete_by = NULL, delete_time = NULL, del_flag = '0';
/

-- 8. 为 super_01 赋权：取对应工作组的菜单权限写入用户权限表（每个租户的 super_01 同步其 super 组菜单）
INSERT INTO sb_user_permission_menu (id, user_id, customer_id, menu_id, create_by, create_time)
SELECT UUID(), u.user_id, wgm.customer_id, wgm.menu_id, 'admin', NOW()
FROM sb_work_group_user wgu
INNER JOIN sb_work_group g ON g.group_id = wgu.group_id AND g.group_key = 'super' AND (g.delete_time IS NULL)
INNER JOIN sb_work_group_menu wgm ON wgm.group_id = wgu.group_id AND IFNULL(wgm.del_flag,'0') = '0'
INNER JOIN sys_user u ON u.user_id = wgu.user_id AND u.user_name = 'super_01' AND IFNULL(u.del_flag,'0') = '0'
WHERE IFNULL(wgu.del_flag, '0') = '0'
ON DUPLICATE KEY UPDATE delete_by = NULL, delete_time = NULL, del_flag = '0';
/

-- 9. 为 user_id=917 授予「科室新增」按钮权限（foundation:depart:add），解决科室维护页看不到新增按钮
INSERT INTO sb_user_permission_menu (id, user_id, customer_id, menu_id, create_by, create_time)
SELECT UUID(), 917, '', '01900000-0000-7000-8000-00000000c76b', 'admin', NOW() FROM DUAL
ON DUPLICATE KEY UPDATE delete_by = NULL, delete_time = NULL, del_flag = '0';
/

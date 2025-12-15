-- 添加货位维护菜单
-- 一级菜单：基础资料
-- 注意：需要先查询基础资料菜单的menu_id，然后替换下面的parent_id

-- 查询基础资料菜单ID
-- SELECT menu_id FROM sys_menu WHERE menu_name = '基础资料' AND menu_type = 'M';

-- 添加货位维护菜单（二级菜单）
-- 如果基础资料菜单ID不是2000，请修改下面的parent_id
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 2050, '货位维护', menu_id, 5, 'location', 'foundation/location/index', 0, 0, 'C', '0', '0', 'foundation:location:view', 'tree', 'admin', NOW(), '', NULL, '货位维护菜单'
FROM sys_menu WHERE menu_name = '基础资料' AND menu_type = 'M' LIMIT 1;

-- 添加货位维护按钮权限
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES 
(20501, '货位查询', 2050, 1, '', '', 0, 0, 'F', '0', '0', 'foundation:location:list', '#', 'admin', NOW(), '', NULL, ''),
(20502, '货位新增', 2050, 2, '', '', 0, 0, 'F', '0', '0', 'foundation:location:add', '#', 'admin', NOW(), '', NULL, ''),
(20503, '货位修改', 2050, 3, '', '', 0, 0, 'F', '0', '0', 'foundation:location:edit', '#', 'admin', NOW(), '', NULL, ''),
(20504, '货位删除', 2050, 4, '', '', 0, 0, 'F', '0', '0', 'foundation:location:remove', '#', 'admin', NOW(), '', NULL, ''),
(20505, '货位导出', 2050, 5, '', '', 0, 0, 'F', '0', '0', 'foundation:location:export', '#', 'admin', NOW(), '', NULL, '');


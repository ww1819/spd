-- 科室领用明细菜单配置和权限SQL

-- 1. 更新现有菜单（menu_id=1563）
UPDATE sys_menu 
SET 
    path = 'consumeDetail',
    component = 'department/consumeDetail/index',
    menu_type = 'C',
    perms = 'department:consumeDetail:list',
    icon = 'fa fa-list-alt'
WHERE menu_id = 1563;

-- 2. 添加按钮权限
-- 科室领用明细查询
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES ('科室领用明细查询', 1563, 1, '#', '', 1, 0, 'F', '0', '0', 'department:consumeDetail:list', '#', 'admin', sysdate(), '', NULL, '');

-- 科室领用明细导出
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES ('科室领用明细导出', 1563, 2, '#', '', 1, 0, 'F', '0', '0', 'department:consumeDetail:export', '#', 'admin', sysdate(), '', NULL, '');

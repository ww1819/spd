-- ============================================
-- 添加68分类菜单
-- 数据库：aspt
-- 父菜单：基础资料（menu_id: 1071）
-- ============================================

-- 添加68分类菜单（二级菜单）
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 1552, '68分类', menu_id, 11, 'category68', 'foundation/category68/index', 0, 0, 'C', '0', '0', 'foundation:category68:list', 'nested', 'admin', NOW(), '', NULL, '68分类菜单'
FROM sys_menu WHERE menu_name = '基础资料' AND menu_type = 'M' LIMIT 1;

-- 添加68分类按钮权限
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES 
(15521, '68分类查询', 1552, 1, '', '', 0, 0, 'F', '0', '0', 'foundation:category68:list', '#', 'admin', NOW(), '', NULL, ''),
(15522, '68分类新增', 1552, 2, '', '', 0, 0, 'F', '0', '0', 'foundation:category68:add', '#', 'admin', NOW(), '', NULL, ''),
(15523, '68分类修改', 1552, 3, '', '', 0, 0, 'F', '0', '0', 'foundation:category68:edit', '#', 'admin', NOW(), '', NULL, ''),
(15524, '68分类删除', 1552, 4, '', '', 0, 0, 'F', '0', '0', 'foundation:category68:remove', '#', 'admin', NOW(), '', NULL, ''),
(15525, '68分类导出', 1552, 5, '', '', 0, 0, 'F', '0', '0', 'foundation:category68:export', '#', 'admin', NOW(), '', NULL, '');

-- 验证菜单是否添加成功
SELECT 
    menu_id AS '菜单ID',
    menu_name AS '菜单名称',
    parent_id AS '父菜单ID',
    order_num AS '显示排序',
    path AS '路由地址',
    component AS '组件路径',
    perms AS '权限字符',
    icon AS '菜单图标',
    menu_type AS '菜单类型(M目录C菜单F按钮)',
    visible AS '显示状态(0显示1隐藏)',
    status AS '菜单状态(0正常1停用)'
FROM sys_menu 
WHERE menu_id IN (1552, 15521, 15522, 15523, 15524, 15525)
ORDER BY menu_id;


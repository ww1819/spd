-- ============================================
-- 添加备货查询导出权限按钮
-- 菜单ID: 1529 (备货查询)
-- ============================================

-- 检查是否已存在导出权限按钮
SELECT menu_id, menu_name, parent_id, perms 
FROM sys_menu 
WHERE parent_id = 1529 AND perms LIKE '%export%';

-- 添加导出权限按钮（如果不存在）
INSERT INTO sys_menu (
    menu_id, menu_name, parent_id, order_num, path, component, 
    is_frame, is_cache, menu_type, visible, status, perms, 
    icon, create_by, create_time, update_by, update_time, remark
) 
SELECT 
    15291, '备货查询导出', 1529, 1, '#', '', 
    1, 0, 'F', '0', '0', 'gz:stockQuery:export', 
    '#', 'admin', NOW(), '', NULL, '备货查询导出按钮'
WHERE NOT EXISTS (
    SELECT 1 FROM sys_menu WHERE menu_id = 15291
);

-- 验证添加结果
SELECT 
    menu_id AS '菜单ID',
    menu_name AS '菜单名称',
    parent_id AS '父菜单ID',
    perms AS '权限字符',
    menu_type AS '菜单类型',
    visible AS '显示状态',
    status AS '菜单状态'
FROM sys_menu 
WHERE menu_id = 15291 OR parent_id = 1529
ORDER BY menu_id;

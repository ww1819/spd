-- ============================================
-- 添加定数监测菜单
-- ============================================

-- 查找监测或库存相关的父菜单ID（作为参考，用于确定定数监测菜单的父菜单ID）
-- SELECT menu_id, menu_name, parent_id FROM sys_menu WHERE menu_name LIKE '%监测%' OR menu_name LIKE '%库存%' OR menu_name LIKE '%仓库%';

-- 定数监测菜单（菜单类型：C）
-- 注意：menu_id需要确保不冲突，建议使用较大的ID，如4001
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (4001, '定数监测', 0, 1, 'monitoring/fixedNumber', 'monitoring/fixedNumber/index', 0, 0, 'C', '0', '0', 'monitoring:fixedNumber:list', 'fa fa-line-chart', 'admin', NOW(), '', NULL, '定数监测菜单');

-- 定数监测按钮权限
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES 
(40011, '定数监测查询', 4001, 1, '#', '', 1, 0, 'F', '0', '0', 'monitoring:fixedNumber:list', '#', 'admin', NOW(), '', NULL, ''),
(40012, '定数监测导出', 4001, 2, '#', '', 1, 0, 'F', '0', '0', 'monitoring:fixedNumber:export', '#', 'admin', NOW(), '', NULL, '');

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
WHERE menu_id IN (4001, 40011, 40012)
ORDER BY menu_id;

-- ============================================
-- 注意：
-- 1. parent_id 需要根据实际的菜单结构调整（如果定数监测应该在某个父菜单下，需要修改parent_id）
-- 2. menu_id 需要确保不与其他菜单ID冲突
-- 3. 执行完SQL后，需要在系统管理->菜单管理中调整菜单的父菜单和显示顺序
-- ============================================


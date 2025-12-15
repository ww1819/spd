-- ============================================
-- 添加结算申请和结算审核菜单
-- ============================================
-- 结算申请菜单（假设父菜单ID需要根据实际情况调整）
-- 结算审核菜单（假设父菜单ID需要根据实际情况调整）
-- ============================================

-- 查找入库管理菜单ID（作为参考，用于确定结算菜单的父菜单ID）
-- SELECT menu_id, menu_name, parent_id FROM sys_menu WHERE menu_name LIKE '%入库%' OR menu_name LIKE '%到货%';

-- 假设入库管理的父菜单ID为某个值，这里需要根据实际情况调整
-- 如果入库申请和入库审核在同一个父菜单下，结算申请和结算审核也应该在同一个父菜单下

-- 结算申请菜单（菜单类型：C）
-- 注意：menu_id需要确保不冲突，建议使用较大的ID，如3001
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (3001, '结算申请', 0, 1, 'settlement/apply', 'settlement/apply/index', 0, 0, 'C', '0', '0', 'settlement:apply:list', 'fa fa-money', 'admin', NOW(), '', NULL, '结算申请菜单');

-- 结算申请按钮权限
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES 
(30011, '结算申请查询', 3001, 1, '#', '', 1, 0, 'F', '0', '0', 'settlement:apply:list', '#', 'admin', NOW(), '', NULL, ''),
(30012, '结算申请新增', 3001, 2, '#', '', 1, 0, 'F', '0', '0', 'settlement:apply:add', '#', 'admin', NOW(), '', NULL, ''),
(30013, '结算申请修改', 3001, 3, '#', '', 1, 0, 'F', '0', '0', 'settlement:apply:edit', '#', 'admin', NOW(), '', NULL, ''),
(30014, '结算申请删除', 3001, 4, '#', '', 1, 0, 'F', '0', '0', 'settlement:apply:remove', '#', 'admin', NOW(), '', NULL, ''),
(30015, '结算申请导出', 3001, 5, '#', '', 1, 0, 'F', '0', '0', 'settlement:apply:export', '#', 'admin', NOW(), '', NULL, ''),
(30016, '结算申请查看', 3001, 6, '#', '', 1, 0, 'F', '0', '0', 'settlement:apply:query', '#', 'admin', NOW(), '', NULL, '');

-- 结算审核菜单（菜单类型：C）
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (3002, '结算审核', 0, 2, 'settlement/audit', 'settlement/audit/index', 0, 0, 'C', '0', '0', 'settlement:audit:list', 'fa fa-check-square-o', 'admin', NOW(), '', NULL, '结算审核菜单');

-- 结算审核按钮权限
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES 
(30021, '结算审核查询', 3002, 1, '#', '', 1, 0, 'F', '0', '0', 'settlement:audit:list', '#', 'admin', NOW(), '', NULL, ''),
(30022, '结算审核修改', 3002, 2, '#', '', 1, 0, 'F', '0', '0', 'settlement:audit:edit', '#', 'admin', NOW(), '', NULL, ''),
(30023, '结算审核删除', 3002, 3, '#', '', 1, 0, 'F', '0', '0', 'settlement:audit:remove', '#', 'admin', NOW(), '', NULL, ''),
(30024, '结算审核导出', 3002, 4, '#', '', 1, 0, 'F', '0', '0', 'settlement:audit:export', '#', 'admin', NOW(), '', NULL, ''),
(30025, '结算审核查看', 3002, 5, '#', '', 1, 0, 'F', '0', '0', 'settlement:audit:query', '#', 'admin', NOW(), '', NULL, ''),
(30026, '结算审核审核', 3002, 6, '#', '', 1, 0, 'F', '0', '0', 'settlement:audit:audit', '#', 'admin', NOW(), '', NULL, '');

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
WHERE menu_id IN (3001, 3002, 30011, 30012, 30013, 30014, 30015, 30016, 30021, 30022, 30023, 30024, 30025, 30026)
ORDER BY menu_id;

-- ============================================
-- 注意：
-- 1. parent_id 需要根据实际的菜单结构调整（如果结算菜单应该在某个父菜单下，需要修改parent_id）
-- 2. menu_id 需要确保不与其他菜单ID冲突
-- 3. 执行完SQL后，需要在系统管理->菜单管理中调整菜单的父菜单和显示顺序
-- ============================================


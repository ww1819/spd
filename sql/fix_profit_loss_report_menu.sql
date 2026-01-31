-- 盈亏报表菜单修正：补全 path、component、perms，使点击可打开报表页
-- 执行后需刷新前端，在「盘点管理」下点击「盈亏报表」即可打开报表页面

UPDATE sys_menu
SET path = 'profitLossReport',
    component = 'warehouse/profitLossReport/index',
    perms = 'warehouse:profitLossReport:list',
    icon = 'chart',
    update_time = NOW()
WHERE menu_id = 1552;

-- 盈亏报表查询按钮（可选，如需按钮级权限可执行）
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT (SELECT IFNULL(MAX(menu_id), 0) + 1 FROM sys_menu),
    '盈亏报表查询', 1552, 1, '#', '', 1, 0, 'F', '0', '0', 'warehouse:profitLossReport:list', '#', 'admin', NOW(), ''
FROM sys_menu WHERE menu_id = 1552
AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE parent_id = 1552 AND perms = 'warehouse:profitLossReport:list');

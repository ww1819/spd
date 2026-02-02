-- 盈亏报表菜单完整配置修正
-- 确保所有字段都正确配置，使菜单可以正常打开
-- 执行后需刷新前端，在「盘点管理」下点击「盈亏报表」即可打开报表页面

UPDATE sys_menu
SET 
    path = 'profitLossReport',
    component = 'warehouse/profitLossReport/index',
    perms = 'warehouse:profitLoss:list',
    icon = 'chart',
    menu_type = 'C',
    is_frame = 1,
    is_cache = 0,
    visible = '0',
    status = '0',
    update_time = NOW()
WHERE menu_id = 1552;

-- 更新按钮权限（如果存在）
UPDATE sys_menu
SET 
    perms = 'warehouse:profitLoss:list',
    update_time = NOW()
WHERE parent_id = 1552 AND menu_type = 'F';

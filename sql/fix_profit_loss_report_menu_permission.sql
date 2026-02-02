-- 盈亏报表菜单权限修正：将权限改为与后端Controller一致的 warehouse:profitLoss:list
-- 执行后需刷新前端，在「盘点管理」下点击「盈亏报表」即可正常打开报表页面

UPDATE sys_menu
SET perms = 'warehouse:profitLoss:list',
    update_time = NOW()
WHERE menu_id = 1552;

-- 更新盈亏报表查询按钮权限
UPDATE sys_menu
SET perms = 'warehouse:profitLoss:list',
    update_time = NOW()
WHERE parent_id = 1552 AND perms = 'warehouse:profitLossReport:list';

-- 将重复菜单「财务分类」(menu_id=2290) 改为「18类重点耗材维护」
-- 保留「财务分类维护」(menu_id=1104) 不变

UPDATE sys_menu
SET menu_name = '18类重点耗材维护',
    path = 'focus18',
    component = 'foundation/focus18/index',
    perms = 'foundation:focus18:list',
    icon = 'list',
    remark = '18类重点耗材字典维护',
    update_by = '1',
    update_time = NOW()
WHERE menu_id = 2290;

UPDATE sys_menu SET menu_name = '18类重点查询', perms = 'foundation:focus18:query', update_time = NOW() WHERE menu_id = 2291;
UPDATE sys_menu SET menu_name = '18类重点新增', perms = 'foundation:focus18:add', update_time = NOW() WHERE menu_id = 2292;
UPDATE sys_menu SET menu_name = '18类重点修改', perms = 'foundation:focus18:edit', update_time = NOW() WHERE menu_id = 2293;
UPDATE sys_menu SET menu_name = '18类重点删除', perms = 'foundation:focus18:remove', update_time = NOW() WHERE menu_id = 2294;
UPDATE sys_menu SET menu_name = '18类重点导出', perms = 'foundation:focus18:export', update_time = NOW() WHERE menu_id = 2295;

-- 原财务分类专属按钮不再属于本菜单
UPDATE sys_menu SET status = '1', visible = '1', update_time = NOW() WHERE menu_id IN (2296, 2297);

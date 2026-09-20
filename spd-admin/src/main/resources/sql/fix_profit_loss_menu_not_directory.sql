-- 盘点管理：纠正「盈亏处理」误标为目录(M)，避免「期初*」等按钮出现在京东式菜单
-- 根因：menu_id=1239 类型为 M，getRouters 会把其子级 F 按钮编入路由；mega 菜单再展平显示
-- 处理：1239 改为页面 C；按钮仍挂其下但不再进侧栏/分栏；隐藏重复页面 3892

-- 1) 旧「盈亏处理」改为菜单页
UPDATE sys_menu
SET menu_type = 'C',
    path = 'profitLoss',
    component = 'warehouse/profitLoss/index',
    perms = 'warehouse:profitLoss:list',
    icon = IFNULL(NULLIF(icon, ''), 'lifebuoy'),
    update_by = '1',
    update_time = NOW()
WHERE menu_id = 1239
  AND menu_type = 'M';

-- 2) 按钮名称与现网盈亏单按钮对齐（仍为 F，不进菜单路由）
UPDATE sys_menu SET menu_name = '盈亏单查询', path = '#', update_by = '1', update_time = NOW()
WHERE menu_id = 1241 AND menu_type = 'F';
UPDATE sys_menu SET menu_name = '盈亏单新增', path = '#', update_by = '1', update_time = NOW()
WHERE menu_id = 1242 AND menu_type = 'F';
UPDATE sys_menu SET menu_name = '盈亏单修改', path = '#', update_by = '1', update_time = NOW()
WHERE menu_id = 1243 AND menu_type = 'F';
UPDATE sys_menu SET menu_name = '盈亏单删除', path = '#', update_by = '1', update_time = NOW()
WHERE menu_id = 1244 AND menu_type = 'F';
UPDATE sys_menu SET menu_name = '盈亏单导出', path = '#', update_by = '1', update_time = NOW()
WHERE menu_id = 1245 AND menu_type = 'F';
UPDATE sys_menu SET menu_name = '盈亏单审核', path = '#', update_by = '1', update_time = NOW()
WHERE menu_id = 1246 AND menu_type = 'F';

-- 3) 隐藏后补的重复「盈亏处理」页及其按钮（权限已由 1239/1241–1246 覆盖）
UPDATE sys_menu
SET visible = '1',
    update_by = '1',
    update_time = NOW()
WHERE menu_id = 3892
   OR parent_id = 3892;

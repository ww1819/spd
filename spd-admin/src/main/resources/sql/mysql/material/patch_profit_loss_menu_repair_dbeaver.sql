-- 盈亏处理菜单修复（DBeaver 生产环境专用，无 @变量，可从上到下逐条 Ctrl+Enter）
-- 1) 先改库名；2) 左侧双击选中 aspt；3) 每条从分号到分号整段选中再执行
-- 执行后重新登录

USE aspt;

-- ① 误配为目录(M)的「盈亏处理」改为页面菜单(C)（若尚无正确 C 菜单）
UPDATE sys_menu m
SET m.menu_type = 'C',
    m.parent_id = COALESCE(
      (SELECT pid FROM (SELECT menu_id AS pid FROM sys_menu WHERE menu_name = '盘点管理' AND menu_type = 'M' ORDER BY menu_id LIMIT 1) t),
      1070
    ),
    m.path = 'profitLoss',
    m.component = 'warehouse/profitLoss/index',
    m.perms = 'warehouse:profitLoss:list',
    m.icon = COALESCE(NULLIF(TRIM(m.icon), ''), 'lifebuoy'),
    m.visible = '0',
    m.status = '0',
    m.update_by = '1',
    m.update_time = NOW(),
    m.remark = '仓库盈亏单 /warehouse/profitLoss'
WHERE m.menu_name = '盈亏处理'
  AND m.menu_type = 'M'
  AND NOT EXISTS (
    SELECT 1 FROM sys_menu WHERE menu_type = 'C' AND component = 'warehouse/profitLoss/index'
  );

-- ② 仍无 C 菜单则新建（INSERT 与 SELECT 必须一起选中执行，不能只选 INSERT 表头）
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT (SELECT IFNULL(MAX(menu_id), 0) + 1 FROM (SELECT menu_id FROM sys_menu) t), '盈亏处理',
  COALESCE((SELECT pid FROM (SELECT menu_id AS pid FROM sys_menu WHERE menu_name = '盘点管理' AND menu_type = 'M' ORDER BY menu_id LIMIT 1) t2), 1070),
  (SELECT IFNULL(MAX(order_num), 0) + 1 FROM sys_menu sm WHERE sm.parent_id = COALESCE((SELECT pid FROM (SELECT menu_id AS pid FROM sys_menu WHERE menu_name = '盘点管理' AND menu_type = 'M' ORDER BY menu_id LIMIT 1) t3), 1070)),
  'profitLoss', 'warehouse/profitLoss/index', NULL, 1, 0, 'C', '0', '0', 'warehouse:profitLoss:list', 'lifebuoy',
  'admin', NOW(), '1', NOW(), '仓库盈亏单 /warehouse/profitLoss', '0', '1'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'C' AND component = 'warehouse/profitLoss/index');

-- ③ 统一 C 菜单字段
UPDATE sys_menu m
SET m.menu_name = '盈亏处理',
    m.parent_id = COALESCE((SELECT pid FROM (SELECT menu_id AS pid FROM sys_menu WHERE menu_name = '盘点管理' AND menu_type = 'M' ORDER BY menu_id LIMIT 1) t), 1070),
    m.path = 'profitLoss',
    m.component = 'warehouse/profitLoss/index',
    m.perms = 'warehouse:profitLoss:list',
    m.menu_type = 'C',
    m.visible = '0',
    m.status = '0',
    m.update_by = '1',
    m.update_time = NOW()
WHERE m.menu_type = 'C' AND m.component = 'warehouse/profitLoss/index';

-- ④ 多余 M 目录：子项挂到 C 后删除 M
UPDATE sys_menu child
INNER JOIN sys_menu dir ON dir.menu_name = '盈亏处理' AND dir.menu_type = 'M'
INNER JOIN sys_menu c ON c.menu_type = 'C' AND c.component = 'warehouse/profitLoss/index' AND c.menu_id <> dir.menu_id
SET child.parent_id = c.menu_id, child.menu_type = 'F', child.path = '#', child.component = '', child.visible = '0', child.update_by = '1', child.update_time = NOW()
WHERE child.parent_id = dir.menu_id;

DELETE dir FROM sys_menu dir
INNER JOIN sys_menu c ON c.menu_type = 'C' AND c.component = 'warehouse/profitLoss/index' AND c.menu_id <> dir.menu_id
WHERE dir.menu_name = '盈亏处理' AND dir.menu_type = 'M';

-- ⑤ 期初* 误名子项 → 盈亏单按钮权限
UPDATE sys_menu SET menu_type = 'F', path = '#', component = '', visible = '0', perms = 'warehouse:profitLoss:query', menu_name = '盈亏单查询', update_by = '1', update_time = NOW()
WHERE parent_id = (SELECT mid FROM (SELECT menu_id AS mid FROM sys_menu WHERE menu_type = 'C' AND component = 'warehouse/profitLoss/index' ORDER BY menu_id LIMIT 1) t) AND menu_name = '期初查询';

UPDATE sys_menu SET menu_type = 'F', path = '#', component = '', visible = '0', perms = 'warehouse:profitLoss:add', menu_name = '盈亏单新增', update_by = '1', update_time = NOW()
WHERE parent_id = (SELECT mid FROM (SELECT menu_id AS mid FROM sys_menu WHERE menu_type = 'C' AND component = 'warehouse/profitLoss/index' ORDER BY menu_id LIMIT 1) t) AND menu_name = '期初新增';

UPDATE sys_menu SET menu_type = 'F', path = '#', component = '', visible = '0', perms = 'warehouse:profitLoss:edit', menu_name = '盈亏单修改', update_by = '1', update_time = NOW()
WHERE parent_id = (SELECT mid FROM (SELECT menu_id AS mid FROM sys_menu WHERE menu_type = 'C' AND component = 'warehouse/profitLoss/index' ORDER BY menu_id LIMIT 1) t) AND menu_name = '期初修改';

UPDATE sys_menu SET menu_type = 'F', path = '#', component = '', visible = '0', perms = 'warehouse:profitLoss:remove', menu_name = '盈亏单删除', update_by = '1', update_time = NOW()
WHERE parent_id = (SELECT mid FROM (SELECT menu_id AS mid FROM sys_menu WHERE menu_type = 'C' AND component = 'warehouse/profitLoss/index' ORDER BY menu_id LIMIT 1) t) AND menu_name = '期初删除';

UPDATE sys_menu SET menu_type = 'F', path = '#', component = '', visible = '1', remark = '无对应后端权限，侧栏隐藏', update_by = '1', update_time = NOW()
WHERE parent_id = (SELECT mid FROM (SELECT menu_id AS mid FROM sys_menu WHERE menu_type = 'C' AND component = 'warehouse/profitLoss/index' ORDER BY menu_id LIMIT 1) t) AND menu_name = '期初导出';

UPDATE sys_menu SET menu_type = 'F', path = '#', component = '', visible = '0', perms = 'warehouse:profitLoss:audit', menu_name = '盈亏单审核', update_by = '1', update_time = NOW()
WHERE parent_id = (SELECT mid FROM (SELECT menu_id AS mid FROM sys_menu WHERE menu_type = 'C' AND component = 'warehouse/profitLoss/index' ORDER BY menu_id LIMIT 1) t) AND menu_name = '期初审核';

-- ⑥ 兜底：C 菜单下非 F 子节点一律改为 F
UPDATE sys_menu SET menu_type = 'F', path = '#', component = '', visible = '0', update_by = '1', update_time = NOW()
WHERE parent_id = (SELECT mid FROM (SELECT menu_id AS mid FROM sys_menu WHERE menu_type = 'C' AND component = 'warehouse/profitLoss/index' ORDER BY menu_id LIMIT 1) t)
  AND menu_type <> 'F';

-- ⑦ 补齐按钮权限（每条 INSERT…SELECT 整段一起执行）
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT (SELECT IFNULL(MAX(menu_id), 0) + 1 FROM (SELECT menu_id FROM sys_menu) t), '盈亏单查询',
  (SELECT mid FROM (SELECT menu_id AS mid FROM sys_menu WHERE menu_type = 'C' AND component = 'warehouse/profitLoss/index' ORDER BY menu_id LIMIT 1) p), 1, '#', '', NULL, 1, 0, 'F', '0', '0', 'warehouse:profitLoss:query', '#', 'admin', NOW(), '1', NOW(), 'GET /{id}', '0', '1'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sys_menu sm WHERE sm.parent_id = (SELECT mid FROM (SELECT menu_id AS mid FROM sys_menu WHERE menu_type = 'C' AND component = 'warehouse/profitLoss/index' ORDER BY menu_id LIMIT 1) p2) AND sm.perms = 'warehouse:profitLoss:query');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT (SELECT IFNULL(MAX(menu_id), 0) + 1 FROM (SELECT menu_id FROM sys_menu) t), '盈亏单新增',
  (SELECT mid FROM (SELECT menu_id AS mid FROM sys_menu WHERE menu_type = 'C' AND component = 'warehouse/profitLoss/index' ORDER BY menu_id LIMIT 1) p), 2, '#', '', NULL, 1, 0, 'F', '0', '0', 'warehouse:profitLoss:add', '#', 'admin', NOW(), '1', NOW(), 'POST', '0', '1'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sys_menu sm WHERE sm.parent_id = (SELECT mid FROM (SELECT menu_id AS mid FROM sys_menu WHERE menu_type = 'C' AND component = 'warehouse/profitLoss/index' ORDER BY menu_id LIMIT 1) p2) AND sm.perms = 'warehouse:profitLoss:add');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT (SELECT IFNULL(MAX(menu_id), 0) + 1 FROM (SELECT menu_id FROM sys_menu) t), '盈亏单修改',
  (SELECT mid FROM (SELECT menu_id AS mid FROM sys_menu WHERE menu_type = 'C' AND component = 'warehouse/profitLoss/index' ORDER BY menu_id LIMIT 1) p), 3, '#', '', NULL, 1, 0, 'F', '0', '0', 'warehouse:profitLoss:edit', '#', 'admin', NOW(), '1', NOW(), 'PUT', '0', '1'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sys_menu sm WHERE sm.parent_id = (SELECT mid FROM (SELECT menu_id AS mid FROM sys_menu WHERE menu_type = 'C' AND component = 'warehouse/profitLoss/index' ORDER BY menu_id LIMIT 1) p2) AND sm.perms = 'warehouse:profitLoss:edit');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT (SELECT IFNULL(MAX(menu_id), 0) + 1 FROM (SELECT menu_id FROM sys_menu) t), '盈亏单删除',
  (SELECT mid FROM (SELECT menu_id AS mid FROM sys_menu WHERE menu_type = 'C' AND component = 'warehouse/profitLoss/index' ORDER BY menu_id LIMIT 1) p), 4, '#', '', NULL, 1, 0, 'F', '0', '0', 'warehouse:profitLoss:remove', '#', 'admin', NOW(), '1', NOW(), 'DELETE', '0', '1'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sys_menu sm WHERE sm.parent_id = (SELECT mid FROM (SELECT menu_id AS mid FROM sys_menu WHERE menu_type = 'C' AND component = 'warehouse/profitLoss/index' ORDER BY menu_id LIMIT 1) p2) AND sm.perms = 'warehouse:profitLoss:remove');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT (SELECT IFNULL(MAX(menu_id), 0) + 1 FROM (SELECT menu_id FROM sys_menu) t), '盈亏单审核',
  (SELECT mid FROM (SELECT menu_id AS mid FROM sys_menu WHERE menu_type = 'C' AND component = 'warehouse/profitLoss/index' ORDER BY menu_id LIMIT 1) p), 5, '#', '', NULL, 1, 0, 'F', '0', '0', 'warehouse:profitLoss:audit', '#', 'admin', NOW(), '1', NOW(), 'PUT /audit/{id}', '0', '1'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sys_menu sm WHERE sm.parent_id = (SELECT mid FROM (SELECT menu_id AS mid FROM sys_menu WHERE menu_type = 'C' AND component = 'warehouse/profitLoss/index' ORDER BY menu_id LIMIT 1) p2) AND sm.perms = 'warehouse:profitLoss:audit');

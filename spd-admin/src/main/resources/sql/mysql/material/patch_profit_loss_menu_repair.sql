-- 修复「盈亏处理」侧栏显示为下拉目录（期初查询/新增…）的问题
-- 原因：menu_type=M 时，子节点（含 F 按钮）会进入侧栏子菜单；应为 C 页面菜单 + F 按钮权限
--
-- 【DBeaver 生产环境请用】patch_profit_loss_menu_repair_dbeaver.sql（无 @变量，可逐条执行）
-- 【本文件】含 SET @变量，须同一连接 Alt+X 整脚本执行，且先 USE aspt、选中数据库
-- 执行后请重新登录刷新路由

USE aspt;

SET @stk_parent := COALESCE(
  (SELECT menu_id FROM sys_menu WHERE menu_name = '盘点管理' AND menu_type = 'M' ORDER BY menu_id LIMIT 1),
  1070
);

-- 1) 已有 C 菜单（component 正确）
SET @profit_loss_c := (
  SELECT menu_id FROM sys_menu
  WHERE menu_type = 'C' AND component = 'warehouse/profitLoss/index'
  ORDER BY menu_id LIMIT 1
);

-- 2) 误配为目录的「盈亏处理」
SET @profit_loss_dir := (
  SELECT menu_id FROM sys_menu
  WHERE menu_name = '盈亏处理' AND menu_type = 'M'
  ORDER BY menu_id LIMIT 1
);

-- 2a) 无 C 但有 M：直接把 M 改成 C
UPDATE sys_menu
SET menu_type = 'C',
    parent_id = @stk_parent,
    path = 'profitLoss',
    component = 'warehouse/profitLoss/index',
    perms = 'warehouse:profitLoss:list',
    icon = COALESCE(NULLIF(TRIM(icon), ''), 'lifebuoy'),
    visible = '0',
    status = '0',
    update_by = '1',
    update_time = NOW(),
    remark = '仓库盈亏单 /warehouse/profitLoss'
WHERE @profit_loss_c IS NULL
  AND @profit_loss_dir IS NOT NULL
  AND menu_id = @profit_loss_dir;

SET @profit_loss_c := (
  SELECT menu_id FROM sys_menu
  WHERE menu_type = 'C' AND component = 'warehouse/profitLoss/index'
  ORDER BY menu_id LIMIT 1
);

-- 2b) 仍无 C：新建
INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  (SELECT IFNULL(MAX(menu_id), 0) + 1 FROM (SELECT menu_id FROM sys_menu) t),
  '盈亏处理',
  @stk_parent,
  (SELECT IFNULL(MAX(order_num), 0) + 1 FROM sys_menu WHERE parent_id = @stk_parent),
  'profitLoss',
  'warehouse/profitLoss/index',
  NULL,
  1, 0, 'C', '0', '0', 'warehouse:profitLoss:list', 'lifebuoy',
  'admin', NOW(), '1', NOW(), '仓库盈亏单 /warehouse/profitLoss',
  '0', '1'
FROM DUAL
WHERE @profit_loss_c IS NULL
  AND NOT EXISTS (
    SELECT 1 FROM sys_menu WHERE menu_type = 'C' AND component = 'warehouse/profitLoss/index'
  );

SET @profit_loss_c := (
  SELECT menu_id FROM sys_menu
  WHERE menu_type = 'C' AND component = 'warehouse/profitLoss/index'
  ORDER BY menu_id LIMIT 1
);

-- 2c) C 与 M 同时存在：子项归并到 C，删除多余 M
UPDATE sys_menu
SET parent_id = @profit_loss_c,
    menu_type = 'F',
    path = '#',
    component = '',
    visible = '0',
    update_by = '1',
    update_time = NOW()
WHERE @profit_loss_c IS NOT NULL
  AND @profit_loss_dir IS NOT NULL
  AND @profit_loss_dir <> @profit_loss_c
  AND parent_id = @profit_loss_dir;

DELETE FROM sys_menu
WHERE @profit_loss_c IS NOT NULL
  AND @profit_loss_dir IS NOT NULL
  AND @profit_loss_dir <> @profit_loss_c
  AND menu_id = @profit_loss_dir;

-- 3) 统一 C 菜单字段（名称、父级、路由）
UPDATE sys_menu
SET menu_name = '盈亏处理',
    parent_id = @stk_parent,
    path = 'profitLoss',
    component = 'warehouse/profitLoss/index',
    perms = 'warehouse:profitLoss:list',
    menu_type = 'C',
    visible = '0',
    status = '0',
    update_by = '1',
    update_time = NOW()
WHERE menu_id = @profit_loss_c;

-- 4) 误挂在盈亏处理下的「期初*」子项：改为 F 按钮并修正 perms（侧栏在 C 父级下不会再展示）
UPDATE sys_menu
SET menu_type = 'F',
    path = '#',
    component = '',
    visible = '0',
    parent_id = @profit_loss_c,
    perms = 'warehouse:profitLoss:query',
    menu_name = '盈亏单查询',
    update_by = '1',
    update_time = NOW()
WHERE @profit_loss_c IS NOT NULL
  AND parent_id = @profit_loss_c
  AND menu_name = '期初查询';

UPDATE sys_menu
SET menu_type = 'F',
    path = '#',
    component = '',
    visible = '0',
    parent_id = @profit_loss_c,
    perms = 'warehouse:profitLoss:add',
    menu_name = '盈亏单新增',
    update_by = '1',
    update_time = NOW()
WHERE @profit_loss_c IS NOT NULL
  AND parent_id = @profit_loss_c
  AND menu_name = '期初新增';

UPDATE sys_menu
SET menu_type = 'F',
    path = '#',
    component = '',
    visible = '0',
    parent_id = @profit_loss_c,
    perms = 'warehouse:profitLoss:edit',
    menu_name = '盈亏单修改',
    update_by = '1',
    update_time = NOW()
WHERE @profit_loss_c IS NOT NULL
  AND parent_id = @profit_loss_c
  AND menu_name = '期初修改';

UPDATE sys_menu
SET menu_type = 'F',
    path = '#',
    component = '',
    visible = '0',
    parent_id = @profit_loss_c,
    perms = 'warehouse:profitLoss:remove',
    menu_name = '盈亏单删除',
    update_by = '1',
    update_time = NOW()
WHERE @profit_loss_c IS NOT NULL
  AND parent_id = @profit_loss_c
  AND menu_name = '期初删除';

UPDATE sys_menu
SET menu_type = 'F',
    path = '#',
    component = '',
    visible = '1',
    parent_id = @profit_loss_c,
    menu_name = '期初导出',
    update_by = '1',
    update_time = NOW(),
    remark = '无对应后端权限，侧栏隐藏'
WHERE @profit_loss_c IS NOT NULL
  AND parent_id = @profit_loss_c
  AND menu_name = '期初导出';

UPDATE sys_menu
SET menu_type = 'F',
    path = '#',
    component = '',
    visible = '0',
    parent_id = @profit_loss_c,
    perms = 'warehouse:profitLoss:audit',
    menu_name = '盈亏单审核',
    update_by = '1',
    update_time = NOW()
WHERE @profit_loss_c IS NOT NULL
  AND parent_id = @profit_loss_c
  AND menu_name = '期初审核';

-- 5) 兜底：盈亏处理下仍为非 F 的子节点一律改为 F（避免再次出现在侧栏）
UPDATE sys_menu
SET menu_type = 'F',
    path = '#',
    component = '',
    visible = '0',
    update_by = '1',
    update_time = NOW()
WHERE @profit_loss_c IS NOT NULL
  AND parent_id = @profit_loss_c
  AND menu_type <> 'F';

-- 6) 补齐标准按钮权限（若不存在）
INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  (SELECT IFNULL(MAX(menu_id), 0) + 1 FROM (SELECT menu_id FROM sys_menu) t),
  '盈亏单查询', @profit_loss_c, 1, '#', '', NULL,
  1, 0, 'F', '0', '0', 'warehouse:profitLoss:query', '#',
  'admin', NOW(), '1', NOW(), 'GET /{id}',
  '0', '1'
FROM DUAL
WHERE @profit_loss_c IS NOT NULL
  AND NOT EXISTS (
    SELECT 1 FROM sys_menu WHERE parent_id = @profit_loss_c AND perms = 'warehouse:profitLoss:query'
  );

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  (SELECT IFNULL(MAX(menu_id), 0) + 1 FROM (SELECT menu_id FROM sys_menu) t),
  '盈亏单新增', @profit_loss_c, 2, '#', '', NULL,
  1, 0, 'F', '0', '0', 'warehouse:profitLoss:add', '#',
  'admin', NOW(), '1', NOW(), 'POST、loadDraft',
  '0', '1'
FROM DUAL
WHERE @profit_loss_c IS NOT NULL
  AND NOT EXISTS (
    SELECT 1 FROM sys_menu WHERE parent_id = @profit_loss_c AND perms = 'warehouse:profitLoss:add'
  );

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  (SELECT IFNULL(MAX(menu_id), 0) + 1 FROM (SELECT menu_id FROM sys_menu) t),
  '盈亏单修改', @profit_loss_c, 3, '#', '', NULL,
  1, 0, 'F', '0', '0', 'warehouse:profitLoss:edit', '#',
  'admin', NOW(), '1', NOW(), 'PUT',
  '0', '1'
FROM DUAL
WHERE @profit_loss_c IS NOT NULL
  AND NOT EXISTS (
    SELECT 1 FROM sys_menu WHERE parent_id = @profit_loss_c AND perms = 'warehouse:profitLoss:edit'
  );

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  (SELECT IFNULL(MAX(menu_id), 0) + 1 FROM (SELECT menu_id FROM sys_menu) t),
  '盈亏单删除', @profit_loss_c, 4, '#', '', NULL,
  1, 0, 'F', '0', '0', 'warehouse:profitLoss:remove', '#',
  'admin', NOW(), '1', NOW(), 'DELETE',
  '0', '1'
FROM DUAL
WHERE @profit_loss_c IS NOT NULL
  AND NOT EXISTS (
    SELECT 1 FROM sys_menu WHERE parent_id = @profit_loss_c AND perms = 'warehouse:profitLoss:remove'
  );

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  (SELECT IFNULL(MAX(menu_id), 0) + 1 FROM (SELECT menu_id FROM sys_menu) t),
  '盈亏单审核', @profit_loss_c, 5, '#', '', NULL,
  1, 0, 'F', '0', '0', 'warehouse:profitLoss:audit', '#',
  'admin', NOW(), '1', NOW(), 'PUT /audit/{id}',
  '0', '1'
FROM DUAL
WHERE @profit_loss_c IS NOT NULL
  AND NOT EXISTS (
    SELECT 1 FROM sys_menu WHERE parent_id = @profit_loss_c AND perms = 'warehouse:profitLoss:audit'
  );

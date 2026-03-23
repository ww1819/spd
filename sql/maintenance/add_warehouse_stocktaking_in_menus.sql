-- =============================================================================
-- 仓库盘点入库：sys_menu + hc_customer_menu（与 StkIoStocktakingController 一致）
-- 正式脚本已合并：spd-admin/src/main/resources/sql/mysql/material/menu.sql 文末「盘点入库」段
-- 存量库可单独执行本文件补菜单（与 menu.sql 段逻辑一致）
-- =============================================================================
-- 接口前缀：GET /stocktaking/in/list 等
-- 权限标识：stocktaking:in:list / query / export / add / edit / remove / audit
-- 前端页面：spd-ui/src/views/stocktaking/in/index.vue（v-hasPermi 已用 stocktaking:in:*）
-- 注意：与「科室盘点申请」department:stocktaking:*（DeptStocktakingController）不是同一套权限。
-- =============================================================================

SET @stk_parent := COALESCE(
  (SELECT menu_id FROM sys_menu WHERE menu_name = '盘点管理' AND menu_type = 'M' ORDER BY menu_id LIMIT 1),
  1070
);
/

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  (SELECT IFNULL(MAX(menu_id), 0) + 1 FROM (SELECT menu_id FROM sys_menu) t),
  '盘点入库',
  @stk_parent,
  (SELECT IFNULL(MAX(order_num), 0) + 1 FROM sys_menu WHERE parent_id = @stk_parent),
  'stkIn',
  'stocktaking/in/index',
  NULL,
  1, 0, 'C', '0', '0', 'stocktaking:in:list', 'form',
  'admin', NOW(), '1', NOW(), '仓库盘点单 StkIoStocktakingController /stocktaking/in',
  '0', '1'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'C' AND component = 'stocktaking/in/index');
/

SET @stk_in_id := (
  SELECT menu_id FROM sys_menu WHERE menu_type = 'C' AND component = 'stocktaking/in/index' ORDER BY menu_id DESC LIMIT 1
);
/

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT (SELECT IFNULL(MAX(menu_id), 0) + 1 FROM (SELECT menu_id FROM sys_menu) t), '盘点查询', @stk_in_id, 1, '#', '', NULL, 1, 0, 'F', '0', '0', 'stocktaking:in:query', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE @stk_in_id IS NOT NULL AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE parent_id = @stk_in_id AND perms = 'stocktaking:in:query');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT (SELECT IFNULL(MAX(menu_id), 0) + 1 FROM (SELECT menu_id FROM sys_menu) t), '盘点导出', @stk_in_id, 2, '#', '', NULL, 1, 0, 'F', '0', '0', 'stocktaking:in:export', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE @stk_in_id IS NOT NULL AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE parent_id = @stk_in_id AND perms = 'stocktaking:in:export');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT (SELECT IFNULL(MAX(menu_id), 0) + 1 FROM (SELECT menu_id FROM sys_menu) t), '盘点新增', @stk_in_id, 3, '#', '', NULL, 1, 0, 'F', '0', '0', 'stocktaking:in:add', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE @stk_in_id IS NOT NULL AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE parent_id = @stk_in_id AND perms = 'stocktaking:in:add');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT (SELECT IFNULL(MAX(menu_id), 0) + 1 FROM (SELECT menu_id FROM sys_menu) t), '盘点修改', @stk_in_id, 4, '#', '', NULL, 1, 0, 'F', '0', '0', 'stocktaking:in:edit', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE @stk_in_id IS NOT NULL AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE parent_id = @stk_in_id AND perms = 'stocktaking:in:edit');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT (SELECT IFNULL(MAX(menu_id), 0) + 1 FROM (SELECT menu_id FROM sys_menu) t), '盘点删除', @stk_in_id, 5, '#', '', NULL, 1, 0, 'F', '0', '0', 'stocktaking:in:remove', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE @stk_in_id IS NOT NULL AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE parent_id = @stk_in_id AND perms = 'stocktaking:in:remove');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT (SELECT IFNULL(MAX(menu_id), 0) + 1 FROM (SELECT menu_id FROM sys_menu) t), '盘点审核', @stk_in_id, 6, '#', '', NULL, 1, 0, 'F', '0', '0', 'stocktaking:in:audit', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE @stk_in_id IS NOT NULL AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE parent_id = @stk_in_id AND perms = 'stocktaking:in:audit');
/

INSERT INTO hc_customer_menu (tenant_id, menu_id, status, is_enabled, create_by, create_time)
SELECT c.customer_id, m.menu_id, '0', '1', 'admin', NOW()
FROM sb_customer c
JOIN sys_menu m
  ON m.perms IN (
    'stocktaking:in:list',
    'stocktaking:in:query',
    'stocktaking:in:export',
    'stocktaking:in:add',
    'stocktaking:in:edit',
    'stocktaking:in:remove',
    'stocktaking:in:audit'
  )
WHERE c.hc_status = '0'
  AND NOT EXISTS (
    SELECT 1 FROM hc_customer_menu h
    WHERE h.tenant_id = c.customer_id AND h.menu_id = m.menu_id
  );
/

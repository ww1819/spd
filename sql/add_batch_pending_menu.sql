-- ============================================
-- 批次追溯 + 盘盈待入账 明细菜单与权限
-- 目标权限：
--   warehouse:batch:*
--   warehouse:profitLossPending:*
-- 要求：默认对客户开放（default_open_to_customer=1 + 回填 hc_customer_menu）
-- ============================================

-- 1) 找父菜单：优先挂在“盈亏单(warehouse:profitLoss:list)”下，其次“盘点管理”
SET @parent_profit_loss := (
  SELECT menu_id
  FROM sys_menu
  WHERE perms = 'warehouse:profitLoss:list'
    AND menu_type = 'C'
  ORDER BY menu_id
  LIMIT 1
);

SET @parent_pd := (
  SELECT menu_id
  FROM sys_menu
  WHERE menu_name = '盘点管理'
    AND menu_type = 'M'
  ORDER BY menu_id
  LIMIT 1
);

SET @parent_id := IFNULL(@parent_profit_loss, IFNULL(@parent_pd, 1));

-- 2) 批次追溯主菜单（warehouse:batch:list）
INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  (SELECT IFNULL(MAX(menu_id), 0) + 1 FROM (SELECT menu_id FROM sys_menu) t),
  '批次追溯',
  @parent_id,
  (SELECT IFNULL(MAX(order_num), 0) + 1 FROM sys_menu WHERE parent_id = @parent_id),
  'batch',
  'warehouse/batch/index',
  NULL,
  1, 0, 'C', '0', '0', 'warehouse:batch:list', 'search',
  'admin', NOW(), '1', NOW(), '批次追溯独立查询',
  '0', '1'
FROM DUAL
WHERE NOT EXISTS (
  SELECT 1 FROM sys_menu WHERE perms = 'warehouse:batch:list' AND menu_type = 'C'
);

SET @batch_menu_id := (
  SELECT menu_id FROM sys_menu WHERE perms = 'warehouse:batch:list' AND menu_type = 'C' ORDER BY menu_id LIMIT 1
);

-- 2.1 批次按钮权限
INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  (SELECT IFNULL(MAX(menu_id), 0) + 1 FROM (SELECT menu_id FROM sys_menu) t),
  '批次查询',
  @batch_menu_id,
  1,
  '#', '', NULL,
  1, 0, 'F', '0', '0', 'warehouse:batch:query', '#',
  'admin', NOW(), '1', NOW(), '',
  '0', '1'
FROM DUAL
WHERE @batch_menu_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE parent_id = @batch_menu_id AND perms = 'warehouse:batch:query');

-- 3) 盘盈待入账主菜单（warehouse:profitLossPending:list）
INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  (SELECT IFNULL(MAX(menu_id), 0) + 1 FROM (SELECT menu_id FROM sys_menu) t),
  '盘盈待入账',
  @parent_id,
  (SELECT IFNULL(MAX(order_num), 0) + 1 FROM sys_menu WHERE parent_id = @parent_id),
  'profitLossPending',
  'warehouse/profitLossPending/index',
  NULL,
  1, 0, 'C', '0', '0', 'warehouse:profitLossPending:list', 'time',
  'admin', NOW(), '1', NOW(), '盘盈待入账明细查询与状态变更',
  '0', '1'
FROM DUAL
WHERE NOT EXISTS (
  SELECT 1 FROM sys_menu WHERE perms = 'warehouse:profitLossPending:list' AND menu_type = 'C'
);

SET @pending_menu_id := (
  SELECT menu_id FROM sys_menu WHERE perms = 'warehouse:profitLossPending:list' AND menu_type = 'C' ORDER BY menu_id LIMIT 1
);

-- 3.1 盘盈待入账按钮权限
INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  (SELECT IFNULL(MAX(menu_id), 0) + 1 FROM (SELECT menu_id FROM sys_menu) t),
  '待入账查询',
  @pending_menu_id,
  1,
  '#', '', NULL,
  1, 0, 'F', '0', '0', 'warehouse:profitLossPending:query', '#',
  'admin', NOW(), '1', NOW(), '',
  '0', '1'
FROM DUAL
WHERE @pending_menu_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE parent_id = @pending_menu_id AND perms = 'warehouse:profitLossPending:query');

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  (SELECT IFNULL(MAX(menu_id), 0) + 1 FROM (SELECT menu_id FROM sys_menu) t),
  '待入账状态变更',
  @pending_menu_id,
  2,
  '#', '', NULL,
  1, 0, 'F', '0', '0', 'warehouse:profitLossPending:edit', '#',
  'admin', NOW(), '1', NOW(), '',
  '0', '1'
FROM DUAL
WHERE @pending_menu_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE parent_id = @pending_menu_id AND perms = 'warehouse:profitLossPending:edit');

-- 4) 默认对客户开放：回填已有客户菜单授权（hc_customer_menu）
-- 仅回填状态正常的客户，避免污染已停用租户
INSERT INTO hc_customer_menu (tenant_id, menu_id, status, is_enabled, create_by, create_time)
SELECT c.customer_id, m.menu_id, '0', '1', 'admin', NOW()
FROM sb_customer c
JOIN sys_menu m
  ON m.perms IN (
    'warehouse:batch:list',
    'warehouse:batch:query',
    'warehouse:profitLossPending:list',
    'warehouse:profitLossPending:query',
    'warehouse:profitLossPending:edit'
  )
WHERE c.hc_status = '0'
  AND NOT EXISTS (
    SELECT 1
    FROM hc_customer_menu h
    WHERE h.tenant_id = c.customer_id
      AND h.menu_id = m.menu_id
  );


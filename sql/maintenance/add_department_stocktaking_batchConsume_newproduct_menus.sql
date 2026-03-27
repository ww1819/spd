-- 盘点申请、科室批量消耗、新品申购申请/审批：sys_menu 菜单与按钮 + hc_customer_menu 默认开放
-- 与后端 @PreAuthorize、spd-ui v-hasPermi 对齐
-- 新品申购申请/审批已并入 spd-admin/.../material/menu.sql 文末；本文件可单独补执行盘点/批量消耗等

SET @dept_parent := COALESCE(
  (SELECT m.menu_id FROM sys_menu m WHERE m.menu_type = 'M' AND m.path = 'department' ORDER BY m.menu_id LIMIT 1),
  (SELECT m.parent_id FROM sys_menu m WHERE m.menu_type = 'C' AND m.component LIKE 'department/%' ORDER BY m.menu_id LIMIT 1),
  1
);

-- ========== 科室盘点申请 department/stocktaking/index ==========
INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  (SELECT IFNULL(MAX(menu_id), 0) + 1 FROM (SELECT menu_id FROM sys_menu) t),
  '盘点申请',
  @dept_parent,
  (SELECT IFNULL(MAX(order_num), 0) + 1 FROM sys_menu WHERE parent_id = @dept_parent),
  'stocktaking',
  'department/stocktaking/index',
  NULL,
  1, 0, 'C', '0', '0', 'department:stocktaking:list', 'clipboard',
  'admin', NOW(), '1', NOW(), '科室盘点申请',
  '0', '1'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'C' AND component = 'department/stocktaking/index');

SET @st_menu := (SELECT menu_id FROM sys_menu WHERE component = 'department/stocktaking/index' AND menu_type = 'C' ORDER BY menu_id DESC LIMIT 1);

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT (SELECT IFNULL(MAX(menu_id), 0) + 1 FROM (SELECT menu_id FROM sys_menu) t), '盘点查询', @st_menu, 1, '#', '', NULL, 1, 0, 'F', '0', '0', 'department:stocktaking:query', '#', 'admin', NOW(), '1', NOW(), '', '0', '1'
FROM DUAL WHERE @st_menu IS NOT NULL AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE parent_id = @st_menu AND perms = 'department:stocktaking:query');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT (SELECT IFNULL(MAX(menu_id), 0) + 1 FROM (SELECT menu_id FROM sys_menu) t), '盘点新增', @st_menu, 2, '#', '', NULL, 1, 0, 'F', '0', '0', 'department:stocktaking:add', '#', 'admin', NOW(), '1', NOW(), '', '0', '1')
FROM DUAL WHERE @st_menu IS NOT NULL AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE parent_id = @st_menu AND perms = 'department:stocktaking:add');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT (SELECT IFNULL(MAX(menu_id), 0) + 1 FROM (SELECT menu_id FROM sys_menu) t), '盘点导出', @st_menu, 3, '#', '', NULL, 1, 0, 'F', '0', '0', 'department:stocktaking:export', '#', 'admin', NOW(), '1', NOW(), '', '0', '1')
FROM DUAL WHERE @st_menu IS NOT NULL AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE parent_id = @st_menu AND perms = 'department:stocktaking:export');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT (SELECT IFNULL(MAX(menu_id), 0) + 1 FROM (SELECT menu_id FROM sys_menu) t), '盘点修改', @st_menu, 4, '#', '', NULL, 1, 0, 'F', '0', '0', 'department:stocktaking:edit', '#', 'admin', NOW(), '1', NOW(), '', '0', '1')
FROM DUAL WHERE @st_menu IS NOT NULL AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE parent_id = @st_menu AND perms = 'department:stocktaking:edit');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT (SELECT IFNULL(MAX(menu_id), 0) + 1 FROM (SELECT menu_id FROM sys_menu) t), '盘点删除', @st_menu, 5, '#', '', NULL, 1, 0, 'F', '0', '0', 'department:stocktaking:remove', '#', 'admin', NOW(), '1', NOW(), '', '0', '1')
FROM DUAL WHERE @st_menu IS NOT NULL AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE parent_id = @st_menu AND perms = 'department:stocktaking:remove');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT (SELECT IFNULL(MAX(menu_id), 0) + 1 FROM (SELECT menu_id FROM sys_menu) t), '盘点审核', @st_menu, 6, '#', '', NULL, 1, 0, 'F', '0', '0', 'department:stocktaking:audit', '#', 'admin', NOW(), '1', NOW(), '', '0', '1')
FROM DUAL WHERE @st_menu IS NOT NULL AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE parent_id = @st_menu AND perms = 'department:stocktaking:audit');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT (SELECT IFNULL(MAX(menu_id), 0) + 1 FROM (SELECT menu_id FROM sys_menu) t), '盘点驳回', @st_menu, 7, '#', '', NULL, 1, 0, 'F', '0', '0', 'department:stocktaking:reject', '#', 'admin', NOW(), '1', NOW(), '', '0', '1')
FROM DUAL WHERE @st_menu IS NOT NULL AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE parent_id = @st_menu AND perms = 'department:stocktaking:reject');

-- ========== 科室批量消耗 department/batchConsume/index ==========
INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  (SELECT IFNULL(MAX(menu_id), 0) + 1 FROM (SELECT menu_id FROM sys_menu) t),
  '科室批量消耗',
  @dept_parent,
  (SELECT IFNULL(MAX(order_num), 0) + 1 FROM sys_menu WHERE parent_id = @dept_parent),
  'batchConsume',
  'department/batchConsume/index',
  NULL,
  1, 0, 'C', '0', '0', 'department:batchConsume:list', 'chart',
  'admin', NOW(), '1', NOW(), '科室批量消耗',
  '0', '1'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'C' AND component = 'department/batchConsume/index');

SET @bc_menu := (SELECT menu_id FROM sys_menu WHERE component = 'department/batchConsume/index' AND menu_type = 'C' ORDER BY menu_id DESC LIMIT 1);

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT (SELECT IFNULL(MAX(menu_id), 0) + 1 FROM (SELECT menu_id FROM sys_menu) t), '批量消耗查询', @bc_menu, 1, '#', '', NULL, 1, 0, 'F', '0', '0', 'department:batchConsume:query', '#', 'admin', NOW(), '1', NOW(), '', '0', '1')
FROM DUAL WHERE @bc_menu IS NOT NULL AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE parent_id = @bc_menu AND perms = 'department:batchConsume:query');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT (SELECT IFNULL(MAX(menu_id), 0) + 1 FROM (SELECT menu_id FROM sys_menu) t), '批量消耗新增', @bc_menu, 2, '#', '', NULL, 1, 0, 'F', '0', '0', 'department:batchConsume:add', '#', 'admin', NOW(), '1', NOW(), '', '0', '1')
FROM DUAL WHERE @bc_menu IS NOT NULL AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE parent_id = @bc_menu AND perms = 'department:batchConsume:add');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT (SELECT IFNULL(MAX(menu_id), 0) + 1 FROM (SELECT menu_id FROM sys_menu) t), '批量消耗导出', @bc_menu, 3, '#', '', NULL, 1, 0, 'F', '0', '0', 'department:batchConsume:export', '#', 'admin', NOW(), '1', NOW(), '', '0', '1')
FROM DUAL WHERE @bc_menu IS NOT NULL AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE parent_id = @bc_menu AND perms = 'department:batchConsume:export');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT (SELECT IFNULL(MAX(menu_id), 0) + 1 FROM (SELECT menu_id FROM sys_menu) t), '批量消耗修改', @bc_menu, 4, '#', '', NULL, 1, 0, 'F', '0', '0', 'department:batchConsume:edit', '#', 'admin', NOW(), '1', NOW(), '', '0', '1')
FROM DUAL WHERE @bc_menu IS NOT NULL AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE parent_id = @bc_menu AND perms = 'department:batchConsume:edit');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT (SELECT IFNULL(MAX(menu_id), 0) + 1 FROM (SELECT menu_id FROM sys_menu) t), '批量消耗删除', @bc_menu, 5, '#', '', NULL, 1, 0, 'F', '0', '0', 'department:batchConsume:remove', '#', 'admin', NOW(), '1', NOW(), '', '0', '1')
FROM DUAL WHERE @bc_menu IS NOT NULL AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE parent_id = @bc_menu AND perms = 'department:batchConsume:remove');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT (SELECT IFNULL(MAX(menu_id), 0) + 1 FROM (SELECT menu_id FROM sys_menu) t), '批量消耗审核', @bc_menu, 6, '#', '', NULL, 1, 0, 'F', '0', '0', 'department:batchConsume:audit', '#', 'admin', NOW(), '1', NOW(), '', '0', '1')
FROM DUAL WHERE @bc_menu IS NOT NULL AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE parent_id = @bc_menu AND perms = 'department:batchConsume:audit');

-- ========== 新品申购申请 department/newProductApply/index ==========
INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  (SELECT IFNULL(MAX(menu_id), 0) + 1 FROM (SELECT menu_id FROM sys_menu) t),
  '新品申购申请',
  @dept_parent,
  (SELECT IFNULL(MAX(order_num), 0) + 1 FROM sys_menu WHERE parent_id = @dept_parent),
  'newProductApply',
  'department/newProductApply/index',
  NULL,
  1, 0, 'C', '0', '0', 'department:newProductApply:list', 'edit',
  'admin', NOW(), '1', NOW(), '',
  '0', '1'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'C' AND component = 'department/newProductApply/index');

SET @npa_menu := (SELECT menu_id FROM sys_menu WHERE component = 'department/newProductApply/index' AND menu_type = 'C' ORDER BY menu_id DESC LIMIT 1);

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT (SELECT IFNULL(MAX(menu_id), 0) + 1 FROM (SELECT menu_id FROM sys_menu) t), '新品申购查询', @npa_menu, 1, '#', '', NULL, 1, 0, 'F', '0', '0', 'department:newProductApply:query', '#', 'admin', NOW(), '1', NOW(), '', '0', '1')
FROM DUAL WHERE @npa_menu IS NOT NULL AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE parent_id = @npa_menu AND perms = 'department:newProductApply:query');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT (SELECT IFNULL(MAX(menu_id), 0) + 1 FROM (SELECT menu_id FROM sys_menu) t), '新品申购新增', @npa_menu, 2, '#', '', NULL, 1, 0, 'F', '0', '0', 'department:newProductApply:add', '#', 'admin', NOW(), '1', NOW(), '', '0', '1')
FROM DUAL WHERE @npa_menu IS NOT NULL AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE parent_id = @npa_menu AND perms = 'department:newProductApply:add');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT (SELECT IFNULL(MAX(menu_id), 0) + 1 FROM (SELECT menu_id FROM sys_menu) t), '新品申购导出', @npa_menu, 3, '#', '', NULL, 1, 0, 'F', '0', '0', 'department:newProductApply:export', '#', 'admin', NOW(), '1', NOW(), '', '0', '1')
FROM DUAL WHERE @npa_menu IS NOT NULL AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE parent_id = @npa_menu AND perms = 'department:newProductApply:export');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT (SELECT IFNULL(MAX(menu_id), 0) + 1 FROM (SELECT menu_id FROM sys_menu) t), '新品申购修改', @npa_menu, 4, '#', '', NULL, 1, 0, 'F', '0', '0', 'department:newProductApply:edit', '#', 'admin', NOW(), '1', NOW(), '', '0', '1')
FROM DUAL WHERE @npa_menu IS NOT NULL AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE parent_id = @npa_menu AND perms = 'department:newProductApply:edit');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT (SELECT IFNULL(MAX(menu_id), 0) + 1 FROM (SELECT menu_id FROM sys_menu) t), '新品申购删除', @npa_menu, 5, '#', '', NULL, 1, 0, 'F', '0', '0', 'department:newProductApply:remove', '#', 'admin', NOW(), '1', NOW(), '', '0', '1')
FROM DUAL WHERE @npa_menu IS NOT NULL AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE parent_id = @npa_menu AND perms = 'department:newProductApply:remove');

-- ========== 新品申购审批 department/newProductAudit/index ==========
INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  (SELECT IFNULL(MAX(menu_id), 0) + 1 FROM (SELECT menu_id FROM sys_menu) t),
  '新品申购审批',
  @dept_parent,
  (SELECT IFNULL(MAX(order_num), 0) + 1 FROM sys_menu WHERE parent_id = @dept_parent),
  'newProductAudit',
  'department/newProductAudit/index',
  NULL,
  1, 0, 'C', '0', '0', 'department:newProductAudit:list', 'audit',
  'admin', NOW(), '1', NOW(), '',
  '0', '1'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_type = 'C' AND component = 'department/newProductAudit/index');

SET @npr_menu := (SELECT menu_id FROM sys_menu WHERE component = 'department/newProductAudit/index' AND menu_type = 'C' ORDER BY menu_id DESC LIMIT 1);

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT (SELECT IFNULL(MAX(menu_id), 0) + 1 FROM (SELECT menu_id FROM sys_menu) t), '新品审批查询', @npr_menu, 1, '#', '', NULL, 1, 0, 'F', '0', '0', 'department:newProductAudit:query', '#', 'admin', NOW(), '1', NOW(), '', '0', '1')
FROM DUAL WHERE @npr_menu IS NOT NULL AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE parent_id = @npr_menu AND perms = 'department:newProductAudit:query');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT (SELECT IFNULL(MAX(menu_id), 0) + 1 FROM (SELECT menu_id FROM sys_menu) t), '新品审批导出', @npr_menu, 2, '#', '', NULL, 1, 0, 'F', '0', '0', 'department:newProductAudit:export', '#', 'admin', NOW(), '1', NOW(), '', '0', '1')
FROM DUAL WHERE @npr_menu IS NOT NULL AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE parent_id = @npr_menu AND perms = 'department:newProductAudit:export');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT (SELECT IFNULL(MAX(menu_id), 0) + 1 FROM (SELECT menu_id FROM sys_menu) t), '新品审批通过', @npr_menu, 3, '#', '', NULL, 1, 0, 'F', '0', '0', 'department:newProductAudit:audit', '#', 'admin', NOW(), '1', NOW(), '', '0', '1')
FROM DUAL WHERE @npr_menu IS NOT NULL AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE parent_id = @npr_menu AND perms = 'department:newProductAudit:audit');
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, `query`, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark, is_platform, default_open_to_customer)
SELECT (SELECT IFNULL(MAX(menu_id), 0) + 1 FROM (SELECT menu_id FROM sys_menu) t), '新品审批驳回', @npr_menu, 4, '#', '', NULL, 1, 0, 'F', '0', '0', 'department:newProductAudit:reject', '#', 'admin', NOW(), '1', NOW(), '', '0', '1')
FROM DUAL WHERE @npr_menu IS NOT NULL AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE parent_id = @npr_menu AND perms = 'department:newProductAudit:reject');

-- 默认对客户开放
INSERT INTO hc_customer_menu (tenant_id, menu_id, status, is_enabled, create_by, create_time)
SELECT c.customer_id, m.menu_id, '0', '1', 'admin', NOW()
FROM sb_customer c
JOIN sys_menu m
  ON m.perms IN (
    'department:stocktaking:list', 'department:stocktaking:query', 'department:stocktaking:add', 'department:stocktaking:export',
    'department:stocktaking:edit', 'department:stocktaking:remove', 'department:stocktaking:audit', 'department:stocktaking:reject',
    'department:batchConsume:list', 'department:batchConsume:query', 'department:batchConsume:add', 'department:batchConsume:export',
    'department:batchConsume:edit', 'department:batchConsume:remove', 'department:batchConsume:audit',
    'department:newProductApply:list', 'department:newProductApply:query', 'department:newProductApply:add', 'department:newProductApply:export',
    'department:newProductApply:edit', 'department:newProductApply:remove',
    'department:newProductAudit:list', 'department:newProductAudit:query', 'department:newProductAudit:export',
    'department:newProductAudit:audit', 'department:newProductAudit:reject'
  )
WHERE c.hc_status = '0'
  AND NOT EXISTS (
    SELECT 1 FROM hc_customer_menu h
    WHERE h.tenant_id = c.customer_id AND h.menu_id = m.menu_id
  );

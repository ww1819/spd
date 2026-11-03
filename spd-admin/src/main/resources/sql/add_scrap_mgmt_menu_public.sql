-- 库房管理：盘点管理后增加「报损管理」分栏（申请 / 审批 / 查询）
-- 目录 3983；申请 3984；审批 3985；查询 3986
-- 顺序：… → 调拨(6) → 盘点(7) → 报损(8)

SET @wh_root := (
  SELECT m.menu_id FROM sys_menu m
  WHERE m.parent_id = 0 AND m.menu_type = 'M'
    AND (m.path = 'warehouse' OR m.menu_name = '库房管理')
    AND (m.visible = '0' OR m.visible IS NULL)
    AND (m.status = '0' OR m.status IS NULL)
  ORDER BY m.menu_id LIMIT 1
);
SET @stocktaking_dir := 1070;
SET @scrap_dir := 3983;
SET @scrap_apply := 3984;
SET @scrap_audit := 3985;
SET @scrap_query := 3986;

-- 0) 报损管理目录（挂在库房管理下，排在盘点管理后）
INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, query,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  @scrap_dir, '报损管理', @wh_root, 8, 'scrapMgmt', NULL, NULL,
  1, 0, 'M', '0', '0', '', 'delete',
  'admin', NOW(), '1', NOW(), '库房管理分栏：报损管理',
  '0', '1'
FROM DUAL
WHERE @wh_root IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = @scrap_dir);

UPDATE sys_menu
SET parent_id = @wh_root,
    order_num = 8,
    menu_name = '报损管理',
    path = 'scrapMgmt',
    component = NULL,
    menu_type = 'M',
    visible = '0',
    status = '0',
    perms = '',
    is_platform = '0',
    default_open_to_customer = '1',
    icon = IFNULL(NULLIF(icon, ''), 'delete'),
    remark = '库房管理分栏：报损管理',
    update_by = '1',
    update_time = NOW()
WHERE menu_id = @scrap_dir
  AND @wh_root IS NOT NULL;

-- 确保盘点管理仍在报废前
UPDATE sys_menu
SET order_num = 7,
    update_by = '1',
    update_time = NOW()
WHERE menu_id = @stocktaking_dir
  AND parent_id = @wh_root;

-- 1) 报损申请
INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, query,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  @scrap_apply, '报损申请', @scrap_dir, 1, 'scrapApply', 'warehouse/scrap/apply/index', NULL,
  1, 0, 'C', '0', '0', 'warehouse:scrap:apply', 'form',
  'admin', NOW(), '1', NOW(), '库房报损申请',
  '0', '1'
FROM DUAL
WHERE @wh_root IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = @scrap_apply);

UPDATE sys_menu
SET parent_id = @scrap_dir,
    order_num = 1,
    menu_name = '报损申请',
    path = 'scrapApply',
    component = 'warehouse/scrap/apply/index',
    menu_type = 'C',
    perms = 'warehouse:scrap:apply',
    visible = '0',
    status = '0',
    is_platform = '0',
    default_open_to_customer = '1',
    icon = IFNULL(NULLIF(icon, ''), 'form'),
    remark = '库房报损申请',
    update_by = '1',
    update_time = NOW()
WHERE menu_id = @scrap_apply;

-- 2) 报损审批
INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, query,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  @scrap_audit, '报损审批', @scrap_dir, 2, 'scrapAudit', 'warehouse/scrap/audit/index', NULL,
  1, 0, 'C', '0', '0', 'warehouse:scrap:audit', 'edit',
  'admin', NOW(), '1', NOW(), '库房报损审批',
  '0', '1'
FROM DUAL
WHERE @wh_root IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = @scrap_audit);

UPDATE sys_menu
SET parent_id = @scrap_dir,
    order_num = 2,
    menu_name = '报损审批',
    path = 'scrapAudit',
    component = 'warehouse/scrap/audit/index',
    menu_type = 'C',
    perms = 'warehouse:scrap:audit',
    visible = '0',
    status = '0',
    is_platform = '0',
    default_open_to_customer = '1',
    icon = IFNULL(NULLIF(icon, ''), 'edit'),
    remark = '库房报损审批',
    update_by = '1',
    update_time = NOW()
WHERE menu_id = @scrap_audit;

-- 3) 报损查询
INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, query,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  @scrap_query, '报损查询', @scrap_dir, 3, 'scrapQuery', 'warehouse/scrap/query/index', NULL,
  1, 0, 'C', '0', '0', 'warehouse:scrap:query', 'search',
  'admin', NOW(), '1', NOW(), '库房报损查询',
  '0', '1'
FROM DUAL
WHERE @wh_root IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = @scrap_query);

UPDATE sys_menu
SET parent_id = @scrap_dir,
    order_num = 3,
    menu_name = '报损查询',
    path = 'scrapQuery',
    component = 'warehouse/scrap/query/index',
    menu_type = 'C',
    perms = 'warehouse:scrap:query',
    visible = '0',
    status = '0',
    is_platform = '0',
    default_open_to_customer = '1',
    icon = IFNULL(NULLIF(icon, ''), 'search'),
    remark = '库房报损查询',
    update_by = '1',
    update_time = NOW()
WHERE menu_id = @scrap_query;

-- 4) 角色：有库房根/盘点/报损权限的，补报损目录与三页（公共）
INSERT IGNORE INTO sys_role_menu (role_id, menu_id)
SELECT DISTINCT rm.role_id, t.menu_id
FROM sys_role_menu rm
CROSS JOIN (
  SELECT @scrap_dir AS menu_id
  UNION ALL SELECT @scrap_apply
  UNION ALL SELECT @scrap_audit
  UNION ALL SELECT @scrap_query
) t
WHERE rm.menu_id IN (@wh_root, @stocktaking_dir, @scrap_dir)
   OR rm.menu_id IN (SELECT menu_id FROM (SELECT menu_id FROM sys_menu WHERE parent_id = @stocktaking_dir) x)
   OR rm.menu_id IN (SELECT menu_id FROM (SELECT menu_id FROM sys_menu WHERE parent_id = @wh_root) y)
   OR rm.role_id = 1;

-- 5) 客户开放（公共：全部已有客户菜单的租户 + 全部 sb_customer + 生产租户清单）
INSERT INTO hc_customer_menu (tenant_id, menu_id, status, is_enabled, create_by, create_time)
SELECT c.tenant_id, t.menu_id, '0', '1', 'admin', NOW()
FROM (
  SELECT DISTINCT tenant_id FROM hc_customer_menu WHERE tenant_id IS NOT NULL AND tenant_id <> ''
  UNION
  SELECT customer_id FROM sb_customer WHERE delete_time IS NULL AND customer_id IS NOT NULL AND customer_id <> ''
  UNION SELECT 'hengsui-third-001'
  UNION SELECT 'zaoqiang-tcm-001'
) c
CROSS JOIN (
  SELECT @scrap_dir AS menu_id
  UNION ALL SELECT @scrap_apply
  UNION ALL SELECT @scrap_audit
  UNION ALL SELECT @scrap_query
) t
WHERE NOT EXISTS (
  SELECT 1 FROM hc_customer_menu h
  WHERE h.tenant_id = c.tenant_id AND h.menu_id = t.menu_id
);

-- 6) 用户菜单（有库房/盘点权限即补；tenant 优先 um，其次用户 customer_id）
INSERT IGNORE INTO sys_user_menu (user_id, menu_id, tenant_id)
SELECT DISTINCT um.user_id, t.menu_id,
       COALESCE(NULLIF(um.tenant_id, ''), NULLIF(u.customer_id, ''), '')
FROM sys_user_menu um
JOIN sys_user u ON u.user_id = um.user_id
CROSS JOIN (
  SELECT @scrap_dir AS menu_id
  UNION ALL SELECT @scrap_apply
  UNION ALL SELECT @scrap_audit
  UNION ALL SELECT @scrap_query
) t
WHERE um.menu_id IN (@wh_root, @stocktaking_dir, @scrap_dir)
   OR um.menu_id IN (SELECT menu_id FROM (SELECT menu_id FROM sys_menu WHERE parent_id = @stocktaking_dir) x)
   OR um.menu_id IN (SELECT menu_id FROM (SELECT menu_id FROM sys_menu WHERE parent_id = @wh_root) y);

-- 7) 岗位菜单（有库房/盘点权限即补）
INSERT INTO sys_post_menu (post_id, menu_id, tenant_id)
SELECT DISTINCT pm.post_id, t.menu_id, pm.tenant_id
FROM sys_post_menu pm
CROSS JOIN (
  SELECT @scrap_dir AS menu_id
  UNION ALL SELECT @scrap_apply
  UNION ALL SELECT @scrap_audit
  UNION ALL SELECT @scrap_query
) t
WHERE (
    pm.menu_id IN (@wh_root, @stocktaking_dir, @scrap_dir)
    OR pm.menu_id IN (SELECT menu_id FROM (SELECT menu_id FROM sys_menu WHERE parent_id = @stocktaking_dir) x)
    OR pm.menu_id IN (SELECT menu_id FROM (SELECT menu_id FROM sys_menu WHERE parent_id = @wh_root) y)
  )
  AND NOT EXISTS (
    SELECT 1 FROM sys_post_menu x
    WHERE x.post_id = pm.post_id AND x.menu_id = t.menu_id
      AND IFNULL(x.tenant_id, '') = IFNULL(pm.tenant_id, '')
  );

-- 8) 清理库房根下重复的空「报损管理」占位（如 3992 path=1），避免分栏底部再出现「库房管理/报损管理」
-- 子查询包一层，避免 MySQL ER_UPDATE_TABLE_USED
UPDATE sys_menu m
JOIN (
  SELECT x.menu_id
  FROM sys_menu x
  WHERE x.parent_id = @wh_root
    AND x.menu_id <> @scrap_dir
    AND x.menu_name IN ('报损管理', '报损管理(废弃占位)', '报废管理', '报废管理(废弃占位)')
    AND (x.path IS NULL OR x.path IN ('', '1') OR x.component IS NULL OR x.component = '')
    AND x.menu_type = 'M'
    AND NOT EXISTS (SELECT 1 FROM sys_menu c WHERE c.parent_id = x.menu_id)
) d ON m.menu_id = d.menu_id
SET m.visible = '1',
    m.status = '1',
    m.menu_name = '报损管理(废弃占位)',
    m.remark = '重复占位已停用；正式分栏为 3983 scrapMgmt',
    m.update_by = '1',
    m.update_time = NOW();

DELETE FROM sys_role_menu
WHERE menu_id IN (
  SELECT menu_id FROM (
    SELECT m.menu_id FROM sys_menu m
    WHERE m.parent_id = @wh_root
      AND m.menu_id <> @scrap_dir
      AND (m.menu_name LIKE '报损管理%' OR m.menu_name LIKE '报废管理%')
      AND m.status = '1'
      AND m.visible = '1'
  ) t
);

DELETE FROM sys_user_menu
WHERE menu_id IN (
  SELECT menu_id FROM (
    SELECT m.menu_id FROM sys_menu m
    WHERE m.parent_id = @wh_root
      AND m.menu_id <> @scrap_dir
      AND (m.menu_name LIKE '报损管理%' OR m.menu_name LIKE '报废管理%')
      AND m.status = '1'
      AND m.visible = '1'
  ) t
);

DELETE FROM hc_customer_menu
WHERE menu_id IN (
  SELECT menu_id FROM (
    SELECT m.menu_id FROM sys_menu m
    WHERE m.parent_id = @wh_root
      AND m.menu_id <> @scrap_dir
      AND (m.menu_name LIKE '报损管理%' OR m.menu_name LIKE '报废管理%')
      AND m.status = '1'
      AND m.visible = '1'
  ) t
);

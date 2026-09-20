-- 科室管理一级下京东式分栏最终顺序：
-- 科室管理 / 科室收货 / 科室盘点 / 科室消耗
-- 做法：新建二级目录「科室管理」收纳原叶子菜单；收货 order_num=6、盘点=7、消耗=8

SET @department_root := (
  SELECT m.menu_id FROM sys_menu m
  WHERE m.path = 'department' AND m.menu_type = 'M'
  ORDER BY m.menu_id LIMIT 1
);

SET @dept_manage_menu := 3946;

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, query,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  @dept_manage_menu, '科室管理', @department_root, 1, 'deptManage', NULL, NULL,
  1, 0, 'M', '0', '0', '', 'list',
  'admin', NOW(), '1', NOW(), '京东式分栏：科室申领/申购等叶子归组',
  '0', '1'
FROM DUAL
WHERE @department_root IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = @dept_manage_menu);

-- 可见叶子菜单归入「科室管理」目录（与原假分组一致）
UPDATE sys_menu
SET parent_id = @dept_manage_menu,
    update_by = '1',
    update_time = NOW()
WHERE parent_id = @department_root
  AND menu_type = 'C'
  AND IFNULL(visible, '0') = '0'
  AND IFNULL(status, '0') = '0'
  AND path IN (
    'dApply',
    'dApplyAudit',
    'dPurchaseAgg',
    'dPurchaseAggAudit',
    'departmentTransfer',
    'whWarehouseApply',
    'deptProfitLoss'
  );

-- 分栏顺序：收货(6) → 盘点(7) → 消耗(8)
UPDATE sys_menu
SET order_num = 6,
    update_by = '1',
    update_time = NOW()
WHERE menu_id = 1557
  AND parent_id = @department_root;

UPDATE sys_menu
SET order_num = 8,
    update_by = '1',
    update_time = NOW()
WHERE menu_id = 1559
  AND parent_id = @department_root;

-- 授权：拥有任一子菜单的角色/用户/客户补目录节点
INSERT IGNORE INTO sys_role_menu (role_id, menu_id)
SELECT DISTINCT rm.role_id, @dept_manage_menu
FROM sys_role_menu rm
WHERE rm.menu_id IN (
  SELECT menu_id FROM sys_menu WHERE parent_id = @dept_manage_menu
);

INSERT INTO hc_customer_menu (tenant_id, menu_id, status, is_enabled, create_by, create_time)
SELECT c.tenant_id, @dept_manage_menu, '0', '1', 'admin', NOW()
FROM (
  SELECT DISTINCT tenant_id FROM hc_customer_menu
  WHERE menu_id IN (SELECT menu_id FROM sys_menu WHERE parent_id = @dept_manage_menu)
) c
WHERE NOT EXISTS (
  SELECT 1 FROM hc_customer_menu h
  WHERE h.tenant_id = c.tenant_id AND h.menu_id = @dept_manage_menu
);

INSERT IGNORE INTO sys_user_menu (user_id, menu_id, tenant_id)
SELECT DISTINCT um.user_id, @dept_manage_menu, um.tenant_id
FROM sys_user_menu um
WHERE um.menu_id IN (
  SELECT menu_id FROM sys_menu WHERE parent_id = @dept_manage_menu
);

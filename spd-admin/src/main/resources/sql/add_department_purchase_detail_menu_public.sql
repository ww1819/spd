-- 科室管理 / 科室采购：申购单审核后增加「科室采购表」
-- 菜单 3990；挂目录 3950；order_num=3（申购 1 / 审核 2 / 采购表 3）

SET @dept_purchase := 3950;
SET @purchase_audit := 1572;
SET @purchase_detail := 3990;

-- 1) 菜单页
INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, query,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  @purchase_detail, '科室采购表', @dept_purchase, 3, 'purchaseDetail', 'department/purchaseDetail/index', NULL,
  1, 0, 'C', '0', '0', 'department:purchaseDetail:list', 'chart',
  'admin', NOW(), '1', NOW(), '科室采购明细/汇总（已审核申购）',
  '0', '1'
FROM DUAL
WHERE EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = @dept_purchase)
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = @purchase_detail);

UPDATE sys_menu
SET parent_id = @dept_purchase,
    order_num = 3,
    menu_name = '科室采购表',
    path = 'purchaseDetail',
    component = 'department/purchaseDetail/index',
    menu_type = 'C',
    perms = 'department:purchaseDetail:list',
    visible = '0',
    status = '0',
    is_platform = '0',
    default_open_to_customer = '1',
    icon = IFNULL(NULLIF(icon, ''), 'chart'),
    remark = '科室采购明细/汇总（已审核申购）',
    update_by = '1',
    update_time = NOW()
WHERE menu_id = @purchase_detail
  AND EXISTS (
    SELECT 1 FROM (SELECT menu_id FROM sys_menu WHERE menu_id = @dept_purchase) t
  );

-- 确保申购 / 审核顺序
UPDATE sys_menu SET order_num = 1, update_by = '1', update_time = NOW()
WHERE menu_id = 1530 AND parent_id = @dept_purchase;
UPDATE sys_menu SET order_num = 2, update_by = '1', update_time = NOW()
WHERE menu_id = @purchase_audit AND parent_id = @dept_purchase;

-- 2) 角色：有申购审核或科室采购目录的角色补菜单
INSERT IGNORE INTO sys_role_menu (role_id, menu_id)
SELECT DISTINCT rm.role_id, @purchase_detail
FROM sys_role_menu rm
WHERE rm.menu_id IN (@dept_purchase, @purchase_audit, 1530);

-- 3) 客户开放
INSERT INTO hc_customer_menu (tenant_id, menu_id, status, is_enabled, create_by, create_time)
SELECT c.tenant_id, @purchase_detail, '0', '1', 'admin', NOW()
FROM (SELECT DISTINCT tenant_id FROM hc_customer_menu WHERE menu_id IN (@dept_purchase, @purchase_audit, 1530)) c
WHERE NOT EXISTS (
  SELECT 1 FROM hc_customer_menu h
  WHERE h.tenant_id = c.tenant_id AND h.menu_id = @purchase_detail
);

-- 4) 用户菜单
INSERT IGNORE INTO sys_user_menu (user_id, menu_id, tenant_id)
SELECT DISTINCT um.user_id, @purchase_detail,
       COALESCE(NULLIF(um.tenant_id, ''), NULLIF(u.customer_id, ''), '')
FROM sys_user_menu um
JOIN sys_user u ON u.user_id = um.user_id
WHERE um.menu_id IN (@dept_purchase, @purchase_audit, 1530);

-- 5) 岗位菜单
INSERT INTO sys_post_menu (post_id, menu_id, tenant_id)
SELECT DISTINCT pm.post_id, @purchase_detail, pm.tenant_id
FROM sys_post_menu pm
WHERE pm.menu_id IN (@dept_purchase, @purchase_audit, 1530)
  AND NOT EXISTS (
    SELECT 1 FROM sys_post_menu x
    WHERE x.post_id = pm.post_id AND x.menu_id = @purchase_detail
      AND IFNULL(x.tenant_id, '') = IFNULL(pm.tenant_id, '')
  );

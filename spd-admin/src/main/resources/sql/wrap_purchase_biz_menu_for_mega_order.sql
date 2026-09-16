-- 采购管理下：将计划/订单等叶子收成二级目录，便于京东分栏「目录优先」时仍为
--   采购管理 | 订单管理 | 采购报表
-- 同时配合前端改回「目录在前」，库房管理分栏变为：入库 | 出库 / 盘点 | 库房(库存查询)

SET @purchase_root := (
  SELECT m.menu_id FROM sys_menu m
  WHERE m.parent_id = 0 AND m.menu_type = 'M'
    AND (m.path = 'purchase' OR m.menu_name = '采购管理')
  ORDER BY m.menu_id LIMIT 1
);
SET @purchase_biz := 3949;

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, query,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  @purchase_biz, '采购管理', @purchase_root, 1, 'purchaseBiz', NULL, NULL,
  1, 0, 'M', '0', '0', '', 'shopping',
  'admin', NOW(), '1', NOW(), '采购计划/审核/订单/预测补货',
  '0', '1'
FROM DUAL
WHERE @purchase_root IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = @purchase_biz);

UPDATE sys_menu
SET parent_id = @purchase_root,
    order_num = 1,
    update_by = '1',
    update_time = NOW()
WHERE menu_id = @purchase_biz;

-- 叶子菜单归入该二级目录
UPDATE sys_menu
SET parent_id = @purchase_biz,
    update_by = '1',
    update_time = NOW()
WHERE parent_id = @purchase_root
  AND menu_type = 'C'
  AND menu_id IN (1263, 1264, 3118, 3730);

-- 订单管理/采购报表保持在采购一级下，排在业务目录之后
UPDATE sys_menu SET order_num = 10, parent_id = @purchase_root, update_by = '1', update_time = NOW() WHERE menu_id = 3947;
UPDATE sys_menu SET order_num = 11, parent_id = @purchase_root, update_by = '1', update_time = NOW() WHERE menu_id = 3948;

INSERT IGNORE INTO sys_role_menu (role_id, menu_id)
SELECT DISTINCT rm.role_id, @purchase_biz
FROM sys_role_menu rm WHERE rm.menu_id IN (1263, 1264, 3118, 3730);

INSERT INTO hc_customer_menu (tenant_id, menu_id, status, is_enabled, create_by, create_time)
SELECT c.tenant_id, @purchase_biz, '0', '1', 'admin', NOW()
FROM (SELECT DISTINCT tenant_id FROM hc_customer_menu WHERE menu_id IN (1263, 1264, 3118, 3730)) c
WHERE NOT EXISTS (
  SELECT 1 FROM hc_customer_menu h WHERE h.tenant_id = c.tenant_id AND h.menu_id = @purchase_biz
);

INSERT IGNORE INTO sys_user_menu (user_id, menu_id, tenant_id)
SELECT DISTINCT um.user_id, @purchase_biz, um.tenant_id
FROM sys_user_menu um WHERE um.menu_id IN (1263, 1264, 3118, 3730);

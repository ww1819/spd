-- 衡水三院公共菜单回填：重建今日新增目录并授权给 hengsui-third-001（及全部启用客户）
-- 覆盖：采购管理下「采购管理/订单管理/采购报表」；科室管理下「科室领用/科室采购」；盈亏进盘点

SET @purchase_root := (
  SELECT m.menu_id FROM sys_menu m
  WHERE m.parent_id = 0 AND m.menu_type = 'M' AND (m.path = 'purchase' OR m.menu_name = '采购管理')
  ORDER BY m.menu_id LIMIT 1
);
SET @department_root := (
  SELECT m.menu_id FROM sys_menu m
  WHERE m.parent_id = 0 AND m.menu_type = 'M' AND m.path = 'department'
  ORDER BY m.menu_id LIMIT 1
);

SET @purchase_biz := 3949;
SET @order_mgmt := 3947;
SET @purchase_report := 3948;
SET @dept_requisition := 3946;
SET @dept_purchase := 3950;

-- ========== 1) 采购：二级目录 ==========
INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, query,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT @purchase_biz, '采购管理', @purchase_root, 1, 'purchaseBiz', NULL, NULL,
  1, 0, 'M', '0', '0', '', 'shopping', 'admin', NOW(), '1', NOW(),
  '采购计划/审核/订单/预测补货', '0', '1'
FROM DUAL WHERE @purchase_root IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = @purchase_biz);

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, query,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT @order_mgmt, '订单管理', @purchase_root, 10, 'orderMgmt', NULL, NULL,
  1, 0, 'M', '0', '0', '', 'list', 'admin', NOW(), '1', NOW(),
  '订单审查/订单发布', '0', '1'
FROM DUAL WHERE @purchase_root IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = @order_mgmt);

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, query,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT @purchase_report, '采购报表', @purchase_root, 11, 'cgReport', NULL, NULL,
  1, 0, 'M', '0', '0', '', 'chart', 'admin', NOW(), '1', NOW(),
  '采购报表综合查询/云平台编码绑定', '0', '1'
FROM DUAL WHERE @purchase_root IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = @purchase_report);

UPDATE sys_menu SET parent_id=@purchase_root, order_num=1, menu_name='采购管理', path='purchaseBiz', menu_type='M',
  is_platform='0', default_open_to_customer='1', visible='0', status='0', update_by='1', update_time=NOW()
WHERE menu_id=@purchase_biz;

UPDATE sys_menu SET parent_id=@purchase_root, order_num=10, menu_name='订单管理', path='orderMgmt', menu_type='M',
  is_platform='0', default_open_to_customer='1', visible='0', status='0', update_by='1', update_time=NOW()
WHERE menu_id=@order_mgmt;

UPDATE sys_menu SET parent_id=@purchase_root, order_num=11, menu_name='采购报表', path='cgReport', menu_type='M',
  is_platform='0', default_open_to_customer='1', visible='0', status='0', update_by='1', update_time=NOW()
WHERE menu_id=@purchase_report;

-- 采购叶子迁入
UPDATE sys_menu SET parent_id=@purchase_biz, update_by='1', update_time=NOW()
WHERE menu_id IN (1263, 1264, 3118, 3730) AND @purchase_biz IS NOT NULL;

UPDATE sys_menu SET parent_id=@order_mgmt, order_num=1, menu_type='C', path='shenhe', component='caigou/shenhe/index',
  perms=IFNULL(NULLIF(perms,''),'caigou:shenhe:list'), update_by='1', update_time=NOW()
WHERE menu_id=1431;

UPDATE sys_menu SET parent_id=@order_mgmt, order_num=2, update_by='1', update_time=NOW()
WHERE menu_id=1432;

UPDATE sys_menu SET parent_id=@purchase_report, order_num=1, menu_type='C', path='purchaseReport', component='caigou/report/index',
  update_by='1', update_time=NOW()
WHERE menu_id=1433;

UPDATE sys_menu SET parent_id=@purchase_report, order_num=2, update_by='1', update_time=NOW()
WHERE menu_id=3550;

-- ========== 2) 科室：领用 / 采购 ==========
INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, query,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT @dept_requisition, '科室领用', @department_root, 1, 'deptRequisition', NULL, NULL,
  1, 0, 'M', '0', '0', '', 'list', 'admin', NOW(), '1', NOW(),
  '科室领用：申领/审核/转科/库房申请', '0', '1'
FROM DUAL WHERE @department_root IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = @dept_requisition);

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, query,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT @dept_purchase, '科室采购', @department_root, 2, 'deptPurchase', NULL, NULL,
  1, 0, 'M', '0', '0', '', 'shopping', 'admin', NOW(), '1', NOW(),
  '科室采购：科室申购/申购单审核', '0', '1'
FROM DUAL WHERE @department_root IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = @dept_purchase);

UPDATE sys_menu SET parent_id=@department_root, order_num=1, menu_name='科室领用', path='deptRequisition', menu_type='M',
  is_platform='0', default_open_to_customer='1', visible='0', status='0', update_by='1', update_time=NOW()
WHERE menu_id=@dept_requisition;

UPDATE sys_menu SET parent_id=@department_root, order_num=2, menu_name='科室采购', path='deptPurchase', menu_type='M',
  is_platform='0', default_open_to_customer='1', visible='0', status='0', update_by='1', update_time=NOW()
WHERE menu_id=@dept_purchase;

-- 领用下：申领/审核/转科/库房申请（不含申购、不含盈亏）
UPDATE sys_menu SET parent_id=@dept_requisition, update_by='1', update_time=NOW()
WHERE menu_id IN (1100, 1566, 1407, 3869);

-- 采购下：申购/申购审核
UPDATE sys_menu SET parent_id=@dept_purchase, order_num=1, update_by='1', update_time=NOW() WHERE menu_id=1530;
UPDATE sys_menu SET parent_id=@dept_purchase, order_num=2, update_by='1', update_time=NOW() WHERE menu_id=1572;

-- 盈亏进盘点
SET @stocktaking_dir := (SELECT menu_id FROM sys_menu WHERE menu_id=1560 LIMIT 1);
UPDATE sys_menu SET parent_id=1560, order_num=3, update_by='1', update_time=NOW()
WHERE menu_id=3714 AND @stocktaking_dir IS NOT NULL;

-- ========== 3) 公共标记 ==========
UPDATE sys_menu SET is_platform='0', default_open_to_customer='1', update_by='1', update_time=NOW()
WHERE menu_id IN (3946, 3947, 3948, 3949, 3950);

-- ========== 4) 回填全部启用客户（含衡水 hengsui-third-001）==========
INSERT INTO hc_customer_menu (tenant_id, menu_id, status, is_enabled, create_by, create_time)
SELECT c.customer_id, m.menu_id, '0', '1', 'admin', NOW()
FROM sb_customer c
JOIN sys_menu m ON m.menu_id IN (3946, 3947, 3948, 3949, 3950)
WHERE IFNULL(c.hc_status, '0') = '0'
  AND c.delete_time IS NULL
  AND NOT EXISTS (
    SELECT 1 FROM hc_customer_menu h
    WHERE h.tenant_id = c.customer_id AND h.menu_id = m.menu_id
  );

-- ========== 5) 衡水及所有租户：有子菜单的用户补目录 ==========
INSERT IGNORE INTO sys_user_menu (user_id, menu_id, tenant_id)
SELECT DISTINCT um.user_id, 3949, COALESCE(NULLIF(um.tenant_id,''), u.customer_id)
FROM sys_user_menu um
JOIN sys_user u ON u.user_id = um.user_id
WHERE um.menu_id IN (1263, 1264, 3118, 3730);

INSERT IGNORE INTO sys_user_menu (user_id, menu_id, tenant_id)
SELECT DISTINCT um.user_id, 3947, COALESCE(NULLIF(um.tenant_id,''), u.customer_id)
FROM sys_user_menu um
JOIN sys_user u ON u.user_id = um.user_id
WHERE um.menu_id IN (1431, 1432);

INSERT IGNORE INTO sys_user_menu (user_id, menu_id, tenant_id)
SELECT DISTINCT um.user_id, 3948, COALESCE(NULLIF(um.tenant_id,''), u.customer_id)
FROM sys_user_menu um
JOIN sys_user u ON u.user_id = um.user_id
WHERE um.menu_id IN (1433, 3550);

INSERT IGNORE INTO sys_user_menu (user_id, menu_id, tenant_id)
SELECT DISTINCT um.user_id, 3946, COALESCE(NULLIF(um.tenant_id,''), u.customer_id)
FROM sys_user_menu um
JOIN sys_user u ON u.user_id = um.user_id
WHERE um.menu_id IN (1100, 1566, 1407, 3869);

INSERT IGNORE INTO sys_user_menu (user_id, menu_id, tenant_id)
SELECT DISTINCT um.user_id, 3950, COALESCE(NULLIF(um.tenant_id,''), u.customer_id)
FROM sys_user_menu um
JOIN sys_user u ON u.user_id = um.user_id
WHERE um.menu_id IN (1530, 1572);

-- ========== 6) super 岗位补目录 ==========
INSERT INTO sys_post_menu (post_id, menu_id, tenant_id)
SELECT p.post_id, m.menu_id, p.tenant_id
FROM sys_post p
JOIN sys_menu m ON m.menu_id IN (3946, 3947, 3948, 3949, 3950)
WHERE IFNULL(p.status, '0') = '0'
  AND p.post_code = 'super'
  AND p.tenant_id IS NOT NULL AND TRIM(p.tenant_id) != ''
  AND EXISTS (SELECT 1 FROM sb_customer c WHERE c.customer_id = p.tenant_id AND IFNULL(c.hc_status,'0')='0' AND c.delete_time IS NULL)
  AND NOT EXISTS (SELECT 1 FROM sys_post_menu x WHERE x.post_id = p.post_id AND x.menu_id = m.menu_id);

-- ========== 7) 角色补目录 ==========
INSERT IGNORE INTO sys_role_menu (role_id, menu_id)
SELECT DISTINCT rm.role_id, 3949 FROM sys_role_menu rm WHERE rm.menu_id IN (1263,1264,3118,3730);
INSERT IGNORE INTO sys_role_menu (role_id, menu_id)
SELECT DISTINCT rm.role_id, 3947 FROM sys_role_menu rm WHERE rm.menu_id IN (1431,1432);
INSERT IGNORE INTO sys_role_menu (role_id, menu_id)
SELECT DISTINCT rm.role_id, 3948 FROM sys_role_menu rm WHERE rm.menu_id IN (1433,3550);
INSERT IGNORE INTO sys_role_menu (role_id, menu_id)
SELECT DISTINCT rm.role_id, 3946 FROM sys_role_menu rm WHERE rm.menu_id IN (1100,1566,1407,3869);
INSERT IGNORE INTO sys_role_menu (role_id, menu_id)
SELECT DISTINCT rm.role_id, 3950 FROM sys_role_menu rm WHERE rm.menu_id IN (1530,1572);

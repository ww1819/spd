-- =============================================================================
-- 京东分栏二级目录补齐（幂等，可重复执行）
-- 由 SqlInitRunner 在 menu.sql 之后自动执行；也可手工按「/」分段执行
-- 覆盖：
--   采购：采购管理(3949) / 订单管理(3947) / 采购报表(3948) + 平台供应商迁入
--   科室：科室领用(3946) / 科室采购(3950)；转科申请挂科室一级；患者收费进科室消耗
--   库房：库存管理(3951)；盈亏处理纠正为页面；条码/即入即出挂库存管理
--   基础：基础资料(3953) / 监测维护(3952) / 分类维护(3957)
--   系统：人员权限(3958)
-- =============================================================================

-- ---------- 0) 根目录与固定 ID ----------
SET @purchase_root := (
  SELECT m.menu_id FROM sys_menu m
  WHERE m.parent_id = 0 AND m.menu_type = 'M' AND (m.path = 'purchase' OR m.menu_name = '采购管理')
  ORDER BY m.menu_id LIMIT 1
);
/
SET @department_root := (
  SELECT m.menu_id FROM sys_menu m
  WHERE m.parent_id = 0 AND m.menu_type = 'M' AND m.path = 'department'
  ORDER BY m.menu_id LIMIT 1
);
/
SET @wh_root := (
  SELECT m.menu_id FROM sys_menu m
  WHERE m.parent_id = 0 AND m.menu_type = 'M' AND (m.path = 'warehouse' OR m.menu_name = '库房管理')
  ORDER BY m.menu_id LIMIT 1
);
/
SET @foundation_root := (
  SELECT m.menu_id FROM sys_menu m
  WHERE m.parent_id = 0 AND m.menu_type = 'M' AND m.path = 'foundation'
  ORDER BY m.menu_id LIMIT 1
);
/
SET @system_root := (
  SELECT m.menu_id FROM sys_menu m
  WHERE m.parent_id = 0 AND m.menu_type = 'M' AND m.path = 'system'
  ORDER BY m.menu_id LIMIT 1
);
/
SET @purchase_biz := 3949;
/
SET @order_mgmt := 3947;
/
SET @purchase_report := 3948;
/
SET @dept_requisition := 3946;
/
SET @dept_purchase := 3950;
/
SET @inventory_mgmt := 3951;
/
SET @monitor_maint := 3952;
/
SET @foundation_biz := 3953;
/
SET @category_maint := 3957;
/
SET @person_perm := 3958;
/

-- ---------- 1) 采购：二级目录 ----------
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
/
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
/
INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, query,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT @purchase_report, '采购报表', @purchase_root, 11, 'cgReport', NULL, NULL,
  1, 0, 'M', '0', '0', '', 'chart', 'admin', NOW(), '1', NOW(),
  '采购报表综合查询/云平台编码绑定/平台供应商', '0', '1'
FROM DUAL WHERE @purchase_root IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = @purchase_report);
/
UPDATE sys_menu SET parent_id=@purchase_root, order_num=1, menu_name='采购管理', path='purchaseBiz', menu_type='M',
  component=NULL, is_platform='0', default_open_to_customer='1', visible='0', status='0', update_by='1', update_time=NOW()
WHERE menu_id=@purchase_biz;
/
UPDATE sys_menu SET parent_id=@purchase_root, order_num=10, menu_name='订单管理', path='orderMgmt', menu_type='M',
  component=NULL, is_platform='0', default_open_to_customer='1', visible='0', status='0', update_by='1', update_time=NOW()
WHERE menu_id=@order_mgmt;
/
UPDATE sys_menu SET parent_id=@purchase_root, order_num=11, menu_name='采购报表', path='cgReport', menu_type='M',
  component=NULL, is_platform='0', default_open_to_customer='1', visible='0', status='0', update_by='1', update_time=NOW()
WHERE menu_id=@purchase_report;
/
UPDATE sys_menu SET parent_id=@purchase_biz, update_by='1', update_time=NOW()
WHERE menu_id IN (1263, 1264, 3118, 3730);
/
UPDATE sys_menu SET parent_id=@order_mgmt, order_num=1, menu_type='C', path='shenhe', component='caigou/shenhe/index',
  perms=IFNULL(NULLIF(perms,''),'caigou:shenhe:list'), update_by='1', update_time=NOW()
WHERE menu_id=1431;
/
UPDATE sys_menu SET parent_id=@order_mgmt, order_num=2, update_by='1', update_time=NOW()
WHERE menu_id=1432;
/
UPDATE sys_menu SET parent_id=@purchase_report, order_num=1, menu_type='C', path='purchaseReport', component='caigou/report/index',
  update_by='1', update_time=NOW()
WHERE menu_id=1433;
/
UPDATE sys_menu SET parent_id=@purchase_report, order_num=2, update_by='1', update_time=NOW()
WHERE menu_id=3550;
/
UPDATE sys_menu SET parent_id=@purchase_report, order_num=3, is_platform='0', default_open_to_customer='1',
  update_by='1', update_time=NOW()
WHERE menu_id=3877;
/

-- ---------- 2) 科室：领用 / 采购 ----------
INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, query,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT @dept_requisition, '科室领用', @department_root, 1, 'deptRequisition', NULL, NULL,
  1, 0, 'M', '0', '0', '', 'list', 'admin', NOW(), '1', NOW(),
  '科室领用：申领/审核/库房申请', '0', '1'
FROM DUAL WHERE @department_root IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = @dept_requisition);
/
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
/
UPDATE sys_menu SET parent_id=@department_root, order_num=1, menu_name='科室领用', path='deptRequisition', menu_type='M',
  component=NULL, is_platform='0', default_open_to_customer='1', visible='0', status='0', update_by='1', update_time=NOW()
WHERE menu_id=@dept_requisition;
/
UPDATE sys_menu SET parent_id=@department_root, order_num=2, menu_name='科室采购', path='deptPurchase', menu_type='M',
  component=NULL, is_platform='0', default_open_to_customer='1', visible='0', status='0', update_by='1', update_time=NOW()
WHERE menu_id=@dept_purchase;
/
UPDATE sys_menu SET parent_id=@dept_requisition, update_by='1', update_time=NOW()
WHERE menu_id IN (1100, 1566, 3869);
/
UPDATE sys_menu SET parent_id=@dept_purchase, order_num=1, update_by='1', update_time=NOW() WHERE menu_id=1530;
/
UPDATE sys_menu SET parent_id=@dept_purchase, order_num=2, update_by='1', update_time=NOW() WHERE menu_id=1572;
/
-- 转科申请挂科室一级（京东「科室管理」假分组）
UPDATE sys_menu SET parent_id=@department_root, order_num=20, update_by='1', update_time=NOW()
WHERE menu_id=1407 AND @department_root IS NOT NULL;
/
-- 患者收费查询进科室消耗
UPDATE sys_menu SET parent_id=1559, order_num=4, update_by='1', update_time=NOW()
WHERE menu_id=3601;
/
-- 科室盈亏进盘点管理
UPDATE sys_menu SET parent_id=1560, order_num=3, update_by='1', update_time=NOW()
WHERE menu_id=3714 AND EXISTS (SELECT 1 FROM sys_menu x WHERE x.menu_id=1560);
/

-- ---------- 3) 库房：库存管理 ----------
INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, query,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT @inventory_mgmt, '库存管理', @wh_root, 5, 'inventoryMgmt', NULL, NULL,
  1, 0, 'M', '0', '0', '', 'chart', 'admin', NOW(), '1', NOW(),
  '库存查询/条码/高值即入即出', '0', '1'
FROM DUAL WHERE @wh_root IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = @inventory_mgmt);
/
UPDATE sys_menu SET parent_id=@wh_root, order_num=5, menu_name='库存管理', path='inventoryMgmt', menu_type='M',
  component=NULL, is_platform='0', default_open_to_customer='1', visible='0', status='0', update_by='1', update_time=NOW()
WHERE menu_id=@inventory_mgmt;
/
UPDATE sys_menu SET parent_id=@inventory_mgmt, order_num=1, is_platform='0', default_open_to_customer='1', update_by='1', update_time=NOW() WHERE menu_id=1069;
/
UPDATE sys_menu SET parent_id=@inventory_mgmt, order_num=2, is_platform='0', default_open_to_customer='1', update_by='1', update_time=NOW() WHERE menu_id=3708;
/
UPDATE sys_menu SET parent_id=@inventory_mgmt, order_num=3, is_platform='0', default_open_to_customer='1', update_by='1', update_time=NOW() WHERE menu_id=3711;
/
UPDATE sys_menu SET parent_id=@inventory_mgmt, order_num=4, is_platform='0', default_open_to_customer='1', update_by='1', update_time=NOW() WHERE menu_id=3720;
/
UPDATE sys_menu SET order_num=6, update_by='1', update_time=NOW() WHERE menu_id=1544 AND parent_id=@wh_root;
/
UPDATE sys_menu SET order_num=7, is_platform='0', default_open_to_customer='1', update_by='1', update_time=NOW() WHERE menu_id=1070 AND parent_id=@wh_root;
/
-- 盈亏处理误标目录纠正
UPDATE sys_menu SET menu_type='C', path='profitLoss', component='warehouse/profitLoss/index',
  perms='warehouse:profitLoss:list', icon=IFNULL(NULLIF(icon,''),'lifebuoy'), update_by='1', update_time=NOW()
WHERE menu_id=1239 AND menu_type='M';
/
UPDATE sys_menu SET visible='1', update_by='1', update_time=NOW() WHERE menu_id=3892 OR parent_id=3892;
/

-- ---------- 4) 基础资料：业务 / 监测 / 分类 ----------
INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, query,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT @foundation_biz, '基础资料', @foundation_root, 1, 'foundationBiz', NULL, NULL,
  1, 0, 'M', '0', '0', '', 'form', 'admin', NOW(), '1', NOW(),
  '基础资料业务叶子归组', '0', '1'
FROM DUAL WHERE @foundation_root IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = @foundation_biz);
/
INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, query,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT @monitor_maint, '监测维护', @foundation_root, 2, 'monitorMaint', NULL, NULL,
  1, 0, 'M', '0', '0', '', 'eye-open', 'admin', NOW(), '1', NOW(),
  '耗材对照/定数监测', '0', '1'
FROM DUAL WHERE @foundation_root IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = @monitor_maint);
/
INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, query,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT @category_maint, '分类维护', @foundation_root, 3, 'categoryMaint', NULL, NULL,
  1, 0, 'M', '0', '0', '', 'tree', 'admin', NOW(), '1', NOW(),
  '财务分类/材料类别/库房分类/货位/18类/收费项目', '0', '1'
FROM DUAL WHERE @foundation_root IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = @category_maint);
/
UPDATE sys_menu SET parent_id=@foundation_root, order_num=1, menu_name='基础资料', path='foundationBiz', menu_type='M',
  component=NULL, is_platform='0', default_open_to_customer='1', visible='0', status='0', update_by='1', update_time=NOW()
WHERE menu_id=@foundation_biz;
/
UPDATE sys_menu SET parent_id=@foundation_root, order_num=2, menu_name='监测维护', path='monitorMaint', menu_type='M',
  component=NULL, is_platform='0', default_open_to_customer='1', visible='0', status='0', update_by='1', update_time=NOW()
WHERE menu_id=@monitor_maint;
/
UPDATE sys_menu SET parent_id=@foundation_root, order_num=3, menu_name='分类维护', path='categoryMaint', menu_type='M',
  component=NULL, is_platform='0', default_open_to_customer='1', visible='0', status='0', update_by='1', update_time=NOW()
WHERE menu_id=@category_maint;
/
-- 一级下叶子先归入「基础资料」（监测/分类项稍后迁走）
UPDATE sys_menu SET parent_id=@foundation_biz, update_by='1', update_time=NOW()
WHERE parent_id=@foundation_root AND menu_type='C'
  AND menu_id NOT IN (1553, 1549, 1104, 1103, 2280, 2270, 2290, 3490);
/
UPDATE sys_menu SET parent_id=@monitor_maint, order_num=1, update_by='1', update_time=NOW() WHERE menu_id=1553;
/
UPDATE sys_menu SET parent_id=@monitor_maint, order_num=2, update_by='1', update_time=NOW() WHERE menu_id=1549;
/
UPDATE sys_menu SET parent_id=@category_maint, order_num=1, update_by='1', update_time=NOW() WHERE menu_id=1104;
/
UPDATE sys_menu SET parent_id=@category_maint, order_num=2, update_by='1', update_time=NOW() WHERE menu_id=1103;
/
UPDATE sys_menu SET parent_id=@category_maint, order_num=3, update_by='1', update_time=NOW() WHERE menu_id=2280;
/
UPDATE sys_menu SET parent_id=@category_maint, order_num=4, update_by='1', update_time=NOW() WHERE menu_id=2270;
/
UPDATE sys_menu SET parent_id=@category_maint, order_num=5, update_by='1', update_time=NOW() WHERE menu_id=2290;
/
UPDATE sys_menu SET parent_id=@category_maint, order_num=6, update_by='1', update_time=NOW() WHERE menu_id=3490;
/

-- ---------- 5) 系统：人员权限 ----------
INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, query,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT @person_perm, '人员权限', @system_root, 1, 'personPerm', NULL, NULL,
  1, 0, 'M', '0', '0', '', 'peoples', 'admin', NOW(), '1', NOW(),
  '用户/角色/菜单/工作组', '0', '1'
FROM DUAL WHERE @system_root IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = @person_perm);
/
UPDATE sys_menu SET parent_id=@system_root, order_num=1, menu_name='人员权限', path='personPerm', menu_type='M',
  component=NULL, is_platform='0', default_open_to_customer='1', visible='0', status='0', update_by='1', update_time=NOW()
WHERE menu_id=@person_perm;
/
UPDATE sys_menu SET order_num=10, update_by='1', update_time=NOW()
WHERE menu_id=108 AND parent_id=@system_root;
/
UPDATE sys_menu SET parent_id=@person_perm, order_num=1, is_platform='0', default_open_to_customer='1', update_by='1', update_time=NOW() WHERE menu_id=100;
/
UPDATE sys_menu SET parent_id=@person_perm, order_num=2, is_platform='0', default_open_to_customer='1', update_by='1', update_time=NOW() WHERE menu_id=101;
/
UPDATE sys_menu SET parent_id=@person_perm, order_num=3, is_platform='0', default_open_to_customer='1', update_by='1', update_time=NOW() WHERE menu_id=102;
/
UPDATE sys_menu SET parent_id=@person_perm, order_num=4, is_platform='0', default_open_to_customer='1', update_by='1', update_time=NOW() WHERE menu_id=104;
/

-- ---------- 6) 公共标记 ----------
UPDATE sys_menu SET is_platform='0', default_open_to_customer='1', update_by='1', update_time=NOW()
WHERE menu_id IN (3946, 3947, 3948, 3949, 3950, 3951, 3952, 3953, 3957, 3958, 1070, 3877);
/

-- ---------- 7) 客户菜单回填 ----------
INSERT INTO hc_customer_menu (tenant_id, menu_id, status, is_enabled, create_by, create_time)
SELECT c.customer_id, m.menu_id, '0', '1', 'admin', NOW()
FROM sb_customer c
JOIN sys_menu m ON m.menu_id IN (3946, 3947, 3948, 3949, 3950, 3951, 3952, 3953, 3957, 3958, 1070, 3877)
WHERE IFNULL(c.hc_status, '0') = '0'
  AND c.delete_time IS NULL
  AND NOT EXISTS (
    SELECT 1 FROM hc_customer_menu h
    WHERE h.tenant_id = c.customer_id AND h.menu_id = m.menu_id
  );
/
INSERT INTO hc_customer_menu (tenant_id, menu_id, status, is_enabled, create_by, create_time)
SELECT t.tenant_id, m.menu_id, '0', '1', 'admin', NOW()
FROM (SELECT 'hengsui-third-001' tenant_id UNION ALL SELECT 'zaoqiang-tcm-001') t
JOIN sys_menu m ON m.menu_id IN (3946, 3947, 3948, 3949, 3950, 3951, 3952, 3953, 3957, 3958, 1070, 3877)
WHERE EXISTS (SELECT 1 FROM sb_customer c WHERE c.customer_id = t.tenant_id AND c.delete_time IS NULL)
  AND NOT EXISTS (
    SELECT 1 FROM hc_customer_menu h
    WHERE h.tenant_id = t.tenant_id AND h.menu_id = m.menu_id
  );
/

-- ---------- 8) 用户 / 角色 / 岗位目录补齐 ----------
INSERT IGNORE INTO sys_user_menu (user_id, menu_id, tenant_id)
SELECT DISTINCT um.user_id, 3949, COALESCE(NULLIF(um.tenant_id,''), u.customer_id)
FROM sys_user_menu um JOIN sys_user u ON u.user_id = um.user_id
WHERE um.menu_id IN (1263, 1264, 3118, 3730);
/
INSERT IGNORE INTO sys_user_menu (user_id, menu_id, tenant_id)
SELECT DISTINCT um.user_id, 3947, COALESCE(NULLIF(um.tenant_id,''), u.customer_id)
FROM sys_user_menu um JOIN sys_user u ON u.user_id = um.user_id
WHERE um.menu_id IN (1431, 1432);
/
INSERT IGNORE INTO sys_user_menu (user_id, menu_id, tenant_id)
SELECT DISTINCT um.user_id, 3948, COALESCE(NULLIF(um.tenant_id,''), u.customer_id)
FROM sys_user_menu um JOIN sys_user u ON u.user_id = um.user_id
WHERE um.menu_id IN (1433, 3550, 3877);
/
INSERT IGNORE INTO sys_user_menu (user_id, menu_id, tenant_id)
SELECT DISTINCT um.user_id, 3946, COALESCE(NULLIF(um.tenant_id,''), u.customer_id)
FROM sys_user_menu um JOIN sys_user u ON u.user_id = um.user_id
WHERE um.menu_id IN (1100, 1566, 3869);
/
INSERT IGNORE INTO sys_user_menu (user_id, menu_id, tenant_id)
SELECT DISTINCT um.user_id, 3950, COALESCE(NULLIF(um.tenant_id,''), u.customer_id)
FROM sys_user_menu um JOIN sys_user u ON u.user_id = um.user_id
WHERE um.menu_id IN (1530, 1572);
/
INSERT IGNORE INTO sys_user_menu (user_id, menu_id, tenant_id)
SELECT DISTINCT um.user_id, 3951, COALESCE(NULLIF(um.tenant_id,''), u.customer_id)
FROM sys_user_menu um JOIN sys_user u ON u.user_id = um.user_id
WHERE um.menu_id IN (1069, 3708, 3711, 3720);
/
INSERT IGNORE INTO sys_user_menu (user_id, menu_id, tenant_id)
SELECT DISTINCT um.user_id, 3953, COALESCE(NULLIF(um.tenant_id,''), u.customer_id)
FROM sys_user_menu um JOIN sys_user u ON u.user_id = um.user_id
WHERE um.menu_id IN (SELECT menu_id FROM sys_menu WHERE parent_id = 3953);
/
INSERT IGNORE INTO sys_user_menu (user_id, menu_id, tenant_id)
SELECT DISTINCT um.user_id, 3952, COALESCE(NULLIF(um.tenant_id,''), u.customer_id)
FROM sys_user_menu um JOIN sys_user u ON u.user_id = um.user_id
WHERE um.menu_id IN (1553, 1549);
/
INSERT IGNORE INTO sys_user_menu (user_id, menu_id, tenant_id)
SELECT DISTINCT um.user_id, 3957, COALESCE(NULLIF(um.tenant_id,''), u.customer_id)
FROM sys_user_menu um JOIN sys_user u ON u.user_id = um.user_id
WHERE um.menu_id IN (1104, 1103, 2280, 2270, 2290, 3490);
/
INSERT IGNORE INTO sys_user_menu (user_id, menu_id, tenant_id)
SELECT DISTINCT um.user_id, 3958, COALESCE(NULLIF(um.tenant_id,''), u.customer_id)
FROM sys_user_menu um JOIN sys_user u ON u.user_id = um.user_id
WHERE um.menu_id IN (100, 101, 102, 104);
/
INSERT IGNORE INTO sys_user_menu (user_id, menu_id, tenant_id)
SELECT DISTINCT um.user_id, 1559, COALESCE(NULLIF(um.tenant_id,''), u.customer_id)
FROM sys_user_menu um JOIN sys_user u ON u.user_id = um.user_id
WHERE um.menu_id = 3601;
/
INSERT IGNORE INTO sys_role_menu (role_id, menu_id)
SELECT DISTINCT rm.role_id, 3949 FROM sys_role_menu rm WHERE rm.menu_id IN (1263,1264,3118,3730);
/
INSERT IGNORE INTO sys_role_menu (role_id, menu_id)
SELECT DISTINCT rm.role_id, 3947 FROM sys_role_menu rm WHERE rm.menu_id IN (1431,1432);
/
INSERT IGNORE INTO sys_role_menu (role_id, menu_id)
SELECT DISTINCT rm.role_id, 3948 FROM sys_role_menu rm WHERE rm.menu_id IN (1433,3550,3877);
/
INSERT IGNORE INTO sys_role_menu (role_id, menu_id)
SELECT DISTINCT rm.role_id, 3946 FROM sys_role_menu rm WHERE rm.menu_id IN (1100,1566,3869);
/
INSERT IGNORE INTO sys_role_menu (role_id, menu_id)
SELECT DISTINCT rm.role_id, 3950 FROM sys_role_menu rm WHERE rm.menu_id IN (1530,1572);
/
INSERT IGNORE INTO sys_role_menu (role_id, menu_id)
SELECT DISTINCT rm.role_id, 3951 FROM sys_role_menu rm WHERE rm.menu_id IN (1069,3708,3711,3720);
/
INSERT IGNORE INTO sys_role_menu (role_id, menu_id)
SELECT DISTINCT rm.role_id, 3953 FROM sys_role_menu rm WHERE rm.menu_id IN (SELECT menu_id FROM sys_menu WHERE parent_id=3953);
/
INSERT IGNORE INTO sys_role_menu (role_id, menu_id)
SELECT DISTINCT rm.role_id, 3952 FROM sys_role_menu rm WHERE rm.menu_id IN (1553,1549);
/
INSERT IGNORE INTO sys_role_menu (role_id, menu_id)
SELECT DISTINCT rm.role_id, 3957 FROM sys_role_menu rm WHERE rm.menu_id IN (1104,1103,2280,2270,2290,3490);
/
INSERT IGNORE INTO sys_role_menu (role_id, menu_id)
SELECT DISTINCT rm.role_id, 3958 FROM sys_role_menu rm WHERE rm.menu_id IN (100,101,102,104);
/
INSERT IGNORE INTO sys_role_menu (role_id, menu_id)
SELECT DISTINCT rm.role_id, 1559 FROM sys_role_menu rm WHERE rm.menu_id = 3601;
/
INSERT INTO sys_post_menu (post_id, menu_id, tenant_id)
SELECT p.post_id, m.menu_id, p.tenant_id
FROM sys_post p
JOIN sys_menu m ON m.menu_id IN (3946,3947,3948,3949,3950,3951,3952,3953,3957,3958,1070)
WHERE IFNULL(p.status,'0')='0'
  AND p.post_code='super'
  AND p.tenant_id IS NOT NULL AND TRIM(p.tenant_id)!=''
  AND EXISTS (SELECT 1 FROM sb_customer c WHERE c.customer_id=p.tenant_id AND IFNULL(c.hc_status,'0')='0' AND c.delete_time IS NULL)
  AND NOT EXISTS (SELECT 1 FROM sys_post_menu x WHERE x.post_id=p.post_id AND x.menu_id=m.menu_id);
/
INSERT INTO sys_post_menu (post_id, menu_id, tenant_id)
SELECT DISTINCT pm.post_id, d.menu_id, pm.tenant_id
FROM sys_post_menu pm
JOIN (
  SELECT 3958 AS menu_id UNION ALL SELECT 3957 UNION ALL SELECT 3952 UNION ALL SELECT 3953
  UNION ALL SELECT 3951 UNION ALL SELECT 3948
) d
WHERE (
  (d.menu_id=3958 AND pm.menu_id IN (100,101,102,104))
  OR (d.menu_id=3957 AND pm.menu_id IN (1104,1103,2280,2270,2290,3490))
  OR (d.menu_id=3952 AND pm.menu_id IN (1553,1549))
  OR (d.menu_id=3953 AND pm.menu_id IN (SELECT menu_id FROM sys_menu WHERE parent_id=3953))
  OR (d.menu_id=3951 AND pm.menu_id IN (1069,3708,3711,3720))
  OR (d.menu_id=3948 AND pm.menu_id IN (1433,3550,3877))
)
AND NOT EXISTS (
  SELECT 1 FROM sys_post_menu x
  WHERE x.post_id=pm.post_id AND x.menu_id=d.menu_id
    AND IFNULL(x.tenant_id,'')=IFNULL(pm.tenant_id,'')
);
/

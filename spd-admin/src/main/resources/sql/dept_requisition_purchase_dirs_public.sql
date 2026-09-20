-- 科室管理分栏：
-- 1) 原二级「科室管理」(3946) 改名为「科室领用」
-- 2) 其后新增「科室采购」(3950)，迁入 科室申购 / 申购单审核
-- 3) 今日新建/调整的目录一律公共开放：default_open_to_customer=1 + 回填 hc_customer_menu

SET @department_root := (
  SELECT m.menu_id FROM sys_menu m
  WHERE m.parent_id = 0 AND m.menu_type = 'M' AND m.path = 'department'
  ORDER BY m.menu_id LIMIT 1
);
SET @dept_requisition := 3946;  -- 原「科室管理」二级目录 → 科室领用
SET @dept_purchase := 3950;     -- 新建「科室采购」

-- ---------- 1) 改名：科室管理 → 科室领用 ----------
UPDATE sys_menu
SET menu_name = '科室领用',
    path = 'deptRequisition',
    remark = '科室领用：申领/审核/转科/库房申请/盈亏等',
    is_platform = '0',
    default_open_to_customer = '1',
    update_by = '1',
    update_time = NOW()
WHERE menu_id = @dept_requisition;

UPDATE sys_menu
SET order_num = 1,
    parent_id = @department_root,
    update_by = '1',
    update_time = NOW()
WHERE menu_id = @dept_requisition;

-- ---------- 2) 新建「科室采购」并迁移申购菜单 ----------
INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, query,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  @dept_purchase, '科室采购', @department_root, 2, 'deptPurchase', NULL, NULL,
  1, 0, 'M', '0', '0', '', 'shopping',
  'admin', NOW(), '1', NOW(), '科室采购：科室申购/申购单审核',
  '0', '1'
FROM DUAL
WHERE @department_root IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = @dept_purchase);

UPDATE sys_menu
SET parent_id = @department_root,
    order_num = 2,
    menu_name = '科室采购',
    path = 'deptPurchase',
    menu_type = 'M',
    is_platform = '0',
    default_open_to_customer = '1',
    update_by = '1',
    update_time = NOW()
WHERE menu_id = @dept_purchase;

-- 科室申购、申购单审核 → 科室采购
UPDATE sys_menu
SET parent_id = @dept_purchase,
    order_num = 1,
    update_by = '1',
    update_time = NOW()
WHERE menu_id = 1530;  -- 科室申购

UPDATE sys_menu
SET parent_id = @dept_purchase,
    order_num = 2,
    update_by = '1',
    update_time = NOW()
WHERE menu_id = 1572;  -- 申购单审核

-- ---------- 3) 今日相关目录统一公共开放 ----------
UPDATE sys_menu
SET is_platform = '0',
    default_open_to_customer = '1',
    update_by = '1',
    update_time = NOW()
WHERE menu_id IN (
  3946, -- 科室领用
  3950, -- 科室采购
  3947, -- 订单管理
  3948, -- 采购报表
  3949  -- 采购管理（采购下二级业务目录）
);

-- 回填全部启用客户
INSERT INTO hc_customer_menu (tenant_id, menu_id, status, is_enabled, create_by, create_time)
SELECT c.customer_id, m.menu_id, '0', '1', 'admin', NOW()
FROM sb_customer c
JOIN sys_menu m ON m.menu_id IN (3946, 3950, 3947, 3948, 3949)
WHERE IFNULL(c.hc_status, '0') = '0'
  AND (m.is_platform IS NULL OR m.is_platform != '1')
  AND NOT EXISTS (
    SELECT 1 FROM hc_customer_menu h
    WHERE h.tenant_id = c.customer_id AND h.menu_id = m.menu_id
  );

-- 角色：有子菜单则补目录
INSERT IGNORE INTO sys_role_menu (role_id, menu_id)
SELECT DISTINCT rm.role_id, 3946
FROM sys_role_menu rm
WHERE rm.menu_id IN (SELECT menu_id FROM sys_menu WHERE parent_id = 3946);

INSERT IGNORE INTO sys_role_menu (role_id, menu_id)
SELECT DISTINCT rm.role_id, 3950
FROM sys_role_menu rm
WHERE rm.menu_id IN (1530, 1572);

INSERT IGNORE INTO sys_role_menu (role_id, menu_id)
SELECT DISTINCT rm.role_id, 3947
FROM sys_role_menu rm WHERE rm.menu_id IN (1431, 1432);

INSERT IGNORE INTO sys_role_menu (role_id, menu_id)
SELECT DISTINCT rm.role_id, 3948
FROM sys_role_menu rm WHERE rm.menu_id IN (1433, 3550);

INSERT IGNORE INTO sys_role_menu (role_id, menu_id)
SELECT DISTINCT rm.role_id, 3949
FROM sys_role_menu rm WHERE rm.menu_id IN (1263, 1264, 3118, 3730);

-- 用户：有子菜单则补目录
INSERT IGNORE INTO sys_user_menu (user_id, menu_id, tenant_id)
SELECT DISTINCT um.user_id, 3946, um.tenant_id
FROM sys_user_menu um
WHERE um.menu_id IN (SELECT menu_id FROM sys_menu WHERE parent_id = 3946);

INSERT IGNORE INTO sys_user_menu (user_id, menu_id, tenant_id)
SELECT DISTINCT um.user_id, 3950, um.tenant_id
FROM sys_user_menu um WHERE um.menu_id IN (1530, 1572);

INSERT IGNORE INTO sys_user_menu (user_id, menu_id, tenant_id)
SELECT DISTINCT um.user_id, 3947, um.tenant_id
FROM sys_user_menu um WHERE um.menu_id IN (1431, 1432);

INSERT IGNORE INTO sys_user_menu (user_id, menu_id, tenant_id)
SELECT DISTINCT um.user_id, 3948, um.tenant_id
FROM sys_user_menu um WHERE um.menu_id IN (1433, 3550);

INSERT IGNORE INTO sys_user_menu (user_id, menu_id, tenant_id)
SELECT DISTINCT um.user_id, 3949, um.tenant_id
FROM sys_user_menu um WHERE um.menu_id IN (1263, 1264, 3118, 3730);

-- 租户 super 岗位补目录
INSERT INTO sys_post_menu (post_id, menu_id, tenant_id)
SELECT p.post_id, m.menu_id, p.tenant_id
FROM sys_post p
JOIN sys_menu m ON m.menu_id IN (3946, 3950, 3947, 3948, 3949)
WHERE IFNULL(p.status, '0') = '0'
  AND p.post_code = 'super'
  AND p.tenant_id IS NOT NULL AND TRIM(p.tenant_id) != ''
  AND EXISTS (SELECT 1 FROM sb_customer c WHERE c.customer_id = p.tenant_id AND IFNULL(c.hc_status, '0') = '0')
  AND NOT EXISTS (
    SELECT 1 FROM sys_post_menu x
    WHERE x.post_id = p.post_id AND x.menu_id = m.menu_id
  );

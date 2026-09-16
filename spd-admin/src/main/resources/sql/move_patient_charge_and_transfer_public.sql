-- 科室管理菜单调整（公共/全局 sys_menu）：
-- 1) 患者收费查询(3601) → 科室消耗(1559)，排在科室批量消耗(1565)下面
-- 2) 转科申请(1407) → 科室一级(department)下，与科室审核同级（京东菜单「科室管理」假分组）

SET @department_root := (
  SELECT m.menu_id FROM sys_menu m
  WHERE m.parent_id = 0 AND m.menu_type = 'M' AND m.path = 'department'
  ORDER BY m.menu_id LIMIT 1
);
SET @dept_consume := 1559;
SET @patient_charge := 3601;
SET @transfer_apply := 1407;
SET @batch_consume := 1565;
SET @dept_audit := 1101;

-- 患者收费查询 → 科室消耗，order 紧随批量消耗
UPDATE sys_menu
SET parent_id = @dept_consume,
    order_num = 3,
    update_by = '1',
    update_time = NOW()
WHERE menu_id = @patient_charge;

-- 转科申请 → 科室一级，落在「科室管理」假分组（与科室审核同级）
UPDATE sys_menu
SET parent_id = @department_root,
    order_num = (
      SELECT IFNULL(MAX(x.order_num), 0) + 1
      FROM (SELECT order_num FROM sys_menu WHERE menu_id = @dept_audit) x
    ),
    update_by = '1',
    update_time = NOW()
WHERE menu_id = @transfer_apply;

-- 有患者收费查询权限的，补科室消耗目录（否则京东分栏看不到）
INSERT IGNORE INTO sys_role_menu (role_id, menu_id)
SELECT DISTINCT rm.role_id, @dept_consume
FROM sys_role_menu rm
WHERE rm.menu_id = @patient_charge;

INSERT IGNORE INTO sys_user_menu (user_id, menu_id, tenant_id)
SELECT DISTINCT um.user_id, @dept_consume, COALESCE(NULLIF(um.tenant_id, ''), u.customer_id)
FROM sys_user_menu um
JOIN sys_user u ON u.user_id = um.user_id
WHERE um.menu_id = @patient_charge;

INSERT INTO sys_post_menu (post_id, menu_id, tenant_id)
SELECT DISTINCT pm.post_id, @dept_consume, pm.tenant_id
FROM sys_post_menu pm
WHERE pm.menu_id = @patient_charge
  AND NOT EXISTS (
    SELECT 1 FROM sys_post_menu x
    WHERE x.post_id = pm.post_id AND x.menu_id = @dept_consume
      AND IFNULL(x.tenant_id, '') = IFNULL(pm.tenant_id, '')
  );

INSERT INTO hc_customer_menu (tenant_id, menu_id, status, is_enabled, create_by, create_time)
SELECT DISTINCT h.tenant_id, @dept_consume, '0', '1', 'admin', NOW()
FROM hc_customer_menu h
WHERE h.menu_id = @patient_charge
  AND NOT EXISTS (
    SELECT 1 FROM hc_customer_menu x
    WHERE x.tenant_id = h.tenant_id AND x.menu_id = @dept_consume
  );

-- 显式回填衡水/枣强（若客户存在）
INSERT INTO hc_customer_menu (tenant_id, menu_id, status, is_enabled, create_by, create_time)
SELECT c.customer_id, m.menu_id, '0', '1', 'admin', NOW()
FROM sb_customer c
JOIN (
  SELECT @dept_consume AS menu_id
  UNION ALL SELECT @patient_charge
  UNION ALL SELECT @transfer_apply
) m
WHERE c.customer_id IN ('hengsui-third-001', 'zaoqiang-tcm-001')
  AND IFNULL(c.hc_status, '0') = '0'
  AND c.delete_time IS NULL
  AND NOT EXISTS (
    SELECT 1 FROM hc_customer_menu h
    WHERE h.tenant_id = c.customer_id AND h.menu_id = m.menu_id
  );

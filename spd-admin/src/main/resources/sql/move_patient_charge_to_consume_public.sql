-- 公共：患者收费查询(3601) → 科室消耗(1559) 下
-- 排在扫码消耗 / 科室批量消耗 / 科室消耗追溯之后
-- 影响：主菜单 + 衡水三院 / 枣强中医院等授权回填目录 1559

SET @dept_consume := 1559;
SET @patient_charge := 3601;

UPDATE sys_menu
SET parent_id = @dept_consume,
    order_num = 4,
    update_by = '1',
    update_time = NOW()
WHERE menu_id = @patient_charge
  AND EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = @dept_consume);

-- 有患者收费权限的角色/用户/岗位/租户，补科室消耗目录（否则分栏看不到）
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

INSERT INTO hc_customer_menu (tenant_id, menu_id, status, is_enabled, create_by, create_time)
SELECT t.tenant_id, m.menu_id, '0', '1', 'admin', NOW()
FROM (SELECT 'hengsui-third-001' tenant_id UNION ALL SELECT 'zaoqiang-tcm-001') t
JOIN (SELECT 1559 menu_id UNION ALL SELECT 3601) m
WHERE EXISTS (SELECT 1 FROM sb_customer c WHERE c.customer_id = t.tenant_id AND c.delete_time IS NULL)
  AND NOT EXISTS (
    SELECT 1 FROM hc_customer_menu h
    WHERE h.tenant_id = t.tenant_id AND h.menu_id = m.menu_id
  );

UPDATE hc_customer_menu
SET is_enabled = '1', status = '0'
WHERE tenant_id IN ('hengsui-third-001', 'zaoqiang-tcm-001')
  AND menu_id IN (1559, 3601);

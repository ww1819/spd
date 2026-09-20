-- 平台供应商信息(3877) → 采购报表(3948)下；公共开放；回填衡水/枣强

SET @purchase_report := 3948;
SET @scm_supplier := 3877;

UPDATE sys_menu
SET parent_id = @purchase_report,
    order_num = (
      SELECT IFNULL(MAX(x.order_num), 0) + 1
      FROM (SELECT order_num FROM sys_menu WHERE parent_id = @purchase_report) x
    ),
    is_platform = '0',
    default_open_to_customer = '1',
    update_by = '1',
    update_time = NOW()
WHERE menu_id = @scm_supplier;

UPDATE sys_menu
SET is_platform = '0',
    default_open_to_customer = '1',
    update_by = '1',
    update_time = NOW()
WHERE menu_id = @purchase_report;

INSERT IGNORE INTO sys_role_menu (role_id, menu_id)
SELECT DISTINCT rm.role_id, @purchase_report
FROM sys_role_menu rm
WHERE rm.menu_id = @scm_supplier;

INSERT IGNORE INTO sys_user_menu (user_id, menu_id, tenant_id)
SELECT DISTINCT um.user_id, @purchase_report, COALESCE(NULLIF(um.tenant_id, ''), u.customer_id)
FROM sys_user_menu um
JOIN sys_user u ON u.user_id = um.user_id
WHERE um.menu_id = @scm_supplier;

INSERT INTO sys_post_menu (post_id, menu_id, tenant_id)
SELECT DISTINCT pm.post_id, @purchase_report, pm.tenant_id
FROM sys_post_menu pm
WHERE pm.menu_id = @scm_supplier
  AND NOT EXISTS (
    SELECT 1 FROM sys_post_menu x
    WHERE x.post_id = pm.post_id AND x.menu_id = @purchase_report
      AND IFNULL(x.tenant_id, '') = IFNULL(pm.tenant_id, '')
  );

INSERT INTO hc_customer_menu (tenant_id, menu_id, status, is_enabled, create_by, create_time)
SELECT c.customer_id, m.menu_id, '0', '1', 'admin', NOW()
FROM sb_customer c
JOIN (
  SELECT @purchase_report AS menu_id
  UNION ALL SELECT @scm_supplier
) m
WHERE IFNULL(c.hc_status, '0') = '0'
  AND c.delete_time IS NULL
  AND NOT EXISTS (
    SELECT 1 FROM hc_customer_menu h
    WHERE h.tenant_id = c.customer_id AND h.menu_id = m.menu_id
  );

INSERT INTO hc_customer_menu (tenant_id, menu_id, status, is_enabled, create_by, create_time)
SELECT t.tenant_id, m.menu_id, '0', '1', 'admin', NOW()
FROM (
  SELECT 'hengsui-third-001' AS tenant_id
  UNION ALL SELECT 'zaoqiang-tcm-001'
) t
JOIN (
  SELECT @purchase_report AS menu_id
  UNION ALL SELECT @scm_supplier
) m
WHERE EXISTS (
  SELECT 1 FROM sb_customer c WHERE c.customer_id = t.tenant_id AND c.delete_time IS NULL
)
AND NOT EXISTS (
  SELECT 1 FROM hc_customer_menu h
  WHERE h.tenant_id = t.tenant_id AND h.menu_id = m.menu_id
);

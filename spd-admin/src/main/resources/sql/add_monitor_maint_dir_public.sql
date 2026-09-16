-- 基础资料一级下京东分栏：
-- 1) 二级目录「基础资料」收纳原叶子（除监测类）
-- 2) 其后新建「监测维护」，迁入：耗材对照、定数监测、货位维护、收费项目维护
-- 公共：default_open_to_customer=1 + 回填衡水/枣强等

SET @foundation_root := (
  SELECT m.menu_id FROM sys_menu m
  WHERE m.parent_id = 0 AND m.menu_type = 'M' AND m.path = 'foundation'
  ORDER BY m.menu_id LIMIT 1
);
SET @foundation_biz := 3953;
SET @monitor_maint := 3952;

-- ---------- 1) 二级「基础资料」----------
INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, query,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  @foundation_biz, '基础资料', @foundation_root, 1, 'foundationBiz', NULL, NULL,
  1, 0, 'M', '0', '0', '', 'form',
  'admin', NOW(), '1', NOW(), '基础资料业务叶子归组',
  '0', '1'
FROM DUAL
WHERE @foundation_root IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = @foundation_biz);

UPDATE sys_menu
SET parent_id = @foundation_root,
    order_num = 1,
    menu_name = '基础资料',
    path = 'foundationBiz',
    menu_type = 'M',
    component = NULL,
    visible = '0',
    status = '0',
    is_platform = '0',
    default_open_to_customer = '1',
    update_by = '1',
    update_time = NOW()
WHERE menu_id = @foundation_biz;

-- 除监测四项外，一级下叶子全部归入「基础资料」
UPDATE sys_menu
SET parent_id = @foundation_biz,
    update_by = '1',
    update_time = NOW()
WHERE parent_id = @foundation_root
  AND menu_type = 'C'
  AND menu_id NOT IN (1553, 1549, 2270, 3490);

-- ---------- 2) 「监测维护」并迁入四项 ----------
INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, query,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  @monitor_maint, '监测维护', @foundation_root, 2, 'monitorMaint', NULL, NULL,
  1, 0, 'M', '0', '0', '', 'eye-open',
  'admin', NOW(), '1', NOW(), '耗材对照/定数监测/货位/收费项目',
  '0', '1'
FROM DUAL
WHERE @foundation_root IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = @monitor_maint);

UPDATE sys_menu
SET parent_id = @foundation_root,
    order_num = 2,
    menu_name = '监测维护',
    path = 'monitorMaint',
    menu_type = 'M',
    component = NULL,
    visible = '0',
    status = '0',
    is_platform = '0',
    default_open_to_customer = '1',
    update_by = '1',
    update_time = NOW()
WHERE menu_id = @monitor_maint;

UPDATE sys_menu SET parent_id = @monitor_maint, order_num = 1, update_by = '1', update_time = NOW() WHERE menu_id = 1553; -- 耗材对照
UPDATE sys_menu SET parent_id = @monitor_maint, order_num = 2, update_by = '1', update_time = NOW() WHERE menu_id = 1549; -- 定数监测
UPDATE sys_menu SET parent_id = @monitor_maint, order_num = 3, update_by = '1', update_time = NOW() WHERE menu_id = 2270; -- 货位维护
UPDATE sys_menu SET parent_id = @monitor_maint, order_num = 4, update_by = '1', update_time = NOW() WHERE menu_id = 3490; -- 收费项目维护

-- ---------- 3) 授权回填 ----------
INSERT IGNORE INTO sys_role_menu (role_id, menu_id)
SELECT DISTINCT rm.role_id, @foundation_biz
FROM sys_role_menu rm
WHERE rm.menu_id IN (SELECT menu_id FROM sys_menu WHERE parent_id = @foundation_biz);

INSERT IGNORE INTO sys_role_menu (role_id, menu_id)
SELECT DISTINCT rm.role_id, @monitor_maint
FROM sys_role_menu rm
WHERE rm.menu_id IN (1553, 1549, 2270, 3490);

INSERT IGNORE INTO sys_user_menu (user_id, menu_id, tenant_id)
SELECT DISTINCT um.user_id, @foundation_biz, COALESCE(NULLIF(um.tenant_id, ''), u.customer_id)
FROM sys_user_menu um
JOIN sys_user u ON u.user_id = um.user_id
WHERE um.menu_id IN (SELECT menu_id FROM sys_menu WHERE parent_id = @foundation_biz);

INSERT IGNORE INTO sys_user_menu (user_id, menu_id, tenant_id)
SELECT DISTINCT um.user_id, @monitor_maint, COALESCE(NULLIF(um.tenant_id, ''), u.customer_id)
FROM sys_user_menu um
JOIN sys_user u ON u.user_id = um.user_id
WHERE um.menu_id IN (1553, 1549, 2270, 3490);

INSERT INTO sys_post_menu (post_id, menu_id, tenant_id)
SELECT DISTINCT pm.post_id, @foundation_biz, pm.tenant_id
FROM sys_post_menu pm
WHERE pm.menu_id IN (SELECT menu_id FROM sys_menu WHERE parent_id = @foundation_biz)
  AND NOT EXISTS (
    SELECT 1 FROM sys_post_menu x
    WHERE x.post_id = pm.post_id AND x.menu_id = @foundation_biz
      AND IFNULL(x.tenant_id, '') = IFNULL(pm.tenant_id, '')
  );

INSERT INTO sys_post_menu (post_id, menu_id, tenant_id)
SELECT DISTINCT pm.post_id, @monitor_maint, pm.tenant_id
FROM sys_post_menu pm
WHERE pm.menu_id IN (1553, 1549, 2270, 3490)
  AND NOT EXISTS (
    SELECT 1 FROM sys_post_menu x
    WHERE x.post_id = pm.post_id AND x.menu_id = @monitor_maint
      AND IFNULL(x.tenant_id, '') = IFNULL(pm.tenant_id, '')
  );

INSERT INTO hc_customer_menu (tenant_id, menu_id, status, is_enabled, create_by, create_time)
SELECT c.customer_id, m.menu_id, '0', '1', 'admin', NOW()
FROM sb_customer c
JOIN (SELECT @foundation_biz AS menu_id UNION ALL SELECT @monitor_maint) m
WHERE IFNULL(c.hc_status, '0') = '0'
  AND c.delete_time IS NULL
  AND NOT EXISTS (
    SELECT 1 FROM hc_customer_menu h
    WHERE h.tenant_id = c.customer_id AND h.menu_id = m.menu_id
  );

INSERT INTO hc_customer_menu (tenant_id, menu_id, status, is_enabled, create_by, create_time)
SELECT 'zaoqiang-tcm-001', m.menu_id, '0', '1', 'admin', NOW()
FROM (SELECT @foundation_biz AS menu_id UNION ALL SELECT @monitor_maint) m
WHERE EXISTS (SELECT 1 FROM sb_customer c WHERE c.customer_id = 'zaoqiang-tcm-001' AND c.delete_time IS NULL)
  AND NOT EXISTS (
    SELECT 1 FROM hc_customer_menu h
    WHERE h.tenant_id = 'zaoqiang-tcm-001' AND h.menu_id = m.menu_id
  );

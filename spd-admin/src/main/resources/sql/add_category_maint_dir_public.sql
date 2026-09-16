-- 基础资料一级：新建公共二级「分类维护」
-- 收纳原一级残留叶子中的货位/库房分类/18类/收费项目，并迁入财务分类维护
-- 顺序：基础资料 → 监测维护 → 分类维护

SET @foundation_root := (
  SELECT m.menu_id FROM sys_menu m
  WHERE m.parent_id = 0 AND m.menu_type = 'M' AND m.path = 'foundation'
  ORDER BY m.menu_id LIMIT 1
);
SET @category_maint := 3957;

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, query,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  @category_maint, '分类维护', @foundation_root, 3, 'categoryMaint', NULL, NULL,
  1, 0, 'M', '0', '0', '', 'tree',
  'admin', NOW(), '1', NOW(), '财务分类/材料类别/库房分类/货位/18类/收费项目',
  '0', '1'
FROM DUAL
WHERE @foundation_root IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = @category_maint);

UPDATE sys_menu
SET parent_id = @foundation_root,
    order_num = 3,
    menu_name = '分类维护',
    path = 'categoryMaint',
    menu_type = 'M',
    component = NULL,
    visible = '0',
    status = '0',
    is_platform = '0',
    default_open_to_customer = '1',
    update_by = '1',
    update_time = NOW()
WHERE menu_id = @category_maint;

UPDATE sys_menu SET parent_id = @category_maint, order_num = 1, update_by = '1', update_time = NOW() WHERE menu_id = 1104; -- 财务分类维护
UPDATE sys_menu SET parent_id = @category_maint, order_num = 2, update_by = '1', update_time = NOW() WHERE menu_id = 1103; -- 材料类别维护
UPDATE sys_menu SET parent_id = @category_maint, order_num = 3, update_by = '1', update_time = NOW() WHERE menu_id = 2280; -- 库房分类维护
UPDATE sys_menu SET parent_id = @category_maint, order_num = 4, update_by = '1', update_time = NOW() WHERE menu_id = 2270; -- 货位维护
UPDATE sys_menu SET parent_id = @category_maint, order_num = 5, update_by = '1', update_time = NOW() WHERE menu_id = 2290; -- 18类重点耗材维护
UPDATE sys_menu SET parent_id = @category_maint, order_num = 6, update_by = '1', update_time = NOW() WHERE menu_id = 3490; -- 收费项目维护

INSERT IGNORE INTO sys_role_menu (role_id, menu_id)
SELECT DISTINCT rm.role_id, @category_maint
FROM sys_role_menu rm
WHERE rm.menu_id IN (1104, 1103, 2280, 2270, 2290, 3490);

INSERT IGNORE INTO sys_user_menu (user_id, menu_id, tenant_id)
SELECT DISTINCT um.user_id, @category_maint, COALESCE(NULLIF(um.tenant_id, ''), u.customer_id)
FROM sys_user_menu um
JOIN sys_user u ON u.user_id = um.user_id
WHERE um.menu_id IN (1104, 1103, 2280, 2270, 2290, 3490);

INSERT INTO sys_post_menu (post_id, menu_id, tenant_id)
SELECT DISTINCT pm.post_id, @category_maint, pm.tenant_id
FROM sys_post_menu pm
WHERE pm.menu_id IN (1104, 1103, 2280, 2270, 2290, 3490)
  AND NOT EXISTS (
    SELECT 1 FROM sys_post_menu x
    WHERE x.post_id = pm.post_id AND x.menu_id = @category_maint
      AND IFNULL(x.tenant_id, '') = IFNULL(pm.tenant_id, '')
  );

INSERT INTO hc_customer_menu (tenant_id, menu_id, status, is_enabled, create_by, create_time)
SELECT c.customer_id, @category_maint, '0', '1', 'admin', NOW()
FROM sb_customer c
WHERE IFNULL(c.hc_status, '0') = '0'
  AND c.delete_time IS NULL
  AND NOT EXISTS (
    SELECT 1 FROM hc_customer_menu h
    WHERE h.tenant_id = c.customer_id AND h.menu_id = @category_maint
  );

INSERT INTO hc_customer_menu (tenant_id, menu_id, status, is_enabled, create_by, create_time)
SELECT 'zaoqiang-tcm-001', @category_maint, '0', '1', 'admin', NOW()
FROM DUAL
WHERE EXISTS (SELECT 1 FROM sb_customer c WHERE c.customer_id = 'zaoqiang-tcm-001' AND c.delete_time IS NULL)
  AND NOT EXISTS (
    SELECT 1 FROM hc_customer_menu h
    WHERE h.tenant_id = 'zaoqiang-tcm-001' AND h.menu_id = @category_maint
  );

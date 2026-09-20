-- 高值管理分栏调整（公共）：
-- 1) 备货管理后新建「跟台管理」，迁入跟台管理菜单(1526)
-- 2) 高值备货库存(1194) → 备货管理(1192)
-- 3) 「追溯管理」目录(3960) + 核销确认/追溯页：见 reorg_gz_trace_dir_with_confirm_public.sql
-- 4) 退库审核(3858) 已废弃（与备货退库 1197 重复），见 remove_gz_goods_audit_menu_3858_public.sql

SET @gz_root := (
  SELECT m.menu_id FROM sys_menu m
  WHERE m.parent_id = 0 AND m.menu_type = 'M' AND m.path = 'gz'
  ORDER BY m.menu_id LIMIT 1
);
SET @stockup := 1192;
SET @follow_dir := 3959;

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, query,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  @follow_dir, '跟台管理', @gz_root, 2, 'followMgmt', NULL, NULL,
  1, 0, 'M', '0', '0', '', 'guide',
  'admin', NOW(), '1', NOW(), '跟台管理分组',
  '0', '1'
FROM DUAL
WHERE @gz_root IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = @follow_dir);

UPDATE sys_menu SET parent_id=@gz_root, order_num=1, is_platform='0', default_open_to_customer='1', update_by='1', update_time=NOW() WHERE menu_id=@stockup;
UPDATE sys_menu SET parent_id=@gz_root, order_num=2, menu_name='跟台管理', path='followMgmt', menu_type='M', component=NULL, visible='0', status='0', is_platform='0', default_open_to_customer='1', update_by='1', update_time=NOW() WHERE menu_id=@follow_dir;
UPDATE sys_menu SET parent_id=@gz_root, order_num=3, update_by='1', update_time=NOW() WHERE menu_id=1203;
UPDATE sys_menu SET parent_id=@gz_root, order_num=4, update_by='1', update_time=NOW() WHERE menu_id=1256;

-- 跟台管理页 → 跟台管理目录
UPDATE sys_menu SET parent_id=@follow_dir, order_num=1, is_platform='0', default_open_to_customer='1', update_by='1', update_time=NOW() WHERE menu_id=1526;

-- 高值备货库存 → 备货管理
UPDATE sys_menu SET parent_id=@stockup, order_num=8, is_platform='0', default_open_to_customer='1', update_by='1', update_time=NOW() WHERE menu_id=1194;

INSERT IGNORE INTO sys_role_menu (role_id, menu_id)
SELECT DISTINCT rm.role_id, @follow_dir FROM sys_role_menu rm WHERE rm.menu_id=1526;
INSERT IGNORE INTO sys_role_menu (role_id, menu_id)
SELECT DISTINCT rm.role_id, @stockup FROM sys_role_menu rm WHERE rm.menu_id=1194;

INSERT IGNORE INTO sys_user_menu (user_id, menu_id, tenant_id)
SELECT DISTINCT um.user_id, @follow_dir, COALESCE(NULLIF(um.tenant_id,''), u.customer_id)
FROM sys_user_menu um JOIN sys_user u ON u.user_id=um.user_id WHERE um.menu_id=1526;
INSERT IGNORE INTO sys_user_menu (user_id, menu_id, tenant_id)
SELECT DISTINCT um.user_id, @stockup, COALESCE(NULLIF(um.tenant_id,''), u.customer_id)
FROM sys_user_menu um JOIN sys_user u ON u.user_id=um.user_id WHERE um.menu_id=1194;

INSERT INTO sys_post_menu (post_id, menu_id, tenant_id)
SELECT DISTINCT pm.post_id, d.menu_id, pm.tenant_id
FROM sys_post_menu pm
JOIN (
  SELECT 1526 src, 3959 menu_id UNION ALL
  SELECT 1194, 1192
) d ON pm.menu_id = d.src
WHERE NOT EXISTS (
  SELECT 1 FROM sys_post_menu x
  WHERE x.post_id=pm.post_id AND x.menu_id=d.menu_id
    AND IFNULL(x.tenant_id,'')=IFNULL(pm.tenant_id,'')
);

INSERT INTO hc_customer_menu (tenant_id, menu_id, status, is_enabled, create_by, create_time)
SELECT c.customer_id, m.menu_id, '0', '1', 'admin', NOW()
FROM sb_customer c
JOIN (SELECT 3959 menu_id UNION ALL SELECT 1192) m
WHERE IFNULL(c.hc_status,'0')='0' AND c.delete_time IS NULL
  AND NOT EXISTS (
    SELECT 1 FROM hc_customer_menu h
    WHERE h.tenant_id=c.customer_id AND h.menu_id=m.menu_id
  );

INSERT INTO hc_customer_menu (tenant_id, menu_id, status, is_enabled, create_by, create_time)
SELECT t.tenant_id, m.menu_id, '0', '1', 'admin', NOW()
FROM (SELECT 'hengsui-third-001' tenant_id UNION ALL SELECT 'zaoqiang-tcm-001') t
JOIN (SELECT 3959 menu_id) m
WHERE EXISTS (SELECT 1 FROM sb_customer c WHERE c.customer_id=t.tenant_id AND c.delete_time IS NULL)
  AND NOT EXISTS (
    SELECT 1 FROM hc_customer_menu h
    WHERE h.tenant_id=t.tenant_id AND h.menu_id=m.menu_id
  );

-- 增量：云平台编码绑定 — 删除权限 caigou:scmBind:remove（单条/批量逻辑删除接口）
-- 与 menu.sql 中「云平台编码绑定」段一致；已全量执行过更新后 menu.sql 的可跳过本文件

SET @scm_bind_c := (
  SELECT menu_id FROM sys_menu WHERE menu_type = 'C' AND component = 'caigou/scmBind/index' ORDER BY menu_id DESC LIMIT 1
);

INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, `query`,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, update_by, update_time, remark,
  is_platform, default_open_to_customer
)
SELECT
  (SELECT IFNULL(MAX(menu_id), 0) + 1 FROM (SELECT menu_id FROM sys_menu) t),
  '云平台绑定删除',
  @scm_bind_c,
  3,
  '#', '', NULL,
  1, 0, 'F', '0', '0', 'caigou:scmBind:remove', '#',
  'admin', NOW(), '1', NOW(), '供应商平台编码绑定逻辑删除；DELETE /caigou/scmBind/supplier/{ids}',
  '0', '1'
FROM DUAL
WHERE @scm_bind_c IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM sys_menu WHERE parent_id = @scm_bind_c AND perms = 'caigou:scmBind:remove');

-- 租户默认开放菜单（与 menu.sql hc_customer_menu 段一致）
INSERT INTO hc_customer_menu (tenant_id, menu_id, status, is_enabled, create_by, create_time)
SELECT c.customer_id, m.menu_id, '0', '1', 'admin', NOW()
FROM sb_customer c
JOIN sys_menu m ON m.perms = 'caigou:scmBind:remove' AND m.menu_type = 'F'
WHERE c.hc_status = '0'
  AND NOT EXISTS (
    SELECT 1 FROM hc_customer_menu h
    WHERE h.tenant_id = c.customer_id AND h.menu_id = m.menu_id
  );

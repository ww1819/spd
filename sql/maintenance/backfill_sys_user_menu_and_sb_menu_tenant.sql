-- =============================================================================
-- 回填租户字段：sys_user_menu.tenant_id、sb_work_group_menu / sb_user_permission_menu 的 customer_id
-- =============================================================================
-- 背景：历史数据在增加 tenant_id/customer_id 列后可能为空；应用层已改为写入租户。
-- 用法：在目标库备份后执行；可按需只执行其中某段。
-- =============================================================================

-- ---------- 1) 耗材：sys_user_menu.tenant_id ← sys_user.customer_id ----------
UPDATE sys_user_menu um
INNER JOIN sys_user u ON u.user_id = um.user_id AND IFNULL(u.del_flag, '0') = '0'
SET um.tenant_id = u.customer_id
WHERE (um.tenant_id IS NULL OR um.tenant_id = '')
  AND u.customer_id IS NOT NULL
  AND TRIM(u.customer_id) != '';

-- ---------- 2) 设备：sb_work_group_menu.customer_id ← sb_work_group.customer_id ----------
UPDATE sb_work_group_menu wgm
INNER JOIN sb_work_group wg ON wg.group_id = wgm.group_id
SET wgm.customer_id = wg.customer_id
WHERE (wgm.customer_id IS NULL OR wgm.customer_id = '')
  AND wg.customer_id IS NOT NULL
  AND TRIM(wg.customer_id) != '';

-- ---------- 3) 设备：sb_user_permission_menu.customer_id ← sys_user.customer_id ----------
UPDATE sb_user_permission_menu upm
INNER JOIN sys_user u ON u.user_id = upm.user_id AND IFNULL(u.del_flag, '0') = '0'
SET upm.customer_id = u.customer_id
WHERE (upm.customer_id IS NULL OR upm.customer_id = '')
  AND u.customer_id IS NOT NULL
  AND TRIM(u.customer_id) != '';

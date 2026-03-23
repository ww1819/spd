-- =============================================================================
-- 将「客户已启用菜单」同步到租户用户的 sys_user_menu（仅补全缺失行，不删除）
-- =============================================================================
-- 背景：
--   后端 @PreAuthorize / v-hasPermi 使用的 LoginUser.permissions 来自
--   SysPermissionService#getMenuPermission → sys_user_menu + sys_menu.perms（非管理员）。
--   hc_customer_menu 只表示租户「可启用」哪些功能，不会自动写入每个用户的 sys_user_menu。
--   若只执行了菜单脚本回填 hc_customer_menu，而未在用户管理中勾选对应菜单，会出现
--   「没有权限 / 未知错误」。
-- 用法：
--   1) 先确保已执行各 maintenance 脚本插入 sys_menu 与 hc_customer_menu。
--   2) 在目标库执行本脚本。
--   3) 受影响用户需重新登录，或调用 getInfo（若已实现刷新 Redis 中的 permissions）。
-- 说明：
--   - 写入列含 tenant_id（与 sys_user.customer_id 一致），便于多租户追溯与排查。
--   - 仅处理 customer_id 非空的租户用户；del_flag='0'。
--   - 仅同步 hc_customer_menu 中 is_enabled='1' 且 status='0'（正常未暂停）的菜单。
--   - 仅同步 sys_menu.status='0' 且非平台菜单（is_platform 不为 1），与 selectMenuPermsByUserId 一致。
-- =============================================================================

INSERT INTO sys_user_menu (user_id, menu_id, tenant_id)
SELECT u.user_id, hcm.menu_id, u.customer_id
FROM sys_user u
INNER JOIN hc_customer_menu hcm
  ON hcm.tenant_id = u.customer_id
  AND hcm.is_enabled = '1'
  AND (hcm.status = '0' OR hcm.status IS NULL)
INNER JOIN sys_menu m
  ON m.menu_id = hcm.menu_id
  AND m.status = '0'
  AND (m.is_platform IS NULL OR m.is_platform != '1')
WHERE u.customer_id IS NOT NULL
  AND u.customer_id != ''
  AND u.del_flag = '0'
  AND NOT EXISTS (
    SELECT 1 FROM sys_user_menu um
    WHERE um.user_id = u.user_id AND um.menu_id = hcm.menu_id
  );

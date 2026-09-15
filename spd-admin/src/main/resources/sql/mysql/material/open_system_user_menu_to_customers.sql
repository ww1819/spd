-- ========== 系统管理 / 用户管理：公共开通（含枣强等全部启用客户） ==========
-- 页面：system/user/index（共享页；枣强仍按 isZqTcmTenant 隐藏手工新增/新增导入）
-- 1) 菜单标记默认对客户开放
UPDATE sys_menu m
INNER JOIN (
  SELECT menu_id FROM sys_menu WHERE menu_id = 1 AND menu_type = 'M'
  UNION
  SELECT menu_id FROM sys_menu WHERE perms = 'system:user:list' AND menu_type = 'C'
  UNION
  SELECT c.menu_id FROM sys_menu c
  INNER JOIN sys_menu p ON p.menu_id = c.parent_id AND p.perms = 'system:user:list' AND p.menu_type = 'C'
) t ON t.menu_id = m.menu_id
SET m.default_open_to_customer = '1',
    m.update_time = NOW()
WHERE IFNULL(m.status, '0') = '0'
  AND (m.is_platform IS NULL OR m.is_platform != '1')
  AND (m.default_open_to_customer IS NULL OR m.default_open_to_customer != '1');

-- 2) 回填 hc_customer_menu（所有 hc_status=0 客户，含 zaoqiang-tcm-001）
INSERT INTO hc_customer_menu (tenant_id, menu_id, status, is_enabled, create_by, create_time)
SELECT c.customer_id, m.menu_id, '0', '1', 'admin', NOW()
FROM sb_customer c
JOIN (
  SELECT menu_id FROM sys_menu WHERE menu_id = 1 AND menu_type = 'M'
  UNION
  SELECT menu_id FROM sys_menu WHERE perms = 'system:user:list' AND menu_type = 'C'
  UNION
  SELECT c2.menu_id FROM sys_menu c2
  INNER JOIN sys_menu p ON p.menu_id = c2.parent_id AND p.perms = 'system:user:list' AND p.menu_type = 'C'
) m
WHERE IFNULL(c.hc_status, '0') = '0'
  AND NOT EXISTS (
    SELECT 1 FROM hc_customer_menu h
    WHERE h.tenant_id = c.customer_id AND h.menu_id = m.menu_id
  );

-- 3) 已有「系统管理」下其它菜单授权的用户：补齐用户管理及其按钮到 sys_user_menu
INSERT INTO sys_user_menu (user_id, menu_id, tenant_id)
SELECT DISTINCT um.user_id, tgt.menu_id, um.tenant_id
FROM sys_user_menu um
INNER JOIN sys_menu sibling ON sibling.menu_id = um.menu_id AND sibling.menu_type IN ('C', 'M')
INNER JOIN (
  SELECT menu_id FROM sys_menu WHERE menu_id = 1 AND menu_type = 'M'
  UNION
  SELECT menu_id FROM sys_menu WHERE perms = 'system:user:list' AND menu_type = 'C'
  UNION
  SELECT c2.menu_id FROM sys_menu c2
  INNER JOIN sys_menu p ON p.menu_id = c2.parent_id AND p.perms = 'system:user:list' AND p.menu_type = 'C'
) tgt
WHERE um.tenant_id IS NOT NULL AND TRIM(um.tenant_id) != ''
  AND (
    sibling.menu_id = 1
    OR sibling.parent_id = 1
    OR EXISTS (
      SELECT 1 FROM sys_menu p2
      WHERE p2.menu_id = sibling.parent_id AND p2.menu_id = 1
    )
  )
  AND NOT EXISTS (
    SELECT 1 FROM sys_user_menu x
    WHERE x.user_id = um.user_id AND x.menu_id = tgt.menu_id
  );

-- 4) 租户 super 岗位补齐用户管理菜单
INSERT INTO sys_post_menu (post_id, menu_id, tenant_id)
SELECT p.post_id, m.menu_id, p.tenant_id
FROM sys_post p
JOIN (
  SELECT menu_id FROM sys_menu WHERE menu_id = 1 AND menu_type = 'M'
  UNION
  SELECT menu_id FROM sys_menu WHERE perms = 'system:user:list' AND menu_type = 'C'
  UNION
  SELECT c2.menu_id FROM sys_menu c2
  INNER JOIN sys_menu p2 ON p2.menu_id = c2.parent_id AND p2.perms = 'system:user:list' AND p2.menu_type = 'C'
) m
WHERE IFNULL(p.status, '0') = '0'
  AND p.post_code = 'super'
  AND p.tenant_id IS NOT NULL AND TRIM(p.tenant_id) != ''
  AND EXISTS (SELECT 1 FROM sb_customer c WHERE c.customer_id = p.tenant_id AND IFNULL(c.hc_status, '0') = '0')
  AND NOT EXISTS (
    SELECT 1 FROM sys_post_menu x
    WHERE x.post_id = p.post_id AND x.menu_id = m.menu_id
  );

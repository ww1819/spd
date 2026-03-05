-- ============================================================
-- 补充现有租户：super 组与 super_01 的默认菜单权限及绑定关系
-- 1）新租户/super 组默认包含系统设置下的所有权限，不包含平台管理功能
-- 2）super_01 已在 super 组内，通过组获得权限
-- 执行一次即可，对已有租户做“缺什么补什么”，不删除已有数据
-- 依赖 MySQL 8.0+（WITH RECURSIVE）
-- ============================================================

-- 系统设置目录下且非平台管理的菜单（与 Java 中 selectMenuIdsSystemSettingsNonPlatform 一致）
-- 1. 补充客户菜单：为每个租户开通「系统设置下、非平台管理」菜单（若尚未存在）
INSERT INTO sb_customer_menu (customer_id, menu_id, status, is_enabled, create_by, create_time)
SELECT c.customer_id, t.menu_id, '0', '1', 'admin', NOW()
FROM sb_customer c
CROSS JOIN (
  WITH RECURSIVE tree AS (
    SELECT menu_id FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-000000001450'
    UNION ALL
    SELECT m.menu_id FROM sb_menu m INNER JOIN tree t ON m.parent_id = t.menu_id
  )
  SELECT t.menu_id FROM tree t
  INNER JOIN sb_menu m ON m.menu_id = t.menu_id
  WHERE (m.is_platform_only = '0' OR m.is_platform_only IS NULL)
    AND m.status = '0'
    AND (m.delete_time IS NULL)
) t
WHERE (c.delete_time IS NULL)
  AND NOT EXISTS (
    SELECT 1 FROM sb_customer_menu cm
    WHERE cm.customer_id = c.customer_id AND cm.menu_id = t.menu_id AND (cm.delete_time IS NULL)
  );
/

-- 2. 补充 super 组菜单权限：为每个租户的 super 组赋予「系统设置下、非平台管理」菜单（若尚未存在）
INSERT INTO sb_work_group_menu (id, group_id, menu_id, customer_id, create_by, create_time)
SELECT UUID(), g.group_id, t.menu_id, g.customer_id, 'admin', NOW()
FROM sb_work_group g
CROSS JOIN (
  WITH RECURSIVE tree AS (
    SELECT menu_id FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-000000001450'
    UNION ALL
    SELECT m.menu_id FROM sb_menu m INNER JOIN tree t ON m.parent_id = t.menu_id
  )
  SELECT t.menu_id FROM tree t
  INNER JOIN sb_menu m ON m.menu_id = t.menu_id
  WHERE (m.is_platform_only = '0' OR m.is_platform_only IS NULL)
    AND m.status = '0'
    AND (m.delete_time IS NULL)
) t
WHERE g.group_key = 'super'
  AND (g.delete_time IS NULL)
  AND NOT EXISTS (
    SELECT 1 FROM sb_work_group_menu wgm
    WHERE wgm.group_id = g.group_id AND wgm.menu_id = t.menu_id AND (wgm.delete_time IS NULL)
  );
/

-- 3. 补充 super_01 与 super 组的绑定：确保每个租户的 super_01 用户归属 super 组（若尚未存在）
INSERT INTO sb_work_group_user (group_id, user_id, customer_id, create_by, create_time)
SELECT g.group_id, u.user_id, g.customer_id, 'admin', NOW()
FROM sb_work_group g
INNER JOIN sys_user u ON u.customer_id = g.customer_id AND u.user_name = 'super_01' AND IFNULL(u.del_flag,'0') = '0'
WHERE g.group_key = 'super'
  AND (g.delete_time IS NULL)
  AND NOT EXISTS (
    SELECT 1 FROM sb_work_group_user wgu
    WHERE wgu.group_id = g.group_id AND wgu.user_id = u.user_id AND (wgu.delete_time IS NULL)
  );
/

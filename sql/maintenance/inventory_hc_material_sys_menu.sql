-- =============================================================================
-- 耗材系统 sys_menu 梳理与重复项扫描（只读，不修改数据）
-- =============================================================================
-- 执行前：建议备份；可在测试库先跑。
-- 代码库菜单脚本：spd-admin/src/main/resources/sql/mysql/material/menu.sql
-- 合并重复项存储过程：material/procedure.sql → sp_hc_merge_sys_menu
--   使用说明：dedupe_hc_material_sys_menu.sql、fix_sys_menu_dupes_from_export_20260321.sql
--
-- 关联表（迁移菜单 ID 时常涉及）：
--   sys_role_menu、sys_user_menu、hc_customer_menu、sys_post_menu、
--   hc_user_permission_menu、hc_customer_menu_status_log、hc_customer_menu_period_log
--
-- 「耗材」权限前缀（筛选用，可按需增删）：
--   warehouse / foundation / finance / hc / caigou / inWarehouse / department / gzOrder / sb
-- =============================================================================

-- 1) 按权限前缀统计（仅 status=0 正常菜单）
SELECT s.perm_prefix, COUNT(*) AS cnt
FROM (
  SELECT
    CASE
      WHEN m.perms IS NULL OR TRIM(m.perms) = '' THEN '(空)'
      WHEN m.perms LIKE 'warehouse:%' THEN 'warehouse'
      WHEN m.perms LIKE 'foundation:%' THEN 'foundation'
      WHEN m.perms LIKE 'finance:%' THEN 'finance'
      WHEN m.perms LIKE 'hc:%' THEN 'hc'
      WHEN m.perms LIKE 'caigou:%' THEN 'caigou'
      WHEN m.perms LIKE 'inWarehouse:%' THEN 'inWarehouse'
      WHEN m.perms LIKE 'department:%' OR m.perms LIKE 'dept:%' THEN 'department'
      WHEN m.perms LIKE 'gzOrder:%' THEN 'gzOrder'
      WHEN m.perms LIKE 'sb:%' THEN 'sb'
      WHEN m.perms LIKE 'material:%' THEN 'material'
      WHEN m.perms LIKE 'monitor:%' THEN 'monitor'
      WHEN m.perms LIKE 'system:%' THEN 'system'
      ELSE 'other'
    END AS perm_prefix
  FROM sys_menu m
  WHERE IFNULL(m.status, '0') = '0'
) s
GROUP BY s.perm_prefix
ORDER BY cnt DESC;
/

-- 2) 同一 perms 出现多条（非空）— 需合并或改 perms（勿盲目合并 C/F 混用行）
SELECT
  m.perms,
  COUNT(*) AS cnt,
  GROUP_CONCAT(m.menu_id ORDER BY m.menu_id) AS menu_ids,
  GROUP_CONCAT(CONCAT(m.menu_id, ':', m.menu_type, ':', IFNULL(m.menu_name, '')) ORDER BY m.menu_id SEPARATOR ' | ') AS detail
FROM sys_menu m
WHERE m.perms IS NOT NULL AND TRIM(m.perms) <> ''
  AND IFNULL(m.status, '0') = '0'
GROUP BY m.perms
HAVING COUNT(*) > 1
ORDER BY cnt DESC, m.perms;
/

-- 3) 同一父菜单 + path 重复（目录/菜单路由，C/M）
SELECT
  m.parent_id,
  m.path,
  m.menu_type,
  COUNT(*) AS cnt,
  GROUP_CONCAT(m.menu_id ORDER BY m.menu_id) AS menu_ids,
  GROUP_CONCAT(m.menu_name ORDER BY m.menu_id SEPARATOR ' | ') AS names
FROM sys_menu m
WHERE IFNULL(m.status, '0') = '0'
  AND m.menu_type IN ('C', 'M')
  AND IFNULL(m.path, '') <> ''
GROUP BY m.parent_id, m.path, m.menu_type
HAVING COUNT(*) > 1;
/

-- 4) 同一 component 多条 C 菜单（页面重复，侧栏易出现两条）
SELECT
  m.component,
  COUNT(*) AS cnt,
  GROUP_CONCAT(m.menu_id ORDER BY m.menu_id) AS menu_ids,
  GROUP_CONCAT(m.menu_name ORDER BY m.menu_id SEPARATOR ' | ') AS names
FROM sys_menu m
WHERE IFNULL(m.status, '0') = '0'
  AND m.menu_type = 'C'
  AND IFNULL(m.component, '') <> ''
GROUP BY m.component
HAVING COUNT(*) > 1;
/

-- 5) 同一父下、相同 perms 的 F 按钮重复
SELECT
  m.parent_id,
  m.perms,
  COUNT(*) AS cnt,
  GROUP_CONCAT(m.menu_id ORDER BY m.menu_id) AS menu_ids
FROM sys_menu m
WHERE IFNULL(m.status, '0') = '0'
  AND m.menu_type = 'F'
  AND m.perms IS NOT NULL AND TRIM(m.perms) <> ''
GROUP BY m.parent_id, m.perms
HAVING COUNT(*) > 1;
/

-- 6) parent_id 指向不存在的菜单（孤儿）
SELECT m.menu_id, m.menu_name, m.parent_id, m.path, m.component
FROM sys_menu m
WHERE m.parent_id IS NOT NULL
  AND m.parent_id != 0
  AND NOT EXISTS (SELECT 1 FROM sys_menu p WHERE p.menu_id = m.parent_id);
/

-- 7) 生成「按 perms 合并到 MIN(menu_id)」的候选 CALL（仅输出，执行前请人工核对）
--    C/F 混用、M/C 混用等不能简单合并，需见 README_hc_material_menu_cleanup_20260321.md
SELECT CONCAT(
  'CALL sp_hc_merge_sys_menu(',
  t.keep_id,
  ', ',
  m.menu_id,
  ');'
) AS merge_stmt_sql
FROM sys_menu m
INNER JOIN (
  SELECT perms, MIN(menu_id) AS keep_id
  FROM sys_menu
  WHERE perms IS NOT NULL AND TRIM(perms) <> ''
    AND IFNULL(status, '0') = '0'
  GROUP BY perms
  HAVING COUNT(*) > 1
) t ON m.perms = t.perms AND m.menu_id > t.keep_id
WHERE IFNULL(m.status, '0') = '0'
ORDER BY m.perms, m.menu_id;
/

-- 8) hc_customer_menu 中存在但 sys_menu 已不存在的孤儿引用
SELECT h.tenant_id, h.menu_id
FROM hc_customer_menu h
LEFT JOIN sys_menu m ON m.menu_id = h.menu_id
WHERE m.menu_id IS NULL;
/

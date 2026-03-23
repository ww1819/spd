-- 诊断：耗材基础资料是否重复出现「同 component、多菜单」导致侧栏出现两条厂家/财务分类
-- 正常：每个 component 仅一条 C 类型菜单（menu_type='C'）。
-- 执行前请备份 sys_menu；若需停用重复项，请人工核对后再执行 UPDATE。
-- 全库梳理与合并脚本：inventory_hc_material_sys_menu.sql、fix_sys_menu_dupes_from_export_20260321.sql

-- 1) 查看重复（同一 component 多条 C 菜单）
SELECT m.component, COUNT(*) AS cnt, GROUP_CONCAT(m.menu_id ORDER BY m.menu_id) AS menu_ids,
       GROUP_CONCAT(m.menu_name ORDER BY m.menu_id SEPARATOR ' | ') AS names
FROM sys_menu m
WHERE IFNULL(m.status, '0') = '0'
  AND m.menu_type = 'C'
  AND IFNULL(m.component, '') <> ''
  AND m.component IN (
    'foundation/factory/index',
    'foundation/financeCategory/index',
    'foundation/warehouseCategory/index',
    'foundation/supplier/index'
  )
GROUP BY m.component
HAVING COUNT(*) > 1;

-- 2) 可选：将重复行中「非最小 menu_id」的菜单置为停用（请取消注释并确认后再执行）
-- UPDATE sys_menu m
-- INNER JOIN (
--   SELECT component, MIN(menu_id) AS keep_id
--   FROM sys_menu
--   WHERE menu_type = 'C' AND IFNULL(status, '0') = '0'
--     AND component IN ('foundation/factory/index', 'foundation/financeCategory/index')
--   GROUP BY component
--   HAVING COUNT(*) > 1
-- ) t ON m.component = t.component AND m.menu_id > t.keep_id
-- SET m.status = '1', m.visible = '1', m.update_time = NOW();

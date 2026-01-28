-- ============================================
-- 申购单审核功能 - 菜单配置
-- 创建时间：2025-01-27
-- ============================================

-- ============================================
-- 一、查询科室申购菜单ID（作为参考）
-- ============================================
-- 执行以下SQL查询科室申购菜单的ID
-- SELECT menu_id, menu_name, parent_id FROM sys_menu WHERE menu_name = '科室申购';
-- 查询结果：menu_id = 1530, parent_id = 1062（科室管理）

-- ============================================
-- 二、配置菜单和权限
-- ============================================

-- 2.1 插入主菜单（申购单审核）
-- 放在科室申购同级，parent_id = 1062
INSERT INTO `sys_menu` (
  `menu_id`, 
  `menu_name`, 
  `parent_id`, 
  `order_num`, 
  `path`, 
  `component`, 
  `is_frame`, 
  `is_cache`, 
  `menu_type`, 
  `visible`, 
  `status`, 
  `perms`, 
  `icon`, 
  `create_by`, 
  `create_time`, 
  `update_by`, 
  `update_time`, 
  `remark`
) VALUES (
  (SELECT IFNULL(MAX(menu_id), 0) + 1 FROM (SELECT menu_id FROM sys_menu) AS temp),
  '申购单审核',
  1062,
  (SELECT IFNULL(MAX(order_num), 0) + 1 FROM sys_menu WHERE parent_id = 1062),
  'dPurchaseAudit',
  'department/dPurchaseAudit/index',
  1,
  0,
  'C',
  '0',
  '0',
  'department:purchaseAudit:list',
  'fa fa-check-circle',
  'admin',
  NOW(),
  '',
  NULL,
  '申购单审核菜单'
);

-- 2.2 插入按钮权限
-- 查询按钮权限
INSERT INTO `sys_menu` (
  `menu_id`, 
  `menu_name`, 
  `parent_id`, 
  `order_num`, 
  `path`, 
  `component`, 
  `is_frame`, 
  `is_cache`, 
  `menu_type`, 
  `visible`, 
  `status`, 
  `perms`, 
  `icon`, 
  `create_by`, 
  `create_time`, 
  `update_by`, 
  `update_time`, 
  `remark`
) 
SELECT 
  (SELECT IFNULL(MAX(menu_id), 0) + 1 FROM (SELECT menu_id FROM sys_menu) AS temp),
  '申购单审核查询',
  menu_id,
  1,
  '#',
  '',
  1,
  0,
  'F',
  '0',
  '0',
  'department:purchaseAudit:list',
  '#',
  'admin',
  NOW(),
  '',
  NULL,
  ''
FROM sys_menu WHERE menu_name = '申购单审核'
UNION ALL
SELECT 
  (SELECT IFNULL(MAX(menu_id), 0) + 2 FROM (SELECT menu_id FROM sys_menu) AS temp),
  '申购单审核审核',
  menu_id,
  2,
  '#',
  '',
  1,
  0,
  'F',
  '0',
  '0',
  'department:purchaseAudit:audit',
  '#',
  'admin',
  NOW(),
  '',
  NULL,
  ''
FROM sys_menu WHERE menu_name = '申购单审核'
UNION ALL
SELECT 
  (SELECT IFNULL(MAX(menu_id), 0) + 3 FROM (SELECT menu_id FROM sys_menu) AS temp),
  '申购单审核驳回',
  menu_id,
  3,
  '#',
  '',
  1,
  0,
  'F',
  '0',
  '0',
  'department:purchaseAudit:reject',
  '#',
  'admin',
  NOW(),
  '',
  NULL,
  ''
FROM sys_menu WHERE menu_name = '申购单审核'
UNION ALL
SELECT 
  (SELECT IFNULL(MAX(menu_id), 0) + 4 FROM (SELECT menu_id FROM sys_menu) AS temp),
  '申购单审核导出',
  menu_id,
  4,
  '#',
  '',
  1,
  0,
  'F',
  '0',
  '0',
  'department:purchaseAudit:export',
  '#',
  'admin',
  NOW(),
  '',
  NULL,
  ''
FROM sys_menu WHERE menu_name = '申购单审核';

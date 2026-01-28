-- ============================================
-- 申领单审核功能 - 菜单配置
-- 创建时间：2025-01-27
-- ============================================

-- ============================================
-- 一、查询科室消耗菜单ID（作为父菜单）
-- ============================================
-- 执行以下SQL查询科室消耗菜单的ID
-- SELECT menu_id, menu_name, parent_id FROM sys_menu WHERE menu_name = '科室消耗';
-- 查询结果：menu_id = 1559

-- ============================================
-- 二、配置菜单和权限
-- ============================================

-- 2.1 插入主菜单（申领单审核）
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
  '申领单审核',
  1559,
  (SELECT IFNULL(MAX(order_num), 0) + 1 FROM sys_menu WHERE parent_id = 1559),
  'dApplyAudit',
  'department/dApplyAudit/index',
  1,
  0,
  'C',
  '0',
  '0',
  'department:dApplyAudit:list',
  'fa fa-check-circle',
  'admin',
  NOW(),
  '',
  NULL,
  '申领单审核菜单'
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
  '申领单审核查询',
  menu_id,
  1,
  '#',
  '',
  1,
  0,
  'F',
  '0',
  '0',
  'department:dApplyAudit:list',
  '#',
  'admin',
  NOW(),
  '',
  NULL,
  ''
FROM sys_menu WHERE menu_name = '申领单审核'
UNION ALL
SELECT 
  (SELECT IFNULL(MAX(menu_id), 0) + 2 FROM (SELECT menu_id FROM sys_menu) AS temp),
  '申领单审核审核',
  menu_id,
  2,
  '#',
  '',
  1,
  0,
  'F',
  '0',
  '0',
  'department:dApplyAudit:audit',
  '#',
  'admin',
  NOW(),
  '',
  NULL,
  ''
FROM sys_menu WHERE menu_name = '申领单审核'
UNION ALL
SELECT 
  (SELECT IFNULL(MAX(menu_id), 0) + 3 FROM (SELECT menu_id FROM sys_menu) AS temp),
  '申领单审核驳回',
  menu_id,
  3,
  '#',
  '',
  1,
  0,
  'F',
  '0',
  '0',
  'department:dApplyAudit:reject',
  '#',
  'admin',
  NOW(),
  '',
  NULL,
  ''
FROM sys_menu WHERE menu_name = '申领单审核'
UNION ALL
SELECT 
  (SELECT IFNULL(MAX(menu_id), 0) + 4 FROM (SELECT menu_id FROM sys_menu) AS temp),
  '申领单审核导出',
  menu_id,
  4,
  '#',
  '',
  1,
  0,
  'F',
  '0',
  '0',
  'department:dApplyAudit:export',
  '#',
  'admin',
  NOW(),
  '',
  NULL,
  ''
FROM sys_menu WHERE menu_name = '申领单审核';

-- ============================================
-- 三、配置编号生成规则（如果需要）
-- ============================================
-- 申领单审核使用科室申领的编号规则，无需单独配置

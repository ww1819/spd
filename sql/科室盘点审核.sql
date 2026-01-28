-- ============================================
-- 科室盘点审核功能 - 菜单配置
-- 创建时间：2025-01-28
-- ============================================

-- ============================================
-- 一、查询科室盘点菜单ID（作为父菜单）
-- ============================================
-- 执行以下SQL查询科室盘点菜单的ID
-- SELECT menu_id, menu_name, parent_id FROM sys_menu WHERE menu_name = '科室盘点';
-- 查询结果：menu_id = 1560

-- ============================================
-- 二、配置菜单和权限
-- ============================================

-- 2.1 插入主菜单（盘点审核）
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
  '盘点审核',
  1560,
  2,
  'stocktakingAudit',
  'department/stocktakingAudit/index',
  1,
  0,
  'C',
  '0',
  '0',
  'department:stocktakingAudit:list',
  'fa fa-check-circle',
  'admin',
  NOW(),
  '',
  NULL,
  '盘点审核菜单'
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
  '盘点审核查询',
  menu_id,
  1,
  '#',
  '',
  1,
  0,
  'F',
  '0',
  '0',
  'department:stocktakingAudit:list',
  '#',
  'admin',
  NOW(),
  '',
  NULL,
  ''
FROM sys_menu WHERE menu_name = '盘点审核'
UNION ALL
SELECT 
  (SELECT IFNULL(MAX(menu_id), 0) + 2 FROM (SELECT menu_id FROM sys_menu) AS temp),
  '盘点审核审核',
  menu_id,
  2,
  '#',
  '',
  1,
  0,
  'F',
  '0',
  '0',
  'department:stocktakingAudit:audit',
  '#',
  'admin',
  NOW(),
  '',
  NULL,
  ''
FROM sys_menu WHERE menu_name = '盘点审核'
UNION ALL
SELECT 
  (SELECT IFNULL(MAX(menu_id), 0) + 3 FROM (SELECT menu_id FROM sys_menu) AS temp),
  '盘点审核驳回',
  menu_id,
  3,
  '#',
  '',
  1,
  0,
  'F',
  '0',
  '0',
  'department:stocktakingAudit:reject',
  '#',
  'admin',
  NOW(),
  '',
  NULL,
  ''
FROM sys_menu WHERE menu_name = '盘点审核'
UNION ALL
SELECT 
  (SELECT IFNULL(MAX(menu_id), 0) + 4 FROM (SELECT menu_id FROM sys_menu) AS temp),
  '盘点审核导出',
  menu_id,
  4,
  '#',
  '',
  1,
  0,
  'F',
  '0',
  '0',
  'department:stocktakingAudit:export',
  '#',
  'admin',
  NOW(),
  '',
  NULL,
  ''
FROM sys_menu WHERE menu_name = '盘点审核';

-- ============================================
-- 三、添加驳回原因字段到数据库表（如果不存在）
-- ============================================
ALTER TABLE `stk_io_stocktaking` 
ADD COLUMN IF NOT EXISTS `reject_reason` VARCHAR(500) DEFAULT NULL COMMENT '驳回原因' AFTER `audit_date`;

-- ============================================
-- 四、验证菜单配置
-- ============================================
SELECT 
    menu_id,
    menu_name,
    parent_id,
    order_num,
    path,
    component,
    perms,
    icon,
    menu_type
FROM sys_menu 
WHERE menu_name = '盘点审核' OR parent_id = (SELECT menu_id FROM sys_menu WHERE menu_name = '盘点审核')
ORDER BY menu_id, order_num;

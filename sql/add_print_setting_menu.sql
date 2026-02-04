-- ============================================
-- 打印设置功能 - 菜单配置
-- 创建时间：2025-01-XX
-- ============================================

-- ============================================
-- 一、查询基础资料菜单ID（作为父菜单）
-- ============================================
-- 基础资料菜单ID：1071

-- ============================================
-- 二、配置菜单和权限
-- ============================================

-- 2.1 插入主菜单（打印设置）
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
  '打印设置',
  1071,
  (SELECT IFNULL(MAX(order_num), 0) + 1 FROM sys_menu WHERE parent_id = 1071),
  'printSetting',
  'system/printSetting/index',
  1,
  0,
  'C',
  '0',
  '0',
  'system:printSetting:list',
  'printer',
  'admin',
  NOW(),
  '',
  NULL,
  '打印设置菜单'
);

-- 2.2 插入按钮权限
-- 查询按钮权限的父菜单ID（打印设置菜单ID）
SET @print_setting_menu_id = (SELECT menu_id FROM sys_menu WHERE menu_name = '打印设置' AND parent_id = 1071);

-- 打印设置查询
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
  '打印设置查询',
  @print_setting_menu_id,
  1,
  '',
  '',
  1,
  0,
  'F',
  '0',
  '0',
  'system:printSetting:query',
  '#',
  'admin',
  NOW(),
  '',
  NULL,
  ''
);

-- 打印设置新增
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
  '打印设置新增',
  @print_setting_menu_id,
  2,
  '',
  '',
  1,
  0,
  'F',
  '0',
  '0',
  'system:printSetting:add',
  '#',
  'admin',
  NOW(),
  '',
  NULL,
  ''
);

-- 打印设置修改
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
  '打印设置修改',
  @print_setting_menu_id,
  3,
  '',
  '',
  1,
  0,
  'F',
  '0',
  '0',
  'system:printSetting:edit',
  '#',
  'admin',
  NOW(),
  '',
  NULL,
  ''
);

-- 打印设置删除
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
  '打印设置删除',
  @print_setting_menu_id,
  4,
  '',
  '',
  1,
  0,
  'F',
  '0',
  '0',
  'system:printSetting:remove',
  '#',
  'admin',
  NOW(),
  '',
  NULL,
  ''
);

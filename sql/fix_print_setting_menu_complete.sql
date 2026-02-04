-- ============================================
-- 修正打印设置菜单配置（完整版）
-- ============================================

-- 如果菜单已存在，先删除（包括按钮权限）
DELETE FROM sys_menu WHERE menu_name = '打印设置' AND parent_id = 1071;
DELETE FROM sys_menu WHERE parent_id IN (SELECT menu_id FROM sys_menu WHERE menu_name = '打印设置' AND parent_id = 1071);

-- 插入正确的打印设置菜单
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
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_name = '打印设置' AND parent_id = 1071);

-- 获取打印设置菜单ID
SET @print_setting_menu_id = (SELECT menu_id FROM sys_menu WHERE menu_name = '打印设置' AND parent_id = 1071);

-- 如果菜单已存在，直接更新配置
UPDATE sys_menu 
SET 
  path = 'printSetting',
  component = 'system/printSetting/index',
  menu_type = 'C',
  icon = 'printer',
  perms = 'system:printSetting:list',
  is_frame = 1,
  is_cache = 0,
  visible = '0',
  status = '0',
  update_by = 'admin',
  update_time = NOW()
WHERE menu_name = '打印设置' AND parent_id = 1071;

-- 重新获取菜单ID（更新后）
SET @print_setting_menu_id = (SELECT menu_id FROM sys_menu WHERE menu_name = '打印设置' AND parent_id = 1071);

-- 删除旧的按钮权限
DELETE FROM sys_menu WHERE parent_id = @print_setting_menu_id AND menu_type = 'F';

-- 插入按钮权限：打印设置查询
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
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_name = '打印设置查询' AND parent_id = @print_setting_menu_id);

-- 插入按钮权限：打印设置新增
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
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_name = '打印设置新增' AND parent_id = @print_setting_menu_id);

-- 插入按钮权限：打印设置修改
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
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_name = '打印设置修改' AND parent_id = @print_setting_menu_id);

-- 插入按钮权限：打印设置删除
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
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_name = '打印设置删除' AND parent_id = @print_setting_menu_id);

-- 验证配置
SELECT menu_id, menu_name, parent_id, order_num, path, component, menu_type, icon, perms, visible, status 
FROM sys_menu 
WHERE menu_name = '打印设置' AND parent_id = 1071;

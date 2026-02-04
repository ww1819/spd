-- ============================================
-- 立即修正打印设置菜单配置
-- 根据截图显示的问题进行修正：
-- 1. 路由地址：0 -> printSetting
-- 2. 菜单类型：目录(M) -> 菜单(C)
-- 3. 菜单图标：date-range -> printer
-- 4. 组件路径：需要设置为 system/printSetting/index
-- ============================================

-- 第一步：修正已存在的菜单（无论当前配置如何）
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

-- 第二步：如果菜单不存在，则插入新菜单
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
  17,
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
  'admin',
  NOW(),
  '打印设置菜单'
WHERE NOT EXISTS (
  SELECT 1 FROM sys_menu 
  WHERE menu_name = '打印设置' AND parent_id = 1071
);

-- 第三步：获取打印设置菜单ID并确保按钮权限存在
SET @print_setting_menu_id = (SELECT menu_id FROM sys_menu WHERE menu_name = '打印设置' AND parent_id = 1071);

-- 删除可能存在的旧按钮权限
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

-- 验证最终配置
SELECT 
  menu_id, 
  menu_name, 
  parent_id, 
  order_num, 
  path, 
  component, 
  menu_type, 
  icon, 
  perms, 
  visible, 
  status 
FROM sys_menu 
WHERE menu_name = '打印设置' AND parent_id = 1071;

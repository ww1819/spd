-- ============================================
-- 修正打印设置菜单配置（简化版）
-- 根据截图显示的问题进行修正
-- ============================================

-- 修正所有可能的错误配置
-- 情况1：路由地址为0或空
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
WHERE menu_name = '打印设置' 
  AND parent_id = 1071
  AND (path = '0' OR path IS NULL OR path = '' OR component = '0' OR component IS NULL OR component = '' OR menu_type != 'C' OR icon != 'printer');

-- 如果菜单不存在，插入新菜单
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

-- 验证配置
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

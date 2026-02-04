-- ============================================
-- 修正打印设置菜单配置
-- 修正路由地址、菜单类型、图标等配置
-- ============================================

-- 更新打印设置菜单配置
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

-- 验证更新结果
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

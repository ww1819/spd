-- ============================================
-- 收货确认功能 - 菜单配置
-- 创建时间：2025-01-27
-- ============================================

-- ============================================
-- 一、更新主菜单（收货确认）
-- ============================================
-- 更新menu_id=1558的菜单配置
UPDATE `sys_menu` SET
  `path` = 'receiptConfirm',
  `component` = 'department/receiptConfirm/index',
  `menu_type` = 'C',
  `perms` = 'department:receiptConfirm:list',
  `icon` = 'fa fa-check-circle',
  `update_by` = 'admin',
  `update_time` = NOW()
WHERE `menu_id` = 1558;

-- ============================================
-- 二、插入按钮权限
-- ============================================
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
) VALUES 
(1577, '收货确认查询', 1558, 1, '#', '', 1, 0, 'F', '0', '0', 'department:receiptConfirm:list', '#', 'admin', NOW(), '', NULL, ''),
(1578, '收货确认确认', 1558, 2, '#', '', 1, 0, 'F', '0', '0', 'department:receiptConfirm:confirm', '#', 'admin', NOW(), '', NULL, ''),
(1579, '收货确认驳回', 1558, 3, '#', '', 1, 0, 'F', '0', '0', 'department:receiptConfirm:reject', '#', 'admin', NOW(), '', NULL, ''),
(1580, '收货确认导出', 1558, 4, '#', '', 1, 0, 'F', '0', '0', 'department:receiptConfirm:export', '#', 'admin', NOW(), '', NULL, '');

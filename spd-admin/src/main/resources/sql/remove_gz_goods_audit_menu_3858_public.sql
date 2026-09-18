-- 公共：移除高值「退库审核」菜单(3858)及其按钮
-- 与备货管理下「备货退库」(1197 → gzOrder/refund/index，/gz/refundStock) 功能重复
-- 影响：主菜单 sys_menu + 全部租户授权（含 hengsui-third-001、zaoqiang-tcm-001）
-- 保留：备货退库 1197

-- 按钮 + 页面
DELETE FROM sys_role_menu WHERE menu_id IN (3858, 3806, 3807, 3808, 3809, 3810, 3849)
   OR menu_id IN (SELECT menu_id FROM (SELECT menu_id FROM sys_menu WHERE parent_id = 3858) t);

DELETE FROM sys_user_menu WHERE menu_id IN (3858, 3806, 3807, 3808, 3809, 3810, 3849)
   OR menu_id IN (SELECT menu_id FROM (SELECT menu_id FROM sys_menu WHERE parent_id = 3858) t);

DELETE FROM sys_post_menu WHERE menu_id IN (3858, 3806, 3807, 3808, 3809, 3810, 3849)
   OR menu_id IN (SELECT menu_id FROM (SELECT menu_id FROM sys_menu WHERE parent_id = 3858) t);

DELETE FROM hc_customer_menu WHERE menu_id IN (3858, 3806, 3807, 3808, 3809, 3810, 3849)
   OR menu_id IN (SELECT menu_id FROM (SELECT menu_id FROM sys_menu WHERE parent_id = 3858) t);

DELETE FROM sys_menu WHERE parent_id = 3858;
DELETE FROM sys_menu WHERE menu_id = 3858;
DELETE FROM sys_menu WHERE menu_type = 'C' AND component = 'gzOrder/goodsAudit/index';

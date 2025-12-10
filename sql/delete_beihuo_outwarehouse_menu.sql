-- 删除备货出库菜单及其所有子菜单和按钮权限
-- 菜单ID 1193: 备货出库 (父菜单ID: 1064)
-- 执行顺序：先删除按钮权限菜单，再删除子菜单，最后删除主菜单

-- 删除按钮权限菜单（退库审核的按钮权限）
DELETE FROM sys_menu WHERE menu_id = 1231;

-- 删除按钮权限菜单（退库申请的按钮权限）
DELETE FROM sys_menu WHERE menu_id IN (1225, 1227, 1228, 1229, 1230);

-- 删除按钮权限菜单（出库审核的按钮权限）
DELETE FROM sys_menu WHERE menu_id = 1224;

-- 删除按钮权限菜单（出库申请的按钮权限）
DELETE FROM sys_menu WHERE menu_id IN (1217, 1218, 1219, 1220, 1221);

-- 删除子菜单：退库审核 (1202)
DELETE FROM sys_menu WHERE menu_id = 1202;

-- 删除子菜单：退库申请 (1201)
DELETE FROM sys_menu WHERE menu_id = 1201;

-- 删除子菜单：出库审核 (1200)
DELETE FROM sys_menu WHERE menu_id = 1200;

-- 删除子菜单：出库申请 (1199)
DELETE FROM sys_menu WHERE menu_id = 1199;

-- 删除主菜单：备货出库 (1193)
DELETE FROM sys_menu WHERE menu_id = 1193;


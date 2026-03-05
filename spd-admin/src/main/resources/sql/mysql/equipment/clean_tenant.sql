-- ============================================================
-- 租户清库脚本：清理指定客户（租户）在设备侧的全部数据
-- 使用前请将下方 @customer_id 改为目标租户的 customer_id，并在事务中执行或备份后执行
-- ============================================================

-- 【使用方式】执行前设置租户ID，例如：
-- SET @customer_id = '01900000-0000-7000-8000-000000000002';

-- 1. 客户菜单功能启停用记录与时间段（按客户+菜单）
DELETE FROM sb_customer_menu_status_log WHERE customer_id = @customer_id;
/
DELETE FROM sb_customer_menu_period_log WHERE customer_id = @customer_id;
/

-- 2. 客户菜单权限
DELETE FROM sb_customer_menu WHERE customer_id = @customer_id;
/

-- 3. 客户启停用记录与时间段
DELETE FROM sb_customer_status_log WHERE customer_id = @customer_id;
/
DELETE FROM sb_customer_period_log WHERE customer_id = @customer_id;
/

-- 4. 工作组权限（先删子表，再删组）
DELETE FROM sb_work_group_menu WHERE customer_id = @customer_id;
/
DELETE FROM sb_work_group_warehouse WHERE customer_id = @customer_id;
/
DELETE FROM sb_work_group_dept WHERE customer_id = @customer_id;
/
DELETE FROM sb_work_group_user WHERE customer_id = @customer_id;
/
DELETE FROM sb_work_group WHERE customer_id = @customer_id;
/

-- 5. 用户权限（设备侧）
DELETE FROM sb_user_permission_menu WHERE customer_id = @customer_id;
/
DELETE FROM sb_user_permission_warehouse WHERE customer_id = @customer_id;
/
DELETE FROM sb_user_permission_dept WHERE customer_id = @customer_id;
/

-- 6. 设备角色菜单、用户-角色关联（须在删除 sb_role 前执行）
DELETE rm FROM sb_role_menu rm INNER JOIN sb_role r ON rm.role_id = r.role_id WHERE r.customer_id = @customer_id;
/
DELETE ur FROM sb_user_role ur WHERE ur.customer_id = @customer_id;
/
DELETE ur FROM sb_user_role ur INNER JOIN sb_role r ON ur.role_id = r.role_id WHERE r.customer_id = @customer_id;
/

-- 7. 设备角色
DELETE FROM sb_role WHERE customer_id = @customer_id;
/

-- 8. 租户用户（sys_user 中归属该客户的用户；若存在 sys_user_role/sys_user_post 等外键，请先清理或改为 UPDATE del_flag='2' 逻辑删除）
DELETE FROM sys_user WHERE customer_id = @customer_id;
/

-- 9. 客户主表（删除后该租户不可恢复，确认无误后再执行）
DELETE FROM sb_customer WHERE customer_id = @customer_id;
/

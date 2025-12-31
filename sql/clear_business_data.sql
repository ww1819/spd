-- ============================================
-- 清除业务数据脚本
-- 说明：清除所有业务数据，保留管理员账号和系统配置
-- 执行时间：请根据实际情况填写
-- 警告：执行前请备份数据库！
-- ============================================
-- 
-- 重要提示：
-- 1. 本脚本会清空所有业务数据，包括订单、采购、库存等
-- 2. 会删除除管理员（user_id=1）外的所有用户账号
-- 3. 会删除除管理员角色（role_id=1）外的所有角色
-- 4. 会清空所有日志数据
-- 5. 会保留系统菜单、系统配置、字典、部门、岗位等系统数据
-- 6. 会保留定时任务配置（qrtz_* 表）
-- 7. 执行前请务必备份数据库！
-- ============================================

-- ============================================
-- 执行前检查：查看将要删除的数据量（可选执行）
-- ============================================
-- 取消下面的注释可以查看将要删除的数据量
/*
SELECT '业务数据统计' as info_type, COUNT(*) as record_count FROM gz_order
UNION ALL
SELECT '采购订单', COUNT(*) FROM purchase_order
UNION ALL
SELECT '库存数据', COUNT(*) FROM stk_inventory
UNION ALL
SELECT '用户数量（除管理员）', COUNT(*) FROM sys_user WHERE user_id != 1
UNION ALL
SELECT '角色数量（除管理员）', COUNT(*) FROM sys_role WHERE role_id != 1
UNION ALL
SELECT '操作日志', COUNT(*) FROM sys_oper_log
UNION ALL
SELECT '登录日志', COUNT(*) FROM sys_logininfor;
*/

-- ============================================
-- 开始执行清理
-- ============================================

-- 设置SQL模式，避免外键约束问题
SET FOREIGN_KEY_CHECKS = 0;
SET SQL_SAFE_UPDATES = 0;

-- ============================================
-- 第一部分：清空业务数据表
-- ============================================

-- 清空订单相关业务数据
TRUNCATE TABLE gz_order_entry_inhospitalcode_list;
TRUNCATE TABLE gz_order_entry;
TRUNCATE TABLE gz_order;
TRUNCATE TABLE gz_shipment_entry;
TRUNCATE TABLE gz_shipment;
TRUNCATE TABLE gz_refund_stock_entry;
TRUNCATE TABLE gz_refund_stock;
TRUNCATE TABLE gz_refund_goods_entry;
TRUNCATE TABLE gz_refund_goods;
TRUNCATE TABLE gz_depot_inventory;
TRUNCATE TABLE gz_dep_inventory;
TRUNCATE TABLE gz_dep_apply_entry;
TRUNCATE TABLE gz_dep_apply;

-- 清空采购相关业务数据
TRUNCATE TABLE purchase_order_entry;
TRUNCATE TABLE purchase_order;
TRUNCATE TABLE purchase_plan_entry;
TRUNCATE TABLE purchase_plan;
TRUNCATE TABLE dep_purchase_apply_entry;
TRUNCATE TABLE dep_purchase_apply;
TRUNCATE TABLE scm_purchase_statistics;

-- 清空库存相关业务数据
TRUNCATE TABLE stk_io_stocktaking_entry;
TRUNCATE TABLE stk_io_stocktaking;
TRUNCATE TABLE stk_io_bill_entry;
TRUNCATE TABLE stk_io_bill;
TRUNCATE TABLE stk_inventory;
TRUNCATE TABLE stk_dep_inventory;

-- 清空设备相关业务数据
TRUNCATE TABLE equipment_purchase_application;
TRUNCATE TABLE equipment_info;

-- 清空其他业务数据
TRUNCATE TABLE bas_apply_entry;
TRUNCATE TABLE bas_apply;
TRUNCATE TABLE excel_import_detail;
TRUNCATE TABLE t_sb_xj_xm;
TRUNCATE TABLE t_sb_xj_mb;
TRUNCATE TABLE t_sb_by_xm;
TRUNCATE TABLE t_sb_by_mb;

-- ============================================
-- 第二部分：清空基础数据表（保留系统基础结构）
-- ============================================

-- 清空基础数据表（这些表的数据可以重新导入）
TRUNCATE TABLE fd_category68;
TRUNCATE TABLE fd_equipment_category;
TRUNCATE TABLE fd_equipment_dict;
TRUNCATE TABLE fd_factory;
TRUNCATE TABLE fd_finance_category;
TRUNCATE TABLE fd_location;
TRUNCATE TABLE fd_material;
TRUNCATE TABLE fd_material_category;
TRUNCATE TABLE fd_supplier;
TRUNCATE TABLE fd_unit;
TRUNCATE TABLE fd_warehouse;
TRUNCATE TABLE fd_warehouse_category;
TRUNCATE TABLE scm_certificate_type;

-- 注意：fd_department 保留，因为可能与 sys_dept 有关联

-- ============================================
-- 第三部分：删除非管理员用户及其关联数据
-- ============================================

-- 删除非管理员用户的岗位关联
DELETE FROM sys_user_post WHERE user_id != 1;

-- 删除非管理员用户的部门关联
DELETE FROM sys_user_department WHERE user_id != 1;

-- 删除非管理员用户的菜单关联
DELETE FROM sys_user_menu WHERE user_id != 1;

-- 删除非管理员用户的仓库关联
DELETE FROM sys_user_warehouse WHERE user_id != 1;

-- 删除非管理员用户的角色关联
DELETE FROM sys_user_role WHERE user_id != 1;

-- 删除非管理员用户
DELETE FROM sys_user WHERE user_id != 1;

-- ============================================
-- 第四部分：删除非管理员角色及其关联数据
-- ============================================

-- 删除非管理员角色的部门关联
DELETE FROM sys_role_dept WHERE role_id != 1;

-- 删除非管理员角色的菜单关联（注意：这里保留管理员角色的菜单权限）
DELETE FROM sys_role_menu WHERE role_id != 1;

-- 删除非管理员角色
DELETE FROM sys_role WHERE role_id != 1;

-- ============================================
-- 第五部分：清空日志数据
-- ============================================

-- 清空操作日志
TRUNCATE TABLE sys_oper_log;

-- 清空登录日志
TRUNCATE TABLE sys_logininfor;

-- 清空定时任务日志
TRUNCATE TABLE sys_job_log;

-- ============================================
-- 第六部分：清空代码生成相关数据（可选）
-- ============================================

-- 如果需要清空代码生成表，取消下面的注释
-- TRUNCATE TABLE gen_table_column;
-- TRUNCATE TABLE gen_table;

-- ============================================
-- 第七部分：重置单据号（可选，根据实际需求）
-- ============================================

-- 如果需要重置单据号，取消下面的注释
-- DELETE FROM sys_sheet_id;

-- ============================================
-- 第八部分：清理岗位和部门的用户关联（保留岗位和部门本身）
-- ============================================

-- 清空岗位部门关联（保留岗位和部门数据）
DELETE FROM sys_post_department;
DELETE FROM sys_post_menu;
DELETE FROM sys_post_warehouse;

-- 注意：sys_post（岗位表）和 sys_dept（部门表）的数据保留，只清空关联关系

-- ============================================
-- 恢复设置
-- ============================================

SET FOREIGN_KEY_CHECKS = 1;
SET SQL_SAFE_UPDATES = 1;

-- ============================================
-- 验证脚本：检查清理结果（可选执行）
-- ============================================

-- 检查用户数量（应该只有1个管理员）
-- SELECT COUNT(*) as admin_count FROM sys_user WHERE user_id = 1;
-- SELECT COUNT(*) as total_users FROM sys_user;

-- 检查角色数量（应该只有1个管理员角色）
-- SELECT COUNT(*) as admin_role_count FROM sys_role WHERE role_id = 1;
-- SELECT COUNT(*) as total_roles FROM sys_role;

-- 检查业务数据是否已清空
-- SELECT COUNT(*) as gz_order_count FROM gz_order;
-- SELECT COUNT(*) as purchase_order_count FROM purchase_order;
-- SELECT COUNT(*) as stk_inventory_count FROM stk_inventory;

-- ============================================
-- 脚本执行完成
-- ============================================
-- 
-- 已清理的内容：
-- 1. 已清空所有业务数据表（订单、采购、库存、设备等）
-- 2. 已清空所有基础数据表（物料、供应商、仓库等）
-- 3. 已删除除管理员（user_id=1）外的所有用户账号
-- 4. 已删除除管理员角色（role_id=1）外的所有角色
-- 5. 已清空所有日志数据（操作日志、登录日志、任务日志）
-- 6. 已清空岗位和部门的用户关联数据
-- 
-- 已保留的系统数据：
-- 1. 系统菜单（sys_menu）- 完整保留
-- 2. 系统配置（sys_config）- 完整保留
-- 3. 字典数据（sys_dict_type, sys_dict_data）- 完整保留
-- 4. 部门结构（sys_dept）- 完整保留
-- 5. 岗位数据（sys_post）- 完整保留
-- 6. 定时任务配置（sys_job）- 完整保留
-- 7. 定时任务执行表（qrtz_*）- 完整保留
-- 8. 通知公告（sys_notice）- 完整保留
-- 9. 单据号配置（sys_sheet_id）- 完整保留
-- 10. 代码生成表（gen_table, gen_table_column）- 可选保留
-- 11. 管理员账号（user_id=1）- 完整保留
-- 12. 管理员角色（role_id=1）- 完整保留，包括其菜单权限
-- 
-- 注意事项：
-- 1. 管理员账号的密码保持不变
-- 2. 管理员角色的所有菜单权限保持不变
-- 3. 系统配置参数保持不变
-- 4. 部门结构和岗位数据保持不变，但关联关系已清空
-- 5. 基础数据表（物料、供应商等）已清空，需要重新导入
-- ============================================


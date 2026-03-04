-- 数据完整性检查 / 测试数据初始化
-- 说明：sys_user 表主键 user_id 为自增，插入时不得指定 user_id，后续通过 (customer_id, user_name) 关联获取。
-- 测试客户：衡水市第三人民医院，计划停用 2035-12-31；测试用户：heshui_test / admin123（登录时选客户 HSSDSRMYY）
-- 执行前请确保 sb_customer、sys_user、sb_user_role 等表及 planned_disable_time 等字段已就绪

-- 1. 客户：衡水市第三人民医院（计划停用 2035-12-31 23:59:59）
INSERT INTO sb_customer (
  customer_id, customer_name, customer_code, status, planned_disable_time, create_by, create_time, remark
)
SELECT
  '01900000-0000-7000-8000-000000000002',
  '衡水市第三人民医院',
  'HSSDSRMYY',
  '0',
  '2035-12-31 23:59:59',
  'admin',
  NOW(),
  '测试客户，计划停用至2035-12-31'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_customer WHERE customer_id = '01900000-0000-7000-8000-000000000002');
/

-- 2. 测试用户（不插入 user_id，依赖自增主键；密码 admin123 的 BCrypt 密文）
INSERT INTO sys_user (
  customer_id, user_name, nick_name, password, status, del_flag, create_by, create_time, remark
)
SELECT
  '01900000-0000-7000-8000-000000000002',
  'heshui_test',
  '衡水测试用户',
  '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2',
  '0',
  '0',
  'admin',
  NOW(),
  '衡水市第三人民医院测试账号'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sys_user WHERE customer_id = '01900000-0000-7000-8000-000000000002' AND user_name = 'heshui_test');
/

-- 3. 赋予设备管理员角色（按 customer_id + user_name 查 user_id，不依赖固定 user_id）
INSERT INTO sb_user_role (user_id, role_id, customer_id)
SELECT u.user_id, '01900000-0000-7000-8000-000000000001', '01900000-0000-7000-8000-000000000002'
FROM sys_user u
WHERE u.customer_id = '01900000-0000-7000-8000-000000000002' AND u.user_name = 'heshui_test' AND u.del_flag = '0'
  AND NOT EXISTS (
    SELECT 1 FROM sb_user_role r
    WHERE r.user_id = u.user_id AND r.role_id = '01900000-0000-7000-8000-000000000001'
  );
/

-- 4. 为该客户分配与默认管理员相同的设备菜单（否则登录后无菜单）
INSERT INTO sb_customer_menu (customer_id, menu_id, create_by, create_time)
SELECT '01900000-0000-7000-8000-000000000002', m.menu_id, 'admin', NOW()
FROM sb_menu m
WHERE NOT EXISTS (
  SELECT 1 FROM sb_customer_menu cm
  WHERE cm.customer_id = '01900000-0000-7000-8000-000000000002' AND cm.menu_id = m.menu_id
);
/

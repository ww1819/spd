
-- 设备前端 sb_* 表初始化菜单与权限数据
/
INSERT INTO sb_role (
  role_id, role_name, role_key, role_sort, data_scope, menu_check_strictly, dept_check_strictly, status, del_flag, create_by, create_time, remark
)
SELECT
  '01900000-0000-7000-8000-000000000001',
  '设备管理员',
  'sb_admin',
  1,
  '1',
  1,
  1,
  '0',
  '0',
  'admin',
  NOW(),
  '设备前端默认管理员角色'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_role WHERE role_id = '01900000-0000-7000-8000-000000000001');
/

-- 设备前端菜单：根目录「设备管理」
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, is_platform_only, default_open_to_customer, del_flag, create_by, create_time, remark, update_by, update_time, delete_by, delete_time
)
SELECT
  '01900000-0000-7000-8000-000000001388',
  '设备管理',
  '0',
  1,
  '/equipment',
  'Layout',
  '1',
  '0',
  'M',
  '0',
  '0',
  NULL,
  'equipment',
  '0',
  '1',
  '0',
  'admin',
  NOW(),
  '设备管理根菜单',
  NULL,
  NULL,
  NULL,
  NULL
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-000000001388');
/

-- 设备前端菜单：根目录「基础资料」
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, is_platform_only, default_open_to_customer, del_flag, create_by, create_time, remark, update_by, update_time, delete_by, delete_time
)
SELECT
  '01900000-0000-7000-8000-0000000013ec',
  '基础资料',
  '0',
  2,
  '/foundation',
  'Layout',
  '1',
  '0',
  'M',
  '0',
  '0',
  NULL,
  'component',
  '0',
  '1',
  '0',
  'admin',
  NOW(),
  '设备前端基础资料根菜单',
  NULL,
  NULL,
  NULL,
  NULL
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-0000000013ec');
/

-- 设备管理子菜单：设备信息
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, is_platform_only, default_open_to_customer, del_flag, create_by, create_time, remark, update_by, update_time, delete_by, delete_time
)
SELECT
  '01900000-0000-7000-8000-000000001389',
  '设备信息',
  '01900000-0000-7000-8000-000000001388',
  1,
  'equipmentInfo',
  'equipment/equipmentInfo/index',
  '1',
  '0',
  'C',
  '0',
  '0',
  'equipment:equipmentInfo:list',
  'list',
  '0',
  '1',
  '0',
  'admin',
  NOW(),
  '设备信息维护',
  NULL,
  NULL,
  NULL,
  NULL
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-000000001389');
/

-- 设备管理子菜单：资产台账
INSERT INTO sb_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, is_platform_only, default_open_to_customer, del_flag, create_by, create_time, remark, update_by, update_time, delete_by, delete_time)
SELECT '01900000-0000-7000-8000-00000000d001', '资产台账', '01900000-0000-7000-8000-000000001388', 0, 'assetLedger', 'equipment/assetLedger/index', '1', '0', 'C', '0', '0', 'equipment:assetLedger:list', 'money', '0', '1', '0', 'admin', NOW(), '资产台账', NULL, NULL, NULL, NULL FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-00000000d001');
/
-- 将已存在的「客户资产台账」菜单名称更新为「资产台账」
UPDATE sb_menu SET menu_name = '资产台账', remark = '资产台账' WHERE menu_id = '01900000-0000-7000-8000-00000000d001';
/
INSERT INTO sb_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, is_platform_only, default_open_to_customer, del_flag, create_by, create_time, remark, update_by, update_time, delete_by, delete_time)
SELECT '01900000-0000-7000-8000-00000000d002', '资产台账查询', '01900000-0000-7000-8000-00000000d001', 1, '#', NULL, '1', '0', 'F', '0', '0', 'equipment:assetLedger:query', '#', '0', '0', '0', 'admin', NOW(), '资产台账查询', NULL, NULL, NULL, NULL FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-00000000d002');
/
INSERT INTO sb_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, is_platform_only, default_open_to_customer, del_flag, create_by, create_time, remark, update_by, update_time, delete_by, delete_time)
SELECT '01900000-0000-7000-8000-00000000d003', '资产台账新增', '01900000-0000-7000-8000-00000000d001', 2, '#', NULL, '1', '0', 'F', '0', '0', 'equipment:assetLedger:add', '#', '0', '1', '0', 'admin', NOW(), '资产台账新增', NULL, NULL, NULL, NULL FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-00000000d003');
/
INSERT INTO sb_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, is_platform_only, default_open_to_customer, del_flag, create_by, create_time, remark, update_by, update_time, delete_by, delete_time)
SELECT '01900000-0000-7000-8000-00000000d007', '资产台账导入', '01900000-0000-7000-8000-00000000d001', 6, '#', NULL, '1', '0', 'F', '0', '0', 'equipment:assetLedger:import', '#', '0', '1', '0', 'admin', NOW(), '资产台账导入', NULL, NULL, NULL, NULL FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-00000000d007');
/
INSERT INTO sb_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, is_platform_only, default_open_to_customer, del_flag, create_by, create_time, remark, update_by, update_time, delete_by, delete_time)
SELECT '01900000-0000-7000-8000-00000000d004', '资产台账修改', '01900000-0000-7000-8000-00000000d001', 3, '#', NULL, '1', '0', 'F', '0', '0', 'equipment:assetLedger:edit', '#', '0', '0', '0', 'admin', NOW(), '资产台账修改', NULL, NULL, NULL, NULL FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-00000000d004');
/
INSERT INTO sb_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, is_platform_only, default_open_to_customer, del_flag, create_by, create_time, remark, update_by, update_time, delete_by, delete_time)
SELECT '01900000-0000-7000-8000-00000000d005', '资产台账删除', '01900000-0000-7000-8000-00000000d001', 4, '#', NULL, '1', '0', 'F', '0', '0', 'equipment:assetLedger:remove', '#', '0', '0', '0', 'admin', NOW(), '资产台账删除', NULL, NULL, NULL, NULL FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-00000000d005');
/
INSERT INTO sb_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, is_platform_only, default_open_to_customer, del_flag, create_by, create_time, remark, update_by, update_time, delete_by, delete_time)
SELECT '01900000-0000-7000-8000-00000000d006', '资产台账导出', '01900000-0000-7000-8000-00000000d001', 5, '#', NULL, '1', '0', 'F', '0', '0', 'equipment:assetLedger:export', '#', '0', '0', '0', 'admin', NOW(), '资产台账导出', NULL, NULL, NULL, NULL FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-00000000d006');
/
-- 资产台账新增、资产台账导入：默认对客户开放
UPDATE sb_menu SET default_open_to_customer = '1' WHERE menu_id IN ('01900000-0000-7000-8000-00000000d003', '01900000-0000-7000-8000-00000000d007');
/

-- 设备管理子菜单：设备巡检
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, is_platform_only, default_open_to_customer, del_flag, create_by, create_time, remark, update_by, update_time, delete_by, delete_time
)
SELECT
  '01900000-0000-7000-8000-00000000138a',
  '设备巡检',
  '01900000-0000-7000-8000-000000001388',
  2,
  'equipmentInspect',
  'equipment/equipmentInspect/index',
  '1',
  '0',
  'C',
  '0',
  '0',
  'equipment:equipmentInspect:list',
  'eye',
  '0',
  '1',
  '0',
  'admin',
  NOW(),
  '设备巡检管理',
  NULL,
  NULL,
  NULL,
  NULL
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-00000000138a');
/

-- 设备管理子菜单：设备维修
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, is_platform_only, default_open_to_customer, del_flag, create_by, create_time, remark, update_by, update_time, delete_by, delete_time
)
SELECT
  '01900000-0000-7000-8000-00000000138b',
  '设备维修',
  '01900000-0000-7000-8000-000000001388',
  3,
  'equipmentRepair',
  'equipment/equipmentRepair/index',
  '1',
  '0',
  'C',
  '0',
  '0',
  'equipment:equipmentRepair:list',
  'tool',
  '0',
  '1',
  '0',
  'admin',
  NOW(),
  '设备维修管理',
  NULL,
  NULL,
  NULL,
  NULL
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-00000000138b');
/

-- 设备管理子菜单：设备保养
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, is_platform_only, default_open_to_customer, del_flag, create_by, create_time, remark, update_by, update_time, delete_by, delete_time
)
SELECT
  '01900000-0000-7000-8000-00000000138c',
  '设备保养',
  '01900000-0000-7000-8000-000000001388',
  4,
  'equipmentMaintain',
  'equipment/equipmentMaintain/index',
  '1',
  '0',
  'C',
  '0',
  '0',
  'equipment:equipmentMaintain:list',
  'maintain',
  '0',
  '1',
  '0',
  'admin',
  NOW(),
  '设备保养管理',
  NULL,
  NULL,
  NULL,
  NULL
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-00000000138c');
/

-- 设备管理子菜单：设备入库
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, is_platform_only, default_open_to_customer, del_flag, create_by, create_time, remark, update_by, update_time, delete_by, delete_time
)
SELECT
  '01900000-0000-7000-8000-00000000138d',
  '设备入库',
  '01900000-0000-7000-8000-000000001388',
  5,
  'equipmentStorage',
  'equipment/equipmentStorage/index',
  '1',
  '0',
  'C',
  '0',
  '0',
  'equipment:equipmentStorage:list',
  'storage',
  '0',
  '1',
  '0',
  'admin',
  NOW(),
  '设备入库管理',
  NULL,
  NULL,
  NULL,
  NULL
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-00000000138d');
/

-- 设备管理子菜单：设备出库（领用/归还等可以再细分，这里只保留主要菜单）
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, is_platform_only, default_open_to_customer, del_flag, create_by, create_time, remark, update_by, update_time, delete_by, delete_time
)
SELECT
  '01900000-0000-7000-8000-00000000138e',
  '设备归还',
  '01900000-0000-7000-8000-000000001388',
  6,
  'equipmentReturn',
  'equipment/equipmentReturn/index',
  '1',
  '0',
  'C',
  '0',
  '0',
  'equipment:equipmentReturn:list',
  'return',
  '0',
  '1',
  '0',
  'admin',
  NOW(),
  '设备归还管理',
  NULL,
  NULL,
  NULL,
  NULL
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-00000000138e');
/

-- 基础资料子菜单：设备字典维护
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, is_platform_only, default_open_to_customer, del_flag, create_by, create_time, remark, update_by, update_time, delete_by, delete_time
)
SELECT
  '01900000-0000-7000-8000-0000000013ed',
  '设备字典维护',
  '01900000-0000-7000-8000-0000000013ec',
  1,
  'equipmentDict',
  'foundation/equipmentDict/index',
  '1',
  '0',
  'C',
  '0',
  '0',
  'foundation:equipmentDict:list',
  'dict',
  '0',
  '0',
  '0',
  'admin',
  NOW(),
  '设备字典维护',
  NULL,
  NULL,
  NULL,
  NULL
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-0000000013ed');
/

-- 基础资料子菜单：设备品牌/生产厂家/供应商/资产分类/计量器具分类
INSERT INTO sb_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, is_platform_only, default_open_to_customer, del_flag, create_by, create_time, remark, update_by, update_time, delete_by, delete_time)
SELECT '01900000-0000-7000-8000-00000000d010', '设备品牌', '01900000-0000-7000-8000-0000000013ec', 10, 'brand', 'equipment/brand/index', '1', '0', 'C', '0', '0', 'equipment:brand:list', 'star', '0', '1', '0', 'admin', NOW(), '设备品牌', NULL, NULL, NULL, NULL FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-00000000d010');
/
INSERT INTO sb_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, is_platform_only, default_open_to_customer, del_flag, create_by, create_time, remark, update_by, update_time, delete_by, delete_time)
SELECT '01900000-0000-7000-8000-00000000d011', '设备生产厂家', '01900000-0000-7000-8000-0000000013ec', 11, 'manufacturer', 'equipment/manufacturer/index', '1', '0', 'C', '0', '0', 'equipment:manufacturer:list', 'build', '0', '1', '0', 'admin', NOW(), '设备生产厂家', NULL, NULL, NULL, NULL FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-00000000d011');
/
INSERT INTO sb_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, is_platform_only, default_open_to_customer, del_flag, create_by, create_time, remark, update_by, update_time, delete_by, delete_time)
SELECT '01900000-0000-7000-8000-00000000d012', '设备供应商', '01900000-0000-7000-8000-0000000013ec', 12, 'supplier', 'equipment/supplier/index', '1', '0', 'C', '0', '0', 'equipment:supplier:list', 'shopping', '0', '1', '0', 'admin', NOW(), '设备供应商', NULL, NULL, NULL, NULL FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-00000000d012');
/
INSERT INTO sb_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, is_platform_only, default_open_to_customer, del_flag, create_by, create_time, remark, update_by, update_time, delete_by, delete_time)
SELECT '01900000-0000-7000-8000-00000000d013', '资产分类', '01900000-0000-7000-8000-0000000013ec', 13, 'assetCategory', 'equipment/assetCategory/index', '1', '0', 'C', '0', '0', 'equipment:assetCategory:list', 'tree', '0', '1', '0', 'admin', NOW(), '资产分类', NULL, NULL, NULL, NULL FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-00000000d013');
/
INSERT INTO sb_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, is_platform_only, default_open_to_customer, del_flag, create_by, create_time, remark, update_by, update_time, delete_by, delete_time)
SELECT '01900000-0000-7000-8000-00000000d014', '计量器具分类', '01900000-0000-7000-8000-0000000013ec', 14, 'measuringCategory', 'equipment/measuringCategory/index', '1', '0', 'C', '0', '0', 'equipment:measuringCategory:list', 'tree-table', '0', '1', '0', 'admin', NOW(), '计量器具分类', NULL, NULL, NULL, NULL FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-00000000d014');
/
-- 设备品牌/生产厂家/供应商/资产分类/计量器具分类 按钮（query/add/edit/remove）（menu_id 限 36 字符，末尾 4 位十六进制）
INSERT INTO sb_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, is_platform_only, default_open_to_customer, del_flag, create_by, create_time, remark, update_by, update_time, delete_by, delete_time)
SELECT '01900000-0000-7000-8000-000000000d01', '品牌查询', '01900000-0000-7000-8000-00000000d010', 1, '#', NULL, '1', '0', 'F', '0', '0', 'equipment:brand:query', '#', '0', '0', '0', 'admin', NOW(), '品牌查询', NULL, NULL, NULL, NULL FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-000000000d01');
/
INSERT INTO sb_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, is_platform_only, default_open_to_customer, del_flag, create_by, create_time, remark, update_by, update_time, delete_by, delete_time)
SELECT '01900000-0000-7000-8000-000000000d02', '品牌新增', '01900000-0000-7000-8000-00000000d010', 2, '#', NULL, '1', '0', 'F', '0', '0', 'equipment:brand:add', '#', '0', '0', '0', 'admin', NOW(), '品牌新增', NULL, NULL, NULL, NULL FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-000000000d02');
/
INSERT INTO sb_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, is_platform_only, default_open_to_customer, del_flag, create_by, create_time, remark, update_by, update_time, delete_by, delete_time)
SELECT '01900000-0000-7000-8000-000000000d03', '品牌修改', '01900000-0000-7000-8000-00000000d010', 3, '#', NULL, '1', '0', 'F', '0', '0', 'equipment:brand:edit', '#', '0', '0', '0', 'admin', NOW(), '品牌修改', NULL, NULL, NULL, NULL FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-000000000d03');
/
INSERT INTO sb_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, is_platform_only, default_open_to_customer, del_flag, create_by, create_time, remark, update_by, update_time, delete_by, delete_time)
SELECT '01900000-0000-7000-8000-000000000d04', '品牌删除', '01900000-0000-7000-8000-00000000d010', 4, '#', NULL, '1', '0', 'F', '0', '0', 'equipment:brand:remove', '#', '0', '0', '0', 'admin', NOW(), '品牌删除', NULL, NULL, NULL, NULL FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-000000000d04');
/
INSERT INTO sb_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, is_platform_only, default_open_to_customer, del_flag, create_by, create_time, remark, update_by, update_time, delete_by, delete_time)
SELECT '01900000-0000-7000-8000-000000000d11', '厂家查询', '01900000-0000-7000-8000-00000000d011', 1, '#', NULL, '1', '0', 'F', '0', '0', 'equipment:manufacturer:query', '#', '0', '0', '0', 'admin', NOW(), '厂家查询', NULL, NULL, NULL, NULL FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-000000000d11');
/
INSERT INTO sb_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, is_platform_only, default_open_to_customer, del_flag, create_by, create_time, remark, update_by, update_time, delete_by, delete_time)
SELECT '01900000-0000-7000-8000-000000000d12', '厂家新增', '01900000-0000-7000-8000-00000000d011', 2, '#', NULL, '1', '0', 'F', '0', '0', 'equipment:manufacturer:add', '#', '0', '0', '0', 'admin', NOW(), '厂家新增', NULL, NULL, NULL, NULL FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-000000000d12');
/
INSERT INTO sb_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, is_platform_only, default_open_to_customer, del_flag, create_by, create_time, remark, update_by, update_time, delete_by, delete_time)
SELECT '01900000-0000-7000-8000-000000000d13', '厂家修改', '01900000-0000-7000-8000-00000000d011', 3, '#', NULL, '1', '0', 'F', '0', '0', 'equipment:manufacturer:edit', '#', '0', '0', '0', 'admin', NOW(), '厂家修改', NULL, NULL, NULL, NULL FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-000000000d13');
/
INSERT INTO sb_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, is_platform_only, default_open_to_customer, del_flag, create_by, create_time, remark, update_by, update_time, delete_by, delete_time)
SELECT '01900000-0000-7000-8000-000000000d14', '厂家删除', '01900000-0000-7000-8000-00000000d011', 4, '#', NULL, '1', '0', 'F', '0', '0', 'equipment:manufacturer:remove', '#', '0', '0', '0', 'admin', NOW(), '厂家删除', NULL, NULL, NULL, NULL FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-000000000d14');
/
INSERT INTO sb_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, is_platform_only, default_open_to_customer, del_flag, create_by, create_time, remark, update_by, update_time, delete_by, delete_time)
SELECT '01900000-0000-7000-8000-000000000d21', '供应商查询', '01900000-0000-7000-8000-00000000d012', 1, '#', NULL, '1', '0', 'F', '0', '0', 'equipment:supplier:query', '#', '0', '0', '0', 'admin', NOW(), '供应商查询', NULL, NULL, NULL, NULL FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-000000000d21');
/
INSERT INTO sb_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, is_platform_only, default_open_to_customer, del_flag, create_by, create_time, remark, update_by, update_time, delete_by, delete_time)
SELECT '01900000-0000-7000-8000-000000000d22', '供应商新增', '01900000-0000-7000-8000-00000000d012', 2, '#', NULL, '1', '0', 'F', '0', '0', 'equipment:supplier:add', '#', '0', '0', '0', 'admin', NOW(), '供应商新增', NULL, NULL, NULL, NULL FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-000000000d22');
/
INSERT INTO sb_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, is_platform_only, default_open_to_customer, del_flag, create_by, create_time, remark, update_by, update_time, delete_by, delete_time)
SELECT '01900000-0000-7000-8000-000000000d23', '供应商修改', '01900000-0000-7000-8000-00000000d012', 3, '#', NULL, '1', '0', 'F', '0', '0', 'equipment:supplier:edit', '#', '0', '0', '0', 'admin', NOW(), '供应商修改', NULL, NULL, NULL, NULL FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-000000000d23');
/
INSERT INTO sb_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, is_platform_only, default_open_to_customer, del_flag, create_by, create_time, remark, update_by, update_time, delete_by, delete_time)
SELECT '01900000-0000-7000-8000-000000000d24', '供应商删除', '01900000-0000-7000-8000-00000000d012', 4, '#', NULL, '1', '0', 'F', '0', '0', 'equipment:supplier:remove', '#', '0', '0', '0', 'admin', NOW(), '供应商删除', NULL, NULL, NULL, NULL FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-000000000d24');
/
INSERT INTO sb_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, is_platform_only, default_open_to_customer, del_flag, create_by, create_time, remark, update_by, update_time, delete_by, delete_time)
SELECT '01900000-0000-7000-8000-000000000d31', '资产分类查询', '01900000-0000-7000-8000-00000000d013', 1, '#', NULL, '1', '0', 'F', '0', '0', 'equipment:assetCategory:query', '#', '0', '0', '0', 'admin', NOW(), '资产分类查询', NULL, NULL, NULL, NULL FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-000000000d31');
/
INSERT INTO sb_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, is_platform_only, default_open_to_customer, del_flag, create_by, create_time, remark, update_by, update_time, delete_by, delete_time)
SELECT '01900000-0000-7000-8000-000000000d32', '资产分类新增', '01900000-0000-7000-8000-00000000d013', 2, '#', NULL, '1', '0', 'F', '0', '0', 'equipment:assetCategory:add', '#', '0', '0', '0', 'admin', NOW(), '资产分类新增', NULL, NULL, NULL, NULL FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-000000000d32');
/
INSERT INTO sb_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, is_platform_only, default_open_to_customer, del_flag, create_by, create_time, remark, update_by, update_time, delete_by, delete_time)
SELECT '01900000-0000-7000-8000-000000000d33', '资产分类修改', '01900000-0000-7000-8000-00000000d013', 3, '#', NULL, '1', '0', 'F', '0', '0', 'equipment:assetCategory:edit', '#', '0', '0', '0', 'admin', NOW(), '资产分类修改', NULL, NULL, NULL, NULL FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-000000000d33');
/
INSERT INTO sb_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, is_platform_only, default_open_to_customer, del_flag, create_by, create_time, remark, update_by, update_time, delete_by, delete_time)
SELECT '01900000-0000-7000-8000-000000000d34', '资产分类删除', '01900000-0000-7000-8000-00000000d013', 4, '#', NULL, '1', '0', 'F', '0', '0', 'equipment:assetCategory:remove', '#', '0', '0', '0', 'admin', NOW(), '资产分类删除', NULL, NULL, NULL, NULL FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-000000000d34');
/
INSERT INTO sb_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, is_platform_only, default_open_to_customer, del_flag, create_by, create_time, remark, update_by, update_time, delete_by, delete_time)
SELECT '01900000-0000-7000-8000-000000000d41', '计量分类查询', '01900000-0000-7000-8000-00000000d014', 1, '#', NULL, '1', '0', 'F', '0', '0', 'equipment:measuringCategory:query', '#', '0', '0', '0', 'admin', NOW(), '计量分类查询', NULL, NULL, NULL, NULL FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-000000000d41');
/
INSERT INTO sb_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, is_platform_only, default_open_to_customer, del_flag, create_by, create_time, remark, update_by, update_time, delete_by, delete_time)
SELECT '01900000-0000-7000-8000-000000000d42', '计量分类新增', '01900000-0000-7000-8000-00000000d014', 2, '#', NULL, '1', '0', 'F', '0', '0', 'equipment:measuringCategory:add', '#', '0', '0', '0', 'admin', NOW(), '计量分类新增', NULL, NULL, NULL, NULL FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-000000000d42');
/
INSERT INTO sb_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, is_platform_only, default_open_to_customer, del_flag, create_by, create_time, remark, update_by, update_time, delete_by, delete_time)
SELECT '01900000-0000-7000-8000-000000000d43', '计量分类修改', '01900000-0000-7000-8000-00000000d014', 3, '#', NULL, '1', '0', 'F', '0', '0', 'equipment:measuringCategory:edit', '#', '0', '0', '0', 'admin', NOW(), '计量分类修改', NULL, NULL, NULL, NULL FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-000000000d43');
/
INSERT INTO sb_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, is_platform_only, default_open_to_customer, del_flag, create_by, create_time, remark, update_by, update_time, delete_by, delete_time)
SELECT '01900000-0000-7000-8000-000000000d44', '计量分类删除', '01900000-0000-7000-8000-00000000d014', 4, '#', NULL, '1', '0', 'F', '0', '0', 'equipment:measuringCategory:remove', '#', '0', '0', '0', 'admin', NOW(), '计量分类删除', NULL, NULL, NULL, NULL FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-000000000d44');
/

-- 设备字典维护 按钮：新增/修改/删除/导出/导入
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, is_platform_only, default_open_to_customer, del_flag, create_by, create_time, remark, update_by, update_time, delete_by, delete_time
)
SELECT
  '01900000-0000-7000-8000-00000000c743',
  '设备字典新增',
  '01900000-0000-7000-8000-0000000013ed',
  1,
  '#',
  NULL,
  '1',
  '0',
  'F',
  '0',
  '0',
  'foundation:equipmentDict:add',
  '#',
  '0',
  '0',
  '0',
  'admin',
  NOW(),
  '设备字典新增按钮',
  NULL,
  NULL,
  NULL,
  NULL
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-00000000c743');
/

INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, is_platform_only, default_open_to_customer, del_flag, create_by, create_time, remark, update_by, update_time, delete_by, delete_time
)
SELECT
  '01900000-0000-7000-8000-00000000c744',
  '设备字典修改',
  '01900000-0000-7000-8000-0000000013ed',
  2,
  '#',
  NULL,
  '1',
  '0',
  'F',
  '0',
  '0',
  'foundation:equipmentDict:edit',
  '#',
  '0',
  '0',
  '0',
  'admin',
  NOW(),
  '设备字典修改按钮',
  NULL,
  NULL,
  NULL,
  NULL
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-00000000c744');
/

INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, is_platform_only, default_open_to_customer, del_flag, create_by, create_time, remark, update_by, update_time, delete_by, delete_time
)
SELECT
  '01900000-0000-7000-8000-00000000c745',
  '设备字典删除',
  '01900000-0000-7000-8000-0000000013ed',
  3,
  '#',
  NULL,
  '1',
  '0',
  'F',
  '0',
  '0',
  'foundation:equipmentDict:remove',
  '#',
  '0',
  '0',
  '0',
  'admin',
  NOW(),
  '设备字典删除按钮',
  NULL,
  NULL,
  NULL,
  NULL
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-00000000c745');
/

INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, is_platform_only, default_open_to_customer, del_flag, create_by, create_time, remark, update_by, update_time, delete_by, delete_time
)
SELECT
  '01900000-0000-7000-8000-00000000c746',
  '设备字典导出',
  '01900000-0000-7000-8000-0000000013ed',
  4,
  '#',
  NULL,
  '1',
  '0',
  'F',
  '0',
  '0',
  'foundation:equipmentDict:export',
  '#',
  '0',
  '0',
  '0',
  'admin',
  NOW(),
  '设备字典导出按钮',
  NULL,
  NULL,
  NULL,
  NULL
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-00000000c746');
/

INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, is_platform_only, default_open_to_customer, del_flag, create_by, create_time, remark, update_by, update_time, delete_by, delete_time
)
SELECT
  '01900000-0000-7000-8000-00000000c747',
  '设备字典导入',
  '01900000-0000-7000-8000-0000000013ed',
  5,
  '#',
  NULL,
  '1',
  '0',
  'F',
  '0',
  '0',
  'foundation:equipmentDict:import',
  '#',
  '0',
  '0',
  '0',
  'admin',
  NOW(),
  '设备字典导入按钮',
  NULL,
  NULL,
  NULL,
  NULL
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-00000000c747');
/

-- 基础资料子菜单：检验平台设置
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, is_platform_only, default_open_to_customer, del_flag, create_by, create_time, remark, update_by, update_time, delete_by, delete_time
)
SELECT
  '01900000-0000-7000-8000-0000000013ee',
  '检验平台设置',
  '01900000-0000-7000-8000-0000000013ec',
  2,
  'testplat',
  'foundation/testplat/index',
  '1',
  '0',
  'C',
  '0',
  '0',
  'foundation:testplat:list',
  'component',
  '0',
  '0',
  '0',
  'admin',
  NOW(),
  '检验平台设置',
  NULL,
  NULL,
  NULL,
  NULL
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-0000000013ee');
/

-- 基础资料子菜单：厂家维护
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, is_platform_only, default_open_to_customer, del_flag, create_by, create_time, remark, update_by, update_time, delete_by, delete_time
)
SELECT
  '01900000-0000-7000-8000-0000000013ef',
  '厂家维护',
  '01900000-0000-7000-8000-0000000013ec',
  3,
  'factory',
  'foundation/factory/index',
  '1',
  '0',
  'C',
  '0',
  '0',
  'foundation:factory:list',
  'factory',
  '0',
  '0',
  '0',
  'admin',
  NOW(),
  '厂家维护',
  NULL,
  NULL,
  NULL,
  NULL
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-0000000013ef');
/

-- 厂家维护 按钮：新增/修改/删除/导出
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, is_platform_only, default_open_to_customer, del_flag, create_by, create_time, remark, update_by, update_time, delete_by, delete_time
)
SELECT
  '01900000-0000-7000-8000-00000000c757',
  '厂家新增',
  '01900000-0000-7000-8000-0000000013ef',
  1,
  '#',
  NULL,
  '1',
  '0',
  'F',
  '0',
  '0',
  'foundation:factory:add',
  '#',
  '0',
  '0',
  '0',
  'admin',
  NOW(),
  '厂家新增按钮',
  NULL,
  NULL,
  NULL,
  NULL
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-00000000c757');
/

INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, is_platform_only, default_open_to_customer, del_flag, create_by, create_time, remark, update_by, update_time, delete_by, delete_time
)
SELECT
  '01900000-0000-7000-8000-00000000c758',
  '厂家修改',
  '01900000-0000-7000-8000-0000000013ef',
  2,
  '#',
  NULL,
  '1',
  '0',
  'F',
  '0',
  '0',
  'foundation:factory:edit',
  '#',
  '0',
  '0',
  '0',
  'admin',
  NOW(),
  '厂家修改按钮',
  NULL,
  NULL,
  NULL,
  NULL
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-00000000c758');
/

INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, is_platform_only, default_open_to_customer, del_flag, create_by, create_time, remark, update_by, update_time, delete_by, delete_time
)
SELECT
  '01900000-0000-7000-8000-00000000c759',
  '厂家删除',
  '01900000-0000-7000-8000-0000000013ef',
  3,
  '#',
  NULL,
  '1',
  '0',
  'F',
  '0',
  '0',
  'foundation:factory:remove',
  '#',
  '0',
  '0',
  '0',
  'admin',
  NOW(),
  '厂家删除按钮',
  NULL,
  NULL,
  NULL,
  NULL
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-00000000c759');
/

INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, is_platform_only, default_open_to_customer, del_flag, create_by, create_time, remark, update_by, update_time, delete_by, delete_time
)
SELECT
  '01900000-0000-7000-8000-00000000c75a',
  '厂家导出',
  '01900000-0000-7000-8000-0000000013ef',
  4,
  '#',
  NULL,
  '1',
  '0',
  'F',
  '0',
  '0',
  'foundation:factory:export',
  '#',
  '0',
  '0',
  '0',
  'admin',
  NOW(),
  '厂家导出按钮',
  NULL,
  NULL,
  NULL,
  NULL
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-00000000c75a');
/

-- 基础资料子菜单：仓库维护
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, is_platform_only, default_open_to_customer, del_flag, create_by, create_time, remark, update_by, update_time, delete_by, delete_time
)
SELECT
  '01900000-0000-7000-8000-0000000013f0',
  '仓库维护',
  '01900000-0000-7000-8000-0000000013ec',
  4,
  'warehouse',
  'foundation/warehouse/index',
  '1',
  '0',
  'C',
  '0',
  '0',
  'foundation:warehouse:list',
  'warehouse',
  '0',
  '1',
  '0',
  'admin',
  NOW(),
  '仓库维护',
  NULL,
  NULL,
  NULL,
  NULL
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-0000000013f0');
/

-- 仓库维护 按钮：新增/修改/删除/导出
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, is_platform_only, default_open_to_customer, del_flag, create_by, create_time, remark, update_by, update_time, delete_by, delete_time
)
SELECT
  '01900000-0000-7000-8000-00000000c761',
  '仓库新增',
  '01900000-0000-7000-8000-0000000013f0',
  1,
  '#',
  NULL,
  '1',
  '0',
  'F',
  '0',
  '0',
  'foundation:warehouse:add',
  '#',
  '0',
  '0',
  '0',
  'admin',
  NOW(),
  '仓库新增按钮',
  NULL,
  NULL,
  NULL,
  NULL
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-00000000c761');
/

INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, is_platform_only, default_open_to_customer, del_flag, create_by, create_time, remark, update_by, update_time, delete_by, delete_time
)
SELECT
  '01900000-0000-7000-8000-00000000c762',
  '仓库修改',
  '01900000-0000-7000-8000-0000000013f0',
  2,
  '#',
  NULL,
  '1',
  '0',
  'F',
  '0',
  '0',
  'foundation:warehouse:edit',
  '#',
  '0',
  '0',
  '0',
  'admin',
  NOW(),
  '仓库修改按钮',
  NULL,
  NULL,
  NULL,
  NULL
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-00000000c762');
/

INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, is_platform_only, default_open_to_customer, del_flag, create_by, create_time, remark, update_by, update_time, delete_by, delete_time
)
SELECT
  '01900000-0000-7000-8000-00000000c763',
  '仓库删除',
  '01900000-0000-7000-8000-0000000013f0',
  3,
  '#',
  NULL,
  '1',
  '0',
  'F',
  '0',
  '0',
  'foundation:warehouse:remove',
  '#',
  '0',
  '0',
  '0',
  'admin',
  NOW(),
  '仓库删除按钮',
  NULL,
  NULL,
  NULL,
  NULL
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-00000000c763');
/

INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, is_platform_only, default_open_to_customer, del_flag, create_by, create_time, remark, update_by, update_time, delete_by, delete_time
)
SELECT
  '01900000-0000-7000-8000-00000000c764',
  '仓库导出',
  '01900000-0000-7000-8000-0000000013f0',
  4,
  '#',
  NULL,
  '1',
  '0',
  'F',
  '0',
  '0',
  'foundation:warehouse:export',
  '#',
  '0',
  '0',
  '0',
  'admin',
  NOW(),
  '仓库导出按钮',
  NULL,
  NULL,
  NULL,
  NULL
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-00000000c764');
/

-- 基础资料子菜单：科室维护
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, is_platform_only, default_open_to_customer, del_flag, create_by, create_time, remark, update_by, update_time, delete_by, delete_time
)
SELECT
  '01900000-0000-7000-8000-0000000013f1',
  '科室维护',
  '01900000-0000-7000-8000-0000000013ec',
  5,
  'depart',
  'foundation/depart/index',
  '1',
  '0',
  'C',
  '0',
  '0',
  'foundation:depart:list',
  'tree',
  '0',
  '1',
  '0',
  'admin',
  NOW(),
  '科室维护',
  NULL,
  NULL,
  NULL,
  NULL
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-0000000013f1');
/

-- 科室维护 按钮：新增/修改/删除/导出
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, is_platform_only, default_open_to_customer, del_flag, create_by, create_time, remark, update_by, update_time, delete_by, delete_time
)
SELECT
  '01900000-0000-7000-8000-00000000c76b',
  '科室新增',
  '01900000-0000-7000-8000-0000000013f1',
  1,
  '#',
  NULL,
  '1',
  '0',
  'F',
  '0',
  '0',
  'foundation:depart:add',
  '#',
  '0',
  '0',
  '0',
  'admin',
  NOW(),
  '科室新增按钮',
  NULL,
  NULL,
  NULL,
  NULL
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-00000000c76b');
/

INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, is_platform_only, default_open_to_customer, del_flag, create_by, create_time, remark, update_by, update_time, delete_by, delete_time
)
SELECT
  '01900000-0000-7000-8000-00000000c76c',
  '科室修改',
  '01900000-0000-7000-8000-0000000013f1',
  2,
  '#',
  NULL,
  '1',
  '0',
  'F',
  '0',
  '0',
  'foundation:depart:edit',
  '#',
  '0',
  '0',
  '0',
  'admin',
  NOW(),
  '科室修改按钮',
  NULL,
  NULL,
  NULL,
  NULL
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-00000000c76c');
/

INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, is_platform_only, default_open_to_customer, del_flag, create_by, create_time, remark, update_by, update_time, delete_by, delete_time
)
SELECT
  '01900000-0000-7000-8000-00000000c76d',
  '科室删除',
  '01900000-0000-7000-8000-0000000013f1',
  3,
  '#',
  NULL,
  '1',
  '0',
  'F',
  '0',
  '0',
  'foundation:depart:remove',
  '#',
  '0',
  '0',
  '0',
  'admin',
  NOW(),
  '科室删除按钮',
  NULL,
  NULL,
  NULL,
  NULL
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-00000000c76d');
/

INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, is_platform_only, default_open_to_customer, del_flag, create_by, create_time, remark, update_by, update_time, delete_by, delete_time
)
SELECT
  '01900000-0000-7000-8000-00000000c76e',
  '科室导出',
  '01900000-0000-7000-8000-0000000013f1',
  4,
  '#',
  NULL,
  '1',
  '0',
  'F',
  '0',
  '0',
  'foundation:depart:export',
  '#',
  '0',
  '0',
  '0',
  'admin',
  NOW(),
  '科室导出按钮',
  NULL,
  NULL,
  NULL,
  NULL
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-00000000c76e');
/

-- 基础资料子菜单：供应商维护
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, is_platform_only, default_open_to_customer, del_flag, create_by, create_time, remark, update_by, update_time, delete_by, delete_time
)
SELECT
  '01900000-0000-7000-8000-0000000013f2',
  '供应商维护',
  '01900000-0000-7000-8000-0000000013ec',
  6,
  'supplier',
  'foundation/supplier/index',
  '1',
  '0',
  'C',
  '0',
  '0',
  'foundation:supplier:list',
  'peoples',
  '0',
  '0',
  '0',
  'admin',
  NOW(),
  '供应商维护',
  NULL,
  NULL,
  NULL,
  NULL
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-0000000013f2');
/

-- 供应商维护 按钮：新增/修改/删除/导出
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, is_platform_only, default_open_to_customer, del_flag, create_by, create_time, remark, update_by, update_time, delete_by, delete_time
)
SELECT
  '01900000-0000-7000-8000-00000000c775',
  '供应商新增',
  '01900000-0000-7000-8000-0000000013f2',
  1,
  '#',
  NULL,
  '1',
  '0',
  'F',
  '0',
  '0',
  'foundation:supplier:add',
  '#',
  '0',
  '0',
  '0',
  'admin',
  NOW(),
  '供应商新增按钮',
  NULL,
  NULL,
  NULL,
  NULL
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-00000000c775');
/

INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, is_platform_only, default_open_to_customer, del_flag, create_by, create_time, remark, update_by, update_time, delete_by, delete_time
)
SELECT
  '01900000-0000-7000-8000-00000000c776',
  '供应商修改',
  '01900000-0000-7000-8000-0000000013f2',
  2,
  '#',
  NULL,
  '1',
  '0',
  'F',
  '0',
  '0',
  'foundation:supplier:edit',
  '#',
  '0',
  '0',
  '0',
  'admin',
  NOW(),
  '供应商修改按钮',
  NULL,
  NULL,
  NULL,
  NULL
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-00000000c776');
/

INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, is_platform_only, default_open_to_customer, del_flag, create_by, create_time, remark, update_by, update_time, delete_by, delete_time
)
SELECT
  '01900000-0000-7000-8000-00000000c777',
  '供应商删除',
  '01900000-0000-7000-8000-0000000013f2',
  3,
  '#',
  NULL,
  '1',
  '0',
  'F',
  '0',
  '0',
  'foundation:supplier:remove',
  '#',
  '0',
  '0',
  '0',
  'admin',
  NOW(),
  '供应商删除按钮',
  NULL,
  NULL,
  NULL,
  NULL
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-00000000c777');
/

INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, is_platform_only, default_open_to_customer, del_flag, create_by, create_time, remark, update_by, update_time, delete_by, delete_time
)
SELECT
  '01900000-0000-7000-8000-00000000c778',
  '供应商导出',
  '01900000-0000-7000-8000-0000000013f2',
  4,
  '#',
  NULL,
  '1',
  '0',
  'F',
  '0',
  '0',
  'foundation:supplier:export',
  '#',
  '0',
  '0',
  '0',
  'admin',
  NOW(),
  '供应商导出按钮',
  NULL,
  NULL,
  NULL,
  NULL
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-00000000c778');
/

-- 设备前端角色与菜单关系：为 sb_admin 授予设备业务与基础资料菜单及按钮（IGNORE 避免重复执行时报主键冲突）
INSERT IGNORE INTO sb_role_menu (role_id, menu_id)
SELECT '01900000-0000-7000-8000-000000000001', m.menu_id
FROM sb_menu m
WHERE m.menu_id >= '01900000-0000-7000-8000-000000001388' AND m.menu_id <= '01900000-0000-7000-8000-00000000176f'
  AND NOT EXISTS (
    SELECT 1 FROM sb_role_menu rm
    WHERE rm.role_id = '01900000-0000-7000-8000-000000000001' AND rm.menu_id = m.menu_id
  );
/

-- 系统设置根目录
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, is_platform_only, default_open_to_customer, del_flag, create_by, create_time, remark, update_by, update_time, delete_by, delete_time
)
SELECT
  '01900000-0000-7000-8000-000000001450',
  '系统设置',
  '0',
  3,
  'system',
  'Layout',
  '1',
  '0',
  'M',
  '0',
  '0',
  NULL,
  'system',
  '0',
  '1',
  '0',
  'admin',
  NOW(),
  '系统管理根菜单',
  NULL,
  NULL,
  NULL,
  NULL
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-000000001450');
/

-- 系统设置子菜单：用户管理
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, is_platform_only, default_open_to_customer, del_flag, create_by, create_time, remark, update_by, update_time, delete_by, delete_time
)
SELECT
  '01900000-0000-7000-8000-000000001451',
  '用户管理',
  '01900000-0000-7000-8000-000000001450',
  1,
  'user',
  'system/user/index',
  '1',
  '0',
  'C',
  '0',
  '0',
  'system:user:list',
  'user',
  '0',
  '1',
  '0',
  'admin',
  NOW(),
  '用户管理',
  NULL,
  NULL,
  NULL,
  NULL
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-000000001451');
/

-- 用户管理 按钮：新增/修改/删除/查询/导出/导入
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, is_platform_only, default_open_to_customer, del_flag, create_by, create_time, remark, update_by, update_time, delete_by, delete_time
)
SELECT
  '01900000-0000-7000-8000-00000000cb2f',
  '用户新增',
  '01900000-0000-7000-8000-000000001451',
  1,
  '#',
  NULL,
  '1',
  '0',
  'F',
  '0',
  '0',
  'system:user:add',
  '#',
  '0',
  '0',
  '0',
  'admin',
  NOW(),
  '用户新增按钮',
  NULL,
  NULL,
  NULL,
  NULL
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-00000000cb2f');
/

INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, is_platform_only, default_open_to_customer, del_flag, create_by, create_time, remark, update_by, update_time, delete_by, delete_time
)
SELECT
  '01900000-0000-7000-8000-00000000cb30',
  '用户修改',
  '01900000-0000-7000-8000-000000001451',
  2,
  '#',
  NULL,
  '1',
  '0',
  'F',
  '0',
  '0',
  'system:user:edit',
  '#',
  '0',
  '0',
  '0',
  'admin',
  NOW(),
  '用户修改按钮',
  NULL,
  NULL,
  NULL,
  NULL
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-00000000cb30');
/

INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, is_platform_only, default_open_to_customer, del_flag, create_by, create_time, remark, update_by, update_time, delete_by, delete_time
)
SELECT
  '01900000-0000-7000-8000-00000000cb31',
  '用户删除',
  '01900000-0000-7000-8000-000000001451',
  3,
  '#',
  NULL,
  '1',
  '0',
  'F',
  '0',
  '0',
  'system:user:remove',
  '#',
  '0',
  '0',
  '0',
  'admin',
  NOW(),
  '用户删除按钮',
  NULL,
  NULL,
  NULL,
  NULL
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-00000000cb31');
/

INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, is_platform_only, default_open_to_customer, del_flag, create_by, create_time, remark, update_by, update_time, delete_by, delete_time
)
SELECT
  '01900000-0000-7000-8000-00000000cb32',
  '用户查询',
  '01900000-0000-7000-8000-000000001451',
  4,
  '#',
  NULL,
  '1',
  '0',
  'F',
  '0',
  '0',
  'system:user:query',
  '#',
  '0',
  '0',
  '0',
  'admin',
  NOW(),
  '用户查询按钮',
  NULL,
  NULL,
  NULL,
  NULL
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-00000000cb32');
/

INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, is_platform_only, default_open_to_customer, del_flag, create_by, create_time, remark, update_by, update_time, delete_by, delete_time
)
SELECT
  '01900000-0000-7000-8000-00000000cb33',
  '用户导出',
  '01900000-0000-7000-8000-000000001451',
  5,
  '#',
  NULL,
  '1',
  '0',
  'F',
  '0',
  '0',
  'system:user:export',
  '#',
  '0',
  '0',
  '0',
  'admin',
  NOW(),
  '用户导出按钮',
  NULL,
  NULL,
  NULL,
  NULL
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-00000000cb33');
/

INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, is_platform_only, default_open_to_customer, del_flag, create_by, create_time, remark, update_by, update_time, delete_by, delete_time
)
SELECT
  '01900000-0000-7000-8000-00000000cb34',
  '用户导入',
  '01900000-0000-7000-8000-000000001451',
  6,
  '#',
  NULL,
  '1',
  '0',
  'F',
  '0',
  '0',
  'system:user:import',
  '#',
  '0',
  '0',
  '0',
  'admin',
  NOW(),
  '用户导入按钮',
  NULL,
  NULL,
  NULL,
  NULL
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-00000000cb34');
/

-- 用户管理按钮赋给平台设备管理员角色（便于管理员可见增删改查与授权）
INSERT IGNORE INTO sb_role_menu (role_id, menu_id) SELECT '01900000-0000-7000-8000-000000000001', '01900000-0000-7000-8000-00000000cb2f' FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sb_role_menu WHERE role_id = '01900000-0000-7000-8000-000000000001' AND menu_id = '01900000-0000-7000-8000-00000000cb2f');
/
INSERT IGNORE INTO sb_role_menu (role_id, menu_id) SELECT '01900000-0000-7000-8000-000000000001', '01900000-0000-7000-8000-00000000cb30' FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sb_role_menu WHERE role_id = '01900000-0000-7000-8000-000000000001' AND menu_id = '01900000-0000-7000-8000-00000000cb30');
/
INSERT IGNORE INTO sb_role_menu (role_id, menu_id) SELECT '01900000-0000-7000-8000-000000000001', '01900000-0000-7000-8000-00000000cb31' FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sb_role_menu WHERE role_id = '01900000-0000-7000-8000-000000000001' AND menu_id = '01900000-0000-7000-8000-00000000cb31');
/
INSERT IGNORE INTO sb_role_menu (role_id, menu_id) SELECT '01900000-0000-7000-8000-000000000001', '01900000-0000-7000-8000-00000000cb32' FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sb_role_menu WHERE role_id = '01900000-0000-7000-8000-000000000001' AND menu_id = '01900000-0000-7000-8000-00000000cb32');
/
INSERT IGNORE INTO sb_role_menu (role_id, menu_id) SELECT '01900000-0000-7000-8000-000000000001', '01900000-0000-7000-8000-00000000cb33' FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sb_role_menu WHERE role_id = '01900000-0000-7000-8000-000000000001' AND menu_id = '01900000-0000-7000-8000-00000000cb33');
/
INSERT IGNORE INTO sb_role_menu (role_id, menu_id) SELECT '01900000-0000-7000-8000-000000000001', '01900000-0000-7000-8000-00000000cb34' FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sb_role_menu WHERE role_id = '01900000-0000-7000-8000-000000000001' AND menu_id = '01900000-0000-7000-8000-00000000cb34');
/

-- 系统设置子菜单：角色管理
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, is_platform_only, default_open_to_customer, del_flag, create_by, create_time, remark, update_by, update_time, delete_by, delete_time
)
SELECT
  '01900000-0000-7000-8000-000000001452',
  '角色管理',
  '01900000-0000-7000-8000-000000001450',
  2,
  'role',
  'system/role/index',
  '1',
  '0',
  'C',
  '0',
  '0',
  'system:role:list',
  'peoples',
  '0',
  '1',
  '0',
  'admin',
  NOW(),
  '角色管理',
  NULL,
  NULL,
  NULL,
  NULL
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-000000001452');
/

-- 系统设置-角色管理 按钮：新增/修改/删除/导出
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, is_platform_only, default_open_to_customer, del_flag, create_by, create_time, remark, update_by, update_time, delete_by, delete_time
)
SELECT
  '01900000-0000-7000-8000-00000000cb35',
  '角色新增',
  '01900000-0000-7000-8000-000000001452',
  1,
  '#',
  NULL,
  '1',
  '0',
  'F',
  '0',
  '0',
  'system:role:add',
  '#',
  '0',
  '1',
  '0',
  'admin',
  NOW(),
  '角色新增按钮',
  NULL,
  NULL,
  NULL,
  NULL
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-00000000cb35');
/

INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, is_platform_only, default_open_to_customer, del_flag, create_by, create_time, remark, update_by, update_time, delete_by, delete_time
)
SELECT
  '01900000-0000-7000-8000-00000000cb36',
  '角色修改',
  '01900000-0000-7000-8000-000000001452',
  2,
  '#',
  NULL,
  '1',
  '0',
  'F',
  '0',
  '0',
  'system:role:edit',
  '#',
  '0',
  '1',
  '0',
  'admin',
  NOW(),
  '角色修改按钮',
  NULL,
  NULL,
  NULL,
  NULL
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-00000000cb36');
/

INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, is_platform_only, default_open_to_customer, del_flag, create_by, create_time, remark, update_by, update_time, delete_by, delete_time
)
SELECT
  '01900000-0000-7000-8000-00000000cb37',
  '角色删除',
  '01900000-0000-7000-8000-000000001452',
  3,
  '#',
  NULL,
  '1',
  '0',
  'F',
  '0',
  '0',
  'system:role:remove',
  '#',
  '0',
  '1',
  '0',
  'admin',
  NOW(),
  '角色删除按钮',
  NULL,
  NULL,
  NULL,
  NULL
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-00000000cb37');
/

INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, is_platform_only, default_open_to_customer, del_flag, create_by, create_time, remark, update_by, update_time, delete_by, delete_time
)
SELECT
  '01900000-0000-7000-8000-00000000cb38',
  '角色导出',
  '01900000-0000-7000-8000-000000001452',
  4,
  '#',
  NULL,
  '1',
  '0',
  'F',
  '0',
  '0',
  'system:role:export',
  '#',
  '0',
  '1',
  '0',
  'admin',
  NOW(),
  '角色导出按钮',
  NULL,
  NULL,
  NULL,
  NULL
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-00000000cb38');
/

-- 系统设置子菜单：菜单管理
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, is_platform_only, default_open_to_customer, del_flag, create_by, create_time, remark, update_by, update_time, delete_by, delete_time
)
SELECT
  '01900000-0000-7000-8000-000000001453',
  '菜单管理',
  '01900000-0000-7000-8000-000000001450',
  3,
  'menu',
  'system/menu/index',
  '1',
  '0',
  'C',
  '0',
  '0',
  'system:menu:list',
  'tree-table',
  '0',
  '1',
  '0',
  'admin',
  NOW(),
  '菜单管理',
  NULL,
  NULL,
  NULL,
  NULL
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-000000001453');
/

-- 系统设置-菜单管理 按钮：新增/修改/删除
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, is_platform_only, default_open_to_customer, del_flag, create_by, create_time, remark, update_by, update_time, delete_by, delete_time
)
SELECT
  '01900000-0000-7000-8000-00000000cb3f',
  '菜单新增',
  '01900000-0000-7000-8000-000000001453',
  1,
  '#',
  NULL,
  '1',
  '0',
  'F',
  '0',
  '0',
  'system:menu:add',
  '#',
  '0',
  '1',
  '0',
  'admin',
  NOW(),
  '菜单新增按钮',
  NULL,
  NULL,
  NULL,
  NULL
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-00000000cb3f');
/

INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, is_platform_only, default_open_to_customer, del_flag, create_by, create_time, remark, update_by, update_time, delete_by, delete_time
)
SELECT
  '01900000-0000-7000-8000-00000000cb40',
  '菜单修改',
  '01900000-0000-7000-8000-000000001453',
  2,
  '#',
  NULL,
  '1',
  '0',
  'F',
  '0',
  '0',
  'system:menu:edit',
  '#',
  '0',
  '1',
  '0',
  'admin',
  NOW(),
  '菜单修改按钮',
  NULL,
  NULL,
  NULL,
  NULL
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-00000000cb40');
/

INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, is_platform_only, default_open_to_customer, del_flag, create_by, create_time, remark, update_by, update_time, delete_by, delete_time
)
SELECT
  '01900000-0000-7000-8000-00000000cb41',
  '菜单删除',
  '01900000-0000-7000-8000-000000001453',
  3,
  '#',
  NULL,
  '1',
  '0',
  'F',
  '0',
  '0',
  'system:menu:remove',
  '#',
  '0',
  '1',
  '0',
  'admin',
  NOW(),
  '菜单删除按钮',
  NULL,
  NULL,
  NULL,
  NULL
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-00000000cb41');
/

-- 系统设置子菜单：部门管理
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, is_platform_only, default_open_to_customer, del_flag, create_by, create_time, remark, update_by, update_time, delete_by, delete_time
)
SELECT
  '01900000-0000-7000-8000-000000001454',
  '部门管理',
  '01900000-0000-7000-8000-000000001450',
  4,
  'dept',
  'system/dept/index',
  '1',
  '0',
  'C',
  '0',
  '0',
  'system:dept:list',
  'tree',
  '0',
  '1',
  '0',
  'admin',
  NOW(),
  '部门管理',
  NULL,
  NULL,
  NULL,
  NULL
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-000000001454');
/

-- 系统设置子菜单：岗位管理
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, is_platform_only, default_open_to_customer, del_flag, create_by, create_time, remark, update_by, update_time, delete_by, delete_time
)
SELECT
  '01900000-0000-7000-8000-000000001455',
  '岗位管理',
  '01900000-0000-7000-8000-000000001450',
  5,
  'post',
  'system/post/index',
  '1',
  '0',
  'C',
  '0',
  '0',
  'system:post:list',
  'post',
  '0',
  '1',
  '0',
  'admin',
  NOW(),
  '岗位管理',
  NULL,
  NULL,
  NULL,
  NULL
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-000000001455');
/

-- 系统设置-岗位管理 按钮：查询/新增/修改/删除/导出
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, is_platform_only, default_open_to_customer, del_flag, create_by, create_time, remark, update_by, update_time, delete_by, delete_time
)
SELECT
  '01900000-0000-7000-8000-00000000cb39',
  '岗位查询',
  '01900000-0000-7000-8000-000000001455',
  1,
  '#',
  NULL,
  '1',
  '0',
  'F',
  '0',
  '0',
  'system:post:query',
  '#',
  '0',
  '1',
  '0',
  'admin',
  NOW(),
  '岗位查询按钮',
  NULL,
  NULL,
  NULL,
  NULL
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-00000000cb39');
/
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, is_platform_only, default_open_to_customer, del_flag, create_by, create_time, remark, update_by, update_time, delete_by, delete_time
)
SELECT
  '01900000-0000-7000-8000-00000000cb3a',
  '岗位新增',
  '01900000-0000-7000-8000-000000001455',
  2,
  '#',
  NULL,
  '1',
  '0',
  'F',
  '0',
  '0',
  'system:post:add',
  '#',
  '0',
  '1',
  '0',
  'admin',
  NOW(),
  '岗位新增按钮',
  NULL,
  NULL,
  NULL,
  NULL
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-00000000cb3a');
/
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, is_platform_only, default_open_to_customer, del_flag, create_by, create_time, remark, update_by, update_time, delete_by, delete_time
)
SELECT
  '01900000-0000-7000-8000-00000000cb3b',
  '岗位修改',
  '01900000-0000-7000-8000-000000001455',
  3,
  '#',
  NULL,
  '1',
  '0',
  'F',
  '0',
  '0',
  'system:post:edit',
  '#',
  '0',
  '1',
  '0',
  'admin',
  NOW(),
  '岗位修改按钮',
  NULL,
  NULL,
  NULL,
  NULL
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-00000000cb3b');
/
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, is_platform_only, default_open_to_customer, del_flag, create_by, create_time, remark, update_by, update_time, delete_by, delete_time
)
SELECT
  '01900000-0000-7000-8000-00000000cb3c',
  '岗位删除',
  '01900000-0000-7000-8000-000000001455',
  4,
  '#',
  NULL,
  '1',
  '0',
  'F',
  '0',
  '0',
  'system:post:remove',
  '#',
  '0',
  '1',
  '0',
  'admin',
  NOW(),
  '岗位删除按钮',
  NULL,
  NULL,
  NULL,
  NULL
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-00000000cb3c');
/
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, is_platform_only, default_open_to_customer, del_flag, create_by, create_time, remark, update_by, update_time, delete_by, delete_time
)
SELECT
  '01900000-0000-7000-8000-00000000cb3d',
  '岗位导出',
  '01900000-0000-7000-8000-000000001455',
  5,
  '#',
  NULL,
  '1',
  '0',
  'F',
  '0',
  '0',
  'system:post:export',
  '#',
  '0',
  '1',
  '0',
  'admin',
  NOW(),
  '岗位导出按钮',
  NULL,
  NULL,
  NULL,
  NULL
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-00000000cb3d');
/

-- 岗位管理按钮赋给平台设备管理员角色
INSERT IGNORE INTO sb_role_menu (role_id, menu_id) SELECT '01900000-0000-7000-8000-000000000001', '01900000-0000-7000-8000-00000000cb39' FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sb_role_menu WHERE role_id = '01900000-0000-7000-8000-000000000001' AND menu_id = '01900000-0000-7000-8000-00000000cb39');
/
INSERT IGNORE INTO sb_role_menu (role_id, menu_id) SELECT '01900000-0000-7000-8000-000000000001', '01900000-0000-7000-8000-00000000cb3a' FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sb_role_menu WHERE role_id = '01900000-0000-7000-8000-000000000001' AND menu_id = '01900000-0000-7000-8000-00000000cb3a');
/
INSERT IGNORE INTO sb_role_menu (role_id, menu_id) SELECT '01900000-0000-7000-8000-000000000001', '01900000-0000-7000-8000-00000000cb3b' FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sb_role_menu WHERE role_id = '01900000-0000-7000-8000-000000000001' AND menu_id = '01900000-0000-7000-8000-00000000cb3b');
/
INSERT IGNORE INTO sb_role_menu (role_id, menu_id) SELECT '01900000-0000-7000-8000-000000000001', '01900000-0000-7000-8000-00000000cb3c' FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sb_role_menu WHERE role_id = '01900000-0000-7000-8000-000000000001' AND menu_id = '01900000-0000-7000-00000000cb3c');
/
INSERT IGNORE INTO sb_role_menu (role_id, menu_id) SELECT '01900000-0000-7000-8000-000000000001', '01900000-0000-7000-8000-00000000cb3d' FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sb_role_menu WHERE role_id = '01900000-0000-7000-8000-000000000001' AND menu_id = '01900000-0000-7000-8000-00000000cb3d');
/

-- 岗位管理-设备工作组接口权限（岗位管理页会调用 /equipment/system/workgroup 接口）
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, is_platform_only, default_open_to_customer, del_flag, create_by, create_time, remark, update_by, update_time, delete_by, delete_time
)
SELECT
  '01900000-0000-7000-8000-00000000cb3e',
  '工作组列表',
  '01900000-0000-7000-8000-000000001455',
  6,
  '#',
  NULL,
  '1',
  '0',
  'F',
  '0',
  '0',
  'sb:system:workgroup:list',
  '#',
  '0',
  '1',
  '0',
  'admin',
  NOW(),
  '设备工作组列表接口权限',
  NULL,
  NULL,
  NULL,
  NULL
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-00000000cb3e');
/
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, is_platform_only, default_open_to_customer, del_flag, create_by, create_time, remark, update_by, update_time, delete_by, delete_time
)
SELECT
  '01900000-0000-7000-8000-00000000cb3f',
  '工作组查询',
  '01900000-0000-7000-8000-000000001455',
  7,
  '#',
  NULL,
  '1',
  '0',
  'F',
  '0',
  '0',
  'sb:system:workgroup:query',
  '#',
  '0',
  '1',
  '0',
  'admin',
  NOW(),
  '设备工作组查询接口权限',
  NULL,
  NULL,
  NULL,
  NULL
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-00000000cb3f');
/
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, is_platform_only, default_open_to_customer, del_flag, create_by, create_time, remark, update_by, update_time, delete_by, delete_time
)
SELECT
  '01900000-0000-7000-8000-00000000cb40',
  '工作组新增',
  '01900000-0000-7000-8000-000000001455',
  8,
  '#',
  NULL,
  '1',
  '0',
  'F',
  '0',
  '0',
  'sb:system:workgroup:add',
  '#',
  '0',
  '1',
  '0',
  'admin',
  NOW(),
  '设备工作组新增接口权限',
  NULL,
  NULL,
  NULL,
  NULL
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-00000000cb40');
/
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, is_platform_only, default_open_to_customer, del_flag, create_by, create_time, remark, update_by, update_time, delete_by, delete_time
)
SELECT
  '01900000-0000-7000-8000-00000000cb41',
  '工作组修改',
  '01900000-0000-7000-8000-000000001455',
  9,
  '#',
  NULL,
  '1',
  '0',
  'F',
  '0',
  '0',
  'sb:system:workgroup:edit',
  '#',
  '0',
  '1',
  '0',
  'admin',
  NOW(),
  '设备工作组修改接口权限',
  NULL,
  NULL,
  NULL,
  NULL
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-00000000cb41');
/
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, is_platform_only, default_open_to_customer, del_flag, create_by, create_time, remark, update_by, update_time, delete_by, delete_time
)
SELECT
  '01900000-0000-7000-8000-00000000cb42',
  '工作组删除',
  '01900000-0000-7000-8000-000000001455',
  10,
  '#',
  NULL,
  '1',
  '0',
  'F',
  '0',
  '0',
  'sb:system:workgroup:remove',
  '#',
  '0',
  '1',
  '0',
  'admin',
  NOW(),
  '设备工作组删除接口权限',
  NULL,
  NULL,
  NULL,
  NULL
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-00000000cb42');
/

-- 岗位管理-工作组权限按钮赋给平台设备管理员角色
INSERT IGNORE INTO sb_role_menu (role_id, menu_id) SELECT '01900000-0000-7000-8000-000000000001', '01900000-0000-7000-8000-00000000cb3e' FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sb_role_menu WHERE role_id = '01900000-0000-7000-8000-000000000001' AND menu_id = '01900000-0000-7000-8000-00000000cb3e');
/
INSERT IGNORE INTO sb_role_menu (role_id, menu_id) SELECT '01900000-0000-7000-8000-000000000001', '01900000-0000-7000-8000-00000000cb3f' FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sb_role_menu WHERE role_id = '01900000-0000-7000-8000-000000000001' AND menu_id = '01900000-0000-7000-8000-00000000cb3f');
/
INSERT IGNORE INTO sb_role_menu (role_id, menu_id) SELECT '01900000-0000-7000-8000-000000000001', '01900000-0000-7000-8000-00000000cb40' FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sb_role_menu WHERE role_id = '01900000-0000-7000-8000-000000000001' AND menu_id = '01900000-0000-7000-8000-00000000cb40');
/
INSERT IGNORE INTO sb_role_menu (role_id, menu_id) SELECT '01900000-0000-7000-8000-000000000001', '01900000-0000-7000-8000-00000000cb41' FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sb_role_menu WHERE role_id = '01900000-0000-7000-8000-000000000001' AND menu_id = '01900000-0000-7000-8000-00000000cb41');
/
INSERT IGNORE INTO sb_role_menu (role_id, menu_id) SELECT '01900000-0000-7000-8000-000000000001', '01900000-0000-7000-8000-00000000cb42' FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sb_role_menu WHERE role_id = '01900000-0000-7000-8000-000000000001' AND menu_id = '01900000-0000-7000-8000-00000000cb42');
/

-- 系统设置子菜单：客户管理（SaaS租户）
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, is_platform_only, default_open_to_customer, del_flag, create_by, create_time, remark, update_by, update_time, delete_by, delete_time
)
SELECT
  '01900000-0000-7000-8000-000000001456',
  '客户管理',
  '01900000-0000-7000-8000-000000001450',
  6,
  'customer',
  'system/customer/index',
  '1',
  '0',
  'C',
  '0',
  '0',
  'sb:system:customer:list',
  'peoples',
  '0',
  '0',
  '0',
  'admin',
  NOW(),
  '设备系统客户（租户）管理',
  NULL,
  NULL,
  NULL,
  NULL
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-000000001456');
/

-- 系统设置子菜单：客户菜单功能管理（对客户已具备功能做启用/停用，仅平台可见）
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, is_platform_only, default_open_to_customer, del_flag, create_by, create_time, remark, update_by, update_time, delete_by, delete_time
)
SELECT
  '01900000-0000-7000-8000-000000001457',
  '客户菜单功能管理',
  '01900000-0000-7000-8000-000000001450',
  7,
  'customerMenuManage',
  'system/customerMenuManage/index',
  '1',
  '0',
  'C',
  '0',
  '0',
  'sb:system:customerMenuManage:list',
  'switch',
  '0',
  '0',
  '0',
  'admin',
  NOW(),
  '客户名下已具备功能的启用停用，租户不可见',
  NULL,
  NULL,
  NULL,
  NULL
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-000000001457');
/

-- 系统设置-客户菜单功能管理 按钮
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, is_platform_only, default_open_to_customer, del_flag, create_by, create_time, remark, update_by, update_time, delete_by, delete_time
)
SELECT
  '01900000-0000-7000-8000-00000000cb5e',
  '功能管理列表',
  '01900000-0000-7000-8000-000000001457',
  1,
  '#',
  NULL,
  '1',
  '0',
  'F',
  '0',
  '0',
  'sb:system:customerMenuManage:list',
  '#',
  '0',
  '0',
  '0',
  'admin',
  NOW(),
  '客户菜单功能管理列表',
  NULL,
  NULL,
  NULL,
  NULL
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-00000000cb5e');
/

INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, is_platform_only, default_open_to_customer, del_flag, create_by, create_time, remark, update_by, update_time, delete_by, delete_time
)
SELECT
  '01900000-0000-7000-8000-00000000cb5f',
  '功能管理查询',
  '01900000-0000-7000-8000-000000001457',
  2,
  '#',
  NULL,
  '1',
  '0',
  'F',
  '0',
  '0',
  'sb:system:customerMenuManage:query',
  '#',
  '0',
  '0',
  '0',
  'admin',
  NOW(),
  '启停用记录与时间段查询',
  NULL,
  NULL,
  NULL,
  NULL
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-00000000cb5f');
/

INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, is_platform_only, default_open_to_customer, del_flag, create_by, create_time, remark, update_by, update_time, delete_by, delete_time
)
SELECT
  '01900000-0000-7000-8000-00000000cb60',
  '功能管理启停用',
  '01900000-0000-7000-8000-000000001457',
  3,
  '#',
  NULL,
  '1',
  '0',
  'F',
  '0',
  '0',
  'sb:system:customerMenuManage:edit',
  '#',
  '0',
  '0',
  '0',
  'admin',
  NOW(),
  '客户菜单功能启用停用',
  NULL,
  NULL,
  NULL,
  NULL
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-00000000cb60');
/

-- 系统设置-客户管理 按钮：查询/新增/修改/删除
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, is_platform_only, default_open_to_customer, del_flag, create_by, create_time, remark, update_by, update_time, delete_by, delete_time
)
SELECT
  '01900000-0000-7000-8000-00000000cb5a',
  '客户查询',
  '01900000-0000-7000-8000-000000001456',
  1,
  '#',
  NULL,
  '1',
  '0',
  'F',
  '0',
  '0',
  'sb:system:customer:query',
  '#',
  '0',
  '0',
  '0',
  'admin',
  NOW(),
  '客户查询按钮',
  NULL,
  NULL,
  NULL,
  NULL
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-00000000cb5a');
/

INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, is_platform_only, default_open_to_customer, del_flag, create_by, create_time, remark, update_by, update_time, delete_by, delete_time
)
SELECT
  '01900000-0000-7000-8000-00000000cb5b',
  '客户新增',
  '01900000-0000-7000-8000-000000001456',
  2,
  '#',
  NULL,
  '1',
  '0',
  'F',
  '0',
  '0',
  'sb:system:customer:add',
  '#',
  '0',
  '0',
  '0',
  'admin',
  NOW(),
  '客户新增按钮',
  NULL,
  NULL,
  NULL,
  NULL
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-00000000cb5b');
/

INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, is_platform_only, default_open_to_customer, del_flag, create_by, create_time, remark, update_by, update_time, delete_by, delete_time
)
SELECT
  '01900000-0000-7000-8000-00000000cb5c',
  '客户修改',
  '01900000-0000-7000-8000-000000001456',
  3,
  '#',
  NULL,
  '1',
  '0',
  'F',
  '0',
  '0',
  'sb:system:customer:edit',
  '#',
  '0',
  '0',
  '0',
  'admin',
  NOW(),
  '客户修改及菜单权限按钮',
  NULL,
  NULL,
  NULL,
  NULL
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-00000000cb5c');
/

INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, is_platform_only, default_open_to_customer, del_flag, create_by, create_time, remark, update_by, update_time, delete_by, delete_time
)
SELECT
  '01900000-0000-7000-8000-00000000cb5d',
  '客户删除',
  '01900000-0000-7000-8000-000000001456',
  4,
  '#',
  NULL,
  '1',
  '0',
  'F',
  '0',
  '0',
  'sb:system:customer:remove',
  '#',
  '0',
  '0',
  '0',
  'admin',
  NOW(),
  '客户删除按钮',
  NULL,
  NULL,
  NULL,
  NULL
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-00000000cb5d');
/

-- 系统设置子菜单：医疗器械68分类（客户可自行维护自己的68分类，以标准 fd_category68 为蓝本）
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, is_platform_only, default_open_to_customer, del_flag, create_by, create_time, remark, update_by, update_time, delete_by, delete_time
)
SELECT
  '01900000-0000-7000-8000-00000000cc68',
  '医疗器械68分类',
  '01900000-0000-7000-8000-000000001450',
  65,
  'customerCategory68',
  'foundation/customerCategory68/index',
  '1',
  '0',
  'C',
  '0',
  '0',
  'foundation:customerCategory68:list',
  'tree-table',
  '0',
  '1',
  '0',
  'admin',
  NOW(),
  '客户对自身医疗器械68分类的增删改查与同步',
  NULL,
  NULL,
  NULL,
  NULL
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-00000000cc68');
/

INSERT INTO sb_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, is_platform_only, default_open_to_customer, del_flag, create_by, create_time, remark, update_by, update_time, delete_by, delete_time)
SELECT '01900000-0000-7000-8000-00000000cc69', '医疗器械68分类查询', '01900000-0000-7000-8000-00000000cc68', 1, '#', NULL, '1', '0', 'F', '0', '0', 'foundation:customerCategory68:query', '#', '0', '1', '0', 'admin', NOW(), '医疗器械68分类查询', NULL, NULL, NULL, NULL FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-00000000cc69');
/
INSERT INTO sb_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, is_platform_only, default_open_to_customer, del_flag, create_by, create_time, remark, update_by, update_time, delete_by, delete_time)
SELECT '01900000-0000-7000-8000-00000000cc6a', '医疗器械68分类新增', '01900000-0000-7000-8000-00000000cc68', 2, '#', NULL, '1', '0', 'F', '0', '0', 'foundation:customerCategory68:add', '#', '0', '1', '0', 'admin', NOW(), '医疗器械68分类新增', NULL, NULL, NULL, NULL FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-00000000cc6a');
/
INSERT INTO sb_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, is_platform_only, default_open_to_customer, del_flag, create_by, create_time, remark, update_by, update_time, delete_by, delete_time)
SELECT '01900000-0000-7000-8000-00000000cc6b', '医疗器械68分类修改', '01900000-0000-7000-8000-00000000cc68', 3, '#', NULL, '1', '0', 'F', '0', '0', 'foundation:customerCategory68:edit', '#', '0', '1', '0', 'admin', NOW(), '医疗器械68分类修改', NULL, NULL, NULL, NULL FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-00000000cc6b');
/
INSERT INTO sb_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, is_platform_only, default_open_to_customer, del_flag, create_by, create_time, remark, update_by, update_time, delete_by, delete_time)
SELECT '01900000-0000-7000-8000-00000000cc6c', '医疗器械68分类删除', '01900000-0000-7000-8000-00000000cc68', 4, '#', NULL, '1', '0', 'F', '0', '0', 'foundation:customerCategory68:remove', '#', '0', '1', '0', 'admin', NOW(), '医疗器械68分类删除', NULL, NULL, NULL, NULL FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-00000000cc6c');
/
INSERT INTO sb_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, is_platform_only, default_open_to_customer, del_flag, create_by, create_time, remark, update_by, update_time, delete_by, delete_time)
SELECT '01900000-0000-7000-8000-00000000cc6d', '医疗器械68分类同步', '01900000-0000-7000-8000-00000000cc68', 5, '#', NULL, '1', '0', 'F', '0', '0', 'foundation:customerCategory68:sync', '#', '0', '1', '0', 'admin', NOW(), '以标准68分类为蓝本同步', NULL, NULL, NULL, NULL FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-00000000cc6d');
/
INSERT INTO sb_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, is_platform_only, default_open_to_customer, del_flag, create_by, create_time, remark, update_by, update_time, delete_by, delete_time)
SELECT '01900000-0000-7000-8000-00000000cc6e', '医疗器械68分类操作记录', '01900000-0000-7000-8000-00000000cc68', 6, '#', NULL, '1', '0', 'F', '0', '0', 'foundation:customerCategory68:log', '#', '0', '1', '0', 'admin', NOW(), '查看修改记录', NULL, NULL, NULL, NULL FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-00000000cc6e');
/

-- 将已存在的「客户68分类维护」相关菜单名称更新为「医疗器械68分类」（每段一条语句执行）
UPDATE sb_menu SET menu_name = '医疗器械68分类', remark = '客户对自身医疗器械68分类的增删改查与同步' WHERE menu_id = '01900000-0000-7000-8000-00000000cc68';
/
UPDATE sb_menu SET menu_name = '医疗器械68分类查询' WHERE menu_id = '01900000-0000-7000-8000-00000000cc69';
/
UPDATE sb_menu SET menu_name = '医疗器械68分类新增' WHERE menu_id = '01900000-0000-7000-8000-00000000cc6a';
/
UPDATE sb_menu SET menu_name = '医疗器械68分类修改' WHERE menu_id = '01900000-0000-7000-8000-00000000cc6b';
/
UPDATE sb_menu SET menu_name = '医疗器械68分类删除' WHERE menu_id = '01900000-0000-7000-8000-00000000cc6c';
/
UPDATE sb_menu SET menu_name = '医疗器械68分类同步' WHERE menu_id = '01900000-0000-7000-8000-00000000cc6d';
/
UPDATE sb_menu SET menu_name = '医疗器械68分类操作记录' WHERE menu_id = '01900000-0000-7000-8000-00000000cc6e';
/

-- 系统设置子菜单：字典管理
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, is_platform_only, default_open_to_customer, del_flag, create_by, create_time, remark, update_by, update_time, delete_by, delete_time
)
SELECT
  '01900000-0000-7000-8000-000000001456',
  '字典管理',
  '01900000-0000-7000-8000-000000001450',
  6,
  'dict',
  'system/dict/index',
  '1',
  '0',
  'C',
  '0',
  '0',
  'system:dict:list',
  'dict',
  '0',
  '0',
  '0',
  'admin',
  NOW(),
  '字典管理',
  NULL,
  NULL,
  NULL,
  NULL
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-000000001456');
/

-- 系统设置子菜单：参数设置
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, is_platform_only, default_open_to_customer, del_flag, create_by, create_time, remark, update_by, update_time, delete_by, delete_time
)
SELECT
  '01900000-0000-7000-8000-000000001457',
  '参数设置',
  '01900000-0000-7000-8000-000000001450',
  7,
  'config',
  'system/config/index',
  '1',
  '0',
  'C',
  '0',
  '0',
  'system:config:list',
  'edit',
  '0',
  '0',
  '0',
  'admin',
  NOW(),
  '参数设置',
  NULL,
  NULL,
  NULL,
  NULL
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-000000001457');
/

-- 系统设置子菜单：通知公告
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, is_platform_only, default_open_to_customer, del_flag, create_by, create_time, remark, update_by, update_time, delete_by, delete_time
)
SELECT
  '01900000-0000-7000-8000-000000001458',
  '通知公告',
  '01900000-0000-7000-8000-000000001450',
  8,
  'notice',
  'system/notice/index',
  '1',
  '0',
  'C',
  '0',
  '0',
  'system:notice:list',
  'message',
  '0',
  '1',
  '0',
  'admin',
  NOW(),
  '通知公告',
  NULL,
  NULL,
  NULL,
  NULL
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-000000001458');
/

-- 系统设置子菜单：部门角色管理
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, is_platform_only, default_open_to_customer, del_flag, create_by, create_time, remark, update_by, update_time, delete_by, delete_time
)
SELECT
  '01900000-0000-7000-8000-000000001459',
  '部门角色管理',
  '01900000-0000-7000-8000-000000001450',
  9,
  'departmeRole',
  'system/departmeRole/index',
  '1',
  '0',
  'C',
  '0',
  '0',
  NULL,
  'tree',
  '0',
  '1',
  '0',
  'admin',
  NOW(),
  '部门角色管理',
  NULL,
  NULL,
  NULL,
  NULL
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-000000001459');
/

-- 系统设置子菜单：库房角色管理
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, is_platform_only, default_open_to_customer, del_flag, create_by, create_time, remark, update_by, update_time, delete_by, delete_time
)
SELECT
  '01900000-0000-7000-8000-00000000145a',
  '库房角色管理',
  '01900000-0000-7000-8000-000000001450',
  10,
  'warehouseRole',
  'system/warehouseRole/index',
  '1',
  '0',
  'C',
  '0',
  '0',
  NULL,
  'warehouse',
  '0',
  '1',
  '0',
  'admin',
  NOW(),
  '库房角色管理',
  NULL,
  NULL,
  NULL,
  NULL
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-00000000145a');
/

-- 系统监控根目录
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, is_platform_only, default_open_to_customer, del_flag, create_by, create_time, remark, update_by, update_time, delete_by, delete_time
)
SELECT
  '01900000-0000-7000-8000-0000000014b4',
  '系统监控',
  '0',
  4,
  'monitor',
  'Layout',
  '1',
  '0',
  'M',
  '0',
  '0',
  NULL,
  'monitor',
  '0',
  '0',
  '0',
  'admin',
  NOW(),
  '系统监控根菜单',
  NULL,
  NULL,
  NULL,
  NULL
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-0000000014b4');
/

-- 系统监控子菜单：在线用户
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, is_platform_only, default_open_to_customer, del_flag, create_by, create_time, remark, update_by, update_time, delete_by, delete_time
)
SELECT
  '01900000-0000-7000-8000-0000000014b5',
  '在线用户',
  '01900000-0000-7000-8000-0000000014b4',
  1,
  'online',
  'monitor/online/index',
  '1',
  '0',
  'C',
  '0',
  '0',
  'monitor:online:list',
  'online',
  '0',
  '0',
  '0',
  'admin',
  NOW(),
  '在线用户',
  NULL,
  NULL,
  NULL,
  NULL
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-0000000014b5');
/

-- 系统监控子菜单：定时任务
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, is_platform_only, default_open_to_customer, del_flag, create_by, create_time, remark, update_by, update_time, delete_by, delete_time
)
SELECT
  '01900000-0000-7000-8000-0000000014b6',
  '定时任务',
  '01900000-0000-7000-8000-0000000014b4',
  2,
  'job',
  'monitor/job/index',
  '1',
  '0',
  'C',
  '0',
  '0',
  'monitor:job:list',
  'job',
  '0',
  '0',
  '0',
  'admin',
  NOW(),
  '定时任务',
  NULL,
  NULL,
  NULL,
  NULL
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-0000000014b6');
/

-- 系统监控子菜单：调度日志入口（列表页）
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, is_platform_only, default_open_to_customer, del_flag, create_by, create_time, remark, update_by, update_time, delete_by, delete_time
)
SELECT
  '01900000-0000-7000-8000-0000000014b7',
  '调度日志',
  '01900000-0000-7000-8000-0000000014b4',
  3,
  'job-log',
  'monitor/job/log',
  '1',
  '0',
  'C',
  '0',
  '0',
  'monitor:job:list',
  'log',
  '0',
  '0',
  '0',
  'admin',
  NOW(),
  '调度日志',
  NULL,
  NULL,
  NULL,
  NULL
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-0000000014b7');
/

-- 系统监控子菜单：操作日志
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, is_platform_only, default_open_to_customer, del_flag, create_by, create_time, remark, update_by, update_time, delete_by, delete_time
)
SELECT
  '01900000-0000-7000-8000-0000000014b8',
  '操作日志',
  '01900000-0000-7000-8000-0000000014b4',
  4,
  'operlog',
  'monitor/operlog/index',
  '1',
  '0',
  'C',
  '0',
  '0',
  'monitor:operlog:list',
  'form',
  '0',
  '0',
  '0',
  'admin',
  NOW(),
  '操作日志',
  NULL,
  NULL,
  NULL,
  NULL
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-0000000014b8');
/

-- 系统监控子菜单：登录日志
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, is_platform_only, default_open_to_customer, del_flag, create_by, create_time, remark, update_by, update_time, delete_by, delete_time
)
SELECT
  '01900000-0000-7000-8000-0000000014b9',
  '登录日志',
  '01900000-0000-7000-8000-0000000014b4',
  5,
  'logininfor',
  'monitor/logininfor/index',
  '1',
  '0',
  'C',
  '0',
  '0',
  'monitor:logininfor:list',
  'logininfor',
  '0',
  '0',
  '0',
  'admin',
  NOW(),
  '登录日志',
  NULL,
  NULL,
  NULL,
  NULL
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-0000000014b9');
/

-- 系统监控子菜单：服务监控
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, is_platform_only, default_open_to_customer, del_flag, create_by, create_time, remark, update_by, update_time, delete_by, delete_time
)
SELECT
  '01900000-0000-7000-8000-0000000014ba',
  '服务监控',
  '01900000-0000-7000-8000-0000000014b4',
  6,
  'server',
  'monitor/server/index',
  '1',
  '0',
  'C',
  '0',
  '0',
  'monitor:server:list',
  'server',
  '0',
  '0',
  '0',
  'admin',
  NOW(),
  '服务监控',
  NULL,
  NULL,
  NULL,
  NULL
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-0000000014ba');
/

-- 系统监控子菜单：缓存监控
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, is_platform_only, default_open_to_customer, del_flag, create_by, create_time, remark, update_by, update_time, delete_by, delete_time
)
SELECT
  '01900000-0000-7000-8000-0000000014bb',
  '缓存监控',
  '01900000-0000-7000-8000-0000000014b4',
  7,
  'cache',
  'monitor/cache/index',
  '1',
  '0',
  'C',
  '0',
  '0',
  'monitor:cache:list',
  'redis',
  '0',
  '0',
  '0',
  'admin',
  NOW(),
  '缓存监控',
  NULL,
  NULL,
  NULL,
  NULL
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-0000000014bb');
/

-- 系统监控子菜单：缓存列表
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, is_platform_only, default_open_to_customer, del_flag, create_by, create_time, remark, update_by, update_time, delete_by, delete_time
)
SELECT
  '01900000-0000-7000-8000-0000000014bc',
  '缓存列表',
  '01900000-0000-7000-8000-0000000014b4',
  8,
  'cache-list',
  'monitor/cache/list',
  '1',
  '0',
  'C',
  '0',
  '0',
  'monitor:cache:list',
  'list',
  '0',
  '0',
  '0',
  'admin',
  NOW(),
  '缓存列表',
  NULL,
  NULL,
  NULL,
  NULL
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-0000000014bc');
/

-- 系统监控子菜单：数据监控（druid）
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, is_platform_only, default_open_to_customer, del_flag, create_by, create_time, remark, update_by, update_time, delete_by, delete_time
)
SELECT
  '01900000-0000-7000-8000-0000000014bd',
  '数据监控',
  '01900000-0000-7000-8000-0000000014b4',
  9,
  'druid',
  'monitor/druid/index',
  '1',
  '0',
  'C',
  '0',
  '0',
  NULL,
  'druid',
  '0',
  '0',
  '0',
  'admin',
  NOW(),
  '数据监控',
  NULL,
  NULL,
  NULL,
  NULL
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-0000000014bd');
/

-- 系统工具根目录
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, is_platform_only, default_open_to_customer, del_flag, create_by, create_time, remark, update_by, update_time, delete_by, delete_time
)
SELECT
  '01900000-0000-7000-8000-000000001518',
  '系统工具',
  '0',
  5,
  'tool',
  'Layout',
  '1',
  '0',
  'M',
  '0',
  '0',
  NULL,
  'tool',
  '0',
  '0',
  '0',
  'admin',
  NOW(),
  '系统工具根菜单',
  NULL,
  NULL,
  NULL,
  NULL
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-000000001518');
/

-- 系统工具子菜单：代码生成
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, is_platform_only, default_open_to_customer, del_flag, create_by, create_time, remark, update_by, update_time, delete_by, delete_time
)
SELECT
  '01900000-0000-7000-8000-000000001519',
  '代码生成',
  '01900000-0000-7000-8000-000000001518',
  1,
  'gen',
  'tool/gen/index',
  '1',
  '0',
  'C',
  '0',
  '0',
  'tool:gen:list',
  'code',
  '0',
  '0',
  '0',
  'admin',
  NOW(),
  '代码生成',
  NULL,
  NULL,
  NULL,
  NULL
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-000000001519');
/

-- 系统工具子菜单：表单构建
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, is_platform_only, default_open_to_customer, del_flag, create_by, create_time, remark, update_by, update_time, delete_by, delete_time
)
SELECT
  '01900000-0000-7000-8000-00000000151a',
  '表单构建',
  '01900000-0000-7000-8000-000000001518',
  2,
  'build',
  'tool/build/index',
  '1',
  '0',
  'C',
  '0',
  '0',
  NULL,
  'build',
  '0',
  '0',
  '0',
  'admin',
  NOW(),
  '表单构建',
  NULL,
  NULL,
  NULL,
  NULL
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-00000000151a');
/

-- 系统工具子菜单：系统接口（Swagger）
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, is_platform_only, default_open_to_customer, del_flag, create_by, create_time, remark, update_by, update_time, delete_by, delete_time
)
SELECT
  '01900000-0000-7000-8000-00000000151b',
  '系统接口',
  '01900000-0000-7000-8000-000000001518',
  3,
  'swagger',
  'tool/swagger/index',
  '1',
  '0',
  'C',
  '0',
  '0',
  NULL,
  'swagger',
  '0',
  '0',
  '0',
  'admin',
  NOW(),
  '系统接口',
  NULL,
  NULL,
  NULL,
  NULL
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-00000000151b');
/

-- 设备前端角色与菜单关系：为 sb_admin 授予系统设置、系统监控、系统工具菜单及按钮（IGNORE 避免重复执行时报主键冲突）
INSERT IGNORE INTO sb_role_menu (role_id, menu_id)
SELECT '01900000-0000-7000-8000-000000000001', m.menu_id
FROM sb_menu m
WHERE m.menu_id >= '01900000-0000-7000-8000-000000001450' AND m.menu_id <= '01900000-0000-7000-8000-00000000176f'
  AND NOT EXISTS (
    SELECT 1 FROM sb_role_menu rm
    WHERE rm.role_id = '01900000-0000-7000-8000-000000000001' AND rm.menu_id = m.menu_id
  );
/

-- 将 admin 用户绑定为设备管理员（假定 admin 用户ID为 1）
INSERT INTO sb_user_role (user_id, role_id)
SELECT 1, '01900000-0000-7000-8000-000000000001'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_user_role WHERE user_id = 1 AND role_id = '01900000-0000-7000-8000-000000000001');
/

-- 1. 客户菜单功能管理（菜单）
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, is_platform_only, default_open_to_customer, del_flag, create_by, create_time, remark, update_by, update_time, delete_by, delete_time
) VALUES (
  '01900000-0000-7000-8000-000000001457',
  '客户菜单功能管理',
  '01900000-0000-7000-8000-000000001450',
  7,
  'customerMenuManage',
  'system/customerMenuManage/index',
  '1',
  '0',
  'C',
  '0',
  '0',
  'sb:system:customerMenuManage:list',
  'switch',
  '0',
  '0',
  '0',
  'admin',
  NOW(),
  '客户名下已具备功能的启用停用，租户不可见',
  NULL,
  NULL,
  NULL,
  NULL
) ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), path = VALUES(path), component = VALUES(component), remark = VALUES(remark);
/

-- 2. 按钮：功能管理列表
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, is_platform_only, default_open_to_customer, del_flag, create_by, create_time, remark, update_by, update_time, delete_by, delete_time
) VALUES (
  '01900000-0000-7000-8000-00000000cb5e',
  '功能管理列表',
  '01900000-0000-7000-8000-000000001457',
  1,
  '#',
  NULL,
  '1',
  '0',
  'F',
  '0',
  '0',
  'sb:system:customerMenuManage:list',
  '#',
  '0',
  '0',
  '0',
  'admin',
  NOW(),
  '客户菜单功能管理列表',
  NULL,
  NULL,
  NULL,
  NULL
) ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), remark = VALUES(remark);
/
-- 3. 按钮：功能管理查询
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, is_platform_only, default_open_to_customer, del_flag, create_by, create_time, remark, update_by, update_time, delete_by, delete_time
) VALUES (
  '01900000-0000-7000-8000-00000000cb5f',
  '功能管理查询',
  '01900000-0000-7000-8000-000000001457',
  2,
  '#',
  NULL,
  '1',
  '0',
  'F',
  '0',
  '0',
  'sb:system:customerMenuManage:query',
  '#',
  '0',
  '0',
  '0',
  'admin',
  NOW(),
  '启停用记录与时间段查询',
  NULL,
  NULL,
  NULL,
  NULL
) ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), remark = VALUES(remark);
/
-- 4. 按钮：功能管理启停用
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, is_platform_only, default_open_to_customer, del_flag, create_by, create_time, remark, update_by, update_time, delete_by, delete_time
) VALUES (
  '01900000-0000-7000-8000-00000000cb60',
  '功能管理启停用',
  '01900000-0000-7000-8000-000000001457',
  3,
  '#',
  NULL,
  '1',
  '0',
  'F',
  '0',
  '0',
  'sb:system:customerMenuManage:edit',
  '#',
  '0',
  '0',
  '0',
  'admin',
  NOW(),
  '客户菜单功能启用停用',
  NULL,
  NULL,
  NULL,
  NULL
) ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name), remark = VALUES(remark);
/

-- 将客户管理、客户菜单功能管理及其按钮设为仅平台管理（客户分配/工作组/用户权限中不展示）
UPDATE sb_menu SET is_platform_only = '1' WHERE menu_id IN (
  '01900000-0000-7000-8000-000000001456',
  '01900000-0000-7000-8000-00000000cb5a','01900000-0000-7000-8000-00000000cb5b','01900000-0000-7000-8000-00000000cb5c','01900000-0000-7000-8000-00000000cb5d',
  '01900000-0000-7000-8000-000000001457',
  '01900000-0000-7000-8000-00000000cb5e','01900000-0000-7000-8000-00000000cb5f','01900000-0000-7000-8000-00000000cb60'
);
/

-- 设备菜单图标说明（图标对应 spd-sb/src/assets/icons/svg/*.svg，按钮 F 类型通常为 # 不显示）
-- 根目录：设备管理=equipment，基础资料=component，系统设置=system
-- 设备管理下：设备信息=list，资产台账=money，设备巡检=eye，设备维修=tool，设备保养=maintain，设备入库=storage，设备归还=return
-- 基础资料下：设备字典维护=dict，检验平台设置=component，厂家维护=factory，仓库维护=warehouse，科室维护=tree，设备品牌=star，生产厂家=build，供应商=shopping，资产分类=tree，计量分类=tree-table
-- 系统设置下：用户管理=user，角色管理=peoples，菜单管理=tree，部门管理=tree，岗位管理=post，客户管理=peoples，客户菜单功能管理=switch
/
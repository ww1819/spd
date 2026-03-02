-- 期初库存导入菜单：若不存在则插入，存在则跳过（项目启动时执行）
/
INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
)
SELECT
  (SELECT IFNULL(MAX(menu_id), 0) + 1 FROM (SELECT menu_id FROM sys_menu) AS t),
  '期初库存导入',
  (SELECT parent_id FROM sys_menu WHERE menu_id = 1552 LIMIT 1),
  15,
  'initialStockImport',
  'warehouse/initialStockImport/index',
  1, 0, 'C', '0', '0',
  'warehouse:initialStockImport:list',
  'upload',
  'admin', NOW(), '期初库存导入'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_name = '期初库存导入' AND parent_id = (SELECT parent_id FROM sys_menu WHERE menu_id = 1552 LIMIT 1));
/
INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
)
SELECT
  (SELECT IFNULL(MAX(menu_id), 0) + 1 FROM (SELECT menu_id FROM sys_menu) AS t),
  '期初库存导入查询',
  (SELECT menu_id FROM sys_menu WHERE menu_name = '期初库存导入' AND parent_id = (SELECT parent_id FROM sys_menu WHERE menu_id = 1552 LIMIT 1) LIMIT 1),
  1, '#', '', 1, 0, 'F', '0', '0',
  'warehouse:initialStockImport:query',
  '#', 'admin', NOW(), ''
FROM DUAL
WHERE EXISTS (SELECT 1 FROM sys_menu WHERE menu_name = '期初库存导入' AND parent_id = (SELECT parent_id FROM sys_menu WHERE menu_id = 1552 LIMIT 1))
  AND NOT EXISTS (SELECT 1 FROM sys_menu m2 WHERE m2.parent_id = (SELECT menu_id FROM sys_menu WHERE menu_name = '期初库存导入' AND parent_id = (SELECT parent_id FROM sys_menu WHERE menu_id = 1552 LIMIT 1) LIMIT 1) AND m2.perms = 'warehouse:initialStockImport:query');
/
INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
)
SELECT
  (SELECT IFNULL(MAX(menu_id), 0) + 1 FROM (SELECT menu_id FROM sys_menu) AS t),
  '期初库存导入',
  (SELECT menu_id FROM sys_menu WHERE menu_name = '期初库存导入' AND parent_id = (SELECT parent_id FROM sys_menu WHERE menu_id = 1552 LIMIT 1) LIMIT 1),
  2, '#', '', 1, 0, 'F', '0', '0',
  'warehouse:initialStockImport:import',
  '#', 'admin', NOW(), ''
FROM DUAL
WHERE EXISTS (SELECT 1 FROM sys_menu WHERE menu_name = '期初库存导入' AND parent_id = (SELECT parent_id FROM sys_menu WHERE menu_id = 1552 LIMIT 1))
  AND NOT EXISTS (SELECT 1 FROM sys_menu m2 WHERE m2.parent_id = (SELECT menu_id FROM sys_menu WHERE menu_name = '期初库存导入' AND parent_id = (SELECT parent_id FROM sys_menu WHERE menu_id = 1552 LIMIT 1) LIMIT 1) AND m2.perms = 'warehouse:initialStockImport:import');
/
INSERT INTO sys_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
)
SELECT
  (SELECT IFNULL(MAX(menu_id), 0) + 1 FROM (SELECT menu_id FROM sys_menu) AS t),
  '期初库存导入审核',
  (SELECT menu_id FROM sys_menu WHERE menu_name = '期初库存导入' AND parent_id = (SELECT parent_id FROM sys_menu WHERE menu_id = 1552 LIMIT 1) LIMIT 1),
  3, '#', '', 1, 0, 'F', '0', '0',
  'warehouse:initialStockImport:audit',
  '#', 'admin', NOW(), ''
FROM DUAL
WHERE EXISTS (SELECT 1 FROM sys_menu WHERE menu_name = '期初库存导入' AND parent_id = (SELECT parent_id FROM sys_menu WHERE menu_id = 1552 LIMIT 1))
  AND NOT EXISTS (SELECT 1 FROM sys_menu m2 WHERE m2.parent_id = (SELECT menu_id FROM sys_menu WHERE menu_name = '期初库存导入' AND parent_id = (SELECT parent_id FROM sys_menu WHERE menu_id = 1552 LIMIT 1) LIMIT 1) AND m2.perms = 'warehouse:initialStockImport:audit');
/

-- 设备前端 sb_* 表初始化菜单与权限数据
/
INSERT INTO sb_role (
  role_id, role_name, role_key, role_sort, data_scope, menu_check_strictly, dept_check_strictly, status, del_flag, create_by, create_time, remark
)
SELECT
  1,
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
WHERE NOT EXISTS (SELECT 1 FROM sb_role WHERE role_id = 1);
/

-- 设备前端菜单：根目录「设备管理」
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
)
SELECT
  5000,
  '设备管理',
  0,
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
  'admin',
  NOW(),
  '设备管理根菜单'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = 5000);
/

-- 设备前端菜单：根目录「基础资料」
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
)
SELECT
  5100,
  '基础资料',
  0,
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
  'admin',
  NOW(),
  '设备前端基础资料根菜单'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = 5100);
/

-- 设备管理子菜单：设备信息
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
)
SELECT
  5001,
  '设备信息',
  5000,
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
  'admin',
  NOW(),
  '设备信息维护'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = 5001);
/

-- 设备管理子菜单：设备巡检
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
)
SELECT
  5002,
  '设备巡检',
  5000,
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
  'admin',
  NOW(),
  '设备巡检管理'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = 5002);
/

-- 设备管理子菜单：设备维修
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
)
SELECT
  5003,
  '设备维修',
  5000,
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
  'admin',
  NOW(),
  '设备维修管理'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = 5003);
/

-- 设备管理子菜单：设备保养
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
)
SELECT
  5004,
  '设备保养',
  5000,
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
  'admin',
  NOW(),
  '设备保养管理'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = 5004);
/

-- 设备管理子菜单：设备入库
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
)
SELECT
  5005,
  '设备入库',
  5000,
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
  'admin',
  NOW(),
  '设备入库管理'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = 5005);
/

-- 设备管理子菜单：设备出库（领用/归还等可以再细分，这里只保留主要菜单）
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
)
SELECT
  5006,
  '设备归还',
  5000,
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
  'admin',
  NOW(),
  '设备归还管理'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = 5006);
/

-- 基础资料子菜单：设备字典维护
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
)
SELECT
  5101,
  '设备字典维护',
  5100,
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
  'admin',
  NOW(),
  '设备字典维护'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = 5101);
/

-- 设备字典维护 按钮：新增/修改/删除/导出/导入
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
)
SELECT
  51011,
  '设备字典新增',
  5101,
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
  'admin',
  NOW(),
  '设备字典新增按钮'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = 51011);
/

INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
)
SELECT
  51012,
  '设备字典修改',
  5101,
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
  'admin',
  NOW(),
  '设备字典修改按钮'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = 51012);
/

INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
)
SELECT
  51013,
  '设备字典删除',
  5101,
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
  'admin',
  NOW(),
  '设备字典删除按钮'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = 51013);
/

INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
)
SELECT
  51014,
  '设备字典导出',
  5101,
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
  'admin',
  NOW(),
  '设备字典导出按钮'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = 51014);
/

INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
)
SELECT
  51015,
  '设备字典导入',
  5101,
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
  'admin',
  NOW(),
  '设备字典导入按钮'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = 51015);
/

-- 基础资料子菜单：检验平台设置
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
)
SELECT
  5102,
  '检验平台设置',
  5100,
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
  'admin',
  NOW(),
  '检验平台设置'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = 5102);
/

-- 基础资料子菜单：厂家维护
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
)
SELECT
  5103,
  '厂家维护',
  5100,
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
  'admin',
  NOW(),
  '厂家维护'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = 5103);
/

-- 厂家维护 按钮：新增/修改/删除/导出
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
)
SELECT
  51031,
  '厂家新增',
  5103,
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
  'admin',
  NOW(),
  '厂家新增按钮'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = 51031);
/

INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
)
SELECT
  51032,
  '厂家修改',
  5103,
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
  'admin',
  NOW(),
  '厂家修改按钮'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = 51032);
/

INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
)
SELECT
  51033,
  '厂家删除',
  5103,
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
  'admin',
  NOW(),
  '厂家删除按钮'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = 51033);
/

INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
)
SELECT
  51034,
  '厂家导出',
  5103,
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
  'admin',
  NOW(),
  '厂家导出按钮'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = 51034);
/

-- 基础资料子菜单：仓库维护
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
)
SELECT
  5104,
  '仓库维护',
  5100,
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
  'admin',
  NOW(),
  '仓库维护'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = 5104);
/

-- 仓库维护 按钮：新增/修改/删除/导出
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
)
SELECT
  51041,
  '仓库新增',
  5104,
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
  'admin',
  NOW(),
  '仓库新增按钮'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = 51041);
/

INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
)
SELECT
  51042,
  '仓库修改',
  5104,
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
  'admin',
  NOW(),
  '仓库修改按钮'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = 51042);
/

INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
)
SELECT
  51043,
  '仓库删除',
  5104,
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
  'admin',
  NOW(),
  '仓库删除按钮'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = 51043);
/

INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
)
SELECT
  51044,
  '仓库导出',
  5104,
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
  'admin',
  NOW(),
  '仓库导出按钮'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = 51044);
/

-- 基础资料子菜单：科室维护
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
)
SELECT
  5105,
  '科室维护',
  5100,
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
  'admin',
  NOW(),
  '科室维护'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = 5105);
/

-- 科室维护 按钮：新增/修改/删除/导出
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
)
SELECT
  51051,
  '科室新增',
  5105,
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
  'admin',
  NOW(),
  '科室新增按钮'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = 51051);
/

INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
)
SELECT
  51052,
  '科室修改',
  5105,
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
  'admin',
  NOW(),
  '科室修改按钮'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = 51052);
/

INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
)
SELECT
  51053,
  '科室删除',
  5105,
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
  'admin',
  NOW(),
  '科室删除按钮'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = 51053);
/

INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
)
SELECT
  51054,
  '科室导出',
  5105,
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
  'admin',
  NOW(),
  '科室导出按钮'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = 51054);
/

-- 基础资料子菜单：供应商维护
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
)
SELECT
  5106,
  '供应商维护',
  5100,
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
  'admin',
  NOW(),
  '供应商维护'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = 5106);
/

-- 供应商维护 按钮：新增/修改/删除/导出
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
)
SELECT
  51061,
  '供应商新增',
  5106,
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
  'admin',
  NOW(),
  '供应商新增按钮'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = 51061);
/

INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
)
SELECT
  51062,
  '供应商修改',
  5106,
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
  'admin',
  NOW(),
  '供应商修改按钮'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = 51062);
/

INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
)
SELECT
  51063,
  '供应商删除',
  5106,
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
  'admin',
  NOW(),
  '供应商删除按钮'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = 51063);
/

INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
)
SELECT
  51064,
  '供应商导出',
  5106,
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
  'admin',
  NOW(),
  '供应商导出按钮'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = 51064);
/

-- 设备前端角色与菜单关系：为 sb_admin 授予设备业务与基础资料菜单及按钮
INSERT INTO sb_role_menu (role_id, menu_id)
SELECT 1, m.menu_id
FROM sb_menu m
WHERE m.menu_id BETWEEN 5000 AND 5999
  AND NOT EXISTS (
    SELECT 1 FROM sb_role_menu rm
    WHERE rm.role_id = 1 AND rm.menu_id = m.menu_id
  );
/

-- 系统设置根目录
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
)
SELECT
  5200,
  '系统设置',
  0,
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
  'admin',
  NOW(),
  '系统管理根菜单'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = 5200);
/

-- 系统设置子菜单：用户管理
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
)
SELECT
  5201,
  '用户管理',
  5200,
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
  'admin',
  NOW(),
  '用户管理'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = 5201);
/

-- 系统设置子菜单：角色管理
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
)
SELECT
  5202,
  '角色管理',
  5200,
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
  'admin',
  NOW(),
  '角色管理'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = 5202);
/

-- 系统设置-角色管理 按钮：新增/修改/删除/导出
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
)
SELECT
  52021,
  '角色新增',
  5202,
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
  'admin',
  NOW(),
  '角色新增按钮'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = 52021);
/

INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
)
SELECT
  52022,
  '角色修改',
  5202,
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
  'admin',
  NOW(),
  '角色修改按钮'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = 52022);
/

INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
)
SELECT
  52023,
  '角色删除',
  5202,
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
  'admin',
  NOW(),
  '角色删除按钮'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = 52023);
/

INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
)
SELECT
  52024,
  '角色导出',
  5202,
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
  'admin',
  NOW(),
  '角色导出按钮'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = 52024);
/

-- 系统设置子菜单：菜单管理
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
)
SELECT
  5203,
  '菜单管理',
  5200,
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
  'admin',
  NOW(),
  '菜单管理'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = 5203);
/

-- 系统设置-菜单管理 按钮：新增/修改/删除
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
)
SELECT
  52031,
  '菜单新增',
  5203,
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
  'admin',
  NOW(),
  '菜单新增按钮'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = 52031);
/

INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
)
SELECT
  52032,
  '菜单修改',
  5203,
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
  'admin',
  NOW(),
  '菜单修改按钮'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = 52032);
/

INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
)
SELECT
  52033,
  '菜单删除',
  5203,
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
  'admin',
  NOW(),
  '菜单删除按钮'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = 52033);
/

-- 系统设置子菜单：部门管理
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
)
SELECT
  5204,
  '部门管理',
  5200,
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
  'admin',
  NOW(),
  '部门管理'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = 5204);
/

-- 系统设置子菜单：岗位管理
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
)
SELECT
  5205,
  '岗位管理',
  5200,
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
  'admin',
  NOW(),
  '岗位管理'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = 5205);
/

-- 系统设置子菜单：字典管理
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
)
SELECT
  5206,
  '字典管理',
  5200,
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
  'admin',
  NOW(),
  '字典管理'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = 5206);
/

-- 系统设置子菜单：参数设置
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
)
SELECT
  5207,
  '参数设置',
  5200,
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
  'admin',
  NOW(),
  '参数设置'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = 5207);
/

-- 系统设置子菜单：通知公告
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
)
SELECT
  5208,
  '通知公告',
  5200,
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
  'admin',
  NOW(),
  '通知公告'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = 5208);
/

-- 系统设置子菜单：部门角色管理
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
)
SELECT
  5209,
  '部门角色管理',
  5200,
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
  'admin',
  NOW(),
  '部门角色管理'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = 5209);
/

-- 系统设置子菜单：库房角色管理
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
)
SELECT
  5210,
  '库房角色管理',
  5200,
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
  'admin',
  NOW(),
  '库房角色管理'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = 5210);
/

-- 系统监控根目录
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
)
SELECT
  5300,
  '系统监控',
  0,
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
  'admin',
  NOW(),
  '系统监控根菜单'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = 5300);
/

-- 系统监控子菜单：在线用户
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
)
SELECT
  5301,
  '在线用户',
  5300,
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
  'admin',
  NOW(),
  '在线用户'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = 5301);
/

-- 系统监控子菜单：定时任务
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
)
SELECT
  5302,
  '定时任务',
  5300,
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
  'admin',
  NOW(),
  '定时任务'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = 5302);
/

-- 系统监控子菜单：调度日志入口（列表页）
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
)
SELECT
  5303,
  '调度日志',
  5300,
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
  'admin',
  NOW(),
  '调度日志'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = 5303);
/

-- 系统监控子菜单：操作日志
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
)
SELECT
  5304,
  '操作日志',
  5300,
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
  'admin',
  NOW(),
  '操作日志'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = 5304);
/

-- 系统监控子菜单：登录日志
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
)
SELECT
  5305,
  '登录日志',
  5300,
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
  'admin',
  NOW(),
  '登录日志'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = 5305);
/

-- 系统监控子菜单：服务监控
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
)
SELECT
  5306,
  '服务监控',
  5300,
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
  'admin',
  NOW(),
  '服务监控'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = 5306);
/

-- 系统监控子菜单：缓存监控
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
)
SELECT
  5307,
  '缓存监控',
  5300,
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
  'admin',
  NOW(),
  '缓存监控'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = 5307);
/

-- 系统监控子菜单：缓存列表
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
)
SELECT
  5308,
  '缓存列表',
  5300,
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
  'admin',
  NOW(),
  '缓存列表'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = 5308);
/

-- 系统监控子菜单：数据监控（druid）
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
)
SELECT
  5309,
  '数据监控',
  5300,
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
  'admin',
  NOW(),
  '数据监控'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = 5309);
/

-- 系统工具根目录
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
)
SELECT
  5400,
  '系统工具',
  0,
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
  'admin',
  NOW(),
  '系统工具根菜单'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = 5400);
/

-- 系统工具子菜单：代码生成
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
)
SELECT
  5401,
  '代码生成',
  5400,
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
  'admin',
  NOW(),
  '代码生成'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = 5401);
/

-- 系统工具子菜单：表单构建
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
)
SELECT
  5402,
  '表单构建',
  5400,
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
  'admin',
  NOW(),
  '表单构建'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = 5402);
/

-- 系统工具子菜单：系统接口（Swagger）
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
)
SELECT
  5403,
  '系统接口',
  5400,
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
  'admin',
  NOW(),
  '系统接口'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = 5403);
/

-- 设备前端角色与菜单关系：为 sb_admin 授予系统设置、系统监控、系统工具菜单及按钮
INSERT INTO sb_role_menu (role_id, menu_id)
SELECT 1, m.menu_id
FROM sb_menu m
WHERE m.menu_id BETWEEN 5200 AND 5999
  AND NOT EXISTS (
    SELECT 1 FROM sb_role_menu rm
    WHERE rm.role_id = 1 AND rm.menu_id = m.menu_id
  );
/

-- 将 admin 用户绑定为设备管理员（假定 admin 用户ID为 1）
INSERT INTO sb_user_role (user_id, role_id)
SELECT 1, 1
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_user_role WHERE user_id = 1 AND role_id = 1);
/

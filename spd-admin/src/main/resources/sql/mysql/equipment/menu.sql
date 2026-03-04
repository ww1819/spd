
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
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
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
  'admin',
  NOW(),
  '设备管理根菜单'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-000000001388');
/

-- 设备前端菜单：根目录「基础资料」
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
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
  'admin',
  NOW(),
  '设备前端基础资料根菜单'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-0000000013ec');
/

-- 设备管理子菜单：设备信息
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
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
  'admin',
  NOW(),
  '设备信息维护'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-000000001389');
/

-- 设备管理子菜单：设备巡检
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
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
  'admin',
  NOW(),
  '设备巡检管理'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-00000000138a');
/

-- 设备管理子菜单：设备维修
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
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
  'admin',
  NOW(),
  '设备维修管理'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-00000000138b');
/

-- 设备管理子菜单：设备保养
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
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
  'admin',
  NOW(),
  '设备保养管理'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-00000000138c');
/

-- 设备管理子菜单：设备入库
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
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
  'admin',
  NOW(),
  '设备入库管理'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-00000000138d');
/

-- 设备管理子菜单：设备出库（领用/归还等可以再细分，这里只保留主要菜单）
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
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
  'admin',
  NOW(),
  '设备归还管理'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-00000000138e');
/

-- 基础资料子菜单：设备字典维护
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
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
  'admin',
  NOW(),
  '设备字典维护'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-0000000013ed');
/

-- 设备字典维护 按钮：新增/修改/删除/导出/导入
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
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
  'admin',
  NOW(),
  '设备字典新增按钮'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-00000000c743');
/

INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
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
  'admin',
  NOW(),
  '设备字典修改按钮'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-00000000c744');
/

INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
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
  'admin',
  NOW(),
  '设备字典删除按钮'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-00000000c745');
/

INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
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
  'admin',
  NOW(),
  '设备字典导出按钮'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-00000000c746');
/

INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
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
  'admin',
  NOW(),
  '设备字典导入按钮'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-00000000c747');
/

-- 基础资料子菜单：检验平台设置
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
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
  'admin',
  NOW(),
  '检验平台设置'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-0000000013ee');
/

-- 基础资料子菜单：厂家维护
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
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
  'admin',
  NOW(),
  '厂家维护'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-0000000013ef');
/

-- 厂家维护 按钮：新增/修改/删除/导出
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
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
  'admin',
  NOW(),
  '厂家新增按钮'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-00000000c757');
/

INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
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
  'admin',
  NOW(),
  '厂家修改按钮'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-00000000c758');
/

INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
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
  'admin',
  NOW(),
  '厂家删除按钮'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-00000000c759');
/

INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
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
  'admin',
  NOW(),
  '厂家导出按钮'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-00000000c75a');
/

-- 基础资料子菜单：仓库维护
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
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
  'admin',
  NOW(),
  '仓库维护'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-0000000013f0');
/

-- 仓库维护 按钮：新增/修改/删除/导出
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
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
  'admin',
  NOW(),
  '仓库新增按钮'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-00000000c761');
/

INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
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
  'admin',
  NOW(),
  '仓库修改按钮'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-00000000c762');
/

INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
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
  'admin',
  NOW(),
  '仓库删除按钮'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-00000000c763');
/

INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
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
  'admin',
  NOW(),
  '仓库导出按钮'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-00000000c764');
/

-- 基础资料子菜单：科室维护
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
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
  'admin',
  NOW(),
  '科室维护'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-0000000013f1');
/

-- 科室维护 按钮：新增/修改/删除/导出
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
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
  'admin',
  NOW(),
  '科室新增按钮'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-00000000c76b');
/

INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
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
  'admin',
  NOW(),
  '科室修改按钮'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-00000000c76c');
/

INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
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
  'admin',
  NOW(),
  '科室删除按钮'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-00000000c76d');
/

INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
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
  'admin',
  NOW(),
  '科室导出按钮'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-00000000c76e');
/

-- 基础资料子菜单：供应商维护
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
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
  'admin',
  NOW(),
  '供应商维护'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-0000000013f2');
/

-- 供应商维护 按钮：新增/修改/删除/导出
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
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
  'admin',
  NOW(),
  '供应商新增按钮'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-00000000c775');
/

INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
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
  'admin',
  NOW(),
  '供应商修改按钮'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-00000000c776');
/

INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
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
  'admin',
  NOW(),
  '供应商删除按钮'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-00000000c777');
/

INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
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
  'admin',
  NOW(),
  '供应商导出按钮'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-00000000c778');
/

-- 设备前端角色与菜单关系：为 sb_admin 授予设备业务与基础资料菜单及按钮
INSERT INTO sb_role_menu (role_id, menu_id)
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
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
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
  'admin',
  NOW(),
  '系统管理根菜单'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-000000001450');
/

-- 系统设置子菜单：用户管理
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
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
  'admin',
  NOW(),
  '用户管理'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-000000001451');
/

-- 系统设置子菜单：角色管理
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
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
  'admin',
  NOW(),
  '角色管理'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-000000001452');
/

-- 系统设置-角色管理 按钮：新增/修改/删除/导出
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
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
  'admin',
  NOW(),
  '角色新增按钮'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-00000000cb35');
/

INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
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
  'admin',
  NOW(),
  '角色修改按钮'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-00000000cb36');
/

INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
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
  'admin',
  NOW(),
  '角色删除按钮'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-00000000cb37');
/

INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
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
  'admin',
  NOW(),
  '角色导出按钮'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-00000000cb38');
/

-- 系统设置子菜单：菜单管理
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
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
  'admin',
  NOW(),
  '菜单管理'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-000000001453');
/

-- 系统设置-菜单管理 按钮：新增/修改/删除
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
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
  'admin',
  NOW(),
  '菜单新增按钮'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-00000000cb3f');
/

INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
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
  'admin',
  NOW(),
  '菜单修改按钮'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-00000000cb40');
/

INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
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
  'admin',
  NOW(),
  '菜单删除按钮'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-00000000cb41');
/

-- 系统设置子菜单：部门管理
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
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
  'admin',
  NOW(),
  '部门管理'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-000000001454');
/

-- 系统设置子菜单：岗位管理
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
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
  'admin',
  NOW(),
  '岗位管理'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-000000001455');
/

-- 系统设置子菜单：客户管理（SaaS租户）
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
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
  'admin',
  NOW(),
  '设备系统客户（租户）管理'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-000000001456');
/

-- 系统设置-客户管理 按钮：查询/新增/修改/删除
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
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
  'admin',
  NOW(),
  '客户查询按钮'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-00000000cb5a');
/

INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
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
  'admin',
  NOW(),
  '客户新增按钮'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-00000000cb5b');
/

INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
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
  'admin',
  NOW(),
  '客户修改及菜单权限按钮'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-00000000cb5c');
/

INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
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
  'admin',
  NOW(),
  '客户删除按钮'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-00000000cb5d');
/

-- 系统设置子菜单：字典管理
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
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
  'admin',
  NOW(),
  '字典管理'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-000000001456');
/

-- 系统设置子菜单：参数设置
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
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
  'admin',
  NOW(),
  '参数设置'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-000000001457');
/

-- 系统设置子菜单：通知公告
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
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
  'admin',
  NOW(),
  '通知公告'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-000000001458');
/

-- 系统设置子菜单：部门角色管理
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
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
  'admin',
  NOW(),
  '部门角色管理'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-000000001459');
/

-- 系统设置子菜单：库房角色管理
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
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
  'admin',
  NOW(),
  '库房角色管理'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-00000000145a');
/

-- 系统监控根目录
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
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
  'admin',
  NOW(),
  '系统监控根菜单'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-0000000014b4');
/

-- 系统监控子菜单：在线用户
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
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
  'admin',
  NOW(),
  '在线用户'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-0000000014b5');
/

-- 系统监控子菜单：定时任务
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
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
  'admin',
  NOW(),
  '定时任务'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-0000000014b6');
/

-- 系统监控子菜单：调度日志入口（列表页）
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
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
  'admin',
  NOW(),
  '调度日志'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-0000000014b7');
/

-- 系统监控子菜单：操作日志
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
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
  'admin',
  NOW(),
  '操作日志'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-0000000014b8');
/

-- 系统监控子菜单：登录日志
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
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
  'admin',
  NOW(),
  '登录日志'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-0000000014b9');
/

-- 系统监控子菜单：服务监控
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
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
  'admin',
  NOW(),
  '服务监控'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-0000000014ba');
/

-- 系统监控子菜单：缓存监控
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
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
  'admin',
  NOW(),
  '缓存监控'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-0000000014bb');
/

-- 系统监控子菜单：缓存列表
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
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
  'admin',
  NOW(),
  '缓存列表'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-0000000014bc');
/

-- 系统监控子菜单：数据监控（druid）
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
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
  'admin',
  NOW(),
  '数据监控'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-0000000014bd');
/

-- 系统工具根目录
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
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
  'admin',
  NOW(),
  '系统工具根菜单'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-000000001518');
/

-- 系统工具子菜单：代码生成
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
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
  'admin',
  NOW(),
  '代码生成'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-000000001519');
/

-- 系统工具子菜单：表单构建
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
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
  'admin',
  NOW(),
  '表单构建'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-00000000151a');
/

-- 系统工具子菜单：系统接口（Swagger）
INSERT INTO sb_menu (
  menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark
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
  'admin',
  NOW(),
  '系统接口'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sb_menu WHERE menu_id = '01900000-0000-7000-8000-00000000151b');
/

-- 设备前端角色与菜单关系：为 sb_admin 授予系统设置、系统监控、系统工具菜单及按钮
INSERT INTO sb_role_menu (role_id, menu_id)
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

-- ============================================
-- 科室盘点申请功能 - 菜单配置
-- 创建时间：2025-01-28
-- ============================================

-- ============================================
-- 一、更新盘点申请菜单配置（menu_id=1561）
-- ============================================
UPDATE sys_menu 
SET 
    path = 'deptStocktaking',
    component = 'department/stocktaking/index',
    menu_type = 'C',
    perms = 'department:stocktaking:list',
    icon = 'time-range',
    is_frame = '1',
    is_cache = '0',
    visible = '0',
    status = '0',
    update_time = NOW()
WHERE menu_id = 1561;

-- ============================================
-- 二、插入按钮权限
-- ============================================

-- 2.1 查询按钮权限
INSERT INTO sys_menu (
    menu_id, 
    menu_name, 
    parent_id, 
    order_num, 
    path, 
    component, 
    is_frame, 
    is_cache, 
    menu_type, 
    visible, 
    status, 
    perms, 
    icon, 
    create_by, 
    create_time, 
    update_by, 
    update_time, 
    remark
) 
SELECT 
    (SELECT IFNULL(MAX(menu_id), 0) + 1 FROM (SELECT menu_id FROM sys_menu) AS temp),
    '科室盘点申请查询',
    menu_id,
    1,
    '#',
    '',
    1,
    0,
    'F',
    '0',
    '0',
    'department:stocktaking:list',
    '#',
    'admin',
    NOW(),
    '',
    NULL,
    ''
FROM sys_menu WHERE menu_id = 1561
UNION ALL
SELECT 
    (SELECT IFNULL(MAX(menu_id), 0) + 2 FROM (SELECT menu_id FROM sys_menu) AS temp),
    '科室盘点申请新增',
    menu_id,
    2,
    '#',
    '',
    1,
    0,
    'F',
    '0',
    '0',
    'department:stocktaking:add',
    '#',
    'admin',
    NOW(),
    '',
    NULL,
    ''
FROM sys_menu WHERE menu_id = 1561
UNION ALL
SELECT 
    (SELECT IFNULL(MAX(menu_id), 0) + 3 FROM (SELECT menu_id FROM sys_menu) AS temp),
    '科室盘点申请修改',
    menu_id,
    3,
    '#',
    '',
    1,
    0,
    'F',
    '0',
    '0',
    'department:stocktaking:edit',
    '#',
    'admin',
    NOW(),
    '',
    NULL,
    ''
FROM sys_menu WHERE menu_id = 1561
UNION ALL
SELECT 
    (SELECT IFNULL(MAX(menu_id), 0) + 4 FROM (SELECT menu_id FROM sys_menu) AS temp),
    '科室盘点申请删除',
    menu_id,
    4,
    '#',
    '',
    1,
    0,
    'F',
    '0',
    '0',
    'department:stocktaking:remove',
    '#',
    'admin',
    NOW(),
    '',
    NULL,
    ''
FROM sys_menu WHERE menu_id = 1561
UNION ALL
SELECT 
    (SELECT IFNULL(MAX(menu_id), 0) + 5 FROM (SELECT menu_id FROM sys_menu) AS temp),
    '科室盘点申请导出',
    menu_id,
    5,
    '#',
    '',
    1,
    0,
    'F',
    '0',
    '0',
    'department:stocktaking:export',
    '#',
    'admin',
    NOW(),
    '',
    NULL,
    ''
FROM sys_menu WHERE menu_id = 1561
UNION ALL
SELECT 
    (SELECT IFNULL(MAX(menu_id), 0) + 6 FROM (SELECT menu_id FROM sys_menu) AS temp),
    '科室盘点申请审核',
    menu_id,
    6,
    '#',
    '',
    1,
    0,
    'F',
    '0',
    '0',
    'department:stocktaking:audit',
    '#',
    'admin',
    NOW(),
    '',
    NULL,
    ''
FROM sys_menu WHERE menu_id = 1561;

-- ============================================
-- 三、验证菜单配置
-- ============================================
SELECT 
    menu_id,
    menu_name,
    parent_id,
    path,
    component,
    perms,
    icon,
    menu_type,
    order_num
FROM sys_menu 
WHERE menu_id = 1561 OR parent_id = 1561
ORDER BY menu_id, order_num;

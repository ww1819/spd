-- ============================================
-- 修正定数监测菜单配置
-- ============================================

-- 查找"耗材产品维护"菜单ID（作为"耗材维护"的参考）
-- SELECT menu_id, menu_name, parent_id, path, component FROM sys_menu WHERE menu_name LIKE '%耗材%' AND menu_type = 'C';

-- 查找现有的定数监测菜单配置
SELECT 
    menu_id AS '菜单ID',
    menu_name AS '菜单名称',
    parent_id AS '父菜单ID',
    order_num AS '显示排序',
    path AS '路由地址',
    component AS '组件路径',
    perms AS '权限字符',
    icon AS '菜单图标',
    menu_type AS '菜单类型',
    visible AS '显示状态',
    status AS '菜单状态'
FROM sys_menu 
WHERE menu_name = '定数监测'
ORDER BY menu_id;

-- 修正定数监测菜单的路由地址和组件路径
-- 注意：如果parent_id需要改为"耗材维护"的menu_id，请先查询"耗材维护"的menu_id，然后更新parent_id
UPDATE sys_menu 
SET 
    path = 'monitoring/fixedNumber',                      -- 路由地址（修正：从 0 改为 monitoring/fixedNumber）
    component = 'monitoring/fixedNumber/index',           -- 组件路径（修正：从空改为 monitoring/fixedNumber/index）
    is_frame = '0',                                       -- 是否外链：否（确保不是外链）
    is_cache = '0',                                       -- 是否缓存：缓存（可选，根据需求调整）
    update_time = NOW()                                   -- 更新时间
WHERE menu_name = '定数监测' 
  AND menu_type = 'C';                                    -- 只更新菜单类型为C（菜单）的记录

-- 验证更新结果
SELECT 
    menu_id AS '菜单ID',
    menu_name AS '菜单名称',
    parent_id AS '父菜单ID',
    order_num AS '显示排序',
    path AS '路由地址',
    component AS '组件路径',
    perms AS '权限字符',
    icon AS '菜单图标',
    menu_type AS '菜单类型(M目录C菜单F按钮)',
    visible AS '显示状态(0显示1隐藏)',
    status AS '菜单状态(0正常1停用)',
    is_frame AS '是否外链(0否1是)',
    is_cache AS '是否缓存(0缓存1不缓存)',
    update_time AS '更新时间'
FROM sys_menu 
WHERE menu_name = '定数监测'
ORDER BY menu_id;

-- ============================================
-- 说明：
-- 1. 如果"定数监测"需要在"耗材维护"下，请先查询"耗材维护"的menu_id，然后更新parent_id
--    示例：UPDATE sys_menu SET parent_id = [耗材维护的menu_id] WHERE menu_name = '定数监测' AND menu_type = 'C';
-- 2. 路由地址 path 应为：monitoring/fixedNumber
-- 3. 组件路径 component 应为：monitoring/fixedNumber/index
-- 4. 是否外链 is_frame 应为：0（否）
-- 5. 是否缓存 is_cache 根据需要设置：0（缓存）或 1（不缓存）
-- ============================================


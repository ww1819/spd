-- ============================================
-- 修正货位维护菜单配置
-- ============================================

-- 查找现有的货位维护菜单配置
SELECT 
    menu_id AS '菜单ID',
    menu_name AS '菜单名称',
    parent_id AS '父菜单ID',
    order_num AS '显示排序',
    path AS '路由地址',
    component AS '组件路径',
    perms AS '权限字符',
    icon AS '菜单图标',
    is_frame AS '是否外链',
    is_cache AS '是否缓存',
    menu_type AS '菜单类型',
    visible AS '显示状态',
    status AS '菜单状态'
FROM sys_menu 
WHERE menu_name = '货位维护' AND menu_type = 'C';

-- 修正货位维护菜单的路由地址和组件路径
UPDATE sys_menu 
SET 
    path = 'location',                              -- 路由地址（修正：从 0 改为 location）
    component = 'foundation/location/index',        -- 组件路径（修正：从空改为 foundation/location/index）
    is_frame = '0',                                 -- 是否外链：否（确保不是外链）
    is_cache = '0',                                 -- 是否缓存：缓存
    update_time = NOW()                             -- 更新时间
WHERE menu_name = '货位维护' 
  AND menu_type = 'C';                              -- 只更新菜单类型为C（菜单）的记录

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
    is_frame AS '是否外链',
    is_cache AS '是否缓存',
    menu_type AS '菜单类型(M目录C菜单F按钮)',
    visible AS '显示状态(0显示1隐藏)',
    status AS '菜单状态(0正常1停用)',
    update_time AS '更新时间'
FROM sys_menu 
WHERE menu_name = '货位维护' AND menu_type = 'C';

-- ============================================
-- 注意：
-- 1. 如果菜单还没有创建，请先执行 add_location_menu.sql
-- 2. 如果上级菜单名称不是"基础资料"，请先查询正确的上级菜单ID，然后更新parent_id
-- 3. 执行完SQL后，需要在系统管理->菜单管理中刷新菜单列表
-- ============================================


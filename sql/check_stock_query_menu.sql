-- ============================================
-- 备货查询菜单配置检查报告
-- 菜单ID: 1529
-- 检查日期: 2026-01-11
-- ============================================

-- 1. 检查备货查询菜单基本配置
SELECT 
    menu_id AS '菜单ID',
    menu_name AS '菜单名称',
    parent_id AS '父菜单ID',
    order_num AS '显示排序',
    path AS '路由地址',
    component AS '组件路径',
    query AS '路由参数',
    perms AS '权限字符',
    icon AS '菜单图标',
    is_frame AS '是否外链(0是1否)',
    is_cache AS '是否缓存(0缓存1不缓存)',
    menu_type AS '菜单类型(M目录C菜单F按钮)',
    visible AS '显示状态(0显示1隐藏)',
    status AS '菜单状态(0正常1停用)'
FROM sys_menu 
WHERE menu_id = 1529;

-- 2. 检查父菜单配置
SELECT 
    menu_id AS '菜单ID',
    menu_name AS '菜单名称',
    parent_id AS '父菜单ID',
    path AS '路由地址',
    component AS '组件路径'
FROM sys_menu 
WHERE menu_id = 1192;

-- 3. 检查同级别菜单配置（对比）
SELECT 
    menu_id AS '菜单ID',
    menu_name AS '菜单名称',
    parent_id AS '父菜单ID',
    path AS '路由地址',
    component AS '组件路径',
    perms AS '权限字符',
    icon AS '菜单图标',
    order_num AS '显示排序'
FROM sys_menu 
WHERE parent_id = 1192
ORDER BY order_num;

-- 4. 检查是否有导出权限按钮
SELECT 
    menu_id AS '菜单ID',
    menu_name AS '菜单名称',
    parent_id AS '父菜单ID',
    perms AS '权限字符',
    menu_type AS '菜单类型'
FROM sys_menu 
WHERE parent_id = 1529 OR perms LIKE '%stockQuery%'
ORDER BY menu_id;

-- ============================================
-- 检查结果说明：
-- ============================================
-- 1. 菜单基本配置：✓ 正确
--    - path: stockQuery
--    - component: gz/stockQuery/index
--    - perms: gz:stockQuery:list
--    - icon: search
--
-- 2. 组件路径验证：
--    - 数据库配置：gz/stockQuery/index
--    - 实际文件路径：spd-ui/src/views/gz/stockQuery/index.vue
--    - 状态：✓ 匹配
--
-- 3. 权限配置：
--    - 菜单权限：gz:stockQuery:list ✓
--    - 导出权限：gz:stockQuery:export (前端使用，但数据库中没有按钮权限配置)
--    - 建议：如果需要导出功能，需要添加导出权限按钮
--
-- 4. 缓存配置：
--    - 当前设置：is_cache = 0 (不缓存)
--    - 建议：如果页面数据不频繁变化，可设置为 1 (缓存) 提升性能
--
-- 5. 代码问题：
--    - RightToolbar 组件导入缺失（已修复）
-- ============================================

-- 如果需要添加导出权限按钮，执行以下SQL：
/*
INSERT INTO sys_menu (
    menu_id, menu_name, parent_id, order_num, path, component, 
    is_frame, is_cache, menu_type, visible, status, perms, 
    icon, create_by, create_time, remark
) VALUES (
    15291, '备货查询导出', 1529, 1, '#', '', 
    1, 0, 'F', '0', '0', 'gz:stockQuery:export', 
    '#', 'admin', NOW(), '备货查询导出按钮'
);
*/

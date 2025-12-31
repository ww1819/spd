-- 核对盘点申请菜单配置
-- 菜单ID: 1240

-- 查询当前菜单配置
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
    status AS '菜单状态',
    is_frame AS '是否外链',
    is_cache AS '是否缓存',
    query AS '路由参数'
FROM sys_menu 
WHERE menu_id = 1240;

-- 核对配置是否正确
-- 预期配置：
-- 路由地址: profit
-- 组件路径: stocktaking/profit/index
-- 权限字符: stocktaking:profit:list (注意：数据库中使用冒号，前端显示可能使用点号)
-- 菜单图标: international
-- 菜单类型: C (菜单)
-- 显示状态: 0 (显示)
-- 菜单状态: 0 (正常)
-- 是否外链: 1 (否)
-- 是否缓存: 0 (缓存)

-- 如果权限字符显示不一致，可能是前端显示时的转换问题
-- 数据库存储: stocktaking:profit:list (冒号分隔)
-- 前端显示: stocktaking:profit.list (点号分隔，可能是显示时的转换)


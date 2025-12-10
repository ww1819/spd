-- 更新"备货查询"菜单配置
-- 菜单ID: 1529
-- 父菜单ID: 1192 (备货管理)

-- 更新菜单配置
UPDATE sys_menu 
SET 
    path = 'stockQuery',                    -- 路由地址：stockQuery（相对路径）
    component = 'gz/stockQuery/index',       -- 组件路径：gz/stockQuery/index
    query = NULL,                            -- 路由参数：清空（不需要路由参数）
    perms = 'gz:stockQuery:list',            -- 权限字符：gz:stockQuery:list
    icon = 'search',                         -- 菜单图标：search（从radio改为search）
    order_num = 7,                           -- 显示排序：7
    is_frame = '1',                          -- 是否外链：否
    is_cache = '0',                          -- 是否缓存：缓存
    menu_type = 'C',                         -- 菜单类型：菜单
    visible = '0',                           -- 显示状态：显示
    status = '0',                            -- 菜单状态：正常
    update_time = NOW()                      -- 更新时间
WHERE menu_id = 1529;

-- 验证更新结果
SELECT 
    menu_id,
    menu_name,
    parent_id,
    order_num,
    path,
    component,
    query,
    perms,
    icon,
    is_frame,
    is_cache,
    menu_type,
    visible,
    status
FROM sys_menu 
WHERE menu_id = 1529;


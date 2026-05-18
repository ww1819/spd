-- =============================================================================
-- 科室申购 → 汇总申购：菜单 component/path 切换（按实际 menu_id）
-- 说明：
--   1) 本环境录入菜单 menu_id=1530，审核菜单 menu_id=1572（非 material/menu.sql 中的 3342/3350）
--   2) 权限 perms 不变，无需重赋权
--   3) 执行后请【退出重新登录】或清浏览器缓存，前端 /getRouters 才会加载新 component
--   4) 执行前请确认连的是目标库（与 application-dev.yml 一致）
-- =============================================================================

-- 录入：科室申购 → 汇总申购页
UPDATE sys_menu
SET path       = 'dPurchaseAgg',
    component  = 'department/dPurchaseAgg/index',
    menu_name  = '科室申购',
    remark     = '汇总申购录入（切换自科室分仓申购）',
    is_cache   = 0,
    update_by  = '1',
    update_time = NOW()
WHERE menu_id = 1530;

-- 审核：申购单审核 → 汇总申购审核页
UPDATE sys_menu
SET path       = 'dPurchaseAggAudit',
    component  = 'department/dPurchaseAggAudit/index',
    menu_name  = '申购单审核',
    remark     = '汇总申购审核（审核后按明细仓库拆分）',
    is_cache   = 0,
    update_by  = '1',
    update_time = NOW()
WHERE menu_id = 1572;

-- 若环境使用 material/menu.sql 种子 ID，可一并执行（无对应行则影响 0 行）：
UPDATE sys_menu
SET path = 'dPurchaseAgg', component = 'department/dPurchaseAgg/index', is_cache = 0, update_time = NOW()
WHERE menu_id = 3342 AND component = 'department/dPurchase/index';

UPDATE sys_menu
SET path = 'dPurchaseAggAudit', component = 'department/dPurchaseAggAudit/index', is_cache = 0, update_time = NOW()
WHERE menu_id = 3350 AND component = 'department/dPurchaseAudit/index';

-- 校验（component 应为 dPurchaseAgg / dPurchaseAggAudit）
SELECT menu_id, menu_name, path, component, perms, is_cache
FROM sys_menu
WHERE menu_id IN (1530, 1572, 3342, 3350)
   OR component IN (
        'department/dPurchase/index',
        'department/dPurchaseAudit/index',
        'department/dPurchaseAgg/index',
        'department/dPurchaseAggAudit/index'
      )
ORDER BY menu_id;

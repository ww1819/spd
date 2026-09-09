-- 科室申领/审核、科室请购(申购)/审核：开启菜单缓存（is_cache=0 → meta.noCache=false）
-- 配合前端组件 name 与路由 name 对齐后，顶部 Tags 切页可保留筛选与弹窗操作状态。

UPDATE sys_menu
SET is_cache = '0',
    update_time = NOW()
WHERE menu_type = 'C'
  AND path IN (
    'dApply',
    'dApplyAudit',
    'dPurchaseAgg',
    'dPurchaseAggAudit',
    'dPurchase',
    'dPurchaseAudit'
  )
  AND IFNULL(is_cache, '0') <> '0';

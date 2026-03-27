-- =============================================================================
-- 修复：订单发布、到货验收、盘点申请、定数监测、新品申购申请/审批 —— 无权限 / 未知错误(多为 403)
-- =============================================================================
-- 原因简述：
--   1) 未执行或未成功执行菜单脚本 → sys_menu / hc_customer_menu 缺行；
--   2) 仅 hc_customer_menu 有授权，但租户用户 sys_user_menu 未勾选 → LoginUser.permissions 无对应 perms；
--   3) 改权限后未重新登录 / 未调 getInfo → Redis 中仍为旧 permissions。
--
-- 建议执行顺序（在目标库、备份后）：
--   0) 若访问 /stocktaking/in/* 报 403：先执行 add_warehouse_stocktaking_in_menus.sql（写入 stocktaking:in:* 菜单）；
--   0b) 若访问 /monitoring/fixedNumber/* 报 403：先执行 add_monitoring_fixed_number_menus.sql（写入 monitoring:fixedNumber:* 菜单）；
--   A) 本脚本：为所有「耗材启用」客户补全 hc_customer_menu（仅缺失行）；
--   B) sync_sys_user_menu_from_hc_customer_menu.sql：把客户已启用菜单同步到各用户 sys_user_menu；
--   C) 用户重新登录，或前端调一次 getInfo。
--
-- 若仍 403：在「系统管理 → 用户」中为该用户勾选上述菜单，或核对 sys_menu.perms 与
--   CaigouDingdanController / StkIoBillController / StkIoStocktakingController（/stocktaking/in → stocktaking:in:*）
--   / DeptStocktakingController（/department/stocktaking → department:stocktaking:*）
--   / FixedNumberController（/monitoring/fixedNumber → monitoring:fixedNumber:*）
--   / NewProductApplyController / NewProductAuditController 的 @PreAuthorize 一致。
-- =============================================================================

-- ---------- A) 回填 hc_customer_menu（按 perms 关联 sys_menu，仅 INSERT 不存在组合） ----------
INSERT INTO hc_customer_menu (tenant_id, menu_id, status, is_enabled, create_by, create_time)
SELECT c.customer_id, m.menu_id, '0', '1', 'admin', NOW()
FROM sb_customer c
JOIN sys_menu m
  ON m.perms IN (
    -- 订单发布（与 CaigouDingdanController 一致）
    'caigou:dingdan:list',
    'caigou:dingdan:query',
    'caigou:dingdan:export',
    'caigou:dingdan:add',
    'caigou:dingdan:edit',
    'caigou:dingdan:remove',
    'caigou:dingdan:audit',
    -- 到货验收（StkIoBillController inWarehouse:apply:*）
    'inWarehouse:apply:list',
    'inWarehouse:apply:query',
    'inWarehouse:apply:export',
    'inWarehouse:apply:audit',
    'inWarehouse:apply:edit',
    -- 仓库盘点入库（StkIoStocktakingController，/dev-api/stocktaking/in/list）
    'stocktaking:in:list',
    'stocktaking:in:query',
    'stocktaking:in:export',
    'stocktaking:in:add',
    'stocktaking:in:edit',
    'stocktaking:in:remove',
    'stocktaking:in:audit',
    -- 定数监测（FixedNumberController，/monitoring/fixedNumber/*）
    'monitoring:fixedNumber:list',
    'monitoring:fixedNumber:add',
    'monitoring:fixedNumber:remove',
    'monitoring:fixedNumber:export',
    -- 科室盘点申请（DeptStocktakingController，/department/stocktaking/*）
    'department:stocktaking:list',
    'department:stocktaking:query',
    'department:stocktaking:add',
    'department:stocktaking:export',
    'department:stocktaking:edit',
    'department:stocktaking:remove',
    'department:stocktaking:audit',
    'department:stocktaking:reject',
    -- 新品申购申请 / 审批
    'department:newProductApply:list',
    'department:newProductApply:query',
    'department:newProductApply:add',
    'department:newProductApply:export',
    'department:newProductApply:edit',
    'department:newProductApply:remove',
    'department:newProductAudit:list',
    'department:newProductAudit:query',
    'department:newProductAudit:export',
    'department:newProductAudit:audit',
    'department:newProductAudit:reject'
  )
  AND IFNULL(m.status, '0') = '0'
WHERE c.hc_status = '0'
  AND c.delete_time IS NULL
  AND NOT EXISTS (
    SELECT 1 FROM hc_customer_menu h
    WHERE h.tenant_id = c.customer_id AND h.menu_id = m.menu_id
  );
/

-- ---------- B) 请继续执行（单独脚本）：sync_sys_user_menu_from_hc_customer_menu.sql ----------
-- ---------- C) 执行后让用户重新登录 ----------

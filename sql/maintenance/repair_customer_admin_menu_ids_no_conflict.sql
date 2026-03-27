-- 说明：新库请直接使用最新 spd-admin/.../material/menu.sql（客户管理维护按钮 menu_id 为 3100/3101/3102，
--       避免与库房分类 2280 段、财务分类导入 2297 冲突）。
--
-- 以下与 material/column.sql 末尾一致：保证「财务分类导入」「耗材产品导入（新增/更新导入）」对客户默认开放并回填 hc_customer_menu。

UPDATE sys_menu
SET default_open_to_customer = '1',
    update_time = NOW()
WHERE IFNULL(status, '0') = '0'
  AND (is_platform IS NULL OR is_platform != '1')
  AND perms IN ('foundation:financeCategory:import', 'foundation:material:import');

INSERT INTO hc_customer_menu (tenant_id, menu_id, status, is_enabled, create_by, create_time)
SELECT c.customer_id, m.menu_id, '0', '1', 'admin', NOW()
FROM sb_customer c
JOIN sys_menu m ON m.perms IN ('foundation:financeCategory:import', 'foundation:material:import')
  AND IFNULL(m.status, '0') = '0'
  AND (m.is_platform IS NULL OR m.is_platform != '1')
WHERE IFNULL(c.hc_status, '0') = '0'
  AND NOT EXISTS (
    SELECT 1 FROM hc_customer_menu h
    WHERE h.tenant_id = c.customer_id AND h.menu_id = m.menu_id
  );
/

-- 若缺少「耗材产品导入」菜单行，请先执行 spd-admin/.../material/menu.sql 中「8.5) 耗材产品档案导入」段（menu_id=2298）。
-- 若历史上曾执行过旧脚本导致 2297 被「清理设备数据」覆盖，需从备份恢复 sys_menu 中财务分类导入行，
-- 或重新执行 material/menu.sql 中「财务分类」整段 INSERT，再执行本脚本回填 hc_customer_menu。

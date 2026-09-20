-- 公共：精简数据中心菜单（衡水三院 / 枣强中医院等同库生效）
-- 保留：
--   全院类报表下除「医院医疗设备平台」外的原有项
--   决策性报表：耗材领用收入同比(1514)、高耗单品使用统计(1522)、全院耗材领用同比(1523)
--   数据中心叶子：BI数据分析（耗材）(1164)、效能分析报表(1502)、数字孪生监控大屏(3890)
-- 移除：医院医疗设备平台(1507)、决策性报表其余项、BI数据分析（设备）(1543)

-- 子按钮 + 页面/目录授权清理
DELETE FROM sys_role_menu
WHERE menu_id IN (1507,1508,1509,1510,1511,1512,1513,1515,1516,1517,1518,1519,1520,1521,1524,1525,1543)
   OR menu_id IN (
     SELECT menu_id FROM (
       SELECT menu_id FROM sys_menu
       WHERE parent_id IN (1507,1508,1509,1510,1511,1512,1513,1515,1516,1517,1518,1519,1520,1521,1524,1525,1543)
     ) t
   );

DELETE FROM sys_user_menu
WHERE menu_id IN (1507,1508,1509,1510,1511,1512,1513,1515,1516,1517,1518,1519,1520,1521,1524,1525,1543)
   OR menu_id IN (
     SELECT menu_id FROM (
       SELECT menu_id FROM sys_menu
       WHERE parent_id IN (1507,1508,1509,1510,1511,1512,1513,1515,1516,1517,1518,1519,1520,1521,1524,1525,1543)
     ) t
   );

DELETE FROM sys_post_menu
WHERE menu_id IN (1507,1508,1509,1510,1511,1512,1513,1515,1516,1517,1518,1519,1520,1521,1524,1525,1543)
   OR menu_id IN (
     SELECT menu_id FROM (
       SELECT menu_id FROM sys_menu
       WHERE parent_id IN (1507,1508,1509,1510,1511,1512,1513,1515,1516,1517,1518,1519,1520,1521,1524,1525,1543)
     ) t
   );

DELETE FROM hc_customer_menu
WHERE menu_id IN (1507,1508,1509,1510,1511,1512,1513,1515,1516,1517,1518,1519,1520,1521,1524,1525,1543)
   OR menu_id IN (
     SELECT menu_id FROM (
       SELECT menu_id FROM sys_menu
       WHERE parent_id IN (1507,1508,1509,1510,1511,1512,1513,1515,1516,1517,1518,1519,1520,1521,1524,1525,1543)
     ) t
   );

DELETE FROM sys_menu
WHERE parent_id IN (1507,1508,1509,1510,1511,1512,1513,1515,1516,1517,1518,1519,1520,1521,1524,1525,1543);

DELETE FROM sys_menu
WHERE menu_id IN (1507,1508,1509,1510,1511,1512,1513,1515,1516,1517,1518,1519,1520,1521,1524,1525,1543);

-- 确保保留菜单对衡水/枣强开通（目录 + 叶子）
INSERT INTO hc_customer_menu (tenant_id, menu_id, status, is_enabled, create_by, create_time)
SELECT t.tenant_id, m.menu_id, '0', '1', 'admin', NOW()
FROM (SELECT 'hengsui-third-001' tenant_id UNION ALL SELECT 'zaoqiang-tcm-001') t
JOIN (
  SELECT 1066 menu_id UNION ALL SELECT 1500 UNION ALL SELECT 1501
  UNION ALL SELECT 1514 UNION ALL SELECT 1522 UNION ALL SELECT 1523
  UNION ALL SELECT 1164 UNION ALL SELECT 1502 UNION ALL SELECT 3890
) m
WHERE EXISTS (SELECT 1 FROM sb_customer c WHERE c.customer_id = t.tenant_id AND c.delete_time IS NULL)
  AND NOT EXISTS (
    SELECT 1 FROM hc_customer_menu h
    WHERE h.tenant_id = t.tenant_id AND h.menu_id = m.menu_id
  );

UPDATE hc_customer_menu
SET is_enabled = '1', status = '0'
WHERE tenant_id IN ('hengsui-third-001', 'zaoqiang-tcm-001')
  AND menu_id IN (1066, 1500, 1501, 1514, 1522, 1523, 1164, 1502, 3890);

-- 决策性报表目录下保留三项顺序
UPDATE sys_menu SET order_num = 1, update_by = '1', update_time = NOW() WHERE menu_id = 1514;
UPDATE sys_menu SET order_num = 2, update_by = '1', update_time = NOW() WHERE menu_id = 1522;
UPDATE sys_menu SET order_num = 3, update_by = '1', update_time = NOW() WHERE menu_id = 1523;

-- 数据中心叶子顺序：BI耗材、效能分析、数字孪生
UPDATE sys_menu SET order_num = 1, update_by = '1', update_time = NOW() WHERE menu_id = 1164;
UPDATE sys_menu SET order_num = 2, update_by = '1', update_time = NOW() WHERE menu_id = 1502;
UPDATE sys_menu SET order_num = 3, update_by = '1', update_time = NOW() WHERE menu_id = 3890;
UPDATE sys_menu SET order_num = 4, update_by = '1', update_time = NOW() WHERE menu_id = 1500;
UPDATE sys_menu SET order_num = 5, update_by = '1', update_time = NOW() WHERE menu_id = 1501;

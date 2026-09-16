-- 科室盈亏处理(3714) 从「科室领用」挪到「科室盘点」(1560)
UPDATE sys_menu
SET parent_id = 1560,
    order_num = (
      SELECT IFNULL(MAX(o), 0) + 1 FROM (
        SELECT order_num o FROM sys_menu WHERE parent_id = 1560 AND menu_id <> 3714
      ) t
    ),
    update_by = '1',
    update_time = NOW()
WHERE menu_id = 3714;

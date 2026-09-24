# -*- coding: utf-8 -*-
import pymysql

c = pymysql.connect(
    host="rm-bp1tov1b3948fc5inbo.mysql.rds.aliyuncs.com",
    user="spd",
    password="Spd@456ww",
    database="aspt",
    charset="utf8mb4",
)
cur = c.cursor()
cur.execute(
    """
    SELECT id, code, name, warehouse_type, warehouse_status, tenant_id, del_flag
    FROM fd_warehouse
    WHERE name LIKE %s OR name LIKE %s
    ORDER BY id
    """,
    ("%二级库%", "%原有库存%"),
)
print("WAREHOUSES:")
for row in cur.fetchall():
    print(row)

cur.execute(
    """
    SELECT w.id, w.name, w.warehouse_type,
           COUNT(s.id) AS cnt,
           SUM(CASE WHEN IFNULL(s.qty,0) > 0 THEN 1 ELSE 0 END) AS pos_cnt,
           SUM(IFNULL(s.qty,0)) AS qty_sum
    FROM fd_warehouse w
    LEFT JOIN stk_inventory s
      ON s.warehouse_id = w.id
     AND (s.del_flag = 0 OR s.del_flag IS NULL)
     AND s.tenant_id = w.tenant_id
    WHERE w.name LIKE %s OR w.name LIKE %s
    GROUP BY w.id, w.name, w.warehouse_type
    """,
    ("%二级库%", "%原有库存%"),
)
print("STK_INVENTORY:")
for row in cur.fetchall():
    print(row)

cur.execute(
    """
    SELECT w.id, w.name,
           COUNT(d.id) AS cnt,
           SUM(CASE WHEN IFNULL(d.qty,0) > 0 THEN 1 ELSE 0 END) AS pos_cnt
    FROM fd_warehouse w
    LEFT JOIN stk_dep_inventory d
      ON d.warehouse_id = w.id
     AND (d.del_flag = 0 OR d.del_flag IS NULL)
    WHERE w.name LIKE %s OR w.name LIKE %s
    GROUP BY w.id, w.name
    """,
    ("%二级库%", "%原有库存%"),
)
print("STK_DEP_INVENTORY:")
for row in cur.fetchall():
    print(row)

# All warehouses with positive stock for tenant, to see id/name mapping
cur.execute(
    """
    SELECT w.id, w.name, w.warehouse_type, COUNT(*) c
    FROM stk_inventory s
    JOIN fd_warehouse w ON w.id = s.warehouse_id
    WHERE s.tenant_id = 'hengsui-third-001'
      AND (s.del_flag = 0 OR s.del_flag IS NULL)
      AND IFNULL(s.qty,0) > 0
    GROUP BY w.id, w.name, w.warehouse_type
    ORDER BY c DESC
    LIMIT 30
    """
)
print("TOP_STOCK_WAREHOUSES:")
for row in cur.fetchall():
    print(row)

c.close()

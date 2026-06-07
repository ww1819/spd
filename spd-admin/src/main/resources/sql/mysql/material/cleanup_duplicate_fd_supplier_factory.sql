-- 众阳镜像同步主数据去重：按租户业务键保留最小主键，合并引用后物理删除重复行
-- 覆盖：供应商、厂家、科室、单位、库房分类、产品档案、用户
-- 执行时机：补建 column.sql 中各 uk_* 唯一索引之前
-- 建议：先在测试库验证；执行前备份相关表

-- ========== 供应商 fd_supplier (tenant_id, his_id) ==========
UPDATE fd_material m
INNER JOIN (
    SELECT s.id AS dup_id, d.keep_id
    FROM fd_supplier s
    INNER JOIN (
        SELECT COALESCE(tenant_id, '') AS tid, his_id, MIN(id) AS keep_id
        FROM fd_supplier
        WHERE his_id IS NOT NULL AND TRIM(his_id) <> '' AND del_flag = 0
        GROUP BY COALESCE(tenant_id, ''), his_id
        HAVING COUNT(*) > 1
    ) d ON COALESCE(s.tenant_id, '') = d.tid AND s.his_id = d.his_id AND s.id <> d.keep_id
    WHERE s.del_flag = 0
) x ON m.supplier_id = x.dup_id
SET m.supplier_id = x.keep_id;

DELETE scl FROM fd_supplier_change_log scl
INNER JOIN (
    SELECT s2.id AS dup_id
    FROM fd_supplier s2
    INNER JOIN (
        SELECT COALESCE(tenant_id, '') AS tid, his_id, MIN(id) AS keep_id
        FROM fd_supplier
        WHERE his_id IS NOT NULL AND TRIM(his_id) <> '' AND del_flag = 0
        GROUP BY COALESCE(tenant_id, ''), his_id
        HAVING COUNT(*) > 1
    ) d ON COALESCE(s2.tenant_id, '') = d.tid AND s2.his_id = d.his_id AND s2.id <> d.keep_id
    WHERE s2.del_flag = 0
) dup ON scl.supplier_id = dup.dup_id;

DELETE s FROM fd_supplier s
INNER JOIN (
    SELECT s2.id AS dup_id
    FROM fd_supplier s2
    INNER JOIN (
        SELECT COALESCE(tenant_id, '') AS tid, his_id, MIN(id) AS keep_id
        FROM fd_supplier
        WHERE his_id IS NOT NULL AND TRIM(his_id) <> '' AND del_flag = 0
        GROUP BY COALESCE(tenant_id, ''), his_id
        HAVING COUNT(*) > 1
    ) d ON COALESCE(s2.tenant_id, '') = d.tid AND s2.his_id = d.his_id AND s2.id <> d.keep_id
    WHERE s2.del_flag = 0
) dup ON s.id = dup.dup_id;

-- ========== 生产厂家 fd_factory (tenant_id, his_id) ==========
UPDATE fd_material m
INNER JOIN (
    SELECT f.factory_id AS dup_id, d.keep_id
    FROM fd_factory f
    INNER JOIN (
        SELECT COALESCE(tenant_id, '') AS tid, his_id, MIN(factory_id) AS keep_id
        FROM fd_factory
        WHERE his_id IS NOT NULL AND TRIM(his_id) <> '' AND del_flag = 0
        GROUP BY COALESCE(tenant_id, ''), his_id
        HAVING COUNT(*) > 1
    ) d ON COALESCE(f.tenant_id, '') = d.tid AND f.his_id = d.his_id AND f.factory_id <> d.keep_id
    WHERE f.del_flag = 0
) x ON m.factory_id = x.dup_id
SET m.factory_id = x.keep_id;

UPDATE stk_initial_import_entry e
INNER JOIN (
    SELECT f.factory_id AS dup_id, d.keep_id
    FROM fd_factory f
    INNER JOIN (
        SELECT COALESCE(tenant_id, '') AS tid, his_id, MIN(factory_id) AS keep_id
        FROM fd_factory
        WHERE his_id IS NOT NULL AND TRIM(his_id) <> '' AND del_flag = 0
        GROUP BY COALESCE(tenant_id, ''), his_id
        HAVING COUNT(*) > 1
    ) d ON COALESCE(f.tenant_id, '') = d.tid AND f.his_id = d.his_id AND f.factory_id <> d.keep_id
    WHERE f.del_flag = 0
) x ON e.factory_id = x.dup_id
SET e.factory_id = x.keep_id;

UPDATE stk_initial_import_entry e
INNER JOIN (
    SELECT s.id AS dup_id, d.keep_id
    FROM fd_supplier s
    INNER JOIN (
        SELECT COALESCE(tenant_id, '') AS tid, his_id, MIN(id) AS keep_id
        FROM fd_supplier
        WHERE his_id IS NOT NULL AND TRIM(his_id) <> '' AND del_flag = 0
        GROUP BY COALESCE(tenant_id, ''), his_id
        HAVING COUNT(*) > 1
    ) d ON COALESCE(s.tenant_id, '') = d.tid AND s.his_id = d.his_id AND s.id <> d.keep_id
    WHERE s.del_flag = 0
) x ON e.supplier_id = x.dup_id
SET e.supplier_id = x.keep_id;

DELETE fcl FROM fd_factory_change_log fcl
INNER JOIN (
    SELECT f2.factory_id AS dup_id
    FROM fd_factory f2
    INNER JOIN (
        SELECT COALESCE(tenant_id, '') AS tid, his_id, MIN(factory_id) AS keep_id
        FROM fd_factory
        WHERE his_id IS NOT NULL AND TRIM(his_id) <> '' AND del_flag = 0
        GROUP BY COALESCE(tenant_id, ''), his_id
        HAVING COUNT(*) > 1
    ) d ON COALESCE(f2.tenant_id, '') = d.tid AND f2.his_id = d.his_id AND f2.factory_id <> d.keep_id
    WHERE f2.del_flag = 0
) dup ON fcl.factory_id = dup.dup_id;

DELETE f FROM fd_factory f
INNER JOIN (
    SELECT f2.factory_id AS dup_id
    FROM fd_factory f2
    INNER JOIN (
        SELECT COALESCE(tenant_id, '') AS tid, his_id, MIN(factory_id) AS keep_id
        FROM fd_factory
        WHERE his_id IS NOT NULL AND TRIM(his_id) <> '' AND del_flag = 0
        GROUP BY COALESCE(tenant_id, ''), his_id
        HAVING COUNT(*) > 1
    ) d ON COALESCE(f2.tenant_id, '') = d.tid AND f2.his_id = d.his_id AND f2.factory_id <> d.keep_id
    WHERE f2.del_flag = 0
) dup ON f.factory_id = dup.dup_id;

-- ========== 科室 fd_department (tenant_id, his_id) ==========
UPDATE sys_user_department ud
INNER JOIN (
    SELECT dep.id AS dup_id, d.keep_id
    FROM fd_department dep
    INNER JOIN (
        SELECT COALESCE(tenant_id, '') AS tid, his_id, MIN(id) AS keep_id
        FROM fd_department
        WHERE his_id IS NOT NULL AND TRIM(his_id) <> '' AND del_flag = 0
        GROUP BY COALESCE(tenant_id, ''), his_id
        HAVING COUNT(*) > 1
    ) d ON COALESCE(dep.tenant_id, '') = d.tid AND dep.his_id = d.his_id AND dep.id <> d.keep_id
    WHERE dep.del_flag = 0
) x ON ud.department_id = x.dup_id
SET ud.department_id = x.keep_id;

DELETE dcl FROM fd_department_change_log dcl
INNER JOIN (
    SELECT dep.id AS dup_id
    FROM fd_department dep
    INNER JOIN (
        SELECT COALESCE(tenant_id, '') AS tid, his_id, MIN(id) AS keep_id
        FROM fd_department
        WHERE his_id IS NOT NULL AND TRIM(his_id) <> '' AND del_flag = 0
        GROUP BY COALESCE(tenant_id, ''), his_id
        HAVING COUNT(*) > 1
    ) d ON COALESCE(dep.tenant_id, '') = d.tid AND dep.his_id = d.his_id AND dep.id <> d.keep_id
    WHERE dep.del_flag = 0
) dup ON dcl.department_id = dup.dup_id;

DELETE dep FROM fd_department dep
INNER JOIN (
    SELECT dep2.id AS dup_id
    FROM fd_department dep2
    INNER JOIN (
        SELECT COALESCE(tenant_id, '') AS tid, his_id, MIN(id) AS keep_id
        FROM fd_department
        WHERE his_id IS NOT NULL AND TRIM(his_id) <> '' AND del_flag = 0
        GROUP BY COALESCE(tenant_id, ''), his_id
        HAVING COUNT(*) > 1
    ) d ON COALESCE(dep2.tenant_id, '') = d.tid AND dep2.his_id = d.his_id AND dep2.id <> d.keep_id
    WHERE dep2.del_flag = 0
) dup ON dep.id = dup.dup_id;

-- ========== 计量单位 fd_unit (tenant_id, his_unit_id) ==========
UPDATE fd_material m
INNER JOIN (
    SELECT u.unit_id AS dup_id, d.keep_id
    FROM fd_unit u
    INNER JOIN (
        SELECT COALESCE(tenant_id, '') AS tid, his_unit_id, MIN(unit_id) AS keep_id
        FROM fd_unit
        WHERE his_unit_id IS NOT NULL AND TRIM(his_unit_id) <> '' AND del_flag = 0
        GROUP BY COALESCE(tenant_id, ''), his_unit_id
        HAVING COUNT(*) > 1
    ) d ON COALESCE(u.tenant_id, '') = d.tid AND u.his_unit_id = d.his_unit_id AND u.unit_id <> d.keep_id
    WHERE u.del_flag = 0
) x ON m.unit_id = x.dup_id
SET m.unit_id = x.keep_id;

DELETE u FROM fd_unit u
INNER JOIN (
    SELECT u2.unit_id AS dup_id
    FROM fd_unit u2
    INNER JOIN (
        SELECT COALESCE(tenant_id, '') AS tid, his_unit_id, MIN(unit_id) AS keep_id
        FROM fd_unit
        WHERE his_unit_id IS NOT NULL AND TRIM(his_unit_id) <> '' AND del_flag = 0
        GROUP BY COALESCE(tenant_id, ''), his_unit_id
        HAVING COUNT(*) > 1
    ) d ON COALESCE(u2.tenant_id, '') = d.tid AND u2.his_unit_id = d.his_unit_id AND u2.unit_id <> d.keep_id
    WHERE u2.del_flag = 0
) dup ON u.unit_id = dup.dup_id;

-- ========== 库房分类 fd_warehouse_category (tenant_id, his_id) ==========
UPDATE fd_material m
INNER JOIN (
    SELECT wc.warehouse_category_id AS dup_id, d.keep_id
    FROM fd_warehouse_category wc
    INNER JOIN (
        SELECT COALESCE(tenant_id, '') AS tid, his_id, MIN(warehouse_category_id) AS keep_id
        FROM fd_warehouse_category
        WHERE his_id IS NOT NULL AND TRIM(his_id) <> '' AND del_flag = 0
        GROUP BY COALESCE(tenant_id, ''), his_id
        HAVING COUNT(*) > 1
    ) d ON COALESCE(wc.tenant_id, '') = d.tid AND wc.his_id = d.his_id AND wc.warehouse_category_id <> d.keep_id
    WHERE wc.del_flag = 0
) x ON m.storeroom_id = x.dup_id
SET m.storeroom_id = x.keep_id;

DELETE wc FROM fd_warehouse_category wc
INNER JOIN (
    SELECT wc2.warehouse_category_id AS dup_id
    FROM fd_warehouse_category wc2
    INNER JOIN (
        SELECT COALESCE(tenant_id, '') AS tid, his_id, MIN(warehouse_category_id) AS keep_id
        FROM fd_warehouse_category
        WHERE his_id IS NOT NULL AND TRIM(his_id) <> '' AND del_flag = 0
        GROUP BY COALESCE(tenant_id, ''), his_id
        HAVING COUNT(*) > 1
    ) d ON COALESCE(wc2.tenant_id, '') = d.tid AND wc2.his_id = d.his_id AND wc2.warehouse_category_id <> d.keep_id
    WHERE wc2.del_flag = 0
) dup ON wc.warehouse_category_id = dup.dup_id;

-- ========== 产品档案 fd_material (tenant_id, his_id, his_spec_packing_id) ==========
UPDATE stk_initial_import_entry e
INNER JOIN (
    SELECT m.id AS dup_id, d.keep_id
    FROM fd_material m
    INNER JOIN (
        SELECT COALESCE(tenant_id, '') AS tid, his_id, his_spec_packing_id, MIN(id) AS keep_id
        FROM fd_material
        WHERE his_id IS NOT NULL AND TRIM(his_id) <> ''
          AND his_spec_packing_id IS NOT NULL AND TRIM(his_spec_packing_id) <> ''
          AND (del_flag = 0 OR del_flag IS NULL)
        GROUP BY COALESCE(tenant_id, ''), his_id, his_spec_packing_id
        HAVING COUNT(*) > 1
    ) d ON COALESCE(m.tenant_id, '') = d.tid AND m.his_id = d.his_id
       AND m.his_spec_packing_id = d.his_spec_packing_id AND m.id <> d.keep_id
    WHERE m.del_flag = 0 OR m.del_flag IS NULL
) x ON e.material_id = x.dup_id
SET e.material_id = x.keep_id;

DELETE mcl FROM fd_material_change_log mcl
INNER JOIN (
    SELECT m.id AS dup_id
    FROM fd_material m
    INNER JOIN (
        SELECT COALESCE(tenant_id, '') AS tid, his_id, his_spec_packing_id, MIN(id) AS keep_id
        FROM fd_material
        WHERE his_id IS NOT NULL AND TRIM(his_id) <> ''
          AND his_spec_packing_id IS NOT NULL AND TRIM(his_spec_packing_id) <> ''
          AND (del_flag = 0 OR del_flag IS NULL)
        GROUP BY COALESCE(tenant_id, ''), his_id, his_spec_packing_id
        HAVING COUNT(*) > 1
    ) d ON COALESCE(m.tenant_id, '') = d.tid AND m.his_id = d.his_id
       AND m.his_spec_packing_id = d.his_spec_packing_id AND m.id <> d.keep_id
    WHERE m.del_flag = 0 OR m.del_flag IS NULL
) dup ON mcl.material_id = dup.dup_id;

DELETE m FROM fd_material m
INNER JOIN (
    SELECT m2.id AS dup_id
    FROM fd_material m2
    INNER JOIN (
        SELECT COALESCE(tenant_id, '') AS tid, his_id, his_spec_packing_id, MIN(id) AS keep_id
        FROM fd_material
        WHERE his_id IS NOT NULL AND TRIM(his_id) <> ''
          AND his_spec_packing_id IS NOT NULL AND TRIM(his_spec_packing_id) <> ''
          AND (del_flag = 0 OR del_flag IS NULL)
        GROUP BY COALESCE(tenant_id, ''), his_id, his_spec_packing_id
        HAVING COUNT(*) > 1
    ) d ON COALESCE(m2.tenant_id, '') = d.tid AND m2.his_id = d.his_id
       AND m2.his_spec_packing_id = d.his_spec_packing_id AND m2.id <> d.keep_id
    WHERE m2.del_flag = 0 OR m2.del_flag IS NULL
) dup ON m.id = dup.dup_id;

-- ========== 用户 sys_user (customer_id, his_id) ==========
UPDATE sys_user_department ud
INNER JOIN (
    SELECT u.user_id AS dup_id, d.keep_id
    FROM sys_user u
    INNER JOIN (
        SELECT COALESCE(customer_id, '') AS cid, his_id, MIN(user_id) AS keep_id
        FROM sys_user
        WHERE his_id IS NOT NULL AND TRIM(his_id) <> '' AND del_flag = '0'
        GROUP BY COALESCE(customer_id, ''), his_id
        HAVING COUNT(*) > 1
    ) d ON COALESCE(u.customer_id, '') = d.cid AND u.his_id = d.his_id AND u.user_id <> d.keep_id
    WHERE u.del_flag = '0'
) x ON ud.user_id = x.dup_id
SET ud.user_id = x.keep_id;

DELETE u FROM sys_user u
INNER JOIN (
    SELECT u2.user_id AS dup_id
    FROM sys_user u2
    INNER JOIN (
        SELECT COALESCE(customer_id, '') AS cid, his_id, MIN(user_id) AS keep_id
        FROM sys_user
        WHERE his_id IS NOT NULL AND TRIM(his_id) <> '' AND del_flag = '0'
        GROUP BY COALESCE(customer_id, ''), his_id
        HAVING COUNT(*) > 1
    ) d ON COALESCE(u2.customer_id, '') = d.cid AND u2.his_id = d.his_id AND u2.user_id <> d.keep_id
    WHERE u2.del_flag = '0'
) dup ON u.user_id = dup.dup_id;

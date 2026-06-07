-- 一次性：为当前租户全部有效耗材档案关联供应商「期初维护的供应商」
-- 适用：aspt / 枣强中医院租户 zaoqiang-tcm-001（可按需改 @tenant_id）
-- 说明：库内若已有 id=1 的「期初维护」，直接改名并全量关联；否则按名称查找或新建后再关联。

SET NAMES utf8mb4;
SET @tenant_id := 'zaoqiang-tcm-001';
SET @supplier_name := '期初维护的供应商';

START TRANSACTION;

-- 1) 优先使用 id=1 的期初维护供应商（aspt 已存在）
UPDATE fd_supplier
SET name = @supplier_name,
    update_by = 'patch_qichu_supplier',
    update_time = NOW()
WHERE id = 1
  AND tenant_id = @tenant_id
  AND (del_flag IS NULL OR del_flag = 0);

SET @supplier_id := NULL;
SELECT id INTO @supplier_id
FROM fd_supplier
WHERE tenant_id = @tenant_id
  AND (del_flag IS NULL OR del_flag = 0)
  AND name = @supplier_name
ORDER BY id
LIMIT 1;

-- 2) 仍无则新建
SET @next_code := (
    SELECT IFNULL(MAX(CAST(code AS UNSIGNED)), 99999) + 1
    FROM fd_supplier
    WHERE tenant_id = @tenant_id
      AND code REGEXP '^[0-9]+$'
);
SET @next_code := IF(@next_code < 100000, 100000, @next_code);

INSERT INTO fd_supplier (code, name, del_flag, tenant_id, remark, create_by, create_time)
SELECT CAST(@next_code AS CHAR), @supplier_name, 0, @tenant_id,
       '期初数据维护：统一关联全部耗材档案', 'patch_qichu_supplier', NOW()
FROM DUAL
WHERE @supplier_id IS NULL;

SELECT id INTO @supplier_id
FROM fd_supplier
WHERE tenant_id = @tenant_id
  AND (del_flag IS NULL OR del_flag = 0)
  AND name = @supplier_name
ORDER BY id
LIMIT 1;

-- 3) 全部有效耗材档案关联该供应商
UPDATE fd_material
SET supplier_id = @supplier_id,
    update_by = 'patch_qichu_supplier',
    update_time = NOW()
WHERE tenant_id = @tenant_id
  AND (del_flag IS NULL OR del_flag != 1);

COMMIT;

SELECT @supplier_id AS supplier_id, @supplier_name AS supplier_name;
SELECT COUNT(*) AS material_linked
FROM fd_material
WHERE tenant_id = @tenant_id
  AND (del_flag IS NULL OR del_flag != 1)
  AND supplier_id = @supplier_id;

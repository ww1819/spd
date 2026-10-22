-- =============================================================================
-- 衡水三院：按耗材档案 is_gz 重算 HIS 收费项 / 计费镜像高低值（仅两档：1高值 / 2低值）
-- 租户：hengsui-third-001
-- 说明：
--   1) 先在测试库执行并核对下方校验 SQL，再上生产。
--   2) 生产建议业务低峰执行；unified 表数据量大时可能跑几十秒～数分钟。
--   3) 执行后：档案 is_gz=1 且已对照的收费编码 → value_level=1；其余 → 2。
-- =============================================================================

-- A. 收费项目镜像：有对照且档案标高值 → 1，否则 → 2
UPDATE his_charge_item_mirror cim
LEFT JOIN (
    SELECT DISTINCT
           trim(fm.his_charge_item_id) AS charge_item_id
      FROM fd_material fm
     WHERE fm.tenant_id = 'hengsui-third-001'
       AND (fm.del_flag = 0 OR fm.del_flag IS NULL)
       AND trim(ifnull(fm.is_gz, '')) = '1'
       AND fm.his_charge_item_id IS NOT NULL
       AND trim(fm.his_charge_item_id) != ''
) hf ON hf.charge_item_id = trim(cim.charge_item_id)
SET cim.value_level = CASE WHEN hf.charge_item_id IS NOT NULL THEN '1' ELSE '2' END,
    cim.update_time = NOW()
WHERE cim.tenant_id = 'hengsui-third-001'
  AND (cim.deleted_flag IS NULL OR cim.deleted_flag <> 1);

-- B. 统一计费镜像：同上规则刷新冗余 value_level
UPDATE his_patient_charge_mirror_unified u
LEFT JOIN (
    SELECT DISTINCT
           fm.tenant_id AS tenant_id,
           trim(fm.his_charge_item_id) AS charge_item_id
      FROM fd_material fm
     WHERE fm.tenant_id = 'hengsui-third-001'
       AND (fm.del_flag = 0 OR fm.del_flag IS NULL)
       AND trim(ifnull(fm.is_gz, '')) = '1'
       AND fm.his_charge_item_id IS NOT NULL
       AND trim(fm.his_charge_item_id) != ''
) hf ON hf.tenant_id = u.tenant_id
    AND hf.charge_item_id = trim(u.charge_item_id)
LEFT JOIN (
    SELECT DISTINCT
           cim.tenant_id AS tenant_id,
           trim(cim.charge_item_id) AS charge_item_id
      FROM his_charge_item_mirror cim
     WHERE cim.tenant_id = 'hengsui-third-001'
       AND (cim.deleted_flag IS NULL OR cim.deleted_flag <> 1)
       AND cim.value_level = '1'
       AND cim.charge_item_id IS NOT NULL
       AND trim(cim.charge_item_id) != ''
) hcf ON hcf.tenant_id = u.tenant_id
    AND hcf.charge_item_id = trim(u.charge_item_id)
SET u.value_level = CASE
        WHEN hf.charge_item_id IS NOT NULL OR hcf.charge_item_id IS NOT NULL THEN '1'
        ELSE '2'
    END,
    u.update_time = NOW()
WHERE u.tenant_id = 'hengsui-third-001';

-- -----------------------------------------------------------------------------
-- 校验（可选）
-- -----------------------------------------------------------------------------
-- 1) 收费项镜像高低值分布
-- SELECT value_level, COUNT(*) cnt
--   FROM his_charge_item_mirror
--  WHERE tenant_id = 'hengsui-third-001'
--    AND (deleted_flag IS NULL OR deleted_flag <> 1)
--  GROUP BY value_level;

-- 2) 统一镜像高低值分布（期望不再大量 null/0）
-- SELECT ifnull(value_level, 'NULL') AS value_level, COUNT(*) cnt
--   FROM his_patient_charge_mirror_unified
--  WHERE tenant_id = 'hengsui-third-001'
--  GROUP BY ifnull(value_level, 'NULL');

-- 3) 档案已标高值但镜像仍非高值（应为 0 行）
-- SELECT fm.code, fm.name, fm.his_charge_item_id, fm.is_gz,
--        cim.value_level AS charge_item_vl
--   FROM fd_material fm
--   LEFT JOIN his_charge_item_mirror cim
--     ON cim.tenant_id = fm.tenant_id
--    AND trim(cim.charge_item_id) = trim(fm.his_charge_item_id)
--    AND (cim.deleted_flag IS NULL OR cim.deleted_flag <> 1)
--  WHERE fm.tenant_id = 'hengsui-third-001'
--    AND (fm.del_flag = 0 OR fm.del_flag IS NULL)
--    AND trim(ifnull(fm.is_gz, '')) = '1'
--    AND fm.his_charge_item_id IS NOT NULL
--    AND trim(fm.his_charge_item_id) != ''
--    AND ifnull(cim.value_level, '') <> '1'
--  LIMIT 50;

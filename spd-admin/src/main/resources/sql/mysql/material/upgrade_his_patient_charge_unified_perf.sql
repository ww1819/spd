-- 患者计费统一表列表性能（方案A）：冗余 value_level、规范化 charge_item_id、复合索引
-- 部署后于 aspt 执行一次（可与 upgrade_his_patient_charge_mirror_unified.sql 分开执行）

SET @col_exists = (
  SELECT COUNT(*) FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'his_patient_charge_mirror_unified'
    AND COLUMN_NAME = 'value_level'
);
SET @ddl_col = IF(@col_exists = 0,
  'ALTER TABLE his_patient_charge_mirror_unified ADD COLUMN value_level varchar(8) DEFAULT NULL COMMENT ''高低值(冗余自收费项镜像,1高2低)'' AFTER process_by',
  'SELECT 1');
PREPARE stmt_col FROM @ddl_col;
EXECUTE stmt_col;
DEALLOCATE PREPARE stmt_col;
/

UPDATE his_patient_charge_mirror_unified
SET charge_item_id = trim(charge_item_id)
WHERE charge_item_id IS NOT NULL AND charge_item_id != trim(charge_item_id);
/

UPDATE his_inpatient_charge_mirror
SET charge_item_id = trim(charge_item_id)
WHERE charge_item_id IS NOT NULL AND charge_item_id != trim(charge_item_id);
/

UPDATE his_outpatient_charge_mirror
SET charge_item_id = trim(charge_item_id)
WHERE charge_item_id IS NOT NULL AND charge_item_id != trim(charge_item_id);
/

UPDATE his_patient_charge_mirror_unified u
INNER JOIN his_charge_item_mirror cim
  ON cim.tenant_id = u.tenant_id
 AND cim.charge_item_id = u.charge_item_id
 AND (cim.deleted_flag IS NULL OR cim.deleted_flag = 0)
SET u.value_level = cim.value_level
WHERE u.value_level IS NULL OR trim(ifnull(u.value_level, '')) = '';
/

UPDATE his_patient_charge_mirror_unified
SET value_level = '2'
WHERE value_level IS NULL OR trim(ifnull(value_level, '')) = '';
/

-- 列表常用筛选：租户 + 就诊类型 + 处理状态 + 计费时间
SET @idx_exists = (
  SELECT COUNT(*) FROM information_schema.statistics
  WHERE table_schema = DATABASE()
    AND table_name = 'his_patient_charge_mirror_unified'
    AND index_name = 'idx_hpcm_unified_q'
);
SET @ddl = IF(@idx_exists = 0,
  'ALTER TABLE his_patient_charge_mirror_unified ADD KEY idx_hpcm_unified_q (tenant_id, visit_kind, process_status, charge_at)',
  'SELECT 1');
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
/

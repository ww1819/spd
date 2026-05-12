-- 患者计费镜像统一表：列表查询单表；与住院/门诊镜像主键 id 一致；需部署后执行一次回填历史数据。
-- MySQL 5.7+

CREATE TABLE IF NOT EXISTS `his_patient_charge_mirror_unified` (
  `id` varchar(36) NOT NULL COMMENT '与 his_inpatient_charge_mirror / his_outpatient_charge_mirror 主键相同',
  `tenant_id` varchar(36) NOT NULL COMMENT '租户ID',
  `visit_kind` varchar(16) NOT NULL COMMENT 'INPATIENT/OUTPATIENT',
  `fetch_batch_id` varchar(36) DEFAULT NULL COMMENT '抓取批次ID',
  `his_inpatient_charge_id` varchar(32) DEFAULT NULL COMMENT 'HIS住院计费明细主键',
  `his_outpatient_charge_id` varchar(32) DEFAULT NULL COMMENT 'HIS门诊计费明细主键',
  `his_inpatient_charge_id_tf` varchar(32) DEFAULT NULL COMMENT '住院退费关联ID',
  `his_outpatient_charge_id_tf` varchar(32) DEFAULT NULL COMMENT '门诊退费关联ID',
  `patient_id` varchar(32) DEFAULT NULL,
  `patient_name` varchar(128) DEFAULT NULL,
  `inpatient_no` varchar(64) DEFAULT NULL,
  `outpatient_no` varchar(64) DEFAULT NULL,
  `dept_code` varchar(32) DEFAULT NULL COMMENT '住院费用科室编码',
  `dept_name` varchar(128) DEFAULT NULL,
  `clinic_code` varchar(32) DEFAULT NULL COMMENT '门诊就诊编码',
  `clinic_name` varchar(128) DEFAULT NULL,
  `doctor_id` varchar(32) DEFAULT NULL,
  `doctor_name` varchar(128) DEFAULT NULL,
  `charge_item_id` varchar(64) DEFAULT NULL,
  `item_name` varchar(512) DEFAULT NULL,
  `spec_model` varchar(128) DEFAULT NULL,
  `batch_no` varchar(128) DEFAULT NULL,
  `expire_date` varchar(64) DEFAULT NULL,
  `use_date` datetime DEFAULT NULL COMMENT '住院使用时间',
  `charge_date_display` varchar(64) DEFAULT NULL COMMENT '计费时间原始展示',
  `charge_at` datetime DEFAULT NULL COMMENT '计费时间(用于排序与区间筛选)',
  `quantity` decimal(18,6) DEFAULT NULL,
  `unit_price` decimal(18,6) DEFAULT NULL,
  `total_amount` decimal(18,6) DEFAULT NULL,
  `charge_operator` varchar(128) DEFAULT NULL,
  `payment_type` varchar(32) DEFAULT NULL,
  `receipt_no` varchar(64) DEFAULT NULL,
  `remark` varchar(512) DEFAULT NULL,
  `row_fingerprint` varchar(64) DEFAULT NULL,
  `process_status` varchar(32) NOT NULL DEFAULT 'PENDING_CONSUME',
  `process_type` varchar(32) DEFAULT NULL,
  `process_time` datetime DEFAULT NULL,
  `process_by` varchar(64) DEFAULT NULL,
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_hpcm_unified_list` (`tenant_id`,`visit_kind`,`charge_at`),
  KEY `idx_hpcm_unified_tenant_at` (`tenant_id`,`charge_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='HIS患者计费镜像统一表(住院+门诊)';

INSERT INTO his_patient_charge_mirror_unified (
  id, tenant_id, visit_kind, fetch_batch_id,
  his_inpatient_charge_id, his_outpatient_charge_id, his_inpatient_charge_id_tf, his_outpatient_charge_id_tf,
  patient_id, patient_name, inpatient_no, outpatient_no,
  dept_code, dept_name, clinic_code, clinic_name,
  doctor_id, doctor_name, charge_item_id, item_name, spec_model, batch_no, expire_date,
  use_date, charge_date_display, charge_at,
  quantity, unit_price, total_amount, charge_operator, payment_type, receipt_no, remark, row_fingerprint,
  process_status, process_type, process_time, process_by, create_by, create_time, update_by, update_time
)
SELECT
  m.id, m.tenant_id, 'INPATIENT', m.fetch_batch_id,
  m.his_inpatient_charge_id, NULL, m.his_inpatient_charge_id_tf, NULL,
  m.patient_id, m.patient_name, m.inpatient_no, NULL,
  m.dept_code, m.dept_name, NULL, NULL,
  m.doctor_id, m.doctor_name, m.charge_item_id, m.item_name, m.spec_model, m.batch_no, m.expire_date,
  CASE WHEN m.use_date IS NULL OR trim(ifnull(m.use_date,'')) = '' THEN NULL ELSE str_to_date(left(trim(m.use_date), 19), '%Y-%m-%d %H:%i:%s') END,
  left(replace(trim(ifnull(cast(m.charge_date as char), '')), '/', '-'), 64),
  coalesce(
    str_to_date(left(replace(trim(ifnull(cast(m.charge_date as char), '')), '/', '-'), 19), '%Y-%m-%d %H:%i:%s'),
    str_to_date(concat(left(replace(trim(ifnull(cast(m.charge_date as char), '')), '/', '-'), 10), ' 00:00:00'), '%Y-%m-%d %H:%i:%s'),
    cast(m.charge_date as datetime)
  ),
  m.quantity, m.unit_price, m.total_amount, m.charge_operator, NULL, NULL, m.remark, m.row_fingerprint,
  m.process_status, m.process_type, m.process_time, m.process_by, m.create_by, m.create_time, m.update_by, m.update_time
FROM his_inpatient_charge_mirror m
WHERE NOT EXISTS (SELECT 1 FROM his_patient_charge_mirror_unified u WHERE u.id = m.id);

INSERT INTO his_patient_charge_mirror_unified (
  id, tenant_id, visit_kind, fetch_batch_id,
  his_inpatient_charge_id, his_outpatient_charge_id, his_inpatient_charge_id_tf, his_outpatient_charge_id_tf,
  patient_id, patient_name, inpatient_no, outpatient_no,
  dept_code, dept_name, clinic_code, clinic_name,
  doctor_id, doctor_name, charge_item_id, item_name, spec_model, batch_no, expire_date,
  use_date, charge_date_display, charge_at,
  quantity, unit_price, total_amount, charge_operator, payment_type, receipt_no, remark, row_fingerprint,
  process_status, process_type, process_time, process_by, create_by, create_time, update_by, update_time
)
SELECT
  m.id, m.tenant_id, 'OUTPATIENT', m.fetch_batch_id,
  NULL, m.his_outpatient_charge_id, NULL, m.his_outpatient_charge_id_tf,
  m.patient_id, m.patient_name, NULL, m.outpatient_no,
  NULL, NULL, m.clinic_code, m.clinic_name,
  m.doctor_id, m.doctor_name, m.charge_item_id, m.item_name, m.spec_model, m.batch_no, m.expire_date,
  NULL,
  left(replace(trim(ifnull(cast(m.charge_date as char), '')), '/', '-'), 64),
  coalesce(
    str_to_date(left(replace(trim(ifnull(cast(m.charge_date as char), '')), '/', '-'), 19), '%Y-%m-%d %H:%i:%s'),
    str_to_date(concat(left(replace(trim(ifnull(cast(m.charge_date as char), '')), '/', '-'), 10), ' 00:00:00'), '%Y-%m-%d %H:%i:%s')
  ),
  m.quantity, m.unit_price, m.total_amount, m.charge_operator, m.payment_type, m.receipt_no, m.remark, m.row_fingerprint,
  m.process_status, m.process_type, m.process_time, m.process_by, m.create_by, m.create_time, m.update_by, m.update_time
FROM his_outpatient_charge_mirror m
WHERE NOT EXISTS (SELECT 1 FROM his_patient_charge_mirror_unified u WHERE u.id = m.id);

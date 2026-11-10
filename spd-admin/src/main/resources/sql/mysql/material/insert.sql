-- ========== 耗材模块 初始化数据（非菜单）==========
-- 建议在 table.sql、menu.sql 之后按需执行。
-- 数据备份：平台维度默认一行（tenant_id 空串）；定时任务 job 在首次「保存配置」时由后端创建。

INSERT INTO sys_data_backup_config (
  tenant_id, backup_path, mysqldump_path, backup_time, enabled, job_id,
  retain_days, last_backup_time, last_backup_status, last_backup_message,
  create_by, create_time, remark
)
SELECT
  '',
  'D:/backup',
  '',
  '02:00',
  '0',
  NULL,
  7,
  NULL,
  NULL,
  NULL,
  'admin',
  NOW(),
  '默认数据备份配置（平台）；启用前请填写备份目录并保存'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sys_data_backup_config WHERE tenant_id = '');
/

-- 默认平台数据备份对应的定时任务（与 sys_data_backup_config.id=1 对应）
INSERT INTO sys_job (
  job_id, job_name, job_group, invoke_target, cron_expression,
  misfire_policy, concurrent, status, create_by, create_time,
  update_by, update_time, remark
)
SELECT
  5,
  '数据备份-platform',
  'DEFAULT',
  'dataBackupTask.execute(1L)',
  '0 0 11 * * ?',
  '3',
  '1',
  '1',
  'admin',
  NOW(),
  '',
  NULL,
  '数据备份调度'
WHERE NOT EXISTS (SELECT 1 FROM sys_job WHERE job_id = 5);
/

-- 按单据类型打印每页行数（与 sys_print_doc_rows 表、spd-ui docKind 一致；可重复执行）
INSERT IGNORE INTO `sys_print_doc_rows` (`doc_kind`, `rows_per_page`, `create_by`, `remark`) VALUES
('INBOUND', 6, 'system', '入库'),
('OUTBOUND', 6, 'system', '出库'),
('REFUND_DEPOT', 6, 'system', '退库'),
('REFUND_GOODS', 6, 'system', '退货');
/

-- ========== WH-X-007：进销存明细单据类型字典（与 selectListPurInventory 一致，可重复执行）==========
-- 501=调拨；502=盘点；701=期初导入；801=报损。手工脚本同源：docs/sql/WH-X-007-bill-type-dict-jxc-detail.sql
INSERT INTO sys_dict_type (dict_name, dict_type, status, create_by, create_time, remark)
SELECT '单据类型', 'bill_type', '0', 'admin', NOW(), '出入库/调拨/盘点/报损等业务单据类型'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sys_dict_type WHERE dict_type = 'bill_type');
/
UPDATE sys_dict_data
SET dict_label = '调拨',
    remark = 'stk_io_bill.bill_type=501 调拨单'
WHERE dict_type = 'bill_type' AND dict_value = '501';
/
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark)
SELECT 5, '调拨', '501', 'bill_type', '', 'primary', 'N', '0', 'admin', NOW(), 'stk_io_bill 调拨'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'bill_type' AND dict_value = '501');
/
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark)
SELECT 6, '盘点', '502', 'bill_type', '', 'warning', 'N', '0', 'admin', NOW(), '仓库盘点→明细类型502'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'bill_type' AND dict_value = '502');
/
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark)
SELECT 7, '期初导入', '701', 'bill_type', '', 'info', 'N', '0', 'admin', NOW(), 'stk_initial_import'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'bill_type' AND dict_value = '701');
/
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark)
SELECT 8, '报损', '801', 'bill_type', '', 'danger', 'N', '0', 'admin', NOW(), 'stk_scrap_bill'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM sys_dict_data WHERE dict_type = 'bill_type' AND dict_value = '801');
/

-- ========== HV-X-003：已有 his_charge_item_id 的耗材回填「当前生效」对照履历（可重复执行）==========
-- 表结构：material/table.sql → fd_material_his_charge_bind；手工同源：docs/sql/HV-X-003-material-his-charge-bind.sql
INSERT INTO fd_material_his_charge_bind (
  id, tenant_id, material_id, charge_item_id,
  effective_from, effective_to, bind_by, unbind_by, create_time, update_time
)
SELECT
  REPLACE(UUID(), '-', ''),
  m.tenant_id,
  m.id,
  TRIM(m.his_charge_item_id),
  COALESCE(m.update_time, m.create_time, NOW()),
  NULL,
  COALESCE(NULLIF(TRIM(m.update_by), ''), NULLIF(TRIM(m.create_by), ''), 'system'),
  NULL,
  NOW(),
  NOW()
FROM fd_material m
WHERE (m.del_flag = 0 OR m.del_flag IS NULL)
  AND m.his_charge_item_id IS NOT NULL
  AND TRIM(m.his_charge_item_id) <> ''
  AND NOT EXISTS (
    SELECT 1 FROM fd_material_his_charge_bind b
    WHERE b.tenant_id = m.tenant_id
      AND b.material_id = m.id
      AND b.effective_to IS NULL
  );
/

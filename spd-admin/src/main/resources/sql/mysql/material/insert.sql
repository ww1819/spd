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

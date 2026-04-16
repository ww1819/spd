-- ========== 设备模块 初始化数据（非菜单）==========
-- 建议在 table.sql 之后按需执行；与耗材 material/insert.sql 中 sys_data_backup_config 语义一致。

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

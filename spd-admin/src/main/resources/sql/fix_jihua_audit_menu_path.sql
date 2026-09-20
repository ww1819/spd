-- 采购计划审核(1264) path 误为绝对路径 /caigou/jihuaAudit，
-- 挂到采购管理二级目录后前端拼路由会 404。改为相对 path，与采购计划(jihua)一致。

UPDATE sys_menu
SET path = 'jihuaAudit',
    component = 'caigou/jihua/audit/index',
    update_by = '1',
    update_time = NOW()
WHERE menu_id = 1264
  AND (path = '/caigou/jihuaAudit' OR path LIKE '/%');

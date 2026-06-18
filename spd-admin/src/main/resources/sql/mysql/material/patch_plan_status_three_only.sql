-- 采购计划状态精简为：0未提交 1待审核 2已审核（移除已执行、已取消）
--
-- 【DBeaver 执行说明】
-- 1) 必须先选中数据库！连接旁不能是 <N/A>，否则报 1046 No database selected
--    方式 A：左侧库列表双击 aspt（或你们生产库名）
--    方式 B：先执行下面 USE（把 aspt 改成实际库名）
-- 2) 多条语句：用 Alt+X「执行 SQL 脚本」；或一条一条 Ctrl+Enter（每次只选一条）
-- 3) 执行后：清字典缓存或重新登录前端
--
-- 生产/开发库名一般为 aspt（以 application-*.yml 里 jdbc url 为准）
USE aspt;

UPDATE purchase_plan SET plan_status = '2' WHERE plan_status IN ('3', '4');

DELETE FROM sys_dict_data WHERE dict_type = 'plan_status' AND dict_value IN ('3', '4');

UPDATE sys_dict_type
SET remark = '计划状态（0未提交 1待审核 2已审核）'
WHERE dict_type = 'plan_status';

UPDATE sys_dict_data SET remark = '采购计划状态：未提交' WHERE dict_type = 'plan_status' AND dict_value = '0';

UPDATE sys_dict_data SET remark = '采购计划状态：待审核' WHERE dict_type = 'plan_status' AND dict_value = '1';

UPDATE sys_dict_data SET remark = '采购计划状态：已审核' WHERE dict_type = 'plan_status' AND dict_value = '2';

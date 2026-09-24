-- 用户/工作组首页设置授权字段（与 column.sql 同源；SqlInitRunner 重启会自动加列）
-- home_page_keys：simple,full,purchase,warehouse,department；空/NULL=未单独授权，前端默认「默认」首页
CALL add_table_column('sys_user', 'home_page_keys', 'varchar(64)', 'home page auth keys simple,full,purchase,warehouse,department; empty=default simple', NULL);
/
CALL add_table_column('sys_post', 'home_page_keys', 'varchar(64)', 'home page auth keys simple,full,purchase,warehouse,department; empty=default simple', NULL);
/

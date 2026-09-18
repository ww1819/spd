-- 公共：仅当「高值追溯」目录(3960)仍为空时才删除（历史修复脚本）
-- 当前正式结构见 reorg_gz_trace_dir_with_confirm_public.sql（3960 下挂 3850/1238），勿误删。

DELETE FROM sys_role_menu
WHERE menu_id = 3960
  AND NOT EXISTS (
    SELECT 1 FROM sys_menu c WHERE c.parent_id = 3960 AND c.menu_type IN ('M', 'C')
  );

DELETE FROM sys_user_menu
WHERE menu_id = 3960
  AND NOT EXISTS (
    SELECT 1 FROM sys_menu c WHERE c.parent_id = 3960 AND c.menu_type IN ('M', 'C')
  );

DELETE FROM sys_post_menu
WHERE menu_id = 3960
  AND NOT EXISTS (
    SELECT 1 FROM sys_menu c WHERE c.parent_id = 3960 AND c.menu_type IN ('M', 'C')
  );

DELETE FROM hc_customer_menu
WHERE menu_id = 3960
  AND NOT EXISTS (
    SELECT 1 FROM sys_menu c WHERE c.parent_id = 3960 AND c.menu_type IN ('M', 'C')
  );

DELETE FROM sys_menu
WHERE menu_id = 3960
  AND menu_type = 'M'
  AND path = 'gzTraceMgmt'
  AND NOT EXISTS (
    SELECT 1 FROM sys_menu c WHERE c.parent_id = 3960 AND c.menu_type IN ('M', 'C')
  );

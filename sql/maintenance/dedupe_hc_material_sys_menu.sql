-- =============================================================================
-- 耗材 sys_menu：合并重复菜单/按钮并删除冗余行
-- =============================================================================
-- 存储过程正式定义：spd-admin/src/main/resources/sql/mysql/material/procedure.sql（sp_hc_merge_sys_menu）
-- 本文件便于单独执行/维护；与 procedure.sql 内容保持一致。
-- =============================================================================
-- 用途：将「删除侧」menu_id 的关联迁移到「保留侧」keep_id，再删除删除侧记录。
-- 依赖表：sys_role_menu、sys_user_menu、hc_customer_menu、sys_post_menu、
--         hc_user_permission_menu、hc_customer_menu_status_log、hc_customer_menu_period_log
--
-- 执行前请备份：sys_menu 及上述关联表。
-- 若某表不存在，请将存储过程中对应段落注释后再执行。
--
-- 典型场景：inventory_hc_material_sys_menu.sql 扫描出重复 perms / 同 component 多 C 菜单后，
-- 人工确认 keep / drop，再 CALL 本过程。
-- 与导出 CSV 配套的批量修复示例见：fix_sys_menu_dupes_from_export_20260321.sql
-- =============================================================================

DELIMITER //

DROP PROCEDURE IF EXISTS sp_hc_merge_sys_menu //

CREATE PROCEDURE sp_hc_merge_sys_menu(IN p_keep BIGINT, IN p_drop BIGINT)
proc_label: BEGIN
  DECLARE v_child_cnt INT DEFAULT 0;

  IF p_keep IS NULL OR p_drop IS NULL OR p_keep = p_drop THEN
    LEAVE proc_label;
  END IF;

  IF NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = p_keep) THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'sp_hc_merge_sys_menu: keep menu_id 不存在';
  END IF;
  IF NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = p_drop) THEN
    LEAVE proc_label;
  END IF;

  -- 1) 角色菜单
  INSERT IGNORE INTO sys_role_menu (role_id, menu_id)
  SELECT role_id, p_keep FROM sys_role_menu WHERE menu_id = p_drop;
  DELETE FROM sys_role_menu WHERE menu_id = p_drop;

  -- 2) 用户菜单（耗材数字菜单）
  INSERT IGNORE INTO sys_user_menu (user_id, menu_id, tenant_id)
  SELECT user_id, p_keep, tenant_id FROM sys_user_menu WHERE menu_id = p_drop;
  DELETE FROM sys_user_menu WHERE menu_id = p_drop;

  -- 3) 客户菜单授权
  INSERT IGNORE INTO hc_customer_menu (tenant_id, menu_id, status, is_enabled, create_by, create_time)
  SELECT tenant_id, p_keep, status, is_enabled, create_by, create_time
  FROM hc_customer_menu WHERE menu_id = p_drop;
  DELETE FROM hc_customer_menu WHERE menu_id = p_drop;

  -- 4) 耗材工作组菜单
  INSERT IGNORE INTO sys_post_menu (post_id, menu_id, tenant_id)
  SELECT post_id, p_keep, tenant_id FROM sys_post_menu WHERE menu_id = p_drop;
  DELETE FROM sys_post_menu WHERE menu_id = p_drop;

  -- 5) hc_user_permission_menu：先删冲突再改 menu_id
  DELETE dup FROM hc_user_permission_menu dup
  INNER JOIN hc_user_permission_menu k
    ON dup.user_id = k.user_id AND dup.tenant_id = k.tenant_id AND k.menu_id = p_keep
  WHERE dup.menu_id = p_drop;
  UPDATE hc_user_permission_menu SET menu_id = p_keep WHERE menu_id = p_drop;

  -- 6) 客户菜单启停日志（表不存在则注释本段）
  UPDATE hc_customer_menu_status_log SET menu_id = p_keep WHERE menu_id = p_drop;
  UPDATE hc_customer_menu_period_log SET menu_id = p_keep WHERE menu_id = p_drop;

  -- 7) 子菜单改挂到保留节点下
  SELECT COUNT(*) INTO v_child_cnt FROM sys_menu WHERE parent_id = p_drop;
  IF v_child_cnt > 0 THEN
    UPDATE sys_menu SET parent_id = p_keep WHERE parent_id = p_drop;
  END IF;

  -- 8) 删除被合并的菜单行
  DELETE FROM sys_menu WHERE menu_id = p_drop;
END //

DELIMITER ;

-- =============================================================================
-- 使用示例（将 ID 换成实际保留/删除的 menu_id）
-- =============================================================================
-- CALL sp_hc_merge_sys_menu(1092, 2230);
-- =============================================================================

-- =============================================================================
-- 可选：仅「无子菜单」且「重复 perms」的叶子节点批量合并（慎用，默认注释）
-- =============================================================================
/*
DELIMITER //
CREATE PROCEDURE sp_hc_dedupe_menu_by_perms_leaf_only()
BEGIN
  DECLARE done INT DEFAULT 0;
  DECLARE v_keep BIGINT;
  DECLARE v_drop BIGINT;
  DECLARE cur CURSOR FOR
    SELECT t.keep_id, m.menu_id
    FROM (
      SELECT perms, MIN(menu_id) AS keep_id
      FROM sys_menu
      WHERE perms IS NOT NULL AND TRIM(perms) <> ''
        AND IFNULL(status, '0') = '0'
      GROUP BY perms
      HAVING COUNT(*) > 1
    ) t
    JOIN sys_menu m ON m.perms = t.perms AND m.menu_id > t.keep_id
    WHERE IFNULL(m.status, '0') = '0'
      AND NOT EXISTS (SELECT 1 FROM sys_menu c WHERE c.parent_id = m.menu_id);
  DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = 1;

  OPEN cur;
  read_loop: LOOP
    FETCH cur INTO v_keep, v_drop;
    IF done = 1 THEN
      LEAVE read_loop;
    END IF;
    CALL sp_hc_merge_sys_menu(v_keep, v_drop);
  END LOOP;
  CLOSE cur;
END //
DELIMITER ;

-- CALL sp_hc_dedupe_menu_by_perms_leaf_only();
-- DROP PROCEDURE IF EXISTS sp_hc_dedupe_menu_by_perms_leaf_only;
*/

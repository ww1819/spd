-- =============================================================================
-- 期初库存导入明细 stk_initial_import_entry：存量库一次性升级
-- 1) third_party_detail_id → his_id（迁移后删除旧列）
-- 2) unit_price / qty / amt 调整为 decimal(18,6)
-- 新环境若已按 material/table.sql 建表，可跳过本脚本（或执行时各步骤会安全跳过）
-- =============================================================================

DELIMITER $$

DROP PROCEDURE IF EXISTS `upgrade_stk_initial_import_entry_hisid_decimal`$$

CREATE PROCEDURE `upgrade_stk_initial_import_entry_hisid_decimal`()
BEGIN
    DECLARE v_has_old INT DEFAULT 0;
    DECLARE v_has_new INT DEFAULT 0;

    SELECT COUNT(*) INTO v_has_old
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'stk_initial_import_entry'
      AND COLUMN_NAME = 'third_party_detail_id';

    SELECT COUNT(*) INTO v_has_new
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'stk_initial_import_entry'
      AND COLUMN_NAME = 'his_id';

    IF v_has_new = 0 THEN
        ALTER TABLE `stk_initial_import_entry`
            ADD COLUMN `his_id` varchar(128) DEFAULT NULL COMMENT '第三方/HIS系统库存明细ID（对应导入列，业务主键追溯）' AFTER `supplier_id`;
    END IF;

    IF v_has_old > 0 THEN
        UPDATE `stk_initial_import_entry`
        SET `his_id` = `third_party_detail_id`
        WHERE (`his_id` IS NULL OR TRIM(`his_id`) = '')
          AND `third_party_detail_id` IS NOT NULL
          AND TRIM(`third_party_detail_id`) <> '';

        ALTER TABLE `stk_initial_import_entry` DROP COLUMN `third_party_detail_id`;
    END IF;

    -- 单价、数量、金额六位小数（与 table.sql 一致）
    ALTER TABLE `stk_initial_import_entry`
        MODIFY COLUMN `unit_price` decimal(18,6) DEFAULT NULL COMMENT '单价（六位小数）',
        MODIFY COLUMN `qty` decimal(18,6) NOT NULL DEFAULT 0.000000 COMMENT '数量（六位小数）',
        MODIFY COLUMN `amt` decimal(18,6) DEFAULT NULL COMMENT '金额（六位小数）';
END$$

DELIMITER ;

CALL `upgrade_stk_initial_import_entry_hisid_decimal`();

DROP PROCEDURE IF EXISTS `upgrade_stk_initial_import_entry_hisid_decimal`;

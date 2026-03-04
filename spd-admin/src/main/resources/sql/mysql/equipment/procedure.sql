-- 若 sys_user 上不存在「用户名+客户id」唯一约束则创建（同一客户下用户名唯一，平台用户 customer_id 为空）
-- 按「/」分段执行

DROP PROCEDURE IF EXISTS add_sys_user_name_customer_unique_if_not_exists;
/
CREATE PROCEDURE add_sys_user_name_customer_unique_if_not_exists()
BEGIN
  DECLARE v_exists INT DEFAULT 0;

  SELECT COUNT(*) INTO v_exists
  FROM information_schema.TABLE_CONSTRAINTS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'sys_user'
    AND CONSTRAINT_NAME = 'uk_sys_user_name_customer'
    AND CONSTRAINT_TYPE = 'UNIQUE';

  IF v_exists = 0 THEN
    ALTER TABLE sys_user ADD UNIQUE KEY uk_sys_user_name_customer (user_name, customer_id);
  END IF;
END;
/
CALL add_sys_user_name_customer_unique_if_not_exists();
/
DROP PROCEDURE IF EXISTS add_sys_user_name_customer_unique_if_not_exists;
/

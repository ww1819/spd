-- equipment/column.sql：设备/共用表增量字段（按「/」分段）
-- 约定：新列在此 ADD；并同步补进 equipment/table.sql 全量 CREATE（见约定包 §2.3 双轨）。
-- ADD 若列已存在可跳过报错。

-- SYS-F-002：客户金额显示小数位（已生效字段；登录下发）
ALTER TABLE `sb_customer`
  ADD COLUMN `price_decimal_places` tinyint NOT NULL DEFAULT 3 COMMENT '单价显示小数位(0-6，已生效)' AFTER `tenant_key`;
/
ALTER TABLE `sb_customer`
  ADD COLUMN `amount_decimal_places` tinyint NOT NULL DEFAULT 3 COMMENT '金额显示小数位(0-6，已生效)' AFTER `price_decimal_places`;
/
ALTER TABLE `sb_customer`
  ADD COLUMN `money_round_mode` varchar(16) NOT NULL DEFAULT 'HALF_UP' COMMENT '金额舍入：HALF_UP/HALF_EVEN/DOWN' AFTER `amount_decimal_places`;
/

-- 审核表整表见 docs/sql/SYS-F-002-customer-money-decimal.sql 或本目录 table.sql（CREATE IF NOT EXISTS）
-- 业务表金额精度升 (18,6) 见 docs/sql/SYS-F-002-money-precision-18-6.sql（勿并入本文件）
/

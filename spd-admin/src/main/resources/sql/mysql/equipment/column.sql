-- equipment/column.sql：设备侧增量字段（按「/」分段）
-- 注意：SPD 启动 SqlInitRunner 当前只执行 material/*，不会自动跑本文件。
-- 客户金额小数位等与耗材共用的列，已写入 material/column.sql（add_table_column），重启即可补列。
-- 本文件保留设备侧专用增量；与 material 双轨时以 material 为准补共用表。

-- SYS-F-002（设备侧镜像，可选；耗材环境请以 material/column.sql 为准）
ALTER TABLE `sb_customer`
  ADD COLUMN `price_decimal_places` tinyint NOT NULL DEFAULT 3 COMMENT '单价显示小数位(0-6，已生效)' AFTER `tenant_key`;
/
ALTER TABLE `sb_customer`
  ADD COLUMN `amount_decimal_places` tinyint NOT NULL DEFAULT 3 COMMENT '金额显示小数位(0-6，已生效)' AFTER `price_decimal_places`;
/
ALTER TABLE `sb_customer`
  ADD COLUMN `money_round_mode` varchar(16) NOT NULL DEFAULT 'HALF_UP' COMMENT '金额舍入：HALF_UP/HALF_EVEN/DOWN' AFTER `amount_decimal_places`;
/

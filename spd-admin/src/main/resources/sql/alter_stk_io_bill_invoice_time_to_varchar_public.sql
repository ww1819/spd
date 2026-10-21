-- 发票时间由 date 改为文本，允许非严格日期格式录入
-- 手工入口；更新系统重启时由 SqlInitRunner 自动执行 material/column.sql 同段（幂等 MODIFY）
ALTER TABLE `stk_io_bill`
  MODIFY COLUMN `invoice_time` varchar(64) DEFAULT NULL COMMENT '发票时间';

-- 流水表增加 lx 类型字段：RK入库/CK出库/TH退货/TK退库

ALTER TABLE t_hc_ck_flow ADD COLUMN lx varchar(10) DEFAULT NULL COMMENT '类型：RK入库/CK出库/TH退货/TK退库';
ALTER TABLE t_hc_ks_flow ADD COLUMN lx varchar(10) DEFAULT NULL COMMENT '类型：CK出库';

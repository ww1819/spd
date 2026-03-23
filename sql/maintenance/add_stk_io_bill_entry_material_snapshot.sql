-- 出入库明细 stk_io_bill_entry：产品名称/规格/型号/生产厂家ID 快照（与当时耗材档案一致，便于追溯与打印）
-- MySQL 5.7+ / 8.0+

ALTER TABLE stk_io_bill_entry
  ADD COLUMN material_name varchar(256) DEFAULT NULL COMMENT '产品名称（快照）' AFTER material_id,
  ADD COLUMN material_speci varchar(256) DEFAULT NULL COMMENT '规格（快照）' AFTER material_name,
  ADD COLUMN material_model varchar(256) DEFAULT NULL COMMENT '型号（快照）' AFTER material_speci,
  ADD COLUMN material_factory_id bigint DEFAULT NULL COMMENT '生产厂家ID（快照，fd_factory.factory_id）' AFTER material_model;

-- 仓库库存 stk_inventory、科室库存 stk_dep_inventory：产品名称/规格/型号/生产厂家ID 快照
-- 与耗材档案一致时可冗余追溯；执行前请确认表名与库名

ALTER TABLE stk_inventory
  ADD COLUMN material_name varchar(256) DEFAULT NULL COMMENT '产品名称（快照）' AFTER material_id,
  ADD COLUMN material_speci varchar(256) DEFAULT NULL COMMENT '规格（快照）' AFTER material_name,
  ADD COLUMN material_model varchar(256) DEFAULT NULL COMMENT '型号（快照）' AFTER material_speci,
  ADD COLUMN material_factory_id bigint DEFAULT NULL COMMENT '生产厂家ID（快照，fd_factory.factory_id）' AFTER material_model;

ALTER TABLE stk_dep_inventory
  ADD COLUMN material_name varchar(256) DEFAULT NULL COMMENT '产品名称（快照）' AFTER material_id,
  ADD COLUMN material_speci varchar(256) DEFAULT NULL COMMENT '规格（快照）' AFTER material_name,
  ADD COLUMN material_model varchar(256) DEFAULT NULL COMMENT '型号（快照）' AFTER material_speci,
  ADD COLUMN material_factory_id bigint DEFAULT NULL COMMENT '生产厂家ID（快照，fd_factory.factory_id）' AFTER material_model;

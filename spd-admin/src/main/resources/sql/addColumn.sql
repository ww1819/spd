ALTER TABLE spd.gz_depot_inventory ADD master_barcode varchar(200) NULL COMMENT '主条码';
ALTER TABLE spd.gz_depot_inventory ADD secondary_barcode varchar(200) NULL COMMENT '辅条码';

ALTER TABLE spd.gz_dep_inventory ADD master_barcode varchar(200) NULL COMMENT '主条码';
ALTER TABLE spd.gz_dep_inventory ADD secondary_barcode varchar(200) NULL COMMENT '辅条码';

ALTER TABLE spd.gz_order_entry ADD master_barcode varchar(200) NULL COMMENT '主条码';
ALTER TABLE spd.gz_order_entry ADD secondary_barcode varchar(200) NULL COMMENT '辅条码';

ALTER TABLE spd.gz_shipment_entry ADD master_barcode varchar(200) NULL COMMENT '主条码';
ALTER TABLE spd.gz_shipment_entry ADD secondary_barcode varchar(200) NULL COMMENT '辅条码';

ALTER TABLE spd.gz_refund_stock ADD master_barcode varchar(200) NULL COMMENT '主条码';
ALTER TABLE spd.gz_refund_stock ADD secondary_barcode varchar(200) NULL COMMENT '辅条码';

ALTER TABLE spd.gz_refund_goods_entry ADD master_barcode varchar(200) NULL COMMENT '主条码';
ALTER TABLE spd.gz_refund_goods_entry ADD secondary_barcode varchar(200) NULL COMMENT '辅条码';

ALTER TABLE spd.gz_depot_inventory ADD in_hospital_code varchar(200) NULL COMMENT '院内码';
ALTER TABLE spd.gz_dep_inventory ADD in_hospital_code varchar(200) NULL COMMENT '院内码';




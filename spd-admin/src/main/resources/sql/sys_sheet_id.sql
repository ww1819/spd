create table if not exists spd.sys_sheet_id
(
    id            bigint auto_increment
    primary key,
    business_type varchar(200) null comment '业务类型',
    sheet_type    varchar(200) null comment '单据类型',
    stock_id      bigint       null comment '仓库id',
    dept_id       bigint       null comment '科室id',
    sheet_id      bigint       null comment '种子id'
    )
    comment '系统相关单据种子表';

create table if not exists spd.gz_order_entry_inhospitalcode_list
(
    id                bigint auto_increment
    primary key,
    parent_id         bigint         null comment '主id',
    code              varchar(200)   null comment '单据号',
    detail_id         bigint         null comment '单据明细id',
    material_id       bigint         null comment '产品档案id',
    price             decimal(18, 6) null comment '单价',
    qty               decimal(18, 6) null comment '数量',
    batch_no          varchar(100)   null comment '批次号',
    batch_number      varchar(100)   null comment '批号',
    master_barcode    varchar(200)   null comment '主条码',
    secondary_barcode varchar(200)   null comment '辅条码',
    end_date          datetime       null comment '有效期',
    in_hospital_code  varchar(200)   null comment '院内码',
    create_date       datetime       null comment '创建时间'
    )
    comment '高值耗材备货单明细院内码列表';


-- aspt.view_stock_all_detail_jxc source

create or replace
algorithm = UNDEFINED view `view_stock_all_detail_jxc` as
select
    `sib`.`id` as `mid`,
    `sibe`.`id` as `mxId`,
    `sib`.`bill_no` as `bill_no`,
    `sib`.`warehouse_id` as `warehouse_id`,
    `fw`.`code` as `warehouse_code`,
    `fw`.`name` as `warehouse_name`,
    `sib`.`audit_date` as `audit_date`,
    `sib`.`bill_type` as `bill_type`,
    (case
         when (`sib`.`bill_type` in (101, 301)) then `sib`.`suppler_id`
         when (`sib`.`bill_type` in (201, 401)) then `sibe`.`suppler_id`
        end) as `suppler_id`,
    (case
         when (`sib`.`bill_type` in (101, 301)) then `fs`.`name`
         when (`sib`.`bill_type` in (201, 401)) then `fs2`.`name`
        end) as `suppler_name`,
    (case
         when (`sib`.`bill_type` in (101, 301)) then 'RK'
         when (`sib`.`bill_type` in (201, 401)) then 'CK'
        end) as `io_type`,
    `sibe`.`unit_price` as `unit_price`,
    (case
         when (`sib`.`bill_type` in (101, 201)) then `sibe`.`qty`
         when (`sib`.`bill_type` in (301, 401)) then -(`sibe`.`qty`)
        end) as `io_qty`,
    (case
         when (`sib`.`bill_type` in (101, 201)) then (`sibe`.`qty` * `sibe`.`unit_price`)
         when (`sib`.`bill_type` in (301, 401)) then (-(`sibe`.`qty`) * `sibe`.`unit_price`)
        end) as `io_amt`,
    (case
         when (`sib`.`bill_type` in (101, 401)) then `sibe`.`qty`
         when (`sib`.`bill_type` in (201, 301)) then -(`sibe`.`qty`)
        end) as `jc_qty`,
    (case
         when (`sib`.`bill_type` in (101, 401)) then (`sibe`.`qty` * `sibe`.`unit_price`)
         when (`sib`.`bill_type` in (201, 301)) then (-(`sibe`.`qty`) * `sibe`.`unit_price`)
        end) as `jc_amt`,
    `sibe`.`material_id` as `material_id`,
    `fm`.`code` as `material_code`,
    `fm`.`name` as `material_name`,
    `fm`.`speci` as `speci`,
    `fm`.`model` as `model`,
    `fm`.`unit_id` as `unit_id`,
    `fu`.`unit_name` as `unit_name`,
    `fm`.`factory_id` as `factory_id`,
    `ff`.`factory_code` as `factory_code`,
    `ff`.`factory_name` as `factory_name`,
    `sibe`.`batch_number` as `batch_number`,
    `sibe`.`batch_no` as `batch_no`,
    `sibe`.`begin_time` as `begin_time`,
    `sibe`.`end_time` as `end_time`,
    `fm`.`storeroom_id` as `storeroom_id`,
    `fwc`.`warehouse_category_code` as `warehouse_category_code`,
    `fwc`.`warehouse_category_name` as `warehouse_category_name`,
    `fm`.`finance_category_id` as `finance_category_id`,
    `ffc`.`finance_category_code` as `finance_category_code`,
    `ffc`.`finance_category_name` as `finance_category_name`
from
    (((((((((`stk_io_bill` `sib`
        left join `stk_io_bill_entry` `sibe` on
        ((`sib`.`id` = `sibe`.`paren_id`)))
        left join `fd_warehouse` `fw` on
        ((`sib`.`warehouse_id` = `fw`.`id`)))
        left join `fd_material` `fm` on
        ((`sibe`.`material_id` = `fm`.`id`)))
        left join `fd_warehouse_category` `fwc` on
        ((`fm`.`storeroom_id` = `fwc`.`warehouse_category_id`)))
        left join `fd_finance_category` `ffc` on
        ((`fm`.`finance_category_id` = `ffc`.`finance_category_id`)))
        left join `fd_supplier` `fs` on
        ((`sib`.`suppler_id` = `fs`.`id`)))
        left join `fd_supplier` `fs2` on
        ((`sibe`.`suppler_id` = `fs2`.`id`)))
        left join `fd_unit` `fu` on
        ((`fm`.`unit_id` = `fu`.`unit_id`)))
        left join `fd_factory` `ff` on
        ((`fm`.`factory_id` = `ff`.`factory_id`)))
where
    ((1 = 1)
        and (`sib`.`del_flag` = 0)
        and (`sibe`.`del_flag` = 0)
        and (`sib`.`bill_status` = 2)
        and (`sib`.`bill_type` in (101, 201, 301, 401)))
union all
select
    `sib`.`id` as `id`,
    `sibe`.`id` as `id`,
    `sib`.`bill_no` as `bill_no`,
    `sib`.`warehouse_id` as `warehouse_id`,
    `fw`.`code` as `code`,
    `fw`.`name` as `name`,
    `sib`.`audit_date` as `audit_date`,
    `sib`.`bill_type` as `bill_type`,
    `sibe`.`suppler_id` as `suppler_id`,
    `fs`.`name` as `name`,
    'DBC' as `DBC`,
    `sibe`.`unit_price` as `unit_price`,
    `sibe`.`qty` as `qty`,
    (`sibe`.`qty` * `sibe`.`unit_price`) as `sibe.qty * sibe.unit_price`,
    -(`sibe`.`qty`) as `-sibe.qty`,
    (-(`sibe`.`qty`) * `sibe`.`unit_price`) as `-sibe.qty * sibe.unit_price`,
    `sibe`.`material_id` as `material_id`,
    `fm`.`code` as `code`,
    `fm`.`name` as `name`,
    `fm`.`speci` as `speci`,
    `fm`.`model` as `model`,
    `fm`.`unit_id` as `unit_id`,
    `fu`.`unit_name` as `unit_name`,
    `fm`.`factory_id` as `factory_id`,
    `ff`.`factory_code` as `factory_code`,
    `ff`.`factory_name` as `factory_name`,
    `sibe`.`batch_number` as `batch_number`,
    `sibe`.`batch_no` as `batch_no`,
    `sibe`.`begin_time` as `begin_time`,
    `sibe`.`end_time` as `end_time`,
    `fm`.`storeroom_id` as `storeroom_id`,
    `fwc`.`warehouse_category_code` as `warehouse_category_code`,
    `fwc`.`warehouse_category_name` as `warehouse_category_name`,
    `fm`.`finance_category_id` as `finance_category_id`,
    `ffc`.`finance_category_code` as `finance_category_code`,
    `ffc`.`finance_category_name` as `finance_category_name`
from
    ((((((((`stk_io_bill` `sib`
        left join `stk_io_bill_entry` `sibe` on
        ((`sib`.`id` = `sibe`.`paren_id`)))
        left join `fd_warehouse` `fw` on
        ((`sib`.`warehouse_id` = `fw`.`id`)))
        left join `fd_material` `fm` on
        ((`sibe`.`material_id` = `fm`.`id`)))
        left join `fd_warehouse_category` `fwc` on
        ((`fm`.`storeroom_id` = `fwc`.`warehouse_category_id`)))
        left join `fd_finance_category` `ffc` on
        ((`fm`.`finance_category_id` = `ffc`.`finance_category_id`)))
        left join `fd_supplier` `fs` on
        ((`sibe`.`suppler_id` = `fs`.`id`)))
        left join `fd_unit` `fu` on
        ((`fm`.`unit_id` = `fu`.`unit_id`)))
        left join `fd_factory` `ff` on
        ((`fm`.`factory_id` = `ff`.`factory_id`)))
where
    ((1 = 1)
        and (`sib`.`del_flag` = 0)
        and (`sibe`.`del_flag` = 0)
        and (`sib`.`bill_status` = 2)
        and (`sib`.`bill_type` = 501)
        and (left(`sib`.`bill_no`, 2) = 'DB'))
union all
select
    `sib`.`id` as `id`,
    `sibe`.`id` as `id`,
    `sib`.`bill_no` as `bill_no`,
    `sib`.`department_id` as `department_id`,
    `fw`.`code` as `code`,
    `fw`.`name` as `name`,
    `sib`.`audit_date` as `audit_date`,
    `sib`.`bill_type` as `bill_type`,
    `sibe`.`suppler_id` as `suppler_id`,
    `fs`.`name` as `name`,
    'DBR' as `DBR`,
    `sibe`.`unit_price` as `unit_price`,
    `sibe`.`qty` as `qty`,
    (`sibe`.`qty` * `sibe`.`unit_price`) as `sibe.qty * sibe.unit_price`,
    `sibe`.`qty` as `qty`,
    (`sibe`.`qty` * `sibe`.`unit_price`) as `sibe.qty * sibe.unit_price`,
    `sibe`.`material_id` as `material_id`,
    `fm`.`code` as `code`,
    `fm`.`name` as `name`,
    `fm`.`speci` as `speci`,
    `fm`.`model` as `model`,
    `fm`.`unit_id` as `unit_id`,
    `fu`.`unit_name` as `unit_name`,
    `fm`.`factory_id` as `factory_id`,
    `ff`.`factory_code` as `factory_code`,
    `ff`.`factory_name` as `factory_name`,
    `sibe`.`batch_number` as `batch_number`,
    `sibe`.`batch_no` as `batch_no`,
    `sibe`.`begin_time` as `begin_time`,
    `sibe`.`end_time` as `end_time`,
    `fm`.`storeroom_id` as `storeroom_id`,
    `fwc`.`warehouse_category_code` as `warehouse_category_code`,
    `fwc`.`warehouse_category_name` as `warehouse_category_name`,
    `fm`.`finance_category_id` as `finance_category_id`,
    `ffc`.`finance_category_code` as `finance_category_code`,
    `ffc`.`finance_category_name` as `finance_category_name`
from
    ((((((((`stk_io_bill` `sib`
        left join `stk_io_bill_entry` `sibe` on
        ((`sib`.`id` = `sibe`.`paren_id`)))
        left join `fd_warehouse` `fw` on
        ((`sib`.`department_id` = `fw`.`id`)))
        left join `fd_material` `fm` on
        ((`sibe`.`material_id` = `fm`.`id`)))
        left join `fd_warehouse_category` `fwc` on
        ((`fm`.`storeroom_id` = `fwc`.`warehouse_category_id`)))
        left join `fd_finance_category` `ffc` on
        ((`fm`.`finance_category_id` = `ffc`.`finance_category_id`)))
        left join `fd_supplier` `fs` on
        ((`sibe`.`suppler_id` = `fs`.`id`)))
        left join `fd_unit` `fu` on
        ((`fm`.`unit_id` = `fu`.`unit_id`)))
        left join `fd_factory` `ff` on
        ((`fm`.`factory_id` = `ff`.`factory_id`)))
where
    ((1 = 1)
        and (`sib`.`del_flag` = 0)
        and (`sibe`.`del_flag` = 0)
        and (`sib`.`bill_status` = 2)
        and (`sib`.`bill_type` = 501)
        and (left(`sib`.`bill_no`, 2) = 'DB'))
union all
select
    `sipl`.`id` as `id`,
    `siple`.`id` as `id`,
    `sipl`.`bill_no` as `bill_no`,
    `sipl`.`warehouse_id` as `warehouse_id`,
    `fw`.`code` as `code`,
    `fw`.`name` as `name`,
    `sipl`.`audit_date` as `audit_date`,
    601 as `601`,
    `siple`.`suppler_id` as `suppler_id`,
    `fs`.`name` as `name`,
    'PD' as `PD`,
    `siple`.`unit_price` as `unit_price`,
    `siple`.`profit_qty` as `profit_qty`,
    (`siple`.`profit_qty` * `siple`.`unit_price`) as `siple.profit_qty * siple.unit_price`,
    `siple`.`profit_qty` as `profit_qty`,
    (`siple`.`profit_qty` * `siple`.`unit_price`) as `siple.profit_qty * siple.unit_price`,
    `siple`.`material_id` as `material_id`,
    `fm`.`code` as `code`,
    `fm`.`name` as `name`,
    `fm`.`speci` as `speci`,
    `fm`.`model` as `model`,
    `fm`.`unit_id` as `unit_id`,
    `fu`.`unit_name` as `unit_name`,
    `fm`.`factory_id` as `factory_id`,
    `ff`.`factory_code` as `factory_code`,
    `ff`.`factory_name` as `factory_name`,
    `siple`.`batch_number` as `batch_number`,
    `siple`.`batch_no` as `batch_no`,
    `siple`.`begin_time` as `begin_time`,
    `siple`.`end_time` as `end_time`,
    `fm`.`storeroom_id` as `storeroom_id`,
    `fwc`.`warehouse_category_code` as `warehouse_category_code`,
    `fwc`.`warehouse_category_name` as `warehouse_category_name`,
    `fm`.`finance_category_id` as `finance_category_id`,
    `ffc`.`finance_category_code` as `finance_category_code`,
    `ffc`.`finance_category_name` as `finance_category_name`
from
    ((((((((`stk_io_profit_loss` `sipl`
        left join `stk_io_profit_loss_entry` `siple` on
        ((`sipl`.`id` = `siple`.`paren_id`)))
        left join `fd_warehouse` `fw` on
        ((`sipl`.`warehouse_id` = `fw`.`id`)))
        left join `fd_material` `fm` on
        ((`siple`.`material_id` = `fm`.`id`)))
        left join `fd_warehouse_category` `fwc` on
        ((`fm`.`storeroom_id` = `fwc`.`warehouse_category_id`)))
        left join `fd_finance_category` `ffc` on
        ((`fm`.`finance_category_id` = `ffc`.`finance_category_id`)))
        left join `fd_supplier` `fs` on
        ((`siple`.`suppler_id` = `fs`.`id`)))
        left join `fd_unit` `fu` on
        ((`fm`.`unit_id` = `fu`.`unit_id`)))
        left join `fd_factory` `ff` on
        ((`fm`.`factory_id` = `ff`.`factory_id`)))
where
    ((1 = 1)
        and (`sipl`.`del_flag` = 0)
        and (`siple`.`del_flag` = 0)
        and (`sipl`.`bill_status` = 2));
/
create or replace view view_stock_all_detail
as
select
	a.id as m_id
	,a.bill_no as dh
	,a.audit_date as shrq
	,DATE_FORMAT(a.audit_date, '%Y-%m-%d %H:%i:%s') as shrqStr
	,substring(a.bill_no ,1,2) as ywlx
	,a.warehouse_id as ckid
	,c.name as ckmc
	,a.suppler_id as gysid
	,e.name as gysmc
	,a.department_id as ksid
	,d.name as ksmc
	,b.id as mxid
	,b.material_id as cpdaid
	,f.name as cpmc
	,f.speci as cpgg
	,f.model as cpxh
	,f.unit_id as dwid
	,h.unit_name as dw
	,(case when substring(a.bill_no ,1,2) in ('RK','TH') then b.price when substring(a.bill_no ,1,2) in ('CK','TK') then b.unit_price end) as dj
	,b.qty as sl
	,(case when substring(a.bill_no ,1,2) in ('RK','TK') then b.qty when substring(a.bill_no ,1,2) in ('CK','TH') then -b.qty end) as jcys_qty
from
	stk_io_bill a
	inner join stk_io_bill_entry b on a.id = b.paren_id
	left join fd_warehouse c on a.warehouse_id = c.id
	left join fd_department d on a.department_id = d.id
	left join fd_supplier e on a.suppler_id = e.id
	left join fd_material f  on b.material_id = f.id
	left join fd_factory g on f.factory_id = g.factory_id 
	left join fd_unit h on f.unit_id = h.unit_id 
where
	1 = 1
	and a.bill_status = 2
union all
select
	a.id as m_id
	,a.stock_no  as dh
	,a.audit_date as shrq
	,DATE_FORMAT(a.audit_date, '%Y-%m-%d %H:%i:%s') as shrqStr
	,substring(a.stock_no ,1,2) as ywlx
	,a.warehouse_id as ckid
	,c.name as ckmc
	,a.suppler_id as gysid
	,e.name as gysmc
	,a.department_id as ksid
	,d.name as ksmc
	,b.id as mxid
	,b.material_id as cpdaid
	,f.name as cpmc
	,f.speci as cpgg
	,f.model as cpxh
	,f.unit_id as dwid
	,h.unit_name as dw
	,b.price as dj
	,b.profit_qty as sl
	,b.profit_qty as jcys_qty
from
	stk_io_stocktaking a
	inner join stk_io_stocktaking_entry b on a.id = b.paren_id
	left join fd_warehouse c on a.warehouse_id = c.id
	left join fd_department d on a.department_id = d.id
	left join fd_supplier e on a.suppler_id = e.id
	left join fd_material f  on b.material_id = f.id
	left join fd_factory g on f.factory_id = g.factory_id 
	left join fd_unit h on f.unit_id = h.unit_id
where
	1 = 1
	and a.stock_status = 2;
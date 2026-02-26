-- 数据完整性检查，为有默认值的字段赋值
-- 物料表 是否启用
update fd_material fm set fm.is_use = '2' where fm.is_use is null;
/
-- 物料表 是否归组
update fd_material fm set fm.is_gz = '2' where fm.is_gz  is null;
/
-- 物料表 是否跟踪
update fd_material fm set fm.is_follow = '2' where fm.is_follow is null;
/
-- 物料表 是否计费
update fd_material fm set fm.is_billing = '2' where fm.is_billing is null;
/
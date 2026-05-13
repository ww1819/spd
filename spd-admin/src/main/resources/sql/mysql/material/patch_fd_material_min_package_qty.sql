-- 产品档案 fd_material：最小包装数（执行前请确认列不存在，避免重复执行报错）
-- 含义：最小销售/发放包装下的数量（如 10 支/盒填 10）；可为空表示未维护

ALTER TABLE fd_material
  ADD COLUMN min_package_qty DECIMAL(18,6) NULL COMMENT '最小包装数（每最小包装所含数量，如 10 支/盒）' AFTER package_speci;

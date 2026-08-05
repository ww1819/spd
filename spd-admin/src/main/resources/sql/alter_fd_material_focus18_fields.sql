-- 18类重点耗材字段（fd_material）
-- 执行库：业务库 aspt

ALTER TABLE fd_material
  ADD COLUMN focus18_category varchar(100) DEFAULT NULL COMMENT '18类重点-耗材类别' AFTER selection_reason,
  ADD COLUMN focus18_class_code varchar(100) DEFAULT NULL COMMENT '18类重点-耗材分类代码' AFTER focus18_category,
  ADD COLUMN focus18_level1 varchar(200) DEFAULT NULL COMMENT '18类重点-一级分类（学科、品类）' AFTER focus18_class_code,
  ADD COLUMN focus18_level2 varchar(200) DEFAULT NULL COMMENT '18类重点-二级分类（用途、品目）' AFTER focus18_level1,
  ADD COLUMN focus18_level3 varchar(200) DEFAULT NULL COMMENT '18类重点-三级分类（部位、功能、品种）' AFTER focus18_level2,
  ADD COLUMN focus18_generic_code varchar(100) DEFAULT NULL COMMENT '18类重点-通用名代码' AFTER focus18_level3,
  ADD COLUMN focus18_medical_generic_name varchar(200) DEFAULT NULL COMMENT '18类重点-医保通用名' AFTER focus18_generic_code,
  ADD COLUMN focus18_material_code varchar(100) DEFAULT NULL COMMENT '18类重点-材质代码' AFTER focus18_medical_generic_name,
  ADD COLUMN focus18_material varchar(200) DEFAULT NULL COMMENT '18类重点-材质' AFTER focus18_material_code,
  ADD COLUMN focus18_feature_code varchar(100) DEFAULT NULL COMMENT '18类重点-特征代码' AFTER focus18_material,
  ADD COLUMN focus18_feature_param varchar(500) DEFAULT NULL COMMENT '18类重点-特征参数' AFTER focus18_feature_code;

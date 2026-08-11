-- 产品档案 fd_material 补齐 18类重点耗材字段（可重复执行）
-- 适用：部署库缺少 focus18_* 导致 Unknown column 'm.focus18_category'
-- 执行后无需改代码，重启/刷新即可

SET @db := DATABASE();

SET @sql := (
  SELECT IF(
    (SELECT COUNT(*) FROM information_schema.COLUMNS
     WHERE TABLE_SCHEMA = @db AND TABLE_NAME = 'fd_material' AND COLUMN_NAME = 'focus18_category') = 0,
    'ALTER TABLE fd_material ADD COLUMN focus18_category varchar(100) DEFAULT NULL COMMENT ''18类重点-耗材类别''',
    'SELECT ''skip focus18_category'' AS msg'
  )
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := (
  SELECT IF(
    (SELECT COUNT(*) FROM information_schema.COLUMNS
     WHERE TABLE_SCHEMA = @db AND TABLE_NAME = 'fd_material' AND COLUMN_NAME = 'focus18_class_code') = 0,
    'ALTER TABLE fd_material ADD COLUMN focus18_class_code varchar(100) DEFAULT NULL COMMENT ''18类重点-耗材分类代码''',
    'SELECT ''skip focus18_class_code'' AS msg'
  )
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := (
  SELECT IF(
    (SELECT COUNT(*) FROM information_schema.COLUMNS
     WHERE TABLE_SCHEMA = @db AND TABLE_NAME = 'fd_material' AND COLUMN_NAME = 'focus18_level1') = 0,
    'ALTER TABLE fd_material ADD COLUMN focus18_level1 varchar(200) DEFAULT NULL COMMENT ''18类重点-一级分类（学科、品类）''',
    'SELECT ''skip focus18_level1'' AS msg'
  )
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := (
  SELECT IF(
    (SELECT COUNT(*) FROM information_schema.COLUMNS
     WHERE TABLE_SCHEMA = @db AND TABLE_NAME = 'fd_material' AND COLUMN_NAME = 'focus18_level2') = 0,
    'ALTER TABLE fd_material ADD COLUMN focus18_level2 varchar(200) DEFAULT NULL COMMENT ''18类重点-二级分类（用途、品目）''',
    'SELECT ''skip focus18_level2'' AS msg'
  )
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := (
  SELECT IF(
    (SELECT COUNT(*) FROM information_schema.COLUMNS
     WHERE TABLE_SCHEMA = @db AND TABLE_NAME = 'fd_material' AND COLUMN_NAME = 'focus18_level3') = 0,
    'ALTER TABLE fd_material ADD COLUMN focus18_level3 varchar(200) DEFAULT NULL COMMENT ''18类重点-三级分类（部位、功能、品种）''',
    'SELECT ''skip focus18_level3'' AS msg'
  )
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := (
  SELECT IF(
    (SELECT COUNT(*) FROM information_schema.COLUMNS
     WHERE TABLE_SCHEMA = @db AND TABLE_NAME = 'fd_material' AND COLUMN_NAME = 'focus18_generic_code') = 0,
    'ALTER TABLE fd_material ADD COLUMN focus18_generic_code varchar(100) DEFAULT NULL COMMENT ''18类重点-通用名代码''',
    'SELECT ''skip focus18_generic_code'' AS msg'
  )
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := (
  SELECT IF(
    (SELECT COUNT(*) FROM information_schema.COLUMNS
     WHERE TABLE_SCHEMA = @db AND TABLE_NAME = 'fd_material' AND COLUMN_NAME = 'focus18_medical_generic_name') = 0,
    'ALTER TABLE fd_material ADD COLUMN focus18_medical_generic_name varchar(200) DEFAULT NULL COMMENT ''18类重点-医保通用名''',
    'SELECT ''skip focus18_medical_generic_name'' AS msg'
  )
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := (
  SELECT IF(
    (SELECT COUNT(*) FROM information_schema.COLUMNS
     WHERE TABLE_SCHEMA = @db AND TABLE_NAME = 'fd_material' AND COLUMN_NAME = 'focus18_material_code') = 0,
    'ALTER TABLE fd_material ADD COLUMN focus18_material_code varchar(100) DEFAULT NULL COMMENT ''18类重点-材质代码''',
    'SELECT ''skip focus18_material_code'' AS msg'
  )
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := (
  SELECT IF(
    (SELECT COUNT(*) FROM information_schema.COLUMNS
     WHERE TABLE_SCHEMA = @db AND TABLE_NAME = 'fd_material' AND COLUMN_NAME = 'focus18_material') = 0,
    'ALTER TABLE fd_material ADD COLUMN focus18_material varchar(200) DEFAULT NULL COMMENT ''18类重点-材质''',
    'SELECT ''skip focus18_material'' AS msg'
  )
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := (
  SELECT IF(
    (SELECT COUNT(*) FROM information_schema.COLUMNS
     WHERE TABLE_SCHEMA = @db AND TABLE_NAME = 'fd_material' AND COLUMN_NAME = 'focus18_feature_code') = 0,
    'ALTER TABLE fd_material ADD COLUMN focus18_feature_code varchar(100) DEFAULT NULL COMMENT ''18类重点-特征代码''',
    'SELECT ''skip focus18_feature_code'' AS msg'
  )
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := (
  SELECT IF(
    (SELECT COUNT(*) FROM information_schema.COLUMNS
     WHERE TABLE_SCHEMA = @db AND TABLE_NAME = 'fd_material' AND COLUMN_NAME = 'focus18_feature_param') = 0,
    'ALTER TABLE fd_material ADD COLUMN focus18_feature_param varchar(500) DEFAULT NULL COMMENT ''18类重点-特征参数''',
    'SELECT ''skip focus18_feature_param'' AS msg'
  )
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

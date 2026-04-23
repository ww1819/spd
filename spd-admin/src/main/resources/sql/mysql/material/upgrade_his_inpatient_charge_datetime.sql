-- his_inpatient_charge_mirror: use_date / charge_date 由 varchar 转为 datetime
-- 执行前建议先备份该表

-- 1) 先把空串清理为 NULL
UPDATE his_inpatient_charge_mirror
SET use_date = NULL
WHERE use_date IS NOT NULL AND TRIM(use_date) = '';

UPDATE his_inpatient_charge_mirror
SET charge_date = NULL
WHERE charge_date IS NOT NULL AND TRIM(charge_date) = '';

-- 2) 规范化可识别时间格式（支持 yyyy-MM-dd HH:mm:ss、yyyy/MM/dd HH:mm:ss、yyyy-MM-dd）
UPDATE his_inpatient_charge_mirror
SET use_date = CASE
    WHEN use_date IS NULL THEN NULL
    WHEN TRIM(use_date) REGEXP '^[0-9]{4}[-/][0-9]{2}[-/][0-9]{2}[[:space:]][0-9]{2}:[0-9]{2}:[0-9]{2}$'
        THEN DATE_FORMAT(STR_TO_DATE(REPLACE(TRIM(use_date), '/', '-'), '%Y-%m-%d %H:%i:%s'), '%Y-%m-%d %H:%i:%s')
    WHEN TRIM(use_date) REGEXP '^[0-9]{4}[-/][0-9]{2}[-/][0-9]{2}$'
        THEN DATE_FORMAT(STR_TO_DATE(CONCAT(REPLACE(TRIM(use_date), '/', '-'), ' 00:00:00'), '%Y-%m-%d %H:%i:%s'), '%Y-%m-%d %H:%i:%s')
    ELSE NULL
END;

UPDATE his_inpatient_charge_mirror
SET charge_date = CASE
    WHEN charge_date IS NULL THEN NULL
    WHEN TRIM(charge_date) REGEXP '^[0-9]{4}[-/][0-9]{2}[-/][0-9]{2}[[:space:]][0-9]{2}:[0-9]{2}:[0-9]{2}$'
        THEN DATE_FORMAT(STR_TO_DATE(REPLACE(TRIM(charge_date), '/', '-'), '%Y-%m-%d %H:%i:%s'), '%Y-%m-%d %H:%i:%s')
    WHEN TRIM(charge_date) REGEXP '^[0-9]{4}[-/][0-9]{2}[-/][0-9]{2}$'
        THEN DATE_FORMAT(STR_TO_DATE(CONCAT(REPLACE(TRIM(charge_date), '/', '-'), ' 00:00:00'), '%Y-%m-%d %H:%i:%s'), '%Y-%m-%d %H:%i:%s')
    ELSE NULL
END;

-- 3) 修改字段类型为 datetime
ALTER TABLE his_inpatient_charge_mirror
    MODIFY COLUMN use_date datetime NULL COMMENT '使用时间',
    MODIFY COLUMN charge_date datetime NULL COMMENT '计费时间';

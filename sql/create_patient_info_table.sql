-- 患者信息表
CREATE TABLE `gz_patient_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `medical_record_no` varchar(50) NOT NULL COMMENT '病历号（唯一）',
  `name` varchar(50) DEFAULT NULL COMMENT '姓名',
  `sex` varchar(10) DEFAULT NULL COMMENT '性别',
  `age` int(11) DEFAULT NULL COMMENT '年龄',
  `hospital_number` varchar(50) DEFAULT NULL COMMENT '住院号',
  `ward` varchar(50) DEFAULT NULL COMMENT '病区',
  `ward_no` varchar(50) DEFAULT NULL COMMENT '病房号',
  `bed_no` varchar(50) DEFAULT NULL COMMENT '病床号',
  `apply_dept_id` bigint(20) DEFAULT NULL COMMENT '申请科室ID',
  `exec_dept_id` bigint(20) DEFAULT NULL COMMENT '执行科室ID',
  `hospital_date` date DEFAULT NULL COMMENT '住院日期',
  `contact_phone` varchar(20) DEFAULT NULL COMMENT '联系电话',
  `contact_address` varchar(200) DEFAULT NULL COMMENT '联系地址',
  `chief_surgeon` varchar(50) DEFAULT NULL COMMENT '主刀医生',
  `surgery_date` date DEFAULT NULL COMMENT '手术日期',
  `surgery_name` varchar(200) DEFAULT NULL COMMENT '手术名称',
  `admission_diagnosis` varchar(500) DEFAULT NULL COMMENT '入院诊断',
  `surgery_id` varchar(50) DEFAULT NULL COMMENT '手术ID',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_medical_record_no` (`medical_record_no`),
  KEY `idx_hospital_number` (`hospital_number`),
  KEY `idx_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='患者信息表';

-- 插入测试数据（病历号：ceshi）
INSERT INTO `gz_patient_info` (
  `medical_record_no`, `name`, `sex`, `age`, `hospital_number`, 
  `ward`, `ward_no`, `bed_no`, `apply_dept_id`, `exec_dept_id`,
  `hospital_date`, `contact_phone`, `contact_address`, `chief_surgeon`,
  `surgery_date`, `surgery_name`, `admission_diagnosis`, `surgery_id`, `remark`
) VALUES (
  'ceshi', '测试患者', '男', 35, '20250101001',
  '内科一病区', '101', '1', NULL, NULL,
  '2025-01-01', '13800138000', '测试地址', '张医生',
  '2025-01-05', '测试手术', '测试诊断', 'S001', '测试备注'
);

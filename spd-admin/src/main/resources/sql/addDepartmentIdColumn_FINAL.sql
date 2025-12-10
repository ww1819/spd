-- ============================================
-- 添加科室ID字段 - 最终版本
-- ============================================
-- 执行此 SQL 后，需要取消 Mapper XML 中的注释
-- 文件位置：spd/spd-biz/src/main/resources/mapper/gz/GzOrderMapper.xml
-- ============================================

-- 添加 department_id 字段
ALTER TABLE gz_order ADD COLUMN department_id bigint(20) NULL COMMENT '科室ID' AFTER warehouse_id;

-- 验证字段是否添加成功
SELECT COLUMN_NAME, DATA_TYPE, IS_NULLABLE, COLUMN_COMMENT 
FROM information_schema.COLUMNS 
WHERE TABLE_SCHEMA = DATABASE() 
AND TABLE_NAME = 'gz_order' 
AND COLUMN_NAME = 'department_id';

-- 如果上面的查询返回结果，说明字段添加成功
-- 然后需要取消以下文件中的注释：
-- spd/spd-biz/src/main/resources/mapper/gz/GzOrderMapper.xml
-- 
-- 需要取消注释的位置：
-- 1. 第14行：<result property="departmentId"    column="department_id"    />
-- 2. 第27行：<association property="department" ... />
-- 3. 第70行：gz.department_id,
-- 4. 第76行：,d.id dId,d.code departmentCode,d.name departmentName
-- 5. 第81行：left join fd_department d on gz.department_id = d.id
-- 6. 第100行：a.department_id,
-- 7. 第119行：<if test="departmentId != null">department_id,</if>
-- 8. 第136行：<if test="departmentId != null">#{departmentId},</if>
-- 9. 第157行：<if test="departmentId != null">department_id = #{departmentId},</if>


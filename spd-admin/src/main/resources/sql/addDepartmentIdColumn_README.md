# 添加科室ID字段说明

## 问题
系统需要为 `gz_order` 表添加 `department_id` 字段来支持科室功能。

## 解决方案

### 步骤1：执行 SQL 添加字段

**重要：必须在执行 SQL 后才能取消 Mapper XML 中的注释！**

连接到数据库后，执行以下 SQL：

```sql
ALTER TABLE gz_order ADD COLUMN department_id bigint(20) NULL COMMENT '科室ID' AFTER warehouse_id;
```

### 步骤2：验证字段是否添加成功

执行以下 SQL 检查字段是否存在：

```sql
SHOW COLUMNS FROM gz_order LIKE 'department_id';
```

如果返回结果，说明字段已添加成功。

### 步骤3：取消 Mapper XML 中的注释

字段添加成功后，需要取消以下文件中的注释：

**文件：** `spd/spd-biz/src/main/resources/mapper/gz/GzOrderMapper.xml`

**需要取消注释的位置：**

1. **第13行附近** - `departmentId` 字段映射：
   ```xml
   <result property="departmentId"    column="department_id"    />
   ```

2. **第25行附近** - `department` 对象关联：
   ```xml
   <association property="department"    column="id" javaType="FdDepartment" resultMap="departmentResult" />
   ```

3. **第68行附近** - `selectGzOrderVo` 中的字段查询：
   ```xml
   gz.department_id,
   ```

4. **第75行附近** - 科室信息查询：
   ```xml
   ,d.id dId,d.code departmentCode,d.name departmentName
   ```

5. **第80行附近** - 科室表关联：
   ```xml
   left join fd_department d on gz.department_id = d.id
   ```

6. **第98行附近** - `selectGzOrderById` 中的字段查询：
   ```xml
   a.department_id,
   ```

### 步骤4：重启应用

修改 Mapper XML 后，需要重启后端服务才能生效。

## 注意事项

- 如果字段已存在，执行 ALTER TABLE 会报错，可以忽略
- 添加字段不会影响现有数据，新字段值为 NULL
- 确保在正确的数据库中执行 SQL（通常是 `aspt` 或 `spd`）


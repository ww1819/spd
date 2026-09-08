-- 回填：高值即入即出已生成的 G-RK/G-CK（及 G-TH/G-TK）明细缺少「批号/生产日期」
-- 原因：审核建单查询 selectConfirmLineDetailsByLinkIds 曾把 batchNumber、beginTime 写成 null；
--       列表「高值核销确认」能显示，是因为列表 SQL 从 gz_dep_inventory 取了 material_no / material_date。
-- 注意：云库 gz_traceability_entry 仅有 batch_no / expiry_date，无 material_no、material_date、batch_number、begin_time。
-- 使用前请先 SELECT 核对，再执行 UPDATE。租户默认衡水三院，可按需改 tenant_id / bill_no。

-- 1) 核对目标单据（示例：G-RK2026090400002）
SELECT e.id, e.bill_no, e.material_name, e.batch_no, e.batch_number, e.begin_time, e.end_time, e.remark
FROM stk_io_bill_entry e
WHERE e.tenant_id = 'hengsui-third-001'
  AND e.bill_no IN ('G-RK2026090400002', 'G-CK2026090400002')
  AND ifnull(e.del_flag, 0) = 0;

-- 2) 按明细备注中的 link= 回填批号、生产日期（入出库明细）
UPDATE stk_io_bill_entry e
INNER JOIN his_mirror_consume_link l
    ON l.tenant_id = e.tenant_id
   AND ifnull(l.del_flag, 0) = 0
   AND e.remark LIKE concat('%link=', l.id, '%')
LEFT JOIN gz_dep_inventory gdi
    ON gdi.id = l.gz_dep_inventory_id AND gdi.tenant_id = l.tenant_id
LEFT JOIN gz_traceability_entry te
    ON te.id = l.traceability_entry_id AND te.tenant_id = l.tenant_id AND ifnull(te.del_flag, 0) = 0
LEFT JOIN t_hc_ks_xh_entry xe
    ON xe.id = l.dept_batch_consume_entry_id AND ifnull(xe.del_flag, 0) = 0
SET
    e.batch_number = coalesce(
        nullif(trim(gdi.material_no), ''),
        nullif(trim(xe.material_no), ''),
        nullif(trim(xe.batch_number), ''),
        nullif(trim(te.batch_no), ''),
        xe.batch_no,
        e.batch_number
    ),
    e.begin_time = coalesce(
        gdi.material_date,
        xe.material_date,
        xe.begin_time,
        e.begin_time
    )
WHERE e.tenant_id = 'hengsui-third-001'
  AND ifnull(e.del_flag, 0) = 0
  AND e.remark LIKE '%高值即入即出%'
  AND e.remark LIKE '%link=%'
  AND (
        e.batch_number IS NULL OR trim(e.batch_number) = ''
        OR e.begin_time IS NULL
      );

-- 3) 同步流水 t_hc_ck_flow（若有）
UPDATE t_hc_ck_flow f
INNER JOIN stk_io_bill_entry e
    ON e.id = f.entry_id AND e.tenant_id = f.tenant_id
SET
    f.batch_number = coalesce(nullif(trim(e.batch_number), ''), f.batch_number),
    f.begin_time = coalesce(e.begin_time, f.begin_time)
WHERE f.tenant_id = 'hengsui-third-001'
  AND ifnull(f.del_flag, 0) = 0
  AND e.remark LIKE '%高值即入即出%'
  AND (
        f.batch_number IS NULL OR trim(f.batch_number) = ''
        OR f.begin_time IS NULL
      );

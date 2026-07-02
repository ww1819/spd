-- =============================================================================
-- 衡水市第三人民医院（hengsui-third-001）：出库单号 CK 重复存量修复
--
-- 【重要】请在 SPD 业务库 MySQL（aspt）执行，不是 SQL Server / HIS 库！
--   - 若报错「必须声明标量变量 @tenant_id」(SQL 137)，说明连错了库（连到 SQL Server 了）。
--   - DBeaver：左侧连接选 MySQL aspt；可逐条执行下面第 1 节（已写死租户 ID，无需 SET 变量）。
--
-- 背景：取号 SQL 曾仅统计 bill_type=201，部分出库单 bill_type 为空导致
--       同一流水号（如 CK202606252500012）被多次分配。
--
-- 修复策略：
--   1) 出库单（bill_no LIKE 'CK%'）补全 bill_type=201
--   2) 同一 bill_no 多条时，保留 id 最小的一条不动，其余按当日最大流水递增改号
--   3) 同步更新明细及常见冗余快照表（按 bill_id / bill_no 关联）
--
-- 使用前请：全库备份；先跑「第 1 节」核对；再执行第 2 节（默认注释）。
-- 要求：MySQL 8.0+（窗口函数）
-- =============================================================================


/* ============================================================================
   第 1 节：只读核对（可直接逐条执行，无需 SET 变量）
============================================================================ */

-- 1.1 重复出库单号汇总
SELECT
    b.bill_no,
    COUNT(*) AS cnt,
    SUM(CASE WHEN b.bill_type IS NULL THEN 1 ELSE 0 END) AS bill_type_null_cnt,
    SUM(CASE WHEN b.bill_type = 201 THEN 1 ELSE 0 END) AS bill_type_201_cnt,
    MIN(b.id) AS keep_id,
    GROUP_CONCAT(b.id ORDER BY b.id) AS all_ids
FROM stk_io_bill b
WHERE b.tenant_id = 'hengsui-third-001'
  AND (b.del_flag IS NULL OR b.del_flag != 1)
  AND b.bill_no LIKE 'CK%'
GROUP BY b.bill_no
HAVING COUNT(*) > 1
ORDER BY b.bill_no;

-- 1.2 bill_type 为空的 CK 单（历史脏数据）
SELECT
    b.id,
    b.bill_no,
    b.bill_type,
    b.bill_status,
    b.warehouse_id,
    b.department_id,
    b.total_amount,
    b.create_time,
    b.audit_date
FROM stk_io_bill b
WHERE b.tenant_id = 'hengsui-third-001'
  AND (b.del_flag IS NULL OR b.del_flag != 1)
  AND b.bill_no LIKE 'CK%'
  AND b.bill_type IS NULL
ORDER BY b.create_time, b.id;

-- 1.3 指定单号明细（示例：CK202606252500012）
SELECT
    b.id,
    b.bill_no,
    b.bill_type,
    b.bill_status,
    w.warehouse_name,
    b.department_id,
    b.total_amount,
    b.create_by,
    b.create_time,
    b.audit_by,
    b.audit_date
FROM stk_io_bill b
LEFT JOIN fd_warehouse w ON w.id = b.warehouse_id AND w.tenant_id = b.tenant_id
WHERE b.tenant_id = 'hengsui-third-001'
  AND (b.del_flag IS NULL OR b.del_flag != 1)
  AND b.bill_no = 'CK202606252500012'
ORDER BY b.id;


/* ============================================================================
   第 2 节 A：DBeaver 推荐（分步执行，避免 1064 / 临时表批量失败）
   同一连接、按步骤 1→2→3→4 顺序执行；步骤 1 建表后勿断开连接。
============================================================================ */

-- 【步骤 1】生成改号映射表（执行本段整段，Ctrl+Enter 多语句或 Alt+X 脚本）
DROP TABLE IF EXISTS patch_hengsui_ck_dup_map;

CREATE TABLE patch_hengsui_ck_dup_map (
    bill_id      BIGINT       NOT NULL PRIMARY KEY,
    old_bill_no  VARCHAR(64)  NOT NULL,
    new_bill_no  VARCHAR(64)  NOT NULL,
    KEY idx_patch_ck_old (old_bill_no),
    KEY idx_patch_ck_new (new_bill_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='衡水三院CK重复单号修复映射(执行完步骤4后删除)';

INSERT INTO patch_hengsui_ck_dup_map (bill_id, old_bill_no, new_bill_no)
WITH ranked AS (
    SELECT
        b.id AS bill_id,
        b.bill_no AS old_bill_no,
        SUBSTRING(b.bill_no, 3, 8) AS bill_date,
        ROW_NUMBER() OVER (PARTITION BY b.bill_no ORDER BY b.id) AS dup_rn
    FROM stk_io_bill b
    WHERE b.tenant_id = 'hengsui-third-001'
      AND (b.del_flag IS NULL OR b.del_flag != 1)
      AND b.bill_no LIKE 'CK%'
      AND b.bill_no IN (
          SELECT bill_no
          FROM stk_io_bill
          WHERE tenant_id = 'hengsui-third-001'
            AND (del_flag IS NULL OR del_flag != 1)
            AND bill_no LIKE 'CK%'
          GROUP BY bill_no
          HAVING COUNT(*) > 1
      )
),
serial_base AS (
    SELECT
        SUBSTRING(bill_no, 3, 8) AS bill_date,
        COALESCE(MAX(CAST(SUBSTRING(bill_no, 11) AS UNSIGNED)), 0) AS max_serial
    FROM stk_io_bill
    WHERE tenant_id = 'hengsui-third-001'
      AND (del_flag IS NULL OR del_flag != 1)
      AND bill_no LIKE 'CK%'
      AND LENGTH(bill_no) > 10
    GROUP BY SUBSTRING(bill_no, 3, 8)
),
to_fix AS (
    SELECT
        r.bill_id,
        r.old_bill_no,
        r.bill_date,
        ROW_NUMBER() OVER (PARTITION BY r.bill_date ORDER BY r.bill_id) AS fix_seq
    FROM ranked r
    WHERE r.dup_rn > 1
)
SELECT
    t.bill_id,
    t.old_bill_no,
    CONCAT(
        'CK',
        t.bill_date,
        CASE
            WHEN sb.max_serial + t.fix_seq < 100000
                THEN LPAD(sb.max_serial + t.fix_seq, 5, '0')
            ELSE CAST(sb.max_serial + t.fix_seq AS CHAR)
        END
    ) AS new_bill_no
FROM to_fix t
JOIN serial_base sb ON sb.bill_date = t.bill_date;

-- 【步骤 2】预览（应约 22 行）
SELECT * FROM patch_hengsui_ck_dup_map ORDER BY old_bill_no, bill_id;

-- 【步骤 3】确认无误后执行更新（可 START TRANSACTION 后逐条 UPDATE）
START TRANSACTION;

UPDATE stk_io_bill b
JOIN patch_hengsui_ck_dup_map m ON m.bill_id = b.id
SET b.bill_no = m.new_bill_no,
    b.update_by = 'patch_hs_ck_dup',
    b.update_time = NOW()
WHERE b.tenant_id = 'hengsui-third-001';

UPDATE stk_io_bill_entry e
JOIN patch_hengsui_ck_dup_map m ON m.bill_id = e.paren_id
SET e.bill_no = m.new_bill_no,
    e.update_by = 'patch_hs_ck_dup',
    e.update_time = NOW()
WHERE e.tenant_id = 'hengsui-third-001'
  AND (e.del_flag IS NULL OR e.del_flag != 1);

UPDATE t_hc_ck_flow f
JOIN patch_hengsui_ck_dup_map m ON m.bill_id = f.bill_id
SET f.bill_no = m.new_bill_no,
    f.update_by = 'patch_hs_ck_dup',
    f.update_time = NOW()
WHERE f.tenant_id = 'hengsui-third-001'
  AND (f.del_flag IS NULL OR f.del_flag = 0);

UPDATE t_hc_ks_flow f
JOIN patch_hengsui_ck_dup_map m ON m.bill_id = f.bill_id
SET f.bill_no = m.new_bill_no,
    f.update_by = 'patch_hs_ck_dup',
    f.update_time = NOW()
WHERE f.tenant_id = 'hengsui-third-001'
  AND (f.del_flag IS NULL OR f.del_flag = 0);

UPDATE stk_dep_inventory di
JOIN patch_hengsui_ck_dup_map m ON m.bill_id = di.bill_id
SET di.bill_no = m.new_bill_no,
    di.out_order_no = m.new_bill_no,
    di.update_by = 'patch_hs_ck_dup',
    di.update_time = NOW()
WHERE di.tenant_id = 'hengsui-third-001'
  AND (di.del_flag IS NULL OR di.del_flag = 0);

UPDATE dep_pur_apply_ck_entry_ref r
JOIN patch_hengsui_ck_dup_map m ON m.bill_id = CAST(r.ck_bill_id AS UNSIGNED)
SET r.ck_bill_no = m.new_bill_no,
    r.update_by = 'patch_hs_ck_dup',
    r.update_time = NOW()
WHERE r.tenant_id = 'hengsui-third-001'
  AND (r.del_flag IS NULL OR r.del_flag = 0)
  AND r.link_status = 1;

UPDATE wh_wh_apply_ck_entry_ref r
JOIN patch_hengsui_ck_dup_map m ON m.bill_id = CAST(r.ck_bill_id AS UNSIGNED)
SET r.ck_bill_no = m.new_bill_no,
    r.update_by = 'patch_hs_ck_dup',
    r.update_time = NOW()
WHERE r.tenant_id = 'hengsui-third-001'
  AND (r.del_flag IS NULL OR r.del_flag = 0)
  AND r.link_status = 1;

UPDATE hc_doc_bill_ref ref
JOIN patch_hengsui_ck_dup_map m ON m.bill_id = CAST(ref.tgt_bill_id AS UNSIGNED)
SET ref.tgt_bill_no = m.new_bill_no,
    ref.update_by = 'patch_hs_ck_dup',
    ref.update_time = NOW()
WHERE ref.tenant_id = 'hengsui-third-001'
  AND (ref.del_flag IS NULL OR ref.del_flag = 0);

UPDATE hc_doc_bill_ref ref
JOIN patch_hengsui_ck_dup_map m ON m.bill_id = CAST(ref.src_bill_id AS UNSIGNED)
SET ref.src_bill_no = m.new_bill_no,
    ref.update_by = 'patch_hs_ck_dup',
    ref.update_time = NOW()
WHERE ref.tenant_id = 'hengsui-third-001'
  AND (ref.del_flag IS NULL OR ref.del_flag = 0);

UPDATE stk_lv_io_inhospital_barcode bar
JOIN patch_hengsui_ck_dup_map m ON m.bill_id = CAST(bar.stk_io_bill_id AS UNSIGNED)
SET bar.stk_io_bill_no = m.new_bill_no,
    bar.update_by = 'patch_hs_ck_dup',
    bar.update_time = NOW()
WHERE bar.tenant_id = 'hengsui-third-001'
  AND (bar.del_flag IS NULL OR bar.del_flag = 0);

-- 复核：应 0 行
SELECT b.bill_no, COUNT(*) AS cnt
FROM stk_io_bill b
WHERE b.tenant_id = 'hengsui-third-001'
  AND (b.del_flag IS NULL OR b.del_flag != 1)
  AND b.bill_no LIKE 'CK%'
GROUP BY b.bill_no
HAVING COUNT(*) > 1;

COMMIT;

-- 【步骤 4】提交成功后清理
DROP TABLE IF EXISTS patch_hengsui_ck_dup_map;


/* ============================================================================
   第 2 节 B：一次性临时表版（部分客户端批量执行会 1064，优先用 2 节 A）
============================================================================ */
/*
START TRANSACTION;

-- 2.1 补全 bill_type=201（仅 CK 出库单）
UPDATE stk_io_bill b
SET
    b.bill_type = 201,
    b.update_by = 'patch_hs_ck_dup',
    b.update_time = NOW()
WHERE b.tenant_id = 'hengsui-third-001'
  AND (b.del_flag IS NULL OR b.del_flag != 1)
  AND b.bill_no LIKE 'CK%'
  AND b.bill_type IS NULL;

-- 2.2 计算需改号的主单（同号保留最小 id）
DROP TEMPORARY TABLE IF EXISTS tmp_ck_dup_repair;
CREATE TEMPORARY TABLE tmp_ck_dup_repair AS
SELECT
    b.id AS bill_id,
    b.bill_no AS old_bill_no,
    SUBSTRING(b.bill_no, 3, 8) AS bill_date,
    ROW_NUMBER() OVER (PARTITION BY b.bill_no ORDER BY b.id) AS dup_rn
FROM stk_io_bill b
WHERE b.tenant_id = 'hengsui-third-001'
  AND (b.del_flag IS NULL OR b.del_flag != 1)
  AND b.bill_no LIKE 'CK%'
  AND b.bill_no IN (
      SELECT t.bill_no
      FROM (
          SELECT bill_no
          FROM stk_io_bill
          WHERE tenant_id = 'hengsui-third-001'
            AND (del_flag IS NULL OR del_flag != 1)
            AND bill_no LIKE 'CK%'
          GROUP BY bill_no
          HAVING COUNT(*) > 1
      ) t
  );

DROP TEMPORARY TABLE IF EXISTS tmp_ck_dup_new_no;
CREATE TEMPORARY TABLE tmp_ck_dup_new_no AS
SELECT
    r.bill_id,
    r.old_bill_no,
    r.bill_date,
    base.max_serial
      + ROW_NUMBER() OVER (PARTITION BY r.bill_date ORDER BY r.bill_id) AS new_serial,
    CONCAT(
        'CK',
        r.bill_date,
        CASE
            WHEN base.max_serial + ROW_NUMBER() OVER (PARTITION BY r.bill_date ORDER BY r.bill_id) < 100000
                THEN LPAD(base.max_serial + ROW_NUMBER() OVER (PARTITION BY r.bill_date ORDER BY r.bill_id), 5, '0')
            ELSE CAST(base.max_serial + ROW_NUMBER() OVER (PARTITION BY r.bill_date ORDER BY r.bill_id) AS CHAR)
        END
    ) AS new_bill_no
FROM tmp_ck_dup_repair r
JOIN (
    SELECT
        SUBSTRING(bill_no, 3, 8) AS bill_date,
        COALESCE(MAX(CAST(SUBSTRING(bill_no, 11) AS UNSIGNED)), 0) AS max_serial
    FROM stk_io_bill
    WHERE tenant_id = 'hengsui-third-001'
      AND (del_flag IS NULL OR del_flag != 1)
      AND bill_no LIKE 'CK%'
      AND LENGTH(bill_no) > 10
    GROUP BY SUBSTRING(bill_no, 3, 8)
) base ON base.bill_date = r.bill_date
WHERE r.dup_rn > 1;

-- 2.3 预览改号映射（执行 UPDATE 前可单独 SELECT 核对）
SELECT * FROM tmp_ck_dup_new_no ORDER BY bill_date, bill_id;

-- 2.4 主表改号
UPDATE stk_io_bill b
JOIN tmp_ck_dup_new_no m ON m.bill_id = b.id
SET
    b.bill_no = m.new_bill_no,
    b.update_by = 'patch_hs_ck_dup',
    b.update_time = NOW()
WHERE b.tenant_id = 'hengsui-third-001';

-- 2.5 明细改号
UPDATE stk_io_bill_entry e
JOIN tmp_ck_dup_new_no m ON m.bill_id = e.paren_id
SET
    e.bill_no = m.new_bill_no,
    e.update_by = 'patch_hs_ck_dup',
    e.update_time = NOW()
WHERE e.tenant_id = 'hengsui-third-001'
  AND (e.del_flag IS NULL OR e.del_flag != 1);

-- 2.6 仓库流水快照
UPDATE t_hc_ck_flow f
JOIN tmp_ck_dup_new_no m ON m.bill_id = f.bill_id
SET
    f.bill_no = m.new_bill_no,
    f.update_by = 'patch_hs_ck_dup',
    f.update_time = NOW()
WHERE f.tenant_id = 'hengsui-third-001'
  AND (f.del_flag IS NULL OR f.del_flag = 0);

-- 2.7 科室流水快照
UPDATE t_hc_ks_flow f
JOIN tmp_ck_dup_new_no m ON m.bill_id = f.bill_id
SET
    f.bill_no = m.new_bill_no,
    f.update_by = 'patch_hs_ck_dup',
    f.update_time = NOW()
WHERE f.tenant_id = 'hengsui-third-001'
  AND (f.del_flag IS NULL OR f.del_flag = 0);

-- 2.8 科室库存冗余单号
UPDATE stk_dep_inventory di
JOIN tmp_ck_dup_new_no m ON m.bill_id = di.bill_id
SET
    di.bill_no = m.new_bill_no,
    di.out_order_no = m.new_bill_no,
    di.update_by = 'patch_hs_ck_dup',
    di.update_time = NOW()
WHERE di.tenant_id = 'hengsui-third-001'
  AND (di.del_flag IS NULL OR di.del_flag = 0);

-- 2.9 科室申购 -> 出库关联
UPDATE dep_pur_apply_ck_entry_ref r
JOIN tmp_ck_dup_new_no m ON m.bill_id = CAST(r.ck_bill_id AS UNSIGNED)
SET
    r.ck_bill_no = m.new_bill_no,
    r.update_by = 'patch_hs_ck_dup',
    r.update_time = NOW()
WHERE r.tenant_id = 'hengsui-third-001'
  AND (r.del_flag IS NULL OR r.del_flag = 0)
  AND r.link_status = 1;

-- 2.10 库房申请 -> 出库关联
UPDATE wh_wh_apply_ck_entry_ref r
JOIN tmp_ck_dup_new_no m ON m.bill_id = CAST(r.ck_bill_id AS UNSIGNED)
SET
    r.ck_bill_no = m.new_bill_no,
    r.update_by = 'patch_hs_ck_dup',
    r.update_time = NOW()
WHERE r.tenant_id = 'hengsui-third-001'
  AND (r.del_flag IS NULL OR r.del_flag = 0)
  AND r.link_status = 1;

-- 2.11 单据引用（作为目标单）
UPDATE hc_doc_bill_ref ref
JOIN tmp_ck_dup_new_no m ON m.bill_id = CAST(ref.tgt_bill_id AS UNSIGNED)
SET
    ref.tgt_bill_no = m.new_bill_no,
    ref.update_by = 'patch_hs_ck_dup',
    ref.update_time = NOW()
WHERE ref.tenant_id = 'hengsui-third-001'
  AND (ref.del_flag IS NULL OR ref.del_flag = 0);

-- 2.12 单据引用（作为源单）
UPDATE hc_doc_bill_ref ref
JOIN tmp_ck_dup_new_no m ON m.bill_id = CAST(ref.src_bill_id AS UNSIGNED)
SET
    ref.src_bill_no = m.new_bill_no,
    ref.update_by = 'patch_hs_ck_dup',
    ref.update_time = NOW()
WHERE ref.tenant_id = 'hengsui-third-001'
  AND (ref.del_flag IS NULL OR ref.del_flag = 0);

-- 2.13 低值定数包院内码快照
UPDATE stk_lv_io_inhospital_barcode bar
JOIN tmp_ck_dup_new_no m ON m.bill_id = CAST(bar.stk_io_bill_id AS UNSIGNED)
SET
    bar.stk_io_bill_no = m.new_bill_no,
    bar.update_by = 'patch_hs_ck_dup',
    bar.update_time = NOW()
WHERE bar.tenant_id = 'hengsui-third-001'
  AND (bar.del_flag IS NULL OR bar.del_flag = 0);

-- 2.14 修复后复核：应无重复 CK 单号
SELECT
    b.bill_no,
    COUNT(*) AS cnt
FROM stk_io_bill b
WHERE b.tenant_id = 'hengsui-third-001'
  AND (b.del_flag IS NULL OR b.del_flag != 1)
  AND b.bill_no LIKE 'CK%'
GROUP BY b.bill_no
HAVING COUNT(*) > 1;

COMMIT;
*/

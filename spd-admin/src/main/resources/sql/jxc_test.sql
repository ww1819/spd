set @startDate = '2024-07-01 00:00:00';
set @endDate = '2025-01-02 23:59:59';

SELECT
    jc.ckid,
    jc.ckmc,
    ifnull(qc.qcsl, 0) AS qcsl,
    ifnull(qc.qcje, 0) AS qcje,
    ifnull(jxc.rksl, 0) AS rksl,
    ifnull(jxc.rkje, 0) AS rkje,
    ifnull(jxc.cksl, 0) AS cksl,
    ifnull(jxc.ckje, 0) AS ckje,
    ifnull(jxc.yksl, 0) AS yksl,
    ifnull(jxc.ykje, 0) AS ykje,
    jc.jcsl AS jcsl,
    jc.jcje AS jcje
FROM
    (
        SELECT
            a.ckid,
            a.ckmc,
            SUM(a.jcys_qty) AS jcsl,
            SUM(a.jcys_qty * a.dj) AS jcje
        FROM
            spd.view_stock_all_detail a
        WHERE
            a.shrq <= @endDate
        GROUP BY
            a.ckid,
            a.ckmc
    ) AS jc
        LEFT JOIN (
        SELECT
            a.ckid,
            a.ckmc,
            SUM(
                    CASE
                        WHEN a.ywlx IN ('RK') THEN a.sl
                        WHEN a.ywlx IN ('TH') THEN - a.sl
                        ELSE 0
                        END
            ) AS rksl,
            SUM(
                    CASE
                        WHEN a.ywlx IN ('RK') THEN a.sl * a.dj
                        WHEN a.ywlx IN ('TH') THEN - a.sl * a.dj
                        ELSE 0
                        END
            ) AS rkje,
            SUM(
                    CASE
                        WHEN a.ywlx IN ('CK') THEN a.sl
                        WHEN a.ywlx IN ('TK') THEN - a.sl
                        ELSE 0
                        END
            ) AS cksl,
            SUM(
                    CASE
                        WHEN a.ywlx IN ('CK') THEN a.sl * a.dj
                        WHEN a.ywlx IN ('TK') THEN - a.sl * a.dj
                        ELSE 0
                        END
            ) AS ckje,
            SUM(
                    CASE
                        WHEN a.ywlx IN ('PD') THEN a.sl
                        ELSE 0
                        END
            ) AS yksl,
            SUM(
                    CASE
                        WHEN a.ywlx IN ('PD') THEN a.sl * a.dj
                        ELSE 0
                        END
            ) AS ykje
        FROM
            spd.view_stock_all_detail a
        WHERE
            a.shrq >= @startDate
          AND a.shrq <= @endDate
        GROUP BY
            a.ckid,
            a.ckmc
    ) jxc ON jc.ckid = jxc.ckid
        LEFT JOIN (
        SELECT
            a.ckid,
            a.ckmc,
            SUM(a.jcys_qty) AS qcsl,
            SUM(a.jcys_qty * a.dj) AS qcje
        FROM
            spd.view_stock_all_detail a
        WHERE
            a.shrq < @startDate
        GROUP BY
            a.ckid,
            a.ckmc
    ) qc ON jc.ckid = qc.ckid
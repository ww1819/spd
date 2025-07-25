--香河中西医结合全明细视图
CREATE OR REPLACE VIEW VIEW_ZS_HC_KS_ALL_DETAIL AS
SELECT
    'CK' AS SHEET_LX
     ,A.CODE AS DJH
     ,A.ID AS M_ID
     ,B.ID AS MX_ID
     ,A.KS_NO
     ,A.DT_FHR
     ,B.HC_NO
     ,B.SL
     ,B.DJ
     ,(B.SL * B.DJ) AS JE
     ,B.SL AS KCYS_SL
     ,(B.SL * B.DJ) AS KCYS_JE
FROM
    T_HC_CK_MASTER A
        LEFT JOIN T_HC_CK_DETAIL B ON A.ID = B.M_NO
WHERE
    1 = 1
  AND A.IS_DEL = 0
  AND B.IS_DEL = 0
  AND A.ZT = '1'
  AND A.FH_FLAG = '1'
UNION ALL
SELECT
    'KT' AS SHEET_LX
     ,A.CODE AS DJH
     ,A.ID AS M_ID
     ,B.ID AS MX_ID
     ,A.KS_NO
     ,A.DT_SHR
     ,B.HC_NO
     ,B.SL
     ,B.DJ
     ,(B.SL * B.DJ) AS JE
     ,-B.SL AS KCYS_SL
     ,(-B.SL * B.DJ) AS KCYS_JE
FROM
    T_HC_TK_MASTER A
        LEFT JOIN T_HC_TK_DETAIL B ON A.ID = B.M_NO
WHERE
    1 = 1
  AND A.IS_DEL = 0
  AND B.IS_DEL = 0
  AND A.ZT = '1'
UNION ALL
SELECT
    'DC' AS SHEET_LX
     ,A.CODE AS DJH
     ,A.ID AS M_ID
     ,B.ID AS MX_ID
     ,A.YKS_NO
     ,A.DT_SHR
     ,B.HC_NO
     ,B.SL
     ,B.DJ
     ,(B.SL * B.DJ) AS JE
     ,-B.SL AS KCYS_SL
     ,(-B.SL * B.DJ) AS KCYS_JE
FROM
    T_HC_KS_DB_MASTER A
        LEFT JOIN T_HC_KS_DB_DETAIL B ON A.ID = B.M_NO
WHERE
    1 = 1
  AND A.IS_DEL = 0
  AND B.IS_DEL = 0
  AND A.ZT = '1'
UNION ALL
SELECT
    'DR' AS SHEET_LX
     ,A.CODE AS DJH
     ,A.ID AS M_ID
     ,B.ID AS MX_ID
     ,A.KS_NO
     ,A.DT_SHR
     ,B.HC_NO
     ,B.SL
     ,B.DJ
     ,(B.SL * B.DJ) AS JE
     ,B.SL AS KCYS_SL
     ,(B.SL * B.DJ) AS KCYS_JE
FROM
    T_HC_KS_DB_MASTER A
        LEFT JOIN T_HC_KS_DB_DETAIL B ON A.ID = B.M_NO
WHERE
    1 = 1
  AND A.IS_DEL = 0
  AND B.IS_DEL = 0
  AND A.ZT = '1'
UNION ALL
SELECT
    A.LX AS SHEET_LX
     ,A.CODE AS DJH
     ,A.ID AS M_ID
     ,B.ID AS MX_ID
     ,A.KS_NO
     ,A.DT_SHR
     ,B.HC_NO
     ,(CASE
           WHEN A.LX IN ('KSXH','ZZJ') THEN B.SL
           WHEN A.LX IN ('ZZT') THEN -B.SL
    END) AS SL
     ,B.DJ
     ,(CASE
           WHEN A.LX IN ('KSXH','ZZJ') THEN B.SL
           WHEN A.LX IN ('ZZT') THEN -B.SL
    END) * B.DJ AS JE
     ,-B.SL AS KCYS_SL
     ,(-B.SL * B.DJ) AS KCYS_JE
FROM
    T_HC_XH_MASTER A
        LEFT JOIN T_HC_XH_DETAIL B ON A.ID = B.M_NO
WHERE
    1 = 1
  AND A.IS_DEL = 0
  AND B.IS_DEL = 0
  AND A.ZT = '1'

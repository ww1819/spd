package com.spd.his.mapper;

import java.util.Date;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.spd.his.domain.HisChargeFetchBatch;

public interface HisChargeFetchBatchMapper
{
    int insertHisChargeFetchBatch(HisChargeFetchBatch row);

    HisChargeFetchBatch selectByIdAndTenant(@Param("id") String id, @Param("tenantId") String tenantId);

    List<HisChargeFetchBatch> selectRecentByTenant(@Param("tenantId") String tenantId, @Param("limit") int limit);

    /**
     * 某就诊类型下，与计费明细相关的抓取批次：
     * 1) 抓取时间落在 [beginTime, endTime]；或
     * 2) 查询窗口覆盖 chargeAt，且抓取时间落在 [lookbackBegin, endTime]。
     */
    List<HisChargeFetchBatch> selectForMirrorTrace(
        @Param("tenantId") String tenantId,
        @Param("chargeKind") String chargeKind,
        @Param("chargeAt") Date chargeAt,
        @Param("beginTime") Date beginTime,
        @Param("endTime") Date endTime,
        @Param("lookbackBegin") Date lookbackBegin,
        @Param("limit") int limit);
}

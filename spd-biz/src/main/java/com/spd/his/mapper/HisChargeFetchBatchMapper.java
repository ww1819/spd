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
     * 某就诊类型下，抓取完成时间落在 [beginTime, endTime] 的批次（按时间升序）。
     */
    List<HisChargeFetchBatch> selectByTenantKindAndCreateTimeBetween(
        @Param("tenantId") String tenantId,
        @Param("chargeKind") String chargeKind,
        @Param("beginTime") Date beginTime,
        @Param("endTime") Date endTime,
        @Param("limit") int limit);
}

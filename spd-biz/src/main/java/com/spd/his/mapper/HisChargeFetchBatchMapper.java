package com.spd.his.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.spd.his.domain.HisChargeFetchBatch;

public interface HisChargeFetchBatchMapper
{
    int insertHisChargeFetchBatch(HisChargeFetchBatch row);

    HisChargeFetchBatch selectByIdAndTenant(@Param("id") String id, @Param("tenantId") String tenantId);

    List<HisChargeFetchBatch> selectRecentByTenant(@Param("tenantId") String tenantId, @Param("limit") int limit);
}

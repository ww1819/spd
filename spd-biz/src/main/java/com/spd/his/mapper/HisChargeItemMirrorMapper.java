package com.spd.his.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.spd.his.domain.HisChargeItemMirror;

public interface HisChargeItemMirrorMapper
{
    List<HisChargeItemMirror> selectList(@Param("tenantId") String tenantId,
        @Param("name") String name, @Param("speci") String speci);

    int markAllDeletedByTenant(@Param("tenantId") String tenantId);

    int insertOrUpdateBatch(@Param("rows") List<HisChargeItemMirror> rows);
}

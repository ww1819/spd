package com.spd.his.mapper;

import java.math.BigDecimal;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.spd.his.domain.HisMirrorConsumeLink;

public interface HisMirrorConsumeLinkMapper
{
    int insertBatch(@Param("list") List<HisMirrorConsumeLink> list);

    BigDecimal sumAllocQtyForMirrorRow(@Param("tenantId") String tenantId, @Param("visitKind") String visitKind,
        @Param("mirrorRowId") String mirrorRowId);

    List<String> selectDistinctBillSourcesForMirror(@Param("tenantId") String tenantId, @Param("visitKind") String visitKind,
        @Param("mirrorRowId") String mirrorRowId);
}

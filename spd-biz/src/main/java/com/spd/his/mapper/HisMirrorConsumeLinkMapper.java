package com.spd.his.mapper;

import java.math.BigDecimal;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.spd.his.domain.HisMirrorConsumeLink;
import com.spd.his.domain.dto.HisMirrorConsumeRecordVo;

public interface HisMirrorConsumeLinkMapper
{
    int insertBatch(@Param("list") List<HisMirrorConsumeLink> list);

    BigDecimal sumAllocQtyForMirrorRow(@Param("tenantId") String tenantId, @Param("visitKind") String visitKind,
        @Param("mirrorRowId") String mirrorRowId);

    List<String> selectDistinctBillSourcesForMirror(@Param("tenantId") String tenantId, @Param("visitKind") String visitKind,
        @Param("mirrorRowId") String mirrorRowId);

    /**
     * 某条计费镜像行关联的科室消耗记录（按关联时间倒序）
     */
    List<HisMirrorConsumeRecordVo> selectConsumeRecordsByMirrorRow(@Param("tenantId") String tenantId,
        @Param("visitKind") String visitKind, @Param("mirrorRowId") String mirrorRowId);
}

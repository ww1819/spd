package com.spd.his.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.spd.his.domain.HisMirrorProcessLog;
import com.spd.his.domain.dto.HisMirrorProcessLogVo;

public interface HisMirrorProcessLogMapper
{
    int insert(HisMirrorProcessLog row);

    List<HisMirrorProcessLogVo> selectByMirrorRow(@Param("tenantId") String tenantId,
        @Param("visitKind") String visitKind, @Param("mirrorRowId") String mirrorRowId);
}

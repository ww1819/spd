package com.spd.gz.service;

import java.util.List;
import com.spd.gz.domain.dto.GzHighChargeConfirmQuery;
import com.spd.gz.domain.dto.GzHighChargeConfirmResultVo;
import com.spd.gz.domain.dto.GzHighChargeConfirmRowVo;
import com.spd.gz.domain.dto.GzInstantIoAuditBody;
import com.spd.gz.domain.dto.GzInstantIoReverseBody;

public interface IGzInstantIoService
{
    List<GzHighChargeConfirmRowVo> selectList(GzHighChargeConfirmQuery query);

    /** 导出：按当前筛选取全量（上限保护），或按 linkIds 取选中行 */
    List<GzHighChargeConfirmRowVo> selectListForExport(GzHighChargeConfirmQuery query, List<String> linkIds);

    GzHighChargeConfirmResultVo audit(GzInstantIoAuditBody body);

    /** 人工生成退货301+退库401（冲销/退费反向） */
    GzHighChargeConfirmResultVo reverse(GzInstantIoReverseBody body);
}

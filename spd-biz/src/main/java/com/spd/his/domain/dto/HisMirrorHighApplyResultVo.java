package com.spd.his.domain.dto;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class HisMirrorHighApplyResultVo
{
    /** @deprecated 历史字段，新流程请用 traceBillId */
    private Long consumeBillId;
    private Long traceBillId;
    private String traceNo;
    private BigDecimal appliedQty;
    private BigDecimal remainingBillQty;
    private String mirrorProcessStatus;
}

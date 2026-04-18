package com.spd.his.domain.dto;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class HisMirrorHighApplyResultVo
{
    private Long consumeBillId;
    private BigDecimal appliedQty;
    private BigDecimal remainingBillQty;
    private String mirrorProcessStatus;
}

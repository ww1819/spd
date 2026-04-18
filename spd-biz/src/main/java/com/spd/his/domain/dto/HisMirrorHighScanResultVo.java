package com.spd.his.domain.dto;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class HisMirrorHighScanResultVo
{
    private Long gzDepInventoryId;
    private String inHospitalCode;
    private BigDecimal gzAvailableQty;
    private BigDecimal billQty;
    private BigDecimal alreadyConsumedQty;
    private BigDecimal billRemainingQty;
    private BigDecimal maxApplyQty;
    private String materialName;
    private String batchNo;
}

package com.spd.his.domain.dto;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class HisBillingRefundLowBody
{
    private String visitKind;
    /** HIS 原收费明细主键（与镜像 his_inpatient_charge_id / his_outpatient_charge_id 对应） */
    private String originChargeDetailId;
    private BigDecimal refundQty;
    private String refundMirrorRowId;
    private String remark;
}

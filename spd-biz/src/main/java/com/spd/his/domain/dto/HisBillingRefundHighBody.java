package com.spd.his.domain.dto;

import java.util.List;
import lombok.Data;

@Data
public class HisBillingRefundHighBody
{
    private String visitKind;
    private String originChargeDetailId;
    private String refundMirrorRowId;
    private List<HisBillingRefundHighLineBody> lines;
    private String remark;
}

package com.spd.his.domain.dto;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class HisBillingRefundHighLineBody
{
    /** 计费消耗关联行 his_mirror_consume_link.id（优先） */
    private String consumeLinkId;
    /** 与 link 上快照一致时可按院内码匹配 */
    private String inHospitalCode;
    private BigDecimal returnQty;
}

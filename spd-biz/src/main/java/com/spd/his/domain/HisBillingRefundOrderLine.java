package com.spd.his.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.spd.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class HisBillingRefundOrderLine extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private String id;
    private String tenantId;
    private String refundOrderId;
    private String consumeLinkId;
    private BigDecimal returnQty;
    private Long depInventoryId;
    private Long gzDepInventoryId;
    private String inHospitalCode;
    private String batchNo;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date endDateSnapshot;
    private Integer delFlag;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date deleteTime;
    private String deleteBy;
}

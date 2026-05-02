package com.spd.his.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.spd.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class HisBillingRefundOrder extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private String id;
    private String tenantId;
    private String visitKind;
    private String refundMirrorRowId;
    private String originChargeDetailId;
    private String originMirrorRowId;
    private BigDecimal refundQty;
    private String valueLevel;
    private String processStatus;
    private String failReason;
    private String patientName;
    private Long departmentId;
    private String departmentName;
    private Integer delFlag;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date deleteTime;
    private String deleteBy;
}

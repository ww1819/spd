package com.spd.his.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import com.spd.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * HIS 门诊计费镜像行
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class HisOutpatientChargeMirror extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private String id;
    private String tenantId;
    private String fetchBatchId;
    private String hisOutpatientChargeId;
    private String patientId;
    private String patientName;
    private String outpatientNo;
    private String clinicCode;
    private String clinicName;
    private String doctorId;
    private String doctorName;
    private String chargeItemId;
    private String itemName;
    private String specModel;
    private String batchNo;
    private String expireDate;
    private String chargeDate;
    private BigDecimal quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalAmount;
    private String chargeOperator;
    private String paymentType;
    private String receiptNo;
    private String remark;
    private String rowFingerprint;
    private String processStatus;
    private String processType;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date processTime;
    private String processBy;

    private Long departmentId;
    private String processed;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date beginProcessTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endProcessTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date beginChargeDate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endChargeDate;
}

package com.spd.his.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import com.spd.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * HIS 住院计费镜像行
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class HisInpatientChargeMirror extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private String id;
    private String tenantId;
    private String fetchBatchId;
    private String hisInpatientChargeId;
    private String patientId;
    private String patientName;
    private String inpatientNo;
    private String deptCode;
    private String deptName;
    private String doctorId;
    private String doctorName;
    private String chargeItemId;
    private String itemName;
    private String specModel;
    private String batchNo;
    private String expireDate;
    private String useDate;
    private String chargeDate;
    private BigDecimal quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalAmount;
    private String chargeOperator;
    private String remark;
    private String rowFingerprint;
    private String processStatus;

    /** 查询：计费日起 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date beginChargeDate;
    /** 查询：计费日止 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endChargeDate;
}

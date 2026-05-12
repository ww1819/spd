package com.spd.his.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.spd.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 住院+门诊计费镜像统一表行（列表查询用；高低值库存为查询后填充，非持久化）。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class HisPatientChargeMirrorUnified extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private String id;
    private String tenantId;
    /** INPATIENT / OUTPATIENT */
    private String visitKind;
    private String fetchBatchId;
    private String hisInpatientChargeId;
    private String hisOutpatientChargeId;
    private String hisInpatientChargeIdTf;
    private String hisOutpatientChargeIdTf;
    private String patientId;
    private String patientName;
    private String inpatientNo;
    private String outpatientNo;
    private String deptCode;
    private String deptName;
    private String clinicCode;
    private String clinicName;
    private String doctorId;
    private String doctorName;
    private String chargeItemId;
    private String itemName;
    private String specModel;
    private String batchNo;
    private String expireDate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date useDate;
    private String chargeDateDisplay;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date chargeAt;
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

    /** 来自 his_charge_item_mirror，仅列表展示 */
    private String valueLevel;
    /** 查询后填充 */
    private BigDecimal highValueStockQty;
    /** 查询后填充 */
    private BigDecimal lowValueStockQty;
}

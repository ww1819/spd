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
    /** 该科室该项目对应的高值耗材库存数量 */
    private BigDecimal highValueStockQty;
    /** 该科室该项目对应的低值耗材库存数量 */
    private BigDecimal lowValueStockQty;
    /** 收费项目高低值：1高值 2低值（来自 his_charge_item_mirror，仅查询展示） */
    private String valueLevel;

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
    /** 查询排序字段（仅允许白名单字段） */
    private String orderByColumn;
    /** 查询排序方向：asc/desc */
    private String isAsc;
}

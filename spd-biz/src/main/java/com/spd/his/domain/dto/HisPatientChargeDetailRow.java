package com.spd.his.domain.dto;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * 患者费用明细统一行（住院+门诊）
 */
@Data
public class HisPatientChargeDetailRow
{
    private String id;
    /** INPATIENT / OUTPATIENT */
    private String visitType;
    private String patientName;
    private String inpatientNo;
    private String outpatientNo;
    /** 展示统一号 */
    private String visitNo;
    private String deptName;
    private String clinicName;
    /** 展示统一科室/就诊 */
    private String deptDisplayName;
    private String chargeItemId;
    private String itemName;
    private String specModel;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date chargeDate;
    private BigDecimal quantity;
    private BigDecimal totalAmount;
    private String processStatus;
    private String processType;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date processTime;
    private String processBy;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    private BigDecimal highValueStockQty;
    private BigDecimal lowValueStockQty;
}

package com.spd.gz.domain.dto;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * 高值核销确认列表行
 */
@Data
public class GzHighChargeConfirmRowVo
{
    private String linkId;
    private String visitKind;
    private String mirrorRowId;
    private BigDecimal allocQty;
    private String inHospitalCode;
    private Integer confirmStatus;
    private String confirmId;
    private String confirmNo;
    /** 消耗确认生成的入库单号 G-RK */
    private String inboundBillNo;
    /** 消耗确认生成的出库单号 G-CK */
    private String outboundBillNo;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date consumeAuditTime;
    private String consumeBillNo;
    private Long consumeEntryId;
    private Long materialId;
    private String materialName;
    private BigDecimal unitPrice;
    private BigDecimal entryQty;
    private BigDecimal amt;
    private String supplierId;
    private String supplierName;
    private String batchNo;
    private String batchNumber;
    private Long departmentId;
    private String departmentName;
    private Long factoryId;
    private String materialSpeci;
    private String materialModel;
    private String mainBarcode;
    private String subBarcode;
    private java.util.Date beginTime;
    private java.util.Date endTime;

    private String patientName;
    private String visitNo;
    private String chargeItemId;
    private String itemName;
}

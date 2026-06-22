package com.spd.his.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.spd.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 住院+门诊计费镜像统一表行（列表查询用）。
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
    /** 患者姓名拼音简码（首字母） */
    private String patientNameReferred;
    /** 患者性别 */
    private String patientSex;
    private String inpatientNo;
    private String outpatientNo;
    private String deptCode;
    private String deptName;
    private String clinicCode;
    private String clinicName;
    /** 执行科室ID（HIS exec_dept_id） */
    private String execDeptId;
    /** 执行科室名称（HIS exec_dept_name） */
    private String execDeptName;
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
    /** 处理方式（如：自动处理、手动处理） */
    private String processParty;
    /** 处理情况说明（成功/失败原因等） */
    private String processSituation;
    /** 处理人姓名（列表展示，非表字段） */
    private String processByName;

    /** 高低值冗余字段（1高2低，入库时从收费项镜像写入） */
    private String valueLevel;
    /** 查询后填充 */
    private BigDecimal highValueStockQty;
    /** 查询后填充 */
    private BigDecimal lowValueStockQty;
}

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
    /** 患者性别 */
    private String patientSex;
    private String inpatientNo;
    private String outpatientNo;
    /** 展示统一号 */
    private String visitNo;
    /** 住院开单科室编码（HIS） */
    private String deptCode;
    private String deptName;
    /** 门诊开单科室编码（HIS） */
    private String clinicCode;
    private String clinicName;
    /** 展示统一科室/就诊 */
    private String deptDisplayName;
    /** 执行科室ID（HIS） */
    private String execDeptId;
    /** 执行科室名称（HIS） */
    private String execDeptName;
    private String chargeItemId;
    /** HIS 费用明细主键 */
    private String hisChargeId;
    /** 退费记录对应的原收费明细ID（住院/门诊统一展示） */
    private String chargeIdTf;
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
    /** 处理人姓名 */
    private String processByName;
    /** 处理方（手动/自动） */
    private String processParty;
    /** 处理情况说明 */
    private String processSituation;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    private BigDecimal highValueStockQty;
    private BigDecimal lowValueStockQty;
    /** 收费项目高低值：1高值 2低值（his_charge_item_mirror.value_level） */
    private String valueLevel;
}

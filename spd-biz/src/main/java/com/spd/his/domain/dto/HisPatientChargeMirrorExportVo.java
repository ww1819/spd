package com.spd.his.domain.dto;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.spd.common.annotation.Excel;

/**
 * 患者费用明细导出
 */
public class HisPatientChargeMirrorExportVo
{
    @Excel(name = "类型", readConverterExp = "INPATIENT=住院,OUTPATIENT=门诊")
    private String visitType;

    @Excel(name = "号")
    private String visitNo;

    @Excel(name = "开单科室")
    private String deptDisplayName;

    @Excel(name = "执行科室")
    private String execDeptName;

    @Excel(name = "患者")
    private String patientName;

    @Excel(name = "收费项ID")
    private String chargeItemId;

    @Excel(name = "费用明细主键")
    private String hisChargeId;

    @Excel(name = "退费关联ID")
    private String chargeIdTf;

    @Excel(name = "项目名称")
    private String itemName;

    @Excel(name = "规格")
    private String specModel;

    @Excel(name = "高低值类型", readConverterExp = "1=高值,2=低值,0=未识别")
    private String valueLevel;

    @Excel(name = "计费时间", width = 20, dateFormat = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date chargeDate;

    @Excel(name = "数量")
    private BigDecimal quantity;

    @Excel(name = "金额")
    private BigDecimal totalAmount;

    @Excel(name = "处理类型", readConverterExp = "LOW_VALUE=低值耗材,HIGH_VALUE=高值耗材,REFUND=计费退费")
    private String processType;

    @Excel(name = "处理状态", readConverterExp = "PENDING_CONSUME=待处理,PARTIALLY_CONSUMED=部分消耗,CONSUMED=已处理,REFUNDED=已退费返还")
    private String processStatus;

    @Excel(name = "处理方")
    private String processParty;

    @Excel(name = "处理人")
    private String processByName;

    @Excel(name = "处理情况")
    private String processSituation;

    @Excel(name = "处理时间", width = 20, dateFormat = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date processTime;

    @Excel(name = "本地入库", width = 20, dateFormat = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    public String getVisitType() { return visitType; }
    public void setVisitType(String visitType) { this.visitType = visitType; }
    public String getVisitNo() { return visitNo; }
    public void setVisitNo(String visitNo) { this.visitNo = visitNo; }
    public String getDeptDisplayName() { return deptDisplayName; }
    public void setDeptDisplayName(String deptDisplayName) { this.deptDisplayName = deptDisplayName; }
    public String getExecDeptName() { return execDeptName; }
    public void setExecDeptName(String execDeptName) { this.execDeptName = execDeptName; }
    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }
    public String getChargeItemId() { return chargeItemId; }
    public void setChargeItemId(String chargeItemId) { this.chargeItemId = chargeItemId; }
    public String getHisChargeId() { return hisChargeId; }
    public void setHisChargeId(String hisChargeId) { this.hisChargeId = hisChargeId; }
    public String getChargeIdTf() { return chargeIdTf; }
    public void setChargeIdTf(String chargeIdTf) { this.chargeIdTf = chargeIdTf; }
    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }
    public String getSpecModel() { return specModel; }
    public void setSpecModel(String specModel) { this.specModel = specModel; }
    public String getValueLevel() { return valueLevel; }
    public void setValueLevel(String valueLevel) { this.valueLevel = valueLevel; }
    public Date getChargeDate() { return chargeDate; }
    public void setChargeDate(Date chargeDate) { this.chargeDate = chargeDate; }
    public BigDecimal getQuantity() { return quantity; }
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public String getProcessType() { return processType; }
    public void setProcessType(String processType) { this.processType = processType; }
    public String getProcessStatus() { return processStatus; }
    public void setProcessStatus(String processStatus) { this.processStatus = processStatus; }
    public String getProcessParty() { return processParty; }
    public void setProcessParty(String processParty) { this.processParty = processParty; }
    public String getProcessByName() { return processByName; }
    public void setProcessByName(String processByName) { this.processByName = processByName; }
    public String getProcessSituation() { return processSituation; }
    public void setProcessSituation(String processSituation) { this.processSituation = processSituation; }
    public Date getProcessTime() { return processTime; }
    public void setProcessTime(Date processTime) { this.processTime = processTime; }
    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
}

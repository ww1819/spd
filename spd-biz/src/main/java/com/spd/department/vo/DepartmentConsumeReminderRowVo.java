package com.spd.department.vo;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 消息提醒：科室销提醒（HIS 计费镜像待处理行，与患者收费查询未处理口径一致）
 */
public class DepartmentConsumeReminderRowVo implements Serializable
{
    private static final long serialVersionUID = 1L;

    /** 处理状态码（PENDING_CONSUME 等） */
    private String processStatus;
    /** 处理状态展示文案 */
    private String processStatusText;
    private String orderDeptName;
    private String execDeptName;
    /** 住院号/门诊号 */
    private String medicalRecordNo;
    private String patientName;
    private String chargeItemId;
    private String itemName;
    private String specModel;
    private String unitName;
    private BigDecimal unitPrice;

    public String getProcessStatus()
    {
        return processStatus;
    }

    public void setProcessStatus(String processStatus)
    {
        this.processStatus = processStatus;
    }

    public String getProcessStatusText()
    {
        return processStatusText;
    }

    public void setProcessStatusText(String processStatusText)
    {
        this.processStatusText = processStatusText;
    }

    public String getOrderDeptName()
    {
        return orderDeptName;
    }

    public void setOrderDeptName(String orderDeptName)
    {
        this.orderDeptName = orderDeptName;
    }

    public String getExecDeptName()
    {
        return execDeptName;
    }

    public void setExecDeptName(String execDeptName)
    {
        this.execDeptName = execDeptName;
    }

    public String getMedicalRecordNo()
    {
        return medicalRecordNo;
    }

    public void setMedicalRecordNo(String medicalRecordNo)
    {
        this.medicalRecordNo = medicalRecordNo;
    }

    public String getPatientName()
    {
        return patientName;
    }

    public void setPatientName(String patientName)
    {
        this.patientName = patientName;
    }

    public String getChargeItemId()
    {
        return chargeItemId;
    }

    public void setChargeItemId(String chargeItemId)
    {
        this.chargeItemId = chargeItemId;
    }

    public String getItemName()
    {
        return itemName;
    }

    public void setItemName(String itemName)
    {
        this.itemName = itemName;
    }

    public String getSpecModel()
    {
        return specModel;
    }

    public void setSpecModel(String specModel)
    {
        this.specModel = specModel;
    }

    public String getUnitName()
    {
        return unitName;
    }

    public void setUnitName(String unitName)
    {
        this.unitName = unitName;
    }

    public BigDecimal getUnitPrice()
    {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice)
    {
        this.unitPrice = unitPrice;
    }
}

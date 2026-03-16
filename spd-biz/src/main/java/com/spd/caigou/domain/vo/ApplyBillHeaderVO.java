package com.spd.caigou.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

/**
 * 申购单表头信息（查看申购单弹窗：科室申购单号、仓库、制单人、制单时间、提交人、提交时间、审核人、审核时间）
 */
public class ApplyBillHeaderVO {
    /** 科室申购单号 */
    private String applyBillNo;
    /** 仓库名称 */
    private String warehouseName;
    /** 制单人 */
    private String createByName;
    /** 制单时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
    /** 提交人 */
    private String submitByName;
    /** 提交时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date submitTime;
    /** 审核人 */
    private String auditByName;
    /** 审核时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date auditTime;

    public String getApplyBillNo() { return applyBillNo; }
    public void setApplyBillNo(String applyBillNo) { this.applyBillNo = applyBillNo; }
    public String getWarehouseName() { return warehouseName; }
    public void setWarehouseName(String warehouseName) { this.warehouseName = warehouseName; }
    public String getCreateByName() { return createByName; }
    public void setCreateByName(String createByName) { this.createByName = createByName; }
    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
    public String getSubmitByName() { return submitByName; }
    public void setSubmitByName(String submitByName) { this.submitByName = submitByName; }
    public Date getSubmitTime() { return submitTime; }
    public void setSubmitTime(Date submitTime) { this.submitTime = submitTime; }
    public String getAuditByName() { return auditByName; }
    public void setAuditByName(String auditByName) { this.auditByName = auditByName; }
    public Date getAuditTime() { return auditTime; }
    public void setAuditTime(Date auditTime) { this.auditTime = auditTime; }
}

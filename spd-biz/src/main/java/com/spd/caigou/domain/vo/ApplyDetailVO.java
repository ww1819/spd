package com.spd.caigou.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 采购计划明细关联的申购明细展示VO（科室申购单单号、申购科室、申购数量、制单人、制单时间、审核人、审核时间）
 *
 * @author spd
 */
public class ApplyDetailVO {

    /** 科室申购单单号 */
    private String applyBillNo;
    /** 申购科室 */
    private String departmentName;
    /** 申购数量 */
    private BigDecimal qty;
    /** 制单人 */
    private String createByName;
    /** 制单时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
    /** 审核人 */
    private String auditByName;
    /** 审核时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date auditTime;

    public String getApplyBillNo() { return applyBillNo; }
    public void setApplyBillNo(String applyBillNo) { this.applyBillNo = applyBillNo; }
    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }
    public BigDecimal getQty() { return qty; }
    public void setQty(BigDecimal qty) { this.qty = qty; }
    public String getCreateByName() { return createByName; }
    public void setCreateByName(String createByName) { this.createByName = createByName; }
    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
    public String getAuditByName() { return auditByName; }
    public void setAuditByName(String auditByName) { this.auditByName = auditByName; }
    public Date getAuditTime() { return auditTime; }
    public void setAuditTime(Date auditTime) { this.auditTime = auditTime; }
}

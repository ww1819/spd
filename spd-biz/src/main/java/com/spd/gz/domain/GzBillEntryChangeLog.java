package com.spd.gz.domain;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

/**
 * 高值单据明细变更记录 gz_bill_entry_change_log
 */
public class GzBillEntryChangeLog {
    private String id;
    private String billType;
    private Long billId;
    private String entryType;
    private Long entryId;
    private String actionType;
    private String beforeJson;
    private String afterJson;
    private String operator;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date changeTime;
    private String tenantId;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getBillType() { return billType; }
    public void setBillType(String billType) { this.billType = billType; }
    public Long getBillId() { return billId; }
    public void setBillId(Long billId) { this.billId = billId; }
    public String getEntryType() { return entryType; }
    public void setEntryType(String entryType) { this.entryType = entryType; }
    public Long getEntryId() { return entryId; }
    public void setEntryId(Long entryId) { this.entryId = entryId; }
    public String getActionType() { return actionType; }
    public void setActionType(String actionType) { this.actionType = actionType; }
    public String getBeforeJson() { return beforeJson; }
    public void setBeforeJson(String beforeJson) { this.beforeJson = beforeJson; }
    public String getAfterJson() { return afterJson; }
    public void setAfterJson(String afterJson) { this.afterJson = afterJson; }
    public String getOperator() { return operator; }
    public void setOperator(String operator) { this.operator = operator; }
    public Date getChangeTime() { return changeTime; }
    public void setChangeTime(Date changeTime) { this.changeTime = changeTime; }
    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }
}

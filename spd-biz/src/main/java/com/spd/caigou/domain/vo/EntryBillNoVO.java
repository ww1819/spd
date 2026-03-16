package com.spd.caigou.domain.vo;

/**
 * 采购计划明细ID与申购单号（用于按 entry 聚合显示）
 */
public class EntryBillNoVO {
    private Long entryId;
    private String purchaseBillNo;

    public Long getEntryId() { return entryId; }
    public void setEntryId(Long entryId) { this.entryId = entryId; }
    public String getPurchaseBillNo() { return purchaseBillNo; }
    public void setPurchaseBillNo(String purchaseBillNo) { this.purchaseBillNo = purchaseBillNo; }
}

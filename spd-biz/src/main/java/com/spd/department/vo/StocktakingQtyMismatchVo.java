package com.spd.department.vo;

import java.math.BigDecimal;

/** 审核前库存一致性校验返回行 */
public class StocktakingQtyMismatchVo
{
    private Long entryId;
    private String materialName;
    private String batchNo;
    private BigDecimal detailQty;
    private BigDecimal currentQty;
    private BigDecimal stockQty;

    public Long getEntryId() { return entryId; }
    public void setEntryId(Long entryId) { this.entryId = entryId; }
    public String getMaterialName() { return materialName; }
    public void setMaterialName(String materialName) { this.materialName = materialName; }
    public String getBatchNo() { return batchNo; }
    public void setBatchNo(String batchNo) { this.batchNo = batchNo; }
    public BigDecimal getDetailQty() { return detailQty; }
    public void setDetailQty(BigDecimal detailQty) { this.detailQty = detailQty; }
    public BigDecimal getCurrentQty() { return currentQty; }
    public void setCurrentQty(BigDecimal currentQty) { this.currentQty = currentQty; }
    public BigDecimal getStockQty() { return stockQty; }
    public void setStockQty(BigDecimal stockQty) { this.stockQty = stockQty; }
}

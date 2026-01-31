package com.spd.warehouse.domain;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.spd.common.annotation.Excel;
import com.spd.common.core.domain.BaseEntity;
import com.spd.foundation.domain.FdMaterial;

/**
 * 盈亏单明细对象 stk_io_profit_loss_entry
 *
 * @author spd
 */
public class StkIoProfitLossEntry extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;
    /** 盈亏单ID */
    private Long parenId;
    /** 来源盘点明细ID */
    private Long stocktakingEntryId;
    /** 库存明细id（来自盘点明细，审核时按此查库存） */
    private Long kcNo;
    /** 耗材ID */
    private Long materialId;
    /** 批次号 */
    private String batchNo;
    /** 批号 */
    private String batchNumber;
    /** 当前库存（盘点单当时账面qty，用于审核校验） */
    private BigDecimal bookQty;
    /** 盘点库存（盘点数量） */
    private BigDecimal stockQty;
    /** 盈亏数量（正=盘盈，负=盘亏） */
    private BigDecimal profitQty;
    /** 单价 */
    private BigDecimal unitPrice;
    /** 盈亏金额 */
    private BigDecimal profitAmount;
    /** 生产日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date beginTime;
    /** 有效期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date endTime;
    /** 删除标志 */
    private Integer delFlag;
    /** 耗材对象 */
    private FdMaterial material;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getParenId() { return parenId; }
    public void setParenId(Long parenId) { this.parenId = parenId; }
    public Long getStocktakingEntryId() { return stocktakingEntryId; }
    public void setStocktakingEntryId(Long stocktakingEntryId) { this.stocktakingEntryId = stocktakingEntryId; }
    public Long getKcNo() { return kcNo; }
    public void setKcNo(Long kcNo) { this.kcNo = kcNo; }
    public Long getMaterialId() { return materialId; }
    public void setMaterialId(Long materialId) { this.materialId = materialId; }
    public String getBatchNo() { return batchNo; }
    public void setBatchNo(String batchNo) { this.batchNo = batchNo; }
    public String getBatchNumber() { return batchNumber; }
    public void setBatchNumber(String batchNumber) { this.batchNumber = batchNumber; }
    public BigDecimal getBookQty() { return bookQty; }
    public void setBookQty(BigDecimal bookQty) { this.bookQty = bookQty; }
    public BigDecimal getStockQty() { return stockQty; }
    public void setStockQty(BigDecimal stockQty) { this.stockQty = stockQty; }
    public BigDecimal getProfitQty() { return profitQty; }
    public void setProfitQty(BigDecimal profitQty) { this.profitQty = profitQty; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
    public BigDecimal getProfitAmount() { return profitAmount; }
    public void setProfitAmount(BigDecimal profitAmount) { this.profitAmount = profitAmount; }
    public Date getBeginTime() { return beginTime; }
    public void setBeginTime(Date beginTime) { this.beginTime = beginTime; }
    public Date getEndTime() { return endTime; }
    public void setEndTime(Date endTime) { this.endTime = endTime; }
    public Integer getDelFlag() { return delFlag; }
    public void setDelFlag(Integer delFlag) { this.delFlag = delFlag; }
    public FdMaterial getMaterial() { return material; }
    public void setMaterial(FdMaterial material) { this.material = material; }
}

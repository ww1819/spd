package com.spd.warehouse.domain;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.spd.common.annotation.Excel;
import com.spd.common.core.domain.BaseEntity;
import com.spd.foundation.domain.FdWarehouse;

/**
 * 盈亏单主表对象 stk_io_profit_loss
 *
 * @author spd
 */
public class StkIoProfitLoss extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;
    /** 盈亏单号 */
    @Excel(name = "盈亏单号")
    private String billNo;
    /** 关联盘点单ID */
    private Long stocktakingId;
    /** 盘点单号（冗余） */
    private String stocktakingNo;
    /** 仓库ID */
    private Long warehouseId;
    /** 单据状态 1待审核 2已审核 */
    private Integer billStatus;
    /** 审核人 */
    private String auditBy;
    /** 审核时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date auditDate;
    /** 删除标志 */
    private Integer delFlag;
    /** 盈亏单明细 */
    private List<StkIoProfitLossEntry> entryList;
    /** 仓库对象 */
    private FdWarehouse warehouse;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getBillNo() { return billNo; }
    public void setBillNo(String billNo) { this.billNo = billNo; }
    public Long getStocktakingId() { return stocktakingId; }
    public void setStocktakingId(Long stocktakingId) { this.stocktakingId = stocktakingId; }
    public String getStocktakingNo() { return stocktakingNo; }
    public void setStocktakingNo(String stocktakingNo) { this.stocktakingNo = stocktakingNo; }
    public Long getWarehouseId() { return warehouseId; }
    public void setWarehouseId(Long warehouseId) { this.warehouseId = warehouseId; }
    public Integer getBillStatus() { return billStatus; }
    public void setBillStatus(Integer billStatus) { this.billStatus = billStatus; }
    public String getAuditBy() { return auditBy; }
    public void setAuditBy(String auditBy) { this.auditBy = auditBy; }
    public Date getAuditDate() { return auditDate; }
    public void setAuditDate(Date auditDate) { this.auditDate = auditDate; }
    public Integer getDelFlag() { return delFlag; }
    public void setDelFlag(Integer delFlag) { this.delFlag = delFlag; }
    public List<StkIoProfitLossEntry> getEntryList() { return entryList; }
    public void setEntryList(List<StkIoProfitLossEntry> entryList) { this.entryList = entryList; }
    public FdWarehouse getWarehouse() { return warehouse; }
    public void setWarehouse(FdWarehouse warehouse) { this.warehouse = warehouse; }
}

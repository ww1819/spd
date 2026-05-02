package com.spd.warehouse.domain;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    /** 高值耗材主条码 */
    private String mainBarcode;
    /** 高值耗材辅条码 */
    private String subBarcode;
    /** 供应商ID（出退库单明细内的供应商id） */
    private String supplerId;

    private String entryUuid;
    private String stocktakingLineUuid;
    private String origBatchNo;
    private Long origBatchId;
    /** SURPLUS / LOSS */
    private String plKind;
    private String materialNameSnap;
    private String materialSpeciSnap;
    private String warehouseNameSnap;
    private Long surplusStkBatchId;
    private Long resultStkInventoryId;
    /** 科室库存明细 id（盘亏扣减目标，biz_scope=DEP） */
    private Long depInventoryId;
    /** 盘盈审核生成的科室库存 id */
    private Long resultDepInventoryId;
    /** 科室名称快照 */
    private String departmentNameSnap;
    /** 盘盈可退库仓库（快照） */
    private Long returnWarehouseId;
    /** 租户ID */
    private String tenantId;

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
    public String getMainBarcode() { return mainBarcode; }
    public void setMainBarcode(String mainBarcode) { this.mainBarcode = mainBarcode; }
    public String getSubBarcode() { return subBarcode; }
    public void setSubBarcode(String subBarcode) { this.subBarcode = subBarcode; }
    public String getSupplerId() { return supplerId; }
    public void setSupplerId(String supplerId) { this.supplerId = supplerId; }

    public String getEntryUuid() { return entryUuid; }
    public void setEntryUuid(String entryUuid) { this.entryUuid = entryUuid; }

    public String getStocktakingLineUuid() { return stocktakingLineUuid; }
    public void setStocktakingLineUuid(String stocktakingLineUuid) { this.stocktakingLineUuid = stocktakingLineUuid; }

    public String getOrigBatchNo() { return origBatchNo; }
    public void setOrigBatchNo(String origBatchNo) { this.origBatchNo = origBatchNo; }

    public Long getOrigBatchId() { return origBatchId; }
    public void setOrigBatchId(Long origBatchId) { this.origBatchId = origBatchId; }

    public String getPlKind() { return plKind; }
    public void setPlKind(String plKind) { this.plKind = plKind; }

    public String getMaterialNameSnap() { return materialNameSnap; }
    public void setMaterialNameSnap(String materialNameSnap) { this.materialNameSnap = materialNameSnap; }

    public String getMaterialSpeciSnap() { return materialSpeciSnap; }
    public void setMaterialSpeciSnap(String materialSpeciSnap) { this.materialSpeciSnap = materialSpeciSnap; }

    public String getWarehouseNameSnap() { return warehouseNameSnap; }
    public void setWarehouseNameSnap(String warehouseNameSnap) { this.warehouseNameSnap = warehouseNameSnap; }

    public Long getSurplusStkBatchId() { return surplusStkBatchId; }
    public void setSurplusStkBatchId(Long surplusStkBatchId) { this.surplusStkBatchId = surplusStkBatchId; }

    public Long getResultStkInventoryId() { return resultStkInventoryId; }
    public void setResultStkInventoryId(Long resultStkInventoryId) { this.resultStkInventoryId = resultStkInventoryId; }

    public Long getDepInventoryId() { return depInventoryId; }
    public void setDepInventoryId(Long depInventoryId) { this.depInventoryId = depInventoryId; }

    public Long getResultDepInventoryId() { return resultDepInventoryId; }
    public void setResultDepInventoryId(Long resultDepInventoryId) { this.resultDepInventoryId = resultDepInventoryId; }

    public String getDepartmentNameSnap() { return departmentNameSnap; }
    public void setDepartmentNameSnap(String departmentNameSnap) { this.departmentNameSnap = departmentNameSnap; }

    public Long getReturnWarehouseId() { return returnWarehouseId; }
    public void setReturnWarehouseId(Long returnWarehouseId) { this.returnWarehouseId = returnWarehouseId; }

    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }
}

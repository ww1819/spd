package com.spd.gz.domain.vo;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonFormat;

/** 备货库存院内码完整追溯结果 */
public class GzDepotInventoryTraceResultVo
{
    private Long depotInventoryId;
    private BigDecimal currentQty;
    private String batchNo;
    private String orderNo;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date warehouseDate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
    private String warehouseName;
    /** 科室是否仍有库存 */
    private BigDecimal depInventoryQty;
    private String depDepartmentName;
    private List<GzDepotInventoryTraceVo> traces;
    private List<GzDepotInventoryTraceVo> suspectDeductions;

    public Long getDepotInventoryId() { return depotInventoryId; }
    public void setDepotInventoryId(Long depotInventoryId) { this.depotInventoryId = depotInventoryId; }
    public BigDecimal getCurrentQty() { return currentQty; }
    public void setCurrentQty(BigDecimal currentQty) { this.currentQty = currentQty; }
    public String getBatchNo() { return batchNo; }
    public void setBatchNo(String batchNo) { this.batchNo = batchNo; }
    public String getOrderNo() { return orderNo; }
    public void setOrderNo(String orderNo) { this.orderNo = orderNo; }
    public Date getWarehouseDate() { return warehouseDate; }
    public void setWarehouseDate(Date warehouseDate) { this.warehouseDate = warehouseDate; }
    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }
    public String getWarehouseName() { return warehouseName; }
    public void setWarehouseName(String warehouseName) { this.warehouseName = warehouseName; }
    public BigDecimal getDepInventoryQty() { return depInventoryQty; }
    public void setDepInventoryQty(BigDecimal depInventoryQty) { this.depInventoryQty = depInventoryQty; }
    public String getDepDepartmentName() { return depDepartmentName; }
    public void setDepDepartmentName(String depDepartmentName) { this.depDepartmentName = depDepartmentName; }
    public List<GzDepotInventoryTraceVo> getTraces() { return traces; }
    public void setTraces(List<GzDepotInventoryTraceVo> traces) { this.traces = traces; }
    public List<GzDepotInventoryTraceVo> getSuspectDeductions() { return suspectDeductions; }
    public void setSuspectDeductions(List<GzDepotInventoryTraceVo> suspectDeductions) { this.suspectDeductions = suspectDeductions; }
}

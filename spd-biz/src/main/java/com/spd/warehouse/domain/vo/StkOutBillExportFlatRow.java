package com.spd.warehouse.domain.vo;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 出库单按单导出：扁平行（后续在 Service 中按 billId 分组写 Excel）
 */
public class StkOutBillExportFlatRow
{
    private Long billId;
    private String billNo;
    private String departmentName;
    private String materialName;
    private String speci;
    private String model;
    private String unitName;
    private BigDecimal qty;
    private BigDecimal unitPrice;
    private BigDecimal amt;
    /** 批号（优先 batch_number，否则 batch_no） */
    private String batchPh;
    private Date endTime;

    public Long getBillId() { return billId; }
    public void setBillId(Long billId) { this.billId = billId; }
    public String getBillNo() { return billNo; }
    public void setBillNo(String billNo) { this.billNo = billNo; }
    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }
    public String getMaterialName() { return materialName; }
    public void setMaterialName(String materialName) { this.materialName = materialName; }
    public String getSpeci() { return speci; }
    public void setSpeci(String speci) { this.speci = speci; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public String getUnitName() { return unitName; }
    public void setUnitName(String unitName) { this.unitName = unitName; }
    public BigDecimal getQty() { return qty; }
    public void setQty(BigDecimal qty) { this.qty = qty; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
    public BigDecimal getAmt() { return amt; }
    public void setAmt(BigDecimal amt) { this.amt = amt; }
    public String getBatchPh() { return batchPh; }
    public void setBatchPh(String batchPh) { this.batchPh = batchPh; }
    public Date getEndTime() { return endTime; }
    public void setEndTime(Date endTime) { this.endTime = endTime; }
}

package com.spd.gz.domain;

/**
 * 高值库存查询参数
 */
public class GzStockQueryParam
{
    private Long warehouseId;
    private Long departmentId;
    private Long supplierId;
    private Long materialId;
    private String inHospitalCode;
    private String orderNo;
    private Integer orderStatus;
    private String beginDate;
    private String endDate;
    private String timeField;
    /** shipment / refundStock / refundGoods，空=出库+退库 */
    private String billKindScope;

    public Long getWarehouseId() { return warehouseId; }
    public void setWarehouseId(Long warehouseId) { this.warehouseId = warehouseId; }
    public Long getDepartmentId() { return departmentId; }
    public void setDepartmentId(Long departmentId) { this.departmentId = departmentId; }
    public Long getSupplierId() { return supplierId; }
    public void setSupplierId(Long supplierId) { this.supplierId = supplierId; }
    public Long getMaterialId() { return materialId; }
    public void setMaterialId(Long materialId) { this.materialId = materialId; }
    public String getInHospitalCode() { return inHospitalCode; }
    public void setInHospitalCode(String inHospitalCode) { this.inHospitalCode = inHospitalCode; }
    public String getOrderNo() { return orderNo; }
    public void setOrderNo(String orderNo) { this.orderNo = orderNo; }
    public Integer getOrderStatus() { return orderStatus; }
    public void setOrderStatus(Integer orderStatus) { this.orderStatus = orderStatus; }
    public String getBeginDate() { return beginDate; }
    public void setBeginDate(String beginDate) { this.beginDate = beginDate; }
    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }
    public String getTimeField() { return timeField; }
    public void setTimeField(String timeField) { this.timeField = timeField; }
    public String getBillKindScope() { return billKindScope; }
    public void setBillKindScope(String billKindScope) { this.billKindScope = billKindScope; }
}

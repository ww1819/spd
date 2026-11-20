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
    /** shipment / refundStock / acceptance / refundGoods，空=该接口默认的全部单据 */
    private String billKindScope;
    /** UNION 各分支预取行数（分页查询性能优化，由 Controller 注入） */
    private Integer branchFetchLimit;
    /** 耗材编码/名称/规格/型号模糊 */
    private String materialKeyword;
    /** 批号模糊 */
    private String batchNumber;
    /** 批次模糊 */
    private String batchNo;
    /** UDI 模糊 */
    private String udiNo;
    /** 生产厂家模糊 */
    private String factoryKeyword;
    /** 规格模糊 */
    private String materialSpeci;

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
    public Integer getBranchFetchLimit() { return branchFetchLimit; }
    public void setBranchFetchLimit(Integer branchFetchLimit) { this.branchFetchLimit = branchFetchLimit; }
    public String getMaterialKeyword() { return materialKeyword; }
    public void setMaterialKeyword(String materialKeyword) { this.materialKeyword = materialKeyword; }
    public String getBatchNumber() { return batchNumber; }
    public void setBatchNumber(String batchNumber) { this.batchNumber = batchNumber; }
    public String getBatchNo() { return batchNo; }
    public void setBatchNo(String batchNo) { this.batchNo = batchNo; }
    public String getUdiNo() { return udiNo; }
    public void setUdiNo(String udiNo) { this.udiNo = udiNo; }
    public String getFactoryKeyword() { return factoryKeyword; }
    public void setFactoryKeyword(String factoryKeyword) { this.factoryKeyword = factoryKeyword; }
    public String getMaterialSpeci() { return materialSpeci; }
    public void setMaterialSpeci(String materialSpeci) { this.materialSpeci = materialSpeci; }
}

package com.spd.gz.domain.vo;

/** 科室库存院内码错码修复候选 */
public class GzDepInventoryCodeRepairVo
{
    private Long shipmentEntryId;
    private Long shipmentId;
    private String shipmentNo;
    private Long departmentId;
    private String departmentName;
    private String batchNo;
    private Long materialId;
    private Long warehouseId;
    private String wrongInHospitalCode;
    private String correctInHospitalCode;
    private Long depInventoryId;

    public Long getShipmentEntryId() { return shipmentEntryId; }
    public void setShipmentEntryId(Long shipmentEntryId) { this.shipmentEntryId = shipmentEntryId; }
    public Long getShipmentId() { return shipmentId; }
    public void setShipmentId(Long shipmentId) { this.shipmentId = shipmentId; }
    public String getShipmentNo() { return shipmentNo; }
    public void setShipmentNo(String shipmentNo) { this.shipmentNo = shipmentNo; }
    public Long getDepartmentId() { return departmentId; }
    public void setDepartmentId(Long departmentId) { this.departmentId = departmentId; }
    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }
    public String getBatchNo() { return batchNo; }
    public void setBatchNo(String batchNo) { this.batchNo = batchNo; }
    public Long getMaterialId() { return materialId; }
    public void setMaterialId(Long materialId) { this.materialId = materialId; }
    public Long getWarehouseId() { return warehouseId; }
    public void setWarehouseId(Long warehouseId) { this.warehouseId = warehouseId; }
    public String getWrongInHospitalCode() { return wrongInHospitalCode; }
    public void setWrongInHospitalCode(String wrongInHospitalCode) { this.wrongInHospitalCode = wrongInHospitalCode; }
    public String getCorrectInHospitalCode() { return correctInHospitalCode; }
    public void setCorrectInHospitalCode(String correctInHospitalCode) { this.correctInHospitalCode = correctInHospitalCode; }
    public Long getDepInventoryId() { return depInventoryId; }
    public void setDepInventoryId(Long depInventoryId) { this.depInventoryId = depInventoryId; }
}

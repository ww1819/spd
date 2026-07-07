package com.spd.gz.domain.vo;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

/** 备货库存院内码流水追溯 */
public class GzDepotInventoryTraceVo
{
    private String traceKind;
    private String lx;
    private String originBusinessType;
    private String billNo;
    private BigDecimal qty;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date flowTime;
    private String warehouseName;
    private String departmentName;
    private String inHospitalCode;
    private Integer orderStatus;
    private String remark;

    public String getTraceKind() { return traceKind; }
    public void setTraceKind(String traceKind) { this.traceKind = traceKind; }
    public String getLx() { return lx; }
    public void setLx(String lx) { this.lx = lx; }
    public String getOriginBusinessType() { return originBusinessType; }
    public void setOriginBusinessType(String originBusinessType) { this.originBusinessType = originBusinessType; }
    public String getBillNo() { return billNo; }
    public void setBillNo(String billNo) { this.billNo = billNo; }
    public BigDecimal getQty() { return qty; }
    public void setQty(BigDecimal qty) { this.qty = qty; }
    public Date getFlowTime() { return flowTime; }
    public void setFlowTime(Date flowTime) { this.flowTime = flowTime; }
    public String getWarehouseName() { return warehouseName; }
    public void setWarehouseName(String warehouseName) { this.warehouseName = warehouseName; }
    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }
    public String getInHospitalCode() { return inHospitalCode; }
    public void setInHospitalCode(String inHospitalCode) { this.inHospitalCode = inHospitalCode; }
    public Integer getOrderStatus() { return orderStatus; }
    public void setOrderStatus(Integer orderStatus) { this.orderStatus = orderStatus; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
}

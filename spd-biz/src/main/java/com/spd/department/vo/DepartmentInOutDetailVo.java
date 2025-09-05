package com.spd.department.vo;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 科室进销存明细VO
 * 
 * @author spd
 * @date 2024-12-19
 */
public class DepartmentInOutDetailVo {
    
    /** 主键ID */
    private Long id;
    
    /** 单据编号 */
    private String billNo;
    
    /** 单据类型 */
    private Integer billType;
    
    /** 单据状态 */
    private Integer billStatus;
    
    /** 制单日期 */
    private Date billDate;
    
    /** 耗材编码 */
    private String materialCode;
    
    /** 耗材名称 */
    private String materialName;
    
    /** 规格 */
    private String specification;
    
    /** 型号 */
    private String model;
    
    /** 科室名称 */
    private String departmentName;
    
    /** 仓库名称 */
    private String warehouseName;
    
    /** 数量 */
    private BigDecimal qty;
    
    /** 单价 */
    private BigDecimal unitPrice;
    
    /** 金额 */
    private BigDecimal amount;
    
    /** 批次号 */
    private String batchNo;
    
    /** 操作人 */
    private String createBy;
    
    /** 备注 */
    private String remark;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBillNo() {
        return billNo;
    }

    public void setBillNo(String billNo) {
        this.billNo = billNo;
    }

    public Integer getBillType() {
        return billType;
    }

    public void setBillType(Integer billType) {
        this.billType = billType;
    }

    public Integer getBillStatus() {
        return billStatus;
    }

    public void setBillStatus(Integer billStatus) {
        this.billStatus = billStatus;
    }

    public Date getBillDate() {
        return billDate;
    }

    public void setBillDate(Date billDate) {
        this.billDate = billDate;
    }

    public String getMaterialCode() {
        return materialCode;
    }

    public void setMaterialCode(String materialCode) {
        this.materialCode = materialCode;
    }

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }

    public String getSpecification() {
        return specification;
    }

    public void setSpecification(String specification) {
        this.specification = specification;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getWarehouseName() {
        return warehouseName;
    }

    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName;
    }

    public BigDecimal getQty() {
        return qty;
    }

    public void setQty(BigDecimal qty) {
        this.qty = qty;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}

package com.spd.gz.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.spd.common.annotation.Excel;
import com.spd.common.core.domain.BaseEntity;

/**
 * 高值追溯单明细对象 gz_traceability_entry
 *
 * @author spd
 * @date 2025-01-01
 */
public class GzTraceabilityEntry extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** ID */
    private Long id;

    /** 追溯单ID */
    private Long parentId;

    /** 耗材ID */
    private Long materialId;

    /** 库存ID */
    private Long inventoryId;

    /** 耗材名称 */
    @Excel(name = "耗材名称")
    private String materialName;

    /** 规格 */
    @Excel(name = "规格")
    private String specification;

    /** 型号 */
    @Excel(name = "型号")
    private String model;

    /** 单位 */
    @Excel(name = "单位")
    private String unit;

    /** 数量 */
    @Excel(name = "数量")
    private BigDecimal quantity;

    /** 收费价 */
    @Excel(name = "收费价")
    private BigDecimal chargePrice;

    /** 批号 */
    @Excel(name = "批号")
    private String batchNo;

    /** 有效期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "有效期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date expiryDate;

    /** 院内码 */
    @Excel(name = "院内码")
    private String inHospitalCode;

    /** 生产厂家 */
    @Excel(name = "生产厂家")
    private String manufacturer;

    /** 供应商 */
    @Excel(name = "供应商")
    private String supplier;

    /** 注册证号 */
    @Excel(name = "注册证号")
    private String certificateNo;

    /** 跟台标识（0=否，1=是） */
    @Excel(name = "跟台标识", readConverterExp = "0=否,1=是")
    private String billingFollow;

    /** 删除标识 */
    private String delFlag;

    /** 追溯单状态（1=未审核，2=已审核） */
    private Integer parentOrderStatus;

    /** 耗材对象 */
    private com.spd.foundation.domain.FdMaterial material;

    /** 病人姓名 */
    private String patientName;

    /** 病人性别 */
    private String patientSex;

    /** 手术医生 */
    private String chiefSurgeon;

    /** 手术诊断 */
    private String surgicalDiagnosis;

    /** 计费时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date billingTime;

    /** 扫描人 */
    private String scanUser;

    /** 扫描日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date scanDate;

    /** 生产日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date beginTime;

    /** 批次号 */
    private String batchNumber;

    /** 耗材日期（用于显示生产日期） */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date materialDate;

    /** 批次号（material_no） */
    private String materialNo;

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId()
    {
        return id;
    }

    public void setParentId(Long parentId)
    {
        this.parentId = parentId;
    }

    public Long getParentId()
    {
        return parentId;
    }

    public void setMaterialId(Long materialId)
    {
        this.materialId = materialId;
    }

    public Long getMaterialId()
    {
        return materialId;
    }

    public void setInventoryId(Long inventoryId)
    {
        this.inventoryId = inventoryId;
    }

    public Long getInventoryId()
    {
        return inventoryId;
    }

    public void setMaterialName(String materialName)
    {
        this.materialName = materialName;
    }

    public String getMaterialName()
    {
        return materialName;
    }

    public void setSpecification(String specification)
    {
        this.specification = specification;
    }

    public String getSpecification()
    {
        return specification;
    }

    public void setModel(String model)
    {
        this.model = model;
    }

    public String getModel()
    {
        return model;
    }

    public void setUnit(String unit)
    {
        this.unit = unit;
    }

    public String getUnit()
    {
        return unit;
    }

    public void setQuantity(BigDecimal quantity)
    {
        this.quantity = quantity;
    }

    public BigDecimal getQuantity()
    {
        return quantity;
    }

    public void setChargePrice(BigDecimal chargePrice)
    {
        this.chargePrice = chargePrice;
    }

    public BigDecimal getChargePrice()
    {
        return chargePrice;
    }

    public void setBatchNo(String batchNo)
    {
        this.batchNo = batchNo;
    }

    public String getBatchNo()
    {
        return batchNo;
    }

    public void setExpiryDate(Date expiryDate)
    {
        this.expiryDate = expiryDate;
    }

    public Date getExpiryDate()
    {
        return expiryDate;
    }

    public void setInHospitalCode(String inHospitalCode)
    {
        this.inHospitalCode = inHospitalCode;
    }

    public String getInHospitalCode()
    {
        return inHospitalCode;
    }

    public void setManufacturer(String manufacturer)
    {
        this.manufacturer = manufacturer;
    }

    public String getManufacturer()
    {
        return manufacturer;
    }

    public void setSupplier(String supplier)
    {
        this.supplier = supplier;
    }

    public String getSupplier()
    {
        return supplier;
    }

    public void setCertificateNo(String certificateNo)
    {
        this.certificateNo = certificateNo;
    }

    public String getCertificateNo()
    {
        return certificateNo;
    }

    public void setBillingFollow(String billingFollow)
    {
        this.billingFollow = billingFollow;
    }

    public String getBillingFollow()
    {
        return billingFollow;
    }

    public void setDelFlag(String delFlag)
    {
        this.delFlag = delFlag;
    }

    public String getDelFlag()
    {
        return delFlag;
    }

    public void setParentOrderStatus(Integer parentOrderStatus)
    {
        this.parentOrderStatus = parentOrderStatus;
    }

    public Integer getParentOrderStatus()
    {
        return parentOrderStatus;
    }

    public void setPatientName(String patientName)
    {
        this.patientName = patientName;
    }

    public String getPatientName()
    {
        return patientName;
    }

    public void setPatientSex(String patientSex)
    {
        this.patientSex = patientSex;
    }

    public String getPatientSex()
    {
        return patientSex;
    }

    public void setChiefSurgeon(String chiefSurgeon)
    {
        this.chiefSurgeon = chiefSurgeon;
    }

    public String getChiefSurgeon()
    {
        return chiefSurgeon;
    }

    public void setSurgicalDiagnosis(String surgicalDiagnosis)
    {
        this.surgicalDiagnosis = surgicalDiagnosis;
    }

    public String getSurgicalDiagnosis()
    {
        return surgicalDiagnosis;
    }

    public void setBillingTime(Date billingTime)
    {
        this.billingTime = billingTime;
    }

    public Date getBillingTime()
    {
        return billingTime;
    }

    public void setScanUser(String scanUser)
    {
        this.scanUser = scanUser;
    }

    public String getScanUser()
    {
        return scanUser;
    }

    public void setScanDate(Date scanDate)
    {
        this.scanDate = scanDate;
    }

    public Date getScanDate()
    {
        return scanDate;
    }

    public void setMaterial(com.spd.foundation.domain.FdMaterial material)
    {
        this.material = material;
    }

    public com.spd.foundation.domain.FdMaterial getMaterial()
    {
        return material;
    }

    public void setBeginTime(Date beginTime)
    {
        this.beginTime = beginTime;
    }

    public Date getBeginTime()
    {
        return beginTime;
    }

    public void setBatchNumber(String batchNumber)
    {
        this.batchNumber = batchNumber;
    }

    public String getBatchNumber()
    {
        return batchNumber;
    }

    public void setMaterialDate(Date materialDate)
    {
        this.materialDate = materialDate;
    }

    public Date getMaterialDate()
    {
        return materialDate;
    }

    public void setMaterialNo(String materialNo)
    {
        this.materialNo = materialNo;
    }

    public String getMaterialNo()
    {
        return materialNo;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("parentId", getParentId())
            .append("materialId", getMaterialId())
            .append("inventoryId", getInventoryId())
            .append("materialName", getMaterialName())
            .append("specification", getSpecification())
            .append("model", getModel())
            .append("unit", getUnit())
            .append("quantity", getQuantity())
            .append("chargePrice", getChargePrice())
            .append("batchNo", getBatchNo())
            .append("expiryDate", getExpiryDate())
            .append("inHospitalCode", getInHospitalCode())
            .append("manufacturer", getManufacturer())
            .append("supplier", getSupplier())
            .append("certificateNo", getCertificateNo())
            .append("billingFollow", getBillingFollow())
            .append("delFlag", getDelFlag())
            .toString();
    }
}

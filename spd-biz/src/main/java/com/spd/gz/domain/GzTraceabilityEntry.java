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

    /** 主条码 */
    @Excel(name = "主条码")
    private String masterBarcode;

    /** 辅条码 */
    @Excel(name = "辅条码")
    private String secondaryBarcode;

    /** 生产厂家 */
    @Excel(name = "生产厂家")
    private String manufacturer;

    /** 供应商 */
    @Excel(name = "供应商")
    private String supplier;
    /** 供应商ID */
    private Long supplierId;

    /** 注册证号 */
    @Excel(name = "注册证号")
    private String certificateNo;

    /** 跟台标识（0=否，1=是） */
    @Excel(name = "跟台标识", readConverterExp = "0=否,1=是")
    private String billingFollow;

    /** 删除标识 */
    private String delFlag;

    /** 租户ID */
    private String tenantId;

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

    /** 仓库来源（来自 stk_batch.warehouse_name） */
    private String warehouseName;

    /** 批次来源（来自 stk_batch.batch_source） */
    private String batchSource;

    /** 来源业务类型（来自 stk_batch.origin_business_type） */
    private String originBusinessType;

    /** 供应商展示名（关联供应商档案或明细快照） */
    private String supplierDisplayName;

    /** 收费编码（HIS 收费项目编码或 ID） */
    private String chargeCode;

    /** 病人住院号/门诊号（来自追溯单 hospital_number） */
    private String hospitalNumber;

    /** 就诊类型（来自追溯单 visit_kind） */
    private String visitKind;

    /** 开单科室名称（来自追溯单 apply_dept_id） */
    private String applyDeptName;

    /** 执行科室名称（来自追溯单 exec_dept_id） */
    private String execDeptName;

    /** 核销科室名称（来自追溯单 write_off_dept_id） */
    private String writeOffDeptName;

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

    public void setMasterBarcode(String masterBarcode)
    {
        this.masterBarcode = masterBarcode;
    }

    public String getMasterBarcode()
    {
        return masterBarcode;
    }

    public void setSecondaryBarcode(String secondaryBarcode)
    {
        this.secondaryBarcode = secondaryBarcode;
    }

    public String getSecondaryBarcode()
    {
        return secondaryBarcode;
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
    public void setSupplierId(Long supplierId)
    {
        this.supplierId = supplierId;
    }
    public Long getSupplierId()
    {
        return supplierId;
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

    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }

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

    public void setWarehouseName(String warehouseName)
    {
        this.warehouseName = warehouseName;
    }

    public String getWarehouseName()
    {
        return warehouseName;
    }

    public void setBatchSource(String batchSource)
    {
        this.batchSource = batchSource;
    }

    public String getBatchSource()
    {
        return batchSource;
    }

    public void setOriginBusinessType(String originBusinessType)
    {
        this.originBusinessType = originBusinessType;
    }

    public String getOriginBusinessType()
    {
        return originBusinessType;
    }

    public String getSupplierDisplayName()
    {
        return supplierDisplayName;
    }

    public void setSupplierDisplayName(String supplierDisplayName)
    {
        this.supplierDisplayName = supplierDisplayName;
    }

    public String getChargeCode()
    {
        return chargeCode;
    }

    public void setChargeCode(String chargeCode)
    {
        this.chargeCode = chargeCode;
    }

    public String getHospitalNumber()
    {
        return hospitalNumber;
    }

    public void setHospitalNumber(String hospitalNumber)
    {
        this.hospitalNumber = hospitalNumber;
    }

    public String getVisitKind()
    {
        return visitKind;
    }

    public void setVisitKind(String visitKind)
    {
        this.visitKind = visitKind;
    }

    public String getApplyDeptName()
    {
        return applyDeptName;
    }

    public void setApplyDeptName(String applyDeptName)
    {
        this.applyDeptName = applyDeptName;
    }

    public String getExecDeptName()
    {
        return execDeptName;
    }

    public void setExecDeptName(String execDeptName)
    {
        this.execDeptName = execDeptName;
    }

    public String getWriteOffDeptName()
    {
        return writeOffDeptName;
    }

    public void setWriteOffDeptName(String writeOffDeptName)
    {
        this.writeOffDeptName = writeOffDeptName;
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
            .append("masterBarcode", getMasterBarcode())
            .append("secondaryBarcode", getSecondaryBarcode())
            .append("inHospitalCode", getInHospitalCode())
            .append("manufacturer", getManufacturer())
            .append("supplier", getSupplier())
            .append("supplierId", getSupplierId())
            .append("certificateNo", getCertificateNo())
            .append("billingFollow", getBillingFollow())
            .append("delFlag", getDelFlag())
            .append("warehouseName", getWarehouseName())
            .append("batchSource", getBatchSource())
            .append("originBusinessType", getOriginBusinessType())
            .toString();
    }
}

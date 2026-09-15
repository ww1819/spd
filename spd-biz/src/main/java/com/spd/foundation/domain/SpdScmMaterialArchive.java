package com.spd.foundation.domain;

import java.math.BigDecimal;
import java.util.Date;

/**
 * SPD 侧供应链产品档案镜像 spd_scm_material_archive
 */
public class SpdScmMaterialArchive
{
    private String id;
    private String tenantId;
    private String scmArchiveId;
    private String hospitalCode;
    private String scmSupplierCode;
    private String spdMaterialId;
    private String spdSupplierId;
    private String materialName;
    private String pinyinCode;
    private String specification;
    private String model;
    private String unitName;
    private BigDecimal price;
    private BigDecimal salePrice;
    private String registerNo;
    private String registerName;
    private String manufacturerName;
    private String udiCode;
    private String medicalName;
    private String medicalNo;
    private String brand;
    private String payloadJson;
    private Date scmUpdateTime;
    private String lastSyncBatchId;
    /** 0未应用 1已应用 2部分应用 */
    private String applyStatus;
    private String lastApplyLogId;
    private String delFlag;
    private String createBy;
    private Date createTime;
    private String updateBy;
    private Date updateTime;
    private String remark;

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getTenantId()
    {
        return tenantId;
    }

    public void setTenantId(String tenantId)
    {
        this.tenantId = tenantId;
    }

    public String getScmArchiveId()
    {
        return scmArchiveId;
    }

    public void setScmArchiveId(String scmArchiveId)
    {
        this.scmArchiveId = scmArchiveId;
    }

    public String getHospitalCode()
    {
        return hospitalCode;
    }

    public void setHospitalCode(String hospitalCode)
    {
        this.hospitalCode = hospitalCode;
    }

    public String getScmSupplierCode()
    {
        return scmSupplierCode;
    }

    public void setScmSupplierCode(String scmSupplierCode)
    {
        this.scmSupplierCode = scmSupplierCode;
    }

    public String getSpdMaterialId()
    {
        return spdMaterialId;
    }

    public void setSpdMaterialId(String spdMaterialId)
    {
        this.spdMaterialId = spdMaterialId;
    }

    public String getSpdSupplierId()
    {
        return spdSupplierId;
    }

    public void setSpdSupplierId(String spdSupplierId)
    {
        this.spdSupplierId = spdSupplierId;
    }

    public String getMaterialName()
    {
        return materialName;
    }

    public void setMaterialName(String materialName)
    {
        this.materialName = materialName;
    }

    public String getPinyinCode()
    {
        return pinyinCode;
    }

    public void setPinyinCode(String pinyinCode)
    {
        this.pinyinCode = pinyinCode;
    }

    public String getSpecification()
    {
        return specification;
    }

    public void setSpecification(String specification)
    {
        this.specification = specification;
    }

    public String getModel()
    {
        return model;
    }

    public void setModel(String model)
    {
        this.model = model;
    }

    public String getUnitName()
    {
        return unitName;
    }

    public void setUnitName(String unitName)
    {
        this.unitName = unitName;
    }

    public BigDecimal getPrice()
    {
        return price;
    }

    public void setPrice(BigDecimal price)
    {
        this.price = price;
    }

    public BigDecimal getSalePrice()
    {
        return salePrice;
    }

    public void setSalePrice(BigDecimal salePrice)
    {
        this.salePrice = salePrice;
    }

    public String getRegisterNo()
    {
        return registerNo;
    }

    public void setRegisterNo(String registerNo)
    {
        this.registerNo = registerNo;
    }

    public String getRegisterName()
    {
        return registerName;
    }

    public void setRegisterName(String registerName)
    {
        this.registerName = registerName;
    }

    public String getManufacturerName()
    {
        return manufacturerName;
    }

    public void setManufacturerName(String manufacturerName)
    {
        this.manufacturerName = manufacturerName;
    }

    public String getUdiCode()
    {
        return udiCode;
    }

    public void setUdiCode(String udiCode)
    {
        this.udiCode = udiCode;
    }

    public String getMedicalName()
    {
        return medicalName;
    }

    public void setMedicalName(String medicalName)
    {
        this.medicalName = medicalName;
    }

    public String getMedicalNo()
    {
        return medicalNo;
    }

    public void setMedicalNo(String medicalNo)
    {
        this.medicalNo = medicalNo;
    }

    public String getBrand()
    {
        return brand;
    }

    public void setBrand(String brand)
    {
        this.brand = brand;
    }

    public String getPayloadJson()
    {
        return payloadJson;
    }

    public void setPayloadJson(String payloadJson)
    {
        this.payloadJson = payloadJson;
    }

    public Date getScmUpdateTime()
    {
        return scmUpdateTime;
    }

    public void setScmUpdateTime(Date scmUpdateTime)
    {
        this.scmUpdateTime = scmUpdateTime;
    }

    public String getLastSyncBatchId()
    {
        return lastSyncBatchId;
    }

    public void setLastSyncBatchId(String lastSyncBatchId)
    {
        this.lastSyncBatchId = lastSyncBatchId;
    }

    public String getApplyStatus()
    {
        return applyStatus;
    }

    public void setApplyStatus(String applyStatus)
    {
        this.applyStatus = applyStatus;
    }

    public String getLastApplyLogId()
    {
        return lastApplyLogId;
    }

    public void setLastApplyLogId(String lastApplyLogId)
    {
        this.lastApplyLogId = lastApplyLogId;
    }

    public String getDelFlag()
    {
        return delFlag;
    }

    public void setDelFlag(String delFlag)
    {
        this.delFlag = delFlag;
    }

    public String getCreateBy()
    {
        return createBy;
    }

    public void setCreateBy(String createBy)
    {
        this.createBy = createBy;
    }

    public Date getCreateTime()
    {
        return createTime;
    }

    public void setCreateTime(Date createTime)
    {
        this.createTime = createTime;
    }

    public String getUpdateBy()
    {
        return updateBy;
    }

    public void setUpdateBy(String updateBy)
    {
        this.updateBy = updateBy;
    }

    public Date getUpdateTime()
    {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime)
    {
        this.updateTime = updateTime;
    }

    public String getRemark()
    {
        return remark;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }
}

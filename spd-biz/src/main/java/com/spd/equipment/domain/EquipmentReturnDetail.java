package com.spd.equipment.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.spd.common.annotation.Excel;
import com.spd.common.core.domain.BaseEntity;

/**
 * 设备退货明细对象 equipment_return_detail
 * 
 * @author spd
 * @date 2024-01-01
 */
public class EquipmentReturnDetail extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 明细ID */
    private Long detailId;

    /** 退货单ID */
    private Long returnId;

    /** 设备编码 */
    @Excel(name = "设备编码")
    private String equipmentCode;

    /** 设备名称 */
    @Excel(name = "设备名称")
    private String equipmentName;

    /** 规格 */
    @Excel(name = "规格")
    private String specification;

    /** 型号 */
    @Excel(name = "型号")
    private String model;

    /** 数量 */
    @Excel(name = "数量")
    private Integer quantity;

    /** 单价 */
    @Excel(name = "单价")
    private BigDecimal unitPrice;

    /** 总价 */
    @Excel(name = "总价")
    private BigDecimal totalPrice;

    /** 备注 */
    @Excel(name = "备注")
    private String remark;

    /** 规格 */
    private String spec;

    /** 型号 */
    private String modelNo;

    /** 分类编码 */
    private String categoryCode;

    /** 商检编号 */
    private String inspectionNo;

    /** 注册证号 */
    private String registrationNo;

    /** 序列号 */
    private String serialNo;

    /** 生产日期 */
    private Date productionDate;

    /** 共计箱数 */
    private Integer totalBoxes;

    /** 金额 */
    private BigDecimal amount;

    /** 大写金额 */
    private String amountInWords;

    /** 使用期限 */
    private String usagePeriod;

    /** 生产商 */
    private String manufacturer;

    /** 生产商联系方式 */
    private String manufacturerContact;

    /** 经销商 */
    private String dealer;

    /** 经销商联系方式 */
    private String dealerContact;

    /** 外包装类型 */
    private String packagingType;

    /** 外包装状态 */
    private String packagingStatus;

    /** 外包装破损情况 */
    private String packagingDamage;

    /** 外形 */
    private String appearance;

    /** 附件 */
    private String accessories;

    /** 技术参数相符情况 */
    private String techParamMatch;

    /** 说明书份数 */
    private Integer manualCount;

    /** 合格证份数 */
    private Integer certificateCount;

    /** 装箱单份数 */
    private Integer packingListCount;

    /** 安装验收单份数 */
    private Integer acceptanceFormCount;

    /** 验收结果 */
    private String acceptanceResult;

    /** 验收日期 */
    private Date acceptanceDate;

    public void setDetailId(Long detailId) 
    {
        this.detailId = detailId;
    }

    public Long getDetailId() 
    {
        return detailId;
    }

    public void setReturnId(Long returnId) 
    {
        this.returnId = returnId;
    }

    public Long getReturnId() 
    {
        return returnId;
    }

    public void setEquipmentCode(String equipmentCode) 
    {
        this.equipmentCode = equipmentCode;
    }

    public String getEquipmentCode() 
    {
        return equipmentCode;
    }

    public void setEquipmentName(String equipmentName) 
    {
        this.equipmentName = equipmentName;
    }

    public String getEquipmentName() 
    {
        return equipmentName;
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

    public void setQuantity(Integer quantity) 
    {
        this.quantity = quantity;
    }

    public Integer getQuantity() 
    {
        return quantity;
    }

    public void setUnitPrice(BigDecimal unitPrice) 
    {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getUnitPrice() 
    {
        return unitPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) 
    {
        this.totalPrice = totalPrice;
    }

    public BigDecimal getTotalPrice() 
    {
        return totalPrice;
    }

    public void setRemark(String remark) 
    {
        this.remark = remark;
    }

    public String getRemark() 
    {
        return remark;
    }

    public void setSpec(String spec) 
    {
        this.spec = spec;
    }

    public String getSpec() 
    {
        return spec;
    }

    public void setModelNo(String modelNo) 
    {
        this.modelNo = modelNo;
    }

    public String getModelNo() 
    {
        return modelNo;
    }

    public void setCategoryCode(String categoryCode) 
    {
        this.categoryCode = categoryCode;
    }

    public String getCategoryCode() 
    {
        return categoryCode;
    }

    public void setInspectionNo(String inspectionNo) 
    {
        this.inspectionNo = inspectionNo;
    }

    public String getInspectionNo() 
    {
        return inspectionNo;
    }

    public void setRegistrationNo(String registrationNo) 
    {
        this.registrationNo = registrationNo;
    }

    public String getRegistrationNo() 
    {
        return registrationNo;
    }

    public void setSerialNo(String serialNo) 
    {
        this.serialNo = serialNo;
    }

    public String getSerialNo() 
    {
        return serialNo;
    }

    public void setProductionDate(Date productionDate) 
    {
        this.productionDate = productionDate;
    }

    public Date getProductionDate() 
    {
        return productionDate;
    }

    public void setTotalBoxes(Integer totalBoxes) 
    {
        this.totalBoxes = totalBoxes;
    }

    public Integer getTotalBoxes() 
    {
        return totalBoxes;
    }

    public void setAmount(BigDecimal amount) 
    {
        this.amount = amount;
    }

    public BigDecimal getAmount() 
    {
        return amount;
    }

    public void setAmountInWords(String amountInWords) 
    {
        this.amountInWords = amountInWords;
    }

    public String getAmountInWords() 
    {
        return amountInWords;
    }

    public void setUsagePeriod(String usagePeriod) 
    {
        this.usagePeriod = usagePeriod;
    }

    public String getUsagePeriod() 
    {
        return usagePeriod;
    }

    public void setManufacturer(String manufacturer) 
    {
        this.manufacturer = manufacturer;
    }

    public String getManufacturer() 
    {
        return manufacturer;
    }

    public void setManufacturerContact(String manufacturerContact) 
    {
        this.manufacturerContact = manufacturerContact;
    }

    public String getManufacturerContact() 
    {
        return manufacturerContact;
    }

    public void setDealer(String dealer) 
    {
        this.dealer = dealer;
    }

    public String getDealer() 
    {
        return dealer;
    }

    public void setDealerContact(String dealerContact) 
    {
        this.dealerContact = dealerContact;
    }

    public String getDealerContact() 
    {
        return dealerContact;
    }

    public void setPackagingType(String packagingType) 
    {
        this.packagingType = packagingType;
    }

    public String getPackagingType() 
    {
        return packagingType;
    }

    public void setPackagingStatus(String packagingStatus) 
    {
        this.packagingStatus = packagingStatus;
    }

    public String getPackagingStatus() 
    {
        return packagingStatus;
    }

    public void setPackagingDamage(String packagingDamage) 
    {
        this.packagingDamage = packagingDamage;
    }

    public String getPackagingDamage() 
    {
        return packagingDamage;
    }

    public void setAppearance(String appearance) 
    {
        this.appearance = appearance;
    }

    public String getAppearance() 
    {
        return appearance;
    }

    public void setAccessories(String accessories) 
    {
        this.accessories = accessories;
    }

    public String getAccessories() 
    {
        return accessories;
    }

    public void setTechParamMatch(String techParamMatch) 
    {
        this.techParamMatch = techParamMatch;
    }

    public String getTechParamMatch() 
    {
        return techParamMatch;
    }

    public void setManualCount(Integer manualCount) 
    {
        this.manualCount = manualCount;
    }

    public Integer getManualCount() 
    {
        return manualCount;
    }

    public void setCertificateCount(Integer certificateCount) 
    {
        this.certificateCount = certificateCount;
    }

    public Integer getCertificateCount() 
    {
        return certificateCount;
    }

    public void setPackingListCount(Integer packingListCount) 
    {
        this.packingListCount = packingListCount;
    }

    public Integer getPackingListCount() 
    {
        return packingListCount;
    }

    public void setAcceptanceFormCount(Integer acceptanceFormCount) 
    {
        this.acceptanceFormCount = acceptanceFormCount;
    }

    public Integer getAcceptanceFormCount() 
    {
        return acceptanceFormCount;
    }

    public void setAcceptanceResult(String acceptanceResult) 
    {
        this.acceptanceResult = acceptanceResult;
    }

    public String getAcceptanceResult() 
    {
        return acceptanceResult;
    }

    public void setAcceptanceDate(Date acceptanceDate) 
    {
        this.acceptanceDate = acceptanceDate;
    }

    public Date getAcceptanceDate() 
    {
        return acceptanceDate;
    }
}

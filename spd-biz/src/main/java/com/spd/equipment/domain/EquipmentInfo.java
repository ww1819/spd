package com.spd.equipment.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.spd.common.annotation.Excel;
import com.spd.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 设备信息管理对象 equipment_info
 * 
 * @author spd
 * @date 2024-01-01
 */
public class EquipmentInfo extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private String id;

    /** 资产编号 */
    @Excel(name = "资产编号")
    private String assetCode;

    /** 院内编码（仓库） */
    @Excel(name = "仓库")
    private String hospitalCode;

    /** 条码号（价格/元） */
    @Excel(name = "价格/元")
    private String barcode;

    /** 资产名称 */
    @Excel(name = "资产名称")
    private String assetName;

    /** 资产别名 */
    private String assetAlias;

    /** 辅助分类 */
    @Excel(name = "辅助分类")
    private String auxiliaryCategory;

    /** 资产状态（0停用 1启用） */
    @Excel(name = "资产状态", readConverterExp = "0=停用,1=启用")
    private String assetStatus;

    /** 财务分类 */
    @Excel(name = "财务分类")
    private String financialCategory;

    /** 财务编号 */
    @Excel(name = "财务编号")
    private String financialCode;

    /** 品牌 */
    @Excel(name = "品牌")
    private String brand;

    /** 产地 */
    @Excel(name = "产地")
    private String origin;

    /** 国别 */
    @Excel(name = "国别")
    private String country;

    /** 规格 */
    @Excel(name = "规格")
    private String specification;

    /** 型号 */
    @Excel(name = "型号")
    private String model;

    /** 单位 */
    @Excel(name = "单位")
    private String unit;

    /** 档案编号（保修类型） */
    @Excel(name = "保修类型")
    private String archiveCode;

    /** 资产序列号 */
    @Excel(name = "资产序列号")
    private String serialNumber;

    /** 资产负责人 */
    @Excel(name = "资产负责人")
    private String assetManager;

    /** 维修负责人 */
    @Excel(name = "维修负责人")
    private String maintenanceManager;

    /** 使用科室 */
    @Excel(name = "使用科室")
    private String useDepartment;

    /** 管理科室 */
    @Excel(name = "管理科室")
    private String manageDepartment;

    /** 存放地点 */
    @Excel(name = "存放地点")
    private String storageLocation;

    /** 资产类型（资产分类） */
    @Excel(name = "资产分类")
    private String assetType;

    /** 出厂编号 */
    @Excel(name = "出厂编号")
    private String factoryNumber;

    /** 出厂日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "出厂日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date productionDate;

    /** 注册证件号 */
    @Excel(name = "注册证件号")
    private String registrationNumber;

    /** 预计开机工作日 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "预计开机工作日", width = 30, dateFormat = "yyyy-MM-dd")
    private Date expectedOperationDate;

    /** 资金来源 */
    @Excel(name = "资金来源")
    private String fundSource;

    /** 发票编号 */
    @Excel(name = "发票编号")
    private String invoiceNumber;

    /** 发票金额 */
    @Excel(name = "发票金额")
    private BigDecimal invoiceAmount;

    /** 附资产标志（0否 1是） */
    @Excel(name = "附资产标志", readConverterExp = "0=否,1=是")
    private String attachedAssetFlag;

    /** 急救资产（0否 1是） */
    @Excel(name = "急救资产", readConverterExp = "0=否,1=是")
    private String emergencyAsset;

    /** 特种资产（0否 1是） */
    @Excel(name = "特种资产", readConverterExp = "0=否,1=是")
    private String specialAsset;

    /** 计量资产（0否 1是） */
    @Excel(name = "计量资产", readConverterExp = "0=否,1=是")
    private String measurementAsset;

    /** 附属资料（JSON格式） */
    @JsonIgnore
    private String attachedMaterials;

    /** 附属资料列表 */
    @JsonProperty("attachedMaterials")
    private List<String> attachedMaterialsList;

    /** 效益分析（0否 1是） */
    @Excel(name = "效益分析", readConverterExp = "0=否,1=是")
    private String benefitAnalysis;

    /** 设备功率 */
    @Excel(name = "设备功率")
    private String power;

    /** 理论开机时间（小时/天） */
    @Excel(name = "理论开机时间")
    private Integer theoryOperationTime;

    /** 公用设备（0否 1是） */
    @Excel(name = "公用设备", readConverterExp = "0=否,1=是")
    private String publicEquipment;

    /** 录入人 */
    @Excel(name = "录入人")
    private String creator;

    /** 录入日期 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "录入日期", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /** 最后修改人 */
    private String modifier;

    /** 最后修改时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date modifyTime;

    /** 档案使用情况（0否 1是） */
    @Excel(name = "档案使用情况", readConverterExp = "0=否,1=是")
    private String archiveUsage;

    /** 合同与清单是否一致（0否 1是） */
    @Excel(name = "合同与清单是否一致", readConverterExp = "0=否,1=是")
    private String contractConsistent;

    /** 是否专网资产（0否 1是） */
    @Excel(name = "是否专网资产", readConverterExp = "0=否,1=是")
    private String specialNetworkAsset;

    /** 楼宇 */
    @Excel(name = "楼宇")
    private String building;

    /** 楼层 */
    @Excel(name = "楼层")
    private String floor;

    /** 数量 */
    @Excel(name = "数量")
    private Integer quantity;

    /** 合同名称 */
    private String contractName;

    /** 合同价格 */
    @Excel(name = "合同价格")
    private BigDecimal contractPrice;

    /** 资产原值 */
    @Excel(name = "资产原值")
    private BigDecimal originalPrice;

    /** 签订日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date signDate;

    /** 购入日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date purchaseDate;

    /** 验收合格日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date acceptanceDate;

    /** 送审时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date reviewTime;

    /** 保修到期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date warrantyExpireDate;

    /** 中标日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date bidDate;

    /** 通知供货时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date supplyNoticeDate;

    /** 首次验收日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date firstAcceptanceDate;

    /** 交货截止日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date deliveryDeadline;

    /** 二次验收日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date secondAcceptanceDate;

    /** 出保日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date warrantyOutDate;

    /** 供应商 */
    private String supplier;

    /** 供应商联系人 */
    private String supplierContact;

    /** 供应电话 */
    private String supplierPhone;

    /** 维修公司 */
    private String maintenanceCompany;

    /** 维修联系人 */
    private String maintenanceContact;

    /** 维修电话 */
    private String maintenancePhone;

    /** 生产厂商 */
    private String manufacturer;

    /** 采购方式 */
    private String purchaseMethod;

    /** 招标形式 */
    private String biddingForm;

    /** 单项预算 */
    private BigDecimal singleBudget;

    /** 立项依据 */
    private String projectBasis;

    /** 招标编号 */
    private String biddingNumber;

    /** 招标日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date biddingDate;

    /** 中标金额 */
    private BigDecimal biddingAmount;

    /** 删除标志（0代表存在 2代表删除） */
    private String delFlag;

    /** 备注 */
    @Excel(name = "备注")
    private String remark;

    public void setId(String id) 
    {
        this.id = id;
    }

    public String getId() 
    {
        return id;
    }

    public void setAssetCode(String assetCode) 
    {
        this.assetCode = assetCode;
    }

    public String getAssetCode() 
    {
        return assetCode;
    }

    public void setHospitalCode(String hospitalCode) 
    {
        this.hospitalCode = hospitalCode;
    }

    public String getHospitalCode() 
    {
        return hospitalCode;
    }

    public void setBarcode(String barcode) 
    {
        this.barcode = barcode;
    }

    public String getBarcode() 
    {
        return barcode;
    }

    public void setAssetName(String assetName) 
    {
        this.assetName = assetName;
    }

    public String getAssetName() 
    {
        return assetName;
    }

    public void setAssetAlias(String assetAlias) 
    {
        this.assetAlias = assetAlias;
    }

    public String getAssetAlias() 
    {
        return assetAlias;
    }

    public void setAuxiliaryCategory(String auxiliaryCategory) 
    {
        this.auxiliaryCategory = auxiliaryCategory;
    }

    public String getAuxiliaryCategory() 
    {
        return auxiliaryCategory;
    }

    public void setAssetStatus(String assetStatus) 
    {
        this.assetStatus = assetStatus;
    }

    public String getAssetStatus() 
    {
        return assetStatus;
    }

    public void setFinancialCategory(String financialCategory) 
    {
        this.financialCategory = financialCategory;
    }

    public String getFinancialCategory() 
    {
        return financialCategory;
    }

    public void setFinancialCode(String financialCode) 
    {
        this.financialCode = financialCode;
    }

    public String getFinancialCode() 
    {
        return financialCode;
    }

    public void setBrand(String brand) 
    {
        this.brand = brand;
    }

    public String getBrand() 
    {
        return brand;
    }

    public void setOrigin(String origin) 
    {
        this.origin = origin;
    }

    public String getOrigin() 
    {
        return origin;
    }

    public void setCountry(String country) 
    {
        this.country = country;
    }

    public String getCountry() 
    {
        return country;
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

    public void setArchiveCode(String archiveCode) 
    {
        this.archiveCode = archiveCode;
    }

    public String getArchiveCode() 
    {
        return archiveCode;
    }

    public void setSerialNumber(String serialNumber) 
    {
        this.serialNumber = serialNumber;
    }

    public String getSerialNumber() 
    {
        return serialNumber;
    }

    public void setAssetManager(String assetManager) 
    {
        this.assetManager = assetManager;
    }

    public String getAssetManager() 
    {
        return assetManager;
    }

    public void setMaintenanceManager(String maintenanceManager) 
    {
        this.maintenanceManager = maintenanceManager;
    }

    public String getMaintenanceManager() 
    {
        return maintenanceManager;
    }

    public void setUseDepartment(String useDepartment) 
    {
        this.useDepartment = useDepartment;
    }

    public String getUseDepartment() 
    {
        return useDepartment;
    }

    public void setManageDepartment(String manageDepartment) 
    {
        this.manageDepartment = manageDepartment;
    }

    public String getManageDepartment() 
    {
        return manageDepartment;
    }

    public void setStorageLocation(String storageLocation) 
    {
        this.storageLocation = storageLocation;
    }

    public String getStorageLocation() 
    {
        return storageLocation;
    }

    public void setAssetType(String assetType) 
    {
        this.assetType = assetType;
    }

    public String getAssetType() 
    {
        return assetType;
    }

    public void setFactoryNumber(String factoryNumber) 
    {
        this.factoryNumber = factoryNumber;
    }

    public String getFactoryNumber() 
    {
        return factoryNumber;
    }

    public void setProductionDate(Date productionDate) 
    {
        this.productionDate = productionDate;
    }

    public Date getProductionDate() 
    {
        return productionDate;
    }

    public void setRegistrationNumber(String registrationNumber) 
    {
        this.registrationNumber = registrationNumber;
    }

    public String getRegistrationNumber() 
    {
        return registrationNumber;
    }

    public void setExpectedOperationDate(Date expectedOperationDate) 
    {
        this.expectedOperationDate = expectedOperationDate;
    }

    public Date getExpectedOperationDate() 
    {
        return expectedOperationDate;
    }

    public void setFundSource(String fundSource) 
    {
        this.fundSource = fundSource;
    }

    public String getFundSource() 
    {
        return fundSource;
    }

    public void setInvoiceNumber(String invoiceNumber) 
    {
        this.invoiceNumber = invoiceNumber;
    }

    public String getInvoiceNumber() 
    {
        return invoiceNumber;
    }

    public void setInvoiceAmount(BigDecimal invoiceAmount) 
    {
        this.invoiceAmount = invoiceAmount;
    }

    public BigDecimal getInvoiceAmount() 
    {
        return invoiceAmount;
    }

    public void setAttachedAssetFlag(String attachedAssetFlag) 
    {
        this.attachedAssetFlag = attachedAssetFlag;
    }

    public String getAttachedAssetFlag() 
    {
        return attachedAssetFlag;
    }

    public void setEmergencyAsset(String emergencyAsset) 
    {
        this.emergencyAsset = emergencyAsset;
    }

    public String getEmergencyAsset() 
    {
        return emergencyAsset;
    }

    public void setSpecialAsset(String specialAsset) 
    {
        this.specialAsset = specialAsset;
    }

    public String getSpecialAsset() 
    {
        return specialAsset;
    }

    public void setMeasurementAsset(String measurementAsset) 
    {
        this.measurementAsset = measurementAsset;
    }

    public String getMeasurementAsset() 
    {
        return measurementAsset;
    }

    public void setAttachedMaterials(String attachedMaterials) 
    {
        this.attachedMaterials = attachedMaterials;
    }

    public String getAttachedMaterials() 
    {
        return attachedMaterials;
    }

    public void setAttachedMaterialsList(List<String> attachedMaterialsList) 
    {
        this.attachedMaterialsList = attachedMaterialsList;
    }

    public List<String> getAttachedMaterialsList() 
    {
        return attachedMaterialsList;
    }

    public void setBenefitAnalysis(String benefitAnalysis) 
    {
        this.benefitAnalysis = benefitAnalysis;
    }

    public String getBenefitAnalysis() 
    {
        return benefitAnalysis;
    }

    public void setPower(String power) 
    {
        this.power = power;
    }

    public String getPower() 
    {
        return power;
    }

    public void setTheoryOperationTime(Integer theoryOperationTime) 
    {
        this.theoryOperationTime = theoryOperationTime;
    }

    public Integer getTheoryOperationTime() 
    {
        return theoryOperationTime;
    }

    public void setPublicEquipment(String publicEquipment) 
    {
        this.publicEquipment = publicEquipment;
    }

    public String getPublicEquipment() 
    {
        return publicEquipment;
    }

    public void setCreator(String creator) 
    {
        this.creator = creator;
    }

    public String getCreator() 
    {
        return creator;
    }

    public void setCreateTime(Date createTime) 
    {
        this.createTime = createTime;
    }

    public Date getCreateTime() 
    {
        return createTime;
    }

    public void setModifier(String modifier) 
    {
        this.modifier = modifier;
    }

    public String getModifier() 
    {
        return modifier;
    }

    public void setModifyTime(Date modifyTime) 
    {
        this.modifyTime = modifyTime;
    }

    public Date getModifyTime() 
    {
        return modifyTime;
    }

    public void setArchiveUsage(String archiveUsage) 
    {
        this.archiveUsage = archiveUsage;
    }

    public String getArchiveUsage() 
    {
        return archiveUsage;
    }

    public void setContractConsistent(String contractConsistent) 
    {
        this.contractConsistent = contractConsistent;
    }

    public String getContractConsistent() 
    {
        return contractConsistent;
    }

    public void setSpecialNetworkAsset(String specialNetworkAsset) 
    {
        this.specialNetworkAsset = specialNetworkAsset;
    }

    public String getSpecialNetworkAsset() 
    {
        return specialNetworkAsset;
    }

    public void setBuilding(String building) 
    {
        this.building = building;
    }

    public String getBuilding() 
    {
        return building;
    }

    public void setFloor(String floor) 
    {
        this.floor = floor;
    }

    public String getFloor() 
    {
        return floor;
    }

    public void setQuantity(Integer quantity) 
    {
        this.quantity = quantity;
    }

    public Integer getQuantity() 
    {
        return quantity;
    }

    public void setContractName(String contractName) 
    {
        this.contractName = contractName;
    }

    public String getContractName() 
    {
        return contractName;
    }

    public void setContractPrice(BigDecimal contractPrice) 
    {
        this.contractPrice = contractPrice;
    }

    public BigDecimal getContractPrice() 
    {
        return contractPrice;
    }

    public void setSignDate(Date signDate) 
    {
        this.signDate = signDate;
    }

    public Date getSignDate() 
    {
        return signDate;
    }

    public void setPurchaseDate(Date purchaseDate) 
    {
        this.purchaseDate = purchaseDate;
    }

    public Date getPurchaseDate() 
    {
        return purchaseDate;
    }

    public void setAcceptanceDate(Date acceptanceDate) 
    {
        this.acceptanceDate = acceptanceDate;
    }

    public Date getAcceptanceDate() 
    {
        return acceptanceDate;
    }

    public void setReviewTime(Date reviewTime) 
    {
        this.reviewTime = reviewTime;
    }

    public Date getReviewTime() 
    {
        return reviewTime;
    }

    public void setWarrantyExpireDate(Date warrantyExpireDate) 
    {
        this.warrantyExpireDate = warrantyExpireDate;
    }

    public Date getWarrantyExpireDate() 
    {
        return warrantyExpireDate;
    }

    public void setBidDate(Date bidDate) 
    {
        this.bidDate = bidDate;
    }

    public Date getBidDate() 
    {
        return bidDate;
    }

    public void setSupplyNoticeDate(Date supplyNoticeDate) 
    {
        this.supplyNoticeDate = supplyNoticeDate;
    }

    public Date getSupplyNoticeDate() 
    {
        return supplyNoticeDate;
    }

    public void setFirstAcceptanceDate(Date firstAcceptanceDate) 
    {
        this.firstAcceptanceDate = firstAcceptanceDate;
    }

    public Date getFirstAcceptanceDate() 
    {
        return firstAcceptanceDate;
    }

    public void setDeliveryDeadline(Date deliveryDeadline) 
    {
        this.deliveryDeadline = deliveryDeadline;
    }

    public Date getDeliveryDeadline() 
    {
        return deliveryDeadline;
    }

    public void setSecondAcceptanceDate(Date secondAcceptanceDate) 
    {
        this.secondAcceptanceDate = secondAcceptanceDate;
    }

    public Date getSecondAcceptanceDate() 
    {
        return secondAcceptanceDate;
    }

    public void setWarrantyOutDate(Date warrantyOutDate) 
    {
        this.warrantyOutDate = warrantyOutDate;
    }

    public Date getWarrantyOutDate() 
    {
        return warrantyOutDate;
    }

    public void setSupplier(String supplier) 
    {
        this.supplier = supplier;
    }

    public String getSupplier() 
    {
        return supplier;
    }

    public void setSupplierContact(String supplierContact) 
    {
        this.supplierContact = supplierContact;
    }

    public String getSupplierContact() 
    {
        return supplierContact;
    }

    public void setSupplierPhone(String supplierPhone) 
    {
        this.supplierPhone = supplierPhone;
    }

    public String getSupplierPhone() 
    {
        return supplierPhone;
    }

    public void setMaintenanceCompany(String maintenanceCompany) 
    {
        this.maintenanceCompany = maintenanceCompany;
    }

    public String getMaintenanceCompany() 
    {
        return maintenanceCompany;
    }

    public void setMaintenanceContact(String maintenanceContact) 
    {
        this.maintenanceContact = maintenanceContact;
    }

    public String getMaintenanceContact() 
    {
        return maintenanceContact;
    }

    public void setMaintenancePhone(String maintenancePhone) 
    {
        this.maintenancePhone = maintenancePhone;
    }

    public String getMaintenancePhone() 
    {
        return maintenancePhone;
    }

    public void setManufacturer(String manufacturer) 
    {
        this.manufacturer = manufacturer;
    }

    public String getManufacturer() 
    {
        return manufacturer;
    }

    public void setPurchaseMethod(String purchaseMethod) 
    {
        this.purchaseMethod = purchaseMethod;
    }

    public String getPurchaseMethod() 
    {
        return purchaseMethod;
    }

    public void setBiddingForm(String biddingForm) 
    {
        this.biddingForm = biddingForm;
    }

    public String getBiddingForm() 
    {
        return biddingForm;
    }

    public void setSingleBudget(BigDecimal singleBudget) 
    {
        this.singleBudget = singleBudget;
    }

    public BigDecimal getSingleBudget() 
    {
        return singleBudget;
    }

    public void setProjectBasis(String projectBasis) 
    {
        this.projectBasis = projectBasis;
    }

    public String getProjectBasis() 
    {
        return projectBasis;
    }

    public void setBiddingNumber(String biddingNumber) 
    {
        this.biddingNumber = biddingNumber;
    }

    public String getBiddingNumber() 
    {
        return biddingNumber;
    }

    public void setBiddingDate(Date biddingDate) 
    {
        this.biddingDate = biddingDate;
    }

    public Date getBiddingDate() 
    {
        return biddingDate;
    }

    public void setBiddingAmount(BigDecimal biddingAmount) 
    {
        this.biddingAmount = biddingAmount;
    }

    public BigDecimal getBiddingAmount() 
    {
        return biddingAmount;
    }

    public void setDelFlag(String delFlag) 
    {
        this.delFlag = delFlag;
    }

    public String getDelFlag() 
    {
        return delFlag;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("assetCode", getAssetCode())
            .append("hospitalCode", getHospitalCode())
            .append("barcode", getBarcode())
            .append("assetName", getAssetName())
            .append("assetAlias", getAssetAlias())
            .append("auxiliaryCategory", getAuxiliaryCategory())
            .append("assetStatus", getAssetStatus())
            .append("financialCategory", getFinancialCategory())
            .append("financialCode", getFinancialCode())
            .append("brand", getBrand())
            .append("origin", getOrigin())
            .append("country", getCountry())
            .append("specification", getSpecification())
            .append("model", getModel())
            .append("unit", getUnit())
            .append("archiveCode", getArchiveCode())
            .append("serialNumber", getSerialNumber())
            .append("assetManager", getAssetManager())
            .append("maintenanceManager", getMaintenanceManager())
            .append("useDepartment", getUseDepartment())
            .append("manageDepartment", getManageDepartment())
            .append("storageLocation", getStorageLocation())
            .append("assetType", getAssetType())
            .append("factoryNumber", getFactoryNumber())
            .append("productionDate", getProductionDate())
            .append("registrationNumber", getRegistrationNumber())
            .append("expectedOperationDate", getExpectedOperationDate())
            .append("fundSource", getFundSource())
            .append("invoiceNumber", getInvoiceNumber())
            .append("invoiceAmount", getInvoiceAmount())
            .append("attachedAssetFlag", getAttachedAssetFlag())
            .append("emergencyAsset", getEmergencyAsset())
            .append("specialAsset", getSpecialAsset())
            .append("measurementAsset", getMeasurementAsset())
            .append("attachedMaterials", getAttachedMaterials())
            .append("benefitAnalysis", getBenefitAnalysis())
            .append("remark", getRemark())
            .append("power", getPower())
            .append("theoryOperationTime", getTheoryOperationTime())
            .append("publicEquipment", getPublicEquipment())
            .append("creator", getCreator())
            .append("createTime", getCreateTime())
            .append("modifier", getModifier())
            .append("modifyTime", getModifyTime())
            .append("archiveUsage", getArchiveUsage())
            .append("contractConsistent", getContractConsistent())
            .append("specialNetworkAsset", getSpecialNetworkAsset())
            .append("building", getBuilding())
            .append("floor", getFloor())
            .append("quantity", getQuantity())
            .append("contractName", getContractName())
            .append("contractPrice", getContractPrice())
            .append("signDate", getSignDate())
            .append("purchaseDate", getPurchaseDate())
            .append("acceptanceDate", getAcceptanceDate())
            .append("reviewTime", getReviewTime())
            .append("warrantyExpireDate", getWarrantyExpireDate())
            .append("bidDate", getBidDate())
            .append("supplyNoticeDate", getSupplyNoticeDate())
            .append("firstAcceptanceDate", getFirstAcceptanceDate())
            .append("deliveryDeadline", getDeliveryDeadline())
            .append("secondAcceptanceDate", getSecondAcceptanceDate())
            .append("warrantyOutDate", getWarrantyOutDate())
            .append("supplier", getSupplier())
            .append("supplierContact", getSupplierContact())
            .append("supplierPhone", getSupplierPhone())
            .append("maintenanceCompany", getMaintenanceCompany())
            .append("maintenanceContact", getMaintenanceContact())
            .append("maintenancePhone", getMaintenancePhone())
            .append("manufacturer", getManufacturer())
            .append("purchaseMethod", getPurchaseMethod())
            .append("biddingForm", getBiddingForm())
            .append("singleBudget", getSingleBudget())
            .append("projectBasis", getProjectBasis())
            .append("biddingNumber", getBiddingNumber())
            .append("biddingDate", getBiddingDate())
            .append("biddingAmount", getBiddingAmount())
            .append("delFlag", getDelFlag())
            .append("createBy", getCreateBy())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }

    public BigDecimal getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(BigDecimal originalPrice) {
        this.originalPrice = originalPrice;
    }
}
package com.spd.foundation.domain;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.spd.common.annotation.Excel;
import com.spd.common.core.domain.BaseEntity;

/**
 * 耗材产品对象 fd_material
 *
 * @author spd
 * @date 2023-12-23
 */
public class FdMaterial extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** ID */
    private Long id;

    /** 耗材编码 */
    @Excel(name = "耗材编码")
    private String code;

    /** 耗材名称 */
    @Excel(name = "耗材名称")
    private String name;

    /** 供应商ID */
    @Excel(name = "供应商ID")
    private Long supplierId;

    /** 规格 */
    @Excel(name = "规格")
    private String speci;

    /** 型号 */
    @Excel(name = "型号")
    private String model;

    /** 价格 */
    @Excel(name = "价格")
    private BigDecimal prince;

    /** 供应商对象 */
    private FdSupplier supplier;

    /** 删除标识 */
    private Integer delFlag;

    /** 名称简码 */
    @Excel(name = "名称简码")
    private String referredName;

    /** 通用名称 */
    @Excel(name = "通用名称")
    private String useName;

    /** 生产厂家ID */
    @Excel(name = "生产厂家ID")
    private Long factoryId;

    /** 库房分类ID */
    @Excel(name = "库房分类ID")
    private Long storeroomId;

    /** 财务分类ID */
    @Excel(name = "财务分类ID")
    private Long financeCategoryId;

    /** 单位分类ID */
    @Excel(name = "单位分类ID")
    private Long unitId;

    /** 注册证名称 */
    @Excel(name = "注册证名称")
    private String registerName;

    /** 注册证件号 */
    @Excel(name = "注册证件号")
    private String registerNo;

    /** 医保名称 */
    @Excel(name = "医保名称")
    private String medicalName;

    /** 医保编码 */
    @Excel(name = "医保编码")
    private String medicalNo;

    /** 有效期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "有效期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date periodDate;

    /** 招标类别 */
    @Excel(name = "招标类别")
    private String successfulType;

    /** 中标号 */
    @Excel(name = "中标号")
    private String successfulNo;

    /** 中标价格 */
    @Excel(name = "中标价格")
    private BigDecimal successfulPrice;

    /** 销售价（最小单位） */
    @Excel(name = "销售价", readConverterExp = "最=小单位")
    private BigDecimal salePrice;

    /** 包装规格 */
    @Excel(name = "包装规格")
    private String packageSpeci;

    /**  产地 */
    @Excel(name = " 产地")
    private String producer;

    /** 耗材级别  */
    @Excel(name = "耗材级别 ")
    private String materialLevel;

    /** 注册证级别 */
    @Excel(name = "注册证级别")
    private String registerLevel;

    /** 风险级别 */
    @Excel(name = "风险级别")
    private String riskLevel;

    /** 急救类型 */
    @Excel(name = "急救类型")
    private String firstaidLevel;

    /** 医用级别 */
    @Excel(name = "医用级别")
    private String doctorLevel;

    /** 品牌 */
    @Excel(name = "品牌")
    private String brand;

    /** 用途 */
    @Excel(name = "用途")
    private String useto;

    /** 材质 */
    @Excel(name = "材质")
    private String quality;

    /** 功能 */
    @Excel(name = "功能")
    private String function;

    /** 储存方式 */
    @Excel(name = "储存方式")
    private String isWay;

    /** UDI码 */
    @Excel(name = "UDI码")
    private String udiNo;

    /** 许可证编号 */
    @Excel(name = "许可证编号")
    private String permitNo;

    /** 国家编码 */
    @Excel(name = "国家编码")
    private String countryNo;

    /** 国家医保名称 */
    @Excel(name = "国家医保名称")
    private String countryName;

    /** 商品说明 */
    @Excel(name = "商品说明")
    private String description;

    /** 使用状态（停用/在用） */
    @Excel(name = "使用状态", readConverterExp = "停=用/在用")
    private String isUse;

    /** 带量采购（是/否） */
    @Excel(name = "带量采购", readConverterExp = "是=/否")
    private String isProcure;

    /** 重点监测（是/否） */
    @Excel(name = "重点监测", readConverterExp = "是=/否")
    private String isMonitor;

    /** 厂家对象 */
    private FdFactory fdFactory;

    /** 库房分类对象 */
    private FdWarehouseCategory fdWarehouseCategory;

    /** 单位分类对象 */
    private FdUnit fdUnit;

    /** 是否高值 */
    @Excel(name = "是否高值", readConverterExp = "1=是,2=否")
    private String isGz;

    /** 查询参数：起始日期 */
    private Date beginDate;

    /** 查询参数：截止日期 */
    private Date endDate;

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId()
    {
        return id;
    }
    public void setCode(String code)
    {
        this.code = code;
    }

    public String getCode()
    {
        return code;
    }
    public void setName(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }
    public void setSupplierId(Long supplierId)
    {
        this.supplierId = supplierId;
    }

    public Long getSupplierId()
    {
        return supplierId;
    }
    public void setSpeci(String speci)
    {
        this.speci = speci;
    }

    public String getSpeci()
    {
        return speci;
    }
    public void setModel(String model)
    {
        this.model = model;
    }

    public String getModel()
    {
        return model;
    }
    public void setPrince(BigDecimal prince)
    {
        this.prince = prince;
    }

    public BigDecimal getPrince()
    {
        return prince;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public FdSupplier getSupplier() {
        return supplier;
    }

    public void setSupplier(FdSupplier supplier) {
        this.supplier = supplier;
    }

    public Integer getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(Integer delFlag) {
        this.delFlag = delFlag;
    }

    public String getReferredName() {
        return referredName;
    }

    public void setReferredName(String referredName) {
        this.referredName = referredName;
    }

    public String getUseName() {
        return useName;
    }

    public void setUseName(String useName) {
        this.useName = useName;
    }

    public Long getFactoryId() {
        return factoryId;
    }

    public void setFactoryId(Long factoryId) {
        this.factoryId = factoryId;
    }

    public Long getStoreroomId() {
        return storeroomId;
    }

    public void setStoreroomId(Long storeroomId) {
        this.storeroomId = storeroomId;
    }

    public Long getFinanceCategoryId() {
        return financeCategoryId;
    }

    public void setFinanceCategoryId(Long financeCategoryId) {
        this.financeCategoryId = financeCategoryId;
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public String getRegisterName() {
        return registerName;
    }

    public void setRegisterName(String registerName) {
        this.registerName = registerName;
    }

    public String getRegisterNo() {
        return registerNo;
    }

    public void setRegisterNo(String registerNo) {
        this.registerNo = registerNo;
    }

    public String getMedicalName() {
        return medicalName;
    }

    public void setMedicalName(String medicalName) {
        this.medicalName = medicalName;
    }

    public String getMedicalNo() {
        return medicalNo;
    }

    public void setMedicalNo(String medicalNo) {
        this.medicalNo = medicalNo;
    }

    public Date getPeriodDate() {
        return periodDate;
    }

    public void setPeriodDate(Date periodDate) {
        this.periodDate = periodDate;
    }

    public String getSuccessfulType() {
        return successfulType;
    }

    public void setSuccessfulType(String successfulType) {
        this.successfulType = successfulType;
    }

    public String getSuccessfulNo() {
        return successfulNo;
    }

    public void setSuccessfulNo(String successfulNo) {
        this.successfulNo = successfulNo;
    }

    public BigDecimal getSuccessfulPrice() {
        return successfulPrice;
    }

    public void setSuccessfulPrice(BigDecimal successfulPrice) {
        this.successfulPrice = successfulPrice;
    }

    public BigDecimal getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(BigDecimal salePrice) {
        this.salePrice = salePrice;
    }

    public String getPackageSpeci() {
        return packageSpeci;
    }

    public void setPackageSpeci(String packageSpeci) {
        this.packageSpeci = packageSpeci;
    }

    public String getProducer() {
        return producer;
    }

    public void setProducer(String producer) {
        this.producer = producer;
    }

    public String getMaterialLevel() {
        return materialLevel;
    }

    public void setMaterialLevel(String materialLevel) {
        this.materialLevel = materialLevel;
    }

    public String getRegisterLevel() {
        return registerLevel;
    }

    public void setRegisterLevel(String registerLevel) {
        this.registerLevel = registerLevel;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }

    public String getFirstaidLevel() {
        return firstaidLevel;
    }

    public void setFirstaidLevel(String firstaidLevel) {
        this.firstaidLevel = firstaidLevel;
    }

    public String getDoctorLevel() {
        return doctorLevel;
    }

    public void setDoctorLevel(String doctorLevel) {
        this.doctorLevel = doctorLevel;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getUseto() {
        return useto;
    }

    public void setUseto(String useto) {
        this.useto = useto;
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    public String getFunction() {
        return function;
    }

    public void setFunction(String function) {
        this.function = function;
    }

    public String getIsWay() {
        return isWay;
    }

    public void setIsWay(String isWay) {
        this.isWay = isWay;
    }

    public String getUdiNo() {
        return udiNo;
    }

    public void setUdiNo(String udiNo) {
        this.udiNo = udiNo;
    }

    public String getPermitNo() {
        return permitNo;
    }

    public void setPermitNo(String permitNo) {
        this.permitNo = permitNo;
    }

    public String getCountryNo() {
        return countryNo;
    }

    public void setCountryNo(String countryNo) {
        this.countryNo = countryNo;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIsUse() {
        return isUse;
    }

    public void setIsUse(String isUse) {
        this.isUse = isUse;
    }

    public String getIsProcure() {
        return isProcure;
    }

    public void setIsProcure(String isProcure) {
        this.isProcure = isProcure;
    }

    public String getIsMonitor() {
        return isMonitor;
    }

    public void setIsMonitor(String isMonitor) {
        this.isMonitor = isMonitor;
    }

    public FdFactory getFdFactory() {
        return fdFactory;
    }

    public void setFdFactory(FdFactory fdFactory) {
        this.fdFactory = fdFactory;
    }

    public FdWarehouseCategory getFdWarehouseCategory() {
        return fdWarehouseCategory;
    }

    public void setFdWarehouseCategory(FdWarehouseCategory fdWarehouseCategory) {
        this.fdWarehouseCategory = fdWarehouseCategory;
    }

    public FdUnit getFdUnit() {
        return fdUnit;
    }

    public void setFdUnit(FdUnit fdUnit) {
        this.fdUnit = fdUnit;
    }

    public String getIsGz() {
        return isGz;
    }

    public void setIsGz(String isGz) {
        this.isGz = isGz;
    }

    public Date getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("code", getCode())
            .append("name", getName())
            .append("supplierId", getSupplierId())
            .append("speci", getSpeci())
            .append("model", getModel())
            .append("prince", getPrince())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("supplier", getSupplier())
            .append("delFlag", getDelFlag())
                .append("referredName", getReferredName())
                .append("useName", getUseName())
                .append("factoryId", getFactoryId())
                .append("storeroomId", getStoreroomId())
                .append("financeCategoryId", getFinanceCategoryId())
                .append("unitId", getUnitId())
                .append("registerName", getRegisterName())
                .append("registerNo", getRegisterNo())
                .append("medicalName", getMedicalName())
                .append("medicalNo", getMedicalNo())
                .append("periodDate", getPeriodDate())
                .append("successfulType", getSuccessfulType())
                .append("successfulNo", getSuccessfulNo())
                .append("successfulPrice", getSuccessfulPrice())
                .append("salePrice", getSalePrice())
                .append("packageSpeci", getPackageSpeci())
                .append("producer", getProducer())
                .append("materialLevel", getMaterialLevel())
                .append("registerLevel", getRegisterLevel())
                .append("riskLevel", getRiskLevel())
                .append("firstaidLevel", getFirstaidLevel())
                .append("doctorLevel", getDoctorLevel())
                .append("brand", getBrand())
                .append("useto", getUseto())
                .append("quality", getQuality())
                .append("function", getFunction())
                .append("isWay", getIsWay())
                .append("udiNo", getUdiNo())
                .append("permitNo", getPermitNo())
                .append("countryNo", getCountryNo())
                .append("countryName", getCountryName())
                .append("description", getDescription())
                .append("isUse", getIsUse())
                .append("isProcure", getIsProcure())
                .append("isMonitor", getIsMonitor())
                .append("createBy", getCreateBy())
                .append("createTime", getCreateTime())
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .append("fdFactory", getFdFactory())
                .append("fdWarehouseCategory", getFdWarehouseCategory())
                .append("fdUnit", getFdUnit())
                .append("isGz", getIsGz())
                .append("ennDate", getEndDate())
                .append("beginDate", getBeginDate())
            .toString();
    }
}

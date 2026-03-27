package com.spd.foundation.dto;

import java.math.BigDecimal;
import com.spd.common.annotation.Excel;

/**
 * 耗材档案「新增导入」Excel 行（模板不含 SPD 主键、解析结果列由系统回填）
 */
public class MaterialImportAddDto
{
    @Excel(name = "HIS系统ID", sort = 1, prompt = "选填；填写时须租户内唯一，若已存在于产品档案则整单不可导入")
    private String hisId;

    @Excel(name = "耗材编码", sort = 2, prompt = "选填；不填则导入时自动生成；填写则须本租户内唯一")
    private String code;

    @Excel(name = "名称*", sort = 3, prompt = "必填", nameAliases = { "名称", "耗材名称" })
    private String name;

    @Excel(name = "规格", sort = 4, prompt = "选填")
    private String speci;

    @Excel(name = "型号", sort = 5, prompt = "选填")
    private String model;

    @Excel(name = "HIS系统供应商ID", sort = 6, prompt = "选填；用于匹配本租户供应商主键 fd_supplier.id（按 his_id）")
    private String supplierHisId;

    @Excel(name = "生产厂家ID", sort = 7, prompt = "选填；可与 HIS 生产厂家 ID 同时填写（HIS 优先解析）")
    private Long factoryId;

    @Excel(name = "HIS系统生产厂家ID", sort = 8, prompt = "选填；填写时须能匹配本租户生产厂家")
    private String factoryHisId;

    @Excel(name = "单位名称", sort = 9, prompt = "选填；按名称匹配单位，不存在则导入时自动新建单位")
    private String unitName;

    @Excel(name = "单位ID", sort = 10, prompt = "选填；有单位名称时忽略本列")
    private Long unitId;

    @Excel(name = "单价", sort = 11, prompt = "选填")
    private BigDecimal price;

    @Excel(name = "HIS系统财务分类ID", sort = 12, prompt = "选填；填写时须能匹配本租户财务分类")
    private String financeHisId;

    @Excel(name = "医保代码", sort = 13, prompt = "选填")
    private String medicalNo;

    @Excel(name = "解析后供应商ID", sort = 14, isExport = false)
    private Long resolvedSupplierId;

    @Excel(name = "解析后单位ID", sort = 15, isExport = false)
    private Long resolvedUnitId;

    @Excel(name = "解析后生产厂家ID", sort = 16, isExport = false)
    private Long resolvedFactoryId;

    @Excel(name = "解析后财务分类ID", sort = 17, isExport = false)
    private Long resolvedFinanceCategoryId;

    @Excel(name = "数据校验结果", width = 40, sort = 99999)
    private String validationResult;

    public String getHisId()
    {
        return hisId;
    }

    public void setHisId(String hisId)
    {
        this.hisId = hisId;
    }

    public String getCode()
    {
        return code;
    }

    public void setCode(String code)
    {
        this.code = code;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getSpeci()
    {
        return speci;
    }

    public void setSpeci(String speci)
    {
        this.speci = speci;
    }

    public String getModel()
    {
        return model;
    }

    public void setModel(String model)
    {
        this.model = model;
    }

    public String getSupplierHisId()
    {
        return supplierHisId;
    }

    public void setSupplierHisId(String supplierHisId)
    {
        this.supplierHisId = supplierHisId;
    }

    public Long getFactoryId()
    {
        return factoryId;
    }

    public void setFactoryId(Long factoryId)
    {
        this.factoryId = factoryId;
    }

    public String getFactoryHisId()
    {
        return factoryHisId;
    }

    public void setFactoryHisId(String factoryHisId)
    {
        this.factoryHisId = factoryHisId;
    }

    public String getUnitName()
    {
        return unitName;
    }

    public void setUnitName(String unitName)
    {
        this.unitName = unitName;
    }

    public Long getUnitId()
    {
        return unitId;
    }

    public void setUnitId(Long unitId)
    {
        this.unitId = unitId;
    }

    public BigDecimal getPrice()
    {
        return price;
    }

    public void setPrice(BigDecimal price)
    {
        this.price = price;
    }

    public String getFinanceHisId()
    {
        return financeHisId;
    }

    public void setFinanceHisId(String financeHisId)
    {
        this.financeHisId = financeHisId;
    }

    public String getMedicalNo()
    {
        return medicalNo;
    }

    public void setMedicalNo(String medicalNo)
    {
        this.medicalNo = medicalNo;
    }

    public Long getResolvedSupplierId()
    {
        return resolvedSupplierId;
    }

    public void setResolvedSupplierId(Long resolvedSupplierId)
    {
        this.resolvedSupplierId = resolvedSupplierId;
    }

    public Long getResolvedUnitId()
    {
        return resolvedUnitId;
    }

    public void setResolvedUnitId(Long resolvedUnitId)
    {
        this.resolvedUnitId = resolvedUnitId;
    }

    public Long getResolvedFactoryId()
    {
        return resolvedFactoryId;
    }

    public void setResolvedFactoryId(Long resolvedFactoryId)
    {
        this.resolvedFactoryId = resolvedFactoryId;
    }

    public Long getResolvedFinanceCategoryId()
    {
        return resolvedFinanceCategoryId;
    }

    public void setResolvedFinanceCategoryId(Long resolvedFinanceCategoryId)
    {
        this.resolvedFinanceCategoryId = resolvedFinanceCategoryId;
    }

    public String getValidationResult()
    {
        return validationResult;
    }

    public void setValidationResult(String validationResult)
    {
        this.validationResult = validationResult;
    }
}

package com.spd.foundation.dto;

import java.math.BigDecimal;
import com.spd.common.annotation.Excel;

/**
 * 耗材档案「更新导入」Excel 行
 */
public class MaterialImportUpdateDto
{
    @Excel(name = "SPD系统主键", sort = 1, prompt = "必填；fd_material.id，须存在于本租户产品档案")
    private Long id;

    @Excel(name = "名称*", sort = 2, prompt = "必填", nameAliases = { "名称", "耗材名称" })
    private String name;

    @Excel(name = "规格", sort = 3, prompt = "选填；不填则不修改原值")
    private String speci;

    @Excel(name = "型号", sort = 4, prompt = "选填；不填则不修改原值")
    private String model;

    @Excel(name = "单位ID", sort = 5, prompt = "选填；不填则不修改原值")
    private Long unitId;

    @Excel(name = "单价", sort = 6, prompt = "选填；不填则不修改原值")
    private BigDecimal price;

    @Excel(name = "医保代码", sort = 7, prompt = "选填；不填则不修改原值")
    private String medicalNo;

    @Excel(name = "数据校验结果", width = 40, sort = 99999, isExport = false)
    private String validationResult;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
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

    public String getMedicalNo()
    {
        return medicalNo;
    }

    public void setMedicalNo(String medicalNo)
    {
        this.medicalNo = medicalNo;
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

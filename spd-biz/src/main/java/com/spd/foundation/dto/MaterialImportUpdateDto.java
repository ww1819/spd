package com.spd.foundation.dto;

import java.math.BigDecimal;
import java.util.Date;
import com.spd.common.annotation.Excel;
import com.spd.common.annotation.Excel.Type;
import com.spd.common.utils.StringUtils;

/**
 * 耗材档案「更新导入」Excel 行
 */
public class MaterialImportUpdateDto
{
    @Excel(name = "档案ID", sort = 1, hidden = true, cellType = Excel.ColumnType.STRING, prompt = "系统识别用，请勿删除或修改",
        nameAliases = { "SPD系统主键", "产品档案ID", "档案ID", "id" })
    private String archiveId;

    @Excel(name = "耗材编码", sort = 2, prompt = "仅供核对，更新导入不会改编码")
    private String code;

    @Excel(name = "耗材名称", sort = 3, prompt = "必填", nameAliases = { "名称", "耗材名称", "名称*" })
    private String name;

    @Excel(name = "规格", sort = 4, prompt = "空着表示不改")
    private String speci;

    @Excel(name = "型号", sort = 5, prompt = "空着表示不改")
    private String model;

    @Excel(name = "单位", sort = 6, prompt = "按名称匹配；系统中没有则导入时自动新建", nameAliases = { "单位名称", "单位" })
    private String unitName;

    @Excel(name = "价格", sort = 7, prompt = "空着表示不改", nameAliases = { "单价", "价格" })
    private BigDecimal price;

    @Excel(name = "生产厂家", sort = 8, prompt = "按名称匹配；不存在时需先勾选创建", nameAliases = { "厂家", "生产厂家名称" })
    private String factoryName;

    @Excel(name = "注册证号", sort = 9, prompt = "空着表示不改", nameAliases = { "注册证件号" })
    private String registerNo;

    @Excel(name = "医保编码", sort = 10, prompt = "空着表示不改", nameAliases = { "医保代码", "医保编码" })
    private String medicalNo;

    @Excel(name = "品牌", sort = 11, prompt = "空着表示不改")
    private String brand;

    @Excel(name = "UDI码", sort = 12, prompt = "空着表示不改", nameAliases = { "UDI", "UDI码" })
    private String udiNo;

    @Excel(name = "注册证名称", sort = 13, prompt = "空着表示不改")
    private String registerName;

    @Excel(name = "注册证有效期", sort = 14, dateFormat = "yyyy-MM-dd", prompt = "空着表示不改", nameAliases = { "有效期", "注册证有效期" })
    private Date periodDate;

    @Excel(name = "包装规格", sort = 15, prompt = "空着表示不改")
    private String packageSpeci;

    @Excel(name = "最小包装数", sort = 16, prompt = "空着表示不改")
    private BigDecimal minPackageQty;

    @Excel(name = "产地", sort = 17, prompt = "空着表示不改")
    private String producer;

    @Excel(name = "通用名称", sort = 18, prompt = "空着表示不改")
    private String useName;

    @Excel(name = "材质", sort = 19, prompt = "空着表示不改")
    private String quality;

    @Excel(name = "许可证编号", sort = 20, prompt = "空着表示不改")
    private String permitNo;

    @Excel(name = "阳光平台编码", sort = 21, prompt = "空着表示不改", nameAliases = { "阳采编码", "阳光平台编码" })
    private String sunshineCode;

    /** 兼容旧模板「单位ID」列 */
    @Excel(name = "单位ID", sort = 22, type = Type.IMPORT, prompt = "选填；有单位名称时忽略")
    private Long unitId;

    @Excel(name = "数据校验结果", width = 40, sort = 99999, type = Type.IMPORT)
    private String validationResult;

    public Long resolveArchiveId()
    {
        String s = StringUtils.sanitizeImportCell(archiveId);
        if (s == null)
        {
            return null;
        }
        if (s.matches("\\d+\\.0+"))
        {
            s = s.substring(0, s.indexOf('.'));
        }
        try
        {
            return Long.valueOf(s);
        }
        catch (NumberFormatException e)
        {
            return null;
        }
    }

    public String getArchiveId()
    {
        return archiveId;
    }

    public void setArchiveId(String archiveId)
    {
        this.archiveId = archiveId;
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

    public String getFactoryName()
    {
        return factoryName;
    }

    public void setFactoryName(String factoryName)
    {
        this.factoryName = factoryName;
    }

    public String getRegisterNo()
    {
        return registerNo;
    }

    public void setRegisterNo(String registerNo)
    {
        this.registerNo = registerNo;
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

    public String getUdiNo()
    {
        return udiNo;
    }

    public void setUdiNo(String udiNo)
    {
        this.udiNo = udiNo;
    }

    public String getRegisterName()
    {
        return registerName;
    }

    public void setRegisterName(String registerName)
    {
        this.registerName = registerName;
    }

    public Date getPeriodDate()
    {
        return periodDate;
    }

    public void setPeriodDate(Date periodDate)
    {
        this.periodDate = periodDate;
    }

    public String getPackageSpeci()
    {
        return packageSpeci;
    }

    public void setPackageSpeci(String packageSpeci)
    {
        this.packageSpeci = packageSpeci;
    }

    public BigDecimal getMinPackageQty()
    {
        return minPackageQty;
    }

    public void setMinPackageQty(BigDecimal minPackageQty)
    {
        this.minPackageQty = minPackageQty;
    }

    public String getProducer()
    {
        return producer;
    }

    public void setProducer(String producer)
    {
        this.producer = producer;
    }

    public String getUseName()
    {
        return useName;
    }

    public void setUseName(String useName)
    {
        this.useName = useName;
    }

    public String getQuality()
    {
        return quality;
    }

    public void setQuality(String quality)
    {
        this.quality = quality;
    }

    public String getPermitNo()
    {
        return permitNo;
    }

    public void setPermitNo(String permitNo)
    {
        this.permitNo = permitNo;
    }

    public String getSunshineCode()
    {
        return sunshineCode;
    }

    public void setSunshineCode(String sunshineCode)
    {
        this.sunshineCode = sunshineCode;
    }

    public Long getUnitId()
    {
        return unitId;
    }

    public void setUnitId(Long unitId)
    {
        this.unitId = unitId;
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

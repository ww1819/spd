package com.spd.finance.domain.vo;

import java.math.BigDecimal;

import com.spd.common.annotation.Excel;

/**
 * 卫材出库汇总（统计口径：耗材出库数据）
 */
public class MedicalOutboundSummaryVo
{
    @Excel(name = "日期", width = 20)
    private String statDate;

    @Excel(name = "科室名称", width = 28)
    private String departmentName;

    @Excel(name = "材料类别", width = 25)
    private String materialCategoryName;

    @Excel(name = "金额", width = 18)
    private BigDecimal amount;

    @Excel(name = "单位", width = 14)
    private String unitName;

    @Excel(name = "是否高值", width = 12)
    private String isGzText;

    public String getStatDate()
    {
        return statDate;
    }

    public void setStatDate(String statDate)
    {
        this.statDate = statDate;
    }

    public String getDepartmentName()
    {
        return departmentName;
    }

    public void setDepartmentName(String departmentName)
    {
        this.departmentName = departmentName;
    }

    public String getMaterialCategoryName()
    {
        return materialCategoryName;
    }

    public void setMaterialCategoryName(String materialCategoryName)
    {
        this.materialCategoryName = materialCategoryName;
    }

    public BigDecimal getAmount()
    {
        return amount;
    }

    public void setAmount(BigDecimal amount)
    {
        this.amount = amount;
    }

    public String getUnitName()
    {
        return unitName;
    }

    public void setUnitName(String unitName)
    {
        this.unitName = unitName;
    }

    public String getIsGzText()
    {
        return isGzText;
    }

    public void setIsGzText(String isGzText)
    {
        this.isGzText = isGzText;
    }
}

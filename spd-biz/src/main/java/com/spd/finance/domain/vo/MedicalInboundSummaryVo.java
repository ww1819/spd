package com.spd.finance.domain.vo;

import java.math.BigDecimal;

import com.spd.common.annotation.Excel;

/**
 * 卫材入库汇总（统计口径：耗材出库数据）
 */
public class MedicalInboundSummaryVo
{
    @Excel(name = "日期", width = 20)
    private String statDate;

    @Excel(name = "供货商", width = 30)
    private String supplierName;

    @Excel(name = "材料类别", width = 25)
    private String materialCategoryName;

    @Excel(name = "金额", width = 18)
    private BigDecimal amount;

    public String getStatDate()
    {
        return statDate;
    }

    public void setStatDate(String statDate)
    {
        this.statDate = statDate;
    }

    public String getSupplierName()
    {
        return supplierName;
    }

    public void setSupplierName(String supplierName)
    {
        this.supplierName = supplierName;
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
}

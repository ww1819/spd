package com.spd.gz.domain;

import java.math.BigDecimal;
import com.spd.common.annotation.Excel;

/**
 * 高值耗材使用情况报表行
 */
public class GzMaterialUsageReportVo
{
    private Long departmentId;

    @Excel(name = "科室")
    private String departmentName;

    private Long materialId;

    @Excel(name = "耗材名称")
    private String materialName;

    @Excel(name = "规格")
    private String specification;

    @Excel(name = "型号")
    private String model;

    @Excel(name = "单位")
    private String unitName;

    @Excel(name = "消耗数量")
    private BigDecimal consumeQty;

    @Excel(name = "剩余数量")
    private BigDecimal remainQty;

    @Excel(name = "预计下个月需求")
    private BigDecimal nextMonthDemand;

    @Excel(name = "备注")
    private String remark;

    public Long getDepartmentId()
    {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId)
    {
        this.departmentId = departmentId;
    }

    public String getDepartmentName()
    {
        return departmentName;
    }

    public void setDepartmentName(String departmentName)
    {
        this.departmentName = departmentName;
    }

    public Long getMaterialId()
    {
        return materialId;
    }

    public void setMaterialId(Long materialId)
    {
        this.materialId = materialId;
    }

    public String getMaterialName()
    {
        return materialName;
    }

    public void setMaterialName(String materialName)
    {
        this.materialName = materialName;
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

    public BigDecimal getConsumeQty()
    {
        return consumeQty;
    }

    public void setConsumeQty(BigDecimal consumeQty)
    {
        this.consumeQty = consumeQty;
    }

    public BigDecimal getRemainQty()
    {
        return remainQty;
    }

    public void setRemainQty(BigDecimal remainQty)
    {
        this.remainQty = remainQty;
    }

    public BigDecimal getNextMonthDemand()
    {
        return nextMonthDemand;
    }

    public void setNextMonthDemand(BigDecimal nextMonthDemand)
    {
        this.nextMonthDemand = nextMonthDemand;
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

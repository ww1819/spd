package com.spd.caigou.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.spd.common.annotation.Excel;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 采购计划明细导出（供货清单，一行一条计划明细，便于分发给供应商配送）
 */
public class PurchasePlanEntrySupplierExportVO
{
    @Excel(name = "计划单号", width = 22)
    private String planNo;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "计划日期", width = 14, dateFormat = "yyyy-MM-dd")
    private Date planDate;

    @Excel(name = "计划状态", readConverterExp = "0=未提交,1=未提交,2=已审核,3=已执行,4=已取消")
    private String planStatus;

    @Excel(name = "送货仓库", width = 20)
    private String warehouseName;

    @Excel(name = "计划科室", width = 18)
    private String planDepartmentName;

    @Excel(name = "供货单位", width = 22)
    private String supplierName;

    @Excel(name = "采购员", width = 14)
    private String proPerson;

    @Excel(name = "联系电话", width = 16)
    private String telephone;

    @Excel(name = "耗材编码", width = 18)
    private String materialCode;

    @Excel(name = "耗材名称", width = 28)
    private String materialName;

    @Excel(name = "规格", width = 22)
    private String speci;

    @Excel(name = "型号", width = 18)
    private String model;

    @Excel(name = "单位", width = 10)
    private String unitName;

    @Excel(name = "计划数量")
    private BigDecimal qty;

    @Excel(name = "单价")
    private BigDecimal price;

    @Excel(name = "金额")
    private BigDecimal amt;

    @Excel(name = "申请科室", width = 18)
    private String applyDepartmentName;

    @Excel(name = "明细备注", width = 24)
    private String entryRemark;

    @Excel(name = "计划备注", width = 24)
    private String planRemark;

    public String getPlanNo()
    {
        return planNo;
    }

    public void setPlanNo(String planNo)
    {
        this.planNo = planNo;
    }

    public Date getPlanDate()
    {
        return planDate;
    }

    public void setPlanDate(Date planDate)
    {
        this.planDate = planDate;
    }

    public String getPlanStatus()
    {
        return planStatus;
    }

    public void setPlanStatus(String planStatus)
    {
        this.planStatus = planStatus;
    }

    public String getWarehouseName()
    {
        return warehouseName;
    }

    public void setWarehouseName(String warehouseName)
    {
        this.warehouseName = warehouseName;
    }

    public String getPlanDepartmentName()
    {
        return planDepartmentName;
    }

    public void setPlanDepartmentName(String planDepartmentName)
    {
        this.planDepartmentName = planDepartmentName;
    }

    public String getSupplierName()
    {
        return supplierName;
    }

    public void setSupplierName(String supplierName)
    {
        this.supplierName = supplierName;
    }

    public String getProPerson()
    {
        return proPerson;
    }

    public void setProPerson(String proPerson)
    {
        this.proPerson = proPerson;
    }

    public String getTelephone()
    {
        return telephone;
    }

    public void setTelephone(String telephone)
    {
        this.telephone = telephone;
    }

    public String getMaterialCode()
    {
        return materialCode;
    }

    public void setMaterialCode(String materialCode)
    {
        this.materialCode = materialCode;
    }

    public String getMaterialName()
    {
        return materialName;
    }

    public void setMaterialName(String materialName)
    {
        this.materialName = materialName;
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

    public BigDecimal getQty()
    {
        return qty;
    }

    public void setQty(BigDecimal qty)
    {
        this.qty = qty;
    }

    public BigDecimal getPrice()
    {
        return price;
    }

    public void setPrice(BigDecimal price)
    {
        this.price = price;
    }

    public BigDecimal getAmt()
    {
        return amt;
    }

    public void setAmt(BigDecimal amt)
    {
        this.amt = amt;
    }

    public String getApplyDepartmentName()
    {
        return applyDepartmentName;
    }

    public void setApplyDepartmentName(String applyDepartmentName)
    {
        this.applyDepartmentName = applyDepartmentName;
    }

    public String getEntryRemark()
    {
        return entryRemark;
    }

    public void setEntryRemark(String entryRemark)
    {
        this.entryRemark = entryRemark;
    }

    public String getPlanRemark()
    {
        return planRemark;
    }

    public void setPlanRemark(String planRemark)
    {
        this.planRemark = planRemark;
    }
}

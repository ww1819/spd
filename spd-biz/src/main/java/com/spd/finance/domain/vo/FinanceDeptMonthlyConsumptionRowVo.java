package com.spd.finance.domain.vo;

import java.math.BigDecimal;

/**
 * 财务结算汇总表三：科室月消耗（按计费/不计费耗材金额汇总）
 */
public class FinanceDeptMonthlyConsumptionRowVo
{
    private Long departmentId;

    private String departmentName;

    /** 计费耗材金额（产品档案 is_billing = 1） */
    private BigDecimal billingConsumablesAmt;

    /** 不计费耗材金额（is_billing 非 1 或未维护） */
    private BigDecimal nonBillingConsumablesAmt;

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

    public BigDecimal getBillingConsumablesAmt()
    {
        return billingConsumablesAmt;
    }

    public void setBillingConsumablesAmt(BigDecimal billingConsumablesAmt)
    {
        this.billingConsumablesAmt = billingConsumablesAmt;
    }

    public BigDecimal getNonBillingConsumablesAmt()
    {
        return nonBillingConsumablesAmt;
    }

    public void setNonBillingConsumablesAmt(BigDecimal nonBillingConsumablesAmt)
    {
        this.nonBillingConsumablesAmt = nonBillingConsumablesAmt;
    }
}

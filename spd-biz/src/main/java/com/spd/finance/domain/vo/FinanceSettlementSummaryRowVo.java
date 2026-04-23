package com.spd.finance.domain.vo;

import java.math.BigDecimal;

/**
 * 财务结算汇总：按供货单位一行（批发金额已按出退库正负汇总）
 */
public class FinanceSettlementSummaryRowVo
{
    private String supplierName;
    private BigDecimal wholesaleAmt;

    public String getSupplierName()
    {
        return supplierName;
    }

    public void setSupplierName(String supplierName)
    {
        this.supplierName = supplierName;
    }

    public BigDecimal getWholesaleAmt()
    {
        return wholesaleAmt;
    }

    public void setWholesaleAmt(BigDecimal wholesaleAmt)
    {
        this.wholesaleAmt = wholesaleAmt;
    }
}

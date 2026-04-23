package com.spd.finance.domain.vo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 财务结算汇总表：材料、试剂、未识别分类（库房分类未维护或非 11/12/13）及合计
 */
public class FinanceSettlementSummaryBundleVo
{
    private List<FinanceSettlementSummaryRowVo> materialSuppliers = new ArrayList<>();
    private BigDecimal materialWholesaleTotal = BigDecimal.ZERO;
    private List<FinanceSettlementSummaryRowVo> reagentSuppliers = new ArrayList<>();
    private BigDecimal reagentWholesaleTotal = BigDecimal.ZERO;
    /** 库房分类为空或其它 id 的供货单位明细 */
    private List<FinanceSettlementSummaryRowVo> unrecognizedSuppliers = new ArrayList<>();
    private BigDecimal unrecognizedWholesaleTotal = BigDecimal.ZERO;

    public List<FinanceSettlementSummaryRowVo> getMaterialSuppliers()
    {
        return materialSuppliers;
    }

    public void setMaterialSuppliers(List<FinanceSettlementSummaryRowVo> materialSuppliers)
    {
        this.materialSuppliers = materialSuppliers != null ? materialSuppliers : new ArrayList<>();
    }

    public BigDecimal getMaterialWholesaleTotal()
    {
        return materialWholesaleTotal;
    }

    public void setMaterialWholesaleTotal(BigDecimal materialWholesaleTotal)
    {
        this.materialWholesaleTotal = materialWholesaleTotal != null ? materialWholesaleTotal : BigDecimal.ZERO;
    }

    public List<FinanceSettlementSummaryRowVo> getReagentSuppliers()
    {
        return reagentSuppliers;
    }

    public void setReagentSuppliers(List<FinanceSettlementSummaryRowVo> reagentSuppliers)
    {
        this.reagentSuppliers = reagentSuppliers != null ? reagentSuppliers : new ArrayList<>();
    }

    public BigDecimal getReagentWholesaleTotal()
    {
        return reagentWholesaleTotal;
    }

    public void setReagentWholesaleTotal(BigDecimal reagentWholesaleTotal)
    {
        this.reagentWholesaleTotal = reagentWholesaleTotal != null ? reagentWholesaleTotal : BigDecimal.ZERO;
    }

    public List<FinanceSettlementSummaryRowVo> getUnrecognizedSuppliers()
    {
        return unrecognizedSuppliers;
    }

    public void setUnrecognizedSuppliers(List<FinanceSettlementSummaryRowVo> unrecognizedSuppliers)
    {
        this.unrecognizedSuppliers = unrecognizedSuppliers != null ? unrecognizedSuppliers : new ArrayList<>();
    }

    public BigDecimal getUnrecognizedWholesaleTotal()
    {
        return unrecognizedWholesaleTotal;
    }

    public void setUnrecognizedWholesaleTotal(BigDecimal unrecognizedWholesaleTotal)
    {
        this.unrecognizedWholesaleTotal = unrecognizedWholesaleTotal != null ? unrecognizedWholesaleTotal : BigDecimal.ZERO;
    }
}

package com.spd.finance.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.spd.common.utils.StringUtils;
import com.spd.finance.domain.vo.FinanceSettlementSummaryBundleVo;
import com.spd.finance.domain.vo.FinanceSettlementSummaryRowVo;
import com.spd.finance.service.IFinanceSettlementSummaryService;
import com.spd.warehouse.domain.StkIoBill;
import com.spd.warehouse.mapper.StkIoBillMapper;
import com.spd.warehouse.service.IStkIoBillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 财务结算汇总
 */
@Service
public class FinanceSettlementSummaryServiceImpl implements IFinanceSettlementSummaryService
{
    @Autowired
    private StkIoBillMapper stkIoBillMapper;

    @Autowired
    private IStkIoBillService stkIoBillService;

    @Override
    public FinanceSettlementSummaryBundleVo summarize(StkIoBill query)
    {
        if (query == null)
        {
            query = new StkIoBill();
        }
        stkIoBillService.applyCtkDepartmentScopeToQuery(query);
        List<Map<String, Object>> raw = stkIoBillMapper.selectFinanceSettlementSupplierSummary(query);

        List<FinanceSettlementSummaryRowVo> material = new ArrayList<>();
        List<FinanceSettlementSummaryRowVo> reagent = new ArrayList<>();
        List<FinanceSettlementSummaryRowVo> unrecognized = new ArrayList<>();
        BigDecimal materialSum = BigDecimal.ZERO;
        BigDecimal reagentSum = BigDecimal.ZERO;
        BigDecimal unrecognizedSum = BigDecimal.ZERO;

        Collator zh = Collator.getInstance(Locale.CHINA);
        Comparator<FinanceSettlementSummaryRowVo> byName = (a, b) ->
            zh.compare(StringUtils.nvl(a.getSupplierName(), ""), StringUtils.nvl(b.getSupplierName(), ""));

        if (raw != null)
        {
            for (Map<String, Object> row : raw)
            {
                if (row == null)
                {
                    continue;
                }
                String kind = row.get("materialKind") != null ? row.get("materialKind").toString() : "";
                FinanceSettlementSummaryRowVo vo = new FinanceSettlementSummaryRowVo();
                vo.setSupplierName(row.get("supplierName") != null ? row.get("supplierName").toString() : "");
                vo.setWholesaleAmt(toBigDecimal(row.get("wholesaleAmt")));
                if ("材料".equals(kind))
                {
                    material.add(vo);
                    materialSum = materialSum.add(vo.getWholesaleAmt() != null ? vo.getWholesaleAmt() : BigDecimal.ZERO);
                }
                else if ("试剂".equals(kind))
                {
                    reagent.add(vo);
                    reagentSum = reagentSum.add(vo.getWholesaleAmt() != null ? vo.getWholesaleAmt() : BigDecimal.ZERO);
                }
                else if ("未识别分类".equals(kind) || StringUtils.isEmpty(kind))
                {
                    unrecognized.add(vo);
                    unrecognizedSum = unrecognizedSum.add(vo.getWholesaleAmt() != null ? vo.getWholesaleAmt() : BigDecimal.ZERO);
                }
            }
        }

        material.sort(byName);
        reagent.sort(byName);
        unrecognized.sort(byName);

        FinanceSettlementSummaryBundleVo bundle = new FinanceSettlementSummaryBundleVo();
        bundle.setMaterialSuppliers(material);
        bundle.setMaterialWholesaleTotal(materialSum.setScale(2, RoundingMode.HALF_UP));
        bundle.setReagentSuppliers(reagent);
        bundle.setReagentWholesaleTotal(reagentSum.setScale(2, RoundingMode.HALF_UP));
        bundle.setUnrecognizedSuppliers(unrecognized);
        bundle.setUnrecognizedWholesaleTotal(unrecognizedSum.setScale(2, RoundingMode.HALF_UP));
        return bundle;
    }

    private static BigDecimal toBigDecimal(Object v)
    {
        if (v == null)
        {
            return BigDecimal.ZERO;
        }
        if (v instanceof BigDecimal)
        {
            return (BigDecimal) v;
        }
        if (v instanceof Number)
        {
            return BigDecimal.valueOf(((Number) v).doubleValue());
        }
        try
        {
            return new BigDecimal(v.toString());
        }
        catch (Exception e)
        {
            return BigDecimal.ZERO;
        }
    }
}

package com.spd.finance.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.spd.finance.domain.vo.MedicalInboundSummaryVo;
import com.spd.finance.domain.vo.MedicalOutboundSummaryVo;
import com.spd.finance.service.IMedicalStockSummaryService;
import com.spd.warehouse.domain.StkIoBill;
import com.spd.warehouse.mapper.StkIoBillMapper;
import com.spd.warehouse.service.IStkIoBillService;

/**
 * 卫材入出库汇总（统计口径：耗材出库数据）
 */
@Service
public class MedicalStockSummaryServiceImpl implements IMedicalStockSummaryService
{
    @Autowired
    private StkIoBillMapper stkIoBillMapper;

    @Autowired
    private IStkIoBillService stkIoBillService;

    @Override
    public List<MedicalInboundSummaryVo> listInboundSummary(StkIoBill query)
    {
        StkIoBill q = ensureQuery(query);
        List<Map<String, Object>> rows = stkIoBillMapper.selectMedicalInboundSummary(q);
        List<MedicalInboundSummaryVo> list = new ArrayList<>();
        if (rows == null)
        {
            return list;
        }
        for (Map<String, Object> r : rows)
        {
            if (r == null)
            {
                continue;
            }
            MedicalInboundSummaryVo vo = new MedicalInboundSummaryVo();
            vo.setStatDate(str(r.get("statDate")));
            vo.setSupplierName(str(r.get("supplierName")));
            vo.setMaterialCategoryName(str(r.get("materialCategoryName")));
            vo.setAmount(dec(r.get("amount")));
            list.add(vo);
        }
        return list;
    }

    @Override
    public List<MedicalOutboundSummaryVo> listOutboundSummary(StkIoBill query)
    {
        StkIoBill q = ensureQuery(query);
        List<Map<String, Object>> rows = stkIoBillMapper.selectMedicalOutboundSummary(q);
        List<MedicalOutboundSummaryVo> list = new ArrayList<>();
        if (rows == null)
        {
            return list;
        }
        for (Map<String, Object> r : rows)
        {
            if (r == null)
            {
                continue;
            }
            MedicalOutboundSummaryVo vo = new MedicalOutboundSummaryVo();
            vo.setStatDate(str(r.get("statDate")));
            vo.setDepartmentName(str(r.get("departmentName")));
            vo.setMaterialCategoryName(str(r.get("materialCategoryName")));
            vo.setAmount(dec(r.get("amount")));
            vo.setUnitName(str(r.get("unitName")));
            vo.setIsGzText(str(r.get("isGzText")));
            list.add(vo);
        }
        return list;
    }

    private StkIoBill ensureQuery(StkIoBill query)
    {
        StkIoBill q = query != null ? query : new StkIoBill();
        stkIoBillService.applyCtkDepartmentScopeToQuery(q);
        return q;
    }

    private static String str(Object v)
    {
        return v == null ? "" : v.toString();
    }

    private static BigDecimal dec(Object v)
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
        catch (Exception ignore)
        {
            return BigDecimal.ZERO;
        }
    }
}

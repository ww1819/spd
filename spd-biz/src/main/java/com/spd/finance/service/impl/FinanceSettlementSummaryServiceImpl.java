package com.spd.finance.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import com.spd.common.core.domain.entity.SysUser;
import com.spd.common.utils.SecurityUtils;
import com.spd.common.utils.StringUtils;
import com.spd.finance.domain.vo.FinanceDeptConsumablePickupRowVo;
import com.spd.finance.domain.vo.FinanceDeptMonthlyConsumptionRowVo;
import com.spd.finance.domain.vo.FinanceSettlementSummaryBundleVo;
import com.spd.finance.domain.vo.FinanceSettlementSummaryRowVo;
import com.spd.finance.service.IFinanceSettlementSummaryService;
import com.spd.foundation.domain.FdDepartment;
import com.spd.foundation.service.IFdDepartmentService;
import com.spd.system.service.ITenantScopeService;
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

    @Autowired
    private IFdDepartmentService fdDepartmentService;

    @Autowired
    private ITenantScopeService tenantScopeService;

    @Override
    public FinanceSettlementSummaryBundleVo summarize(StkIoBill query)
    {
        if (query == null)
        {
            query = new StkIoBill();
        }
        stkIoBillService.applyCtkDepartmentScopeToQuery(query);
        List<Map<String, Object>> raw = stkIoBillMapper.selectFinanceSettlementSupplierSummary(query);
        List<Map<String, Object>> rawDept = stkIoBillMapper.selectFinanceDeptConsumablePickupSummary(query);
        List<Map<String, Object>> rawDeptMonthly = stkIoBillMapper.selectFinanceDeptMonthlyConsumptionSummary(query);

        List<FinanceSettlementSummaryRowVo> material = new ArrayList<>();
        List<FinanceSettlementSummaryRowVo> reagent = new ArrayList<>();
        List<FinanceSettlementSummaryRowVo> unrecognized = new ArrayList<>();
        BigDecimal materialSum = BigDecimal.ZERO;
        BigDecimal reagentSum = BigDecimal.ZERO;
        BigDecimal unrecognizedSum = BigDecimal.ZERO;

        Collator zh = Collator.getInstance(Locale.CHINA);
        Comparator<FinanceSettlementSummaryRowVo> bySupplierName = (a, b) -> {
            int rank = Integer.compare(
                supplierDisplayOrderRank(a.getSupplierName()),
                supplierDisplayOrderRank(b.getSupplierName()));
            if (rank != 0)
            {
                return rank;
            }
            return zh.compare(StringUtils.nvl(a.getSupplierName(), ""), StringUtils.nvl(b.getSupplierName(), ""));
        };

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

        material.sort(bySupplierName);
        reagent.sort(bySupplierName);
        unrecognized.sort(bySupplierName);

        List<FinanceDeptConsumablePickupRowVo> deptRows = new ArrayList<>();
        Comparator<FinanceDeptConsumablePickupRowVo> byDeptName = (a, b) ->
            zh.compare(StringUtils.nvl(a.getDepartmentName(), ""), StringUtils.nvl(b.getDepartmentName(), ""));
        if (rawDept != null)
        {
            for (Map<String, Object> row : rawDept)
            {
                if (row == null)
                {
                    continue;
                }
                FinanceDeptConsumablePickupRowVo dr = new FinanceDeptConsumablePickupRowVo();
                Object did = row.get("departmentId");
                if (did instanceof Number)
                {
                    dr.setDepartmentId(((Number) did).longValue());
                }
                dr.setDepartmentName(row.get("departmentName") != null ? row.get("departmentName").toString() : "");
                dr.setPlainConsumablesAmt(toBigDecimal(row.get("plainConsumablesAmt")).setScale(2, RoundingMode.HALF_UP));
                dr.setHighValueConsumablesAmt(toBigDecimal(row.get("highValueConsumablesAmt")).setScale(2, RoundingMode.HALF_UP));
                dr.setReagentAmt(toBigDecimal(row.get("reagentAmt")).setScale(2, RoundingMode.HALF_UP));
                deptRows.add(dr);
            }
        }
        deptRows.sort(byDeptName);

        List<FinanceDeptMonthlyConsumptionRowVo> deptMonthlyRows =
            buildDeptMonthlyConsumptionRows(query, rawDeptMonthly, zh);

        FinanceSettlementSummaryBundleVo bundle = new FinanceSettlementSummaryBundleVo();
        bundle.setMaterialSuppliers(material);
        bundle.setMaterialWholesaleTotal(materialSum.setScale(2, RoundingMode.HALF_UP));
        bundle.setReagentSuppliers(reagent);
        bundle.setReagentWholesaleTotal(reagentSum.setScale(2, RoundingMode.HALF_UP));
        bundle.setUnrecognizedSuppliers(unrecognized);
        bundle.setUnrecognizedWholesaleTotal(unrecognizedSum.setScale(2, RoundingMode.HALF_UP));
        bundle.setDeptConsumablePickupRows(deptRows);
        bundle.setDeptMonthlyConsumptionRows(deptMonthlyRows);
        return bundle;
    }

    /**
     * 供货单位展示顺序：带量 → 集采 → 其余；同组内再按名称排序。
     * 名称同时含「带量」「集采」时归入带量组。
     */
    private static int supplierDisplayOrderRank(String supplierName)
    {
        if (StringUtils.isEmpty(supplierName))
        {
            return 2;
        }
        if (supplierName.contains("带量"))
        {
            return 0;
        }
        if (supplierName.contains("集采"))
        {
            return 1;
        }
        return 2;
    }

    /**
     * 表三：补全当前账号有权限的全部科室（无消耗记 0），按合计金额从高到低排序。
     */
    private List<FinanceDeptMonthlyConsumptionRowVo> buildDeptMonthlyConsumptionRows(
        StkIoBill query, List<Map<String, Object>> rawDeptMonthly, Collator zh)
    {
        Map<Long, FinanceDeptMonthlyConsumptionRowVo> amountByDeptId = new HashMap<>();
        if (rawDeptMonthly != null)
        {
            for (Map<String, Object> row : rawDeptMonthly)
            {
                if (row == null)
                {
                    continue;
                }
                Object did = row.get("departmentId");
                if (!(did instanceof Number))
                {
                    continue;
                }
                Long deptId = ((Number) did).longValue();
                FinanceDeptMonthlyConsumptionRowVo dr = new FinanceDeptMonthlyConsumptionRowVo();
                dr.setDepartmentId(deptId);
                dr.setDepartmentName(row.get("departmentName") != null ? row.get("departmentName").toString() : "");
                dr.setBillingConsumablesAmt(toBigDecimal(row.get("billingConsumablesAmt")).setScale(2, RoundingMode.HALF_UP));
                dr.setNonBillingConsumablesAmt(toBigDecimal(row.get("nonBillingConsumablesAmt")).setScale(2, RoundingMode.HALF_UP));
                amountByDeptId.put(deptId, dr);
            }
        }

        List<FdDepartment> scopedDepts = resolveScopedDepartments();
        if (query != null && query.getDepartmentId() != null)
        {
            Long filterId = query.getDepartmentId();
            scopedDepts = scopedDepts.stream()
                .filter(d -> d.getId() != null && filterId.equals(d.getId()))
                .collect(Collectors.toList());
        }

        List<FinanceDeptMonthlyConsumptionRowVo> rows = new ArrayList<>();
        for (FdDepartment dept : scopedDepts)
        {
            if (dept == null || dept.getId() == null)
            {
                continue;
            }
            FinanceDeptMonthlyConsumptionRowVo existing = amountByDeptId.get(dept.getId());
            if (existing != null)
            {
                if (StringUtils.isEmpty(existing.getDepartmentName()))
                {
                    existing.setDepartmentName(StringUtils.nvl(dept.getName(), ""));
                }
                rows.add(existing);
                continue;
            }
            FinanceDeptMonthlyConsumptionRowVo dr = new FinanceDeptMonthlyConsumptionRowVo();
            dr.setDepartmentId(dept.getId());
            dr.setDepartmentName(StringUtils.nvl(dept.getName(), ""));
            dr.setBillingConsumablesAmt(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
            dr.setNonBillingConsumablesAmt(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
            rows.add(dr);
        }

        Comparator<FinanceDeptMonthlyConsumptionRowVo> byTotalDesc = (a, b) -> {
            int cmp = deptMonthlyTotal(b).compareTo(deptMonthlyTotal(a));
            if (cmp != 0)
            {
                return cmp;
            }
            return zh.compare(StringUtils.nvl(a.getDepartmentName(), ""), StringUtils.nvl(b.getDepartmentName(), ""));
        };
        rows.sort(byTotalDesc);
        return rows;
    }

    private static BigDecimal deptMonthlyTotal(FinanceDeptMonthlyConsumptionRowVo row)
    {
        if (row == null)
        {
            return BigDecimal.ZERO;
        }
        BigDecimal billing = row.getBillingConsumablesAmt() != null ? row.getBillingConsumablesAmt() : BigDecimal.ZERO;
        BigDecimal nonBilling = row.getNonBillingConsumablesAmt() != null ? row.getNonBillingConsumablesAmt() : BigDecimal.ZERO;
        return billing.add(nonBilling);
    }

    /** 与财务结算汇总筛选下拉科室列表口径一致 */
    private List<FdDepartment> resolveScopedDepartments()
    {
        Long userId = SecurityUtils.getUserId();
        String customerId = SecurityUtils.getCustomerId();
        List<FdDepartment> list;
        if (StringUtils.isNotEmpty(customerId))
        {
            list = fdDepartmentService.selectdepartmenAll();
            if (list != null && !tenantScopeService.isTenantSuper(userId, customerId))
            {
                List<Long> allowedIds = tenantScopeService.resolveDepartmentScope(userId, customerId);
                if (allowedIds == null || allowedIds.isEmpty())
                {
                    list = new ArrayList<>();
                }
                else
                {
                    list = list.stream()
                        .filter(d -> d.getId() != null && allowedIds.contains(d.getId()))
                        .collect(Collectors.toList());
                }
            }
        }
        else
        {
            if (SysUser.isAdmin(userId))
            {
                list = fdDepartmentService.selectdepartmenAll();
            }
            else
            {
                list = fdDepartmentService.selectUserDepartmenAll(userId);
            }
        }
        return list != null ? list : new ArrayList<>();
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

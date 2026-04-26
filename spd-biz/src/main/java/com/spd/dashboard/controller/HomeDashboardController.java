package com.spd.dashboard.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.spd.common.core.controller.BaseController;
import com.spd.common.core.domain.AjaxResult;
import com.spd.common.core.page.TotalInfo;
import com.spd.common.utils.SecurityUtils;
import com.spd.common.utils.StringUtils;
import com.spd.department.domain.BasApply;
import com.spd.department.domain.DepPurchaseApply;
import com.spd.department.domain.DeptBatchConsume;
import com.spd.department.service.IBasApplyService;
import com.spd.department.service.IDepPurchaseApplyService;
import com.spd.department.service.IDeptBatchConsumeService;
import com.spd.foundation.domain.FdWarehouse;
import com.spd.foundation.service.IFdWarehouseService;
import com.spd.system.service.ITenantScopeService;
import com.spd.warehouse.domain.StkInventory;
import com.spd.warehouse.domain.StkIoBill;
import com.spd.warehouse.service.IStkInventoryService;
import com.spd.warehouse.service.IStkIoBillService;

/**
 * 首页看板聚合数据：仅校验已登录，不校验各业务菜单权限，避免不同岗位人员打开首页即提示权限不足。
 * 数据口径与原先前端分别调用入退货/出退库/申领/申购/库存/消耗合计接口一致，仍受租户与数据范围 SQL 约束。
 */
@RestController
@RequestMapping("/dashboard/home")
public class HomeDashboardController extends BaseController
{
    @Autowired
    private IFdWarehouseService fdWarehouseService;

    @Autowired
    private ITenantScopeService tenantScopeService;

    @Autowired
    @Qualifier("stkIoBillServiceImpl")
    private IStkIoBillService stkIoBillService;

    @Autowired
    private IDeptBatchConsumeService deptBatchConsumeService;

    @Autowired
    private IBasApplyService basApplyService;

    @Autowired
    private IDepPurchaseApplyService depPurchaseApplyService;

    @Autowired
    private IStkInventoryService stkInventoryService;

    /**
     * 仓库采购情况图：按仓库、按月的入退货金额合计 + 出退库金额合计（与首页原逻辑一致：纯日期区间）。
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/warehousePurchase")
    public AjaxResult warehousePurchase(@RequestParam(value = "year", required = false) Integer year)
    {
        int y = year != null ? year : LocalDate.now().getYear();
        List<FdWarehouse> warehouses = scopedWarehouses();
        List<String> monthLabels = new ArrayList<>(12);
        for (int m = 1; m <= 12; m++)
        {
            monthLabels.add(m + "月");
        }
        Map<String, Object> body = new HashMap<>(4);
        body.put("year", y);
        body.put("monthLabels", monthLabels);
        if (warehouses == null || warehouses.isEmpty())
        {
            body.put("warehouses", new ArrayList<>());
            body.put("series", new ArrayList<>());
            return success(body);
        }
        int whCount = warehouses.size();
        BigDecimal[][] amtMatrix = new BigDecimal[whCount][12];
        BigDecimal[][] qtyMatrix = new BigDecimal[whCount][12];
        for (int wi = 0; wi < whCount; wi++)
        {
            for (int mi = 0; mi < 12; mi++)
            {
                amtMatrix[wi][mi] = BigDecimal.ZERO;
                qtyMatrix[wi][mi] = BigDecimal.ZERO;
            }
        }
        SimpleDateFormat dayFmt = new SimpleDateFormat("yyyy-MM-dd");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        IntStream.range(0, 12).parallel().forEach(mi ->
        {
            SecurityContext ctx = SecurityContextHolder.createEmptyContext();
            ctx.setAuthentication(auth);
            SecurityContextHolder.setContext(ctx);
            try
            {
                int month = mi + 1;
                YearMonth ym = YearMonth.of(y, month);
                LocalDate first = ym.atDay(1);
                LocalDate last = ym.atEndOfMonth();
                Date begin;
                Date end;
                try
                {
                    begin = dayFmt.parse(first.toString());
                    end = dayFmt.parse(last.toString());
                }
                catch (ParseException e)
                {
                    return;
                }
                for (int wi = 0; wi < whCount; wi++)
                {
                    FdWarehouse wh = warehouses.get(wi);
                    StkIoBill rthQ = new StkIoBill();
                    rthQ.setWarehouseId(wh.getId());
                    rthQ.setBeginDate(begin);
                    rthQ.setEndDate(end);
                    StkIoBill ctkQ = new StkIoBill();
                    ctkQ.setWarehouseId(wh.getId());
                    ctkQ.setBeginDate(begin);
                    ctkQ.setEndDate(end);
                    stkIoBillService.applyCtkDepartmentScopeToQuery(ctkQ);
                    TotalInfo rth = stkIoBillService.selectRTHStkIoBillListTotal(rthQ);
                    TotalInfo ctk = stkIoBillService.selectCTKStkIoBillListTotal(ctkQ);
                    BigDecimal amount = nz(rth.getTotalAmt()).abs().add(nz(ctk.getTotalAmt()).abs());
                    BigDecimal qty = nz(rth.getTotalQty()).abs().add(nz(ctk.getTotalQty()).abs());
                    amtMatrix[wi][mi] = amount.setScale(2, RoundingMode.HALF_UP);
                    qtyMatrix[wi][mi] = qty.setScale(2, RoundingMode.HALF_UP);
                }
            }
            finally
            {
                SecurityContextHolder.clearContext();
            }
        });
        List<Map<String, Object>> whBrief = new ArrayList<>();
        for (FdWarehouse w : warehouses)
        {
            Map<String, Object> m = new HashMap<>(2);
            m.put("id", w.getId());
            m.put("name", w.getName());
            whBrief.add(m);
        }
        List<Map<String, Object>> series = new ArrayList<>();
        for (int wi = 0; wi < whCount; wi++)
        {
            FdWarehouse wh = warehouses.get(wi);
            Map<String, Object> row = new HashMap<>(6);
            row.put("warehouseId", wh.getId());
            row.put("warehouseName", wh.getName());
            List<BigDecimal> amounts = new ArrayList<>(12);
            List<BigDecimal> qtys = new ArrayList<>(12);
            for (int mi = 0; mi < 12; mi++)
            {
                amounts.add(amtMatrix[wi][mi]);
                qtys.add(qtyMatrix[wi][mi]);
            }
            row.put("amounts", amounts);
            row.put("qtys", qtys);
            series.add(row);
        }
        body.put("warehouses", whBrief);
        body.put("series", series);
        return success(body);
    }

    /**
     * 科室使用记录图：每月出退库合计（领用）+ 已审核科室消耗合计（消耗），口径同原首页。
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/departmentUsage")
    public AjaxResult departmentUsage(@RequestParam(value = "year", required = false) Integer year)
    {
        int y = year != null ? year : LocalDate.now().getYear();
        List<String> monthLabels = new ArrayList<>(12);
        for (int m = 1; m <= 12; m++)
        {
            monthLabels.add(m + "月");
        }
        SimpleDateFormat dayFmt = new SimpleDateFormat("yyyy-MM-dd");
        BigDecimal[] receiveQty = new BigDecimal[12];
        BigDecimal[] receiveAmt = new BigDecimal[12];
        BigDecimal[] consumeQty = new BigDecimal[12];
        BigDecimal[] consumeAmt = new BigDecimal[12];
        for (int i = 0; i < 12; i++)
        {
            receiveQty[i] = BigDecimal.ZERO;
            receiveAmt[i] = BigDecimal.ZERO;
            consumeQty[i] = BigDecimal.ZERO;
            consumeAmt[i] = BigDecimal.ZERO;
        }
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        IntStream.range(0, 12).parallel().forEach(mi ->
        {
            SecurityContext ctx = SecurityContextHolder.createEmptyContext();
            ctx.setAuthentication(auth);
            SecurityContextHolder.setContext(ctx);
            try
            {
                int month = mi + 1;
                YearMonth ym = YearMonth.of(y, month);
                LocalDate first = ym.atDay(1);
                LocalDate last = ym.atEndOfMonth();
                LocalDateTime startDt = first.atStartOfDay();
                LocalDateTime endDt = last.atTime(23, 59, 59);
                Date ctkBegin = Date.from(startDt.atZone(ZoneId.systemDefault()).toInstant());
                Date ctkEnd = Date.from(endDt.atZone(ZoneId.systemDefault()).toInstant());
                StkIoBill ctkQ = new StkIoBill();
                ctkQ.setBeginDate(ctkBegin);
                ctkQ.setEndDate(ctkEnd);
                stkIoBillService.applyCtkDepartmentScopeToQuery(ctkQ);
                TotalInfo ctk = stkIoBillService.selectCTKStkIoBillListTotal(ctkQ);
                DeptBatchConsume dc = new DeptBatchConsume();
                try
                {
                    dc.setBeginDate(dayFmt.parse(first.toString()));
                    dc.setEndDate(dayFmt.parse(last.toString()));
                }
                catch (ParseException e)
                {
                    return;
                }
                TotalInfo consume = deptBatchConsumeService.selectAuditedConsumeReportTotal(dc);
                receiveQty[mi] = nz(ctk.getTotalQty()).abs().setScale(2, RoundingMode.HALF_UP);
                receiveAmt[mi] = nz(ctk.getTotalAmt()).abs().setScale(2, RoundingMode.HALF_UP);
                consumeQty[mi] = nz(consume.getTotalQty()).setScale(2, RoundingMode.HALF_UP);
                consumeAmt[mi] = nz(consume.getTotalAmt()).setScale(2, RoundingMode.HALF_UP);
            }
            finally
            {
                SecurityContextHolder.clearContext();
            }
        });
        Map<String, Object> body = new HashMap<>(8);
        body.put("year", y);
        body.put("monthLabels", monthLabels);
        body.put("receiveQty", asList(receiveQty));
        body.put("receiveAmt", asList(receiveAmt));
        body.put("consumeQty", asList(consumeQty));
        body.put("consumeAmt", asList(consumeAmt));
        return success(body);
    }

    /**
     * 今日统计：入库量、出库量、退库量、申领明细量、申购明细量、库存总数量（口径同原首页）。
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/todayStats")
    public AjaxResult todayStats(@RequestParam(value = "day", required = false) String day)
    {
        LocalDate d;
        try
        {
            d = StringUtils.isNotEmpty(day) ? LocalDate.parse(day.trim()) : LocalDate.now();
        }
        catch (Exception e)
        {
            d = LocalDate.now();
        }
        String dayStr = d.toString();
        SimpleDateFormat dtFmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date begin;
        Date end;
        try
        {
            begin = dtFmt.parse(dayStr + " 00:00:00");
            end = dtFmt.parse(dayStr + " 23:59:59");
        }
        catch (ParseException e)
        {
            return error("日期格式无效");
        }
        StkIoBill inQ = new StkIoBill();
        inQ.setBeginDate(begin);
        inQ.setEndDate(end);
        inQ.setBillType(101);
        TotalInfo inTotal = stkIoBillService.selectRTHStkIoBillListTotal(inQ);

        StkIoBill outQ = new StkIoBill();
        outQ.setBeginDate(begin);
        outQ.setEndDate(end);
        outQ.setBillType(201);
        stkIoBillService.applyCtkDepartmentScopeToQuery(outQ);
        TotalInfo outTotal = stkIoBillService.selectCTKStkIoBillListTotal(outQ);

        StkIoBill retQ = new StkIoBill();
        retQ.setBeginDate(begin);
        retQ.setEndDate(end);
        retQ.setBillType(401);
        stkIoBillService.applyCtkDepartmentScopeToQuery(retQ);
        TotalInfo retTotal = stkIoBillService.selectCTKStkIoBillListTotal(retQ);

        BasApply basApply = new BasApply();
        basApply.setApplyBillDate(java.sql.Date.valueOf(d));
        basApply.setBillType(1);
        basApply.setApplyBillStatus(2);
        basApplyService.applyDepartmentScopeToQuery(basApply);
        if (StringUtils.isEmpty(basApply.getTenantId()) && StringUtils.isNotEmpty(SecurityUtils.getCustomerId()))
        {
            basApply.setTenantId(SecurityUtils.getCustomerId());
        }
        BigDecimal applySum = basApplyService.selectBasApplyEntryQtySum(basApply);

        DepPurchaseApply purchase = new DepPurchaseApply();
        purchase.setPurchaseBillDate(java.sql.Date.valueOf(d));
        purchase.setPurchaseBillStatus(2);
        depPurchaseApplyService.applyDepartmentScopeToQuery(purchase);
        if (StringUtils.isEmpty(purchase.getTenantId()) && StringUtils.isNotEmpty(SecurityUtils.getCustomerId()))
        {
            purchase.setTenantId(SecurityUtils.getCustomerId());
        }
        BigDecimal purchaseSum = depPurchaseApplyService.selectDepPurchaseApplyEntryQtySum(purchase);

        StkInventory invQ = new StkInventory();
        TotalInfo invTotal = stkInventoryService.selectStkInventoryListTotal(invQ);

        Map<String, Object> body = new HashMap<>(12);
        body.put("day", dayStr);
        body.put("inCount", tableTotalQty(inTotal));
        body.put("outCount", tableTotalQty(outTotal));
        body.put("returnCount", Math.abs(tableTotalQty(retTotal)));
        body.put("applyCount", applySum != null ? applySum : BigDecimal.ZERO);
        body.put("purchaseCount", purchaseSum != null ? purchaseSum : BigDecimal.ZERO);
        body.put("inventoryQty", tableTotalQty(invTotal));
        return success(body);
    }

    private static List<BigDecimal> asList(BigDecimal[] arr)
    {
        List<BigDecimal> list = new ArrayList<>(arr.length);
        for (BigDecimal v : arr)
        {
            list.add(v != null ? v : BigDecimal.ZERO);
        }
        return list;
    }

    private static double tableTotalQty(TotalInfo ti)
    {
        if (ti == null)
        {
            return 0d;
        }
        BigDecimal raw = ti.getTotalQty() != null ? ti.getTotalQty() : ti.getSubTotalQty();
        double n = raw != null ? raw.doubleValue() : 0d;
        return Double.isFinite(n) ? n : 0d;
    }

    private static BigDecimal nz(BigDecimal v)
    {
        return v != null ? v : BigDecimal.ZERO;
    }

    private List<FdWarehouse> scopedWarehouses()
    {
        List<FdWarehouse> fdWarehouseList = fdWarehouseService.selectwarehouseAll();
        String customerId = SecurityUtils.requiredScopedTenantIdForSql();
        if (StringUtils.isNotEmpty(customerId) && fdWarehouseList != null
            && !tenantScopeService.isTenantSuper(SecurityUtils.getUserId(), customerId))
        {
            List<Long> allowedIds = tenantScopeService.resolveWarehouseScope(SecurityUtils.getUserId(), customerId);
            if (allowedIds == null || allowedIds.isEmpty())
            {
                fdWarehouseList = new ArrayList<>();
            }
            else
            {
                fdWarehouseList = fdWarehouseList.stream()
                    .filter(w -> w.getId() != null && allowedIds.contains(w.getId()))
                    .collect(java.util.stream.Collectors.toList());
            }
        }
        if (fdWarehouseList == null)
        {
            return new ArrayList<>();
        }
        return fdWarehouseList;
    }
}

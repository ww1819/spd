package com.spd.dashboard.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;
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
import com.spd.department.vo.WarehouseApplyReminderRowVo;
import com.spd.department.vo.WarehousePurchaseReminderRowVo;
import com.spd.warehouse.vo.WarehouseNearExpiryReminderRowVo;
import com.spd.department.domain.DepPurchaseApply;
import com.spd.department.domain.DeptBatchConsume;
import com.spd.department.service.IBasApplyService;
import com.spd.department.service.IDepPurchaseApplyService;
import com.spd.department.service.IStkDepInventoryService;
import com.spd.department.vo.DepartmentNearExpiryReminderRowVo;
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

    @Autowired
    private IStkDepInventoryService stkDepInventoryService;

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
        ZoneId zone = ZoneId.systemDefault();
        for (int mi = 0; mi < 12; mi++)
        {
            YearMonth ym = YearMonth.of(y, mi + 1);
            LocalDate first = ym.atDay(1);
            LocalDate last = ym.atEndOfMonth();
            ZonedDateTime zStart = first.atStartOfDay(zone);
            ZonedDateTime zEnd = last.atTime(23, 59, 59).atZone(zone);
            Date begin = Date.from(zStart.toInstant());
            Date end = Date.from(zEnd.toInstant());
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
                // 首页看板：按仓库汇总全租户出退库，不按当前用户科室范围过滤（否则无科室权限用户图表恒为 0）
                TotalInfo rth = stkIoBillService.selectRTHStkIoBillListTotal(rthQ);
                TotalInfo ctk = stkIoBillService.selectCTKStkIoBillListTotal(ctkQ);
                BigDecimal amount = nz(rth.getTotalAmt()).abs().add(nz(ctk.getTotalAmt()).abs());
                BigDecimal qty = nz(rth.getTotalQty()).abs().add(nz(ctk.getTotalQty()).abs());
                amtMatrix[wi][mi] = amount.setScale(2, RoundingMode.HALF_UP);
                qtyMatrix[wi][mi] = qty.setScale(2, RoundingMode.HALF_UP);
            }
        }
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
        SimpleDateFormat dayEndFmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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
        ZoneId zone = ZoneId.systemDefault();
        for (int mi = 0; mi < 12; mi++)
        {
            int month = mi + 1;
            YearMonth ym = YearMonth.of(y, month);
            LocalDate first = ym.atDay(1);
            LocalDate last = ym.atEndOfMonth();
            LocalDateTime startDt = first.atStartOfDay();
            LocalDateTime endDt = last.atTime(23, 59, 59);
            Date ctkBegin = Date.from(startDt.atZone(zone).toInstant());
            Date ctkEnd = Date.from(endDt.atZone(zone).toInstant());
            StkIoBill ctkQ = new StkIoBill();
            ctkQ.setBeginDate(ctkBegin);
            ctkQ.setEndDate(ctkEnd);
            // 首页看板：租户级领用（出退库）合计，不按当前用户科室过滤
            TotalInfo ctk = stkIoBillService.selectCTKStkIoBillListTotal(ctkQ);
            DeptBatchConsume dc = new DeptBatchConsume();
            try
            {
                dc.setBeginDate(dayFmt.parse(first.toString()));
                dc.setEndDate(dayEndFmt.parse(last.toString() + " 23:59:59"));
            }
            catch (ParseException e)
            {
                continue;
            }
            TotalInfo consume = deptBatchConsumeService.selectAuditedConsumeReportTotal(dc);
            receiveQty[mi] = nz(ctk.getTotalQty()).abs().setScale(2, RoundingMode.HALF_UP);
            receiveAmt[mi] = nz(ctk.getTotalAmt()).abs().setScale(2, RoundingMode.HALF_UP);
            consumeQty[mi] = nz(consume.getTotalQty()).setScale(2, RoundingMode.HALF_UP);
            consumeAmt[mi] = nz(consume.getTotalAmt()).setScale(2, RoundingMode.HALF_UP);
        }
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

    /**
     * 仓库消息提醒：当前用户科室数据范围内，待审核申领单（bill_type=1）与待审核申购单单据数（与各自审核列表口径一致）。
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/warehouseReminderCounts")
    public AjaxResult warehouseReminderCounts()
    {
        long pendingApply = basApplyService.countPendingAuditApplyRequisition();
        long pendingPurchase = depPurchaseApplyService.countPendingAuditPurchaseApply();
        long nearExpiryLines = stkInventoryService.countWarehouseNearExpiryReminderLines();
        long inventoryAlertLines = stkInventoryService.countWarehouseInventoryAlertReminderLines();
        Map<String, Object> body = new HashMap<>(8);
        body.put("pendingApplyBillCount", pendingApply);
        body.put("pendingPurchaseBillCount", pendingPurchase);
        body.put("nearExpiryInventoryLineCount", nearExpiryLines);
        body.put("inventoryAlertLineCount", inventoryAlertLines);
        return success(body);
    }

    /**
     * 消息提醒：待出库申领单列表（含关联已审核出库时间），仅登录；科室数据范围与申领列表一致。
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/warehouseReminderApplyList")
    public AjaxResult warehouseReminderApplyList()
    {
        List<WarehouseApplyReminderRowVo> list = basApplyService.selectWarehouseReminderApplyMonitorList();
        return success(list);
    }

    /**
     * 消息提醒：科室申购监控列表（待审核/已审核，仅登录；科室数据范围与申购审核列表一致）
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/warehouseReminderPurchaseList")
    public AjaxResult warehouseReminderPurchaseList()
    {
        List<WarehousePurchaseReminderRowVo> list = depPurchaseApplyService.selectWarehouseReminderPurchaseMonitorList();
        return success(list);
    }

    /**
     * 消息提醒：仓库库存近效期明细（有效期距今天在 30 天及以内且未过期，仅登录）
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/warehouseReminderNearExpiryList")
    public AjaxResult warehouseReminderNearExpiryList()
    {
        List<WarehouseNearExpiryReminderRowVo> list = stkInventoryService.selectWarehouseNearExpiryReminderList();
        return success(list);
    }

    /**
     * 消息提醒：库存预警明细（与库存查询「库存预警」一致，仅「预警」行，最多 500 条，仅登录）
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/warehouseReminderInventoryAlertList")
    public AjaxResult warehouseReminderInventoryAlertList()
    {
        List<Map<String, Object>> list = stkInventoryService.selectWarehouseInventoryAlertReminderList();
        return success(list);
    }

    /**
     * 科室消息提醒：未收货确认的已审核出库单（与「科室收货确认」列表口径一致；billCount 为全量计数，bills 最多 500 条）
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/departmentReminderUnreceivedReceipt")
    public AjaxResult departmentReminderUnreceivedReceipt()
    {
        long billCount = stkIoBillService.countDepartmentUnreceivedReceiptReminder();
        List<StkIoBill> bills = stkIoBillService.selectDepartmentUnreceivedReceiptReminderList();
        Map<String, Object> body = new HashMap<>(4);
        body.put("billCount", billCount);
        body.put("bills", bills);
        return success(body);
    }

    /**
     * 消息提醒：科室近效期库存明细（与科室库存查询「近效期」一致；lineCount 全量，lines 最多 500 条）
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/departmentReminderNearExpiryList")
    public AjaxResult departmentReminderNearExpiryList()
    {
        long lineCount = stkDepInventoryService.countDepartmentNearExpiryReminderMonitor();
        List<DepartmentNearExpiryReminderRowVo> lines = stkDepInventoryService.selectDepartmentNearExpiryReminderMonitorList();
        Map<String, Object> body = new HashMap<>(4);
        body.put("lineCount", lineCount);
        body.put("lines", lines);
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
            // resolveWarehouseScope 对租户超管返回 null 表示不限制；非超管不应为 null，若为空列表则无仓库权限
            if (allowedIds != null)
            {
                if (allowedIds.isEmpty())
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
        }
        if (fdWarehouseList == null)
        {
            return new ArrayList<>();
        }
        return fdWarehouseList;
    }
}

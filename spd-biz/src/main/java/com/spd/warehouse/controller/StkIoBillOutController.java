package com.spd.warehouse.controller;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.spd.common.annotation.Log;
import com.spd.common.annotation.RepeatSubmit;
import com.spd.common.core.controller.BaseController;
import com.spd.common.core.domain.AjaxResult;
import com.spd.common.core.page.TableDataInfo;
import com.spd.common.core.page.TotalInfo;
import com.spd.common.enums.BusinessType;
import com.spd.common.exception.ServiceException;
import com.spd.common.utils.poi.ExcelUtil;
import com.spd.department.domain.DepPurchaseApply;
import com.spd.department.domain.WhWarehouseApply;
import com.spd.department.service.IDepPurchaseApplyService;
import com.spd.department.service.IWhWarehouseApplyService;
import com.spd.warehouse.domain.StkIoBill;
import com.spd.warehouse.domain.vo.MaterialWarehouseStockAgg;
import com.spd.warehouse.mapper.StkInventoryMapper;
import com.spd.warehouse.service.IStkIoBillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 出库Controller
 *
 * @author spd
 * @date 2023-12-17
 */
@RestController
@RequestMapping("/warehouse/outWarehouse")
public class StkIoBillOutController extends BaseController {

    private static final Logger log = LoggerFactory.getLogger(StkIoBillOutController.class);

    @Qualifier("stkIoBillServiceImpl")
    @Autowired
    private IStkIoBillService stkIoBillService;

    @Autowired
    private IWhWarehouseApplyService whWarehouseApplyService;

    @Autowired
    private IDepPurchaseApplyService depPurchaseApplyService;

    @Autowired
    private StkInventoryMapper stkInventoryMapper;

    /**
     * 按仓库+耗材批量查询当前库存数量（出库明细「库存数量」列）
     */
    @PreAuthorize("@ss.hasPermi('outWarehouse:apply:list') || @ss.hasPermi('outWarehouse:apply:add') || @ss.hasPermi('outWarehouse:apply:edit') || @ss.hasPermi('outWarehouse:apply:query')")
    @GetMapping("/materialStockQty")
    public AjaxResult materialStockQty(@RequestParam("warehouseId") Long warehouseId,
                                       @RequestParam(name = "materialIds", required = false) String materialIdsStr)
    {
        if (warehouseId == null)
        {
            return success(new HashMap<>());
        }
        List<Long> materialIds = parseMaterialIds(materialIdsStr);
        if (materialIds.isEmpty())
        {
            return success(new HashMap<>());
        }
        Map<String, BigDecimal> body = new HashMap<>();
        List<MaterialWarehouseStockAgg> aggs = stkInventoryMapper.selectSumQtyGroupByMaterialAndWarehouse(warehouseId, materialIds);
        if (aggs != null)
        {
            for (MaterialWarehouseStockAgg agg : aggs)
            {
                if (agg != null && agg.getMaterialId() != null)
                {
                    body.put(String.valueOf(agg.getMaterialId()),
                        agg.getSumQty() != null ? agg.getSumQty() : BigDecimal.ZERO);
                }
            }
        }
        for (Long materialId : materialIds)
        {
            body.putIfAbsent(String.valueOf(materialId), BigDecimal.ZERO);
        }
        return success(body);
    }

    private List<Long> parseMaterialIds(String materialIdsStr)
    {
        List<Long> ids = new ArrayList<>();
        if (!StringUtils.hasText(materialIdsStr))
        {
            return ids;
        }
        for (String part : materialIdsStr.split(","))
        {
            if (part == null)
            {
                continue;
            }
            String trimmed = part.trim();
            if (trimmed.isEmpty())
            {
                continue;
            }
            ids.add(Long.valueOf(trimmed));
        }
        return ids;
    }

    /**
     * 出库引用：分页查询仍有可出库数量的仓库申请单（科室申领按仓拆分后的 CKSQ 单）。
     * 权限与出库申请页、科室/仓库申请单列表对齐，避免仅有 list/add 而无 createCkEntriesByDApply 按钮权限时 403。
     */
    @PreAuthorize("@ss.hasPermi('outWarehouse:apply:list') || @ss.hasPermi('outWarehouse:apply:add') || @ss.hasPermi('outWarehouse:apply:edit') || @ss.hasPermi('outWarehouse:apply:createCkEntriesByDApply') || @ss.hasPermi('department:whWarehouseApply:list') || @ss.hasPermi('department:dApply:list')")
    @GetMapping("/whApplyListForCk")
    public TableDataInfo whApplyListForCk(WhWarehouseApply query) {
        startPage();
        List<WhWarehouseApply> list = whWarehouseApplyService.selectWhWarehouseApplyListForOutboundCk(query);
        return getDataTable(list);
    }

    /**
     * 出库引用：分页查询科室申购单（按引用状态页签筛选）。
     */
    @PreAuthorize("@ss.hasPermi('outWarehouse:apply:list') || @ss.hasPermi('outWarehouse:apply:add') || @ss.hasPermi('outWarehouse:apply:edit') || @ss.hasPermi('outWarehouse:apply:createCkEntriesByDApply') || @ss.hasPermi('department:purchase:list')")
    @GetMapping("/depPurchaseApplyListForCk")
    public TableDataInfo depPurchaseApplyListForCk(DepPurchaseApply query) {
        startPage();
        List<DepPurchaseApply> list = depPurchaseApplyService.selectDepPurchaseApplyListForOutboundCk(query);
        return getDataTable(list);
    }

    /**
     * 查询出库列表
     */
    @PreAuthorize("@ss.hasPermi('outWarehouse:apply:list')")
    @GetMapping("/list")
    public TableDataInfo list(StkIoBill stkIoBill)
    {
        // 合计必须在 startPage 之前查，避免 PageHelper 干扰聚合结果
        clearPage();
        TotalInfo totalInfo = stkIoBillService.selectStkIoBillTotal(stkIoBill);
        if (totalInfo == null) {
            totalInfo = new TotalInfo();
        }
        if (totalInfo.getTotalAmt() == null) {
            totalInfo.setTotalAmt(BigDecimal.ZERO);
        }
        startPage();
        List<StkIoBill> list = stkIoBillService.selectStkIoBillList(stkIoBill);
        Long total = new com.github.pagehelper.PageInfo<>(list).getTotal();
        return getDataTable(list, totalInfo, total);
    }

    /**
     * 获取入库详细信息
     */
    @PreAuthorize("@ss.hasPermi('outWarehouse:apply:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(stkIoBillService.selectStkIoBillById(id));
    }

    /**
     * 记录出库单打印（更新打印状态、打印人、打印时间）
     */
    @Log(title = "出库单打印", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('outWarehouse:apply:query') || @ss.hasPermi('outWarehouse:apply:audit')")
    @PutMapping("/recordPrint/{id}")
    public AjaxResult recordPrint(@PathVariable("id") Long id)
    {
        return toAjax(stkIoBillService.recordStkIoBillPrint(id));
    }

    /**
     * 新增出库
     */
    @Log(title = "出库", businessType = BusinessType.INSERT)
    @PreAuthorize("@ss.hasPermi('outWarehouse:apply:add')")
    @RepeatSubmit(interval = 3000, message = "出库单正在保存，请勿重复提交")
    @PostMapping("/addOutWarehouse")
    public AjaxResult addOutWarehouse(@RequestBody StkIoBill stkIoBill)
    {
        int rows = stkIoBillService.insertOutStkIoBill(stkIoBill);
        if (rows > 0) {
            Integer filteredCount = stkIoBill.getDedupFilteredCount();
            String msg = (filteredCount != null && filteredCount > 0)
                ? String.format("新增成功，后台已自动过滤 %d 条重复明细", filteredCount)
                : "新增成功";
            return AjaxResult.success(msg, stkIoBill);
        }
        return AjaxResult.error("新增失败");
    }

    /**
     * 修改出库
     */
    @PreAuthorize("@ss.hasPermi('outWarehouse:apply:edit')")
    @Log(title = "出库", businessType = BusinessType.UPDATE)
    @RepeatSubmit(interval = 3000, message = "出库单正在保存，请勿重复提交")
    @PutMapping("/updateOutWarehouse")
    public AjaxResult updateOutWarehouse(@RequestBody StkIoBill stkIoBill)
    {
        int rows = stkIoBillService.updateOutStkIoBill(stkIoBill);
        if (rows > 0) {
            Integer filteredCount = stkIoBill.getDedupFilteredCount();
            String msg = (filteredCount != null && filteredCount > 0)
                ? String.format("修改成功，后台已自动过滤 %d 条重复明细", filteredCount)
                : "修改成功";
            return AjaxResult.success(msg, stkIoBill);
        }
        return AjaxResult.error("修改失败");
    }

    /**
     * 审核出库
     */
    @Log(title = "出库", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('outWarehouse:apply:audit')")
    @PutMapping("/auditOutWarehouse")
    public AjaxResult auditOutWarehouse(@RequestBody JSONObject json)
    {
        long t0 = System.currentTimeMillis();
        int result = stkIoBillService.auditStkIoBill(json.getString("id"), getUserIdStr());
        long cost = System.currentTimeMillis() - t0;
        if (cost >= 5000L)
        {
            log.error("出库审核慢链路 costMs={} billId={}", cost, json.getString("id"));
        }
        else if (cost >= 3000L)
        {
            log.warn("出库审核偏慢 costMs={} billId={}", cost, json.getString("id"));
        }
        return toAjax(result);
    }

    /**
     * 批量审核出库：服务端串行处理，避免前端 Promise.all 并发打满连接池导致超时。
     * body: { ids: ["1","2",...] }
     */
    @Log(title = "出库批量审核", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('outWarehouse:apply:audit')")
    @PutMapping("/auditOutWarehouse/batch")
    public AjaxResult auditOutWarehouseBatch(@RequestBody JSONObject json)
    {
        JSONArray arr = json == null ? null : json.getJSONArray("ids");
        if (arr == null || arr.isEmpty())
        {
            return AjaxResult.error("请选择要审核的出库单");
        }
        String auditBy = getUserIdStr();
        int ok = 0;
        List<Map<String, String>> errors = new ArrayList<>();
        long t0 = System.currentTimeMillis();
        for (int i = 0; i < arr.size(); i++)
        {
            String id = arr.getString(i);
            if (!StringUtils.hasText(id))
            {
                continue;
            }
            long oneT0 = System.currentTimeMillis();
            try
            {
                stkIoBillService.auditStkIoBill(id.trim(), auditBy);
                ok++;
                long oneCost = System.currentTimeMillis() - oneT0;
                if (oneCost >= 3000L)
                {
                    log.warn("批量出库审核单笔偏慢 costMs={} billId={} index={}/{}",
                        oneCost, id, i + 1, arr.size());
                }
            }
            catch (ServiceException e)
            {
                Map<String, String> err = new HashMap<>();
                err.put("id", id);
                err.put("msg", e.getMessage() == null ? "审核失败" : e.getMessage());
                errors.add(err);
                log.warn("批量出库审核业务失败 billId={} msg={}", id, e.getMessage());
            }
            catch (Exception e)
            {
                Map<String, String> err = new HashMap<>();
                err.put("id", id);
                err.put("msg", e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage());
                errors.add(err);
                log.error("批量出库审核异常 billId={}", id, e);
            }
        }
        long cost = System.currentTimeMillis() - t0;
        Map<String, Object> data = new HashMap<>();
        data.put("okCount", ok);
        data.put("failCount", errors.size());
        data.put("errors", errors);
        data.put("costMs", cost);
        if (cost >= 30000L || (arr.size() >= 10 && cost >= 15000L))
        {
            log.error("批量出库审核慢/可能超时 costMs={} total={} ok={} fail={}",
                cost, arr.size(), ok, errors.size());
        }
        else
        {
            log.info("批量出库审核完成 costMs={} total={} ok={} fail={}",
                cost, arr.size(), ok, errors.size());
        }
        if (ok == 0 && !errors.isEmpty())
        {
            String firstMsg = errors.get(0).get("msg");
            return AjaxResult.error("批量审核全部失败：" + firstMsg, data);
        }
        if (!errors.isEmpty())
        {
            return AjaxResult.success(
                String.format("批量审核完成：成功 %d，失败 %d。首条失败：%s",
                    ok, errors.size(), errors.get(0).get("msg")),
                data);
        }
        return AjaxResult.success(String.format("批量审核成功，共 %d 单", ok), data);
    }

    /**
     * 删除出库单（逻辑删除；若引用库房申请单会先解除 wh_wh_apply_ck_entry_ref）
     */
    @PreAuthorize("@ss.hasPermi('outWarehouse:apply:remove')")
    @Log(title = "出库", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long ids)
    {
        return toAjax(stkIoBillService.deleteStkIoBillById(ids));
    }

    /**
     * 导出出库单主表（旧）
     */
    @PreAuthorize("@ss.hasPermi('outWarehouse:apply:export')")
    @Log(title = "出入库", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, StkIoBill stkIoBill)
    {
        List<StkIoBill> list = stkIoBillService.selectStkIoBillList(stkIoBill);
        ExcelUtil<StkIoBill> util = new ExcelUtil<StkIoBill>(StkIoBill.class);
        util.exportExcel(response, list, "出入库数据");
    }

    /**
     * 出库单导出：按单据隔离（单据号、科室名称 + 明细：名称、规格、型号、单位、数量、批号、有效期）
     * 参数与列表查询一致；可选 exportBillIds=1,2,3 仅导出勾选单据
     */
    @PreAuthorize("@ss.hasPermi('outWarehouse:apply:export')")
    @Log(title = "出库单按单导出", businessType = BusinessType.EXPORT)
    @PostMapping("/exportGroupedByBill")
    public void exportGroupedByBill(HttpServletResponse response, StkIoBill stkIoBill) throws IOException
    {
        stkIoBillService.exportOutWarehouseGroupedByBill(stkIoBill, response);
    }

    @PreAuthorize("@ss.hasPermi('outWarehouse:audit:export')")
    @Log(title = "出库单按单导出", businessType = BusinessType.EXPORT)
    @PostMapping("/auditExportGroupedByBill")
    public void auditExportGroupedByBill(HttpServletResponse response, StkIoBill stkIoBill) throws IOException
    {
        stkIoBillService.exportOutWarehouseGroupedByBill(stkIoBill, response);
    }

    @PreAuthorize("@ss.hasPermi('outWarehouse:apply:list') || @ss.hasPermi('outWarehouse:apply:add') || @ss.hasPermi('outWarehouse:apply:edit') || @ss.hasPermi('outWarehouse:apply:createCkEntriesByDApply') || @ss.hasPermi('department:whWarehouseApply:list') || @ss.hasPermi('department:dApply:list')")
    @GetMapping("/createCkEntriesByWhApply")
    public AjaxResult createCkEntriesByWhApply(@RequestParam String whWarehouseApplyId) {
        if (whWarehouseApplyId == null) {
            throw new RuntimeException("仓库申请单ID不能为空");
        }
        StkIoBill stkIoBill1 = stkIoBillService.createCkEntriesByWhApply(whWarehouseApplyId);
        return success(stkIoBill1);
    }

    @PreAuthorize("@ss.hasPermi('outWarehouse:apply:list') || @ss.hasPermi('outWarehouse:apply:add') || @ss.hasPermi('outWarehouse:apply:edit') || @ss.hasPermi('outWarehouse:apply:createCkEntriesByDApply') || @ss.hasPermi('department:purchase:list')")
    @GetMapping("/createCkEntriesByDepPurchaseApply")
    public AjaxResult createCkEntriesByDepPurchaseApply(@RequestParam Long depPurchaseApplyId) {
        if (depPurchaseApplyId == null) {
            throw new RuntimeException("科室申购单ID不能为空");
        }
        StkIoBill stkIoBill1 = stkIoBillService.createCkEntriesByDepPurchaseApply(depPurchaseApplyId);
        return success(stkIoBill1);
    }

    @PreAuthorize("@ss.hasPermi('outWarehouse:apply:createCkEntriesByDApply')")
    @GetMapping("/createCkEntriesByDApply")
    public AjaxResult createCkEntriesByDApply(@RequestParam String dApplyId) {
//        if (stkIoBill == null){
//            throw new RuntimeException("科室申领ID不能为空");
//        }
//        String dApplyId = stkIoBill.getDApplyId();
        if (dApplyId == null) {
            throw new RuntimeException("科室申领ID不能为空");
        }
        StkIoBill stkIoBill1 = stkIoBillService.createCkEntriesByDApply(dApplyId);
        return success(stkIoBill1);
    }

    /**
     * 引用入库单生成出库明细草稿。权限与 createCkEntriesByWhApply 一致，
     * 避免仅有出库新增/修改却无独立按钮权限 createCkEntriesByRkApply 时 403。
     */
    @PreAuthorize("@ss.hasPermi('outWarehouse:apply:list') || @ss.hasPermi('outWarehouse:apply:add') || @ss.hasPermi('outWarehouse:apply:edit') || @ss.hasPermi('outWarehouse:apply:createCkEntriesByDApply') || @ss.hasPermi('outWarehouse:apply:createCkEntriesByRkApply') || @ss.hasPermi('department:whWarehouseApply:list') || @ss.hasPermi('department:dApply:list')")
    @GetMapping("/createCkEntriesByRkApply")
    public AjaxResult createCkEntriesByRkApply(@RequestParam String rkApplyId) {
//        if (stkIoBill == null){
//            throw new RuntimeException("科室申领ID不能为空");
//        }
//        String dApplyId = stkIoBill.getDApplyId();
        if (rkApplyId == null) {
            throw new RuntimeException("入库单ID不能为空");
        }
        StkIoBill stkIoBill1 = stkIoBillService.createCkEntriesByRkApply(rkApplyId);
        return success(stkIoBill1);
    }
}

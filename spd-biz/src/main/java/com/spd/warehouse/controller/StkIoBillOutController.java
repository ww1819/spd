package com.spd.warehouse.controller;

import com.alibaba.fastjson2.JSONObject;
import com.spd.common.annotation.Log;
import com.spd.common.core.controller.BaseController;
import com.spd.common.core.domain.AjaxResult;
import com.spd.common.core.page.TableDataInfo;
import com.spd.common.enums.BusinessType;
import com.spd.common.utils.poi.ExcelUtil;
import com.spd.department.domain.WhWarehouseApply;
import com.spd.department.service.IWhWarehouseApplyService;
import com.spd.warehouse.domain.StkIoBill;
import com.spd.warehouse.service.IStkIoBillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * 出库Controller
 *
 * @author spd
 * @date 2023-12-17
 */
@RestController
@RequestMapping("/warehouse/outWarehouse")
public class StkIoBillOutController extends BaseController {

    @Qualifier("stkIoBillServiceImpl")
    @Autowired
    private IStkIoBillService stkIoBillService;

    @Autowired
    private IWhWarehouseApplyService whWarehouseApplyService;

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
     * 查询出库列表
     */
    @PreAuthorize("@ss.hasPermi('outWarehouse:apply:list')")
    @GetMapping("/list")
    public TableDataInfo list(StkIoBill stkIoBill)
    {
        startPage();
        List<StkIoBill> list = stkIoBillService.selectStkIoBillList(stkIoBill);
        return getDataTable(list);
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
     * 新增出库
     */
    @Log(title = "出库", businessType = BusinessType.INSERT)
    @PreAuthorize("@ss.hasPermi('outWarehouse:apply:add')")
    @PostMapping("/addOutWarehouse")
    public AjaxResult addOutWarehouse(@RequestBody StkIoBill stkIoBill)
    {
        int rows = stkIoBillService.insertOutStkIoBill(stkIoBill);
        if (rows > 0) {
            return success(stkIoBill);
        }
        return AjaxResult.error("新增失败");
    }

    /**
     * 修改出库
     */
    @PreAuthorize("@ss.hasPermi('outWarehouse:apply:edit')")
    @Log(title = "出库", businessType = BusinessType.UPDATE)
    @PutMapping("/updateOutWarehouse")
    public AjaxResult updateOutWarehouse(@RequestBody StkIoBill stkIoBill)
    {
        return toAjax(stkIoBillService.updateOutStkIoBill(stkIoBill));
    }

    /**
     * 审核出库
     */
    @Log(title = "出库", businessType = BusinessType.UPDATE)
    @PreAuthorize("@ss.hasPermi('outWarehouse:apply:audit')")
    @PutMapping("/auditOutWarehouse")
    public AjaxResult auditOutWarehouse(@RequestBody JSONObject json)
    {
        int result = stkIoBillService.auditStkIoBill(json.getString("id"), getUserIdStr());
        return toAjax(result);
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

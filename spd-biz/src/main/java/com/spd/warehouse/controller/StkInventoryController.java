package com.spd.warehouse.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;

import com.github.pagehelper.PageInfo;
import com.spd.common.core.page.PageDomain;
import com.spd.common.core.page.TotalInfo;
import com.spd.foundation.domain.FdMaterial;
import com.spd.foundation.domain.FdWarehouse;
import com.spd.warehouse.vo.StkInventorySummaryVo;
import com.spd.warehouse.vo.StkInventoryVo;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.spd.common.annotation.Log;
import com.spd.common.core.controller.BaseController;
import com.spd.common.core.domain.AjaxResult;
import com.spd.common.enums.BusinessType;
import com.spd.warehouse.domain.StkInventory;
import com.spd.warehouse.service.IStkInventoryService;
import com.spd.common.utils.poi.ExcelUtil;
import com.spd.common.core.page.TableDataInfo;

/**
 * 库存明细Controller
 *
 * @author spd
 * @date 2023-12-17
 */
@RestController
@RequestMapping("/warehouse/inventory")
public class StkInventoryController extends BaseController
{
    @Autowired
    private IStkInventoryService stkInventoryService;

    /**
     * 查询库存明细列表
     */
    @PreAuthorize("@ss.hasPermi('warehouse:inventory:list')")
    @GetMapping("/list")
    public TableDataInfo list(StkInventory stkInventory)
    {
        startPage();
        List<StkInventory> list = stkInventoryService.selectStkInventoryList(stkInventory);
        BigDecimal subTotalQty = BigDecimal.ZERO;
        BigDecimal subTotalAmt = BigDecimal.ZERO;
        for (StkInventory inventory : list) {
            subTotalQty = subTotalQty.add(inventory.getQty());
            subTotalAmt = subTotalAmt.add(inventory.getAmt());
        }

        TotalInfo totalInfo = stkInventoryService.selectStkInventoryListTotal(stkInventory);
        totalInfo.setSubTotalQty(subTotalQty);
        totalInfo.setSubTotalAmt(subTotalAmt);
        Long total = new PageInfo(list).getTotal();
        return getDataTable(list, totalInfo, total);
    }

    /**
     * 查询库存明细汇总列表
     */
    @GetMapping("/listInventorySummary")
    public TableDataInfo listInventorySummary(StkInventory stkInventory)
    {
        List<Map<String, Object>> mapList = stkInventoryService.selectStkInventoryListSummary(stkInventory);
//        startPage();
        List<StkInventorySummaryVo> summaryVoList = new ArrayList<StkInventorySummaryVo>();
        BigDecimal subTotalQty = BigDecimal.ZERO;
        BigDecimal subTotalAmt = BigDecimal.ZERO;
        for(Map<String, Object> map : mapList){
            StkInventorySummaryVo inventoryVo = new StkInventorySummaryVo();
            inventoryVo.setId((Long) map.get("id"));
            inventoryVo.setMaterialCode(map.get("materialCode").toString());
            inventoryVo.setMaterialName(map.get("materialName").toString());
            inventoryVo.setMaterialModel(map.get("materialModel").toString());
            inventoryVo.setMaterialQty((BigDecimal) map.get("materialQty"));
            inventoryVo.setMaterialSpeci(map.get("materialSpeci").toString());
            inventoryVo.setMaterialAmt((BigDecimal) map.get("materialAmt"));
            inventoryVo.setUnitName(map.get("unitName").toString());
            inventoryVo.setUnitPrice((BigDecimal) map.get("unitPrice"));
            inventoryVo.setWarehouseName(map.get("warehouseName").toString());
            inventoryVo.setFactoryName(map.get("factoryName").toString());
            inventoryVo.setSupplierName(map.get("supplierName").toString());
            summaryVoList.add(inventoryVo);
            BigDecimal materialQty, materialAmt;
            if (map.get("materialQty") == null) {
                materialQty = BigDecimal.ZERO;
            } else {
                materialQty = (BigDecimal) map.get("materialQty");
            }
            if (map.get("materialAmt") == null) {
                materialAmt = BigDecimal.ZERO;
            } else {
                materialAmt = (BigDecimal) map.get("materialAmt");
            }
            subTotalQty = subTotalQty.add(materialQty);
            subTotalAmt = subTotalAmt.add(materialAmt);
        }
        Long total = Long.valueOf(summaryVoList.size());
        summaryVoList = subListPage(summaryVoList);
        TotalInfo totalInfo = stkInventoryService.selectStkInventoryListSummaryTotal(stkInventory);
        totalInfo.setSubTotalQty(subTotalQty);
        totalInfo.setSubTotalAmt(subTotalAmt);
        return getDataTable(summaryVoList,totalInfo, total);
    }

    /**
     * 按仓库过滤库存明细耗材、基础耗材
     * @param stkInventory
     * @return
     */
    @GetMapping("/listPDFilter")
    public TableDataInfo ListPDInventoryFilter(StkInventory stkInventory)
    {
        List<StkInventory> list = stkInventoryService.selectPDInventoryFilter(stkInventory);
        return getDataTable(list);
    }

    /**
     * 按仓库筛选实时库存耗材
     */
    @GetMapping("/listInventoryMaterialAll")
    public List<StkInventoryVo> listInventoryMaterialAll(StkInventory stkInventory)
    {
        List<StkInventory> stkInventoryList = stkInventoryService.selectStkMaterialList(stkInventory);
        List<StkInventoryVo> stkInventoryVos = new ArrayList<StkInventoryVo>();
        for(StkInventory list : stkInventoryList){
            StkInventoryVo stkInventoryVo = new StkInventoryVo();
            stkInventoryVo.setId(list.getId());
            FdMaterial material = list.getMaterial();
            stkInventoryVo.setMaterialName(material.getName());
            stkInventoryVos.add(stkInventoryVo);
        }
        return stkInventoryVos;
    }

    /**
     * 导出库存明细列表
     */
    @PreAuthorize("@ss.hasPermi('warehouse:inventory:export')")
    @Log(title = "库存明细", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, StkInventory stkInventory)
    {
        List<StkInventory> list = stkInventoryService.selectStkInventoryList(stkInventory);
        ExcelUtil<StkInventory> util = new ExcelUtil<StkInventory>(StkInventory.class);
        util.exportExcel(response, list, "库存明细数据");
    }

    /**
     * 获取库存明细详细信息
     */
    @PreAuthorize("@ss.hasPermi('warehouse:inventory:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(stkInventoryService.selectStkInventoryById(id));
    }

    /**
     * 新增库存明细
     */
    @PreAuthorize("@ss.hasPermi('warehouse:inventory:add')")
    @Log(title = "库存明细", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody StkInventory stkInventory)
    {
        return toAjax(stkInventoryService.insertStkInventory(stkInventory));
    }

    /**
     * 修改库存明细
     */
    @PreAuthorize("@ss.hasPermi('warehouse:inventory:edit')")
    @Log(title = "库存明细", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody StkInventory stkInventory)
    {
        return toAjax(stkInventoryService.updateStkInventory(stkInventory));
    }

    /**
     * 删除库存明细
     */
    @PreAuthorize("@ss.hasPermi('warehouse:inventory:remove')")
    @Log(title = "库存明细", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(stkInventoryService.deleteStkInventoryByIds(ids));
    }
}

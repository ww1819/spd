package com.spd.gz.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson2.JSONObject;
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
import com.spd.gz.domain.GzRefundStock;
import com.spd.gz.service.IGzRefundStockService;
import com.spd.common.utils.poi.ExcelUtil;
import com.spd.common.core.page.TableDataInfo;

/**
 * 高值退库Controller
 *
 * @author spd
 * @date 2024-06-11
 */
@RestController
@RequestMapping("/gz/stock")
public class GzRefundStockController extends BaseController
{
    @Autowired
    private IGzRefundStockService gzRefundStockService;

    /**
     * 查询高值退库列表
     */
    @PreAuthorize("@ss.hasPermi('gzShipment:stockApply:list')")
    @GetMapping("/list")
    public TableDataInfo list(GzRefundStock gzRefundStock)
    {
        startPage();
        List<GzRefundStock> list = gzRefundStockService.selectGzRefundStockList(gzRefundStock);
        return getDataTable(list);
    }

    /**
     * 导出高值退库列表
     */
    @PreAuthorize("@ss.hasPermi('gzShipment:stockApply:export')")
    @Log(title = "高值退库", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, GzRefundStock gzRefundStock)
    {
        List<GzRefundStock> list = gzRefundStockService.selectGzRefundStockList(gzRefundStock);
        ExcelUtil<GzRefundStock> util = new ExcelUtil<GzRefundStock>(GzRefundStock.class);
        util.exportExcel(response, list, "高值退库数据");
    }

    /**
     * 获取高值退库详细信息
     */
    @PreAuthorize("@ss.hasPermi('gzShipment:stockApply:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(gzRefundStockService.selectGzRefundStockById(id));
    }

    /**
     * 新增高值退库
     */
    @PreAuthorize("@ss.hasPermi('gzShipment:stockApply:add')")
    @Log(title = "高值退库", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody GzRefundStock gzRefundStock)
    {
        return toAjax(gzRefundStockService.insertGzRefundStock(gzRefundStock));
    }

    /**
     * 修改高值退库
     */
    @PreAuthorize("@ss.hasPermi('gzShipment:stockApply:edit')")
    @Log(title = "高值退库", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody GzRefundStock gzRefundStock)
    {
        return toAjax(gzRefundStockService.updateGzRefundStock(gzRefundStock));
    }

    /**
     * 删除高值退库
     */
    @PreAuthorize("@ss.hasPermi('gzShipment:stockApply:remove')")
    @Log(title = "高值退库", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long id)
    {
        return toAjax(gzRefundStockService.deleteGzRefundStockById(id));
    }

    /**
     * 审核高值入库
     */
    @PreAuthorize("@ss.hasPermi('gzShipment:stockApply:audit')")
    @Log(title = "高值入库", businessType = BusinessType.UPDATE)
    @PutMapping("/auditStock")
    public AjaxResult audit(@RequestBody JSONObject json)
    {
        int result = gzRefundStockService.auditStock(json.getString("id"));
        return toAjax(result);
    }
}

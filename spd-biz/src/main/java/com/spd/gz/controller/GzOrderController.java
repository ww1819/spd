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
import com.spd.gz.domain.GzOrder;
import com.spd.gz.service.IGzOrderService;
import com.spd.common.utils.poi.ExcelUtil;
import com.spd.common.core.page.TableDataInfo;

/**
 * 高值入库Controller
 *
 * @author spd
 * @date 2024-06-11
 */
@RestController
@RequestMapping("/gz/order")
public class GzOrderController extends BaseController
{
    @Autowired
    private IGzOrderService gzOrderService;

    /**
     * 查询高值入库列表
     */
    @PreAuthorize("@ss.hasPermi('gzOrder:apply:list')")
    @GetMapping("/list")
    public TableDataInfo list(GzOrder gzOrder)
    {
        startPage();
        List<GzOrder> list = gzOrderService.selectGzOrderList(gzOrder);
        return getDataTable(list);
    }

    /**
     * 导出高值入库列表
     */
    @PreAuthorize("@ss.hasPermi('gzOrder:apply:export')")
    @Log(title = "高值入库", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, GzOrder gzOrder)
    {
        List<GzOrder> list = gzOrderService.selectGzOrderList(gzOrder);
        ExcelUtil<GzOrder> util = new ExcelUtil<GzOrder>(GzOrder.class);
        util.exportExcel(response, list, "高值入库数据");
    }

    /**
     * 获取高值入库详细信息
     */
    @PreAuthorize("@ss.hasPermi('gzOrder:apply:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(gzOrderService.selectGzOrderById(id));
    }

    /**
     * 新增高值入库
     */
    @PreAuthorize("@ss.hasPermi('gzOrder:apply:add')")
    @Log(title = "高值入库", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody GzOrder gzOrder)
    {
        return toAjax(gzOrderService.insertGzOrder(gzOrder));
    }

    /**
     * 修改高值入库
     */
    @PreAuthorize("@ss.hasPermi('gzOrder:apply:edit')")
    @Log(title = "高值入库", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody GzOrder gzOrder)
    {
        return toAjax(gzOrderService.updateGzOrder(gzOrder));
    }

    /**
     * 删除高值入库
     */
    @PreAuthorize("@ss.hasPermi('gzOrder:apply:remove')")
    @Log(title = "高值入库", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long ids)
    {
        return toAjax(gzOrderService.deleteGzOrderById(ids));
    }

    /**
     * 审核高值入库
     */
    @PreAuthorize("@ss.hasPermi('gzOrder:apply:audit')")
    @Log(title = "高值入库", businessType = BusinessType.UPDATE)
    @PutMapping("/auditOrder")
    public AjaxResult audit(@RequestBody JSONObject json)
    {
        int result = gzOrderService.auditGzOrder(json.getString("id"));
        return toAjax(result);
    }
}

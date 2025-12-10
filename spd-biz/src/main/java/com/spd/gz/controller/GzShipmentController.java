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
import com.spd.gz.domain.GzShipment;
import com.spd.gz.service.IGzShipmentService;
import com.spd.common.utils.poi.ExcelUtil;
import com.spd.common.core.page.TableDataInfo;

/**
 * 高值出库Controller
 *
 * @author spd
 * @date 2024-12-08
 */
@RestController
@RequestMapping("/gz/shipment")
public class GzShipmentController extends BaseController
{
    @Autowired
    private IGzShipmentService gzShipmentService;

    /**
     * 查询高值出库列表
     */
    @PreAuthorize("@ss.hasPermi('gzOrder:apply:list')")
    @GetMapping("/list")
    public TableDataInfo list(GzShipment gzShipment)
    {
        startPage();
        List<GzShipment> list = gzShipmentService.selectGzShipmentList(gzShipment);
        return getDataTable(list);
    }

    /**
     * 导出高值出库列表
     */
    @PreAuthorize("@ss.hasPermi('gzOrder:apply:export')")
    @Log(title = "高值出库", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, GzShipment gzShipment)
    {
        List<GzShipment> list = gzShipmentService.selectGzShipmentList(gzShipment);
        ExcelUtil<GzShipment> util = new ExcelUtil<GzShipment>(GzShipment.class);
        util.exportExcel(response, list, "高值出库数据");
    }

    /**
     * 获取高值出库详细信息
     */
    @PreAuthorize("@ss.hasPermi('gzOrder:apply:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(gzShipmentService.selectGzShipmentById(id));
    }

    /**
     * 新增高值出库
     */
    @PreAuthorize("@ss.hasPermi('gzOrder:apply:add')")
    @Log(title = "高值出库", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody GzShipment gzShipment)
    {
        return toAjax(gzShipmentService.insertGzShipment(gzShipment));
    }

    /**
     * 修改高值出库
     */
    @PreAuthorize("@ss.hasPermi('gzOrder:apply:edit')")
    @Log(title = "高值出库", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody GzShipment gzShipment)
    {
        return toAjax(gzShipmentService.updateGzShipment(gzShipment));
    }

    /**
     * 删除高值出库
     */
    @PreAuthorize("@ss.hasPermi('gzOrder:apply:remove')")
    @Log(title = "高值出库", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long ids)
    {
        return toAjax(gzShipmentService.deleteGzShipmentById(ids));
    }

    /**
     * 审核高值出库
     */
    @PreAuthorize("@ss.hasPermi('gzOrder:apply:audit')")
    @Log(title = "高值出库", businessType = BusinessType.UPDATE)
    @PutMapping("/auditOrder")
    public AjaxResult audit(@RequestBody JSONObject json)
    {
        int result = gzShipmentService.auditGzShipment(json.getString("id"));
        return toAjax(result);
    }
}


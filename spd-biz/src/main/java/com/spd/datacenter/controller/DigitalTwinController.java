package com.spd.datacenter.controller;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.spd.common.core.controller.BaseController;
import com.spd.common.core.domain.AjaxResult;
import com.spd.datacenter.service.IDigitalTwinService;

/**
 * 数字孪生监控大屏
 */
@RestController
@RequestMapping("/datacenter/digitalTwin")
public class DigitalTwinController extends BaseController
{
    @Autowired
    private IDigitalTwinService digitalTwinService;

    /**
     * KPI 总览：库存、出入库、预警、五区占用
     */
    @PreAuthorize("@ss.hasPermi('datacenter:digitalTwin:list')")
    @GetMapping("/overview")
    public AjaxResult overview(@RequestParam(value = "warehouseId", required = false) Long warehouseId)
    {
        return success(digitalTwinService.overview(warehouseId));
    }

    /**
     * 五区 → 货架 → 格口树（含三色状态）
     */
    @PreAuthorize("@ss.hasPermi('datacenter:digitalTwin:list')")
    @GetMapping("/shelves")
    public AjaxResult shelves(@RequestParam(value = "warehouseId", required = false) Long warehouseId)
    {
        return success(digitalTwinService.shelves(warehouseId));
    }

    /**
     * 库存预警 + 效期预警
     */
    @PreAuthorize("@ss.hasPermi('datacenter:digitalTwin:list')")
    @GetMapping("/alerts")
    public AjaxResult alerts(@RequestParam(value = "warehouseId", required = false) Long warehouseId)
    {
        return success(digitalTwinService.alerts(warehouseId));
    }

    /**
     * 今日出入库实时流水
     */
    @PreAuthorize("@ss.hasPermi('datacenter:digitalTwin:list')")
    @GetMapping("/ioRealtime")
    public AjaxResult ioRealtime(@RequestParam(value = "warehouseId", required = false) Long warehouseId,
                                 @RequestParam(value = "limit", required = false) Integer limit)
    {
        return success(digitalTwinService.ioRealtime(warehouseId, limit));
    }

    /**
     * 货位库存明细（点击格口）
     */
    @PreAuthorize("@ss.hasPermi('datacenter:digitalTwin:list')")
    @GetMapping("/locationDetail")
    public AjaxResult locationDetail(@RequestParam("locationId") Long locationId,
                                     @RequestParam(value = "warehouseId", required = false) Long warehouseId)
    {
        List<Map<String, Object>> list = digitalTwinService.locationDetail(locationId, warehouseId);
        return success(list);
    }
}

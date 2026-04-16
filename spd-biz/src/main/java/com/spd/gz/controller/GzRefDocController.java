package com.spd.gz.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.spd.common.core.controller.BaseController;
import com.spd.common.core.domain.AjaxResult;
import com.spd.gz.domain.GzDepotInventory;
import com.spd.gz.domain.GzOrder;
import com.spd.gz.domain.GzShipment;
import com.spd.gz.domain.GzShipmentEntry;
import com.spd.gz.service.IGzRefDocService;

/**
 * 高值单据引用专用查询（低权限，避免引用时因原单菜单权限不足失败）
 */
@RestController
@RequestMapping("/gz/refDoc")
public class GzRefDocController extends BaseController
{
    @Autowired
    private IGzRefDocService gzRefDocService;

    @PreAuthorize("@ss.hasPermi('gz:refDoc:query')")
    @GetMapping("/acceptance/audited")
    public AjaxResult listAuditedAcceptance(GzOrder query)
    {
        List<GzOrder> list = gzRefDocService.listAuditedAcceptance(query);
        return success(list);
    }

    @PreAuthorize("@ss.hasPermi('gz:refDoc:query')")
    @GetMapping("/acceptance/{orderId}/depotLines")
    public AjaxResult acceptanceDepotLines(@PathVariable Long orderId, @RequestParam Long warehouseId)
    {
        return success(gzRefDocService.listAcceptanceDepotLines(orderId, warehouseId));
    }

    @PreAuthorize("@ss.hasPermi('gz:refDoc:query')")
    @GetMapping("/shipment/audited")
    public AjaxResult listAuditedShipment(GzShipment query)
    {
        return success(gzRefDocService.listAuditedShipment(query));
    }

    @PreAuthorize("@ss.hasPermi('gz:refDoc:query')")
    @GetMapping("/shipment/{shipmentId}/linesForTk")
    public AjaxResult shipmentLinesForTk(@PathVariable Long shipmentId, @RequestParam Long departmentId)
    {
        List<GzShipmentEntry> list = gzRefDocService.listShipmentLinesForTk(shipmentId, departmentId);
        return success(list);
    }
}

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.spd.common.annotation.Log;
import com.spd.common.core.controller.BaseController;
import com.spd.common.core.domain.AjaxResult;
import com.spd.common.enums.BusinessType;
import com.spd.common.utils.StringUtils;
import com.spd.gz.domain.GzDepotInventory;
import com.spd.gz.domain.GzShipment;
import com.spd.gz.service.IGzDepotInventoryService;
import com.spd.gz.service.IGzOrderService;
import com.spd.gz.service.IGzShipmentService;
import com.spd.common.utils.poi.ExcelUtil;
import com.spd.common.core.page.TableDataInfo;

/**
 * 高值备货出库 Controller
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

    @Autowired
    private IGzDepotInventoryService gzDepotInventoryService;

    @Autowired
    private IGzOrderService gzOrderService;

    /**
     * 查询高值备货出库列表
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
     * 导出高值备货出库列表
     */
    @PreAuthorize("@ss.hasPermi('gzOrder:apply:export')")
    @Log(title = "高值备货出库", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, GzShipment gzShipment)
    {
        List<GzShipment> list = gzShipmentService.selectGzShipmentList(gzShipment);
        ExcelUtil<GzShipment> util = new ExcelUtil<GzShipment>(GzShipment.class);
        util.exportExcel(response, list, "高值备货出库数据");
    }

    /**
     * 获取高值备货出库详细信息
     */
    @PreAuthorize("@ss.hasPermi('gzOrder:apply:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(gzShipmentService.selectGzShipmentById(id));
    }

    /**
     * 新增高值备货出库
     */
    @PreAuthorize("@ss.hasPermi('gzOrder:apply:add')")
    @Log(title = "高值备货出库", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody GzShipment gzShipment)
    {
        int rows = gzShipmentService.insertGzShipment(gzShipment);
        if (rows > 0) {
            Integer filteredCount = gzShipment.getDedupFilteredCount();
            String msg = (filteredCount != null && filteredCount > 0)
                ? String.format("新增成功，后台已自动过滤 %d 条重复明细", filteredCount)
                : "新增成功";
            return AjaxResult.success(msg, gzShipment);
        }
        return AjaxResult.error("新增失败");
    }

    /**
     * 修改高值备货出库
     */
    @PreAuthorize("@ss.hasPermi('gzOrder:apply:edit')")
    @Log(title = "高值备货出库", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody GzShipment gzShipment)
    {
        int rows = gzShipmentService.updateGzShipment(gzShipment);
        if (rows > 0) {
            Integer filteredCount = gzShipment.getDedupFilteredCount();
            String msg = (filteredCount != null && filteredCount > 0)
                ? String.format("修改成功，后台已自动过滤 %d 条重复明细", filteredCount)
                : "修改成功";
            return AjaxResult.success(msg, gzShipment);
        }
        return AjaxResult.error("修改失败");
    }

    /**
     * 删除高值备货出库
     */
    @PreAuthorize("@ss.hasPermi('gzOrder:apply:remove')")
    @Log(title = "高值备货出库", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long ids)
    {
        return toAjax(gzShipmentService.deleteGzShipmentById(ids));
    }

    /**
     * 审核高值备货出库
     */
    @PreAuthorize("@ss.hasPermi('gzOrder:apply:audit')")
    @Log(title = "高值备货出库", businessType = BusinessType.UPDATE)
    @PutMapping("/auditOrder")
    public AjaxResult audit(@RequestBody JSONObject json)
    {
        int result = gzShipmentService.auditGzShipment(json.getString("id"));
        return toAjax(result);
    }

    /**
     * 备货出库扫码：按院内码 + 仓库查询备货库存一条
     */
    @PreAuthorize("@ss.hasPermi('gzOrder:apply:query')")
    @GetMapping("/depotInventory/byInHospitalCode")
    public AjaxResult getDepotByInHospitalCode(@RequestParam("inHospitalCode") String inHospitalCode,
            @RequestParam("warehouseId") Long warehouseId)
    {
        if (StringUtils.isEmpty(inHospitalCode))
        {
            return error("院内码不能为空");
        }
        if (warehouseId == null)
        {
            return error("仓库不能为空");
        }
        GzDepotInventory inv = gzDepotInventoryService.selectByInHospitalCodeAndWarehouse(inHospitalCode.trim(), warehouseId);
        return success(inv);
    }

    /**
     * 根据院内码查询是否有未出库的出库单
     */
    @PostMapping("/checkInHospitalCode")
    public AjaxResult checkInHospitalCode(@RequestBody JSONObject json)
    {
        String inHospitalCode = json.getString("inHospitalCode");
        List<String> orderNos = gzOrderService.selectOutboundOrderNosByInHospitalCode(inHospitalCode);
        return success(orderNos);
    }
}


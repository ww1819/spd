package com.spd.monitoring.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;

import com.spd.common.annotation.Log;
import com.spd.common.core.controller.BaseController;
import com.spd.common.core.domain.AjaxResult;
import com.spd.common.core.page.TableDataInfo;
import com.spd.common.enums.BusinessType;
import com.spd.common.utils.poi.ExcelUtil;
import com.spd.monitoring.domain.DeptFixedNumber;
import com.spd.monitoring.domain.WhFixedNumber;
import com.spd.monitoring.domain.FixedNumberSaveRequest;
import com.spd.monitoring.service.IFixedNumberService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 定数监测Controller
 *
 * @author spd
 * @date 2024-12-13
 */
@RestController
@RequestMapping("/monitoring/fixedNumber")
public class FixedNumberController extends BaseController
{
    private final IFixedNumberService fixedNumberService;

    public FixedNumberController(IFixedNumberService fixedNumberService) {
        this.fixedNumberService = fixedNumberService;
    }

    /**
     * 查询定数监测列表
     */
    @PreAuthorize("@ss.hasPermi('monitoring:fixedNumber:list')")
    @GetMapping("/list")
    public TableDataInfo list(WhFixedNumber whQuery, DeptFixedNumber deptQuery, String fixedNumberType)
    {
        startPage();
        List<Map<String, Object>> result = new ArrayList<>();

        // 默认为仓库定数监测
        if (fixedNumberType == null || "".equals(fixedNumberType) || "1".equals(fixedNumberType)) {
            List<WhFixedNumber> list = fixedNumberService.selectWhFixedNumberList(whQuery);
            for (WhFixedNumber item : list) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", item.getId());
                map.put("materialId", item.getMaterialId());
                map.put("warehouseId", item.getWarehouseId());
                map.put("upperLimit", item.getUpperLimit());
                map.put("lowerLimit", item.getLowerLimit());
                map.put("expiryReminder", item.getExpiryReminder());
                map.put("monitoring", item.getMonitoring());
                map.put("location", item.getLocation());
                map.put("locationId", item.getLocationId());
                if (item.getMaterial() != null) {
                    map.put("code", item.getMaterial().getCode());
                    map.put("name", item.getMaterial().getName());
                    map.put("specification", item.getMaterial().getSpeci());
                    map.put("model", item.getMaterial().getModel());
                    map.put("registerNo", item.getMaterial().getRegisterNo());
                }
                if (item.getWarehouse() != null) {
                    map.put("warehouseName", item.getWarehouse().getName());
                }
                result.add(map);
            }
        } else if ("2".equals(fixedNumberType)) {
            List<DeptFixedNumber> list = fixedNumberService.selectDeptFixedNumberList(deptQuery);
            for (DeptFixedNumber item : list) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", item.getId());
                map.put("materialId", item.getMaterialId());
                map.put("departmentId", item.getDepartmentId());
                map.put("upperLimit", item.getUpperLimit());
                map.put("lowerLimit", item.getLowerLimit());
                map.put("expiryReminder", item.getExpiryReminder());
                map.put("monitoring", item.getMonitoring());
                map.put("location", item.getLocation());
                map.put("locationId", item.getLocationId());
                if (item.getMaterial() != null) {
                    map.put("code", item.getMaterial().getCode());
                    map.put("name", item.getMaterial().getName());
                    map.put("specification", item.getMaterial().getSpeci());
                    map.put("model", item.getMaterial().getModel());
                    map.put("registerNo", item.getMaterial().getRegisterNo());
                }
                if (item.getDepartment() != null) {
                    map.put("departmentName", item.getDepartment().getName());
                }
                result.add(map);
            }
        }

        return getDataTable(result);
    }

    /**
     * 新增定数监测
     */
    @PreAuthorize("@ss.hasPermi('monitoring:fixedNumber:add')")
    @Log(title = "定数监测", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody FixedNumberSaveRequest fixedNumber)
    {
        fixedNumberService.saveFixedNumber(fixedNumber, getUsername());
        return AjaxResult.success("保存成功");
    }

    /**
     * 导出定数监测列表
     */
    @PreAuthorize("@ss.hasPermi('monitoring:fixedNumber:export')")
    @Log(title = "定数监测", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response)
    {
        // TODO: 实现实际的导出逻辑
        List<Object> list = new ArrayList<>();
        ExcelUtil<Object> util = new ExcelUtil<Object>(Object.class);
        util.exportExcel(response, list, "定数监测数据");
    }
}


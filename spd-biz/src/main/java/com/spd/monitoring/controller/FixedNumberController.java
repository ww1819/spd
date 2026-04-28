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
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.spd.common.utils.poi.ExcelUtil;
import com.spd.foundation.domain.FdMaterial;
import com.spd.foundation.domain.FdMaterialListRequest;
import com.spd.foundation.service.IFdMaterialService;
import com.spd.monitoring.domain.DeptFixedNumber;
import com.spd.monitoring.domain.WhFixedNumber;
import com.spd.monitoring.domain.FixedNumberSaveRequest;
import com.spd.monitoring.service.IFixedNumberService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
    private final IFdMaterialService fdMaterialService;

    public FixedNumberController(IFixedNumberService fixedNumberService, IFdMaterialService fdMaterialService) {
        this.fixedNumberService = fixedNumberService;
        this.fdMaterialService = fdMaterialService;
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
        long total = 0L;

        // 默认为仓库定数监测
        if (fixedNumberType == null || "".equals(fixedNumberType) || "1".equals(fixedNumberType)) {
            // 未选择仓库时，不返回任何明细，避免一进页面就查全院定数
            if (whQuery == null || whQuery.getWarehouseId() == null) {
                return getDataTable(result);
            }
            List<WhFixedNumber> list = fixedNumberService.selectWhFixedNumberList(whQuery);
            total = new PageInfo<>(list).getTotal();
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
                map.put("tenantId", item.getTenantId());
                map.put("remark", item.getRemark());
                if (item.getMaterial() != null) {
                    map.put("code", item.getMaterial().getCode());
                    map.put("name", item.getMaterial().getName());
                    map.put("specification", item.getMaterial().getSpeci());
                    map.put("model", item.getMaterial().getModel());
                    map.put("registerNo", item.getMaterial().getRegisterNo());
                    map.put("isProcure", item.getMaterial().getIsProcure());
                    if (item.getMaterial().getSupplierId() != null) {
                        map.put("supplierId", item.getMaterial().getSupplierId());
                    }
                }
                if (item.getWarehouse() != null) {
                    map.put("warehouseName", item.getWarehouse().getName());
                }
                // 产品档案展示字段（来自关联查询）
                if (item.getUnitName() != null) {
                    map.put("unitName", item.getUnitName());
                }
                if (item.getPrice() != null) {
                    map.put("price", item.getPrice());
                }
                if (item.getSupplierName() != null) {
                    map.put("supplierName", item.getSupplierName());
                }
                if (item.getFactoryName() != null) {
                    map.put("factoryName", item.getFactoryName());
                }
                if (item.getWarehouseCategoryName() != null) {
                    map.put("warehouseCategoryName", item.getWarehouseCategoryName());
                }
                if (item.getFinanceCategoryName() != null) {
                    map.put("financeCategoryName", item.getFinanceCategoryName());
                }
                if (item.getRegisterNo() != null) {
                    map.put("registerNo", item.getRegisterNo());
                }
                result.add(map);
            }
        } else if ("2".equals(fixedNumberType)) {
            // 未选择科室时，不返回任何明细
            if (deptQuery == null || deptQuery.getDepartmentId() == null) {
                return getDataTable(result);
            }
            List<DeptFixedNumber> list = fixedNumberService.selectDeptFixedNumberList(deptQuery);
            total = new PageInfo<>(list).getTotal();
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
                map.put("tenantId", item.getTenantId());
                map.put("remark", item.getRemark());
                if (item.getMaterial() != null) {
                    map.put("code", item.getMaterial().getCode());
                    map.put("name", item.getMaterial().getName());
                    map.put("specification", item.getMaterial().getSpeci());
                    map.put("model", item.getMaterial().getModel());
                    map.put("registerNo", item.getMaterial().getRegisterNo());
                    map.put("isProcure", item.getMaterial().getIsProcure());
                }
                if (item.getDepartment() != null) {
                    map.put("departmentName", item.getDepartment().getName());
                }
                // 产品档案展示字段（来自关联查询）
                if (item.getUnitName() != null) {
                    map.put("unitName", item.getUnitName());
                }
                if (item.getPrice() != null) {
                    map.put("price", item.getPrice());
                }
                if (item.getSupplierName() != null) {
                    map.put("supplierName", item.getSupplierName());
                }
                if (item.getFactoryName() != null) {
                    map.put("factoryName", item.getFactoryName());
                }
                if (item.getWarehouseCategoryName() != null) {
                    map.put("warehouseCategoryName", item.getWarehouseCategoryName());
                }
                if (item.getFinanceCategoryName() != null) {
                    map.put("financeCategoryName", item.getFinanceCategoryName());
                }
                if (item.getRegisterNo() != null) {
                    map.put("registerNo", item.getRegisterNo());
                }
                result.add(map);
            }
        }

        TableDataInfo data = getDataTable(result);
        data.setTotal(total);
        return data;
    }

    /**
     * 科室申购新增明细专用：查询指定仓库的定数检测数据（不依赖 monitoring:fixedNumber:list 权限）
     */
    @PreAuthorize("@ss.hasPermi('department:purchase:add')")
    @GetMapping("/listForPurchase")
    public TableDataInfo listForPurchase(WhFixedNumber whQuery)
    {
        if (whQuery == null || whQuery.getWarehouseId() == null) {
            return getDataTable(new ArrayList<>());
        }
        whQuery.setIsGz("2");
        startPage();
        List<WhFixedNumber> list = fixedNumberService.selectWhFixedNumberList(whQuery);
        long total = new PageInfo<>(list).getTotal();
        List<Map<String, Object>> result = new ArrayList<>();
        for (WhFixedNumber item : list) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", item.getMaterialId());
            map.put("materialId", item.getMaterialId());
            map.put("code", item.getMaterial() != null ? item.getMaterial().getCode() : null);
            map.put("name", item.getMaterial() != null ? item.getMaterial().getName() : null);
            map.put("speci", item.getMaterial() != null ? item.getMaterial().getSpeci() : null);
            map.put("specification", item.getMaterial() != null ? item.getMaterial().getSpeci() : null);
            map.put("model", item.getMaterial() != null ? item.getMaterial().getModel() : null);
            map.put("registerNo", item.getRegisterNo());
            map.put("unitName", item.getUnitName());
            map.put("price", item.getPrice());
            map.put("supplierName", item.getSupplierName());
            map.put("factoryName", item.getFactoryName());
            map.put("warehouseCategoryName", item.getWarehouseCategoryName());
            map.put("financeCategoryName", item.getFinanceCategoryName());
            if (item.getUnitName() != null) {
                map.put("fdUnit", java.util.Collections.singletonMap("unitName", item.getUnitName()));
            }
            if (item.getSupplierName() != null) {
                map.put("supplier", java.util.Collections.singletonMap("name", item.getSupplierName()));
            }
            if (item.getFactoryName() != null) {
                map.put("fdFactory", java.util.Collections.singletonMap("factoryName", item.getFactoryName()));
            }
            result.add(map);
        }
        TableDataInfo data = getDataTable(result);
        data.setTotal(total);
        return data;
    }

    /**
     * 定数监测新增/维护明细：分页查询可选产品档案（POST body，避免 excludeMaterialIds 过长）。
     * 与 {@code POST /foundation/material/list} 的区别：不按「本仓库已有未删除定数行」做 EXISTS，
     * 避免与 excludeMaterialIds 叠加结果为空、或删除定数后无法再选该产品。
     */
    @PreAuthorize("@ss.hasPermi('monitoring:fixedNumber:list')")
    @PostMapping("/materialDetailPick")
    public TableDataInfo materialDetailPick(@RequestBody(required = false) FdMaterialListRequest request)
    {
        Integer pageNum = request != null && request.getPageNum() != null ? request.getPageNum() : 1;
        Integer pageSize = request != null && request.getPageSize() != null ? request.getPageSize() : 10;
        FdMaterial query = request != null && request.getQuery() != null ? request.getQuery() : new FdMaterial();
        query.setSkipWarehouseFixedNumberExists(Boolean.TRUE);
        PageHelper.startPage(pageNum, pageSize);
        List<FdMaterial> list = fdMaterialService.selectFdMaterialList(query);
        return getDataTable(list);
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
     * 删除单条定数监测
     */
    @PreAuthorize("@ss.hasPermi('monitoring:fixedNumber:remove')")
    @Log(title = "定数监测", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public AjaxResult remove(@PathVariable("id") String id)
    {
        int rows = fixedNumberService.deleteFixedNumberById(id);
        return toAjax(rows);
    }

    /**
     * 批量删除定数监测
     */
    @PreAuthorize("@ss.hasPermi('monitoring:fixedNumber:remove')")
    @Log(title = "定数监测", businessType = BusinessType.DELETE)
    @PostMapping("/batchDelete")
    public AjaxResult batchRemove(@RequestBody Map<String, List<String>> body)
    {
        List<String> ids = body == null ? null : body.get("ids");
        if (ids == null || ids.isEmpty())
        {
            return error("ID 列表不能为空");
        }
        int rows = fixedNumberService.deleteFixedNumberByIds(ids);
        return toAjax(rows);
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


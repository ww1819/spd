package com.spd.warehouse.controller;

import com.spd.common.core.controller.BaseController;
import com.spd.common.core.domain.AjaxResult;
import com.spd.warehouse.domain.DepartmentInventoryQuery;
import com.spd.warehouse.service.IDepartmentInventoryService;
import com.spd.warehouse.vo.DepartmentInventoryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 科室库存查询Controller
 *
 * @author spd
 * @date 2024-11-03
 */
@RestController
@RequestMapping("/warehouse/departmentInventory")
public class DepartmentInventoryController extends BaseController {

    @Autowired
    private IDepartmentInventoryService departmentInventoryService;

    /**
     * 查询科室库存明细列表（不分页）
     */
    @PreAuthorize("@ss.hasPermi('warehouse:departmentInventory:list')")
    @GetMapping("/list")
    public AjaxResult list(DepartmentInventoryQuery query) {
        List<DepartmentInventoryVo> list = departmentInventoryService.selectDepartmentInventoryList(query);
        return success(list);
    }
}
package com.spd.foundation.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.spd.common.core.domain.entity.SysUser;
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
import com.spd.foundation.domain.FdWarehouse;
import com.spd.foundation.service.IFdWarehouseService;
import com.spd.common.utils.poi.ExcelUtil;
import com.spd.common.core.page.TableDataInfo;

/**
 * 仓库Controller
 *
 * @author spd
 * @date 2023-11-26
 */
@RestController
@RequestMapping("/foundation/warehouse")
public class FdWarehouseController extends BaseController
{
    @Autowired
    private IFdWarehouseService fdWarehouseService;

    /**
     * 查询仓库列表
     */
    @PreAuthorize("@ss.hasPermi('foundation:warehouse:list')")
    @GetMapping("/list")
    public TableDataInfo list(FdWarehouse fdWarehouse)
    {
        startPage();
        List<FdWarehouse> list = fdWarehouseService.selectFdWarehouseList(fdWarehouse);
        return getDataTable(list);
    }

    /**
     * 根据用户查询所有仓库列表
     */
    @GetMapping("/listAll/{userId}")
    public List<FdWarehouse> listAll(@PathVariable(value = "userId") Long userId)
    {
        List<FdWarehouse> fdWarehouseList = null;
        if(SysUser.isAdmin(userId)){
            fdWarehouseList = fdWarehouseService.selectwarehouseAll();
        }else {
            fdWarehouseList = fdWarehouseService.selectUserWarehouseAll(userId);
        }
        return fdWarehouseList;
    }

    /**
     * 导出仓库列表
     */
    @PreAuthorize("@ss.hasPermi('foundation:warehouse:export')")
    @Log(title = "仓库", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, FdWarehouse fdWarehouse)
    {
        List<FdWarehouse> list = fdWarehouseService.selectFdWarehouseList(fdWarehouse);
        ExcelUtil<FdWarehouse> util = new ExcelUtil<FdWarehouse>(FdWarehouse.class);
        util.exportExcel(response, list, "仓库数据");
    }

    /**
     * 获取仓库详细信息
     */
    @PreAuthorize("@ss.hasPermi('foundation:warehouse:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id)
    {
        return success(fdWarehouseService.selectFdWarehouseById(id));
    }

    /**
     * 新增仓库
     */
    @PreAuthorize("@ss.hasPermi('foundation:warehouse:add')")
    @Log(title = "仓库", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody FdWarehouse fdWarehouse)
    {
        return toAjax(fdWarehouseService.insertFdWarehouse(fdWarehouse));
    }

    /**
     * 修改仓库
     */
    @PreAuthorize("@ss.hasPermi('foundation:warehouse:edit')")
    @Log(title = "仓库", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody FdWarehouse fdWarehouse)
    {
        return toAjax(fdWarehouseService.updateFdWarehouse(fdWarehouse));
    }

    /**
     * 删除仓库
     */
    @PreAuthorize("@ss.hasPermi('foundation:warehouse:remove')")
    @Log(title = "仓库", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable String ids)
    {
        return toAjax(fdWarehouseService.deleteFdWarehouseById(ids));
    }

    /**
     * 获取仓库列表
     */
    @GetMapping("/optionselect")
    public AjaxResult optionselect()
    {
        List<FdWarehouse> fdWarehouseList = fdWarehouseService.selectwarehouseAll();
        return success(fdWarehouseList);
    }
}

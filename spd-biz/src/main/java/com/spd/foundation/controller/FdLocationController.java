package com.spd.foundation.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
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
import com.spd.foundation.domain.FdLocation;
import com.spd.foundation.service.IFdLocationService;
import com.spd.common.utils.poi.ExcelUtil;
import com.spd.common.core.page.TableDataInfo;

/**
 * 货位Controller
 *
 * @author spd
 * @date 2024-12-13
 */
@RestController
@RequestMapping("/foundation/location")
public class FdLocationController extends BaseController
{
    @Autowired
    private IFdLocationService fdLocationService;

    /**
     * 查询货位列表
     */
    @PreAuthorize("@ss.hasPermi('foundation:location:list')")
    @GetMapping("/list")
    public TableDataInfo list(FdLocation fdLocation)
    {
        startPage();
        List<FdLocation> list = fdLocationService.selectFdLocationList(fdLocation);
        return getDataTable(list);
    }

    /**
     * 查询所有货位列表
     */
    @GetMapping("/listAll")
    public List<FdLocation> listAll(FdLocation fdLocation)
    {
        List<FdLocation> list = fdLocationService.selectFdLocationList(fdLocation);
        return list;
    }

    /**
     * 查询货位树形列表
     */
    @GetMapping("/treeselect")
    public AjaxResult treeselect()
    {
        List<FdLocation> list = fdLocationService.selectFdLocationTree();
        return success(list);
    }

    /**
     * 导出货位列表
     */
    @PreAuthorize("@ss.hasPermi('foundation:location:export')")
    @Log(title = "货位", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, FdLocation fdLocation)
    {
        List<FdLocation> list = fdLocationService.selectFdLocationList(fdLocation);
        ExcelUtil<FdLocation> util = new ExcelUtil<FdLocation>(FdLocation.class);
        util.exportExcel(response, list, "货位数据");
    }

    /**
     * 获取货位详细信息
     */
    @PreAuthorize("@ss.hasPermi('foundation:location:query')")
    @GetMapping(value = "/{locationId}")
    public AjaxResult getInfo(@PathVariable("locationId") Long locationId)
    {
        return success(fdLocationService.selectFdLocationByLocationId(locationId));
    }

    /**
     * 新增货位
     */
    @PreAuthorize("@ss.hasPermi('foundation:location:add')")
    @Log(title = "货位", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody FdLocation fdLocation)
    {
        return toAjax(fdLocationService.insertFdLocation(fdLocation));
    }

    /**
     * 修改货位
     */
    @PreAuthorize("@ss.hasPermi('foundation:location:edit')")
    @Log(title = "货位", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody FdLocation fdLocation)
    {
        return toAjax(fdLocationService.updateFdLocation(fdLocation));
    }

    /**
     * 删除货位
     */
    @PreAuthorize("@ss.hasPermi('foundation:location:remove')")
    @Log(title = "货位", businessType = BusinessType.DELETE)
    @DeleteMapping("/{locationIds}")
    public AjaxResult remove(@PathVariable Long locationIds)
    {
        return toAjax(fdLocationService.deleteFdLocationByLocationIds(locationIds));
    }
}


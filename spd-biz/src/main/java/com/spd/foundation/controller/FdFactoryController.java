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
import com.spd.foundation.domain.FdFactory;
import com.spd.foundation.service.IFdFactoryService;
import com.spd.common.utils.poi.ExcelUtil;
import com.spd.common.core.page.TableDataInfo;

/**
 * 厂家维护Controller
 *
 * @author spd
 * @date 2024-03-04
 */
@RestController
@RequestMapping("/foundation/factory")
public class FdFactoryController extends BaseController
{
    @Autowired
    private IFdFactoryService fdFactoryService;

    /**
     * 查询厂家维护列表
     */
    @PreAuthorize("@ss.hasPermi('foundation:factory:list')")
    @GetMapping("/list")
    public TableDataInfo list(FdFactory fdFactory)
    {
        startPage();
        List<FdFactory> list = fdFactoryService.selectFdFactoryList(fdFactory);
        return getDataTable(list);
    }

    /**
     * 查询所有厂家维护列表
     */
    @GetMapping("/listAll")
    public List<FdFactory> listAll(FdFactory fdFactory)
    {
        List<FdFactory> list = fdFactoryService.selectFdFactoryList(fdFactory);
        return list;
    }

    /**
     * 导出厂家维护列表
     */
    @PreAuthorize("@ss.hasPermi('foundation:factory:export')")
    @Log(title = "厂家维护", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, FdFactory fdFactory)
    {
        List<FdFactory> list = fdFactoryService.selectFdFactoryList(fdFactory);
        ExcelUtil<FdFactory> util = new ExcelUtil<FdFactory>(FdFactory.class);
        util.exportExcel(response, list, "厂家维护数据");
    }

    /**
     * 获取厂家维护详细信息
     */
    @PreAuthorize("@ss.hasPermi('foundation:factory:query')")
    @GetMapping(value = "/{factoryId}")
    public AjaxResult getInfo(@PathVariable("factoryId") Long factoryId)
    {
        return success(fdFactoryService.selectFdFactoryByFactoryId(factoryId));
    }

    /**
     * 新增厂家维护
     */
    @PreAuthorize("@ss.hasPermi('foundation:factory:add')")
    @Log(title = "厂家维护", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody FdFactory fdFactory)
    {
        return toAjax(fdFactoryService.insertFdFactory(fdFactory));
    }

    /**
     * 修改厂家维护
     */
    @PreAuthorize("@ss.hasPermi('foundation:factory:edit')")
    @Log(title = "厂家维护", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody FdFactory fdFactory)
    {
        return toAjax(fdFactoryService.updateFdFactory(fdFactory));
    }

    /**
     * 删除厂家维护
     */
    @PreAuthorize("@ss.hasPermi('foundation:factory:remove')")
    @Log(title = "厂家维护", businessType = BusinessType.DELETE)
	@DeleteMapping("/{factoryIds}")
    public AjaxResult remove(@PathVariable Long factoryIds)
    {
        return toAjax(fdFactoryService.deleteFdFactoryByFactoryId(factoryIds));
    }
}

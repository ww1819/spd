package com.spd.foundation.controller;

import java.util.List;
import java.util.Map;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.spd.common.annotation.Log;
import com.spd.common.core.controller.BaseController;
import com.spd.common.core.domain.AjaxResult;
import com.spd.common.enums.BusinessType;
import com.spd.common.enums.TenantEnum;
import com.spd.common.utils.SecurityUtils;
import com.spd.foundation.domain.FdFactory;
import com.spd.foundation.domain.FdFactoryChangeLog;
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

    @PreAuthorize("@ss.hasPermi('foundation:factory:list')")
    @GetMapping("/list")
    public TableDataInfo list(FdFactory fdFactory)
    {
        startPage();
        List<FdFactory> list = fdFactoryService.selectFdFactoryList(fdFactory);
        return getDataTable(list);
    }

    @GetMapping("/listAll")
    public List<FdFactory> listAll(FdFactory fdFactory)
    {
        List<FdFactory> list = fdFactoryService.selectFdFactoryList(fdFactory);
        return list;
    }

    @PreAuthorize("@ss.hasPermi('foundation:factory:export')")
    @Log(title = "厂家维护", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, FdFactory fdFactory)
    {
        List<FdFactory> list = fdFactoryService.selectFdFactoryList(fdFactory);
        ExcelUtil<FdFactory> util = new ExcelUtil<FdFactory>(FdFactory.class);
        util.exportExcel(response, list, "厂家维护数据");
    }

    @PreAuthorize("@ss.hasPermi('foundation:factory:list')")
    @GetMapping("/changeLog/{factoryId}")
    public AjaxResult factoryChangeLog(@PathVariable("factoryId") Long factoryId)
    {
        if (factoryId == null)
        {
            return error("生产厂家 id 无效");
        }
        FdFactory f = fdFactoryService.selectFdFactoryByFactoryId(factoryId);
        if (f == null)
        {
            return error("生产厂家不存在");
        }
        List<FdFactoryChangeLog> logs = fdFactoryService.selectFactoryChangeLog(factoryId);
        return success(logs);
    }

    @PreAuthorize("@ss.hasPermi('foundation:factory:import')")
    @PostMapping("/importValidate")
    public AjaxResult importValidate(MultipartFile file, boolean updateSupport) throws Exception
    {
        ExcelUtil<FdFactory> util = new ExcelUtil<FdFactory>(FdFactory.class);
        List<FdFactory> list = util.importExcel(file.getInputStream());
        Map<String, Object> data = fdFactoryService.validateFdFactoryImport(list, updateSupport);
        if (!Boolean.TRUE.equals(data.get("valid")))
        {
            return AjaxResult.success("校验未通过", data);
        }
        return AjaxResult.success("校验通过，请确认后导入", data);
    }

    @Log(title = "生产厂家导入", businessType = BusinessType.IMPORT)
    @PreAuthorize("@ss.hasPermi('foundation:factory:import')")
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file, boolean updateSupport,
        @RequestParam(value = "confirm", defaultValue = "false") boolean confirm) throws Exception
    {
        ExcelUtil<FdFactory> util = new ExcelUtil<FdFactory>(FdFactory.class);
        List<FdFactory> list = util.importExcel(file.getInputStream());
        String operName = getUsername();
        String message = fdFactoryService.importFdFactory(list, updateSupport, operName, confirm);
        return success(message);
    }

    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) throws Exception
    {
        ExcelUtil<FdFactory> util = new ExcelUtil<FdFactory>(FdFactory.class);
        util.importTemplateExcel(response, "生产厂家数据");
    }

    @PreAuthorize("@ss.hasPermi('foundation:factory:query')")
    @GetMapping(value = "/{factoryId}")
    public AjaxResult getInfo(@PathVariable("factoryId") Long factoryId)
    {
        return success(fdFactoryService.selectFdFactoryByFactoryId(factoryId));
    }

    @PreAuthorize("@ss.hasPermi('foundation:factory:add')")
    @Log(title = "厂家维护", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody FdFactory fdFactory)
    {
        if (TenantEnum.HS_003 != TenantEnum.fromCustomerId(SecurityUtils.getCustomerId()))
        {
            fdFactory.setHisId(null);
        }
        return toAjax(fdFactoryService.insertFdFactory(fdFactory));
    }

    @PreAuthorize("@ss.hasPermi('foundation:factory:edit')")
    @Log(title = "厂家维护", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody FdFactory fdFactory)
    {
        return toAjax(fdFactoryService.updateFdFactory(fdFactory));
    }

    @PreAuthorize("@ss.hasPermi('foundation:factory:remove')")
    @Log(title = "厂家维护", businessType = BusinessType.DELETE)
	@DeleteMapping("/{factoryIds}")
    public AjaxResult remove(@PathVariable Long factoryIds)
    {
        return toAjax(fdFactoryService.deleteFdFactoryByFactoryId(factoryIds));
    }

    @PreAuthorize("@ss.hasPermi('foundation:factory:updateReferred')")
    @Log(title = "厂家维护", businessType = BusinessType.UPDATE)
    @PostMapping("/updateReferred")
    public AjaxResult updateReferred(@RequestBody java.util.Map<String, java.util.List<Long>> body)
    {
        java.util.List<Long> ids = body.get("ids");
        if (ids == null || ids.isEmpty())
        {
            return error("ID 列表不能为空");
        }
        fdFactoryService.updateReferred(ids);
        return success("更新简码成功");
    }
}

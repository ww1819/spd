package com.spd.gz.controller;

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
import com.spd.gz.domain.GzTraceability;
import com.spd.gz.service.IGzTraceabilityService;
import com.spd.common.utils.poi.ExcelUtil;
import com.spd.common.core.page.TableDataInfo;

/**
 * 高值追溯单Controller
 *
 * @author spd
 * @date 2025-01-01
 */
@RestController
@RequestMapping("/gz/traceability")
public class GzTraceabilityController extends BaseController
{
    @Autowired
    private IGzTraceabilityService gzTraceabilityService;

    /**
     * 查询高值追溯单列表
     */
    @PreAuthorize("@ss.hasPermi('gz:traceability:list')")
    @GetMapping("/list")
    public TableDataInfo list(GzTraceability gzTraceability)
    {
        startPage();
        List<GzTraceability> list = gzTraceabilityService.selectGzTraceabilityList(gzTraceability);
        return getDataTable(list);
    }

    /**
     * 导出高值追溯单列表
     */
    @PreAuthorize("@ss.hasPermi('gz:traceability:export')")
    @Log(title = "高值追溯单", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, GzTraceability gzTraceability)
    {
        List<GzTraceability> list = gzTraceabilityService.selectGzTraceabilityList(gzTraceability);
        ExcelUtil<GzTraceability> util = new ExcelUtil<GzTraceability>(GzTraceability.class);
        util.exportExcel(response, list, "高值追溯单数据");
    }

    /**
     * 获取高值追溯单详细信息
     */
    @PreAuthorize("@ss.hasPermi('gz:traceability:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(gzTraceabilityService.selectGzTraceabilityById(id));
    }

    /**
     * 新增高值追溯单
     */
    @PreAuthorize("@ss.hasPermi('gz:traceability:add')")
    @Log(title = "高值追溯单", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody GzTraceability gzTraceability)
    {
        return toAjax(gzTraceabilityService.insertGzTraceability(gzTraceability));
    }

    /**
     * 修改高值追溯单
     */
    @PreAuthorize("@ss.hasPermi('gz:traceability:edit')")
    @Log(title = "高值追溯单", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody GzTraceability gzTraceability)
    {
        return toAjax(gzTraceabilityService.updateGzTraceability(gzTraceability));
    }

    /**
     * 删除高值追溯单
     */
    @PreAuthorize("@ss.hasPermi('gz:traceability:remove')")
    @Log(title = "高值追溯单", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(gzTraceabilityService.deleteGzTraceabilityByIds(ids));
    }

    /**
     * 审核高值追溯单
     */
    @PreAuthorize("@ss.hasPermi('gz:traceability:audit')")
    @Log(title = "高值追溯单", businessType = BusinessType.UPDATE)
    @PutMapping("/audit/{id}")
    public AjaxResult audit(@PathVariable("id") Long id)
    {
        return toAjax(gzTraceabilityService.auditGzTraceability(id));
    }

    /**
     * 反审核高值追溯单
     */
    @PreAuthorize("@ss.hasPermi('gz:traceability:audit')")
    @Log(title = "高值追溯单", businessType = BusinessType.UPDATE)
    @PutMapping("/unaudit/{id}")
    public AjaxResult unaudit(@PathVariable("id") Long id)
    {
        return toAjax(gzTraceabilityService.unauditGzTraceability(id));
    }

    /**
     * 查询追溯单明细列表（用于使用追溯明细表）
     */
    @PreAuthorize("@ss.hasPermi('gz:traceability:list')")
    @GetMapping("/entry/list")
    public TableDataInfo entryList(GzTraceability gzTraceability)
    {
        startPage();
        List<com.spd.gz.domain.GzTraceabilityEntry> list = gzTraceabilityService.selectTraceabilityEntryList(gzTraceability);
        return getDataTable(list);
    }
}

package com.spd.foundation.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.spd.common.annotation.Log;
import com.spd.common.core.controller.BaseController;
import com.spd.common.core.domain.AjaxResult;
import com.spd.common.core.page.TableDataInfo;
import com.spd.common.enums.BusinessType;
import com.spd.common.utils.poi.ExcelUtil;
import com.spd.foundation.domain.FdFocus18;
import com.spd.foundation.service.IFdFocus18Service;

/**
 * 18类重点耗材维护
 */
@RestController
@RequestMapping("/foundation/focus18")
public class FdFocus18Controller extends BaseController
{
    @Autowired
    private IFdFocus18Service fdFocus18Service;

    @PreAuthorize("@ss.hasPermi('foundation:focus18:list')")
    @GetMapping("/list")
    public TableDataInfo list(FdFocus18 query)
    {
        startPage();
        List<FdFocus18> list = fdFocus18Service.selectFdFocus18List(query);
        return getDataTable(list);
    }

    @GetMapping("/listAll")
    public List<FdFocus18> listAll(FdFocus18 query)
    {
        return fdFocus18Service.selectFdFocus18List(query);
    }

    /** 左侧分类树：仅返回耗材类别名称，避免全量明细导致超时 */
    @GetMapping("/categories")
    public List<String> categories()
    {
        return fdFocus18Service.selectFdFocus18Categories();
    }

    /**
     * 产品维护：医保编码前 15 位匹配耗材分类代码，回填 18 类字段
     */
    @GetMapping("/matchByMedicalNo")
    public AjaxResult matchByMedicalNo(String medicalNo)
    {
        return success(fdFocus18Service.matchByMedicalNo(medicalNo));
    }

    @PreAuthorize("@ss.hasPermi('foundation:focus18:export')")
    @Log(title = "18类重点耗材", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, FdFocus18 query)
    {
        List<FdFocus18> list = fdFocus18Service.selectFdFocus18List(query);
        ExcelUtil<FdFocus18> util = new ExcelUtil<FdFocus18>(FdFocus18.class);
        util.exportExcel(response, list, "18类重点耗材");
    }

    @PreAuthorize("@ss.hasPermi('foundation:focus18:query')")
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable Long id)
    {
        return success(fdFocus18Service.selectFdFocus18ById(id));
    }

    @PreAuthorize("@ss.hasPermi('foundation:focus18:add')")
    @Log(title = "18类重点耗材", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody FdFocus18 row)
    {
        return toAjax(fdFocus18Service.insertFdFocus18(row));
    }

    @PreAuthorize("@ss.hasPermi('foundation:focus18:edit')")
    @Log(title = "18类重点耗材", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody FdFocus18 row)
    {
        return toAjax(fdFocus18Service.updateFdFocus18(row));
    }

    @PreAuthorize("@ss.hasPermi('foundation:focus18:remove')")
    @Log(title = "18类重点耗材", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        int rows = 0;
        for (Long id : ids)
        {
            rows += fdFocus18Service.deleteFdFocus18ById(id);
        }
        return toAjax(rows);
    }
}

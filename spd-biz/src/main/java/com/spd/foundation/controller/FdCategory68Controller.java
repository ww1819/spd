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
import com.spd.foundation.domain.FdCategory68;
import com.spd.foundation.service.IFdCategory68Service;
import com.spd.common.utils.poi.ExcelUtil;
import com.spd.common.core.page.TableDataInfo;

/**
 * 68分类Controller
 *
 * @author spd
 * @date 2024-04-12
 */
@RestController
@RequestMapping("/foundation/category68")
public class FdCategory68Controller extends BaseController
{
    @Autowired
    private IFdCategory68Service fdCategory68Service;

    /**
     * 查询68分类列表
     */
    @PreAuthorize("@ss.hasPermi('foundation:category68:list')")
    @GetMapping("/list")
    public TableDataInfo list(FdCategory68 fdCategory68)
    {
        startPage();
        List<FdCategory68> list = fdCategory68Service.selectFdCategory68List(fdCategory68);
        return getDataTable(list);
    }

    /**
     * 查询所有68分类列表
     */
    @GetMapping("/listAll")
    public List<FdCategory68> listAll(FdCategory68 fdCategory68)
    {
        List<FdCategory68> list = fdCategory68Service.selectFdCategory68List(fdCategory68);
        return list;
    }

    /**
     * 查询68分类树形列表
     */
    @GetMapping("/treeselect")
    public AjaxResult treeselect()
    {
        List<FdCategory68> list = fdCategory68Service.selectFdCategory68Tree();
        return success(list);
    }

    /**
     * 导出68分类列表
     */
    @PreAuthorize("@ss.hasPermi('foundation:category68:export')")
    @Log(title = "68分类", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, FdCategory68 fdCategory68)
    {
        List<FdCategory68> list = fdCategory68Service.selectFdCategory68List(fdCategory68);
        ExcelUtil<FdCategory68> util = new ExcelUtil<FdCategory68>(FdCategory68.class);
        util.exportExcel(response, list, "68分类数据");
    }

    /**
     * 获取68分类详细信息
     */
    @PreAuthorize("@ss.hasPermi('foundation:category68:query')")
    @GetMapping(value = "/{category68Id}")
    public AjaxResult getInfo(@PathVariable("category68Id") Long category68Id)
    {
        return success(fdCategory68Service.selectFdCategory68ByCategory68Id(category68Id));
    }

    /**
     * 新增68分类
     */
    @PreAuthorize("@ss.hasPermi('foundation:category68:add')")
    @Log(title = "68分类", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody FdCategory68 fdCategory68)
    {
        return toAjax(fdCategory68Service.insertFdCategory68(fdCategory68));
    }

    /**
     * 修改68分类
     */
    @PreAuthorize("@ss.hasPermi('foundation:category68:edit')")
    @Log(title = "68分类", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody FdCategory68 fdCategory68)
    {
        return toAjax(fdCategory68Service.updateFdCategory68(fdCategory68));
    }

    /**
     * 删除68分类
     */
    @PreAuthorize("@ss.hasPermi('foundation:category68:remove')")
    @Log(title = "68分类", businessType = BusinessType.DELETE)
	@DeleteMapping("/{category68Ids}")
    public AjaxResult remove(@PathVariable Long category68Ids)
    {
        return toAjax(fdCategory68Service.deleteFdCategory68ByCategory68Ids(category68Ids));
    }
}


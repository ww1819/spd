package com.spd.department.controller;

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
import com.spd.department.domain.NewProductApply;
import com.spd.department.service.INewProductApplyService;
import com.spd.common.utils.poi.ExcelUtil;
import com.spd.common.core.page.TableDataInfo;

/**
 * 新品申购申请Controller
 * 
 * @author spd
 * @date 2025-01-01
 */
@RestController
@RequestMapping("/department/newProductApply")
public class NewProductApplyController extends BaseController
{
    @Autowired
    private INewProductApplyService newProductApplyService;

    /**
     * 查询新品申购申请列表
     */
    @PreAuthorize("@ss.hasPermi('department:newProductApply:list')")
    @GetMapping("/list")
    public TableDataInfo list(NewProductApply newProductApply)
    {
        startPage();
        List<NewProductApply> list = newProductApplyService.selectNewProductApplyList(newProductApply);
        return getDataTable(list);
    }

    /**
     * 导出新品申购申请列表
     */
    @PreAuthorize("@ss.hasPermi('department:newProductApply:export')")
    @Log(title = "新品申购申请", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, NewProductApply newProductApply)
    {
        List<NewProductApply> list = newProductApplyService.selectNewProductApplyList(newProductApply);
        ExcelUtil<NewProductApply> util = new ExcelUtil<NewProductApply>(NewProductApply.class);
        util.exportExcel(response, list, "新品申购申请数据");
    }

    /**
     * 获取新品申购申请详细信息
     */
    @PreAuthorize("@ss.hasPermi('department:newProductApply:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(newProductApplyService.selectNewProductApplyById(id));
    }

    /**
     * 新增新品申购申请
     */
    @PreAuthorize("@ss.hasPermi('department:newProductApply:add')")
    @Log(title = "新品申购申请", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody NewProductApply newProductApply)
    {
        int result = newProductApplyService.insertNewProductApply(newProductApply);
        if (result > 0) {
            // 插入成功后返回newProductApply对象，此时id已被自动填充
            return success(newProductApply);
        }
        return toAjax(result);
    }

    /**
     * 修改新品申购申请
     */
    @PreAuthorize("@ss.hasPermi('department:newProductApply:edit')")
    @Log(title = "新品申购申请", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody NewProductApply newProductApply)
    {
        return toAjax(newProductApplyService.updateNewProductApply(newProductApply));
    }

    /**
     * 删除新品申购申请
     */
    @PreAuthorize("@ss.hasPermi('department:newProductApply:remove')")
    @Log(title = "新品申购申请", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(newProductApplyService.deleteNewProductApplyByIds(ids));
    }
}


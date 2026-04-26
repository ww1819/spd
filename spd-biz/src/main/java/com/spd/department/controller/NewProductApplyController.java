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

    private TableDataInfo buildList(NewProductApply newProductApply)
    {
        startPage();
        List<NewProductApply> list = newProductApplyService.selectNewProductApplyList(newProductApply);
        return getDataTable(list);
    }

    private AjaxResult buildGetInfo(Long id)
    {
        return success(newProductApplyService.selectNewProductApplyById(id));
    }

    /**
     * 查询新品申购申请列表
     */
    @PreAuthorize("@ss.hasPermi('department:newProductApply:list')")
    @GetMapping("/list")
    public TableDataInfo list(NewProductApply newProductApply)
    {
        return buildList(newProductApply);
    }

    /**
     * 列表（仅需登录）：与 {@link #list} 数据一致，租户由 Mapper/SQL 约束；供仅有菜单入口无 list 权限用户使用。
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/pick/list")
    public TableDataInfo pickList(NewProductApply newProductApply)
    {
        return buildList(newProductApply);
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
        return buildGetInfo(id);
    }

    /**
     * 详情（仅需登录）：与 {@link #getInfo} 数据一致。
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/pick/{id}")
    public AjaxResult pickGetInfo(@PathVariable("id") Long id)
    {
        return buildGetInfo(id);
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


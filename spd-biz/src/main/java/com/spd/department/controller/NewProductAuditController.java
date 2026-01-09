package com.spd.department.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.spd.common.annotation.Log;
import com.spd.common.core.controller.BaseController;
import com.spd.common.core.domain.AjaxResult;
import com.spd.common.enums.BusinessType;
import com.spd.department.domain.NewProductApply;
import com.spd.department.service.INewProductAuditService;
import com.spd.common.utils.poi.ExcelUtil;
import com.spd.common.core.page.TableDataInfo;

/**
 * 新品申购审批Controller
 * 
 * @author spd
 * @date 2025-01-01
 */
@RestController
@RequestMapping("/department/newProductAudit")
public class NewProductAuditController extends BaseController
{
    @Autowired
    private INewProductAuditService newProductAuditService;

    /**
     * 查询新品申购审批列表
     */
    @PreAuthorize("@ss.hasPermi('department:newProductAudit:list')")
    @GetMapping("/list")
    public TableDataInfo list(NewProductApply newProductApply)
    {
        startPage();
        List<NewProductApply> list = newProductAuditService.selectNewProductAuditList(newProductApply);
        return getDataTable(list);
    }

    /**
     * 导出新品申购审批列表
     */
    @PreAuthorize("@ss.hasPermi('department:newProductAudit:export')")
    @Log(title = "新品申购审批", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, NewProductApply newProductApply)
    {
        List<NewProductApply> list = newProductAuditService.selectNewProductAuditList(newProductApply);
        ExcelUtil<NewProductApply> util = new ExcelUtil<NewProductApply>(NewProductApply.class);
        util.exportExcel(response, list, "新品申购审批数据");
    }

    /**
     * 获取新品申购申请详细信息
     */
    @PreAuthorize("@ss.hasPermi('department:newProductAudit:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(newProductAuditService.selectNewProductAuditById(id));
    }

    /**
     * 审核通过
     */
    @PreAuthorize("@ss.hasPermi('department:newProductAudit:audit')")
    @Log(title = "新品申购审批", businessType = BusinessType.UPDATE)
    @PutMapping("/audit/{id}")
    public AjaxResult audit(@PathVariable Long id)
    {
        return toAjax(newProductAuditService.auditNewProductApply(id));
    }

    /**
     * 驳回
     */
    @PreAuthorize("@ss.hasPermi('department:newProductAudit:reject')")
    @Log(title = "新品申购审批", businessType = BusinessType.UPDATE)
    @PutMapping("/reject")
    public AjaxResult reject(@RequestBody NewProductApply newProductApply)
    {
        // 前端传入的数据结构：{ id: xxx, rejectReason: "驳回原因" }
        // 将rejectReason设置到remark字段中
        if (newProductApply.getRemark() == null || newProductApply.getRemark().isEmpty()) {
            // 如果前端传入的是rejectReason字段，需要映射到remark
            // 这里假设前端直接传入remark字段，如果传入的是rejectReason，需要添加getter/setter
        }
        return toAjax(newProductAuditService.rejectNewProductApply(newProductApply));
    }
}

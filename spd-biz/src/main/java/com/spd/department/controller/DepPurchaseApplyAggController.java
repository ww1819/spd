package com.spd.department.controller;

import java.util.List;

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

import com.alibaba.fastjson2.JSONObject;
import com.spd.common.annotation.Log;
import com.spd.common.core.controller.BaseController;
import com.spd.common.core.domain.AjaxResult;
import com.spd.common.core.page.TableDataInfo;
import com.spd.common.enums.BusinessType;
import com.spd.common.utils.StringUtils;
import com.spd.department.domain.DepPurchaseApplyAgg;
import com.spd.department.service.IDepPurchaseApplyAggService;

/**
 * 科室汇总申购（不分仓库）；审核后按默认仓库拆分为科室申购单。
 */
@RestController
@RequestMapping("/department/purchaseAgg")
public class DepPurchaseApplyAggController extends BaseController {

    @Autowired
    private IDepPurchaseApplyAggService depPurchaseApplyAggService;

    @PreAuthorize("@ss.hasPermi('department:purchase:list') || @ss.hasPermi('department:purchaseAudit:list')")
    @GetMapping("/list")
    public TableDataInfo list(DepPurchaseApplyAgg query) {
        depPurchaseApplyAggService.applyDepartmentScopeToQuery(query);
        startPage();
        List<DepPurchaseApplyAgg> list = depPurchaseApplyAggService.selectDepPurchaseApplyAggList(query);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('department:purchase:query') || @ss.hasPermi('department:purchaseAudit:list')")
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id) {
        return success(depPurchaseApplyAggService.selectDepPurchaseApplyAggById(id));
    }

    @PreAuthorize("@ss.hasPermi('department:purchase:add')")
    @Log(title = "科室汇总申购", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody DepPurchaseApplyAgg row) {
        int result = depPurchaseApplyAggService.insertDepPurchaseApplyAgg(row);
        if (result > 0) {
            return success(row);
        }
        return toAjax(result);
    }

    @PreAuthorize("@ss.hasPermi('department:purchase:edit')")
    @Log(title = "科室汇总申购", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody DepPurchaseApplyAgg row) {
        return toAjax(depPurchaseApplyAggService.updateDepPurchaseApplyAgg(row));
    }

    @PreAuthorize("@ss.hasPermi('department:purchase:remove')")
    @Log(title = "科室汇总申购", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable String ids) {
        if (StringUtils.isEmpty(ids)) {
            return error("请选择要删除的数据");
        }
        String[] idArr = ids.split(",");
        return toAjax(depPurchaseApplyAggService.deleteDepPurchaseApplyAggByIds(idArr));
    }

    @PreAuthorize("@ss.hasPermi('department:purchase:audit') || @ss.hasPermi('department:purchaseAudit:audit')")
    @Log(title = "科室汇总申购审核", businessType = BusinessType.UPDATE)
    @PutMapping("/auditApply")
    public AjaxResult audit(@RequestBody JSONObject json) {
        int result = depPurchaseApplyAggService.auditDepPurchaseApplyAgg(json.getString("id"));
        return toAjax(result);
    }

    @PreAuthorize("@ss.hasPermi('department:purchase:reject') || @ss.hasPermi('department:purchaseAudit:reject')")
    @Log(title = "科室汇总申购驳回", businessType = BusinessType.UPDATE)
    @PutMapping("/reject")
    public AjaxResult reject(@RequestBody JSONObject json) {
        int result = depPurchaseApplyAggService.rejectDepPurchaseApplyAgg(
            json.getString("id"), json.getString("rejectReason"));
        return toAjax(result);
    }
}

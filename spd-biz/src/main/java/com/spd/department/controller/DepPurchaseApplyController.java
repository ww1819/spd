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
import com.spd.department.domain.DepPurchaseApply;
import com.spd.department.service.IDepPurchaseApplyService;
import com.spd.common.utils.poi.ExcelUtil;
import com.spd.common.core.page.TableDataInfo;
import com.alibaba.fastjson2.JSONObject;

/**
 * 科室申购Controller
 * 
 * @author spd
 * @date 2025-01-01
 */
@RestController
@RequestMapping("/department/purchase")
public class DepPurchaseApplyController extends BaseController
{
    @Autowired
    private IDepPurchaseApplyService depPurchaseApplyService;

    /**
     * 查询科室申购列表
     */
    @PreAuthorize("@ss.hasPermi('department:purchase:list')")
    @GetMapping("/list")
    public TableDataInfo list(DepPurchaseApply depPurchaseApply)
    {
        startPage();
        List<DepPurchaseApply> list = depPurchaseApplyService.selectDepPurchaseApplyList(depPurchaseApply);
        return getDataTable(list);
    }

    /**
     * 导出科室申购列表
     */
    @PreAuthorize("@ss.hasPermi('department:purchase:export')")
    @Log(title = "科室申购", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, DepPurchaseApply depPurchaseApply)
    {
        List<DepPurchaseApply> list = depPurchaseApplyService.selectDepPurchaseApplyList(depPurchaseApply);
        ExcelUtil<DepPurchaseApply> util = new ExcelUtil<DepPurchaseApply>(DepPurchaseApply.class);
        util.exportExcel(response, list, "科室申购数据");
    }

    /**
     * 获取科室申购详细信息
     */
    @PreAuthorize("@ss.hasPermi('department:purchase:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(depPurchaseApplyService.selectDepPurchaseApplyById(id));
    }

    /**
     * 新增科室申购
     */
    @PreAuthorize("@ss.hasPermi('department:purchase:add')")
    @Log(title = "科室申购", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody DepPurchaseApply depPurchaseApply)
    {
        int result = depPurchaseApplyService.insertDepPurchaseApply(depPurchaseApply);
        if (result > 0) {
            // 插入成功后返回depPurchaseApply对象，此时id已被自动填充
            return success(depPurchaseApply);
        }
        return toAjax(result);
    }

    /**
     * 修改科室申购
     */
    @PreAuthorize("@ss.hasPermi('department:purchase:edit')")
    @Log(title = "科室申购", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody DepPurchaseApply depPurchaseApply)
    {
        return toAjax(depPurchaseApplyService.updateDepPurchaseApply(depPurchaseApply));
    }

    /**
     * 删除科室申购
     */
    @PreAuthorize("@ss.hasPermi('department:purchase:remove')")
    @Log(title = "科室申购", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(depPurchaseApplyService.deleteDepPurchaseApplyByIds(ids));
    }

    /**
     * 审核科室申购
     */
    @PreAuthorize("@ss.hasPermi('department:purchase:audit')")
    @Log(title = "科室申购审核", businessType = BusinessType.UPDATE)
    @PutMapping("/auditApply")
    public AjaxResult audit(@RequestBody JSONObject json)
    {
        int result = depPurchaseApplyService.auditPurchaseApply(json.getString("id"), json.getString("auditBy"));
        return toAjax(result);
    }

    /**
     * 驳回科室申购
     */
    @PreAuthorize("@ss.hasPermi('department:purchase:reject')")
    @Log(title = "科室申购驳回", businessType = BusinessType.UPDATE)
    @PutMapping("/reject")
    public AjaxResult reject(@RequestBody JSONObject json)
    {
        int result = depPurchaseApplyService.rejectPurchaseApply(json.getString("id"), json.getString("rejectReason"));
        return toAjax(result);
    }
}

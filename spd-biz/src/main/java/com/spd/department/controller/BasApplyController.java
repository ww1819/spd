package com.spd.department.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson2.JSONObject;
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
import com.spd.department.domain.BasApply;
import com.spd.department.service.IBasApplyService;
import com.spd.common.utils.poi.ExcelUtil;
import com.spd.common.core.page.TableDataInfo;

/**
 * 科室申领Controller
 *
 * @author spd
 * @date 2024-02-26
 */
@RestController
@RequestMapping("/department/apply")
public class BasApplyController extends BaseController
{
    @Autowired
    private IBasApplyService basApplyService;

    /**
     * 查询科室申领列表
     */
    @PreAuthorize("@ss.hasPermi('department:dApply:list')")
    @GetMapping("/list")
    public TableDataInfo list(BasApply basApply)
    {
        startPage();
        List<BasApply> list = basApplyService.selectBasApplyList(basApply);
        return getDataTable(list);
    }

    /**
     * 导出科室申领列表
     */
    @PreAuthorize("@ss.hasPermi('department:dApply:export')")
    @Log(title = "科室申领", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, BasApply basApply)
    {
        List<BasApply> list = basApplyService.selectBasApplyList(basApply);
        ExcelUtil<BasApply> util = new ExcelUtil<BasApply>(BasApply.class);
        util.exportExcel(response, list, "科室申领数据");
    }

    /**
     * 获取科室申领详细信息
     */
    @PreAuthorize("@ss.hasPermi('department:dApply:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(basApplyService.selectBasApplyById(id));
    }

    /**
     * 新增科室申领
     */
    @PreAuthorize("@ss.hasPermi('department:dApply:add')")
    @Log(title = "科室申领", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody BasApply basApply)
    {
        int result = basApplyService.insertBasApply(basApply);
        if (result > 0) {
            // 插入成功后返回basApply对象，此时id已被自动填充
            return success(basApply);
        }
        return toAjax(result);
    }

    /**
     * 修改科室申领
     */
    @PreAuthorize("@ss.hasPermi('department:dApply:edit')")
    @Log(title = "科室申领", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody BasApply basApply)
    {
        return toAjax(basApplyService.updateBasApply(basApply));
    }

    /**
     * 删除科室申领
     */
    @PreAuthorize("@ss.hasPermi('department:dApply:remove')")
    @Log(title = "科室申领", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(basApplyService.deleteBasApplyByIds(ids));
    }


    /**
     * 审核科室申领
     */
    @PreAuthorize("@ss.hasPermi('department:dApply:audit')")
    @Log(title = "科室申领审核", businessType = BusinessType.UPDATE)
    @PutMapping("/auditApply")
    public AjaxResult audit(@RequestBody JSONObject json)
    {
        int result = basApplyService.auditApply(json.getString("id"), json.getString("auditBy"));
        return toAjax(result);
    }
}

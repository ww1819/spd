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
import com.spd.common.core.controller.BaseController;
import com.spd.common.core.domain.AjaxResult;
import com.spd.department.domain.BasApplyTemplate;
import com.spd.department.service.IBasApplyTemplateService;

/**
 * 科室申领制单模板Controller
 *
 * @author spd
 */
@RestController
@RequestMapping("/department/applyTemplate")
public class BasApplyTemplateController extends BaseController {

    @Autowired
    private IBasApplyTemplateService basApplyTemplateService;

    /**
     * 查询制单模板列表（按模板名称模糊，仅返回未删除）
     */
    @PreAuthorize("@ss.hasPermi('department:dApply:list')")
    @GetMapping("/list")
    public AjaxResult list(BasApplyTemplate template) {
        List<BasApplyTemplate> list = basApplyTemplateService.selectBasApplyTemplateList(template);
        return success(list);
    }

    /**
     * 获取制单模板详细信息（含明细）
     */
    @PreAuthorize("@ss.hasPermi('department:dApply:query')")
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return success(basApplyTemplateService.selectBasApplyTemplateById(id));
    }

    /**
     * 新增制单模板（含明细）
     */
    @PreAuthorize("@ss.hasPermi('department:dApply:add')")
    @PostMapping
    public AjaxResult add(@RequestBody BasApplyTemplate template) {
        return toAjax(basApplyTemplateService.insertBasApplyTemplate(template));
    }

    /**
     * 删除制单模板（及明细）
     */
    @PreAuthorize("@ss.hasPermi('department:dApply:remove')")
    @DeleteMapping("/{id}")
    public AjaxResult remove(@PathVariable("id") Long id) {
        return toAjax(basApplyTemplateService.deleteBasApplyTemplateById(id));
    }

    /**
     * 修改制单模板（含明细）
     */
    @PreAuthorize("@ss.hasPermi('department:dApply:edit')")
    @PutMapping
    public AjaxResult edit(@RequestBody BasApplyTemplate template) {
        return toAjax(basApplyTemplateService.updateBasApplyTemplate(template));
    }
}

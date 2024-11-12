package com.spd.gz.controller;

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
import com.spd.gz.domain.GzDepApply;
import com.spd.gz.service.IGzDepApplyService;
import com.spd.common.utils.poi.ExcelUtil;
import com.spd.common.core.page.TableDataInfo;

/**
 * 高值科室申领Controller
 *
 * @author spd
 * @date 2024-06-22
 */
@RestController
@RequestMapping("/gzDepartment/apply")
public class GzDepApplyController extends BaseController
{
    @Autowired
    private IGzDepApplyService gzDepApplyService;

    /**
     * 查询高值科室申领列表
     */
    @PreAuthorize("@ss.hasPermi('gzDepartment:apply:list')")
    @GetMapping("/list")
    public TableDataInfo list(GzDepApply gzDepApply)
    {
        startPage();
        List<GzDepApply> list = gzDepApplyService.selectGzDepApplyList(gzDepApply);
        return getDataTable(list);
    }

    /**
     * 导出高值科室申领列表
     */
    @PreAuthorize("@ss.hasPermi('gzDepartment:apply:export')")
    @Log(title = "高值科室申领", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, GzDepApply gzDepApply)
    {
        List<GzDepApply> list = gzDepApplyService.selectGzDepApplyList(gzDepApply);
        ExcelUtil<GzDepApply> util = new ExcelUtil<GzDepApply>(GzDepApply.class);
        util.exportExcel(response, list, "高值科室申领数据");
    }

    /**
     * 获取高值科室申领详细信息
     */
    @PreAuthorize("@ss.hasPermi('gzDepartment:apply:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(gzDepApplyService.selectGzDepApplyById(id));
    }

    /**
     * 新增高值科室申领
     */
    @PreAuthorize("@ss.hasPermi('gzDepartment:apply:add')")
    @Log(title = "高值科室申领", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody GzDepApply gzDepApply)
    {
        return toAjax(gzDepApplyService.insertGzDepApply(gzDepApply));
    }

    /**
     * 修改高值科室申领
     */
    @PreAuthorize("@ss.hasPermi('gzDepartment:apply:edit')")
    @Log(title = "高值科室申领", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody GzDepApply gzDepApply)
    {
        return toAjax(gzDepApplyService.updateGzDepApply(gzDepApply));
    }

    /**
     * 删除高值科室申领
     */
    @PreAuthorize("@ss.hasPermi('gzDepartment:apply:remove')")
    @Log(title = "高值科室申领", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(gzDepApplyService.deleteGzDepApplyByIds(ids));
    }

    /**
     * 审核科室申领
     */
    @PreAuthorize("@ss.hasPermi('gzDepartment:dApply:audit')")
    @Log(title = "科室申领审核", businessType = BusinessType.UPDATE)
    @PutMapping("/auditApply")
    public AjaxResult audit(@RequestBody JSONObject json)
    {
        int result = gzDepApplyService.auditApply(json.getString("id"));
        return toAjax(result);
    }
}

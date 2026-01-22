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
import com.spd.department.domain.DeptBatchConsume;
import com.spd.department.service.IDeptBatchConsumeService;
import com.spd.common.utils.poi.ExcelUtil;
import com.spd.common.core.page.TableDataInfo;

/**
 * 科室批量消耗Controller
 *
 * @author spd
 * @date 2025-01-15
 */
@RestController
@RequestMapping("/department/batchConsume")
public class DeptBatchConsumeController extends BaseController
{
    @Autowired
    private IDeptBatchConsumeService deptBatchConsumeService;

    /**
     * 查询科室批量消耗列表
     */
    @PreAuthorize("@ss.hasPermi('department:batchConsume:list')")
    @GetMapping("/list")
    public TableDataInfo list(DeptBatchConsume deptBatchConsume)
    {
        startPage();
        List<DeptBatchConsume> list = deptBatchConsumeService.selectDeptBatchConsumeList(deptBatchConsume);
        return getDataTable(list);
    }

    /**
     * 导出科室批量消耗列表
     */
    @PreAuthorize("@ss.hasPermi('department:batchConsume:export')")
    @Log(title = "科室批量消耗", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, DeptBatchConsume deptBatchConsume)
    {
        List<DeptBatchConsume> list = deptBatchConsumeService.selectDeptBatchConsumeList(deptBatchConsume);
        ExcelUtil<DeptBatchConsume> util = new ExcelUtil<DeptBatchConsume>(DeptBatchConsume.class);
        util.exportExcel(response, list, "科室批量消耗数据");
    }

    /**
     * 获取科室批量消耗详细信息
     */
    @PreAuthorize("@ss.hasPermi('department:batchConsume:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(deptBatchConsumeService.selectDeptBatchConsumeById(id));
    }

    /**
     * 新增科室批量消耗
     */
    @PreAuthorize("@ss.hasPermi('department:batchConsume:add')")
    @Log(title = "科室批量消耗", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody DeptBatchConsume deptBatchConsume)
    {
        int result = deptBatchConsumeService.insertDeptBatchConsume(deptBatchConsume);
        if (result > 0) {
            // 插入成功后返回deptBatchConsume对象，此时id已被自动填充
            return success(deptBatchConsume);
        }
        return toAjax(result);
    }

    /**
     * 修改科室批量消耗
     */
    @PreAuthorize("@ss.hasPermi('department:batchConsume:edit')")
    @Log(title = "科室批量消耗", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody DeptBatchConsume deptBatchConsume)
    {
        return toAjax(deptBatchConsumeService.updateDeptBatchConsume(deptBatchConsume));
    }

    /**
     * 删除科室批量消耗
     */
    @PreAuthorize("@ss.hasPermi('department:batchConsume:remove')")
    @Log(title = "科室批量消耗", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(deptBatchConsumeService.deleteDeptBatchConsumeByIds(ids));
    }

    /**
     * 审核科室批量消耗
     */
    @PreAuthorize("@ss.hasPermi('department:batchConsume:audit')")
    @Log(title = "科室批量消耗审核", businessType = BusinessType.UPDATE)
    @PutMapping("/audit")
    public AjaxResult audit(@RequestBody JSONObject json)
    {
        int result = deptBatchConsumeService.auditConsume(json.getString("id"), json.getString("auditBy"));
        return toAjax(result);
    }
}

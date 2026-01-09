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
 * 转科申请Controller
 *
 * @author spd
 * @date 2024-01-02
 */
@RestController
@RequestMapping("/department/transfer")
public class DepartmentTransferController extends BaseController
{
    @Autowired
    private IBasApplyService basApplyService;

    /**
     * 查询转科申请列表
     */
    @PreAuthorize("@ss.hasPermi('departmentTransfer:apply:list')")
    @GetMapping("/list")
    public TableDataInfo list(BasApply basApply)
    {
        // 设置单据类型为转科申请单
        basApply.setBillType(3);
        startPage();
        List<BasApply> list = basApplyService.selectBasApplyList(basApply);
        return getDataTable(list);
    }

    /**
     * 导出转科申请列表
     */
    @PreAuthorize("@ss.hasPermi('departmentTransfer:apply:export')")
    @Log(title = "转科申请", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, BasApply basApply)
    {
        basApply.setBillType(3);
        List<BasApply> list = basApplyService.selectBasApplyList(basApply);
        ExcelUtil<BasApply> util = new ExcelUtil<BasApply>(BasApply.class);
        util.exportExcel(response, list, "转科申请数据");
    }

    /**
     * 获取转科申请详细信息
     */
    @PreAuthorize("@ss.hasPermi('departmentTransfer:apply:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(basApplyService.selectBasApplyById(id));
    }

    /**
     * 新增转科申请
     */
    @PreAuthorize("@ss.hasPermi('departmentTransfer:apply:add')")
    @Log(title = "转科申请", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody BasApply basApply)
    {
        // 设置单据类型为转科申请单
        basApply.setBillType(3);
        int result = basApplyService.insertBasApply(basApply);
        if (result > 0) {
            // 插入成功后返回basApply对象，此时id已被自动填充
            return success(basApply);
        }
        return toAjax(result);
    }

    /**
     * 修改转科申请
     */
    @PreAuthorize("@ss.hasPermi('departmentTransfer:apply:edit')")
    @Log(title = "转科申请", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody BasApply basApply)
    {
        basApply.setBillType(3);
        return toAjax(basApplyService.updateBasApply(basApply));
    }

    /**
     * 删除转科申请
     */
    @PreAuthorize("@ss.hasPermi('departmentTransfer:apply:remove')")
    @Log(title = "转科申请", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(basApplyService.deleteBasApplyByIds(ids));
    }

    /**
     * 审核转科申请
     */
    @PreAuthorize("@ss.hasPermi('departmentTransfer:apply:audit')")
    @Log(title = "转科申请审核", businessType = BusinessType.UPDATE)
    @PutMapping("/auditApply")
    public AjaxResult audit(@RequestBody JSONObject json)
    {
        int result = basApplyService.auditApply(json.getString("id"), json.getString("auditBy"));
        return toAjax(result);
    }
}


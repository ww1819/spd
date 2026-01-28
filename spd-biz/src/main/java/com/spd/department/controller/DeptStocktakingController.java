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
import com.spd.warehouse.domain.StkIoStocktaking;
import com.spd.department.service.IDeptStocktakingService;
import com.spd.common.utils.poi.ExcelUtil;
import com.spd.common.core.page.TableDataInfo;

/**
 * 科室盘点Controller
 *
 * @author spd
 * @date 2025-01-28
 */
@RestController
@RequestMapping("/department/stocktaking")
public class DeptStocktakingController extends BaseController
{
    @Autowired
    private IDeptStocktakingService deptStocktakingService;

    /**
     * 查询科室盘点列表
     */
    @PreAuthorize("@ss.hasPermi('department:stocktaking:list')")
    @GetMapping("/list")
    public TableDataInfo list(StkIoStocktaking stkIoStocktaking)
    {
        startPage();
        List<StkIoStocktaking> list = deptStocktakingService.selectDeptStocktakingList(stkIoStocktaking);
        return getDataTable(list);
    }

    /**
     * 导出科室盘点列表
     */
    @PreAuthorize("@ss.hasPermi('department:stocktaking:export')")
    @Log(title = "科室盘点", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, StkIoStocktaking stkIoStocktaking)
    {
        List<StkIoStocktaking> list = deptStocktakingService.selectDeptStocktakingList(stkIoStocktaking);
        ExcelUtil<StkIoStocktaking> util = new ExcelUtil<StkIoStocktaking>(StkIoStocktaking.class);
        util.exportExcel(response, list, "科室盘点数据");
    }

    /**
     * 获取科室盘点详细信息
     */
    @PreAuthorize("@ss.hasPermi('department:stocktaking:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(deptStocktakingService.selectDeptStocktakingById(id));
    }

    /**
     * 新增科室盘点
     */
    @PreAuthorize("@ss.hasPermi('department:stocktaking:add')")
    @Log(title = "科室盘点", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody StkIoStocktaking stkIoStocktaking)
    {
        int rows = deptStocktakingService.insertDeptStocktaking(stkIoStocktaking);
        if (rows > 0) {
            // 返回保存后的完整对象（包括ID和明细）
            StkIoStocktaking saved = deptStocktakingService.selectDeptStocktakingById(stkIoStocktaking.getId());
            return success(saved);
        }
        return toAjax(rows);
    }

    /**
     * 修改科室盘点
     */
    @PreAuthorize("@ss.hasPermi('department:stocktaking:edit')")
    @Log(title = "科室盘点", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody StkIoStocktaking stkIoStocktaking)
    {
        return toAjax(deptStocktakingService.updateDeptStocktaking(stkIoStocktaking));
    }

    /**
     * 删除科室盘点
     */
    @PreAuthorize("@ss.hasPermi('department:stocktaking:remove')")
    @Log(title = "科室盘点", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(deptStocktakingService.deleteDeptStocktakingByIds(ids));
    }

    /**
     * 审核科室盘点
     */
    @PreAuthorize("@ss.hasPermi('department:stocktaking:audit') or @ss.hasPermi('department:stocktakingAudit:audit')")
    @Log(title = "科室盘点审核", businessType = BusinessType.UPDATE)
    @PutMapping("/auditStocktaking")
    public AjaxResult audit(@RequestBody JSONObject json)
    {
        int result = deptStocktakingService.auditDeptStocktaking(json.getString("id"));
        return toAjax(result);
    }

    /**
     * 驳回科室盘点
     */
    @PreAuthorize("@ss.hasPermi('department:stocktaking:reject') or @ss.hasPermi('department:stocktakingAudit:reject')")
    @Log(title = "科室盘点驳回", businessType = BusinessType.UPDATE)
    @PutMapping("/reject")
    public AjaxResult reject(@RequestBody JSONObject json)
    {
        int result = deptStocktakingService.rejectDeptStocktaking(
            json.getString("id"), 
            json.getString("rejectReason")
        );
        return toAjax(result);
    }
}

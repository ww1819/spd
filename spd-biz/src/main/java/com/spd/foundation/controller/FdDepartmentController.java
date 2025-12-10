package com.spd.foundation.controller;

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
import com.spd.common.core.domain.entity.SysUser;
import com.spd.common.enums.BusinessType;
import com.spd.foundation.domain.FdDepartment;
import com.spd.foundation.service.IFdDepartmentService;
import com.spd.common.utils.poi.ExcelUtil;
import com.spd.common.core.page.TableDataInfo;

/**
 * 科室Controller
 *
 * @author spd
 * @date 2023-11-26
 */
@RestController
@RequestMapping("/foundation/depart")
public class FdDepartmentController extends BaseController
{
    @Autowired
    private IFdDepartmentService fdDepartmentService;

    /**
     * 查询科室列表
     */
    @PreAuthorize("@ss.hasPermi('foundation:depart:list')")
    @GetMapping("/list")
    public TableDataInfo list(FdDepartment fdDepartment)
    {
        startPage();
        List<FdDepartment> list = fdDepartmentService.selectFdDepartmentList(fdDepartment);
        return getDataTable(list);
    }

    /**
     * 查询所有科室列表
     */
    @GetMapping("/listAll/{userId}")
    public List<FdDepartment> listAll(@PathVariable(value = "userId") Long userId)
    {
        List<FdDepartment> list = null;
        if(SysUser.isAdmin(userId)){
            list = fdDepartmentService.selectdepartmenAll();
        }else{
            list = fdDepartmentService.selectUserDepartmenAll(userId);
        }

        return list;
    }

    /**
     * 导出科室列表
     */
    @PreAuthorize("@ss.hasPermi('foundation:depart:export')")
    @Log(title = "科室", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, FdDepartment fdDepartment)
    {
        List<FdDepartment> list = fdDepartmentService.selectFdDepartmentList(fdDepartment);
        ExcelUtil<FdDepartment> util = new ExcelUtil<FdDepartment>(FdDepartment.class);
        util.exportExcel(response, list, "科室数据");
    }

    /**
     * 获取科室详细信息
     */
    @PreAuthorize("@ss.hasPermi('foundation:depart:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id)
    {
        return success(fdDepartmentService.selectFdDepartmentById(id));
    }

    /**
     * 新增科室
     */
    @PreAuthorize("@ss.hasPermi('foundation:depart:add')")
    @Log(title = "科室", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody FdDepartment fdDepartment)
    {
        return toAjax(fdDepartmentService.insertFdDepartment(fdDepartment));
    }

    /**
     * 修改科室
     */
    @PreAuthorize("@ss.hasPermi('foundation:depart:edit')")
    @Log(title = "科室", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody FdDepartment fdDepartment)
    {
        return toAjax(fdDepartmentService.updateFdDepartment(fdDepartment));
    }

    /**
     * 删除科室
     */
    @PreAuthorize("@ss.hasPermi('foundation:depart:remove')")
    @Log(title = "科室", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable String ids)
    {
        return toAjax(fdDepartmentService.deleteFdDepartmentById(ids));
    }

    /**
     * 获取科室列表
     */
    @GetMapping("/optionselect")
    public AjaxResult optionselect()
    {
        List<FdDepartment> fdDepartmentList = fdDepartmentService.selectdepartmenAll();
        return success(fdDepartmentList);
    }
}

package com.spd.foundation.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.spd.common.core.domain.entity.SysUser;
import com.spd.foundation.domain.FdSupplier;
import com.spd.foundation.domain.FdWarehouse;
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
import com.spd.foundation.domain.FdMaterial;
import com.spd.foundation.service.IFdMaterialService;
import com.spd.common.utils.poi.ExcelUtil;
import com.spd.common.core.page.TableDataInfo;
import org.springframework.web.multipart.MultipartFile;

/**
 * 耗材产品Controller
 *
 * @author spd
 * @date 2023-12-23
 */
@RestController
@RequestMapping("/foundation/material")
public class FdMaterialController extends BaseController
{
    @Autowired
    private IFdMaterialService fdMaterialService;

    /**
     * 查询耗材产品列表
     */
    @PreAuthorize("@ss.hasPermi('foundation:material:list')")
    @GetMapping("/list")
    public TableDataInfo list(FdMaterial fdMaterial)
    {
        startPage();
        List<FdMaterial> list = fdMaterialService.selectFdMaterialList(fdMaterial);
        return getDataTable(list);
    }

    /**
     * 查询所有耗材产品列表
     */
    @GetMapping("/listAll")
    public List<FdMaterial> listAll(FdMaterial fdMaterial)
    {
        List<FdMaterial> list = fdMaterialService.selectFdMaterialList(fdMaterial);
        return list;
    }

    /**
     * 导出耗材产品列表
     */
    @PreAuthorize("@ss.hasPermi('foundation:material:export')")
    @Log(title = "耗材产品", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, FdMaterial fdMaterial)
    {
        List<FdMaterial> list = fdMaterialService.selectFdMaterialList(fdMaterial);
        ExcelUtil<FdMaterial> util = new ExcelUtil<FdMaterial>(FdMaterial.class);
        util.exportExcel(response, list, "耗材产品数据");
    }

    /**
     * 获取耗材产品详细信息
     */
    @PreAuthorize("@ss.hasPermi('foundation:material:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(fdMaterialService.selectFdMaterialById(id));
    }

    /**
     * 新增耗材产品
     */
    @PreAuthorize("@ss.hasPermi('foundation:material:add')")
    @Log(title = "耗材产品", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody FdMaterial fdMaterial)
    {
        return toAjax(fdMaterialService.insertFdMaterial(fdMaterial));
    }

    /**
     * 修改耗材产品
     */
    @PreAuthorize("@ss.hasPermi('foundation:material:edit')")
    @Log(title = "耗材产品", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody FdMaterial fdMaterial)
    {
        return toAjax(fdMaterialService.updateFdMaterial(fdMaterial));
    }

    /**
     * 删除耗材产品
     */
    @PreAuthorize("@ss.hasPermi('foundation:material:remove')")
    @Log(title = "耗材产品", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long ids)
    {
        return toAjax(fdMaterialService.deleteFdMaterialByIds(ids));
    }

    @Log(title = "耗材产品导入", businessType = BusinessType.IMPORT)
    @PreAuthorize("@ss.hasPermi('foundation:material:import')")
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file, boolean updateSupport) throws Exception
    {
        ExcelUtil<FdMaterial> util = new ExcelUtil<FdMaterial>(FdMaterial.class);
        List<FdMaterial> fdmaterialList = util.importExcel(file.getInputStream());
        String operName = getUsername();
        String message = fdMaterialService.importFdMaterial(fdmaterialList, updateSupport, operName);
        return success(message);
    }

    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response)
    {
        ExcelUtil<FdMaterial> util = new ExcelUtil<FdMaterial>(FdMaterial.class);
        util.importTemplateExcel(response, "耗材数据");
    }


}

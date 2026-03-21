package com.spd.foundation.controller;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.LinkedHashMap;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.spd.common.annotation.Log;
import com.spd.common.core.controller.BaseController;
import com.spd.common.core.domain.AjaxResult;
import com.spd.common.enums.BusinessType;
import com.spd.common.enums.TenantEnum;
import com.spd.common.utils.PinyinUtils;
import com.spd.common.utils.SecurityUtils;
import com.spd.common.utils.StringUtils;
import com.spd.foundation.domain.FdWarehouseCategory;
import com.spd.foundation.dto.WarehouseCategoryImportUpdateDto;
import com.spd.foundation.service.IFdWarehouseCategoryService;
import com.spd.common.utils.poi.ExcelUtil;
import com.spd.common.utils.poi.ImportRowErrorCollector;
import com.spd.common.core.page.TableDataInfo;

/**
 * 库房分类Controller
 *
 * @author spd
 * @date 2024-04-12
 */
@RestController
@RequestMapping("/foundation/warehouseCategory")
public class FdWarehouseCategoryController extends BaseController
{
    @Autowired
    private IFdWarehouseCategoryService fdWarehouseCategoryService;

    /**
     * 查询库房分类列表
     */
    @PreAuthorize("@ss.hasPermi('foundation:warehouseCategory:list')")
    @GetMapping("/list")
    public TableDataInfo list(FdWarehouseCategory fdWarehouseCategory)
    {
        startPage();
        List<FdWarehouseCategory> list = fdWarehouseCategoryService.selectFdWarehouseCategoryList(fdWarehouseCategory);
        return getDataTable(list);
    }

    /**
     * 查询所有库房分类列表
     */
    @GetMapping("/listAll")
    public List<FdWarehouseCategory> listAll(FdWarehouseCategory fdWarehouseCategory)
    {
        List<FdWarehouseCategory> list = fdWarehouseCategoryService.selectFdWarehouseCategoryList(fdWarehouseCategory);
        return list;
    }

    /**
     * 查询库房分类树形列表
     */
    @GetMapping("/treeselect")
    public AjaxResult treeselect()
    {
        List<FdWarehouseCategory> list = fdWarehouseCategoryService.selectFdWarehouseCategoryTree();
        return success(list);
    }

    /**
     * 导出库房分类列表
     */
    @PreAuthorize("@ss.hasPermi('foundation:warehouseCategory:export')")
    @Log(title = "库房分类", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, FdWarehouseCategory fdWarehouseCategory)
    {
        List<FdWarehouseCategory> list = fdWarehouseCategoryService.selectFdWarehouseCategoryList(fdWarehouseCategory);
        ExcelUtil<FdWarehouseCategory> util = new ExcelUtil<FdWarehouseCategory>(FdWarehouseCategory.class);
        util.exportExcel(response, list, "库房分类数据");
    }

    @PreAuthorize("@ss.hasPermi('foundation:warehouseCategory:import')")
    @PostMapping("/importValidate")
    public AjaxResult importValidate(MultipartFile file, boolean updateSupport) throws Exception
    {
        ExcelUtil<FdWarehouseCategory> util = new ExcelUtil<FdWarehouseCategory>(FdWarehouseCategory.class);
        List<FdWarehouseCategory> list = util.importExcel(file.getInputStream());
        Map<String, Object> data = fdWarehouseCategoryService.validateWarehouseCategoryImport(list, updateSupport);
        if (!Boolean.TRUE.equals(data.get("valid")))
        {
            return AjaxResult.success("校验未通过", data);
        }
        return AjaxResult.success("校验通过，请确认后导入", data);
    }

    @Log(title = "库房分类导入", businessType = BusinessType.IMPORT)
    @PreAuthorize("@ss.hasPermi('foundation:warehouseCategory:import')")
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file, boolean updateSupport,
        @RequestParam(value = "confirm", defaultValue = "false") boolean confirm) throws Exception
    {
        ExcelUtil<FdWarehouseCategory> util = new ExcelUtil<FdWarehouseCategory>(FdWarehouseCategory.class);
        List<FdWarehouseCategory> list = util.importExcel(file.getInputStream());
        String message = fdWarehouseCategoryService.importWarehouseCategory(list, updateSupport, getUsername(), confirm);
        java.util.Map<String, Object> preview = new LinkedHashMap<>();
        preview.put("previewRows", ExcelUtil.buildImportPreviewMaps(FdWarehouseCategory.class, list));
        return AjaxResult.success(message, preview);
    }

    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) throws Exception
    {
        ExcelUtil<FdWarehouseCategory> util = new ExcelUtil<FdWarehouseCategory>(FdWarehouseCategory.class);
        util.importTemplateExcel(response, "库房分类数据");
    }

    @PreAuthorize("@ss.hasPermi('foundation:warehouseCategory:import')")
    @PostMapping("/importAddValidate")
    public AjaxResult importAddValidate(MultipartFile file) throws Exception
    {
        return importValidate(file, false);
    }

    @Log(title = "库房分类新增导入", businessType = BusinessType.IMPORT)
    @PreAuthorize("@ss.hasPermi('foundation:warehouseCategory:import')")
    @PostMapping("/importAddData")
    public AjaxResult importAddData(MultipartFile file,
        @RequestParam(value = "confirm", defaultValue = "false") boolean confirm) throws Exception
    {
        return importData(file, false, confirm);
    }

    @PostMapping("/importAddTemplate")
    public void importAddTemplate(HttpServletResponse response) throws Exception
    {
        importTemplate(response);
    }

    @PreAuthorize("@ss.hasPermi('foundation:warehouseCategory:import')")
    @PostMapping("/importUpdateValidate")
    public AjaxResult importUpdateValidate(MultipartFile file) throws Exception
    {
        ExcelUtil<WarehouseCategoryImportUpdateDto> util = new ExcelUtil<WarehouseCategoryImportUpdateDto>(WarehouseCategoryImportUpdateDto.class);
        List<WarehouseCategoryImportUpdateDto> list = util.importExcel(file.getInputStream());
        Map<String, Object> data = validateWarehouseCategoryUpdateRows(list);
        if (!Boolean.TRUE.equals(data.get("valid")))
        {
            return AjaxResult.success("校验未通过", data);
        }
        return AjaxResult.success("校验通过，请确认后导入", data);
    }

    @Log(title = "库房分类更新导入", businessType = BusinessType.IMPORT)
    @PreAuthorize("@ss.hasPermi('foundation:warehouseCategory:import')")
    @PostMapping("/importUpdateData")
    public AjaxResult importUpdateData(MultipartFile file,
        @RequestParam(value = "confirm", defaultValue = "false") boolean confirm) throws Exception
    {
        if (!confirm)
        {
            return AjaxResult.error("请先完成校验并在确认后再导入");
        }
        ExcelUtil<WarehouseCategoryImportUpdateDto> util = new ExcelUtil<WarehouseCategoryImportUpdateDto>(WarehouseCategoryImportUpdateDto.class);
        List<WarehouseCategoryImportUpdateDto> list = util.importExcel(file.getInputStream());
        Map<String, Object> data = validateWarehouseCategoryUpdateRows(list);
        if (!Boolean.TRUE.equals(data.get("valid")))
        {
            return AjaxResult.error("数据校验未通过：" + String.valueOf(data.get("errors")));
        }
        int successNum = 0;
        StringBuilder msg = new StringBuilder();
        for (WarehouseCategoryImportUpdateDto row : list)
        {
            if (row == null || row.getWarehouseCategoryId() == null)
            {
                continue;
            }
            FdWarehouseCategory existing = fdWarehouseCategoryService.selectFdWarehouseCategoryByWarehouseCategoryId(row.getWarehouseCategoryId());
            existing.setWarehouseCategoryName(row.getWarehouseCategoryName().trim());
            existing.setReferredName(PinyinUtils.getPinyinInitials(existing.getWarehouseCategoryName()));
            existing.setUpdateBy(getUsername());
            fdWarehouseCategoryService.updateFdWarehouseCategory(existing);
            successNum++;
            msg.append("<br/>").append(successNum).append("、库房分类 ").append(existing.getWarehouseCategoryName()).append(" 更新成功");
        }
        msg.insert(0, "更新导入完成。共处理 " + successNum + " 条，明细如下：");
        for (WarehouseCategoryImportUpdateDto row : list)
        {
            if (row != null && row.getWarehouseCategoryId() != null)
            {
                row.setValidationResult("更新成功");
            }
        }
        java.util.Map<String, Object> preview = new LinkedHashMap<>();
        preview.put("previewRows", ExcelUtil.buildImportPreviewMaps(WarehouseCategoryImportUpdateDto.class, list));
        return AjaxResult.success(msg.toString(), preview);
    }

    @PostMapping("/importUpdateTemplate")
    public void importUpdateTemplate(HttpServletResponse response) throws Exception
    {
        ExcelUtil<WarehouseCategoryImportUpdateDto> util = new ExcelUtil<WarehouseCategoryImportUpdateDto>(WarehouseCategoryImportUpdateDto.class);
        util.importTemplateExcel(response, "库房分类更新导入模板");
    }

    private Map<String, Object> validateWarehouseCategoryUpdateRows(List<WarehouseCategoryImportUpdateDto> list)
    {
        clearWarehouseCategoryUpdateDtoValidation(list);
        Map<String, Object> result = new LinkedHashMap<>();
        ImportRowErrorCollector c = new ImportRowErrorCollector();
        if (list == null || list.isEmpty())
        {
            c.addGlobal("导入数据不能为空");
        }
        else
        {
            for (int i = 0; i < list.size(); i++)
            {
                WarehouseCategoryImportUpdateDto row = list.get(i);
                int excelRow = i + 2;
                if (row == null || row.getWarehouseCategoryId() == null)
                {
                    c.addRow(excelRow, "主键库房分类ID不能为空");
                    continue;
                }
                FdWarehouseCategory existing = fdWarehouseCategoryService.selectFdWarehouseCategoryByWarehouseCategoryId(row.getWarehouseCategoryId());
                if (existing == null)
                {
                    c.addRow(excelRow, "主键库房分类ID=" + row.getWarehouseCategoryId() + " 在当前租户下不存在");
                    continue;
                }
                if (StringUtils.isEmpty(row.getWarehouseCategoryName()) || StringUtils.isEmpty(row.getWarehouseCategoryName().trim()))
                {
                    c.addRow(excelRow, "库房分类名称不能为空");
                }
            }
        }
        List<String> errors = c.getAllErrors();
        boolean valid = errors.isEmpty();
        result.put("valid", valid);
        result.put("errors", errors);
        result.put("totalRows", list == null ? 0 : list.size());
        fillWarehouseCategoryUpdateValidationTexts(list, c, valid);
        result.put("previewRows", ExcelUtil.buildImportPreviewMaps(WarehouseCategoryImportUpdateDto.class, list));
        return result;
    }

    private void clearWarehouseCategoryUpdateDtoValidation(List<WarehouseCategoryImportUpdateDto> list)
    {
        if (list == null)
        {
            return;
        }
        for (WarehouseCategoryImportUpdateDto row : list)
        {
            if (row != null)
            {
                row.setValidationResult(null);
            }
        }
    }

    private void fillWarehouseCategoryUpdateValidationTexts(List<WarehouseCategoryImportUpdateDto> list, ImportRowErrorCollector c, boolean fileValid)
    {
        if (list == null)
        {
            return;
        }
        for (int i = 0; i < list.size(); i++)
        {
            int excelRow = i + 2;
            WarehouseCategoryImportUpdateDto row = list.get(i);
            if (row == null)
            {
                continue;
            }
            if (row.getWarehouseCategoryId() == null && StringUtils.isEmpty(row.getWarehouseCategoryName()))
            {
                row.setValidationResult("空行（已跳过）");
                continue;
            }
            java.util.List<String> msgs = c.getRowMessages(excelRow);
            if (!msgs.isEmpty())
            {
                row.setValidationResult(String.join("；", msgs));
            }
            else if (fileValid)
            {
                row.setValidationResult("校验通过");
            }
            else
            {
                row.setValidationResult("本行未单独报错；文件因其他数据未通过校验");
            }
        }
    }

    /**
     * 获取库房分类详细信息
     */
    @PreAuthorize("@ss.hasPermi('foundation:warehouseCategory:query')")
    @GetMapping(value = "/{warehouseCategoryId}")
    public AjaxResult getInfo(@PathVariable("warehouseCategoryId") Long warehouseCategoryId)
    {
        return success(fdWarehouseCategoryService.selectFdWarehouseCategoryByWarehouseCategoryId(warehouseCategoryId));
    }

    /**
     * 新增库房分类
     */
    @PreAuthorize("@ss.hasPermi('foundation:warehouseCategory:add')")
    @Log(title = "库房分类", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody FdWarehouseCategory fdWarehouseCategory)
    {
        if (TenantEnum.HS_003 != TenantEnum.fromCustomerId(SecurityUtils.getCustomerId()))
        {
            fdWarehouseCategory.setHisId(null);
        }
        return toAjax(fdWarehouseCategoryService.insertFdWarehouseCategory(fdWarehouseCategory));
    }

    /**
     * 修改库房分类
     */
    @PreAuthorize("@ss.hasPermi('foundation:warehouseCategory:edit')")
    @Log(title = "库房分类", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody FdWarehouseCategory fdWarehouseCategory)
    {
        return toAjax(fdWarehouseCategoryService.updateFdWarehouseCategory(fdWarehouseCategory));
    }

    /**
     * 删除库房分类
     */
    @PreAuthorize("@ss.hasPermi('foundation:warehouseCategory:remove')")
    @Log(title = "库房分类", businessType = BusinessType.DELETE)
	@DeleteMapping("/{warehouseCategoryIds}")
    public AjaxResult remove(@PathVariable Long warehouseCategoryIds)
    {
        return toAjax(fdWarehouseCategoryService.deleteFdWarehouseCategoryByWarehouseCategoryIds(warehouseCategoryIds));
    }

    /**
     * 批量更新库房分类名称简码
     */
    @PreAuthorize("@ss.hasPermi('foundation:warehouseCategory:updateReferred')")
    @Log(title = "库房分类", businessType = BusinessType.UPDATE)
    @PostMapping("/updateReferred")
    public AjaxResult updateReferred(@RequestBody java.util.Map<String, java.util.List<Long>> body)
    {
        java.util.List<Long> ids = body.get("ids");
        if (ids == null || ids.isEmpty())
        {
            return error("ID 列表不能为空");
        }
        fdWarehouseCategoryService.updateReferred(ids);
        return success("更新简码成功");
    }
}

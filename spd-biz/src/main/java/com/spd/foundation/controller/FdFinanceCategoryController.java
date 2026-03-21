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
import com.spd.foundation.domain.FdFinanceCategory;
import com.spd.foundation.dto.FinanceCategoryImportUpdateDto;
import com.spd.foundation.service.IFdFinanceCategoryService;
import com.spd.common.utils.poi.ExcelUtil;
import com.spd.common.utils.poi.ImportRowErrorCollector;
import com.spd.common.core.page.TableDataInfo;

/**
 * 财务分类维护Controller
 *
 * @author spd
 * @date 2024-03-04
 */
@RestController
@RequestMapping("/foundation/financeCategory")
public class FdFinanceCategoryController extends BaseController
{
    @Autowired
    private IFdFinanceCategoryService fdFinanceCategoryService;

    /**
     * 查询财务分类维护列表
     */
    @PreAuthorize("@ss.hasPermi('foundation:financeCategory:list')")
    @GetMapping("/list")
    public TableDataInfo list(FdFinanceCategory fdFinanceCategory)
    {
        startPage();
        List<FdFinanceCategory> list = fdFinanceCategoryService.selectFdFinanceCategoryList(fdFinanceCategory);
        return getDataTable(list);
    }

    /**
     * 查询所有财务分类维护列表
     */
    @GetMapping("/listAll")
    public List<FdFinanceCategory> listAll(FdFinanceCategory fdFinanceCategory)
    {
        List<FdFinanceCategory> list = fdFinanceCategoryService.selectFdFinanceCategoryList(fdFinanceCategory);
        return list;
    }

    /**
     * 导出财务分类维护列表
     */
    @PreAuthorize("@ss.hasPermi('foundation:financeCategory:export')")
    @Log(title = "财务分类维护", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, FdFinanceCategory fdFinanceCategory)
    {
        List<FdFinanceCategory> list = fdFinanceCategoryService.selectFdFinanceCategoryList(fdFinanceCategory);
        ExcelUtil<FdFinanceCategory> util = new ExcelUtil<FdFinanceCategory>(FdFinanceCategory.class);
        util.exportExcel(response, list, "财务分类维护数据");
    }

    @PreAuthorize("@ss.hasPermi('foundation:financeCategory:import')")
    @PostMapping("/importValidate")
    public AjaxResult importValidate(MultipartFile file, boolean updateSupport) throws Exception
    {
        ExcelUtil<FdFinanceCategory> util = new ExcelUtil<FdFinanceCategory>(FdFinanceCategory.class);
        List<FdFinanceCategory> list = util.importExcel(file.getInputStream());
        Map<String, Object> data = fdFinanceCategoryService.validateFinanceCategoryImport(list, updateSupport);
        if (!Boolean.TRUE.equals(data.get("valid")))
        {
            return AjaxResult.success("校验未通过", data);
        }
        return AjaxResult.success("校验通过，请确认后导入", data);
    }

    @Log(title = "财务分类导入", businessType = BusinessType.IMPORT)
    @PreAuthorize("@ss.hasPermi('foundation:financeCategory:import')")
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file, boolean updateSupport,
        @RequestParam(value = "confirm", defaultValue = "false") boolean confirm) throws Exception
    {
        ExcelUtil<FdFinanceCategory> util = new ExcelUtil<FdFinanceCategory>(FdFinanceCategory.class);
        List<FdFinanceCategory> list = util.importExcel(file.getInputStream());
        String message = fdFinanceCategoryService.importFinanceCategory(list, updateSupport, getUsername(), confirm);
        java.util.Map<String, Object> preview = new LinkedHashMap<>();
        preview.put("previewRows", ExcelUtil.buildImportPreviewMaps(FdFinanceCategory.class, list));
        return AjaxResult.success(message, preview);
    }

    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) throws Exception
    {
        ExcelUtil<FdFinanceCategory> util = new ExcelUtil<FdFinanceCategory>(FdFinanceCategory.class);
        util.importTemplateExcel(response, "财务分类维护数据");
    }

    @PreAuthorize("@ss.hasPermi('foundation:financeCategory:import')")
    @PostMapping("/importAddValidate")
    public AjaxResult importAddValidate(MultipartFile file) throws Exception
    {
        return importValidate(file, false);
    }

    @Log(title = "财务分类新增导入", businessType = BusinessType.IMPORT)
    @PreAuthorize("@ss.hasPermi('foundation:financeCategory:import')")
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

    @PreAuthorize("@ss.hasPermi('foundation:financeCategory:import')")
    @PostMapping("/importUpdateValidate")
    public AjaxResult importUpdateValidate(MultipartFile file) throws Exception
    {
        ExcelUtil<FinanceCategoryImportUpdateDto> util = new ExcelUtil<FinanceCategoryImportUpdateDto>(FinanceCategoryImportUpdateDto.class);
        List<FinanceCategoryImportUpdateDto> list = util.importExcel(file.getInputStream());
        Map<String, Object> data = validateFinanceCategoryUpdateRows(list);
        if (!Boolean.TRUE.equals(data.get("valid")))
        {
            return AjaxResult.success("校验未通过", data);
        }
        return AjaxResult.success("校验通过，请确认后导入", data);
    }

    @Log(title = "财务分类更新导入", businessType = BusinessType.IMPORT)
    @PreAuthorize("@ss.hasPermi('foundation:financeCategory:import')")
    @PostMapping("/importUpdateData")
    public AjaxResult importUpdateData(MultipartFile file,
        @RequestParam(value = "confirm", defaultValue = "false") boolean confirm) throws Exception
    {
        if (!confirm)
        {
            return AjaxResult.error("请先完成校验并在确认后再导入");
        }
        ExcelUtil<FinanceCategoryImportUpdateDto> util = new ExcelUtil<FinanceCategoryImportUpdateDto>(FinanceCategoryImportUpdateDto.class);
        List<FinanceCategoryImportUpdateDto> list = util.importExcel(file.getInputStream());
        Map<String, Object> data = validateFinanceCategoryUpdateRows(list);
        if (!Boolean.TRUE.equals(data.get("valid")))
        {
            return AjaxResult.error("数据校验未通过：" + String.valueOf(data.get("errors")));
        }
        int successNum = 0;
        StringBuilder msg = new StringBuilder();
        for (FinanceCategoryImportUpdateDto row : list)
        {
            if (row == null || row.getFinanceCategoryId() == null)
            {
                continue;
            }
            FdFinanceCategory existing = fdFinanceCategoryService.selectFdFinanceCategoryByFinanceCategoryId(row.getFinanceCategoryId());
            existing.setFinanceCategoryName(row.getFinanceCategoryName().trim());
            existing.setReferredName(PinyinUtils.getPinyinInitials(existing.getFinanceCategoryName()));
            existing.setUpdateBy(getUsername());
            fdFinanceCategoryService.updateFdFinanceCategory(existing);
            successNum++;
            msg.append("<br/>").append(successNum).append("、财务分类 ").append(existing.getFinanceCategoryName()).append(" 更新成功");
        }
        msg.insert(0, "更新导入完成。共处理 " + successNum + " 条，明细如下：");
        for (FinanceCategoryImportUpdateDto row : list)
        {
            if (row != null && row.getFinanceCategoryId() != null)
            {
                row.setValidationResult("更新成功");
            }
        }
        java.util.Map<String, Object> preview = new LinkedHashMap<>();
        preview.put("previewRows", ExcelUtil.buildImportPreviewMaps(FinanceCategoryImportUpdateDto.class, list));
        return AjaxResult.success(msg.toString(), preview);
    }

    @PostMapping("/importUpdateTemplate")
    public void importUpdateTemplate(HttpServletResponse response) throws Exception
    {
        ExcelUtil<FinanceCategoryImportUpdateDto> util = new ExcelUtil<FinanceCategoryImportUpdateDto>(FinanceCategoryImportUpdateDto.class);
        util.importTemplateExcel(response, "财务分类更新导入模板");
    }

    private Map<String, Object> validateFinanceCategoryUpdateRows(List<FinanceCategoryImportUpdateDto> list)
    {
        clearFinanceCategoryUpdateDtoValidation(list);
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
                FinanceCategoryImportUpdateDto row = list.get(i);
                int excelRow = i + 2;
                if (row == null || row.getFinanceCategoryId() == null)
                {
                    c.addRow(excelRow, "主键财务分类ID不能为空");
                    continue;
                }
                FdFinanceCategory existing = fdFinanceCategoryService.selectFdFinanceCategoryByFinanceCategoryId(row.getFinanceCategoryId());
                if (existing == null)
                {
                    c.addRow(excelRow, "主键财务分类ID=" + row.getFinanceCategoryId() + " 在当前租户下不存在");
                    continue;
                }
                if (StringUtils.isEmpty(row.getFinanceCategoryName()) || StringUtils.isEmpty(row.getFinanceCategoryName().trim()))
                {
                    c.addRow(excelRow, "财务分类名称不能为空");
                }
            }
        }
        List<String> errors = c.getAllErrors();
        boolean valid = errors.isEmpty();
        result.put("valid", valid);
        result.put("errors", errors);
        result.put("totalRows", list == null ? 0 : list.size());
        fillFinanceCategoryUpdateValidationTexts(list, c, valid);
        result.put("previewRows", ExcelUtil.buildImportPreviewMaps(FinanceCategoryImportUpdateDto.class, list));
        return result;
    }

    private void clearFinanceCategoryUpdateDtoValidation(List<FinanceCategoryImportUpdateDto> list)
    {
        if (list == null)
        {
            return;
        }
        for (FinanceCategoryImportUpdateDto row : list)
        {
            if (row != null)
            {
                row.setValidationResult(null);
            }
        }
    }

    private void fillFinanceCategoryUpdateValidationTexts(List<FinanceCategoryImportUpdateDto> list, ImportRowErrorCollector c, boolean fileValid)
    {
        if (list == null)
        {
            return;
        }
        for (int i = 0; i < list.size(); i++)
        {
            int excelRow = i + 2;
            FinanceCategoryImportUpdateDto row = list.get(i);
            if (row == null)
            {
                continue;
            }
            if (row.getFinanceCategoryId() == null && StringUtils.isEmpty(row.getFinanceCategoryName()))
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
     * 获取财务分类维护详细信息
     */
    @PreAuthorize("@ss.hasPermi('foundation:financeCategory:query')")
    @GetMapping(value = "/{financeCategoryId}")
    public AjaxResult getInfo(@PathVariable("financeCategoryId") Long financeCategoryId)
    {
        return success(fdFinanceCategoryService.selectFdFinanceCategoryByFinanceCategoryId(financeCategoryId));
    }

    /**
     * 新增财务分类维护
     */
    @PreAuthorize("@ss.hasPermi('foundation:financeCategory:add')")
    @Log(title = "财务分类维护", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody FdFinanceCategory fdFinanceCategory)
    {
        if (TenantEnum.HS_003 != TenantEnum.fromCustomerId(SecurityUtils.getCustomerId()))
        {
            fdFinanceCategory.setHisId(null);
        }
        return toAjax(fdFinanceCategoryService.insertFdFinanceCategory(fdFinanceCategory));
    }

    /**
     * 修改财务分类维护
     */
    @PreAuthorize("@ss.hasPermi('foundation:financeCategory:edit')")
    @Log(title = "财务分类维护", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody FdFinanceCategory fdFinanceCategory)
    {
        return toAjax(fdFinanceCategoryService.updateFdFinanceCategory(fdFinanceCategory));
    }

    /**
     * 删除财务分类维护
     */
    @PreAuthorize("@ss.hasPermi('foundation:financeCategory:remove')")
    @Log(title = "财务分类维护", businessType = BusinessType.DELETE)
	@DeleteMapping("/{financeCategoryIds}")
    public AjaxResult remove(@PathVariable Long financeCategoryIds)
    {
        return toAjax(fdFinanceCategoryService.deleteFdFinanceCategoryByFinanceCategoryId(financeCategoryIds));
    }

    /**
     * 批量更新财务分类名称简码
     */
    @PreAuthorize("@ss.hasPermi('foundation:financeCategory:updateReferred')")
    @Log(title = "财务分类维护", businessType = BusinessType.UPDATE)
    @PostMapping("/updateReferred")
    public AjaxResult updateReferred(@RequestBody java.util.Map<String, java.util.List<Long>> body)
    {
        java.util.List<Long> ids = body.get("ids");
        if (ids == null || ids.isEmpty())
        {
            return error("ID 列表不能为空");
        }
        fdFinanceCategoryService.updateReferred(ids);
        return success("更新简码成功");
    }
}

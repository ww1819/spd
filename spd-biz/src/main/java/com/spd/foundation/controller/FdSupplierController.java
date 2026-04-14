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
import com.spd.foundation.domain.FdSupplier;
import com.spd.foundation.domain.FdSupplierChangeLog;
import com.spd.foundation.dto.SupplierImportUpdateDto;
import com.spd.foundation.service.IFdSupplierService;
import com.spd.common.utils.poi.ExcelUtil;
import com.spd.common.utils.poi.ImportRowErrorCollector;
import com.spd.common.core.page.TableDataInfo;

/**
 * 供应商Controller
 *
 * @author spd
 * @date 2023-12-05
 */
@RestController
@RequestMapping("/foundation/supplier")
public class FdSupplierController extends BaseController
{
    @Autowired
    private IFdSupplierService fdSupplierService;

    /**
     * 查询供应商列表
     */
    @PreAuthorize("@ss.hasPermi('foundation:supplier:list')")
    @GetMapping("/list")
    public TableDataInfo list(FdSupplier fdSupplier)
    {
        startPage();
        List<FdSupplier> list = fdSupplierService.selectFdSupplierList(fdSupplier);
        return getDataTable(list);
    }

    /**
     * 查询所有供应商列表
     */
    @GetMapping("/listAll")
    public List<FdSupplier> listAll(FdSupplier fdSupplier)
    {
        List<FdSupplier> suppliers = fdSupplierService.selectFdSupplierList(fdSupplier);
        return suppliers;
    }

    /**
     * 科室模块专用供应商低敏列表（仅必要字段，避免返回完整供应商信息）
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/listDeptSafe")
    public List<Map<String, Object>> listDeptSafe(@RequestParam(value = "name", required = false) String name)
    {
        FdSupplier query = new FdSupplier();
        if (StringUtils.isNotEmpty(name))
        {
            query.setName(name.trim());
        }
        List<FdSupplier> suppliers = fdSupplierService.selectFdSupplierList(query);
        List<Map<String, Object>> safeList = new ArrayList<>();
        for (FdSupplier supplier : suppliers)
        {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", supplier.getId());
            item.put("name", supplier.getName());
            item.put("code", supplier.getCode());
            item.put("referredCode", supplier.getReferredCode());
            safeList.add(item);
        }
        return safeList;
    }

    /**
     * 导出供应商列表
     */
    @PreAuthorize("@ss.hasPermi('foundation:supplier:export')")
    @Log(title = "供应商", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, FdSupplier fdSupplier)
    {
        List<FdSupplier> list = fdSupplierService.selectFdSupplierList(fdSupplier);
        ExcelUtil<FdSupplier> util = new ExcelUtil<FdSupplier>(FdSupplier.class);
        util.exportExcel(response, list, "供应商数据");
    }

    /**
     * 供应商字段变更记录
     */
    @PreAuthorize("@ss.hasPermi('foundation:supplier:list')")
    @GetMapping("/changeLog/{supplierId}")
    public AjaxResult supplierChangeLog(@PathVariable("supplierId") Long supplierId)
    {
        if (supplierId == null)
        {
            return error("供应商 id 无效");
        }
        FdSupplier s = fdSupplierService.selectFdSupplierById(supplierId);
        if (s == null)
        {
            return error("供应商不存在");
        }
        List<FdSupplierChangeLog> logs = fdSupplierService.selectSupplierChangeLog(supplierId);
        return success(logs);
    }

    /**
     * 供应商导入：仅校验（不落库）
     */
    @PreAuthorize("@ss.hasPermi('foundation:supplier:import')")
    @PostMapping("/importValidate")
    public AjaxResult importValidate(MultipartFile file, boolean updateSupport) throws Exception
    {
        ExcelUtil<FdSupplier> util = new ExcelUtil<FdSupplier>(FdSupplier.class);
        List<FdSupplier> list = util.importExcel(file.getInputStream());
        Map<String, Object> data = fdSupplierService.validateFdSupplierImport(list, updateSupport);
        if (!Boolean.TRUE.equals(data.get("valid")))
        {
            return AjaxResult.success("校验未通过", data);
        }
        return AjaxResult.success("校验通过，请确认后导入", data);
    }

    /**
     * 供应商 Excel 导入（须先 importValidate 通过，且 confirm=true）
     */
    @Log(title = "供应商导入", businessType = BusinessType.IMPORT)
    @PreAuthorize("@ss.hasPermi('foundation:supplier:import')")
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file, boolean updateSupport,
        @RequestParam(value = "confirm", defaultValue = "false") boolean confirm) throws Exception
    {
        ExcelUtil<FdSupplier> util = new ExcelUtil<FdSupplier>(FdSupplier.class);
        List<FdSupplier> list = util.importExcel(file.getInputStream());
        String operName = getUsername();
        String message = fdSupplierService.importFdSupplier(list, updateSupport, operName, confirm);
        return AjaxResult.success(message, ExcelUtil.buildImportCommitSummaryMap(list != null ? list.size() : 0));
    }

    /**
     * 供应商导入模板下载
     */
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) throws Exception
    {
        ExcelUtil<FdSupplier> util = new ExcelUtil<FdSupplier>(FdSupplier.class);
        util.importTemplateExcel(response, "供应商数据");
    }

    @PreAuthorize("@ss.hasPermi('foundation:supplier:import')")
    @PostMapping("/importAddValidate")
    public AjaxResult importAddValidate(MultipartFile file) throws Exception
    {
        return importValidate(file, false);
    }

    @Log(title = "供应商新增导入", businessType = BusinessType.IMPORT)
    @PreAuthorize("@ss.hasPermi('foundation:supplier:import')")
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

    @PreAuthorize("@ss.hasPermi('foundation:supplier:import')")
    @PostMapping("/importUpdateValidate")
    public AjaxResult importUpdateValidate(MultipartFile file) throws Exception
    {
        ExcelUtil<SupplierImportUpdateDto> util = new ExcelUtil<SupplierImportUpdateDto>(SupplierImportUpdateDto.class);
        List<SupplierImportUpdateDto> list = util.importExcel(file.getInputStream());
        Map<String, Object> data = validateSupplierUpdateRows(list);
        if (!Boolean.TRUE.equals(data.get("valid")))
        {
            return AjaxResult.success("校验未通过", data);
        }
        return AjaxResult.success("校验通过，请确认后导入", data);
    }

    @Log(title = "供应商更新导入", businessType = BusinessType.IMPORT)
    @PreAuthorize("@ss.hasPermi('foundation:supplier:import')")
    @PostMapping("/importUpdateData")
    public AjaxResult importUpdateData(MultipartFile file,
        @RequestParam(value = "confirm", defaultValue = "false") boolean confirm) throws Exception
    {
        if (!confirm)
        {
            return AjaxResult.error("请先完成校验并在确认后再导入");
        }
        ExcelUtil<SupplierImportUpdateDto> util = new ExcelUtil<SupplierImportUpdateDto>(SupplierImportUpdateDto.class);
        List<SupplierImportUpdateDto> list = util.importExcel(file.getInputStream());
        Map<String, Object> data = validateSupplierUpdateRows(list);
        if (!Boolean.TRUE.equals(data.get("valid")))
        {
            return AjaxResult.error("数据校验未通过：" + String.valueOf(data.get("errors")));
        }
        int successNum = 0;
        for (SupplierImportUpdateDto row : list)
        {
            if (row == null || row.getId() == null)
            {
                continue;
            }
            FdSupplier existing = fdSupplierService.selectFdSupplierById(row.getId());
            existing.setName(row.getName().trim());
            existing.setReferredCode(PinyinUtils.getPinyinInitials(existing.getName()));
            existing.setUpdateBy(getUsername());
            fdSupplierService.updateFdSupplier(existing);
            successNum++;
        }
        String shortMsg = "更新导入完成，共成功 " + successNum + " 条";
        return AjaxResult.success(shortMsg, ExcelUtil.buildImportCommitSummaryMap(successNum));
    }

    @PostMapping("/importUpdateTemplate")
    public void importUpdateTemplate(HttpServletResponse response) throws Exception
    {
        ExcelUtil<SupplierImportUpdateDto> util = new ExcelUtil<SupplierImportUpdateDto>(SupplierImportUpdateDto.class);
        util.importTemplateExcel(response, "供应商更新导入模板");
    }

    private Map<String, Object> validateSupplierUpdateRows(List<SupplierImportUpdateDto> list)
    {
        clearSupplierUpdateDtoValidation(list);
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
                SupplierImportUpdateDto row = list.get(i);
                int excelRow = i + 2;
                if (row == null || row.getId() == null)
                {
                    c.addRow(excelRow, "主键ID不能为空");
                    continue;
                }
                FdSupplier existing = fdSupplierService.selectFdSupplierById(row.getId());
                if (existing == null)
                {
                    c.addRow(excelRow, "主键ID=" + row.getId() + " 在当前租户下不存在");
                    continue;
                }
                if (StringUtils.isEmpty(row.getName()) || StringUtils.isEmpty(row.getName().trim()))
                {
                    c.addRow(excelRow, "供应商名称不能为空");
                }
            }
        }
        List<String> errors = c.getAllErrors();
        boolean valid = errors.isEmpty();
        result.put("valid", valid);
        result.put("errors", errors);
        result.put("totalRows", list == null ? 0 : list.size());
        fillSupplierUpdateValidationTexts(list, c, valid);
        result.put("previewRows", ExcelUtil.buildImportPreviewMaps(SupplierImportUpdateDto.class, list));
        return result;
    }

    private void clearSupplierUpdateDtoValidation(List<SupplierImportUpdateDto> list)
    {
        if (list == null)
        {
            return;
        }
        for (SupplierImportUpdateDto row : list)
        {
            if (row != null)
            {
                row.setValidationResult(null);
            }
        }
    }

    private void fillSupplierUpdateValidationTexts(List<SupplierImportUpdateDto> list, ImportRowErrorCollector c, boolean fileValid)
    {
        if (list == null)
        {
            return;
        }
        for (int i = 0; i < list.size(); i++)
        {
            int excelRow = i + 2;
            SupplierImportUpdateDto row = list.get(i);
            if (row == null)
            {
                continue;
            }
            if (row.getId() == null && StringUtils.isEmpty(row.getName()))
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
     * 获取供应商详细信息
     */
    @PreAuthorize("@ss.hasPermi('foundation:supplier:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(fdSupplierService.selectFdSupplierById(id));
    }

    /**
     * 新增供应商
     */
    @PreAuthorize("@ss.hasPermi('foundation:supplier:add')")
    @Log(title = "供应商", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody FdSupplier fdSupplier)
    {
        if (TenantEnum.HS_003 != TenantEnum.fromCustomerId(SecurityUtils.getCustomerId()))
        {
            fdSupplier.setHisId(null);
        }
        return toAjax(fdSupplierService.insertFdSupplier(fdSupplier));
    }

    /**
     * 修改供应商
     */
    @PreAuthorize("@ss.hasPermi('foundation:supplier:edit')")
    @Log(title = "供应商", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody FdSupplier fdSupplier)
    {
        return toAjax(fdSupplierService.updateFdSupplier(fdSupplier));
    }

    /**
     * 删除供应商
     */
    @PreAuthorize("@ss.hasPermi('foundation:supplier:remove')")
    @Log(title = "供应商", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long ids)
    {
        return toAjax(fdSupplierService.deleteFdSupplierById(ids));
    }

    /**
     * 批量更新供应商名称简码
     */
    @PreAuthorize("@ss.hasPermi('foundation:supplier:updateReferred')")
    @Log(title = "供应商", businessType = BusinessType.UPDATE)
    @PostMapping("/updateReferred")
    public AjaxResult updateReferred(@RequestBody java.util.Map<String, java.util.List<Long>> body)
    {
        java.util.List<Long> ids = body.get("ids");
        if (ids == null || ids.isEmpty())
        {
            return error("ID 列表不能为空");
        }
        fdSupplierService.updateReferred(ids);
        return success("更新简码成功");
    }
}

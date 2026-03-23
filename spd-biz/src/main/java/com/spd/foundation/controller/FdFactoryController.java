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
import com.spd.foundation.domain.FdFactory;
import com.spd.foundation.domain.FdFactoryChangeLog;
import com.spd.foundation.dto.FactoryImportUpdateDto;
import com.spd.foundation.service.IFdFactoryService;
import com.spd.common.utils.poi.ExcelUtil;
import com.spd.common.utils.poi.ImportRowErrorCollector;
import com.spd.common.core.page.TableDataInfo;

/**
 * 厂家维护Controller
 *
 * @author spd
 * @date 2024-03-04
 */
@RestController
@RequestMapping("/foundation/factory")
public class FdFactoryController extends BaseController
{
    @Autowired
    private IFdFactoryService fdFactoryService;

    @PreAuthorize("@ss.hasPermi('foundation:factory:list')")
    @GetMapping("/list")
    public TableDataInfo list(FdFactory fdFactory)
    {
        startPage();
        List<FdFactory> list = fdFactoryService.selectFdFactoryList(fdFactory);
        return getDataTable(list);
    }

    @GetMapping("/listAll")
    public List<FdFactory> listAll(FdFactory fdFactory)
    {
        List<FdFactory> list = fdFactoryService.selectFdFactoryList(fdFactory);
        return list;
    }

    @PreAuthorize("@ss.hasPermi('foundation:factory:export')")
    @Log(title = "厂家维护", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, FdFactory fdFactory)
    {
        List<FdFactory> list = fdFactoryService.selectFdFactoryList(fdFactory);
        ExcelUtil<FdFactory> util = new ExcelUtil<FdFactory>(FdFactory.class);
        util.exportExcel(response, list, "厂家维护数据");
    }

    @PreAuthorize("@ss.hasPermi('foundation:factory:list')")
    @GetMapping("/changeLog/{factoryId}")
    public AjaxResult factoryChangeLog(@PathVariable("factoryId") Long factoryId)
    {
        if (factoryId == null)
        {
            return error("生产厂家 id 无效");
        }
        FdFactory f = fdFactoryService.selectFdFactoryByFactoryId(factoryId);
        if (f == null)
        {
            return error("生产厂家不存在");
        }
        List<FdFactoryChangeLog> logs = fdFactoryService.selectFactoryChangeLog(factoryId);
        return success(logs);
    }

    @PreAuthorize("@ss.hasPermi('foundation:factory:import')")
    @PostMapping("/importValidate")
    public AjaxResult importValidate(MultipartFile file, boolean updateSupport) throws Exception
    {
        ExcelUtil<FdFactory> util = new ExcelUtil<FdFactory>(FdFactory.class);
        List<FdFactory> list = util.importExcel(file.getInputStream());
        Map<String, Object> data = fdFactoryService.validateFdFactoryImport(list, updateSupport);
        if (!Boolean.TRUE.equals(data.get("valid")))
        {
            return AjaxResult.success("校验未通过", data);
        }
        return AjaxResult.success("校验通过，请确认后导入", data);
    }

    @Log(title = "生产厂家导入", businessType = BusinessType.IMPORT)
    @PreAuthorize("@ss.hasPermi('foundation:factory:import')")
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file, boolean updateSupport,
        @RequestParam(value = "confirm", defaultValue = "false") boolean confirm) throws Exception
    {
        ExcelUtil<FdFactory> util = new ExcelUtil<FdFactory>(FdFactory.class);
        List<FdFactory> list = util.importExcel(file.getInputStream());
        String operName = getUsername();
        String message = fdFactoryService.importFdFactory(list, updateSupport, operName, confirm);
        return AjaxResult.success(message, ExcelUtil.buildImportCommitSummaryMap(list != null ? list.size() : 0));
    }

    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) throws Exception
    {
        ExcelUtil<FdFactory> util = new ExcelUtil<FdFactory>(FdFactory.class);
        util.importTemplateExcel(response, "生产厂家数据");
    }

    @PreAuthorize("@ss.hasPermi('foundation:factory:import')")
    @PostMapping("/importAddValidate")
    public AjaxResult importAddValidate(MultipartFile file) throws Exception
    {
        return importValidate(file, false);
    }

    @Log(title = "生产厂家新增导入", businessType = BusinessType.IMPORT)
    @PreAuthorize("@ss.hasPermi('foundation:factory:import')")
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

    @PreAuthorize("@ss.hasPermi('foundation:factory:import')")
    @PostMapping("/importUpdateValidate")
    public AjaxResult importUpdateValidate(MultipartFile file) throws Exception
    {
        ExcelUtil<FactoryImportUpdateDto> util = new ExcelUtil<FactoryImportUpdateDto>(FactoryImportUpdateDto.class);
        List<FactoryImportUpdateDto> list = util.importExcel(file.getInputStream());
        Map<String, Object> data = validateFactoryUpdateRows(list);
        if (!Boolean.TRUE.equals(data.get("valid")))
        {
            return AjaxResult.success("校验未通过", data);
        }
        return AjaxResult.success("校验通过，请确认后导入", data);
    }

    @Log(title = "生产厂家更新导入", businessType = BusinessType.IMPORT)
    @PreAuthorize("@ss.hasPermi('foundation:factory:import')")
    @PostMapping("/importUpdateData")
    public AjaxResult importUpdateData(MultipartFile file,
        @RequestParam(value = "confirm", defaultValue = "false") boolean confirm) throws Exception
    {
        if (!confirm)
        {
            return AjaxResult.error("请先完成校验并在确认后再导入");
        }
        ExcelUtil<FactoryImportUpdateDto> util = new ExcelUtil<FactoryImportUpdateDto>(FactoryImportUpdateDto.class);
        List<FactoryImportUpdateDto> list = util.importExcel(file.getInputStream());
        Map<String, Object> data = validateFactoryUpdateRows(list);
        if (!Boolean.TRUE.equals(data.get("valid")))
        {
            return AjaxResult.error("数据校验未通过：" + String.valueOf(data.get("errors")));
        }
        int successNum = 0;
        for (FactoryImportUpdateDto row : list)
        {
            if (row == null || row.getFactoryId() == null)
            {
                continue;
            }
            FdFactory existing = fdFactoryService.selectFdFactoryByFactoryId(row.getFactoryId());
            existing.setFactoryName(row.getFactoryName().trim());
            existing.setFactoryReferredCode(PinyinUtils.getPinyinInitials(existing.getFactoryName()));
            existing.setUpdateBy(getUsername());
            fdFactoryService.updateFdFactory(existing);
            successNum++;
        }
        String shortMsg = "更新导入完成，共成功 " + successNum + " 条";
        return AjaxResult.success(shortMsg, ExcelUtil.buildImportCommitSummaryMap(successNum));
    }

    @PostMapping("/importUpdateTemplate")
    public void importUpdateTemplate(HttpServletResponse response) throws Exception
    {
        ExcelUtil<FactoryImportUpdateDto> util = new ExcelUtil<FactoryImportUpdateDto>(FactoryImportUpdateDto.class);
        util.importTemplateExcel(response, "生产厂家更新导入模板");
    }

    private Map<String, Object> validateFactoryUpdateRows(List<FactoryImportUpdateDto> list)
    {
        clearFactoryUpdateDtoValidation(list);
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
                FactoryImportUpdateDto row = list.get(i);
                int excelRow = i + 2;
                if (row == null || row.getFactoryId() == null)
                {
                    c.addRow(excelRow, "主键厂家ID不能为空");
                    continue;
                }
                FdFactory existing = fdFactoryService.selectFdFactoryByFactoryId(row.getFactoryId());
                if (existing == null)
                {
                    c.addRow(excelRow, "主键厂家ID=" + row.getFactoryId() + " 在当前租户下不存在");
                    continue;
                }
                if (StringUtils.isEmpty(row.getFactoryName()) || StringUtils.isEmpty(row.getFactoryName().trim()))
                {
                    c.addRow(excelRow, "厂家名称不能为空");
                }
            }
        }
        List<String> errors = c.getAllErrors();
        boolean valid = errors.isEmpty();
        result.put("valid", valid);
        result.put("errors", errors);
        result.put("totalRows", list == null ? 0 : list.size());
        fillFactoryUpdateValidationTexts(list, c, valid);
        result.put("previewRows", ExcelUtil.buildImportPreviewMaps(FactoryImportUpdateDto.class, list));
        return result;
    }

    private void clearFactoryUpdateDtoValidation(List<FactoryImportUpdateDto> list)
    {
        if (list == null)
        {
            return;
        }
        for (FactoryImportUpdateDto row : list)
        {
            if (row != null)
            {
                row.setValidationResult(null);
            }
        }
    }

    private void fillFactoryUpdateValidationTexts(List<FactoryImportUpdateDto> list, ImportRowErrorCollector c, boolean fileValid)
    {
        if (list == null)
        {
            return;
        }
        for (int i = 0; i < list.size(); i++)
        {
            int excelRow = i + 2;
            FactoryImportUpdateDto row = list.get(i);
            if (row == null)
            {
                continue;
            }
            if (row.getFactoryId() == null && StringUtils.isEmpty(row.getFactoryName()))
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

    @PreAuthorize("@ss.hasPermi('foundation:factory:query')")
    @GetMapping(value = "/{factoryId}")
    public AjaxResult getInfo(@PathVariable("factoryId") Long factoryId)
    {
        return success(fdFactoryService.selectFdFactoryByFactoryId(factoryId));
    }

    @PreAuthorize("@ss.hasPermi('foundation:factory:add')")
    @Log(title = "厂家维护", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody FdFactory fdFactory)
    {
        if (TenantEnum.HS_003 != TenantEnum.fromCustomerId(SecurityUtils.getCustomerId()))
        {
            fdFactory.setHisId(null);
        }
        return toAjax(fdFactoryService.insertFdFactory(fdFactory));
    }

    @PreAuthorize("@ss.hasPermi('foundation:factory:edit')")
    @Log(title = "厂家维护", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody FdFactory fdFactory)
    {
        return toAjax(fdFactoryService.updateFdFactory(fdFactory));
    }

    @PreAuthorize("@ss.hasPermi('foundation:factory:remove')")
    @Log(title = "厂家维护", businessType = BusinessType.DELETE)
	@DeleteMapping("/{factoryIds}")
    public AjaxResult remove(@PathVariable Long factoryIds)
    {
        return toAjax(fdFactoryService.deleteFdFactoryByFactoryId(factoryIds));
    }

    @PreAuthorize("@ss.hasPermi('foundation:factory:updateReferred')")
    @Log(title = "厂家维护", businessType = BusinessType.UPDATE)
    @PostMapping("/updateReferred")
    public AjaxResult updateReferred(@RequestBody java.util.Map<String, java.util.List<Long>> body)
    {
        java.util.List<Long> ids = body.get("ids");
        if (ids == null || ids.isEmpty())
        {
            return error("ID 列表不能为空");
        }
        fdFactoryService.updateReferred(ids);
        return success("更新简码成功");
    }
}

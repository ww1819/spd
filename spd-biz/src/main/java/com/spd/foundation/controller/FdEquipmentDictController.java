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
import com.spd.common.enums.BusinessType;
import com.spd.foundation.domain.FdEquipmentDict;
import com.spd.foundation.service.IFdEquipmentDictService;
import com.spd.common.utils.poi.ExcelUtil;
import com.spd.common.core.page.TableDataInfo;
import org.springframework.web.multipart.MultipartFile;

/**
 * 设备字典Controller
 *
 * @author spd
 * @date 2024-12-16
 */
@RestController
@RequestMapping("/foundation/equipmentDict")
public class FdEquipmentDictController extends BaseController
{
    @Autowired
    private IFdEquipmentDictService fdEquipmentDictService;

    /**
     * 查询设备字典列表
     */
    @PreAuthorize("@ss.hasPermi('foundation:equipmentDict:list')")
    @GetMapping("/list")
    public TableDataInfo list(FdEquipmentDict fdEquipmentDict)
    {
        startPage();
        List<FdEquipmentDict> list = fdEquipmentDictService.selectFdEquipmentDictList(fdEquipmentDict);
        return getDataTable(list);
    }

    /**
     * 查询所有设备字典列表
     */
    @GetMapping("/listAll")
    public List<FdEquipmentDict> listAll(FdEquipmentDict fdEquipmentDict)
    {
        List<FdEquipmentDict> list = fdEquipmentDictService.selectFdEquipmentDictList(fdEquipmentDict);
        return list;
    }

    /**
     * 导出设备字典列表
     */
    @PreAuthorize("@ss.hasPermi('foundation:equipmentDict:export')")
    @Log(title = "设备字典", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, FdEquipmentDict fdEquipmentDict)
    {
        List<FdEquipmentDict> list = fdEquipmentDictService.selectFdEquipmentDictList(fdEquipmentDict);
        ExcelUtil<FdEquipmentDict> util = new ExcelUtil<FdEquipmentDict>(FdEquipmentDict.class);
        util.exportExcel(response, list, "设备字典数据");
    }

    /**
     * 获取设备字典详细信息
     */
    @PreAuthorize("@ss.hasPermi('foundation:equipmentDict:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(fdEquipmentDictService.selectFdEquipmentDictById(id));
    }

    /**
     * 新增设备字典
     */
    @PreAuthorize("@ss.hasPermi('foundation:equipmentDict:add')")
    @Log(title = "设备字典", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody FdEquipmentDict fdEquipmentDict)
    {
        return toAjax(fdEquipmentDictService.insertFdEquipmentDict(fdEquipmentDict));
    }

    /**
     * 修改设备字典
     */
    @PreAuthorize("@ss.hasPermi('foundation:equipmentDict:edit')")
    @Log(title = "设备字典", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody FdEquipmentDict fdEquipmentDict)
    {
        return toAjax(fdEquipmentDictService.updateFdEquipmentDict(fdEquipmentDict));
    }

    /**
     * 删除设备字典
     */
    @PreAuthorize("@ss.hasPermi('foundation:equipmentDict:remove')")
    @Log(title = "设备字典", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long ids)
    {
        return toAjax(fdEquipmentDictService.deleteFdEquipmentDictByIds(ids));
    }

    @Log(title = "设备字典导入", businessType = BusinessType.IMPORT)
    @PreAuthorize("@ss.hasPermi('foundation:equipmentDict:import')")
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file, boolean updateSupport) throws Exception
    {
        ExcelUtil<FdEquipmentDict> util = new ExcelUtil<FdEquipmentDict>(FdEquipmentDict.class);
        List<FdEquipmentDict> fdEquipmentDictList = util.importExcel(file.getInputStream());
        String operName = getUsername();
        String message = fdEquipmentDictService.importFdEquipmentDict(fdEquipmentDictList, updateSupport, operName);
        return success(message);
    }

    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response)
    {
        ExcelUtil<FdEquipmentDict> util = new ExcelUtil<FdEquipmentDict>(FdEquipmentDict.class);
        util.importTemplateExcel(response, "设备数据");
    }
}


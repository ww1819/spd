package com.spd.equipment.controller;

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
import org.springframework.web.multipart.MultipartFile;
import com.spd.common.annotation.Log;
import com.spd.common.core.controller.BaseController;
import com.spd.common.core.domain.AjaxResult;
import com.spd.common.enums.BusinessType;
import com.spd.common.exception.ServiceException;
import com.spd.equipment.domain.EquipmentInfo;
import com.spd.equipment.service.IEquipmentInfoService;
import com.spd.common.utils.poi.ExcelUtil;
import com.spd.common.core.page.TableDataInfo;
import com.spd.common.utils.file.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 设备信息管理Controller
 * 
 * @author spd
 * @date 2024-01-01
 */
@RestController
@RequestMapping("/equipment/info")
public class EquipmentInfoController extends BaseController
{
    private static final Logger log = LoggerFactory.getLogger(EquipmentInfoController.class);
    
    @Autowired
    private IEquipmentInfoService equipmentInfoService;

    /**
     * 查询设备信息管理列表
     */
    @PreAuthorize("@ss.hasPermi('equipment:info:list')")
    @GetMapping("/list")
    public TableDataInfo list(EquipmentInfo equipmentInfo)
    {
        startPage();
        List<EquipmentInfo> list = equipmentInfoService.selectEquipmentInfoList(equipmentInfo);
        return getDataTable(list);
    }

    /**
     * 导出设备信息管理列表
     */
    @PreAuthorize("@ss.hasPermi('equipment:info:export')")
    @Log(title = "设备信息管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, EquipmentInfo equipmentInfo)
    {
        List<EquipmentInfo> list = equipmentInfoService.selectEquipmentInfoList(equipmentInfo);
        ExcelUtil<EquipmentInfo> util = new ExcelUtil<EquipmentInfo>(EquipmentInfo.class);
        util.exportExcel(response, list, "设备信息管理数据");
    }

    /**
     * 获取设备信息管理详细信息
     */
    @PreAuthorize("@ss.hasPermi('equipment:info:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id)
    {
        return success(equipmentInfoService.selectEquipmentInfoById(id));
    }

    /**
     * 根据资产编号查询设备信息
     */
    @PreAuthorize("@ss.hasPermi('equipment:info:query')")
    @GetMapping(value = "/assetCode/{assetCode}")
    public AjaxResult getInfoByAssetCode(@PathVariable("assetCode") String assetCode)
    {
        return success(equipmentInfoService.selectEquipmentInfoByAssetCode(assetCode));
    }

    /**
     * 新增设备信息管理
     */
    @PreAuthorize("@ss.hasPermi('equipment:info:add')")
    @Log(title = "设备信息管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody EquipmentInfo equipmentInfo)
    {
        return toAjax(equipmentInfoService.insertEquipmentInfo(equipmentInfo));
    }

    /**
     * 修改设备信息管理
     */
    @PreAuthorize("@ss.hasPermi('equipment:info:edit')")
    @Log(title = "设备信息管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody EquipmentInfo equipmentInfo)
    {
        return toAjax(equipmentInfoService.updateEquipmentInfo(equipmentInfo));
    }

    /**
     * 删除设备信息管理
     */
    @PreAuthorize("@ss.hasPermi('equipment:info:remove')")
    @Log(title = "设备信息管理", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable String[] ids)
    {
        return toAjax(equipmentInfoService.deleteEquipmentInfoByIds(ids));
    }

    /**
     * 查询设备信息统计
     */
    @PreAuthorize("@ss.hasPermi('equipment:info:statistics')")
    @GetMapping("/statistics")
    public AjaxResult statistics(EquipmentInfo equipmentInfo)
    {
        List<EquipmentInfo> list = equipmentInfoService.selectEquipmentInfoStatistics(equipmentInfo);
        return success(list);
    }

    /**
     * 导入资产数据
     */
    @Log(title = "设备信息管理", businessType = BusinessType.IMPORT)
    @PreAuthorize("@ss.hasPermi('equipment:info:import')")
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file, boolean updateSupport) throws Exception
    {
        try
        {
            if (file == null || file.isEmpty())
            {
                return error("导入文件不能为空！");
            }
            ExcelUtil<EquipmentInfo> util = new ExcelUtil<EquipmentInfo>(EquipmentInfo.class);
            List<EquipmentInfo> equipmentInfoList = util.importExcel(file.getInputStream());
            if (equipmentInfoList == null || equipmentInfoList.isEmpty())
            {
                return error("导入文件内容为空，请检查文件格式！\n" +
                           "请确保：\n" +
                           "1. 使用系统下载的模板文件（不要修改列名）\n" +
                           "2. 至少填写一行数据\n" +
                           "3. 数据填写在正确的列中");
            }
            
            // 记录导入的数据数量，用于调试
            log.info("导入数据条数：{}", equipmentInfoList.size());
            int emptyRowCount = 0;
            for (int i = 0; i < equipmentInfoList.size(); i++)
            {
                EquipmentInfo info = equipmentInfoList.get(i);
                if (info != null)
                {
                    boolean isEmpty = (info.getAssetCode() == null || info.getAssetCode().trim().isEmpty()) &&
                                     (info.getAssetName() == null || info.getAssetName().trim().isEmpty()) &&
                                     (info.getSpecification() == null || info.getSpecification().trim().isEmpty()) &&
                                     (info.getModel() == null || info.getModel().trim().isEmpty());
                    if (isEmpty)
                    {
                        emptyRowCount++;
                    }
                    log.debug("第{}条数据 - 资产编号：{}, 资产名称：{}, 规格：{}, 型号：{}", 
                            (i+1), info.getAssetCode(), info.getAssetName(), info.getSpecification(), info.getModel());
                }
                else
                {
                    emptyRowCount++;
                    log.warn("第{}条数据为null", (i+1));
                }
            }
            
            if (emptyRowCount == equipmentInfoList.size())
            {
                return error("所有数据行都为空！\n" +
                           "可能的原因：\n" +
                           "1. Excel文件的列名与模板不匹配（请使用系统下载的模板文件）\n" +
                           "2. 数据填写在错误的列中\n" +
                           "3. 数据格式不正确\n" +
                           "请检查Excel文件，确保列名与模板完全一致，并且至少填写资产编号、资产名称、规格或型号中的一项。");
            }
            String operName = getUsername();
            if (operName == null || operName.isEmpty())
            {
                operName = "system";
            }
            String message = equipmentInfoService.importEquipmentInfo(equipmentInfoList, updateSupport, operName);
            return success(message);
        }
        catch (ServiceException e)
        {
            // ServiceException直接返回错误信息
            return error(e.getMessage());
        }
        catch (Exception e)
        {
            // 记录详细异常信息
            e.printStackTrace();
            String errorMsg = e.getMessage();
            if (errorMsg == null || errorMsg.isEmpty())
            {
                errorMsg = e.getClass().getSimpleName();
                if (e.getCause() != null)
                {
                    errorMsg += ": " + e.getCause().getMessage();
                }
            }
            return error("导入失败：" + errorMsg);
        }
    }

    /**
     * 下载导入模板
     */
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) throws Exception
    {
        ExcelUtil<EquipmentInfo> util = new ExcelUtil<EquipmentInfo>(EquipmentInfo.class);
        // 设置文件名响应头
        FileUtils.setAttachmentResponseHeader(response, "设备固定资产导入模板.xlsx");
        util.importTemplateExcel(response, "资产数据");
    }
} 
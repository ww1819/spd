package com.spd.equipment.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
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
import com.spd.common.core.page.TableDataInfo;
import com.spd.common.utils.file.FileUtils;
import com.spd.common.config.SPDConfig;
import com.spd.equipment.domain.EquipmentFile;
import com.spd.equipment.service.IEquipmentFileService;

/**
 * 设备文件管理Controller
 * 
 * @author spd
 * @date 2024-01-01
 */
@RestController
@RequestMapping("/equipment/file")
public class EquipmentFileController extends BaseController
{
    @Autowired
    private IEquipmentFileService equipmentFileService;

    /**
     * 查询设备文件列表
     */
    @PreAuthorize("@ss.hasPermi('equipment:file:list')")
    @GetMapping("/list")
    public TableDataInfo list(EquipmentFile equipmentFile)
    {
        startPage();
        List<EquipmentFile> list = equipmentFileService.selectEquipmentFileList(equipmentFile);
        return getDataTable(list);
    }

    /**
     * 获取设备文件详细信息
     */
    @PreAuthorize("@ss.hasPermi('equipment:file:query')")
    @GetMapping(value = "/{fileId}")
    public AjaxResult getInfo(@PathVariable("fileId") String fileId)
    {
        return success(equipmentFileService.selectEquipmentFileByFileId(fileId));
    }

    /**
     * 新增设备文件
     */
    @PreAuthorize("@ss.hasPermi('equipment:file:add')")
    @Log(title = "设备文件", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody EquipmentFile equipmentFile)
    {
        return toAjax(equipmentFileService.insertEquipmentFile(equipmentFile));
    }

    /**
     * 修改设备文件
     */
    @PreAuthorize("@ss.hasPermi('equipment:file:edit')")
    @Log(title = "设备文件", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody EquipmentFile equipmentFile)
    {
        return toAjax(equipmentFileService.updateEquipmentFile(equipmentFile));
    }

    /**
     * 删除设备文件
     */
    @PreAuthorize("@ss.hasPermi('equipment:file:remove')")
    @Log(title = "设备文件", businessType = BusinessType.DELETE)
	@DeleteMapping("/{fileIds}")
    public AjaxResult remove(@PathVariable String[] fileIds)
    {
        return toAjax(equipmentFileService.deleteEquipmentFileByFileIds(fileIds));
    }

    /**
     * 下载设备文件
     */
    @PreAuthorize("@ss.hasPermi('equipment:file:download')")
    @GetMapping("/download/{fileId}")
    public void download(@PathVariable("fileId") String fileId, HttpServletResponse response)
    {
        EquipmentFile equipmentFile = equipmentFileService.selectEquipmentFileByFileId(fileId);
        if (equipmentFile == null)
        {
            return;
        }
        
        String filePath = equipmentFile.getFilePath();
        if (filePath == null || filePath.isEmpty())
        {
            filePath = equipmentFile.getUrl();
        }
        
        if (filePath != null && !filePath.isEmpty())
        {
            try
            {
                String fileName = equipmentFile.getOriginalFilename();
                if (fileName == null || fileName.isEmpty())
                {
                    fileName = equipmentFile.getFileName();
                }
                
                // 如果filePath是相对路径，需要加上配置的路径前缀
                String fullPath = filePath;
                if (!filePath.startsWith("/") && !filePath.contains(":"))
                {
                    fullPath = SPDConfig.getProfile() + filePath;
                }
                
                response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
                FileUtils.setAttachmentResponseHeader(response, fileName);
                FileUtils.writeBytes(fullPath, response.getOutputStream());
            }
            catch (Exception e)
            {
                logger.error("下载文件失败", e);
            }
        }
    }
}

package com.spd.gz.controller;

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
import com.spd.gz.domain.GzPatientInfo;
import com.spd.gz.service.IGzPatientInfoService;
import com.spd.common.utils.poi.ExcelUtil;
import com.spd.common.core.page.TableDataInfo;

/**
 * 患者信息Controller
 *
 * @author spd
 * @date 2025-01-01
 */
@RestController
@RequestMapping("/gz/patient")
public class GzPatientInfoController extends BaseController
{
    @Autowired
    private IGzPatientInfoService gzPatientInfoService;

    /**
     * 查询患者信息列表
     */
    @PreAuthorize("@ss.hasPermi('gz:patient:list')")
    @GetMapping("/list")
    public TableDataInfo list(GzPatientInfo gzPatientInfo)
    {
        startPage();
        List<GzPatientInfo> list = gzPatientInfoService.selectGzPatientInfoList(gzPatientInfo);
        return getDataTable(list);
    }

    /**
     * 根据病历号查询患者信息
     */
    @GetMapping("/getByMedicalRecordNo/{medicalRecordNo}")
    public AjaxResult getByMedicalRecordNo(@PathVariable("medicalRecordNo") String medicalRecordNo)
    {
        GzPatientInfo patientInfo = gzPatientInfoService.selectGzPatientInfoByMedicalRecordNo(medicalRecordNo);
        if (patientInfo == null) {
            return AjaxResult.error("未找到该病历号对应的患者信息");
        }
        return AjaxResult.success(patientInfo);
    }

    /**
     * 导出患者信息列表
     */
    @PreAuthorize("@ss.hasPermi('gz:patient:export')")
    @Log(title = "患者信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, GzPatientInfo gzPatientInfo)
    {
        List<GzPatientInfo> list = gzPatientInfoService.selectGzPatientInfoList(gzPatientInfo);
        ExcelUtil<GzPatientInfo> util = new ExcelUtil<GzPatientInfo>(GzPatientInfo.class);
        util.exportExcel(response, list, "患者信息数据");
    }

    /**
     * 获取患者信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('gz:patient:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(gzPatientInfoService.selectGzPatientInfoById(id));
    }

    /**
     * 新增患者信息
     */
    @PreAuthorize("@ss.hasPermi('gz:patient:add')")
    @Log(title = "患者信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody GzPatientInfo gzPatientInfo)
    {
        // 检查病历号是否已存在
        GzPatientInfo existPatient = gzPatientInfoService.selectGzPatientInfoByMedicalRecordNo(gzPatientInfo.getMedicalRecordNo());
        if (existPatient != null) {
            return AjaxResult.error("该病历号已存在");
        }
        return toAjax(gzPatientInfoService.insertGzPatientInfo(gzPatientInfo));
    }

    /**
     * 修改患者信息
     */
    @PreAuthorize("@ss.hasPermi('gz:patient:edit')")
    @Log(title = "患者信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody GzPatientInfo gzPatientInfo)
    {
        // 如果修改了病历号，检查新病历号是否已存在
        GzPatientInfo existPatient = gzPatientInfoService.selectGzPatientInfoByMedicalRecordNo(gzPatientInfo.getMedicalRecordNo());
        if (existPatient != null && !existPatient.getId().equals(gzPatientInfo.getId())) {
            return AjaxResult.error("该病历号已存在");
        }
        return toAjax(gzPatientInfoService.updateGzPatientInfo(gzPatientInfo));
    }

    /**
     * 删除患者信息
     */
    @PreAuthorize("@ss.hasPermi('gz:patient:remove')")
    @Log(title = "患者信息", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(gzPatientInfoService.deleteGzPatientInfoByIds(ids));
    }
}

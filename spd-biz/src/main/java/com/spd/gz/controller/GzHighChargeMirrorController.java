package com.spd.gz.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.spd.common.annotation.Log;
import com.spd.common.core.controller.BaseController;
import com.spd.common.core.domain.AjaxResult;
import com.spd.common.core.page.TableDataInfo;
import com.spd.common.enums.BusinessType;
import com.spd.common.utils.SecurityUtils;
import com.spd.his.domain.HisInpatientChargeMirror;
import com.spd.his.domain.HisOutpatientChargeMirror;
import com.spd.his.domain.dto.HisMirrorHighApplyBody;
import com.spd.his.domain.dto.HisMirrorHighApplyResultVo;
import com.spd.his.domain.dto.HisMirrorHighScanBody;
import com.spd.his.domain.dto.HisMirrorHighScanResultVo;
import com.spd.his.domain.dto.HisMirrorConsumeRecordVo;
import com.spd.his.domain.dto.HisPatientChargeAllQuery;
import com.spd.his.domain.dto.HisPatientChargeDetailRow;
import com.spd.his.service.IHisPatientChargeService;

/**
 * 高值扫描核销：HIS 计费镜像高值扫码消耗（薄封装，业务逻辑在 his 模块）。
 */
@RestController
@RequestMapping("/gz/highChargeMirror")
public class GzHighChargeMirrorController extends BaseController
{
    @Autowired
    private IHisPatientChargeService hisPatientChargeService;

    @PreAuthorize("@ss.hasPermi('gz:highChargeScan:list')")
    @GetMapping("/mirror/inpatient/list")
    public TableDataInfo inpatientMirrorList(HisInpatientChargeMirror query)
    {
        query.setTenantId(SecurityUtils.getCustomerId());
        List<HisInpatientChargeMirror> list = hisPatientChargeService.selectHighChargeInpatientMirrorList(query);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('gz:highChargeScan:list')")
    @GetMapping("/mirror/outpatient/list")
    public TableDataInfo outpatientMirrorList(HisOutpatientChargeMirror query)
    {
        query.setTenantId(SecurityUtils.getCustomerId());
        List<HisOutpatientChargeMirror> list = hisPatientChargeService.selectHighChargeOutpatientMirrorList(query);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('gz:highChargeScan:list')")
    @GetMapping("/mirror/all/list")
    public TableDataInfo allMirrorList(HisPatientChargeAllQuery query)
    {
        query.setTenantId(SecurityUtils.getCustomerId());
        List<HisPatientChargeDetailRow> list = hisPatientChargeService.selectHighChargeAllMirrorList(query);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('gz:highChargeScan:list')")
    @GetMapping("/mirror/consumeRecords")
    public AjaxResult mirrorConsumeRecords(@RequestParam("visitKind") String visitKind,
        @RequestParam("mirrorRowId") String mirrorRowId)
    {
        List<HisMirrorConsumeRecordVo> list = hisPatientChargeService.listHighChargeMirrorConsumeRecords(visitKind, mirrorRowId);
        return success(list);
    }

    @PreAuthorize("@ss.hasPermi('gz:highChargeScan:list') or @ss.hasPermi('gz:highChargeScan:scan') "
        + "or @ss.hasPermi('department:patientCharge:processMirrorHigh') "
        + "or @ss.hasPermi('department:patientCharge:generateConsume')")
    @PostMapping("/mirror/scanHighBarcode")
    public AjaxResult scanHighBarcode(@RequestBody HisMirrorHighScanBody body)
    {
        HisMirrorHighScanResultVo vo = hisPatientChargeService.scanMirrorHighBarcode(body);
        return success(vo);
    }

    @PreAuthorize("@ss.hasPermi('gz:highChargeScan:list') or @ss.hasPermi('gz:highChargeScan:apply') "
        + "or @ss.hasPermi('department:patientCharge:processMirrorHigh') "
        + "or @ss.hasPermi('department:patientCharge:generateConsume')")
    @Log(title = "高值扫描核销", businessType = BusinessType.OTHER)
    @PostMapping("/mirror/applyHighConsume")
    public AjaxResult applyHighConsume(@RequestBody HisMirrorHighApplyBody body)
    {
        HisMirrorHighApplyResultVo vo = hisPatientChargeService.applyMirrorHighConsume(body);
        return success(vo);
    }
}

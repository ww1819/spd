package com.spd.his.controller;

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
import com.spd.his.domain.HisChargeFetchBatch;
import com.spd.his.domain.HisInpatientChargeMirror;
import com.spd.his.domain.HisOutpatientChargeMirror;
import com.spd.his.domain.dto.HisFetchResultVo;
import com.spd.his.domain.dto.HisGenerateConsumeResultVo;
import com.spd.his.domain.dto.HisMirrorHighApplyBody;
import com.spd.his.domain.dto.HisMirrorHighApplyResultVo;
import com.spd.his.domain.dto.HisMirrorHighScanBody;
import com.spd.his.domain.dto.HisMirrorHighScanResultVo;
import com.spd.his.domain.dto.HisMirrorLowBatchResultVo;
import com.spd.his.domain.dto.HisMirrorManualBatchBody;
import com.spd.his.domain.dto.HisMirrorManualRowBody;
import com.spd.his.domain.dto.HisPatientChargeFetchBody;
import com.spd.his.domain.dto.HisPatientChargeSummaryRow;
import com.spd.his.service.IHisPatientChargeService;

/**
 * HIS 患者计费镜像：查询、抓取、按明细手动生成科室消耗（低值/高值）。
 */
@RestController
@RequestMapping("/his/patientCharge")
public class HisPatientChargeController extends BaseController
{
    @Autowired
    private IHisPatientChargeService hisPatientChargeService;

    @PreAuthorize("@ss.hasPermi('department:patientCharge:list')")
    @GetMapping("/mirror/inpatient/list")
    public TableDataInfo inpatientMirrorList(HisInpatientChargeMirror query)
    {
        startPage();
        query.setTenantId(SecurityUtils.getCustomerId());
        List<HisInpatientChargeMirror> list = hisPatientChargeService.selectInpatientMirrorList(query);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('department:patientCharge:list')")
    @GetMapping("/mirror/outpatient/list")
    public TableDataInfo outpatientMirrorList(HisOutpatientChargeMirror query)
    {
        startPage();
        query.setTenantId(SecurityUtils.getCustomerId());
        List<HisOutpatientChargeMirror> list = hisPatientChargeService.selectOutpatientMirrorList(query);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('department:patientCharge:list')")
    @GetMapping("/summary/list")
    public TableDataInfo summaryList(String beginChargeDate, String endChargeDate)
    {
        List<HisPatientChargeSummaryRow> list = hisPatientChargeService.selectChargeSummary(beginChargeDate, endChargeDate);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('department:patientCharge:fetchInpatient')")
    @Log(title = "HIS住院计费抓取", businessType = BusinessType.OTHER)
    @PostMapping("/mirror/fetch/inpatient")
    public AjaxResult fetchInpatient(@RequestBody HisPatientChargeFetchBody body)
    {
        HisFetchResultVo vo = hisPatientChargeService.fetchInpatientMirror(body);
        return success(vo);
    }

    @PreAuthorize("@ss.hasPermi('department:patientCharge:fetchOutpatient')")
    @Log(title = "HIS门诊计费抓取", businessType = BusinessType.OTHER)
    @PostMapping("/mirror/fetch/outpatient")
    public AjaxResult fetchOutpatient(@RequestBody HisPatientChargeFetchBody body)
    {
        HisFetchResultVo vo = hisPatientChargeService.fetchOutpatientMirror(body);
        return success(vo);
    }

    @PreAuthorize("@ss.hasPermi('department:patientCharge:list') or @ss.hasPermi('department:patientCharge:fetchBatchList')")
    @GetMapping("/fetchBatch/list")
    public TableDataInfo fetchBatchList(@RequestParam(value = "limit", defaultValue = "30") int limit)
    {
        List<HisChargeFetchBatch> list = hisPatientChargeService.listRecentFetchBatches(limit);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('department:patientCharge:generateConsume') or @ss.hasPermi('department:patientCharge:processMirrorLow')")
    @Log(title = "HIS计费镜像低值处理", businessType = BusinessType.OTHER)
    @PostMapping("/mirror/processLowValue")
    public AjaxResult processLowValue(@RequestBody HisMirrorManualRowBody body)
    {
        HisGenerateConsumeResultVo vo = hisPatientChargeService.processMirrorLowValue(body);
        return success(vo);
    }

    @PreAuthorize("@ss.hasPermi('department:patientCharge:generateConsume') or @ss.hasPermi('department:patientCharge:processMirrorLow')")
    @Log(title = "HIS计费镜像批量低值处理", businessType = BusinessType.OTHER)
    @PostMapping("/mirror/processLowValueBatch")
    public AjaxResult processLowValueBatch(@RequestBody HisMirrorManualBatchBody body)
    {
        HisMirrorLowBatchResultVo vo = hisPatientChargeService.processMirrorLowValueBatch(body);
        return success(vo);
    }

    @PreAuthorize("@ss.hasPermi('department:patientCharge:generateConsume') or @ss.hasPermi('department:patientCharge:processMirrorHigh')")
    @PostMapping("/mirror/scanHighBarcode")
    public AjaxResult scanHighBarcode(@RequestBody HisMirrorHighScanBody body)
    {
        HisMirrorHighScanResultVo vo = hisPatientChargeService.scanMirrorHighBarcode(body);
        return success(vo);
    }

    @PreAuthorize("@ss.hasPermi('department:patientCharge:generateConsume') or @ss.hasPermi('department:patientCharge:processMirrorHigh')")
    @Log(title = "HIS计费镜像高值扫码消耗", businessType = BusinessType.OTHER)
    @PostMapping("/mirror/applyHighConsume")
    public AjaxResult applyHighConsume(@RequestBody HisMirrorHighApplyBody body)
    {
        HisMirrorHighApplyResultVo vo = hisPatientChargeService.applyMirrorHighConsume(body);
        return success(vo);
    }
}

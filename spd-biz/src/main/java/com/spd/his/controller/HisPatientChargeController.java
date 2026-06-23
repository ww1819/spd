package com.spd.his.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletResponse;
import com.spd.common.annotation.Log;
import com.spd.common.utils.poi.ExcelUtil;
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
import com.spd.his.domain.dto.HisMirrorLowBatchResultVo;
import com.spd.his.domain.dto.HisMirrorManualBatchBody;
import com.spd.his.domain.dto.HisMirrorManualRowBody;
import com.spd.his.domain.dto.HisMirrorWriteOffBody;
import com.spd.his.domain.dto.HisMirrorWriteOffResultVo;
import com.spd.his.domain.dto.HisPatientChargeAllQuery;
import com.spd.his.domain.dto.HisPatientChargeDetailRow;
import com.spd.his.domain.dto.HisPatientChargeFetchBody;
import com.spd.his.domain.dto.HisPatientChargeMirrorExportVo;
import com.spd.his.domain.dto.HisPatientChargeSummaryRow;
import com.spd.his.domain.dto.HisMirrorConsumeRecordVo;
import com.spd.his.domain.dto.HisTenantBillingSettingBody;
import com.spd.his.service.IHisPatientChargeService;

/**
 * HIS 患者计费镜像：查询、抓取、按明细低值核销。
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
        query.setTenantId(SecurityUtils.getCustomerId());
        List<HisInpatientChargeMirror> list = hisPatientChargeService.selectInpatientMirrorList(query);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('department:patientCharge:list')")
    @GetMapping("/mirror/outpatient/list")
    public TableDataInfo outpatientMirrorList(HisOutpatientChargeMirror query)
    {
        query.setTenantId(SecurityUtils.getCustomerId());
        List<HisOutpatientChargeMirror> list = hisPatientChargeService.selectOutpatientMirrorList(query);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('department:patientCharge:list')")
    @GetMapping("/mirror/all/list")
    public TableDataInfo allMirrorList(HisPatientChargeAllQuery query)
    {
        query.setTenantId(SecurityUtils.getCustomerId());
        List<HisPatientChargeDetailRow> list = hisPatientChargeService.selectAllMirrorList(query);
        return getDataTable(list);
    }

    /**
     * 患者费用明细导出（按当前筛选条件；末尾含处理方式、处理情况）
     */
    @PreAuthorize("@ss.hasPermi('department:patientCharge:list')")
    @Log(title = "患者费用明细导出", businessType = BusinessType.EXPORT)
    @PostMapping("/mirror/export")
    public void exportMirrorList(HttpServletResponse response, HisPatientChargeAllQuery query,
        @RequestParam(value = "visitKind", required = false) String visitKind,
        @RequestParam(value = "inpatientNo", required = false) String inpatientNo,
        @RequestParam(value = "outpatientNo", required = false) String outpatientNo)
    {
        query.setTenantId(SecurityUtils.getCustomerId());
        List<HisPatientChargeMirrorExportVo> list = hisPatientChargeService.selectMirrorExportList(
            query, visitKind, inpatientNo, outpatientNo);
        ExcelUtil<HisPatientChargeMirrorExportVo> util = new ExcelUtil<>(HisPatientChargeMirrorExportVo.class);
        util.exportExcel(response, list, "患者费用明细");
    }

    /**
     * 某条计费明细关联的科室消耗记录（追溯 his_mirror_consume_link）
     */
    @PreAuthorize("@ss.hasPermi('department:patientCharge:list')")
    @GetMapping("/mirror/consumeRecords")
    public AjaxResult mirrorConsumeRecords(@RequestParam("visitKind") String visitKind,
        @RequestParam("mirrorRowId") String mirrorRowId)
    {
        List<HisMirrorConsumeRecordVo> list = hisPatientChargeService.listMirrorConsumeRecords(visitKind, mirrorRowId);
        return success(list);
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

    @PreAuthorize("@ss.hasPermi('department:patientCharge:fetchInpatient')")
    @Log(title = "HIS住院执行科室补全", businessType = BusinessType.UPDATE)
    @PostMapping("/mirror/backfillExecDept/inpatient")
    public AjaxResult backfillInpatientExecDept(@RequestBody HisPatientChargeFetchBody body)
    {
        return success(hisPatientChargeService.backfillInpatientExecDept(body));
    }

    @PreAuthorize("@ss.hasPermi('department:patientCharge:fetchOutpatient')")
    @Log(title = "HIS门诊执行科室补全", businessType = BusinessType.UPDATE)
    @PostMapping("/mirror/backfillExecDept/outpatient")
    public AjaxResult backfillOutpatientExecDept(@RequestBody HisPatientChargeFetchBody body)
    {
        return success(hisPatientChargeService.backfillOutpatientExecDept(body));
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

    /**
     * 低值冲销：反消耗并恢复为待处理；计费行会联动已核销/已退费返还的关联退费行。
     */
    @PreAuthorize("@ss.hasPermi('department:patientCharge:writeOffLow')")
    @Log(title = "HIS计费镜像低值冲销", businessType = BusinessType.UPDATE)
    @PostMapping("/mirror/writeOffLowValue")
    public AjaxResult writeOffLowValue(@RequestBody HisMirrorWriteOffBody body)
    {
        HisMirrorWriteOffResultVo vo = hisPatientChargeService.processMirrorLowValueWriteOff(body);
        return success(vo);
    }

    /**
     * 衡水三院：计费自动处理开关（低值自动消耗、退费自动返还）
     */
    @PreAuthorize("@ss.hasPermi('department:patientCharge:billingTenantSetting')")
    @GetMapping("/tenant/billingSetting")
    public AjaxResult getTenantBillingSetting()
    {
        return success(hisPatientChargeService.getTenantBillingSetting());
    }

    /**
     * 衡水三院：保存计费自动处理开关
     */
    @PreAuthorize("@ss.hasPermi('department:patientCharge:billingTenantSetting')")
    @Log(title = "租户计费自动消耗开关", businessType = BusinessType.UPDATE)
    @PutMapping("/tenant/billingSetting")
    public AjaxResult saveTenantBillingSetting(@RequestBody HisTenantBillingSettingBody body)
    {
        hisPatientChargeService.saveTenantBillingSetting(body);
        return success();
    }
}

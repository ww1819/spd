package com.spd.his.service;

import java.util.List;
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
import com.spd.his.domain.dto.HisPatientChargeAllQuery;
import com.spd.his.domain.dto.HisPatientChargeDetailRow;
import com.spd.his.domain.dto.HisPatientChargeSummaryRow;
import com.spd.his.domain.dto.HisMirrorConsumeRecordVo;
import com.spd.his.domain.dto.HisTenantBillingSettingBody;

/**
 * HIS 患者计费镜像：抓取、查询与按明细手动生成科室消耗。
 */
public interface IHisPatientChargeService
{
    HisFetchResultVo fetchInpatientMirror(HisPatientChargeFetchBody body);

    HisFetchResultVo fetchOutpatientMirror(HisPatientChargeFetchBody body);

    List<HisInpatientChargeMirror> selectInpatientMirrorList(HisInpatientChargeMirror query);

    List<HisOutpatientChargeMirror> selectOutpatientMirrorList(HisOutpatientChargeMirror query);

    List<HisPatientChargeDetailRow> selectAllMirrorList(HisPatientChargeAllQuery query);

    /**
     * 查询某条计费镜像行关联的科室消耗记录（含消耗单号、明细、分摊数量等）
     */
    List<HisMirrorConsumeRecordVo> listMirrorConsumeRecords(String visitKind, String mirrorRowId);

    List<HisPatientChargeSummaryRow> selectChargeSummary(String beginChargeDate, String endChargeDate);

    List<HisChargeFetchBatch> listRecentFetchBatches(int limit);

    HisGenerateConsumeResultVo processMirrorLowValue(HisMirrorManualRowBody body);

    HisMirrorLowBatchResultVo processMirrorLowValueBatch(HisMirrorManualBatchBody body);

    HisMirrorHighScanResultVo scanMirrorHighBarcode(HisMirrorHighScanBody body);

    HisMirrorHighApplyResultVo applyMirrorHighConsume(HisMirrorHighApplyBody body);

    /** 衡水三院：低值计费自动消耗开关查询 */
    HisTenantBillingSettingBody getTenantBillingSetting();

    /** 衡水三院：保存低值计费自动消耗开关 */
    void saveTenantBillingSetting(HisTenantBillingSettingBody body);
}

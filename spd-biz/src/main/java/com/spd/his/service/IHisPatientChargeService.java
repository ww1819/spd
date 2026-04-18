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
import com.spd.his.domain.dto.HisMirrorManualRowBody;
import com.spd.his.domain.dto.HisPatientChargeFetchBody;
import com.spd.his.domain.dto.HisPatientChargeSummaryRow;

/**
 * HIS 患者计费镜像：抓取、查询与按明细手动生成科室消耗。
 */
public interface IHisPatientChargeService
{
    HisFetchResultVo fetchInpatientMirror(HisPatientChargeFetchBody body);

    HisFetchResultVo fetchOutpatientMirror(HisPatientChargeFetchBody body);

    List<HisInpatientChargeMirror> selectInpatientMirrorList(HisInpatientChargeMirror query);

    List<HisOutpatientChargeMirror> selectOutpatientMirrorList(HisOutpatientChargeMirror query);

    List<HisPatientChargeSummaryRow> selectChargeSummary(String beginChargeDate, String endChargeDate);

    List<HisChargeFetchBatch> listRecentFetchBatches(int limit);

    HisGenerateConsumeResultVo processMirrorLowValue(HisMirrorManualRowBody body);

    HisMirrorHighScanResultVo scanMirrorHighBarcode(HisMirrorHighScanBody body);

    HisMirrorHighApplyResultVo applyMirrorHighConsume(HisMirrorHighApplyBody body);
}

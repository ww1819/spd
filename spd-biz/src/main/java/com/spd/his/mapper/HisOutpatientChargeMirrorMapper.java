package com.spd.his.mapper;

import java.util.Date;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.spd.his.domain.HisOutpatientChargeMirror;
import com.spd.his.domain.dto.HisIdFingerprint;
import com.spd.his.domain.dto.HisPatientChargeSummaryRow;

public interface HisOutpatientChargeMirrorMapper
{
    int insertBatch(@Param("list") List<HisOutpatientChargeMirror> list);

    List<HisIdFingerprint> selectFingerprintsByHisIds(@Param("tenantId") String tenantId, @Param("hisIds") List<String> hisIds);

    List<HisOutpatientChargeMirror> selectMirrorList(HisOutpatientChargeMirror query);

    List<HisPatientChargeSummaryRow> selectSummary(
            @Param("tenantId") String tenantId,
            @Param("beginChargeDate") String beginChargeDate,
            @Param("endChargeDate") String endChargeDate);

    List<HisOutpatientChargeMirror> selectPendingByFetchBatch(@Param("tenantId") String tenantId, @Param("fetchBatchId") String fetchBatchId);

    int countConsumedInFetchBatch(@Param("tenantId") String tenantId, @Param("fetchBatchId") String fetchBatchId);

    int updateMirrorProcessByIds(
            @Param("tenantId") String tenantId,
            @Param("ids") List<String> ids,
            @Param("processStatus") String processStatus,
            @Param("processType") String processType,
            @Param("processTime") Date processTime,
            @Param("processBy") String processBy);

    HisOutpatientChargeMirror selectByIdAndTenant(@Param("tenantId") String tenantId, @Param("id") String id);

    String selectMirrorIdByHisChargeId(@Param("tenantId") String tenantId, @Param("hisChargeId") String hisChargeId);
}

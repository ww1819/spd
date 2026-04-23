package com.spd.his.mapper;

import java.util.Date;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.spd.his.domain.HisInpatientChargeMirror;
import com.spd.his.domain.dto.HisIdFingerprint;
import com.spd.his.domain.dto.HisPatientChargeSummaryRow;

public interface HisInpatientChargeMirrorMapper
{
    int insertBatch(@Param("list") List<HisInpatientChargeMirror> list);

    List<HisIdFingerprint> selectFingerprintsByHisIds(@Param("tenantId") String tenantId, @Param("hisIds") List<String> hisIds);

    List<HisInpatientChargeMirror> selectMirrorList(HisInpatientChargeMirror query);

    List<HisPatientChargeSummaryRow> selectSummary(
            @Param("tenantId") String tenantId,
            @Param("beginChargeDate") String beginChargeDate,
            @Param("endChargeDate") String endChargeDate);

    List<HisInpatientChargeMirror> selectPendingByFetchBatch(@Param("tenantId") String tenantId, @Param("fetchBatchId") String fetchBatchId);

    int countConsumedInFetchBatch(@Param("tenantId") String tenantId, @Param("fetchBatchId") String fetchBatchId);

    int updateMirrorProcessByIds(
            @Param("tenantId") String tenantId,
            @Param("ids") List<String> ids,
            @Param("processStatus") String processStatus,
            @Param("processType") String processType,
            @Param("processTime") Date processTime,
            @Param("processBy") String processBy);

    HisInpatientChargeMirror selectByIdAndTenant(@Param("tenantId") String tenantId, @Param("id") String id);
}

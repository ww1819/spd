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

    List<HisPatientChargeSummaryRow> selectSummary(
            @Param("tenantId") String tenantId,
            @Param("beginChargeDate") String beginChargeDate,
            @Param("endChargeDate") String endChargeDate);

    List<HisInpatientChargeMirror> selectPendingByFetchBatch(@Param("tenantId") String tenantId, @Param("fetchBatchId") String fetchBatchId);

    /** 本批次待自动处理的退费镜像行（含 charge_id_tf、value_level） */
    List<HisInpatientChargeMirror> selectRefundPendingByFetchBatch(@Param("tenantId") String tenantId,
        @Param("fetchBatchId") String fetchBatchId);

    int countConsumedInFetchBatch(@Param("tenantId") String tenantId, @Param("fetchBatchId") String fetchBatchId);

    int updateMirrorProcessByIds(
            @Param("tenantId") String tenantId,
            @Param("ids") List<String> ids,
            @Param("processStatus") String processStatus,
            @Param("processType") String processType,
            @Param("processTime") Date processTime,
            @Param("processBy") String processBy,
            @Param("processSituation") String processSituation,
            @Param("processParty") String processParty);

    /**
     * 乐观认领：仅当仍为 PENDING_CONSUME 时改为 CONSUMING，返回影响行数。
     * 用于并发下避免同一镜像行被多次核销。
     */
    int claimMirrorProcessPending(
            @Param("tenantId") String tenantId,
            @Param("id") String id,
            @Param("processTime") Date processTime,
            @Param("processBy") String processBy,
            @Param("processParty") String processParty);

    int updateMirrorProcessOutcome(
            @Param("tenantId") String tenantId,
            @Param("ids") List<String> ids,
            @Param("processSituation") String processSituation,
            @Param("processParty") String processParty,
            @Param("processTime") Date processTime,
            @Param("processBy") String processBy);

    HisInpatientChargeMirror selectByIdAndTenant(@Param("tenantId") String tenantId, @Param("id") String id);

    String selectMirrorIdByHisChargeId(@Param("tenantId") String tenantId, @Param("hisChargeId") String hisChargeId);

    /** 关联到原收费明细的退费镜像行（已处理或已退费返还） */
    List<HisInpatientChargeMirror> selectLinkedRefundByChargeIdTf(@Param("tenantId") String tenantId,
        @Param("chargeIdTf") String chargeIdTf);

    int updateExecDeptIfMissing(
        @Param("tenantId") String tenantId,
        @Param("hisChargeId") String hisChargeId,
        @Param("execDeptId") String execDeptId,
        @Param("execDeptName") String execDeptName,
        @Param("rowFingerprint") String rowFingerprint,
        @Param("updateBy") String updateBy);
}

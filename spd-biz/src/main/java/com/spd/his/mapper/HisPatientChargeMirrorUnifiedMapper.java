package com.spd.his.mapper;

import java.util.Date;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.spd.his.domain.HisPatientChargeMirrorUnified;
import com.spd.his.domain.dto.HisPatientChargeMirrorUnifiedQuery;

public interface HisPatientChargeMirrorUnifiedMapper
{
    long countByTenantId(@Param("tenantId") String tenantId);

    /** 住院+门诊镜像表中该租户行数之和（用于判断是否需要从镜像回填统一表） */
    long countMirrorSourceRowsByTenant(@Param("tenantId") String tenantId);

    /** 从住院镜像补入统一表（仅缺 id 的行） */
    int backfillInpatientFromMirror(@Param("tenantId") String tenantId);

    /** 从门诊镜像补入统一表（仅缺 id 的行） */
    int backfillOutpatientFromMirror(@Param("tenantId") String tenantId);

    int insertBatch(@Param("list") List<HisPatientChargeMirrorUnified> list);

    int updateMirrorProcessByIds(
        @Param("tenantId") String tenantId,
        @Param("ids") List<String> ids,
        @Param("processStatus") String processStatus,
        @Param("processType") String processType,
        @Param("processTime") Date processTime,
        @Param("processBy") String processBy,
        @Param("processSituation") String processSituation,
        @Param("processParty") String processParty);

    int updateMirrorProcessOutcome(
        @Param("tenantId") String tenantId,
        @Param("ids") List<String> ids,
        @Param("processSituation") String processSituation,
        @Param("processParty") String processParty,
        @Param("processTime") Date processTime,
        @Param("processBy") String processBy);

    long countList(HisPatientChargeMirrorUnifiedQuery query);

    List<HisPatientChargeMirrorUnified> selectList(HisPatientChargeMirrorUnifiedQuery query);

    int updateInpatientExecDeptIfMissing(
        @Param("tenantId") String tenantId,
        @Param("hisChargeId") String hisChargeId,
        @Param("execDeptId") String execDeptId,
        @Param("execDeptName") String execDeptName,
        @Param("rowFingerprint") String rowFingerprint);

    int updateOutpatientExecDeptIfMissing(
        @Param("tenantId") String tenantId,
        @Param("hisChargeId") String hisChargeId,
        @Param("execDeptId") String execDeptId,
        @Param("execDeptName") String execDeptName,
        @Param("rowFingerprint") String rowFingerprint);

    int syncInpatientExecDeptFromMirror(@Param("tenantId") String tenantId);

    int syncOutpatientExecDeptFromMirror(@Param("tenantId") String tenantId);
}

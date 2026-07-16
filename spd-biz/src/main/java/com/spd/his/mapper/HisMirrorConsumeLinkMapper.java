package com.spd.his.mapper;

import java.math.BigDecimal;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.spd.his.domain.HisMirrorConsumeLink;
import com.spd.his.domain.dto.HisMirrorConsumeRecordVo;

public interface HisMirrorConsumeLinkMapper
{
    int insertBatch(@Param("list") List<HisMirrorConsumeLink> list);

    BigDecimal sumAllocQtyForMirrorRow(@Param("tenantId") String tenantId, @Param("visitKind") String visitKind,
        @Param("mirrorRowId") String mirrorRowId);

    /** 净已核销数量 = sum(alloc_qty - returned_qty) */
    BigDecimal sumNetAllocQtyForMirrorRow(@Param("tenantId") String tenantId, @Param("visitKind") String visitKind,
        @Param("mirrorRowId") String mirrorRowId);

    List<String> selectDistinctBillSourcesForMirror(@Param("tenantId") String tenantId, @Param("visitKind") String visitKind,
        @Param("mirrorRowId") String mirrorRowId);

    /**
     * 某条计费镜像行关联的科室消耗记录（按关联时间倒序）
     */
    List<HisMirrorConsumeRecordVo> selectConsumeRecordsByMirrorRow(@Param("tenantId") String tenantId,
        @Param("visitKind") String visitKind, @Param("mirrorRowId") String mirrorRowId);

    /** 镜像行关联正向消耗所产生的冲销（反消耗）单明细 */
    List<HisMirrorConsumeRecordVo> selectReverseConsumeRecordsByMirrorRow(@Param("tenantId") String tenantId,
        @Param("visitKind") String visitKind, @Param("mirrorRowId") String mirrorRowId);

    /**
     * 退费候选：尚有余量可返还的消耗关联行（低值 dep_inventory 非空；高值 gz_dep_inventory 非空）
     */
    List<HisMirrorConsumeLink> selectCandidateLinksForRefund(@Param("tenantId") String tenantId,
        @Param("visitKind") String visitKind, @Param("mirrorRowId") String mirrorRowId,
        @Param("valueLevel") String valueLevel);

    int increaseReturnedQtyById(@Param("id") String id, @Param("delta") java.math.BigDecimal delta,
        @Param("updateBy") String updateBy);

    /** 低值反消耗后：按来源消耗明细回写关联行的 returned_qty */
    int increaseReturnedQtyBySrcConsumeEntryId(@Param("srcConsumeEntryId") Long srcConsumeEntryId,
        @Param("delta") java.math.BigDecimal delta, @Param("updateBy") String updateBy);

    int decreaseReturnedQtyById(@Param("id") String id, @Param("delta") java.math.BigDecimal delta,
        @Param("updateBy") String updateBy);

    HisMirrorConsumeLink selectById(@Param("tenantId") String tenantId, @Param("id") String id);

    List<HisMirrorConsumeLink> selectAllLowValueLinksForMirror(@Param("tenantId") String tenantId,
        @Param("visitKind") String visitKind, @Param("mirrorRowId") String mirrorRowId);

    int resetReturnedQtyForMirror(@Param("tenantId") String tenantId, @Param("visitKind") String visitKind,
        @Param("mirrorRowId") String mirrorRowId, @Param("updateBy") String updateBy);

    /** 低值冲销完成后：关联行视为已全部返还，释放待核销数量 */
    int markLinksFullyReturnedForWriteOff(@Param("tenantId") String tenantId, @Param("visitKind") String visitKind,
        @Param("mirrorRowId") String mirrorRowId, @Param("updateBy") String updateBy);

    /** 高值冲销：单行全部返还 */
    int markLinkFullyReturnedById(@Param("tenantId") String tenantId, @Param("id") String id,
        @Param("updateBy") String updateBy);

    /** 高值冲销：软删关联行（释放后可重新扫码核销） */
    int softDeleteLinkById(@Param("tenantId") String tenantId, @Param("id") String id,
        @Param("deleteBy") String deleteBy);

    /** 确认批次下仍有效且尚有净核销量的关联行（反向后同批一并冲销回补） */
    List<String> selectActiveLinkIdsByConfirmIds(@Param("tenantId") String tenantId,
        @Param("confirmIds") List<String> confirmIds);
}

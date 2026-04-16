package com.spd.department.mapper;

import java.util.List;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import com.spd.department.domain.WhWhApplyCkEntryRef;
import com.spd.department.domain.WhWarehouseApply;
import com.spd.department.domain.WhWarehouseApplyEntry;

/**
 * 仓库申请单（科室申领按仓拆分）
 */
@Mapper
@Repository
public interface WhWarehouseApplyMapper {

    int countActiveByBasApplyId(@Param("basApplyId") String basApplyId, @Param("tenantId") String tenantId);

    String selectMaxBillNo(@Param("date") String date);

    int insertWhWarehouseApply(WhWarehouseApply row);

    int insertWhWarehouseApplyEntry(WhWarehouseApplyEntry row);

    WhWarehouseApply selectWhWarehouseApplyById(@Param("id") String id);

    List<WhWarehouseApplyEntry> selectWhWarehouseApplyEntryListByParenId(@Param("parenId") String parenId);

    List<WhWarehouseApply> selectWhWarehouseApplyList(WhWarehouseApply query);

    /** 出库管理引用：仅返回仍有可出库数量的仓库申请单 */
    List<WhWarehouseApply> selectWhWarehouseApplyListForOutboundCk(WhWarehouseApply query);

    WhWarehouseApplyEntry selectWhWarehouseApplyEntryById(@Param("id") String id);

    int updateWhWarehouseApplyVoidWhole(@Param("id") String id,
        @Param("voidWholeBy") String voidWholeBy,
        @Param("voidWholeTime") Date voidWholeTime,
        @Param("voidWholeReason") String voidWholeReason);

    int updateWhWarehouseApplyEntryLineVoid(@Param("id") String id,
        @Param("parenId") String parenId,
        @Param("lineVoidStatus") Integer lineVoidStatus,
        @Param("lineVoidQty") BigDecimal lineVoidQty,
        @Param("lineVoidBy") String lineVoidBy,
        @Param("lineVoidTime") Date lineVoidTime,
        @Param("lineVoidReason") String lineVoidReason);

    int countActiveCkRefsByWhApplyId(@Param("whApplyId") String whApplyId);

    BigDecimal sumLinkedQtyByWhApplyEntryId(@Param("entryId") String entryId);

    /** 库房申请明细已被出库单占用的数量（可选排除某张出库单，用于修改本单） */
    BigDecimal sumLinkedQtyByWhApplyEntryIdExcludingCkBill(@Param("entryId") String entryId,
        @Param("excludeCkBillId") String excludeCkBillId);

    int insertWhWhApplyCkEntryRef(WhWhApplyCkEntryRef row);

    /** 按出库单主表解除库房申请关联（逻辑删除 ref 行） */
    int softDeleteCkEntryRefsByCkBillId(@Param("ckBillId") String ckBillId,
        @Param("tenantId") String tenantId,
        @Param("updateBy") String updateBy);
}

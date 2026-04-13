package com.spd.department.service;

import java.math.BigDecimal;
import java.util.List;

import com.spd.department.domain.BasApply;
import com.spd.department.domain.WhWarehouseApply;

/**
 * 仓库申请单（科室申领审核按仓拆分）
 */
public interface IWhWarehouseApplyService {

    /**
     * 科室申领（billType=1）审核通过后：按仓库 + FIFO 库存行拆分生成仓库申请单。
     */
    void generateFromDeptApplyAfterAudit(BasApply basApply);

    List<WhWarehouseApply> selectWhWarehouseApplyList(WhWarehouseApply query);

    WhWarehouseApply selectWhWarehouseApplyById(String id);

    /**
     * 出库单新增/修改明细并落库后：按出库明细上的 wh_apply_entry_id 重写 wh_wh_apply_ck_entry_ref（先软删本单旧关联）。
     */
    void syncWhApplyCkRefsAfterOutboundSave(Long ckBillId);

    /** 出库单逻辑删除前：软删本单在 wh_wh_apply_ck_entry_ref 中的关联 */
    void releaseWhApplyCkRefsForOutboundBill(Long ckBillId, String tenantId);

    /** 整单作废（无关联出库引用时允许） */
    void voidWholeWhWarehouseApply(String id, String reason);

    /** 明细作废：增加作废数量，并记录人、时、原因 */
    void voidWhWarehouseApplyEntryLine(String whApplyId, String entryId, BigDecimal voidQty, String reason);
}

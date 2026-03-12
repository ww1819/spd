package com.spd.caigou.mapper;

import com.spd.caigou.domain.PurchasePlanEntryDepApply;
import com.spd.caigou.domain.vo.ApplyDetailVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 采购计划明细关联科室申购单明细(dep) Mapper
 *
 * @author spd
 */
public interface PurchasePlanEntryDepApplyMapper {

    int batchInsert(@Param("list") List<PurchasePlanEntryDepApply> list);

    /** 根据关联的明细从主表回填 dep_purchase_apply_id、purchase_bill_no */
    int updateFillApplyInfo(@Param("purchasePlanEntryId") Long purchasePlanEntryId);

    int logicDeleteByPlanId(@Param("planId") Long planId, @Param("deleteBy") String deleteBy);

    /** 根据采购计划明细ID查询关联的申购明细（dep_purchase_apply） */
    List<ApplyDetailVO> selectApplyDetailsByEntryId(@Param("entryId") Long entryId);

    /** 根据采购计划ID查询关联的申购单号列表（去重，用于表头弹窗） */
    List<String> selectBillNoListByPlanId(@Param("planId") Long planId);

    /** 根据采购计划ID查询各明细关联的申购单号（每行一条 entry_id + purchase_bill_no，可能多行同一 entry） */
    List<com.spd.caigou.domain.vo.EntryBillNoVO> selectEntryBillNosByPlanId(@Param("planId") Long planId);
}

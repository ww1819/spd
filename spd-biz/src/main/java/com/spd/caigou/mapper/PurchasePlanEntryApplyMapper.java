package com.spd.caigou.mapper;

import com.spd.caigou.domain.PurchasePlanEntryApply;
import com.spd.caigou.domain.vo.ApplyDetailVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 采购计划明细关联科室申购单明细 Mapper
 *
 * @author spd
 */
public interface PurchasePlanEntryApplyMapper {

    int insert(PurchasePlanEntryApply record);

    int batchInsert(@Param("list") List<PurchasePlanEntryApply> list);

    /** 逻辑删除：根据采购计划明细ID删除关联 */
    int logicDeleteByPurchasePlanEntryId(@Param("purchasePlanEntryId") Long purchasePlanEntryId, @Param("deleteBy") String deleteBy);

    /** 逻辑删除：根据采购计划主表ID删除该计划下所有明细的关联（计划更新时先删后插） */
    int logicDeleteByPlanId(@Param("planId") Long planId, @Param("deleteBy") String deleteBy);

    /** 根据采购计划明细ID查询关联的申购明细列表（科室申购单单号、申购科室、申购数量、制单人、制单时间、审核人、审核时间） */
    List<ApplyDetailVO> selectApplyDetailsByEntryId(@Param("entryId") Long entryId);
}

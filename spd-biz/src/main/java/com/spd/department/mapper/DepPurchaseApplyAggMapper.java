package com.spd.department.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

import com.spd.department.domain.DepPurchaseApplyAgg;
import com.spd.department.domain.DepPurchaseApplyAggEntry;

/**
 * 科室汇总申购（不分仓库）
 */
public interface DepPurchaseApplyAggMapper {

    DepPurchaseApplyAgg selectDepPurchaseApplyAggById(@Param("id") String id);

    List<DepPurchaseApplyAgg> selectDepPurchaseApplyAggList(DepPurchaseApplyAgg query);

    int insertDepPurchaseApplyAgg(DepPurchaseApplyAgg row);

    int updateDepPurchaseApplyAgg(DepPurchaseApplyAgg row);

    int deleteDepPurchaseApplyAggById(@Param("id") String id, @Param("deleteBy") String deleteBy);

    int deleteDepPurchaseApplyAggByIds(@Param("ids") String[] ids, @Param("deleteBy") String deleteBy);

    int deleteDepPurchaseApplyAggEntryByParentId(@Param("parentId") String parentId, @Param("deleteBy") String deleteBy);

    int deleteDepPurchaseApplyAggEntryByIds(@Param("parentId") String parentId, @Param("ids") List<String> ids,
        @Param("deleteBy") String deleteBy);

    int updateDepPurchaseApplyAggEntry(DepPurchaseApplyAggEntry entry);

    int batchInsertDepPurchaseApplyAggEntry(@Param("list") List<DepPurchaseApplyAggEntry> list);

    /** 按条件汇总明细数量（首页统计等） */
    BigDecimal selectAggEntryQtySum(DepPurchaseApplyAgg query);
}

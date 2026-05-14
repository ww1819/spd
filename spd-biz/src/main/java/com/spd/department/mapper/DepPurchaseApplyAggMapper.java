package com.spd.department.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

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

    int batchInsertDepPurchaseApplyAggEntry(@Param("list") List<DepPurchaseApplyAggEntry> list);
}

package com.spd.department.service;

import java.math.BigDecimal;
import java.util.List;

import com.spd.department.domain.DepPurchaseApplyAgg;

/**
 * 科室汇总申购（主单不分仓库；明细带仓库定数仓库ID，审核后按明细仓库拆分）。
 */
public interface IDepPurchaseApplyAggService {

    void applyDepartmentScopeToQuery(DepPurchaseApplyAgg query);

    DepPurchaseApplyAgg selectDepPurchaseApplyAggById(String id);

    List<DepPurchaseApplyAgg> selectDepPurchaseApplyAggList(DepPurchaseApplyAgg query);

    BigDecimal selectAggEntryQtySum(DepPurchaseApplyAgg query);

    int insertDepPurchaseApplyAgg(DepPurchaseApplyAgg row);

    int updateDepPurchaseApplyAgg(DepPurchaseApplyAgg row);

    int deleteDepPurchaseApplyAggById(String id);

    int deleteDepPurchaseApplyAggByIds(String[] ids);

    /** 审核汇总单：按明细仓库拆分为多张 dep_purchase_apply，并标记汇总单已拆分。 */
    int auditDepPurchaseApplyAgg(String id);

    int rejectDepPurchaseApplyAgg(String id, String rejectReason);
}

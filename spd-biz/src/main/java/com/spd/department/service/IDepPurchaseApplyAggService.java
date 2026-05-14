package com.spd.department.service;

import java.util.List;

import com.spd.department.domain.DepPurchaseApplyAgg;

/**
 * 科室汇总申购（不分仓库）；审核后按产品默认仓库拆分为科室申购单。
 */
public interface IDepPurchaseApplyAggService {

    void applyDepartmentScopeToQuery(DepPurchaseApplyAgg query);

    DepPurchaseApplyAgg selectDepPurchaseApplyAggById(String id);

    List<DepPurchaseApplyAgg> selectDepPurchaseApplyAggList(DepPurchaseApplyAgg query);

    int insertDepPurchaseApplyAgg(DepPurchaseApplyAgg row);

    int updateDepPurchaseApplyAgg(DepPurchaseApplyAgg row);

    int deleteDepPurchaseApplyAggById(String id);

    int deleteDepPurchaseApplyAggByIds(String[] ids);

    /**
     * 审核汇总单：按耗材默认仓库拆分为多张 dep_purchase_apply，并标记汇总单已拆分。
     */
    int auditDepPurchaseApplyAgg(String id);

    int rejectDepPurchaseApplyAgg(String id, String rejectReason);
}

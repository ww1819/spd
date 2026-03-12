package com.spd.caigou.service;

import com.spd.caigou.domain.vo.ApplyDetailVO;

import java.util.List;

/**
 * 采购计划明细关联科室申购单明细 Service
 *
 * @author spd
 */
public interface IPurchasePlanEntryApplyService {

    /**
     * 根据采购计划明细ID查询关联的申购明细列表
     *
     * @param entryId 采购计划明细ID
     * @return 申购明细（科室申购单单号、申购科室、申购数量、制单人、制单时间、审核人、审核时间）
     */
    List<ApplyDetailVO> listApplyDetailsByEntryId(Long entryId);

    /**
     * 根据采购计划ID查询关联的申购单号列表（去重，表头引用申购单号弹窗用）
     *
     * @param planId 采购计划ID
     * @return 申购单号列表
     */
    List<String> listApplyBillNosByPlanId(Long planId);
}

package com.spd.caigou.service.impl;

import com.spd.caigou.domain.vo.ApplyBillHeaderVO;
import com.spd.caigou.domain.vo.ApplyDetailVO;
import com.spd.caigou.mapper.PurchasePlanEntryDepApplyMapper;
import com.spd.caigou.service.IPurchasePlanEntryApplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 采购计划明细关联科室申购单明细 Service 实现（仅从 purchase_plan_entry_dep_apply 表查询）
 *
 * @author spd
 */
@Service
public class PurchasePlanEntryApplyServiceImpl implements IPurchasePlanEntryApplyService {

    @Autowired
    private PurchasePlanEntryDepApplyMapper purchasePlanEntryDepApplyMapper;

    @Override
    public List<ApplyDetailVO> listApplyDetailsByEntryId(Long entryId) {
        List<ApplyDetailVO> list = purchasePlanEntryDepApplyMapper.selectApplyDetailsByEntryId(entryId);
        return list != null ? list : new ArrayList<>();
    }

    @Override
    public List<String> listApplyBillNosByPlanId(Long planId) {
        List<String> list = purchasePlanEntryDepApplyMapper.selectBillNoListByPlanId(planId);
        return list != null ? list : new ArrayList<>();
    }

    @Override
    public List<ApplyBillHeaderVO> listApplyBillHeaderListByPlanId(Long planId) {
        List<ApplyBillHeaderVO> list = purchasePlanEntryDepApplyMapper.selectApplyBillHeaderListByPlanId(planId);
        return list != null ? list : new ArrayList<>();
    }
}

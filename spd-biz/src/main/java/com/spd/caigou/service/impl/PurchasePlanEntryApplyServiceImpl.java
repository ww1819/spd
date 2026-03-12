package com.spd.caigou.service.impl;

import com.spd.caigou.domain.vo.ApplyDetailVO;
import com.spd.caigou.mapper.PurchasePlanEntryApplyMapper;
import com.spd.caigou.mapper.PurchasePlanEntryDepApplyMapper;
import com.spd.caigou.service.IPurchasePlanEntryApplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 采购计划明细关联科室申购单明细 Service 实现（优先查 dep 表，采购计划引用的是科室申购 dep）
 *
 * @author spd
 */
@Service
public class PurchasePlanEntryApplyServiceImpl implements IPurchasePlanEntryApplyService {

    @Autowired
    private PurchasePlanEntryApplyMapper purchasePlanEntryApplyMapper;
    @Autowired
    private PurchasePlanEntryDepApplyMapper purchasePlanEntryDepApplyMapper;

    @Override
    public List<ApplyDetailVO> listApplyDetailsByEntryId(Long entryId) {
        List<ApplyDetailVO> list = purchasePlanEntryDepApplyMapper.selectApplyDetailsByEntryId(entryId);
        if (list != null && !list.isEmpty()) {
            return list;
        }
        list = purchasePlanEntryApplyMapper.selectApplyDetailsByEntryId(entryId);
        return list != null ? list : new ArrayList<>();
    }

    @Override
    public List<String> listApplyBillNosByPlanId(Long planId) {
        List<String> list = purchasePlanEntryDepApplyMapper.selectBillNoListByPlanId(planId);
        return list != null ? list : new ArrayList<>();
    }
}

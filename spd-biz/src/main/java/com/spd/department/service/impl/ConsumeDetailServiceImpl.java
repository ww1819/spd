package com.spd.department.service.impl;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.spd.department.service.IConsumeDetailService;
import com.spd.warehouse.domain.StkIoBill;
import com.spd.warehouse.mapper.StkIoBillMapper;

/**
 * 科室领用明细Service业务层处理
 * 
 * @author spd
 * @date 2025-01-27
 */
@Service
public class ConsumeDetailServiceImpl implements IConsumeDetailService
{
    @Autowired
    private StkIoBillMapper stkIoBillMapper;

    /**
     * 查询领用明细列表
     * 
     * @param stkIoBill 查询条件
     * @return 领用明细列表
     */
    @Override
    public List<Map<String, Object>> selectConsumeDetailList(StkIoBill stkIoBill)
    {
        // 设置查询条件：出库单（bill_type=201）且已审核（bill_status=2）
        stkIoBill.setBillType(201);
        stkIoBill.setBillStatus(2);
        return stkIoBillMapper.selectConsumeDetailList(stkIoBill);
    }

    /**
     * 查询领用汇总列表（按耗材汇总）
     * 
     * @param stkIoBill 查询条件
     * @return 领用汇总列表
     */
    @Override
    public List<Map<String, Object>> selectConsumeSummaryList(StkIoBill stkIoBill)
    {
        // 设置查询条件：出库单（bill_type=201）且已审核（bill_status=2）
        stkIoBill.setBillType(201);
        stkIoBill.setBillStatus(2);
        return stkIoBillMapper.selectConsumeSummaryList(stkIoBill);
    }

    /**
     * 查询领用排名列表（按金额降序）
     * 
     * @param stkIoBill 查询条件
     * @return 领用排名列表
     */
    @Override
    public List<Map<String, Object>> selectConsumeRankingList(StkIoBill stkIoBill)
    {
        // 设置查询条件：出库单（bill_type=201）且已审核（bill_status=2）
        stkIoBill.setBillType(201);
        stkIoBill.setBillStatus(2);
        return stkIoBillMapper.selectConsumeRankingList(stkIoBill);
    }
}

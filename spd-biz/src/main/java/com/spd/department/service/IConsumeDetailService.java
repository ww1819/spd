package com.spd.department.service;

import java.util.List;
import java.util.Map;
import com.spd.warehouse.domain.StkIoBill;

/**
 * 科室领用明细Service接口
 * 
 * @author spd
 * @date 2025-01-27
 */
public interface IConsumeDetailService
{
    /**
     * 查询领用明细列表
     * 
     * @param stkIoBill 查询条件
     * @return 领用明细列表
     */
    public List<Map<String, Object>> selectConsumeDetailList(StkIoBill stkIoBill);

    /**
     * 查询领用汇总列表（按耗材汇总）
     * 
     * @param stkIoBill 查询条件
     * @return 领用汇总列表
     */
    public List<Map<String, Object>> selectConsumeSummaryList(StkIoBill stkIoBill);

    /**
     * 查询领用排名列表（按金额降序）
     * 
     * @param stkIoBill 查询条件
     * @return 领用排名列表
     */
    public List<Map<String, Object>> selectConsumeRankingList(StkIoBill stkIoBill);
}

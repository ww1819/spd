package com.spd.warehouse.service;

import java.util.List;

import com.spd.warehouse.domain.StkBatch;

/**
 * 批次追溯 Service 接口
 */
public interface IStkBatchService
{
    /**
     * 查询批次详情
     *
     * @param id 批次ID
     * @return 批次
     */
    StkBatch selectStkBatchById(Long id);

    /**
     * 查询批次列表
     *
     * @param stkBatch 筛选条件
     * @return 批次列表
     */
    List<StkBatch> selectStkBatchList(StkBatch stkBatch);
}


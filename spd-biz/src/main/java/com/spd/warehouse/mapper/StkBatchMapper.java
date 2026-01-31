package com.spd.warehouse.mapper;

import com.spd.warehouse.domain.StkBatch;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * 批次表Mapper接口
 *
 * @author spd
 */
@Mapper
@Repository
public interface StkBatchMapper {

    /**
     * 新增批次
     *
     * @param stkBatch 批次
     * @return 结果
     */
    int insertStkBatch(StkBatch stkBatch);

    /**
     * 按批次号查询批次（未删除）
     *
     * @param batchNo 批次号
     * @return 批次
     */
    StkBatch selectByBatchNo(@Param("batchNo") String batchNo);

    /**
     * 按主键查询批次
     *
     * @param id 主键
     * @return 批次
     */
    StkBatch selectStkBatchById(Long id);
}

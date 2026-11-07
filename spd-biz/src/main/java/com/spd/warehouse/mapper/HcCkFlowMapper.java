package com.spd.warehouse.mapper;

import com.spd.warehouse.domain.HcCkFlow;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * 仓库流水Mapper接口 t_hc_ck_flow
 *
 * @author spd
 */
@Mapper
@Repository
public interface HcCkFlowMapper {

    /**
     * 新增仓库流水
     */
    int insertHcCkFlow(HcCkFlow flow);

    /**
     * 按出入库单统计未删除流水条数（用于拦截误删已发生库存变动的单据）
     */
    int countAliveByBillId(@Param("billId") Long billId);

    /**
     * 按单据+类型+明细统计未删除流水（报损等与盘点 bill_id 可能撞号，须带 lx）
     */
    int countAliveByBillIdLxEntryId(@Param("billId") Long billId,
                                    @Param("lx") String lx,
                                    @Param("entryId") Long entryId);
}

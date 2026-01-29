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
     * 按出库单明细id更新kc_no（收货确认后反写）
     */
    int updateKcNoByEntryId(@Param("entryId") Long entryId, @Param("kcNo") Long kcNo);
}

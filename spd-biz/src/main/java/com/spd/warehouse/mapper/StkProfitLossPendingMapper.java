package com.spd.warehouse.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

import com.spd.warehouse.domain.StkProfitLossPending;

@Mapper
public interface StkProfitLossPendingMapper {

    int insertStkProfitLossPending(StkProfitLossPending pending);

    StkProfitLossPending selectStkProfitLossPendingById(@Param("id") Long id);

    List<StkProfitLossPending> selectStkProfitLossPendingList(StkProfitLossPending pending);

    int updatePendingStatusById(@Param("id") Long id,
                                @Param("applyStatus") String applyStatus,
                                @Param("settlementEffectStatus") String settlementEffectStatus,
                                @Param("updateBy") String updateBy);
}


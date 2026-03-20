package com.spd.warehouse.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.spd.warehouse.domain.StkProfitLossPending;

@Mapper
public interface StkProfitLossPendingMapper {

    int insertStkProfitLossPending(StkProfitLossPending pending);
}


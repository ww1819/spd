package com.spd.system.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import java.util.Date;

import com.spd.system.domain.hc.HcCustomerPeriodLog;

/**
 * 耗材客户实际使用/停用时间段 hc_customer_period_log
 */
public interface HcCustomerPeriodLogMapper {

  List<HcCustomerPeriodLog> selectByTenantId(@Param("tenantId") String tenantId);

  /** 查询该租户该类型下 end_time 为空的一条（当前未结束的时段） */
  HcCustomerPeriodLog selectLastWithNullEnd(@Param("tenantId") String tenantId, @Param("periodType") String periodType);

  /** 更新某条记录的结束时间 */
  int updateEndTime(@Param("periodId") String periodId, @Param("endTime") Date endTime);

  int insert(HcCustomerPeriodLog record);
}

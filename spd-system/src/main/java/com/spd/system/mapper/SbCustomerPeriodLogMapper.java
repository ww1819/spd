package com.spd.system.mapper;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.spd.system.domain.SbCustomerPeriodLog;

/**
 * 客户实际使用/停用时间段记录 sb_customer_period_log 数据层
 */
public interface SbCustomerPeriodLogMapper {

  List<SbCustomerPeriodLog> selectByCustomerId(String customerId);

  /** 查询某客户最近一条 usage 类型记录的 end_time，用于计算下一段 usage 的 start */
  Date selectLastUsageEndTime(@Param("customerId") String customerId);

  /** 查询某客户该类型下 end_time 为空的一条（当前未结束的时段） */
  SbCustomerPeriodLog selectLastWithNullEnd(@Param("customerId") String customerId, @Param("periodType") String periodType);

  /** 更新某条记录的结束时间 */
  int updateEndTime(@Param("periodId") String periodId, @Param("endTime") Date endTime);

  int insert(SbCustomerPeriodLog log);
}

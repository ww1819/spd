package com.spd.system.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.spd.system.domain.SbCustomerPeriodLog;

/**
 * 客户实际使用/停用时间段记录 sb_customer_period_log 数据层
 */
public interface SbCustomerPeriodLogMapper {

  List<SbCustomerPeriodLog> selectByCustomerId(String customerId);

  /** 查询某客户最近一条 usage 类型记录的 end_time，用于计算下一段 usage 的 start */
  java.util.Date selectLastUsageEndTime(@Param("customerId") String customerId);

  int insert(SbCustomerPeriodLog log);
}

package com.spd.system.mapper;

import java.util.List;

import com.spd.system.domain.SbCustomerStatusLog;

/**
 * 客户启停用记录 sb_customer_status_log 数据层
 */
public interface SbCustomerStatusLogMapper {

  List<SbCustomerStatusLog> selectByCustomerId(String customerId);

  int insert(SbCustomerStatusLog log);
}

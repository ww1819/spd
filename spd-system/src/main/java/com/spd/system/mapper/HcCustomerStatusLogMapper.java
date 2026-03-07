package com.spd.system.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.spd.system.domain.hc.HcCustomerStatusLog;

/**
 * 耗材客户启停用记录 hc_customer_status_log
 */
public interface HcCustomerStatusLogMapper {

  List<HcCustomerStatusLog> selectByTenantId(@Param("tenantId") String tenantId);

  int insert(HcCustomerStatusLog record);
}

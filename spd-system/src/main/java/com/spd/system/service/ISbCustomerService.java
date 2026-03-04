package com.spd.system.service;

import java.util.List;

import com.spd.system.domain.SbCustomer;
import com.spd.system.domain.SbCustomerPeriodLog;
import com.spd.system.domain.SbCustomerStatusLog;

/**
 * 设备系统客户（SaaS租户） 服务接口
 */
public interface ISbCustomerService {

  List<SbCustomer> selectSbCustomerList(SbCustomer customer);

  SbCustomer selectSbCustomerById(String customerId);

  /** 根据客户编码查询（用于登录等） */
  SbCustomer selectSbCustomerByCode(String customerCode);

  boolean checkSbCustomerCodeUnique(SbCustomer customer);

  int insertSbCustomer(SbCustomer customer);

  int updateSbCustomer(SbCustomer customer);

  int deleteSbCustomerById(String customerId);

  List<SbCustomerStatusLog> selectStatusLogList(String customerId);

  List<SbCustomerPeriodLog> selectPeriodLogList(String customerId);
}

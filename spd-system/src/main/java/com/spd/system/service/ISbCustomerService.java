package com.spd.system.service;

import java.util.List;

import com.spd.system.domain.SbCustomer;
import com.spd.system.domain.SbCustomerPeriodLog;
import com.spd.system.domain.SbCustomerStatusLog;
import com.spd.system.domain.hc.HcCustomerPeriodLog;
import com.spd.system.domain.hc.HcCustomerStatusLog;

/**
 * 设备系统客户（SaaS租户） 服务接口
 */
public interface ISbCustomerService {

  List<SbCustomer> selectSbCustomerList(SbCustomer customer);

  SbCustomer selectSbCustomerById(String customerId);

  /** 根据客户编码查询（用于登录等） */
  SbCustomer selectSbCustomerByCode(String customerCode);

  /** 按 tenant_key（TenantEnum.name）查询 */
  SbCustomer selectSbCustomerByTenantKey(String tenantKey);

  boolean checkSbCustomerCodeUnique(SbCustomer customer);

  int insertSbCustomer(SbCustomer customer);

  int updateSbCustomer(SbCustomer customer);

  int deleteSbCustomerById(String customerId);

  /**
   * 客户启停用（设备侧，必须填写原因，会写入 sb 启停用记录与时间段）
   */
  int changeStatus(String customerId, String status, String statusChangeReason);

  List<SbCustomerStatusLog> selectStatusLogList(String customerId);

  List<SbCustomerPeriodLog> selectPeriodLogList(String customerId);

  /**
   * 耗材侧客户启停用（更新 hc_status，写入 hc_customer_status_log、hc_customer_period_log）
   */
  int changeHcStatus(String customerId, String status, String statusChangeReason);

  List<HcCustomerStatusLog> selectHcStatusLogList(String tenantId);

  List<HcCustomerPeriodLog> selectHcPeriodLogList(String tenantId);
}

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

  /**
   * 到达计划停用时间时自动将客户设为已停用，并生成启停用记录与启停用时间段记录，停用原因为「已到达计划停用时间」。
   * 若客户已停用或未到计划停用时间则不做任何操作。通常在登录校验发现已到期时调用。
   */
  void autoDisableByPlannedTime(String customerId);

  /**
   * 设备功能重置：若 super 组和 super_01 不存在则创建；将默认对客户开放的权限开放给客户、super 组、super_01 用户。
   */
  void resetEquipmentFunctions(String customerId);

  /**
   * 耗材功能重置：若 super 组（岗位）和 super_01 不存在则创建；重置耗材客户菜单权限、super 岗位菜单权限、super_01 菜单权限为系统设置下非平台管理功能。
   */
  void resetMaterialFunctions(String customerId);
}

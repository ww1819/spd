package com.spd.system.service;

import java.util.List;

import com.spd.system.domain.SbCustomerMenuPeriodLog;
import com.spd.system.domain.SbCustomerMenuStatusLog;
import com.spd.system.domain.vo.SbCustomerMenuManageVo;

/**
 * 客户菜单功能管理：对客户已具备的功能做启用/停用，仅平台用户可访问
 */
public interface ISbCustomerMenuManageService {

  /** 客户已分配菜单列表（含菜单名、状态），仅 M/C 类型 */
  List<SbCustomerMenuManageVo> listMenusByCustomerId(String customerId);

  /** 启停用：必填原因，写记录与时间段 */
  int changeStatus(String customerId, String menuId, String status, String reason);

  List<SbCustomerMenuStatusLog> getStatusLogList(String customerId, String menuId);

  List<SbCustomerMenuPeriodLog> getPeriodLogList(String customerId, String menuId);
}

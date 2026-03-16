package com.spd.system.service;

import java.util.List;

import com.spd.system.domain.hc.HcCustomerMenuPeriodLog;
import com.spd.system.domain.hc.HcCustomerMenuStatusLog;
import com.spd.system.domain.vo.HcCustomerMenuManageVo;

/**
 * 耗材客户菜单功能管理：对客户（租户）在耗材侧已具备功能做启用/停用
 */
public interface IHcCustomerMenuManageService {

  List<HcCustomerMenuManageVo> listMenusByTenantId(String tenantId);

  int changeStatus(String tenantId, Long menuId, String status, String reason);

  List<HcCustomerMenuStatusLog> getStatusLogList(String tenantId, Long menuId);

  List<HcCustomerMenuPeriodLog> getPeriodLogList(String tenantId, Long menuId);
}

package com.spd.system.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.spd.system.domain.hc.HcCustomerMenuStatusLog;

/**
 * 耗材客户菜单功能启停用记录 hc_customer_menu_status_log
 */
public interface HcCustomerMenuStatusLogMapper {

  List<HcCustomerMenuStatusLog> selectByTenantIdAndMenuId(@Param("tenantId") String tenantId, @Param("menuId") Long menuId);

  int insert(HcCustomerMenuStatusLog record);
}

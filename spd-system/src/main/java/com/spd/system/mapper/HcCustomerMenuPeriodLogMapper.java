package com.spd.system.mapper;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.spd.system.domain.hc.HcCustomerMenuPeriodLog;

/**
 * 耗材客户菜单功能启停用时间段 hc_customer_menu_period_log
 */
public interface HcCustomerMenuPeriodLogMapper {

  List<HcCustomerMenuPeriodLog> selectByTenantIdAndMenuId(@Param("tenantId") String tenantId, @Param("menuId") Long menuId);

  HcCustomerMenuPeriodLog selectLastWithNullEnd(@Param("tenantId") String tenantId, @Param("menuId") Long menuId, @Param("periodType") String periodType);

  int updateEndTime(@Param("periodId") String periodId, @Param("endTime") java.util.Date endTime);

  int insert(HcCustomerMenuPeriodLog record);
}

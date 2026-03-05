package com.spd.system.mapper;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.spd.system.domain.SbCustomerMenuPeriodLog;

/**
 * 客户菜单功能启停用时间段 数据层
 */
public interface SbCustomerMenuPeriodLogMapper {

  List<SbCustomerMenuPeriodLog> selectByCustomerIdAndMenuId(@Param("customerId") String customerId, @Param("menuId") String menuId);

  SbCustomerMenuPeriodLog selectLastWithNullEnd(@Param("customerId") String customerId, @Param("menuId") String menuId, @Param("periodType") String periodType);

  int updateEndTime(@Param("periodId") String periodId, @Param("endTime") Date endTime);

  int insert(SbCustomerMenuPeriodLog log);
}

package com.spd.system.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.spd.system.domain.SbCustomerMenuStatusLog;

/**
 * 客户菜单功能启停用记录 数据层
 */
public interface SbCustomerMenuStatusLogMapper {

  List<SbCustomerMenuStatusLog> selectByCustomerIdAndMenuId(@Param("customerId") String customerId, @Param("menuId") String menuId);

  int insert(SbCustomerMenuStatusLog log);
}

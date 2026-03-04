package com.spd.system.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.spd.system.domain.SbCustomerMenu;

/**
 * 设备系统客户菜单权限表 sb_customer_menu 数据层
 */
public interface SbCustomerMenuMapper {

  List<SbCustomerMenu> selectSbCustomerMenuListByCustomerId(String customerId);

  List<String> selectMenuIdsByCustomerId(String customerId);

  int deleteSbCustomerMenuByCustomerId(String customerId);

  int batchSbCustomerMenu(List<SbCustomerMenu> list);

  /**
   * 查询客户是否已分配某菜单
   */
  int countByCustomerIdAndMenuId(@Param("customerId") String customerId, @Param("menuId") String menuId);
}

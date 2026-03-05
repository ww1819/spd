package com.spd.system.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.spd.system.domain.SbCustomerMenu;
import com.spd.system.domain.vo.SbCustomerMenuManageVo;

/**
 * 设备系统客户菜单权限表 sb_customer_menu 数据层
 */
public interface SbCustomerMenuMapper {

  List<SbCustomerMenu> selectSbCustomerMenuListByCustomerId(String customerId);

  List<String> selectMenuIdsByCustomerId(String customerId);

  /** 客户菜单功能管理：列表（含菜单名称、状态），仅目录和菜单类型 */
  List<SbCustomerMenuManageVo> selectListWithMenuNameByCustomerId(String customerId);

  int deleteSbCustomerMenuByCustomerId(String customerId);

  int updateIsEnabled(@Param("customerId") String customerId, @Param("menuId") String menuId, @Param("isEnabled") String isEnabled);

  int insertSbCustomerMenu(SbCustomerMenu record);

  int batchSbCustomerMenu(List<SbCustomerMenu> list);

  int updateStatus(@Param("customerId") String customerId, @Param("menuId") String menuId, @Param("status") String status);

  /**
   * 查询客户是否已分配某菜单
   */
  int countByCustomerIdAndMenuId(@Param("customerId") String customerId, @Param("menuId") String menuId);
}

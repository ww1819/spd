package com.spd.system.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.spd.system.domain.hc.HcCustomerMenu;
import com.spd.system.domain.vo.HcCustomerMenuManageVo;

/**
 * 耗材客户菜单权限 hc_customer_menu
 */
public interface HcCustomerMenuMapper {

  List<HcCustomerMenuManageVo> selectListWithMenuNameByTenantId(String tenantId);

  List<Long> selectMenuIdsByTenantId(String tenantId);

  /** 租户已开通且未暂停的菜单 ID（与 countByTenantIdAndMenuId 条件一致） */
  List<Long> selectEnabledMenuIdsByTenantId(@Param("tenantId") String tenantId);

  /** 耗材租户下已被暂停的菜单ID（status='1'） */
  List<Long> selectPausedMenuIdsByTenantId(String tenantId);

  int deleteByTenantId(String tenantId);

  int countByTenantIdAndMenuId(@Param("tenantId") String tenantId, @Param("menuId") Long menuId);

  int updateStatus(@Param("tenantId") String tenantId, @Param("menuId") Long menuId, @Param("status") String status);

  int insert(HcCustomerMenu record);

  int batchInsert(List<HcCustomerMenu> list);
}

package com.spd.system.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.spd.system.domain.hc.HcUserPermissionMenu;

/**
 * 耗材用户菜单权限 hc_user_permission_menu
 */
public interface HcUserPermissionMenuMapper {

  int batchInsert(List<HcUserPermissionMenu> list);

  /** 按用户与租户删除（耗材功能重置时清该租户下 super_01 的菜单） */
  int deleteByUserIdAndTenantId(@Param("userId") Long userId, @Param("tenantId") String tenantId);

  /** 客户收回菜单权限时：删除该租户下所有用户对这些菜单的授权 */
  int deleteByTenantIdAndMenuIds(@Param("tenantId") String tenantId, @Param("menuIds") List<Long> menuIds);

  /** 耗材客户菜单重置/收回后：删除 hc_user_permission_menu 中已不在 hc_customer_menu 内的行 */
  int deleteHcUserPermissionMenusNotInHcCustomerMenus(@Param("tenantId") String tenantId);
}

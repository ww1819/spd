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
}

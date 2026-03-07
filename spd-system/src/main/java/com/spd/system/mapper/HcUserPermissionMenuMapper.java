package com.spd.system.mapper;

import java.util.List;

import com.spd.system.domain.hc.HcUserPermissionMenu;

/**
 * 耗材用户菜单权限 hc_user_permission_menu
 */
public interface HcUserPermissionMenuMapper {

  int batchInsert(List<HcUserPermissionMenu> list);
}

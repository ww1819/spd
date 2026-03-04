package com.spd.system.mapper;

import java.util.List;

import com.spd.system.domain.SbRoleMenu;

/**
 * 设备角色和菜单关联表 sb_role_menu 数据层
 */
public interface SbRoleMenuMapper {

  /**
   * 查询菜单是否存在设备角色
   */
  int checkSbMenuExistRole(String menuId);

  /**
   * 根据角色ID删除设备角色和菜单关联
   */
  int deleteSbRoleMenuByRoleId(String roleId);

  /**
   * 批量删除角色与菜单关联
   */
  int deleteSbRoleMenu(String[] roleIds);

  /**
   * 批量新增角色菜单信息
   */
  int batchSbRoleMenu(List<SbRoleMenu> list);
}


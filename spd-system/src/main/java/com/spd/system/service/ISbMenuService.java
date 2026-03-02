package com.spd.system.service;

import java.util.List;

import com.spd.common.core.domain.entity.SysMenu;
import com.spd.system.domain.SbMenu;
import com.spd.system.domain.vo.RouterVo;

/**
 * 设备菜单业务接口（基于 sb_menu）
 */
public interface ISbMenuService {

  /**
   * 查询设备菜单列表
   */
  List<SbMenu> selectSbMenuList(SbMenu menu);

  /**
   * 根据用户ID查询设备菜单树
   */
  List<SbMenu> selectSbMenuTreeByUserId(Long userId);

  /**
   * 新增设备菜单
   */
  int insertSbMenu(SbMenu menu);

  /**
   * 修改设备菜单
   */
  int updateSbMenu(SbMenu menu);

  /**
   * 删除设备菜单
   */
  int deleteSbMenuById(Long menuId);

  /**
   * 校验设备菜单名称是否唯一
   */
  boolean checkSbMenuNameUnique(SbMenu menu);

  /**
   * 构建前端路由所需要的菜单（使用 SysMenu 结构）
   */
  List<RouterVo> buildMenus(List<SysMenu> menus);

  /**
   * 设备菜单树转为 SysMenu 列表，便于与现有路由构建逻辑复用。
   */
  List<SysMenu> convertToSysMenus(List<SbMenu> sbMenus);

  /**
   * 根据用户ID查询设备菜单权限标识集合
   */
  java.util.Set<String> selectSbMenuPermsByUserId(Long userId);
}


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
   * 根据菜单ID查询设备菜单详情
   */
  SbMenu selectSbMenuById(String menuId);

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
   * 逻辑删除设备菜单
   */
  int deleteSbMenuById(String menuId);

  /**
   * 根据设备菜单树构建前端路由（UUID7 主键）
   */
  List<RouterVo> buildMenusFromSb(List<SbMenu> menus);

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

  /** 租户时仅返回该客户已启用菜单的权限 */
  java.util.Set<String> selectSbMenuPermsByUserIdAndCustomer(Long userId, String customerId);

  /**
   * 用于「客户菜单权限」分配的菜单树（排除客户管理及其按钮，租户不可被分配客户管理）
   */
  List<SbMenu> selectSbMenuTreeForCustomerAssign();

  /**
   * 某客户已开启的菜单树（用于工作组/用户权限分配）
   */
  List<SbMenu> selectSbMenuTreeByCustomerIdEnabling(String customerId);

  /**
   * 设备菜单树（含 default_open_to_customer），用于批量设置默认对客户开放
   */
  List<SbMenu> selectSbMenuTreeForDefaultOpenBatch();

  /**
   * 批量设置设备菜单「默认对客户开放」：先全部置否，再对 menuIds 置是（平台专属菜单始终为否）
   */
  void batchSetDefaultOpenToCustomer(List<String> menuIds);
}


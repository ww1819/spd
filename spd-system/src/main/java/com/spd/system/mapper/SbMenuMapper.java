package com.spd.system.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.spd.system.domain.SbMenu;

/**
 * 设备菜单表 sb_menu 数据层
 */
public interface SbMenuMapper {

  /**
   * 查询设备菜单列表
   *
   * @param menu 菜单查询条件
   * @return 菜单列表
   */
  List<SbMenu> selectSbMenuList(SbMenu menu);

  /**
   * 查询设备菜单树（全部）
   *
   * @return 菜单列表
   */
  List<SbMenu> selectSbMenuTreeAll();

  /**
   * 根据用户ID查询设备菜单树
   *
   * @param userId 用户ID
   * @return 菜单列表
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
  int deleteSbMenuById(String menuId);

  /**
   * 校验菜单名称是否唯一
   */
  SbMenu checkSbMenuNameUnique(@Param("menuName") String menuName, @Param("parentId") String parentId);

  /**
   * 根据用户ID查询设备菜单权限标识集合
   */
  List<String> selectSbMenuPermsByUserId(Long userId);
}


package com.spd.system.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.spd.system.domain.SbUserPermissionMenu;

/**
 * 设备用户菜单权限 sb_user_permission_menu 数据层
 */
public interface SbUserPermissionMenuMapper {

  List<String> selectMenuIdsByUserId(@Param("userId") Long userId, @Param("customerId") String customerId);

  int deleteByUserId(@Param("userId") Long userId, @Param("deleteBy") String deleteBy);

  int insert(SbUserPermissionMenu row);

  int batchInsert(List<SbUserPermissionMenu> list);
}

package com.spd.system.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.spd.system.domain.SbRole;

/**
 * 设备角色表 sb_role 数据层
 */
public interface SbRoleMapper {

  /**
   * 查询设备角色列表
   */
  List<SbRole> selectSbRoleList(SbRole role);

  /**
   * 根据用户ID查询设备角色列表
   */
  List<SbRole> selectSbRolePermissionByUserId(Long userId);

  /**
   * 查询所有设备角色
   */
  List<SbRole> selectSbRoleAll();

  /**
   * 根据用户ID查询设备角色ID列表
   */
  List<Long> selectSbRoleListByUserId(Long userId);

  /**
   * 根据角色ID查询角色
   */
  SbRole selectSbRoleById(Long roleId);

  /**
   * 校验角色名称是否唯一
   */
  SbRole checkSbRoleNameUnique(String roleName);

  /**
   * 校验角色权限是否唯一
   */
  SbRole checkSbRoleKeyUnique(String roleKey);

  /**
   * 新增角色
   */
  int insertSbRole(SbRole role);

  /**
   * 修改角色
   */
  int updateSbRole(SbRole role);

  /**
   * 批量逻辑删除角色
   */
  int deleteSbRoleByIds(@Param("roleIds") Long[] roleIds);

  /**
   * 逻辑删除角色
   */
  int deleteSbRoleById(Long roleId);
}


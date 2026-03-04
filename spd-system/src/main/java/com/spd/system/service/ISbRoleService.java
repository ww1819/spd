package com.spd.system.service;

import java.util.List;
import java.util.Set;

import com.spd.system.domain.SbRole;
import com.spd.system.domain.SbUserRole;

/**
 * 设备角色业务接口（基于 sb_role）
 */
public interface ISbRoleService {

  /**
   * 根据条件查询设备角色列表
   */
  List<SbRole> selectSbRoleList(SbRole role);

  /**
   * 根据用户ID查询设备角色列表
   */
  List<SbRole> selectSbRolesByUserId(Long userId);

  /**
   * 根据用户ID查询设备角色权限（roleKey）
   */
  Set<String> selectSbRolePermissionByUserId(Long userId);

  /**
   * 查询所有设备角色
   */
  List<SbRole> selectSbRoleAll();

  /**
   * 根据用户ID获取设备角色ID集合
   */
  List<String> selectSbRoleListByUserId(Long userId);

  /**
   * 通过角色ID查询设备角色
   */
  SbRole selectSbRoleById(String roleId);

  /**
   * 校验设备角色名称是否唯一
   */
  boolean checkSbRoleNameUnique(SbRole role);

  /**
   * 校验设备角色权限是否唯一
   */
  boolean checkSbRoleKeyUnique(SbRole role);

  /**
   * 新增保存设备角色信息
   */
  int insertSbRole(SbRole role);

  /**
   * 修改保存设备角色信息
   */
  int updateSbRole(SbRole role);

  /**
   * 修改设备角色状态
   */
  int updateSbRoleStatus(SbRole role);

  /**
   * 删除设备角色
   */
  int deleteSbRoleById(String roleId);

  /**
   * 批量删除设备角色
   */
  int deleteSbRoleByIds(String[] roleIds);

  /**
   * 通过角色ID查询设备用户数量
   */
  int countSbUserRoleByRoleId(String roleId);

  /**
   * 取消授权设备用户角色
   */
  int deleteSbAuthUser(SbUserRole userRole);

  /**
   * 批量取消授权设备用户角色
   */
  int deleteSbAuthUsers(String roleId, Long[] userIds);

  /**
   * 批量选择授权设备用户角色
   */
  int insertSbAuthUsers(String roleId, Long[] userIds);
}


package com.spd.system.mapper;

import java.util.List;

import com.spd.system.domain.SbUserRole;

/**
 * 设备用户和角色关联表 sb_user_role 数据层
 */
public interface SbUserRoleMapper {

  /**
   * 根据用户ID删除设备用户角色关联
   */
  int deleteSbUserRoleByUserId(Long userId);

  /**
   * 统计设备角色使用数量
   */
  int countSbUserRoleByRoleId(Long roleId);

  /**
   * 批量删除用户与设备角色关联
   */
  int deleteSbUserRole(Long[] userIds);

  /**
   * 批量新增设备用户角色关联
   */
  int batchSbUserRole(List<SbUserRole> list);

  /**
   * 删除单个用户和设备角色关联
   */
  int deleteSbUserRoleInfo(SbUserRole userRole);
}


package com.spd.system.service;

import java.util.List;

import com.spd.system.domain.SbWorkGroup;

/**
 * 设备系统工作组 业务层
 */
public interface ISbWorkGroupService {

  List<SbWorkGroup> selectListByCustomerId(String customerId);

  SbWorkGroup selectByGroupId(String groupId);

  int insertSbWorkGroup(SbWorkGroup group);

  int updateSbWorkGroup(SbWorkGroup group);

  int deleteByGroupId(String groupId);

  List<Long> selectUserIdsByGroupId(String groupId);

  int addUsersToGroup(String groupId, Long[] userIds);

  int removeUserFromGroup(String groupId, Long userId);

  /** 查询用户在某客户下所属的工作组ID列表（用于用户编辑回显） */
  List<String> selectGroupIdsByUserId(Long userId, String customerId);

  /** 设置用户在某客户下的工作组归属（先删后插，写入 sb_work_group_user） */
  void setUserWorkGroups(Long userId, String customerId, String[] groupIds);


  List<String> selectMenuIdsByGroupId(String groupId);

  int saveGroupMenus(String groupId, String customerId, String[] menuIds);

  List<Long> selectWarehouseIdsByGroupId(String groupId);

  int saveGroupWarehouses(String groupId, String customerId, Long[] warehouseIds);

  List<Long> selectDeptIdsByGroupId(String groupId);

  int saveGroupDepts(String groupId, String customerId, Long[] deptIds);

  /** 将工作组的菜单、仓库、科室权限同步到组内所有用户（写入用户权限表） */
  int syncToGroupUsers(String groupId);

  /**
   * 判断用户是否属于指定客户下的 super 组（super 组用户不受科室/仓库权限限制，可看客户下全部）
   * @param userId 用户ID
   * @param customerId 客户ID，为空则返回 false
   * @return 是否在 super 组
   */
  boolean isUserInSuperGroup(Long userId, String customerId);
}

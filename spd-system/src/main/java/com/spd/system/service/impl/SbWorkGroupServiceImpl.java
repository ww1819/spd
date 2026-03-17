package com.spd.system.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.spd.common.utils.SecurityUtils;
import com.spd.common.utils.StringUtils;
import com.spd.common.utils.uuid.UUID7;
import com.spd.system.domain.SbWorkGroup;
import com.spd.system.domain.SbWorkGroupDept;
import com.spd.system.domain.SbWorkGroupMenu;
import com.spd.system.domain.SbWorkGroupUser;
import com.spd.system.domain.SbWorkGroupWarehouse;
import com.spd.system.domain.SbUserPermissionDept;
import com.spd.system.domain.SbUserPermissionMenu;
import com.spd.system.domain.SbUserPermissionWarehouse;
import com.spd.system.mapper.SbWorkGroupDeptMapper;
import com.spd.system.mapper.SbWorkGroupMapper;
import com.spd.system.mapper.SbWorkGroupMenuMapper;
import com.spd.system.mapper.SbWorkGroupUserMapper;
import com.spd.system.mapper.SbWorkGroupWarehouseMapper;
import com.spd.system.mapper.SbUserPermissionDeptMapper;
import com.spd.system.mapper.SbUserPermissionMenuMapper;
import com.spd.system.mapper.SbUserPermissionWarehouseMapper;
import com.spd.system.service.ISbWorkGroupService;

/**
 * 设备系统工作组 服务实现
 */
@Service
public class SbWorkGroupServiceImpl implements ISbWorkGroupService {

  @Autowired
  private SbWorkGroupMapper sbWorkGroupMapper;
  @Autowired
  private SbWorkGroupUserMapper sbWorkGroupUserMapper;
  @Autowired
  private SbWorkGroupMenuMapper sbWorkGroupMenuMapper;
  @Autowired
  private SbWorkGroupWarehouseMapper sbWorkGroupWarehouseMapper;
  @Autowired
  private SbWorkGroupDeptMapper sbWorkGroupDeptMapper;
  @Autowired
  private SbUserPermissionMenuMapper sbUserPermissionMenuMapper;
  @Autowired
  private SbUserPermissionWarehouseMapper sbUserPermissionWarehouseMapper;
  @Autowired
  private SbUserPermissionDeptMapper sbUserPermissionDeptMapper;

  @Override
  public List<SbWorkGroup> selectListByCustomerId(String customerId) {
    return sbWorkGroupMapper.selectListByCustomerId(customerId);
  }

  @Override
  public SbWorkGroup selectByGroupId(String groupId) {
    return sbWorkGroupMapper.selectByGroupId(groupId);
  }

  @Override
  public int insertSbWorkGroup(SbWorkGroup group) {
    if (StringUtils.isEmpty(group.getGroupId())) {
      group.setGroupId(UUID7.generateUUID7());
    }
    group.setCreateBy(SecurityUtils.getUserIdStr());
    return sbWorkGroupMapper.insertSbWorkGroup(group);
  }

  @Override
  public int updateSbWorkGroup(SbWorkGroup group) {
    group.setUpdateBy(SecurityUtils.getUserIdStr());
    return sbWorkGroupMapper.updateSbWorkGroup(group);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public int deleteByGroupId(String groupId) {
    String deleteBy = SecurityUtils.getUserIdStr();
    sbWorkGroupMenuMapper.deleteByGroupId(groupId, deleteBy);
    sbWorkGroupWarehouseMapper.deleteByGroupId(groupId, deleteBy);
    sbWorkGroupDeptMapper.deleteByGroupId(groupId, deleteBy);
    return sbWorkGroupMapper.deleteByGroupId(groupId, deleteBy);
  }

  @Override
  public List<Long> selectUserIdsByGroupId(String groupId) {
    return sbWorkGroupUserMapper.selectUserIdsByGroupId(groupId);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public int addUsersToGroup(String groupId, Long[] userIds) {
    if (userIds == null || userIds.length == 0) return 0;
    SbWorkGroup group = sbWorkGroupMapper.selectByGroupId(groupId);
    if (group == null) return 0;
    String createBy = SecurityUtils.getUserIdStr();
    int n = 0;
    for (Long userId : userIds) {
      if (sbWorkGroupUserMapper.countByGroupIdAndUserId(groupId, userId) > 0) continue;
      SbWorkGroupUser wgu = new SbWorkGroupUser();
      wgu.setGroupId(groupId);
      wgu.setUserId(userId);
      wgu.setCustomerId(group.getCustomerId());
      wgu.setCreateBy(createBy);
      sbWorkGroupUserMapper.insertSbWorkGroupUser(wgu);
      n++;
    }
    return n;
  }

  @Override
  public int removeUserFromGroup(String groupId, Long userId) {
    return sbWorkGroupUserMapper.deleteByGroupIdAndUserId(groupId, userId, SecurityUtils.getUserIdStr());
  }

  @Override
  public List<String> selectGroupIdsByUserId(Long userId, String customerId) {
    return sbWorkGroupUserMapper.selectGroupIdsByUserId(userId, customerId);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void setUserWorkGroups(Long userId, String customerId, String[] groupIds) {
    String createBy = SecurityUtils.getUserIdStr();
    sbWorkGroupUserMapper.deleteByUserIdAndCustomerId(userId, customerId, createBy);
    if (groupIds == null || groupIds.length == 0) return;
    java.util.Set<String> seen = new java.util.LinkedHashSet<>();
    for (String groupId : groupIds) {
      if (StringUtils.isEmpty(groupId)) continue;
      if (!seen.add(groupId)) continue;
      SbWorkGroupUser wgu = new SbWorkGroupUser();
      wgu.setGroupId(groupId);
      wgu.setUserId(userId);
      wgu.setCustomerId(customerId);
      wgu.setCreateBy(createBy);
      sbWorkGroupUserMapper.insertSbWorkGroupUser(wgu);
    }
  }

  @Override
  public List<String> selectMenuIdsByGroupId(String groupId) {
    SbWorkGroup group = sbWorkGroupMapper.selectByGroupId(groupId);
    if (group != null && StringUtils.isNotEmpty(group.getCustomerId())) {
      return sbWorkGroupMenuMapper.selectMenuIdsByGroupIdAndCustomerId(groupId, group.getCustomerId());
    }
    return sbWorkGroupMenuMapper.selectMenuIdsByGroupId(groupId);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public int saveGroupMenus(String groupId, String customerId, String[] menuIds) {
    String createBy = SecurityUtils.getUserIdStr();
    sbWorkGroupMenuMapper.deleteByGroupId(groupId, createBy);
    if (menuIds == null || menuIds.length == 0) return 0;
    List<SbWorkGroupMenu> list = new ArrayList<>();
    java.util.Set<String> seen = new java.util.LinkedHashSet<>();
    for (String menuId : menuIds) {
      if (StringUtils.isEmpty(menuId)) continue;
      if (!seen.add(menuId)) continue;
      SbWorkGroupMenu m = new SbWorkGroupMenu();
      m.setId(UUID7.generateUUID7());
      m.setGroupId(groupId);
      m.setMenuId(menuId);
      m.setCustomerId(customerId);
      m.setCreateBy(createBy);
      list.add(m);
    }
    if (list.isEmpty()) return 0;
    return sbWorkGroupMenuMapper.batchInsert(list);
  }

  @Override
  public List<Long> selectWarehouseIdsByGroupId(String groupId) {
    return sbWorkGroupWarehouseMapper.selectWarehouseIdsByGroupId(groupId);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public int saveGroupWarehouses(String groupId, String customerId, Long[] warehouseIds) {
    String createBy = SecurityUtils.getUserIdStr();
    sbWorkGroupWarehouseMapper.deleteByGroupId(groupId, createBy);
    if (warehouseIds == null || warehouseIds.length == 0) return 0;
    List<SbWorkGroupWarehouse> list = new ArrayList<>();
    for (Long warehouseId : warehouseIds) {
      if (warehouseId == null) continue;
      SbWorkGroupWarehouse w = new SbWorkGroupWarehouse();
      w.setId(UUID7.generateUUID7());
      w.setGroupId(groupId);
      w.setWarehouseId(warehouseId);
      w.setCustomerId(customerId);
      w.setCreateBy(createBy);
      list.add(w);
    }
    if (list.isEmpty()) return 0;
    return sbWorkGroupWarehouseMapper.batchInsert(list);
  }

  @Override
  public List<Long> selectDeptIdsByGroupId(String groupId) {
    return sbWorkGroupDeptMapper.selectDeptIdsByGroupId(groupId);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public int saveGroupDepts(String groupId, String customerId, Long[] deptIds) {
    String createBy = SecurityUtils.getUserIdStr();
    sbWorkGroupDeptMapper.deleteByGroupId(groupId, createBy);
    if (deptIds == null || deptIds.length == 0) return 0;
    List<SbWorkGroupDept> list = new ArrayList<>();
    for (Long deptId : deptIds) {
      if (deptId == null) continue;
      SbWorkGroupDept d = new SbWorkGroupDept();
      d.setId(UUID7.generateUUID7());
      d.setGroupId(groupId);
      d.setDeptId(deptId);
      d.setCustomerId(customerId);
      d.setCreateBy(createBy);
      list.add(d);
    }
    if (list.isEmpty()) return 0;
    return sbWorkGroupDeptMapper.batchInsert(list);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public int syncToGroupUsers(String groupId) {
    SbWorkGroup group = sbWorkGroupMapper.selectByGroupId(groupId);
    if (group == null) return 0;
    String customerId = group.getCustomerId();
    List<Long> userIds = sbWorkGroupUserMapper.selectUserIdsByGroupId(groupId);
    if (userIds == null || userIds.isEmpty()) return 0;
    String createBy = SecurityUtils.getUserIdStr();

    List<String> menuIds = sbWorkGroupMenuMapper.selectMenuIdsByGroupId(groupId);
    List<Long> warehouseIds = sbWorkGroupWarehouseMapper.selectWarehouseIdsByGroupId(groupId);
    List<Long> deptIds = sbWorkGroupDeptMapper.selectDeptIdsByGroupId(groupId);

    int count = 0;
    for (Long userId : userIds) {
      if (menuIds != null && !menuIds.isEmpty()) {
        for (String menuId : menuIds) {
          if (StringUtils.isEmpty(menuId)) continue;
          SbUserPermissionMenu pm = new SbUserPermissionMenu();
          pm.setId(UUID7.generateUUID7());
          pm.setUserId(userId);
          pm.setCustomerId(customerId);
          pm.setMenuId(menuId);
          pm.setCreateBy(createBy);
          try {
            sbUserPermissionMenuMapper.insert(pm);
            count++;
          } catch (Exception e) {
            // uk 冲突则忽略，表示已有
          }
        }
      }
      if (warehouseIds != null && !warehouseIds.isEmpty()) {
        for (Long warehouseId : warehouseIds) {
          if (warehouseId == null) continue;
          SbUserPermissionWarehouse pw = new SbUserPermissionWarehouse();
          pw.setId(UUID7.generateUUID7());
          pw.setUserId(userId);
          pw.setCustomerId(customerId);
          pw.setWarehouseId(warehouseId);
          pw.setCreateBy(createBy);
          try {
            sbUserPermissionWarehouseMapper.insert(pw);
            count++;
          } catch (Exception e) {
            // uk 冲突则忽略
          }
        }
      }
      if (deptIds != null && !deptIds.isEmpty()) {
        for (Long deptId : deptIds) {
          if (deptId == null) continue;
          SbUserPermissionDept pd = new SbUserPermissionDept();
          pd.setId(UUID7.generateUUID7());
          pd.setUserId(userId);
          pd.setCustomerId(customerId);
          pd.setDeptId(deptId);
          pd.setCreateBy(createBy);
          try {
            sbUserPermissionDeptMapper.insert(pd);
            count++;
          } catch (Exception e) {
            // uk 冲突则忽略
          }
        }
      }
    }
    return count;
  }

  private static final String GROUP_KEY_SUPER = "super";

  @Override
  public boolean isUserInSuperGroup(Long userId, String customerId) {
    if (userId == null || StringUtils.isEmpty(customerId)) {
      return false;
    }
    List<SbWorkGroup> groups = sbWorkGroupMapper.selectListByCustomerId(customerId);
    if (groups == null) return false;
    SbWorkGroup superGroup = groups.stream()
        .filter(g -> GROUP_KEY_SUPER.equals(g.getGroupKey()))
        .findFirst()
        .orElse(null);
    if (superGroup == null) return false;
    return sbWorkGroupUserMapper.countByGroupIdAndUserId(superGroup.getGroupId(), userId) > 0;
  }
}

package com.spd.system.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.spd.common.constant.UserConstants;
import com.spd.common.exception.ServiceException;
import com.spd.common.utils.SecurityUtils;
import com.spd.common.utils.StringUtils;
import com.spd.common.utils.uuid.UUID7;
import com.spd.system.domain.SbRole;
import com.spd.system.domain.SbRoleMenu;
import com.spd.system.domain.SbUserRole;
import com.spd.system.mapper.SbRoleMapper;
import com.spd.system.mapper.SbRoleMenuMapper;
import com.spd.system.mapper.SbUserRoleMapper;
import com.spd.system.service.ISbRoleService;

/**
 * 设备角色业务实现（基于 sb_role）
 */
@Service
public class SbRoleServiceImpl implements ISbRoleService {

  @Autowired
  private SbRoleMapper sbRoleMapper;

  @Autowired
  private SbRoleMenuMapper sbRoleMenuMapper;

  @Autowired
  private SbUserRoleMapper sbUserRoleMapper;

  @Override
  public List<SbRole> selectSbRoleList(SbRole role) {
    return sbRoleMapper.selectSbRoleList(role);
  }

  @Override
  public List<SbRole> selectSbRolesByUserId(Long userId) {
    List<SbRole> userRoles = sbRoleMapper.selectSbRolePermissionByUserId(userId);
    List<SbRole> roles = selectSbRoleAll();
    for (SbRole role : roles) {
      for (SbRole userRole : userRoles) {
        if (role.getRoleId() != null && role.getRoleId().equals(userRole.getRoleId())) {
          role.setFlag(true);
          break;
        }
      }
    }
    return roles;
  }

  @Override
  public Set<String> selectSbRolePermissionByUserId(Long userId) {
    List<SbRole> perms = sbRoleMapper.selectSbRolePermissionByUserId(userId);
    Set<String> permsSet = new HashSet<>();
    for (SbRole perm : perms) {
      if (StringUtils.isNotNull(perm)) {
        permsSet.addAll(Arrays.asList(perm.getRoleKey().trim().split(",")));
      }
    }
    return permsSet;
  }

  @Override
  public List<SbRole> selectSbRoleAll() {
    return sbRoleMapper.selectSbRoleAll();
  }

  @Override
  public List<String> selectSbRoleListByUserId(Long userId) {
    return sbRoleMapper.selectSbRoleListByUserId(userId);
  }

  @Override
  public SbRole selectSbRoleById(String roleId) {
    return sbRoleMapper.selectSbRoleById(roleId);
  }

  @Override
  public boolean checkSbRoleNameUnique(SbRole role) {
    String roleId = StringUtils.isNull(role.getRoleId()) ? "" : role.getRoleId();
    SbRole info = sbRoleMapper.checkSbRoleNameUnique(role.getRoleName());
    if (StringUtils.isNotNull(info) && !roleId.equals(info.getRoleId())) {
      return UserConstants.NOT_UNIQUE;
    }
    return UserConstants.UNIQUE;
  }

  @Override
  public boolean checkSbRoleKeyUnique(SbRole role) {
    String roleId = StringUtils.isNull(role.getRoleId()) ? "" : role.getRoleId();
    SbRole info = sbRoleMapper.checkSbRoleKeyUnique(role.getRoleKey());
    if (StringUtils.isNotNull(info) && !roleId.equals(info.getRoleId())) {
      return UserConstants.NOT_UNIQUE;
    }
    return UserConstants.UNIQUE;
  }

  @Override
  @Transactional
  public int insertSbRole(SbRole role) {
    if (StringUtils.isEmpty(role.getRoleId())) {
      role.setRoleId(UUID7.generateUUID7());
    }
    sbRoleMapper.insertSbRole(role);
    return insertSbRoleMenu(role);
  }

  @Override
  @Transactional
  public int updateSbRole(SbRole role) {
    sbRoleMapper.updateSbRole(role);
    sbRoleMenuMapper.deleteSbRoleMenuByRoleId(role.getRoleId());
    return insertSbRoleMenu(role);
  }

  @Override
  public int updateSbRoleStatus(SbRole role) {
    return sbRoleMapper.updateSbRole(role);
  }

  @Override
  public int deleteSbRoleById(String roleId) {
    if (countSbUserRoleByRoleId(roleId) > 0) {
      throw new ServiceException("该角色已分配用户，不能删除");
    }
    sbRoleMenuMapper.deleteSbRoleMenuByRoleId(roleId);
    return sbRoleMapper.deleteSbRoleById(roleId, SecurityUtils.getUsername());
  }

  @Override
  public int deleteSbRoleByIds(String[] roleIds) {
    for (String roleId : roleIds) {
      if (countSbUserRoleByRoleId(roleId) > 0) {
        throw new ServiceException("角色已分配用户，不能删除");
      }
    }
    sbRoleMenuMapper.deleteSbRoleMenu(roleIds);
    return sbRoleMapper.deleteSbRoleByIds(roleIds, SecurityUtils.getUsername());
  }

  @Override
  public int countSbUserRoleByRoleId(String roleId) {
    return sbUserRoleMapper.countSbUserRoleByRoleId(roleId);
  }

  @Override
  public int deleteSbAuthUser(SbUserRole userRole) {
    return sbUserRoleMapper.deleteSbUserRoleInfo(userRole);
  }

  @Override
  public int deleteSbAuthUsers(String roleId, Long[] userIds) {
    SbUserRole ur = new SbUserRole();
    ur.setRoleId(roleId);
    for (Long userId : userIds) {
      ur.setUserId(userId);
      sbUserRoleMapper.deleteSbUserRoleInfo(ur);
    }
    return userIds.length;
  }

  @Override
  public int insertSbAuthUsers(String roleId, Long[] userIds) {
    SbRole role = sbRoleMapper.selectSbRoleById(roleId);
    String customerId = role != null ? role.getCustomerId() : null;
    List<SbUserRole> list = new ArrayList<>();
    for (Long userId : userIds) {
      SbUserRole ur = new SbUserRole();
      ur.setUserId(userId);
      ur.setRoleId(roleId);
      ur.setCustomerId(customerId);
      list.add(ur);
    }
    if (!list.isEmpty()) {
      return sbUserRoleMapper.batchSbUserRole(list);
    }
    return 0;
  }

  private int insertSbRoleMenu(SbRole role) {
    int rows = 1;
    List<SbRoleMenu> list = new ArrayList<>();
    String[] menuIds = role.getMenuIds();
    if (menuIds != null) {
      for (String menuId : menuIds) {
        SbRoleMenu rm = new SbRoleMenu();
        rm.setRoleId(role.getRoleId());
        rm.setMenuId(menuId);
        rm.setCustomerId(role.getCustomerId());
        list.add(rm);
      }
    }
    if (!list.isEmpty()) {
      rows = sbRoleMenuMapper.batchSbRoleMenu(list);
    }
    return rows;
  }
}


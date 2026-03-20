package com.spd.system.service.impl;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.spd.common.utils.SecurityUtils;
import com.spd.common.utils.ServletUtils;
import com.spd.common.utils.StringUtils;
import com.spd.system.mapper.SysPostMapper;
import com.spd.system.mapper.SysUserDepartmentMapper;
import com.spd.system.mapper.SysUserWarehouseMapper;
import com.spd.system.service.ISbUserPermissionService;
import com.spd.system.service.ISbWorkGroupService;
import com.spd.system.service.ITenantScopeService;

@Service
public class TenantScopeServiceImpl implements ITenantScopeService {

  private static final String HEADER_LOGIN_CHANNEL = "X-Login-Channel";

  @Autowired
  private ISbWorkGroupService sbWorkGroupService;
  @Autowired
  private ISbUserPermissionService sbUserPermissionService;
  @Autowired
  private SysPostMapper sysPostMapper;
  @Autowired
  private SysUserWarehouseMapper sysUserWarehouseMapper;
  @Autowired
  private SysUserDepartmentMapper sysUserDepartmentMapper;

  /**
   * 解析当前请求所属系统：优先 Token 内 loginChannel，其次请求头 X-Login-Channel（hc / equipment）。
   * 均为空视为「未识别」，走兼容合并逻辑。
   */
  private String effectiveChannel() {
    String c = StringUtils.trimToEmpty(SecurityUtils.getLoginChannel());
    if (StringUtils.isNotEmpty(c)) {
      return c;
    }
    try {
      HttpServletRequest req = ServletUtils.getRequest();
      if (req != null) {
        String h = StringUtils.trimToEmpty(req.getHeader(HEADER_LOGIN_CHANNEL));
        if (StringUtils.isNotEmpty(h)) {
          return h;
        }
      }
    } catch (Exception ignored) {
    }
    return null;
  }

  private boolean isHc(String ch) {
    return ch != null && "hc".equalsIgnoreCase(ch.trim());
  }

  private boolean isEquipment(String ch) {
    if (ch == null) {
      return false;
    }
    String t = ch.trim();
    return "equipment".equalsIgnoreCase(t) || "sb".equalsIgnoreCase(t) || "eq".equalsIgnoreCase(t);
  }

  @Override
  public boolean isTenantSuper(Long userId, String customerId) {
    if (userId == null || customerId == null) {
      return false;
    }
    String ch = effectiveChannel();
    if (isHc(ch)) {
      return sysPostMapper.countUserSuperPostInTenant(userId, customerId) > 0;
    }
    if (isEquipment(ch)) {
      return sbWorkGroupService.isUserInSuperGroup(userId, customerId);
    }
    return sbWorkGroupService.isUserInSuperGroup(userId, customerId)
        || sysPostMapper.countUserSuperPostInTenant(userId, customerId) > 0;
  }

  @Override
  public List<Long> resolveWarehouseScope(Long userId, String customerId) {
    if (isTenantSuper(userId, customerId)) {
      return null;
    }
    String ch = effectiveChannel();
    if (isHc(ch)) {
      return copyList(sysUserWarehouseMapper.selectWarehouseIdsByUserId(userId));
    }
    if (isEquipment(ch)) {
      return copyList(sbUserPermissionService.selectWarehouseIdsByUserId(userId, customerId));
    }
    return mergeWarehouse(userId, customerId);
  }

  @Override
  public List<Long> resolveDepartmentScope(Long userId, String customerId) {
    if (isTenantSuper(userId, customerId)) {
      return null;
    }
    String ch = effectiveChannel();
    if (isHc(ch)) {
      return copyList(sysUserDepartmentMapper.selectDepartmentIdsByUserId(userId));
    }
    if (isEquipment(ch)) {
      return copyList(sbUserPermissionService.selectDeptIdsByUserId(userId, customerId));
    }
    return mergeDept(userId, customerId);
  }

  private List<Long> mergeWarehouse(Long userId, String customerId) {
    Set<Long> set = new LinkedHashSet<>();
    List<Long> sbIds = sbUserPermissionService.selectWarehouseIdsByUserId(userId, customerId);
    if (sbIds != null) {
      set.addAll(sbIds);
    }
    List<Long> sysIds = sysUserWarehouseMapper.selectWarehouseIdsByUserId(userId);
    if (sysIds != null) {
      set.addAll(sysIds);
    }
    return new ArrayList<>(set);
  }

  private List<Long> mergeDept(Long userId, String customerId) {
    Set<Long> set = new LinkedHashSet<>();
    List<Long> sbIds = sbUserPermissionService.selectDeptIdsByUserId(userId, customerId);
    if (sbIds != null) {
      set.addAll(sbIds);
    }
    List<Long> sysIds = sysUserDepartmentMapper.selectDepartmentIdsByUserId(userId);
    if (sysIds != null) {
      set.addAll(sysIds);
    }
    return new ArrayList<>(set);
  }

  private static List<Long> copyList(List<Long> src) {
    if (src == null || src.isEmpty()) {
      return new ArrayList<>();
    }
    return new ArrayList<>(src);
  }
}

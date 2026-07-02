package com.spd.system.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.spd.common.core.domain.entity.SysUser;
import com.spd.common.utils.SecurityUtils;
import com.spd.common.utils.StringUtils;
import com.spd.system.mapper.SysPostMapper;
import com.spd.system.mapper.SysUserDepartmentMapper;
import com.spd.system.mapper.SysUserMapper;
import com.spd.system.mapper.SysUserWarehouseMapper;
import com.spd.system.service.ITenantScopeService;

@Service
public class TenantScopeServiceImpl implements ITenantScopeService {

  private static final String DEFAULT_TENANT_ADMIN_USERNAME = "super_01";

  @Autowired
  private SysPostMapper sysPostMapper;
  @Autowired
  private SysUserMapper sysUserMapper;
  @Autowired
  private SysUserWarehouseMapper sysUserWarehouseMapper;
  @Autowired
  private SysUserDepartmentMapper sysUserDepartmentMapper;

  private boolean isDefaultTenantAdmin(Long userId, String customerId) {
    if (userId == null) {
      return false;
    }
    String cidParam = StringUtils.trimToEmpty(customerId);
    if (StringUtils.isEmpty(cidParam)) {
      return false;
    }
    try {
      if (userId.equals(SecurityUtils.getUserId())) {
        if (DEFAULT_TENANT_ADMIN_USERNAME.equalsIgnoreCase(StringUtils.trimToEmpty(SecurityUtils.getUsername()))) {
          String effective = StringUtils.trimToEmpty(SecurityUtils.resolveEffectiveTenantId(null));
          if (StringUtils.isNotEmpty(effective) && cidParam.equalsIgnoreCase(effective)) {
            return true;
          }
          String loginCid = StringUtils.trimToEmpty(SecurityUtils.getCustomerId());
          return StringUtils.isNotEmpty(loginCid) && cidParam.equalsIgnoreCase(loginCid);
        }
      }
    } catch (Exception ignored) {
    }
    SysUser u = sysUserMapper.selectUserById(userId);
    if (u == null || !"0".equals(StringUtils.trimToEmpty(u.getDelFlag()))) {
      return false;
    }
    if (!DEFAULT_TENANT_ADMIN_USERNAME.equalsIgnoreCase(StringUtils.trimToEmpty(u.getUserName()))) {
      return false;
    }
    return cidParam.equalsIgnoreCase(StringUtils.trimToEmpty(u.getCustomerId()));
  }

  @Override
  public boolean isTenantSuper(Long userId, String customerId) {
    if (userId == null || StringUtils.isEmpty(StringUtils.trimToEmpty(customerId))) {
      return false;
    }
    String cid = StringUtils.trimToEmpty(customerId);
    if (isDefaultTenantAdmin(userId, cid)) {
      return true;
    }
    return sysPostMapper.countUserSuperPostInTenant(userId, cid) > 0;
  }

  @Override
  public List<Long> resolveWarehouseScope(Long userId, String customerId) {
    if (isTenantSuper(userId, customerId)) {
      return null;
    }
    return copyList(sysUserWarehouseMapper.selectWarehouseIdsByUserId(userId));
  }

  @Override
  public void applyDepartmentScopeQueryParams(Map<String, Object> params, Long userId, String customerId) {
    if (params == null || userId == null) {
      return;
    }
    if (isTenantSuper(userId, StringUtils.trimToEmpty(customerId))) {
      return;
    }
    params.put("scopeDeptUserId", userId);
  }

  @Override
  public void applyWarehouseScopeQueryParams(Map<String, Object> params, Long userId, String customerId) {
    if (params == null || userId == null) {
      return;
    }
    if (isTenantSuper(userId, StringUtils.trimToEmpty(customerId))) {
      return;
    }
    params.put("scopeWarehouseUserId", userId);
  }

  @Override
  public List<Long> resolveDepartmentScope(Long userId, String customerId) {
    if (isTenantSuper(userId, customerId)) {
      return null;
    }
    return copyList(sysUserDepartmentMapper.selectDepartmentIdsByUserId(userId));
  }

  private static List<Long> copyList(List<Long> src) {
    if (src == null || src.isEmpty()) {
      return new ArrayList<>();
    }
    return new ArrayList<>(src);
  }
}

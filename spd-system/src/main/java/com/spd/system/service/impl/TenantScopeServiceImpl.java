package com.spd.system.service.impl;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.spd.common.core.domain.entity.SysUser;
import com.spd.common.utils.SecurityUtils;
import com.spd.common.utils.ServletUtils;
import com.spd.common.utils.StringUtils;
import com.spd.system.mapper.SysPostMapper;
import com.spd.system.mapper.SysUserDepartmentMapper;
import com.spd.system.mapper.SysUserMapper;
import com.spd.system.mapper.SysUserWarehouseMapper;
import com.spd.system.service.ISbUserPermissionService;
import com.spd.system.service.ISbWorkGroupService;
import com.spd.system.service.ITenantScopeService;

@Service
public class TenantScopeServiceImpl implements ITenantScopeService {

  private static final String HEADER_LOGIN_CHANNEL = "X-Login-Channel";

  /**
   * 与 {@link com.spd.system.service.impl.SbCustomerServiceImpl} 新租户默认管理员账号一致。
   * 该账号需在租户内可维护全部仓库/科室以便给其他用户授权，不依赖岗位/工作组绑定是否完整。
   */
  private static final String DEFAULT_TENANT_ADMIN_USERNAME = "super_01";

  @Autowired
  private ISbWorkGroupService sbWorkGroupService;
  @Autowired
  private ISbUserPermissionService sbUserPermissionService;
  @Autowired
  private SysPostMapper sysPostMapper;
  @Autowired
  private SysUserMapper sysUserMapper;
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

  /**
   * 租户默认管理员 super_01：只要属于当前租户，即视为租户管理员（全量仓库/科室数据权限），
   * 避免因 sys_user_post 与 super 岗位脱节、或耗材/设备登录渠道差异导致 isTenantSuper 误判。
   */
  private boolean isDefaultTenantAdmin(Long userId, String customerId) {
    if (userId == null) {
      return false;
    }
    String cidParam = StringUtils.trimToEmpty(customerId);
    if (StringUtils.isEmpty(cidParam)) {
      return false;
    }
    if (userId.equals(SecurityUtils.getUserId())) {
      if (DEFAULT_TENANT_ADMIN_USERNAME.equalsIgnoreCase(StringUtils.trimToEmpty(SecurityUtils.getUsername()))) {
        // 与 Controller 一致：优先 resolveEffectiveTenantId（含 TenantContext），避免仅 getCustomerId() 与入参不一致
        String effective = StringUtils.trimToEmpty(SecurityUtils.resolveEffectiveTenantId(null));
        if (StringUtils.isNotEmpty(effective) && cidParam.equalsIgnoreCase(effective)) {
          return true;
        }
        String loginCid = StringUtils.trimToEmpty(SecurityUtils.getCustomerId());
        return StringUtils.isNotEmpty(loginCid) && cidParam.equalsIgnoreCase(loginCid);
      }
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
    String ch = effectiveChannel();
    boolean hcSuper = sysPostMapper.countUserSuperPostInTenant(userId, cid) > 0;
    boolean eqSuper = sbWorkGroupService.isUserInSuperGroup(userId, cid);
    // 耗材端若仅认 sys_post，易与登录渠道/历史数据不一致；与「未识别渠道」一致：岗位 super 与设备管理员组 super 任一命中即可
    if (isHc(ch)) {
      return hcSuper || eqSuper;
    }
    if (isEquipment(ch)) {
      return eqSuper || hcSuper;
    }
    return eqSuper || hcSuper;
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
  public void applyDepartmentScopeQueryParams(Map<String, Object> params, Long userId, String customerId) {
    if (params == null || userId == null) {
      return;
    }
    String cid = StringUtils.trimToEmpty(customerId);
    if (isTenantSuper(userId, cid)) {
      return;
    }
    params.put("scopeDeptUserId", userId);
    if (StringUtils.isNotEmpty(cid)) {
      params.put("scopeDeptCustomerId", cid);
    }
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

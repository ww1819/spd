package com.spd.system.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.spd.common.core.domain.entity.SysUser;
import com.spd.common.utils.StringUtils;
import com.spd.system.domain.SysPost;
import com.spd.system.domain.SysPostMenu;
import com.spd.system.domain.SysUserMenu;
import com.spd.system.mapper.HcCustomerMenuMapper;
import com.spd.system.mapper.SysPostMapper;
import com.spd.system.mapper.SysPostMenuMapper;
import com.spd.system.mapper.SysUserMapper;
import com.spd.system.mapper.SysUserMenuMapper;
import com.spd.system.service.ITenantAdminMenuSyncService;

/**
 * 登录时为客户 super 工作组与 super_01 补齐 hc_customer_menu 菜单，与 {@link TenantFoundationAutoGrantServiceImpl} 科室/仓库自动授权一致。
 */
@Service
public class TenantAdminMenuSyncServiceImpl implements ITenantAdminMenuSyncService {

  private static final Logger log = LoggerFactory.getLogger(TenantAdminMenuSyncServiceImpl.class);

  private static final String DEFAULT_TENANT_ADMIN = "super_01";
  private static final String POST_CODE_SUPER = "super";

  @Autowired
  private HcCustomerMenuMapper hcCustomerMenuMapper;
  @Autowired
  private SysPostMapper sysPostMapper;
  @Autowired
  private SysPostMenuMapper sysPostMenuMapper;
  @Autowired
  private SysUserMapper sysUserMapper;
  @Autowired
  private SysUserMenuMapper sysUserMenuMapper;

  @Override
  @Transactional(rollbackFor = Exception.class)
  public int syncHcCustomerMenusToTenantAdmins(String tenantId) {
    String tid = StringUtils.trimToEmpty(tenantId);
    if (StringUtils.isEmpty(tid)) {
      return 0;
    }
    List<Long> customerMenuIds = hcCustomerMenuMapper.selectEnabledMenuIdsByTenantId(tid);
    if (customerMenuIds == null || customerMenuIds.isEmpty()) {
      return 0;
    }

    int affected = 0;
    Long superPostId = resolveSuperPostId(tid);
    if (superPostId != null) {
      affected += supplementPostMenus(superPostId, tid, customerMenuIds);
    }
    SysUser super01 = sysUserMapper.selectUserByUserNameAndCustomerId(DEFAULT_TENANT_ADMIN, tid);
    if (super01 != null && super01.getUserId() != null) {
      affected += supplementUserMenus(super01.getUserId(), tid, customerMenuIds);
    }
    if (affected > 0) {
      log.info("租户管理员菜单同步: tenantId={}, 新增关联 {} 条", tid, affected);
    }
    return affected;
  }

  private int supplementPostMenus(Long postId, String tenantId, List<Long> menuIds) {
    List<Long> existing = sysPostMenuMapper.selectMenuListByPostId(postId);
    Set<Long> exists = new HashSet<>(existing != null ? existing : new ArrayList<>());
    List<SysPostMenu> toInsert = new ArrayList<>();
    for (Long menuId : menuIds) {
      if (menuId != null && menuId > 0 && !exists.contains(menuId)) {
        SysPostMenu row = new SysPostMenu();
        row.setPostId(postId);
        row.setMenuId(menuId);
        row.setTenantId(tenantId);
        toInsert.add(row);
        exists.add(menuId);
      }
    }
    if (toInsert.isEmpty()) {
      return 0;
    }
    return sysPostMenuMapper.batchPostMenu(toInsert);
  }

  private int supplementUserMenus(Long userId, String tenantId, List<Long> menuIds) {
    List<Long> existing = sysUserMenuMapper.selectMenuListByUserId(userId);
    Set<Long> exists = new HashSet<>(existing != null ? existing : new ArrayList<>());
    List<SysUserMenu> toInsert = new ArrayList<>();
    for (Long menuId : menuIds) {
      if (menuId != null && menuId > 0 && !exists.contains(menuId)) {
        SysUserMenu row = new SysUserMenu();
        row.setUserId(userId);
        row.setMenuId(menuId);
        row.setTenantId(tenantId);
        toInsert.add(row);
        exists.add(menuId);
      }
    }
    if (toInsert.isEmpty()) {
      return 0;
    }
    return sysUserMenuMapper.batchUserMenu(toInsert);
  }

  private Long resolveSuperPostId(String tenantId) {
    SysPost q = new SysPost();
    q.setTenantId(tenantId);
    q.setPostCode(POST_CODE_SUPER);
    List<SysPost> posts = sysPostMapper.selectPostList(q);
    if (posts != null && !posts.isEmpty()) {
      return posts.get(0).getPostId();
    }
    SysPost linked = sysPostMapper.selectHcSuperPostLinkedToCustomer(tenantId);
    return linked != null ? linked.getPostId() : null;
  }
}

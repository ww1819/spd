package com.spd.system.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.spd.common.constant.UserConstants;
import com.spd.common.core.domain.entity.SysUser;
import com.spd.common.exception.ServiceException;
import com.spd.common.enums.TenantEnum;
import com.spd.common.utils.SecurityUtils;
import com.spd.common.utils.StringUtils;
import com.spd.common.utils.uuid.UUID7;
import com.spd.system.domain.SbCustomer;
import com.spd.system.domain.SbCustomerMenu;
import com.spd.system.domain.SbCustomerPeriodLog;
import com.spd.system.domain.SbCustomerStatusLog;
import com.spd.system.domain.SbRole;
import com.spd.system.domain.SbUserRole;
import com.spd.system.domain.SbUserPermissionMenu;
import com.spd.system.domain.SbWorkGroup;
import com.spd.system.domain.SbWorkGroupMenu;
import com.spd.system.domain.SbWorkGroupUser;
import com.spd.system.mapper.SbCustomerMenuMapper;
import com.spd.system.mapper.SbCustomerMapper;
import com.spd.system.mapper.SbCustomerPeriodLogMapper;
import com.spd.system.mapper.SbCustomerStatusLogMapper;
import com.spd.system.mapper.SbMenuMapper;
import com.spd.system.mapper.SbUserRoleMapper;
import com.spd.system.mapper.SbWorkGroupMapper;
import com.spd.system.mapper.SbUserPermissionMenuMapper;
import com.spd.system.mapper.SbWorkGroupMenuMapper;
import com.spd.system.mapper.SbWorkGroupUserMapper;
import com.spd.system.mapper.SysMenuMapper;
import com.spd.system.mapper.SysPostMapper;
import com.spd.system.mapper.SysUserPostMapper;
import com.spd.system.mapper.SysPostMenuMapper;
import com.spd.system.mapper.SysUserMenuMapper;
import com.spd.system.mapper.HcUserPermissionMenuMapper;
import com.spd.system.mapper.HcCustomerMenuMapper;
import com.spd.system.mapper.HcCustomerStatusLogMapper;
import com.spd.system.mapper.HcCustomerPeriodLogMapper;
import com.spd.system.domain.hc.HcCustomerMenu;
import com.spd.system.domain.hc.HcCustomerStatusLog;
import com.spd.system.domain.hc.HcCustomerPeriodLog;
import com.spd.system.domain.SysPost;
import com.spd.system.domain.SysUserPost;
import com.spd.system.domain.SysPostMenu;
import com.spd.system.domain.SysUserMenu;
import com.spd.system.domain.hc.HcUserPermissionMenu;
import com.spd.system.service.ISbCustomerCategory68Service;
import com.spd.system.service.ISbCustomerService;
import com.spd.system.service.ISbRoleService;
import com.spd.system.service.ISysConfigService;
import com.spd.system.service.ISysUserService;

/**
 * 设备系统客户（SaaS租户） 服务实现
 */
@Service
public class SbCustomerServiceImpl implements ISbCustomerService {

  @Autowired
  private SbCustomerMapper sbCustomerMapper;

  @Autowired
  private SbCustomerStatusLogMapper sbCustomerStatusLogMapper;

  @Autowired
  private SbCustomerPeriodLogMapper sbCustomerPeriodLogMapper;

  @Autowired
  private SbWorkGroupMapper sbWorkGroupMapper;
  @Autowired
  private SbWorkGroupUserMapper sbWorkGroupUserMapper;
  @Autowired
  private SbWorkGroupMenuMapper sbWorkGroupMenuMapper;
  @Autowired
  private SbMenuMapper sbMenuMapper;
  @Autowired
  private SbCustomerMenuMapper sbCustomerMenuMapper;
  @Autowired
  private SbUserPermissionMenuMapper sbUserPermissionMenuMapper;
  @Autowired
  private ISbRoleService sbRoleService;
  @Autowired
  private ISysUserService sysUserService;
  @Autowired
  private SbUserRoleMapper sbUserRoleMapper;
  @Autowired
  private ISysConfigService configService;
  @Autowired
  private SysMenuMapper sysMenuMapper;
  @Autowired
  private SysPostMapper sysPostMapper;
  @Autowired
  private SysUserPostMapper sysUserPostMapper;
  @Autowired
  private SysPostMenuMapper sysPostMenuMapper;
  @Autowired
  private HcUserPermissionMenuMapper hcUserPermissionMenuMapper;
  @Autowired
  private HcCustomerStatusLogMapper hcCustomerStatusLogMapper;
  @Autowired
  private HcCustomerPeriodLogMapper hcCustomerPeriodLogMapper;
  @Autowired
  private HcCustomerMenuMapper hcCustomerMenuMapper;
  @Autowired
  private SysUserMenuMapper sysUserMenuMapper;
  @Autowired
  private ISbCustomerCategory68Service sbCustomerCategory68Service;

  /** 新增客户时默认管理员组标识 */
  private static final String DEFAULT_GROUP_KEY = "super";
  /** 新增客户时默认管理员账号 */
  private static final String DEFAULT_ADMIN_USERNAME = "super_01";
  /** 新增客户时默认角色标识（租户管理员） */
  private static final String DEFAULT_ROLE_KEY = "tenant_admin";

  @Override
  public List<SbCustomer> selectSbCustomerList(SbCustomer customer) {
    return sbCustomerMapper.selectSbCustomerList(customer);
  }

  @Override
  public SbCustomer selectSbCustomerById(String customerId) {
    return sbCustomerMapper.selectSbCustomerById(customerId);
  }

  @Override
  public SbCustomer selectSbCustomerByCode(String customerCode) {
    return sbCustomerMapper.selectSbCustomerByCode(customerCode);
  }

  @Override
  public SbCustomer selectSbCustomerByTenantKey(String tenantKey) {
    return sbCustomerMapper.selectSbCustomerByTenantKey(tenantKey);
  }

  @Override
  public boolean checkSbCustomerCodeUnique(SbCustomer customer) {
    String customerId = StringUtils.isNull(customer.getCustomerId()) ? "" : customer.getCustomerId();
    SbCustomer info = sbCustomerMapper.checkSbCustomerCodeUnique(customer.getCustomerCode());
    if (StringUtils.isNotNull(info) && !customerId.equals(info.getCustomerId())) {
      return UserConstants.NOT_UNIQUE;
    }
    return UserConstants.UNIQUE;
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public int insertSbCustomer(SbCustomer customer) {
    String tenantKey = customer.getTenantKey();
    if (StringUtils.isNotEmpty(tenantKey)) {
      TenantEnum tenantEnum = TenantEnum.fromTenantKey(tenantKey);
      if (tenantEnum == null) {
        throw new IllegalArgumentException("租户类型不合法，请从代码内租户列表选择：" + tenantKey);
      }
      if (StringUtils.isNotNull(sbCustomerMapper.selectSbCustomerByTenantKey(tenantKey))) {
        throw new IllegalArgumentException("该租户类型已存在客户记录，每个租户类型只能创建一条客户：" + tenantKey);
      }
      customer.setCustomerId(tenantEnum.getCustomerId());
      if (StringUtils.isEmpty(customer.getCustomerCode())) {
        customer.setCustomerCode(tenantEnum.getCustomerCode());
      }
    } else {
      if (StringUtils.isEmpty(customer.getCustomerId())) {
        customer.setCustomerId(UUID7.generateUUID7());
      }
    }
    customer.setCreateBy(SecurityUtils.getUserIdStr());
    int rows = sbCustomerMapper.insertSbCustomer(customer);
    if (rows > 0) {
      createDefaultTenantAdmin(customer.getCustomerId(),
          customer.getCustomerName() != null ? customer.getCustomerName() : "客户",
          customer.getCreateBy());
      sbCustomerCategory68Service.initForCustomer(customer.getCustomerId());
    }
    return rows;
  }

  /**
   * 新增客户后自动创建：
   * 1）设备侧：管理员组(super)、租户管理员角色、管理员账号(super_01)并加入该组；新租户与 super 组默认包含系统设置下非平台管理功能。
   * 2）耗材侧：耗材系统工作组管理员组(super)（sys_post 岗位）、super_01 加入该岗位，并为管理员组与 super_01 授予系统设置下非平台管理功能的权限（客户管理、客户菜单功能管理已设为平台管理功能，不授予租户）。
   */
  private void createDefaultTenantAdmin(String customerId, String customerName, String createBy) {
    String groupId = UUID7.generateUUID7();
    String roleId = UUID7.generateUUID7();

    SbRole role = new SbRole();
    role.setRoleId(roleId);
    role.setCustomerId(customerId);
    role.setRoleName("租户管理员");
    role.setRoleKey(DEFAULT_ROLE_KEY);
    role.setRoleSort(0);
    role.setStatus("0");
    role.setCreateBy(createBy);
    sbRoleService.insertSbRole(role);

    SbWorkGroup group = new SbWorkGroup();
    group.setGroupId(groupId);
    group.setCustomerId(customerId);
    group.setGroupName("管理员组");
    group.setGroupKey(DEFAULT_GROUP_KEY);
    group.setOrderNum(0);
    group.setCreateBy(createBy);
    sbWorkGroupMapper.insertSbWorkGroup(group);

    String initPassword = configService.selectConfigByKey("sys.user.initPassword");
    if (StringUtils.isEmpty(initPassword)) {
      initPassword = "admin123";
    } else {
      initPassword = initPassword.trim();
    }
    SysUser user = new SysUser();
    user.setCustomerId(customerId);
    user.setUserName(DEFAULT_ADMIN_USERNAME);
    user.setNickName("租户管理员");
    user.setPassword(SecurityUtils.encryptPassword(initPassword));
    user.setStatus("0");
    user.setCreateBy(createBy);
    sysUserService.insertUser(user);

    List<SbUserRole> urList = new ArrayList<>();
    SbUserRole ur = new SbUserRole();
    ur.setUserId(user.getUserId());
    ur.setRoleId(roleId);
    ur.setCustomerId(customerId);
    urList.add(ur);
    sbUserRoleMapper.batchSbUserRole(urList);

    SbWorkGroupUser wgu = new SbWorkGroupUser();
    wgu.setGroupId(groupId);
    wgu.setUserId(user.getUserId());
    wgu.setCustomerId(customerId);
    wgu.setCreateBy(createBy);
    sbWorkGroupUserMapper.insertSbWorkGroupUser(wgu);

    // 新租户与 super 组默认包含「默认对客户开放」的菜单，若无则退化为系统设置下非平台管理功能
    List<String> defaultMenuIds = sbMenuMapper.selectMenuIdsDefaultForCustomer();
    if (defaultMenuIds == null || defaultMenuIds.isEmpty()) {
      defaultMenuIds = sbMenuMapper.selectMenuIdsSystemSettingsNonPlatform();
    }
    if (defaultMenuIds != null && !defaultMenuIds.isEmpty()) {
      List<SbCustomerMenu> customerMenus = new ArrayList<>();
      for (String menuId : defaultMenuIds) {
        SbCustomerMenu cm = new SbCustomerMenu();
        cm.setCustomerId(customerId);
        cm.setMenuId(menuId);
        cm.setStatus("0");
        cm.setIsEnabled("1");
        cm.setCreateBy(createBy);
        customerMenus.add(cm);
      }
      sbCustomerMenuMapper.batchSbCustomerMenu(customerMenus);

      List<SbWorkGroupMenu> groupMenus = new ArrayList<>();
      java.util.Set<String> menuIdSet = new java.util.LinkedHashSet<>(defaultMenuIds);
      for (String menuId : menuIdSet) {
        SbWorkGroupMenu wgm = new SbWorkGroupMenu();
        wgm.setId(UUID7.generateUUID7());
        wgm.setGroupId(groupId);
        wgm.setMenuId(menuId);
        wgm.setCustomerId(customerId);
        wgm.setCreateBy(createBy);
        groupMenus.add(wgm);
      }
      sbWorkGroupMenuMapper.batchInsert(groupMenus);

      // super_01 同步写入用户权限表，登录后以用户权限表作为菜单权限数据
      List<SbUserPermissionMenu> userMenus = new ArrayList<>();
      for (String menuId : defaultMenuIds) {
        SbUserPermissionMenu upm = new SbUserPermissionMenu();
        upm.setId(UUID7.generateUUID7());
        upm.setUserId(user.getUserId());
        upm.setCustomerId(customerId);
        upm.setMenuId(menuId);
        upm.setCreateBy(createBy);
        userMenus.add(upm);
      }
      sbUserPermissionMenuMapper.batchInsert(userMenus);
    }

    // 耗材系统工作组：创建默认岗位「管理员组」(super)，将 super_01 加入该岗位，并授予系统设置下非平台管理功能（is_platform!=1；客户管理、客户菜单功能管理为平台管理不授予）
    SysPost post = new SysPost();
    post.setPostCode(DEFAULT_GROUP_KEY);
    post.setPostName("管理员组");
    post.setPostSort(0);
    post.setStatus("0");
    post.setTenantId(customerId);
    post.setCreateBy(createBy);
    sysPostMapper.insertPost(post);

    SysUserPost userPost = new SysUserPost();
    userPost.setUserId(user.getUserId());
    userPost.setPostId(post.getPostId());
    userPost.setTenantId(customerId);
    List<SysUserPost> userPostList = new ArrayList<>();
    userPostList.add(userPost);
    sysUserPostMapper.batchUserPost(userPostList);

    List<Long> materialMenuIds = resolveDefaultMaterialMenuIds();
    if (materialMenuIds != null && !materialMenuIds.isEmpty()) {
      List<SysPostMenu> postMenus = new ArrayList<>();
      for (Long menuId : materialMenuIds) {
        if (menuId != null && menuId > 0) {
          SysPostMenu pm = new SysPostMenu();
          pm.setPostId(post.getPostId());
          pm.setMenuId(menuId);
          pm.setTenantId(customerId);
          postMenus.add(pm);
        }
      }
      if (!postMenus.isEmpty()) {
        sysPostMenuMapper.batchPostMenu(postMenus);
      }

      List<HcUserPermissionMenu> hcUserMenus = new ArrayList<>();
      for (Long menuId : materialMenuIds) {
        HcUserPermissionMenu upm = new HcUserPermissionMenu();
        upm.setId(UUID7.generateUUID7());
        upm.setUserId(user.getUserId());
        upm.setTenantId(customerId);
        upm.setMenuId(menuId);
        upm.setCreateBy(createBy);
        hcUserMenus.add(upm);
      }
      hcUserPermissionMenuMapper.batchInsert(hcUserMenus);

      // 耗材客户菜单权限：新增客户时增加系统设置下非平台管理功能的权限
      List<HcCustomerMenu> hcCustomerMenus = new ArrayList<>();
      for (Long menuId : materialMenuIds) {
        if (menuId != null && menuId > 0) {
          HcCustomerMenu cm = new HcCustomerMenu();
          cm.setTenantId(customerId);
          cm.setMenuId(menuId);
          cm.setStatus("0");
          cm.setIsEnabled("1");
          cm.setCreateBy(createBy);
          hcCustomerMenus.add(cm);
        }
      }
      if (!hcCustomerMenus.isEmpty()) {
        hcCustomerMenuMapper.batchInsert(hcCustomerMenus);
      }
      syncSysUserMenusForMaterial(user.getUserId(), materialMenuIds, customerId);
    }
  }

  /** 耗材默认菜单：优先 default_open_to_customer，未配置时回退到「系统设置」子树（兼容旧库） */
  private List<Long> resolveDefaultMaterialMenuIds() {
    List<Long> ids = sysMenuMapper.selectMenuIdsDefaultOpenForHcCustomer();
    if (ids != null && !ids.isEmpty()) {
      return ids;
    }
    return sysMenuMapper.selectMaterialSystemSettingMenuIdsExcludeCustomerManage();
  }

  /**
   * 解析耗材管理员组（post_code=super）：优先 tenant_id=客户；历史数据 tenant_id 为空时按客户下用户已关联岗位反查，并回填 sys_post.tenant_id。
   */
  private SysPost resolveHcSuperPostForTenant(String customerId) {
    if (StringUtils.isEmpty(customerId)) {
      return null;
    }
    SysPost q = new SysPost();
    q.setTenantId(customerId);
    q.setPostCode(DEFAULT_GROUP_KEY);
    List<SysPost> posts = sysPostMapper.selectPostList(q);
    if (posts != null && !posts.isEmpty()) {
      SysPost p = posts.get(0);
      sysPostMapper.updatePostTenantIdIfBlank(p.getPostId(), customerId);
      return sysPostMapper.selectPostById(p.getPostId());
    }
    SysPost linked = sysPostMapper.selectHcSuperPostLinkedToCustomer(customerId);
    if (linked != null) {
      sysPostMapper.updatePostTenantIdIfBlank(linked.getPostId(), customerId);
      return sysPostMapper.selectPostById(linked.getPostId());
    }
    return null;
  }

  /**
   * 回填 super 岗位下该客户用户的 sys_user_post.tenant_id（关联丢失修复后统一补租户字段）
   */
  private void patchHcSuperUserPostTenantIds(String customerId, Long superPostId) {
    if (StringUtils.isEmpty(customerId) || superPostId == null) {
      return;
    }
    sysUserPostMapper.updateTenantIdIfBlankForCustomerSuperPost(customerId, superPostId);
  }

  private void syncSysUserMenusForMaterial(Long userId, List<Long> menuIds, String tenantId) {
    if (userId == null) {
      return;
    }
    sysUserMenuMapper.deleteUserMenuByUserId(userId, StringUtils.trimToNull(tenantId));
    if (menuIds == null || menuIds.isEmpty()) {
      return;
    }
    String tid = StringUtils.trimToNull(tenantId);
    List<SysUserMenu> rows = new ArrayList<>();
    for (Long menuId : menuIds) {
      if (menuId == null || menuId <= 0) {
        continue;
      }
      SysUserMenu um = new SysUserMenu();
      um.setUserId(userId);
      um.setMenuId(menuId);
      um.setTenantId(tid);
      rows.add(um);
    }
    if (!rows.isEmpty()) {
      sysUserMenuMapper.batchUserMenu(rows);
    }
  }

  /** 启用被拒绝时的启停用记录原因（当前客户已到计划停用时间） */
  private static final String ENABLE_REJECT_REASON = "尝试启用被拒绝：已到达计划停用时间，请延长计划停用时间后再启用";

  @Override
  @Transactional
  public int updateSbCustomer(SbCustomer customer) {
    SbCustomer old = sbCustomerMapper.selectSbCustomerById(customer.getCustomerId());
    if (old == null) {
      return 0;
    }
    String operateBy = SecurityUtils.getUserIdStr();
    Date now = new Date();

    if (StringUtils.isNotEmpty(customer.getStatus()) && !customer.getStatus().equals(old.getStatus())) {
      if (StringUtils.isEmpty(customer.getStatusChangeReason())) {
        throw new IllegalArgumentException("启停用原因不能为空，请通过启停用操作并填写原因");
      }
      // 再次启用（1→0）时：若当前时间已超过计划停用时间，拒绝启用并写一条启停用记录后抛异常
      if ("0".equals(customer.getStatus()) && "1".equals(old.getStatus())) {
        if (old.getPlannedDisableTime() != null && now.getTime() >= old.getPlannedDisableTime().getTime()) {
          SbCustomerStatusLog rejectLog = new SbCustomerStatusLog();
          rejectLog.setLogId(UUID7.generateUUID7());
          rejectLog.setCustomerId(customer.getCustomerId());
          rejectLog.setStatus("1");
          rejectLog.setOperateTime(now);
          rejectLog.setOperateBy(operateBy);
          rejectLog.setReason(ENABLE_REJECT_REASON);
          sbCustomerStatusLogMapper.insert(rejectLog);
          throw new ServiceException("当前客户已到达计划停用时间，请延长计划停用时间后再启用");
        }
      }
      SbCustomerStatusLog statusLog = new SbCustomerStatusLog();
      statusLog.setLogId(UUID7.generateUUID7());
      statusLog.setCustomerId(customer.getCustomerId());
      statusLog.setStatus(customer.getStatus());
      statusLog.setOperateTime(now);
      statusLog.setOperateBy(operateBy);
      statusLog.setReason(customer.getStatusChangeReason());
      sbCustomerStatusLogMapper.insert(statusLog);

      String cid = customer.getCustomerId();
      if ("0".equals(customer.getStatus())) {
        // 启用：更新上一条停用时间段的结束时间，新增一条使用时段（仅记录开始时间）
        SbCustomerPeriodLog lastSuspend = sbCustomerPeriodLogMapper.selectLastWithNullEnd(cid, SbCustomerPeriodLog.PERIOD_TYPE_SUSPEND);
        if (lastSuspend != null) {
          sbCustomerPeriodLogMapper.updateEndTime(lastSuspend.getPeriodId(), now);
        }
        SbCustomerPeriodLog usagePeriod = new SbCustomerPeriodLog();
        usagePeriod.setPeriodId(UUID7.generateUUID7());
        usagePeriod.setCustomerId(cid);
        usagePeriod.setPeriodType(SbCustomerPeriodLog.PERIOD_TYPE_USAGE);
        usagePeriod.setStartTime(now);
        usagePeriod.setEndTime(null);
        usagePeriod.setCreateBy(operateBy);
        usagePeriod.setCreateTime(now);
        sbCustomerPeriodLogMapper.insert(usagePeriod);
      } else {
        // 停用：更新上一条使用时间段的结束时间，新增一条停用时段（仅记录开始时间）
        SbCustomerPeriodLog lastUsage = sbCustomerPeriodLogMapper.selectLastWithNullEnd(cid, SbCustomerPeriodLog.PERIOD_TYPE_USAGE);
        if (lastUsage != null) {
          sbCustomerPeriodLogMapper.updateEndTime(lastUsage.getPeriodId(), now);
        }
        SbCustomerPeriodLog suspendPeriod = new SbCustomerPeriodLog();
        suspendPeriod.setPeriodId(UUID7.generateUUID7());
        suspendPeriod.setCustomerId(cid);
        suspendPeriod.setPeriodType(SbCustomerPeriodLog.PERIOD_TYPE_SUSPEND);
        suspendPeriod.setStartTime(now);
        suspendPeriod.setEndTime(null);
        suspendPeriod.setCreateBy(operateBy);
        suspendPeriod.setCreateTime(now);
        sbCustomerPeriodLogMapper.insert(suspendPeriod);
      }
    }

    customer.setUpdateBy(operateBy);
    return sbCustomerMapper.updateSbCustomer(customer);
  }

  @Override
  public int deleteSbCustomerById(String customerId) {
    return sbCustomerMapper.deleteSbCustomerById(customerId, SecurityUtils.getUserIdStr());
  }

  @Override
  public int changeStatus(String customerId, String status, String statusChangeReason) {
    if (StringUtils.isEmpty(customerId) || StringUtils.isEmpty(status)) {
      return 0;
    }
    SbCustomer customer = sbCustomerMapper.selectSbCustomerById(customerId);
    if (customer == null) {
      return 0;
    }
    customer.setStatus(status);
    customer.setStatusChangeReason(statusChangeReason);
    return updateSbCustomer(customer);
  }

  @Override
  public List<SbCustomerStatusLog> selectStatusLogList(String customerId) {
    return sbCustomerStatusLogMapper.selectByCustomerId(customerId);
  }

  @Override
  public List<SbCustomerPeriodLog> selectPeriodLogList(String customerId) {
    return sbCustomerPeriodLogMapper.selectByCustomerId(customerId);
  }

  private static final String AUTO_DISABLE_OPERATE_BY = "system";
  private static final String AUTO_DISABLE_REASON = "已到达计划停用时间";

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void autoDisableByPlannedTime(String customerId) {
    if (StringUtils.isEmpty(customerId)) {
      return;
    }
    SbCustomer customer = sbCustomerMapper.selectSbCustomerById(customerId);
    if (customer == null) {
      return;
    }
    if ("1".equals(customer.getStatus())) {
      return;
    }
    Date planned = customer.getPlannedDisableTime();
    if (planned == null) {
      return;
    }
    Date now = new Date();
    if (now.getTime() < planned.getTime()) {
      return;
    }
    String cid = customer.getCustomerId();
    SbCustomerStatusLog statusLog = new SbCustomerStatusLog();
    statusLog.setLogId(UUID7.generateUUID7());
    statusLog.setCustomerId(cid);
    statusLog.setStatus("1");
    statusLog.setOperateTime(now);
    statusLog.setOperateBy(AUTO_DISABLE_OPERATE_BY);
    statusLog.setReason(AUTO_DISABLE_REASON);
    sbCustomerStatusLogMapper.insert(statusLog);

    SbCustomerPeriodLog lastUsage = sbCustomerPeriodLogMapper.selectLastWithNullEnd(cid, SbCustomerPeriodLog.PERIOD_TYPE_USAGE);
    if (lastUsage != null) {
      sbCustomerPeriodLogMapper.updateEndTime(lastUsage.getPeriodId(), now);
    }
    SbCustomerPeriodLog suspendPeriod = new SbCustomerPeriodLog();
    suspendPeriod.setPeriodId(UUID7.generateUUID7());
    suspendPeriod.setCustomerId(cid);
    suspendPeriod.setPeriodType(SbCustomerPeriodLog.PERIOD_TYPE_SUSPEND);
    suspendPeriod.setStartTime(now);
    suspendPeriod.setEndTime(null);
    suspendPeriod.setCreateBy(AUTO_DISABLE_OPERATE_BY);
    suspendPeriod.setCreateTime(now);
    sbCustomerPeriodLogMapper.insert(suspendPeriod);

    SbCustomer update = new SbCustomer();
    update.setCustomerId(cid);
    update.setStatus("1");
    update.setUpdateBy(AUTO_DISABLE_OPERATE_BY);
    sbCustomerMapper.updateSbCustomer(update);
  }

  @Override
  @Transactional
  public int changeHcStatus(String customerId, String status, String statusChangeReason) {
    if (StringUtils.isEmpty(customerId) || StringUtils.isEmpty(status)) {
      return 0;
    }
    if (StringUtils.isEmpty(statusChangeReason) || statusChangeReason.trim().isEmpty()) {
      throw new IllegalArgumentException("启停用原因不能为空，请通过启停用操作并填写原因");
    }
    SbCustomer customer = sbCustomerMapper.selectSbCustomerById(customerId);
    if (customer == null) {
      return 0;
    }
    String operateBy = SecurityUtils.getUserIdStr();
    Date now = new Date();

    HcCustomerStatusLog statusLog = new HcCustomerStatusLog();
    statusLog.setLogId(UUID7.generateUUID7());
    statusLog.setTenantId(customerId);
    statusLog.setStatus(status);
    statusLog.setOperateTime(now);
    statusLog.setOperateBy(operateBy);
    statusLog.setReason(statusChangeReason.trim());
    hcCustomerStatusLogMapper.insert(statusLog);

    if ("0".equals(status)) {
      HcCustomerPeriodLog lastSuspend = hcCustomerPeriodLogMapper.selectLastWithNullEnd(customerId, HcCustomerPeriodLog.PERIOD_TYPE_SUSPEND);
      if (lastSuspend != null) {
        hcCustomerPeriodLogMapper.updateEndTime(lastSuspend.getPeriodId(), now);
      }
      HcCustomerPeriodLog usagePeriod = new HcCustomerPeriodLog();
      usagePeriod.setPeriodId(UUID7.generateUUID7());
      usagePeriod.setTenantId(customerId);
      usagePeriod.setPeriodType(HcCustomerPeriodLog.PERIOD_TYPE_USAGE);
      usagePeriod.setStartTime(now);
      usagePeriod.setEndTime(null);
      usagePeriod.setCreateBy(operateBy);
      usagePeriod.setCreateTime(now);
      hcCustomerPeriodLogMapper.insert(usagePeriod);
    } else {
      HcCustomerPeriodLog lastUsage = hcCustomerPeriodLogMapper.selectLastWithNullEnd(customerId, HcCustomerPeriodLog.PERIOD_TYPE_USAGE);
      if (lastUsage != null) {
        hcCustomerPeriodLogMapper.updateEndTime(lastUsage.getPeriodId(), now);
      }
      HcCustomerPeriodLog suspendPeriod = new HcCustomerPeriodLog();
      suspendPeriod.setPeriodId(UUID7.generateUUID7());
      suspendPeriod.setTenantId(customerId);
      suspendPeriod.setPeriodType(HcCustomerPeriodLog.PERIOD_TYPE_SUSPEND);
      suspendPeriod.setStartTime(now);
      suspendPeriod.setEndTime(null);
      suspendPeriod.setCreateBy(operateBy);
      suspendPeriod.setCreateTime(now);
      hcCustomerPeriodLogMapper.insert(suspendPeriod);
    }

    customer.setHcStatus(status);
    customer.setUpdateBy(operateBy);
    return sbCustomerMapper.updateSbCustomer(customer);
  }

  @Override
  public List<HcCustomerStatusLog> selectHcStatusLogList(String tenantId) {
    return hcCustomerStatusLogMapper.selectByTenantId(tenantId);
  }

  @Override
  public List<HcCustomerPeriodLog> selectHcPeriodLogList(String tenantId) {
    return hcCustomerPeriodLogMapper.selectByTenantId(tenantId);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void resetEquipmentFunctions(String customerId) {
    if (StringUtils.isEmpty(customerId)) {
      throw new IllegalArgumentException("客户ID不能为空");
    }
    SbCustomer customer = sbCustomerMapper.selectSbCustomerById(customerId);
    if (customer == null) {
      throw new IllegalArgumentException("客户不存在");
    }
    String createBy = SecurityUtils.getUserIdStr();

    List<SbWorkGroup> groups = sbWorkGroupMapper.selectListByCustomerId(customerId);
    SbWorkGroup superGroup = groups == null ? null : groups.stream().filter(g -> DEFAULT_GROUP_KEY.equals(g.getGroupKey())).findFirst().orElse(null);
    SysUser super01 = sysUserService.selectUserByUserNameAndCustomerId(DEFAULT_ADMIN_USERNAME, customerId);

    if (superGroup == null && super01 == null) {
      createDefaultTenantAdmin(customerId, customer.getCustomerName() != null ? customer.getCustomerName() : "客户", createBy);
      return;
    }
    ensureEquipmentSuperAndUser(customerId, customer.getCustomerName(), createBy, superGroup, super01);
    superGroup = sbWorkGroupMapper.selectListByCustomerId(customerId).stream().filter(g -> DEFAULT_GROUP_KEY.equals(g.getGroupKey())).findFirst().orElse(null);
    super01 = sysUserService.selectUserByUserNameAndCustomerId(DEFAULT_ADMIN_USERNAME, customerId);
    if (superGroup == null || super01 == null) {
      return;
    }

    // 设备功能重置：仅将「默认对客户开放」的权限开放给客户、super 组、super_01 用户（不再回退到系统设置下非平台管理）
    List<String> defaultMenuIds = sbMenuMapper.selectMenuIdsDefaultForCustomer();
    if (defaultMenuIds == null) {
      defaultMenuIds = new ArrayList<>();
    }

    sbCustomerMenuMapper.deleteSbCustomerMenuByCustomerId(customerId);
    List<SbCustomerMenu> customerMenus = new ArrayList<>();
    for (String menuId : defaultMenuIds) {
      SbCustomerMenu cm = new SbCustomerMenu();
      cm.setCustomerId(customerId);
      cm.setMenuId(menuId);
      cm.setStatus("0");
      cm.setIsEnabled("1");
      cm.setCreateBy(createBy);
      customerMenus.add(cm);
    }
    sbCustomerMenuMapper.batchSbCustomerMenu(customerMenus);

    sbWorkGroupMenuMapper.deleteByGroupId(superGroup.getGroupId(), createBy);
    List<SbWorkGroupMenu> groupMenus = new ArrayList<>();
    java.util.Set<String> menuIdSet = new java.util.LinkedHashSet<>(defaultMenuIds);
    for (String menuId : menuIdSet) {
      SbWorkGroupMenu wgm = new SbWorkGroupMenu();
      wgm.setId(UUID7.generateUUID7());
      wgm.setGroupId(superGroup.getGroupId());
      wgm.setMenuId(menuId);
      wgm.setCustomerId(customerId);
      wgm.setCreateBy(createBy);
      groupMenus.add(wgm);
    }
    sbWorkGroupMenuMapper.batchInsert(groupMenus);

    sbUserPermissionMenuMapper.deleteByUserIdAndCustomerId(super01.getUserId(), customerId, createBy);
    List<SbUserPermissionMenu> userMenus = new ArrayList<>();
    for (String menuId : defaultMenuIds) {
      SbUserPermissionMenu upm = new SbUserPermissionMenu();
      upm.setId(UUID7.generateUUID7());
      upm.setUserId(super01.getUserId());
      upm.setCustomerId(customerId);
      upm.setMenuId(menuId);
      upm.setCreateBy(createBy);
      userMenus.add(upm);
    }
    sbUserPermissionMenuMapper.batchInsert(userMenus);

    sbCustomerCategory68Service.syncFromStandard(customerId);
  }

  /**
   * 耗材功能重置：仅下发「客户名下」默认开放的功能菜单（{@link #resolveDefaultMaterialMenuIds}），不含平台独占菜单；
   * 若 super 岗位、super_01 或其关联、tenant_id 缺失，则自动补齐并回填租户字段，保证管理员组用户可见租户内全部仓库与科室（见 {@link com.spd.system.service.ITenantScopeService#isTenantSuper}）。
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public void resetMaterialFunctions(String customerId) {
    if (StringUtils.isEmpty(customerId)) {
      throw new IllegalArgumentException("客户ID不能为空");
    }
    SbCustomer customer = sbCustomerMapper.selectSbCustomerById(customerId);
    if (customer == null) {
      throw new IllegalArgumentException("客户不存在");
    }
    String createBy = SecurityUtils.getUserIdStr();

    SysPost superPost = resolveHcSuperPostForTenant(customerId);
    SysUser super01 = sysUserService.selectUserByUserNameAndCustomerId(DEFAULT_ADMIN_USERNAME, customerId);

    if (superPost == null && super01 == null) {
      createDefaultTenantAdmin(customerId, customer.getCustomerName() != null ? customer.getCustomerName() : "客户", createBy);
      return;
    }
    ensureMaterialSuperAndUser(customerId, customer.getCustomerName(), createBy, superPost, super01);
    superPost = resolveHcSuperPostForTenant(customerId);
    super01 = sysUserService.selectUserByUserNameAndCustomerId(DEFAULT_ADMIN_USERNAME, customerId);
    if (superPost != null) {
      patchHcSuperUserPostTenantIds(customerId, superPost.getPostId());
    }
    if (superPost == null || super01 == null) {
      return;
    }

    List<Long> materialMenuIds = resolveDefaultMaterialMenuIds();
    if (materialMenuIds == null || materialMenuIds.isEmpty()) {
      return;
    }

    hcCustomerMenuMapper.deleteByTenantId(customerId);
    List<HcCustomerMenu> hcCustomerMenus = new ArrayList<>();
    for (Long menuId : materialMenuIds) {
      if (menuId != null && menuId > 0) {
        HcCustomerMenu cm = new HcCustomerMenu();
        cm.setTenantId(customerId);
        cm.setMenuId(menuId);
        cm.setStatus("0");
        cm.setIsEnabled("1");
        cm.setCreateBy(createBy);
        hcCustomerMenus.add(cm);
      }
    }
    if (!hcCustomerMenus.isEmpty()) {
      hcCustomerMenuMapper.batchInsert(hcCustomerMenus);
    }

    sysPostMenuMapper.deletePostMenuByPostId(superPost.getPostId());
    List<SysPostMenu> postMenus = new ArrayList<>();
    for (Long menuId : materialMenuIds) {
      if (menuId != null && menuId > 0) {
        SysPostMenu pm = new SysPostMenu();
        pm.setPostId(superPost.getPostId());
        pm.setMenuId(menuId);
        pm.setTenantId(customerId);
        postMenus.add(pm);
      }
    }
    if (!postMenus.isEmpty()) {
      sysPostMenuMapper.batchPostMenu(postMenus);
    }

    hcUserPermissionMenuMapper.deleteByUserIdAndTenantId(super01.getUserId(), customerId);
    List<HcUserPermissionMenu> hcUserMenus = new ArrayList<>();
    for (Long menuId : materialMenuIds) {
      if (menuId != null && menuId > 0) {
        HcUserPermissionMenu upm = new HcUserPermissionMenu();
        upm.setId(UUID7.generateUUID7());
        upm.setUserId(super01.getUserId());
        upm.setTenantId(customerId);
        upm.setMenuId(menuId);
        upm.setCreateBy(createBy);
        hcUserMenus.add(upm);
      }
    }
    if (!hcUserMenus.isEmpty()) {
      hcUserPermissionMenuMapper.batchInsert(hcUserMenus);
    }

    syncSysUserMenusForMaterial(super01.getUserId(), materialMenuIds, customerId);
  }

  /** 设备侧：若 super 组或 super_01 缺失则补齐（不创建菜单） */
  private void ensureEquipmentSuperAndUser(String customerId, String customerName, String createBy, SbWorkGroup existingGroup, SysUser existingUser) {
    if (existingUser != null && existingGroup != null) {
      // 工作组与用户均存在时仍可能缺少 sb_work_group_user（历史数据或误删），否则 isTenantSuper 设备侧恒为 false
      if (sbWorkGroupUserMapper.countByGroupIdAndUserId(existingGroup.getGroupId(), existingUser.getUserId()) == 0) {
        SbWorkGroupUser wgu = new SbWorkGroupUser();
        wgu.setGroupId(existingGroup.getGroupId());
        wgu.setUserId(existingUser.getUserId());
        wgu.setCustomerId(customerId);
        wgu.setCreateBy(createBy);
        sbWorkGroupUserMapper.insertSbWorkGroupUser(wgu);
      }
      return;
    }
    String initPassword = configService.selectConfigByKey("sys.user.initPassword");
    if (StringUtils.isEmpty(initPassword)) initPassword = "admin123";
    else initPassword = initPassword.trim();

    if (existingUser == null) {
      String roleId = UUID7.generateUUID7();
      String groupId = existingGroup != null ? existingGroup.getGroupId() : UUID7.generateUUID7();

      SbRole role = new SbRole();
      role.setRoleId(roleId);
      role.setCustomerId(customerId);
      role.setRoleName("租户管理员");
      role.setRoleKey(DEFAULT_ROLE_KEY);
      role.setRoleSort(0);
      role.setStatus("0");
      role.setCreateBy(createBy);
      sbRoleService.insertSbRole(role);

      if (existingGroup == null) {
        SbWorkGroup group = new SbWorkGroup();
        group.setGroupId(groupId);
        group.setCustomerId(customerId);
        group.setGroupName("管理员组");
        group.setGroupKey(DEFAULT_GROUP_KEY);
        group.setOrderNum(0);
        group.setCreateBy(createBy);
        sbWorkGroupMapper.insertSbWorkGroup(group);
      }

      SysUser user = new SysUser();
      user.setCustomerId(customerId);
      user.setUserName(DEFAULT_ADMIN_USERNAME);
      user.setNickName("租户管理员");
      user.setPassword(SecurityUtils.encryptPassword(initPassword));
      user.setStatus("0");
      user.setCreateBy(createBy);
      sysUserService.insertUser(user);

      List<SbUserRole> urList = new ArrayList<>();
      SbUserRole ur = new SbUserRole();
      ur.setUserId(user.getUserId());
      ur.setRoleId(roleId);
      ur.setCustomerId(customerId);
      urList.add(ur);
      sbUserRoleMapper.batchSbUserRole(urList);

      SbWorkGroupUser wgu = new SbWorkGroupUser();
      wgu.setGroupId(groupId);
      wgu.setUserId(user.getUserId());
      wgu.setCustomerId(customerId);
      wgu.setCreateBy(createBy);
      sbWorkGroupUserMapper.insertSbWorkGroupUser(wgu);
      return;
    }

    if (existingGroup == null) {
      String groupId = UUID7.generateUUID7();
      SbWorkGroup group = new SbWorkGroup();
      group.setGroupId(groupId);
      group.setCustomerId(customerId);
      group.setGroupName("管理员组");
      group.setGroupKey(DEFAULT_GROUP_KEY);
      group.setOrderNum(0);
      group.setCreateBy(createBy);
      sbWorkGroupMapper.insertSbWorkGroup(group);

      SbWorkGroupUser wgu = new SbWorkGroupUser();
      wgu.setGroupId(groupId);
      wgu.setUserId(existingUser.getUserId());
      wgu.setCustomerId(customerId);
      wgu.setCreateBy(createBy);
      sbWorkGroupUserMapper.insertSbWorkGroupUser(wgu);
    }
  }

  /** 耗材侧：若 super 岗位或 super_01 缺失则补齐（不创建菜单） */
  private void ensureMaterialSuperAndUser(String customerId, String customerName, String createBy, SysPost existingPost, SysUser existingUser) {
    if (existingUser != null && existingPost != null) {
      // 回填岗位租户 ID（历史数据 tenant_id 为空会导致功能重置/管理员判定失败）
      sysPostMapper.updatePostTenantIdIfBlank(existingPost.getPostId(), customerId);
      // 岗位与用户均存在时仍可能缺少 sys_user_post（历史数据或误删），补齐后「用户管理-管理员组」筛选才能命中
      List<Long> assignedPostIds = sysPostMapper.selectPostListByUserId(existingUser.getUserId());
      if (assignedPostIds == null || !assignedPostIds.contains(existingPost.getPostId())) {
        SysUserPost userPost = new SysUserPost();
        userPost.setUserId(existingUser.getUserId());
        userPost.setPostId(existingPost.getPostId());
        userPost.setTenantId(customerId);
        List<SysUserPost> userPostList = new ArrayList<>();
        userPostList.add(userPost);
        sysUserPostMapper.batchUserPost(userPostList);
      }
      sysUserPostMapper.updateUserPostTenantIdIfBlank(existingUser.getUserId(), existingPost.getPostId(), customerId);
      return;
    }
    String initPassword = configService.selectConfigByKey("sys.user.initPassword");
    if (StringUtils.isEmpty(initPassword)) initPassword = "admin123";
    else initPassword = initPassword.trim();

    if (existingUser == null) {
      SysPost post = existingPost;
      if (post == null) {
        post = new SysPost();
        post.setPostCode(DEFAULT_GROUP_KEY);
        post.setPostName("管理员组");
        post.setPostSort(0);
        post.setStatus("0");
        post.setTenantId(customerId);
        post.setCreateBy(createBy);
        sysPostMapper.insertPost(post);
      }

      SysUser user = new SysUser();
      user.setCustomerId(customerId);
      user.setUserName(DEFAULT_ADMIN_USERNAME);
      user.setNickName("租户管理员");
      user.setPassword(SecurityUtils.encryptPassword(initPassword));
      user.setStatus("0");
      user.setCreateBy(createBy);
      sysUserService.insertUser(user);

      SysUserPost userPost = new SysUserPost();
      userPost.setUserId(user.getUserId());
      userPost.setPostId(post.getPostId());
      userPost.setTenantId(customerId);
      List<SysUserPost> userPostList = new ArrayList<>();
      userPostList.add(userPost);
      sysUserPostMapper.batchUserPost(userPostList);
      return;
    }

    if (existingPost == null) {
      SysPost post = new SysPost();
      post.setPostCode(DEFAULT_GROUP_KEY);
      post.setPostName("管理员组");
      post.setPostSort(0);
      post.setStatus("0");
      post.setTenantId(customerId);
      post.setCreateBy(createBy);
      sysPostMapper.insertPost(post);

      SysUserPost userPost = new SysUserPost();
      userPost.setUserId(existingUser.getUserId());
      userPost.setPostId(post.getPostId());
      userPost.setTenantId(customerId);
      List<SysUserPost> userPostList = new ArrayList<>();
      userPostList.add(userPost);
      sysUserPostMapper.batchUserPost(userPostList);
    }
  }
}

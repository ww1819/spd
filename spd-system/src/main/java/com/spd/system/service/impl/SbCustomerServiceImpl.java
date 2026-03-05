package com.spd.system.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.spd.common.constant.UserConstants;
import com.spd.common.core.domain.entity.SysUser;
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
    if (StringUtils.isEmpty(customer.getCustomerId())) {
      customer.setCustomerId(UUID7.generateUUID7());
    }
    customer.setCreateBy(SecurityUtils.getUsername());
    int rows = sbCustomerMapper.insertSbCustomer(customer);
    if (rows > 0) {
      createDefaultTenantAdmin(customer.getCustomerId(),
          customer.getCustomerName() != null ? customer.getCustomerName() : "客户",
          customer.getCreateBy());
    }
    return rows;
  }

  /**
   * 新增客户后自动创建：管理员组(super)、租户管理员角色、管理员账号(super_01)并加入该组；
   * 新租户和 super 组默认包含系统设置下的所有权限，不包含平台管理功能。
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

    // 新租户与 super 组默认包含系统设置下的所有权限，不包含平台管理功能
    List<String> defaultMenuIds = sbMenuMapper.selectMenuIdsSystemSettingsNonPlatform();
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
      for (String menuId : defaultMenuIds) {
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
  }

  @Override
  @Transactional
  public int updateSbCustomer(SbCustomer customer) {
    SbCustomer old = sbCustomerMapper.selectSbCustomerById(customer.getCustomerId());
    if (old == null) {
      return 0;
    }
    String operateBy = SecurityUtils.getUsername();
    Date now = new Date();

    if (StringUtils.isNotEmpty(customer.getStatus()) && !customer.getStatus().equals(old.getStatus())) {
      if (StringUtils.isEmpty(customer.getStatusChangeReason())) {
        throw new IllegalArgumentException("启停用原因不能为空，请通过启停用操作并填写原因");
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
    return sbCustomerMapper.deleteSbCustomerById(customerId, SecurityUtils.getUsername());
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
}

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
import com.spd.system.domain.SbCustomerPeriodLog;
import com.spd.system.domain.SbCustomerStatusLog;
import com.spd.system.mapper.SbCustomerMapper;
import com.spd.system.mapper.SbCustomerPeriodLogMapper;
import com.spd.system.mapper.SbCustomerStatusLogMapper;
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
  private ISysUserService sysUserService;
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

  /** 新增客户时默认机构管理员岗位标识 */
  private static final String DEFAULT_GROUP_KEY = "super";
  private static final String DEFAULT_SUPER_GROUP_NAME = "机构管理员";
  /** 新增客户时默认管理员账号 */
  private static final String DEFAULT_ADMIN_USERNAME = "super_01";

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
   * 新增客户后自动创建耗材侧机构管理员：super_01 账号、super 岗位及默认菜单权限。
   */
  private void createDefaultTenantAdmin(String customerId, String customerName, String createBy) {
    String initPassword = configService.selectConfigByKey("sys.user.initPassword");
    if (StringUtils.isEmpty(initPassword)) {
      initPassword = "admin123";
    } else {
      initPassword = initPassword.trim();
    }
    SysUser user = new SysUser();
    user.setCustomerId(customerId);
    user.setUserName(DEFAULT_ADMIN_USERNAME);
    user.setNickName("机构管理员");
    user.setPassword(SecurityUtils.encryptPassword(initPassword));
    user.setStatus("0");
    user.setCreateBy(createBy);
    sysUserService.insertUser(user);

    SysPost post = new SysPost();
    post.setPostCode(DEFAULT_GROUP_KEY);
    post.setPostName(DEFAULT_SUPER_GROUP_NAME);
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

  /**
   * 耗材功能重置：仅下发「客户名下」默认开放的功能菜单（{@link #resolveDefaultMaterialMenuIds}），不含平台独占菜单；
   * 若 super 岗位、super_01 或其关联、tenant_id 缺失，则自动补齐并回填租户字段，保证管理员组用户可见租户内全部仓库与科室（见 {@link com.spd.system.service.ITenantScopeService#isTenantSuper}）。
   * 重置写入 hc_customer_menu 后，会删除该租户下所有工作组、所有用户中已不在客户菜单范围内的 sys_post_menu、hc_user_permission_menu、sys_user_menu 记录，再为 super_01 回填 sys_user_menu。
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

    // 与「耗材客户权限」保存一致：重置后的 hc_customer_menu 为唯一真源，清理该租户下所有岗位/用户仍挂着的越权菜单
    sysPostMenuMapper.deletePostMenusNotInHcCustomerMenus(customerId);
    hcUserPermissionMenuMapper.deleteHcUserPermissionMenusNotInHcCustomerMenus(customerId);
    sysUserMenuMapper.deleteUserMenusNotInHcCustomerMenus(customerId);

    syncSysUserMenusForMaterial(super01.getUserId(), materialMenuIds, customerId);
  }

  /** 耗材侧：若 super 岗位或 super_01 缺失则补齐（不创建菜单） */
  private void ensureMaterialSuperAndUser(String customerId, String customerName, String createBy, SysPost existingPost, SysUser existingUser) {
    if (existingUser != null && existingPost != null) {
      // 回填岗位租户 ID（历史数据 tenant_id 为空会导致功能重置/管理员判定失败）
      sysPostMapper.updatePostTenantIdIfBlank(existingPost.getPostId(), customerId);
      // 岗位与用户均存在时仍可能缺少 sys_user_post（历史数据或误删），补齐后「用户管理-机构管理员」工作组筛选才能命中
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
        post.setPostName(DEFAULT_SUPER_GROUP_NAME);
        post.setPostSort(0);
        post.setStatus("0");
        post.setTenantId(customerId);
        post.setCreateBy(createBy);
        sysPostMapper.insertPost(post);
      }

      SysUser user = new SysUser();
      user.setCustomerId(customerId);
      user.setUserName(DEFAULT_ADMIN_USERNAME);
      user.setNickName("机构管理员");
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
      post.setPostName(DEFAULT_SUPER_GROUP_NAME);
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

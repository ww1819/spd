package com.spd.system.service.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.spd.common.core.domain.entity.SysMenu;
import com.spd.common.utils.SecurityUtils;
import com.spd.common.utils.StringUtils;
import com.spd.common.utils.uuid.UUID7;
import com.spd.system.domain.hc.HcCustomerMenuPeriodLog;
import com.spd.system.domain.hc.HcCustomerMenuStatusLog;
import com.spd.system.domain.vo.HcCustomerMenuManageVo;
import com.spd.system.mapper.HcCustomerMenuMapper;
import com.spd.system.mapper.HcCustomerMenuPeriodLogMapper;
import com.spd.system.mapper.HcCustomerMenuStatusLogMapper;
import com.spd.system.mapper.SysMenuMapper;
import com.spd.system.service.IHcCustomerMenuManageService;

/**
 * 耗材客户菜单功能管理：对客户（租户）在耗材侧已分配功能做启用/停用
 */
@Service
public class HcCustomerMenuManageServiceImpl implements IHcCustomerMenuManageService {

  @Autowired
  private HcCustomerMenuMapper hcCustomerMenuMapper;
  @Autowired
  private SysMenuMapper sysMenuMapper;
  @Autowired
  private HcCustomerMenuStatusLogMapper statusLogMapper;
  @Autowired
  private HcCustomerMenuPeriodLogMapper periodLogMapper;

  @Override
  public List<HcCustomerMenuManageVo> listMenusByTenantId(String tenantId) {
    List<HcCustomerMenuManageVo> list = hcCustomerMenuMapper.selectListWithMenuNameByTenantId(tenantId);
    if (list == null) {
      list = new ArrayList<>();
    }
    for (HcCustomerMenuManageVo vo : list) {
      if (vo != null) {
        vo.setAssigned(Boolean.TRUE);
      }
    }
    appendAncestorNodes(list, tenantId);
    list.sort(Comparator
        .comparing((HcCustomerMenuManageVo v) -> v.getParentId() == null ? 0L : v.getParentId())
        .thenComparing(v -> v.getOrderNum() == null ? 0 : v.getOrderNum())
        .thenComparing(v -> v.getMenuId() == null ? 0L : v.getMenuId()));
    return list;
  }

  /** 客户未单独登记父级目录时，向上补齐祖先节点，便于前端按菜单层级展示 */
  private void appendAncestorNodes(List<HcCustomerMenuManageVo> list, String tenantId) {
    Map<Long, HcCustomerMenuManageVo> byId = new LinkedHashMap<>();
    for (HcCustomerMenuManageVo vo : list) {
      if (vo != null && vo.getMenuId() != null) {
        byId.put(vo.getMenuId(), vo);
      }
    }
    int guard = 0;
    while (guard++ < 64) {
      Set<Long> missing = new HashSet<>();
      for (HcCustomerMenuManageVo vo : byId.values()) {
        Long pid = vo.getParentId();
        if (pid != null && pid > 0 && !byId.containsKey(pid)) {
          missing.add(pid);
        }
      }
      if (missing.isEmpty()) {
        break;
      }
      List<SysMenu> parents = sysMenuMapper.selectMenuByIds(new ArrayList<>(missing));
      if (parents == null || parents.isEmpty()) {
        break;
      }
      int added = 0;
      for (SysMenu parent : parents) {
        if (parent == null || parent.getMenuId() == null || byId.containsKey(parent.getMenuId())) {
          continue;
        }
        HcCustomerMenuManageVo node = new HcCustomerMenuManageVo();
        node.setTenantId(tenantId);
        node.setMenuId(parent.getMenuId());
        node.setParentId(parent.getParentId());
        node.setMenuName(parent.getMenuName());
        node.setMenuType(parent.getMenuType());
        node.setOrderNum(parent.getOrderNum());
        node.setAssigned(Boolean.FALSE);
        byId.put(parent.getMenuId(), node);
        list.add(node);
        added++;
      }
      if (added == 0) {
        break;
      }
    }
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public int changeStatus(String tenantId, Long menuId, String status, String reason) {
    if (StringUtils.isEmpty(tenantId) || menuId == null || StringUtils.isEmpty(status)) {
      return 0;
    }
    if (StringUtils.isEmpty(reason)) {
      throw new IllegalArgumentException("启停用原因不能为空");
    }
    if (hcCustomerMenuMapper.countByTenantIdAndMenuId(tenantId, menuId) == 0) {
      return 0;
    }
    String operateBy = SecurityUtils.getUserIdStr();
    Date now = new Date();

    HcCustomerMenuStatusLog statusLog = new HcCustomerMenuStatusLog();
    statusLog.setLogId(UUID7.generateUUID7());
    statusLog.setTenantId(tenantId);
    statusLog.setMenuId(menuId);
    statusLog.setStatus(status);
    statusLog.setOperateTime(now);
    statusLog.setOperateBy(operateBy);
    statusLog.setReason(reason);
    statusLogMapper.insert(statusLog);

    if ("0".equals(status)) {
      HcCustomerMenuPeriodLog lastSuspend = periodLogMapper.selectLastWithNullEnd(tenantId, menuId, HcCustomerMenuPeriodLog.PERIOD_TYPE_SUSPEND);
      if (lastSuspend != null) {
        periodLogMapper.updateEndTime(lastSuspend.getPeriodId(), now);
      }
      HcCustomerMenuPeriodLog usage = new HcCustomerMenuPeriodLog();
      usage.setPeriodId(UUID7.generateUUID7());
      usage.setTenantId(tenantId);
      usage.setMenuId(menuId);
      usage.setPeriodType(HcCustomerMenuPeriodLog.PERIOD_TYPE_USAGE);
      usage.setStartTime(now);
      usage.setEndTime(null);
      usage.setCreateBy(operateBy);
      usage.setCreateTime(now);
      periodLogMapper.insert(usage);
    } else {
      HcCustomerMenuPeriodLog lastUsage = periodLogMapper.selectLastWithNullEnd(tenantId, menuId, HcCustomerMenuPeriodLog.PERIOD_TYPE_USAGE);
      if (lastUsage != null) {
        periodLogMapper.updateEndTime(lastUsage.getPeriodId(), now);
      }
      HcCustomerMenuPeriodLog suspend = new HcCustomerMenuPeriodLog();
      suspend.setPeriodId(UUID7.generateUUID7());
      suspend.setTenantId(tenantId);
      suspend.setMenuId(menuId);
      suspend.setPeriodType(HcCustomerMenuPeriodLog.PERIOD_TYPE_SUSPEND);
      suspend.setStartTime(now);
      suspend.setEndTime(null);
      suspend.setCreateBy(operateBy);
      suspend.setCreateTime(now);
      periodLogMapper.insert(suspend);
    }

    return hcCustomerMenuMapper.updateStatus(tenantId, menuId, status);
  }

  @Override
  public List<HcCustomerMenuStatusLog> getStatusLogList(String tenantId, Long menuId) {
    return statusLogMapper.selectByTenantIdAndMenuId(tenantId, menuId);
  }

  @Override
  public List<HcCustomerMenuPeriodLog> getPeriodLogList(String tenantId, Long menuId) {
    return periodLogMapper.selectByTenantIdAndMenuId(tenantId, menuId);
  }
}

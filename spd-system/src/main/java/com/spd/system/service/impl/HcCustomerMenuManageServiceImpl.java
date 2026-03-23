package com.spd.system.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.spd.common.utils.SecurityUtils;
import com.spd.common.utils.StringUtils;
import com.spd.common.utils.uuid.UUID7;
import com.spd.system.domain.hc.HcCustomerMenuPeriodLog;
import com.spd.system.domain.hc.HcCustomerMenuStatusLog;
import com.spd.system.domain.vo.HcCustomerMenuManageVo;
import com.spd.system.mapper.HcCustomerMenuMapper;
import com.spd.system.mapper.HcCustomerMenuPeriodLogMapper;
import com.spd.system.mapper.HcCustomerMenuStatusLogMapper;
import com.spd.system.service.IHcCustomerMenuManageService;

/**
 * 耗材客户菜单功能管理：对客户（租户）在耗材侧已分配功能做启用/停用
 */
@Service
public class HcCustomerMenuManageServiceImpl implements IHcCustomerMenuManageService {

  @Autowired
  private HcCustomerMenuMapper hcCustomerMenuMapper;
  @Autowired
  private HcCustomerMenuStatusLogMapper statusLogMapper;
  @Autowired
  private HcCustomerMenuPeriodLogMapper periodLogMapper;

  @Override
  public List<HcCustomerMenuManageVo> listMenusByTenantId(String tenantId) {
    return hcCustomerMenuMapper.selectListWithMenuNameByTenantId(tenantId);
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

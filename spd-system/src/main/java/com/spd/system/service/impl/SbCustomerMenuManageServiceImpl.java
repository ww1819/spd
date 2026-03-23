package com.spd.system.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.spd.common.utils.SecurityUtils;
import com.spd.common.utils.StringUtils;
import com.spd.common.utils.uuid.UUID7;
import com.spd.system.domain.SbCustomerMenuPeriodLog;
import com.spd.system.domain.SbCustomerMenuStatusLog;
import com.spd.system.domain.vo.SbCustomerMenuManageVo;
import com.spd.system.mapper.SbCustomerMenuMapper;
import com.spd.system.mapper.SbCustomerMenuPeriodLogMapper;
import com.spd.system.mapper.SbCustomerMenuStatusLogMapper;
import com.spd.system.service.ISbCustomerMenuManageService;

/**
 * 客户菜单功能管理：对客户已分配的功能做启用/停用
 */
@Service
public class SbCustomerMenuManageServiceImpl implements ISbCustomerMenuManageService {

  @Autowired
  private SbCustomerMenuMapper sbCustomerMenuMapper;
  @Autowired
  private SbCustomerMenuStatusLogMapper statusLogMapper;
  @Autowired
  private SbCustomerMenuPeriodLogMapper periodLogMapper;

  @Override
  public List<SbCustomerMenuManageVo> listMenusByCustomerId(String customerId) {
    return sbCustomerMenuMapper.selectListWithMenuNameByCustomerId(customerId);
  }

  @Override
  @Transactional
  public int changeStatus(String customerId, String menuId, String status, String reason) {
    if (StringUtils.isEmpty(customerId) || StringUtils.isEmpty(menuId) || StringUtils.isEmpty(status)) {
      return 0;
    }
    if (StringUtils.isEmpty(reason)) {
      throw new IllegalArgumentException("启停用原因不能为空");
    }
    if (sbCustomerMenuMapper.countByCustomerIdAndMenuId(customerId, menuId) == 0) {
      return 0;
    }
    String operateBy = SecurityUtils.getUserIdStr();
    Date now = new Date();

    SbCustomerMenuStatusLog statusLog = new SbCustomerMenuStatusLog();
    statusLog.setLogId(UUID7.generateUUID7());
    statusLog.setCustomerId(customerId);
    statusLog.setMenuId(menuId);
    statusLog.setStatus(status);
    statusLog.setOperateTime(now);
    statusLog.setOperateBy(operateBy);
    statusLog.setReason(reason);
    statusLogMapper.insert(statusLog);

    if ("0".equals(status)) {
      SbCustomerMenuPeriodLog lastSuspend = periodLogMapper.selectLastWithNullEnd(customerId, menuId, SbCustomerMenuPeriodLog.PERIOD_TYPE_SUSPEND);
      if (lastSuspend != null) {
        periodLogMapper.updateEndTime(lastSuspend.getPeriodId(), now);
      }
      SbCustomerMenuPeriodLog usage = new SbCustomerMenuPeriodLog();
      usage.setPeriodId(UUID7.generateUUID7());
      usage.setCustomerId(customerId);
      usage.setMenuId(menuId);
      usage.setPeriodType(SbCustomerMenuPeriodLog.PERIOD_TYPE_USAGE);
      usage.setStartTime(now);
      usage.setEndTime(null);
      usage.setCreateBy(operateBy);
      usage.setCreateTime(now);
      periodLogMapper.insert(usage);
    } else {
      SbCustomerMenuPeriodLog lastUsage = periodLogMapper.selectLastWithNullEnd(customerId, menuId, SbCustomerMenuPeriodLog.PERIOD_TYPE_USAGE);
      if (lastUsage != null) {
        periodLogMapper.updateEndTime(lastUsage.getPeriodId(), now);
      }
      SbCustomerMenuPeriodLog suspend = new SbCustomerMenuPeriodLog();
      suspend.setPeriodId(UUID7.generateUUID7());
      suspend.setCustomerId(customerId);
      suspend.setMenuId(menuId);
      suspend.setPeriodType(SbCustomerMenuPeriodLog.PERIOD_TYPE_SUSPEND);
      suspend.setStartTime(now);
      suspend.setEndTime(null);
      suspend.setCreateBy(operateBy);
      suspend.setCreateTime(now);
      periodLogMapper.insert(suspend);
    }

    return sbCustomerMenuMapper.updateStatus(customerId, menuId, status);
  }

  @Override
  public List<SbCustomerMenuStatusLog> getStatusLogList(String customerId, String menuId) {
    return statusLogMapper.selectByCustomerIdAndMenuId(customerId, menuId);
  }

  @Override
  public List<SbCustomerMenuPeriodLog> getPeriodLogList(String customerId, String menuId) {
    return periodLogMapper.selectByCustomerIdAndMenuId(customerId, menuId);
  }
}

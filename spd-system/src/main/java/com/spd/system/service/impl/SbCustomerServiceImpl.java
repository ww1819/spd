package com.spd.system.service.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.spd.common.constant.UserConstants;
import com.spd.common.utils.SecurityUtils;
import com.spd.common.utils.StringUtils;
import com.spd.common.utils.uuid.UUID7;
import com.spd.system.domain.SbCustomer;
import com.spd.system.domain.SbCustomerPeriodLog;
import com.spd.system.domain.SbCustomerStatusLog;
import com.spd.system.mapper.SbCustomerMapper;
import com.spd.system.mapper.SbCustomerPeriodLogMapper;
import com.spd.system.mapper.SbCustomerStatusLogMapper;
import com.spd.system.service.ISbCustomerService;

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
  public int insertSbCustomer(SbCustomer customer) {
    if (StringUtils.isEmpty(customer.getCustomerId())) {
      customer.setCustomerId(UUID7.generateUUID7());
    }
    customer.setCreateBy(SecurityUtils.getUsername());
    return sbCustomerMapper.insertSbCustomer(customer);
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
      SbCustomerStatusLog statusLog = new SbCustomerStatusLog();
      statusLog.setLogId(UUID7.generateUUID7());
      statusLog.setCustomerId(customer.getCustomerId());
      statusLog.setStatus(customer.getStatus());
      statusLog.setOperateTime(now);
      statusLog.setOperateBy(operateBy);
      statusLog.setReason(customer.getStatusChangeReason());
      sbCustomerStatusLogMapper.insert(statusLog);
    }

    Date oldPlanned = old.getPlannedDisableTime();
    Date newPlanned = customer.getPlannedDisableTime();
    boolean plannedChanged = (oldPlanned == null && newPlanned != null)
        || (oldPlanned != null && (newPlanned == null || oldPlanned.getTime() != newPlanned.getTime()));
    if (plannedChanged && oldPlanned != null && now.getTime() >= oldPlanned.getTime()) {
      Date usageStart = sbCustomerPeriodLogMapper.selectLastUsageEndTime(customer.getCustomerId());
      if (usageStart == null && old.getCreateTime() != null) {
        usageStart = old.getCreateTime();
      }
      if (usageStart == null) {
        Calendar c = Calendar.getInstance();
        c.set(2000, 0, 1);
        usageStart = c.getTime();
      }
      SbCustomerPeriodLog suspendPeriod = new SbCustomerPeriodLog();
      suspendPeriod.setPeriodId(UUID7.generateUUID7());
      suspendPeriod.setCustomerId(customer.getCustomerId());
      suspendPeriod.setPeriodType(SbCustomerPeriodLog.PERIOD_TYPE_SUSPEND);
      suspendPeriod.setStartTime(oldPlanned);
      suspendPeriod.setEndTime(now);
      suspendPeriod.setCreateBy(operateBy);
      suspendPeriod.setCreateTime(now);
      sbCustomerPeriodLogMapper.insert(suspendPeriod);

      SbCustomerPeriodLog usagePeriod = new SbCustomerPeriodLog();
      usagePeriod.setPeriodId(UUID7.generateUUID7());
      usagePeriod.setCustomerId(customer.getCustomerId());
      usagePeriod.setPeriodType(SbCustomerPeriodLog.PERIOD_TYPE_USAGE);
      usagePeriod.setStartTime(usageStart);
      usagePeriod.setEndTime(oldPlanned);
      usagePeriod.setCreateBy(operateBy);
      usagePeriod.setCreateTime(now);
      sbCustomerPeriodLogMapper.insert(usagePeriod);
    }

    customer.setUpdateBy(operateBy);
    return sbCustomerMapper.updateSbCustomer(customer);
  }

  @Override
  public int deleteSbCustomerById(String customerId) {
    return sbCustomerMapper.deleteSbCustomerById(customerId, SecurityUtils.getUsername());
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

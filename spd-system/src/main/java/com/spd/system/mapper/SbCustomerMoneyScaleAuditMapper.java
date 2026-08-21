package com.spd.system.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.spd.system.domain.SbCustomerMoneyScaleAudit;

/**
 * 客户金额小数位变更审核
 */
public interface SbCustomerMoneyScaleAuditMapper {

  SbCustomerMoneyScaleAudit selectById(String auditId);

  List<SbCustomerMoneyScaleAudit> selectByCustomerId(@Param("customerId") String customerId);

  SbCustomerMoneyScaleAudit selectPendingByCustomerId(@Param("customerId") String customerId);

  int insert(SbCustomerMoneyScaleAudit row);

  int updateAuditResult(SbCustomerMoneyScaleAudit row);
}

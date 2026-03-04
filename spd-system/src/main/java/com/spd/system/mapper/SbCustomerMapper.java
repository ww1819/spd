package com.spd.system.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.spd.system.domain.SbCustomer;

/**
 * 设备系统客户表 sb_customer 数据层
 */
public interface SbCustomerMapper {

  List<SbCustomer> selectSbCustomerList(SbCustomer customer);

  SbCustomer selectSbCustomerById(String customerId);

  SbCustomer selectSbCustomerByCode(String customerCode);

  SbCustomer checkSbCustomerCodeUnique(String customerCode);

  int insertSbCustomer(SbCustomer customer);

  int updateSbCustomer(SbCustomer customer);

  /**
   * 逻辑删除（置删除者、删除时间）
   */
  int deleteSbCustomerById(@Param("customerId") String customerId, @Param("deleteBy") String deleteBy);
}

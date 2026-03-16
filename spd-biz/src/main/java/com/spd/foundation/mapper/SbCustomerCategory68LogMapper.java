package com.spd.foundation.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.spd.foundation.domain.SbCustomerCategory68Log;

/**
 * 客户68分类操作记录 Mapper 接口
 *
 * @author spd
 */
public interface SbCustomerCategory68LogMapper {

    int insert(SbCustomerCategory68Log log);

    List<SbCustomerCategory68Log> selectByCustomerId(@Param("customerId") String customerId);

    List<SbCustomerCategory68Log> selectByTargetId(@Param("targetId") String targetId);
}

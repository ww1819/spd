package com.spd.system.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.spd.system.domain.SbUserPermissionDept;

/**
 * 设备用户科室权限 sb_user_permission_dept 数据层
 */
public interface SbUserPermissionDeptMapper {

  List<Long> selectDeptIdsByUserId(@Param("userId") Long userId, @Param("customerId") String customerId);

  int deleteByUserId(@Param("userId") Long userId, @Param("deleteBy") String deleteBy);

  int insert(SbUserPermissionDept row);

  int batchInsert(List<SbUserPermissionDept> list);
}

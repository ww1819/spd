package com.spd.foundation.mapper;

import com.spd.foundation.domain.FdFinanceCategoryChangeLog;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 财务分类档案变更记录
 */
public interface FdFinanceCategoryChangeLogMapper {

    int insert(FdFinanceCategoryChangeLog record);

    List<FdFinanceCategoryChangeLog> selectByEntityId(@Param("financeCategoryId") Long financeCategoryId);
}

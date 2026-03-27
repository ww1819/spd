package com.spd.foundation.mapper;

import com.spd.foundation.domain.FdSupplierChangeLog;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 供应商档案变更记录
 */
public interface FdSupplierChangeLogMapper {

    int insert(FdSupplierChangeLog record);

    List<FdSupplierChangeLog> selectBySupplierId(@Param("supplierId") Long supplierId);
}

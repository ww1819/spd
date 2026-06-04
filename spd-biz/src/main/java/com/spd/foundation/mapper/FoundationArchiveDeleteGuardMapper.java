package com.spd.foundation.mapper;

import org.apache.ibatis.annotations.Param;

/**
 * 基础档案删除前业务引用校验（未删除业务数据存在则不可删）
 */
public interface FoundationArchiveDeleteGuardMapper
{
    int countMaterialBusinessUsage(@Param("materialId") Long materialId);

    int countWarehouseBusinessUsage(@Param("warehouseId") Long warehouseId);

    int countSupplierBusinessUsage(@Param("supplierId") Long supplierId);

    int countFactoryBusinessUsage(@Param("factoryId") Long factoryId);
}

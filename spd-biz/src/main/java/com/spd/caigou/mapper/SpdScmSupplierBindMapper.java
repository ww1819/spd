package com.spd.caigou.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.spd.caigou.domain.SpdScmSupplierBind;

@Mapper
@Repository
public interface SpdScmSupplierBindMapper
{
    SpdScmSupplierBind selectByTenantAndSupplier(@Param("tenantId") String tenantId, @Param("supplierId") String supplierId);

    String selectSupplierCode(@Param("tenantId") String tenantId, @Param("supplierId") String supplierId);

    List<SpdScmSupplierBind> selectListByTenantId(@Param("tenantId") String tenantId);

    int insert(SpdScmSupplierBind row);

    int updateByTenantAndSupplier(SpdScmSupplierBind row);
}

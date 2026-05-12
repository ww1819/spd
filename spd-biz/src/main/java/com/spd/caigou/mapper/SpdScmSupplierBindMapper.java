package com.spd.caigou.mapper;

import java.util.Collection;
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

    /** 含已逻辑删除，用于保证租户+供应商仅一条物理记录时的保存/复活 */
    SpdScmSupplierBind selectByTenantAndSupplierAnyDel(@Param("tenantId") String tenantId, @Param("supplierId") String supplierId);

    String selectSupplierCode(@Param("tenantId") String tenantId, @Param("supplierId") String supplierId);

    List<SpdScmSupplierBind> selectListByTenantId(@Param("tenantId") String tenantId,
            @Param("spdSupplierCode") String spdSupplierCode,
            @Param("scmSupplierCode") String scmSupplierCode,
            @Param("referredCode") String referredCode);

    int insert(SpdScmSupplierBind row);

    int updateByTenantAndSupplier(SpdScmSupplierBind row);

    /** 复活已删除绑定并更新编码（where 仅 tenant+supplier，不按 del_flag） */
    int updateReviveByTenantAndSupplier(SpdScmSupplierBind row);

    /** 逻辑删除：del_flag 置为 1 */
    int logicalDeleteByTenantAndSupplierIds(@Param("tenantId") String tenantId,
            @Param("supplierIds") Collection<String> supplierIds, @Param("updateBy") String updateBy);
}

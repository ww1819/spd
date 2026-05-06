package com.spd.caigou.service;

import com.spd.caigou.domain.SpdScmSupplierBind;
import com.spd.caigou.domain.SpdScmTenantBind;

import java.util.List;

/**
 * SPD 租户/供应商与云平台编码绑定维护
 */
public interface ISpdScmBindService
{
    SpdScmTenantBind getTenantBind();

    void saveTenantBind(String scmHospitalCode, String remark);

    SpdScmSupplierBind getSupplierBind(Long supplierId);

    List<SpdScmSupplierBind> listSupplierBinds();

    void saveSupplierBind(Long supplierId, String scmSupplierCode, String remark);
}

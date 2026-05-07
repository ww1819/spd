package com.spd.caigou.service;

import com.spd.caigou.domain.SpdScmSupplierBind;
import com.spd.caigou.domain.SpdScmTenantBind;

import java.util.List;
import java.util.Set;

/**
 * SPD 租户/供应商与云平台编码绑定维护
 */
public interface ISpdScmBindService
{
    SpdScmTenantBind getTenantBind();

    void saveTenantBind(String scmHospitalCode, String remark);

    SpdScmSupplierBind getSupplierBind(Long supplierId);

    /**
     * 供应商绑定列表；条件均为模糊匹配，空则忽略
     *
     * @param spdSupplierCode SPD 供应商编码（fd_supplier.code）
     * @param scmSupplierCode 平台供应商编码（绑定表 scm_supplier_code）
     * @param referredCode    供应商名称简码 / 拼音简码（fd_supplier.referred_code）
     */
    List<SpdScmSupplierBind> listSupplierBinds(String spdSupplierCode, String scmSupplierCode, String referredCode);

    void saveSupplierBind(Long supplierId, String scmSupplierCode, String remark);

    /** 逻辑删除当前租户下指定 SPD 供应商的绑定（del_flag=1） */
    int removeSupplierBinds(Set<Long> supplierIds);
}

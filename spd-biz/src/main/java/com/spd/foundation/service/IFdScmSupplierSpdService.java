package com.spd.foundation.service;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson2.JSONObject;

/**
 * 院内端：供应链平台供应商信息查询、下载、向院内主数据补全
 */
public interface IFdScmSupplierSpdService
{
    /**
     * 当前租户在平台医院下可见的供应商列表（经前置机查 SCM）
     */
    List<Map<String, Object>> listScmSuppliersForTenantHospital();

    /**
     * 平台供应商档案 + 是否院供绑定（经前置机；会写 SCM 审计日志）
     */
    JSONObject loadScmSupplierProfile(String scmSupplierCode);

    /**
     * 组装可下载的供应商 JSON：FULL 或 LIMITED
     */
    JSONObject buildExportPayload(Long spdSupplierId);

    /**
     * 用平台主数据补全院内 fd_supplier（默认仅填空字段）
     */
    void mergeScmIntoFdSupplier(Long spdSupplierId, boolean overwriteNonEmpty);

    /**
     * 当前租户下某院内供应商绑定的平台编码（无绑定返回 null）
     */
    String resolveScmSupplierCode(Long spdSupplierId);
}

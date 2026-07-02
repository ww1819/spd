package com.spd.foundation.util;

import com.spd.common.exception.ServiceException;
import com.spd.common.utils.DictUtils;
import com.spd.common.utils.StringUtils;
import com.spd.foundation.domain.FdWarehouse;

/**
 * 仓库启用状态（字典 is_use_status，与耗材 is_use 一致：1=启用，2=停用）
 */
public final class WarehouseStatusUtil {

    /** 与耗材档案 is_use 一致 */
    public static final String STATUS_ENABLED = "1";
    public static final String STATUS_DISABLED = "2";

    private static final String DICT_TYPE = "is_use_status";

    private WarehouseStatusUtil() {
    }

    public static boolean isEnabled(FdWarehouse warehouse) {
        if (warehouse == null) {
            return false;
        }
        return !isDisabledStatus(warehouse.getWarehouseStatus());
    }

    public static boolean isDisabledStatus(String warehouseStatus) {
        String status = StringUtils.trimToNull(warehouseStatus);
        if (status == null) {
            return false;
        }
        String label = DictUtils.getDictLabel(DICT_TYPE, status);
        if (StringUtils.isNotEmpty(label)) {
            if (label.contains("停")) {
                return true;
            }
            if (label.contains("启") || label.contains("正常")) {
                return false;
            }
        }
        return STATUS_DISABLED.equals(status);
    }

    public static void assertEnabledForInbound(FdWarehouse warehouse, String message) {
        if (!isEnabled(warehouse)) {
            throw new ServiceException(message);
        }
    }
}

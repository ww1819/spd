package com.spd.foundation.constants;

/**
 * 众阳 HIS 对接常量（业务报文字段；租户判定见 {@link com.spd.foundation.support.MsunHisTenantRegistry}）。
 */
public final class MsunHisConstants
{
    private MsunHisConstants()
    {
    }

    public static final String TENANT_ZQ_TCM = "zaoqiang-tcm-001";

    /** 未推送 */
    public static final String PUSH_NOT = "0";
    /** 推送中 */
    public static final String PUSHING = "1";
    /** 成功 */
    public static final String PUSH_SUCCESS = "2";
    /** 失败 */
    public static final String PUSH_FAILED = "3";

    /** 入药房并增加库存（pharmacyDeptId 必传） */
    public static final String IN_STOCK_STATUS_PHARMACY = "";
    public static final String SAVE_CORRELATION_FLAG = "1";
    public static final String RETURN_TO_SUPPLIER_YES = "1";

    public static String buildEntryMemo(String tenantId, Long entryId)
    {
        return "ZQ-" + tenantId + "-" + entryId;
    }
}

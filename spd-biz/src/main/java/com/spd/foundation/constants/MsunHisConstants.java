package com.spd.foundation.constants;

import com.spd.common.utils.StringUtils;

/**
 * 众阳 HIS 对接常量（业务报文字段；租户判定见 {@link com.spd.foundation.support.MsunHisTenantRegistry}）。
 *
 * <h3>SPD 明细对照键 {@code spdDetailId}（写入 {@code stk_io_bill_entry.his_spd_detail_id}，随 2.5.41 报文下发）</h3>
 * <ul>
 *   <li><b>连接符</b>：{@value #SPD_DETAIL_ID_SEP}（半角冒号，主表/明细/条码段均为数字主键）</li>
 *   <li><b>标准格式</b>：{@code {billMainId}:{entryDetailId}}，例 {@code 12001:88002}</li>
 *   <li><b>含条码（预留）</b>：{@code {billMainId}:{entryDetailId}:{barcodeDetailId}}，第三段为 SPD 单据条码明细主键，无条码时不拼第三段</li>
 *   <li><b>拼接</b>：{@link #buildSpdDetailId(Long, Long)} / {@link #buildSpdDetailId(Long, Long, Long)}</li>
 *   <li><b>解析</b>：{@link #parseSpdDetailId(String)} → {@link SpdDetailIdParts}；段数非 2/3 或含空段返回 {@code null}</li>
 *   <li><b>兼容</b>：历史仅明细 id 的纯数字串，匹配时同时尝试 {@code String.valueOf(entryId)}</li>
 * </ul>
 *
 * <h3>{@code memo}（与 spdDetailId 并存，租户内唯一）</h3>
 * <p>格式 {@code ZQ-{tenantId}-{entryId}}，用于 HIS 侧 memo 幂等与 2.5.42 退库对照；不替代 spdDetailId 主从拼接语义。</p>
 */
public final class MsunHisConstants
{
    private MsunHisConstants()
    {
    }

    public static final String TENANT_ZQ_TCM = "zaoqiang-tcm-001";

    /** spdDetailId 段连接符：主表id:明细id[:条码明细id] */
    public static final String SPD_DETAIL_ID_SEP = ":";

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

    /** 推送后校验：HIS 未生成出退库明细 */
    public static final String VERIFY_MSG_YK_DETAIL_MISSING = "HIS未生成出退库明细";
    /** 推送后校验：HIS 未查到汇总库存（2.5.82） */
    public static final String VERIFY_MSG_MERGE_STOCK_MISSING = "HIS未查到汇总库存";
    /** 推送后校验：HIS 未查到批次库存（2.5.43） */
    public static final String VERIFY_MSG_BATCH_STOCK_MISSING = "HIS未查到批次库存";
    /** 推送后校验：查询接口失败 */
    public static final String VERIFY_MSG_QUERY_FAILED = "推送后校验查询失败";

    public static String buildEntryMemo(String tenantId, Long entryId)
    {
        return "ZQ-" + tenantId + "-" + entryId;
    }

    /**
     * 拼接 spdDetailId：{@code billMainId:entryDetailId}。
     */
    public static String buildSpdDetailId(Long billMainId, Long entryDetailId)
    {
        if (billMainId == null || entryDetailId == null)
        {
            return null;
        }
        return billMainId + SPD_DETAIL_ID_SEP + entryDetailId;
    }

    /**
     * 拼接 spdDetailId（含条码明细段）：{@code billMainId:entryDetailId:barcodeDetailId}。
     */
    public static String buildSpdDetailId(Long billMainId, Long entryDetailId, Long barcodeDetailId)
    {
        String base = buildSpdDetailId(billMainId, entryDetailId);
        if (base == null)
        {
            return null;
        }
        if (barcodeDetailId == null)
        {
            return base;
        }
        return base + SPD_DETAIL_ID_SEP + barcodeDetailId;
    }

    /**
     * 解析 spdDetailId。支持 2 段（主表+明细）或 3 段（+条码明细）；无法识别返回 {@code null}。
     */
    public static SpdDetailIdParts parseSpdDetailId(String composite)
    {
        if (StringUtils.isEmpty(composite))
        {
            return null;
        }
        String trimmed = composite.trim();
        String[] parts = trimmed.split(SPD_DETAIL_ID_SEP, -1);
        if (parts.length != 2 && parts.length != 3)
        {
            return null;
        }
        try
        {
            Long billMainId = Long.valueOf(parts[0].trim());
            Long entryDetailId = Long.valueOf(parts[1].trim());
            Long barcodeDetailId = parts.length == 3 ? Long.valueOf(parts[2].trim()) : null;
            return new SpdDetailIdParts(billMainId, entryDetailId, barcodeDetailId);
        }
        catch (NumberFormatException ex)
        {
            return null;
        }
    }

    /** spdDetailId 解析结果 */
    public static final class SpdDetailIdParts
    {
        private final Long billMainId;
        private final Long entryDetailId;
        private final Long barcodeDetailId;

        public SpdDetailIdParts(Long billMainId, Long entryDetailId, Long barcodeDetailId)
        {
            this.billMainId = billMainId;
            this.entryDetailId = entryDetailId;
            this.barcodeDetailId = barcodeDetailId;
        }

        public Long getBillMainId()
        {
            return billMainId;
        }

        public Long getEntryDetailId()
        {
            return entryDetailId;
        }

        public Long getBarcodeDetailId()
        {
            return barcodeDetailId;
        }
    }
}

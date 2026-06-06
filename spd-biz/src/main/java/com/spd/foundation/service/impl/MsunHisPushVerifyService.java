package com.spd.foundation.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.spd.common.utils.DateUtils;
import com.spd.common.utils.StringUtils;
import com.spd.common.utils.http.HttpUtils;
import com.spd.foundation.constants.MsunHisConstants;
import com.spd.foundation.domain.FdMaterial;
import com.spd.foundation.mapper.FdMaterialMapper;
import com.spd.foundation.support.MsunHisTenantSupport;
import com.spd.system.service.ISysConfigService;
import com.spd.warehouse.domain.StkIoBill;
import com.spd.warehouse.domain.StkIoBillEntry;
import com.spd.warehouse.mapper.StkIoBillMapper;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 众阳 HIS 单据推送成功后的即时校验：拉取 2.5.102 出退库明细，出库单另查 2.5.43 批次库存，
 * 在 {@code stk_io_bill_entry.his_push_msg} 标注 HIS 未落明细/库存等异常（不抛异常、不回滚推送结果）。
 */
@Service
public class MsunHisPushVerifyService
{
    private static final Logger log = LoggerFactory.getLogger(MsunHisPushVerifyService.class);

    private static final String DEFAULT_INTERFACE_IP = "127.0.0.1";
    private static final String DEFAULT_INTERFACE_PORT = "8088";
    private static final int VERIFY_WINDOW_MINUTES = 15;

    private final ISysConfigService sysConfigService;
    private final StkIoBillMapper stkIoBillMapper;
    private final FdMaterialMapper fdMaterialMapper;

    public MsunHisPushVerifyService(
            ISysConfigService sysConfigService,
            StkIoBillMapper stkIoBillMapper,
            FdMaterialMapper fdMaterialMapper)
    {
        this.sysConfigService = sysConfigService;
        this.stkIoBillMapper = stkIoBillMapper;
        this.fdMaterialMapper = fdMaterialMapper;
    }

    /**
     * 出退库推送 HTTP 已成功后的校验入口（捕获全部异常，仅写标注）。
     *
     * @param outbound true=出库201（含 2.5.43 批次库存校验）；false=退库401（仅 2.5.102）
     */
    public void verifyAfterPush(
            StkIoBill bill,
            List<StkIoBillEntry> entries,
            String storageDeptHisId,
            String pharmacyDeptHisId,
            String tenantId,
            boolean outbound)
    {
        if (bill == null || entries == null || entries.isEmpty())
        {
            return;
        }
        try
        {
            List<String> billWarnings = new ArrayList<>();
            JSONArray ykDetails = fetchYkInstockDetails(bill, storageDeptHisId, outbound, tenantId);
            if (ykDetails == null)
            {
                billWarnings.add(MsunHisConstants.VERIFY_MSG_QUERY_FAILED + "(2.5.102)");
            }
            for (StkIoBillEntry entry : entries)
            {
                if (entry == null || entry.getId() == null)
                {
                    continue;
                }
                List<String> entryWarnings = new ArrayList<>();
                if (ykDetails == null)
                {
                    entryWarnings.add(MsunHisConstants.VERIFY_MSG_YK_DETAIL_MISSING);
                }
                else if (!matchYkInstockDetail(ykDetails, bill, entry, tenantId))
                {
                    entryWarnings.add(MsunHisConstants.VERIFY_MSG_YK_DETAIL_MISSING);
                }
                if (outbound)
                {
                    verifyOutboundStocks(entry, pharmacyDeptHisId, tenantId, entryWarnings);
                }
                String msg = entryWarnings.isEmpty() ? null : String.join("；", entryWarnings);
                if (msg != null)
                {
                    stkIoBillMapper.updateEntryHisPushStatus(entry.getId(), MsunHisConstants.PUSH_SUCCESS, msg);
                    billWarnings.add("明细" + entry.getId() + ":" + msg);
                }
                else
                {
                    stkIoBillMapper.updateEntryHisPushStatus(entry.getId(), MsunHisConstants.PUSH_SUCCESS, null);
                }
            }
            if (!billWarnings.isEmpty())
            {
                String billMsg = "推送成功，校验异常：" + String.join("；", billWarnings);
                if (billMsg.length() > 480)
                {
                    billMsg = billMsg.substring(0, 480) + "…";
                }
                stkIoBillMapper.updateBillHisPushStatus(
                    bill.getId(), MsunHisConstants.PUSH_SUCCESS, billMsg, null, new Date());
            }
        }
        catch (Exception ex)
        {
            log.warn("众阳HIS推送后校验异常 billId={} err={}", bill.getId(), ex.getMessage());
            String msg = MsunHisConstants.VERIFY_MSG_QUERY_FAILED + ":" + ex.getMessage();
            if (msg.length() > 480)
            {
                msg = msg.substring(0, 480);
            }
            stkIoBillMapper.updateBillHisPushStatus(
                bill.getId(), MsunHisConstants.PUSH_SUCCESS, msg, null, new Date());
        }
    }

    private void verifyOutboundStocks(
            StkIoBillEntry entry,
            String pharmacyDeptHisId,
            String tenantId,
            List<String> entryWarnings)
    {
        FdMaterial material = entry.getMaterialId() != null
            ? fdMaterialMapper.selectFdMaterialById(entry.getMaterialId()) : null;
        if (material == null || StringUtils.isEmpty(material.getHisId())
            || StringUtils.isEmpty(material.getHisSpecPackingId()))
        {
            entryWarnings.add(MsunHisConstants.VERIFY_MSG_BATCH_STOCK_MISSING + "(缺HIS对照)");
            return;
        }
        Long deptId = parseLongQuiet(pharmacyDeptHisId);
        Long drugId = parseLongQuiet(material.getHisId());
        Long specId = parseLongQuiet(material.getHisSpecPackingId());
        if (deptId == null || drugId == null || specId == null)
        {
            entryWarnings.add(MsunHisConstants.VERIFY_MSG_BATCH_STOCK_MISSING + "(科室/耗材对照非法)");
            return;
        }
        JSONArray batchRows = fetchBatchStockRows(deptId, drugId, specId, tenantId);
        if (batchRows == null)
        {
            entryWarnings.add(MsunHisConstants.VERIFY_MSG_BATCH_STOCK_MISSING + "(查询失败)");
            return;
        }
        if (!matchBatchStock(batchRows, entry))
        {
            entryWarnings.add(MsunHisConstants.VERIFY_MSG_BATCH_STOCK_MISSING);
        }
    }

    private JSONArray fetchYkInstockDetails(
            StkIoBill bill, String storageDeptHisId, boolean outbound, String tenantId)
    {
        String[] window = buildVerifyTimeWindow(bill.getAuditDate() != null ? bill.getAuditDate() : new Date());
        Map<String, Object> body = new LinkedHashMap<>();
        if (StringUtils.isNotEmpty(storageDeptHisId))
        {
            body.put("deptId", parseLongQuiet(storageDeptHisId));
        }
        body.put("startTime", window[0]);
        body.put("endTime", window[1]);
        body.put("instockCode", bill.getBillNo());
        body.put("type", outbound ? "0" : "1");
        String url = MsunHisTenantSupport.joinUrl(buildInterfaceBaseUrl(),
            MsunHisTenantSupport.spdQueryApiPrefix(tenantId) + "/yk-instock");
        JSONObject root = postQuery(url, body);
        JSONArray headers = extractHisDataArray(root);
        if (headers == null)
        {
            return null;
        }
        JSONArray allDetails = new JSONArray();
        for (int i = 0; i < headers.size(); i++)
        {
            JSONObject header = headers.getJSONObject(i);
            if (header == null)
            {
                continue;
            }
            JSONArray details = header.getJSONArray("stockDetailList");
            if (details != null)
            {
                for (int j = 0; j < details.size(); j++)
                {
                    allDetails.add(details.get(j));
                }
            }
        }
        return allDetails;
    }

    private JSONArray fetchBatchStockRows(Long deptId, Long drugId, Long specId, String tenantId)
    {
        String url = MsunHisTenantSupport.joinUrl(buildInterfaceBaseUrl(),
            MsunHisTenantSupport.spdQueryApiPrefix(tenantId)
                + "/drug-batch-stocks?deptId=" + deptId
                + "&drugId=" + drugId
                + "&drugSpecPackingId=" + specId);
        JSONObject root = getQuery(url);
        return extractHisDataArray(root);
    }

    private static boolean matchYkInstockDetail(
            JSONArray ykDetails, StkIoBill bill, StkIoBillEntry entry, String tenantId)
    {
        String spdDetailId = MsunHisConstants.buildSpdDetailId(bill.getId(), entry.getId());
        String memo = MsunHisConstants.buildEntryMemo(tenantId, entry.getId());
        String legacyDetailId = String.valueOf(entry.getId());
        for (int i = 0; i < ykDetails.size(); i++)
        {
            JSONObject detail = ykDetails.getJSONObject(i);
            if (detail == null)
            {
                continue;
            }
            String rowSpdDetailId = detail.getString("spdDetailId");
            if (StringUtils.isNotEmpty(spdDetailId) && spdDetailId.equals(rowSpdDetailId))
            {
                return true;
            }
            if (legacyDetailId.equals(rowSpdDetailId))
            {
                return true;
            }
            if (memo.equals(detail.getString("memo")))
            {
                return true;
            }
        }
        return false;
    }

    private static boolean matchBatchStock(JSONArray batchRows, StkIoBillEntry entry)
    {
        if (batchRows == null || batchRows.isEmpty())
        {
            return false;
        }
        String pharmacyStockId = entry.getHisPharmacyStockId();
        String batchNumber = entry.getBatchNumber();
        for (int i = 0; i < batchRows.size(); i++)
        {
            JSONObject row = batchRows.getJSONObject(i);
            if (row == null)
            {
                continue;
            }
            if (StringUtils.isNotEmpty(pharmacyStockId))
            {
                String psId = row.getString("pharmacyStockId");
                if (pharmacyStockId.equals(psId))
                {
                    return hasPositiveStock(row);
                }
            }
            else if (StringUtils.isNotEmpty(batchNumber))
            {
                String batch = row.getString("ycBatchNo");
                if (batchNumber.equals(batch))
                {
                    return hasPositiveStock(row);
                }
            }
            else if (hasPositiveStock(row))
            {
                return true;
            }
        }
        return false;
    }

    private static boolean hasPositiveStock(JSONObject row)
    {
        Object amt = row.get("stockAmount");
        if (amt == null)
        {
            amt = row.get("quantity");
        }
        if (amt == null)
        {
            return true;
        }
        return new BigDecimal(String.valueOf(amt)).compareTo(BigDecimal.ZERO) > 0;
    }

    private static JSONArray extractHisDataArray(JSONObject scminterfaceRoot)
    {
        if (scminterfaceRoot == null)
        {
            return null;
        }
        JSONObject data = scminterfaceRoot.getJSONObject("data");
        if (data == null)
        {
            return null;
        }
        Object hisBodyObj = data.get("hisBody");
        if (!(hisBodyObj instanceof JSONObject))
        {
            return null;
        }
        JSONObject hisBody = (JSONObject) hisBodyObj;
        if (!Boolean.TRUE.equals(hisBody.getBoolean("success")))
        {
            return null;
        }
        return hisBody.getJSONArray("data");
    }

    private JSONObject getQuery(String url)
    {
        String raw = HttpUtils.sendGet(url);
        return parseScminterfaceResponse(raw);
    }

    private JSONObject postQuery(String url, Map<String, Object> body)
    {
        String raw = HttpUtils.sendPost(url, JSON.toJSONString(body));
        return parseScminterfaceResponse(raw);
    }

    private static JSONObject parseScminterfaceResponse(String raw)
    {
        if (StringUtils.isEmpty(raw))
        {
            return null;
        }
        JSONObject json = JSON.parseObject(raw);
        Integer code = json.getInteger("code");
        if (code == null || code != 200)
        {
            return null;
        }
        return json;
    }

    private static String[] buildVerifyTimeWindow(Date anchor)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(anchor);
        cal.add(Calendar.MINUTE, -VERIFY_WINDOW_MINUTES);
        String start = DateUtils.parseDateToStr("yyyy-MM-dd HH:mm:ss", cal.getTime());
        cal.setTime(anchor);
        cal.add(Calendar.MINUTE, VERIFY_WINDOW_MINUTES);
        String end = DateUtils.parseDateToStr("yyyy-MM-dd HH:mm:ss", cal.getTime());
        return new String[] { start, end };
    }

    private String buildInterfaceBaseUrl()
    {
        String ip = StringUtils.trim(sysConfigService.selectConfigByKey("spd.interface.ip"));
        String port = StringUtils.trim(sysConfigService.selectConfigByKey("spd.interface.port"));
        if (StringUtils.isEmpty(ip))
        {
            ip = DEFAULT_INTERFACE_IP;
        }
        if (StringUtils.isEmpty(port))
        {
            port = DEFAULT_INTERFACE_PORT;
        }
        return "http://" + ip + ":" + port;
    }

    private static Long parseLongQuiet(String val)
    {
        if (StringUtils.isEmpty(val))
        {
            return null;
        }
        try
        {
            return Long.parseLong(val.trim());
        }
        catch (NumberFormatException ex)
        {
            return null;
        }
    }
}

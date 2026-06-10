package com.spd.foundation.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.spd.common.exception.ServiceException;
import com.spd.common.utils.StringUtils;
import com.spd.common.utils.http.HttpUtils;
import com.spd.department.domain.StkDepInventory;
import com.spd.department.mapper.StkDepInventoryMapper;
import com.spd.foundation.service.IMsunHisBillPushService;
import com.spd.foundation.support.MsunHisInterfaceSupport;
import com.spd.foundation.support.MsunHisTenantRegistry;
import com.spd.foundation.support.MsunHisTenantSupport;
import com.spd.warehouse.domain.StkIoBill;
import com.spd.warehouse.domain.StkIoBillEntry;
import com.spd.warehouse.mapper.StkIoBillMapper;
import java.math.BigDecimal;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 众阳 HIS 出库/退库推送：SPD 仅做租户门禁与本地业务校验，组包、调 HIS、回写标志统一委托 scminterface
 * {@code POST /api/spd/msun/hospitals/{hospitalKey}/bill-push/push/{billId}}（{@code MsunSpdBillPushService}）。
 */
@Service
public class MsunHisBillPushServiceImpl implements IMsunHisBillPushService
{
    private static final Logger log = LoggerFactory.getLogger(MsunHisBillPushServiceImpl.class);

    private final MsunHisInterfaceSupport interfaceSupport;
    private final StkIoBillMapper stkIoBillMapper;
    private final StkDepInventoryMapper stkDepInventoryMapper;

    public MsunHisBillPushServiceImpl(
            MsunHisInterfaceSupport interfaceSupport,
            StkIoBillMapper stkIoBillMapper,
            StkDepInventoryMapper stkDepInventoryMapper)
    {
        this.interfaceSupport = interfaceSupport;
        this.stkIoBillMapper = stkIoBillMapper;
        this.stkDepInventoryMapper = stkDepInventoryMapper;
    }

    @Override
    public boolean isMsunIntegratedTenant(String tenantId)
    {
        return MsunHisTenantSupport.isIntegrated(tenantId);
    }

    @Override
    public void assertMsunIntegratedTenant(String tenantId)
    {
        MsunHisTenantSupport.assertIntegrated(tenantId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void pushAfterOutboundAudit(StkIoBill bill)
    {
        invokeInterfaceBillPush(requireBillId(bill), 201);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void pushOutbound(Long billId)
    {
        invokeInterfaceBillPush(billId, 201);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void repushOutbound(Long billId)
    {
        pushOutbound(billId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void pushReturn(Long billId)
    {
        StkIoBill bill = requireAuditedBill(billId, 401);
        validateReturnGateLocal(bill);
        invokeInterfaceBillPush(billId, 401);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void pushAfterReturnAudit(StkIoBill bill)
    {
        Long billId = requireBillId(bill);
        if (bill.getBillType() == null || bill.getBillType() != 401)
        {
            throw new ServiceException("非退库单不允许走退库推送");
        }
        validateReturnGateLocal(bill);
        invokeInterfaceBillPush(billId, 401);
    }

    @Override
    public void validateReturnGate(StkIoBill bill)
    {
        validateReturnGateLocal(bill);
    }

    /**
     * SPD 本地退库门禁（收货确认、本地科室库存）；HIS 2.5.43 与组包由前置机 {@code MsunSpdBillPushService} 处理。
     */
    private void validateReturnGateLocal(StkIoBill bill)
    {
        String tenantId = resolveTenantId(bill);
        assertMsunIntegratedTenant(tenantId);
        List<StkIoBillEntry> entries = bill.getStkIoBillEntryList();
        if (entries == null || entries.isEmpty())
        {
            throw new ServiceException("退库明细不能为空");
        }
        for (StkIoBillEntry entry : entries)
        {
            if (entry == null || (entry.getDelFlag() != null && entry.getDelFlag() == 1))
            {
                continue;
            }
            Long depKey = entry.resolveDepInventoryKeyForDepOps();
            if (depKey == null)
            {
                throw new ServiceException("退库明细未关联科室库存");
            }
            StkDepInventory dep = stkDepInventoryMapper.selectStkDepInventoryById(depKey);
            if (dep == null)
            {
                throw new ServiceException("科室库存不存在 id=" + depKey);
            }
            if (dep.getReceiptConfirmStatus() == null || dep.getReceiptConfirmStatus() != 1)
            {
                throw new ServiceException("科室库存未收货确认，不能退库");
            }
            // 已审核(2)退库单在审核环节已校验数量并扣减 stk_dep_inventory，推送时不应再用「扣减后余额」与明细数量比较（会误报超库存）
            if (bill.getBillStatus() != null && bill.getBillStatus() == 2)
            {
                continue;
            }
            BigDecimal depQty = dep.getQty() != null ? dep.getQty() : BigDecimal.ZERO;
            if (entry.getQty() != null && entry.getQty().compareTo(depQty) > 0)
            {
                throw new ServiceException("退库数量超过SPD科室库存");
            }
        }
    }

    private void invokeInterfaceBillPush(Long billId, int expectedBillType)
    {
        StkIoBill bill = requireAuditedBill(billId, expectedBillType);
        String tenantId = resolveTenantId(bill);
        assertMsunIntegratedTenant(tenantId);
        String url = MsunHisTenantSupport.joinUrl(interfaceSupport.buildInterfaceBaseUrl(),
                MsunHisTenantSupport.spdHospitalApiPrefix(tenantId) + "/bill-push/push/" + billId);
        try
        {
            log.info("众阳HIS单据推送委托前置机 billId={} billType={} url={}", billId, expectedBillType, url);
            String raw = HttpUtils.sendPost(url, "{}");
            JSONObject json = parseInterfaceResponse(raw);
            Integer code = json.getInteger("code");
            if (code == null || code != 200)
            {
                throw new ServiceException(json.getString("msg") != null ? json.getString("msg") : "前置机推送失败");
            }
            assertPushResultsSuccessful(json);
        }
        catch (ServiceException ex)
        {
            throw ex;
        }
        catch (Exception ex)
        {
            throw new ServiceException("调用前置机单据推送失败: " + ex.getMessage());
        }
    }

    private static void assertPushResultsSuccessful(JSONObject json)
    {
        JSONObject data = json.getJSONObject("data");
        if (data == null)
        {
            return;
        }
        Integer failCount = data.getInteger("failCount");
        if (failCount != null && failCount > 0)
        {
            throw new ServiceException(extractFirstFailureMessage(data, json.getString("msg")));
        }
        JSONArray results = data.getJSONArray("results");
        if (results == null || results.isEmpty())
        {
            return;
        }
        for (int i = 0; i < results.size(); i++)
        {
            JSONObject row = results.getJSONObject(i);
            if (row != null && Boolean.FALSE.equals(row.getBoolean("success")))
            {
                String msg = row.getString("message");
                throw new ServiceException(StringUtils.isNotEmpty(msg) ? msg : "HIS单据推送失败");
            }
        }
    }

    private static String extractFirstFailureMessage(JSONObject data, String fallback)
    {
        JSONArray results = data.getJSONArray("results");
        if (results != null)
        {
            for (int i = 0; i < results.size(); i++)
            {
                JSONObject row = results.getJSONObject(i);
                if (row != null && Boolean.FALSE.equals(row.getBoolean("success")))
                {
                    String msg = row.getString("message");
                    if (StringUtils.isNotEmpty(msg))
                    {
                        return msg;
                    }
                }
            }
        }
        String summary = data.getString("message");
        if (StringUtils.isNotEmpty(summary))
        {
            return summary;
        }
        return StringUtils.isNotEmpty(fallback) ? fallback : "HIS单据推送失败";
    }

    private StkIoBill requireAuditedBill(Long billId, int expectedBillType)
    {
        StkIoBill bill = stkIoBillMapper.selectStkIoBillById(billId);
        if (bill == null)
        {
            throw new ServiceException("单据不存在");
        }
        if (bill.getBillStatus() == null || bill.getBillStatus() != 2)
        {
            throw new ServiceException("未审核单据不允许推送HIS");
        }
        if (bill.getBillType() == null || bill.getBillType() != expectedBillType)
        {
            throw new ServiceException("单据类型与推送接口不匹配");
        }
        return bill;
    }

    private static Long requireBillId(StkIoBill bill)
    {
        if (bill == null || bill.getId() == null)
        {
            throw new ServiceException("单据不存在");
        }
        return bill.getId();
    }

    private static JSONObject parseInterfaceResponse(String raw)
    {
        if (StringUtils.isEmpty(raw))
        {
            throw new ServiceException("前置机无响应");
        }
        return JSON.parseObject(raw);
    }

    private static String resolveTenantId(StkIoBill bill)
    {
        return StringUtils.isNotEmpty(bill.getTenantId())
                ? bill.getTenantId().trim()
                : MsunHisTenantRegistry.ZAOQIANG_TCM.getHospitalKey();
    }

}

package com.spd.caigou.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.spd.caigou.domain.PurchaseOrder;
import com.spd.caigou.domain.PurchaseOrderEntry;
import com.spd.caigou.mapper.PurchaseOrderMapper;
import com.spd.caigou.mapper.SpdScmSupplierBindMapper;
import com.spd.caigou.mapper.SpdScmTenantBindMapper;
import com.spd.caigou.service.IPremiseOrderPublishService;
import com.spd.caigou.service.IPurchaseOrderService;
import com.spd.common.core.domain.AjaxResult;
import com.spd.common.exception.ServiceException;
import com.spd.common.utils.SecurityUtils;
import com.spd.common.utils.StringUtils;
import com.spd.common.utils.http.HttpUtils;
import com.spd.foundation.domain.FdFactory;
import com.spd.foundation.domain.FdMaterial;
import com.spd.system.domain.SbCustomer;
import com.spd.system.service.ISbCustomerService;
import com.spd.system.service.ISysConfigService;

/**
 * 推送模式 {@value #CONFIG_PUSH_MODE}：<br>
 * {@code ids} — 仅 POST /api/spd/order/publish（前置机连 SPD 库组装）；<br>
 * {@code payload} — 仅 POST /api/spd/order/publishPayload（SPD 组装 JSON）；<br>
 * {@code auto} — 先试 payload，失败再试 ids（默认，兼顾无库前置机与兼容旧环境）。
 */
@Service
public class PremiseOrderPublishServiceImpl implements IPremiseOrderPublishService
{
    private static final Logger log = LoggerFactory.getLogger(PremiseOrderPublishServiceImpl.class);

    public static final String CONFIG_PUSH_MODE = "spd.order.push.mode";

    private static final String MODE_IDS = "ids";
    private static final String MODE_PAYLOAD = "payload";
    private static final String MODE_AUTO = "auto";

    private static final String DEFAULT_INTERFACE_IP = "127.0.0.1";
    private static final String DEFAULT_INTERFACE_PORT = "8088";

    @Autowired
    private ISysConfigService sysConfigService;

    @Autowired
    private IPurchaseOrderService purchaseOrderService;

    @Autowired
    private ISbCustomerService sbCustomerService;

    @Autowired
    private SpdScmTenantBindMapper spdScmTenantBindMapper;

    @Autowired
    private SpdScmSupplierBindMapper spdScmSupplierBindMapper;

    @Autowired
    private PurchaseOrderMapper purchaseOrderMapper;

    @Override
    public AjaxResult publish(List<Long> ids)
    {
        if (ids == null || ids.isEmpty())
        {
            return AjaxResult.error("订单ID列表不能为空");
        }
        try
        {
            validateAndWritePushCodeSnapshots(ids);
        }
        catch (ServiceException ex)
        {
            return AjaxResult.error(ex.getMessage());
        }
        String mode = StringUtils.trim(sysConfigService.selectConfigByKey(CONFIG_PUSH_MODE));
        if (StringUtils.isEmpty(mode))
        {
            mode = MODE_AUTO;
        }
        mode = mode.toLowerCase();

        String base = buildInterfaceBaseUrl();
        try
        {
            AjaxResult r;
            if (MODE_IDS.equals(mode))
            {
                r = postPublishIds(base, ids);
            }
            else if (MODE_PAYLOAD.equals(mode))
            {
                r = postPublishPayload(base, buildOrderPayloads(ids));
            }
            else if (MODE_AUTO.equals(mode))
            {
                try
                {
                    r = postPublishPayload(base, buildOrderPayloads(ids));
                    if (isSuccess(r))
                    {
                        applyPushOutcomeToOrders(ids, r);
                        return r;
                    }
                    log.warn("payload 推送未成功，回退 ids 模式: {}", r != null ? r.get(AjaxResult.MSG_TAG) : null);
                }
                catch (IllegalArgumentException ex)
                {
                    log.warn("payload 组装失败，回退 ids 模式: {}", ex.getMessage());
                    r = null;
                }
                if (r == null || !isSuccess(r))
                {
                    r = postPublishIds(base, ids);
                }
            }
            else
            {
                return AjaxResult.error("未知的 spd.order.push.mode：" + mode + "，请配置为 ids、payload 或 auto");
            }
            applyPushOutcomeToOrders(ids, r);
            return r;
        }
        catch (Exception e)
        {
            log.error("推送采购订单失败", e);
            String msg = e.getMessage();
            applyPushOutcomeToOrders(ids, AjaxResult.error(msg != null ? msg : "推送失败"));
            return AjaxResult.error("推送采购订单失败: " + msg);
        }
    }

    /**
     * 读取绑定表校验平台医院/供应商编码，并写入订单头快照（推送前实时绑定值）。
     */
    private void validateAndWritePushCodeSnapshots(List<Long> ids)
    {
        String updateBy = SecurityUtils.getUsername();
        for (Long id : ids)
        {
            PurchaseOrder po = purchaseOrderService.selectPurchaseOrderById(id);
            if (po == null)
            {
                throw new ServiceException("订单不存在或无权限访问，id=" + id);
            }
            if (StringUtils.isEmpty(po.getTenantId()))
            {
                throw new ServiceException("订单「" + po.getOrderNo() + "」缺少租户信息，无法校验云平台编码");
            }
            String hospitalCode = spdScmTenantBindMapper.selectHospitalCodeByTenantId(po.getTenantId());
            if (StringUtils.isEmpty(hospitalCode))
            {
                throw new ServiceException("订单「" + po.getOrderNo() + "」无法推送：当前租户未维护「平台医院编码」。请在【系统设置-云平台编码绑定】中维护「租户医院编码」后再推送。");
            }
            if (po.getSupplierId() == null)
            {
                throw new ServiceException("订单「" + po.getOrderNo() + "」缺少供应商，无法校验平台供应商编码");
            }
            String supplierCode = spdScmSupplierBindMapper.selectSupplierCode(po.getTenantId(), String.valueOf(po.getSupplierId()));
            if (StringUtils.isEmpty(supplierCode))
            {
                throw new ServiceException("订单「" + po.getOrderNo() + "」无法推送：该订单供应商未维护「平台供应商编码」。请在【系统设置-云平台编码绑定】的供应商编码中维护后再推送。");
            }
            purchaseOrderMapper.updatePushCodesSnapshot(id, hospitalCode.trim(), supplierCode.trim(), updateBy);
        }
    }

    private void applyPushOutcomeToOrders(List<Long> ids, AjaxResult remote)
    {
        if (ids == null || ids.isEmpty())
        {
            return;
        }
        String updateBy = SecurityUtils.getUsername();
        boolean ok = remote != null && remote.isSuccess();
        String err = remote != null && remote.get(AjaxResult.MSG_TAG) != null ? String.valueOf(remote.get(AjaxResult.MSG_TAG)) : null;
        String status = ok ? "1" : "2";
        String msg = ok ? null : (StringUtils.isNotEmpty(err) ? err : "推送失败");
        for (Long id : ids)
        {
            try
            {
                purchaseOrderMapper.updatePushOutcome(id, status, msg, updateBy);
            }
            catch (Exception ex)
            {
                log.warn("更新订单推送状态失败 id={}", id, ex);
            }
        }
    }

    private static boolean isSuccess(AjaxResult r)
    {
        return r != null && r.isSuccess();
    }

    private AjaxResult postPublishIds(String base, List<Long> ids) throws Exception
    {
        String url = base + "/api/spd/order/publish";
        Map<String, Object> body = new HashMap<>(2);
        body.put("ids", ids);
        String jsonData = JSON.toJSONString(body);
        String result = HttpUtils.sendPost(url, jsonData, "application/json;charset=UTF-8");
        return parseRemoteAjax(result);
    }

    private AjaxResult postPublishPayload(String base, List<Map<String, Object>> orders) throws Exception
    {
        String url = base + "/api/spd/order/publishPayload";
        String jsonData = JSON.toJSONString(orders);
        String result = HttpUtils.sendPost(url, jsonData, "application/json;charset=UTF-8");
        return parseRemoteAjax(result);
    }

    private static AjaxResult parseRemoteAjax(String result)
    {
        if (StringUtils.isEmpty(result))
        {
            return AjaxResult.error("前置机无响应");
        }
        JSONObject jsonResult = JSON.parseObject(result);
        Integer code = jsonResult.getInteger("code");
        String msg = jsonResult.getString("msg");
        if (code != null && code == 200)
        {
            return AjaxResult.success(jsonResult.get("data"));
        }
        return AjaxResult.error(StringUtils.isNotEmpty(msg) ? msg : "发布失败");
    }

    /**
     * 与 scminterface {@code PurchaseOrderDTO} / {@code PurchaseOrderItem} 字段名一致，便于反序列化。
     */
    private List<Map<String, Object>> buildOrderPayloads(List<Long> ids)
    {
        List<Map<String, Object>> list = new ArrayList<>();
        for (Long id : ids)
        {
            PurchaseOrder po = purchaseOrderService.selectPurchaseOrderById(id);
            if (po == null)
            {
                throw new IllegalArgumentException("订单不存在或无权限访问，id=" + id);
            }
            String hospitalName = null;
            if (StringUtils.isNotEmpty(po.getTenantId()))
            {
                SbCustomer c = sbCustomerService.selectSbCustomerById(po.getTenantId());
                if (c != null)
                {
                    hospitalName = c.getCustomerName();
                }
            }
            Map<String, Object> dto = new LinkedHashMap<>();
            dto.put("orderId", po.getId());
            dto.put("orderNo", po.getOrderNo());
            dto.put("planNo", po.getPlanNo());
            dto.put("supplierId", po.getSupplierId());
            dto.put("supplierName", po.getSupplier() != null ? po.getSupplier().getName() : null);
            dto.put("hospitalName", hospitalName);
            dto.put("warehouseId", po.getWarehouseId());
            dto.put("warehouseName", po.getWarehouse() != null ? po.getWarehouse().getName() : null);
            dto.put("departmentId", po.getDepartmentId());
            dto.put("orderDate", po.getOrderDate());
            dto.put("totalAmount", po.getTotalAmount());
            dto.put("orderStatus", po.getOrderStatus());
            dto.put("remark", po.getRemark());
            dto.put("spdTenantId", po.getTenantId());
            dto.put("tenantId", po.getTenantId());
            dto.put("scmHospitalCode", po.getScmHospitalCode());
            dto.put("scmSupplierCode", po.getScmSupplierCode());

            List<PurchaseOrderEntry> entries = po.getPurchaseOrderEntryList();
            if (entries == null || entries.isEmpty())
            {
                throw new IllegalArgumentException("订单无明细，无法推送，orderNo=" + po.getOrderNo());
            }
            List<Map<String, Object>> items = new ArrayList<>();
            for (PurchaseOrderEntry e : entries)
            {
                Map<String, Object> item = new LinkedHashMap<>();
                item.put("entryId", e.getId());
                item.put("materialId", e.getMaterialId());
                item.put("materialCode", e.getMaterialCode());
                item.put("materialName", e.getMaterialName());
                item.put("specification", e.getMaterialSpec());
                item.put("unit", e.getMaterialUnit());
                item.put("quantity", e.getOrderQty());
                item.put("unitPrice", e.getUnitPrice());
                item.put("amount", e.getTotalAmount());
                item.put("remark", e.getRemark());
                FdMaterial mat = e.getMaterial();
                if (mat != null)
                {
                    item.put("registerNo", mat.getRegisterNo());
                    FdFactory fac = mat.getFdFactory();
                    if (fac != null && StringUtils.isNotEmpty(fac.getFactoryName()))
                    {
                        item.put("manufacturerName", fac.getFactoryName());
                    }
                }
                items.add(item);
            }
            dto.put("items", items);
            list.add(dto);
        }
        return list;
    }

    private String buildInterfaceBaseUrl()
    {
        String ip = StringUtils.trim(sysConfigService.selectConfigByKey("spd.interface.ip"));
        String port = StringUtils.trim(sysConfigService.selectConfigByKey("spd.interface.port"));

        if (StringUtils.isEmpty(ip))
        {
            ip = DEFAULT_INTERFACE_IP;
        }
        if (!port.matches("\\d{1,5}"))
        {
            port = DEFAULT_INTERFACE_PORT;
        }
        int portNum = Integer.parseInt(port);
        if (portNum < 1 || portNum > 65535)
        {
            port = DEFAULT_INTERFACE_PORT;
        }
        return "http://" + ip + ":" + port;
    }
}

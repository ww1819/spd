package com.spd.foundation.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.spd.common.exception.ServiceException;
import com.spd.common.utils.DateUtils;
import com.spd.common.utils.StringUtils;
import com.spd.common.utils.http.HttpUtils;
import com.spd.department.domain.StkDepInventory;
import com.spd.department.mapper.StkDepInventoryMapper;
import com.spd.foundation.constants.MsunHisConstants;
import com.spd.foundation.support.MsunHisBatchStockSupport;
import com.spd.foundation.support.MsunHisTenantRegistry;
import com.spd.foundation.support.MsunHisTenantSupport;
import com.spd.foundation.domain.FdDepartment;
import com.spd.foundation.domain.FdMaterial;
import com.spd.foundation.domain.FdSupplier;
import com.spd.foundation.domain.FdWarehouse;
import com.spd.foundation.mapper.FdDepartmentMapper;
import com.spd.foundation.mapper.FdMaterialMapper;
import com.spd.foundation.mapper.FdSupplierMapper;
import com.spd.foundation.mapper.FdWarehouseMapper;
import com.spd.foundation.service.IMsunHisBillPushService;
import com.spd.system.service.ISysConfigService;
import com.spd.warehouse.domain.StkIoBill;
import com.spd.warehouse.domain.StkIoBillEntry;
import com.spd.warehouse.mapper.StkIoBillMapper;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 枣强众阳 HIS 出库 2.5.41 / 退库 2.5.42 推送编排。
 * <p>{@code spdDetailId} 拼接/解析规则见 {@link com.spd.foundation.constants.MsunHisConstants}；
 * 推送成功后由 {@link MsunHisPushVerifyService} 即时拉取 2.5.102（及出库 2.5.43 批次库存）并标注异常。
 */
@Service
public class MsunHisBillPushServiceImpl implements IMsunHisBillPushService
{
    private static final Logger log = LoggerFactory.getLogger(MsunHisBillPushServiceImpl.class);

    private static final String DEFAULT_INTERFACE_IP = "127.0.0.1";
    private static final String DEFAULT_INTERFACE_PORT = "8088";

    private final ISysConfigService sysConfigService;
    private final StkIoBillMapper stkIoBillMapper;
    private final FdWarehouseMapper fdWarehouseMapper;
    private final FdDepartmentMapper fdDepartmentMapper;
    private final FdSupplierMapper fdSupplierMapper;
    private final FdMaterialMapper fdMaterialMapper;
    private final StkDepInventoryMapper stkDepInventoryMapper;
    private final MsunHisPushVerifyService msunHisPushVerifyService;

    public MsunHisBillPushServiceImpl(
            ISysConfigService sysConfigService,
            StkIoBillMapper stkIoBillMapper,
            FdWarehouseMapper fdWarehouseMapper,
            FdDepartmentMapper fdDepartmentMapper,
            FdSupplierMapper fdSupplierMapper,
            FdMaterialMapper fdMaterialMapper,
            StkDepInventoryMapper stkDepInventoryMapper,
            MsunHisPushVerifyService msunHisPushVerifyService)
    {
        this.sysConfigService = sysConfigService;
        this.stkIoBillMapper = stkIoBillMapper;
        this.fdWarehouseMapper = fdWarehouseMapper;
        this.fdDepartmentMapper = fdDepartmentMapper;
        this.fdSupplierMapper = fdSupplierMapper;
        this.fdMaterialMapper = fdMaterialMapper;
        this.stkDepInventoryMapper = stkDepInventoryMapper;
        this.msunHisPushVerifyService = msunHisPushVerifyService;
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
        String tenantId = resolveTenantId(bill);
        assertMsunIntegratedTenant(tenantId);
        List<StkIoBillEntry> toPush = filterEntriesForPush(bill.getStkIoBillEntryList(), false);
        if (toPush.isEmpty())
        {
            return;
        }
        updateBillPushStatus(bill.getId(), MsunHisConstants.PUSHING, null, null);
        PushContext ctx = buildOutboundContext(bill, tenantId);
        Map<String, Object> body = buildOutboundBody(bill, ctx, toPush, tenantId);
        JSONObject response = postInterface(tenantId, "/push/drug-stocks-new", body);
        applyOutboundResponse(bill, toPush, tenantId, response);
        String traceId = extractTraceId(response);
        updateBillPushStatus(bill.getId(), MsunHisConstants.PUSH_SUCCESS, null, traceId);
        msunHisPushVerifyService.verifyAfterPush(
            bill, toPush, ctx.storageDeptHisId, ctx.pharmacyDeptHisId, tenantId, true);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void pushOutbound(Long billId)
    {
        StkIoBill bill = stkIoBillMapper.selectStkIoBillById(billId);
        if (bill == null)
        {
            throw new ServiceException("出库单不存在");
        }
        if (bill.getBillStatus() == null || bill.getBillStatus() != 2)
        {
            throw new ServiceException("未审核出库单不允许推送HIS");
        }
        pushAfterOutboundAudit(bill);
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
        StkIoBill bill = stkIoBillMapper.selectStkIoBillById(billId);
        if (bill == null)
        {
            throw new ServiceException("退库单不存在");
        }
        if (bill.getBillType() == null || bill.getBillType() != 401)
        {
            throw new ServiceException("非退库单不允许走退库推送");
        }
        if (bill.getBillStatus() == null || bill.getBillStatus() != 2)
        {
            throw new ServiceException("未审核退库单不允许推送HIS");
        }
        validateReturnGate(bill);
        pushAfterReturnAudit(bill);
    }

    @Override
    public void validateReturnGate(StkIoBill bill)
    {
        String tenantId = resolveTenantId(bill);
        assertMsunIntegratedTenant(tenantId);
        PushContext ctx = buildReturnContext(bill, tenantId);
        List<StkIoBillEntry> entries = bill.getStkIoBillEntryList();
        if (entries == null || entries.isEmpty())
        {
            throw new ServiceException("退库明细不能为空");
        }
        for (StkIoBillEntry entry : entries)
        {
            if (entry == null || entry.getDelFlag() != null && entry.getDelFlag() == 1)
            {
                continue;
            }
            validateReturnEntry(bill, entry, ctx, tenantId);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void pushAfterReturnAudit(StkIoBill bill)
    {
        String tenantId = resolveTenantId(bill);
        assertMsunIntegratedTenant(tenantId);
        List<StkIoBillEntry> toPush = filterEntriesForPush(bill.getStkIoBillEntryList(), true);
        if (toPush.isEmpty())
        {
            return;
        }
        updateBillPushStatus(bill.getId(), MsunHisConstants.PUSHING, null, null);
        PushContext ctx = buildReturnContext(bill, tenantId);
        Map<String, Object> body = buildReturnBody(bill, ctx, toPush, tenantId);
        JSONObject response = postInterface(tenantId, "/push/drug-stocks-return", body);
        assertHisSuccess(response);
        for (StkIoBillEntry entry : toPush)
        {
            stkIoBillMapper.updateEntryHisPushStatus(entry.getId(), MsunHisConstants.PUSH_SUCCESS, null);
        }
        String traceId = extractTraceId(response);
        updateBillPushStatus(bill.getId(), MsunHisConstants.PUSH_SUCCESS, null, traceId);
        msunHisPushVerifyService.verifyAfterPush(
            bill, toPush, ctx.storageDeptHisId, ctx.pharmacyDeptHisId, tenantId, false);
    }

    private void validateReturnEntry(StkIoBill bill, StkIoBillEntry entry, PushContext ctx, String tenantId)
    {
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
        BigDecimal depQty = dep.getQty() != null ? dep.getQty() : BigDecimal.ZERO;
        if (entry.getQty() != null && entry.getQty().compareTo(depQty) > 0)
        {
            throw new ServiceException("退库数量超过SPD科室库存");
        }
        String returnStockId = resolveReturnPushStockIdLocal(dep, entry);
        if (StringUtils.isEmpty(returnStockId))
        {
            HisReturnStockIds ids = resolveReturnStockIdsFromHis(ctx.pharmacyDeptHisId, entry, tenantId);
            returnStockId = ids.returnStockId;
            applyReturnStockIds(entry, dep, ids);
        }
        else
        {
            entry.setHisStockQueryId(returnStockId);
        }
        BigDecimal hisQty = queryHisStockAmount(ctx.pharmacyDeptHisId, entry, returnStockId, tenantId);
        if (entry.getQty() != null && hisQty != null && entry.getQty().compareTo(hisQty) > 0)
        {
            throw new ServiceException("退库数量超过HIS可退量，HIS库存=" + hisQty);
        }
    }

    private static String resolveReturnPushStockIdLocal(StkDepInventory dep, StkIoBillEntry entry)
    {
        if (dep != null && StringUtils.isNotEmpty(dep.getHisStockQueryId()))
        {
            return dep.getHisStockQueryId();
        }
        return StringUtils.isNotEmpty(entry.getHisStockQueryId()) ? entry.getHisStockQueryId() : null;
    }

    private HisReturnStockIds resolveReturnStockIdsFromHis(String deptHisId, StkIoBillEntry entry, String tenantId)
    {
        JSONArray data = fetchDrugBatchStocks(deptHisId, entry, tenantId);
        if (data == null || data.isEmpty())
        {
            throw new ServiceException("HIS 无可用批次库存，无法退库推送");
        }
        String batchNumber = entry.getBatchNumber();
        for (int i = 0; i < data.size(); i++)
        {
            JSONObject row = data.getJSONObject(i);
            if (row == null)
            {
                continue;
            }
            if (StringUtils.isNotEmpty(batchNumber))
            {
                String batch = row.getString("ycBatchNo");
                if (batch != null && !batchNumber.equals(batch))
                {
                    continue;
                }
            }
            String returnStockId = MsunHisBatchStockSupport.resolveReturnPushStockId(row);
            if (StringUtils.isNotEmpty(returnStockId))
            {
                HisReturnStockIds ids = new HisReturnStockIds();
                ids.returnStockId = returnStockId;
                ids.pharmacyStockId = row.getString("pharmacyStockId");
                ids.storageStockId = row.getString("storageStockId");
                return ids;
            }
        }
        throw new ServiceException("HIS 批次库存未匹配到 stockQueryId，批号="
            + (batchNumber != null ? batchNumber : ""));
    }

    private void applyReturnStockIds(StkIoBillEntry entry, StkDepInventory dep, HisReturnStockIds ids)
    {
        entry.setHisStockQueryId(ids.returnStockId);
        if (StringUtils.isNotEmpty(ids.pharmacyStockId))
        {
            entry.setHisPharmacyStockId(ids.pharmacyStockId);
        }
        if (StringUtils.isNotEmpty(ids.storageStockId))
        {
            entry.setHisStorageStockId(ids.storageStockId);
        }
        if (dep != null)
        {
            dep.setHisStockQueryId(ids.returnStockId);
            if (StringUtils.isNotEmpty(ids.pharmacyStockId))
            {
                dep.setHisPharmacyStockId(ids.pharmacyStockId);
            }
            if (StringUtils.isNotEmpty(ids.storageStockId))
            {
                dep.setHisStorageStockId(ids.storageStockId);
            }
            stkDepInventoryMapper.updateStkDepInventory(dep);
        }
        stkIoBillMapper.updateEntryHisStockIds(entry.getId(), ids.pharmacyStockId,
            ids.storageStockId, ids.returnStockId);
    }

    private JSONArray fetchDrugBatchStocks(String deptHisId, StkIoBillEntry entry, String tenantId)
    {
        FdMaterial material = fdMaterialMapper.selectFdMaterialById(entry.getMaterialId());
        if (material == null || StringUtils.isEmpty(material.getHisId()))
        {
            throw new ServiceException("耗材未维护 HIS drugId");
        }
        String specId = material.getHisSpecPackingId();
        if (StringUtils.isEmpty(specId))
        {
            throw new ServiceException("耗材未维护 HIS drugSpecPackingId");
        }
        String url = MsunHisTenantSupport.joinUrl(buildInterfaceBaseUrl(),
            MsunHisTenantSupport.spdHospitalApiPrefix(tenantId)
                + "/query/drug-batch-stocks?deptId=" + deptHisId
                + "&drugId=" + material.getHisId()
                + "&drugSpecPackingId=" + specId);
        String raw = HttpUtils.sendGet(url);
        JSONObject root = parseInterfaceResponse(raw);
        JSONObject wrapped = root.getJSONObject("data");
        if (wrapped == null)
        {
            throw new ServiceException("2.5.43 无响应数据");
        }
        Object hisBodyObj = wrapped.get("hisBody");
        if (!(hisBodyObj instanceof JSONObject))
        {
            throw new ServiceException("2.5.43 响应格式异常");
        }
        JSONObject hisBody = (JSONObject) hisBodyObj;
        if (!Boolean.TRUE.equals(hisBody.getBoolean("success")))
        {
            throw new ServiceException("2.5.43 查询失败: " + hisBody.getString("message"));
        }
        return hisBody.getJSONArray("data");
    }

    private BigDecimal queryHisStockAmount(
            String deptHisId, StkIoBillEntry entry, String returnStockId, String tenantId)
    {
        JSONArray data = fetchDrugBatchStocks(deptHisId, entry, tenantId);
        if (data == null || data.isEmpty())
        {
            return BigDecimal.ZERO;
        }
        BigDecimal total = BigDecimal.ZERO;
        for (int i = 0; i < data.size(); i++)
        {
            JSONObject row = data.getJSONObject(i);
            if (row == null)
            {
                continue;
            }
            if (StringUtils.isNotEmpty(returnStockId)
                    && !MsunHisBatchStockSupport.matchesReturnStockFilter(row, returnStockId))
            {
                continue;
            }
            if (StringUtils.isEmpty(returnStockId) && StringUtils.isNotEmpty(entry.getBatchNumber()))
            {
                String batch = row.getString("ycBatchNo");
                if (batch != null && !entry.getBatchNumber().equals(batch))
                {
                    continue;
                }
            }
            BigDecimal amt = MsunHisBatchStockSupport.resolveStockAmount(row);
            if (amt != null)
            {
                total = total.add(amt);
            }
        }
        return total;
    }

    private List<StkIoBillEntry> filterEntriesForPush(List<StkIoBillEntry> entries, boolean forReturn)
    {
        List<StkIoBillEntry> list = new ArrayList<>();
        if (entries == null)
        {
            return list;
        }
        for (StkIoBillEntry e : entries)
        {
            if (e == null || (e.getDelFlag() != null && e.getDelFlag() == 1))
            {
                continue;
            }
            String st = e.getHisPushStatus();
            if (MsunHisConstants.PUSH_SUCCESS.equals(st))
            {
                continue;
            }
            list.add(e);
        }
        return list;
    }

    private Map<String, Object> buildOutboundBody(
            StkIoBill bill, PushContext ctx, List<StkIoBillEntry> entries, String tenantId)
    {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("supplierId", parseLongRequired(ctx.supplierHisId, "供应商HIS对照"));
        body.put("storageDeptId", parseLongRequired(ctx.storageDeptHisId, "仓库HIS药库科室"));
        body.put("pharmacyDeptId", parseLongRequired(ctx.pharmacyDeptHisId, "科室HIS对照"));
        body.put("invoiceCode", bill.getBillNo());
        body.put("inStockStatus", MsunHisConstants.IN_STOCK_STATUS_PHARMACY);
        body.put("spdMainId", bill.getBillNo());
        body.put("saveCorrelationFlag", MsunHisConstants.SAVE_CORRELATION_FLAG);

        List<Map<String, Object>> details = new ArrayList<>();
        for (StkIoBillEntry entry : entries)
        {
            FdMaterial material = fdMaterialMapper.selectFdMaterialById(entry.getMaterialId());
            if (material == null)
            {
                throw new ServiceException("耗材不存在 id=" + entry.getMaterialId());
            }
            String memo = MsunHisConstants.buildEntryMemo(tenantId, entry.getId());
            String spdDetailId = MsunHisConstants.buildSpdDetailId(bill.getId(), entry.getId());
            Map<String, Object> line = new LinkedHashMap<>();
            line.put("drugId", parseLongRequired(material.getHisId(), "耗材HIS drugId"));
            line.put("drugSpecPackingId", parseLongRequired(material.getHisSpecPackingId(), "耗材HIS规格"));
            line.put("quantity", entry.getQty());
            line.put("buyPrice", entry.getUnitPrice());
            line.put("retailPrice", entry.getUnitPrice());
            line.put("invoiceCode", bill.getBillNo());
            line.put("produceDate", formatHisDateTime(entry.getBeginTime()));
            line.put("effectiveDate", formatHisEffectiveDate(entry.getEndTime()));
            line.put("ycBatchNo", entry.getBatchNumber());
            line.put("spdDetailId", spdDetailId);
            line.put("memo", memo);
            details.add(line);

            stkIoBillMapper.updateEntryHisPrepare(entry.getId(), memo, spdDetailId,
                material.getHisId(), material.getHisSpecPackingId(), MsunHisConstants.PUSHING);
        }
        body.put("inStockDetailDTOList", details);
        body.put("_spdLogMeta", logMeta(bill));
        return body;
    }

    private Map<String, Object> buildReturnBody(
            StkIoBill bill, PushContext ctx, List<StkIoBillEntry> entries, String tenantId)
    {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("storageDeptId", parseLongRequired(ctx.storageDeptHisId, "仓库HIS药库科室"));
        body.put("pharmacyDeptId", parseLongRequired(ctx.pharmacyDeptHisId, "科室HIS对照"));
        body.put("isReturnToSupplier", MsunHisConstants.RETURN_TO_SUPPLIER_YES);

        List<Map<String, Object>> details = new ArrayList<>();
        for (StkIoBillEntry entry : entries)
        {
            Long depKey = entry.resolveDepInventoryKeyForDepOps();
            StkDepInventory dep = depKey != null ? stkDepInventoryMapper.selectStkDepInventoryById(depKey) : null;
            String returnStockId = resolveReturnPushStockIdLocal(dep, entry);
            if (StringUtils.isEmpty(returnStockId))
            {
                HisReturnStockIds ids = resolveReturnStockIdsFromHis(ctx.pharmacyDeptHisId, entry, tenantId);
                returnStockId = ids.returnStockId;
                applyReturnStockIds(entry, dep, ids);
            }
            String memo = StringUtils.isNotEmpty(entry.getHisMemo())
                ? entry.getHisMemo() : MsunHisConstants.buildEntryMemo(tenantId, entry.getId());
            String spdDetailId = StringUtils.isNotEmpty(entry.getHisSpdDetailId())
                ? entry.getHisSpdDetailId() : MsunHisConstants.buildSpdDetailId(bill.getId(), entry.getId());
            Map<String, Object> line = new LinkedHashMap<>();
            line.put("pharmacyStockId", parseLongRequired(returnStockId, "pharmacyStockId"));
            line.put("quantity", entry.getQty());
            line.put("memo", memo);
            details.add(line);
            String drugId = entry.getHisDrugId();
            String specId = entry.getHisDrugSpecPackingId();
            if (StringUtils.isEmpty(drugId) || StringUtils.isEmpty(specId))
            {
                FdMaterial material = fdMaterialMapper.selectFdMaterialById(entry.getMaterialId());
                if (material != null)
                {
                    if (StringUtils.isEmpty(drugId))
                    {
                        drugId = material.getHisId();
                    }
                    if (StringUtils.isEmpty(specId))
                    {
                        specId = material.getHisSpecPackingId();
                    }
                }
            }
            stkIoBillMapper.updateEntryHisPrepare(entry.getId(), memo, spdDetailId,
                drugId, specId, MsunHisConstants.PUSHING);
        }
        body.put("outStockDetailDTOList", details);
        body.put("_spdLogMeta", logMeta(bill));
        return body;
    }

    private void applyOutboundResponse(
            StkIoBill bill, List<StkIoBillEntry> entries, String tenantId, JSONObject response)
    {
        assertHisSuccess(response);
        JSONObject hisBody = resolveHisBody(response);
        if (hisBody == null)
        {
            throw new ServiceException("HIS推送响应格式异常");
        }
        JSONArray data = hisBody.getJSONArray("data");
        if (data == null)
        {
            throw new ServiceException("HIS未返回入库明细数据，请确认 inStockStatus 与 pharmacyDeptId");
        }
        Map<String, JSONObject> byMemo = new HashMap<>();
        Map<String, JSONObject> bySpdDetail = new HashMap<>();
        for (int i = 0; i < data.size(); i++)
        {
            JSONObject row = data.getJSONObject(i);
            if (row == null)
            {
                continue;
            }
            if (StringUtils.isNotEmpty(row.getString("memo")))
            {
                byMemo.put(row.getString("memo"), row);
            }
            if (StringUtils.isNotEmpty(row.getString("spdDetailId")))
            {
                bySpdDetail.put(row.getString("spdDetailId"), row);
            }
        }
        for (StkIoBillEntry entry : entries)
        {
            String memo = MsunHisConstants.buildEntryMemo(tenantId, entry.getId());
            String spdDetailId = MsunHisConstants.buildSpdDetailId(bill.getId(), entry.getId());
            JSONObject row = byMemo.get(memo);
            if (row == null && StringUtils.isNotEmpty(spdDetailId))
            {
                row = bySpdDetail.get(spdDetailId);
            }
            if (row == null)
            {
                row = bySpdDetail.get(String.valueOf(entry.getId()));
            }
            if (row == null)
            {
                throw new ServiceException("HIS回参未匹配明细 entryId=" + entry.getId());
            }
            String pharmacyStockId = firstNonEmpty(row.getString("pharmacyStockId"), row.getString("storageStockId"));
            String storageStockId = row.getString("storageStockId");
            String stockQueryId = MsunHisBatchStockSupport.resolveStockQueryId(row);
            stkIoBillMapper.updateEntryHisPushResult(entry.getId(), pharmacyStockId, storageStockId, stockQueryId,
                MsunHisConstants.PUSH_SUCCESS, null);
            Long depId = entry.getDepInventoryId();
            if (depId != null && StringUtils.isNotEmpty(pharmacyStockId))
            {
                stkIoBillMapper.updateDepInventoryHisStock(depId, pharmacyStockId, storageStockId, stockQueryId);
            }
        }
    }

    private PushContext buildOutboundContext(StkIoBill bill, String tenantId)
    {
        PushContext ctx = new PushContext();
        ctx.storageDeptHisId = resolveWarehouseHisId(bill.getWarehouseId());
        ctx.pharmacyDeptHisId = resolveDepartmentHisId(bill.getDepartmentId());
        ctx.supplierHisId = resolveSupplierHisId(bill, bill.getStkIoBillEntryList());
        return ctx;
    }

    private PushContext buildReturnContext(StkIoBill bill, String tenantId)
    {
        PushContext ctx = new PushContext();
        ctx.storageDeptHisId = resolveWarehouseHisId(bill.getWarehouseId());
        ctx.pharmacyDeptHisId = resolveDepartmentHisId(bill.getDepartmentId());
        return ctx;
    }

    private String resolveWarehouseHisId(Long warehouseId)
    {
        if (warehouseId == null)
        {
            throw new ServiceException("仓库不能为空");
        }
        FdWarehouse wh = fdWarehouseMapper.selectFdWarehouseById(String.valueOf(warehouseId));
        if (wh == null || StringUtils.isEmpty(wh.getHisId()))
        {
            throw new ServiceException("仓库未维护 HIS 药库科室对照(his_id)");
        }
        return wh.getHisId();
    }

    private String resolveDepartmentHisId(Long departmentId)
    {
        if (departmentId == null)
        {
            throw new ServiceException("科室不能为空");
        }
        FdDepartment dept = fdDepartmentMapper.selectFdDepartmentById(String.valueOf(departmentId));
        if (dept == null || StringUtils.isEmpty(dept.getHisId()))
        {
            throw new ServiceException("科室未维护 HIS 对照(his_id)");
        }
        return dept.getHisId();
    }

    private String resolveSupplierHisId(StkIoBill bill, List<StkIoBillEntry> entries)
    {
        Long supId = bill.getSupplerId();
        if (supId == null && entries != null)
        {
            for (StkIoBillEntry e : entries)
            {
                if (e != null && StringUtils.isNotEmpty(e.getSupplerId()))
                {
                    supId = Long.valueOf(e.getSupplerId().trim());
                    break;
                }
            }
        }
        if (supId == null)
        {
            throw new ServiceException("出库单未指定供应商，无法推送HIS");
        }
        FdSupplier supplier = fdSupplierMapper.selectFdSupplierById(supId);
        if (supplier == null || StringUtils.isEmpty(supplier.getHisId()))
        {
            throw new ServiceException("供应商未维护 HIS 对照(his_id)");
        }
        return supplier.getHisId();
    }

    private JSONObject postInterface(String tenantId, String pathSuffix, Map<String, Object> body)
    {
        String url = MsunHisTenantSupport.joinUrl(buildInterfaceBaseUrl(),
            MsunHisTenantSupport.spdHospitalApiPrefix(tenantId) + pathSuffix);
        try
        {
            log.info("众阳HIS推送请求 url={}", url);
            String raw = HttpUtils.sendPost(url, JSON.toJSONString(body));
            JSONObject json = parseInterfaceResponse(raw);
            Integer code = json.getInteger("code");
            if (code == null || code != 200)
            {
                throw new ServiceException(json.getString("msg") != null ? json.getString("msg") : "前置机推送失败");
            }
            return json;
        }
        catch (ServiceException e)
        {
            throw e;
        }
        catch (Exception ex)
        {
            throw new ServiceException("调用前置机失败: " + ex.getMessage());
        }
    }

    private static JSONObject parseInterfaceResponse(String raw)
    {
        if (StringUtils.isEmpty(raw))
        {
            throw new ServiceException("前置机无响应");
        }
        return JSON.parseObject(raw);
    }

    private static void assertHisSuccess(JSONObject json)
    {
        JSONObject hisBody = resolveHisBody(json);
        if (hisBody == null)
        {
            return;
        }
        if (!Boolean.TRUE.equals(hisBody.getBoolean("success")))
        {
            throw new ServiceException(hisBody.getString("message") != null
                ? hisBody.getString("message") : "HIS推送失败");
        }
    }

    private static JSONObject resolveHisBody(JSONObject response)
    {
        if (response == null)
        {
            return null;
        }
        JSONObject hisBody = response.getJSONObject("hisBody");
        if (hisBody != null)
        {
            return hisBody;
        }
        JSONObject data = response.getJSONObject("data");
        if (data == null)
        {
            return null;
        }
        hisBody = data.getJSONObject("hisBody");
        if (hisBody != null)
        {
            return hisBody;
        }
        JSONObject inner = data.getJSONObject("data");
        if (inner != null)
        {
            return inner.getJSONObject("hisBody");
        }
        return null;
    }

    private void updateBillPushStatus(Long billId, String status, String msg, String traceId)
    {
        stkIoBillMapper.updateBillHisPushStatus(billId, status, msg, traceId, new Date());
    }

    private static Map<String, Object> logMeta(StkIoBill bill)
    {
        Map<String, Object> meta = new HashMap<>(4);
        meta.put("spdBillId", bill.getId());
        meta.put("billNo", bill.getBillNo());
        meta.put("billType", bill.getBillType());
        return meta;
    }

    private static String resolveTenantId(StkIoBill bill)
    {
        return StringUtils.isNotEmpty(bill.getTenantId())
            ? bill.getTenantId().trim()
            : MsunHisTenantRegistry.ZAOQIANG_TCM.getHospitalKey();
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

    private static long parseLongRequired(String val, String label)
    {
        if (StringUtils.isEmpty(val))
        {
            throw new ServiceException(label + " 不能为空");
        }
        try
        {
            return Long.parseLong(val.trim());
        }
        catch (NumberFormatException e)
        {
            throw new ServiceException(label + " 格式非法: " + val);
        }
    }

    private static String formatHisDateTime(Date date)
    {
        if (date == null)
        {
            return DateUtils.getTime();
        }
        return DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, date);
    }

    private static String formatHisEffectiveDate(Date endTime)
    {
        if (endTime == null)
        {
            throw new ServiceException("出库明细有效期不能为空");
        }
        return DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, endTime);
    }

    private static String extractTraceId(JSONObject json)
    {
        JSONObject hisBody = resolveHisBody(json);
        return hisBody != null ? hisBody.getString("traceId") : null;
    }

    private static String firstNonEmpty(String a, String b)
    {
        return StringUtils.isNotEmpty(a) ? a : b;
    }

    private static final class PushContext
    {
        private String storageDeptHisId;
        private String pharmacyDeptHisId;
        private String supplierHisId;
    }

    private static final class HisReturnStockIds
    {
        private String returnStockId;
        private String pharmacyStockId;
        private String storageStockId;
    }
}

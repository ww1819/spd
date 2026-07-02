package com.spd.his.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.spd.common.exception.ServiceException;
import com.spd.common.utils.DateUtils;
import com.spd.common.utils.HisMatchTextUtils;
import com.spd.common.utils.SecurityUtils;
import com.spd.common.utils.uuid.UUID7;
import com.spd.department.domain.DeptBatchConsumeReverseReq;
import com.spd.department.service.IDeptBatchConsumeService;
import com.spd.foundation.service.ISbTenantSettingService;
import com.spd.gz.domain.GzDepInventory;
import com.spd.gz.domain.GzTraceability;
import com.spd.gz.domain.GzTraceabilityEntry;
import com.spd.gz.mapper.GzDepInventoryMapper;
import com.spd.gz.service.IGzTraceabilityService;
import com.spd.hc.service.IHcBarcodeLifecycleService;
import com.spd.his.constants.HisBillingTenantConstants;
import com.spd.his.domain.HisBillingRefundOrder;
import com.spd.his.domain.HisBillingRefundOrderLine;
import com.spd.his.domain.HisInpatientChargeMirror;
import com.spd.his.domain.HisMirrorConsumeLink;
import com.spd.his.domain.HisOutpatientChargeMirror;
import com.spd.his.domain.dto.HisBillingRefundHighBody;
import com.spd.his.domain.dto.HisBillingRefundHighLineBody;
import com.spd.his.domain.dto.HisBillingRefundLowBody;
import com.spd.his.mapper.HisBillingRefundOrderLineMapper;
import com.spd.his.mapper.HisBillingRefundOrderMapper;
import com.spd.his.mapper.HisInpatientChargeMirrorMapper;
import com.spd.his.mapper.HisMirrorConsumeLinkMapper;
import com.spd.his.mapper.HisOutpatientChargeMirrorMapper;
import com.spd.his.mapper.HisPatientChargeMirrorUnifiedMapper;
import com.spd.his.service.IHisBillingRefundService;
import com.spd.his.service.refund.LvRefundConsumeLinkOrderStrategy;
import com.spd.his.support.HisAutoWriteOffOperatorSupport;

@Service
public class HisBillingRefundServiceImpl implements IHisBillingRefundService
{
    private static final Logger log = LoggerFactory.getLogger(HisBillingRefundServiceImpl.class);

    private static final String KIND_IN = "INPATIENT";
    private static final String STATUS_DONE = "DONE";
    private static final String STATUS_FAILED = "FAILED";
    private static final String STATUS_PENDING = "PENDING";
    private static final String MIRROR_STATUS_REFUNDED = "REFUNDED";
    private static final String MIRROR_PROC_TYPE_REFUND = "REFUND";

    @Autowired
    private HisInpatientChargeMirrorMapper hisInpatientChargeMirrorMapper;
    @Autowired
    private HisOutpatientChargeMirrorMapper hisOutpatientChargeMirrorMapper;
    @Autowired
    private HisMirrorConsumeLinkMapper hisMirrorConsumeLinkMapper;
    @Autowired
    private HisBillingRefundOrderMapper hisBillingRefundOrderMapper;
    @Autowired
    private HisBillingRefundOrderLineMapper hisBillingRefundOrderLineMapper;
    @Autowired
    private IDeptBatchConsumeService deptBatchConsumeService;
    @Autowired
    private LvRefundConsumeLinkOrderStrategy lvRefundConsumeLinkOrderStrategy;
    @Autowired
    private HisPatientChargeMirrorUnifiedMapper hisPatientChargeMirrorUnifiedMapper;
    @Autowired
    private IGzTraceabilityService gzTraceabilityService;
    @Autowired
    private GzDepInventoryMapper gzDepInventoryMapper;
    @Autowired
    private IHcBarcodeLifecycleService hcBarcodeLifecycleService;
    @Autowired
    private ISbTenantSettingService sbTenantSettingService;

    /** 避免同类自调用导致 @Transactional 失效 */
    @Lazy
    @Autowired
    private IHisBillingRefundService billingRefundService;

    private void assertHengsuiTenant()
    {
        String tid = SecurityUtils.getCustomerId();
        if (!HisBillingTenantConstants.TENANT_HENGSHUI_THIRD.equals(tid))
        {
            throw new ServiceException("该功能仅对衡水三院租户开放");
        }
    }

    private String resolveVisitKind(String raw)
    {
        if (StringUtils.isBlank(raw))
        {
            throw new ServiceException("请指定 visitKind（INPATIENT/OUTPATIENT）");
        }
        String v = raw.trim().toUpperCase();
        if (!KIND_IN.equals(v) && !"OUTPATIENT".equals(v))
        {
            throw new ServiceException("visitKind 仅支持 INPATIENT 或 OUTPATIENT");
        }
        return v;
    }

    private String resolveOriginMirrorRowId(String tenantId, String visitKind, String originChargeDetailId)
    {
        if (KIND_IN.equals(visitKind))
        {
            String id = hisInpatientChargeMirrorMapper.selectMirrorIdByHisChargeId(tenantId, originChargeDetailId);
            if (StringUtils.isNotBlank(id))
            {
                return id;
            }
        }
        else
        {
            String id = hisOutpatientChargeMirrorMapper.selectMirrorIdByHisChargeId(tenantId, originChargeDetailId);
            if (StringUtils.isNotBlank(id))
            {
                return id;
            }
        }
        throw new ServiceException("未找到原计费镜像行，请确认收费明细ID与抓取镜像一致");
    }

    private static BigDecimal nz(BigDecimal v)
    {
        return v == null ? BigDecimal.ZERO : v;
    }

    private BigDecimal refundableOnLink(HisMirrorConsumeLink lk)
    {
        if (lk.getRefundableRemainingQty() != null)
        {
            return lk.getRefundableRemainingQty().max(BigDecimal.ZERO);
        }
        return nz(lk.getAllocQty()).subtract(nz(lk.getReturnedQty())).max(BigDecimal.ZERO);
    }

    private Map<Long, BigDecimal> loadCanReverseByEntry(Long consumeId)
    {
        List<Map<String, Object>> rows = deptBatchConsumeService.selectReverseableEntryList(consumeId);
        Map<Long, BigDecimal> m = new HashMap<>();
        if (rows == null)
        {
            return m;
        }
        for (Map<String, Object> row : rows)
        {
            Long eid = toLong(row.get("srcConsumeEntryId"));
            BigDecimal can = toBigDecimal(row.get("canReverseQty"));
            if (eid != null && can != null)
            {
                m.put(eid, can);
            }
        }
        return m;
    }

    private Long toLong(Object v)
    {
        if (v == null)
        {
            return null;
        }
        if (v instanceof Number)
        {
            return ((Number) v).longValue();
        }
        try
        {
            return Long.parseLong(String.valueOf(v));
        }
        catch (Exception e)
        {
            return null;
        }
    }

    private BigDecimal toBigDecimal(Object v)
    {
        if (v == null)
        {
            return null;
        }
        if (v instanceof BigDecimal)
        {
            return (BigDecimal) v;
        }
        try
        {
            return new BigDecimal(String.valueOf(v));
        }
        catch (Exception e)
        {
            return null;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public HisBillingRefundOrder processLowValueRefund(HisBillingRefundLowBody body)
    {
        assertHengsuiTenant();
        if (body == null || StringUtils.isBlank(body.getOriginChargeDetailId()) || body.getRefundQty() == null)
        {
            throw new ServiceException("请提供 originChargeDetailId 与 refundQty");
        }
        if (body.getRefundQty().compareTo(BigDecimal.ZERO) <= 0)
        {
            throw new ServiceException("退费数量须大于 0");
        }
        String tenantId = SecurityUtils.getCustomerId();
        String visitKind = resolveVisitKind(body.getVisitKind());
        String originMirrorRowId = resolveOriginMirrorRowId(tenantId, visitKind, body.getOriginChargeDetailId().trim());

        HisInpatientChargeMirror inProbe = null;
        HisOutpatientChargeMirror outProbe = null;
        if (KIND_IN.equals(visitKind))
        {
            inProbe = hisInpatientChargeMirrorMapper.selectByIdAndTenant(tenantId, originMirrorRowId);
        }
        else
        {
            outProbe = hisOutpatientChargeMirrorMapper.selectByIdAndTenant(tenantId, originMirrorRowId);
        }

        RefundAllocationPlan plan = planRefundAllocation(tenantId, visitKind, originMirrorRowId, "2", body.getRefundQty());
        Map<Long, List<DeptBatchConsumeReverseReq.ReverseItem>> byConsume = plan.byConsume;
        List<PlannedLine> planned = plan.planned;
        List<HisMirrorConsumeLink> candidates = plan.candidates;

        HisBillingRefundOrder order = buildOrderHeader(tenantId, visitKind, body, originMirrorRowId, "2", inProbe, outProbe);
        hisBillingRefundOrderMapper.insertHisBillingRefundOrder(order);

        String operator = resolveRefundOperator(body == null ? null : body.getRemark());
        List<HisBillingRefundOrderLine> lineRows = new ArrayList<>();
        Date now = DateUtils.getNowDate();
        String uid = SecurityUtils.getUserIdStr();

        try
        {
            for (Map.Entry<Long, List<DeptBatchConsumeReverseReq.ReverseItem>> en : byConsume.entrySet())
            {
                DeptBatchConsumeReverseReq req = new DeptBatchConsumeReverseReq();
                req.setConsumeId(en.getKey());
                req.setItems(mergeReverseItems(en.getValue()));
                req.setRemark(String.format("HIS计费低值退费 originMirror=%s", originMirrorRowId));
                deptBatchConsumeService.reverseConsumeForBillingRefund(req, operator);
            }
            for (PlannedLine pl : planned)
            {
                hisMirrorConsumeLinkMapper.increaseReturnedQtyById(pl.linkId, pl.delta, uid);
                HisMirrorConsumeLink lk = findLinkById(candidates, pl.linkId);
                HisBillingRefundOrderLine lr = new HisBillingRefundOrderLine();
                lr.setId(UUID7.generateUUID7());
                lr.setTenantId(tenantId);
                lr.setRefundOrderId(order.getId());
                lr.setConsumeLinkId(pl.linkId);
                lr.setReturnQty(pl.delta);
                lr.setDepInventoryId(lk != null ? lk.getDepInventoryId() : null);
                lr.setRemark("低值退费返还");
                lr.setDelFlag(0);
                lr.setCreateBy(uid);
                lr.setCreateTime(now);
                lr.setUpdateBy(uid);
                lr.setUpdateTime(now);
                lineRows.add(lr);
            }
            if (!lineRows.isEmpty())
            {
                hisBillingRefundOrderLineMapper.insertBatch(lineRows);
            }
            hisBillingRefundOrderMapper.updateProcessStatus(tenantId, order.getId(), STATUS_DONE, null, uid);
            order.setProcessStatus(STATUS_DONE);
            markRefundMirrorProcessed(tenantId, visitKind, body.getRefundMirrorRowId());
        }
        catch (ServiceException ex)
        {
            String msg = ex.getMessage() == null ? ex.toString() : ex.getMessage();
            hisBillingRefundOrderMapper.updateProcessStatus(tenantId, order.getId(), STATUS_FAILED,
                StringUtils.left(msg, 480), uid);
            order.setProcessStatus(STATUS_FAILED);
            order.setFailReason(msg);
            throw ex;
        }
        catch (Exception ex)
        {
            String msg = ex.getMessage() == null ? ex.toString() : ex.getMessage();
            hisBillingRefundOrderMapper.updateProcessStatus(tenantId, order.getId(), STATUS_FAILED,
                StringUtils.left(msg, 480), uid);
            order.setProcessStatus(STATUS_FAILED);
            order.setFailReason(msg);
            throw new ServiceException(msg);
        }
        return order;
    }

    @Override
    public void processAutoRefundForFetchBatch(String tenantId, String fetchBatchId, String visitKind)
    {
        if (!HisBillingTenantConstants.TENANT_HENGSHUI_THIRD.equals(tenantId)
            || StringUtils.isBlank(fetchBatchId) || StringUtils.isBlank(visitKind))
        {
            return;
        }
        String vk = visitKind.trim().toUpperCase();
        if (KIND_IN.equals(vk))
        {
            List<HisInpatientChargeMirror> rows = hisInpatientChargeMirrorMapper.selectRefundPendingByFetchBatch(tenantId, fetchBatchId);
            if (rows == null)
            {
                return;
            }
            for (HisInpatientChargeMirror row : rows)
            {
                tryAutoRefundInpatientRow(tenantId, vk, row);
            }
        }
        else if ("OUTPATIENT".equals(vk))
        {
            List<HisOutpatientChargeMirror> rows = hisOutpatientChargeMirrorMapper.selectRefundPendingByFetchBatch(tenantId, fetchBatchId);
            if (rows == null)
            {
                return;
            }
            for (HisOutpatientChargeMirror row : rows)
            {
                tryAutoRefundOutpatientRow(tenantId, vk, row);
            }
        }
    }

    private void tryAutoRefundInpatientRow(String tenantId, String visitKind, HisInpatientChargeMirror row)
    {
        if (row == null || StringUtils.isBlank(row.getId()))
        {
            return;
        }
        String originChargeId = StringUtils.trimToEmpty(row.getHisInpatientChargeIdTf());
        if (originChargeId.isEmpty())
        {
            return;
        }
        BigDecimal refundQty = refundQtyFromMirror(row.getQuantity());
        if (refundQty.compareTo(BigDecimal.ZERO) <= 0)
        {
            log.warn("HIS自动退费跳过 mirrorRowId={} 退费数量为0", row.getId());
            return;
        }
        if (hisBillingRefundOrderMapper.countDoneByRefundMirrorRowId(tenantId, row.getId()) > 0)
        {
            return;
        }
        try
        {
            if (isHighValueMirrorRow(row.getValueLevel()))
            {
                HisBillingRefundHighBody body = buildAutoHighRefundBody(visitKind, originChargeId, row.getId(), refundQty);
                billingRefundService.processHighValueRefund(body);
            }
            else
            {
                HisBillingRefundLowBody body = new HisBillingRefundLowBody();
                body.setVisitKind(visitKind);
                body.setOriginChargeDetailId(originChargeId);
                body.setRefundQty(refundQty);
                body.setRefundMirrorRowId(row.getId());
                body.setRemark("HIS计费抓取自动退费");
                billingRefundService.processLowValueRefund(body);
            }
        }
        catch (Exception e)
        {
            log.warn("HIS自动退费跳过 mirrorRowId={} err={}", row.getId(), e.toString());
        }
    }

    private void tryAutoRefundOutpatientRow(String tenantId, String visitKind, HisOutpatientChargeMirror row)
    {
        if (row == null || StringUtils.isBlank(row.getId()))
        {
            return;
        }
        String originChargeId = StringUtils.trimToEmpty(row.getHisOutpatientChargeIdTf());
        if (originChargeId.isEmpty())
        {
            return;
        }
        BigDecimal refundQty = refundQtyFromMirror(row.getQuantity());
        if (refundQty.compareTo(BigDecimal.ZERO) <= 0)
        {
            log.warn("HIS自动退费跳过 mirrorRowId={} 退费数量为0", row.getId());
            return;
        }
        if (hisBillingRefundOrderMapper.countDoneByRefundMirrorRowId(tenantId, row.getId()) > 0)
        {
            return;
        }
        try
        {
            if (isHighValueMirrorRow(row.getValueLevel()))
            {
                HisBillingRefundHighBody body = buildAutoHighRefundBody(visitKind, originChargeId, row.getId(), refundQty);
                billingRefundService.processHighValueRefund(body);
            }
            else
            {
                HisBillingRefundLowBody body = new HisBillingRefundLowBody();
                body.setVisitKind(visitKind);
                body.setOriginChargeDetailId(originChargeId);
                body.setRefundQty(refundQty);
                body.setRefundMirrorRowId(row.getId());
                body.setRemark("HIS计费抓取自动退费");
                billingRefundService.processLowValueRefund(body);
            }
        }
        catch (Exception e)
        {
            log.warn("HIS自动退费跳过 mirrorRowId={} err={}", row.getId(), e.toString());
        }
    }

    private HisBillingRefundHighBody buildAutoHighRefundBody(String visitKind, String originChargeDetailId,
        String refundMirrorRowId, BigDecimal refundQty)
    {
        String tenantId = SecurityUtils.getCustomerId();
        String originMirrorRowId = resolveOriginMirrorRowId(tenantId, visitKind, originChargeDetailId);
        RefundAllocationPlan plan = planRefundAllocation(tenantId, visitKind, originMirrorRowId, "1", refundQty);
        List<HisBillingRefundHighLineBody> lines = new ArrayList<>();
        for (PlannedLine pl : plan.planned)
        {
            HisBillingRefundHighLineBody ln = new HisBillingRefundHighLineBody();
            ln.setConsumeLinkId(pl.linkId);
            ln.setReturnQty(pl.delta);
            lines.add(ln);
        }
        HisBillingRefundHighBody body = new HisBillingRefundHighBody();
        body.setVisitKind(visitKind);
        body.setOriginChargeDetailId(originChargeDetailId);
        body.setRefundMirrorRowId(refundMirrorRowId);
        body.setRemark("HIS计费抓取自动退费");
        body.setLines(lines);
        return body;
    }

    private RefundAllocationPlan planRefundAllocation(String tenantId, String visitKind, String originMirrorRowId,
        String valueLevel, BigDecimal refundQty)
    {
        List<HisMirrorConsumeLink> candidates = hisMirrorConsumeLinkMapper.selectCandidateLinksForRefund(
            tenantId, visitKind, originMirrorRowId, valueLevel);
        if (candidates == null || candidates.isEmpty())
        {
            String msg = "1".equals(valueLevel)
                ? "未找到可返还的高值计费消耗关联"
                : "未找到可返还的低值计费消耗关联，请确认已产生消耗且尚有可退数量";
            throw new ServiceException(msg);
        }
        List<HisMirrorConsumeLink> sorted = lvRefundConsumeLinkOrderStrategy.sortForRefund(candidates);
        BigDecimal need = refundQty == null ? BigDecimal.ZERO : refundQty;
        Map<Long, List<DeptBatchConsumeReverseReq.ReverseItem>> byConsume = new LinkedHashMap<>();
        List<PlannedLine> planned = new ArrayList<>();
        Map<Long, Map<Long, BigDecimal>> canCache = new HashMap<>();
        for (HisMirrorConsumeLink lk : sorted)
        {
            if (need.compareTo(BigDecimal.ZERO) <= 0)
            {
                break;
            }
            BigDecimal linkRem = refundableOnLink(lk);
            if (linkRem.compareTo(BigDecimal.ZERO) <= 0)
            {
                continue;
            }
            Long consumeId = lk.getDeptBatchConsumeId();
            Long entryId = lk.getDeptBatchConsumeEntryId();
            if (lk.getTraceabilityId() != null && lk.getTraceabilityEntryId() != null)
            {
                BigDecimal take = need.min(linkRem);
                if (take.compareTo(BigDecimal.ZERO) <= 0)
                {
                    continue;
                }
                planned.add(new PlannedLine(lk.getId(), take, null, null));
                need = need.subtract(take);
                continue;
            }
            if (consumeId == null || entryId == null)
            {
                continue;
            }
            Map<Long, BigDecimal> canByEntry = canCache.computeIfAbsent(consumeId, this::loadCanReverseByEntry);
            BigDecimal canEntry = canByEntry.get(entryId);
            if (canEntry == null || canEntry.compareTo(BigDecimal.ZERO) <= 0)
            {
                continue;
            }
            BigDecimal take = need.min(linkRem).min(canEntry);
            if (take.compareTo(BigDecimal.ZERO) <= 0)
            {
                continue;
            }
            DeptBatchConsumeReverseReq.ReverseItem it = new DeptBatchConsumeReverseReq.ReverseItem();
            it.setSrcConsumeEntryId(entryId);
            it.setReverseQty(take);
            byConsume.computeIfAbsent(consumeId, k -> new ArrayList<>()).add(it);
            planned.add(new PlannedLine(lk.getId(), take, consumeId, entryId));
            need = need.subtract(take);
        }
        if (need.compareTo(BigDecimal.ZERO) > 0)
        {
            throw new ServiceException(String.format("退费数量超过可返还上限，剩余未分配：%s（请检查消耗关联与反消耗额度）", need));
        }
        RefundAllocationPlan plan = new RefundAllocationPlan();
        plan.candidates = candidates;
        plan.byConsume = byConsume;
        plan.planned = planned;
        return plan;
    }

    private static BigDecimal refundQtyFromMirror(BigDecimal quantity)
    {
        if (quantity == null)
        {
            return BigDecimal.ZERO;
        }
        return quantity.abs();
    }

    private static boolean isHighValueMirrorRow(String valueLevel)
    {
        return "1".equals(StringUtils.trimToEmpty(valueLevel));
    }

    private void markRefundMirrorProcessed(String tenantId, String visitKind, String refundMirrorRowId)
    {
        if (StringUtils.isBlank(refundMirrorRowId))
        {
            return;
        }
        Date procTime = DateUtils.getNowDate();
        String procBy = SecurityUtils.getUserIdStr();
        List<String> ids = java.util.Collections.singletonList(refundMirrorRowId.trim());
        if (KIND_IN.equals(visitKind))
        {
            hisInpatientChargeMirrorMapper.updateMirrorProcessByIds(tenantId, ids,
                MIRROR_STATUS_REFUNDED, MIRROR_PROC_TYPE_REFUND, procTime, procBy, null, null);
        }
        else
        {
            hisOutpatientChargeMirrorMapper.updateMirrorProcessByIds(tenantId, ids,
                MIRROR_STATUS_REFUNDED, MIRROR_PROC_TYPE_REFUND, procTime, procBy, null, null);
        }
        hisPatientChargeMirrorUnifiedMapper.updateMirrorProcessByIds(tenantId, ids,
            MIRROR_STATUS_REFUNDED, MIRROR_PROC_TYPE_REFUND, procTime, procBy, null, null);
    }

    private List<DeptBatchConsumeReverseReq.ReverseItem> mergeReverseItems(List<DeptBatchConsumeReverseReq.ReverseItem> items)
    {
        Map<Long, BigDecimal> merged = new LinkedHashMap<>();
        if (items != null)
        {
            for (DeptBatchConsumeReverseReq.ReverseItem it : items)
            {
                if (it == null || it.getSrcConsumeEntryId() == null || it.getReverseQty() == null)
                {
                    continue;
                }
                merged.merge(it.getSrcConsumeEntryId(), it.getReverseQty(), BigDecimal::add);
            }
        }
        List<DeptBatchConsumeReverseReq.ReverseItem> out = new ArrayList<>();
        for (Map.Entry<Long, BigDecimal> me : merged.entrySet())
        {
            DeptBatchConsumeReverseReq.ReverseItem it = new DeptBatchConsumeReverseReq.ReverseItem();
            it.setSrcConsumeEntryId(me.getKey());
            it.setReverseQty(me.getValue());
            out.add(it);
        }
        return out;
    }

    private HisMirrorConsumeLink findLinkById(List<HisMirrorConsumeLink> candidates, String linkId)
    {
        if (candidates == null || linkId == null)
        {
            return null;
        }
        for (HisMirrorConsumeLink c : candidates)
        {
            if (c != null && linkId.equals(c.getId()))
            {
                return c;
            }
        }
        return null;
    }

    private HisBillingRefundOrder buildOrderHeader(String tenantId, String visitKind, HisBillingRefundLowBody low,
        String originMirrorRowId, String valueLevel, HisInpatientChargeMirror inRow, HisOutpatientChargeMirror outRow)
    {
        HisBillingRefundOrder order = new HisBillingRefundOrder();
        order.setId(UUID7.generateUUID7());
        order.setTenantId(tenantId);
        order.setVisitKind(visitKind);
        order.setRefundMirrorRowId(low.getRefundMirrorRowId());
        order.setOriginChargeDetailId(low.getOriginChargeDetailId().trim());
        order.setOriginMirrorRowId(originMirrorRowId);
        order.setRefundQty(low.getRefundQty());
        order.setValueLevel(valueLevel);
        order.setProcessStatus(STATUS_PENDING);
        order.setRemark(low.getRemark());
        order.setDelFlag(0);
        if (inRow != null)
        {
            order.setPatientName(inRow.getPatientName());
            order.setDepartmentId(inRow.getDepartmentId());
            order.setDepartmentName(inRow.getDeptName());
        }
        if (outRow != null)
        {
            order.setPatientName(outRow.getPatientName());
            order.setDepartmentId(outRow.getDepartmentId());
            order.setDepartmentName(outRow.getClinicName());
        }
        String uid = SecurityUtils.getUserIdStr();
        Date now = DateUtils.getNowDate();
        order.setCreateBy(uid);
        order.setCreateTime(now);
        order.setUpdateBy(uid);
        order.setUpdateTime(now);
        return order;
    }

    private HisBillingRefundOrder buildOrderHeaderHigh(String tenantId, String visitKind, HisBillingRefundHighBody body,
        String originMirrorRowId, HisInpatientChargeMirror inRow, HisOutpatientChargeMirror outRow)
    {
        HisBillingRefundOrder order = new HisBillingRefundOrder();
        order.setId(UUID7.generateUUID7());
        order.setTenantId(tenantId);
        order.setVisitKind(visitKind);
        order.setRefundMirrorRowId(body.getRefundMirrorRowId());
        order.setOriginChargeDetailId(body.getOriginChargeDetailId().trim());
        order.setOriginMirrorRowId(originMirrorRowId);
        BigDecimal sum = BigDecimal.ZERO;
        if (body.getLines() != null)
        {
            for (HisBillingRefundHighLineBody ln : body.getLines())
            {
                if (ln != null && ln.getReturnQty() != null)
                {
                    sum = sum.add(ln.getReturnQty());
                }
            }
        }
        order.setRefundQty(sum);
        order.setValueLevel("1");
        order.setProcessStatus(STATUS_PENDING);
        order.setRemark(body.getRemark());
        order.setDelFlag(0);
        if (inRow != null)
        {
            order.setPatientName(inRow.getPatientName());
            order.setDepartmentId(inRow.getDepartmentId());
            order.setDepartmentName(inRow.getDeptName());
        }
        if (outRow != null)
        {
            order.setPatientName(outRow.getPatientName());
            order.setDepartmentId(outRow.getDepartmentId());
            order.setDepartmentName(outRow.getClinicName());
        }
        String uid = SecurityUtils.getUserIdStr();
        Date now = DateUtils.getNowDate();
        order.setCreateBy(uid);
        order.setCreateTime(now);
        order.setUpdateBy(uid);
        order.setUpdateTime(now);
        return order;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public HisBillingRefundOrder processHighValueRefund(HisBillingRefundHighBody body)
    {
        assertHengsuiTenant();
        if (body == null || StringUtils.isBlank(body.getOriginChargeDetailId()) || body.getLines() == null
            || body.getLines().isEmpty())
        {
            throw new ServiceException("请提供 originChargeDetailId 与退费行明细");
        }
        String tenantId = SecurityUtils.getCustomerId();
        String visitKind = resolveVisitKind(body.getVisitKind());
        String originMirrorRowId = resolveOriginMirrorRowId(tenantId, visitKind, body.getOriginChargeDetailId().trim());

        HisInpatientChargeMirror inProbe = null;
        HisOutpatientChargeMirror outProbe = null;
        if (KIND_IN.equals(visitKind))
        {
            inProbe = hisInpatientChargeMirrorMapper.selectByIdAndTenant(tenantId, originMirrorRowId);
        }
        else
        {
            outProbe = hisOutpatientChargeMirrorMapper.selectByIdAndTenant(tenantId, originMirrorRowId);
        }

        List<HisMirrorConsumeLink> candidates = hisMirrorConsumeLinkMapper.selectCandidateLinksForRefund(
            tenantId, visitKind, originMirrorRowId, "1");
        if (candidates == null || candidates.isEmpty())
        {
            throw new ServiceException("未找到可返还的高值计费消耗关联");
        }

        Map<String, HisMirrorConsumeLink> linkById = new HashMap<>();
        for (HisMirrorConsumeLink c : candidates)
        {
            if (c != null && StringUtils.isNotBlank(c.getId()))
            {
                linkById.put(c.getId(), c);
            }
        }

        Map<Long, List<DeptBatchConsumeReverseReq.ReverseItem>> byConsume = new LinkedHashMap<>();
        List<HisBillingRefundOrderLine> lineRows = new ArrayList<>();
        List<PlannedLine> planned = new ArrayList<>();

        Map<Long, Map<Long, BigDecimal>> canCache = new HashMap<>();
        Map<String, BigDecimal> linkRemLeft = new HashMap<>();
        for (HisMirrorConsumeLink c : candidates)
        {
            if (c != null && StringUtils.isNotBlank(c.getId()))
            {
                linkRemLeft.put(c.getId(), refundableOnLink(c));
            }
        }
        Map<String, BigDecimal> entryCanLeft = new HashMap<>();

        for (HisBillingRefundHighLineBody ln : body.getLines())
        {
            if (ln == null || ln.getReturnQty() == null || ln.getReturnQty().compareTo(BigDecimal.ZERO) <= 0)
            {
                continue;
            }
            HisMirrorConsumeLink lk = null;
            if (StringUtils.isNotBlank(ln.getConsumeLinkId()))
            {
                lk = linkById.get(ln.getConsumeLinkId().trim());
            }
            if (lk == null && StringUtils.isNotBlank(ln.getInHospitalCode()))
            {
                String n = HisMatchTextUtils.normalizeMatchKey(ln.getInHospitalCode());
                for (HisMirrorConsumeLink c : candidates)
                {
                    if (c != null && HisMatchTextUtils.normalizeMatchKey(StringUtils.defaultString(c.getInHospitalCode())).equals(n))
                    {
                        lk = c;
                        break;
                    }
                }
            }
            if (lk == null)
            {
                throw new ServiceException("无法匹配退费行与高值消耗关联（consumeLinkId / 院内码）");
            }
            Long consumeId = lk.getDeptBatchConsumeId();
            Long entryId = lk.getDeptBatchConsumeEntryId();
            if (lk.getTraceabilityId() != null && lk.getTraceabilityEntryId() != null)
            {
                BigDecimal linkRem = linkRemLeft.getOrDefault(lk.getId(), BigDecimal.ZERO);
                BigDecimal take = ln.getReturnQty().min(linkRem);
                if (take.compareTo(BigDecimal.ZERO) <= 0)
                {
                    throw new ServiceException("退费数量超过该行可退上限");
                }
                linkRemLeft.put(lk.getId(), linkRem.subtract(take));
                planned.add(new PlannedLine(lk.getId(), take, null, null));
                continue;
            }
            if (consumeId == null || entryId == null)
            {
                throw new ServiceException("消耗关联缺少消耗单或明细");
            }
            Map<Long, BigDecimal> canByEntry = canCache.computeIfAbsent(consumeId, this::loadCanReverseByEntry);
            String ek = consumeId + ":" + entryId;
            if (!entryCanLeft.containsKey(ek))
            {
                BigDecimal initial = canByEntry.get(entryId);
                if (initial == null)
                {
                    initial = BigDecimal.ZERO;
                }
                entryCanLeft.put(ek, initial);
            }
            BigDecimal canEntry = entryCanLeft.get(ek);
            if (canEntry == null || canEntry.compareTo(BigDecimal.ZERO) <= 0)
            {
                throw new ServiceException(String.format("明细[%s]当前无可反消耗数量", entryId));
            }
            BigDecimal linkRem = linkRemLeft.getOrDefault(lk.getId(), BigDecimal.ZERO);
            BigDecimal take = ln.getReturnQty().min(canEntry).min(linkRem);
            if (take.compareTo(BigDecimal.ZERO) <= 0)
            {
                throw new ServiceException("退费数量超过该行可退上限");
            }
            entryCanLeft.put(ek, canEntry.subtract(take));
            linkRemLeft.put(lk.getId(), linkRem.subtract(take));
            DeptBatchConsumeReverseReq.ReverseItem it = new DeptBatchConsumeReverseReq.ReverseItem();
            it.setSrcConsumeEntryId(entryId);
            it.setReverseQty(take);
            byConsume.computeIfAbsent(consumeId, k -> new ArrayList<>()).add(it);
            planned.add(new PlannedLine(lk.getId(), take, consumeId, entryId));
        }
        if (planned.isEmpty())
        {
            throw new ServiceException("没有有效的退费明细");
        }

        HisBillingRefundOrder order = buildOrderHeaderHigh(tenantId, visitKind, body, originMirrorRowId, inProbe, outProbe);
        hisBillingRefundOrderMapper.insertHisBillingRefundOrder(order);

        String operator = resolveRefundOperator(body == null ? null : body.getRemark());
        Date now = DateUtils.getNowDate();
        String uid = SecurityUtils.getUserIdStr();

        try
        {
            for (Map.Entry<Long, List<DeptBatchConsumeReverseReq.ReverseItem>> en : byConsume.entrySet())
            {
                Map<Long, BigDecimal> merged = new LinkedHashMap<>();
                for (DeptBatchConsumeReverseReq.ReverseItem it : en.getValue())
                {
                    if (it == null || it.getSrcConsumeEntryId() == null || it.getReverseQty() == null)
                    {
                        continue;
                    }
                    merged.merge(it.getSrcConsumeEntryId(), it.getReverseQty(), BigDecimal::add);
                }
                List<DeptBatchConsumeReverseReq.ReverseItem> mergedItems = new ArrayList<>();
                for (Map.Entry<Long, BigDecimal> me : merged.entrySet())
                {
                    DeptBatchConsumeReverseReq.ReverseItem it = new DeptBatchConsumeReverseReq.ReverseItem();
                    it.setSrcConsumeEntryId(me.getKey());
                    it.setReverseQty(me.getValue());
                    mergedItems.add(it);
                }
                if (mergedItems.isEmpty())
                {
                    continue;
                }
                DeptBatchConsumeReverseReq req = new DeptBatchConsumeReverseReq();
                req.setConsumeId(en.getKey());
                req.setItems(mergedItems);
                req.setRemark(String.format("HIS计费高值退费 originMirror=%s", originMirrorRowId));
                deptBatchConsumeService.reverseConsumeForBillingRefund(req, operator);
            }
            for (PlannedLine pl : planned)
            {
                HisMirrorConsumeLink lk = linkById.get(pl.linkId);
                if (lk != null && lk.getTraceabilityId() != null && lk.getTraceabilityEntryId() != null)
                {
                    applyTraceHighRefund(tenantId, lk, pl.delta);
                }
                hisMirrorConsumeLinkMapper.increaseReturnedQtyById(pl.linkId, pl.delta, uid);
                HisBillingRefundOrderLine lr = new HisBillingRefundOrderLine();
                lr.setId(UUID7.generateUUID7());
                lr.setTenantId(tenantId);
                lr.setRefundOrderId(order.getId());
                lr.setConsumeLinkId(pl.linkId);
                lr.setReturnQty(pl.delta);
                lr.setGzDepInventoryId(lk != null ? lk.getGzDepInventoryId() : null);
                lr.setInHospitalCode(lk != null ? lk.getInHospitalCode() : null);
                lr.setRemark("高值退费返还");
                lr.setDelFlag(0);
                lr.setCreateBy(uid);
                lr.setCreateTime(now);
                lr.setUpdateBy(uid);
                lr.setUpdateTime(now);
                lineRows.add(lr);
            }
            if (!lineRows.isEmpty())
            {
                hisBillingRefundOrderLineMapper.insertBatch(lineRows);
            }
            hisBillingRefundOrderMapper.updateProcessStatus(tenantId, order.getId(), STATUS_DONE, null, uid);
            order.setProcessStatus(STATUS_DONE);
            markRefundMirrorProcessed(tenantId, visitKind, body.getRefundMirrorRowId());
        }
        catch (ServiceException ex)
        {
            String msg = ex.getMessage() == null ? ex.toString() : ex.getMessage();
            hisBillingRefundOrderMapper.updateProcessStatus(tenantId, order.getId(), STATUS_FAILED,
                StringUtils.left(msg, 480), uid);
            order.setProcessStatus(STATUS_FAILED);
            order.setFailReason(msg);
            throw ex;
        }
        catch (Exception ex)
        {
            String msg = ex.getMessage() == null ? ex.toString() : ex.getMessage();
            hisBillingRefundOrderMapper.updateProcessStatus(tenantId, order.getId(), STATUS_FAILED,
                StringUtils.left(msg, 480), uid);
            order.setProcessStatus(STATUS_FAILED);
            order.setFailReason(msg);
            throw new ServiceException(msg);
        }
        return order;
    }

    private void applyTraceHighRefund(String tenantId, HisMirrorConsumeLink lk, BigDecimal returnQty)
    {
        if (lk == null || returnQty == null || returnQty.compareTo(BigDecimal.ZERO) <= 0)
        {
            return;
        }
        GzTraceability trace = gzTraceabilityService.selectGzTraceabilityById(lk.getTraceabilityId());
        if (trace == null || trace.getTraceabilityEntryList() == null)
        {
            throw new ServiceException("高值计费单不存在，无法退费返还");
        }
        GzTraceabilityEntry entry = trace.getTraceabilityEntryList().stream()
            .filter(e -> e != null && lk.getTraceabilityEntryId().equals(e.getId()))
            .findFirst()
            .orElse(null);
        if (entry == null)
        {
            throw new ServiceException("高值计费明细不存在，无法退费返还");
        }
        Long gzId = lk.getGzDepInventoryId() != null ? lk.getGzDepInventoryId() : entry.getInventoryId();
        if (gzId == null)
        {
            throw new ServiceException("缺少高值库存关联，无法退费返还");
        }
        GzDepInventory gz = gzDepInventoryMapper.selectGzDepInventoryById(gzId);
        if (gz == null)
        {
            throw new ServiceException("高值科室库存不存在，无法退费返还");
        }
        hcBarcodeLifecycleService.onMirrorHighChargeRefund(trace, entry, gz, returnQty);
    }

    private String resolveRefundOperator(String remark)
    {
        return HisAutoWriteOffOperatorSupport.resolveRefundOperator(
            SecurityUtils.getCustomerId(), remark, sbTenantSettingService);
    }

    private static class RefundAllocationPlan
    {
        List<HisMirrorConsumeLink> candidates;
        Map<Long, List<DeptBatchConsumeReverseReq.ReverseItem>> byConsume;
        List<PlannedLine> planned;
    }

    private static class PlannedLine
    {
        final String linkId;
        final BigDecimal delta;

        PlannedLine(String linkId, BigDecimal delta, Long consumeId, Long entryId)
        {
            this.linkId = linkId;
            this.delta = delta;
        }
    }
}

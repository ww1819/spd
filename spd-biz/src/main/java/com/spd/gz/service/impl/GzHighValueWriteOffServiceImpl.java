package com.spd.gz.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.spd.common.exception.ServiceException;
import com.spd.common.utils.DateUtils;
import com.spd.common.utils.SecurityUtils;
import com.spd.department.domain.DeptBatchConsume;
import com.spd.department.domain.DeptBatchConsumeReverseReq;
import com.spd.department.service.IDeptBatchConsumeService;
import com.spd.gz.domain.GzDepInventory;
import com.spd.gz.domain.GzTraceability;
import com.spd.gz.domain.GzTraceabilityEntry;
import com.spd.gz.domain.dto.GzHighValueWriteOffBody;
import com.spd.gz.domain.dto.GzHighValueWriteOffResultVo;
import com.spd.gz.domain.dto.GzInstantIoReverseBody;
import com.spd.gz.mapper.GzDepInventoryMapper;
import com.spd.gz.service.IGzHighValueWriteOffService;
import com.spd.gz.service.IGzInstantIoService;
import com.spd.gz.service.IGzTraceabilityService;
import com.spd.hc.service.IHcBarcodeLifecycleService;
import com.spd.his.domain.HisMirrorConsumeLink;
import com.spd.his.mapper.HisInpatientChargeMirrorMapper;
import com.spd.his.mapper.HisMirrorConsumeLinkMapper;
import com.spd.his.mapper.HisOutpatientChargeMirrorMapper;
import com.spd.his.mapper.HisPatientChargeMirrorUnifiedMapper;
import com.spd.gz.mapper.GzHighConsumeConfirmMapper;

/**
 * 高值冲销：档 A/B 回补科室库存；档 C 反向单据 + 回补。
 */
@Service
public class GzHighValueWriteOffServiceImpl implements IGzHighValueWriteOffService
{
    private static final String KIND_IN = "INPATIENT";
    private static final String STATUS_PENDING = "PENDING_CONSUME";
    private static final String PROC_TYPE_EMPTY = "";

    @Autowired
    private HisMirrorConsumeLinkMapper hisMirrorConsumeLinkMapper;
    @Autowired
    private GzDepInventoryMapper gzDepInventoryMapper;
    @Autowired
    private IGzTraceabilityService gzTraceabilityService;
    @Autowired
    private IHcBarcodeLifecycleService hcBarcodeLifecycleService;
    @Autowired
    private IDeptBatchConsumeService deptBatchConsumeService;
    @Lazy
    @Autowired
    private IGzInstantIoService gzInstantIoService;
    @Autowired
    private GzHighConsumeConfirmMapper gzHighConsumeConfirmMapper;
    @Autowired
    private HisInpatientChargeMirrorMapper hisInpatientChargeMirrorMapper;
    @Autowired
    private HisOutpatientChargeMirrorMapper hisOutpatientChargeMirrorMapper;
    @Autowired
    private HisPatientChargeMirrorUnifiedMapper hisPatientChargeMirrorUnifiedMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public GzHighValueWriteOffResultVo writeOff(GzHighValueWriteOffBody body)
    {
        if (body == null || body.getLinkIds() == null || body.getLinkIds().isEmpty())
        {
            throw new ServiceException("请选择要冲销的高值核销明细");
        }
        String tenantId = requireTenant();
        String operator = SecurityUtils.getUserIdStr();
        String remark = StringUtils.defaultIfBlank(body.getRemark(), "高值核销冲销");
        List<String> linkIds = body.getLinkIds().stream()
            .filter(StringUtils::isNotBlank).map(String::trim).distinct().collect(Collectors.toList());

        String source = StringUtils.trimToEmpty(body.getSource()).toUpperCase();
        boolean fromConfirmPage = "CONFIRM".equals(source);

        List<HisMirrorConsumeLink> links = new ArrayList<>();
        for (String id : linkIds)
        {
            HisMirrorConsumeLink lk = hisMirrorConsumeLinkMapper.selectById(tenantId, id);
            if (lk == null)
            {
                throw new ServiceException("消耗关联不存在：" + id);
            }
            if (lk.getGzDepInventoryId() == null && lk.getTraceabilityId() == null)
            {
                throw new ServiceException("非高值核销关联，无法高值冲销：" + id);
            }
            BigDecimal rem = refundableOnLink(lk);
            if (rem.compareTo(BigDecimal.ZERO) <= 0)
            {
                throw new ServiceException("明细已无剩余可冲销量：" + id);
            }
            if (fromConfirmPage)
            {
                // HV-Q-006：确认页仅档 A（未临床确认）；已确认须到即入即出页由库房冲销
                Integer confirmStatus = lk.getConfirmStatus() == null ? 0 : lk.getConfirmStatus();
                if (confirmStatus == 1)
                {
                    throw new ServiceException(
                        "已临床确认的明细不能在「高值核销确认」冲销，请到「高值即入即出」由库房处理");
                }
                Integer ioStatus = lk.getInstantIoAuditStatus() == null ? 0 : lk.getInstantIoAuditStatus();
                if (ioStatus == 1 || ioStatus == 2)
                {
                    throw new ServiceException(
                        "该明细已做即入即出审核（库房段），禁止在「高值核销确认」冲销，请到「高值即入即出」处理");
                }
            }
            links.add(lk);
        }

        // 档 C：已审核未冲销 → 先生成反向单据（301+401）；反向按确认批次整批，回补也扩到同批
        List<HisMirrorConsumeLink> needReverseLinks = fromConfirmPage
            ? java.util.Collections.emptyList()
            : links.stream()
                .filter(l -> Integer.valueOf(1).equals(l.getConfirmStatus())
                    && Integer.valueOf(1).equals(l.getInstantIoAuditStatus()))
                .collect(Collectors.toList());
        GzHighValueWriteOffResultVo vo = new GzHighValueWriteOffResultVo();
        if (!needReverseLinks.isEmpty())
        {
            Set<String> confirmIds = needReverseLinks.stream()
                .map(HisMirrorConsumeLink::getConfirmId)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toCollection(LinkedHashSet::new));
            List<String> batchLinkIds = hisMirrorConsumeLinkMapper.selectActiveLinkIdsByConfirmIds(
                tenantId, new ArrayList<>(confirmIds));
            if (batchLinkIds == null || batchLinkIds.isEmpty())
            {
                batchLinkIds = needReverseLinks.stream().map(HisMirrorConsumeLink::getId).collect(Collectors.toList());
            }
            GzInstantIoReverseBody revBody = new GzInstantIoReverseBody();
            revBody.setLinkIds(batchLinkIds);
            gzInstantIoService.reverse(revBody);
            vo.setReverseBillPairCount(1);
            vo.getMessages().add("已生成退货/退库反向单据（按确认批次）");
            // 同批全部纳入后续回补
            Set<String> expanded = new LinkedHashSet<>(linkIds);
            expanded.addAll(batchLinkIds);
            linkIds = new ArrayList<>(expanded);
            links = reloadLinks(tenantId, linkIds);
        }
        vo.setLinkCount(links.size());

        Set<String> mirrorKeys = new LinkedHashSet<>();
        Set<String> clearConfirmLinkIds = new LinkedHashSet<>();
        Set<Long> tracesToVoid = new LinkedHashSet<>();

        for (HisMirrorConsumeLink lk : links)
        {
            Integer ioStatus = lk.getInstantIoAuditStatus() == null ? 0 : lk.getInstantIoAuditStatus();
            Integer confirmStatus = lk.getConfirmStatus() == null ? 0 : lk.getConfirmStatus();
            if (confirmStatus == 1 && ioStatus == 1)
            {
                throw new ServiceException("明细仍为已审核状态，请刷新后重试");
            }
            // status=2 表示已有反向单，继续回补；status=0 为 A/B
            BigDecimal rem = refundableOnLink(lk);
            restoreInventory(lk, rem, remark);
            hisMirrorConsumeLinkMapper.markLinkFullyReturnedById(tenantId, lk.getId(), operator);
            if (confirmStatus == 1 || Integer.valueOf(2).equals(ioStatus))
            {
                clearConfirmLinkIds.add(lk.getId());
            }
            if (lk.getTraceabilityId() != null)
            {
                tracesToVoid.add(lk.getTraceabilityId());
            }
            vo.setRestoredCount(vo.getRestoredCount() + 1);
            if (StringUtils.isNotBlank(lk.getVisitKind()) && StringUtils.isNotBlank(lk.getMirrorRowId()))
            {
                mirrorKeys.add(lk.getVisitKind().trim().toUpperCase() + "|" + lk.getMirrorRowId().trim());
            }
        }

        if (!clearConfirmLinkIds.isEmpty())
        {
            gzHighConsumeConfirmMapper.clearLinkConfirmForWriteOff(
                tenantId, new ArrayList<>(clearConfirmLinkIds), operator);
        }
        for (HisMirrorConsumeLink lk : links)
        {
            hisMirrorConsumeLinkMapper.softDeleteLinkById(tenantId, lk.getId(), operator);
        }

        for (Long traceId : tracesToVoid)
        {
            tryVoidTraceIfFullyReleased(tenantId, traceId);
        }

        for (String key : mirrorKeys)
        {
            int p = key.indexOf('|');
            String visitKind = key.substring(0, p);
            String mirrorRowId = key.substring(p + 1);
            BigDecimal net = nz(hisMirrorConsumeLinkMapper.sumNetAllocQtyForMirrorRow(tenantId, visitKind, mirrorRowId));
            if (net.compareTo(BigDecimal.ZERO) <= 0)
            {
                resetMirrorToPending(tenantId, visitKind, mirrorRowId);
                vo.setMirrorResetCount(vo.getMirrorResetCount() + 1);
            }
        }
        vo.getMessages().add("冲销完成：已回补科室库存，计费行可重新核销");
        return vo;
    }

    private List<HisMirrorConsumeLink> reloadLinks(String tenantId, List<String> linkIds)
    {
        List<HisMirrorConsumeLink> out = new ArrayList<>();
        for (String id : linkIds)
        {
            HisMirrorConsumeLink lk = hisMirrorConsumeLinkMapper.selectById(tenantId, id);
            if (lk == null)
            {
                throw new ServiceException("反向单据后关联行不可用：" + id);
            }
            out.add(lk);
        }
        return out;
    }

    private void restoreInventory(HisMirrorConsumeLink lk, BigDecimal returnQty, String remark)
    {
        if (lk.getTraceabilityId() != null && lk.getTraceabilityEntryId() != null)
        {
            restoreTracePath(lk, returnQty);
            return;
        }
        if (lk.getDeptBatchConsumeId() != null && lk.getDeptBatchConsumeEntryId() != null)
        {
            restoreLegacyPath(lk, returnQty, remark);
            return;
        }
        // 仅有 gz 库存引用时直接加回
        if (lk.getGzDepInventoryId() != null)
        {
            GzDepInventory gz = gzDepInventoryMapper.selectGzDepInventoryById(lk.getGzDepInventoryId());
            if (gz == null)
            {
                throw new ServiceException("高值科室库存不存在，无法冲销");
            }
            gz.setQty(nz(gz.getQty()).add(returnQty));
            gzDepInventoryMapper.updateGzDepInventory(gz);
            return;
        }
        throw new ServiceException("消耗关联缺少追溯/库存信息，无法冲销");
    }

    private void restoreTracePath(HisMirrorConsumeLink lk, BigDecimal returnQty)
    {
        GzTraceability trace = gzTraceabilityService.selectGzTraceabilityById(lk.getTraceabilityId());
        if (trace == null || trace.getTraceabilityEntryList() == null)
        {
            throw new ServiceException("高值计费单不存在，无法冲销");
        }
        GzTraceabilityEntry entry = trace.getTraceabilityEntryList().stream()
            .filter(e -> e != null && lk.getTraceabilityEntryId().equals(e.getId()))
            .findFirst()
            .orElse(null);
        if (entry == null)
        {
            throw new ServiceException("高值计费明细不存在，无法冲销");
        }
        Long gzId = lk.getGzDepInventoryId() != null ? lk.getGzDepInventoryId() : entry.getInventoryId();
        if (gzId == null)
        {
            throw new ServiceException("缺少高值库存关联，无法冲销");
        }
        GzDepInventory gz = gzDepInventoryMapper.selectGzDepInventoryById(gzId);
        if (gz == null)
        {
            throw new ServiceException("高值科室库存不存在，无法冲销");
        }
        hcBarcodeLifecycleService.onMirrorHighChargeRefund(trace, entry, gz, returnQty);
    }

    private void restoreLegacyPath(HisMirrorConsumeLink lk, BigDecimal returnQty, String remark)
    {
        DeptBatchConsumeReverseReq.ReverseItem it = new DeptBatchConsumeReverseReq.ReverseItem();
        it.setSrcConsumeEntryId(lk.getDeptBatchConsumeEntryId());
        it.setReverseQty(returnQty);
        DeptBatchConsumeReverseReq req = new DeptBatchConsumeReverseReq();
        req.setConsumeId(lk.getDeptBatchConsumeId());
        req.setItems(java.util.Collections.singletonList(it));
        req.setRemark(remark + " link=" + lk.getId());
        DeptBatchConsume rev = deptBatchConsumeService.reverseConsumeForBillingRefund(req, SecurityUtils.getUserIdStr());
        if (rev == null || rev.getId() == null)
        {
            throw new ServiceException("历史高值消耗反消耗失败");
        }
    }

    private void tryVoidTraceIfFullyReleased(String tenantId, Long traceId)
    {
        if (traceId == null)
        {
            return;
        }
        try
        {
            GzTraceability t = gzTraceabilityService.selectGzTraceabilityById(traceId);
            if (t == null || !"HIS_MIRROR_HIGH".equals(StringUtils.trimToEmpty(t.getTraceSource())))
            {
                return;
            }
            if (StringUtils.isBlank(t.getVisitKind()) || StringUtils.isBlank(t.getMirrorRowId()))
            {
                return;
            }
            BigDecimal net = nz(hisMirrorConsumeLinkMapper.sumNetAllocQtyForMirrorRow(
                tenantId, t.getVisitKind().trim().toUpperCase(), t.getMirrorRowId().trim()));
            if (net.compareTo(BigDecimal.ZERO) <= 0)
            {
                gzTraceabilityService.deleteGzTraceabilityById(traceId);
            }
        }
        catch (Exception ignored)
        {
            // 软删计费单失败不影响冲销主流程
        }
    }

    private void resetMirrorToPending(String tenantId, String visitKind, String mirrorRowId)
    {
        Date procTime = DateUtils.getNowDate();
        String procBy = SecurityUtils.getUserIdStr();
        List<String> ids = java.util.Collections.singletonList(mirrorRowId);
        if (KIND_IN.equals(visitKind))
        {
            hisInpatientChargeMirrorMapper.updateMirrorProcessByIds(tenantId, ids,
                STATUS_PENDING, PROC_TYPE_EMPTY, procTime, procBy, null, null);
        }
        else
        {
            hisOutpatientChargeMirrorMapper.updateMirrorProcessByIds(tenantId, ids,
                STATUS_PENDING, PROC_TYPE_EMPTY, procTime, procBy, null, null);
        }
        hisPatientChargeMirrorUnifiedMapper.updateMirrorProcessByIds(tenantId, ids,
            STATUS_PENDING, PROC_TYPE_EMPTY, procTime, procBy, null, null);
    }

    private static BigDecimal refundableOnLink(HisMirrorConsumeLink lk)
    {
        if (lk.getRefundableRemainingQty() != null)
        {
            return lk.getRefundableRemainingQty().max(BigDecimal.ZERO);
        }
        return nz(lk.getAllocQty()).subtract(nz(lk.getReturnedQty())).max(BigDecimal.ZERO);
    }

    private static BigDecimal nz(BigDecimal v)
    {
        return v == null ? BigDecimal.ZERO : v;
    }

    private String requireTenant()
    {
        String tenantId = SecurityUtils.getCustomerId();
        if (StringUtils.isBlank(tenantId))
        {
            throw new ServiceException("无法解析当前租户");
        }
        return tenantId.trim();
    }
}
